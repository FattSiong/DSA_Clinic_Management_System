package boundary;

import adt.ListInterface;
import control.DoctorControl;
import control.ScheduleControl;
import entity.Appointment;
import entity.Doctor;
import entity.Schedule;

import javax.swing.*;
import java.awt.*;

public class DoctorUI extends JFrame {
    private MainUI mainUI;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Schedule> scheduleList;
    private ListInterface<Appointment> appointmentList;

    public DoctorUI(MainUI mainUI,
                    ListInterface<Doctor> doctorList,
                    ListInterface<Schedule> scheduleList,
                    ListInterface<Appointment> appointmentList) {
        this.mainUI = mainUI;
        this.doctorList = doctorList;
        this.scheduleList = scheduleList;
        this.appointmentList = appointmentList;

        // Use a consistent window title
        setTitle("Consultation & Appointment Dashboard");
        setSize(420, 300); // Slightly taller to accommodate new style
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // Set the main background color
        getContentPane().setBackground(UIStyle.MAIN_BACKGROUND);

        initComponents();
    }

    public MainUI getMainUI() {
        return mainUI;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ======== HEADER PANEL (NORTH) ========
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyle.HEADER_BACKGROUND);
        JLabel lblTitle = UIStyle.createHeaderLabel("Doctor Management Module");
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ======== BUTTONS PANEL (CENTER) ========
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBackground(UIStyle.MAIN_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Create styled buttons using the UIStyle helper class
        JButton btnManageDoctors = UIStyle.createStyledButton("Manage Doctors", UIStyle.SECONDARY_ACTION_COLOR);
        JButton btnManageSchedules = UIStyle.createStyledButton("Manage Schedules", UIStyle.SECONDARY_ACTION_COLOR);
        JButton btnBackMain = UIStyle.createStyledButton("Back to Main", new Color(120, 120, 120));

        panel.add(btnManageDoctors);
        panel.add(btnManageSchedules);
        panel.add(btnBackMain);

        add(panel, BorderLayout.CENTER);

        btnManageDoctors.addActionListener(e -> {
            this.setVisible(false);
            new DoctorControl(this, doctorList, appointmentList); 
        });

        btnManageSchedules.addActionListener(e -> {
            this.setVisible(false);
            new ScheduleControl(this, scheduleList, doctorList, appointmentList); 
        });

        btnBackMain.addActionListener(e -> {
            this.dispose();
            mainUI.setVisible(true);
        });
    }
}