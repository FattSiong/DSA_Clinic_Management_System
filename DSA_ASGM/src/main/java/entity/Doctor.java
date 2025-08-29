/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author tiong
 */
public class Doctor implements Comparable<Doctor> {

    private String doctorID;
    private String name;
    private String contactNumber;
    private String roomNumber;

    public Doctor(String doctorID, String name, String contactNumber, String roomNumber) {
        this.doctorID = doctorID;
        this.name = name;
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

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public int compareTo(Doctor other) {
        return this.doctorID.compareTo(other.getDoctorID());
    }
    
    @Override
    public String toString() {
        return "Doctor{"
                + "doctorID='" + doctorID + '\''
                + ", name='" + name + '\''
                + ", contactNumber='" + contactNumber + '\''
                + ", roomNumber='" + roomNumber + '\''
                + '}';
    }
}
