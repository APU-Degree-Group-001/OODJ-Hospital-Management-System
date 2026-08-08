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

public class MedicalAssessment {
    private String assessmentId;
    private String consultationId;
    private String doctorId;

    private String assessmentType;
    private String findings;
    private String diagnosis;

    public MedicalAssessment() {
        this.assessmentId = "";
        this.consultationId = "";
        this.doctorId = "";
        this.assessmentType = "";
        this.findings = "";
        this.diagnosis = "";
    }

    public MedicalAssessment(
            String assessmentId,
            String consultationId,
            String doctorId,
            String assessmentType,
            String findings,
            String diagnosis
    ) {
        this.assessmentId = assessmentId;
        this.consultationId = consultationId;
        this.doctorId = doctorId;
        this.assessmentType = assessmentType;
        this.findings = findings;
        this.diagnosis = diagnosis;
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

        return Objects.equals(
                this.assessmentId,
                assessmentObj.assessmentId
        );
    }

    // Getters

    public String getAssessmentId() {
        return assessmentId;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public String getFindings() {
        return findings;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    // Setters

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
