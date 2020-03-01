package io.novocaine;

import io.novocaine.example.service.TopLevelService;
import io.novocaine.example.qualifier.*;
import io.novocaine.example.service.*;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Named;

import static org.junit.Assert.*;

public class NovocaineHelperTest {

    @BeforeClass
    public static void setup() {
        TopLevelService topLevelService = new TopLevelService();
        Novocaine.inject(topLevelService);
    }

    /**
     * Qualifier Annotation Map Tests
     */
    @Test
    public void test_qualifier_creditPayment() {
        Class<?> creditPayment = NovocaineHelper.qualifierAnnotationMap.get(CreditPayment.class);
        assertNotNull(creditPayment);
        assertEquals(creditPayment, CreditPaymentImpl.class);
    }

    @Test
    public void test_qualifier_cashPayment() {
        Class<?> cashPayment = NovocaineHelper.qualifierAnnotationMap.get(CashPayment.class);
        assertNotNull(cashPayment);
        assertEquals(cashPayment, CashPaymentImpl.class);
    }

    /**
     * Named Annotation Map Tests
     */
    @Test
    public void test_named_debitPayment() {
        Class<?> debitPayment = NovocaineHelper.namedAnnotationMap.get(DebitPaymentImpl.class.getAnnotation(Named.class).toString());
        assertNotNull(debitPayment);
        assertEquals(debitPayment, DebitPaymentImpl.class);
    }


    /**
     * Field Injection Tests
     */
    @Test
    public void test_fieldInjection_concrete() {
        FieldInjectionService fieldInjectionService = Novocaine.get(FieldInjectionService.class);
        assertNotNull(fieldInjectionService);
        assertNotNull(fieldInjectionService.getCashPayment());
    }

    @Test
    public void test_fieldInjection_qualifier() {
        FieldInjectionService fieldInjectionService = Novocaine.get(FieldInjectionService.class);
        assertNotNull(fieldInjectionService);
        assertNotNull(fieldInjectionService.getCreditPayment());
    }

    @Test
    public void test_fieldInjection_named() {
        FieldInjectionService fieldInjectionService = Novocaine.get(FieldInjectionService.class);
        assertNotNull(fieldInjectionService);
        assertNotNull(fieldInjectionService.getDebitPayment());
    }


    /**
     * Constructor Injection Tests
     */
    @Test
    public void test_constructorInjection_concrete() {
        ConstructorInjectionService constructorInjectionService = Novocaine.get(ConstructorInjectionService.class);
        assertNotNull(constructorInjectionService);
        assertNotNull(constructorInjectionService.getCashPayment());
    }

    @Test
    public void test_constructorInjection_qualifier() {
        ConstructorInjectionService constructorInjectionService = Novocaine.get(ConstructorInjectionService.class);
        assertNotNull(constructorInjectionService);
        assertNotNull(constructorInjectionService.getCreditPayment());
    }

    @Test
    public void test_constructorInjection_named() {
        ConstructorInjectionService constructorInjectionService = Novocaine.get(ConstructorInjectionService.class);
        assertNotNull(constructorInjectionService);
        assertNotNull(constructorInjectionService.getDebitPayment());
    }

    @Test
    public void test_constructorInjection_named_provided() {
        ConstructorInjectionService constructorInjectionService = Novocaine.get(ConstructorInjectionService.class);
        assertNotNull(constructorInjectionService);
        assertNotNull(constructorInjectionService.getProvidedService());
        assertEquals("firstNamedProvidedService", constructorInjectionService.getProvidedService().getName());
    }

    /**
     * Method Injection Tests
     */
    @Test
    public void test_methodInjection_concrete() {
        MethodInjectionService methodInjectionService = Novocaine.get(MethodInjectionService.class);
        assertNotNull(methodInjectionService);
        assertNotNull(methodInjectionService.getCashPayment());
    }

    @Test
    public void test_methodInjection_qualifierOnParam() {
        MethodInjectionService methodInjectionService = Novocaine.get(MethodInjectionService.class);
        assertNotNull(methodInjectionService);
        assertNotNull(methodInjectionService.getCreditPayment());
    }

    @Test
    public void test_methodInjection_qualifierOnMethod() {
        MethodInjectionService methodInjectionService = Novocaine.get(MethodInjectionService.class);
        assertNotNull(methodInjectionService);
        assertNotNull(methodInjectionService.getSecondCreditPayment());
    }

    @Test
    public void test_methodInjection_namedOnParam() {
        MethodInjectionService methodInjectionService = Novocaine.get(MethodInjectionService.class);
        assertNotNull(methodInjectionService);
        assertNotNull(methodInjectionService.getDebitPayment());
    }

