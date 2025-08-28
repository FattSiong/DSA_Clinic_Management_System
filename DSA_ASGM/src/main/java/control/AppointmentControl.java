/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.ListInterface;
import adt.DoublyLinkedList;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;

/**
 *
 * @author tiong
 */
public class AppointmentControl {

    private ListInterface<Appointment> appointmentList;
    private ListInterface<Patient> patientList;
    private ListInterface<Doctor> doctorList;

    public AppointmentControl(ListInterface<Appointment> aList, ListInterface<Patient> pList, ListInterface<Doctor> dList) {
        this.appointmentList = aList;
        this.patientList = pList;
        this.doctorList = dList;
    }

    /**
     * Handles the logic for booking a new appointment.
     * @return A String indicating the result (success or a specific error message).
     */
    public String bookAppointment(String patientId, String doctorId, LocalDate date, LocalTime time) {
        // --- Validation ---
        LocalDateTime selectedDateTime = LocalDateTime.of(date, time);
        if (selectedDateTime.isBefore(LocalDateTime.now())) {
            return "Error: Cannot book an appointment in the past.";
        }

        // --- Business Logic: Check for scheduling conflicts ---
        Iterator<Appointment> it = appointmentList.getIterator();
        while (it.hasNext()) {
            Appointment existingApp = it.next();
            if (existingApp.getDoctor().getDoctorID().equals(doctorId) &&
                existingApp.getAppointmentDate().equals(date) &&
                existingApp.getAppointmentTime().equals(time) &&
                !existingApp.getStatus().equals("Canceled")) { // Ignore canceled appointments
                return "Error: This time slot is already booked for the selected doctor.";
            }
        }

        // --- Data Manipulation ---
        Patient patient = findPatientById(patientId);
        Doctor doctor = findDoctorById(doctorId);
        if (patient == null || doctor == null) {
            return "Error: System could not find the selected patient or doctor.";
        }

        String newId = generateNewAppointmentId();
        Appointment newAppointment = new Appointment(newId, patient, doctor, date, time, "Scheduled");
        appointmentList.add(newAppointment);

        return "Success: Appointment " + newId + " booked successfully!";
    }

    // --- Helper Methods ---
    private String generateNewAppointmentId() {
        if (appointmentList.isEmpty()) {
            return "A1001";
        }
        Appointment lastApp = appointmentList.getEntry(appointmentList.getLength());
        int lastIdNum = Integer.parseInt(lastApp.getAppointmentID().substring(1));
        return "A" + (lastIdNum + 1);
    }

    private Patient findPatientById(String id) {
        Iterator<Patient> it = patientList.getIterator();
        while (it.hasNext()) {
            Patient p = it.next();
            if (p.getPatientID().equals(id)) return p;
        }
        return null;
    }

    private Doctor findDoctorById(String id) {
        Iterator<Doctor> it = doctorList.getIterator();
        while (it.hasNext()) {
            Doctor d = it.next();
            if (d.getDoctorID().equals(id)) return d;
        }
        return null;
    }
    
    public String deleteAppointment(String appointmentId) {
        for (int i = 1; i <= appointmentList.getLength(); i++) {
            if (appointmentList.getEntry(i).getAppointmentID().equals(appointmentId)) {
                appointmentList.remove(i);
                return "Success: Appointment " + appointmentId + " has been deleted.";
            }
        }
        return "Error: Appointment not found.";
    }
    
    public ListInterface<Appointment> filterAppointments(String searchText, String searchBy, String filterStatus) {
        ListInterface<Appointment> filteredList = new DoublyLinkedList<>();
        Iterator<Appointment> it = appointmentList.getIterator();

        while (it.hasNext()) {
            Appointment app = it.next();
            boolean matchesSearch = true;
            boolean matchesStatus = true;

            // Check status filter
            if (!filterStatus.equals("All") && !app.getStatus().equals(filterStatus)) {
                matchesStatus = false;
            }

            // Check search text
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerSearchText = searchText.toLowerCase();
                matchesSearch = switch (searchBy) {
                    case "ID" -> app.getAppointmentID().toLowerCase().contains(lowerSearchText);
                    case "Patient Name" -> app.getPatient().getName().toLowerCase().contains(lowerSearchText);
                    case "Doctor Name" -> app.getDoctor().getName().toLowerCase().contains(lowerSearchText);
                    case "Date (YYYY-MM-DD)" -> app.getAppointmentDate().toString().contains(lowerSearchText);
                    default -> true;
                };
            }

            if (matchesSearch && matchesStatus) {
                filteredList.add(app);
            }
        }
        return filteredList;
    }
}
