/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import entity.Queue;
import entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import utility.ReadWriteFile;

/**
 *
 * @author hangg
 */
public class QueueControl {

    // File paths for data persistence
    private static final String QUEUES_FILE = "src/main/java/Data File/queues";
    private static final String DOCTORS_FILE = "src/main/java/Data File/doctors";
    private static final String SCHEDULES_FILE = "src/main/java/Data File/schedules";

    // In-memory lists to hold the application's state
    private ListInterface<Queue> queueList;
    private ListInterface<Doctor> doctorList;
    private ListInterface<Schedule> scheduleList; // To check doctor availability
    private ReadWriteFile fileManager;

    private Runnable onQueueUpdateCallback;

    // The daily queue number counter, distinct from the persistent queueId
    private int nextQueueId;             // Persistent ID counter, continues across sessions
    private int queueNumber; // Transient daily number counter, resets each day

    public QueueControl() {
        // Initialize lists and the file manager
        this.queueList = new DoublyLinkedList<>();
        this.doctorList = new DoublyLinkedList<>();
        this.scheduleList = new DoublyLinkedList<>();
        this.fileManager = new ReadWriteFile();

        // Load all necessary data from files
        loadInitialData();

        // Determine the starting queue number for the day
        initializeQueue();
    }

    private void loadInitialData() {
        PatientControl tempPatientControl = new PatientControl();
        ListInterface<Patient> patientList = tempPatientControl.getPatientList();
        fileManager.readDoctorsFromFile(DOCTORS_FILE, doctorList);
        fileManager.readSchedulesFromFile(SCHEDULES_FILE, scheduleList, doctorList);
        fileManager.readQueuesFromFile(QUEUES_FILE, queueList, patientList, doctorList);
    }

    private void initializeQueue() {
        if (queueList.isEmpty()) {
            // If there's no data, start counters at 1
            nextQueueId = 1;
            queueNumber = 1;
        } else {
            int highestId = 0;
            int highestQueueNumber = 0;
            Iterator<Queue> it = queueList.getIterator();
            while (it.hasNext()) {
                Queue q = it.next();
                if (q.getQueueId() > highestId) {
                    highestId = q.getQueueId();
                }
                if (q.getQueueNumber() > highestQueueNumber) {
                    highestQueueNumber = q.getQueueNumber();
                }
            }
            // Set the next ID and number to be one greater than the maximums found
            nextQueueId = highestId + 1;
            queueNumber = highestQueueNumber + 1;
        }
    }

    public ListInterface<Queue> getQueueList() {
        return this.queueList;
    }

    public ListInterface<Schedule> getScheduleList() {
        return this.scheduleList;
    }

    // --- Public Methods for UI Interaction ---
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
     * Removes a patient from the active queue. This implementation finds the
     * "Waiting" entry for the patient and removes it.
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

    public Queue callPatientByQueueNumber(int queueNum, Doctor doctor) {
        Queue queueEntry = findWaitingEntryByQueueNumber(queueNum);

        if (queueEntry != null) {
            queueEntry.setDoctor(doctor);
            queueEntry.setExitTime(LocalTime.now());
            queueEntry.setStatus("In Consultation");
            fileManager.writeQueuesToFile(QUEUES_FILE, queueList);
            updateDoctorScheduleStatus(doctor, "Busy");
            return queueEntry;
        }
        return null;
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

    public Queue findLastCalledPatient() {
        Queue lastCalled = null;
        LocalTime latestTime = LocalTime.MIN; // Start with the earliest possible time

        Iterator<Queue> it = queueList.getIterator();
        while (it.hasNext()) {
            Queue q = it.next();
            // Consider any patient who has been called (i.e., has an exitTime)
            if (q.getExitTime() != null) {
                // Check if this patient's call time is later than the latest one found so far
                if (q.getExitTime().isAfter(latestTime)) {
                    latestTime = q.getExitTime();
                    lastCalled = q;
                }
            }
        }
        return lastCalled;
    }

    private void updateDoctorScheduleStatus(Doctor doctor, String newStatus) {
        LocalDate currentDate = LocalDate.now();
        Iterator<Schedule> scheduleIt = scheduleList.getIterator();

        while (scheduleIt.hasNext()) {
            Schedule s = scheduleIt.next();
            if (s.getDoctor().equals(doctor) && s.getDutyDate().equals(currentDate)) {
                s.setAvailability(newStatus);
                break; // Found and updated the schedule, no need to continue looping.
            }
        }

        // After updating the list in memory, write the changes back to the file.
        // This assumes you have a writeSchedulesToFile method in your ReadWriteFile class.
        fileManager.writeSchedulesToFile(SCHEDULES_FILE, scheduleList);
    }

    private void refreshScheduleList() {
        scheduleList.clear();
        fileManager.readSchedulesFromFile(SCHEDULES_FILE, scheduleList, doctorList);
    }

    public String getRoomStatusForToday(int roomNumber) {
        String doctorId = "D100" + roomNumber; // Determine Doctor ID from room number
        Doctor doc = null;

        // Find the doctor object that matches the ID
        Iterator<Doctor> docIt = doctorList.getIterator();
        while (docIt.hasNext()) {
            Doctor currentDoc = docIt.next();
            if (currentDoc.getDoctorID().equals(doctorId)) {
                doc = currentDoc;
                break;
            }
        }

        if (doc == null) {
            return "Unavailable"; // Should not happen if data is correct, but a good safeguard
        }

        refreshScheduleList();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        Iterator<Schedule> scheduleIt = scheduleList.getIterator();
        while (scheduleIt.hasNext()) {
            Schedule s = scheduleIt.next();

            // Find the schedule for this doctor and for today's date
            if (s.getDoctor().equals(doc) && s.getDutyDate().equals(currentDate)) {
                // Check if the current time is within the doctor's working hours
                boolean onDuty = !currentTime.isBefore(s.getStartTime()) && currentTime.isBefore(s.getEndTime());

                if (onDuty) {
                    return s.getAvailability(); // Returns "Available", "Busy", or "On Leave"
                } else {
                    return "Unavailable"; // Doctor is scheduled today, but not at this time
                }
            }
        }

        return "Unavailable"; // No schedule found for this doctor today
    }

    // --- Helper and Getter Methods ---
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

    public String getDoctorStatus(Doctor doctor) {
        Iterator<Queue> queueIt = queueList.getIterator();
        while (queueIt.hasNext()) {
            Queue q = queueIt.next();
            if (doctor.equals(q.getDoctor()) && "In Consultation".equals(q.getStatus())) {
                return "Busy";
            }
        }

        Iterator<Schedule> scheduleIt = scheduleList.getIterator();
        while (scheduleIt.hasNext()) {
            Schedule s = scheduleIt.next();
            if (s.getDoctor().equals(doctor) && s.getDutyDate().equals(LocalDate.now())) {
                return s.getAvailability();
            }
        }
        return "Available";
    }

    // Add this new method. The UI will use it to listen for updates.
    public void setOnQueueUpdateCallback(Runnable callback) {
        this.onQueueUpdateCallback = callback;
    }

    // Add this helper method to send the refresh signal.
    private void triggerQueueUpdate() {
        if (onQueueUpdateCallback != null) {
            onQueueUpdateCallback.run();
        }
    }
}
