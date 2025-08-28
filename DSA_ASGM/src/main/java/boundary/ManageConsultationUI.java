/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import control.ConsultationControl;
import entity.Appointment;
import utility.ReadWriteFile;
import entity.Consultation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 *
 * @author tiong
 */
public class ManageConsultationUI extends JFrame {

    private JTable consultationTable;
    private DefaultTableModel tableModel;
    private ListInterface<Consultation> consultationList;
    private ReadWriteFile fileManager;
    private ConsultationControl consultationControl;

    public ManageConsultationUI(ListInterface<Consultation> conList, ListInterface<Appointment> appList, ReadWriteFile fileManager) {
        this.consultationList = conList;
        this.fileManager = fileManager;
        this.consultationControl = new ConsultationControl(appList, conList);

        setTitle("Manage All Consultations");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Table Panel ---
        String[] columnNames = {"ID", "Patient", "Doctor", "Date", "Diagnosis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        consultationTable = new JTable(tableModel);
        loadAllConsultations();
        add(new JScrollPane(consultationTable), BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnBack = new JButton("Back");
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        btnEdit.addActionListener(e -> editSelectedConsultation());
        btnDelete.addActionListener(e -> deleteSelectedConsultation());
        btnBack.addActionListener(e -> dispose());
    }

    private void loadAllConsultations() {
        tableModel.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Iterator<Consultation> it = consultationList.getIterator();
        while (it.hasNext()) {
            Consultation con = it.next();
            tableModel.addRow(new Object[]{
                con.getConsultationID(),
                con.getPatient().getName(),
                con.getDoctor().getName(),
                con.getConsultationDate().format(dateFormatter),
                con.getDiagnosis()
            });
        }
    }

    private void editSelectedConsultation() {
        JOptionPane.showMessageDialog(this, "Edit functionality would open the consultation form pre-filled with data.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedConsultation() {
        int selectedRow = consultationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a consultation to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String consultationId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete consultation " + consultationId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = consultationControl.deleteConsultation(consultationId);
            if (result.startsWith("Success")) {
                fileManager.writeConsultationsToFile("consultations.txt", consultationList);
                loadAllConsultations(); // Refresh table
                JOptionPane.showMessageDialog(this, result);
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
