/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import entity.Patient;
import adt.DoublyLinkedList;
import adt.ListInterface;
import java.util.Iterator;
import javax.swing.JOptionPane;
import utility.ReadWriteFile;

/**
 *
 * @author hangg
 */
public class PatientControl {
    private ListInterface<Patient> patientList;
    private static final String PATIENT_FILE = "src/main/java/Data File/patients";

    public PatientControl() {
        this.patientList = new DoublyLinkedList<>();
        ReadWriteFile.readPatientsFromFile(PATIENT_FILE, this.patientList);
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
        return null; // Return null if no patient with that ID is found
    }

    public void addPatient(Patient newPatient) {
        if (patientList.contains(newPatient)) {
            JOptionPane.showMessageDialog(null, "A patient with ID " + newPatient.getPatientID() + " already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
        } else {
            patientList.add(newPatient);
            ReadWriteFile.writePatientsToFile(PATIENT_FILE, this.patientList);
            JOptionPane.showMessageDialog(null, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void updatePatient(Patient updatedPatient) {
        for (int i = 1; i <= patientList.getLength(); i++) {
            if (patientList.getEntry(i).getPatientID().equals(updatedPatient.getPatientID())) {
                patientList.replace(i, updatedPatient);
                ReadWriteFile.writePatientsToFile(PATIENT_FILE, this.patientList);
                JOptionPane.showMessageDialog(null, "Patient details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }
}
