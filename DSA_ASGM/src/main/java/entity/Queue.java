/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.time.LocalTime;

/**
 *
 * @author tiong
 */
public class Queue implements Comparable<Queue> {

    private int queueId;
    private int queueNumber;
    private Patient patient; // Link to the patient
    private Doctor doctor;
    private LocalTime entryTime;
    private LocalTime exitTime;
    private String status; // e.g., "Waiting", "In Consultation", "Completed"

    /**
     * Constructor for a new queue entry. Only the persistent ID is stored.
     * @param queueId
     * @param patient
     */
    public Queue(int queueId, Patient patient) {
        this.queueId = queueId;
        this.patient = patient;
        this.doctor = null;
        this.entryTime = LocalTime.now();
        this.exitTime = null;
        this.status = "Waiting";
    }

    /**
     * Full constructor for recreating objects from the data file.
     * @param queueId
     * @param queueNumber
     * @param patient
     * @param doctor
     * @param entryTime
     * @param exitTime
     * @param status
     */
    public Queue(int queueId, int queueNumber, Patient patient, Doctor doctor, LocalTime entryTime, LocalTime exitTime, String status) {
    this.queueId = queueId;
    this.queueNumber = queueNumber;
    this.patient = patient;
    this.doctor = doctor;
    this.entryTime = entryTime;
    this.exitTime = exitTime;
    this.status = status;
}

    // Getters
    public int getQueueId() {
        return queueId;
    }
    
    public int getQueueNumber() {
        return queueNumber;
    }

    public Patient getPatient() {
        return patient;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }

    public LocalTime getEntryTime() {
        return entryTime;
    }

    public LocalTime getExitTime() {
        return exitTime;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }
    
    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalTime exitTime) {
        this.exitTime = exitTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(Queue other) {
        return Integer.compare(this.queueId, other.getQueueId());
    }

    @Override
    public String toString() {
        return "Queue{"
                + "queueId=" + queueId
                + ", queueNumber=" + queueNumber
                + ", patient=" + patient.getName()
                + ", doctor=" + (doctor != null ? doctor.getName() : "N/A")
                + ", entryTime=" + entryTime
                + ", exitTime=" + exitTime
                + ", status='" + status + '\''
                + '}';
    }
}