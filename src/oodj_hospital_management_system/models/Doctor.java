/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;

/**
 *
 * @author sheaw
 */
public class Doctor extends User {
    public enum ShiftStatus {
      ON_SHIFT,
      OFF_SHIFT
    }
    private ShiftStatus shiftStatus;
    private int assignedShiftHourByMonth;
    private MedicalDepartment department;
    private MedicalManager medicalManager;
    
    public Doctor() {
        super();
    }
    
    public Doctor(
            String id, 
            String name, 
            String pwd, 
            String type, 
            String joinedDateTime
    ) {
        super(id, name, pwd, type, joinedDateTime);
        this.assignedShiftHourByMonth = 0;
        this.shiftStatus = ShiftStatus.OFF_SHIFT;
        this.department = new MedicalDepartment();
        this.medicalManager = new MedicalManager();
    }
    
    public Doctor(
            String id, 
            String name, 
            String pwd, 
            String type, 
            String joinedDateTime,
            int assignedShiftHourByMonth,
            ShiftStatus shiftStatus,
            MedicalDepartment department,
            MedicalManager medicalManager
    ) {
        super(id, name, pwd, type, joinedDateTime);
        this.assignedShiftHourByMonth = assignedShiftHourByMonth;
        this.shiftStatus = shiftStatus;
        this.department = department;
        this.medicalManager = medicalManager;
    }
    
    public int getAssignedShiftHourByMonth() {
        return this.assignedShiftHourByMonth;
    }
    
    public MedicalManager getMedicalManager() {
        return this.medicalManager;
    }
    
    public MedicalDepartment getMedicalDepartment() {
        return this.department;
    }
    
    public void setAssignedShiftHourByMonth(int assignedShiftHourByMonth) {
        this.assignedShiftHourByMonth = assignedShiftHourByMonth;
    }
    
    public void setMedicalManager(MedicalManager medicalManager) {
        this.medicalManager = medicalManager;
    }
    
    public void setMedicalDepartment(MedicalDepartment department) {
        this.department = department;
    }

    public void setShift(ShiftStatus status) {
        shiftStatus = status;
    }
    
    @Override
    public LinkedHashMap<String, String> getUserMap() {
        LinkedHashMap<String, String> userDetails = super.getUserMap();
        userDetails.put("departmentId", department != null ? department.getDepartmentId() : "");
        userDetails.put("medicalManagerId", medicalManager != null ? medicalManager.getId() : "");
        return userDetails;
    }
}
