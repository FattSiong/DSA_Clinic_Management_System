/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Melvin
 */
public class DispensedItem {
    private Medicine medicine; // A reference to the medicine dispensed
    private int quantityDispensed;

    public DispensedItem(Medicine medicine, int quantityDispensed) {
        this.medicine = medicine;
        this.quantityDispensed = quantityDispensed;
    }
    
    public Medicine getMedicine() { return medicine; }
    public int getQuantityDispensed() { return quantityDispensed; }
    public double getSubtotal() { return medicine.getPrice() * quantityDispensed; }

    @Override
    public String toString() {
        return String.format("  - %-25s | Qty: %-4d | Subtotal: $%.2f",
                medicine.getName(), quantityDispensed, getSubtotal());
    }
}
