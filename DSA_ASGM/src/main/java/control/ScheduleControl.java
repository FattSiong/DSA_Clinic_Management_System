package control;

import boundary.DoctorUI;
import adt.ListInterface;
import entity.Doctor;
import entity.Schedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleControl extends JFrame {
    private DoctorUI parentUI;
    private ListInterface<Schedule> scheduleList;
    private ListInterface<Doctor> doctorList;
    private DefaultTableModel scheduleTableModel;
    private JTable scheduleTable;

    public ScheduleControl(DoctorUI parentUI, ListInterface<Schedule> scheduleList, ListInterface<Doctor> doctorList) {
        this.parentUI = parentUI;
        this.scheduleList = scheduleList;
        this.doctorList = doctorList;

        setTitle("Manage Schedules");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadSchedulesToTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        scheduleTableModel = new DefaultTableModel(new String[]{"ID", "Doctor", "Date", "Start", "End", "Availability"}, 0);
        scheduleTable = new JTable(scheduleTableModel);
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnBack = new JButton("Back");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // CRUD actions
        btnAdd.addActionListener(e -> addSchedule());
        btnUpdate.addActionListener(e -> updateSchedule());
        btnDelete.addActionListener(e -> deleteSchedule());
        btnBack.addActionListener(e -> {
            this.dispose();
            parentUI.setVisible(true);
        });
    }

    private void loadSchedulesToTable() {
        scheduleTableModel.setRowCount(0);
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            Schedule s = scheduleList.getEntry(i);
            scheduleTableModel.addRow(new Object[]{
                s.getScheduleID(),
                s.getDoctor().getName(),
                s.getDutyDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getAvailability()
            });
        }
    }

    private void addSchedule() {
        JTextField idField = new JTextField();
        JComboBox<String> doctorBox = new JComboBox<>();
        for (int i = 1; i <= doctorList.getLength(); i++) {
            doctorBox.addItem(doctorList.getEntry(i).getDoctorID());
        }
        JTextField dateField = new JTextField("2025-08-28");
        JTextField startField = new JTextField("09:00");
        JTextField endField = new JTextField("17:00");
        JTextField availField = new JTextField("Available");

        Object[] fields = {
            "ID:", idField,
            "Doctor ID:", doctorBox,
            "Date (yyyy-MM-dd):", dateField,
            "Start (HH:mm):", startField,
            "End (HH:mm):", endField,
            "Availability:", availField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Schedule", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Doctor doctor = null;
            String docId = (String) doctorBox.getSelectedItem();
            for (int i = 1; i <= doctorList.getLength(); i++) {
                if (doctorList.getEntry(i).getDoctorID().equals(docId)) {
                    doctor = doctorList.getEntry(i);
                    break;
                }
            }
            Schedule s = new Schedule(
                idField.getText(),
                doctor,
                LocalDate.parse(dateField.getText()),
                LocalTime.parse(startField.getText()),
                LocalTime.parse(endField.getText()),
                availField.getText()
            );
            scheduleList.add(s);
            loadSchedulesToTable();
            JOptionPane.showMessageDialog(this, "Schedule added successfully!");
        }
    }

    private void updateSchedule() {
        int row = scheduleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a schedule to update!");
            return;
        }

        String id = (String) scheduleTableModel.getValueAt(row, 0);
        Schedule s = null;
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            if (scheduleList.getEntry(i).getScheduleID().equals(id)) {
                s = scheduleList.getEntry(i);
                break;
            }
        }

        if (s != null) {
            JTextField dateField = new JTextField(s.getDutyDate().toString());
            JTextField startField = new JTextField(s.getStartTime().toString());
            JTextField endField = new JTextField(s.getEndTime().toString());
            JTextField availField = new JTextField(s.getAvailability());

            Object[] fields = {
                "Date (yyyy-MM-dd):", dateField,
                "Start (HH:mm):", startField,
                "End (HH:mm):", endField,
                "Availability:", availField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Update Schedule", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                s.setDutyDate(LocalDate.parse(dateField.getText()));
                s.setStartTime(LocalTime.parse(startField.getText()));
                s.setEndTime(LocalTime.parse(endField.getText()));
                s.setAvailability(availField.getText());
                loadSchedulesToTable();
                JOptionPane.showMessageDialog(this, "Schedule update successfully!");
            }
        }
    }

    private void deleteSchedule() {
        int row = scheduleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a schedule to delete!");
            return;
        }

        String id = (String) scheduleTableModel.getValueAt(row, 0);
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            if (scheduleList.getEntry(i).getScheduleID().equals(id)) {
                scheduleList.remove(i);
                break;
            }
        }
        loadSchedulesToTable();
        JOptionPane.showMessageDialog(this, "Schedule delete successfully!");
    }
}
