/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface; // Make sure to import your ADT package
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import entity.Patient;
import entity.Doctor;
import entity.Medicine;
import entity.Schedule;
import entity.Consultation;
import entity.Treatment;
import entity.Appointment;

/**
 *
 * @author tiong
 */
public class MainUI extends JFrame {

    private ListInterface<Patient> patientList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Medicine> medicineList;
    private ListInterface<Schedule> scheduleList;
    private ListInterface<Appointment> appointmentList;
    private ListInterface<Consultation> consultationList;
    private ListInterface<Treatment> treatmentList;

    public MainUI(
            ListInterface<Patient> patientList, ListInterface<Doctor> doctorList,
            ListInterface<Medicine> medicineList, ListInterface<Schedule> scheduleList,
            ListInterface<Appointment> appointmentList, ListInterface<Consultation> consultationList,
            ListInterface<Treatment> treatmentList) {

        this.patientList = patientList;
        this.doctorList = doctorList;
        this.medicineList = medicineList;
        this.scheduleList = scheduleList;
        this.appointmentList = appointmentList;
        this.consultationList = consultationList;
        this.treatmentList = treatmentList;

        // --- Frame Setup ---
        setTitle("TAR UMT Clinic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Increased size for better layout
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        // --- Header Panel (NORTH) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204)); // A nice blue color
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("Clinic Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // --- Clock (EAST of Header) ---
        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        clockLabel.setForeground(Color.WHITE);
        updateClock(clockLabel); // Initial clock update
        headerPanel.add(clockLabel, BorderLayout.EAST);

        // Timer to update the clock every second
        Timer clockTimer = new Timer(1000, e -> updateClock(clockLabel));
        clockTimer.start();

        // --- Button Panel (CENTER) ---
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100)); // Add padding

        JButton btnPatient = createModuleButton("Patient Management");
        JButton btnDoctor = createModuleButton("Doctor Management");
        JButton btnConsultation = createModuleButton("Consultation Management");
        JButton btnTreatment = createModuleButton("Medical Treatment Management");
        JButton btnPharmacy = createModuleButton("Pharmacy Management");

        buttonPanel.add(btnPatient);
        buttonPanel.add(btnDoctor);
        buttonPanel.add(btnConsultation);
        buttonPanel.add(btnTreatment);
        buttonPanel.add(btnPharmacy);

        // --- Action Listeners ---
        btnPatient.addActionListener(e -> {
            System.out.println("Navigating to Patient Management Module...");
        });

        btnDoctor.addActionListener(e -> {
            System.out.println("Navigating to Doctor Management Module...");
        });

        btnConsultation.addActionListener(e -> {
            new ConsultationUI(this, appointmentList).setVisible(true);
            this.setVisible(false);
        });

        btnTreatment.addActionListener(e -> {
            System.out.println("Navigating to Medical Treatment Management Module...");
        });

        btnPharmacy.addActionListener(e -> {
            System.out.println("Navigating to Pharmacy Management Module...");
        });

        // --- Footer Panel (SOUTH) ---
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.LIGHT_GRAY);
        footerPanel.add(new JLabel("Developed by [Your Team Name]"));

        // --- Add panels to frame ---
        add(headerPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createModuleButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(new Color(240, 240, 240));
        button.setForeground(new Color(50, 50, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return button;
    }

    private void updateClock(JLabel clockLabel) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss");
        clockLabel.setText(LocalDateTime.now().format(formatter));
    }
}
