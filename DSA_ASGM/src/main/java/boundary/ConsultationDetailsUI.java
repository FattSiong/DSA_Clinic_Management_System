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
import control.QueueControl;
import entity.Queue;

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
    private Queue queue;
    private ConsultationControl consultationControl;
    private QueueControl queueControl;
    private ReadWriteFile fileManager;
    private ConsultationUI parentUI;

    /**
     * Constructor for a scheduled APPOINTMENT.
    */
    public ConsultationDetailsUI(ConsultationUI parent, Appointment app, ConsultationControl control, QueueControl qControl) {
        this.parentUI = parent;
        this.appointment = app;
        this.queue = null;
        this.consultationControl = control;
        this.queueControl = qControl;
        
        initUI("Consultation for: " + app.getPatient().getName(), app.getAppointmentID(), app.getPatient().getName(), app.getDoctor().getName());
    }

    /**
     * Constructor for a walk-in QUEUE patient.
     */
    public ConsultationDetailsUI(ConsultationUI parent, Queue q, ConsultationControl control, QueueControl qControl) {
        this.parentUI = parent;
        this.appointment = null;
        this.queue = q;
        this.consultationControl = control;
        this.queueControl = qControl;

        initUI("Consultation for Queue No: " + q.getQueueNumber(), "N/A (Walk-in)", q.getPatient().getName(), q.getDoctor().getName());
    }
    
    private void initUI(String title, String id, String patientName, String doctorName) {
    // --- Frame Setup ---
    setTitle(title);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(500, 450);
    setLocationRelativeTo(parentUI); // Center it over the main consultation window
    setLayout(new BorderLayout(10, 10));
    getContentPane().setBackground(new Color(245, 245, 245));

    // --- Info Panel (NORTH) ---
    // This panel displays the key information and is not editable.
    JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    infoPanel.setBackground(Color.WHITE);
    
    infoPanel.add(new JLabel("ID:"));
    infoPanel.add(new JLabel(id));
    infoPanel.add(new JLabel("Patient Name:"));
    infoPanel.add(new JLabel(patientName));
    infoPanel.add(new JLabel("Doctor Name:"));
    infoPanel.add(new JLabel(doctorName));

    // --- Form Panel (CENTER) ---
    // This panel holds the input fields for diagnosis and notes.
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    formPanel.setBackground(Color.WHITE);

    formPanel.add(new JLabel("Diagnosis:"));
    diagnosisArea.setLineWrap(true); // Prevents horizontal scrolling
    diagnosisArea.setWrapStyleWord(true);
    formPanel.add(new JScrollPane(diagnosisArea));
    
    formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adds a small vertical space
    
    formPanel.add(new JLabel("Notes:"));
    notesArea.setLineWrap(true);
    notesArea.setWrapStyleWord(true);
    formPanel.add(new JScrollPane(notesArea));

    // --- Button Panel (SOUTH) ---
    // This panel holds the action buttons.
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    buttonPanel.setBackground(Color.WHITE);
    
    JButton saveButton = new JButton("Save Consultation");
    JButton cancelButton = new JButton("Cancel");
    
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    // --- Add all panels to the frame ---
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
        String result;

        if (this.appointment != null) {
            // It's a scheduled appointment, call the appointment-specific method
            result = consultationControl.saveAppointmentConsultation(this.appointment, diagnosis, notes);
        } else if (this.queue != null) {
            // It's a walk-in patient from the queue, call the queue-specific method
            result = consultationControl.saveWalkInConsultation(this.queue, diagnosis, notes);
            
            // Note: The UI now also updates the queue status, and the QueueControl
            // will handle the file saving for the queue.
            this.queue.setStatus("Completed");
            this.queueControl.updateQueueFile();
            
        } else {
            // Neither an appointment nor a queue item was selected
            JOptionPane.showMessageDialog(this, "Internal Error: No patient selected for consultation.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (result.startsWith("Success")) {
            JOptionPane.showMessageDialog(this, result);
            parentUI.refreshTable(); // Refresh both the appointment and queue tables
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result, "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
