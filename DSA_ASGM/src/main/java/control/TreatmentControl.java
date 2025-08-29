/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.ListInterface;
import adt.DoublyLinkedList;
import entity.Consultation;
import entity.Medicine;
import entity.Treatment;
import utility.ReadWriteFile;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TreatmentControl extends JFrame {
    private Consultation consultation;
    private ListInterface<Medicine> medicineList;
    private ListInterface<Treatment> treatmentList;

    private JTextArea txtTreatmentDetails;
    private DefaultListModel<String> selectedMedicineModel;

    private JFrame parentFrame;

    public TreatmentControl(JFrame parent,
                           Consultation consultation,
                           ListInterface<Medicine> medicineList,
                           ListInterface<Treatment> treatmentList) {

        this.parentFrame = parent;
        this.consultation = consultation;
        this.medicineList = medicineList;
        this.treatmentList = treatmentList;

        // --- Frame Setup ---
        setTitle("Record Treatment for Consultation: " + consultation.getConsultationID());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 153, 153));
        JLabel titleLabel = new JLabel("Treatment Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                " Treatment Details ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16)
        ));

        // Treatment details text area
        txtTreatmentDetails = new JTextArea(5, 40);
        txtTreatmentDetails.setLineWrap(true);
        txtTreatmentDetails.setWrapStyleWord(true);
        JScrollPane detailsScroll = new JScrollPane(txtTreatmentDetails);

        // Medicine selection
        JComboBox<String> medicineCombo = new JComboBox<>();
        for (int i = 1; i <= medicineList.getLength(); i++) {
            medicineCombo.addItem(medicineList.getEntry(i).getName());
        }

        JButton btnAddMedicine = new JButton("Add Medicine");
        selectedMedicineModel = new DefaultListModel<>();
        JList<String> selectedMedicineList = new JList<>(selectedMedicineModel);
        JScrollPane medicineScroll = new JScrollPane(selectedMedicineList);

        JPanel medicinePanel = new JPanel(new BorderLayout(5, 5));
        medicinePanel.add(medicineCombo, BorderLayout.NORTH);
        medicinePanel.add(btnAddMedicine, BorderLayout.CENTER);
        medicinePanel.add(medicineScroll, BorderLayout.SOUTH);

        formPanel.add(new JLabel("Treatment Notes:"), BorderLayout.NORTH);
        formPanel.add(detailsScroll, BorderLayout.CENTER);
        formPanel.add(medicinePanel, BorderLayout.EAST);

        add(formPanel, BorderLayout.CENTER);

        // --- Actions ---
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnSave = new JButton("Save Treatment");
        JButton btnBack = new JButton("Back");

        styleButton(btnSave, new Color(92, 184, 92));
        styleButton(btnBack, new Color(217, 83, 79));

        actionPanel.add(btnSave);
        actionPanel.add(btnBack);
        add(actionPanel, BorderLayout.SOUTH);

        // --- Listeners ---
        btnAddMedicine.addActionListener(e -> {
            int index = medicineCombo.getSelectedIndex();
            if (index >= 0) {
                Medicine med = medicineList.getEntry(index + 1);
                selectedMedicineModel.addElement(med.getName());
            }
        });

        btnSave.addActionListener(e -> saveTreatment());
        btnBack.addActionListener(e -> {
            parentFrame.setVisible(true);
            this.dispose();
        });
    }

    private void saveTreatment() {
    String details = txtTreatmentDetails.getText().trim();
    if (details.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Please enter treatment details.", "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    
    ListInterface<Medicine> prescribedList = new DoublyLinkedList<>();
    for (int i = 0; i < selectedMedicineModel.size(); i++) {
        String medName = selectedMedicineModel.get(i);
        // Find medicine by name in medicineList
        for (int j = 1; j <= medicineList.getLength(); j++) {
            Medicine m = medicineList.getEntry(j);
            if (m.getName().equals(medName)) {
                prescribedList.add(m);
            }
        }
    }

    
    String treatmentID = String.format("T%03d", treatmentList.getLength() + 1);
    
    Treatment newTreatment = new Treatment(
            treatmentID,
            consultation,
            details,
            prescribedList
    );
    
    treatmentList.add(newTreatment);
    
    ReadWriteFile rwf = new ReadWriteFile();
    rwf.writeTreatmentsToFile("src/main/java/Data File/treatments", treatmentList);

    JOptionPane.showMessageDialog(this, "Treatment saved successfully!");

    txtTreatmentDetails.setText("");
    selectedMedicineModel.clear();
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