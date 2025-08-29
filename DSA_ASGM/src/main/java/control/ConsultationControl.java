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
import java.time.LocalDate;
import java.time.LocalTime;
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
    private ReadWriteFile fileManager;

    public ConsultationControl(ListInterface<Appointment> appList, ListInterface<Consultation> conList, ListInterface<Queue> qList, ReadWriteFile fileManager) {
        this.appointmentList = appList;
        this.consultationList = conList;
        this.queueList = qList;
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
     * Saves a new consultation record for a scheduled appointment.
     */
    public String saveAppointmentConsultation(Appointment appointment, String diagnosis, String notes) {
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            return "Error: Diagnosis cannot be empty.";
        }
        String newId = generateNewConsultationId();
        Consultation newConsultation = new Consultation(newId, appointment, diagnosis, notes, LocalDate.now());
        consultationList.add(newConsultation);
        appointment.setStatus("Completed");
        
        // Save both files
        fileManager.writeConsultationsToFile("consultations.txt", consultationList);
        fileManager.writeAppointmentsToFile("appointments.txt", appointmentList);

        return "Success: Consultation " + newId + " saved successfully!";
    }

    /**
     * Saves a new consultation record for a walk-in patient from the queue.
     */
    public String saveWalkInConsultation(Queue queue, String diagnosis, String notes) {
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            return "Error: Diagnosis cannot be empty.";
        }
        String newId = generateNewConsultationId();
        // Use the walk-in constructor for the Consultation entity
        Consultation newConsultation = new Consultation(newId, queue.getPatient(), queue.getDoctor(), diagnosis, notes, LocalDate.now());
        consultationList.add(newConsultation);

        // --- NEW LOGIC ---
        // Update the queue item's status and exit time
        queue.setStatus("Completed");
        queue.setExitTime(LocalTime.now());
        
        // Save both the consultation and the updated queue files
        fileManager.writeConsultationsToFile("consultations.txt", consultationList);
        fileManager.writeQueuesToFile("queues.txt", queueList);

        return "Success: Consultation " + newId + " for walk-in patient saved successfully!";
    }

    /**
     * Handles the logic for editing an existing consultation's diagnosis.
     *
     * @param consultationToEdit The consultation object to modify.
     * @param newDiagnosis The new diagnosis text.
     * @return A String indicating the result.
     */
    public String editConsultationDiagnosis(Consultation consultationToEdit, String newDiagnosis) {
        if (newDiagnosis == null || newDiagnosis.trim().isEmpty()) {
            return "Error: Diagnosis cannot be empty.";
        }

        // Update the diagnosis in the object
        consultationToEdit.setDiagnosis(newDiagnosis);

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
    public String saveNewConsultation(Appointment appointment, String diagnosis, String notes) {
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            return "Error: Diagnosis cannot be empty.";
        }

        // 1. Create and add new Consultation
        String newId = generateNewConsultationId();
        Consultation newConsultation = new Consultation(newId, appointment, diagnosis, notes, LocalDate.now());
        consultationList.add(newConsultation);

        // 2. Update appointment status
        appointment.setStatus("Completed");
        appointmentList.sort();
        consultationList.sort();
        fileManager.writeConsultationsToFile("src/main/java/Data File/consultations", consultationList); // Assuming getter in control
        fileManager.writeAppointmentsToFile("src/main/java/Data File/appointments", appointmentList); // Assuming getter in control

        return "Success: Consultation " + newId + " saved successfully!";
    }

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
}
