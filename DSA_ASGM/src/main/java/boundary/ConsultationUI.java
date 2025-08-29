/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.DoublyLinkedList;
import adt.ListInterface;
import control.AppointmentControl;
import control.ConsultationControl;
import entity.*;
import utility.AppointmentComparators;
import utility.ReadWriteFile;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author tiong
 */
public class ConsultationUI extends JFrame {

    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private ListInterface<Appointment> fullTodayAppointmentList;
    private ListInterface<Appointment> filteredAppointmentList;
    private ListInterface<Patient> patientList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Consultation> consultationList;
    private JFrame parentFrame;
    private ReadWriteFile fileManager;
    private ConsultationControl consultationControl;
    private AppointmentControl appointmentControl;

    // UI Components for Search and Pagination
    private JTextField searchField = new JTextField(20);
    private JComboBox<String> searchByComboBox = new JComboBox<>(new String[]{"ID", "Patient Name", "Doctor Name"});
    private JComboBox<String> statusFilterComboBox = new JComboBox<>(new String[]{"All", "Scheduled", "Completed", "Canceled"});
    private JLabel pageLabel = new JLabel();
    private int currentPage = 1;
    private final int ROWS_PER_PAGE = 5;

    private int lastSortedColumn = -1;
    private boolean isAscending = true;

