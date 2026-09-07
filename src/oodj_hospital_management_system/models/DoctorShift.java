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
public class DoctorShift {
    public enum DoctorShiftStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        OVERDUE,
        CANCELLED
    }

    private String shiftId;
    private Doctor doctor;
    private String startDateTime;
    private String endDateTime;
    private String actualStartDateTime;
    private String actualEndDateTime;
    private DoctorShiftStatus status;
    private MedicalManager medicalManager;

    public DoctorShift() {
        this.shiftId = "";
        this.doctor = null;
        this.startDateTime = null;
        this.endDateTime = null;
        this.status = DoctorShiftStatus.SCHEDULED;
    }

    public DoctorShift(
            String shiftId,
            Doctor doctor,
            String startDateTime,
            String endDateTime,
            String actualStartDateTime,
            String actualEndDateTime,
            DoctorShiftStatus status,
            MedicalManager medicalManager
    ) {
        this.shiftId = shiftId;
        this.doctor = doctor;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.actualStartDateTime = actualStartDateTime;
        this.actualEndDateTime = actualEndDateTime;
        this.status = status;
        this.medicalManager = medicalManager;       
    }

    @Override
    public String toString() {
        return String.format(
            "Doctor Shift {%s: %s on %s (%s - %s)}%n Assigned By: %s",
            shiftId,
            doctor.getName(),
            DateTime.toDateStringWords(DateTime.getDate(DateTime.toDateTimeObject(startDateTime))),
            DateTime.toTimeString(DateTime.getTime(DateTime.toDateTimeObject(startDateTime))),
            DateTime.toTimeString(DateTime.getTime(DateTime.toDateTimeObject(endDateTime))),
            medicalManager.getName()
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof DoctorShift)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        DoctorShift shiftObj = (DoctorShift) obj;
        return Objects.equals(this.shiftId, shiftObj.shiftId);
    }

    // Getters

    public String getShiftId() {
        return shiftId;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public String getActualStartDateTime() {
        return actualStartDateTime;
    }

    public String getActualEndDateTime() {
        return actualEndDateTime;
    }

    public DoctorShiftStatus getStatus() {
        return status;
    }

    public MedicalManager getMedicalManager() {
        return medicalManager;
    }

    // Setters
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setActualStartDateTime(String actualStartDateTime) {
        this.actualStartDateTime = actualStartDateTime;
    }

    public void setActualEndDateTime(String actualEndDateTime) {
        this.actualEndDateTime = actualEndDateTime;
    }

    public void setStatus(DoctorShiftStatus status) {
        this.status = status;
    }

    public void setMedicalManager(MedicalManager medicalManager) {
        this.medicalManager = medicalManager;
    }
    
    public LinkedHashMap<String, String> getDoctorShiftMap() {
        // shiftId, doctorId, startDateTime, endDateTime, status, medicalManagerId
        LinkedHashMap<String, String> shiftDetails = new LinkedHashMap<>();
        shiftDetails.put("shiftId", this.shiftId);
        shiftDetails.put("doctorId", this.doctor.getId());
        shiftDetails.put("startDateTime", this.startDateTime);
        shiftDetails.put("endDateTime", this.endDateTime);
        shiftDetails.put("actualStartDateTime", this.actualStartDateTime);
        shiftDetails.put("actualEndDateTime", this.actualEndDateTime);
        shiftDetails.put("status", this.status.toString());
        shiftDetails.put("medicalManagerId", this.medicalManager.getId());
        return shiftDetails;
    }
}
