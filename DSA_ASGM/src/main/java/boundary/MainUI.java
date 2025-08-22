/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author tiong
 */
public class MainUI extends JFrame {

    public MainUI() {
        // --- Frame Setup ---
        setTitle("TAR UMT Clinic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400); // Set a decent default size
        setLocationRelativeTo(null); // Center the frame on the screen

        // --- Main Panel and Layout ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1, 10, 10)); // 5 rows, 1 column, with 10px gaps
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // --- Title Label ---
        JLabel titleLabel = new JLabel("Clinic Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // --- Buttons for each module ---
        JButton btnPatient = createModuleButton("Patient Management");
        JButton btnDoctor = createModuleButton("Doctor Management");
        JButton btnConsultation = createModuleButton("Consultation Management");
        JButton btnTreatment = createModuleButton("Medical Treatment Management");
        JButton btnPharmacy = createModuleButton("Pharmacy Management");

        // --- Add Action Listeners to Buttons ---
        // These currently print to the console.
        // You will replace these with code to open your module's JFrame.
        
        btnPatient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Replace with code to launch the Patient Management UI
                System.out.println("Navigating to Patient Management Module...");
                // Example: new PatientUI().setVisible(true);
            }
        });

        btnDoctor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Replace with code to launch the Doctor Management UI
                System.out.println("Navigating to Doctor Management Module...");
            }
        });

        btnConsultation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Replace with code to launch the Consultation Management UI
                System.out.println("Navigating to Consultation Management Module...");
            }
        });

        btnTreatment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Replace with code to launch the Treatment Management UI
                System.out.println("Navigating to Medical Treatment Management Module...");
            }
        });
        
        btnPharmacy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Replace with code to launch the Pharmacy Management UI
                System.out.println("Navigating to Pharmacy Management Module...");
            }
        });

        // --- Add buttons to the panel ---
        mainPanel.add(btnPatient);
        mainPanel.add(btnDoctor);
        mainPanel.add(btnConsultation);
        mainPanel.add(btnTreatment);
        mainPanel.add(btnPharmacy);
        
        // --- Add components to the frame ---
        // Use BorderLayout to place the title at the top and buttons in the center
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(titleLabel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Helper method to create and style a JButton.
     * @param text The text for the button.
     * @return A styled JButton.
     */
    private JButton createModuleButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false); // Improves look and feel
        return button;
    }

    /**
     * The main entry point for the entire application.
     * @param args
     */
    public static void main(String[] args) {
        // This is how you start your application.
        // It's recommended to run GUI code on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainUI().setVisible(true);
            }
        });
    }
}
