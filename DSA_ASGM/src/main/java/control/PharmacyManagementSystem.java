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
        medicineStock.add(new Medicine("PAN100", "Paracetamol 500mg", 100, 5.50, 20));
        medicineStock.add(new Medicine("IBU200", "Ibuprofen 200mg", 80, 8.00, 25));
        medicineStock.add(new Medicine("AMO500", "Amoxicillin 500mg", 50, 15.75, 10));
        medicineStock.add(new Medicine("LOR010", "Loratadine 10mg", 120, 12.20, 30));
    }

    // ========== 1. Medicine Stock Control ==========

    public void addOrUpdateMedicine() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Medicine ID (e.g., PAN100): ");
        String id = scanner.nextLine().toUpperCase();
        
        Medicine existingMedicine = findMedicineById(id);

        if (existingMedicine != null) {
            System.out.printf("Medicine '%s' found. Current stock: %d\n", existingMedicine.getName(), existingMedicine.getQuantityInStock());
            System.out.print("Enter quantity to add (positive number) or remove (negative number): ");
            int qtyChange = scanner.nextInt();
            int newQty = existingMedicine.getQuantityInStock() + qtyChange;
            existingMedicine.setQuantityInStock(newQty);
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
            
            Medicine newMedicine = new Medicine(id, name, qty, price, reorderLvl);
            medicineStock.add(newMedicine);
            System.out.println("New medicine added successfully!");
        }
    }
    
    public Medicine findMedicineById(String medicineId) {
        Iterator<Medicine> iterator = medicineStock.getIterator();
        while (iterator.hasNext()) {
            Medicine currentMedicine = iterator.next();
            if (currentMedicine.getMedicineId().equals(medicineId)) {
                return currentMedicine;
            }
        }
        return null; // Not found
    }
    
    public void viewAllMedicines() {
        System.out.println("\n--- Current Medicine Inventory ---");
        if (medicineStock.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        Iterator<Medicine> iterator = medicineStock.getIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println("------------------------------------");
    }

    // ========== 2. Medicine Dispensing ==========
    
    public void createDispensingRecord() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter Patient Name: ");
        String patientName = scanner.nextLine();
        System.out.print("Enter Doctor Name: ");
        String doctorName = scanner.nextLine();
        
        String recordId = "REC" + String.format("%04d", nextRecordId++);
        DispensingRecord newRecord = new DispensingRecord(recordId, patientName, doctorName);
        
        String medicineId;
        while (true) {
            System.out.print("Enter Medicine ID to dispense (or 'done' to finish): ");
            medicineId = scanner.nextLine().toUpperCase();
            if (medicineId.equalsIgnoreCase("done")) {
                break;
            }
            
            Medicine medicineToDispense = findMedicineById(medicineId);
            
            if (medicineToDispense == null) {
                System.out.println("Error: Medicine ID not found in stock.");
                continue;
            }
            
            System.out.print("Enter quantity for " + medicineToDispense.getName() + ": ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Stock validation
            if (quantity <= 0) {
                 System.out.println("Error: Quantity must be positive.");
            } else if (medicineToDispense.getQuantityInStock() < quantity) {
                System.out.println("Error: Insufficient stock. Available: " + medicineToDispense.getQuantityInStock());
            } else {
                // All checks passed, process the item
                // 1. Update stock
                int newStock = medicineToDispense.getQuantityInStock() - quantity;
                medicineToDispense.setQuantityInStock(newStock);
                
                // 2. Add to record
                DispensedItem item = new DispensedItem(medicineToDispense, quantity);
                newRecord.addItem(item);
                
                System.out.println("Added to prescription.");
            }
        }
        
        if (!newRecord.getDispensedItems().isEmpty()) {
            dispensingHistory.add(newRecord);
            System.out.println("\nDispensing complete. Final record:");
            System.out.println(newRecord);
        } else {
            System.out.println("\nNo items were dispensed. Record cancelled.");
            nextRecordId--; // Rollback ID if record is empty
        }
    }
    
    // ========== 3. Reporting Features ==========
    
    public void generateLowStockReport() {
        System.out.println("\n--- Low Stock Report ---");
        boolean found = false;
        Iterator<Medicine> iterator = medicineStock.getIterator();
        while (iterator.hasNext()) {
            Medicine medicine = iterator.next();
            if (medicine.getQuantityInStock() <= medicine.getReorderLevel()) {
                System.out.printf("LOW STOCK ALERT: %s (Qty: %d, Reorder Lvl: %d)\n",
                        medicine.getName(), medicine.getQuantityInStock(), medicine.getReorderLevel());
                found = true;
            }
        }
        if (!found) {
            System.out.println("All medicines are above their reorder level.");
        }
        System.out.println("--------------------------");
    }
    
    public void generateDispensingHistoryReport() {
        System.out.println("\n--- Full Dispensing History ---");
        if (dispensingHistory.isEmpty()) {
            System.out.println("No dispensing records found.");
            return;
        }
        
        // You could sort the history before displaying, e.g., by ID
        // dispensingHistory.sort(); // Assuming your record ID increments
        
        Iterator<DispensingRecord> iterator = dispensingHistory.getIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
