/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalDate;

/**
 *
 * @author tiong
 */
public class Consultation {

    private String consultationID;
    private Patient patient;         // Direct link to the patient
    private Doctor doctor;           // Direct link to the doctor
    private Appointment appointment; // This can be null for walk-ins
    private String diagnosis;
    private String notes;
    private LocalDate consultationDate;

    // --- Constructor for SCHEDULED patients ---
    public Consultation(String consultationID, Appointment appointment, String diagnosis, String notes, LocalDate consultationDate) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null for a scheduled consultation.");
        }
        this.consultationID = consultationID;
        this.appointment = appointment;
        this.patient = appointment.getPatient(); // Get patient from the appointment
        this.doctor = appointment.getDoctor();   // Get doctor from the appointment
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.consultationDate = consultationDate;
    }

    // --- Constructor for WALK-IN patients ---
    public Consultation(String consultationID, Patient patient, Doctor doctor, String diagnosis, String notes, LocalDate consultationDate) {
        if (patient == null || doctor == null) {
            throw new IllegalArgumentException("Patient and Doctor cannot be null for a walk-in consultation.");
        }
        this.consultationID = consultationID;
        this.appointment = null; // No appointment for walk-ins
        this.patient = patient;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.consultationDate = consultationDate;
    }

    // Getters
    public String getConsultationID() {
        return consultationID;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Appointment getAppointment() {
        return appointment;
    } // Can return null

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getConsultationDate() {
        return consultationDate;
    }

    // Setters...
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        // Check if there's an appointment ID to display
        String appointmentIdStr = (appointment != null) ? appointment.getAppointmentID() : "N/A (Walk-in)";

        return "Consultation{"
                + "consultationID='" + consultationID + '\''
                + ", patient=" + patient.getName()
                + ", doctor=" + doctor.getName()
                + ", appointmentID=" + appointmentIdStr
                + ", diagnosis='" + diagnosis + '\''
                + '}';
    }
}
