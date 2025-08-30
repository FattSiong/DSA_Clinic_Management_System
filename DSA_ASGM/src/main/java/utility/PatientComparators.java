/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import entity.Patient;
import java.util.Comparator;

/**
 *
 * @author hangg
 */
public class PatientComparators {
    /**
     * Sorts patients by their ID alphabetically (e.g., P001, P002).
     */
    public static class SortById implements Comparator<Patient> {
        @Override
        public int compare(Patient p1, Patient p2) {
            return p1.getPatientID().compareTo(p2.getPatientID());
        }
    }

    /**
     * Sorts patients by their name alphabetically.
     */
    public static class SortByName implements Comparator<Patient> {
        @Override
        public int compare(Patient p1, Patient p2) {
            return p1.getName().compareToIgnoreCase(p2.getName());
        }
    }

    /**
     * Sorts patients by their age in ascending order (youngest to oldest).
     */
    public static class SortByAge implements Comparator<Patient> {
        @Override
        public int compare(Patient p1, Patient p2) {
            // Integer.compare is the standard way to compare primitive ints
            return Integer.compare(p1.getAge(), p2.getAge());
        }
    }
}
