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
    // Here we use another DoublyLinkedList inside our object!
    private ListInterface<DispensedItem> dispensedItems;
    private double totalAmount;

    public DispensingRecord(String recordId, String patientName, String doctorName) {
        this.recordId = recordId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.dispensingDateTime = LocalDateTime.now();
        this.totalAmount = 0.0;
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

    public LocalDateTime getDispensingDateTime() {
        return dispensingDateTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public ListInterface<DispensedItem> getDispensedItems() {
        return dispensedItems;
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
