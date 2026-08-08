/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.Objects;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class HospitalAssetAllocation {
    public static final String STATUS_ALLOCATED = "Allocated";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String allocationId;
    private HospitalAsset hospitalAsset;
    private Consultation consultation;
    private String allocationDateTime;
    private String status;

    public HospitalAssetAllocation() {
        this.allocationId = "";
        this.hospitalAsset = null;
        this.consultation = null;
        this.allocationDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
        this.status = STATUS_ALLOCATED;
    }

    public HospitalAssetAllocation(
            String allocationId,
            HospitalAsset hospitalAsset,
            Consultation consultation,
            String allocationDateTime,
            String status
    ) {
        this.allocationId = allocationId;
        this.hospitalAsset = hospitalAsset;
        this.consultation = consultation;
        this.allocationDateTime = allocationDateTime;
        this.status = status;
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

    public String getAllocationId() {
        return this.allocationId;
    }

    public HospitalAsset getHospitalAsset() {
        return this.hospitalAsset;
    }

    public Consultation getConsultation() {
        return this.consultation;
    }

    public String getAllocationDateTime() {
        return this.allocationDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setHospitalAsset(HospitalAsset hospitalAsset) {
        this.hospitalAsset = hospitalAsset;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public void setAllocationDateTime(String allocationDateTime) {
        this.allocationDateTime = allocationDateTime;
    }

    public void setStatus(String status) {
        if (status.equals(STATUS_ALLOCATED)
                || status.equals(STATUS_COMPLETED)
                || status.equals(STATUS_CANCELLED)) {
            this.status = status;
        }
    }
}
