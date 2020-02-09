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