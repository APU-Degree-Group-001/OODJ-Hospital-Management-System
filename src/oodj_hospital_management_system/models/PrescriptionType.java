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
public class PrescriptionType {
    private String prescriptionTypeId;
    private String typeName;
    private double baseRate;
    public enum PrescriptionTypeStatus {
        ACTIVE,
        INACTIVE
    }
    private PrescriptionTypeStatus status;
    public PrescriptionType() {
//        prescriptionTypeId, typeName, baseRate, status
        this.prescriptionTypeId = "";
        this.typeName = "";
        this.baseRate = 0.0;
        this.status = PrescriptionTypeStatus.ACTIVE;
    }
    public PrescriptionType(
            String prescriptionTypeId,
            String typeName,
            double baseRate,
            PrescriptionTypeStatus status
    ) {
        this.prescriptionTypeId = prescriptionTypeId;
        this.typeName = typeName;
        this.baseRate = baseRate;
        this.status = status;
    }
    @Override
    public String toString() {
        return String.format(
                "PrescriptionType {%s - %s}",
                this.prescriptionTypeId,
                this.typeName
        );
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PrescriptionType)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        PrescriptionType prescriptionTypeObj = (PrescriptionType) obj;
        return Objects.equals(this.prescriptionTypeId, prescriptionTypeObj.prescriptionTypeId);
    }
    public void deactivate() {
        if (status != PrescriptionTypeStatus.ACTIVE) {
            throw new IllegalStateException(
                "Only an active prescription type can be deactivated."
            );
        }
        status = PrescriptionTypeStatus.INACTIVE;
    }
    public void activate() {
        if (status != PrescriptionTypeStatus.INACTIVE) {
            throw new IllegalStateException(
                "Only an inactive prescription type can be activated."
            );
        }
        status = PrescriptionTypeStatus.ACTIVE;
    }
    public String getPrescriptionTypeId() {
        return prescriptionTypeId;
    }
    public String getTypeName() {
        return typeName;
    }
    public double getBaseRate() {
        return baseRate;
    }
    public PrescriptionTypeStatus getStatus() {
        return status;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }
    public void setStatus(PrescriptionTypeStatus status) {
        this.status = status;
    }
    public LinkedHashMap<String, String> getPrescriptionTypeMap() {
        LinkedHashMap<String, String> prescriptionTypeDetails = new LinkedHashMap<>();
        prescriptionTypeDetails.put("prescriptionTypeId", this.prescriptionTypeId);
        prescriptionTypeDetails.put("typeName", this.typeName);
        prescriptionTypeDetails.put("baseRate", String.valueOf(this.baseRate));
        prescriptionTypeDetails.put("status", this.status.name());
        return prescriptionTypeDetails;
    }
}