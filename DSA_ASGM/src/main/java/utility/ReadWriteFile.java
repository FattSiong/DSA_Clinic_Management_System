/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Appointment;
import entity.Consultation;
import entity.Doctor;
import entity.Medicine;
import entity.Patient;
import entity.Schedule;
import entity.Treatment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;

/**
 *
 * @author tiong
 */
public class ReadWriteFile {

    // File paths (constants for easy management)
    private static final String PATIENTS_FILE = "patients.txt";
    private static final String DOCTORS_FILE = "doctors.txt";
    private static final String MEDICINES_FILE = "medicines.txt";
    private static final String SCHEDULES_FILE = "schedules.txt";
    private static final String APPOINTMENTS_FILE = "appointments.txt";
    private static final String CONSULTATIONS_FILE = "consultations.txt";
    private static final String TREATMENTS_FILE = "treatments.txt";

    // --- Master Load and Save Methods ---
    /**
     * Loads all data from the respective text files. This should be called when
     * the application starts. Note: The order of loading is important due to
     * dependencies.
     *
     * @param patientList
     * @param doctorList
     * @param medicineList
     * @param scheduleList
     * @param appointmentList
     * @param consultationList
     * @param treatmentList
     */
    public void loadAllData(
            ListInterface<Patient> patientList,
            ListInterface<Doctor> doctorList,
            ListInterface<Medicine> medicineList,
            ListInterface<Schedule> scheduleList,
            ListInterface<Appointment> appointmentList,
            ListInterface<Consultation> consultationList,
            ListInterface<Treatment> treatmentList) {

        // Load independent entities first
        readPatientsFromFile(PATIENTS_FILE, patientList);
        readDoctorsFromFile(DOCTORS_FILE, doctorList);
        readMedicinesFromFile(MEDICINES_FILE, medicineList);

        // Load dependent entities
        readSchedulesFromFile(SCHEDULES_FILE, scheduleList, doctorList);
        readAppointmentsFromFile(APPOINTMENTS_FILE, appointmentList, patientList, doctorList);
        readConsultationsFromFile(CONSULTATIONS_FILE, consultationList, appointmentList);
        readTreatmentsFromFile(TREATMENTS_FILE, treatmentList, consultationList, medicineList);
    }

    /**
     * Saves all current data back to the respective text files. This should be
     * called when the application is about to exit.
     * @param patientList
     * @param doctorList
     * @param medicineList
     * @param scheduleList
     * @param appointmentList
     * @param consultationList
     * @param treatmentList
     */
    public void saveAllData(
            ListInterface<Patient> patientList,
            ListInterface<Doctor> doctorList,
            ListInterface<Medicine> medicineList,
            ListInterface<Schedule> scheduleList,
            ListInterface<Appointment> appointmentList,
            ListInterface<Consultation> consultationList,
            ListInterface<Treatment> treatmentList) {

        writePatientsToFile(PATIENTS_FILE, patientList);
        writeDoctorsToFile(DOCTORS_FILE, doctorList);
        writeMedicinesToFile(MEDICINES_FILE, medicineList);
        writeSchedulesToFile(SCHEDULES_FILE, scheduleList);
        writeAppointmentsToFile(APPOINTMENTS_FILE, appointmentList);
        writeConsultationsToFile(CONSULTATIONS_FILE, consultationList);
        writeTreatmentsToFile(TREATMENTS_FILE, treatmentList);
    }

