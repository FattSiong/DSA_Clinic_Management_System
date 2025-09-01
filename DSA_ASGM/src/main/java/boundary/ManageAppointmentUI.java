/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import utility.ReadWriteFile;
import javax.swing.*;
import java.awt.*;
import adt.ListInterface;
import control.AppointmentControl;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import utility.AppointmentComparators;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.table.TableCellRenderer;
import boundary.ConsultationUI;
import entity.Schedule;

/**
 *
 * @author tiong
 */
public class ManageAppointmentUI extends JFrame {
    
    private JFrame parentFrame;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private ListInterface<Appointment> fullAppointmentList;
    private ListInterface<Appointment> filteredAppointmentList;
    private ListInterface<Schedule> scheduleList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Patient> patientList;
    private ReadWriteFile fileManager;
    private AppointmentControl appointmentControl;
    private ConsultationUI parentUI;

    // UI Components for Search and Pagination
    private JTextField searchField = new JTextField(20);
    private JComboBox<String> searchByComboBox = new JComboBox<>(new String[]{"ID", "Patient Name", "Doctor Name", "Date (YYYY-MM-DD)"});
    private JComboBox<String> statusFilterComboBox = new JComboBox<>(new String[]{"All", "Scheduled", "Completed", "Canceled"});
    private JLabel pageLabel = new JLabel();
    private int currentPage = 1;
    private final int ROWS_PER_PAGE = 20;
    
    private int lastSortedColumn = -1;
    private boolean isAscending = true;

    public ManageAppointmentUI(JFrame parentFrame, ConsultationUI parentUI, ListInterface<Appointment> appList, ListInterface<Patient> pList, ListInterface<Doctor> dList, ListInterface<Schedule> sList, ReadWriteFile fileManager) {
        this.parentFrame = parentFrame;
        this.parentUI = parentUI;
        this.fullAppointmentList = appList;
        this.filteredAppointmentList = appList;
        this.fileManager = fileManager;
        this.patientList = pList;
        this.doctorList = dList;
        this.appointmentControl = new AppointmentControl(appList, pList, dList, sList, fileManager);

        setTitle("Manage All Appointments");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Panel ---
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Search and Filter Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        searchPanel.add(new JLabel("Search For:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("By:"));
        searchPanel.add(searchByComboBox);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilterComboBox);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] columnNames = {"ID", "Patient", "Doctor", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        appointmentTable.setRowHeight(30);
        
        JTableHeader header = appointmentTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        header.setDefaultRenderer(new SortableHeaderRenderer(header.getDefaultRenderer()));
        contentPanel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        
        appointmentTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = appointmentTable.columnAtPoint(e.getPoint());
                sortAppointmentsByColumn(col);
            }
        });

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
        
        styleButton(btnEdit);
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
        btnEdit.addActionListener(e -> editSelectedAppointment());
        btnDelete.addActionListener(e -> deleteSelectedAppointment());
        btnBack.addActionListener(e -> {
            parentFrame.setVisible(true);
            parentUI.performSearch();
            this.dispose();
        });

        updateTable(); // Initial table load
    }

    public void performSearch() {
        String searchText = searchField.getText();
        String searchBy = (String) searchByComboBox.getSelectedItem();
        String filterStatus = (String) statusFilterComboBox.getSelectedItem();

        filteredAppointmentList = appointmentControl.filterAppointments(searchText, searchBy, filterStatus);
        lastSortedColumn = -1;
        isAscending = true;
        currentPage = 1;
        
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        int totalRows = filteredAppointmentList.getLength();
        int totalPages = (int) Math.ceil((double) totalRows / ROWS_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1;
        }
        pageLabel.setText("Page " + currentPage + " of " + totalPages);

        int startRow = (currentPage - 1) * ROWS_PER_PAGE;
        int endRow = Math.min(startRow + ROWS_PER_PAGE, totalRows);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");

        for (int i = startRow + 1; i <= endRow; i++) {
            Appointment app = filteredAppointmentList.getEntry(i);
            tableModel.addRow(new Object[]{
                app.getAppointmentID(), app.getPatient().getName(), app.getDoctor().getName(),
                app.getAppointmentDate().format(dateFormatter), app.getAppointmentTime().format(timeFormatter), app.getStatus()
            });
        }
    }

    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTable();
        }
    }

    private void goToNextPage() {
        int totalRows = filteredAppointmentList.getLength();
        int totalPages = (int) Math.ceil((double) totalRows / ROWS_PER_PAGE);
        if (currentPage < totalPages) {
            currentPage++;
            updateTable();
        }
    }

    private void editSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find the full Appointment object from the list
        Appointment selectedAppointment = null;
        Iterator<Appointment> it = fullAppointmentList.getIterator();
        while(it.hasNext()){
            Appointment app = it.next();
            if(app.getAppointmentID().equals(appointmentId)){
                selectedAppointment = app;
                break;
            }
        }

        if (selectedAppointment != null) {
            if (selectedAppointment.getStatus().equals("Canceled") || selectedAppointment.getStatus().equals("Completed")) {
                JOptionPane.showMessageDialog(this, "Canceled or Completed appointments cannot be edited.", "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
                return; // Stop the process here
            }
            
            new EditAppointmentUI(this, selectedAppointment, doctorList, appointmentControl).setVisible(true);
        }
    }

    private void deleteSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete appointment " + appointmentId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = appointmentControl.deleteAppointment(appointmentId);
            if (result.startsWith("Success")) {
                performSearch(); // Refresh table
                JOptionPane.showMessageDialog(this, result);
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void sortAppointmentsByColumn(int columnIndex) {
        Comparator<Appointment> comparator = null;
        switch (columnIndex) {
            case 0: comparator = new AppointmentComparators.SortById(); break;
            case 1: comparator = new AppointmentComparators.SortByPatientName(); break;
            case 2: comparator = new AppointmentComparators.SortByDoctorName(); break;
            case 3: comparator = new AppointmentComparators.SortByDate(); break;
            case 5: comparator = new AppointmentComparators.SortByStatus(); break;
            default: return; // No sorting for this column
        }
        
        // Check if the same column is clicked again to toggle the order
        if (columnIndex == lastSortedColumn) {
            isAscending = !isAscending;
        } else {
            isAscending = true; // Default to ascending for a new column
            lastSortedColumn = columnIndex;
        }

        // Perform the sort
        if (isAscending) {
            filteredAppointmentList.sort(comparator);
        } else {
            filteredAppointmentList.sortDescending(comparator); // Use the new descending sort method
        }
        
        // Refresh the table to show the sorted data
        updateTable();
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
    
    /**
     * A custom renderer for the table header to display sort arrows.
     */
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
                // Convert view column index to model column index
                int modelColumn = table.convertColumnIndexToModel(column);
                if (modelColumn == lastSortedColumn) {
                    label.setText(value.toString() + (isAscending ? " ▲" : " ▼"));
                }
            }
            return c;
        }
    }
}
