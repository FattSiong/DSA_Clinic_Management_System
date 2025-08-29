package boundary;

import adt.ListInterface;
import control.TreatmentController;
import entity.Consultation;
import entity.Medicine;
import entity.Treatment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Iterator;

public class TreatmentUI extends JFrame {

    private TreatmentController treatmentController;
    private JFrame parentFrame; // To show the main menu again

    // UI Components
    private JTable consultationTable;
    private DefaultTableModel tableModel;
    private JButton btnPrevious, btnNext, btnCreateTreatment, btnSearch, btnClear, btnBack;
    private JLabel lblPageInfo;
    private JTextField tfSearch;

    // State for pagination and searching
    private int currentPage = 1;
    private final int PAGE_SIZE = 5;
    private String currentSearchTerm = "";
    private ListInterface<Consultation> fullPendingList; // Holds the complete list for the current search

    public TreatmentUI(JFrame parent, TreatmentController controller) {
        this.parentFrame = parent;
        this.treatmentController = controller;

        setTitle("Medical Treatment Management - Pending Consultations");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle closing via button
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
        addListeners();

        // Initial data load
        refreshTableData();
    }

    private void initComponents() {
        // --- Main Panel ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel (Search) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search by Patient IC:"));
        tfSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnClear = new JButton("Clear");
        topPanel.add(tfSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnClear);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Center Panel (Table) ---
        String[] columnNames = {"No.", "Consult ID", "Patient Name", "Patient IC", "Doctor Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        consultationTable = new JTable(tableModel);
        consultationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        consultationTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(consultationTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel (Pagination and Actions) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Pagination controls
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrevious = new JButton("<< Previous");
        lblPageInfo = new JLabel("Page 1 of 1");
        btnNext = new JButton("Next >>");
        paginationPanel.add(btnPrevious);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);
        bottomPanel.add(paginationPanel, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCreateTreatment = new JButton("Create Treatment Record");
        btnBack = new JButton("Back to Main Menu");
        actionPanel.add(btnCreateTreatment);
        actionPanel.add(btnBack);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void refreshTableData() {
        // 1. Fetch the complete filtered list from the controller
        fullPendingList = treatmentController.getPendingConsultationsForToday(currentSearchTerm);

        // 2. Update pagination variables
        int totalEntries = fullPendingList.getLength();
        int totalPages = (int) Math.ceil((double) totalEntries / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        // 3. Clear existing table rows
        tableModel.setRowCount(0);

        // 4. Populate the table with data for the current page
        if (!fullPendingList.isEmpty()) {
            int start = (currentPage - 1) * PAGE_SIZE + 1;
            int end = Math.min(start + PAGE_SIZE - 1, totalEntries);

            for (int i = start; i <= end; i++) {
                Consultation c = fullPendingList.getEntry(i);
                Object[] rowData = {
                    i,
                    c.getConsultationID(),
                    c.getPatient().getName(),
                    c.getPatient().getIcNumber(),
                    c.getDoctor().getName()
                };
                tableModel.addRow(rowData);
            }
        }
        
        // 5. Update UI state
        lblPageInfo.setText(String.format("Page %d of %d", currentPage, totalPages));
        btnPrevious.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        btnCreateTreatment.setEnabled(totalEntries > 0);
    }

    private void addListeners() {
        btnSearch.addActionListener(e -> {
            currentSearchTerm = tfSearch.getText();
            currentPage = 1;
            refreshTableData();
        });

        btnClear.addActionListener(e -> {
            tfSearch.setText("");
            currentSearchTerm = "";
            currentPage = 1;
            refreshTableData();
        });

        btnNext.addActionListener(e -> {
            currentPage++;
            refreshTableData();
        });

        btnPrevious.addActionListener(e -> {
            currentPage--;
            refreshTableData();
        });

        btnBack.addActionListener(e -> {
            this.dispose(); // Close this window
            parentFrame.setVisible(true); // Show the main menu
        });

        btnCreateTreatment.addActionListener(e -> handleCreateTreatment());
    }

    private void handleCreateTreatment() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a consultation from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the original index from the "No." column
        int originalIndex = (int) tableModel.getValueAt(selectedRow, 0);
        Consultation selectedConsultation = fullPendingList.getEntry(originalIndex);

        // --- Prompt for Treatment Details ---
        String details = JOptionPane.showInputDialog(this, "Enter Treatment Details:", "Create Treatment", JOptionPane.PLAIN_MESSAGE);
        if (details == null || details.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Treatment details cannot be empty.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- Prompt to Select Medicines ---
        ListInterface<Medicine> prescribedMedicines = treatmentController.selectMedicinesUI(); // This will still print to console
        // A proper Swing UI would use a JList in a JDialog here, but for the assignment, this is acceptable.

        if (prescribedMedicines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No medicines were prescribed. Treatment record not created.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // --- Add Treatment via Controller ---
        Treatment newTreatment = treatmentController.addTreatment(selectedConsultation, details, prescribedMedicines);
        if (newTreatment != null) {
            JOptionPane.showMessageDialog(this, "Successfully created treatment record:\nID: " + newTreatment.getTreatmentID(), "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh the table to remove the consultation that was just processed
            refreshTableData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create treatment record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
