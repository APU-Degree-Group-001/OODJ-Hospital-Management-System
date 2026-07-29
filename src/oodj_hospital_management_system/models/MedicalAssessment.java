/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

/**
 *
 * @author sheaw
 */
public class MedicalAssessment {
    private String assessmentId;

    private String bloodPressure;
    private int heartRate;
    private double temperature;
    private double weight;
    private double height;
    private double bmi;

    private String symptoms;
    private String diagnosis;
    private String notes;

    public MedicalAssessment() {
        this.assessmentId = "";
        this.bloodPressure = "";
        this.heartRate = 0;
        this.temperature = 0.0;
        this.weight = 0.0;
        this.height = 0.0;
        this.bmi = 0.0;
        this.symptoms = "";
        this.diagnosis = "";
        this.notes = "";
    }

    public MedicalAssessment(
            String assessmentId,
            String bloodPressure,
            int heartRate,
            double temperature,
            double weight,
            double height,
            double bmi,
            String symptoms,
            String diagnosis,
            String notes
    ) {
        this.assessmentId = assessmentId;
        this.bloodPressure = bloodPressure;
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format(
                "Medical Assessment {%s}",
                this.assessmentId
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof MedicalAssessment)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        MedicalAssessment assessmentObj = (MedicalAssessment) obj;
        return Objects.equals(this.assessmentId, assessmentObj.assessmentId);
    }

    // Getters

    public String getAssessmentId() {
        return assessmentId;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public double getBMI() {
        return bmi;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    // Setters

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setBMI(double bmi) {
        this.bmi = bmi;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
