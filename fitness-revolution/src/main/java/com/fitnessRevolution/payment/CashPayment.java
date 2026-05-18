package com.fitnessRevolution.payment;

import com.fitnessRevolution.model.Payment;
import com.fitnessRevolution.model.Refund;

public class CashPayment extends PaymentProcessor {
    private static long counter = 0;
    private static long refundCounter = 0;

    public CashPayment() { super("Cash Payment"); }

    @Override
    public Payment execute(String memberId, String memberName,
                           String plan, double amount, String date, String dueDate) {
        if (!isValidAmount(amount)) return null;
        return new Payment(generateId("PAY-CASH-", counter++),
                memberId, memberName, plan, "CASH", amount, "SUCCESS", date, dueDate);
    }

    @Override
    public Refund refund(String paymentId, String memberId, String memberName,
                         double amount, String reason, String date) {
        return new Refund(generateId("REF-CASH-", refundCounter++),
                paymentId, memberId, memberName, -amount, reason, date, "PROCESSED");
    }
}