    @Test
    public void test_methodInjection_namedOnMethod() {
        MethodInjectionService methodInjectionService = Novocaine.get(MethodInjectionService.class);
        assertNotNull(methodInjectionService);
        assertNotNull(methodInjectionService.getSecondDebitPayment());
    }

    @Test
    public void test_methodInjection_namedOnParam_provided() {
        MethodInjectionService methodInjectionService = Novocaine.get(MethodInjectionService.class);
        assertNotNull(methodInjectionService);
        assertNotNull(methodInjectionService.getFirstProvidedService());
        assertEquals("firstNamedProvidedService", methodInjectionService.getFirstProvidedService().getName());
    }

    @Test
    public void test_methodInjection_namedOnMethod_provided() {
        MethodInjectionService methodInjectionService = Novocaine.get(MethodInjectionService.class);
        assertNotNull(methodInjectionService);
        assertNotNull(methodInjectionService.getSecondProvidedService());
        assertEquals("secondNamedProvidedService", methodInjectionService.getSecondProvidedService().getName());
    }

    @Test
    public void test_methodSingletonInstantiation() {
        UserOfProviderService userOfProviderService = Novocaine.get(UserOfProviderService.class);
        assertNotNull(userOfProviderService);
        assertNotNull(userOfProviderService.getProviderService());
        assertNotNull(userOfProviderService.getSecondProvidedService());
        assertNotNull(userOfProviderService.getSecondProvidedService().getProvidedService());
    }

    @Test
    public void test_methodSingletonInstantiation_named() {
        UserOfProviderService userOfProviderService = Novocaine.get(UserOfProviderService.class);
        assertNotNull(userOfProviderService);
        assertNotNull(userOfProviderService.getFirstNamedProvidedService());
        assertEquals("firstNamedProvidedService", userOfProviderService.getFirstNamedProvidedService().getName());
        assertNotNull(userOfProviderService.getSecondNamedProvidedService());
        assertEquals("secondNamedProvidedService", userOfProviderService.getSecondNamedProvidedService().getName());
    }


    /**
     * Recursive Injection Tests
     */
    @Test
    public void test_recursiveInjection() {
        TopLevelService topLevelService = Novocaine.get(TopLevelService.class);
        assertNotNull(topLevelService);

        FifthLevelService fifthLevelService = topLevelService.getFifthLevelService();
        assertNotNull(fifthLevelService);
        assertEquals(fifthLevelService, Novocaine.get(FifthLevelService.class));

        FourthLevelService fourthLevelService = fifthLevelService.getFourthLevelService();
        assertNotNull(fourthLevelService);
        assertEquals(fourthLevelService, Novocaine.get(FourthLevelService.class));

        // ensure all instances of same class types are the exact same object
        ThirdLevelService thirdLevelService = fourthLevelService.getThirdLevelService();
        assertNotNull(thirdLevelService);
        assertEquals(thirdLevelService, Novocaine.get(ThirdLevelService.class));
        SecondLevelService secondLevelServiceFromFourthLevelService = fourthLevelService.getSecondLevelService();
        assertNotNull(secondLevelServiceFromFourthLevelService);
        assertEquals(secondLevelServiceFromFourthLevelService, Novocaine.get(SecondLevelService.class));

        SecondLevelService secondLevelServiceFromThirdLevelService = thirdLevelService.getSecondLevelService();
        assertNotNull(secondLevelServiceFromThirdLevelService);
        assertEquals(secondLevelServiceFromFourthLevelService, secondLevelServiceFromThirdLevelService);
        assertEquals(secondLevelServiceFromThirdLevelService, Novocaine.get(SecondLevelService.class));

        LowLevelService lowLevelServiceFromSecondFromFourthLevelService = secondLevelServiceFromFourthLevelService.getLowLevelService();
        assertNotNull(lowLevelServiceFromSecondFromFourthLevelService);
        assertEquals(lowLevelServiceFromSecondFromFourthLevelService, Novocaine.get(LowLevelService.class));
        LowLevelService lowLevelServiceFromSecondFromThirdLevelService = secondLevelServiceFromThirdLevelService.getLowLevelService();
        assertNotNull(lowLevelServiceFromSecondFromThirdLevelService);
        assertEquals(lowLevelServiceFromSecondFromFourthLevelService, lowLevelServiceFromSecondFromThirdLevelService);
        assertEquals(lowLevelServiceFromSecondFromThirdLevelService, Novocaine.get(LowLevelService.class));
    }
}