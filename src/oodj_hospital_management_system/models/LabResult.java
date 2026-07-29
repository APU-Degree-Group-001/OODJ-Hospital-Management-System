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
public class LabResult {
    private String resultId;
    private String resultName;
    private String remarks;

    public LabResult() {
        this.resultId = "";
        this.resultName = "";
        this.remarks = "";
    }

    public LabResult(
            String resultId,
            String resultName,
            String remarks
    ) {
        this.resultId = resultId;
        this.resultName = resultName;
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return String.format("Lab Result {%s}", resultId);
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
        return Objects.equals(this.resultId, resultObj.resultId);
    }

    // Getters

    public String getResultId() {
        return this.resultId;
    }

    public String getResultName() {
        return this.resultName;
    }

    public String getRemarks() {
        return this.remarks;
    }

    // Setters

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

class BloodTest extends LabResult {

    private double hemoglobin;
    private double whiteBloodCellCount;
    private double plateletCount;

    public BloodTest(
            String resultId,
            String remarks,
            double hemoglobin,
            double whiteBloodCellCount,
            double plateletCount
    ) {
        super(resultId, "Blood Test", remarks);

        this.hemoglobin = hemoglobin;
        this.whiteBloodCellCount = whiteBloodCellCount;
        this.plateletCount = plateletCount;
    }

    public double getHemoglobin() {
        return this.hemoglobin;
    }

    public double getWhiteBloodCellCount() {
        return this.whiteBloodCellCount;
    }

    public double getPlateletCount() {
        return this.plateletCount;
    }

    public void setHemoglobin(double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public void setWhiteBloodCellCount(double whiteBloodCellCount) {
        this.whiteBloodCellCount = whiteBloodCellCount;
    }

    public void setPlateletCount(double plateletCount) {
        this.plateletCount = plateletCount;
    }

}

class UrineTest extends LabResult {

    private double pH;
    private boolean proteinDetected;
    private boolean glucoseDetected;

    public UrineTest(
            String resultId,
            String remarks,
            double pH,
            boolean proteinDetected,
            boolean glucoseDetected
    ) {
        super(resultId, "Urine Test", remarks);

        this.pH = pH;
        this.proteinDetected = proteinDetected;
        this.glucoseDetected = glucoseDetected;
    }

    public double getPH() {
        return this.pH;
    }

    public boolean isProteinDetected() {
        return this.proteinDetected;
    }

    public boolean isGlucoseDetected() {
        return this.glucoseDetected;
    }

    public void setPH(double pH) {
        this.pH = pH;
    }

    public void setProteinDetected(boolean proteinDetected) {
        this.proteinDetected = proteinDetected;
    }

    public void setGlucoseDetected(boolean glucoseDetected) {
        this.glucoseDetected = glucoseDetected;
    }

}

class XRay extends LabResult {

    private String bodyPart;
    private String findings;

    public XRay(
            String resultId,
            String remarks,
            String bodyPart,
            String findings
    ) {
        super(resultId, "X-Ray", remarks);

        this.bodyPart = bodyPart;
        this.findings = findings;
    }

    public String getBodyPart() {
        return this.bodyPart;
    }

    public String getFindings() {
        return this.findings;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

}
