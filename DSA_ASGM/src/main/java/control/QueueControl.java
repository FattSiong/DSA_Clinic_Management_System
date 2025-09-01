/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Patient;
import entity.Queue;
import entity.Doctor;
import entity.Schedule;
import utility.ReadWriteFile;
import java.util.Iterator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author hangg
 */
public class QueueControl {

    private static final String QUEUES_FILE = "src/main/java/Data File/queues";
    private static final String DOCTORS_FILE = "src/main/java/Data File/doctors";
    private static final String SCHEDULES_FILE = "src/main/java/Data File/schedules";

    private ListInterface<Queue> queueList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Schedule> scheduleList;
    private ReadWriteFile fileManager;

    private Runnable onQueueUpdateCallback;

    private int nextQueueId;
    private int queueNumber;

    public QueueControl() {
        this.queueList = new DoublyLinkedList<>();
        this.doctorList = new DoublyLinkedList<>();
        this.scheduleList = new DoublyLinkedList<>();
        this.fileManager = new ReadWriteFile();
      
        loadInitialData();

        initializeQueue();
    }
    
    public QueueControl(ListInterface<Queue> qList) {
        this.queueList = qList;
        this.fileManager = new ReadWriteFile();
    }

    private void loadInitialData() {
        PatientControl patientControl = new PatientControl();
        ListInterface<Patient> patientList = patientControl.getPatientList();
        fileManager.readDoctorsFromFile(DOCTORS_FILE, doctorList);
        fileManager.readSchedulesFromFile(SCHEDULES_FILE, scheduleList, doctorList);
        fileManager.readQueuesFromFile(QUEUES_FILE, queueList, patientList, doctorList);
    }

    private void initializeQueue() {
        this.queueNumber = 1;

        if (queueList.isEmpty()) {
            this.nextQueueId = 1;
        } else {
            int highestId = 0;
            Iterator<Queue> it = queueList.getIterator();
            while (it.hasNext()) {
                Queue q = it.next();
                if (q.getQueueId() > highestId) {
                    highestId = q.getQueueId();
                }
            }
            this.nextQueueId = highestId + 1;
        }
    }

    public void loadQueueData() {
        PatientControl patientControl = new PatientControl();
        ListInterface<Patient> patientList = patientControl.getPatientList();
        // Clear the existing list before reloading
        if (queueList != null) {
            queueList.clear();
        }
        // Call the file manager to read the data into the list
        fileManager.readQueuesFromFile(QUEUES_FILE, queueList, patientList, doctorList);
    }

    public ListInterface<Queue> getQueueList() {
        return this.queueList;
    }

