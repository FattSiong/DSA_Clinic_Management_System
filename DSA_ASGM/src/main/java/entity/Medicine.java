/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author tiong
 */
public class Medicine {
    private String medicineID;
    private String name;
    private String description;
    private int stockQuantity;
    private double price;

    public Medicine(String medicineID, String name, String description, int stockQuantity, double price) {
        this.medicineID = medicineID;
        this.name = name;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.price = price;
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

    @Override
    public String toString() {
        return "Medicine{"
                + "medicineID='" + medicineID + '\''
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", stockQuantity=" + stockQuantity
                + ", price=" + price
                + '}';
    }
}
