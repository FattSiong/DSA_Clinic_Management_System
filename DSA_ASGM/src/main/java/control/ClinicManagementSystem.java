/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import boundary.MainUI;
import entity.Patient;
import entity.Doctor;
import entity.Medicine;
import entity.Schedule;
import entity.Consultation;
import entity.Treatment;
import entity.Appointment;
import entity.DispensedItem;
import entity.DispensingRecord;
import entity.Queue;
import javax.swing.SwingUtilities;
import utility.ReadWriteFile;

/**
 *
 * @author tiong
 */
public class ClinicManagementSystem {

    // --- Data Lists ---
    // These lists will hold all the application's data in memory.
    private static ListInterface<Patient> patientList = new DoublyLinkedList<>();
    private static ListInterface<Queue> queueList = new DoublyLinkedList<>();
    private static ListInterface<Doctor> doctorList = new DoublyLinkedList<>();
    private static ListInterface<Medicine> medicineList = new DoublyLinkedList<>();
    private static ListInterface<Schedule> scheduleList = new DoublyLinkedList<>();
    private static ListInterface<Appointment> appointmentList = new DoublyLinkedList<>();
    private static ListInterface<Consultation> consultationList = new DoublyLinkedList<>();
    private static ListInterface<Treatment> treatmentList = new DoublyLinkedList<>();
    private static ListInterface<DispensedItem> dispensedItemList = new DoublyLinkedList<>();
    private static ListInterface<DispensingRecord> dispensingRecordList = new DoublyLinkedList<>();

    // --- Data Manager ---
    private static ReadWriteFile readWriteFile = new ReadWriteFile();

    /**
     * The main method to start the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        // 1. Load all data from files into the lists
        readWriteFile.loadAllData(
                patientList, queueList, doctorList, medicineList, scheduleList,
                appointmentList, consultationList, treatmentList, dispensedItemList,
                dispensingRecordList
        );

        System.out.println("Data loaded. Appointments found: " + appointmentList.getLength());

        // 2. Start the main GUI
        // It's recommended to run GUI code on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Pass all the loaded data to the MainUI
                new MainUI(
                        patientList, doctorList, medicineList, scheduleList,
                        appointmentList, consultationList, treatmentList, dispensedItemList,
                        dispensingRecordList
                ).setVisible(true);
            }
        });

        // Optional: Add a shutdown hook to save data when the application closes
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            System.out.println("Saving all data...");
//            readWriteFile.saveAllData(
//                    patientList, queueList, doctorList, medicineList, scheduleList,
//                    appointmentList, consultationList, treatmentList
//            );
//            System.out.println("Data saved successfully.");
//        }));
    }
}
