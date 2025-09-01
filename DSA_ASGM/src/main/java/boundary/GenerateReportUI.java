/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import control.ConsultationControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Objects;
import java.time.Year;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.time.YearMonth;


/**
 * UI to select and generate various reports for the Consultation Management Module.
 *
 * @author tiong
 */
public class GenerateReportUI extends JDialog {

    private final ConsultationControl consultationControl;
    private JComboBox<String> reportComboBox;
    private JComboBox<String> periodComboBox;
    private JPanel dynamicDatePanel;
    private JComboBox<Integer> dayComboBox;
    private JComboBox<Integer> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JLabel dayLabel;
    private JLabel monthLabel;
    private JLabel yearLabel;

    private final String[] reportOptions = {
        "Consultation Summary Report",
        "Appointment Cancellation Analysis Report",
        "Top Diagnoses Report"
    };

    private final String[] periodOptions = {
        "Daily",
        "Monthly",
        "Annually"
    };

    public GenerateReportUI(JFrame parent, ConsultationControl consultationControl) {
        super(parent, "Generate Reports", true); // Modal dialog
        this.consultationControl = consultationControl;

        // --- Frame Setup ---
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- Report Selection Panel ---
        JPanel selectionPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        selectionPanel.setBackground(Color.WHITE);

        reportComboBox = new JComboBox<>(reportOptions);
        reportComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        selectionPanel.add(new JLabel("Select Report:"));
        selectionPanel.add(reportComboBox);

        periodComboBox = new JComboBox<>(periodOptions);
        periodComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        
        dynamicDatePanel = new JPanel();
        dynamicDatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dynamicDatePanel.setBackground(Color.WHITE);

        // Date pickers for daily/monthly/annually
        dayLabel = new JLabel("Day:");
        monthLabel = new JLabel("Month:");
        yearLabel = new JLabel("Year:");
        dayComboBox = new JComboBox<>();
        monthComboBox = new JComboBox<>();
        yearComboBox = new JComboBox<>();
        
        dynamicDatePanel.add(dayLabel);
        dynamicDatePanel.add(dayComboBox);
        dynamicDatePanel.add(monthLabel);
        dynamicDatePanel.add(monthComboBox);
        dynamicDatePanel.add(yearLabel);
        dynamicDatePanel.add(yearComboBox);

        selectionPanel.add(periodComboBox);
        selectionPanel.add(dynamicDatePanel);
        
        populateDatePickers();

        add(selectionPanel, BorderLayout.CENTER);

        reportComboBox.addActionListener(e -> updateUIBasedOnReport());
        periodComboBox.addActionListener(e -> updateUIBasedOnPeriod());
        updateUIBasedOnReport(); // Initial UI setup

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton generateButton = new JButton("Generate");
        JButton closeButton = new JButton("Close");

        styleButton(generateButton, new Color(66, 139, 202));
        styleButton(closeButton, new Color(217, 83, 79));

        buttonPanel.add(generateButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSelectedReport();
            }
        });

        closeButton.addActionListener(e -> dispose());
        
        monthComboBox.addActionListener(e -> updateDays());
        yearComboBox.addActionListener(e -> updateDays());
    }

    private void updateUIBasedOnReport() {
        String selectedReport = (String) reportComboBox.getSelectedItem();
        boolean isSummaryReportSelected = consultationControl.getConsultationList().customEquals(selectedReport, "Consultation Summary Report");
        boolean isCancellationReportSelected = consultationControl.getConsultationList().customEquals(selectedReport, "Appointment Cancellation Analysis Report");

        if (isSummaryReportSelected || isCancellationReportSelected) {
            periodComboBox.setVisible(true);
            dynamicDatePanel.setVisible(true);
            updateUIBasedOnPeriod();
        } else {
            periodComboBox.setVisible(false);
            dynamicDatePanel.setVisible(false);
        }
    }
    
    private void updateUIBasedOnPeriod() {
        String selectedPeriod = (String) periodComboBox.getSelectedItem();
        
        if ("Daily".equals(selectedPeriod)) {
            dayLabel.setVisible(true);
            dayComboBox.setVisible(true);
            monthLabel.setVisible(true);
            monthComboBox.setVisible(true);
            yearLabel.setVisible(true);
            yearComboBox.setVisible(true);
            dynamicDatePanel.setBorder(BorderFactory.createTitledBorder("Daily Report Period"));
        } else if ("Monthly".equals(selectedPeriod)) {
            dayLabel.setVisible(false);
            dayComboBox.setVisible(false);
            monthLabel.setVisible(true);
            monthComboBox.setVisible(true);
            yearLabel.setVisible(true);
            yearComboBox.setVisible(true);
            dynamicDatePanel.setBorder(BorderFactory.createTitledBorder("Monthly Report Period"));
        } else if ("Annually".equals(selectedPeriod)) {
            dayLabel.setVisible(false);
            dayComboBox.setVisible(false);
            monthLabel.setVisible(false);
            monthComboBox.setVisible(false);
            yearLabel.setVisible(true);
            yearComboBox.setVisible(true);
            dynamicDatePanel.setBorder(BorderFactory.createTitledBorder("Annually Report Period"));
        }
    }
    
    private void populateDatePickers() {
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }
        int currentYear = today.getYear();
        for (int i = 2020; i <= currentYear + 5; i++) {
            yearComboBox.addItem(i);
        }
        
        monthComboBox.setSelectedItem(today.getMonthValue());
        yearComboBox.setSelectedItem(today.getYear());
        updateDays();
        dayComboBox.setSelectedItem(today.getDayOfMonth());
    }
    
    private void updateDays() {
        if(monthComboBox.getSelectedItem() == null || yearComboBox.getSelectedItem() == null) {
            return;
        }
        
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


    private void generateSelectedReport() {
        String selectedReport = (String) reportComboBox.getSelectedItem();
        String reportOutput;

        if (selectedReport == null) {
            JOptionPane.showMessageDialog(this, "Please select a report to generate.", "No Report Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (selectedReport) {
            case "Consultation Summary Report":
                String period = (String) periodComboBox.getSelectedItem();
                reportOutput = generateSummaryReportWithDate(period);
                break;
            case "Appointment Cancellation Analysis Report":
                String periodCancel = (String) periodComboBox.getSelectedItem();
                reportOutput = generateCancellationReportWithDate(periodCancel);
                break;
            case "Top Diagnoses Report":
                reportOutput = consultationControl.getTopDiagnosesReport(5); // Show top 5 diagnoses
                break;
            default:
                reportOutput = "Error: Invalid report selected.";
        }

        new ReportUI(this, selectedReport, reportOutput).setVisible(true);
    }
    
    private String generateSummaryReportWithDate(String period) {
        int year = (int) yearComboBox.getSelectedItem();
        int month = 1;
        int day = 1;

        if ("Daily".equals(period) && dayComboBox.getSelectedItem() != null) {
            day = (int) dayComboBox.getSelectedItem();
            month = (int) monthComboBox.getSelectedItem();
        } else if ("Monthly".equals(period) && monthComboBox.getSelectedItem() != null) {
            month = (int) monthComboBox.getSelectedItem();
        }

        LocalDate selectedDate = LocalDate.of(year, month, day);
        return consultationControl.getConsultationSummaryReport(period, selectedDate);
    }
    
    private String generateCancellationReportWithDate(String period) {
        int year = (int) yearComboBox.getSelectedItem();
        int month = 1;
        int day = 1;

        if ("Daily".equals(period) && dayComboBox.getSelectedItem() != null) {
            day = (int) dayComboBox.getSelectedItem();
            month = (int) monthComboBox.getSelectedItem();
        } else if ("Monthly".equals(period) && monthComboBox.getSelectedItem() != null) {
            month = (int) monthComboBox.getSelectedItem();
        }

        LocalDate selectedDate = LocalDate.of(year, month, day);
        return consultationControl.getAppointmentCancellationReport(period, selectedDate);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
