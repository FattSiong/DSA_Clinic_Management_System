/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Melvin
 */
import adt.DoublyLinkedList;
import adt.ListInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Objects;
import entity.DispensedItem;

public class DispensingRecord implements Comparable<DispensingRecord> {

    private String recordId;
    private String patientName;
    private String doctorName;
    private LocalDateTime dispensingDateTime;
    private ListInterface<DispensedItem> dispensedItems;
    private double totalAmount;
    private Consultation originalConsultation;
    private String originalTreatmentDetails;

    public DispensingRecord(String recordId, String patientName, String doctorName,
            Consultation originalConsultation, String originalTreatmentDetails) {
        this.recordId = recordId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.dispensingDateTime = LocalDateTime.now();
        this.dispensedItems = new DoublyLinkedList<>();
        this.totalAmount = 0.0;
        this.originalConsultation = originalConsultation;
        this.originalTreatmentDetails = originalTreatmentDetails;
    }

    // Method to add a medicine to this record
    public void addItem(DispensedItem item) {
        dispensedItems.add(item);
        totalAmount += item.getSubtotal();
    }

    // Getters
    public String getRecordId() {
        return recordId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDateTime getDispensingDateTime() {
        return dispensingDateTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public ListInterface<DispensedItem> getDispensedItems() {
        return dispensedItems;
    }

    public Consultation getOriginalConsultation() {
        return originalConsultation;
    }

    public String getOriginalTreatmentDetails() {
        return originalTreatmentDetails;
    }

    //Setters
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDispensingDateTime(LocalDateTime dispensingDateTime) {
        this.dispensingDateTime = dispensingDateTime;
    }

    public void setDispensedItems(ListInterface dispensedItems) {
        this.dispensedItems = dispensedItems;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOriginalConsultation(Consultation consultation) {
        this.originalConsultation = consultation;
    }

    public void setOriginalTreatmentDetails(String details) {
        this.originalTreatmentDetails = details;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        sb.append("==============================================\n");
        sb.append("Dispensing Record ID: ").append(recordId).append("\n");
        sb.append("Date: ").append(dispensingDateTime.format(formatter)).append("\n");
        sb.append("Patient: ").append(patientName).append("\n");
        sb.append("Doctor: ").append(doctorName).append("\n");
        sb.append("--- Items ---\n");

        Iterator<DispensedItem> iterator = dispensedItems.getIterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString()).append("\n");
        }

        sb.append("----------------------------------------------\n");
        sb.append(String.format("Total Amount: $%.2f\n", totalAmount));
        sb.append("==============================================\n");
        return sb.toString();
    }

    /**
     * Recalculates the total amount of the record by summing up the subtotals
     * of all dispensed items. This is used after a record is updated.
     */
    public void recalculateTotal() {
        this.totalAmount = 0.0; // Reset the total
        if (dispensedItems != null) {
            Iterator<DispensedItem> iterator = dispensedItems.getIterator();
            while (iterator.hasNext()) {
                this.totalAmount += iterator.next().getSubtotal();
            }
        }
    }

    /**
     * NEW (Overloaded) Constructor: For brand-new records or records read from
     * the old file format that do not have original treatment details.
     */
    public DispensingRecord(String recordId, String patientName, String doctorName) {
        // This calls the main constructor, passing null for the extra details.
        this(recordId, patientName, doctorName, null, "N/A");
    }

    @Override
    public int compareTo(DispensingRecord other) {
        return this.recordId.compareTo(other.getRecordId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DispensingRecord that = (DispensingRecord) obj;
        return Objects.equals(recordId, that.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }
}
