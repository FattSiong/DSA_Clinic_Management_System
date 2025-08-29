/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import entity.Treatment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TreatmentHistoryUI extends JFrame {
    private JTable treatmentTable;
    private DefaultTableModel tableModel;
    private ListInterface<Treatment> treatmentList;
    private JFrame parentFrame;

    public TreatmentHistoryUI(JFrame parent, ListInterface<Treatment> treatments) {
        this.parentFrame = parent;
        this.treatmentList = treatments;

        setTitle("Treatment History");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Table ---
        String[] columnNames = {"Treatment ID", "Consultation ID", "Details", "Medicines"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        treatmentTable = new JTable(tableModel);
        treatmentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        treatmentTable.setRowHeight(30);

        loadTreatmentData();

        JScrollPane tableScrollPane = new JScrollPane(treatmentTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // --- Back Button ---
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            parentFrame.setVisible(true);
            this.dispose();
        });
        add(btnBack, BorderLayout.SOUTH);
    }

    private void loadTreatmentData() {
        tableModel.setRowCount(0);

        for (int i = 1; i <= treatmentList.getLength(); i++) {
            Treatment t = treatmentList.getEntry(i);

            // Build medicine string
            StringBuilder meds = new StringBuilder();
            for (int j = 1; j <= t.getPrescribedMedicine().getLength(); j++) {
                meds.append(t.getPrescribedMedicine().getEntry(j).getName());
                if (j < t.getPrescribedMedicine().getLength()) {
                    meds.append(", ");
                }
            }

            tableModel.addRow(new Object[]{
                    t.getTreatmentID(),
                    t.getConsultation().getConsultationID(),
                    t.getTreatmentDetails(),
                    meds.toString()
            });
        }
    }
}