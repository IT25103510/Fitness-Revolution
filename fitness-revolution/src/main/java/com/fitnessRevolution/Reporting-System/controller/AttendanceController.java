package com.fitnessRevolution.controller;

import com.fitnessRevolution.model.AttendanceRecord;
import com.fitnessRevolution.service.AttendanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService service;

    // ── Weekly attendance (last 7 days) ──────────────────────────
    @GetMapping("/weekly")
    public List<Map<String, Object>> getWeekly() {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("date",  date.toString());
            entry.put("day",   date.getDayOfWeek().toString().substring(0, 3));
            entry.put("count", getCountForDate(date));
            result.add(entry);
        }
        return result;
    }

    private long getCountForDate(LocalDate date) {
        return service.getAll().stream()
                .filter(a -> a.getDate() != null && a.getDate().equals(date.toString()))
                .count();
    }

    // ── All attendance records ────────────────────────────────────
    @GetMapping
    public List<AttendanceRecord> getAll() {
        return service.getAll();
    }

    // ── Today's attendance ────────────────────────────────────────
    @GetMapping("/today")
    public List<AttendanceRecord> today() {
        return service.getToday();                   // ✅ fix: getToday()
    }

    // ── Stats ─────────────────────────────────────────────────────
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return service.getStats();
    }

    // ── By member ─────────────────────────────────────────────────
    @GetMapping("/member/{memberId}")
    public List<AttendanceRecord> byMember(@PathVariable String memberId) {  // ✅ fix: String
        return service.getByMember(memberId);        // ✅ fix: getByMember()
    }

    // ── Check-in ──────────────────────────────────────────────────
    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody Map<String, String> b) {
        try {
            String memberId   = b.get("memberId");
            String memberName = b.get("memberName");
            if (memberId == null || memberId.isBlank()) {
                return ResponseEntity.badRequest().body("Member ID is required");
            }
            AttendanceRecord rec = service.checkIn(memberId, memberName); // ✅ fix: 2 params
            return ResponseEntity.ok(rec);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Check-out ─────────────────────────────────────────────────
    @PutMapping("/checkout/{memberId}")
    public ResponseEntity<?> checkOut(@PathVariable String memberId) {
        return service.checkOut(memberId)
                ? ResponseEntity.ok("Checked out")
                : ResponseEntity.notFound().build();
    }

    // ── Delete ────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return service.delete(id)
                ? ResponseEntity.ok("Deleted")
                : ResponseEntity.notFound().build();
    }
}
