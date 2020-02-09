package io.novocaine.example.service;

import io.novocaine.example.qualifier.CashPayment;
import io.novocaine.example.qualifier.CashPaymentImpl;
import io.novocaine.example.qualifier.CreditPayment;
import io.novocaine.example.qualifier.Payment;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class FieldInjectionService {

    /**
     * Field injection with qualifier annotation
     */
    @Inject
    @CreditPayment
    private Payment creditPayment;

    /**
     * Field injection with named annotation
     */
    @Inject
    @Named("debit")
    private Payment debitPayment;

    /**
     * Field injection with concrete clas
     */
    @Inject
    private CashPaymentImpl cashPayment;


    public Payment getCreditPayment() {
        return creditPayment;
    }

    public Payment getCashPayment() {
        return cashPayment;
    }

    public Payment getDebitPayment() {
        return debitPayment;
    }
}
