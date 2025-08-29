/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import control.ConsultationControl;
import utility.ReadWriteFile;
import entity.Consultation;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author tiong
 */
public class EditConsultationUI extends JFrame {

    // UI Components
    private JTextArea diagnosisArea = new JTextArea(5, 20);

    // Data and Control
    private Consultation consultationToEdit;
    private ConsultationControl consultationControl;
    private ManageConsultationUI parentUI;
    private ReadWriteFile fileManager;

    public EditConsultationUI(ManageConsultationUI parent, Consultation consultation, ConsultationControl control, ReadWriteFile manager) {
        this.parentUI = parent;
        this.consultationToEdit = consultation;
        this.consultationControl = control;
        this.fileManager = manager;

        setTitle("Edit Diagnosis for: " + consultation.getConsultationID());
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Info Panel (NORTH) ---
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel("Patient:"));
        infoPanel.add(new JLabel(consultation.getPatient().getName()));
        infoPanel.add(new JLabel("Doctor:"));
        infoPanel.add(new JLabel(consultation.getDoctor().getName()));
        add(infoPanel, BorderLayout.NORTH);

        // --- Form Panel (CENTER) ---
        JPanel formPanel = new JPanel(new BorderLayout(0, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        formPanel.add(new JLabel("Diagnosis:"), BorderLayout.NORTH);
        diagnosisArea.setText(consultation.getDiagnosis()); // Pre-fill data
        formPanel.add(new JScrollPane(diagnosisArea), BorderLayout.CENTER);
        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel (SOUTH) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveChanges() {
        String newDiagnosis = diagnosisArea.getText();

        // Call the control class to perform the logic
        String result = consultationControl.editConsultationDiagnosis(consultationToEdit, newDiagnosis);

        if (result.startsWith("Success")) {
            JOptionPane.showMessageDialog(this, result);
            parentUI.performSearch();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result, "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
