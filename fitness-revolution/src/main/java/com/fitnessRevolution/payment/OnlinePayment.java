package com.fitnessRevolution.payment;

import com.fitnessRevolution.model.Payment;
import com.fitnessRevolution.model.Refund;

public class OnlinePayment extends PaymentProcessor {
    private static long counter = 0;
    private static long refundCounter = 0;

    public OnlinePayment() { super("Online Payment"); }

    @Override
    public Payment execute(String memberId, String memberName,
                           String plan, double amount, String date, String dueDate) {
        if (!isValidAmount(amount)) return null;
        return new Payment(generateId("PAY-ONL-", counter++),
                memberId, memberName, plan, "ONLINE", amount, "SUCCESS", date, dueDate);
    }

    @Override
    public Refund refund(String paymentId, String memberId, String memberName,
                         double amount, String reason, String date) {
        return new Refund(generateId("REF-ONL-", refundCounter++),
                paymentId, memberId, memberName, -amount, reason, date, "PROCESSED");
    }
}
