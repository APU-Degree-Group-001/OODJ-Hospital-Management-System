/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;
import java.util.Objects;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.services.PrescriptionTypeManagement;

/**
 *
 * @author sheaw
 */

public class Prescription {
    private String prescriptionId;
    private String consultationId;

    private String medicineName;
    private String dosage;
    private String frequencyPerDay;
    private String durationInDays;
    private String instructions;

    public Prescription() {
        this.prescriptionId = "";
        this.consultationId = "";
        this.medicineName = "";
        this.dosage = "";
        this.frequencyPerDay = "";
        this.durationInDays = "";
        this.instructions = "";
    }

    // Preferred constructor - takes consultationId directly, no eager lookup
    public Prescription(
            String prescriptionId,
            String consultationId,
            String medicineName,
            String dosage,
            String frequencyPerDay,
            String durationInDays,
            String instructions
    ) {
        this.prescriptionId = prescriptionId;
        this.consultationId = consultationId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequencyPerDay = frequencyPerDay;
        this.durationInDays = durationInDays;
        this.instructions = instructions;
    }

    // Backward-compatible constructor - accepts a Consultation object, stores only its ID
    public Prescription(
            String prescriptionId,
            Consultation consultation,
            String medicineName,
            String dosage,
            String frequencyPerDay,
            String durationInDays,
            String instructions
    ) {
        this(
            prescriptionId,
            consultation != null ? consultation.getConsultationId() : "",
            medicineName,
            dosage,
            frequencyPerDay,
            durationInDays,
            instructions
        );
    }

    // Convenience constructor - accepts a PrescriptionType object, stores only its typeName
    public Prescription(
            String prescriptionId,
            String consultationId,
            PrescriptionType prescriptionType,
            String dosage,
            String frequencyPerDay,
            String durationInDays,
            String instructions
    ) {
        this(
            prescriptionId,
            consultationId,
            prescriptionType != null ? prescriptionType.getTypeName() : "",
            dosage,
            frequencyPerDay,
            durationInDays,
            instructions
        );
    }

    @Override
    public String toString() {
        return String.format(
                "Prescription {%s - %s}",
                this.prescriptionId,
                this.medicineName
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Prescription)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        Prescription prescriptionObj = (Prescription) obj;

        return Objects.equals(
                this.prescriptionId,
                prescriptionObj.prescriptionId
        );
    }

    // Getters
    public String getPrescriptionId() {
        return this.prescriptionId;
    }

    public String getConsultationId() {
        return this.consultationId;
    }

    // Lazily resolves the Consultation - safe to call regardless of load order
    public Consultation getConsultation() {
        if (this.consultationId == null || this.consultationId.isEmpty()) {
            return null;
        }
        return ConsultationManagement.findConsultationById(this.consultationId);
    }

    public String getMedicineName() {
        return this.medicineName;
    }

    // Lazily resolves the PrescriptionType by name - safe to call regardless of load order
    public PrescriptionType getPrescriptionType() {
        if (this.medicineName == null || this.medicineName.isEmpty()) {
            return null;
        }
        return PrescriptionTypeManagement.findPrescriptionTypeByName(this.medicineName);
    }

    public String getDosage() {
        return this.dosage;
    }

    public String getFrequencyPerDay() {
        return this.frequencyPerDay;
    }

    public String getDurationInDays() {
        return this.durationInDays;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public String getFrequencyText() {
        return this.frequencyPerDay + " time(s) daily";
    }

    public String getDurationText() {
        return this.durationInDays + " day(s)";
    }

    // Setters
    public void setConsultation(Consultation consultation) {
        this.consultationId = consultation != null ? consultation.getConsultationId() : "";
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    // Sets the medicine via a PrescriptionType object, storing only its typeName
    public void setPrescriptionType(PrescriptionType prescriptionType) {
        this.medicineName = prescriptionType != null ? prescriptionType.getTypeName() : "";
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setFrequencyPerDay(String frequencyPerDay) {
        this.frequencyPerDay = frequencyPerDay;
    }

    public void setDurationInDays(String durationInDays) {
        this.durationInDays = durationInDays;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public LinkedHashMap<String, String> getPrescriptionMap() {
        LinkedHashMap<String, String> prescriptionMap = new LinkedHashMap<>();

        prescriptionMap.put("prescriptionId", this.prescriptionId);
        prescriptionMap.put("consultationId", this.consultationId != null ? this.consultationId : "");
        prescriptionMap.put("medicineName", this.medicineName);
        prescriptionMap.put("dosage", this.dosage);
        prescriptionMap.put("frequencyPerDay", this.frequencyPerDay);
        prescriptionMap.put("durationInDays", this.durationInDays);
        prescriptionMap.put("instructions", this.instructions);

        return prescriptionMap;
    }
}