    public ListInterface<Queue> getWaitingPatients() {
        ListInterface<Queue> waitingList = new DoublyLinkedList<>();
        Iterator<Queue> it = queueList.getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            if ("Waiting".equals(q.getStatus())) {
                waitingList.add(q);
            }
        }
        return waitingList;
    }

    public ListInterface<Schedule> getScheduleList() {
        return this.scheduleList;
    }

    /**
     * Adds a patient to the queue, assigns the next queue number, and saves to
     * file.
     *
     * @param patient The patient to add.
     */
    public void enqueuePatient(Patient patient) {
        Queue newEntry = new Queue(nextQueueId, patient);
        newEntry.setQueueNumber(queueNumber);
        queueList.add(newEntry);
        nextQueueId++;
        queueNumber++;
        fileManager.writeQueuesToFile(QUEUES_FILE, queueList);
        triggerQueueUpdate();
    }

    /**
     * Removes a patient from the active queue. Finds the "Waiting" entry for
     * the patient and removes it.
     *
     * @param patient The patient to dequeue.
     * @return True if the patient was found and removed, false otherwise.
     */
    public boolean dequeuePatient(Patient patient) {
        for (int i = 1; i <= queueList.getLength(); i++) {
            Queue q = queueList.getEntry(i);
            if (q.getPatient().equals(patient) && "Waiting".equals(q.getStatus())) {
                queueList.remove(i);
                // After removing, we must re-save the entire list to the file
                fileManager.writeQueuesToFile(QUEUES_FILE, queueList);
                triggerQueueUpdate();
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a patient is currently in a "Waiting" state in the queue.
     *
     * @param patient The patient to check.
     * @return True if the patient is waiting, false otherwise.
     */
    public boolean isPatientInQueue(Patient patient) {
        Iterator<Queue> it = queueList.getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            if (q.getPatient().equals(patient) && "Waiting".equals(q.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public Queue callNextPatientInQueue(Doctor doctor) {
        Queue nextPatientToCall = null;
        int lowestNumber = Integer.MAX_VALUE;

        Iterator<Queue> it = queueList.getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            if ("Waiting".equals(q.getStatus()) && q.getQueueNumber() < lowestNumber) {
                lowestNumber = q.getQueueNumber();
                nextPatientToCall = q;
            }
        }

        if (nextPatientToCall != null) {
            return callPatientByQueueNumber(nextPatientToCall.getQueueNumber(), doctor);
        }
        return null;
    }

    public Queue recallPatient(Doctor doctor) {
        Queue lastCalled = null;
        Iterator<Queue> it = queueList.getIterator();
        while (it.hasNext()) {
            Queue current = it.next();
            if (doctor.equals(current.getDoctor()) && "In Consultation".equals(current.getStatus())) {
                lastCalled = current;
            }
        }
        return lastCalled;
    }

    public Queue callPatientByQueueNumber(int queueNum, Doctor doctor) {
        Queue queueEntry = findWaitingEntryByQueueNumber(queueNum);

        if (queueEntry != null) {
            queueEntry.setDoctor(doctor);
            queueEntry.setExitTime(LocalDateTime.now());
            queueEntry.setStatus("In Consultation");
            fileManager.writeQueuesToFile(QUEUES_FILE, queueList);
            updateDoctorScheduleStatus(doctor, "Busy");
            return queueEntry;
        }
        return null;
    }

    public Doctor findDoctorByRoomNumber(String roomNumber) {
        Iterator<Doctor> it = doctorList.getIterator();
        while (it.hasNext()) {
            Doctor d = it.next();
            if (d.getRoomNumber().equals("ROOM" + roomNumber)) {
                return d;
            }
        }
        return null;
    }

    public Queue findLastCalledPatient() {
        Queue lastCalled = null;
        LocalDateTime latestTime = LocalDateTime.MIN;

        Iterator<Queue> it = queueList.getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            if (q.getExitTime() != null) {
                if (q.getExitTime().isAfter(latestTime)) {
                    latestTime = q.getExitTime();
                    lastCalled = q;
                }
            }
        }
        return lastCalled;
    }

    private Queue findWaitingEntryByQueueNumber(int queueNum) {
        Iterator<Queue> it = queueList.getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            if (q.getQueueNumber() == queueNum && "Waiting".equals(q.getStatus())) {
                return q;
            }
        }
        return null;
    }

    private void updateDoctorScheduleStatus(Doctor doctor, String newStatus) {
        LocalDate currentDate = LocalDate.now();
        Iterator<Schedule> scheduleIt = scheduleList.getIterator();

        while (scheduleIt.hasNext()) {
            Schedule s = scheduleIt.next();
            if (s.getDoctor().equals(doctor) && s.getDutyDate().equals(currentDate)) {
                s.setAvailability(newStatus);
                break;
            }
        }
        fileManager.writeSchedulesToFile(SCHEDULES_FILE, scheduleList);
    }

    private void refreshScheduleList() {
        scheduleList.clear();
        fileManager.readSchedulesFromFile(SCHEDULES_FILE, scheduleList, doctorList);
    }

    public String getRoomStatusForToday(int roomNumber) {
        String doctorId = "D100" + roomNumber;
        Doctor doc = null;

        Iterator<Doctor> docIt = doctorList.getIterator();
        while (docIt.hasNext()) {
            Doctor currentDoc = docIt.next();
            if (currentDoc.getDoctorID().equals(doctorId)) {
                doc = currentDoc;
                break;
            }
        }

        if (doc == null) {
            return "Unavailable";
        }

        refreshScheduleList();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        Iterator<Schedule> scheduleIt = scheduleList.getIterator();
        while (scheduleIt.hasNext()) {
            Schedule s = scheduleIt.next();

            if (s.getDoctor().equals(doc) && s.getDutyDate().equals(currentDate)) {
                boolean onDuty = !currentTime.isBefore(s.getStartTime()) && currentTime.isBefore(s.getEndTime());

                if (onDuty) {
                    return s.getAvailability();
                } else {
                    return "Unavailable";
                }
            }
        }

        return "Unavailable";
    }

    // The UI will use it to listen for updates.
    public void setOnQueueUpdateCallback(Runnable callback) {
        this.onQueueUpdateCallback = callback;
    }

    // Helper method to send the refresh signal.
    private void triggerQueueUpdate() {
        if (onQueueUpdateCallback != null) {
            onQueueUpdateCallback.run();
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
        
        Iterator<Queue> it = queueList.getIterator();
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
        Iterator<Queue> it = queueList.getIterator();
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
        fileManager.writeQueuesToFile("src/main/java/Data File/queues", queueList);
    }
}
