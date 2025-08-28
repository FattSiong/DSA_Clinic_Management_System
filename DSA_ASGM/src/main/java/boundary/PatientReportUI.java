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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 *
 * @author hangg
 */
public class PatientReportUI extends JFrame {

    public PatientReportUI(String reportType, PatientControl patientControl, QueueControl queueControl) {
        setTitle(reportType); // Use the report type as the window title
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);

        // --- GENERATE AND SET THE REPORT CONTENT ---
        String reportContent = generateReportContent(reportType, patientControl, queueControl);
        reportArea.setText(reportContent);
        reportArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);
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
        sb.append("========================================\n");
        sb.append("       PATIENT DEMOGRAPHICS REPORT\n");
        sb.append("        Generated on: ").append(LocalDate.now()).append("\n");
        sb.append("========================================\n\n");

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

        sb.append("--- Overall Summary ---\n");
        sb.append(String.format("Total Patients: %d\n", totalPatients));
        sb.append(String.format("Average Patient Age: %.1f\n\n", (totalPatients > 0 ? totalAge / totalPatients : 0)));
        sb.append("--- Gender Distribution ---\n");
        sb.append(String.format("Male: %d\n", maleCount));
        sb.append(String.format("Female: %d\n\n", femaleCount));
        sb.append("--- Age Group Distribution ---\n");
        sb.append(String.format("Children (0-12):   %d\n", childCount));
        sb.append(String.format("Teens (13-18):     %d\n", teenCount));
        sb.append(String.format("Adults (19-59):    %d\n", adultCount));
        sb.append(String.format("Seniors (60+):     %d\n", seniorCount));
        sb.append("\n========================================\n");

        return sb.toString();
    }

    private String generateClinicPeakHoursReport(QueueControl queueControl) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("      OVERALL CLINIC PEAK HOURS REPORT\n");
        sb.append("    (Based on all data in the queue file)\n");
        sb.append("========================================\n\n");

        int[] hourlyCounts = new int[24];
        int totalEntries = 0;

        Iterator<Queue> it = queueControl.getQueueList().getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            int hour = q.getEntryTime().getHour();
            if (hour >= 0 && hour < 24) {
                hourlyCounts[hour]++;
                totalEntries++;
            }
        }

        sb.append("--- Total Patients per Hour (All Time) ---\n");
        int peakHour = -1;
        int maxCount = 0;
        for (int i = 0; i < hourlyCounts.length; i++) {
            if (hourlyCounts[i] > 0) {
                String timeSlot = String.format("%02d:00 - %02d:59", i, i);
                sb.append(String.format("%-15s: %d patient(s)\n", timeSlot, hourlyCounts[i]));
            }
            if (hourlyCounts[i] > maxCount) {
                maxCount = hourlyCounts[i];
                peakHour = i;
            }
        }

        sb.append("\n--- Summary ---\n");
        if (totalEntries > 0 && peakHour != -1) {
            String peakTimeSlot = String.format("%02d:00 - %02d:59", peakHour, peakHour);
            sb.append("Busiest Hour Overall: ").append(peakTimeSlot).append(" with ").append(maxCount).append(" patient(s).\n");
        } else {
            sb.append("No patient queue entries found in the file.\n");
        }

        sb.append("\n========================================\n");
        return sb.toString();
    }
}
