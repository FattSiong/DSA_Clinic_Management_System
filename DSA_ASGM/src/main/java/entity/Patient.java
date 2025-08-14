/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author tiong
 */
public class Patient {

    private String patientID;
    private String name;
    private String icNumber;
    private String contactNumber;
    private String email;
    private String medicalHistory;

    public Patient(String patientID, String name, String icNumber, String contactNumber, String email, String medicalHistory) {
        this.patientID = patientID;
        this.name = name;
        this.icNumber = icNumber;
        this.contactNumber = contactNumber;
        this.email = email;
        this.medicalHistory = medicalHistory; // Alergy or somethings
    }

    // Getters
    public String getPatientID() {
        return patientID;
    }

    public String getName() {
        return name;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    // Setters
    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    @Override
    public String toString() {
        return "Patient{"
                + "patientID='" + patientID + '\''
                + ", name='" + name + '\''
                + ", icNumber='" + icNumber + '\''
                + ", contactNumber='" + contactNumber + '\''
                + ", email='" + email + '\''
                + ", medicalHistory='" + medicalHistory + '\''
                + '}';
    }
}
