/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import java.util.Iterator;
import entity.Patient;
import utility.ReadWriteFile;

/**
 *
 * @author hangg
 */
public class PatientControl {

    private static final String PATIENT_FILE = "src/main/java/Data File/patients";

    private ListInterface<Patient> patientList;
    private ReadWriteFile fileManager;

    public PatientControl() {
        this.patientList = new DoublyLinkedList<>();
        this.fileManager = new ReadWriteFile();
        fileManager.readPatientsFromFile(PATIENT_FILE, this.patientList);
    }

    public PatientControl(ListInterface<Patient> pList) {
        this.patientList = pList;
    }

    public ListInterface<Patient> getPatientList() {
        return patientList;
    }

    public Patient findPatientById(String id) {
        Iterator<Patient> it = patientList.getIterator();
        while (it.hasNext()) {
            Patient p = it.next();
            if (p.getPatientID().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public boolean addPatient(Patient newPatient) {
        if (patientList.contains(newPatient)) {
            return false;
        } else {
            patientList.add(newPatient);
            fileManager.writePatientsToFile(PATIENT_FILE, this.patientList);
            return true;
        }
    }

    public boolean updatePatient(Patient updatedPatient) {
        for (int i = 1; i <= patientList.getLength(); i++) {
            if (patientList.getEntry(i).getPatientID().equals(updatedPatient.getPatientID())) {
                patientList.replace(i, updatedPatient);
                fileManager.writePatientsToFile(PATIENT_FILE, this.patientList);
                return true;
            }
        }
        return false;
    }

    public boolean deletePatient(String patientId) {
        for (int i = 1; i <= patientList.getLength(); i++) {
            if (patientList.getEntry(i).getPatientID().equals(patientId)) {
                patientList.remove(i);
                fileManager.writePatientsToFile(PATIENT_FILE, this.patientList);
                return true; // Deletion successful
            }
        }
        return false; // Patient not found
    }

    public Patient findPatientByIc(String icNumber) {
        if (icNumber == null || icNumber.trim().isEmpty()) {
            return null;
        }

        // Use the find method from the ADT, passing the search condition as a lambda expression.
        // The search algorithm itself is now inside the DoublyLinkedList.
        return patientList.find(patient -> patient.getIcNumber().equals(icNumber.trim()));
    }
}
