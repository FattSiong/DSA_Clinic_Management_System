/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import entity.Appointment;
import entity.Consultation;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import utility.ReadWriteFile;
import control.ConsultationControl;

/**
 *
 * @author tiong
 */
public class ConsultationDetailsUI extends JFrame {

    // UI Components
    private JTextArea diagnosisArea = new JTextArea(5, 20);
    private JTextArea notesArea = new JTextArea(5, 20);

    // Data
    private Appointment appointment;
    private ConsultationControl consultationControl;
    private ReadWriteFile fileManager;
    private ConsultationUI parentUI;

    public ConsultationDetailsUI(ConsultationUI parent, Appointment app, ReadWriteFile fileManager, ConsultationControl control) {
        this.parentUI = parent;
        this.appointment = app;
        this.consultationControl = control;
        this.fileManager = fileManager;

        setTitle("Consultation for: " + app.getPatient().getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Info Panel (NORTH) ---
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel("Appointment ID:"));
        infoPanel.add(new JLabel(app.getAppointmentID()));
        infoPanel.add(new JLabel("Patient Name:"));
        infoPanel.add(new JLabel(app.getPatient().getName()));
        infoPanel.add(new JLabel("Doctor Name:"));
        infoPanel.add(new JLabel(app.getDoctor().getName()));

        // --- Form Panel (CENTER) ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Diagnosis:"));
        formPanel.add(new JScrollPane(diagnosisArea));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(new JScrollPane(notesArea));

        // --- Button Panel (SOUTH) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Consultation");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // --- Add panels to frame ---
        add(infoPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        saveButton.addActionListener(e -> saveConsultation());
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveConsultation() {
        String diagnosis = diagnosisArea.getText();
        String notes = notesArea.getText();

        // Call the control class to do the work
        String result = consultationControl.saveNewConsultation(appointment, diagnosis, notes);

        if (result.startsWith("Success")) {
            // Save both files since changes were made to both lists
            fileManager.writeConsultationsToFile("src/main/java/Data File/consultations", consultationControl.getConsultationList()); // Assuming getter in control
            fileManager.writeAppointmentsToFile("src/main/java/Data File/appointments", consultationControl.getAppointmentList()); // Assuming getter in control

            JOptionPane.showMessageDialog(this, result);
            parentUI.refreshTable();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result, "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
