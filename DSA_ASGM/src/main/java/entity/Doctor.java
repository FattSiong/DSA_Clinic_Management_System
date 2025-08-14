/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author tiong
 */
public class Doctor {

    private String doctorID;
    private String name;
    private String specialization; // what medical specialty
    private String contactNumber;
    private String roomNumber;

    public Doctor(String doctorID, String name, String specialization, String contactNumber, String roomNumber) {
        this.doctorID = doctorID;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.roomNumber = roomNumber;
    }

    // Getters
    public String getDoctorID() {
        return doctorID;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    // Setters
    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public String toString() {
        return "Doctor{"
                + "doctorID='" + doctorID + '\''
                + ", name='" + name + '\''
                + ", specialization='" + specialization + '\''
                + ", contactNumber='" + contactNumber + '\''
                + ", roomNumber='" + roomNumber + '\''
                + '}';
    }
}
