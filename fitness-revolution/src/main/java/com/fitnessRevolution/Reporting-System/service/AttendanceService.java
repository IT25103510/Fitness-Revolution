package com.fitnessRevolution.service;

import com.fitnessRevolution.model.AttendanceRecord;
import com.fitnessRevolution.storage.FileStorageUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    public AttendanceRecord checkIn(String memberId, String memberName) {
        List<AttendanceRecord> all = loadAll();
        String today = LocalDate.now().toString();
        boolean alreadyIn = all.stream().anyMatch(a ->
                a.getMemberId().equals(memberId) && a.getDate().equals(today)
                        && "PRESENT".equals(a.getStatus()));
        if (alreadyIn) throw new RuntimeException("Already checked in today");
        String id = "ATT-" + String.format("%05d", all.size() + 1);
        AttendanceRecord rec = new AttendanceRecord(id, memberId, memberName,
                today, LocalTime.now().toString().substring(0, 5), "-", "PRESENT");
        FileStorageUtil.appendLine(FileStorageUtil.ATTENDANCE, rec.toCsv());
        return rec;
    }

    public boolean checkOut(String memberId) {
        List<AttendanceRecord> all = loadAll();
        String today = LocalDate.now().toString();
        for (AttendanceRecord r : all) {
            if (r.getMemberId().equals(memberId) && r.getDate().equals(today)
                    && "PRESENT".equals(r.getStatus())) {
                r.setCheckOut(LocalTime.now().toString().substring(0, 5));
                r.setStatus("COMPLETED");
                saveAll(all); return true;
            }
        }
        return false;
    }

    public List<AttendanceRecord> getAll() { return loadAll(); }

    public List<AttendanceRecord> getByMember(String memberId) {
        return loadAll().stream()
                .filter(a -> a.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public List<AttendanceRecord> getToday() {
        String today = LocalDate.now().toString();
        return loadAll().stream()
                .filter(a -> a.getDate().equals(today))
                .collect(Collectors.toList());
    }

    public boolean delete(String attendanceId) {
        List<AttendanceRecord> all = loadAll();
        boolean ok = all.removeIf(a -> a.getAttendanceId().equals(attendanceId));
        if (ok) saveAll(all);
        return ok;
    }

    public Map<String, Object> getStats() {
        List<AttendanceRecord> all = loadAll();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("total",     all.size());
        s.put("today",     getToday().size());
        s.put("present",   all.stream().filter(a -> "PRESENT".equals(a.getStatus())).count());
        s.put("completed", all.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count());
        return s;
    }

    private List<AttendanceRecord> loadAll() {
        return FileStorageUtil.readLines(FileStorageUtil.ATTENDANCE)
                .stream().map(AttendanceRecord::fromCsv).collect(Collectors.toList());
    }

    private void saveAll(List<AttendanceRecord> list) {
        FileStorageUtil.writeLines(FileStorageUtil.ATTENDANCE,
                list.stream().map(AttendanceRecord::toCsv).toList());
    }
}
