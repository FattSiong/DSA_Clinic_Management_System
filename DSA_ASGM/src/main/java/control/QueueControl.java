/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Doctor;
import java.util.Iterator;
import entity.Patient;
import entity.Queue;
import utility.ReadWriteFile;

/**
 *
 * @author hangg
 */
public class QueueControl {

    private ListInterface<Queue> patientQueue;
    private ReadWriteFile fileManager;
    private PatientControl patientControl;

    public QueueControl() {
        this.patientQueue = new DoublyLinkedList<>();
        this.patientControl = new PatientControl();
    }
    
    public QueueControl(ListInterface<Queue> qList) {
        this.patientQueue = qList;
        this.fileManager = new ReadWriteFile();
    }

    public ListInterface<Patient> getAllPatients() {
        return patientControl.getPatientList();
    }

    public ListInterface<Queue> getPatientQueue() {
        return patientQueue;
    }

    public void enqueuePatient(Patient patient) {
        if (!isPatientInQueue(patient)) {
            int highestQueueNumber = 0;
            Iterator<Queue> it = patientQueue.getIterator();
            while (it.hasNext()) {
                int currentNum = it.next().getQueueNumber();
                if (currentNum > highestQueueNumber) {
                    highestQueueNumber = currentNum;
                }
            }
            int newQueueNumber = highestQueueNumber + 1;

            //patientQueue.add(new Queue(patient, newQueueNumber));
        }
    }

    public Queue dequeueNextPatient() {
        if (patientQueue.isEmpty()) {
            return null;
        }
        return patientQueue.remove(1);
    }

    public Queue callSpecificPatient(int queueNumber) {
        for (int i = 1; i <= patientQueue.getLength(); i++) {
            Queue currentQueueEntry = patientQueue.getEntry(i);
            if (currentQueueEntry.getQueueNumber() == queueNumber) {
                patientQueue.remove(i);
                return currentQueueEntry;
            }
        }
        return null;
    }

    public boolean isPatientInQueue(Patient patient) {
        Iterator<Queue> it = patientQueue.getIterator();
        while (it.hasNext()) {
            if (it.next().getPatient().equals(patient)) {
                return true;
            }
        }
        return false;
    }

    public void dequeuePatient(Patient patient) {
        for (int i = 1; i <= patientQueue.getLength(); i++) {
            if (patientQueue.getEntry(i).getPatient().equals(patient)) {
                patientQueue.remove(i);
                return;
            }
        }
    }
    
    /**
     * Checks if a doctor is currently busy with a walk-in patient.
     * @param doctor The doctor to check.
     * @return True if the doctor is busy, false otherwise.
     */
    public boolean isDoctorBusyWithQueue(Doctor doctor) {
        if (doctor == null) {
            return false;
        }
        
        Iterator<Queue> it = patientQueue.getIterator();
        while (it.hasNext()) {
            Queue queueEntry = it.next();
            // Check if a queue entry is assigned to this doctor and is currently active
            if (queueEntry.getDoctor() != null &&
                queueEntry.getDoctor().getDoctorID().equals(doctor.getDoctorID()) &&
                queueEntry.getStatus().equals("In Consultation")) {
                return true; // Found a match, the doctor is busy
            }
        }
        
        return false; // No active queue entry found for this doctor
    }
    
    /**
     * Filters the main queue list to get only entries with "In Consultation" status.
     * @return A new list containing only the active consultation queue entries.
     */
    public ListInterface<Queue> getInConsultationQueue() {
        ListInterface<Queue> inConsultationList = new DoublyLinkedList<>();
        Iterator<Queue> it = patientQueue.getIterator();
        while (it.hasNext()) {
            Queue queueEntry = it.next();
            if (queueEntry.getStatus().equals("In Consultation")) {
                inConsultationList.add(queueEntry);
            }
        }
        return inConsultationList;
    }
    
    /**
     * Updates the queue data file.
     * This method is called by the UI after a consultation is saved.
     */
    public void updateQueueFile() {
        fileManager.writeQueuesToFile("src/main/java/Data File/queues", patientQueue);
    }
}
