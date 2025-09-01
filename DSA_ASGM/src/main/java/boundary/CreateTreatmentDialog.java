// Name: <Your Name>
package boundary;

import adt.DoublyLinkedList;
import adt.ListInterface;
import control.TreatmentController;
import entity.Consultation;
import entity.Medicine;
import entity.Treatment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Iterator;

public class CreateTreatmentDialog extends JDialog {

    private TreatmentController treatmentController;
    private Consultation consultation;

    // UI Components
    private JTable availableMedsTable;
    private JList<String> prescribedMedsList;
    private DefaultTableModel availableMedsModel;
    private DefaultListModel<String> prescribedMdsListModel;
    private JTextArea taTreatmentNotes;
    private JButton btnAddMed, btnRemoveMed, btnSave, btnCancel;
    private JComboBox<String> cmbSearchBy;
    private JTextField tfMedSearch;
    private JButton btnMedSearch, btnMedClear;

    // Data holders
    private ListInterface<Medicine> prescribedMedicines = new DoublyLinkedList<>();

    public CreateTreatmentDialog(Frame owner, TreatmentController controller, Consultation consultation) {
        super(owner, "Create Treatment Record", true); // true for modal
        this.treatmentController = controller;
        this.consultation = consultation;

        setSize(850, 700); // Adjusted size for the new layout
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        addListeners();
        populateAvailableMedicines("", "Name"); // Initial population with no filter
    }

    private void initComponents() {
        // --- Main Panel with Padding ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Info Panel ---
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Changed to 4 rows
        infoPanel.setBorder(BorderFactory.createTitledBorder("Consultation Details"));
        infoPanel.add(new JLabel("Patient Name:"));
        infoPanel.add(new JLabel(consultation.getPatient().getName()));
        infoPanel.add(new JLabel("Doctor Name:"));
        infoPanel.add(new JLabel(consultation.getDoctor().getName()));
        infoPanel.add(new JLabel("Diagnosis:"));
        infoPanel.add(new JLabel(consultation.getDiagnosis())); // Added Diagnosis
        infoPanel.add(new JLabel("Consultation Notes:"));
        infoPanel.add(new JLabel(consultation.getNotes())); // Added Consultation Notes
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // --- Main Content Area using a Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7); // Give more initial space to the left side

        // --- LEFT PANEL (Search, Table, Add/Remove Buttons) ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Medicines"));

        // Search bar panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchCriteriaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchCriteriaPanel.add(new JLabel("Search By:"));
        String[] searchOptions = {"Name", "Description"};
        cmbSearchBy = new JComboBox<>(searchOptions);
        searchCriteriaPanel.add(cmbSearchBy);
        searchPanel.add(searchCriteriaPanel, BorderLayout.WEST);

        tfMedSearch = new JTextField();
        searchPanel.add(tfMedSearch, BorderLayout.CENTER);

        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        btnMedSearch = new JButton("Search");
        btnMedClear = new JButton("Clear");
        searchButtonPanel.add(btnMedSearch);
        searchButtonPanel.add(btnMedClear);
        searchPanel.add(searchButtonPanel, BorderLayout.EAST);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Available Medicines Table (Center)
        String[] availableCols = {"ID", "Name", "Description", "Stock"};
        availableMedsModel = new DefaultTableModel(availableCols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        availableMedsTable = new JTable(availableMedsModel);
        availableMedsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(availableMedsTable), BorderLayout.CENTER);

        // Add/Remove Buttons Panel (South)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAddMed = new JButton("Add >>");
        buttonPanel.add(btnAddMed);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);

        // --- RIGHT PANEL (Prescribed List and Remove Button) ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Prescribed"));

        prescribedMdsListModel = new DefaultListModel<>();
        prescribedMedsList = new JList<>(prescribedMdsListModel);
        prescribedMedsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightPanel.add(new JScrollPane(prescribedMedsList), BorderLayout.CENTER);

        JPanel removeButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRemoveMed = new JButton("<< Remove");
        removeButtonPanel.add(btnRemoveMed);
        rightPanel.add(removeButtonPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // --- Bottom Panel for Notes and Actions ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Treatment Notes
        taTreatmentNotes = new JTextArea(5, 20);
        taTreatmentNotes.setLineWrap(true);
        taTreatmentNotes.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(taTreatmentNotes);
        notesScrollPane.setBorder(BorderFactory.createTitledBorder("Treatment Notes / Details"));
        bottomPanel.add(notesScrollPane, BorderLayout.CENTER);

        // Save/Cancel Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save Treatment");
        btnCancel = new JButton("Cancel");
        actionPanel.add(btnSave);
        actionPanel.add(btnCancel);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    private void populateAvailableMedicines(String searchTerm, String searchBy) {
        availableMedsModel.setRowCount(0); // Clear the table first

        ListInterface<Medicine> medsToShow = treatmentController.searchMedicines(searchTerm, searchBy);
        Iterator<Medicine> it = medsToShow.getIterator();
        while (it.hasNext()) {
            Medicine med = it.next();
            Object[] row = {
                med.getMedicineID(),
                med.getName(),
                med.getDescription(),
                med.getStockQuantity()
            };
            availableMedsModel.addRow(row);
        }
    }

    private void addListeners() {
        btnAddMed.addActionListener(e -> addMedicineToPrescription());
        btnRemoveMed.addActionListener(e -> removeMedicineFromPrescription());
        btnSave.addActionListener(e -> saveTreatment());
        btnCancel.addActionListener(e -> dispose());

        btnMedSearch.addActionListener(e -> {
            String searchTerm = tfMedSearch.getText();
            String searchBy = (String) cmbSearchBy.getSelectedItem();
            populateAvailableMedicines(searchTerm, searchBy);
        });

        btnMedClear.addActionListener(e -> {
            tfMedSearch.setText("");
            cmbSearchBy.setSelectedIndex(0);
            populateAvailableMedicines("", "Name");
        });
    }

    private void addMedicineToPrescription() {
        int selectedRow = availableMedsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the available list.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medId = (String) availableMedsModel.getValueAt(selectedRow, 0);
        String medName = (String) availableMedsModel.getValueAt(selectedRow, 1);

        // Check for duplicates
        if (prescribedMdsListModel.contains(medName)) {
            JOptionPane.showMessageDialog(this, "'" + medName + "' has already been prescribed.", "Duplicate Medicine", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add to UI list and internal ADT list
        prescribedMdsListModel.addElement(medName);
        prescribedMedicines.add(treatmentController.findMedicineById(medId));
    }
    
    private void removeMedicineFromPrescription() {
        int selectedIndex = prescribedMedsList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the 'Prescribed' list to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Remove from UI list and internal ADT list
        prescribedMdsListModel.remove(selectedIndex);
        prescribedMedicines.remove(selectedIndex + 1); // ADT is 1-based index
    }

    private void saveTreatment() {
        String notes = taTreatmentNotes.getText();
        if (notes.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Treatment notes cannot be empty.", "Input Required", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (prescribedMedicines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must prescribe at least one medicine.", "Input Required", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Use the controller to create the treatment
        Treatment newTreatment = treatmentController.addTreatment(consultation, notes, prescribedMedicines);

        if (newTreatment != null) {
            JOptionPane.showMessageDialog(this, "Treatment record saved successfully!\nID: " + newTreatment.getTreatmentID(), "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save treatment record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

