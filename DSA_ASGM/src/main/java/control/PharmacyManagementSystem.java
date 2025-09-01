/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

/**
 *
 * @author Melvin
 */
import adt.ListInterface;
import entity.DispensedItem;
import entity.DispensingRecord;
import entity.Medicine;
import entity.Treatment;
import entity.Consultation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Iterator;
import utility.ReadWriteFile;

public class PharmacyManagementSystem {

    private static final String MEDICINES_FILE = "src/main/java/Data File/medicines";
    private static final String TREATMENTS_FILE = "src/main/java/Data File/treatments";
    private static final String DISPENSING_RECORD_FILE = "src/main/java/Data File/dispensingRecord";

    // Access the central data lists from the main application class
    private ListInterface<DispensingRecord> dispensingRecords = ClinicManagementSystem.getDispensingRecordList();
    private ListInterface<Medicine> medicineList = ClinicManagementSystem.getMedicineList();
    private ListInterface<Treatment> treatmentList = ClinicManagementSystem.getTreatmentList();
    private ReadWriteFile fileManager;

    public PharmacyManagementSystem(ReadWriteFile fileManager) {
        this.fileManager = fileManager;
    }

    public void saveData() {
        fileManager.writeMedicinesToFile(MEDICINES_FILE, medicineList);
        fileManager.writeTreatmentsToFile(TREATMENTS_FILE, treatmentList);
        fileManager.writeDispensingRecordToFile(DISPENSING_RECORD_FILE, dispensingRecords);
    }

    // ========== Report Generation Logic ==========
    public String generateSalesReport() {
        if (dispensingRecords.isEmpty()) {
            return "No dispensing records available to generate a report.";
        }
        ListInterface<DispensingRecord> sortedRecords = new adt.DoublyLinkedList<>();
        Iterator<DispensingRecord> copyIterator = dispensingRecords.getIterator();
        while (copyIterator.hasNext()) {
            sortedRecords.add(copyIterator.next());
        }
        Comparator<DispensingRecord> byAmount = Comparator.comparingDouble(DispensingRecord::getTotalAmount);
        sortedRecords.sortDescending(byAmount);

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("===== Sales Report (Descending Order) =====\n");
        reportContent.append("Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        reportContent.append("----------------------------------------------------------\n");
        reportContent.append(String.format("%-15s | %-20s | %s\n", "Record ID", "Patient Name", "Total Amount"));
        reportContent.append("----------------------------------------------------------\n");

        Iterator<DispensingRecord> reportIterator = sortedRecords.getIterator();
        while (reportIterator.hasNext()) {
            DispensingRecord record = reportIterator.next();
            reportContent.append(String.format("%-15s | %-20s | $%-12.2f\n",
                    record.getRecordId(),
                    record.getPatientName(),
                    record.getTotalAmount()));
        }
        reportContent.append("----------------------------------------------------------\n");
        return reportContent.toString();
    }

    public String generateLowStockReport() {
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("===== Low Stock Report =====\n");
        reportContent.append("Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        reportContent.append("--------------------------------------------------\n");
        reportContent.append(String.format("%-10s | %-25s | %-10s | %-10s\n", "ID", "Name", "Current", "Reorder Lvl"));
        reportContent.append("--------------------------------------------------\n");

        boolean foundLowStock = false;
        Iterator<Medicine> iterator = medicineList.getIterator();
        while (iterator.hasNext()) {
            Medicine medicine = iterator.next();
            if (medicine.getStockQuantity() <= medicine.getReorderLvl()) {
                reportContent.append(String.format("%-10s | %-25s | %-10d | %-10d\n",
                        medicine.getMedicineID(),
                        medicine.getName(),
                        medicine.getStockQuantity(),
                        medicine.getReorderLvl()));
                foundLowStock = true;
            }
        }
        if (!foundLowStock) {
            reportContent.append("\nAll medicines are sufficiently stocked.\n");
        }
        reportContent.append("--------------------------------------------------\n");
        return reportContent.toString();
    }

    // ========== Medicine CRUD Logic ==========
    public void addMedicine(String name, String description, int stock, double price, int reorderLvl) throws Exception {
        String newId = generateNewMedicineId();
        Medicine newMedicine = new Medicine(newId, name, description, stock, price, reorderLvl);
        medicineList.add(newMedicine);
        
        saveData();
    }

    public void deleteMedicine(String medicineIdToDelete) throws Exception {
        Iterator<DispensingRecord> recordIterator = dispensingRecords.getIterator();
        while (recordIterator.hasNext()) {
            DispensingRecord record = recordIterator.next();
            Iterator<DispensedItem> itemIterator = record.getDispensedItems().getIterator();
            while (itemIterator.hasNext()) {
                if (itemIterator.next().getMedicine().getMedicineID().equals(medicineIdToDelete)) {
                    throw new Exception("Cannot delete medicine: It is part of historical record " + record.getRecordId());
                }
            }
        }

        int position = -1;
        int counter = 1;
        Iterator<Medicine> medicineIterator = medicineList.getIterator();
        while (medicineIterator.hasNext()) {
            if (medicineIterator.next().getMedicineID().equals(medicineIdToDelete)) {
                position = counter;
                break;
            }
            counter++;
        }

        if (position != -1) {
            medicineList.remove(position);
            saveData();
        } else {
            throw new Exception("Could not find medicine to delete.");
        }
    }

    // ========== Dispensing & Revert Logic ==========
    public void dispenseFromTreatment(Treatment treatment, ListInterface<Integer> quantities) throws Exception {
        String newRecordId = generateNewRecordId();
        Consultation consultation = treatment.getConsultation();
        String patientName = consultation.getPatient() != null ? consultation.getPatient().getName() : "N/A";
        String doctorName = consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A";
        DispensingRecord newRecord = new DispensingRecord(newRecordId, patientName, doctorName, treatment);

        Iterator<Medicine> medIterator = treatment.getPrescribedMedicine().getIterator();
        int i = 1;
        while (medIterator.hasNext()) {
            Medicine medicine = medIterator.next();
            int quantity = quantities.getEntry(i);
            newRecord.addItem(new DispensedItem(medicine, quantity));
            medicine.setStockQuantity(medicine.getStockQuantity() - quantity);
            i++;
        }
        dispensingRecords.add(newRecord);

        int position = -1;
        int counter = 1;
        Iterator<Treatment> treatmentIterator = treatmentList.getIterator();
        while (treatmentIterator.hasNext()) {
            if (treatmentIterator.next().getTreatmentID().equals(treatment.getTreatmentID())) {
                position = counter;
                break;
            }
            counter++;
        }

        saveData();
    }
    
    public void revertDispensingRecord(String recordId) throws Exception {
        DispensingRecord recordToRevert = findRecordById(recordId);
        if (recordToRevert == null) {
            throw new Exception("Record not found.");
        }

        ListInterface<Medicine> prescribedMedicines = new adt.DoublyLinkedList<>();
        Iterator<DispensedItem> itemIterator = recordToRevert.getDispensedItems().getIterator();
        while (itemIterator.hasNext()) {
            DispensedItem item = itemIterator.next();
            Medicine medicine = item.getMedicine();
            medicine.setStockQuantity(medicine.getStockQuantity() + item.getQuantityDispensed());
            prescribedMedicines.add(medicine);
        }

        int position = -1;
        int counter = 1;
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getRecordId().equals(recordId)) {
                position = counter;
                break;
            }
            counter++;
        }
        if (position != -1) {
            dispensingRecords.remove(position);
        }
        
        saveData();
    }

