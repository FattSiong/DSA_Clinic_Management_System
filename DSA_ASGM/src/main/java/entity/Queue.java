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
public class Queue {

    private int queueNumber;
    private Patient patient; // Link to the patient
    private LocalTime entryTime;
    private String status; // e.g., "Waiting", "In Consultation", "Completed"

    public Queue(int queueNumber, Patient patient, LocalTime entryTime, String status) {
        this.queueNumber = queueNumber;
        this.patient = patient;
        this.entryTime = entryTime;
        this.status = status;
    }

    // Getters
    public int getQueueNumber() {
        return queueNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalTime getEntryTime() {
        return entryTime;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Queue{"
                + "queueNumber=" + queueNumber
                + ", patient=" + patient.getName()
                + ", entryTime=" + entryTime
                + ", status='" + status + '\''
                + '}';
    }
}
