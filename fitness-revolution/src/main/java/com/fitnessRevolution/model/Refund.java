package com.fitnessRevolution.model;

public class Refund {
    private String refundId;
    private String paymentId;
    private String memberId;
    private String memberName;
    private double amount;
    private String reason;
    private String date;
    private String status;

    public Refund() {}

    public Refund(String refundId, String paymentId, String memberId,
                  String memberName, double amount, String reason,
                  String date, String status) {
        this.refundId   = refundId;
        this.paymentId  = paymentId;
        this.memberId   = memberId;
        this.memberName = memberName;
        this.amount     = amount;
        this.reason     = reason;
        this.date       = date;
        this.status     = status;
    }

    public String toCsv() {
        return String.join("|", refundId, paymentId, memberId,
                memberName, String.valueOf(amount), reason, date, status);
    }

    public static Refund fromCsv(String csv) {
        String[] p = csv.split("\\|");
        return new Refund(p[0], p[1], p[2], p[3],
                Double.parseDouble(p[4]), p[5], p[6], p[7]);
    }

    public String getRefundId()   { return refundId; }
    public void setRefundId(String v)   { this.refundId = v; }
    public String getPaymentId()  { return paymentId; }
    public void setPaymentId(String v)  { this.paymentId = v; }
    public String getMemberId()   { return memberId; }
    public void setMemberId(String v)   { this.memberId = v; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String v) { this.memberName = v; }
    public double getAmount()     { return amount; }
    public void setAmount(double v)    { this.amount = v; }
    public String getReason()     { return reason; }
    public void setReason(String v)    { this.reason = v; }
    public String getDate()       { return date; }
    public void setDate(String v) { this.date = v; }
    public String getStatus()     { return status; }
    public void setStatus(String v)    { this.status = v; }
}