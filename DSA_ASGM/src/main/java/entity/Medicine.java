/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author tiong
 */
public class Medicine implements Comparable<Medicine> {
    private String medicineID;
    private String name;
    private String description;
    private int stockQuantity;
    private double price;
    //reorderLvl - stock threshold for triggering a low stock warning
    private int reorderLvl;

    public Medicine(String medicineID, String name, String description, int stockQuantity, double price, int reorderLvl) {
        this.medicineID = medicineID;
        this.name = name;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.reorderLvl = reorderLvl;
    }

    // Getters
    public String getMedicineID() {
        return medicineID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public double getPrice() {
        return price;
    }
    
    //Pharmacy - Melvin
    public int getReorderLvl() {
        return reorderLvl;
    }

    // Setters
    public void setMedicineID(String medicineID) {
        this.medicineID = medicineID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    //Pharmacy - Melvin
    public void setReorderLvl(int reorderLv) {
        this.reorderLvl = reorderLvl;
    }
    
    @Override
    public int compareTo(Medicine other) {
        return this.medicineID.compareTo(other.getMedicineID());
    }

    @Override
    public String toString() {
        return "Medicine{"
                + "medicineID='" + medicineID + '\''
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", stockQuantity=" + stockQuantity
                + ", price=" + price
                + ", reorderLvl=" + reorderLvl
                + '}';
    }
}
