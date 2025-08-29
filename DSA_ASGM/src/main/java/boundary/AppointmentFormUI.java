/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import adt.ListInterface;
import control.AppointmentControl;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import control.PatientControl;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import utility.ReadWriteFile;

/**
 *
 * @author tiong
 */
public class AppointmentFormUI extends JFrame {

    // UI Components
    private JTextField patientIcField = new JTextField(15);
    private JButton findPatientButton = new JButton("Find");
    private JTextField patientNameField = new JTextField(20);
    
    private JComboBox<String> patientComboBox = new JComboBox<>();
    private JComboBox<String> doctorComboBox = new JComboBox<>();
    private JComboBox<Integer> dayComboBox = new JComboBox<>();
    private JComboBox<Integer> monthComboBox = new JComboBox<>();
    private JComboBox<Integer> yearComboBox = new JComboBox<>();
    private JTextField dateField = new JTextField(10);
    private JComboBox<String> timeSlotComboBox = new JComboBox<>();
    private JButton saveButton = new JButton("Save Appointment");

    // --- Data and Control ---
    private Patient selectedPatient = null;
    private ListInterface<Patient> patientList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Appointment> appointmentList;
    private AppointmentControl appointmentControl;
    private PatientControl patientControl;
    private ReadWriteFile fileManager;
    private ConsultationUI parentUI; // To call the refresh method

    /**
     * Constructor for booking a brand new appointment.
     */
    public AppointmentFormUI(ConsultationUI parent, ReadWriteFile fileManager, ListInterface<Patient> pList, ListInterface<Doctor> dList, ListInterface<Appointment> aList) {
        this.parentUI = parent;
        this.fileManager = fileManager;
        this.appointmentList = aList;
        this.appointmentControl = new AppointmentControl(aList, pList, dList, fileManager); // Initialize control class
        this.patientControl = new PatientControl(pList);
        
        initUI(true);
        setTitle("Book New Appointment");
        populateDoctorComboBox(dList);
    }

    /**
     * Constructor for scheduling a follow-up appointment.
     */
   public AppointmentFormUI(ConsultationUI parent, ReadWriteFile fileManager, Patient patient, Doctor doctor, ListInterface<Patient> pList, ListInterface<Doctor> dList, ListInterface<Appointment> aList) {
        this.parentUI = parent;
        this.fileManager = fileManager;
        this.appointmentList = aList;
        this.appointmentControl = new AppointmentControl(aList, pList, dList, fileManager);  // Control class for follow-up
        this.patientControl = new PatientControl(pList);
        
        initUI(false);
        setTitle("Schedule Follow-up for " + patient.getName());
        
        this.selectedPatient = patient;
        patientIcField.setText(patient.getIcNumber());
        patientIcField.setEditable(false);
        findPatientButton.setEnabled(false);
        patientNameField.setText(patient.getName());
        
        doctorComboBox.addItem(doctor.getName() + " (" + doctor.getDoctorID() + ")");
        doctorComboBox.setEnabled(false);
    }

    private void initUI(boolean isNewAppointment) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(parentUI);
        setLayout(new BorderLayout(10, 10));

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel patientSearchPanel = new JPanel(new BorderLayout(5, 0));
        patientSearchPanel.add(patientIcField, BorderLayout.CENTER);
        patientSearchPanel.add(findPatientButton, BorderLayout.EAST);
        
        formPanel.add(new JLabel("Patient IC Number:"));
        formPanel.add(patientSearchPanel);

        formPanel.add(new JLabel("Patient Name:"));
        patientNameField.setEditable(false);
        patientNameField.setBackground(new Color(230, 230, 230));
        formPanel.add(patientNameField);
        
        formPanel.add(new JLabel("Doctor:"));
        formPanel.add(doctorComboBox);
        
        // --- Date Picker Panel ---
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(dayComboBox);
        datePanel.add(monthComboBox);
        datePanel.add(yearComboBox);
        formPanel.add(new JLabel("Date:"));
        formPanel.add(datePanel);
        
        formPanel.add(new JLabel("Time:"));
        formPanel.add(timeSlotComboBox);
        
