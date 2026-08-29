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
public class ConsultationType {
    private String consultationTypeId;
    private String typeName;
    private double baseRate;

    public enum ConsultationTypeStatus {
        ACTIVE,
        INACTIVE
    }
    private ConsultationTypeStatus status;

    public ConsultationType() {
//        consultationTypeId, typeName, baseRate, status
        this.consultationTypeId = "";
        this.typeName = "";
        this.baseRate = 0.0;
        this.status = ConsultationTypeStatus.ACTIVE;
    }

    public ConsultationType(
            String consultationTypeId,
            String typeName,
            double baseRate,
            ConsultationTypeStatus status
    ) {
        this.consultationTypeId = consultationTypeId;
        this.typeName = typeName;
        this.baseRate = baseRate;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "ConsultationType {%s - %s}",
                this.consultationTypeId,
                this.typeName
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConsultationType)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        ConsultationType consultationTypeObj = (ConsultationType) obj;
        return Objects.equals(this.consultationTypeId, consultationTypeObj.consultationTypeId);
    }

    public void deactivate() {
        if (status != ConsultationTypeStatus.ACTIVE) {
            throw new IllegalStateException(
                "Only an active consultation type can be deactivated."
            );
        }
        status = ConsultationTypeStatus.INACTIVE;
    }

    public void activate() {
        if (status != ConsultationTypeStatus.INACTIVE) {
            throw new IllegalStateException(
                "Only an inactive consultation type can be activated."
            );
        }
        status = ConsultationTypeStatus.ACTIVE;
    }

    public String getConsultationTypeId() {
        return consultationTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public ConsultationTypeStatus getStatus() {
        return status;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public void setStatus(ConsultationTypeStatus status) {
        this.status = status;
    }

    public LinkedHashMap<String, String> getConsultationTypeMap() {
        LinkedHashMap<String, String> consultationTypeDetails = new LinkedHashMap<>();
        consultationTypeDetails.put("consultationTypeId", this.consultationTypeId);
        consultationTypeDetails.put("typeName", this.typeName);
        consultationTypeDetails.put("baseRate", String.valueOf(this.baseRate));
        consultationTypeDetails.put("status", this.status.name());
        return consultationTypeDetails;
    }
}