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

    private final ListInterface<Treatment> treatmentList;
    private final ListInterface<Consultation> consultationList;
    private final ListInterface<Medicine> medicineList;
    private final ReadWriteFile fileManager;

    // Helper inner class for aggregating report data
    private static class ReportItem implements Comparable<ReportItem> {
        String name;
        int count;

        ReportItem(String name, int count) {
            this.name = name;
            this.count = count;
        }

        @Override
        public int compareTo(ReportItem other) {
            // Compare by count for sorting purposes
            return Integer.compare(this.count, other.count);
        }
    }

    public TreatmentController(ListInterface<Treatment> treatmentList, ListInterface<Consultation> consultationList, ListInterface<Medicine> medicineList, ReadWriteFile fileManager) {
        this.treatmentList = treatmentList;
        this.consultationList = consultationList;
        this.medicineList = medicineList;
        this.fileManager = fileManager;
    }

    public String generateMedicineFrequencyReport() {
        ListInterface<ReportItem> reportData = new DoublyLinkedList<>();
        Iterator<Treatment> treatmentIt = treatmentList.getIterator();

        while (treatmentIt.hasNext()) {
            Treatment t = treatmentIt.next();
            if (t == null || t.getPrescribedMedicine() == null) continue;

            Iterator<Medicine> medicineIt = t.getPrescribedMedicine().getIterator();
            while (medicineIt.hasNext()) {
                Medicine med = medicineIt.next();
                if (med == null) continue;
                
                String medName = med.getName();
                ReportItem existingItem = reportData.find(item -> item.name.equals(medName));

                if (existingItem != null) {
                    existingItem.count++;
                } else {
                    reportData.add(new ReportItem(medName, 1));
                }
            }
        }

        reportData.sortDescending(); // Use ADT to sort by count

        StringBuilder report = new StringBuilder();
        report.append("==============================================\n");
        report.append("--- Medicine Prescription Frequency Report ---\n");
        report.append("==============================================\n\n");
        report.append(String.format("%-27s | %s\n", "Medicine Name", "Times Prescribed"));
        report.append("==============================================\n");

        if (reportData.isEmpty()) {
            report.append("No prescription data available.\n");
        } else {
            Iterator<ReportItem> reportIt = reportData.getIterator();
            while(reportIt.hasNext()){
                ReportItem item = reportIt.next();
                report.append(String.format("%-27s | %d\n", item.name, item.count));
            }
        }
        
        report.append("==============================================\n\n");
        report.append("====================\n");
        report.append("---Most used list---\n");
        report.append("    (Top Three)   \n");
        report.append("====================\n");

        Iterator<ReportItem> topThreeIt = reportData.getIterator();
        int count = 0;
        while(topThreeIt.hasNext() && count < 3) {
            report.append(String.format("%d. %s\n", count + 1, topThreeIt.next().name));
            count++;
        }
        if (count == 0) {
            report.append("No data to display.\n");
        }

        report.append("\n==============================================\n");
        
        return report.toString();
    }

    public String generateDiagnosisFrequencyReport(LocalDate startDate, LocalDate endDate) {
        ListInterface<ReportItem> reportData = new DoublyLinkedList<>();
        Iterator<Treatment> treatmentIt = treatmentList.getIterator();

        while (treatmentIt.hasNext()) {
            Treatment t = treatmentIt.next();
            if (t == null || t.getConsultation() == null) continue;
            
            LocalDate consultationDate = t.getConsultation().getConsultationDate();

            if (!consultationDate.isBefore(startDate) && !consultationDate.isAfter(endDate)) {
                String diagnosis = t.getConsultation().getDiagnosis();
                if (diagnosis == null || diagnosis.trim().isEmpty()) continue;

                ReportItem existingItem = reportData.find(item -> item.name.equalsIgnoreCase(diagnosis.trim()));

                if (existingItem != null) {
                    existingItem.count++;
                } else {
                    reportData.add(new ReportItem(diagnosis.trim(), 1));
                }
            }
        }
        
        reportData.sortDescending(); // Use ADT to sort by count

        StringBuilder report = new StringBuilder();
        report.append(String.format("=============================================================\n"));
        report.append(String.format("--- Diagnosis Frequency Report (%s to %s) ---\n", startDate, endDate));
        report.append(String.format("=============================================================\n\n"));
        report.append(String.format("%-43s | %s\n", "Diagnosis", "Number of Cases"));
        report.append("=============================================================\n");

        if (reportData.isEmpty()) {
            report.append("No diagnosis data available for the selected date range.\n");
        } else {
            Iterator<ReportItem> reportIt = reportData.getIterator();
            while(reportIt.hasNext()){
                ReportItem item = reportIt.next();
                report.append(String.format("%-43s | %d\n", item.name, item.count));
            }
        }
        
        report.append("=============================================================\n\n");
        report.append("======================\n");
        report.append("--- Diagnosis list ---\n");
        report.append("     (Top Three)    \n");
        report.append("======================\n");

        Iterator<ReportItem> topThreeIt = reportData.getIterator();
        int count = 0;
        while(topThreeIt.hasNext() && count < 3) {
            report.append(String.format("%d. %s\n", count + 1, topThreeIt.next().name));
            count++;
        }
        if (count == 0) {
            report.append("No data to display.\n");
        }

        report.append("\n=============================================================\n");
        
        return report.toString();
    }
    
    
    public ListInterface<Consultation> getPendingConsultations(LocalDate date, String searchTerm, String searchBy) {
        ListInterface<Consultation> pending = new DoublyLinkedList<>();
        Iterator<Consultation> it = consultationList.getIterator();

        while (it.hasNext()) {
            Consultation c = it.next();
            boolean dateMatch = c.getConsultationDate().equals(date);
            
            boolean criteriaMatch;
            if (searchTerm.trim().isEmpty()) {
                criteriaMatch = true; 
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
        String newId = "T" + String.format("%03d", treatmentList.getLength() + 1);
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

