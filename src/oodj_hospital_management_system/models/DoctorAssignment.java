/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author sheaw
 */
public class DoctorAssignment {
    private String assignmentId;
    private Doctor doctor;
    private MedicalManager medicalManager;
    private LocalDateTime assignedDateTime;

    public DoctorAssignment() {
        this.assignmentId = "";
        this.doctor = null;
        this.medicalManager = null;
        this.assignedDateTime = null;
    }

    public DoctorAssignment(
            String assignmentId,
            Doctor doctor,
            MedicalManager medicalManager,
            LocalDateTime assignedDateTime
    ) {
        this.assignmentId = assignmentId;
        this.doctor = doctor;
        this.medicalManager = medicalManager;
        this.assignedDateTime = assignedDateTime;
    }

    @Override
    public String toString() {
        return String.format(
                "Doctor Assignment {%s - %s}",
                this.assignmentId,
                this.doctor.getName()
        );
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof DoctorAssignment)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        DoctorAssignment assignmentObj = (DoctorAssignment) obj;

        return Objects.equals(
                this.assignmentId,
                assignmentObj.assignmentId
        );
    }

    // Getters

    public String getAssignmentId() {
        return this.assignmentId;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public MedicalManager getMedicalManager() {
        return this.medicalManager;
    }

    public LocalDateTime getAssignedDateTime() {
        return this.assignedDateTime;
    }

    // Setters

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setMedicalManager(MedicalManager medicalManager) {
        this.medicalManager = medicalManager;
    }

    public void setAssignedDateTime(LocalDateTime assignedDateTime) {
        this.assignedDateTime = assignedDateTime;
    }
}
