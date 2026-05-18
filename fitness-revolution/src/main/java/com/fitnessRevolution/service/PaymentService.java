package com.fitnessRevolution.service;

import com.fitnessRevolution.model.Invoice;
import com.fitnessRevolution.model.Payment;
import com.fitnessRevolution.model.Refund;
import com.fitnessRevolution.storage.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private MembershipTypeService membershipTypeService;

    // ── Process payment ───────────────────────────────────────────
    public Payment processPayment(String memberId, String memberName,
                                  String plan, String method) {
        if (memberId == null || memberId.isBlank())
            throw new RuntimeException("Member ID is required");


        com.fitnessRevolution.model.MembershipType mType =
                membershipTypeService.getByCode(plan)
                        .orElseThrow(() -> new RuntimeException("Invalid plan: " + plan));

        List<Payment> all = loadAll();
        String id     = "PAY-" + String.format("%05d", all.size() + 1);
        String date   = LocalDate.now().toString();
        String due    = LocalDate.now().plusMonths(mType.getDurationMonths()).toString();
        double amount = mType.getPrice();

        Payment p = new Payment(id, memberId, memberName,
                plan.toUpperCase(), method, amount, "SUCCESS", date, due);
        FileStorageUtil.appendLine(FileStorageUtil.PAYMENTS, p.toCsv());

        // Auto-generate invoice
        invoiceService.generate(p);
        return p;
    }

    // ── Process refund ────────────────────────────────────────────
    public Refund processRefund(String paymentId, String reason) {
        List<Payment> payments = loadAll();
        Payment p = payments.stream()
                .filter(x -> x.getPaymentId().equals(paymentId))
                .findFirst().orElse(null);
        if (p == null) return null;

        // Mark payment as refunded
        p.setStatus("REFUNDED");
        saveAll(payments);

        List<Refund> refunds = loadRefunds();
        String id   = "REF-" + String.format("%05d", refunds.size() + 1);
        String date = LocalDate.now().toString();
        Refund r = new Refund(id, paymentId, p.getMemberId(),
                p.getMemberName(), p.getAmount(), reason, date, "PROCESSED");
        FileStorageUtil.appendLine(FileStorageUtil.REFUNDS, r.toCsv());
        return r;
    }

    // ── Get all payments ──────────────────────────────────────────
    public List<Payment> getAll() { return loadAll(); }

    // ── Get all refunds ───────────────────────────────────────────
    public List<Refund> getAllRefunds() { return loadRefunds(); }

    // ── Delete payment ────────────────────────────────────────────
    public boolean delete(String paymentId) {
        List<Payment> all = loadAll();
        boolean ok = all.removeIf(p -> p.getPaymentId().equals(paymentId));
        if (ok) saveAll(all);
        return ok;
    }

    // ── Stats ─────────────────────────────────────────────────────
    public Map<String, Object> getStats() {
        List<Payment> all = loadAll();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("total",    all.size());
        s.put("revenue",  all.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount).sum());
        s.put("refunded", all.stream()
                .filter(p -> "REFUNDED".equals(p.getStatus())).count());
        s.put("overdue",  all.stream()
                .filter(Payment::isOverdue).count());
        return s;
    }

    // ── Private helpers ───────────────────────────────────────────
    private List<Payment> loadAll() {
        return FileStorageUtil.readLines(FileStorageUtil.PAYMENTS)
                .stream().map(Payment::fromCsv).collect(Collectors.toList());
    }

    private void saveAll(List<Payment> list) {
        FileStorageUtil.writeLines(FileStorageUtil.PAYMENTS,
                list.stream().map(Payment::toCsv).toList());
    }

    private List<Refund> loadRefunds() {
        return FileStorageUtil.readLines(FileStorageUtil.REFUNDS)
                .stream().map(Refund::fromCsv).collect(Collectors.toList());
    }
}