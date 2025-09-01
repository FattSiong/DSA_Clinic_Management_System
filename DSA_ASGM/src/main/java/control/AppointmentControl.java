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
import entity.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import utility.ReadWriteFile;

/**
 *
 * @author tiong
 */
public class AppointmentControl {

    private ListInterface<Appointment> appointmentList;
    private ListInterface<Patient> patientList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Schedule> scheduleList;
    private ReadWriteFile fileManager;

    private static final String PATIENT_FILE = "src/main/java/Data File/patients";
    private static final String APPOINTMENT_FILE = "src/main/java/Data File/appointments";
    private static final String DOCTOR_FILE = "src/main/java/Data File/doctors";

    public AppointmentControl(ListInterface<Appointment> aList, ListInterface<Patient> pList, ListInterface<Doctor> dList, ListInterface<Schedule> sList, ReadWriteFile manager) {
        this.patientList = pList;
        this.doctorList = dList;
        this.appointmentList = aList;
        this.scheduleList = sList;
        this.fileManager = manager;

    }

    /**
     * Filters the main appointment list to get only appointments for today
     *
     * @return A new list containing only the matching appointments for today.
     */
    public ListInterface<Appointment> filterTodaysAppointments() {
        ListInterface<Appointment> todaysList = new DoublyLinkedList<>();
        Iterator<Appointment> it = appointmentList.getIterator();
        LocalDate today = LocalDate.now();

        while (it.hasNext()) {
            Appointment app = it.next();
            // First, check if the appointment is for today
            if (app.getAppointmentDate().equals(today)) {
                todaysList.add(app);
            }
        }
        return todaysList;
    }

    /**
     * Filters the main appointment list to get only appointments for today that
     * match the given search and status criteria.
     *
     * @return A new list containing only the matching appointments for today.
     */
    public ListInterface<Appointment> filterTodaysAppointments(String searchText, String searchBy, String statusFilter) {
        ListInterface<Appointment> todaysList = new DoublyLinkedList<>();
        Iterator<Appointment> it = appointmentList.getIterator();
        LocalDate today = LocalDate.now();

        while (it.hasNext()) {
            Appointment app = it.next();
            // First, check if the appointment is for today
            if (app.getAppointmentDate().equals(today)) {
                boolean matchesSearch = true;
                boolean matchesStatus = statusFilter.equals("All") || app.getStatus().equals(statusFilter);

                if (searchText != null && !searchText.trim().isEmpty()) {
                    String lowerSearchText = searchText.toLowerCase();
                    matchesSearch = switch (searchBy) {
                        case "ID" ->
                            app.getAppointmentID().toLowerCase().contains(lowerSearchText);
                        case "Patient Name" ->
                            app.getPatient().getName().toLowerCase().contains(lowerSearchText);
                        case "Doctor Name" ->
                            app.getDoctor().getName().toLowerCase().contains(lowerSearchText);
                        // No need for a status case here, as it's handled separately
                        default ->
                            true;
                    };
                }

                if (matchesSearch && matchesStatus) {
                    todaysList.add(app);
                }
            }
        }
        return todaysList;
    }

    /**
     * Handles the logic for canceling an appointment.
     *
     * @param appointment The appointment to cancel.
     * @return A String indicating the result.
     */
    public String cancelAppointment(Appointment appointment) {
        if (appointment == null) {
            return "Error: No appointment selected.";
        }
        if (!appointment.getStatus().equals("Scheduled")) {
            return "Error: Only 'Scheduled' appointments can be canceled.";
        }

        appointment.setStatus("Canceled");
        appointmentList.sort();
        fileManager.writeAppointmentsToFile(APPOINTMENT_FILE, appointmentList);
        // The list is updated directly since 'appointment' is a reference to the object in the list.
        return "Success: Appointment " + appointment.getAppointmentID() + " has been canceled.";
    }

