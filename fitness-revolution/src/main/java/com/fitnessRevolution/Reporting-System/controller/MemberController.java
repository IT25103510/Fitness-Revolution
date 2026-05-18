package com.fitnessRevolution.controller;

import com.fitnessRevolution.model.Member;
import com.fitnessRevolution.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired private MemberService service;

    @GetMapping           public List<Member> getAll() { return service.getAllMembers(); }
    @GetMapping("/stats") public Map<String,Object> stats() { return service.getStats(); }

    @GetMapping("/{id}")
    

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Map<String,String> b) {
        try {
            return ResponseEntity.ok(service.addMember(b.get("name"), b.get("email"),
                    b.get("phone"), b.get("address"), b.get("membershipType")));
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Map<String,String> b) {
        boolean ok = service.update(id, b.get("name"), b.get("email"),
                b.get("phone"), b.get("address"), b.get("membershipType"));
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
}
