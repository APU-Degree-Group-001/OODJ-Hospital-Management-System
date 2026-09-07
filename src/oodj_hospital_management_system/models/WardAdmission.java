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
public class WardAdmission {
    public enum AdmissionStatus {
        ADMITTED,
        DISCHARGED
    }

    private String admissionId;
    private String patientId;
    private String wardId;
    private String admittingDoctorId;
    private String admittedDateTime;
    private String dischargedDateTime;
    private AdmissionStatus status;
    private String reason;

    public WardAdmission(
            String admissionId,
            String patientId,
            String wardId,
            String admittingDoctorId,
            String admittedDateTime,
            String dischargedDateTime,
            AdmissionStatus status,
            String reason
    ) {
        this.admissionId = admissionId;
        this.patientId = patientId;
        this.wardId = wardId;
        this.admittingDoctorId = admittingDoctorId;
        this.admittedDateTime = admittedDateTime;
        this.dischargedDateTime = dischargedDateTime;
        this.status = status;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return String.format("Ward Admission {%s: patient %s in ward %s}", admissionId, patientId, wardId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WardAdmission)) return false;
        if (obj == this) return true;
        return Objects.equals(this.admissionId, ((WardAdmission) obj).admissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.admissionId);
    }

    // Getters
    public String getAdmissionId() { return admissionId; }
    public String getPatientId() { return patientId; }
    public String getWardId() { return wardId; }
    public String getAdmittingDoctorId() { return admittingDoctorId; }
    public String getAdmittedDateTime() { return admittedDateTime; }
    public String getDischargedDateTime() { return dischargedDateTime; }
    public AdmissionStatus getStatus() { return status; }
    public String getReason() { return reason; }

    // Setters
    public void setDischargedDateTime(String dischargedDateTime) { this.dischargedDateTime = dischargedDateTime; }
    public void setStatus(AdmissionStatus status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }

    public LinkedHashMap<String, String> getWardAdmissionMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("admissionId", admissionId);
        map.put("patientId", patientId);
        map.put("wardId", wardId);
        map.put("admittingDoctorId", admittingDoctorId);
        map.put("admittedDateTime", admittedDateTime);
        map.put("dischargedDateTime", dischargedDateTime != null ? dischargedDateTime : "");
        map.put("status", status.toString());
        map.put("reason", reason != null ? reason : "");
        return map;
    }
}
