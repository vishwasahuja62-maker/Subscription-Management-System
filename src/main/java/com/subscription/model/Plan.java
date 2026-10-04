package com.subscription.model;

import java.util.List;
import java.util.ArrayList;

/**
 * JavaBean representing a Subscription Plan.
 * Maps to the 'plans' table in MySQL (future JDBC phase).
 */
public class Plan {

    private int          id;
    private String       planName;
    private String       description;
    private int          duration;
    private String       durationUnit;   // "Days" | "Months" | "Years"
    private double       price;
    private List<String> features;
    private String       status;         // "Active" | "Inactive"

    // ─── No-arg constructor ────────────────────────────────────────
    public Plan() {
        this.features = new ArrayList<>();
    }

    // ─── Parameterized constructor ─────────────────────────────────
    public Plan(int id, String planName, String description, int duration,
                String durationUnit, double price, List<String> features, String status) {
        this.id           = id;
        this.planName     = planName;
        this.description  = description;
        this.duration     = duration;
        this.durationUnit = durationUnit;
        this.price        = price;
        this.features     = (features != null) ? features : new ArrayList<>();
        this.status       = status;
    }

    // ─── Getters & Setters ─────────────────────────────────────────
    public int          getId()                          { return id; }
    public void         setId(int id)                    { this.id = id; }

    public String       getPlanName()                    { return planName; }
    public void         setPlanName(String planName)     { this.planName = planName; }

    public String       getDescription()                 { return description; }
    public void         setDescription(String desc)      { this.description = desc; }

    public int          getDuration()                    { return duration; }
    public void         setDuration(int duration)        { this.duration = duration; }

    public String       getDurationUnit()                { return durationUnit; }
    public void         setDurationUnit(String unit)     { this.durationUnit = unit; }

    public double       getPrice()                       { return price; }
    public void         setPrice(double price)           { this.price = price; }

    public List<String> getFeatures()                   { return features; }
    public void         setFeatures(List<String> features){ this.features = features; }

    public String       getStatus()                      { return status; }
    public void         setStatus(String status)         { this.status = status; }

    /** Convenience: formatted price string */
    public String getFormattedPrice() {
        return String.format("₹%.0f", price);
    }

    @Override
    public String toString() {
        return "Plan{id=" + id + ", planName='" + planName + "', price=" + price + ", status='" + status + "'}";
    }
}
