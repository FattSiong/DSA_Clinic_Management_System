// Name: <Your Name>
package boundary;

import adt.ListInterface;
import control.TreatmentController;
import entity.Treatment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Objects;

public class TreatmentHistoryDialog extends JDialog {

    private final TreatmentController treatmentController;
    private final Frame owner;

    // UI Components
    private JTable treatmentTable;
    private DefaultTableModel treatmentTableModel;
    private JComboBox<String> cmbSearchBy;
    private JTextField tfSearchTerm;
    private JComboBox<String> cmbReportType;
    private JTextField tfStartDate;
    private JTextField tfEndDate;
    private JPanel datePanel;

    public TreatmentHistoryDialog(Frame owner, TreatmentController controller) {
        super(owner, "Treatment History and Reports", true);
        this.owner = owner;
        this.treatmentController = controller;

        setSize(900, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        populateTreatments("", "Patient Name");
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // --- Search Panel (NORTH) ---
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControls.add(new JLabel("Search History By:"));
        String[] searchOptions = {"Patient Name", "Doctor Name", "Treatment ID"};
        cmbSearchBy = new JComboBox<>(searchOptions);
        searchControls.add(cmbSearchBy);
        searchPanel.add(searchControls, BorderLayout.WEST);

        tfSearchTerm = new JTextField(20);
        searchPanel.add(tfSearchTerm, BorderLayout.CENTER);

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        searchButtons.add(btnSearch);
        searchButtons.add(btnClear);
        searchPanel.add(searchButtons, BorderLayout.EAST);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Center Panel with Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.8);

        // --- Table Panel (Top of Split Pane) ---
        String[] columnNames = {"ID", "Patient", "Doctor", "Date", "Details", "Medicines"};
        treatmentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        treatmentTable = new JTable(treatmentTableModel);
        treatmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(treatmentTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Treatment Records"));
        splitPane.setTopComponent(tableScrollPane);
        
        // --- Report Control Panel (Bottom of Split Pane) ---
        JPanel reportControlPanel = new JPanel(new BorderLayout(10, 5));
        reportControlPanel.setBorder(BorderFactory.createTitledBorder("Reports"));
        
        datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        tfStartDate = new JTextField(10);
        tfStartDate.setText(LocalDate.now().minusMonths(1).toString());
        datePanel.add(tfStartDate);
        datePanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        tfEndDate = new JTextField(10);
        tfEndDate.setText(LocalDate.now().toString());
        datePanel.add(tfEndDate);
        datePanel.setVisible(true);
        reportControlPanel.add(datePanel, BorderLayout.NORTH);

        JPanel generationControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generationControls.add(new JLabel("Report Type:"));
        cmbReportType = new JComboBox<>(new String[]{"Diagnosis Frequency", "Medicine Prescription Frequency"});
        generationControls.add(cmbReportType);
        JButton btnGenerateReport = new JButton("Generate Report");
        generationControls.add(btnGenerateReport);
        reportControlPanel.add(generationControls, BorderLayout.CENTER);

        splitPane.setBottomComponent(reportControlPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // --- Action Button Panel (SOUTH) ---
        JPanel actionPanel = new JPanel(new BorderLayout());
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnClose = new JButton("Close");
        rightButtons.add(btnEdit);
        rightButtons.add(btnDelete);
        rightButtons.add(btnClose);
        actionPanel.add(rightButtons, BorderLayout.EAST);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // --- Add Listeners ---
        btnSearch.addActionListener(e -> searchTreatments());
        btnClear.addActionListener(e -> {
            tfSearchTerm.setText("");
            cmbSearchBy.setSelectedIndex(0);
            populateTreatments("", "Patient Name");
        });
        btnClose.addActionListener(e -> dispose());
        btnDelete.addActionListener(e -> deleteSelectedTreatment());
        btnEdit.addActionListener(e -> editSelectedTreatment());
        btnGenerateReport.addActionListener(e -> generateAndShowReport());

        cmbReportType.addActionListener(e -> {
            boolean isDiagnosisReport = "Diagnosis Frequency".equals(cmbReportType.getSelectedItem());
            datePanel.setVisible(isDiagnosisReport);
        });

        // ADDED: Mouse listener for double-click action
        treatmentTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2 && treatmentTable.getSelectedRow() != -1) {
                    showTreatmentDetails();
                }
            }
        });
    }

    private void showTreatmentDetails() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) return; // Should not happen, but safe to check

        String treatmentId = (String) treatmentTableModel.getValueAt(selectedRow, 0);
        Treatment treatment = treatmentController.findTreatmentById(treatmentId);

        if (treatment != null) {
            new TreatmentDetailDialog(this.owner, treatment).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Could not retrieve details for the selected treatment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateAndShowReport() {
        String reportType = Objects.requireNonNull(cmbReportType.getSelectedItem()).toString();
        String reportText;

        if ("Medicine Prescription Frequency".equals(reportType)) {
            reportText = treatmentController.generateMedicineFrequencyReport();
        } else if ("Diagnosis Frequency".equals(reportType)) {
            try {
                LocalDate startDate = LocalDate.parse(tfStartDate.getText());
                LocalDate endDate = LocalDate.parse(tfEndDate.getText());
                
                if (endDate.isBefore(startDate)) {
                     JOptionPane.showMessageDialog(this, "End date cannot be before start date.", "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                reportText = treatmentController.generateDiagnosisFrequencyReport(startDate, endDate);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            reportText = "Invalid report type selected.";
        }
        
        ReportDisplayDialog displayDialog = new ReportDisplayDialog(this.owner, reportType, reportText);
        displayDialog.setVisible(true);
    }

    private void populateTreatments(String searchTerm, String searchBy) {
        treatmentTableModel.setRowCount(0);
        ListInterface<Treatment> treatments = treatmentController.searchTreatments(searchTerm, searchBy);

        Iterator<Treatment> it = treatments.getIterator();
        while (it.hasNext()) {
            Treatment t = it.next();
            Object[] row = {
                    t.getTreatmentID(),
                    t.getConsultation().getPatient().getName(),
                    t.getConsultation().getDoctor().getName(),
                    t.getConsultation().getConsultationDate(),
                    t.getTreatmentDetails(),
                    treatmentController.getMedicineNames(t.getPrescribedMedicine())
            };
            treatmentTableModel.addRow(row);
        }
    }
    
    private void searchTreatments() {
        String searchTerm = tfSearchTerm.getText();
        String searchBy = Objects.requireNonNull(cmbSearchBy.getSelectedItem()).toString();
        populateTreatments(searchTerm, searchBy);
    }

    private void deleteSelectedTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a treatment from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String treatmentId = (String) treatmentTableModel.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete treatment " + treatmentId + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            boolean success = treatmentController.deleteTreatment(treatmentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Treatment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                populateTreatments(tfSearchTerm.getText(), (String) cmbSearchBy.getSelectedItem());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete treatment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editSelectedTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a treatment from the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String treatmentId = (String) treatmentTableModel.getValueAt(selectedRow, 0);
        Treatment treatmentToEdit = treatmentController.findTreatmentById(treatmentId);

        if (treatmentToEdit != null) {
            EditTreatmentDialog editDialog = new EditTreatmentDialog(this.owner, treatmentController, treatmentToEdit);
            editDialog.setVisible(true);
            populateTreatments(tfSearchTerm.getText(), (String) cmbSearchBy.getSelectedItem());
        } else {
            JOptionPane.showMessageDialog(this, "Could not find the selected treatment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

