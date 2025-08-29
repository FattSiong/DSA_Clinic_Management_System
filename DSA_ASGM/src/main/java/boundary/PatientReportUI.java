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
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);

        String reportContent = generateReportContent(reportType, patientControl, queueControl);
        reportArea.setText(reportContent);
        reportArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(reportArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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
        int totalPatients = 0;
        int maleCount = 0, femaleCount = 0;
        int childCount = 0, teenCount = 0, adultCount = 0, seniorCount = 0;
        double totalAge = 0;

        final String[] stateNames = {
            "Johor", "Kedah", "Kelantan", "Melaka", "Negeri Sembilan",
            "Pahang", "Pulau Pinang", "Perak", "Perlis", "Selangor",
            "Terengganu", "Sabah", "Sarawak", "W.P. Kuala Lumpur",
            "W.P. Labuan", "W.P. Putrajaya", "Unknown/Foreign"
        };

        int[] statePatientCounts = new int[stateNames.length];

        Iterator<Patient> it = patientControl.getPatientList().getIterator();
        while (it.hasNext()) {
            Patient p = it.next();
            totalPatients++;
            totalAge += p.getAge();

            // Gender
            if ("Male".equalsIgnoreCase(p.getGender())) {
                maleCount++;
            } else {
                femaleCount++;
            }

            // Age groups
            if (p.getAge() <= 12) {
                childCount++;
            } else if (p.getAge() <= 18) {
                teenCount++;
            } else if (p.getAge() <= 59) {
                adultCount++;
            } else {
                seniorCount++;
            }

            // State
            String ic = p.getIcNumber();
            if (ic != null && ic.length() >= 9 && ic.charAt(6) == '-') {
                try {
                    String stateCode = ic.substring(7, 9);
                    int stateIndex = getStateIndexFromCode(stateCode);
                    statePatientCounts[stateIndex]++;
                } catch (NumberFormatException e) {
                    // Handle cases where characters 7-8 are not digits if necessary
                    statePatientCounts[stateNames.length - 1]++; // Count as Unknown
                }
            } else {
                statePatientCounts[stateNames.length - 1]++; // Count as Unknown
            }
        }

        // Calculate percentages
        double malePercent = calculatePercentage(maleCount, totalPatients);
        double femalePercent = calculatePercentage(femaleCount, totalPatients);

        double childPercent = calculatePercentage(childCount, totalPatients);
        double teenPercent = calculatePercentage(teenCount, totalPatients);
        double adultPercent = calculatePercentage(adultCount, totalPatients);
        double seniorPercent = calculatePercentage(seniorCount, totalPatients);

        // Finding Most and Fewest Patients States
        int maxCount = 0;
        int minCount = Integer.MAX_VALUE;

        for (int i = 0; i < statePatientCounts.length - 1; i++) {
            int currentCount = statePatientCounts[i];
            if (currentCount > 0) {
                if (currentCount > maxCount) {
                    maxCount = currentCount;
                }
                if (currentCount < minCount) {
                    minCount = currentCount;
                }
            }
        }

        StringBuilder mostStatesBuilder = new StringBuilder();
        StringBuilder fewestStatesBuilder = new StringBuilder();

        if (maxCount > 0) {
            for (int i = 0; i < statePatientCounts.length - 1; i++) {
                // Check for states matching the max count
                if (statePatientCounts[i] == maxCount) {
                    if (mostStatesBuilder.length() > 0) {
                        mostStatesBuilder.append(", ");
                    }
                    mostStatesBuilder.append(stateNames[i]);
                }

                // Check for states matching the min count
                if (statePatientCounts[i] == minCount) {
                    if (fewestStatesBuilder.length() > 0) {
                        fewestStatesBuilder.append(", ");
                    }
                    fewestStatesBuilder.append(stateNames[i]);
                }
            }
        }

        String mostPatientsStates = mostStatesBuilder.length() > 0 ? mostStatesBuilder.toString() : "N/A";
        String fewestPatientsStates = fewestStatesBuilder.length() > 0 ? fewestStatesBuilder.toString() : "N/A";

        if (minCount == Integer.MAX_VALUE) {
            minCount = 0;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append(String.format("%43s\n", "PATIENT DEMOGRAPHICS REPORT"));
        sb.append(String.format("%44s\n\n", "-----------------------------"));
        sb.append(String.format("%s\n", "Generated on: " + LocalDate.now()));
        sb.append("************************************************************\n");

        // Gender distribution
        sb.append("____________________________________\n");
        sb.append("GENDER DISTRIBUTION\n");
        sb.append("____________________________________\n");
        sb.append(String.format("%-18s | %-5s | %s\n", "Category", "Count", "Percent"));
        sb.append("------------------------------------\n");
        sb.append(String.format("%-18s | %5d | %6.1f%%\n", "Male", maleCount, malePercent));
        sb.append(String.format("%-18s | %5d | %6.1f%%\n", "Female", femaleCount, femalePercent));
        sb.append("____________________________________\n\n************************************\n");

        // Age group distribution
        sb.append("____________________________________\n");
        sb.append("AGE GROUP DISTRIBUTION\n");
        sb.append("____________________________________\n");
        sb.append(String.format("%-18s | %-5s | %s\n", "Category", "Count", "Percent"));
        sb.append("------------------------------------\n");
        sb.append(String.format("%-18s | %-5d | %6.1f%%\n", "Children (0-12)", childCount, childPercent));
        sb.append(String.format("%-18s | %-5d | %6.1f%%\n", "Teens (13-18)", teenCount, teenPercent));
        sb.append(String.format("%-18s | %-5d | %6.1f%%\n", "Adults (19-59)", adultCount, adultPercent));
        sb.append(String.format("%-18s | %-5d | %6.1f%%\n", "Seniors (60+)", seniorCount, seniorPercent));
        sb.append("____________________________________\n\n************************************\n");

        // State of birth distribution
        sb.append("____________________________________\n");
        sb.append("STATE OF BIRTH DISTRIBUTION\n");
        sb.append("____________________________________\n");
        sb.append(String.format("%-18s | %-5s | %s\n", "Category", "Count", "Percent"));
        sb.append("------------------------------------\n");

        boolean hasStateData = false;
        for (int i = 0; i < stateNames.length; i++) {
            if (statePatientCounts[i] > 0) {
                hasStateData = true;
                String state = stateNames[i];
                int count = statePatientCounts[i];
                double percent = calculatePercentage(count, totalPatients);
                sb.append(String.format("%-18s | %5d | %6.1f%%\n", state, count, percent));
            }
        }
        if (!hasStateData) {
            sb.append(String.format("%-30s\n", "No state data available."));
        }
        sb.append("____________________________________\n\n");

        // Summary
        sb.append("OVERALL SUMMARY\n");
        sb.append("---------------\n");
        sb.append(String.format("%-25s: %d\n", "Total Patients", totalPatients));
        sb.append(String.format("%-25s: %.1f\n", "Average Patient Age", (totalPatients > 0 ? totalAge / totalPatients : 0)));
        sb.append(String.format("%-25s: %s (%d)\n", "Most Patients State", mostPatientsStates, maxCount));
        sb.append(String.format("%-25s: %s (%d)\n", "Fewest Patients State", fewestPatientsStates, minCount));

        sb.append("\n************************************************************\n");
        sb.append(String.format("%38s\n", "END OF THE REPORT"));
        sb.append("============================================================\n");

        return sb.toString();
    }

    private String generateClinicPeakHoursReport(QueueControl queueControl) {
        int[] hourlyCounts = new int[24];
        int totalEntries = 0;
        Iterator<Queue> it = queueControl.getQueueList().getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            if (q.getEntryTime().toLocalDate().equals(LocalDate.now())) {
                int hour = q.getEntryTime().getHour();
                if (hour >= 0 && hour < 24) {
                    hourlyCounts[hour]++;
                    totalEntries++;
                }
            }
        }

        int maxCount = 0;
        int minCount = Integer.MAX_VALUE;
        int activeHours = 0; // Count hours that had at least one patient

        // Find min, max, and active hours across the full day.
        for (int currentCount : hourlyCounts) {
            if (currentCount > 0) {
                activeHours++;
                if (currentCount > maxCount) {
                    maxCount = currentCount;
                }
                if (currentCount < minCount) {
                    minCount = currentCount;
                }
            }
        }

        StringBuilder peakHours = new StringBuilder();
        StringBuilder lullHours = new StringBuilder();

        if (maxCount > 0) {
            for (int i = 0; i < hourlyCounts.length; i++) {
                if (hourlyCounts[i] == maxCount) {
                    if (peakHours.length() > 0) {
                        peakHours.append(", ");
                    }
                    peakHours.append(String.format("%02d:00", i));
                }
                if (minCount != Integer.MAX_VALUE && hourlyCounts[i] == minCount) {
                    if (lullHours.length() > 0) {
                        lullHours.append(", ");
                    }
                    lullHours.append(String.format("%02d:00", i));
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append(String.format("%46s\n", "CLINIC PEAK HOURS REPORT (TODAY)"));
        sb.append(String.format("%47s\n\n", "----------------------------------"));
        sb.append(String.format("%s\n", "Generated on: " + LocalDate.now()));
        sb.append("************************************************************\n");

        sb.append("____________________________________________________________\n");
        sb.append("HOURLY PATIENT TRAFFIC ANALYSIS\n");
        sb.append("____________________________________________________________\n");
        sb.append(String.format("%-18s | %-5s | %s\n", "Time Slot", "Count", "Traffic Volume"));
        sb.append("------------------------------------------------------------\n");

        final int MAX_ASTERISKS = 30;
        for (int i = 0; i < hourlyCounts.length; i++) {
            if (hourlyCounts[i] > 0) { // Only show hours that had patient
                String timeSlot = String.format("%02d:00 - %02d:59", i, i);
                int barLength = Math.min(hourlyCounts[i], MAX_ASTERISKS);
                if (barLength == 0 && hourlyCounts[i] > 0) {
                    barLength = 1;
                }
                String bar = generateAsteriskBar(barLength);
                sb.append(String.format("%-18s | %-5d | %s\n", timeSlot, hourlyCounts[i], bar));
            }
        }
        sb.append("____________________________________________________________\n\n");

        sb.append("SUMMARY & INSIGHTS\n");
        sb.append("------------------\n");
        sb.append(String.format("%-31s: %d\n", "Total Patients Today", totalEntries));

        if (totalEntries > 0) {
            double averageTraffic = (activeHours > 0) ? (double) totalEntries / activeHours : 0;
            sb.append(String.format("%-31s: %.1f patients/hour\n", "Average Traffic (Per Active Hr)", averageTraffic));
            sb.append(String.format("%-31s: %s\n", "Peak Hour(s) with " + maxCount + " patient(s)", peakHours.toString()));
            if (minCount != Integer.MAX_VALUE && minCount != maxCount) {
                sb.append(String.format("%-31s: %s\n", "Lull Hour(s) with " + minCount + " patient(s)", lullHours.toString()));
            }
        } else {
            sb.append("No patient queue entries found for today.\n");
        }

        sb.append("\n************************************************************\n");
        sb.append(String.format("%38s\n", "END OF THE REPORT"));
        sb.append("============================================================\n");

        return sb.toString();
    }

    /**
     * Helper method to get the array index for a state from an IC's
     * place-of-birth code.
     *
     */
    private int getStateIndexFromCode(String code) {
        switch (code) {
            case "01": case "21": case "22": case "23": case "24": return 0; // Johor
            case "02": case "25": case "26": case "27": return 1; // Kedah
            case "03": case "28": case "29": return 2; // Kelantan
            case "04": case "30": return 3; // Melaka
            case "05": case "31": case "59": return 4; // Negeri Sembilan
            case "06": case "32": case "33": return 5; // Pahang
            case "07": case "34": case "35": return 6; // Pulau Pinang
            case "08": case "36": case "37": case "38": case "39": return 7; // Perak
            case "09": case "40": return 8; // Perlis
            case "10": case "41": case "42": case "43": case "44": return 9; // Selangor
            case "11": case "45": case "46": return 10; // Terengganu
            case "12": case "47": case "48": case "49": return 11; // Sabah
            case "13": case "50": case "51": case "52": case "53": return 12; // Sarawak
            case "14": case "54": case "55": case "56": case "57": return 13; // W.P. Kuala Lumpur
            case "15": case "58": return 14; // W.P. Labuan
            case "16": return 15; // W.P. Putrajaya
            default: return 16; // Unknown / Foreign
        }
    }

    /**
     * Calculates the percentage of a count out of a total. Handles
     * division-by-zero errors safely.
     *
     * @param count The part or subgroup count.
     * @param total The total count.
     * @return The calculated percentage as a double.
     */
    private double calculatePercentage(int count, int total) {
        if (total == 0) {
            return 0.0; // Avoid division by zero
        }
        // Cast to double to ensure floating-point division
        return ((double) count / total) * 100;
    }

    /**
     * Helper method to generate a string of asterisks for the bar chart. This
     * demonstrates good practice by separating logic.
     *
     * @param length The number of asterisks to generate.
     * @return A string of asterisks.
     */
    private String generateAsteriskBar(int length) {
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < length; i++) {
            bar.append("*");
        }
        return bar.toString();
    }
}
