// Name: <Your Name>
package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Consultation;
import entity.Medicine;
import entity.Treatment;
import utility.ReadWriteFile;

import java.time.LocalDate;
import java.util.Iterator;

public class TreatmentController {

    private ListInterface<Treatment> treatmentList;
    private ListInterface<Consultation> consultationList;
    private ListInterface<Medicine> medicineList;
    private ReadWriteFile fileManager;

    public TreatmentController(ListInterface<Treatment> treatmentList, ListInterface<Consultation> consultationList, ListInterface<Medicine> medicineList, ReadWriteFile fileManager) {
        this.treatmentList = treatmentList;
        this.consultationList = consultationList;
        this.medicineList = medicineList;
        this.fileManager = fileManager;
        
        // Add this line for debugging
        System.out.println("TreatmentController initialized. Number of treatments loaded: " + treatmentList.getLength());
    }

    public ListInterface<Consultation> getPendingConsultations(LocalDate date, String searchTerm, String searchBy) {
        ListInterface<Consultation> pending = new DoublyLinkedList<>();
        Iterator<Consultation> it = consultationList.getIterator();

        while (it.hasNext()) {
            Consultation c = it.next();
            boolean dateMatch = c.getConsultationDate().equals(date);
            
            boolean criteriaMatch;
            if (searchTerm.trim().isEmpty()) {
                criteriaMatch = true; // Match all if search term is blank
            } else {
                String lowerCaseSearchTerm = searchTerm.toLowerCase();
                switch (searchBy) {
                    case "Patient IC":
                        criteriaMatch = c.getPatient().getIcNumber().contains(searchTerm);
                        break;
                    case "Patient Name":
                        criteriaMatch = c.getPatient().getName().toLowerCase().contains(lowerCaseSearchTerm);
                        break;
                    case "Doctor Name":
                        criteriaMatch = c.getDoctor().getName().toLowerCase().contains(lowerCaseSearchTerm);
                        break;
                    default:
                        criteriaMatch = false;
                        break;
                }
            }

            boolean alreadyTreated = isConsultationTreated(c.getConsultationID());

            if (dateMatch && criteriaMatch && !alreadyTreated) {
                pending.add(c);
            }
        }
        return pending;
    }

    private boolean isConsultationTreated(String consultationID) {
        Iterator<Treatment> it = treatmentList.getIterator();
        while (it.hasNext()) {
            if (it.next().getConsultation().getConsultationID().equals(consultationID)) {
                return true;
            }
        }
        return false;
    }
    
    public Consultation findConsultationById(String consultationId) {
        Iterator<Consultation> it = consultationList.getIterator();
        while (it.hasNext()) {
            Consultation c = it.next();
            if (c.getConsultationID().equals(consultationId)) {
                return c;
            }
        }
        return null;
    }
    
    public Medicine findMedicineById(String medId) {
        Iterator<Medicine> it = medicineList.getIterator();
        while (it.hasNext()) {
            Medicine m = it.next();
            if (m.getMedicineID().equalsIgnoreCase(medId)) {
                return m;
            }
        }
        return null;
    }
    
    public Treatment findTreatmentById(String treatmentId) {
        Iterator<Treatment> it = treatmentList.getIterator();
        while (it.hasNext()) {
            Treatment t = it.next();
            if (t.getTreatmentID().equalsIgnoreCase(treatmentId)) {
                return t;
            }
        }
        return null;
    }

    public ListInterface<Medicine> searchMedicines(String searchTerm, String searchBy) {
        ListInterface<Medicine> found = new DoublyLinkedList<>();
        Iterator<Medicine> it = medicineList.getIterator();
        while (it.hasNext()) {
            Medicine m = it.next();
            boolean match = false;
            if (searchBy.equals("Name")) {
                match = m.getName().toLowerCase().contains(searchTerm.toLowerCase());
            } else if (searchBy.equals("Description")) {
                match = m.getDescription().toLowerCase().contains(searchTerm.toLowerCase());
            }
            if (match) {
                found.add(m);
            }
        }
        return found;
    }

    public Treatment addTreatment(Consultation consultation, String details, ListInterface<Medicine> prescribedMedicines) {
        String newId = "TR" + String.format("%03d", treatmentList.getLength() + 1);
        Treatment newTreatment = new Treatment(newId, consultation, details, prescribedMedicines);
        if (treatmentList.add(newTreatment)) {
            fileManager.writeTreatmentsToFile("src/main/java/Data File/treatments", treatmentList);
            return newTreatment;
        }
        return null;
    }
    
    public ListInterface<Treatment> searchTreatments(String searchTerm, String searchBy) {
        ListInterface<Treatment> found = new DoublyLinkedList<>();
        Iterator<Treatment> it = treatmentList.getIterator();
        while(it.hasNext()){
            Treatment t = it.next();
            boolean match = false;
            String lowerCaseSearchTerm = searchTerm.toLowerCase();

            if (searchTerm.trim().isEmpty()) {
                match = true;
            } else {
                 switch (searchBy) {
                    case "Patient Name":
                        match = t.getConsultation().getPatient().getName().toLowerCase().contains(lowerCaseSearchTerm);
                        break;
                    case "Doctor Name":
                        match = t.getConsultation().getDoctor().getName().toLowerCase().contains(lowerCaseSearchTerm);
                        break;
                    case "Treatment ID":
                        match = t.getTreatmentID().toLowerCase().contains(lowerCaseSearchTerm);
                        break;
                }
            }
           
            if(match){
                found.add(t);
            }
        }
        return found;
    }
    
    public String getMedicineNames(ListInterface<Medicine> medicines) {
        StringBuilder names = new StringBuilder();
        Iterator<Medicine> it = medicines.getIterator();
        while (it.hasNext()) {
            names.append(it.next().getName());
            if (it.hasNext()) {
                names.append(", ");
            }
        }
        return names.toString();
    }
    
    public boolean deleteTreatment(String treatmentId) {
        for (int i = 1; i <= treatmentList.getLength(); i++) {
            if (treatmentList.getEntry(i).getTreatmentID().equals(treatmentId)) {
                treatmentList.remove(i);
                fileManager.writeTreatmentsToFile("src/main/java/Data File/treatments", treatmentList);
                return true;
            }
        }
        return false;
    }
    
    public boolean updateTreatment(Treatment treatment, String newDetails, ListInterface<Medicine> newMedicines) {
        treatment.setTreatmentDetails(newDetails);
        treatment.setPrescribedMedicine(newMedicines);
        fileManager.writeTreatmentsToFile("src/main/java/Data File/treatments", treatmentList);
        return true;
    }
}


