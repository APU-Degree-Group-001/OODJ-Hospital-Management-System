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

public class LabResult {
    private String resultId;
    private Consultation consultation;
    private String resultType;
    private String result;
    private String remarks;
    private String resultDateTime;

    public LabResult() {
        this.resultId = "";
        this.consultation = null;
        this.resultType = "";
        this.result = "";
        this.remarks = "";
        this.resultDateTime = "";
    }

    public LabResult(
            String resultId,
            Consultation consultation,
            String resultType,
            String result,
            String remarks,
            String resultDateTime
    ) {
        this.resultId = resultId;
        this.consultation = consultation;
        this.resultType = resultType;
        this.result = result;
        this.remarks = remarks;
        this.resultDateTime = resultDateTime;
    }

    @Override
    public String toString() {
        return String.format(
                "Lab Result {%s - %s}",
                resultId,
                this.resultType
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabResult)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        LabResult resultObj = (LabResult) obj;

        return Objects.equals(
                this.resultId,
                resultObj.resultId
        );
    }

    // Getters

    public String getResultId() {
        return this.resultId;
    }

    public Consultation getConsultation() {
        return this.consultation;
    }

    public String getResultType() {
        return this.resultType;
    }

    public String getResult() {
        return this.result;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public String getResultDateTime() {
        return this.resultDateTime;
    }

    // Setters

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setResultDateTime(String resultDateTime) {
        this.resultDateTime = resultDateTime;
    }
    
    public LinkedHashMap<String, String> getLabResultMap() {
        LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
        resultMap.put("resultId", this.resultId);
        resultMap.put("consultationId", this.consultation != null ? this.consultation.getConsultationId() : "");
        resultMap.put("resultType", this.resultType);
        resultMap.put("result", this.result);
        resultMap.put("remarks", this.remarks);
        resultMap.put("resultDateTime", this.resultDateTime);
        return resultMap;
    }
}
