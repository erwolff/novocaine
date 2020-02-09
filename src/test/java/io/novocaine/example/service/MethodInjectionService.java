package io.novocaine.example.service;

import io.novocaine.example.qualifier.CashPaymentImpl;
import io.novocaine.example.qualifier.CreditPayment;
import io.novocaine.example.qualifier.Payment;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MethodInjectionService {

    private CashPaymentImpl cashPayment;
    private Payment creditPayment;
    private Payment secondCreditPayment;
    private Payment debitPayment;
    private Payment secondDebitPayment;

    /**
     * Method injection with concrete class type as param
     */
    @Inject
    public void setCashPayment(CashPaymentImpl cashPayment) {
        this.cashPayment = cashPayment;
    }

    /**
     * Method injection with qualifier annotation on param
     */
    @Inject
    public void setCreditPayment(@CreditPayment Payment creditPayment) {
        this.creditPayment = creditPayment;
    }

    /**
     * Method injection with qualifier annotation on method
     */
    @Inject
    @CreditPayment
    public void setSecondCreditPayment(Payment secondCreditPayment) {
        this.secondCreditPayment = secondCreditPayment;
    }

    /**
     * Method injection with named annotation on param
     */
    @Inject
    public void setDebitPayment(@Named("debit") Payment debitPayment) {
        this.debitPayment = debitPayment;
    }

    /**
     * Method injection with named annotation on method
     */
    @Inject
    @Named("debit")
    public void setSecondDebitPayment(Payment secondDebitPayment) {
        this.secondDebitPayment = secondDebitPayment;
    }

    public CashPaymentImpl getCashPayment() {
        return cashPayment;
    }

    public Payment getCreditPayment() {
        return creditPayment;
    }

    public Payment getSecondCreditPayment() {
        return secondCreditPayment;
    }

    public Payment getDebitPayment() {
        return debitPayment;
    }

    public Payment getSecondDebitPayment() {
        return secondDebitPayment;
    }
}
