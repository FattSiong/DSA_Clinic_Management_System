/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
    private MainUI mainUI;  // reference to main menu
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

        setTitle("Doctor Management Module");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
    }

    public MainUI getMainUI() {   // ✅ getter for MainUI
        return mainUI;
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Doctor Management", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton btnManageDoctors = new JButton("Manage Doctors");
        JButton btnManageSchedules = new JButton("Manage Schedules");
        JButton btnBackMain = new JButton("Back to Main");

        panel.add(btnManageDoctors);
        panel.add(btnManageSchedules);
        panel.add(btnBackMain);

        add(panel, BorderLayout.CENTER);

        btnManageDoctors.addActionListener(e -> {
            this.setVisible(false);
            new DoctorControl(this, doctorList).setVisible(true);
        });

        btnManageSchedules.addActionListener(e -> {
            this.setVisible(false);
            new ScheduleControl(this, scheduleList, doctorList, appointmentList).setVisible(true);
        });

        btnBackMain.addActionListener(e -> {
            this.dispose();
            mainUI.setVisible(true);   // back to MainUI
        });
    }
}

