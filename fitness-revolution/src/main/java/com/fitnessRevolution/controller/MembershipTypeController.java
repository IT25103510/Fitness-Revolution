package com.fitnessRevolution.controller;


import com.fitnessRevolution.model.MembershipType;
import com.fitnessRevolution.service.MembershipTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/membership-types")
@CrossOrigin(origins = "*")
public class MembershipTypeController {

    @Autowired
    private MembershipTypeService service;

    
    @GetMapping
    public List<MembershipType> getAll() {
        return service.getAll();
    }

    
    @GetMapping("/active")
    public List<MembershipType> getActive() {
        return service.getActive();
    }

   
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return service.getStats();
    }

   
    @GetMapping("/{code}")
    public ResponseEntity<?> getOne(@PathVariable String code) {
        return service.getByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
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

   
    @PutMapping("/{code}/toggle")
    public ResponseEntity<?> toggle(@PathVariable String code) {
        boolean ok = service.toggle(code);
        return ok ? ResponseEntity.ok("Status toggled.")
                : ResponseEntity.notFound().build();
    }

    
    @DeleteMapping("/{code}")
    public ResponseEntity<?> delete(@PathVariable String code) {
        boolean ok = service.delete(code);
        return ok ? ResponseEntity.ok("Deleted successfully.")
                : ResponseEntity.notFound().build();
    }
}
