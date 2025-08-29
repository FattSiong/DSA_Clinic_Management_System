/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import boundary.DoctorUI;
import adt.ListInterface;
import entity.Doctor;
import utility.ReadWriteFile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DoctorControl extends JFrame {
    private DoctorUI parentUI;
    private ListInterface<Doctor> doctorList;
    private DefaultTableModel doctorTableModel;
    private JTable doctorTable;
    private JButton btnAdd;
    private ReadWriteFile rwf = new ReadWriteFile();
    private final String DOCTOR_FILE = "src/main/java/Data File/doctors";

    public DoctorControl(DoctorUI parentUI, ListInterface<Doctor> doctorList) {
        this.parentUI = parentUI;
        this.doctorList = doctorList;

        setTitle("Manage Doctors");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadDoctorsToTable(); // ✅ use only the existing list
        checkAddButtonStatus();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        doctorTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Room"}, 0);
        doctorTable = new JTable(doctorTableModel);
        add(new JScrollPane(doctorTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnBack = new JButton("Back");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addDoctor());
        btnUpdate.addActionListener(e -> updateDoctor());
        btnDelete.addActionListener(e -> deleteDoctor());
        btnBack.addActionListener(e -> {
            this.dispose();
            parentUI.setVisible(true);
        });
    }

    private void loadDoctorsToTable() {
        doctorTableModel.setRowCount(0);
        for (int i = 1; i <= doctorList.getLength(); i++) {
            Doctor d = doctorList.getEntry(i);
            doctorTableModel.addRow(new Object[]{d.getDoctorID(), d.getName(), d.getContactNumber(), d.getRoomNumber()});
        }
    }

    private void addDoctor() {
        if (doctorList.getLength() >= 4) {
            JOptionPane.showMessageDialog(this, "Maximum of 4 doctors allowed!");
            return;
        }

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField roomField = new JTextField();
        Object[] fields = {"ID:", idField, "Name:", nameField, "Contact:", contactField, "Room:", roomField};

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Doctor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String room = roomField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || contact.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            doctorList.add(new Doctor(id, name, contact, room));
            rwf.writeDoctorsToFile(DOCTOR_FILE, doctorList); // ✅ save
            loadDoctorsToTable();
            JOptionPane.showMessageDialog(this, "Doctor added successfully!");
            checkAddButtonStatus();
        }
    }

    private void updateDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor to update!");
            return;
        }

        String id = (String) doctorTableModel.getValueAt(row, 0);
        Doctor d = null;
        for (int i = 1; i <= doctorList.getLength(); i++) {
            if (doctorList.getEntry(i).getDoctorID().equals(id)) {
                d = doctorList.getEntry(i);
                break;
            }
        }

        if (d != null) {
            JTextField nameField = new JTextField(d.getName());
            JTextField contactField = new JTextField(d.getContactNumber());
            JTextField roomField = new JTextField(d.getRoomNumber());
            Object[] fields = {"Name:", nameField, "Contact:", contactField, "Room:", roomField};

            int option = JOptionPane.showConfirmDialog(this, fields, "Update Doctor", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();
                String room = roomField.getText().trim();

                if (name.isEmpty() || contact.isEmpty() || room.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                d.setName(name);
                d.setContactNumber(contact);
                d.setRoomNumber(room);
                rwf.writeDoctorsToFile(DOCTOR_FILE, doctorList); // ✅ save
                loadDoctorsToTable();
                JOptionPane.showMessageDialog(this, "Doctor updated successfully!");
            }
        }
    }

    private void deleteDoctor() {
        int row = doctorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor to delete!");
            return;
        }

        String id = (String) doctorTableModel.getValueAt(row, 0);
        for (int i = 1; i <= doctorList.getLength(); i++) {
            if (doctorList.getEntry(i).getDoctorID().equals(id)) {
                doctorList.remove(i);
                rwf.writeDoctorsToFile(DOCTOR_FILE, doctorList); // ✅ save
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully!");
                break;
            }
        }
        loadDoctorsToTable();
        checkAddButtonStatus();
    }

    private void checkAddButtonStatus() {
        btnAdd.setEnabled(doctorList.getLength() < 4);
    }
}

