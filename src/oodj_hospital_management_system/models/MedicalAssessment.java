/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;
import java.util.LinkedHashMap;
import java.util.Objects;
import oodj_hospital_management_system.services.ConsultationManagement;
/**
 *
 * @author sheaw
 */
public class MedicalAssessment {
    private String assessmentId;
    private String consultationId;
    private String assessmentType;
    private String findings;
    private String diagnosis;

    public MedicalAssessment() {
        this.assessmentId = "";
        this.consultationId = "";
        this.assessmentType = "";
        this.findings = "";
        this.diagnosis = "";
    }

    // Preferred constructor - takes consultationId directly, no eager lookup
    public MedicalAssessment(
            String assessmentId,
            String consultationId,
            String assessmentType,
            String findings,
            String diagnosis
    ) {
        this.assessmentId = assessmentId;
        this.consultationId = consultationId;
        this.assessmentType = assessmentType;
        this.findings = findings;
        this.diagnosis = diagnosis;
    }

    // Backward-compatible constructor - accepts a Consultation object, stores only its ID
    public MedicalAssessment(
            String assessmentId,
            Consultation consultation,
            String assessmentType,
            String findings,
            String diagnosis
    ) {
        this(
            assessmentId,
            consultation != null ? consultation.getConsultationId() : "",
            assessmentType,
            findings,
            diagnosis
        );
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
        return this.consultationId;
    }

    // Lazily resolves the Consultation - safe to call regardless of load order
    public Consultation getConsultation() {
        if (this.consultationId == null || this.consultationId.isEmpty()) {
            return null;
        }
        return ConsultationManagement.findConsultationById(this.consultationId);
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
        this.consultationId = consultation != null ? consultation.getConsultationId() : "";
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
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
        assessmentMap.put("consultationId", this.consultationId != null ? this.consultationId : "");
        assessmentMap.put("assessmentType", this.assessmentType);
        assessmentMap.put("findings", this.findings);
        assessmentMap.put("diagnosis", this.diagnosis);
        return assessmentMap;
    }
}