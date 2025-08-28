package boundary;

import control.ManageDoctorUI;
import control.ManageScheduleUI;
import adt.ListInterface;
import entity.Doctor;
import entity.Schedule;
import boundary.MainUI;

import javax.swing.*;
import java.awt.*;

public class DoctorUI extends JFrame {
    private ListInterface<Doctor> doctorList;
    private ListInterface<Schedule> scheduleList;
    private MainUI mainUI;

    public DoctorUI(MainUI mainUI, ListInterface<Doctor> doctorList, ListInterface<Schedule> scheduleList) {
        this.mainUI = mainUI;
        this.doctorList = doctorList;
        this.scheduleList = scheduleList;

        setTitle("Doctor Management Module");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnManageDoctors = new JButton("Manage Doctors");
        JButton btnManageSchedules = new JButton("Manage Schedules");
        JButton btnBackMain = new JButton("Back to Main");

        btnManageDoctors.addActionListener(e -> {
            this.setVisible(false);
            new ManageDoctorUI(this, doctorList).setVisible(true);
        });

        btnManageSchedules.addActionListener(e -> {
            this.setVisible(false);
            new ManageScheduleUI(this, scheduleList, doctorList).setVisible(true);
        });


        btnBackMain.addActionListener(e -> {
            this.dispose();
            mainUI.setVisible(true);
        });

        panel.add(btnManageDoctors);
        panel.add(btnManageSchedules);
        panel.add(btnBackMain);

        add(panel);
    }
}
