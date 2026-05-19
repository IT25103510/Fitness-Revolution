package com.fitnessRevolution.controller;


import com.fitnessRevolution.model.MembershipType;
import com.fitnessRevolution.service.MembershipTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST API for MembershipType CRUD.
 *
 * Endpoints:
 *   GET    /api/membership-types          → getAll()
 *   GET    /api/membership-types/active   → getActive()
 *   GET    /api/membership-types/stats    → getStats()
 *   GET    /api/membership-types/{code}   → getOne()
 *   POST   /api/membership-types          → create()
 *   PUT    /api/membership-types/{code}   → update()
 *   PUT    /api/membership-types/{code}/toggle → toggle active/inactive
 *   DELETE /api/membership-types/{code}   → delete()
 */
@RestController
@RequestMapping("/api/membership-types")
@CrossOrigin(origins = "*")
public class MembershipTypeController {

    @Autowired
    private MembershipTypeService service;

    // ── GET ALL ──────────────────────────────────────────────────────────
    @GetMapping
    public List<MembershipType> getAll() {
        return service.getAll();
    }

    // ── GET ACTIVE ONLY ──────────────────────────────────────────────────
    @GetMapping("/active")
    public List<MembershipType> getActive() {
        return service.getActive();
    }

    // ── GET STATS ────────────────────────────────────────────────────────
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return service.getStats();
    }

    // ── GET ONE ──────────────────────────────────────────────────────────
    @GetMapping("/{code}")
    public ResponseEntity<?> getOne(@PathVariable String code) {
        return service.getByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── CREATE ───────────────────────────────────────────────────────────
    /**
     * Request body (JSON):
     * {
     *   "code": "PREMIUM",
     *   "name": "Premium",
     *   "price": 5000,
     *   "durationMonths": 1,
     *   "description": "Premium access with personal trainer"
     * }
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            String code        = (String) body.get("code");
            String name        = (String) body.get("name");
            double price       = Double.parseDouble(body.get("price").toString());
            int durationMonths = Integer.parseInt(body.get("durationMonths").toString());
            String description = (String) body.getOrDefault("description", "");

            if (code == null || code.isBlank())
                return ResponseEntity.badRequest().body("Code required.");
            if (name == null || name.isBlank())
                return ResponseEntity.badRequest().body("Name required.");
            if (price <= 0)
                return ResponseEntity.badRequest().body("Price must be > 0.");
            if (durationMonths <= 0)
                return ResponseEntity.badRequest().body("Duration must be > 0 months.");

            MembershipType created = service.create(code, name, price, durationMonths, description);
            return ResponseEntity.ok(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    // ── UPDATE ───────────────────────────────────────────────────────────
    /**
     * Request body (JSON) — code path variable ekei update karanne:
     * {
     *   "name": "Monthly Plus",
     *   "price": 3500,
     *   "durationMonths": 1,
     *   "description": "Updated description"
     * }
     */
    @PutMapping("/{code}")
    public ResponseEntity<?> update(@PathVariable String code,
                                    @RequestBody Map<String, Object> body) {
        try {
            String name        = (String) body.get("name");
            double price       = Double.parseDouble(body.get("price").toString());
            int durationMonths = Integer.parseInt(body.get("durationMonths").toString());
            String description = (String) body.getOrDefault("description", "");

            if (name == null || name.isBlank())
                return ResponseEntity.badRequest().body("Name required.");
            if (price <= 0)
                return ResponseEntity.badRequest().body("Price must be > 0.");
            if (durationMonths <= 0)
                return ResponseEntity.badRequest().body("Duration must be > 0 months.");

            boolean ok = service.update(code, name, price, durationMonths, description);
            return ok ? ResponseEntity.ok("Updated successfully.")
                    : ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    // ── TOGGLE ACTIVE / INACTIVE ──────────────────────────────────────────
    @PutMapping("/{code}/toggle")
    public ResponseEntity<?> toggle(@PathVariable String code) {
        boolean ok = service.toggle(code);
        return ok ? ResponseEntity.ok("Status toggled.")
                : ResponseEntity.notFound().build();
    }

    // ── DELETE ───────────────────────────────────────────────────────────
    @DeleteMapping("/{code}")
    public ResponseEntity<?> delete(@PathVariable String code) {
        boolean ok = service.delete(code);
        return ok ? ResponseEntity.ok("Deleted successfully.")
                : ResponseEntity.notFound().build();
    }
}