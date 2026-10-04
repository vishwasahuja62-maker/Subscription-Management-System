package com.subscription.model;

/**
 * JavaBean representing a Payment record.
 * Maps to the 'payments' table in MySQL (future JDBC phase).
 *
 * Relationships:
 *   payments.customer_id     → customers.id
 *   payments.subscription_id → subscriptions.id
 */
public class Payment {

    private int          id;
    private Customer     customer;        // FK → Customer
    private Subscription subscription;   // FK → Subscription
    private double       amount;
    private String       paymentDate;    // ISO date string: yyyy-MM-dd
    private String       paymentStatus;  // "Paid" | "Pending" | "Failed"

    // ─── No-arg constructor ────────────────────────────────────────
    public Payment() {}

    // ─── Parameterized constructor ─────────────────────────────────
    public Payment(int id, Customer customer, Subscription subscription,
                   double amount, String paymentDate, String paymentStatus) {
        this.id           = id;
        this.customer     = customer;
        this.subscription = subscription;
        this.amount       = amount;
        this.paymentDate  = paymentDate;
        this.paymentStatus= paymentStatus;
    }

    // ─── Getters & Setters ─────────────────────────────────────────
    public int           getId()                               { return id; }
    public void          setId(int id)                         { this.id = id; }

    public Customer      getCustomer()                         { return customer; }
    public void          setCustomer(Customer customer)        { this.customer = customer; }

    public Subscription  getSubscription()                     { return subscription; }
    public void          setSubscription(Subscription s)       { this.subscription = s; }

    public double        getAmount()                           { return amount; }
    public void          setAmount(double amount)              { this.amount = amount; }

    public String        getPaymentDate()                      { return paymentDate; }
    public void          setPaymentDate(String paymentDate)    { this.paymentDate = paymentDate; }

    public String        getPaymentStatus()                    { return paymentStatus; }
    public void          setPaymentStatus(String status)       { this.paymentStatus = status; }

    /** Convenience getters for JSP EL / table rendering */
    public String getCustomerName() { return (customer != null) ? customer.getName() : ""; }
    public String getPlanName()     { return (subscription != null && subscription.getPlan() != null)
                                             ? subscription.getPlan().getPlanName() : ""; }
    public String getFormattedAmount() { return String.format("₹%.0f", amount); }

    @Override
    public String toString() {
        return "Payment{id=" + id + ", customer=" + getCustomerName()
             + ", amount=" + amount + ", status='" + paymentStatus + "'}";
    }
}