    public ConsultationUI(JFrame parent, ReadWriteFile fileManager, ListInterface<Appointment> appointments, ListInterface<Patient> patients, ListInterface<Doctor> doctors, ListInterface<Consultation> consultations) {
        this.fileManager = fileManager;
        this.parentFrame = parent;
        this.fullTodayAppointmentList = appointments;
        this.filteredAppointmentList = appointments;
        this.patientList = patients;
        this.doctorList = doctors;
        this.consultationList = consultations;
        this.consultationControl = new ConsultationControl(appointments, consultations, fileManager);
        this.appointmentControl = new AppointmentControl(appointments, patients, doctors, fileManager);

        // --- Frame Setup ---
        setTitle("Consultation Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- Header Panel (NORTH) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Consultation & Appointment Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Panel (CENTER) ---
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);

        // --- Search Panel (NORTH of Content Panel) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Filter Today's Appointments"));
        searchPanel.add(new JLabel("Search For:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("By:"));
        searchPanel.add(searchByComboBox);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilterComboBox);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        
        JPanel tableAndPaginationPanel = new JPanel(new BorderLayout());

//        // --- Table Panel ---
//        JPanel tablePanel = new JPanel(new BorderLayout());
//        tablePanel.setBorder(BorderFactory.createTitledBorder(
//                BorderFactory.createLineBorder(Color.GRAY),
//                " Today's Appointments ",
//                TitledBorder.LEFT,
//                TitledBorder.TOP,
//                new Font("Arial", Font.BOLD, 16)
//        ));
//        tablePanel.setBackground(Color.WHITE);

        String[] columnNames = {"ID", "Patient Name", "Doctor Name", "Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentsTable = new JTable(tableModel);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        appointmentsTable.setRowHeight(30);

        JTableHeader header = appointmentsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        header.setDefaultRenderer(new SortableHeaderRenderer(header.getDefaultRenderer()));
        tableAndPaginationPanel.add(new JScrollPane(appointmentsTable), BorderLayout.CENTER);

        // --- Pagination Panel ---
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("<< Previous");
        JButton nextButton = new JButton("Next >>");
        paginationPanel.add(prevButton);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextButton);
        tableAndPaginationPanel.add(paginationPanel, BorderLayout.SOUTH);
        
        contentPanel.add(tableAndPaginationPanel, BorderLayout.CENTER);
        
        // --- Actions Panel (SOUTH of Content Panel) ---
        JPanel actionsPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionsPanel.setBackground(Color.WHITE);
        
        contentPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);

        JButton btnStartConsultation = new JButton("Start Consultation");
        JButton btnBookNew = new JButton("Book New Appointment");
        JButton btnScheduleFollowUp = new JButton("Schedule Follow-up");
        JButton btnManageAppointments = new JButton("Manage Appointments");
        JButton btnManageConsultations = new JButton("Manage Consultations");
        JButton btnCancelAppointment = new JButton("Cancel Appointment");
        JButton btnViewHistory = new JButton("View Patient History");
        JButton btnGenerateReport = new JButton("Generate Reports");
        JButton btnBack = new JButton("Back to Main Menu");

        // Style buttons
        styleButton(btnStartConsultation);
        styleButton(btnBookNew);
        styleButton(btnScheduleFollowUp);
        styleButton(btnManageAppointments, new Color(66, 139, 202)); // Blue
        styleButton(btnManageConsultations, new Color(66, 139, 202));
        styleButton(btnCancelAppointment, new Color(240, 173, 78));
        styleButton(btnViewHistory);
        styleButton(btnGenerateReport);
        styleButton(btnBack, new Color(217, 83, 79)); // Red color for back button

        actionsPanel.add(btnStartConsultation);
        actionsPanel.add(btnBookNew);
        actionsPanel.add(btnScheduleFollowUp);
        actionsPanel.add(btnManageAppointments);
        actionsPanel.add(btnManageConsultations);
        actionsPanel.add(btnCancelAppointment);
        actionsPanel.add(btnViewHistory);
        actionsPanel.add(btnGenerateReport);
        actionsPanel.add(btnBack);
        
        add(contentPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
        searchButton.addActionListener(e -> performSearch());
        prevButton.addActionListener(e -> goToPreviousPage());
        nextButton.addActionListener(e -> goToNextPage());
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = appointmentsTable.columnAtPoint(e.getPoint());
                sortAppointmentsByColumn(col);
            }
        });
        
        btnStartConsultation.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
                Appointment selectedApp = appointmentControl.findAppointmentById(appointmentId);

                if (selectedApp != null) {
                    if (!selectedApp.getStatus().equals("Scheduled")) {
                        JOptionPane.showMessageDialog(this, "Only 'Scheduled' appointments can be started.", "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    // Open the details form, passing the control class
                    new ConsultationDetailsUI(this, selectedApp, fileManager, consultationControl).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to start.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnBookNew.addActionListener(e -> {
            new AppointmentFormUI(this, fileManager, patientList, doctorList, fullTodayAppointmentList).setVisible(true);
        });

        btnScheduleFollowUp.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
                Appointment selectedApp = appointmentControl.findAppointmentById(appointmentId);
                if (selectedApp != null) {
                    // Open form in "Follow-up" mode with patient and doctor pre-filled
                    new AppointmentFormUI(this, fileManager, selectedApp.getPatient(), selectedApp.getDoctor(), patientList, doctorList, fullTodayAppointmentList).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to schedule a follow-up.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnManageAppointments.addActionListener(e -> {
            new ManageAppointmentUI(this, this, fullTodayAppointmentList, patientList, doctorList, fileManager).setVisible(true);
            this.setVisible(false);
        });

        btnManageConsultations.addActionListener(e -> {
            new ManageConsultationUI(this, this, consultationList, fullTodayAppointmentList, fileManager).setVisible(true);
            this.setVisible(false);
        });

        btnCancelAppointment.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
                Appointment selectedApp = appointmentControl.findAppointmentById(appointmentId);

                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Call the control class to do the work
                    String result = appointmentControl.cancelAppointment(selectedApp);

                    if (result.startsWith("Success")) {
                        updateTable();
                        JOptionPane.showMessageDialog(this, result);
                    } else {
                        JOptionPane.showMessageDialog(this, result, "Action Not Allowed", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnViewHistory.addActionListener(e -> {
            // ... (action listener logic)
        });

        btnGenerateReport.addActionListener(e -> {
            // ... (action listener logic)
        });

        btnBack.addActionListener(e -> {
            parentFrame.setVisible(true);
            this.dispose();
        });

        updateTable();
    }

    public void performSearch() {
        String searchText = searchField.getText();
        String searchBy = (String) searchByComboBox.getSelectedItem();
        String statusFilter = (String) statusFilterComboBox.getSelectedItem();

        // The UI now calls the control class to do all the filtering work.
        filteredAppointmentList = appointmentControl.filterTodaysAppointments(searchText, searchBy, statusFilter);
        lastSortedColumn = -1;
        isAscending = true;
        currentPage = 1; // Reset to the first page after every search
        
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

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        for (int i = startRow + 1; i <= endRow; i++) {
            Appointment app = filteredAppointmentList.getEntry(i);
            tableModel.addRow(new Object[]{
                app.getAppointmentID(),
                app.getPatient().getName(),
                app.getDoctor().getName(),
                app.getAppointmentTime().format(timeFormatter),
                app.getStatus()
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

    private void sortAppointmentsByColumn(int columnIndex) {
        Comparator<Appointment> comparator = null;
        switch (columnIndex) {
            case 0:
                comparator = new AppointmentComparators.SortById();
                break;
            case 1:
                comparator = new AppointmentComparators.SortByPatientName();
                break;
            case 2:
                comparator = new AppointmentComparators.SortByDoctorName();
                break;
            case 4:
                comparator = new AppointmentComparators.SortByStatus();
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
            filteredAppointmentList.sort(comparator);
        } else {
            filteredAppointmentList.sortDescending(comparator);
        }

        updateTable();
        appointmentsTable.getTableHeader().repaint();
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