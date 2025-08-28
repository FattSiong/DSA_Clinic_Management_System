/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface; // Import your ADT package
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import entity.Appointment;
import boundary.MainUI;

/**
 *
 * @author tiong
 */

public class ConsultationUI extends JFrame {

    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private ListInterface<Appointment> appointmentList;
    private JFrame parentFrame;

    public ConsultationUI(JFrame parent, ListInterface<Appointment> appointments) {
        this.parentFrame = parent;
        this.appointmentList = appointments;

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
        JPanel actionsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionsPanel.setBackground(Color.WHITE);

        JButton btnStartConsultation = new JButton("Start Consultation");
        JButton btnBookNew = new JButton("Book New Appointment");
        JButton btnScheduleFollowUp = new JButton("Schedule Follow-up");
        JButton btnViewHistory = new JButton("View Patient History");
        JButton btnGenerateReport = new JButton("Generate Reports");
        JButton btnBack = new JButton("Back to Main Menu");

        // Style buttons
        styleButton(btnStartConsultation);
        styleButton(btnBookNew);
        styleButton(btnScheduleFollowUp);
        styleButton(btnViewHistory);
        styleButton(btnGenerateReport);
        styleButton(btnBack, new Color(217, 83, 79)); // Red color for back button

        actionsPanel.add(btnStartConsultation);
        actionsPanel.add(btnBookNew);
        actionsPanel.add(btnScheduleFollowUp);
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
                JOptionPane.showMessageDialog(this, "Starting consultation for Appointment ID: " + appointmentId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to start.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnBookNew.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Opening new appointment booking window...");
        });

        btnScheduleFollowUp.addActionListener(e -> {
            // ... (action listener logic)
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

    private void loadAppointmentData() {
        tableModel.setRowCount(0);
        LocalDate today = LocalDate.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");

        Iterator<Appointment> iterator = appointmentList.getIterator();
        while (iterator.hasNext()) {
            Appointment app = iterator.next();
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
