package com.fitnessRevolution.service;

import com.fitnessRevolution.model.Trainer;
import com.fitnessRevolution.model.TrainingSession;
import com.fitnessRevolution.storage.FileStorageUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainerService {

    public Trainer add(String name, String email, String phone,
                       String specialization, int exp, double fee) {
        List<Trainer> all = loadAll();
        String id = "TRN-" + String.format("%04d", all.size() + 1);
        Trainer t = new Trainer(id, name, email, phone, specialization,
                exp, fee, true, LocalDate.now().toString());
        FileStorageUtil.appendLine(FileStorageUtil.TRAINERS, t.toCsv());
        return t;
    }

    public List<Trainer> getAll() { return loadAll(); }

    public Optional<Trainer> getById(String id) {
        return loadAll().stream().filter(t -> t.getTrainerId().equals(id)).findFirst();
    }

    public List<Trainer> getAvailable() {
        return loadAll().stream().filter(Trainer::isAvailable).collect(Collectors.toList());
    }

    public List<Trainer> search(String q) {
        String lq = q.toLowerCase();
        return loadAll().stream().filter(t ->
                        t.getName().toLowerCase().contains(lq) ||
                                t.getSpecialization().toLowerCase().contains(lq))
                .collect(Collectors.toList());
    }

    public boolean update(String id, String name, String email, String phone,
                          String spec, int exp, double fee) {
        List<Trainer> all = loadAll();
        for (Trainer t : all) {
            if (t.getTrainerId().equals(id)) {
                t.setName(name); t.setEmail(email); t.setPhone(phone);
                t.setSpecialization(spec); t.setExperienceYears(exp); t.setMonthlyFee(fee);
                saveAll(all); return true;
            }
        }
        return false;
    }

    public boolean toggle(String id) {
        List<Trainer> all = loadAll();
        for (Trainer t : all) {
            if (t.getTrainerId().equals(id)) {
                t.setAvailable(!t.isAvailable());
                saveAll(all); return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        List<Trainer> all = loadAll();
        boolean ok = all.removeIf(t -> t.getTrainerId().equals(id));
        if (ok) saveAll(all);
        return ok;
    }

    public TrainingSession bookSession(String trainerId, String memberId,
                                       String memberName, String date,
                                       String timeSlot, String type) {
        List<Trainer> trainers = loadAll();
        Trainer trainer = trainers.stream()
                .filter(t -> t.getTrainerId().equals(trainerId))
                .findFirst().orElseThrow(() -> new RuntimeException("Trainer not found"));
        if (!trainer.isAvailable()) throw new RuntimeException("Trainer unavailable");
        List<TrainingSession> sessions = loadSessions();
        String id = "SES-" + String.format("%05d", sessions.size() + 1);
        TrainingSession s = new TrainingSession(id, trainerId, trainer.getName(),
                memberId, memberName, date, timeSlot, type, "SCHEDULED");
        FileStorageUtil.appendLine(FileStorageUtil.SESSIONS, s.toCsv());
        return s;
    }

    public List<TrainingSession> getAllSessions() { return loadSessions(); }

    public List<TrainingSession> getSessionsByTrainer(String trainerId) {
        return loadSessions().stream()
                .filter(s -> s.getTrainerId().equals(trainerId))
                .collect(Collectors.toList());
    }

    public boolean updateSessionStatus(String sessionId, String status) {
        List<TrainingSession> all = loadSessions();
        for (TrainingSession s : all) {
            if (s.getSessionId().equals(sessionId)) {
                s.setStatus(status);
                saveSessions(all); return true;
            }
        }
        return false;
    }

    public boolean cancelSession(String sessionId) {
        return updateSessionStatus(sessionId, "CANCELLED");
    }

    public Map<String, Object> getStats() {
        List<Trainer> trainers = loadAll();
        List<TrainingSession> sessions = loadSessions();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("total",         trainers.size());
        s.put("available",     trainers.stream().filter(Trainer::isAvailable).count());
        s.put("totalSessions", sessions.size());
        s.put("completed",     sessions.stream().filter(x -> "COMPLETED".equals(x.getStatus())).count());
        s.put("scheduled",     sessions.stream().filter(x -> "SCHEDULED".equals(x.getStatus())).count());
        s.put("cancelled",     sessions.stream().filter(x -> "CANCELLED".equals(x.getStatus())).count());
        return s;
    }

    private List<Trainer> loadAll() {
        return FileStorageUtil.readLines(FileStorageUtil.TRAINERS)
                .stream().map(Trainer::fromCsv).collect(Collectors.toList());
    }

    private void saveAll(List<Trainer> list) {
        FileStorageUtil.writeLines(FileStorageUtil.TRAINERS,
                list.stream().map(Trainer::toCsv).toList());
    }

    private List<TrainingSession> loadSessions() {
        return FileStorageUtil.readLines(FileStorageUtil.SESSIONS)
                .stream().map(TrainingSession::fromCsv).collect(Collectors.toList());
    }

    private void saveSessions(List<TrainingSession> list) {
        FileStorageUtil.writeLines(FileStorageUtil.SESSIONS,
                list.stream().map(TrainingSession::toCsv).toList());
    }
}
