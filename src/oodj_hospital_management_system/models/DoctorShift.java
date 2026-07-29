/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.time.LocalDateTime;
import java.util.Objects;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class DoctorShift {
    public static final String STATUS_SCHEDULED = "Scheduled";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String shiftId;
    private Doctor doctor;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;

    public DoctorShift() {
        this.shiftId = "";
        this.doctor = null;
        this.startDateTime = null;
        this.endDateTime = null;
        this.status = STATUS_SCHEDULED;
    }

    public DoctorShift(
            String shiftId,
            Doctor doctor,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        this.shiftId = shiftId;
        this.doctor = doctor;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = STATUS_SCHEDULED;
    }

    @Override
    public String toString() {
        return String.format(
            "Doctor Shift {%s - %s (%s)}",
            shiftId,
            doctor.getName(),
            DateTime.formattedLocalDateTime(startDateTime)
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

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getStatus() {
        return status;
    }

    // Setters

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setStatus(String status) {

        if (status.equals(STATUS_SCHEDULED)
                || status.equals(STATUS_COMPLETED)
                || status.equals(STATUS_CANCELLED)) {

            this.status = status;
        }
    }
}
