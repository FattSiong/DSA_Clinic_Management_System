/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import java.util.Iterator;
import entity.Patient;
import entity.Queue;

/**
 *
 * @author hangg
 */
public class QueueControl {

    private ListInterface<Queue> patientQueue;
    private PatientControl patientControl;

    public QueueControl() {
        this.patientQueue = new DoublyLinkedList<>();
        this.patientControl = new PatientControl();
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
}
