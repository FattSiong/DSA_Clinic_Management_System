/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.Objects;

/**
 *
 * @author tiong
 */
public class Patient implements Comparable<Patient> {

    private String patientID;
    private String name;
    private String gender;
    private int age;
    private String icNumber;
    private String contactNumber;
    private String email;
    private String medicalHistory;

    public Patient(String patientID, String name, String gender, int age, String icNumber, String contactNumber, String email, String medicalHistory) {
        this.patientID = patientID;
        this.name = name;
        this.gender = gender;
        this.age = age;
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

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
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

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Patient other = (Patient) obj;

        return Objects.equals(this.patientID, other.patientID);
    }
    
    @Override
    public int compareTo(Patient other) {
        return this.patientID.compareTo(other.getPatientID());
    }

    @Override
    public String toString() {
        return "Patient{"
                + "patientID='" + patientID + '\''
                + ", name='" + name + '\''
                + ", gender='" + gender + '\''
                + ", age='" + age + '\''
                + ", icNumber='" + icNumber + '\''
                + ", contactNumber='" + contactNumber + '\''
                + ", email='" + email + '\''
                + ", medicalHistory='" + medicalHistory + '\''
                + '}';
    }
}
