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
public class LabResultType {
    private String labResultTypeId;
    private String typeName;
    private double baseRate;
    public enum LabResultTypeStatus {
        ACTIVE,
        INACTIVE
    }
    private LabResultTypeStatus status;
    public LabResultType() {
//        labResultTypeId, typeName, baseRate, status
        this.labResultTypeId = "";
        this.typeName = "";
        this.baseRate = 0.0;
        this.status = LabResultTypeStatus.ACTIVE;
    }
    public LabResultType(
            String labResultTypeId,
            String typeName,
            double baseRate,
            LabResultTypeStatus status
    ) {
        this.labResultTypeId = labResultTypeId;
        this.typeName = typeName;
        this.baseRate = baseRate;
        this.status = status;
    }
    @Override
    public String toString() {
        return String.format(
                "LabResultType {%s - %s}",
                this.labResultTypeId,
                this.typeName
        );
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabResultType)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        LabResultType labResultTypeObj = (LabResultType) obj;
        return Objects.equals(this.labResultTypeId, labResultTypeObj.labResultTypeId);
    }
    public void deactivate() {
        if (status != LabResultTypeStatus.ACTIVE) {
            throw new IllegalStateException(
                "Only an active lab result type can be deactivated."
            );
        }
        status = LabResultTypeStatus.INACTIVE;
    }
    public void activate() {
        if (status != LabResultTypeStatus.INACTIVE) {
            throw new IllegalStateException(
                "Only an inactive lab result type can be activated."
            );
        }
        status = LabResultTypeStatus.ACTIVE;
    }
    public String getLabResultTypeId() {
        return labResultTypeId;
    }
    public String getTypeName() {
        return typeName;
    }
    public double getBaseRate() {
        return baseRate;
    }
    public LabResultTypeStatus getStatus() {
        return status;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }
    public void setStatus(LabResultTypeStatus status) {
        this.status = status;
    }
    public LinkedHashMap<String, String> getLabResultTypeMap() {
        LinkedHashMap<String, String> labResultTypeDetails = new LinkedHashMap<>();
        labResultTypeDetails.put("labResultTypeId", this.labResultTypeId);
        labResultTypeDetails.put("typeName", this.typeName);
        labResultTypeDetails.put("baseRate", String.valueOf(this.baseRate));
        labResultTypeDetails.put("status", this.status.name());
        return labResultTypeDetails;
    }
}