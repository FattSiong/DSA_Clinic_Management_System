/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author tiong
 */
public class Schedule implements Comparable<Schedule> {
    private String scheduleID;
    private Doctor doctor; // Link to the doctor
    private LocalDate dutyDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String availability; // e.g., "Available", "On Leave"

    public Schedule(String scheduleID, Doctor doctor, LocalDate dutyDate, LocalTime startTime, LocalTime endTime, String availability) {
        this.scheduleID = scheduleID;
        this.doctor = doctor;
        this.dutyDate = dutyDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.availability = availability;
    }

    // Getters
    public String getScheduleID() {
        return scheduleID;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDate getDutyDate() {
        return dutyDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getAvailability() {
        return availability;
    }

    // Setters
    public void setScheduleID(String scheduleID) {
        this.scheduleID = scheduleID;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setDutyDate(LocalDate dutyDate) {
        this.dutyDate = dutyDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
    @Override
    public int compareTo(Schedule other) {
        return this.scheduleID.compareTo(other.getScheduleID());
    }

    @Override
    public String toString() {
        return "Schedule{"
                + "scheduleID='" + scheduleID + '\''
                + ", doctor=" + doctor.getName()
                + ", dutyDate=" + dutyDate
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", availability='" + availability + '\''
                + '}';
    }
}
