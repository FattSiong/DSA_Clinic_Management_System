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
import entity.Treatment;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private ListInterface<Treatment> treatmentList;
    private ReadWriteFile fileManager;
    
    private static final String QUEUES_FILE = "src/main/java/Data File/queues";
    private static final String SCHEDULES_FILE = "src/main/java/Data File/schedules";
    private static final String APPOINTMENTS_FILE = "src/main/java/Data File/appointments";
    private static final String CONSULTATIONS_FILE = "src/main/java/Data File/consultations";
    
    public ConsultationControl(ListInterface<Appointment> appList, ListInterface<Consultation> conList, ListInterface<Queue> qList, ListInterface<Schedule> sList, ListInterface<Treatment> tList, ReadWriteFile fileManager) {
        this.appointmentList = appList;
        this.consultationList = conList;
        this.queueList = qList;
        this.scheduleList = sList;
        this.treatmentList = tList;
        this.fileManager = fileManager;

    }

    // --- Getter Functions ---
    public ListInterface<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public ListInterface<Consultation> getConsultationList() {
        return consultationList;
    }
    
    public ListInterface<Treatment> getTreatmentList() {
        return treatmentList;
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
    
    /**
     * Retrieves all consultations and their associated treatments for a given patient.
     * The treatments are simplified to only show the medicine names.
     *
     * @param patientId The ID of the patient to search for.
     * @return A ListInterface of Consultation objects related to the patient.
     */
    public ListInterface<Consultation> getPatientHistory(String patientId) {
        ListInterface<Consultation> patientHistory = new DoublyLinkedList<>();
        Iterator<Consultation> conIterator = consultationList.getIterator();

        while (conIterator.hasNext()) {
            Consultation consultation = conIterator.next();
            if (consultation.getPatient().getPatientID().equals(patientId)) {
                // Find and link the treatments for this consultation
                // Create a temporary list to hold the treatments
                ListInterface<Treatment> consultationTreatments = new DoublyLinkedList<>();
                Iterator<Treatment> treatIterator = treatmentList.getIterator();
                while (treatIterator.hasNext()) {
                    Treatment treatment = treatIterator.next();
                    if (treatment.getConsultation().getConsultationID().equals(consultation.getConsultationID())) {
                        consultationTreatments.add(treatment);
                    }
                }
                
                // You can add the treatments to the consultation object or a custom history object if needed.
                // For this implementation, we will assume a way to pass this information to the UI.
                patientHistory.add(consultation);
            }
        }
        return patientHistory;
    }
    
    /**
     * Generates a summary report of consultations for a given period.
     * @param period The period to report on (e.g., "daily", "monthly", "annually").
     * @param selectedDate The specific date to use for filtering.
     * @return A formatted String containing the report.
     */
    public String getConsultationSummaryReport(String period, LocalDate selectedDate) {
        StringBuilder report = new StringBuilder();

        report.append("================================================================================\n");
        report.append(String.format("                        CLINIC CONSULTATION SUMMARY REPORT                     \n"));
        report.append("================================================================================\n");
        report.append(String.format("Report Period: %s\n", period.substring(0, 1).toUpperCase() + period.substring(1)));
        report.append("Date Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + "\n");
        report.append("--------------------------------------------------------------------------------\n");

        int totalConsultations = 0;
        int scheduledCount = 0;
        int walkinCount = 0;
        
        ListInterface<String> doctorNames = new DoublyLinkedList<>();
        ListInterface<Integer> consultationsByDoctor = new DoublyLinkedList<>();
        ListInterface<String> diagnoses = new DoublyLinkedList<>();
        ListInterface<Integer> diagnosesCount = new DoublyLinkedList<>();

        Iterator<Consultation> iterator = consultationList.getIterator();
        while (iterator.hasNext()) {
            Consultation con = iterator.next();
            LocalDate conDate = con.getConsultationDate();

            boolean includeInReport = false;
            switch (period.toLowerCase()) {
                case "daily":
                    if (conDate.isEqual(selectedDate)) {
                        includeInReport = true;
                    }
                    break;
                case "monthly":
                    if (conDate.getYear() == selectedDate.getYear() && conDate.getMonth() == selectedDate.getMonth()) {
                        includeInReport = true;
                    }
                    break;
                case "annually":
                    if (conDate.getYear() == selectedDate.getYear()) {
                        includeInReport = true;
                    }
                    break;
            }

            if (includeInReport) {
                totalConsultations++;
                if (con.getAppointment() != null) {
                    scheduledCount++;
                } else {
                    walkinCount++;
                }

                // Count consultations per doctor using DoublyLinkedList
                String doctorName = con.getDoctor().getName();
                int doctorIndex = findIndexOfString(doctorNames, doctorName);
                if (doctorIndex == -1) {
                    doctorNames.add(doctorName);
                    consultationsByDoctor.add(1);
                } else {
                    int count = consultationsByDoctor.getEntry(doctorIndex) + 1;
                    consultationsByDoctor.replace(doctorIndex, count);
                }

                // Count diagnoses using DoublyLinkedList
                String diagnosis = con.getDiagnosis();
                int diagnosisIndex = findIndexOfString(diagnoses, diagnosis);
                if (diagnosisIndex == -1) {
                    diagnoses.add(diagnosis);
                    diagnosesCount.add(1);
                } else {
                    int count = diagnosesCount.getEntry(diagnosisIndex) + 1;
                    diagnosesCount.replace(diagnosisIndex, count);
                }
            }
        }
        
        report.append(String.format("Total Consultations: %d\n", totalConsultations));
        report.append(String.format("  - Scheduled Appointments: %d\n", scheduledCount));
        report.append(String.format("  - Walk-in Patients: %d\n\n", walkinCount));
        
        report.append("--------------------------------------------------------------------------------\n");
        report.append(String.format("CONSULTATIONS BY DOCTOR:\n"));
        report.append("--------------------------------------------------------------------------------\n");
        report.append(String.format("%-3s | %-45s | %-10s\n", "No.", "Doctor Name", "Consultations"));
        report.append("--------------------------------------------------------------------------------\n");

        for (int i = 1; i <= doctorNames.getLength(); i++) {
            report.append(String.format("%-3d | %-45s | %-10d\n", i, doctorNames.getEntry(i), consultationsByDoctor.getEntry(i)));
        }
        report.append("\n");

        report.append("--------------------------------------------------------------------------------\n");
        report.append(String.format("TOP DIAGNOSES:\n"));
        report.append("--------------------------------------------------------------------------------\n");
        
        ListInterface<String> sortedDiagnoses = new DoublyLinkedList<>();
        ListInterface<Integer> sortedCounts = new DoublyLinkedList<>();

        for (int i = 1; i <= diagnoses.getLength(); i++) {
            String currentDiagnosis = diagnoses.getEntry(i);
            int currentCount = diagnosesCount.getEntry(i);
            
            // Insert in sorted order
            boolean inserted = false;
            for (int j = 1; j <= sortedDiagnoses.getLength(); j++) {
                if (currentCount > sortedCounts.getEntry(j)) {
                    sortedDiagnoses.add(j, currentDiagnosis);
                    sortedCounts.add(j, currentCount);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                sortedDiagnoses.add(currentDiagnosis);
                sortedCounts.add(currentCount);
            }
        }
        
        report.append(String.format("Top %d Most Common Diagnoses:\n", 5));
        report.append("--------------------------------------------------------------------------------\n");
        report.append(String.format("%-4s | %-45s | %-10s\n", "No.", "Diagnosis", "Count"));
        report.append("--------------------------------------------------------------------------------\n");

        int topCount = 0;
        for (int i = 1; i <= sortedDiagnoses.getLength() && topCount < 5; i++) {
            report.append(String.format("%-4d | %-45s | %-10d\n", i, sortedDiagnoses.getEntry(i), sortedCounts.getEntry(i)));
            topCount++;
        }
        report.append("================================================================================\n");

        return report.toString();
    }
    
    /**
     * Generates a report on cancelled appointments.
     * @param period The period to report on (e.g., "daily", "monthly", "annually").
     * @param selectedDate The specific date to use for filtering.
     * @return A formatted String containing the report.
     */
    public String getAppointmentCancellationReport(String period, LocalDate selectedDate) {
        StringBuilder report = new StringBuilder();
        
        report.append("================================================================================\n");
        report.append(String.format("                     APPOINTMENT CANCELLATION REPORT                      \n"));
        report.append("================================================================================\n");
        report.append(String.format("Report Period: %s\n", period.substring(0, 1).toUpperCase() + period.substring(1)));
        report.append("Date Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + "\n");
        report.append("--------------------------------------------------------------------------------\n");

        int totalAppointments = 0;
        int totalCancelled = 0;
        
        ListInterface<String> doctorNames = new DoublyLinkedList<>();
        ListInterface<Integer> doctorTotalAppointments = new DoublyLinkedList<>();
        ListInterface<Integer> doctorCancelledAppointments = new DoublyLinkedList<>();
        
        Iterator<Appointment> iterator = appointmentList.getIterator();
        
        while(iterator.hasNext()){
            Appointment app = iterator.next();
            
            boolean includeInReport = false;
            LocalDate appDate = app.getAppointmentDate();

            switch (period.toLowerCase()) {
                case "daily":
                    if (appDate.isEqual(selectedDate)) {
                        includeInReport = true;
                    }
                    break;
                case "monthly":
                    if (appDate.getYear() == selectedDate.getYear() && appDate.getMonth() == selectedDate.getMonth()) {
                        includeInReport = true;
                    }
                    break;
                case "annually":
                    if (appDate.getYear() == selectedDate.getYear()) {
                        includeInReport = true;
                    }
                    break;
            }
            
            if(includeInReport){
                totalAppointments++;

                String doctorName = app.getDoctor().getName();
                int doctorIndex = findIndexOfString(doctorNames, doctorName);
                if(doctorIndex == -1){
                    doctorNames.add(doctorName);
                    doctorTotalAppointments.add(1);
                    if("Canceled".equals(app.getStatus())){
                        doctorCancelledAppointments.add(1);
                        totalCancelled++;
                    } else {
                        doctorCancelledAppointments.add(0);
                    }
                } else {
                    doctorTotalAppointments.replace(doctorIndex, doctorTotalAppointments.getEntry(doctorIndex) + 1);
                    if("Canceled".equals(app.getStatus())){
                        doctorCancelledAppointments.replace(doctorIndex, doctorCancelledAppointments.getEntry(doctorIndex) + 1);
                        totalCancelled++;
                    }
                }
            }
        }

        double cancellationRate = (totalAppointments > 0) ? ((double) totalCancelled / totalAppointments) * 100 : 0.0;
        
        report.append(String.format("Total Appointments: %d\n", totalAppointments));
        report.append(String.format("Total Canceled Appointments: %d\n", totalCancelled));
        report.append(String.format("Cancellation Rate: %.2f%%\n", cancellationRate));
        report.append("--------------------------------------------------------------------------------\n");
        
        if (!doctorNames.isEmpty()){
            report.append(String.format("CANCELLATION RATES BY DOCTOR:\n"));
            report.append("--------------------------------------------------------------------------------\n");
            report.append(String.format("%-3s | %-25s | %-15s | %-15s | %-15s\n", "No.", "Doctor Name", "Total Appts", "Canceled", "Rate"));
            report.append("--------------------------------------------------------------------------------\n");
            
            for (int i = 1; i <= doctorNames.getLength(); i++) {
                int totalAppts = doctorTotalAppointments.getEntry(i);
                int canceledAppts = doctorCancelledAppointments.getEntry(i);
                double doctorRate = (totalAppts > 0) ? ((double) canceledAppts / totalAppts) * 100 : 0.0;

                report.append(String.format("%-3d | %-25s | %-15d | %-15d | %.2f%%\n", 
                    i,
                    doctorNames.getEntry(i),
                    totalAppts,
                    canceledAppts,
                    doctorRate
                ));
            }
        } else {
            report.append("No canceled appointments found for this period.\n");
        }
        report.append("================================================================================\n");
        
        return report.toString();
    }
    
    /**
     * Generates a report on the top diagnoses in the clinic.
     * @param limit The number of top diagnoses to include in the report.
     * @return A formatted String containing the report.
     */
    public String getTopDiagnosesReport(int limit) {
        StringBuilder report = new StringBuilder();
        
        report.append("================================================================================\n");
        report.append(String.format("                            TOP DIAGNOSES REPORT                            \n"));
        report.append("================================================================================\n");
        
        ListInterface<String> diagnoses = new DoublyLinkedList<>();
        ListInterface<Integer> diagnosesCount = new DoublyLinkedList<>();
        Iterator<Consultation> iterator = consultationList.getIterator();
        
        while(iterator.hasNext()){
            Consultation con = iterator.next();
            String diagnosis = con.getDiagnosis();
            
            int index = findIndexOfString(diagnoses, diagnosis);
            if (index == -1) {
                diagnoses.add(diagnosis);
                diagnosesCount.add(1);
            } else {
                int count = diagnosesCount.getEntry(index) + 1;
                diagnosesCount.replace(index, count);
            }
        }
        
        if(diagnoses.isEmpty()){
            report.append("No diagnoses found in the system.\n");
            return report.toString();
        }
        
        // Combine and sort diagnoses
        ListInterface<String> sortedDiagnoses = new DoublyLinkedList<>();
        ListInterface<Integer> sortedCounts = new DoublyLinkedList<>();

        for (int i = 1; i <= diagnoses.getLength(); i++) {
            String currentDiagnosis = diagnoses.getEntry(i);
            int currentCount = diagnosesCount.getEntry(i);
            
            boolean inserted = false;
            for (int j = 1; j <= sortedDiagnoses.getLength(); j++) {
                if (currentCount > sortedCounts.getEntry(j)) {
                    sortedDiagnoses.add(j, currentDiagnosis);
                    sortedCounts.add(j, currentCount);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                sortedDiagnoses.add(currentDiagnosis);
                sortedCounts.add(currentCount);
            }
        }
        
        report.append(String.format("Top %d Most Common Diagnoses:\n", limit));
        report.append("--------------------------------------------------------------------------------\n");
        report.append(String.format("%-4s | %-45s | %-10s\n", "No.", "Diagnosis", "Count"));
        report.append("--------------------------------------------------------------------------------\n");
        
        int topCount = 0;
        for (int i = 1; i <= sortedDiagnoses.getLength() && topCount < limit; i++) {
            report.append(String.format("%-4d | %-45s | %-10d\n", i, sortedDiagnoses.getEntry(i), sortedCounts.getEntry(i)));
            topCount++;
        }
        report.append("================================================================================\n");
        
        return report.toString();
    }
    
    /**
     * Helper method to find the index of a string in a ListInterface.
     */
    private int findIndexOfString(ListInterface<String> list, String target) {
        for (int i = 1; i <= list.getLength(); i++) {
            if (list.getEntry(i).equals(target)) {
                return i;
            }
        }
        return -1;
    }
}
