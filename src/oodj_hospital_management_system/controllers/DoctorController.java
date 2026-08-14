/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author paure
 */
public class DoctorController {
    private DoctorManagement doctorServices;
    private User activeUser;

    public DoctorController(User activeUser) {
        this.activeUser = activeUser;
        this.doctorServices = new DoctorManagement(activeUser);
    }
    
    // Get Shifts, Departments
    public List<DoctorShift> getAllDoctorShifts() {
        return DoctorManagement.getAllDoctorShifts();
    }
 
    public List<MedicalDepartment> getAllMedicalDepartments() {
        return DoctorManagement.getAllMedicalDepartments();
    }
    
    // Lookup by ID
    public DoctorShift getDoctorShiftById(String shiftId) {
        return DoctorManagement.getDoctorShiftById(shiftId);
    }
 
    public MedicalDepartment getMedicalDepartmentById(String departmentId) {
        return DoctorManagement.getMedicalDepartmentById(departmentId);
    }
    
    // Search and Sort
    public List<DoctorShift> searchDoctorShifts(String keywords) {
        return doctorServices.searchDoctorShifts(keywords);
    }
 
    public List<DoctorShift> searchDoctorShifts(String keywords, List<DoctorShift> shiftList) {
        return doctorServices.searchDoctorShifts(keywords, shiftList);
    }
 
    public List<DoctorShift> sortDoctorShiftsById(List<DoctorShift> list) {
        return doctorServices.sortDoctorShiftsById(list);
    }
 
    public List<DoctorShift> sortDoctorShiftsByStartDateTime(List<DoctorShift> list) {
        return doctorServices.sortDoctorShiftsByStartDateTime(list);
    }
 
    public List<MedicalDepartment> searchMedicalDepartments(String keywords) {
        return doctorServices.searchMedicalDepartments(keywords);
    }
 
    public List<MedicalDepartment> searchMedicalDepartments(String keywords, List<MedicalDepartment> departmentList) {
        return doctorServices.searchMedicalDepartments(keywords, departmentList);
    }
 
    public List<MedicalDepartment> sortMedicalDepartmentsById(List<MedicalDepartment> list) {
        return doctorServices.sortMedicalDepartmentsById(list);
    }
 
    public List<MedicalDepartment> sortMedicalDepartmentsByName(List<MedicalDepartment> list) {
        return doctorServices.sortMedicalDepartmentsByName(list);
    }
    
    // Shift, Shift Hour
    public Doctor.ShiftStatus getDoctorAttendanceStatus(Doctor doctor) {
        return DoctorManagement.getDoctorAttendanceStatus(doctor);
    }
 
    public void updateShiftStatus(DoctorShift shift) {
        DoctorManagement.updateShiftStatus(shift);
    }
 
    public int calculateAssignedShiftHourByMonth(Doctor doctor) {
        return DoctorManagement.calculateAssignedShiftHourByMonth(doctor);
    }
 
    public String calculateActualShiftHourByMonth(Doctor doctor) {
        return DoctorManagement.calculateActualShiftHourByMonth(doctor);
    }
    
    // CRUD
    public LinkedHashMap<String, String> createShift(Doctor doctor, String startDateTime, String endDateTime) {
        LinkedHashMap<String, String> msg = doctorServices.createShift(doctor, startDateTime, endDateTime);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Doctor Shift {%s} for Doctor {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newShiftId"), doctor.getId(), doctor.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a doctor shift for Doctor {%s - %s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    doctor.getId(), doctor.getName(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> cancelShift(DoctorShift shift) {
        LinkedHashMap<String, String> msg = doctorServices.cancelShift(shift);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} cancelled Doctor Shift {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), shift.getShiftId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to cancel Doctor Shift {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), shift.getShiftId(), msg.get("msg")));
        }
        return msg;
    }
}
