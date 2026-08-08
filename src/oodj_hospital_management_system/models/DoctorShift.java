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
public class DoctorShift {
    public enum DoctorShiftStatus {
        SCHEDULED,
        COMPLETED,
        IN_PROGRESS,
        CANCELLED
    }

    private String shiftId;
    private Doctor doctor;
    private String startDateTime;
    private String endDateTime;
    private DoctorShiftStatus status;

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
            DoctorShiftStatus status
    ) {
        this.shiftId = shiftId;
        this.doctor = doctor;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
            "Doctor Shift {%s: %s on %s (%s - %s)}",
            shiftId,
            doctor.getName(),
            DateTime.toDateStringWords(DateTime.getDate(DateTime.toDateTimeObject(startDateTime))),
            DateTime.toTimeString(DateTime.getTime(DateTime.toDateTimeObject(startDateTime))),
            DateTime.toTimeString(DateTime.getTime(DateTime.toDateTimeObject(endDateTime)))
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

    public DoctorShiftStatus getStatus() {
        return status;
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

    public void setStatus(DoctorShiftStatus status) {
        this.status = status;
    }
}
