package com.fitnessRevolution.model;

public class AttendanceRecord {
    private String attendanceId;
    private String memberId;
    private String memberName;
    private String date;
    private String checkIn;
    private String checkOut;
    private String status;

    public AttendanceRecord() {}

    public AttendanceRecord(String attendanceId, String memberId, String memberName,
                            String date, String checkIn, String checkOut, String status) {
        this.attendanceId = attendanceId;
        this.memberId     = memberId;
        this.memberName   = memberName;
        this.date         = date;
        this.checkIn      = checkIn;
        this.checkOut     = checkOut;
        this.status       = status;
    }

    public String toCsv() {
        return String.join("|", attendanceId, memberId, memberName,
                date, checkIn, checkOut, status);
    }

    public static AttendanceRecord fromCsv(String csv) {
        String[] p = csv.split("\\|");
        return new AttendanceRecord(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
    }

    public String getAttendanceId() { return attendanceId; }
    public void setAttendanceId(String v) { this.attendanceId = v; }
    public String getMemberId()   { return memberId; }
    public void setMemberId(String v)   { this.memberId = v; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String v) { this.memberName = v; }
    public String getDate()       { return date; }
    public void setDate(String v) { this.date = v; }
    public String getCheckIn()    { return checkIn; }
    public void setCheckIn(String v)    { this.checkIn = v; }
    public String getCheckOut()   { return checkOut; }
    public void setCheckOut(String v)   { this.checkOut = v; }
    public String getStatus()     { return status; }
    public void setStatus(String v)    { this.status = v; }
}
