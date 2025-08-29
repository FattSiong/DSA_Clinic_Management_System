/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Melvin
 */
public class DispensedItem implements Comparable<DispensedItem> {

    private String recordId;
    private Medicine medicine; // A reference to the medicine dispensed
    private int quantityDispensed;

    public DispensedItem(Medicine medicine, int quantityDispensed) {
        this.medicine = medicine;
        this.quantityDispensed = quantityDispensed;
    }

    //Getter
    public String getRecordId() {
        return recordId;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public int getQuantityDispensed() {
        return quantityDispensed;
    }

    public double getSubtotal() {
        return medicine.getPrice() * quantityDispensed;
    }

    //Setter
    public void setRecordId(String recordID) {
        this.recordId = recordId;
    }

    @Override
    public int compareTo(DispensedItem other) {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%s | - %s | Qty: %d | Subtotal: $%.2f",
                this.medicine.getDescription(),
                this.medicine.getName(),
                this.getQuantityDispensed(),
                this.getSubtotal()
        );
    }
}
