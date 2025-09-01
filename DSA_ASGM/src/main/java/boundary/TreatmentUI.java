// Name: <Your Name>
package boundary;

import adt.ListInterface;
import control.TreatmentController;
import entity.Consultation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Objects;

public class TreatmentUI extends JFrame {

    private final TreatmentController treatmentController;
    private final Frame owner;

    // UI Components
    private JTable consultationTable;
    private DefaultTableModel consultationTableModel;
    private JTextField tfSearch;
    private JComboBox<String> cmbSearchBy; // Added for search criteria
    private JButton btnSearch, btnClear, btnCreate, btnHistory, btnBack;
    private JLabel lblPageInfo;
    private JButton btnPrev, btnNext;

    // Pagination state
    private int currentPage = 1;
    private final int rowsPerPage = 10;
    private ListInterface<Consultation> currentConsultationList;

    public TreatmentUI(Frame owner, TreatmentController controller) {
        super("Treatment Module - Pending Consultations");
        this.owner = owner;
        this.treatmentController = controller;
        
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Don't exit the whole app
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                owner.setVisible(true); // Show the main menu again when this closes
            }
        });

        initComponents();
        addListeners();
        updateConsultationListAndTable();
    }

    private void initComponents() {
        // --- Main Panel with Padding ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // --- Search Panel (NORTH) ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search By:"));
        
        String[] searchOptions = {"Patient IC", "Patient Name", "Doctor Name"};
        cmbSearchBy = new JComboBox<>(searchOptions);
        searchPanel.add(cmbSearchBy);

        tfSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnClear = new JButton("Clear");
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Consultation Table (CENTER) ---
        String[] columnNames = {"Consultation ID", "Patient Name", "Patient IC", "Doctor Name", "Date"};
        consultationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        consultationTable = new JTable(consultationTableModel);
        consultationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(new JScrollPane(consultationTable), BorderLayout.CENTER);

        // --- Navigation Panel (SOUTH) ---
        JPanel navPanel = new JPanel(new BorderLayout());
        
        // FIX: Create a separate panel for the left-side buttons
        JPanel leftActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCreate = new JButton("Create Treatment Record");
        btnHistory = new JButton("View Treatment History");
        btnBack = new JButton("Back to Main Menu");
        leftActionPanel.add(btnCreate);
        leftActionPanel.add(btnHistory);
        leftActionPanel.add(btnBack);
        navPanel.add(leftActionPanel, BorderLayout.WEST);

        JPanel paginationControls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPrev = new JButton("< Prev");
        btnNext = new JButton("Next >");
        lblPageInfo = new JLabel("Page 1 of 1");
        paginationControls.add(btnPrev);
        paginationControls.add(lblPageInfo);
        paginationControls.add(btnNext);
        navPanel.add(paginationControls, BorderLayout.EAST);

        mainPanel.add(navPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    private void addListeners() {
        btnSearch.addActionListener(e -> updateConsultationListAndTable());
        btnClear.addActionListener(e -> {
            tfSearch.setText("");
            cmbSearchBy.setSelectedIndex(0); // Reset dropdown
            updateConsultationListAndTable();
        });
        btnCreate.addActionListener(e -> createTreatmentRecord());
        
        btnHistory.addActionListener(e -> {
            TreatmentHistoryDialog historyDialog = new TreatmentHistoryDialog(this, treatmentController);
            historyDialog.setVisible(true);
        });

        btnBack.addActionListener(e -> {
            owner.setVisible(true);
            dispose();
        });

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTableDisplay();
            }
        });

        btnNext.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) currentConsultationList.getLength() / rowsPerPage);
            if (currentPage < totalPages) {
                currentPage++;
                updateTableDisplay();
            }
        });
    }

    private void updateConsultationListAndTable() {
        currentPage = 1; // Reset to first page on new search
        String searchTerm = tfSearch.getText();
        String searchBy = (String) cmbSearchBy.getSelectedItem();
        currentConsultationList = treatmentController.getPendingConsultations(LocalDate.now(), searchTerm, searchBy);
        updateTableDisplay();
    }
    
    private void updateTableDisplay() {
        consultationTableModel.setRowCount(0); // Clear table
        
        int totalRecords = currentConsultationList.getLength();
        if (totalRecords == 0) {
            lblPageInfo.setText("Page 0 of 0");
            btnPrev.setEnabled(false);
            btnNext.setEnabled(false);
            return;
        }
        
        int totalPages = (int) Math.ceil((double) totalRecords / rowsPerPage);
        lblPageInfo.setText("Page " + currentPage + " of " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
        
        int start = (currentPage - 1) * rowsPerPage;
        
        Iterator<Consultation> it = currentConsultationList.getIterator();
        int currentIndex = 0;
        while(it.hasNext()){
            Consultation c = it.next();
            if(currentIndex >= start && currentIndex < start + rowsPerPage){
                 Object[] row = {
                    c.getConsultationID(),
                    c.getPatient().getName(),
                    c.getPatient().getIcNumber(),
                    c.getDoctor().getName(),
                    c.getConsultationDate()
                };
                consultationTableModel.addRow(row);
            }
            currentIndex++;
        }
    }

    private void createTreatmentRecord() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a consultation from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String consultationId = (String) consultationTableModel.getValueAt(selectedRow, 0);
        Consultation selectedConsultation = treatmentController.findConsultationById(consultationId);
        
        if (selectedConsultation != null) {
            CreateTreatmentDialog createDialog = new CreateTreatmentDialog(this, treatmentController, selectedConsultation);
            createDialog.setVisible(true);
            updateConsultationListAndTable();
        } else {
             JOptionPane.showMessageDialog(this, "Could not find the selected consultation details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

