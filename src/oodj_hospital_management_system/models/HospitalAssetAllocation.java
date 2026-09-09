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
    private String doctorId;
    private String scheduledStartDateTime;
    private String scheduledEndDateTime;
    private String allocationDateTime;
    private String releasedDateTime;
    public HospitalAssetAllocation() {
        this.allocationId = "";
        this.assetId = "";
        this.doctorId = "";
        this.scheduledStartDateTime = "";
        this.scheduledEndDateTime = "";
        this.allocationDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
        this.releasedDateTime = "";
    }
    public HospitalAssetAllocation(
            String allocationId,
            String assetId,
            String doctorId,
            String scheduledStartDateTime,
            String scheduledEndDateTime,
            String allocationDateTime,
            String releasedDateTime
    ) {
        this.allocationId = allocationId;
        this.assetId = assetId;
        this.doctorId = doctorId;
        this.scheduledStartDateTime = scheduledStartDateTime;
        this.scheduledEndDateTime = scheduledEndDateTime;
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
    public String getDoctorId() {
        return this.doctorId;
    }
    public String getScheduledStartDateTime() {
        return this.scheduledStartDateTime;
    }
    public String getScheduledEndDateTime() {
        return this.scheduledEndDateTime;
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
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    public void setScheduledStartDateTime(String scheduledStartDateTime) {
        this.scheduledStartDateTime = scheduledStartDateTime;
    }
    public void setScheduledEndDateTime(String scheduledEndDateTime) {
        this.scheduledEndDateTime = scheduledEndDateTime;
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
        map.put("doctorId", doctorId != null ? doctorId : "");
        map.put("assetId", assetId != null ? assetId : "");
        map.put("scheduledStartDateTime", scheduledStartDateTime != null ? scheduledStartDateTime : "");
        map.put("scheduledEndDateTime", scheduledEndDateTime != null ? scheduledEndDateTime : "");
        map.put("allocatedDateTime", allocationDateTime);
        map.put("releasedDateTime", releasedDateTime != null ? releasedDateTime : "");
        return map;
    }
}