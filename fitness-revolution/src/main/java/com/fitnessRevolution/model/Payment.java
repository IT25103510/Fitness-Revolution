package com.fitnessRevolution.model;


public class Payment {
    private String paymentId;
    private String memberId;
    private String memberName;
    private String plan;
    private String method;
    private double amount;
    private String status;
    private String date;
    private String dueDate;

    public Payment() {}

    public Payment(String paymentId, String memberId, String memberName,
                   String plan, String method, double amount,
                   String status, String date, String dueDate) {
        this.paymentId  = paymentId;
        this.memberId   = memberId;
        this.memberName = memberName;
        this.plan       = plan;
        this.method     = method;
        this.amount     = amount;
        this.status     = status;
        this.date       = date;
        this.dueDate    = dueDate;
    }

    public boolean isOverdue() {
        return java.time.LocalDate.now()
                .isAfter(java.time.LocalDate.parse(dueDate))
                && !"SUCCESS".equals(status);
    }

    public String toCsv() {
        return String.join("|", paymentId, memberId, memberName,
                plan, method, String.valueOf(amount), status, date, dueDate);
    }

    public static Payment fromCsv(String csv) {
        String[] p = csv.split("\\|");
        return new Payment(p[0], p[1], p[2], p[3], p[4],
                Double.parseDouble(p[5]), p[6], p[7], p[8]);
    }

    public String getPaymentId()  { return paymentId; }
    public void setPaymentId(String v) { this.paymentId = v; }
    public String getMemberId()   { return memberId; }
    public void setMemberId(String v)  { this.memberId = v; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String v){ this.memberName = v; }
    public String getPlan()       { return plan; }
    public void setPlan(String v) { this.plan = v; }
    public String getMethod()     { return method; }
    public void setMethod(String v)    { this.method = v; }
    public double getAmount()     { return amount; }
    public void setAmount(double v)    { this.amount = v; }
    public String getStatus()     { return status; }
    public void setStatus(String v)    { this.status = v; }
    public String getDate()       { return date; }
    public void setDate(String v) { this.date = v; }
    public String getDueDate()    { return dueDate; }
    public void setDueDate(String v)   { this.dueDate = v; }
}
