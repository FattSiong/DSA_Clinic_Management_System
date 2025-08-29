/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package boundary;

import control.TreatmentControl;
import adt.ListInterface;
import entity.Consultation;
import entity.Medicine;
import entity.Treatment;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TreatmentUI extends JFrame {
    private JTable consultationTable;
    private DefaultTableModel tableModel;
    private ListInterface<Consultation> consultationList;
    private ListInterface<Medicine> medicineList;
    private ListInterface<Treatment> treatmentList;
    private JFrame parentFrame;
    

    public TreatmentUI(JFrame parent,
                       ListInterface<Consultation> consultations,
                       ListInterface<Medicine> medicines,
                       ListInterface<Treatment> treatments) {

        this.parentFrame = parent;
        this.consultationList = consultations;
        this.medicineList = medicines;
        this.treatmentList = treatments;

        // --- Frame Setup ---
        setTitle("Consultation Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- Header ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Consultation Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Table ---
        String[] columnNames = {"Consultation ID", "Patient", "Doctor", "Diagnosis", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        consultationTable = new JTable(tableModel);
        consultationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        consultationTable.setFont(new Font("Arial", Font.PLAIN, 14));
        consultationTable.setRowHeight(30);
        consultationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        consultationTable.getTableHeader().setBackground(new Color(230, 230, 230));

        loadConsultationData();

        JScrollPane tableScrollPane = new JScrollPane(consultationTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                " Consultation Records ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16)
        ));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // --- Actions ---
        JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionsPanel.setBackground(Color.WHITE);

        JButton btnRecordTreatment = new JButton("Record Treatment");
        styleButton(btnRecordTreatment, new Color(92, 184, 92));
        
        JButton btnHistory = new JButton("View Treatment History");
        styleButton(btnHistory, new Color(0, 123, 255));
        
        JButton btnBack = new JButton("Back to Main Menu");
        styleButton(btnBack, new Color(217, 83, 79));

        actionsPanel.add(btnRecordTreatment);
        actionsPanel.add(btnHistory);
        actionsPanel.add(btnBack);
        add(actionsPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        btnRecordTreatment.addActionListener(e -> {
            int selectedRow = consultationTable.getSelectedRow();
            if (selectedRow >= 0) {
                Consultation selectedConsultation = consultationList.getEntry(selectedRow + 1);

                // Open TreatmentFormUI for this consultation
                TreatmentControl form = new TreatmentControl(this, selectedConsultation, medicineList, treatmentList);
                form.setVisible(true);
                this.setVisible(false);

            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a consultation first.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnHistory.addActionListener(e -> {
            TreatmentHistoryUI historyUI = new TreatmentHistoryUI(this, treatmentList);
            historyUI.setVisible(true);
            this.setVisible(false);
        });

        btnBack.addActionListener(e -> {
            parentFrame.setVisible(true);
            this.dispose();
        });
    }

    private void loadConsultationData() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (int i = 1; i <= consultationList.getLength(); i++) {
            Consultation c = consultationList.getEntry(i);

            String patientName = (c.getPatient() != null) ? c.getPatient().getName() : "N/A";
            String doctorName = (c.getDoctor() != null) ? c.getDoctor().getName() : "N/A";
            String diagnosis = (c.getDiagnosis() != null) ? c.getDiagnosis() : "-";
            String date = (c.getConsultationDate() != null) ? c.getConsultationDate().format(formatter) : "-";

            tableModel.addRow(new Object[]{
                    c.getConsultationID(),
                    patientName,
                    doctorName,
                    diagnosis,
                    date
            });
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
}