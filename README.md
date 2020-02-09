# Novocaine

Novocaine is a framework designed to make dependency injection simple. It works solely with the native `javax.inject` annotations and handles all of the standard use-cases for injection. Currently, Novocaine supports Constructor, Method, and Field injection strategies.

### Supported Annotations

```
@Inject
@Named
@Qualifier
@Singleton
```

### Example Usage

Let's assume we're wiring up a server. Our server uses a class called `Runner` to start everything up:

```java
public class Runner {

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.run();
    }

    private void run() {
        // ...
    }
}
```

Consider a `@Singleton` service:

```java
@Singleton
public class PaymentService {

    public void pay() {
        // ...
    }
}
```

We can now use Novocaine to inject this service into our `Runner` class (field injection demonstrated):

```java
public class Runner {

    @Inject
    private PaymentService paymentService;

    public static void main(String[] args) {
        Runner runner = new Runner();
        runner.run();
    }

    private void run() {
        // pass Novocaine the top-level object and let it instantiate everything for us:
        Novocaine.inject(this)

        // paymentService is fully instantiated:
        paymentService.pay();
    }
}
```

Let's now assume that we have a few different means of paying in our `PaymentService`: we can pay with cash, credit, or debit. We'll use an interface to define how to pay, and use annotations to define our injection strategies.

First, the interface:

```java
public interface Payment() {

    void pay();
}
```

For our cash payment, we'll simply write a concrete implementing class and inject it directly. Here's our cash implementation:

```java
public class CashPaymentImpl implements Payment {

    @Override
    public void pay() {
        // pay with cash
    }
}
```

For our credit payment, we'll use a qualifier-based implementation. We'll need to create the custom annotation `@CreditPayment` and specify it as a qualifier:

```java
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface CreditPayment { }
```

Now we can annotate our concrete implementing class with this custom annotation:

```java
@CreditPayment
public class CreditPaymentImpl implements Payment {

    @Override
    public void pay() {
        // pay with credit
    }
}
```

For our debit payment, we'll use a named-based implementation. We can simply add the `@Named` annotation with a value of `debit` to the top of our concrete implementing class:

```java
@Named("debit")
public class DebitPaymentImpl implements Payment {

    @Override
    public void pay() {
        // pay with debit
    }
}
```

We're now ready to use these different payment methods in our PaymentService. We have many ways in which we can inject them. Let's go through each.

##### Constructor Injection

```java
@Singleton
public class PaymentService {

    private Payment cashPayment;
    private Payment creditPayment;
    private Payment debitPayment;

    @Inject
    public PaymentService(CashPaymentImpl cashPayment,
                          @CreditPayment Payment creditPayment,
                          @Named("debit") Payment debitPayment) {
        this.cashPayment = cashPayment;
        this.creditPayment = creditPayment;
        this.debitPayment = debitPayment;
    }
}
```

##### Field Injection

```java
@Singleton
public class PaymentService {

    @Inject
    private Payment cashPayment;

    @Inject
    @CreditPayment
    private Payment creditPayment;

    @Inject
    @Named("debit")
    private Payment debitPayment;

}
```

##### Method Injection

```java
@Singleton
public class PaymentService {

    private Payment cashPayment;
    private Payment creditPayment;
    private Payment debitPayment;

    @Inject
    public void setCashPayment(CashPaymentImpl cashPayment) {
        this.cashPayment = cashPayment;
    }

    @Inject
    public void setCreditPayment(@CreditPayment Payment creditPayment) {
        this.creditPayment = creditPayment;
    }

    @Inject
    public void setDebitPayment(@Named("debit") Payment debitPayment) {
        this.debitPayment = debitPayment;
    }
}
```
