/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import entity.Appointment;
import java.util.Comparator;

/**
 *
 * @author tiong
 */
public class AppointmentComparators {
    /**
     * Sorts appointments by their ID alphabetically.
     */
    public static class SortById implements Comparator<Appointment> {
        @Override
        public int compare(Appointment a1, Appointment a2) {
            return a1.getAppointmentID().compareTo(a2.getAppointmentID());
        }
    }

    /**
     * Sorts appointments by the patient's name alphabetically.
     */
    public static class SortByPatientName implements Comparator<Appointment> {
        @Override
        public int compare(Appointment a1, Appointment a2) {
            return a1.getPatient().getName().compareTo(a2.getPatient().getName());
        }
    }

    /**
     * Sorts appointments by the doctor's name alphabetically.
     */
    public static class SortByDoctorName implements Comparator<Appointment> {
        @Override
        public int compare(Appointment a1, Appointment a2) {
            return a1.getDoctor().getName().compareTo(a2.getDoctor().getName());
        }
    }

    /**
     * Sorts appointments chronologically by date.
     */
    public static class SortByDate implements Comparator<Appointment> {
        @Override
        public int compare(Appointment a1, Appointment a2) {
            return a1.getAppointmentDate().compareTo(a2.getAppointmentDate());
        }
    }

    /**
     * Sorts appointments by their status alphabetically.
     */
    public static class SortByStatus implements Comparator<Appointment> {
        @Override
        public int compare(Appointment a1, Appointment a2) {
            return a1.getStatus().compareTo(a2.getStatus());
        }
    }
}
