/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.ListInterface;
import entity.*;
import utility.ReadWriteFile;
import boundary.DoctorUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ScheduleControl extends JFrame {
    private DoctorUI parent;
    private ListInterface<Schedule> scheduleList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Appointment> appointmentList;
    private DefaultTableModel model;
    private ReadWriteFile rwf = new ReadWriteFile();
    private final String SCHEDULE_FILE = "src/main/java/Data File/schedules";

    public ScheduleControl(DoctorUI parent, ListInterface<Schedule> scheduleList, ListInterface<Doctor> doctorList, ListInterface<Appointment> appointmentList) {
        this.parent = parent;
        this.scheduleList = scheduleList;
        this.doctorList = doctorList;
        this.appointmentList = appointmentList;

        setTitle("Manage Schedules");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();

        // Auto refresh every 30 minutes
        new Timer(1800000, e -> refreshDoctorAvailability()).start();

        // Load from file if list is empty (prevents duplicates)
        if (scheduleList.isEmpty()) {
            rwf.readSchedulesFromFile(SCHEDULE_FILE, scheduleList, doctorList);
        }

        loadSchedules();
        refreshDoctorAvailability();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID","Doctor","Date","Start","End","Availability"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnUpd = new JButton("Update");
        JButton btnDel = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh Availability");
        JButton btnBack = new JButton("Back to Module");
        JButton btnBackMain = new JButton("Back to Main");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpd);
        btnPanel.add(btnDel);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnBack);
        btnPanel.add(btnBackMain);
        add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addSchedule());
        btnUpd.addActionListener(e -> updateSchedule());
        btnDel.addActionListener(e -> deleteSchedule());
        btnRefresh.addActionListener(e -> refreshDoctorAvailability());
        btnBack.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        btnBackMain.addActionListener(e -> {
            this.dispose();
            parent.dispose();
            parent.getMainUI().setVisible(true);  // ✅ use getter instead of private field
        });
    }

    private void loadSchedules() {
        model.setRowCount(0);
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            Schedule s = scheduleList.getEntry(i);
            model.addRow(new Object[]{
                    s.getScheduleID(),
                    s.getDoctor() != null ? s.getDoctor().getName() : "N/A",
                    s.getDutyDate(),
                    s.getStartTime(),
                    s.getEndTime(),
                    s.getAvailability()
            });
        }
    }

    private void addSchedule() {
        JTextField idField = new JTextField();
        JComboBox<String> docBox = new JComboBox<>();
        for (int i = 1; i <= doctorList.getLength(); i++) docBox.addItem(doctorList.getEntry(i).getDoctorID());
        JTextField dateField = new JTextField("2025-08-28");
        JTextField startField = new JTextField("09:00");
        JTextField endField = new JTextField("09:30");
        JTextField availField = new JTextField("Available");

        Object[] fields = {"ID:", idField, "Doctor ID:", docBox, "Date (yyyy-MM-dd):", dateField,
                "Start (HH:mm):", startField, "End (HH:mm):", endField, "Availability:", availField};

        if (JOptionPane.showConfirmDialog(this, fields, "Add Schedule", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText().trim();
                String docId = (String)docBox.getSelectedItem();
                LocalDate d = LocalDate.parse(dateField.getText().trim());
                LocalTime s = LocalTime.parse(startField.getText().trim());
                LocalTime e = LocalTime.parse(endField.getText().trim());
                String avail = availField.getText().trim();

                if (id.isEmpty() || docId == null) { JOptionPane.showMessageDialog(this, "ID and Doctor required"); return; }
                Doctor doc = null;
                for (int i = 1; i <= doctorList.getLength(); i++) {
                    if (doctorList.getEntry(i).getDoctorID().equals(docId)) { doc = doctorList.getEntry(i); break; }
                }

                Schedule sc = new Schedule(id, doc, d, s, e, avail);
                scheduleList.add(sc);
                rwf.writeSchedulesToFile(SCHEDULE_FILE, scheduleList);
                loadSchedules();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format");
            }
        }
    }

    private void updateSchedule() {
        int row = getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select schedule"); return; }
        String id = (String) model.getValueAt(row, 0);
        Schedule sc = null;
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            if (scheduleList.getEntry(i).getScheduleID().equals(id)) { sc = scheduleList.getEntry(i); break; }
        }
        if (sc == null) return;

        JTextField dateField = new JTextField(sc.getDutyDate().toString());
        JTextField startField = new JTextField(sc.getStartTime().toString());
        JTextField endField = new JTextField(sc.getEndTime().toString());
        JTextField availField = new JTextField(sc.getAvailability());
        Object[] fields = {"Date (yyyy-MM-dd):", dateField, "Start (HH:mm):", startField, "End (HH:mm):", endField, "Availability:", availField};

        if (JOptionPane.showConfirmDialog(this, fields, "Update Schedule", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                sc.setDutyDate(LocalDate.parse(dateField.getText().trim()));
                sc.setStartTime(LocalTime.parse(startField.getText().trim()));
                sc.setEndTime(LocalTime.parse(endField.getText().trim()));
                sc.setAvailability(availField.getText().trim());
                rwf.writeSchedulesToFile(SCHEDULE_FILE, scheduleList);
                loadSchedules();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format");
            }
        }
    }

    private void deleteSchedule() {
        int row = getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select schedule"); return; }
        String id = (String) model.getValueAt(row, 0);
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            if (scheduleList.getEntry(i).getScheduleID().equals(id)) { scheduleList.remove(i); break; }
        }
        rwf.writeSchedulesToFile(SCHEDULE_FILE, scheduleList);
        loadSchedules();
    }

    private int getSelectedRow() {
        Component[] comps = getContentPane().getComponents();
        for (Component c : comps) {
            if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                JViewport v = sp.getViewport();
                Component inside = v.getView();
                if (inside instanceof JTable) {
                    JTable t = (JTable) inside;
                    return t.getSelectedRow();
                }
            }
        }
        return -1;
    }

    // Availability refresh using appointments (30-minute slots)
    private void refreshDoctorAvailability() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        for (int i = 1; i <= scheduleList.getLength(); i++) {
            Schedule s = scheduleList.getEntry(i);
            String status = "Available";
            for (int j = 1; j <= appointmentList.getLength(); j++) {
                Appointment a = appointmentList.getEntry(j);
                if (a.getDoctor().getDoctorID().equals(s.getDoctor().getDoctorID()) && a.getAppointmentDate().equals(today)) {
                    LocalTime start = a.getAppointmentTime();
                    LocalTime end = start.plusMinutes(30);
                    if (!now.isBefore(start) && !now.isAfter(end)) { status = "Busy"; break; }
                }
            }
            s.setAvailability(status);
        }

        rwf.writeSchedulesToFile(SCHEDULE_FILE, scheduleList);
        loadSchedules();
    }
}

