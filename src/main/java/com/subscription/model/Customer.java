package com.subscription.model;

/**
 * JavaBean representing a Customer.
 * Maps to the 'customers' table in MySQL (future JDBC phase).
 */
public class Customer {

    private int    id;
    private String name;
    private String email;
    private String phone;
    private String dateOfBirth;
    private String address;
    private String status;       // "Active" | "Inactive"

    // ─── No-arg constructor ────────────────────────────────────────
    public Customer() {}

    // ─── Parameterized constructor ─────────────────────────────────
    public Customer(int id, String name, String email, String phone,
                    String dateOfBirth, String address, String status) {
        this.id          = id;
        this.name        = name;
        this.email       = email;
        this.phone       = phone;
        this.dateOfBirth = dateOfBirth;
        this.address     = address;
        this.status      = status;
    }

    // ─── Getters & Setters ─────────────────────────────────────────
    public int     getId()                          { return id; }
    public void    setId(int id)                    { this.id = id; }

    public String  getName()                        { return name; }
    public void    setName(String name)             { this.name = name; }

    public String  getEmail()                       { return email; }
    public void    setEmail(String email)           { this.email = email; }

    public String  getPhone()                       { return phone; }
    public void    setPhone(String phone)           { this.phone = phone; }

    public String  getDateOfBirth()                 { return dateOfBirth; }
    public void    setDateOfBirth(String dateOfBirth){ this.dateOfBirth = dateOfBirth; }

    public String  getAddress()                     { return address; }
    public void    setAddress(String address)       { this.address = address; }

    public String  getStatus()                      { return status; }
    public void    setStatus(String status)         { this.status = status; }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + name + "', email='" + email + "', status='" + status + "'}";
    }
}
