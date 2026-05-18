package com.fitnessRevolution.model;


import java.time.LocalDate;

public class Member {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String membershipType;
    private String joinDate;
    private String expiryDate;
    private boolean active;

    public Member() {}

    public Member(String id, String name, String email, String phone,
                  String address, String membershipType,
                  String joinDate, String expiryDate, boolean active) {
        this.id             = id;
        this.name           = name;
        this.email          = email;
        this.phone          = phone;
        this.address        = address;
        this.membershipType = membershipType;
        this.joinDate       = joinDate;
        this.expiryDate     = expiryDate;
        this.active         = active;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(LocalDate.parse(expiryDate));
    }

    public long daysUntilExpiry() {
        return java.time.temporal.ChronoUnit.DAYS
                .between(LocalDate.now(), LocalDate.parse(expiryDate));
    }

    public String getStatus() {
        if (!active) return "INACTIVE";
        if (isExpired()) return "EXPIRED";
        if (daysUntilExpiry() <= 7) return "EXPIRING_SOON";
        return "ACTIVE";
    }

    public String toCsv() {
        return String.join("|", id, name, email, phone, address,
                membershipType, joinDate, expiryDate, String.valueOf(active));
    }

    public static Member fromCsv(String csv) {
        String[] p = csv.split("\\|");
        return new Member(p[0], p[1], p[2], p[3], p[4],
                p[5], p[6], p[7], Boolean.parseBoolean(p[8]));
    }

    public String getId()             { return id; }
    public void setId(String id)      { this.id = id; }
    public String getName()           { return name; }
    public void setName(String n)     { this.name = n; }
    public String getEmail()          { return email; }
    public void setEmail(String e)    { this.email = e; }
    public String getPhone()          { return phone; }
    public void setPhone(String p)    { this.phone = p; }
    public String getAddress()        { return address; }
    public void setAddress(String a)  { this.address = a; }
    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String t) { this.membershipType = t; }
    public String getJoinDate()       { return joinDate; }
    public void setJoinDate(String d) { this.joinDate = d; }
    public String getExpiryDate()     { return expiryDate; }
    public void setExpiryDate(String d) { this.expiryDate = d; }
    public boolean isActive()         { return active; }
    public void setActive(boolean a)  { this.active = a; }
}