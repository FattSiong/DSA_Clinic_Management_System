package control;

import adt.ListInterface;
import boundary.DoctorManagementUI;
import boundary.DoctorUI;
import entity.Appointment;
import entity.Doctor;
import utility.ReadWriteFile;
import java.time.LocalDate;

public class DoctorControl {
    private ListInterface<Doctor> doctorList;
    private ListInterface<Appointment> appointmentList;
    private ReadWriteFile rwf = new ReadWriteFile();
    private final String DOCTOR_FILE = "src/main/java/Data File/doctors";
    
    private DoctorManagementUI view;
    private DoctorUI parentUI;

    public DoctorControl(DoctorUI parentUI, ListInterface<Doctor> doctorList, ListInterface<Appointment> appointmentList) {
        this.parentUI = parentUI;
        this.doctorList = doctorList;
        this.appointmentList = appointmentList;
        
        this.view = new DoctorManagementUI(this);
        refreshView();
        view.setVisible(true);
    }

    private void refreshView() {
        view.displayDoctors(doctorList);
        view.setAddButtonEnabled(doctorList.getLength() < 4);
    }

    public void addDoctor() {
        if (doctorList.getLength() >= 4) {
            view.showMessage("Maximum of 4 doctors allowed!", "Limit Reached", 0);
            return;
        }
        Doctor newDoctor = view.showDoctorInputDialog(null);
        if (newDoctor != null) {
            doctorList.add(newDoctor);
            rwf.writeDoctorsToFile(DOCTOR_FILE, doctorList);
            refreshView();
            view.showMessage("Doctor added successfully!", "Success", 1);
        }
    }

    public void updateDoctor() {
        String doctorId = view.getSelectedDoctorId();
        if (doctorId == null) return;
        
        Doctor doctorToUpdate = findDoctorById(doctorId);
        if (doctorToUpdate == null) return;

        Doctor updatedDoctor = view.showDoctorInputDialog(doctorToUpdate);
        if (updatedDoctor != null) {
            doctorToUpdate.setName(updatedDoctor.getName());
            doctorToUpdate.setContactNumber(updatedDoctor.getContactNumber());
            doctorToUpdate.setRoomNumber(updatedDoctor.getRoomNumber());
            rwf.writeDoctorsToFile(DOCTOR_FILE, doctorList);
            refreshView();
            view.showMessage("Doctor updated successfully!", "Success", 1);
        }
    }
    
    public void deleteDoctor() {
        String doctorId = view.getSelectedDoctorId();
        if (doctorId == null) return;
        
        for (int i = 1; i <= doctorList.getLength(); i++) {
            if (doctorList.getEntry(i).getDoctorID().equals(doctorId)) {
                doctorList.remove(i);
                rwf.writeDoctorsToFile(DOCTOR_FILE, doctorList);
                refreshView();
                view.showMessage("Doctor deleted successfully!", "Success", 1);
                return;
            }
        }
    }

    public void generatePatientLoadReport() {
        if (doctorList.isEmpty()) {
            view.showMessage("No doctors available to generate a report.", "Error", 0);
            return;
        }

        String[] reportTypes = {"Weekly Patient Load", "Monthly Patient Load"};
        String selectedType = (String) javax.swing.JOptionPane.showInputDialog(
                null, 
                "Select the report type:",
                "Generate Patient Load Report", 
                javax.swing.JOptionPane.QUESTION_MESSAGE, 
                null, 
                reportTypes, 
                reportTypes[0]);

        if (selectedType == null) return;

        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;
        String reportTitle;

        if (selectedType.equals("Weekly Patient Load")) {
            startDate = today.with(java.time.DayOfWeek.MONDAY);
            endDate = today.with(java.time.DayOfWeek.SUNDAY);
            reportTitle = "Weekly Patient Load Report (" + startDate + " to " + endDate + ")";
        } else {
            startDate = today.withDayOfMonth(1);
            endDate = today.withDayOfMonth(today.lengthOfMonth());
            reportTitle = "Monthly Patient Load for " + today.getMonth().toString() + " " + today.getYear();
        }

        java.util.Map<String, Integer> patientCountMap = new java.util.HashMap<>();
        for(int i = 1; i <= doctorList.getLength(); i++){
            patientCountMap.put(doctorList.getEntry(i).getDoctorID(), 0);
        }

        for (int i = 1; i <= appointmentList.getLength(); i++) {
            Appointment app = appointmentList.getEntry(i);
            LocalDate appDate = app.getAppointmentDate();

            if (!appDate.isBefore(startDate) && !appDate.isAfter(endDate) && !app.getStatus().equals("Canceled")) {
                if (app.getDoctor() != null) {
                    String docID = app.getDoctor().getDoctorID();
                    patientCountMap.put(docID, patientCountMap.getOrDefault(docID, 0) + 1);
                }
            }
        }

        StringBuilder reportContent = new StringBuilder();
        reportContent.append(reportTitle).append("\n");
        reportContent.append("------------------------------------------\n");
        reportContent.append(String.format("%-20s | %s\n", "Doctor Name", "Total Patients Met"));
        reportContent.append("------------------------------------------\n");

        for (int i = 1; i <= doctorList.getLength(); i++) {
            Doctor doctor = doctorList.getEntry(i);
            int count = patientCountMap.getOrDefault(doctor.getDoctorID(), 0);
            reportContent.append(String.format("%-20s | %d patients\n", doctor.getName(), count));
        }
        
        view.showReport(reportContent.toString(), "Patient Load Report");
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
    
    private Doctor findDoctorById(String id) {
        for (int i = 1; i <= doctorList.getLength(); i++) {
            if (doctorList.getEntry(i).getDoctorID().equals(id)) {
                return doctorList.getEntry(i);
            }
        }
        return null;
    }
}