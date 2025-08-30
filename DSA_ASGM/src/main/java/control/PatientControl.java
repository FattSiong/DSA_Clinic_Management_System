/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import java.util.Iterator;
import entity.Patient;
import utility.PatientComparators;
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
                return true;
            }
        }
        return false;
    }

    /**
     * Finds patients by ID.
     *
     * @param id The name to search for.
     * @return The Patient object if found, otherwise null.
     */
    public Patient findPatientById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return patientList.find(patient -> patient.getPatientID().equals(id.trim()));
    }

    /**
     * Finds a patient by their exact ID using a binary search.
     *
     * @param patientId The exact ID of the patient to find.
     * @return The Patient object if found, otherwise null.
     */
    public Patient findPatientByIdBinarySearch(String patientId) {
        patientList.sort(new PatientComparators.SortById());
        Patient searchKey = new Patient(patientId, "", "", 0, "", "", "", "");
        int foundIndex = patientList.binarySearch(searchKey);

        if (foundIndex != -1) {
            return patientList.getEntry(foundIndex);
        }

        return null;
    }

    /**
     * Finds patients by name.
     *
     * @param name The name to search for.
     * @return A list of all patients whose name contains the search term.
     */
    public ListInterface<Patient> findPatientsByName(String name) {
        ListInterface<Patient> results = new DoublyLinkedList<>();
        Iterator<Patient> it = patientList.getIterator();
        while (it.hasNext()) {
            Patient p = it.next();
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Finds patients by age.
     *
     * @param age The age to search for.
     * @return A list of all patients with the specified age.
     */
    public ListInterface<Patient> findPatientsByAge(int age) {
        ListInterface<Patient> results = new DoublyLinkedList<>();
        Iterator<Patient> it = patientList.getIterator();
        while (it.hasNext()) {
            Patient p = it.next();
            if (p.getAge() == age) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Finds a patient by their exact contact number.
     *
     * @param icNumber The IC number to search for.
     * @return The Patient object if found, otherwise null.
     */
    public Patient findPatientByIc(String icNumber) {
        if (icNumber == null || icNumber.trim().isEmpty()) {
            return null;
        }

        return patientList.find(patient -> patient.getIcNumber().equals(icNumber.trim()));
    }

    /**
     * Finds a patient by their exact contact number.
     *
     * @param phoneNumber The contact number to search for.
     * @return The Patient object if found, otherwise null.
     */
    public Patient findPatientByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        return patientList.find(patient -> patient.getContactNumber().equals(phoneNumber.trim()));
    }

    /**
     * Finds a patient by their exact email address.
     *
     * @param email The email address to search for.
     * @return The Patient object if found, otherwise null.
     */
    public Patient findPatientByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        return patientList.find(patient -> patient.getEmail().equalsIgnoreCase(email.trim()));
    }
}
