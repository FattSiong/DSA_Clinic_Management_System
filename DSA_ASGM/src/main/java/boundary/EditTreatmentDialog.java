package boundary;

import adt.DoublyLinkedList;
import adt.ListInterface;
import control.TreatmentController;
import entity.Medicine;
import entity.Treatment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Iterator;

public class EditTreatmentDialog extends JDialog {

    private final TreatmentController treatmentController;
    private final Treatment treatmentToEdit;

    
    private JTable availableMedsTable;
    private JList<String> prescribedMedsList;
    private DefaultTableModel availableMedsModel;
    private DefaultListModel<String> prescribedMdsListModel;
    private JTextArea taTreatmentNotes;
    private JComboBox<String> cmbSearchBy;
    private JTextField tfMedSearch;
    
    
    private final ListInterface<Medicine> currentPrescribedMedicines;

    public EditTreatmentDialog(Frame owner, TreatmentController controller, Treatment treatment) {
        super(owner, "Edit Treatment Record", true);
        this.treatmentController = controller;
        this.treatmentToEdit = treatment;
        this.currentPrescribedMedicines = new DoublyLinkedList<>();
        
        
        Iterator<Medicine> initialMeds = treatment.getPrescribedMedicine().getIterator();
        while(initialMeds.hasNext()){
            currentPrescribedMedicines.add(initialMeds.next());
        }

        setSize(850, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        populateInitialData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Consultation Details"));
        infoPanel.add(new JLabel("Patient:"));
        infoPanel.add(new JLabel(treatmentToEdit.getConsultation().getPatient().getName()));
        infoPanel.add(new JLabel("Doctor:"));
        infoPanel.add(new JLabel(treatmentToEdit.getConsultation().getDoctor().getName()));
        infoPanel.add(new JLabel("Diagnosis:"));
        infoPanel.add(new JLabel(treatmentToEdit.getConsultation().getDiagnosis()));
        infoPanel.add(new JLabel("Consultation Notes:"));
        infoPanel.add(new JLabel(treatmentToEdit.getConsultation().getNotes()));
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Medicines"));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchCriteriaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchCriteriaPanel.add(new JLabel("Search By:"));
        cmbSearchBy = new JComboBox<>(new String[]{"Name", "Description"});
        searchCriteriaPanel.add(cmbSearchBy);
        searchPanel.add(searchCriteriaPanel, BorderLayout.WEST);
        tfMedSearch = new JTextField();
        searchPanel.add(tfMedSearch, BorderLayout.CENTER);
        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        JButton btnMedSearch = new JButton("Search");
        JButton btnMedClear = new JButton("Clear");
        searchButtonPanel.add(btnMedSearch);
        searchButtonPanel.add(btnMedClear);
        searchPanel.add(searchButtonPanel, BorderLayout.EAST);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        availableMedsModel = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Stock"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        availableMedsTable = new JTable(availableMedsModel);
        availableMedsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(availableMedsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAddMed = new JButton("Add >>");
        buttonPanel.add(btnAddMed);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Prescribed"));
        prescribedMdsListModel = new DefaultListModel<>();
        prescribedMedsList = new JList<>(prescribedMdsListModel);
        prescribedMedsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightPanel.add(new JScrollPane(prescribedMedsList), BorderLayout.CENTER);
        JPanel removeButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnRemoveMed = new JButton("<< Remove");
        removeButtonPanel.add(btnRemoveMed);
        rightPanel.add(removeButtonPanel, BorderLayout.SOUTH);
        splitPane.setRightComponent(rightPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        taTreatmentNotes = new JTextArea(5, 20);
        taTreatmentNotes.setLineWrap(true);
        taTreatmentNotes.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(taTreatmentNotes);
        notesScrollPane.setBorder(BorderFactory.createTitledBorder("Treatment Notes / Details"));
        bottomPanel.add(notesScrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");
        actionPanel.add(btnSave);
        actionPanel.add(btnCancel);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        
        btnMedSearch.addActionListener(e -> populateAvailableMedicines());
        btnMedClear.addActionListener(e -> {
            tfMedSearch.setText("");
            cmbSearchBy.setSelectedIndex(0);
            populateAvailableMedicines();
        });
        btnAddMed.addActionListener(e -> addMedicineToPrescription());
        btnRemoveMed.addActionListener(e -> removeMedicineFromPrescription());
        btnSave.addActionListener(e -> saveChanges());
        btnCancel.addActionListener(e -> dispose());
    }


    private void populateInitialData() {
        
        populateAvailableMedicines();
        
        taTreatmentNotes.setText(treatmentToEdit.getTreatmentDetails());
        Iterator<Medicine> it = currentPrescribedMedicines.getIterator();
        while (it.hasNext()) {
            prescribedMdsListModel.addElement(it.next().getName());
        }
    }

    private void populateAvailableMedicines() {
        String searchTerm = tfMedSearch.getText();
        String searchBy = (String) cmbSearchBy.getSelectedItem();
        availableMedsModel.setRowCount(0);
        ListInterface<Medicine> medsToShow = treatmentController.searchMedicines(searchTerm, searchBy);
        Iterator<Medicine> it = medsToShow.getIterator();
        while (it.hasNext()) {
            Medicine med = it.next();
            availableMedsModel.addRow(new Object[]{med.getMedicineID(), med.getName(), med.getDescription(), med.getStockQuantity()});
        }
    }

    private void addMedicineToPrescription() {
        int selectedRow = availableMedsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the available list.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String medId = (String) availableMedsModel.getValueAt(selectedRow, 0);
        String medName = (String) availableMedsModel.getValueAt(selectedRow, 1);
        if (prescribedMdsListModel.contains(medName)) {
            JOptionPane.showMessageDialog(this, "'" + medName + "' has already been prescribed.", "Duplicate Medicine", JOptionPane.WARNING_MESSAGE);
            return;
        }
        prescribedMdsListModel.addElement(medName);
        currentPrescribedMedicines.add(treatmentController.findMedicineById(medId));
    }
    
    private void removeMedicineFromPrescription() {
        int selectedIndex = prescribedMedsList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the 'Prescribed' list to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        prescribedMdsListModel.remove(selectedIndex);
        currentPrescribedMedicines.remove(selectedIndex + 1);
    }

    private void saveChanges() {
        String newNotes = taTreatmentNotes.getText();
        if (newNotes.trim().isEmpty() || currentPrescribedMedicines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Treatment notes and prescribed medicines cannot be empty.", "Input Required", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = treatmentController.updateTreatment(treatmentToEdit, newNotes, currentPrescribedMedicines);
        if (success) {
            JOptionPane.showMessageDialog(this, "Treatment record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update treatment record.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