    // ========== Helper / Utility Methods ==========
    private String generateNewRecordId() {
        int maxIdNum = 1000;
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        while (iterator.hasNext()) {
            String currentId = iterator.next().getRecordId();
            if (currentId != null && currentId.startsWith("MDR")) {
                try {
                    int idNum = Integer.parseInt(currentId.substring(3));
                    if (idNum > maxIdNum) {
                        maxIdNum = idNum;
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        return "MDR" + (maxIdNum + 1);
    }

    public Medicine findMedicineById(String id) {
        Iterator<Medicine> iterator = medicineList.getIterator();
        while (iterator.hasNext()) {
            Medicine med = iterator.next();
            if (med.getMedicineID().equals(id)) {
                return med;
            }
        }
        return null;
    }

    public DispensingRecord findRecordById(String id) {
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        while (iterator.hasNext()) {
            DispensingRecord rec = iterator.next();
            if (rec.getRecordId().equals(id)) {
                return rec;
            }
        }
        return null;
    }

    public Treatment findTreatmentById(String id) {
        Iterator<Treatment> iterator = treatmentList.getIterator();
        while (iterator.hasNext()) {
            Treatment t = iterator.next();
            if (t.getTreatmentID().equals(id)) {
                return t;
            }
        }
        return null;
    }
    
    private String generateNewMedicineId() {
        medicineList.sort();

        if (medicineList.isEmpty()) {
            return "M1001";
        }
        Medicine last = medicineList.getEntry(medicineList.getLength());
        int lastIdNum = Integer.parseInt(last.getMedicineID().substring(1));
        return "M" + (lastIdNum + 1);
    }
    
    public String foundTreatmentId(Consultation consultation) {
        Iterator<Treatment> iterator = treatmentList.getIterator();
        while (iterator.hasNext()) {
            Treatment treatment = iterator.next();
            // Check if the treatment is linked to the given consultationID
            if (treatment.getConsultation() != null && treatment.getConsultation().getConsultationID().equals(consultation.getConsultationID())) {
                return treatment.getTreatmentID();
            }
        }
        return null; // Return null if no matching treatment is found
    }
}
