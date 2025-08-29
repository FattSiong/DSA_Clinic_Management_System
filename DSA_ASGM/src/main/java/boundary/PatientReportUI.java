/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package boundary;

import control.PatientControl;
import control.QueueControl;
import entity.Patient;
import entity.Queue;
import java.awt.BorderLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author hangg
 */
public class PatientReportUI extends JFrame {

    public PatientReportUI(String reportType, PatientControl patientControl, QueueControl queueControl) {
        setTitle(reportType);
        setSize(700, 600); // Slightly wider for better text flow
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // --- Use a main panel with a border for padding ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add 10px margin

        JTextArea reportArea = new JTextArea();
        // --- UPDATED FONT ---
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Increased font size
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);

        // --- Generate and Set the Report Content ---
        String reportContent = generateReportContent(reportType, patientControl, queueControl);
        reportArea.setText(reportContent);
        reportArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(reportArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add the main panel to the frame's content pane
        getContentPane().add(mainPanel);
    }

    private String generateReportContent(String reportType, PatientControl patientControl, QueueControl queueControl) {
        switch (reportType) {
            case "PATIENT DEMOGRAPHICS REPORT":
                return generatePatientDemographicsReport(patientControl);
            case "OVERALL CLINIC PEAK HOURS REPORT":
                return generateClinicPeakHoursReport(queueControl);
            default:
                return "Error: Invalid report type selected.";
        }
    }

    private String generatePatientDemographicsReport(PatientControl patientControl) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\tPATIENT DEMOGRAPHICS REPORT\n");
        sb.append("\t\t  Generated on: ").append(LocalDate.now()).append("\n");
        sb.append("----------------------------------------------------------\n\n");

        int totalPatients = 0;
        int maleCount = 0;
        int femaleCount = 0;
        int childCount = 0, teenCount = 0, adultCount = 0, seniorCount = 0;
        double totalAge = 0;

        Iterator<Patient> it = patientControl.getPatientList().getIterator();
        while (it.hasNext()) {
            Patient p = it.next();
            totalPatients++;
            totalAge += p.getAge();
            if ("Male".equalsIgnoreCase(p.getGender())) {
                maleCount++;
            } else {
                femaleCount++;
            }
            if (p.getAge() <= 12) {
                childCount++;
            } else if (p.getAge() <= 18) {
                teenCount++;
            } else if (p.getAge() <= 59) {
                adultCount++;
            } else {
                seniorCount++;
            }
        }

        sb.append("OVERALL SUMMARY\n");
        sb.append("---------------------\n");
        sb.append(String.format("%-25s: %d\n", "Total Patients", totalPatients));
        sb.append(String.format("%-25s: %.1f\n\n", "Average Patient Age", (totalPatients > 0 ? totalAge / totalPatients : 0)));
        sb.append("GENDER DISTRIBUTION\n");
        sb.append("---------------------\n");
        sb.append(String.format("%-25s: %d\n", "Male", maleCount));
        sb.append(String.format("%-25s: %d\n\n", "Female", femaleCount));
        sb.append("AGE GROUP DISTRIBUTION\n");
        sb.append("---------------------\n");
        sb.append(String.format("%-25s: %d\n", "Children (0-12)", childCount));
        sb.append(String.format("%-25s: %d\n", "Teens (13-18)", teenCount));
        sb.append(String.format("%-25s: %d\n", "Adults (19-59)", adultCount));
        sb.append(String.format("%-25s: %d\n", "Seniors (60+)", seniorCount));
        sb.append("\n----------------------------------------------------------\n");

        return sb.toString();
    }

    /**
     * UPDATED REPORT: Now correctly filters for today's data using
     * LocalDateTime.
     */
    private String generateClinicPeakHoursReport(QueueControl queueControl) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\tCLINIC PEAK HOURS REPORT (TODAY)\n");
        sb.append("\t\t      Generated on: ").append(LocalDate.now()).append("\n");
        sb.append("----------------------------------------------------------\n\n");

        int[] hourlyCounts = new int[24];
        int totalEntries = 0;

        Iterator<Queue> it = queueControl.getQueueList().getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            // --- KEY CHANGE HERE ---
            // Check if the queue entry's date is today's date.
            if (q.getEntryTime().toLocalDate().equals(LocalDate.now())) {
                int hour = q.getEntryTime().getHour(); // getHour() returns 0-23
                if (hour >= 0 && hour < 24) {
                    hourlyCounts[hour]++;
                    totalEntries++;
                }
            }
        }

        sb.append("PATIENTS PER HOUR TODAY\n");
        sb.append("--------------------------\n");
        int peakHour = -1;
        int maxCount = 0;
        for (int i = 0; i < hourlyCounts.length; i++) {
            if (hourlyCounts[i] > 0) { // Only show hours with activity
                String timeSlot = String.format("%02d:00 - %02d:59", i, i);
                sb.append(String.format("%-20s: %d patient(s)\n", timeSlot, hourlyCounts[i]));
            }
            if (hourlyCounts[i] > maxCount) {
                maxCount = hourlyCounts[i];
                peakHour = i;
            }
        }

        sb.append("\nSUMMARY\n");
        sb.append("--------------------------\n");
        if (totalEntries > 0 && peakHour != -1) {
            String peakTimeSlot = String.format("%02d:00 - %02d:59", peakHour, peakHour);
            sb.append("Peak Hour Today was ").append(peakTimeSlot).append(" with ").append(maxCount).append(" patient(s).\n");
        } else {
            sb.append("No patient queue entries found for today.\n");
        }

        sb.append("\n----------------------------------------------------------\n");
        return sb.toString();
    }
}
