package com.fitnessRevolution.model;

public class TrainingSession {
    private String sessionId;
    private String trainerId;
    private String trainerName;
    private String memberId;
    private String memberName;
    private String date;
    private String timeSlot;
    private String type;
    private String status;

    public TrainingSession() {}

    public TrainingSession(String sessionId, String trainerId, String trainerName,
                           String memberId, String memberName, String date,
                           String timeSlot, String type, String status) {
        this.sessionId   = sessionId;
        this.trainerId   = trainerId;
        this.trainerName = trainerName;
        this.memberId    = memberId;
        this.memberName  = memberName;
        this.date        = date;
        this.timeSlot    = timeSlot;
        this.type        = type;
        this.status      = status;
    }

    public String toCsv() {
        return String.join("|", sessionId, trainerId, trainerName,
                memberId, memberName, date, timeSlot, type, status);
    }

    public static TrainingSession fromCsv(String csv) {
        String[] p = csv.split("\\|");
        return new TrainingSession(p[0], p[1], p[2], p[3], p[4],
                p[5], p[6], p[7], p[8]);
    }

    public String getSessionId()   { return sessionId; }
    public void setSessionId(String v)   { this.sessionId = v; }
    public String getTrainerId()   { return trainerId; }
    public void setTrainerId(String v)   { this.trainerId = v; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String v) { this.trainerName = v; }
    public String getMemberId()    { return memberId; }
    public void setMemberId(String v)    { this.memberId = v; }
    public String getMemberName()  { return memberName; }
    public void setMemberName(String v)  { this.memberName = v; }
    public String getDate()        { return date; }
    public void setDate(String v)  { this.date = v; }
    public String getTimeSlot()    { return timeSlot; }
    public void setTimeSlot(String v)    { this.timeSlot = v; }
    public String getType()        { return type; }
    public void setType(String v)  { this.type = v; }
    public String getStatus()      { return status; }
    public void setStatus(String v)     { this.status = v; }
}
