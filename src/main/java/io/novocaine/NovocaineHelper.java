package io.novocaine;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class NovocaineHelper {

    /**
     * Map containing the annotation marked with @Qualifier to the class which represents the concrete implementation
     */
    static Map<Class<? extends Annotation>, Class<?>> qualifierAnnotationMap = new ConcurrentHashMap<>();

    /**
     * Map containing the class name marked with @Named to the class which represents the concrete implementation
     */
    static Map<String, Class<?>> namedAnnotationMap = new ConcurrentHashMap<>();

    /**
     * Locates all annotations marked with with @Qualifier and determines which concrete implementation to associate
     */
    @SuppressWarnings("unchecked")
    static void findQualifierAnnotations() {
        // retrieve all types marked with @Qualifier
        Set<Class<?>> qualifiers = Novocaine.reflections.getTypesAnnotatedWith(Qualifier.class, true);

        for (Class<?> qualifier : qualifiers) {
            // ensure this is an annotation
            if (qualifier.isAnnotation()) {
                // now find the class marked with this annotation - this class will be the implementation
                Set<Class<?>> implementations = Novocaine.reflections.getTypesAnnotatedWith((Class<? extends Annotation>) qualifier, true);

                // ensure an implementation has been found
                if (implementations.isEmpty()) {
                    throw new RuntimeException(qualifier.getName() + " must be implemented by a class");
                }

                // ensure only one implementation exists
                if (implementations.size() > 1) {
                    throw new RuntimeException(qualifier.getName() + " may only be implemented by a single class");
                }

                // store the annotation to its implementing class
                qualifierAnnotationMap.put((Class<? extends Annotation>) qualifier, implementations.iterator().next());
            }
        }
    }

    /**
     * Locates all classes marked with with @Named and stores the value to concrete implementation class association
     */
    static void findNamedAnnotations() {
        // retrieve all types marked with @Named
        Set<Class<?>> namedClasses = Novocaine.reflections.getTypesAnnotatedWith(Named.class, true);

        for (Class<?> namedClass : namedClasses) {
            // the key will be the toString() of the @Named annotation in order to retain the value field
            String key = namedClass.getAnnotation(Named.class).toString();

            // ensure only one implementation exists
            if (namedAnnotationMap.get(key) != null) {
                throw new RuntimeException("Multiple classes marked with @Named annotation with value: " + namedClass.getAnnotation(Named.class).value());
            }

            // store the annotation to its implementing class
            namedAnnotationMap.put(key, namedClass);
        }
    }

    /**
     * Verifies that no cyclic dependency exists in this class hierarchy and injects/instantiates all relevant classes
     *
     * @param clazz - the class which may or may not contain fields/constructors/methods marked with @Inject
     * @param seen - the set of classes which have already been seen while checking for cyclic dependencies
     * @param topLevel - the top level class which called Novocaine.inject(this)
     */
    static void checkCyclicDependenciesAndInject(@Nonnull Class<?> clazz, @Nonnull Set<Class<?>> seen, @Nonnull Object topLevel) {
        // add this class to our set of "seen" classes in order to ensure we don't have a cyclic dependency
        seen.add(clazz);

        // inject singletons into any field annotated with @Inject
        handleFieldInjection(clazz, seen, topLevel);

        // inject singletons into any constructor annotated with @Inject
        handleConstructorInjection(clazz, seen, topLevel);

        // inject singletons into any method annotated with @Inject
        handleMethodInjection(clazz, seen, topLevel);

        // all fields/methods marked with @Inject have been resolved - instantiate this class if it's marked with @Singleton
        if (!Novocaine.injectableProvider.containsKey(clazz) && (clazz.getAnnotation(Singleton.class) != null || resolveAnnotatedClassToType(clazz).isPresent())) {
            instantiateClass(clazz);
        }
    }


    /**
     * Resolves and instantiates any field marked with @Inject on the supplied class
     *
     * @param clazz - the class which may or may not contain fields marked with @Inject
     * @param seen - the set of classes which have already been seen while checking for cyclic dependencies
     * @param topLevel - the top level class which called Novocaine.inject(this)
     */
    private static void handleFieldInjection(@Nonnull Class<?> clazz, @Nonnull Set<Class<?>> seen, @Nonnull Object topLevel) {
        // iterate over each field in the class
        for (Field field : clazz.getDeclaredFields()) {
            // check if the field is marked with @Inject
            if (field.getAnnotation(Inject.class) != null) {
                // determine the type of field this is (if interface, determine the concrete implementation)
                Class<?> type = determineType(field);
                // check if we've already instantiated this type
                if (Novocaine.injectableProvider.containsKey(type)) {
                    // we've already instantiated this type - just set the field to its instantiation
                    setField(field, type, topLevel);
                }
                else {
                    // check if we've already seen this type - if so, this is a circular dependency
                    if (seen.contains(type))
                        throw new RuntimeException("Circular Dependency Detected: " + type.getName());

                    // recursively check for cyclic dependencies of classes used by this one
                    checkCyclicDependenciesAndInject(type, seen, topLevel);

                    // all classes used by this class have been resolved - set the field with the fully instantiated object
                    setField(field, type, topLevel);
                }
            }
        }
    }

    /**
     * Resolves and instantiates the first constructor encountered marked with @Inject on the supplied class
     *
     * @param clazz - the class which may or may not contain a constructor marked with @Inject
     * @param seen - the set of classes which have already been seen while checking for cyclic dependencies
     * @param topLevel - the top level class which called Novocaine.inject(this)
     */
    private static void handleConstructorInjection(@Nonnull Class<?> clazz, @Nonnull Set<Class<?>> seen, @Nonnull Object topLevel) {
        // iterate over each constructor in this class
        for (Constructor constructor : clazz.getConstructors()) {
            // check if this constructor is marked with @Inject
            if (constructor.getAnnotation(Inject.class) != null) {

                // resolve all arguments required by this constructor (checking for cyclic dependencies along the way)
                List<Object> args = resolveArgs(constructor.getParameters(), constructor.getParameterAnnotations(), seen, topLevel);

                try {
                    // instantiate this class by invoking the constructor with the resolved args
                    Object o = constructor.newInstance(args.toArray());
                    Novocaine.injectableProvider.put(clazz, o);
                    break;
                }
                catch (Exception e) {
                    throw new RuntimeException("Cannot instantiate class: " + clazz.getName(), e);
                }
            }
        }
    }

    /**
     * Resolves and instantiates any method marked with @Inject on the supplied class
     *
     * @param clazz - the class which may or may not contain methods marked with @Inject
     * @param seen - the set of classes which have already been seen while checking for cyclic dependencies
     * @param topLevel - the top level class which called Novocaine.inject(this)
     */
    private static void handleMethodInjection(@Nonnull Class<?> clazz, @Nonnull Set<Class<?>> seen, @Nonnull Object topLevel) {
        // iterate over each method in this class
        for (Method method : clazz.getMethods()) {
            // check if this method is marked with @Inject
            if (method.getAnnotation(Inject.class) != null) {
                // check to see if the method itself is annotated with @Named or one of the @Qualifier annotations
                Class<?> type = resolveAnnotationsToType(method.getAnnotations()).orElse(null);
                if (type != null) {
                    // the method is annotated, it MUST only have one parameter:
                    if (method.getParameterCount() != 1) {
                        throw new RuntimeException("Method " + clazz.getName() + "#" + method.getName()
                                + " requires " + method.getParameterCount() + " parameters but is annotated with @Named or a @Qualifier-associated annotation");
                    }
                    // resolve the single parameter (checking for cyclic dependencies)
                    List<Object> args = Collections.singletonList(resolveArg(type, seen, topLevel));

                    // invoke this setter with the resolved arg
                    invokeMethod(method, args, topLevel);
                }
                else {
                    // resolve all arguments required by this method (checking for cyclic dependencies along the way)
                    List<Object> args = resolveArgs(method.getParameters(), method.getParameterAnnotations(), seen, topLevel);

                    // invoke the setter with the resolved args
                    invokeMethod(method, args, topLevel);
                }
            }
        }
    }

    /**
     * Resolves and instantiates the parameters required for this constructor or method to be invoked
     *
     * @param parameters - the parameters of the constructor or method to be invoked
     * @param parameterAnnotations - the annotations of the parameters of the constructor or method to be invoked
     * @param seen - the set of classes which have already been seen while checking for cyclic dependencies
     * @param topLevel - the top level class which called Novocaine.inject(this)
     *
     * @return - the args with which to invoke the constructor or method
     */
    private static List<Object> resolveArgs(@Nonnull Parameter[] parameters, @Nonnull Annotation[][] parameterAnnotations, @Nonnull Set<Class<?>> seen, @Nonnull Object topLevel) {
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = determineParameterType(parameter.getType(), parameterAnnotations[i]);
            args.add(resolveArg(type, seen, topLevel));
        }
        return args;
    }

    /**
     * Resolves and instantiates the supplied argument type - or recursively continues down the stack if more classes must be instantiated first
     *
     * @param type - the type of argument
     * @param seen - the set of classes which have already been seen while checking for cyclic dependencies
     * @param topLevel - the top level class which called Novocaine.inject(this)
     * @return
     */
    private static Object resolveArg(Class<?> type, Set<Class<?>> seen, Object topLevel) {
        if (!Novocaine.injectableProvider.containsKey(type)) {
            if (seen.contains(type)) {
                throw new RuntimeException("Circular Dependency Detected: " + type.getName());
            }
            checkCyclicDependenciesAndInject(type, seen, topLevel);
        }
        return Novocaine.injectableProvider.get(type);
    }

    /**
     * Determines which class type this field should be considered as
     *
     * @param field
     * @return
     */
    private static Class<?> determineType(@Nonnull Field field) {
        if (!field.getType().isInterface()) {
            return field.getType();
        }
        return resolveAnnotationsToType(field.getDeclaredAnnotations())
                .orElseThrow(() -> new RuntimeException("Cannot instantiate interface: " + field.getType().getName()));
    }

    /**
     * Determines which class type this parameter should be considered as
     *
     * @param clazz
     * @param annotations
     * @return
     */
    private static Class<?> determineParameterType(@Nonnull Class<?> clazz, @Nonnull Annotation[] annotations) {
        if (!clazz.isInterface()) {
            return clazz;
        }
        return resolveAnnotationsToType(annotations)
                .orElseThrow(() -> new RuntimeException("Cannot instantiate interface: " + clazz.getName()));
    }


    /**
     * Attempts to resolve the supplied class to its concrete type based on its annotations
     *
     * @param clazz
     * @return
     */
    //TODO: We could improve this by storing known class impls with custom annotations in a Set
    private static Optional<Class<?>> resolveAnnotatedClassToType(@Nonnull Class<?> clazz) {
        return resolveAnnotationsToType(clazz.getDeclaredAnnotations());
    }

    /**
     * Attempts to resolve the annotations to the qualified concrete class
     *
     * @param annotations
     * @return
     */
    private static Optional<Class<?>> resolveAnnotationsToType(@Nonnull Annotation[] annotations) {
        // simply return the first one that resolves
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Named.class) {
                return Optional.of(namedAnnotationMap.get(annotation.toString()));
            }
            if (qualifierAnnotationMap.containsKey(annotation.annotationType())) {
                return Optional.of(qualifierAnnotationMap.get(annotation.annotationType()));
            }
        }
        return Optional.empty();
    }

    /**
     * Sets the field marked with @Inject on its declaring class (or the top level class)
     *
     * @param field - the field to set
     * @param type - the type of class this field resolves to
     * @param topLevel - the top level class which called Novocaine.inject(this)
     */
    private static void setField(@Nonnull Field field, @Nonnull Class<?> type, @Nonnull Object topLevel) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            Class<?> clazz = field.getDeclaringClass();
            if (clazz == topLevel.getClass()) {
                field.set(topLevel, Novocaine.injectableProvider.get(type));
            }
            else {
                instantiateClass(clazz);
                field.set(Novocaine.injectableProvider.get(clazz), Novocaine.injectableProvider.get(type));
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot set field: " + field.getName() + " on class: " + field.getDeclaringClass().getName(), e);
        }
    }

    /**
     * Invokes the supplied method marked with @Inject using the supplied args on its declaring class (or the top level class)
     *
     * @param method - the method to invoke
     * @param args - the arguments to provide to the method during invocation
     * @param topLevel - the top level class which called Novocaine.inject(this)
     */
    private static void invokeMethod(@Nonnull Method method, @Nonnull List<Object> args, @Nonnull Object topLevel) {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            Class<?> clazz = method.getDeclaringClass();
            if (clazz == topLevel.getClass()) {
                method.invoke(topLevel, args.toArray());
            }
            else {
                instantiateClass(clazz);
                method.invoke(Novocaine.injectableProvider.get(clazz), args.toArray());
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke method: " + method.getName() + " on class: " + method.getDeclaringClass().getName(), e);
        }
    }

    /**
     * Creates a new instance of the supplied class and stores it in the injectableProvider
     *
     * @param clazz - the class to instantiate and store
     */
    static void instantiateClass(@Nonnull Class<?> clazz) {
        if (!Novocaine.injectableProvider.containsKey(clazz)) {
            try {
                Object o = clazz.newInstance();
                Novocaine.injectableProvider.put(clazz, o);
            }
            catch (Throwable t) {
                throw new RuntimeException("Unable to instantiate class: " + clazz.getName());
            }
        }
    }
}