        populateDatePickers();
        populateTimeSlots();

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        findPatientButton.addActionListener(e -> findPatient());
        saveButton.addActionListener(e -> saveAppointment());
        cancelButton.addActionListener(e -> dispose());
        
        // Listeners to update days when month/year changes
        monthComboBox.addActionListener(e -> updateDays());
        yearComboBox.addActionListener(e -> updateDays());
        
        // Initially disable save button until a patient is found
        if (isNewAppointment) {
            saveButton.setEnabled(false);
        }
    }
    
     private void findPatient() {
        String ic = patientIcField.getText();
        selectedPatient = patientControl.findPatientByIc(ic);

        if (selectedPatient != null) {
            patientNameField.setText(selectedPatient.getName());
            saveButton.setEnabled(true); // Enable save button
        } else {
            patientNameField.setText("");
            saveButton.setEnabled(false); // Disable save button
            JOptionPane.showMessageDialog(this, "Patient with IC number '" + ic + "' not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveAppointment() {
        if (selectedPatient == null) {
            JOptionPane.showMessageDialog(this, "Please find and select a patient first.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 1. Get raw data from the form
        String patientId = selectedPatient.getPatientID();
        String doctorId = getSelectedIdFromComboBox(doctorComboBox);
        LocalDate date;
        try {
            int day = (int) dayComboBox.getSelectedItem();
            int month = (int) monthComboBox.getSelectedItem();
            int year = (int) yearComboBox.getSelectedItem();
            date = LocalDate.of(year, month, day);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please select a valid date.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LocalTime time = LocalTime.parse((String) timeSlotComboBox.getSelectedItem());

        // 2. Call the control class to do the work
        String result = appointmentControl.bookAppointment(patientId, doctorId, date, time);

        // 3. Display the result
        if (result.startsWith("Success")) {
            JOptionPane.showMessageDialog(this, result);
            parentUI.performSearch();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result, "Booking Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- Helper Methods for UI Population ---
    private void populateDatePickers() {
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= 12; i++) monthComboBox.addItem(i);
        yearComboBox.addItem(today.getYear());
        yearComboBox.addItem(today.getYear() + 1);
        
        monthComboBox.setSelectedItem(today.getMonthValue());
        yearComboBox.setSelectedItem(today.getYear());
        updateDays();
        dayComboBox.setSelectedItem(today.getDayOfMonth());
    }

    private void updateDays() {
        int year = (int) yearComboBox.getSelectedItem();
        int month = (int) monthComboBox.getSelectedItem();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        
        Integer selectedDay = (Integer) dayComboBox.getSelectedItem();
        dayComboBox.removeAllItems();
        for (int i = 1; i <= daysInMonth; i++) {
            dayComboBox.addItem(i);
        }
        
        if (selectedDay != null && selectedDay <= daysInMonth) {
            dayComboBox.setSelectedItem(selectedDay);
        }
    }
    
    private void populatePatientComboBox(ListInterface<Patient> pList) {
        if (pList == null) return;
        Iterator<Patient> it = pList.getIterator();
        while (it.hasNext()) {
            Patient p = it.next();
            patientComboBox.addItem(p.getName() + " (" + p.getPatientID() + ")");
        }
    }

    private void populateDoctorComboBox(ListInterface<Doctor> dList) {
        if (dList == null) return;
        Iterator<Doctor> it = dList.getIterator();
        while (it.hasNext()) {
            Doctor d = it.next();
            doctorComboBox.addItem(d.getName() + " (" + d.getDoctorID() + ")");
        }
    }
    
    private void populateTimeSlots() {
        for (int hour = 9; hour <= 17; hour++) {
            timeSlotComboBox.addItem(String.format("%02d:00", hour));
            if (hour < 17) {
                timeSlotComboBox.addItem(String.format("%02d:30", hour));
            }
        }
    }

    private String getSelectedIdFromComboBox(JComboBox<String> comboBox) {
        String selected = (String) comboBox.getSelectedItem();
        if (selected == null) return null;
        return selected.substring(selected.indexOf('(') + 1, selected.indexOf(')'));
    }
}
