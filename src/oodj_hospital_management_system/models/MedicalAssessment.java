/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *
 * @author sheaw
 */

public class MedicalAssessment {
    private String assessmentId;
    private Consultation consultation;

    private String assessmentType;
    private String findings;
    private String diagnosis;

    public MedicalAssessment() {
        this.assessmentId = "";
        this.consultation = null;
        this.assessmentType = "";
        this.findings = "";
        this.diagnosis = "";
    }

    public MedicalAssessment(
            String assessmentId,
            Consultation consultation,
            String assessmentType,
            String findings,
            String diagnosis
    ) {
        this.assessmentId = assessmentId;
        this.consultation = consultation;
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

    public Consultation getConsultation() {
        return consultation;
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
    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
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
    
    public LinkedHashMap<String, String> getMedicalAssessmentMap() {
        LinkedHashMap<String, String> assessmentMap = new LinkedHashMap<>();
        assessmentMap.put("assessmentId", this.assessmentId);
        assessmentMap.put("consultationId", this.consultation != null ? this.consultation.getConsultationId() : "");
        assessmentMap.put("assessmentType", this.assessmentType);
        assessmentMap.put("findings", this.findings);
        assessmentMap.put("diagnosis", this.diagnosis);
        return assessmentMap;
    }
}
