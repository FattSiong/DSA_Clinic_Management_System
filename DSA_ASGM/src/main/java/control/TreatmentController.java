package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Consultation;
import entity.Medicine;
import entity.Treatment;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Scanner;

public class TreatmentController {

    private ListInterface<Treatment> treatmentList;
    private ListInterface<Consultation> consultationList; // From another module
    private ListInterface<Medicine> medicineList;         // From another module

    // The main lists of data are passed in from the main application.
    public TreatmentController(ListInterface<Treatment> treatmentList, ListInterface<Consultation> consultationList, ListInterface<Medicine> medicineList) {
        this.treatmentList = treatmentList;
        this.consultationList = consultationList;
        this.medicineList = medicineList;
    }

    /**
     * Retrieves a list of consultations that occurred today, have not yet been
     * treated, and match an optional patient IC search term.
     *
     * @param patientIcSearch The patient's IC number to filter by (can be an empty string).
     * @return A list of matching consultations.
     */
    public ListInterface<Consultation> getPendingConsultationsForToday(String patientIcSearch) {
        ListInterface<Consultation> pendingList = new DoublyLinkedList<>();
        // In a real application, you might pass the date. For this assignment, using today's date is fine.
        LocalDate today = LocalDate.now();

        Iterator<Consultation> it = consultationList.getIterator();
        while (it.hasNext()) {
            Consultation c = it.next();

            // Condition 1: Must be for today's date.
            boolean isToday = c.getConsultationDate().equals(today);

            // Condition 2: Must NOT already have a treatment record.
            boolean isTreated = isConsultationTreated(c.getConsultationID());

            // Condition 3: Must match the search term (if a search term is provided).
            boolean matchesSearch = patientIcSearch.isEmpty() ||
                                    c.getPatient().getIcNumber().contains(patientIcSearch);

            // If all conditions are met, add it to our list to be displayed.
            if (isToday && !isTreated && matchesSearch) {
                pendingList.add(c);
            }
        }
        return pendingList;
    }

    /**
     * Checks if a given consultation ID already has a treatment record associated with it.
     * @param consultationId The ID of the consultation to check.
     * @return true if a treatment exists, false otherwise.
     */
    private boolean isConsultationTreated(String consultationId) {
        Iterator<Treatment> it = treatmentList.getIterator();
        while (it.hasNext()) {
            Treatment t = it.next();
            if (t.getConsultation().getConsultationID().equals(consultationId)) {
                return true; // Found a match
            }
        }
        return false; // No treatment found for this consultation
    }

    public Treatment addTreatment(Consultation consultation, String details, ListInterface<Medicine> prescribedMedicines) {
        // Generate a new ID
        String newId = "TR" + String.format("%03d", treatmentList.getLength() + 1);
        
        Treatment newTreatment = new Treatment(newId, consultation, details, prescribedMedicines);
        
        treatmentList.add(newTreatment);
        
        return newTreatment;
    }

    // A helper UI function within the controller to select medicines.
    public ListInterface<Medicine> selectMedicinesUI() {
        ListInterface<Medicine> selected = new DoublyLinkedList<>();
        Scanner scanner = new Scanner(System.in);
        String medId;
        
        System.out.println("\n--- Available Medicines ---");
        Iterator<Medicine> medIterator = medicineList.getIterator();
        if (!medIterator.hasNext()) {
            System.out.println("No medicines available in the system.");
            return selected; // Return empty list
        }
        
        while(medIterator.hasNext()){
            Medicine med = medIterator.next();
            System.out.printf("ID: %-5s | Name: %-20s | Stock: %d\n", med.getMedicineID(), med.getName(), med.getStockQuantity());
        }

        while (true) {
            System.out.print("\nEnter Medicine ID to prescribe (or 'done' to finish): ");
            medId = scanner.nextLine();
            if (medId.equalsIgnoreCase("done")) {
                break;
            }
            
            Medicine foundMedicine = findMedicineById(medId);
            if (foundMedicine != null) {
                if (foundMedicine.getStockQuantity() > 0) {
                    selected.add(foundMedicine);
                    System.out.println("Added '" + foundMedicine.getName() + "' to prescription.");
                } else {
                    System.out.println("Warning: '" + foundMedicine.getName() + "' is out of stock and cannot be prescribed.");
                }
            } else {
                System.out.println("Error: Medicine ID not found.");
            }
        }
        return selected;
    }

     private Medicine findMedicineById(String medId) {
        Iterator<Medicine> it = medicineList.getIterator();
        while(it.hasNext()){
            Medicine m = it.next();
            if (m.getMedicineID().equalsIgnoreCase(medId)) {
                return m;
            }
        }
        return null;
    }
}
