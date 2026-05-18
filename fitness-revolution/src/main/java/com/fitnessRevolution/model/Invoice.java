package com.fitnessRevolution.model;

public class Invoice {
    private String invoiceId;
    private String paymentId;
    private String memberId;
    private String memberName;
    private String plan;
    private double amount;
    private String issueDate;
    private String dueDate;
    private String status;

    public Invoice() {}

    public Invoice(String invoiceId, String paymentId, String memberId,
                   String memberName, String plan, double amount,
                   String issueDate, String dueDate, String status) {
        this.invoiceId  = invoiceId;
        this.paymentId  = paymentId;
        this.memberId   = memberId;
        this.memberName = memberName;
        this.plan       = plan;
        this.amount     = amount;
        this.issueDate  = issueDate;
        this.dueDate    = dueDate;
        this.status     = status;
    }

    public boolean isOverdue() {
        return java.time.LocalDate.now()
                .isAfter(java.time.LocalDate.parse(dueDate))
                && !"PAID".equals(status);
    }

    public void sendReminder() {
        System.out.println("REMINDER: Invoice " + invoiceId
                + " overdue for " + memberName);
    }

    public String toCsv() {
        return String.join("|", invoiceId, paymentId, memberId,
                memberName, plan, String.valueOf(amount),
                issueDate, dueDate, status);
    }

    public static Invoice fromCsv(String csv) {
        String[] p = csv.split("\\|");
        return new Invoice(p[0], p[1], p[2], p[3], p[4],
                Double.parseDouble(p[5]), p[6], p[7], p[8]);
    }

    public String getInvoiceId()  { return invoiceId; }
    public void setInvoiceId(String v)  { this.invoiceId = v; }
    public String getPaymentId()  { return paymentId; }
    public void setPaymentId(String v)  { this.paymentId = v; }
    public String getMemberId()   { return memberId; }
    public void setMemberId(String v)   { this.memberId = v; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String v) { this.memberName = v; }
    public String getPlan()       { return plan; }
    public void setPlan(String v) { this.plan = v; }
    public double getAmount()     { return amount; }
    public void setAmount(double v)    { this.amount = v; }
    public String getIssueDate()  { return issueDate; }
    public void setIssueDate(String v) { this.issueDate = v; }
    public String getDueDate()    { return dueDate; }
    public void setDueDate(String v)   { this.dueDate = v; }
    public String getStatus()     { return status; }
    public void setStatus(String v)    { this.status = v; }
}

