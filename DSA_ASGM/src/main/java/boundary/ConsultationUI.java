/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import control.ConsultationControl; 
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import entity.Consultation;
import utility.ReadWriteFile;

/**
 *
 * @author tiong
 */

public class ConsultationUI extends JFrame {

    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private ListInterface<Appointment> appointmentList;
    private ListInterface<Patient> patientList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Consultation> consultationList;
    private JFrame parentFrame;
    private ReadWriteFile fileManager;
    private ConsultationControl consultationControl;
    

    public ConsultationUI(JFrame parent, ReadWriteFile fileManager, ListInterface<Appointment> appointments, ListInterface<Patient> patients, ListInterface<Doctor> doctors, ListInterface<Consultation> consultations) {
        this.fileManager = fileManager;
        this.parentFrame = parent;
        this.appointmentList = appointments;
        this.patientList = patients;
        this.doctorList = doctors;
        this.consultationList = consultations;
        this.consultationControl = new ConsultationControl(appointments, consultations);

        // --- Frame Setup ---
        setTitle("Consultation Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700); // Increased size
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

        // --- Table Panel ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                " Today's Appointments ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16)
        ));
        tablePanel.setBackground(Color.WHITE);

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
        appointmentsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        appointmentsTable.getTableHeader().setBackground(new Color(230, 230, 230));

        loadAppointmentData();

        JScrollPane tableScrollPane = new JScrollPane(appointmentsTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // --- Actions Panel (SOUTH of Content Panel) ---
        JPanel actionsPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionsPanel.setBackground(Color.WHITE);

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

        contentPanel.add(actionsPanel, BorderLayout.SOUTH);

        // --- Add main content panel to frame ---
        add(contentPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
        btnStartConsultation.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
                Appointment selectedApp = findAppointmentById(appointmentId);

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
            new AppointmentFormUI(this, fileManager, patientList, doctorList, appointmentList).setVisible(true);
        });

        btnScheduleFollowUp.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
                Appointment selectedApp = findAppointmentById(appointmentId);
                if (selectedApp != null) {
                    // Open form in "Follow-up" mode with patient and doctor pre-filled
                    new AppointmentFormUI(this, fileManager, selectedApp.getPatient(), selectedApp.getDoctor(), patientList, doctorList, appointmentList).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to schedule a follow-up.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnManageAppointments.addActionListener(e -> {
            new ManageAppointmentUI(appointmentList, patientList, doctorList, fileManager).setVisible(true);
        });

        btnManageConsultations.addActionListener(e -> {
            new ManageConsultationUI(consultationList, appointmentList, fileManager).setVisible(true);
        });
        
        btnCancelAppointment.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
                Appointment selectedApp = findAppointmentById(appointmentId);

                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Call the control class to do the work
                    String result = consultationControl.cancelAppointment(selectedApp);
                    
                    if (result.startsWith("Success")) {
                        fileManager.writeAppointmentsToFile("src/main/java/Data File/appointments", appointmentList);
                        refreshTable();
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
    }
    
    public void refreshTable() {
        loadAppointmentData();
    }

    private void loadAppointmentData() {
        tableModel.setRowCount(0);
        LocalDate today = LocalDate.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");

        Iterator<Appointment> iterator = appointmentList.getIterator();
        while (iterator.hasNext()) {
            Appointment app = iterator.next();
            System.out.println(app.getAppointmentDate());
            if (app.getAppointmentDate().equals(today)) {
                tableModel.addRow(new Object[]{
                    app.getAppointmentID(),
                    app.getPatient().getName(),
                    app.getDoctor().getName(),
                    app.getAppointmentTime().format(timeFormatter),
                    app.getStatus()
                });
            }
        }
    }
    
     private Appointment findAppointmentById(String id) {
        Iterator<Appointment> it = appointmentList.getIterator();
        while (it.hasNext()) {
            Appointment app = it.next();
            if (app.getAppointmentID().equals(id)) return app;
        }
        return null;
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
