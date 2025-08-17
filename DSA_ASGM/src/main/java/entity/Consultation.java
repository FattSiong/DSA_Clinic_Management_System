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
    private Appointment appointment; // Link to the appointment
    private String diagnosis;
    private String notes;
    private LocalDate consultationDate;

    public Consultation(String consultationID, Appointment appointment, String diagnosis, String notes, LocalDate consultationDate) {
        this.consultationID = consultationID;
        this.appointment = appointment;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.consultationDate = consultationDate;
    }

    // Getters
    public String getConsultationID() {
        return consultationID;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getConsultationDate() {
        return consultationDate;
    }

    // Setters
    public void setConsultationID(String consultationID) {
        this.consultationID = consultationID;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setConsultationDate(LocalDate consultationDate) {
        this.consultationDate = consultationDate;
    }

    @Override
    public String toString() {
        return "Consultation{"
                + "consultationID='" + consultationID + '\''
                + ", appointmentID=" + appointment.getAppointmentID()
                + ", diagnosis='" + diagnosis + '\''
                + ", notes='" + notes + '\''
                + ", consultationDate=" + consultationDate
                + '}';
    }
}
