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
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Iterator;
/**
 *
 * @author tiong
 */
public class EditAppointmentUI extends JFrame {

    // UI Components
    private JComboBox<String> doctorComboBox = new JComboBox<>();
    private JComboBox<Integer> dayComboBox = new JComboBox<>();
    private JComboBox<Integer> monthComboBox = new JComboBox<>();
    private JComboBox<Integer> yearComboBox = new JComboBox<>();
    private JComboBox<String> timeSlotComboBox = new JComboBox<>();

    // Data and Control
    private Appointment appointmentToEdit;
    private AppointmentControl appointmentControl;
    private ManageAppointmentUI parentUI;

    public EditAppointmentUI(ManageAppointmentUI parent, Appointment appointment, ListInterface<Doctor> doctorList, AppointmentControl control) {
        this.parentUI = parent;
        this.appointmentToEdit = appointment;
        this.appointmentControl = control;

        setTitle("Edit Appointment: " + appointment.getAppointmentID());
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Patient:"));
        formPanel.add(new JLabel(appointment.getPatient().getName())); // Patient is not editable
        formPanel.add(new JLabel("Doctor:"));
        formPanel.add(doctorComboBox);
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(dayComboBox);
        datePanel.add(monthComboBox);
        datePanel.add(yearComboBox);
        formPanel.add(new JLabel("Date:"));
        formPanel.add(datePanel);
        
        formPanel.add(new JLabel("Time:"));
        formPanel.add(timeSlotComboBox);

        populateDoctorComboBox(doctorList);
        populateDatePickers();
        populateTimeSlots();
        prefillData();

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());
        monthComboBox.addActionListener(e -> updateDays());
        yearComboBox.addActionListener(e -> updateDays());
    }

    private void saveChanges() {
        String newDoctorId = getSelectedIdFromComboBox(doctorComboBox);
        LocalDate newDate;
        try {
            newDate = LocalDate.of((int) yearComboBox.getSelectedItem(), (int) monthComboBox.getSelectedItem(), (int) dayComboBox.getSelectedItem());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please select a valid date.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LocalTime newTime = LocalTime.parse((String) timeSlotComboBox.getSelectedItem());

        String result = appointmentControl.editAppointment(appointmentToEdit, newDoctorId, newDate, newTime);

        if (result.startsWith("Success")) {
            JOptionPane.showMessageDialog(this, result);
            parentUI.performSearch();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result, "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prefillData() {
        // Pre-select the current doctor
        String currentDoctorString = appointmentToEdit.getDoctor().getName() + " (" + appointmentToEdit.getDoctor().getDoctorID() + ")";
        doctorComboBox.setSelectedItem(currentDoctorString);

        // Pre-select the current date
        LocalDate currentDate = appointmentToEdit.getAppointmentDate();
        yearComboBox.setSelectedItem(currentDate.getYear());
        monthComboBox.setSelectedItem(currentDate.getMonthValue());
        dayComboBox.setSelectedItem(currentDate.getDayOfMonth());

        // Pre-select the current time
        timeSlotComboBox.setSelectedItem(String.format("%02d:%02d", appointmentToEdit.getAppointmentTime().getHour(), appointmentToEdit.getAppointmentTime().getMinute()));
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
