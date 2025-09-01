/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.ListInterface;
import adt.DoublyLinkedList;
import entity.Appointment;
import entity.Consultation;
import entity.Queue;
import entity.Schedule;
import java.time.LocalDate;
import java.util.Iterator;
import utility.ReadWriteFile;

/**
 *
 * @author tiong
 */
public class ConsultationControl {

    private ListInterface<Appointment> appointmentList;
    private ListInterface<Consultation> consultationList;
    private ListInterface<Queue> queueList;
    private ListInterface<Schedule> scheduleList;
    private ReadWriteFile fileManager;
    
    private static final String QUEUES_FILE = "src/main/java/Data File/queues";
    private static final String SCHEDULES_FILE = "src/main/java/Data File/schedules";
    private static final String APPOINTMENTS_FILE = "src/main/java/Data File/appointments";
    private static final String CONSULTATIONS_FILE = "src/main/java/Data File/consultations";
    
    public ConsultationControl(ListInterface<Appointment> appList, ListInterface<Consultation> conList, ListInterface<Queue> qList, ListInterface<Schedule> sList, ReadWriteFile fileManager) {
        this.appointmentList = appList;
        this.consultationList = conList;
        this.queueList = qList;
        this.scheduleList = sList;
        this.fileManager = fileManager;

    }

    // --- Getter Functions ---
    public ListInterface<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public ListInterface<Consultation> getConsultationList() {
        return consultationList;
    }
    
    /**
     * Handles the logic for saving a new consultation for a scheduled appointment.
     * * @param appointment The appointment the consultation is for.
     * @param diagnosis The diagnosis text.
     * @param notes The notes text.
     * @return A String indicating the result.
     */
    public String saveAppointmentConsultation(Appointment appointment, String diagnosis, String notes) {
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            return "Error: Diagnosis cannot be empty.";
        }
        
        // 1. Create and add new Consultation
        String newId = generateNewConsultationId();
        Consultation newConsultation = new Consultation(newId, appointment, diagnosis, notes, LocalDate.now());
        consultationList.add(newConsultation);

        // 2. Update appointment status
        appointment.setStatus("Completed");
        
        updateDoctorAvailability(newConsultation.getDoctor().getDoctorID(), "Available");

        // 3. Save both files
        fileManager.writeConsultationsToFile(CONSULTATIONS_FILE, consultationList);
        fileManager.writeAppointmentsToFile(APPOINTMENTS_FILE, appointmentList);

        return "Success: Consultation " + newId + " saved successfully!";
    }

    /**
     * Handles the logic for saving a new consultation for a walk-in patient from the queue.
     * * @param queue The queue item the consultation is for.
     * @param diagnosis The diagnosis text.
     * @param notes The notes text.
     * @return A String indicating the result.
     */
    public String saveWalkInConsultation(Queue queue, String diagnosis, String notes) {
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            return "Error: Diagnosis cannot be empty.";
        }
        
        // 1. Create and add new Consultation, using the updated constructor
        String newId = generateNewConsultationId();
        Consultation newConsultation = new Consultation(newId, queue, diagnosis, notes, LocalDate.now());
        consultationList.add(newConsultation);

        // 2. Update the queue item's status and exit time.
        // This logic is now handled in the QueueControl class.
        
        updateDoctorAvailability(newConsultation.getDoctor().getDoctorID(), "Available");

        // 3. Save the consultation file. The UI will handle saving the queue file.
        fileManager.writeConsultationsToFile(CONSULTATIONS_FILE, consultationList);

        return "Success: Consultation " + newId + " for walk-in patient saved successfully!";
    }

    /**
     * Handles the logic for editing an existing consultation's diagnosis.
     *
     * @param consultationToEdit The consultation object to modify.
     * @param newDiagnosis The new diagnosis text.
     * @return A String indicating the result.
     */
