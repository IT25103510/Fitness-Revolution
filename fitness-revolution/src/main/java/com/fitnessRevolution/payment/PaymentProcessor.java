package com.fitnessRevolution.payment;

import com.fitnessRevolution.model.Payment;
import com.fitnessRevolution.model.Refund;

public abstract class PaymentProcessor {
    protected String processorName;

    public PaymentProcessor(String processorName) {
        this.processorName = processorName;
    }

    public abstract Payment execute(String memberId, String memberName,
                                    String plan, double amount,
                                    String date, String dueDate);

    public abstract Refund refund(String paymentId, String memberId,
                                  String memberName, double amount,
                                  String reason, String date);

    protected boolean isValidAmount(double amount) { return amount > 0; }

    protected String generateId(String prefix, long count) {
        return prefix + String.format("%05d", count + 1);
    }
}
