package io.novocaine.example.service;

import io.novocaine.example.qualifier.CashPaymentImpl;
import io.novocaine.example.qualifier.CreditPayment;
import io.novocaine.example.qualifier.Payment;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ConstructorInjectionService {

    private final Payment creditPayment;
    private final Payment cashPayment;
    private final Payment debitPayment;

    /**
     * Constructor injection with qualifier and named annotations on params
     */
    @Inject
    public ConstructorInjectionService(@CreditPayment Payment creditPayment, CashPaymentImpl cashPayment, @Named("debit") Payment debitPayment) {
        this.creditPayment = creditPayment;
        this.cashPayment = cashPayment;
        this.debitPayment = debitPayment;
    }

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
