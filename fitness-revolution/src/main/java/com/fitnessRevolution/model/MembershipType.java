package com.fitnessRevolution.model;

/**
 * MembershipType model – file storage use karanna pipe (|) CSV format.
 * Format: code|name|price|durationMonths|description|active
 * Example: MONTHLY|Regular|3000.0|1|Basic gym access for one month|true
 */
public class MembershipType {

    private String code;           // Unique key: MONTHLY, YEARLY, etc.
    private String name;           // Display name: "Monthly"
    private double price;          // Price in Rs.
    private int durationMonths;    // Duration in months (12 = yearly)
    private String description;    // Short description: "Every month"
    private boolean active;        // Active/inactive

    public MembershipType() {}

    public MembershipType(String code, String name, double price,
                          int durationMonths, String description, boolean active) {
        this.code           = code.toUpperCase().trim();
        this.name           = name;
        this.price          = price;
        this.durationMonths = durationMonths;
        this.description    = description;
        this.active         = active;
    }

    // ── CSV Serialization ──────────────────────────────────────────────────
    public String toCsv() {
        return String.join("|",
                code,
                name,
                String.valueOf(price),
                String.valueOf(durationMonths),
                description,
                String.valueOf(active));
    }

    public static MembershipType fromCsv(String csv) {
        String[] p = csv.split("\\|", -1);
        return new MembershipType(
                p[0],
                p[1],
                Double.parseDouble(p[2]),
                Integer.parseInt(p[3]),
                p[4],
                Boolean.parseBoolean(p[5])
        );
    }

    // ── Getters & Setters ──────────────────────────────────────────────────
    public String  getCode()                       { return code; }
    public void    setCode(String code)            { this.code = code.toUpperCase().trim(); }

    public String  getName()                       { return name; }
    public void    setName(String name)            { this.name = name; }

    public double  getPrice()                      { return price; }
    public void    setPrice(double price)          { this.price = price; }

    public int     getDurationMonths()             { return durationMonths; }
    public void    setDurationMonths(int d)        { this.durationMonths = d; }

    public String  getDescription()               { return description; }
    public void    setDescription(String d)       { this.description = d; }

    public boolean isActive()                     { return active; }
    public void    setActive(boolean active)      { this.active = active; }
}