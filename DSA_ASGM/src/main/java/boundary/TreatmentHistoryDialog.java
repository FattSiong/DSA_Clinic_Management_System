// Name: <Your Name>
package boundary;

import adt.ListInterface;
import control.TreatmentController;
import entity.Treatment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JButton btnSearch, btnClear, btnEdit, btnDelete, btnClose;

    public TreatmentHistoryDialog(Frame owner, TreatmentController controller) {
        super(owner, "Treatment History", true);
        this.owner = owner;
        this.treatmentController = controller;

        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        addListeners();
        populateTreatments("", "Patient Name"); // Initial population
    }

    private void initComponents() {
        // --- Search Panel (NORTH) ---
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchControls.add(new JLabel("Search By:"));
        String[] searchOptions = {"Patient Name", "Doctor Name", "Treatment ID"};
        cmbSearchBy = new JComboBox<>(searchOptions);
        searchControls.add(cmbSearchBy);
        searchPanel.add(searchControls, BorderLayout.WEST);

        tfSearchTerm = new JTextField(20);
        searchPanel.add(tfSearchTerm, BorderLayout.CENTER);

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSearch = new JButton("Search");
        btnClear = new JButton("Clear");
        searchButtons.add(btnSearch);
        searchButtons.add(btnClear);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        add(searchPanel, BorderLayout.NORTH);

        // --- Table Panel (CENTER) ---
        String[] columnNames = {"ID", "Patient", "Doctor", "Date", "Details", "Medicines"};
        treatmentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        treatmentTable = new JTable(treatmentTableModel);
        treatmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(treatmentTable), BorderLayout.CENTER);

        // --- Action Button Panel (SOUTH) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        btnEdit = new JButton("Edit Selected");
        btnDelete = new JButton("Delete Selected");
        btnClose = new JButton("Close");
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnClose);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        btnSearch.addActionListener(e -> searchTreatments());
        btnClear.addActionListener(e -> {
            tfSearchTerm.setText("");
            cmbSearchBy.setSelectedIndex(0);
            populateTreatments("", "Patient Name");
        });
        btnClose.addActionListener(e -> dispose());

        btnDelete.addActionListener(e -> deleteSelectedTreatment());
        btnEdit.addActionListener(e -> editSelectedTreatment());
    }

    private void populateTreatments(String searchTerm, String searchBy) {
        treatmentTableModel.setRowCount(0); // Clear table
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
            JOptionPane.showMessageDialog(this, "Please select a treatment to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
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
                populateTreatments(tfSearchTerm.getText(), (String) cmbSearchBy.getSelectedItem()); // Refresh list
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete treatment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editSelectedTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a treatment to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String treatmentId = (String) treatmentTableModel.getValueAt(selectedRow, 0);
        Treatment treatmentToEdit = treatmentController.findTreatmentById(treatmentId);

        if (treatmentToEdit != null) {
            EditTreatmentDialog editDialog = new EditTreatmentDialog(owner, treatmentController, treatmentToEdit);
            editDialog.setVisible(true);
            // After the dialog closes, refresh the list to show any changes
            populateTreatments(tfSearchTerm.getText(), (String) cmbSearchBy.getSelectedItem());
        } else {
            JOptionPane.showMessageDialog(this, "Could not find the selected treatment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
