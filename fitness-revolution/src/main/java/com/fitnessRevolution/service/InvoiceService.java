package com.fitnessRevolution.service;

import com.fitnessRevolution.model.Invoice;
import com.fitnessRevolution.model.Payment;
import com.fitnessRevolution.storage.FileStorageUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    public Invoice generate(Payment p) {
        List<Invoice> all = loadAll();
        String id = "INV-" + String.format("%05d", all.size() + 1);
        Invoice inv = new Invoice(id, p.getPaymentId(), p.getMemberId(),
                p.getMemberName(), p.getPlan(), p.getAmount(),
                p.getDate(), p.getDueDate(), "PAID");
        FileStorageUtil.appendLine(FileStorageUtil.INVOICES, inv.toCsv());
        return inv;
    }

    public List<Invoice> getAll() { return loadAll(); }

    public List<Invoice> getOverdue() {
        return loadAll().stream().filter(Invoice::isOverdue).collect(Collectors.toList());
    }

    public List<Invoice> sendReminders() {
        List<Invoice> overdue = getOverdue();
        overdue.forEach(Invoice::sendReminder);
        return overdue;
    }

    private List<Invoice> loadAll() {
        return FileStorageUtil.readLines(FileStorageUtil.INVOICES)
                .stream().map(Invoice::fromCsv).collect(Collectors.toList());
    }
}
