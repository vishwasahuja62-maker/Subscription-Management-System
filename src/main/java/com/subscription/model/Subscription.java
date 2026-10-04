package com.subscription.model;

/**
 * JavaBean representing a Customer Subscription.
 * Maps to the 'subscriptions' table in MySQL (future JDBC phase).
 *
 * Relationships:
 *   subscriptions.customer_id → customers.id
 *   subscriptions.plan_id     → plans.id
 */
public class Subscription {

    private int      id;
    private Customer customer;       // FK → Customer
    private Plan     plan;           // FK → Plan
    private String   startDate;      // ISO date string: yyyy-MM-dd
    private String   endDate;
    private String   status;         // "Active" | "Expiring" | "Inactive"
    private boolean  autoRenewal;

    // ─── No-arg constructor ────────────────────────────────────────
    public Subscription() {}

    // ─── Parameterized constructor ─────────────────────────────────
    public Subscription(int id, Customer customer, Plan plan,
                        String startDate, String endDate,
                        String status, boolean autoRenewal) {
        this.id          = id;
        this.customer    = customer;
        this.plan        = plan;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.status      = status;
        this.autoRenewal = autoRenewal;
    }

    // ─── Getters & Setters ─────────────────────────────────────────
    public int          getId()                            { return id; }
    public void         setId(int id)                      { this.id = id; }

    public Customer     getCustomer()                      { return customer; }
    public void         setCustomer(Customer customer)     { this.customer = customer; }

    public Plan         getPlan()                          { return plan; }
    public void         setPlan(Plan plan)                 { this.plan = plan; }

    public String       getStartDate()                     { return startDate; }
    public void         setStartDate(String startDate)     { this.startDate = startDate; }

    public String       getEndDate()                       { return endDate; }
    public void         setEndDate(String endDate)         { this.endDate = endDate; }

    public String       getStatus()                        { return status; }
    public void         setStatus(String status)           { this.status = status; }

    public boolean      isAutoRenewal()                    { return autoRenewal; }
    public void         setAutoRenewal(boolean autoRenewal){ this.autoRenewal = autoRenewal; }

    /** Convenience getters for JSP EL expressions */
    public String getCustomerName() { return (customer != null) ? customer.getName() : ""; }
    public String getPlanName()     { return (plan     != null) ? plan.getPlanName() : ""; }

    @Override
    public String toString() {
        return "Subscription{id=" + id + ", customer=" + getCustomerName()
             + ", plan=" + getPlanName() + ", status='" + status + "'}";
    }
}
