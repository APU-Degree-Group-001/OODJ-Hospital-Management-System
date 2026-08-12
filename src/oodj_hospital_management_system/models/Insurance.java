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
public class Insurance {
    private String insuranceId;
    private String insuranceName;
    private boolean accepted;
    private double coveragePercentage; // e.g. 0.8 = insurance covers 80%, patient pays 20%

    public Insurance(
            String insuranceId,
            String insuranceName,
            boolean accepted,
            double coveragePercentage
    ) {
        this.insuranceId = insuranceId;
        this.insuranceName = insuranceName;
        this.accepted = accepted;
        this.coveragePercentage = coveragePercentage;
    }

    @Override
    public String toString() {
        return String.format("Insurance {%s - %s - %.0f%% coverage}", this.insuranceId, this.insuranceName, this.coveragePercentage * 100);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Insurance)) return false;
        if (obj == this) return true;
        Insurance insObj = (Insurance) obj;
        return Objects.equals(this.insuranceId, insObj.insuranceId);
    }

    // Getters
    public String getInsuranceId() { return insuranceId; }
    public String getInsuranceName() { return insuranceName; }
    public boolean isAccepted() { return accepted; }
    public double getCoveragePercentage() { return coveragePercentage; }

    // Setters
    public void setInsuranceId(String insuranceId) { this.insuranceId = insuranceId; }
    public void setInsuranceName(String insuranceName) { this.insuranceName = insuranceName; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    public void setCoveragePercentage(double coveragePercentage) { this.coveragePercentage = coveragePercentage; }

    // Business logic
    public double calculatePayableAmount(double totalAmount) {
        if (!accepted) {
            return Math.round(totalAmount * 100.0) / 100.0;
        }
        double payable = totalAmount * (1 - coveragePercentage);
        return Math.round(payable * 100.0) / 100.0;
    }

    public LinkedHashMap<String, String> getInsuranceMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("insuranceId", insuranceId);
        map.put("insuranceName", insuranceName);
        map.put("accepted", Boolean.toString(accepted));
        map.put("coveragePercentage", Double.toString(coveragePercentage));
        return map;
    }
}
