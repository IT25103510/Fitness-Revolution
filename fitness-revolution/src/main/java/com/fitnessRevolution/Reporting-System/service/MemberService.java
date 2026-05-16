package com.fitnessRevolution.service;

import com.fitnessRevolution.model.Member;
import com.fitnessRevolution.storage.FileStorageUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    public Member addMember(String name, String email, String phone,
                            String address, String membershipType) {
        List<Member> members = loadAll();
        String id = "MBR-" + String.format("%04d", members.size() + 1);
        LocalDate join   = LocalDate.now();
        LocalDate expiry = calcExpiry(join, membershipType);
        Member m = new Member(id, name, email, phone, address,
                membershipType, join.toString(), expiry.toString(), true);
        FileStorageUtil.appendLine(FileStorageUtil.MEMBERS, m.toCsv());
        return m;
    }

    public List<Member> getAllMembers() { return loadAll(); }

    public Optional<Member> getById(String id) {
        return loadAll().stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public List<Member> search(String q) {
        String lq = q.toLowerCase();
        return loadAll().stream().filter(m ->
                        m.getName().toLowerCase().contains(lq) ||
                                m.getEmail().toLowerCase().contains(lq) ||
                                m.getId().toLowerCase().contains(lq))
                .collect(Collectors.toList());
    }

    public boolean update(String id, String name, String email,
                          String phone, String address, String type) {
        List<Member> all = loadAll();
        for (Member m : all) {
            if (m.getId().equals(id)) {
                m.setName(name); m.setEmail(email);
                m.setPhone(phone); m.setAddress(address);
                m.setMembershipType(type);
                m.setExpiryDate(calcExpiry(LocalDate.parse(m.getJoinDate()), type).toString());
                saveAll(all); return true;
            }
        }
        return false;
    }

    public boolean renew(String id) {
        List<Member> all = loadAll();
        for (Member m : all) {
            if (m.getId().equals(id)) {
                LocalDate base = m.isExpired() ? LocalDate.now() : LocalDate.parse(m.getExpiryDate());
                m.setExpiryDate(calcExpiry(base, m.getMembershipType()).toString());
                m.setActive(true);
                saveAll(all); return true;
            }
        }
        return false;
    }

    public boolean toggle(String id) {
        List<Member> all = loadAll();
        for (Member m : all) {
            if (m.getId().equals(id)) {
                m.setActive(!m.isActive());
                saveAll(all); return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        List<Member> all = loadAll();
        boolean ok = all.removeIf(m -> m.getId().equals(id));
        if (ok) saveAll(all);
        return ok;
    }

    public Map<String, Object> getStats() {
        List<Member> all = loadAll();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("total",        all.size());
        s.put("active",       all.stream().filter(m -> "ACTIVE".equals(m.getStatus())).count());
        s.put("expired",      all.stream().filter(m -> "EXPIRED".equals(m.getStatus())).count());
        s.put("expiringSoon", all.stream().filter(m -> "EXPIRING_SOON".equals(m.getStatus())).count());
        s.put("inactive",     all.stream().filter(m -> "INACTIVE".equals(m.getStatus())).count());
        return s;
    }

    private List<Member> loadAll() {
        return FileStorageUtil.readLines(FileStorageUtil.MEMBERS)
                .stream().map(Member::fromCsv).collect(Collectors.toList());
    }

    private void saveAll(List<Member> list) {
        FileStorageUtil.writeLines(FileStorageUtil.MEMBERS,
                list.stream().map(Member::toCsv).toList());
    }

    private LocalDate calcExpiry(LocalDate from, String type) {
        return switch (type) {
            case "YEARLY"  -> from.plusYears(1);
            case "STUDENT" -> from.plusMonths(6);
            case "FAMILY"  -> from.plusYears(1);
            default        -> from.plusMonths(1);
        };
    }
}
