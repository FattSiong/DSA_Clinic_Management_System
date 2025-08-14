/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import adt.ListInterface;
import java.util.Iterator;

/**
 *
 * @author tiong
 */
public class Treatment {

    private String treatmentID;
    private Consultation consultation; // Link to the consultation
    private String treatmentDetails;

    private ListInterface<Medicine> prescribedMedicine;

    public Treatment(String treatmentID, Consultation consultation, String treatmentDetails, ListInterface<Medicine> prescribedMedicine) {
        this.treatmentID = treatmentID;
        this.consultation = consultation;
        this.treatmentDetails = treatmentDetails;
        this.prescribedMedicine = prescribedMedicine;
    }

    // Getters
    public String getTreatmentID() {
        return treatmentID;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    public ListInterface<Medicine> getPrescribedMedicine() {
        return prescribedMedicine;
    }

    // Setters
    public void setTreatmentID(String treatmentID) {
        this.treatmentID = treatmentID;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public void setTreatmentDetails(String treatmentDetails) {
        this.treatmentDetails = treatmentDetails;
    }

    public void setPrescribedMedicine(ListInterface<Medicine> prescribedMedicine) {
        this.prescribedMedicine = prescribedMedicine;
    }

    @Override
    public String toString() {
        // Build the string for the list of medicines manually using the iterator
        StringBuilder medicineString = new StringBuilder("[");
        Iterator<Medicine> iterator = prescribedMedicine.getIterator();
        while (iterator.hasNext()) {
            medicineString.append(iterator.next().getName()); // Appending name for brevity
            if (iterator.hasNext()) {
                medicineString.append(", ");
            }
        }
        medicineString.append("]");

        return "Treatment{"
                + "treatmentID='" + treatmentID + '\''
                + ", consultationID=" + consultation.getConsultationID()
                + ", treatmentDetails='" + treatmentDetails + '\''
                + ", prescribedMedicine=" + medicineString.toString()
                + '}';
    }
}
