/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *
 * @author sheaw
 */
public class ConsultationSlot {
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_BOOKED = "BOOKED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private String consultationSlotId;
    private DoctorShift doctorShift;
    private String startDateTime;
    private String endDateTime;
    private String status;

    public ConsultationSlot() {
        this.consultationSlotId = "";
        this.doctorShift = null;
        this.startDateTime = null;
        this.endDateTime = null;
        this.status = STATUS_AVAILABLE;
    }

    public ConsultationSlot(
            String consultationSlotId,
            DoctorShift doctorShift,
            String startDateTime,
            String endDateTime,
            String status
    ) {
        this.consultationSlotId = consultationSlotId;
        this.doctorShift = doctorShift;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "Consultation Slot {%s - %s}",
                this.consultationSlotId,
                this.status
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConsultationSlot)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        ConsultationSlot slotObj = (ConsultationSlot) obj;
        return Objects.equals(this.consultationSlotId, slotObj.consultationSlotId);
    }

    // Getters

    public String getConsultationSlotId() {
        return consultationSlotId;
    }

    public DoctorShift getDoctorShift() {
        return doctorShift;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public String getStatus() {
        return status;
    }

    // Setters

    public void setDoctorShift(DoctorShift doctorShift) {
        this.doctorShift = doctorShift;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setStatus(String status) {
        if (status.equals(STATUS_AVAILABLE)
                || status.equals(STATUS_BOOKED)
                || status.equals(STATUS_CANCELLED)) {
            this.status = status;
        }
    }
    
    public LinkedHashMap<String, String> getConsultationSlotMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("consultationSlotId", consultationSlotId);
        map.put("doctorShiftId", doctorShift.getShiftId());
        map.put("startDateTime", startDateTime);
        map.put("endDateTime", endDateTime);
        map.put("status", status);
        return map;
    }
}
