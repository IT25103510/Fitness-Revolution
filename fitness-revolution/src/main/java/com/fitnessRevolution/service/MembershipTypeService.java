package com.fitnessRevolution.service;

import com.fitnessRevolution.model.MembershipType;
import com.fitnessRevolution.storage.FileStorageUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MembershipTypeService {

    private static final List<MembershipType> DEFAULTS = List.of(
            new MembershipType("MONTHLY",  "Monthly",  3000.0,  1,  "Standard monthly membership", true),
            new MembershipType("YEARLY",   "Yearly",   25000.0, 12, "Full year – best value",       true),
            new MembershipType("STUDENT",  "Student",  1500.0,  6,  "6-month student plan",         true),
            new MembershipType("FAMILY",   "Family",   20000.0, 12, "Up to 4 family members",       true)
    );

    public MembershipType create(String code, String name, double price,
                                 int durationMonths, String description) {
        List<MembershipType> all = loadAll();

        String upperCode = code.toUpperCase().trim();
        boolean exists = all.stream().anyMatch(t -> t.getCode().equals(upperCode));
        if (exists) {
            throw new IllegalArgumentException(
                    "Membership type code '" + upperCode + "' already exists.");
        }

        MembershipType newType = new MembershipType(
                upperCode, name, price, durationMonths, description, true);
        FileStorageUtil.appendLine(FileStorageUtil.MEMBERSHIP_TYPES, newType.toCsv());
        return newType;
    }

    public List<MembershipType> getAll() {
        return loadAll();
    }

    public List<MembershipType> getActive() {
        return loadAll().stream()
                .filter(MembershipType::isActive)
                .collect(Collectors.toList());
    }

    public Optional<MembershipType> getByCode(String code) {
        return loadAll().stream()
                .filter(t -> t.getCode().equals(code.toUpperCase().trim()))
                .findFirst();
    }

    public boolean update(String code, String name, double price,
                          int durationMonths, String description) {
        List<MembershipType> all = loadAll();
        String upperCode = code.toUpperCase().trim();

        for (MembershipType t : all) {
            if (t.getCode().equals(upperCode)) {
                t.setName(name);
                t.setPrice(price);
                t.setDurationMonths(durationMonths);
                t.setDescription(description);
                saveAll(all);
                return true;
            }
        }
        return false;
    }

    public boolean toggle(String code) {
        List<MembershipType> all = loadAll();
        String upperCode = code.toUpperCase().trim();

        for (MembershipType t : all) {
            if (t.getCode().equals(upperCode)) {
                t.setActive(!t.isActive());
                saveAll(all);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String code) {
        List<MembershipType> all = loadAll();
        String upperCode = code.toUpperCase().trim();
        boolean removed = all.removeIf(t -> t.getCode().equals(upperCode));
        if (removed) saveAll(all);
        return removed;
    }

    public Map<String, Object> getStats() {
        List<MembershipType> all = loadAll();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total",    all.size());
        stats.put("active",   all.stream().filter(MembershipType::isActive).count());
        stats.put("inactive", all.stream().filter(t -> !t.isActive()).count());
        stats.put("minPrice", all.stream().mapToDouble(MembershipType::getPrice).min().orElse(0));
        stats.put("maxPrice", all.stream().mapToDouble(MembershipType::getPrice).max().orElse(0));
        return stats;
    }

    private List<MembershipType> loadAll() {
        List<String> lines = FileStorageUtil.readLines(FileStorageUtil.MEMBERSHIP_TYPES);
        if (lines.isEmpty()) {
            seedDefaults();
            return new ArrayList<>(DEFAULTS);
        }
        return lines.stream()
                .map(MembershipType::fromCsv)
                .collect(Collectors.toList());
    }

    private void saveAll(List<MembershipType> list) {
        FileStorageUtil.writeLines(FileStorageUtil.MEMBERSHIP_TYPES,
                list.stream().map(MembershipType::toCsv).toList());
    }

    private void seedDefaults() {
        FileStorageUtil.writeLines(FileStorageUtil.MEMBERSHIP_TYPES,
                DEFAULTS.stream().map(MembershipType::toCsv).toList());
    }
}