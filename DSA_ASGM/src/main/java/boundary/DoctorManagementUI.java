package boundary;

import adt.ListInterface;
import control.DoctorControl;
import entity.Doctor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DoctorManagementUI extends JFrame {
    private DoctorControl controller;
    private DefaultTableModel doctorTableModel;
    private JTable doctorTable;
    private JButton btnAdd;

    public DoctorManagementUI(DoctorControl controller) {
        this.controller = controller;

        setTitle("Consultation & Appointment Dashboard");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyle.MAIN_BACKGROUND);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyle.HEADER_BACKGROUND);
        JLabel lblTitle = UIStyle.createHeaderLabel("Doctor Management");
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = UIStyle.createTitledPanel("Doctor Records");
        doctorTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Room"}, 0);
        doctorTable = new JTable(doctorTableModel);
        UIStyle.styleTable(doctorTable);
        
        tablePanel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(UIStyle.MAIN_BACKGROUND);
        centerContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerContainer.add(tablePanel, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(UIStyle.MAIN_BACKGROUND);

        btnAdd = UIStyle.createStyledButton("Add Doctor", UIStyle.PRIMARY_ACTION_COLOR);
        JButton btnUpdate = UIStyle.createStyledButton("Update Selected", UIStyle.SECONDARY_ACTION_COLOR);
        JButton btnDelete = UIStyle.createStyledButton("Delete Selected", UIStyle.DESTRUCTIVE_ACTION_COLOR);
        JButton btnGenerateReport = UIStyle.createStyledButton("Generate Report", UIStyle.SECONDARY_ACTION_COLOR);
        JButton btnBack = UIStyle.createStyledButton("Back", new Color(120, 120, 120));
        JButton btnBackMain = UIStyle.createStyledButton("Back to Main", new Color(120, 120, 120));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnGenerateReport);
        buttonPanel.add(btnBack);
        buttonPanel.add(btnBackMain);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> controller.addDoctor());
        btnUpdate.addActionListener(e -> controller.updateDoctor());
        btnDelete.addActionListener(e -> controller.deleteDoctor());
        btnGenerateReport.addActionListener(e -> controller.generatePatientLoadReport());
        btnBack.addActionListener(e -> controller.close());
        btnBackMain.addActionListener(e -> controller.closeToMain());
    }
    
    public void displayDoctors(ListInterface<Doctor> doctors) {
        doctorTableModel.setRowCount(0);
        for (int i = 1; i <= doctors.getLength(); i++) {
            Doctor d = doctors.getEntry(i);
            doctorTableModel.addRow(new Object[]{d.getDoctorID(), d.getName(), d.getContactNumber(), d.getRoomNumber()});
        }
    }

    public void setAddButtonEnabled(boolean enabled) {
        btnAdd.setEnabled(enabled);
    }
    
    public String getSelectedDoctorId() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please select a doctor from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) doctorTableModel.getValueAt(selectedRow, 0);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void showReport(String reportContent, String title) {
        JTextArea textArea = new JTextArea(reportContent);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 250));
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public Doctor showDoctorInputDialog(Doctor doctorToUpdate) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField roomField = new JTextField();

        if (doctorToUpdate != null) {
            idField.setText(doctorToUpdate.getDoctorID());
            idField.setEditable(false);
            nameField.setText(doctorToUpdate.getName());
            contactField.setText(doctorToUpdate.getContactNumber());
            roomField.setText(doctorToUpdate.getRoomNumber());
        }

        Object[] fields = { "ID:", idField, "Name:", nameField, "Contact:", contactField, "Room:", roomField };
        if(doctorToUpdate != null) {
            fields = new Object[] { "Name:", nameField, "Contact:", contactField, "Room:", roomField };
        }

        int option = JOptionPane.showConfirmDialog(this, fields, 
            doctorToUpdate == null ? "Add New Doctor" : "Update Doctor", 
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String room = roomField.getText().trim();

            if (name.isEmpty() || contact.isEmpty() || room.isEmpty() || (doctorToUpdate == null && id.isEmpty())) {
                showMessage("All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return new Doctor(doctorToUpdate == null ? id : doctorToUpdate.getDoctorID(), name, contact, room);
        }
        return null;
    }
}