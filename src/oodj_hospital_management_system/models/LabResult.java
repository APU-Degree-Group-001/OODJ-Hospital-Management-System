/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;
import java.util.LinkedHashMap;
import java.util.Objects;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.services.LabResultTypeManagement;
/**
 *
 * @author sheaw
 */
public class LabResult {
    private String resultId;
    private String consultationId;
    private String resultType;
    private String result;
    private String remarks;
    private String resultDateTime;

    public LabResult() {
        this.resultId = "";
        this.consultationId = "";
        this.resultType = "";
        this.result = "";
        this.remarks = "";
        this.resultDateTime = "";
    }

    public LabResult(
            String resultId,
            String consultationId,
            String resultType,
            String result,
            String remarks,
            String resultDateTime
    ) {
        this.resultId = resultId;
        this.consultationId = consultationId;
        this.resultType = resultType;
        this.result = result;
        this.remarks = remarks;
        this.resultDateTime = resultDateTime;
    }

    public LabResult(
            String resultId,
            Consultation consultation,
            String resultType,
            String result,
            String remarks,
            String resultDateTime
    ) {
        this(
            resultId,
            consultation != null ? consultation.getConsultationId() : "",
            resultType,
            result,
            remarks,
            resultDateTime
        );
    }

    // Convenience constructor - accepts a LabResultType object, stores only its typeName
    public LabResult(
            String resultId,
            String consultationId,
            LabResultType labResultType,
            String result,
            String remarks,
            String resultDateTime
    ) {
        this(
            resultId,
            consultationId,
            labResultType != null ? labResultType.getTypeName() : "",
            result,
            remarks,
            resultDateTime
        );
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

    public String getConsultationId() {
        return this.consultationId;
    }

    public Consultation getConsultation() {
        if (this.consultationId == null || this.consultationId.isEmpty()) {
            return null;
        }
        return ConsultationManagement.findConsultationById(this.consultationId);
    }

    public String getResultType() {
        return this.resultType;
    }

    public LabResultType getLabResultType() {
        if (this.resultType == null || this.resultType.isEmpty()) {
            return null;
        }
        return LabResultTypeManagement.findLabResultTypeByName(this.resultType);
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
        this.consultationId = consultation != null ? consultation.getConsultationId() : "";
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    // Sets the result type via a LabResultType object, storing only its typeName
    public void setLabResultType(LabResultType labResultType) {
        this.resultType = labResultType != null ? labResultType.getTypeName() : "";
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
        resultMap.put("resultId", this.resultId != null ? this.resultId : "");
        resultMap.put("consultationId", this.consultationId != null ? this.consultationId : "");
        resultMap.put("resultType", this.resultType != null ? this.resultType : "");
        resultMap.put("result", this.result != null ? this.result : "");
        resultMap.put("remarks", this.remarks != null ? this.remarks : "");
        resultMap.put("resultDateTime", this.resultDateTime != null ? this.resultDateTime : "");
        return resultMap;
    }
}