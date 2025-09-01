/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import control.PharmacyManagementSystem;
import control.ClinicManagementSystem;
import entity.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;

public class PharmacyUI extends JFrame {

    // A single reference to the controller that holds all business logic
    private PharmacyManagementSystem pharmacyController;

    // UI Components
    private JTable recordTable, medicineTable, treatmentTable;
    private DefaultTableModel recordTableModel, medicineTableModel, treatmentTableModel;

    // References to the central data lists
    private ListInterface<DispensingRecord> dispensingRecords;
    private ListInterface<Medicine> medicineList;
    private ListInterface<Treatment> treatmentList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Patient> patientList;

    private JFrame parentFrame;

    public PharmacyUI(JFrame parent, ListInterface<DispensingRecord> dispensingRecords, ListInterface<Medicine> medicineList, ListInterface<Patient> patientList,
            ListInterface<Doctor> doctorList, ListInterface<Treatment> treatmentList, ListInterface<Consultation> consultationList) {
        this.parentFrame = parent;
        this.pharmacyController = new PharmacyManagementSystem();

        // Get references to the central data lists
        this.dispensingRecords = ClinicManagementSystem.getDispensingRecordList();
        this.medicineList = ClinicManagementSystem.getMedicineList();
        this.treatmentList = ClinicManagementSystem.getTreatmentList();

        // --- Frame Setup ---
        setTitle("Pharmacy Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Header Panel (NORTH) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Pharmacy Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Tabbed Pane (CENTER) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.addTab("Treatment Records", createTreatmentPanel());
        tabbedPane.addTab("Dispensing Records", createDispensingPanel());
        tabbedPane.addTab("Medicine Inventory", createMedicinePanel());
        add(tabbedPane, BorderLayout.CENTER);

        // --- Footer Panel (SOUTH) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back to Main Menu");
        footerPanel.add(btnBack);
        add(footerPanel, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> {
            parentFrame.setVisible(true);
            this.dispose();
        });
    }

    // ========== Panel for Dispensing Records ==========
    private JPanel createDispensingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Records:"));
        JTextField searchField = new JTextField(25);
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        String[] recordColumnNames = {"Record ID", "Patient Name", "Doctor Name", "Date/Time", "Total Amount"};
        recordTableModel = new DefaultTableModel(recordColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(recordTableModel);
        loadRecordData();

        javax.swing.table.TableColumnModel dispensingColumnModel = recordTable.getColumnModel();
        dispensingColumnModel.getColumn(0).setPreferredWidth(120);
        dispensingColumnModel.getColumn(1).setPreferredWidth(200);
        dispensingColumnModel.getColumn(2).setPreferredWidth(200);
        dispensingColumnModel.getColumn(3).setPreferredWidth(150);
        dispensingColumnModel.getColumn(4).setPreferredWidth(100);

        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(recordTableModel);
        recordTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String searchText = searchField.getText();
                if (searchText.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                        @Override
                        public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                            // Check columns: 0 (ID), 1 (Patient), 2 (Doctor), 3 (Date/Time)
                            for (int i = 0; i <= 3; i++) {
                                if (entry.getStringValue(i).toLowerCase().contains(searchText.toLowerCase())) {
                                    return true; // Include row if any of the columns match
                                }
                            }
                            return false; // Exclude row if no match is found
                        }
                    });
                }
            }
        });

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnUpdate = new JButton("Update Record");
        JButton btnDelete = new JButton("Revert to Treatment");
        JButton btnSalesReport = new JButton("Generate Sales Report");
        actionsPanel.add(btnUpdate);
        actionsPanel.add(btnDelete);
        actionsPanel.add(btnSalesReport);
        panel.add(actionsPanel, BorderLayout.SOUTH);

        btnUpdate.addActionListener(e -> {
            int selectedViewRow = recordTable.getSelectedRow();
            if (selectedViewRow >= 0) {
                int modelRow = recordTable.convertRowIndexToModel(selectedViewRow);
                String recordId = (String) recordTableModel.getValueAt(modelRow, 0);
                DispensingRecord record = pharmacyController.findRecordById(recordId);
                if (record != null) {
                    showUpdateRecordDialog(record);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedViewRow = recordTable.getSelectedRow();
            if (selectedViewRow >= 0) {
                int modelRow = recordTable.convertRowIndexToModel(selectedViewRow);
                String recordId = (String) recordTableModel.getValueAt(modelRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to revert this record?", "Confirm Revert", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        pharmacyController.revertDispensingRecord(recordId);
                        loadAllTables();
                        JOptionPane.showMessageDialog(this, "Record " + recordId + " has been reverted.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Revert Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record to revert.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSalesReport.addActionListener(e -> {
            String report = pharmacyController.generateSalesReport();
            displayReport("Sales Report", report);
        });

        // --- NEW: Add Mouse Listener for double-click functionality ---
        recordTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Check for a double-click
                    int viewRow = recordTable.rowAtPoint(evt.getPoint());
                    if (viewRow >= 0) {
                        int modelRow = recordTable.convertRowIndexToModel(viewRow);
                        String recordId = (String) recordTableModel.getValueAt(modelRow, 0);
                        DispensingRecord record = pharmacyController.findRecordById(recordId);
                        if (record != null) {
                            showRecordDetails(record); // Call the existing details method
                        }
                    }
                }
            }
        });

        return panel;
    }

    //Add the new method to create the Treatment Panel
    private JPanel createTreatmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Treatment ID", "Consultation ID", "Details", "Prescribed Medicines"};
        treatmentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        treatmentTable = new JTable(treatmentTableModel);
        loadTreatmentData();

        javax.swing.table.TableColumnModel columnModel = treatmentTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(120);
        columnModel.getColumn(2).setPreferredWidth(400);
        columnModel.getColumn(3).setPreferredWidth(150);

        panel.add(new JScrollPane(treatmentTable), BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnDispense = new JButton("Create Dispensing Record from Treatment");
        actionsPanel.add(btnDispense);
        panel.add(actionsPanel, BorderLayout.SOUTH);

        btnDispense.addActionListener(e -> {
            int selectedViewRow = treatmentTable.getSelectedRow();
            if (selectedViewRow >= 0) {
                int modelRow = treatmentTable.convertRowIndexToModel(selectedViewRow);
                String treatmentId = (String) treatmentTableModel.getValueAt(modelRow, 0);
                Treatment treatmentToDispense = pharmacyController.findTreatmentById(treatmentId);
                if (treatmentToDispense != null) {
                    showDispenseDialog(treatmentToDispense);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a treatment record to dispense.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    // ========== Panel for Medicine Inventory ==========
    private JPanel createMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Inventory:"));
        JTextField searchField = new JTextField(25);
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] medicineColumnNames = {"ID", "Name", "Description", "Stock", "Price ($)", "Reorder Lvl"};
        medicineTableModel = new DefaultTableModel(medicineColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicineTable = new JTable(medicineTableModel);
        loadMedicineData();

        javax.swing.table.TableColumnModel medicineColumnModel = medicineTable.getColumnModel();
        medicineColumnModel.getColumn(0).setPreferredWidth(80);   // ID
        medicineColumnModel.getColumn(1).setPreferredWidth(190);  // Name
        medicineColumnModel.getColumn(2).setPreferredWidth(310);  // Description
        medicineColumnModel.getColumn(3).setPreferredWidth(80);   // Stock
        medicineColumnModel.getColumn(4).setPreferredWidth(80);   // Price
        medicineColumnModel.getColumn(3).setPreferredWidth(80);   // Reorder Lvl

        panel.add(new JScrollPane(medicineTable), BorderLayout.CENTER);

        // --- Add Table Sorter for Real-Time Filtering ---
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(medicineTableModel);
        medicineTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            private void filter() {
                String searchText = searchField.getText();
                if (searchText.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                        @Override
                        public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                            // Check columns: 0 (ID), 1 (Name), 2 (Description)
                            for (int i = 0; i <= 2; i++) {
                                if (entry.getStringValue(i).toLowerCase().contains(searchText.toLowerCase())) {
                                    return true; // Include row if any of the columns match
                                }
                            }
                            return false; // Exclude row if no match is found
                        }
                    });
                }
            }
        });

        //Action Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAddMedicine = new JButton("Add New Medicine");
        JButton btnUpdateDetails = new JButton("Update Details");
        JButton btnUpdateStock = new JButton("Update Stock");
        JButton btnDeleteMedicine = new JButton("Delete Medicine");
        JButton btnLowStockReport = new JButton("Generate Low Stock Report");

        actionsPanel.add(btnAddMedicine);
        actionsPanel.add(btnUpdateDetails);
        actionsPanel.add(btnUpdateStock);
        actionsPanel.add(btnDeleteMedicine);
        actionsPanel.add(btnLowStockReport);
        panel.add(actionsPanel, BorderLayout.SOUTH);

        // --- Button Action Listeners ---
        btnAddMedicine.addActionListener(e -> showAddMedicineDialog());

        btnUpdateDetails.addActionListener(e -> {
            int selectedViewRow = medicineTable.getSelectedRow();
            if (selectedViewRow >= 0) {
                int modelRow = medicineTable.convertRowIndexToModel(selectedViewRow);
                String medicineId = (String) medicineTableModel.getValueAt(modelRow, 0);
                Medicine medicineToUpdate = findMedicineById(medicineId);
                if (medicineToUpdate != null) {
                    showUpdateDetailsDialog(medicineToUpdate);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a medicine to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnUpdateStock.addActionListener(e -> {
            int selectedViewRow = medicineTable.getSelectedRow();
            if (selectedViewRow >= 0) {
                int modelRow = medicineTable.convertRowIndexToModel(selectedViewRow);
                String medicineId = (String) medicineTableModel.getValueAt(modelRow, 0);
                Medicine medicineToUpdate = findMedicineById(medicineId);
                if (medicineToUpdate != null) {
                    showUpdateStockDialog(medicineToUpdate);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a medicine to update stock.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDeleteMedicine.addActionListener(e -> {
            int selectedViewRow = medicineTable.getSelectedRow();
            if (selectedViewRow >= 0) {
                int modelRow = medicineTable.convertRowIndexToModel(selectedViewRow);
                String medicineId = (String) medicineTableModel.getValueAt(modelRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Delete " + medicineId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        pharmacyController.deleteMedicine(medicineId);
                        loadMedicineData();
                        JOptionPane.showMessageDialog(this, "Medicine deleted successfully.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Deletion Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a medicine to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnLowStockReport.addActionListener(e -> {
            String report = pharmacyController.generateLowStockReport();
            displayReport("Low Stock Report", report);
        });

        return panel;
    }

    // ========== Dialogs for Dispensing Record CRUD ==========
    private void showAddRecordDialog() {
        JDialog dialog = new JDialog(this, "Add New Dispensing Record", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // --- Main Details Panel ---
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Record Details"));
        JComboBox<String> patientCombo = new JComboBox<>(getPatientNames());
        JComboBox<String> doctorCombo = new JComboBox<>(getDoctorNames());
        detailsPanel.add(new JLabel("Patient:"));
        detailsPanel.add(patientCombo);
        detailsPanel.add(new JLabel("Doctor:"));
        detailsPanel.add(doctorCombo);

        // --- Items Panel ---
        JPanel itemsPanel = new JPanel(new BorderLayout(5, 5));
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Dispensed Items"));
        DefaultTableModel itemsTableModel = new DefaultTableModel(new String[]{"Medicine", "Qty", "Subtotal"}, 0);
        JTable itemsTable = new JTable(itemsTableModel);
        itemsPanel.add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        // --- Add Item Sub-Panel ---
        JPanel addItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> medicineCombo = new JComboBox<>(getMedicineNames());
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton btnAddItem = new JButton("Add Item");
        addItemPanel.add(new JLabel("Medicine:"));
        addItemPanel.add(medicineCombo);
        addItemPanel.add(new JLabel("Quantity:"));
        addItemPanel.add(quantitySpinner);
        addItemPanel.add(btnAddItem);
        itemsPanel.add(addItemPanel, BorderLayout.SOUTH);

        // --- Dialog Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save Record");
        JButton btnCancel = new JButton("Cancel");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        // --- Add Panels to Dialog ---
        dialog.add(detailsPanel, BorderLayout.NORTH);
        dialog.add(itemsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners for Dialog ---
        ListInterface<DispensedItem> tempItemList = new adt.DoublyLinkedList<>();

        btnAddItem.addActionListener(e -> {
            String medName = (String) medicineCombo.getSelectedItem();
            Medicine selectedMed = findMedicineByName(medName);
            int quantity = (Integer) quantitySpinner.getValue();

            if (selectedMed.getStockQuantity() < quantity) {
                JOptionPane.showMessageDialog(dialog, "Not enough stock for " + medName + ". Available: " + selectedMed.getStockQuantity(), "Stock Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DispensedItem newItem = new DispensedItem(selectedMed, quantity);
            tempItemList.add(newItem);
            itemsTableModel.addRow(new Object[]{medName, quantity, String.format("%.2f", newItem.getSubtotal())});
        });

        btnSave.addActionListener(e -> {
            String patientName = (String) patientCombo.getSelectedItem();
            String doctorName = (String) doctorCombo.getSelectedItem();

            if (tempItemList.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please add at least one item.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newRecordId = generateNewRecordId();
            DispensingRecord newRecord = new DispensingRecord(newRecordId, patientName, doctorName);

            Iterator<DispensedItem> iterator = tempItemList.getIterator();
            while (iterator.hasNext()) {
                DispensedItem item = iterator.next();
                newRecord.addItem(item);

                // --- THIS IS THE CRITICAL LINE THAT MUST BE PRESENT ---
                // It finds the medicine from the item and reduces its stock.
                Medicine dispensedMedicine = item.getMedicine();
                dispensedMedicine.setStockQuantity(dispensedMedicine.getStockQuantity() - item.getQuantityDispensed());
                // ---------------------------------------------------------
            }

            dispensingRecords.add(newRecord);
            loadRecordData();
            loadMedicineData(); // This refreshes the medicine table to show the new stock
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "New dispensing record created successfully!");
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showUpdateRecordDialog(DispensingRecord record) {
        JDialog dialog = new JDialog(this, "Update Record: " + record.getRecordId(), true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // --- Create a table model that is editable only in the "New Qty" column ---
        String[] columnNames = {"Medicine", "Current Qty", "New Qty"};
        DefaultTableModel itemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only the "New Qty" column is editable
            }
        };

        // Populate the table with the record's current items
        Iterator<DispensedItem> iterator = record.getDispensedItems().getIterator();
        while (iterator.hasNext()) {
            DispensedItem item = iterator.next();
            itemsTableModel.addRow(new Object[]{
                item.getMedicine().getName(),
                item.getQuantityDispensed(),
                item.getQuantityDispensed() // New Qty starts same as current
            });
        }

        JTable itemsTable = new JTable(itemsTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dialog.add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        // --- Dialog Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSaveChanges = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");
        buttonPanel.add(btnSaveChanges);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // --- "Save Changes" Action Listener ---
        btnSaveChanges.addActionListener(e -> {
            // Stop cell editing to make sure the last typed value is saved
            if (itemsTable.isEditing()) {
                itemsTable.getCellEditor().stopCellEditing();
            }

            // --- Step 1: Validate all changes before applying any ---
            for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
                DispensedItem currentItem = record.getDispensedItems().getEntry(i + 1);
                int originalQty = currentItem.getQuantityDispensed();
                int newQty;
                try {
                    newQty = Integer.parseInt(itemsTableModel.getValueAt(i, 2).toString());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid quantity for " + currentItem.getMedicine().getName(), "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantityChange = newQty - originalQty;
                if (quantityChange > 0 && currentItem.getMedicine().getStockQuantity() < quantityChange) {
                    JOptionPane.showMessageDialog(dialog, "Not enough stock to increase quantity for " + currentItem.getMedicine().getName() + ".\nAvailable stock: " + currentItem.getMedicine().getStockQuantity(), "Stock Error", JOptionPane.ERROR_MESSAGE);
                    return; // Abort the save
                }
            }

            // --- Step 2: Apply all changes since validation passed ---
            for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
                DispensedItem currentItem = record.getDispensedItems().getEntry(i + 1);
                int originalQty = currentItem.getQuantityDispensed();
                int newQty = Integer.parseInt(itemsTableModel.getValueAt(i, 2).toString());

                // This calculates the difference (e.g., 50 - 2 = 48)
                int quantityChange = newQty - originalQty;

                if (quantityChange != 0) {
                    // This adjusts the stock by that difference (e.g., currentStock - 48)
                    Medicine medicine = currentItem.getMedicine();
                    medicine.setStockQuantity(medicine.getStockQuantity() - quantityChange);

                    // This updates the item's quantity for future reference
                    currentItem.setQuantityDispensed(newQty);
                }
            }

            // Recalculate the record's total and refresh UI
            record.recalculateTotal();
            loadRecordData();
            loadMedicineData();

            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Record updated successfully!");
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void revertDispensingRecord(String recordId) {
        // 1. Find the Dispensing Record to revert
        int position = -1;
        int counter = 1;
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        DispensingRecord recordToRevert = null;

        while (iterator.hasNext()) {
            DispensingRecord record = iterator.next();
            if (record.getRecordId().equals(recordId)) {
                position = counter;
                recordToRevert = record;
                break;
            }
            counter++;
        }

        if (position != -1 && recordToRevert != null) {
            // --- THIS IS THE CORRECTED LOGIC ---

            // 2. Create a NEW list and populate it by iterating through ALL items
            ListInterface<Medicine> prescribedMedicines = new adt.DoublyLinkedList<>();
            Iterator<DispensedItem> itemIterator = recordToRevert.getDispensedItems().getIterator();

            while (itemIterator.hasNext()) {
                DispensedItem item = itemIterator.next();
                Medicine medicine = item.getMedicine();

                // Action A: Return the item's quantity back to the main stock
                medicine.setStockQuantity(medicine.getStockQuantity() + item.getQuantityDispensed());

                // Action B: Add the medicine to our list for the new treatment
                prescribedMedicines.add(medicine);
            }

            // 3. Create the new Treatment Record with the complete list of medicines
            String newTreatmentId = "T" + recordToRevert.getRecordId().substring(3);
            String details = recordToRevert.getOriginalTreatmentDetails();
            Consultation originalConsultation = recordToRevert.getOriginalConsultation();

            if (originalConsultation == null) {
                JOptionPane.showMessageDialog(this, "Cannot revert: Original consultation data is missing.", "Revert Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Pass the fully populated 'prescribedMedicines' list here
            Treatment newTreatment = new Treatment(newTreatmentId, originalConsultation, details, prescribedMedicines);
            control.ClinicManagementSystem.getTreatmentList().add(newTreatment);

            // 4. Remove the old Dispensing Record
            dispensingRecords.remove(position);

            // 5. Refresh all tables to show the changes
            loadRecordData();
            loadMedicineData();
            loadTreatmentData();

            JOptionPane.showMessageDialog(this, "Record " + recordId + " has been reverted to Treatment Record " + newTreatmentId + ".");
        }
    }

    // ========== Data Loading and Helper Methods ==========
    private void loadRecordData() {
        recordTableModel.setRowCount(0);
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (iterator.hasNext()) {
            DispensingRecord record = iterator.next();
            recordTableModel.addRow(new Object[]{
                record.getRecordId(), record.getPatientName(), record.getDoctorName(),
                record.getDispensingDateTime().format(formatter), String.format("$%.2f", record.getTotalAmount())
            });
        }
    }

    private void loadMedicineData() {
        medicineTableModel.setRowCount(0);
        Iterator<Medicine> iterator = medicineList.getIterator();
        while (iterator.hasNext()) {
            Medicine med = iterator.next();
            medicineTableModel.addRow(new Object[]{
                med.getMedicineID(), med.getName(), med.getDescription(),
                med.getStockQuantity(), String.format("%.2f", med.getPrice()), med.getReorderLvl()
            });
        }
    }

    // --- Update Dialogs for Medicine ---
    private void showUpdateDetailsDialog(Medicine medicine) {
        JTextField descriptionField = new JTextField(medicine.getDescription());
        JTextField priceField = new JTextField(String.valueOf(medicine.getPrice()));
        JTextField reorderLvlField = new JTextField(String.valueOf(medicine.getReorderLvl()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Price ($):"));
        panel.add(priceField);
        panel.add(new JLabel("Reorder Level:"));
        panel.add(reorderLvlField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update " + medicine.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                medicine.setDescription(descriptionField.getText());
                medicine.setPrice(Double.parseDouble(priceField.getText()));
                medicine.setReorderLvl(Integer.parseInt(reorderLvlField.getText()));
                loadMedicineData();
                JOptionPane.showMessageDialog(this, "Medicine details updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check price and reorder level.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateStockDialog(Medicine medicine) {
        String input = JOptionPane.showInputDialog(this,
                "Current stock for " + medicine.getName() + " is " + medicine.getStockQuantity() + ".\n"
                + "Enter quantity to add (use a negative number to remove):", "Update Stock", JOptionPane.PLAIN_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int qtyChange = Integer.parseInt(input.trim());
                int newStock = medicine.getStockQuantity() + qtyChange;
                if (newStock < 0) {
                    JOptionPane.showMessageDialog(this, "Stock cannot go below zero.", "Invalid Operation", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                medicine.setStockQuantity(newStock);
                loadMedicineData();
                JOptionPane.showMessageDialog(this, "Stock updated successfully. New quantity: " + newStock);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDispenseDialog(Treatment treatment) {
        if (treatment.getPrescribedMedicine().isEmpty()) {
            JOptionPane.showMessageDialog(this, "This treatment has no medication prescribed.", "Cannot Dispense", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Dispense Medication for Treatment: " + treatment.getTreatmentID(), true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Confirm Quantities"));

        java.util.List<JSpinner> quantitySpinners = new java.util.ArrayList<>();
        Iterator<Medicine> medIterator = treatment.getPrescribedMedicine().getIterator();

        while (medIterator.hasNext()) {
            Medicine med = medIterator.next();
            JPanel medPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            medPanel.add(new JLabel(String.format("%s (Available: %d):", med.getName(), med.getStockQuantity())));
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, med.getStockQuantity(), 1));
            quantitySpinners.add(quantitySpinner);
            medPanel.add(quantitySpinner);
            fieldsPanel.add(medPanel);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirmDispense = new JButton("Confirm and Dispense");
        JButton btnCancel = new JButton("Cancel");
        buttonPanel.add(btnConfirmDispense);
        buttonPanel.add(btnCancel);

        dialog.add(new JScrollPane(fieldsPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // --- REFACTORED AND CORRECTED Action Listener ---
        btnConfirmDispense.addActionListener(e -> {
            try {
                // Collect quantities from spinners
                List<Integer> quantities = new ArrayList<>();
                for (JSpinner spinner : quantitySpinners) {
                    quantities.add((Integer) spinner.getValue());
                }
                // Call controller to perform the action
                pharmacyController.dispenseFromTreatment(treatment, quantities);
                loadAllTables();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Dispensing successful.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dispense Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // --- Helper methods to populate JComboBoxes ---
    private String[] getPatientNames() {
        String[] names = new String[patientList.getLength()];
        Iterator<Patient> it = patientList.getIterator();
        int i = 0;
        while (it.hasNext()) {
            names[i++] = it.next().getName();
        }
        return names;
    }

    private String[] getDoctorNames() {
        String[] names = new String[doctorList.getLength()];
        Iterator<Doctor> it = doctorList.getIterator();
        int i = 0;
        while (it.hasNext()) {
            names[i++] = it.next().getName();
        }
        return names;
    }

    private String[] getMedicineNames() {
        String[] names = new String[medicineList.getLength()];
        Iterator<Medicine> it = medicineList.getIterator();
        int i = 0;
        while (it.hasNext()) {
            names[i++] = it.next().getName();
        }
        return names;
    }

    // --- Find Methods ---
    private Medicine findMedicineById(String id) {
        Iterator<Medicine> iterator = medicineList.getIterator();
        while (iterator.hasNext()) {
            Medicine med = iterator.next();
            if (med.getMedicineID().equals(id)) {
                return med;
            }
        }
        return null;
    }

    private Medicine findMedicineByName(String name) {
        Iterator<Medicine> iterator = medicineList.getIterator();
        while (iterator.hasNext()) {
            Medicine med = iterator.next();
            if (med.getName().equals(name)) {
                return med;
            }
        }
        return null;
    }

    private String generateNewRecordId() {
        int maxIdNum = 1000; // Start with 1000 so the first ID will be 1001
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();

        while (iterator.hasNext()) {
            DispensingRecord record = iterator.next();
            String currentId = record.getRecordId();

            // Check if the ID matches the expected format "MDR" + numbers
            if (currentId != null && currentId.startsWith("MDR")) {
                try {
                    int idNum = Integer.parseInt(currentId.substring(3)); // Get number part
                    if (idNum > maxIdNum) {
                        maxIdNum = idNum; // Found a new highest ID
                    }
                } catch (NumberFormatException e) {
                    // Ignore malformed IDs
                    System.err.println("Skipping malformed Record ID: " + currentId);
                }
            }
        }
        return "MDR" + (maxIdNum + 1); // Return the next ID in the sequence
    }

    private void showRecordDetails(DispensingRecord record) {
        StringBuilder details = new StringBuilder();
        details.append("Record ID: \t").append(record.getRecordId()).append("\n");
        details.append("Date/Time: \t").append(record.getDispensingDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        details.append("Patient Name: \t").append(record.getPatientName()).append("\n");
        details.append("Doctor Name: \t").append(record.getDoctorName()).append("\n");
        details.append("Total Amount: \t").append(String.format("$%.2f", record.getTotalAmount())).append("\n\n");
        details.append("---------- Dispensed Items ----------\n");

        if (record.getDispensedItems() == null || record.getDispensedItems().isEmpty()) {
            details.append("No items found for this record.\n");
        } else {
            Iterator<DispensedItem> itemIterator = record.getDispensedItems().getIterator();
            while (itemIterator.hasNext()) {
                DispensedItem item = itemIterator.next();
                details.append(item.toString()).append("\n");
            }
        }

        // Use a JTextArea inside a JScrollPane for better formatting
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Record Details - " + record.getRecordId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddMedicineDialog() {
        // Create input fields for the dialog
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField reorderLvlField = new JTextField();

        // Layout the fields in a panel
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Medicine ID (e.g., M1005):"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Initial Stock:"));
        panel.add(stockField);
        panel.add(new JLabel("Price ($):"));
        panel.add(priceField);
        panel.add(new JLabel("Reorder Level:"));
        panel.add(reorderLvlField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Medicine",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText().trim().toUpperCase();
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                int stock = Integer.parseInt(stockField.getText());
                double price = Double.parseDouble(priceField.getText());
                int reorderLvl = Integer.parseInt(reorderLvlField.getText());

                pharmacyController.addMedicine(id, name, description, stock, price, reorderLvl);
                loadMedicineData();
                JOptionPane.showMessageDialog(this, "New medicine added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Add Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Add the new method to load treatment data into the table
    private void loadTreatmentData() {
        if (treatmentTableModel == null) {
            return; // Defensive check
        }
        treatmentTableModel.setRowCount(0);
        Iterator<Treatment> iterator = treatmentList.getIterator();

        while (iterator.hasNext()) {
            Treatment treatment = iterator.next();

            // Format the list of prescribed medicines into a single string
            StringBuilder medIDs = new StringBuilder();
            Iterator<Medicine> medIterator = treatment.getPrescribedMedicine().getIterator();
            while (medIterator.hasNext()) {
                medIDs.append(medIterator.next().getMedicineID());
                if (medIterator.hasNext()) {
                    medIDs.append(", ");
                }
            }

            treatmentTableModel.addRow(new Object[]{
                treatment.getTreatmentID(),
                treatment.getConsultation() != null ? treatment.getConsultation().getConsultationID() : "N/A",
                treatment.getTreatmentDetails(),
                medIDs.toString()
            });
        }
    }

    private void loadAllTables() {
        loadRecordData();
        loadMedicineData();
        loadTreatmentData();
    }

    private void displayReport(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
