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
public class Consultation implements Comparable<Consultation> {

    private String consultationID;
    private Appointment appointment; // For scheduled patients
    private Queue queue; // For walk-in patients
    private String diagnosis;
    private String notes;
    private LocalDate consultationDate;

    // --- Constructor for SCHEDULED patients (from an Appointment) ---
    public Consultation(String consultationID, Appointment appointment, String diagnosis, String notes, LocalDate consultationDate) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null for a scheduled consultation.");
        }
        this.consultationID = consultationID;
        this.appointment = appointment;
        this.queue = null;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.consultationDate = consultationDate;
    }

    // --- Constructor for WALK-IN patients (from a Queue) ---
    public Consultation(String consultationID, Queue queue, String diagnosis, String notes, LocalDate consultationDate) {
        if (queue == null) {
            throw new IllegalArgumentException("Queue cannot be null for a walk-in consultation.");
        }
        this.consultationID = consultationID;
        this.appointment = null; // No appointment for walk-ins
        this.queue = queue;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.consultationDate = consultationDate;
    }

    // Getters
    public String getConsultationID() {
        return consultationID;
    }

    public Patient getPatient() {
        if (appointment != null) {
            return appointment.getPatient();
        } else if (queue != null) {
            return queue.getPatient();
        }
        return null;
    }

    public Doctor getDoctor() {
        if (appointment != null) {
            return appointment.getDoctor();
        } else if (queue != null) {
            return queue.getDoctor();
        }
        return null;
    }
    
    public Appointment getAppointment() {
        return appointment;
    }

    public Queue getQueue() {
        return queue;
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
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public int compareTo(Consultation other) {
        return this.consultationID.compareTo(other.getConsultationID());
    }

    @Override
    public String toString() {
        String patientName = (getPatient() != null) ? getPatient().getName() : "N/A";
        String doctorName = (getDoctor() != null) ? getDoctor().getName() : "N/A";
        String sourceId = (appointment != null) ? "Appointment ID: " + appointment.getAppointmentID() : "Queue No: " + queue.getQueueNumber();

        return "Consultation{"
                + "consultationID='" + consultationID + '\''
                + ", patient=" + patientName
                + ", doctor=" + doctorName
                + ", source=" + sourceId
                + ", diagnosis='" + diagnosis + '\''
                + ", notes='" + notes + '\''
                + '}';
    }
}
