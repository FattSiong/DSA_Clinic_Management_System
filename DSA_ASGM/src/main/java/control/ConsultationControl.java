/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.ListInterface;
import entity.Appointment;
import entity.Consultation;
import java.time.LocalDate;

/**
 *
 * @author tiong
 */
public class ConsultationControl {

    private ListInterface<Appointment> appointmentList;
    private ListInterface<Consultation> consultationList;

    public ConsultationControl(ListInterface<Appointment> appList, ListInterface<Consultation> conList) {
        this.appointmentList = appList;
        this.consultationList = conList;
    }

    // --- Getter Functions ---
    public ListInterface<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public ListInterface<Consultation> getConsultationList() {
        return consultationList;
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
        // The list is updated directly since 'appointment' is a reference to the object in the list.
        return "Success: Appointment " + appointment.getAppointmentID() + " has been canceled.";
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

        return "Success: Consultation " + newId + " saved successfully!";
    }

    private String generateNewConsultationId() {
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
                return "Success: Consultation " + consultationId + " has been deleted.";
            }
        }
        return "Error: Consultation not found.";
    }
}
