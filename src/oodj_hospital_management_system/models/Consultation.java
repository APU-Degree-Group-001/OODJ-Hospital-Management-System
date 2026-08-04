/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.ArrayList;
import java.util.Objects;
import oodj_hospital_management_system.models.interfaces.ConsultationWorkflow;

/**
 *
 * @author sheaw
 */
public class Consultation implements ConsultationWorkflow {
    private String consultationId;
    private ConsultationSlot consultationSlot;
    private Patient patient;
    private String description;
    private MedicalAssessment medicalAssessment;
    private ArrayList<LabResult> labResults;
    private ArrayList<Prescription> prescriptions;
    private HospitalAssetAllocation hospitalAssetAllocation;
    private Feedback feedback;
    private Bill bill;
    
    public enum ConsultationStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    private ConsultationStatus status;

    public Consultation() {
        this.consultationId = "";
        this.consultationSlot = null;
        this.patient = null;
        this.description = "";
        this.medicalAssessment = null;
        this.labResults = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.hospitalAssetAllocation = null;
        this.feedback = null;
        this.bill = null;
        this.status = ConsultationStatus.SCHEDULED;
    }

    public Consultation(
            String consultationId,
            ConsultationSlot consultationSlot,
            Patient patient,
            String description,
            MedicalAssessment medicalAssessment,
            ArrayList<LabResult> labResults,
            ArrayList<Prescription> prescriptions,
            HospitalAssetAllocation hospitalAssetAllocation,
            Feedback feedback,
            Bill bill,
            ConsultationStatus status
    ) {
        this.consultationId = consultationId;
        this.consultationSlot = consultationSlot;
        this.patient = patient;
        this.description = description;
        this.medicalAssessment = medicalAssessment;
        this.labResults = labResults;
        this.prescriptions = prescriptions;
        this.hospitalAssetAllocation = hospitalAssetAllocation;
        this.feedback = feedback;
        this.bill = bill;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "Consultation {%s - %s}",
                this.consultationId,
                this.patient == null ? "No Patient" : this.patient.getName()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Consultation)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        Consultation consultationObj = (Consultation) obj;
        return Objects.equals(this.consultationId, consultationObj.consultationId);
    }
    
    @Override
    public void startConsultation() {
        if (status != ConsultationStatus.SCHEDULED) {
            throw new IllegalStateException(
                "In-progress or completed consultation unable to be started."
            );
        }
        status = ConsultationStatus.IN_PROGRESS;
    }
    
    @Override
    public void completeConsultation() {
        if (status != ConsultationStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "Only an in-progress consultation can be completed."
            );
        }
        status = ConsultationStatus.COMPLETED;
    }
    
    @Override
    public void cancelConsultation() {
        if (status != ConsultationStatus.SCHEDULED) {
            throw new IllegalStateException(
                "In-progress or completed consultation unable to be cancelled."
            );
        }
        status = ConsultationStatus.CANCELLED;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public ConsultationSlot getConsultationSlot() {
        return consultationSlot;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getDescription() {
        return description;
    }

    public MedicalAssessment getMedicalAssessment() {
        return medicalAssessment;
    }

    public ArrayList<LabResult> getLabResults() {
        return labResults;
    }

    public ArrayList<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public HospitalAssetAllocation getHospitalAssetAllocation() {
        return hospitalAssetAllocation;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public Bill getBill() {
        return bill;
    }

    public ConsultationStatus getConsultationStatus() {
        return this.status;
    }

    public void setConsultationSlot(ConsultationSlot consultationSlot) {
        this.consultationSlot = consultationSlot;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMedicalAssessment(MedicalAssessment medicalAssessment) {
        this.medicalAssessment = medicalAssessment;
    }

    public void setLabResults(ArrayList<LabResult> labResults) {
        this.labResults = labResults;
    }

    public void setPrescriptions(ArrayList<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public void setHospitalAssetAllocation(HospitalAssetAllocation hospitalAssetAllocation) {
        this.hospitalAssetAllocation = hospitalAssetAllocation;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
