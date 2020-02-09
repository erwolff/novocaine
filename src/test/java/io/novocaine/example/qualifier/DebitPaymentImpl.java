package io.novocaine.example.qualifier;

import javax.inject.Named;

@Named("debit")
public class DebitPaymentImpl implements Payment {

    @Override
    public void pay() {
        System.out.println("Payed with debit");
    }
}
