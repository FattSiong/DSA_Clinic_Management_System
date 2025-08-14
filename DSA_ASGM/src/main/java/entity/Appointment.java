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
public class Appointment {

    private String appointmentID;
    private Patient patient; // Entity knowing another entity
    private Doctor doctor;   // Entity knowing another entity
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status; // e.g., "Scheduled", "Completed", "Canceled"

    public Appointment(String appointmentID, Patient patient, Doctor doctor, LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        this.appointmentID = appointmentID;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters
    public String getAppointmentID() {
        return appointmentID;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment{"
                + "appointmentID='" + appointmentID + '\''
                + ", patient=" + patient.getName()
                + // Displaying name for brevity
                ", doctor=" + doctor.getName()
                + // Displaying name for brevity
                ", appointmentDate=" + appointmentDate
                + ", appointmentTime=" + appointmentTime
                + ", status='" + status + '\''
                + '}';
    }
}
