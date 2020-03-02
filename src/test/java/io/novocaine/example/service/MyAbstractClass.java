package io.novocaine.example.service;

import io.novocaine.example.qualifier.DebitPaymentImpl;

import javax.inject.Inject;

public abstract class MyAbstractClass {

    @Inject
    protected DebitPaymentImpl debitPayment;

    public DebitPaymentImpl getDebitPayment() {
        return debitPayment;
    }
}
