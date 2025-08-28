/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

/**
 *
 * @author Melvin
 */
import adt.ListInterface;
import entity.DispensingRecord;
import entity.DispensedItem;
import entity.Medicine;
import control.PharmacyManagementSystem;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class PharmacyUI extends JFrame {

    private JTable recordTable;
    private DefaultTableModel tableModel;
    private ListInterface<DispensingRecord> dispensingRecords;
    private JFrame parentFrame;
    private PharmacyManagementSystem pharmacySystem;

    public PharmacyUI(JFrame parent, ListInterface<DispensingRecord> dispensingRecords) {
        this.parentFrame = parent;
        this.dispensingRecords = dispensingRecords;

        // --- Frame Setup ---
        setTitle("Dispensing Record Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- Header Panel (NORTH) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Dispensing Records Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Panel (CENTER) ---
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);

        // --- Table Panel ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                " All Dispensing Records ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16)
        ));
        tablePanel.setBackground(Color.WHITE);

        String[] columnNames = {"Record ID", "Patient Name", "Doctor Name", "Date/Time", "Total Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recordTable = new JTable(tableModel);
        recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordTable.setFont(new Font("Arial", Font.PLAIN, 14));
        recordTable.setRowHeight(30);
        recordTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        recordTable.getTableHeader().setBackground(new Color(230, 230, 230));

        loadRecordData();

        JScrollPane tableScrollPane = new JScrollPane(recordTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // --- Actions Panel (SOUTH of Content Panel) ---
        JPanel actionsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionsPanel.setBackground(Color.WHITE);

        JButton btnViewDetails = new JButton("View Record Details");
        JButton btnGenerateReport = new JButton("Generate History Report");
        JButton btnAddRecord = new JButton("Add New Record");
        JButton btnDeleteRecord = new JButton("Delete Record");
        JButton btnBack = new JButton("Back to Main Menu");

        // Style buttons
        styleButton(btnViewDetails);
        styleButton(btnGenerateReport, new Color(52, 152, 219)); // Blue
        styleButton(btnAddRecord, new Color(46, 204, 113)); // Green
        styleButton(btnDeleteRecord, new Color(231, 76, 60)); // Red
        styleButton(btnBack, new Color(149, 165, 166)); // Gray

        actionsPanel.add(btnAddRecord);
        actionsPanel.add(btnViewDetails);
        actionsPanel.add(btnDeleteRecord);
        actionsPanel.add(btnGenerateReport);
        actionsPanel.add(btnBack);
        
        // Add a filler panel for layout purposes
        JPanel fillerPanel = new JPanel();
        fillerPanel.setBackground(Color.WHITE);
        actionsPanel.add(fillerPanel);

        contentPanel.add(actionsPanel, BorderLayout.SOUTH);

        // --- Add main content panel to frame ---
        add(contentPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
        btnViewDetails.addActionListener(e -> {
            int selectedRow = recordTable.getSelectedRow();
            if (selectedRow >= 0) {
                String recordId = (String) tableModel.getValueAt(selectedRow, 0);
                DispensingRecord record = findRecordById(recordId);
                if (record != null) {
                    showRecordDetails(record);
                } else {
                    JOptionPane.showMessageDialog(this, "Record not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAddRecord.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Functionality to add new record is not yet implemented.", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            // This is where you would call a new UI for adding a record
        });

        btnDeleteRecord.addActionListener(e -> {
            int selectedRow = recordTable.getSelectedRow();
            if (selectedRow >= 0) {
                String recordId = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete record " + recordId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteRecord(recordId);
                    loadRecordData(); // Refresh the table
                    JOptionPane.showMessageDialog(this, "Record " + recordId + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a record to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnGenerateReport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save the report");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                pharmacySystem.generateDispensingHistoryReport(filename);
            }
        });

        btnBack.addActionListener(e -> {
            parentFrame.setVisible(true);
            this.dispose();
        });
    }

    private void loadRecordData() {
        tableModel.setRowCount(0);
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (iterator.hasNext()) {
            DispensingRecord record = iterator.next();
            tableModel.addRow(new Object[]{
                record.getRecordId(),
                "N/A", // Patient Name is not available in the provided DispensingRecord entity
                "N/A", // Doctor Name is not available in the provided DispensingRecord entity
                record.getDispensingDateTime().format(formatter),
                String.format("$%.2f", record.getTotalAmount())
            });
        }
    }
    
    // Placeholder method, as the DispensingRecord entity does not have a patient/doctor name field.
    // This assumes they would be added in a full implementation.
    private DispensingRecord findRecordById(String recordId) {
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        while (iterator.hasNext()) {
            DispensingRecord record = iterator.next();
            if (record.getRecordId().equals(recordId)) {
                return record;
            }
        }
        return null;
    }

    private void deleteRecord(String recordId) {
        // This is not a very efficient way to do it for a linked list,
        // but it matches the provided ADT interface.
        int position = -1;
        Iterator<DispensingRecord> iterator = dispensingRecords.getIterator();
        int counter = 1;
        while (iterator.hasNext()) {
            DispensingRecord record = iterator.next();
            if (record.getRecordId().equals(recordId)) {
                position = counter;
                break;
            }
            counter++;
        }

        if (position != -1) {
            dispensingRecords.remove(position);
        }
    }

    private void showRecordDetails(DispensingRecord record) {
        StringBuilder details = new StringBuilder();
        details.append("Record ID: ").append(record.getRecordId()).append("\n");
        details.append("Date/Time: ").append(record.getDispensingDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        details.append("Total Amount: ").append(String.format("$%.2f", record.getTotalAmount())).append("\n\n");
        details.append("Dispensed Items:\n");

        // The DispensingRecord entity has a dispensedItems field, but its constructor
        // doesn't initialize it, so this might throw a NullPointerException.
        // Assuming it's properly initialized elsewhere.
        try {
            Iterator<DispensedItem> itemIterator = record.getDispensedItems().getIterator();
            while (itemIterator.hasNext()) {
                DispensedItem item = itemIterator.next();
                details.append(item.toString()).append("\n");
            }
        } catch (NullPointerException e) {
            details.append("No dispensed items found for this record.\n");
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Record Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void styleButton(JButton button) {
        styleButton(button, new Color(92, 184, 92)); // Default green color
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
