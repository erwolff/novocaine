package io.novocaine.example.qualifier;

@CashPayment
public class CashPaymentImpl implements Payment {
    @Override
    public void pay() {
        System.out.println("Payed with cash");
    }
}
