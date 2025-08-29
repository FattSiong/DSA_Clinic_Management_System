/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import boundary.ConsultationUI;
import control.ConsultationControl;
import utility.ReadWriteFile;
import entity.Appointment;
import entity.Consultation;
import entity.Queue;
import utility.ConsultationComparators; // Import the new comparators
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author tiong
 */
public class ManageConsultationUI extends JFrame {

    private JFrame parentFrame;
    private JTable consultationTable;
    private DefaultTableModel tableModel;
    private ListInterface<Consultation> fullConsultationList;
    private ListInterface<Consultation> filteredConsultationList;
    private ReadWriteFile fileManager;
    private ConsultationControl consultationControl;
    private ConsultationUI parentUI;
    private ListInterface<Appointment> appointmentList;

    private JTextField searchField = new JTextField(20);
    private JComboBox<String> searchByComboBox = new JComboBox<>(new String[]{"ID", "Patient Name", "Doctor Name", "Diagnosis"});
    private JLabel pageLabel = new JLabel();
    private int currentPage = 1;
    private final int ROWS_PER_PAGE = 20;

    private int lastSortedColumn = -1;
    private boolean isAscending = true;

    public ManageConsultationUI(JFrame parentFrame, ConsultationUI parentUI, ListInterface<Consultation> conList, ListInterface<Appointment> appList, ListInterface<Queue> queList, ReadWriteFile fileManager) {
        this.parentFrame = parentFrame;
        this.parentUI = parentUI;
        this.fullConsultationList = conList;
        this.filteredConsultationList = conList;
        this.appointmentList = appList;
        this.fileManager = fileManager;
        this.consultationControl = new ConsultationControl(appList, conList, queList, fileManager);

        setTitle("Manage All Consultations");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Consultation Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Panel ---
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(new JLabel("Search For:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("By:"));
        searchPanel.add(searchByComboBox);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] columnNames = {"ID", "Patient", "Doctor", "Date", "Diagnosis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        consultationTable = new JTable(tableModel);
        consultationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        consultationTable.setFont(new Font("Arial", Font.PLAIN, 14));
        consultationTable.setRowHeight(30);

        JTableHeader header = consultationTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        header.setDefaultRenderer(new SortableHeaderRenderer(header.getDefaultRenderer()));

        contentPanel.add(new JScrollPane(consultationTable), BorderLayout.CENTER);

        // --- Pagination Panel ---
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("<< Previous");
        JButton nextButton = new JButton("Next >>");
        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);
        contentPanel.add(paginationPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // --- Actions Panel ---
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnBack = new JButton("Back");

        styleButton(btnEdit, new Color(92, 184, 92));
        styleButton(btnDelete, new Color(240, 173, 78));
        styleButton(btnBack, new Color(217, 83, 79));

        actionsPanel.add(btnEdit);
        actionsPanel.add(btnDelete);
        actionsPanel.add(btnBack);
        add(actionsPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        searchButton.addActionListener(e -> performSearch());
        prevButton.addActionListener(e -> goToPreviousPage());
        nextButton.addActionListener(e -> goToNextPage());
        btnEdit.addActionListener(e -> editSelectedConsultation());
        btnDelete.addActionListener(e -> deleteSelectedConsultation());
        btnBack.addActionListener(e -> {
            parentFrame.setVisible(true);
            parentUI.performSearch();
            this.dispose();
        });
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = consultationTable.columnAtPoint(e.getPoint());
                sortConsultationsByColumn(col);
            }
        });

        updateTable(); // Initial load
    }

    public void performSearch() {
        String searchText = searchField.getText();
        String searchBy = (String) searchByComboBox.getSelectedItem();
        filteredConsultationList = consultationControl.filterConsultations(searchText, searchBy);
        currentPage = 1;
        lastSortedColumn = -1;
        isAscending = true;
        
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        int totalRows = filteredConsultationList.getLength();
        int totalPages = (int) Math.ceil((double) totalRows / ROWS_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1;
        }
        pageLabel.setText("Page " + currentPage + " of " + totalPages);

        int startRow = (currentPage - 1) * ROWS_PER_PAGE;
        int endRow = Math.min(startRow + ROWS_PER_PAGE, totalRows);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (int i = startRow + 1; i <= endRow; i++) {
            Consultation con = filteredConsultationList.getEntry(i);
            tableModel.addRow(new Object[]{
                con.getConsultationID(),
                con.getPatient().getName(),
                con.getDoctor().getName(),
                con.getConsultationDate().format(dateFormatter),
                con.getDiagnosis()
            });
        }
    }

    private void sortConsultationsByColumn(int columnIndex) {
        Comparator<Consultation> comparator = null;
        switch (columnIndex) {
            case 0:
                comparator = new ConsultationComparators.SortById();
                break;
            case 1:
                comparator = new ConsultationComparators.SortByPatientName();
                break;
            case 2:
                comparator = new ConsultationComparators.SortByDoctorName();
                break;
            case 3:
                comparator = new ConsultationComparators.SortByDate();
                break;
            case 4:
                comparator = new ConsultationComparators.SortByDiagnosis();
                break;
            default:
                return;
        }

        if (columnIndex == lastSortedColumn) {
            isAscending = !isAscending;
        } else {
            isAscending = true;
            lastSortedColumn = columnIndex;
        }

        if (isAscending) {
            filteredConsultationList.sort(comparator);
        } else {
            filteredConsultationList.sortDescending(comparator);
        }

        updateTable();
        consultationTable.getTableHeader().repaint();
    }

    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTable();
        }
    }

    private void goToNextPage() {
        int totalRows = filteredConsultationList.getLength();
        int totalPages = (int) Math.ceil((double) totalRows / ROWS_PER_PAGE);
        if (currentPage < totalPages) {
            currentPage++;
            updateTable();
        }
    }

    private void deleteSelectedConsultation() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a consultation to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int actualIndex = ((currentPage - 1) * ROWS_PER_PAGE) + selectedRow + 1;
        Consultation selectedConsultation = filteredConsultationList.getEntry(actualIndex);
        String consultationId = selectedConsultation.getConsultationID();

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete consultation " + consultationId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = consultationControl.deleteConsultation(consultationId);
            if (result.startsWith("Success")) {
                fileManager.writeConsultationsToFile("consultations.txt", fullConsultationList);
                performSearch(); // Refresh the list and table
                JOptionPane.showMessageDialog(this, result);
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedConsultation() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a consultation to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int actualIndex = ((currentPage - 1) * ROWS_PER_PAGE) + selectedRow + 1;
        Consultation selectedConsultation = filteredConsultationList.getEntry(actualIndex);

        if (selectedConsultation != null) {
            // Open the new edit form
            new EditConsultationUI(this, selectedConsultation, consultationControl, fileManager).setVisible(true);
        }
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private class SortableHeaderRenderer implements TableCellRenderer {

        private final TableCellRenderer delegate;

        public SortableHeaderRenderer(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                label.setHorizontalAlignment(JLabel.CENTER);
                int modelColumn = table.convertColumnIndexToModel(column);
                if (modelColumn == lastSortedColumn) {
                    label.setText(value.toString() + (isAscending ? " ▲" : " ▼"));
                }
            }
            return c;
        }
    }
}