    // --- READ Methods ---
    public void readPatientsFromFile(String filename, ListInterface<Patient> patientList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                patientList.add(new Patient(data[0], data[1], data[2], data[3], data[4], data[5]));
            }
        } catch (IOException e) {
            System.err.println("Error reading patients file: " + e.getMessage());
        }
    }

    public void readDoctorsFromFile(String filename, ListInterface<Doctor> doctorList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                doctorList.add(new Doctor(data[0], data[1], data[2], data[3], data[4]));
            }
        } catch (IOException e) {
            System.err.println("Error reading doctors file: " + e.getMessage());
        }
    }

    public void readMedicinesFromFile(String filename, ListInterface<Medicine> medicineList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                medicineList.add(new Medicine(data[0], data[1], data[2], Integer.parseInt(data[3]), Double.parseDouble(data[4])));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading medicines file: " + e.getMessage());
        }
    }

    public void readSchedulesFromFile(String filename, ListInterface<Schedule> scheduleList, ListInterface<Doctor> doctorList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                Doctor doctor = findDoctorById(doctorList, data[1]); // Find doctor by ID
                if (doctor != null) {
                    scheduleList.add(new Schedule(data[0], doctor, LocalDate.parse(data[2]), LocalTime.parse(data[3]), LocalTime.parse(data[4]), data[5]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading schedules file: " + e.getMessage());
        }
    }

    public void readAppointmentsFromFile(String filename, ListInterface<Appointment> appointmentList, ListInterface<Patient> patientList, ListInterface<Doctor> doctorList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                Patient patient = findPatientById(patientList, data[1]);
                Doctor doctor = findDoctorById(doctorList, data[2]);
                if (patient != null && doctor != null) {
                    appointmentList.add(new Appointment(data[0], patient, doctor, LocalDate.parse(data[3]), LocalTime.parse(data[4]), data[5]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading appointments file: " + e.getMessage());
        }
    }

    public void readConsultationsFromFile(String filename, ListInterface<Consultation> consultationList, ListInterface<Appointment> appointmentList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                Appointment appointment = findAppointmentById(appointmentList, data[1]);
                if (appointment != null) {
                    consultationList.add(new Consultation(data[0], appointment, data[2], data[3], LocalDate.parse(data[4])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading consultations file: " + e.getMessage());
        }
    }

    public void readTreatmentsFromFile(String filename, ListInterface<Treatment> treatmentList, ListInterface<Consultation> consultationList, ListInterface<Medicine> medicineList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                Consultation consultation = findConsultationById(consultationList, data[1]);
                if (consultation != null) {
                    ListInterface<Medicine> prescribed = new DoublyLinkedList<>();
                    String[] medicineIds = data[3].split(",");
                    for (String medId : medicineIds) {
                        Medicine medicine = findMedicineById(medicineList, medId);
                        if (medicine != null) {
                            prescribed.add(medicine);
                        }
                    }
                    treatmentList.add(new Treatment(data[0], consultation, data[2], prescribed));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading treatments file: " + e.getMessage());
        }
    }

    // --- WRITE Methods ---
    public void writePatientsToFile(String filename, ListInterface<Patient> patientList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Iterator<Patient> it = patientList.getIterator();
            while (it.hasNext()) {
                Patient p = it.next();
                String line = String.join("|", p.getPatientID(), p.getName(), p.getIcNumber(), p.getContactNumber(), p.getEmail(), p.getMedicalHistory());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing patients file: " + e.getMessage());
        }
    }

    public void writeDoctorsToFile(String filename, ListInterface<Doctor> doctorList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Iterator<Doctor> it = doctorList.getIterator();
            while (it.hasNext()) {
                Doctor d = it.next();
                String line = String.join("|", d.getDoctorID(), d.getName(), d.getSpecialization(), d.getContactNumber(), d.getRoomNumber());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing doctors file: " + e.getMessage());
        }
    }

    public void writeMedicinesToFile(String filename, ListInterface<Medicine> medicineList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Iterator<Medicine> it = medicineList.getIterator();
            while (it.hasNext()) {
                Medicine m = it.next();
                String line = String.join("|", m.getMedicineID(), m.getName(), m.getDescription(), String.valueOf(m.getStockQuantity()), String.valueOf(m.getPrice()));
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing medicines file: " + e.getMessage());
        }
    }

    public void writeSchedulesToFile(String filename, ListInterface<Schedule> scheduleList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Iterator<Schedule> it = scheduleList.getIterator();
            while (it.hasNext()) {
                Schedule s = it.next();
                String line = String.join("|", s.getScheduleID(), s.getDoctor().getDoctorID(), s.getDutyDate().toString(), s.getStartTime().toString(), s.getEndTime().toString(), s.getAvailability());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing schedules file: " + e.getMessage());
        }
    }

    public void writeAppointmentsToFile(String filename, ListInterface<Appointment> appointmentList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Iterator<Appointment> it = appointmentList.getIterator();
            while (it.hasNext()) {
                Appointment a = it.next();
                String line = String.join("|", a.getAppointmentID(), a.getPatient().getPatientID(), a.getDoctor().getDoctorID(), a.getAppointmentDate().toString(), a.getAppointmentTime().toString(), a.getStatus());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing appointments file: " + e.getMessage());
        }
    }

    public void writeConsultationsToFile(String filename, ListInterface<Consultation> consultationList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Iterator<Consultation> it = consultationList.getIterator();
            while (it.hasNext()) {
                Consultation c = it.next();
                String line = String.join("|", c.getConsultationID(), c.getAppointment().getAppointmentID(), c.getDiagnosis(), c.getNotes(), c.getConsultationDate().toString());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing consultations file: " + e.getMessage());
        }
    }

    public void writeTreatmentsToFile(String filename, ListInterface<Treatment> treatmentList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Iterator<Treatment> it = treatmentList.getIterator();
            while (it.hasNext()) {
                Treatment t = it.next();
                StringBuilder medicineIds = new StringBuilder();
                Iterator<Medicine> medIt = t.getPrescribedMedicine().getIterator();
                while (medIt.hasNext()) {
                    medicineIds.append(medIt.next().getMedicineID());
                    if (medIt.hasNext()) {
                        medicineIds.append(",");
                    }
                }
                String line = String.join("|", t.getTreatmentID(), t.getConsultation().getConsultationID(), t.getTreatmentDetails(), medicineIds.toString());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing treatments file: " + e.getMessage());
        }
    }

    // --- Helper Methods to Find Objects by ID (Renamed to avoid erasure clash) ---
    
    private Patient findPatientById(ListInterface<Patient> list, String id) {
        Iterator<Patient> it = list.getIterator();
        while (it.hasNext()) {
            Patient item = it.next();
            if (item.getPatientID().equals(id)) return item;
        }
        return null;
    }

    private Doctor findDoctorById(ListInterface<Doctor> list, String id) {
        Iterator<Doctor> it = list.getIterator();
        while (it.hasNext()) {
            Doctor item = it.next();
            if (item.getDoctorID().equals(id)) return item;
        }
        return null;
    }
    
    private Medicine findMedicineById(ListInterface<Medicine> list, String id) {
        Iterator<Medicine> it = list.getIterator();
        while (it.hasNext()) {
            Medicine item = it.next();
            if (item.getMedicineID().equals(id)) return item;
        }
        return null;
    }
    
    private Appointment findAppointmentById(ListInterface<Appointment> list, String id) {
        Iterator<Appointment> it = list.getIterator();
        while (it.hasNext()) {
            Appointment item = it.next();
            if (item.getAppointmentID().equals(id)) return item;
        }
        return null;
    }
    
    private Consultation findConsultationById(ListInterface<Consultation> list, String id) {
        Iterator<Consultation> it = list.getIterator();
        while (it.hasNext()) {
            Consultation item = it.next();
            if (item.getConsultationID().equals(id)) return item;
        }
        return null;
    }
}
