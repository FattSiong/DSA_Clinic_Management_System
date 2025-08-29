/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import entity.Consultation;
import java.util.Comparator;
/**
 *
 * @author tiong
 */
public class ConsultationComparators {

    /**
     * Sorts consultations by their ID alphabetically.
     */
    public static class SortById implements Comparator<Consultation> {
        @Override
        public int compare(Consultation c1, Consultation c2) {
            return c1.getConsultationID().compareTo(c2.getConsultationID());
        }
    }

    /**
     * Sorts consultations by the patient's name alphabetically.
     */
    public static class SortByPatientName implements Comparator<Consultation> {
        @Override
        public int compare(Consultation c1, Consultation c2) {
            return c1.getPatient().getName().compareTo(c2.getPatient().getName());
        }
    }

    /**
     * Sorts consultations by the doctor's name alphabetically.
     */
    public static class SortByDoctorName implements Comparator<Consultation> {
        @Override
        public int compare(Consultation c1, Consultation c2) {
            return c1.getDoctor().getName().compareTo(c2.getDoctor().getName());
        }
    }

    /**
     * Sorts consultations chronologically by date.
     */
    public static class SortByDate implements Comparator<Consultation> {
        @Override
        public int compare(Consultation c1, Consultation c2) {
            return c1.getConsultationDate().compareTo(c2.getConsultationDate());
        }
    }

    /**
     * Sorts consultations by their diagnosis alphabetically.
     */
    public static class SortByDiagnosis implements Comparator<Consultation> {
        @Override
        public int compare(Consultation c1, Consultation c2) {
            return c1.getDiagnosis().compareTo(c2.getDiagnosis());
        }
    }
}
