/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;
import java.util.Objects;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class HospitalAssetAllocation {
    private String allocationId;
    private String assetId;
    private String consultationId;
    private String allocationDateTime;
    private String releasedDateTime;

    public HospitalAssetAllocation() {
        this.allocationId = "";
        this.assetId = "";
        this.consultationId = "";
        this.allocationDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
        this.releasedDateTime = "";
    }

    public HospitalAssetAllocation(
            String allocationId,
            String assetId,
            String consultationId,
            String allocationDateTime,
            String releasedDateTime
    ) {
        this.allocationId = allocationId;
        this.assetId = assetId;
        this.consultationId = consultationId;
        this.allocationDateTime = allocationDateTime;
        this.releasedDateTime = releasedDateTime;
    }

    @Override
    public String toString() {
        return String.format(
                "Hospital Asset Allocation {%s}",
                this.allocationId
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HospitalAssetAllocation)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        HospitalAssetAllocation allocationObj =
                (HospitalAssetAllocation) obj;
        return Objects.equals(
                this.allocationId,
                allocationObj.allocationId
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.allocationId);
    }

    // Accessor Methods

    public String getAllocationId() {
        return this.allocationId;
    }

    public String getAssetId() {
        return this.assetId;
    }

    public String getConsultationId() {
        return this.consultationId;
    }

    public String getAllocationDateTime() {
        return this.allocationDateTime;
    }

    public String getReleasedDateTime() {
        return this.releasedDateTime;
    }

    // Mutator Methods

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public void setAllocationDateTime(String allocationDateTime) {
        this.allocationDateTime = allocationDateTime;
    }

    public void setReleasedDateTime(String releasedDateTime) {
        this.releasedDateTime = releasedDateTime;
    }


    public LinkedHashMap<String, String> getHospitalAssetAllocationMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("allocationId", allocationId);
        map.put("consultationId", consultationId != null ? consultationId : "");
        map.put("assetId", assetId != null ? assetId : "");
        map.put("allocatedDateTime", allocationDateTime);
        map.put("releasedDateTime", releasedDateTime != null ? releasedDateTime : "");
        return map;
    }
}