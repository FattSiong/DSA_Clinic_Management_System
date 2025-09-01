/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import control.ConsultationControl;
import entity.Consultation;
import entity.Medicine;
import entity.Treatment;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 *
 * @author tiong
 */
public class PatientHistoryUI extends JFrame {

    private ConsultationControl consultationControl;
    private JTextArea historyDisplayArea;

    public PatientHistoryUI(JFrame parent, String patientId, String patientName, ConsultationControl control) {
        this.consultationControl = control;

        // --- Frame Setup ---
        setTitle("Patient History for " + patientName);
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));
        
        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Patient History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Area ---
        historyDisplayArea = new JTextArea();
        historyDisplayArea.setEditable(false);
        historyDisplayArea.setLineWrap(true);
        historyDisplayArea.setWrapStyleWord(true);
        historyDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(historyDisplayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Consultation & Treatment History",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        
        add(scrollPane, BorderLayout.CENTER);

        // --- Back Button ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(217, 83, 79));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> dispose());
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Populate the display area with the patient's history
        displayPatientHistory(patientId);
    }
    
    private void displayPatientHistory(String patientId) {
        ListInterface<Consultation> consultations = consultationControl.getPatientHistory(patientId);
        StringBuilder historyBuilder = new StringBuilder();

        if (consultations == null || consultations.isEmpty()) {
            historyBuilder.append("No consultation history found for this patient.");
        } else {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

            Iterator<Consultation> conIterator = consultations.getIterator();
            while (conIterator.hasNext()) {
                Consultation consultation = conIterator.next();
                
                historyBuilder.append("--------------------------------------------------------------------------------\n");
                historyBuilder.append(String.format("Consultation ID: %s\n", consultation.getConsultationID()));
                historyBuilder.append(String.format("Date: %s\n", consultation.getConsultationDate().format(dateFormatter)));
                historyBuilder.append(String.format("Doctor: %s\n", consultation.getDoctor().getName()));
                historyBuilder.append(String.format("Diagnosis: %s\n", consultation.getDiagnosis()));
                
                // Find and display the treatment for this consultation
                ListInterface<Treatment> allTreatments = consultationControl.getTreatmentList();
                boolean treatmentFound = false;
                if (allTreatments != null) {
                    Iterator<Treatment> treatIterator = allTreatments.getIterator();
                    while(treatIterator.hasNext()) {
                        Treatment treatment = treatIterator.next();
                        if(treatment.getConsultation().getConsultationID().equals(consultation.getConsultationID())) {
                            historyBuilder.append(String.format("Treatment Details: %s\n", treatment.getTreatmentDetails()));
                            
                            // Get and display the prescribed medicine names
                            ListInterface<Medicine> medicines = treatment.getPrescribedMedicine();
                            historyBuilder.append("Prescribed Medicine: ");
                            if (medicines == null || medicines.isEmpty()) {
                                historyBuilder.append("None\n");
                            } else {
                                Iterator<Medicine> medIterator = medicines.getIterator();
                                while(medIterator.hasNext()) {
                                    historyBuilder.append(medIterator.next().getName());
                                    if(medIterator.hasNext()) {
                                        historyBuilder.append(", ");
                                    }
                                }
                                historyBuilder.append("\n");
                            }
                            treatmentFound = true;
                            break; // Found the treatment, no need to continue the inner loop
                        }
                    }
                }
                
                if (!treatmentFound) {
                    historyBuilder.append("Treatment Details: Not recorded\n");
                    historyBuilder.append("Prescribed Medicine: None\n");
                }

                historyBuilder.append("\n"); // Add a blank line between consultations
            }
        }
        historyDisplayArea.setText(historyBuilder.toString());
    }
}