    /**
     * Handles the logic for booking a new appointment.
     *
     * @return A String indicating the result (success or a specific error
     * message).
     */
    public String bookAppointment(String patientId, String doctorId, LocalDate date, LocalTime time) {
        // --- Validation ---
        LocalDateTime selectedDateTime = LocalDateTime.of(date, time);
        if (selectedDateTime.isBefore(LocalDateTime.now())) {
            return "Error: Cannot book an appointment in the past.";
        }
        
        if (!isDoctorOnDuty(doctorId, date, time)) {
            return "Error: The selected doctor is not scheduled to be on duty at this time.";
        }

        // --- Business Logic: Check for scheduling conflicts ---
        Iterator<Appointment> it = appointmentList.getIterator();
        while (it.hasNext()) {
            Appointment existingApp = it.next();
            if (existingApp.getDoctor().getDoctorID().equals(doctorId)
                    && existingApp.getAppointmentDate().equals(date)
                    && existingApp.getAppointmentTime().equals(time)
                    && !existingApp.getStatus().equals("Canceled")) { // Ignore canceled appointments
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
        appointmentList.sort();
        fileManager.writeAppointmentsToFile(APPOINTMENT_FILE, appointmentList);

        return "Success: Appointment " + newId + " booked successfully!";
    }
    
    /**
     * A private helper method to check if a doctor is scheduled to work at a
     * given date and time.
     */
    private boolean isDoctorOnDuty(String doctorId, LocalDate date, LocalTime time) {
        Iterator<Schedule> it = scheduleList.getIterator();
        while (it.hasNext()) {
            Schedule s = it.next();
            // Find the schedule for the correct doctor on the correct date
            if (s.getDoctor().getDoctorID().equals(doctorId) && s.getDutyDate().equals(date)) {
                // Check if the appointment time is within the doctor's shift
                // A time is valid if it's NOT before the start AND it IS before the end.
                if (!time.isBefore(s.getStartTime()) && time.isBefore(s.getEndTime())) {
                    return true; // The doctor is on duty.
                }
            }
        }
        return false; // No matching schedule found.
    }

    // --- Helper Methods ---
    private String generateNewAppointmentId() {
        appointmentList.sort();
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
            if (p.getPatientID().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private Doctor findDoctorById(String id) {
        Iterator<Doctor> it = doctorList.getIterator();
        while (it.hasNext()) {
            Doctor d = it.next();
            if (d.getDoctorID().equals(id)) {
                return d;
            }
        }
        return null;
    }

    public Appointment findAppointmentById(String id) {
        Iterator<Appointment> it = appointmentList.getIterator();
        while (it.hasNext()) {
            Appointment app = it.next();
            if (app.getAppointmentID().equals(id)) {
                return app;
            }
        }
        return null;
    }

    public String deleteAppointment(String appointmentId) {
        for (int i = 1; i <= appointmentList.getLength(); i++) {
            if (appointmentList.getEntry(i).getAppointmentID().equals(appointmentId)) {
                appointmentList.remove(i);
                appointmentList.sort();
                fileManager.writeAppointmentsToFile(APPOINTMENT_FILE, appointmentList);
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
                    case "ID" ->
                        app.getAppointmentID().toLowerCase().contains(lowerSearchText);
                    case "Patient Name" ->
                        app.getPatient().getName().toLowerCase().contains(lowerSearchText);
                    case "Doctor Name" ->
                        app.getDoctor().getName().toLowerCase().contains(lowerSearchText);
                    case "Date (YYYY-MM-DD)" ->
                        app.getAppointmentDate().toString().contains(lowerSearchText);
                    default ->
                        true;
                };
            }

            if (matchesSearch && matchesStatus) {
                filteredList.add(app);
            }
        }
        return filteredList;
    }

    /**
     * Handles the logic for editing an existing appointment.
     *
     * @return A String indicating the result.
     */
    public String editAppointment(Appointment appointmentToEdit, String newDoctorId, LocalDate newDate, LocalTime newTime) {
        // --- Validation ---
        LocalDateTime newDateTime = LocalDateTime.of(newDate, newTime);
        if (newDateTime.isBefore(LocalDateTime.now())) {
            return "Error: Cannot schedule an appointment in the past.";
        }
        
        if (!isDoctorOnDuty(newDoctorId, newDate, newTime)) {
            return "Error: The selected doctor is not scheduled to be on duty at the new time.";
        }

        // --- Business Logic: Check for scheduling conflicts ---
        Iterator<Appointment> it = appointmentList.getIterator();
        while (it.hasNext()) {
            Appointment existingApp = it.next();
            // Skip the check if it's the same appointment we are editing
            if (existingApp.getAppointmentID().equals(appointmentToEdit.getAppointmentID())) {
                continue;
            }
            if (existingApp.getDoctor().getDoctorID().equals(newDoctorId)
                    && existingApp.getAppointmentDate().equals(newDate)
                    && existingApp.getAppointmentTime().equals(newTime)
                    && !existingApp.getStatus().equals("Canceled")) {
                return "Error: This time slot is already booked for the selected doctor.";
            }
        }

        // --- Data Manipulation ---
        Doctor newDoctor = findDoctorById(newDoctorId);
        if (newDoctor == null) {
            return "Error: System could not find the selected doctor.";
        }

        appointmentToEdit.setDoctor(newDoctor);
        appointmentToEdit.setAppointmentDate(newDate);
        appointmentToEdit.setAppointmentTime(newTime);

        appointmentList.sort();
        fileManager.writeAppointmentsToFile(APPOINTMENT_FILE, appointmentList);

        return "Success: Appointment " + appointmentToEdit.getAppointmentID() + " updated successfully!";
    }
}
