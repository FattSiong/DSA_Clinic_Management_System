/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

/**
 *
 * @author Melvin
 */
import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.DispensedItem;
import entity.DispensingRecord;
import entity.Medicine;
import java.util.Iterator;
import java.util.Scanner;
import java.io.FileWriter;
import utility.ReadWriteFile;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PharmacyManagementSystem {

    private ListInterface<Medicine> medicineStock;
    private ListInterface<DispensingRecord> dispensingHistory;
    private int nextRecordId = 1; // Simple ID generator for dispensing records

    public PharmacyManagementSystem() {
        // Initialize the core ADTs
        medicineStock = new DoublyLinkedList<>();
        dispensingHistory = new DoublyLinkedList<>();

        // Add some dummy data to start with
        initializeData();
    }

    private void initializeData() {
        medicineStock.add(new Medicine("PAN100", "Paracetamol 500mg", "Fever/Pain", 100, 4.8, 25));
        medicineStock.add(new Medicine("IBU200", "Ibuprofen 200mg", "Anti-inflammatory", 100, 38.87, 25));
        medicineStock.add(new Medicine("AMO500", "Amoxicillin 500mg", "Penicillin antibiotic", 100, 28, 25));
        medicineStock.add(new Medicine("LOR010", "Loratadine 10mg", "Treat allergies", 100, 11, 25));
    }

    // ========== 1. Medicine Stock Control ==========
    public void addOrUpdateMedicine() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Medicine ID (e.g., PAN100): ");
        String id = scanner.nextLine().toUpperCase();
        String description = null;

        Medicine existingMedicine = findMedicineById(id);

        if (existingMedicine != null) {
            System.out.printf("Medicine '%s' found. Current stock: %d\n", existingMedicine.getName(), existingMedicine.getStockQuantity());
            System.out.print("Enter quantity to add (positive number) or remove (negative number): ");
            int qtyChange = scanner.nextInt();
            int newQty = existingMedicine.getStockQuantity() + qtyChange;
            existingMedicine.setStockQuantity(newQty);
            System.out.println("Stock updated successfully. New quantity: " + newQty);
        } else {
            System.out.println("Medicine ID not found. Creating new medicine entry.");
            System.out.print("Enter Medicine Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Initial Quantity: ");
            int qty = scanner.nextInt();
            System.out.print("Enter Unit Price: ");
            double price = scanner.nextDouble();
            System.out.print("Enter Reorder Level: ");
            int reorderLvl = scanner.nextInt();

            Medicine newMedicine = new Medicine(id, name, description, qty, price, reorderLvl);
            medicineStock.add(newMedicine);
            System.out.println("New medicine added successfully!");
        }
    }

    public Medicine findMedicineById(String medicineId) {
        Iterator<Medicine> iterator = medicineStock.getIterator();
        while (iterator.hasNext()) {
            Medicine currentMedicine = iterator.next();
            if (currentMedicine.getMedicineID().equals(medicineId)) {
                return currentMedicine;
            }
        }
        return null; // Not found
    }

    /**
     * Generates a full inventory report and saves it to a text file. This
     * method replaces the old viewAllMedicines() for file output.
     */
    public void generateInventoryReport(String filename) {
        System.out.println("\nGenerating inventory report...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            writer.println("===== Full Medicine Inventory Report =====");
            writer.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("==========================================");

            if (medicineStock.isEmpty()) {
                writer.println("Inventory is empty.");
            } else {
                Iterator<Medicine> iterator = medicineStock.getIterator();
                while (iterator.hasNext()) {
                    writer.println(iterator.next().toString());
                }
            }

            System.out.println("Success! Inventory report saved to " + filename);

        } catch (IOException e) {
            System.out.println("Error: An error occurred while writing to the file: " + e.getMessage());
        }
    }

    /**
     * Generates a report of all medicines with stock at or below their reorder
     * level and saves it to a text file.
     */
    public void generateLowStockReport(String filename) {
        System.out.println("\nGenerating low stock report...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            writer.println("===== Low Stock Report =====");
            writer.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("============================");

            boolean foundLowStock = false;
            Iterator<Medicine> iterator = medicineStock.getIterator();
            while (iterator.hasNext()) {
                Medicine medicine = iterator.next();
                if (medicine.getStockQuantity() <= medicine.getReorderLvl()) {
                    writer.printf("LOW STOCK: %s (Current Stock: %d, Reorder Level: %d)\n",
                            medicine.getName(), medicine.getStockQuantity(), medicine.getReorderLvl());
                    foundLowStock = true;
                }
            }

            if (!foundLowStock) {
                writer.println("All medicines are sufficiently stocked.");
            }

            System.out.println("Success! Low stock report saved to " + filename);

        } catch (IOException e) {
            System.out.println("Error: An error occurred while writing to the file: " + e.getMessage());
        }
    }

    /**
     * Generates a complete history of all dispensing records and saves it to a
     * text file.
     */
    public void generateDispensingHistoryReport(String filename) {
        System.out.println("\nGenerating dispensing history report...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            writer.println("===== Full Dispensing History Report =====");
            writer.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("========================================");

            if (dispensingHistory.isEmpty()) {
                writer.println("No dispensing records found.");
            } else {
                Iterator<DispensingRecord> iterator = dispensingHistory.getIterator();
                while (iterator.hasNext()) {
                    writer.println(iterator.next().toString());
                    writer.println(); // Add a blank line for better separation
                }
            }

            System.out.println("Success! Dispensing history report saved to " + filename);

        } catch (IOException e) {
            System.out.println("Error: An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
