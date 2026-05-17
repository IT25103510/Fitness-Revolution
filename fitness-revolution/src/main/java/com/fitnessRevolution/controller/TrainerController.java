package com.fitnessRevolution.controller;

import com.fitnessRevolution.model.Trainer;
import com.fitnessRevolution.model.TrainingSession;
import com.fitnessRevolution.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/trainers")
@CrossOrigin(origins = "*")
public class TrainerController {

    @Autowired private TrainerService service;

    @GetMapping           public List<Trainer> getAll() { return service.getAll(); }
    @GetMapping("/stats") public Map<String,Object> stats() { return service.getStats(); }
    @GetMapping("/available") public List<Trainer> available() { return service.getAvailable(); }
    @GetMapping("/sessions")  public List<TrainingSession> sessions() { return service.getAllSessions(); }

    @GetMapping("/search")
    public List<Trainer> search(@RequestParam String q) { return service.search(q); }

    @GetMapping("/{id}/sessions")
    public List<TrainingSession> sessionsByTrainer(@PathVariable String id) {
        return service.getSessionsByTrainer(id);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Map<String,String> b) {
        try {
            return ResponseEntity.ok(service.add(b.get("name"), b.get("email"),
                    b.get("phone"), b.get("specialization"),
                    Integer.parseInt(b.get("experienceYears")),
                    Double.parseDouble(b.get("monthlyFee"))));
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Map<String,String> b) {
        boolean ok = service.update(id, b.get("name"), b.get("email"), b.get("phone"),
                b.get("specialization"), Integer.parseInt(b.get("experienceYears")),
                Double.parseDouble(b.get("monthlyFee")));
        return ok ? ResponseEntity.ok("Updated") : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable String id) {
        return service.toggle(id) ? ResponseEntity.ok("Toggled") : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return service.delete(id) ? ResponseEntity.ok("Deleted") : ResponseEntity.notFound().build();
    }

    @PostMapping("/sessions")
    public ResponseEntity<?> bookSession(@RequestBody Map<String,String> b) {
        try {
            return ResponseEntity.ok(service.bookSession(b.get("trainerId"),
                    b.get("memberId"), b.get("memberName"),
                    b.get("date"), b.get("timeSlot"), b.get("type")));
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PutMapping("/sessions/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody Map<String,String> b) {
        return service.updateSessionStatus(id, b.get("status"))
                ? ResponseEntity.ok("Updated") : ResponseEntity.notFound().build();
    }

    @PutMapping("/sessions/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        return service.cancelSession(id)
                ? ResponseEntity.ok("Cancelled") : ResponseEntity.notFound().build();
    }
}
