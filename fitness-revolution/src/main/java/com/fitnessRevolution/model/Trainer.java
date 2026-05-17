package com.fitnessRevolution.model;

public class Trainer {
    private String trainerId;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private int experienceYears;
    private double monthlyFee;
    private boolean available;
    private String joinDate;

    public Trainer() {}

    public Trainer(String trainerId, String name, String email, String phone,
                   String specialization, int experienceYears,
                   double monthlyFee, boolean available, String joinDate) {
        this.trainerId       = trainerId;
        this.name            = name;
        this.email           = email;
        this.phone           = phone;
        this.specialization  = specialization;
        this.experienceYears = experienceYears;
        this.monthlyFee      = monthlyFee;
        this.available       = available;
        this.joinDate        = joinDate;
    }

    public String getExpertiseLevel() {
        if (experienceYears >= 10) return "SENIOR";
        if (experienceYears >= 5)  return "MID";
        return "JUNIOR";
    }

    public String toCsv() {
        return String.join("|", trainerId, name, email, phone,
                specialization, String.valueOf(experienceYears),
                String.valueOf(monthlyFee), String.valueOf(available), joinDate);
    }

    public static Trainer fromCsv(String csv) {
        String[] p = csv.split("\\|");
        return new Trainer(p[0], p[1], p[2], p[3], p[4],
                Integer.parseInt(p[5]), Double.parseDouble(p[6]),
                Boolean.parseBoolean(p[7]), p[8]);
    }

    public String getTrainerId()    { return trainerId; }
    public void setTrainerId(String v)    { this.trainerId = v; }
    public String getName()         { return name; }
    public void setName(String v)   { this.name = v; }
    public String getEmail()        { return email; }
    public void setEmail(String v)  { this.email = v; }
    public String getPhone()        { return phone; }
    public void setPhone(String v)  { this.phone = v; }
    public String getSpecialization()    { return specialization; }
    public void setSpecialization(String v) { this.specialization = v; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int v) { this.experienceYears = v; }
    public double getMonthlyFee()   { return monthlyFee; }
    public void setMonthlyFee(double v)   { this.monthlyFee = v; }
    public boolean isAvailable()    { return available; }
    public void setAvailable(boolean v)   { this.available = v; }
    public String getJoinDate()     { return joinDate; }
    public void setJoinDate(String v)     { this.joinDate = v; }
}