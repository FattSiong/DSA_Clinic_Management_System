package control;

import adt.ListInterface;
import adt.DoublyLinkedList;
import boundary.DoctorUI;
import boundary.ScheduleManagementUI;
import entity.*;
import utility.ReadWriteFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ScheduleControl {
    private ListInterface<Schedule> scheduleList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Appointment> appointmentList;
    private ReadWriteFile rwf = new ReadWriteFile();
    private final String SCHEDULE_FILE = "src/main/java/Data File/schedules";

    private ScheduleManagementUI view;
    private DoctorUI parentUI;
    
    public ScheduleControl(DoctorUI parent, ListInterface<Schedule> scheduleList, ListInterface<Doctor> doctorList, ListInterface<Appointment> appointmentList) {
        this.parentUI = parent;
        this.scheduleList = scheduleList;
        this.doctorList = doctorList;
        this.appointmentList = appointmentList;

        if (this.scheduleList.isEmpty()) {
            rwf.readSchedulesFromFile(SCHEDULE_FILE, this.scheduleList, this.doctorList);
        }

        this.view = new ScheduleManagementUI(this);
        loadInitialSchedules();
        view.setVisible(true);
    }

    public void loadInitialSchedules() {
        view.clearSearch();
        view.displaySchedules(scheduleList);
    }

    public void performSearch(String searchText, String searchBy) {
        searchText = searchText.trim();
        if (searchText.isEmpty()) {
            view.showMessage("Please enter a search term.", "Search Error", 0);
            return;
        }

        ListInterface<Schedule> filteredList = new DoublyLinkedList<>();
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            Schedule s = scheduleList.getEntry(i);
            if (s.getDoctor() == null) continue;

            switch (searchBy) {
                case "By Doctor":
                    if (s.getDoctor().getName().toLowerCase().contains(searchText.toLowerCase())) filteredList.add(s);
                    break;
                case "By Date (YYYY-MM-DD)":
                    try {
                        if (s.getDutyDate().equals(LocalDate.parse(searchText))) filteredList.add(s);
                    } catch (Exception ex) { view.showMessage("Invalid date format. Use YYYY-MM-DD.", "Error", 0); return; }
                    break;
                case "By Availability":
                    if (s.getAvailability().equalsIgnoreCase(searchText)) filteredList.add(s);
                    break;
                case "By Time (HH:mm)":
                     try {
                        LocalTime searchTime = LocalTime.parse(searchText);
                        if (!searchTime.isBefore(s.getStartTime()) && searchTime.isBefore(s.getEndTime())) filteredList.add(s);
                    } catch (Exception ex) { view.showMessage("Invalid time format. Use HH:mm.", "Error", 0); return; }
                    break;
            }
        }
        view.displaySchedules(filteredList);
    }
    
    public void addSchedule() {
        Schedule newSchedule = view.showScheduleInputDialog(null, doctorList);
        if (newSchedule != null) {
            scheduleList.add(newSchedule);
            rwf.writeSchedulesToFile(SCHEDULE_FILE, scheduleList);
            loadInitialSchedules();
            view.showMessage("Schedule added successfully.", "Success", 1);
        }
    }
    
    public void updateSchedule() {
        String scheduleId = view.getSelectedScheduleId();
        if (scheduleId == null) return;
        
        Schedule scheduleToUpdate = findScheduleById(scheduleId);
        if (scheduleToUpdate == null) return;

        Schedule updatedInfo = view.showScheduleInputDialog(scheduleToUpdate, doctorList);
        if (updatedInfo != null) {
            scheduleToUpdate.setDutyDate(updatedInfo.getDutyDate());
            scheduleToUpdate.setStartTime(updatedInfo.getStartTime());
            scheduleToUpdate.setEndTime(updatedInfo.getEndTime());
            rwf.writeSchedulesToFile(SCHEDULE_FILE, scheduleList);
            loadInitialSchedules();
            view.showMessage("Schedule updated successfully.", "Success", 1);
        }
    }

    public void deleteSchedule() {
        String scheduleId = view.getSelectedScheduleId();
        if (scheduleId == null) return;
        
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            if (scheduleList.getEntry(i).getScheduleID().equals(scheduleId)) {
                scheduleList.remove(i);
                rwf.writeSchedulesToFile(SCHEDULE_FILE, scheduleList);
                loadInitialSchedules();
                view.showMessage("Schedule deleted successfully.", "Success", 1);
                return;
            }
        }
    }

    public void refreshDoctorAvailability() {
        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            Schedule s = scheduleList.getEntry(i);
            String oldStatus = s.getAvailability();
            String newStatus = oldStatus; 

            LocalDateTime scheduleStart = LocalDateTime.of(s.getDutyDate(), s.getStartTime());
            LocalDateTime scheduleEnd = LocalDateTime.of(s.getDutyDate(), s.getEndTime());

            if (now.isAfter(scheduleEnd) || now.isBefore(scheduleStart)) {
                newStatus = "Off Duty";
            } else {
                newStatus = "Available";
                for (int j = 1; j <= appointmentList.getLength(); j++) {
                    Appointment a = appointmentList.getEntry(j);
                    if (a.getDoctor().getDoctorID().equals(s.getDoctor().getDoctorID()) && a.getAppointmentDate().equals(s.getDutyDate())) {
                        LocalDateTime apptStart = LocalDateTime.of(a.getAppointmentDate(), a.getAppointmentTime());
                        LocalDateTime apptEnd = apptStart.plusMinutes(30);
                        if (!now.isBefore(apptStart) && now.isBefore(apptEnd)) {
                            newStatus = "Busy";
                            break;
                        }
                    }
                }
            }
            if (!oldStatus.equals(newStatus)) {
                s.setAvailability(newStatus);
                changed = true;
            }
        }
        if (changed) {
            rwf.writeSchedulesToFile(SCHEDULE_FILE, scheduleList);
            view.displaySchedules(scheduleList);
        }
    }
    
    public void generateWorkloadReport() {
        String[] reportTypes = {"Weekly Workload", "Monthly Workload"};
        String selectedType = (String) javax.swing.JOptionPane.showInputDialog(null, "Select the report type:", "Generate Workload Report", 3, null, reportTypes, reportTypes[0]);
        if (selectedType == null) return;

        LocalDate today = LocalDate.now();
        LocalDate startDate, endDate;
        String reportTitle;

        if (selectedType.equals("Weekly Workload")) {
            startDate = today.with(java.time.DayOfWeek.MONDAY);
            endDate = today.with(java.time.DayOfWeek.SUNDAY);
            reportTitle = "Weekly Workload Report (" + startDate + " to " + endDate + ")";
        } else {
            startDate = today.withDayOfMonth(1);
            endDate = today.withDayOfMonth(today.lengthOfMonth());
            reportTitle = "Monthly Workload Report for " + today.getMonth().toString() + " " + today.getYear();
        }

        java.util.Map<Doctor, Long> workloadMap = new java.util.HashMap<>();
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            Schedule s = scheduleList.getEntry(i);
            LocalDate dutyDate = s.getDutyDate();
            if (!dutyDate.isBefore(startDate) && !dutyDate.isAfter(endDate) && s.getDoctor() != null) {
                long duration = java.time.Duration.between(s.getStartTime(), s.getEndTime()).toMinutes();
                workloadMap.put(s.getDoctor(), workloadMap.getOrDefault(s.getDoctor(), 0L) + duration);
            }
        }

        StringBuilder reportContent = new StringBuilder();
        reportContent.append(reportTitle).append("\n----------------------------------------\n");
        reportContent.append(String.format("%-20s | %s\n", "Doctor Name", "Total Scheduled Hours"));
        reportContent.append("----------------------------------------\n");
        
        if (workloadMap.isEmpty()) {
            reportContent.append("No schedules found for this period.");
        } else {
            for (java.util.Map.Entry<Doctor, Long> entry : workloadMap.entrySet()) {
                double hours = entry.getValue() / 60.0;
                reportContent.append(String.format("%-20s | %.2f hours\n", entry.getKey().getName(), hours));
            }
        }
        view.showReport(reportContent.toString(), "Workload Report");
    }
    
    public void close() {
        view.dispose();
        parentUI.setVisible(true);
    }

    public void closeToMain() {
        view.dispose();
        parentUI.dispose();
        parentUI.getMainUI().setVisible(true);
    }

    private Schedule findScheduleById(String id) {
        for (int i = 1; i <= scheduleList.getLength(); i++) {
            if (scheduleList.getEntry(i).getScheduleID().equals(id)) {
                return scheduleList.getEntry(i);
            }
        }
        return null;
    }
}