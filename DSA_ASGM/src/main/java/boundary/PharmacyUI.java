/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import entity.DispensedItem;
import entity.DispensingRecord;
import entity.Doctor;
import entity.Medicine;
import entity.Patient;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultRowSorter;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;

public class PharmacyUI extends JFrame {

    // Main data lists
    private ListInterface<DispensingRecord> dispensingRecords;
    private ListInterface<Medicine> medicineList;
    private ListInterface<Patient> patientList;
    private ListInterface<Doctor> doctorList;

    // Components for Dispensing Records
    private JTable recordTable;
    private DefaultTableModel recordTableModel;

    // Components for Medicine Inventory
    private JTable medicineTable;
    private DefaultTableModel medicineTableModel;

    private JFrame parentFrame;

    public PharmacyUI(JFrame parent, ListInterface<DispensingRecord> dispensingRecords, ListInterface<Medicine> medicineList, ListInterface<Patient> patientList, ListInterface<Doctor> doctorList) {
        this.parentFrame = parent;
        this.dispensingRecords = dispensingRecords;
        this.medicineList = medicineList;
        this.patientList = patientList;
        this.doctorList = doctorList;

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

        // --- NEW: Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Records:"));
        JTextField searchField = new JTextField(25);
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH); // Add search bar to the top

