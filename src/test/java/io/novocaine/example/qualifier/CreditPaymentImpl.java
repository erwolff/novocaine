package io.novocaine.example.qualifier;

@CreditPayment
public class CreditPaymentImpl implements Payment {
    @Override
    public void pay() {
        System.out.println("Payed with credit");
    }
}
