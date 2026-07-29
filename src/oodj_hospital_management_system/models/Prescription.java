/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.Objects;

/**
 *
 * @author sheaw
 */
public class Prescription {
    private String prescriptionId;
    private String medicineName;
    private String dosage;
    private int frequencyPerDay;
    private int durationInDays;
    private String instructions;

    public Prescription() {
        this.prescriptionId = "";
        this.medicineName = "";
        this.dosage = "";
        this.frequencyPerDay = 0;
        this.durationInDays = 0;
        this.instructions = "";
    }

    public Prescription(
            String prescriptionId,
            String medicineName,
            String dosage,
            int frequencyPerDay,
            int durationInDays,
            String instructions
    ) {
        this.prescriptionId = prescriptionId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequencyPerDay = frequencyPerDay;
        this.durationInDays = durationInDays;
        this.instructions = instructions;
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
        return prescriptionId;
    }

    public String getMedicineName() {
        return this.medicineName;
    }

    public String getDosage() {
        return this.dosage;
    }

    public int getFrequencyPerDay() {
        return this.frequencyPerDay;
    }

    public int getDurationInDays() {
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

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setFrequencyPerDay(int frequencyPerDay) {
        this.frequencyPerDay = frequencyPerDay;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