        // --- Table Setup ---
        String[] recordColumnNames = {"Record ID", "Patient Name", "Doctor Name", "Date/Time", "Total Amount"};
        recordTableModel = new DefaultTableModel(recordColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(recordTableModel);
        loadRecordData();
        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        // --- NEW: Add Table Sorter for Real-Time Filtering ---
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

        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        // CRUD Actions Panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdd = new JButton("Add New Record");
        JButton btnUpdate = new JButton("Update Record");
        JButton btnDelete = new JButton("Delete Record");
        JButton btnSalesReport = new JButton("Generate Sales Report");
        actionsPanel.add(btnAdd);
        actionsPanel.add(btnUpdate);
        actionsPanel.add(btnDelete);
        actionsPanel.add(btnSalesReport);
        panel.add(actionsPanel, BorderLayout.SOUTH);

        // CREATE Action Listener
        btnAdd.addActionListener(e -> showAddRecordDialog());

        // UPDATE Action Listener
        btnUpdate.addActionListener(e -> {
            int selectedViewRow = recordTable.getSelectedRow();
            if (selectedViewRow >= 0) {
                int modelRow = recordTable.convertRowIndexToModel(selectedViewRow);
                String recordId = (String) recordTableModel.getValueAt(modelRow, 0);
                DispensingRecord record = findRecordById(recordId);
                if (record != null) {
                    showUpdateRecordDialog(record);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        // DELETE Action Listener
        btnDelete.addActionListener(e -> {
            int selectedViewRow = recordTable.getSelectedRow();
            if (selectedViewRow >= 0) {
                int modelRow = recordTable.convertRowIndexToModel(selectedViewRow);
                String recordId = (String) recordTableModel.getValueAt(modelRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete record " + recordId + "?\nThis will return the items to stock.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteRecord(recordId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        recordTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int viewRow = recordTable.rowAtPoint(evt.getPoint());
                    if (viewRow >= 0) {
                        int modelRow = recordTable.convertRowIndexToModel(viewRow);
                        String recordId = (String) recordTableModel.getValueAt(modelRow, 0);
                        DispensingRecord record = findRecordById(recordId);
                        if (record != null) {
                            showRecordDetails(record);
                        }
                    }
                }
            }
        });

        btnSalesReport.addActionListener(e -> showSalesReport());

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

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete medicine " + medicineId + "?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteMedicine(medicineId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a medicine to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnLowStockReport.addActionListener(e -> showLowStockReport());

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

            String newRecordId = generateNewRecordId(); // This is the new, standardized ID
            DispensingRecord newRecord = new DispensingRecord(newRecordId, patientName, doctorName);

            Iterator<DispensedItem> iterator = tempItemList.getIterator();
            while (iterator.hasNext()) {
                DispensedItem item = iterator.next();
                newRecord.addItem(item); // Adds item and calculates total

                // Deduct stock
                Medicine dispensedMedicine = item.getMedicine();
                dispensedMedicine.setStockQuantity(dispensedMedicine.getStockQuantity() - item.getQuantityDispensed());
            }

            dispensingRecords.add(newRecord);
            loadRecordData();
            loadMedicineData(); // Refresh medicine table to show new stock levels
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "New dispensing record created successfully!");
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showUpdateRecordDialog(DispensingRecord record) {
        // NOTE: Updating dispensed items in a historical record is complex and often discouraged.
        // This implementation allows updating only the Patient and Doctor.
        JComboBox<String> patientCombo = new JComboBox<>(getPatientNames());
        patientCombo.setSelectedItem(record.getPatientName());

        JComboBox<String> doctorCombo = new JComboBox<>(getDoctorNames());
        doctorCombo.setSelectedItem(record.getDoctorName());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Patient:"));
        panel.add(patientCombo);
        panel.add(new JLabel("Doctor:"));
        panel.add(doctorCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Record " + record.getRecordId(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            record.setPatientName((String) patientCombo.getSelectedItem());
            record.setDoctorName((String) doctorCombo.getSelectedItem());
            loadRecordData();
            JOptionPane.showMessageDialog(this, "Record updated successfully!");
        }
    }

    private void deleteRecord(String recordId) {
        int position = -1;
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        int counter = 1;
        DispensingRecord recordToDelete = null;

        while (iterator.hasNext()) {
            DispensingRecord record = iterator.next();
            if (record.getRecordId().equals(recordId)) {
                position = counter;
                recordToDelete = record;
                break;
            }
            counter++;
        }

        if (position != -1 && recordToDelete != null) {
            // Return items to stock
            Iterator<DispensedItem> itemIterator = recordToDelete.getDispensedItems().getIterator();
            while (itemIterator.hasNext()) {
                DispensedItem item = itemIterator.next();
                Medicine medicine = item.getMedicine();
                medicine.setStockQuantity(medicine.getStockQuantity() + item.getQuantityDispensed());
            }

            dispensingRecords.remove(position);
            loadRecordData();
            loadMedicineData(); // Refresh medicine table to show restored stock
            JOptionPane.showMessageDialog(this, "Record " + recordId + " deleted successfully.");
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

    private DispensingRecord findRecordById(String id) {
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        while (iterator.hasNext()) {
            DispensingRecord rec = iterator.next();
            if (rec.getRecordId().equals(id)) {
                return rec;
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

    /**
     * Generates a report of all dispensing records, sorted by total amount in
     * descending order.
     */
    private void showSalesReport() {
        if (dispensingRecords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No dispensing records available to generate a report.", "Report Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 1. Create a temporary copy of the list to avoid changing the original order.
        ListInterface<DispensingRecord> sortedRecords = new adt.DoublyLinkedList<>();
        Iterator<DispensingRecord> copyIterator = dispensingRecords.getIterator();
        while (copyIterator.hasNext()) {
            sortedRecords.add(copyIterator.next());
        }

        // 2. Define a Comparator to sort records by their total amount.
        Comparator<DispensingRecord> byAmount = Comparator.comparingDouble(DispensingRecord::getTotalAmount);

        // 3. Sort the copied list using the ADT's built-in sort method.
        sortedRecords.sortDescending(byAmount);

        // 4. Build the formatted report string from the sorted list.
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

        // 5. Display the report.
        JTextArea textArea = new JTextArea(reportContent.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Sales Report", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Generates a report of all medicines with stock at or below their reorder
     * level.
     */
    private void showLowStockReport() {
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

        // Display the report
        JTextArea textArea = new JTextArea(reportContent.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Low Stock Report", JOptionPane.INFORMATION_MESSAGE);
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

                // Validation checks
                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "ID and Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (findMedicineById(id) != null) {
                    JOptionPane.showMessageDialog(this, "A medicine with ID '" + id + "' already exists.", "Duplicate ID Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create new medicine and add it using the ADT's add method
                Medicine newMedicine = new Medicine(id, name, description, stock, price, reorderLvl);
                medicineList.add(newMedicine);

                loadMedicineData(); // Refresh the table
                JOptionPane.showMessageDialog(this, "New medicine added successfully!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for stock, price, or reorder level.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMedicine(String medicineIdToDelete) {
        // Important: Check if the medicine is used in any dispensing records
        Iterator<DispensingRecord> recordIterator = dispensingRecords.getIterator();
        while (recordIterator.hasNext()) {
            DispensingRecord record = recordIterator.next();
            Iterator<DispensedItem> itemIterator = record.getDispensedItems().getIterator();
            while (itemIterator.hasNext()) {
                if (itemIterator.next().getMedicine().getMedicineID().equals(medicineIdToDelete)) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete this medicine because it is part of a historical dispensing record (" + record.getRecordId() + ").",
                            "Deletion Blocked",
                            JOptionPane.ERROR_MESSAGE);
                    return; // Stop the deletion process
                }
            }
        }

        // Find the position of the medicine to remove it from the ADT
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

        // Use the ADT's remove method if found
        if (position != -1) {
            medicineList.remove(position);
            loadMedicineData(); // Refresh table
            JOptionPane.showMessageDialog(this, "Medicine " + medicineIdToDelete + " deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Could not find medicine to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
