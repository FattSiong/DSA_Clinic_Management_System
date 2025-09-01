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
import java.util.Map;

public class PharmacyManagementSystem {

    // Access the central data lists from the main application class
    private ListInterface<DispensingRecord> dispensingRecords = ClinicManagementSystem.getDispensingRecordList();
    private ListInterface<Medicine> medicineList = ClinicManagementSystem.getMedicineList();
    private ListInterface<Treatment> treatmentList = ClinicManagementSystem.getTreatmentList();

    public PharmacyManagementSystem() {
        // Constructor is empty as data is loaded centrally.
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
    public void addMedicine(String id, String name, String description, int stock, double price, int reorderLvl) throws Exception {
        if (id.isEmpty() || name.isEmpty()) {
            throw new Exception("ID and Name cannot be empty.");
        }
        if (findMedicineById(id) != null) {
            throw new Exception("A medicine with ID '" + id + "' already exists.");
        }
        Medicine newMedicine = new Medicine(id, name, description, stock, price, reorderLvl);
        medicineList.add(newMedicine);
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
        } else {
            throw new Exception("Could not find medicine to delete.");
        }
    }

    // ========== Dispensing & Revert Logic ==========
    public void dispenseFromTreatment(Treatment treatment, java.util.List<Integer> quantities) throws Exception {
        String newRecordId = generateNewRecordId();
        Consultation consultation = treatment.getConsultation();
        String patientName = consultation.getPatient() != null ? consultation.getPatient().getName() : "N/A";
        String doctorName = consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A";
        String originalDetails = treatment.getTreatmentDetails();
        DispensingRecord newRecord = new DispensingRecord(newRecordId, patientName, doctorName, consultation, originalDetails);

        Iterator<Medicine> medIterator = treatment.getPrescribedMedicine().getIterator();
        int i = 0;
        while (medIterator.hasNext()) {
            Medicine medicine = medIterator.next();
            int quantity = quantities.get(i);
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
        if (position != -1) {
            treatmentList.remove(position);
        }
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

        String newTreatmentId = "T" + recordToRevert.getRecordId().substring(3);
        String details = recordToRevert.getOriginalTreatmentDetails();
        Consultation originalConsultation = recordToRevert.getOriginalConsultation();
        if (originalConsultation == null) {
            throw new Exception("Cannot revert: Original consultation data is missing.");
        }

        Treatment newTreatment = new Treatment(newTreatmentId, originalConsultation, details, prescribedMedicines);
        treatmentList.add(newTreatment);

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
}
