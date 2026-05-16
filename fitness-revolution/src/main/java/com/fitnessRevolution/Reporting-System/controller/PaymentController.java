package com.fitnessRevolution.controller;

import com.fitnessRevolution.model.Invoice;
import com.fitnessRevolution.model.Payment;
import com.fitnessRevolution.model.Refund;
import com.fitnessRevolution.service.InvoiceService;
import com.fitnessRevolution.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired private PaymentService paymentService;
    @Autowired private InvoiceService invoiceService;

    @GetMapping
    public List<Payment> getAll() { return paymentService.getAll(); }

    @GetMapping("/stats")
    public Map<String,Object> stats() { return paymentService.getStats(); }

    @GetMapping("/invoices")
    public List<Invoice> invoices() { return invoiceService.getAll(); }

    @GetMapping("/invoices/overdue")
    public List<Invoice> overdue() { return invoiceService.sendReminders(); }

    @GetMapping("/refunds")
    public List<Refund> refunds() { return paymentService.getAllRefunds(); }

    @GetMapping("/member/{memberId}")
    public List<Payment> byMember(@PathVariable String memberId) {
        return paymentService.getAll().stream()
                .filter(p -> p.getMemberId().equals(memberId))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> pay(@RequestBody Map<String,String> b) {
        try {
            return ResponseEntity.ok(paymentService.processPayment(
                    b.get("memberId"), b.get("memberName"),
                    b.get("plan"), b.get("method")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refund(@PathVariable String id,
                                    @RequestBody Map<String,String> b) {
        Refund r = paymentService.processRefund(id, b.get("reason"));
        return r != null ? ResponseEntity.ok(r) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return paymentService.delete(id)
                ? ResponseEntity.ok("Deleted")
                : ResponseEntity.notFound().build();
    }
}