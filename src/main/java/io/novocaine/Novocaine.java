package io.novocaine;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides full functionality of the native javax.inject annotations
 *
 * Supported Annotations:
 *      \@Inject
 *      \@Named
 *      \@Qualifier
 *      \@Singleton
 *
 * Supported Injection Strategies:
 *      Constructor
 *      Field
 *      Setter
 *
 *
 *  View README.md for usage and examples
 */
public class Novocaine {

    /**
     * Map containing the class type to the instantiated object for all injectable classes
     */
    static Map<Class<?>, Object> injectableProvider = new ConcurrentHashMap<>();
    static Reflections reflections;

    /**
     * Instantiates and injects all relevant classes within the supplied object's package
     *
     * Usage: Novocaine.inject(this)
     *
     * @param topLevel - the top-level class
     */
    public static void inject(Object topLevel) {
        if (topLevel == null) {
            throw new RuntimeException("Novocaine#inject must be passed a valid, non-null, instantiated class: Novocaine.inject(this)");
        }
        if (injectableProvider.size() != 0) {
            throw new RuntimeException(("Novocaine#inject may only be called once"));
        }

        reflections = new Reflections("", new SubTypesScanner(false), new TypeAnnotationsScanner());

        // find and resolve all @Qualifier and @Named annotations
        NovocaineHelper.findQualifierAnnotations();
        NovocaineHelper.findNamedAnnotations();

        // next retrieve all classes
        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

        // "seen" will represent classes we have already instantiated during a recursive call - if we come across an
        // already seen class, then we know there's a cyclic dependency and we must throw an exception
        Set<Class<?>> seen = new HashSet<>();

        // iterate over each method annotated with @Singleton in each class and invoke the method to instantiate the provided classes
        classes.forEach(clazz -> NovocaineHelper.instantiateSuppliedClasses(clazz, seen, topLevel));

        // iterate over each class, check for cyclic dependencies, and instantiate/inject all @Singletons
        classes.forEach(clazz -> NovocaineHelper.checkCyclicDependenciesAndInject(clazz, seen, topLevel));

        // finally, store the top-level class
        injectableProvider.put(topLevel.getClass(), topLevel);
    }

    /**
     * Retrieves the singleton associated with the supplied class type from the classes which have been instantiated by Novocaine
     *
     * @param clazz - the type of class to retrieve
     *
     * @return - the singleton which was instantiated by Novocaine (or null)
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {
        return clazz != null ? (T) injectableProvider.get(clazz) : null;
    }
}