//    public String editConsultationDiagnosis(Consultation consultationToEdit, String newDiagnosis) {
//        if (newDiagnosis == null || newDiagnosis.trim().isEmpty()) {
//            return "Error: Diagnosis cannot be empty.";
//        }
//
//        // Update the diagnosis in the object
//        consultationToEdit.setDiagnosis(newDiagnosis);
//
//        return "Success: Diagnosis for " + consultationToEdit.getConsultationID() + " updated successfully!";
//    }
    public String editConsultationDiagnosis(Consultation consultationToEdit, String newDiagnosis) {
        if (newDiagnosis == null || newDiagnosis.trim().isEmpty()) {
            return "Error: Diagnosis cannot be empty.";
        }

        // Update the diagnosis in the object
        consultationToEdit.setDiagnosis(newDiagnosis);
        
        fileManager.writeConsultationsToFile(CONSULTATIONS_FILE, consultationList);

        return "Success: Diagnosis for " + consultationToEdit.getConsultationID() + " updated successfully!";
    }

    /**
     * Handles the logic for saving a new consultation.
     *
     * @param appointment The appointment the consultation is for.
     * @param diagnosis The diagnosis text.
     * @param notes The notes text.
     * @return A String indicating the result.
     */
//    public String saveNewConsultation(Appointment appointment, String diagnosis, String notes) {
//        if (diagnosis == null || diagnosis.trim().isEmpty()) {
//            return "Error: Diagnosis cannot be empty.";
//        }
//
//        // 1. Create and add new Consultation
//        String newId = generateNewConsultationId();
//        Consultation newConsultation = new Consultation(newId, appointment, diagnosis, notes, LocalDate.now());
//        consultationList.add(newConsultation);
//
//        // 2. Update appointment status
//        appointment.setStatus("Completed");
//        appointmentList.sort();
//        consultationList.sort();
//        fileManager.writeConsultationsToFile("src/main/java/Data File/consultations", consultationList); // Assuming getter in control
//        fileManager.writeAppointmentsToFile("src/main/java/Data File/appointments", appointmentList); // Assuming getter in control
//
//        return "Success: Consultation " + newId + " saved successfully!";
//    }

    private String generateNewConsultationId() {
        consultationList.sort();

        if (consultationList.isEmpty()) {
            return "C1001";
        }
        Consultation last = consultationList.getEntry(consultationList.getLength());
        int lastIdNum = Integer.parseInt(last.getConsultationID().substring(1));
        return "C" + (lastIdNum + 1);
    }

    public String deleteConsultation(String consultationId) {
        for (int i = 1; i <= consultationList.getLength(); i++) {
            if (consultationList.getEntry(i).getConsultationID().equals(consultationId)) {
                consultationList.remove(i);
                consultationList.sort();
                fileManager.writeConsultationsToFile(CONSULTATIONS_FILE, consultationList);
                return "Success: Consultation " + consultationId + " has been deleted.";
            }
        }
        return "Error: Consultation not found.";
    }

    public ListInterface<Consultation> filterConsultations(String searchText, String searchBy) {
        ListInterface<Consultation> filteredList = new DoublyLinkedList<>();
        Iterator<Consultation> it = consultationList.getIterator();

        while (it.hasNext()) {
            Consultation con = it.next();
            boolean matchesSearch = true;

            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerSearchText = searchText.toLowerCase();
                matchesSearch = switch (searchBy) {
                    case "ID" ->
                        con.getConsultationID().toLowerCase().contains(lowerSearchText);
                    case "Patient Name" ->
                        con.getPatient().getName().toLowerCase().contains(lowerSearchText);
                    case "Doctor Name" ->
                        con.getDoctor().getName().toLowerCase().contains(lowerSearchText);
                    case "Diagnosis" ->
                        con.getDiagnosis().toLowerCase().contains(lowerSearchText);
                    default ->
                        true;
                };
            }

            if (matchesSearch) {
                filteredList.add(con);
            }
        }
        return filteredList;
    }
    
    /**
     * Finds the doctor's schedule and updates their availability status.
     * @param doctorId The ID of the doctor to update.
     * @param newStatus The new status (e.g., "Available", "Busy").
     */
    private void updateDoctorAvailability(String doctorId, String newStatus) {
        Iterator<Schedule> it = scheduleList.getIterator();
        while (it.hasNext()) {
            Schedule s = it.next();
            if (s.getDoctor().getDoctorID().equals(doctorId) && s.getDutyDate().equals(LocalDate.now())) {
                s.setAvailability(newStatus);
                fileManager.writeSchedulesToFile(SCHEDULES_FILE, scheduleList);
                return;
            }
        }
    }
}
