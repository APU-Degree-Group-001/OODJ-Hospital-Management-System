/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestController;

/**
 *
 * @author paure
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.controllers.DoctorController;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;

public class TestDoctorController {
    private static DoctorController doctorCtrl;
 
    public static void main(String[] args) {
 
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        if (adminStaff == null) {
            System.out.println("Test failed: Admin Staff '#ADMd4e8f' was not found.");
            return;
        }
 
        doctorCtrl = new DoctorController(adminStaff);
 
        // Test loading
//        testGetAllDoctorShifts();
//        testGetAllMedicalDepartments();
 
        // Test lookup
//        testGetDoctorShiftById();
//        testGetMedicalDepartmentById();
 
        // Test search
//        testSearchDoctorShifts();
//        testSearchMedicalDepartments();
 
        // Test sort
//        testSortDoctorShiftsById();
//        testSortDoctorShiftsByStartDateTime();
//        testSortMedicalDepartmentsById();
//        testSortMedicalDepartmentsByName();
 
        // Test calculations / status (read-only, no logging expected)
//        testGetDoctorAttendanceStatus();
//        testCalculateAssignedShiftHourByMonth();
//        testCalculateActualShiftHourByMonth();
 
        // Test creation
        // Uncomment when needed because these modify doctorShifts.txt AND write to the log file
//        testCreateShift();
 
        // Test modification
        // Uncomment when needed because these modify doctorShifts.txt AND write to the log file
//        testCancelShift();

        // Test department CRUD
        // Uncomment when needed because these modify medicalDepartments.txt AND write to the log file
//        testAddNewDepartment();
//        testEditDepartment();
//        testDeleteDepartment();
    }
 
    // =========================================================
    // PRINT METHODS
    // =========================================================
 
    public static void testPrintShifts(List<DoctorShift> shiftList) {
 
        System.out.printf(
                "%-10s %-10s %-20s %-20s %-14s%n",
                "Shift ID", "Doctor", "Start", "End", "Status"
        );
        System.out.println("-----------------------------------------------------------------------------------");
 
        for (DoctorShift shift : shiftList) {
            System.out.printf(
                    "%-10s %-10s %-20s %-20s %-14s%n",
                    shift.getShiftId(),
                    shift.getDoctor() != null ? shift.getDoctor().getId() : "-",
                    shift.getStartDateTime(),
                    shift.getEndDateTime(),
                    shift.getStatus()
            );
        }
    }
 
    public static void testPrintDepartments(List<MedicalDepartment> deptList) {
 
        System.out.printf("%-10s %-20s%n", "Dept ID", "Name");
        System.out.println("-----------------------------------------");
 
        for (MedicalDepartment dept : deptList) {
            System.out.printf("%-10s %-20s%n", dept.getDepartmentId(), dept.getDepartmentName());
        }
    }
 
    // =========================================================
    // LOAD TESTS
    // =========================================================
 
    static void testGetAllDoctorShifts() {
 
        System.out.println("\n===== TEST GET ALL DOCTOR SHIFTS (via Controller) =====");
 
        List<DoctorShift> shifts = doctorCtrl.getAllDoctorShifts();
 
        System.out.println("Total Shifts: " + shifts.size());
 
        testPrintShifts(shifts);
    }
 
    static void testGetAllMedicalDepartments() {
 
        System.out.println("\n===== TEST GET ALL MEDICAL DEPARTMENTS (via Controller) =====");
 
        List<MedicalDepartment> depts = doctorCtrl.getAllMedicalDepartments();
 
        System.out.println("Total Departments: " + depts.size());
 
        testPrintDepartments(depts);
    }
 
    // =========================================================
    // LOOKUP TESTS
    // =========================================================
 
    static void testGetDoctorShiftById() {
 
        System.out.println("\n===== TEST GET DOCTOR SHIFT BY ID (via Controller) =====");
 
        List<DoctorShift> shifts = doctorCtrl.getAllDoctorShifts();
 
        if (shifts.isEmpty()) {
            System.out.println("No shifts available.");
            return;
        }
 
        String shiftId = shifts.get(0).getShiftId();
 
        DoctorShift shift = doctorCtrl.getDoctorShiftById(shiftId);
 
        if (shift != null) {
            System.out.println("Shift found: " + shift.getShiftId() + " | " + shift.getStatus());
        } else {
            System.out.println("Test failed: Shift not found.");
        }
    }
 
    static void testGetMedicalDepartmentById() {
 
        System.out.println("\n===== TEST GET MEDICAL DEPARTMENT BY ID (via Controller) =====");
 
        List<MedicalDepartment> depts = doctorCtrl.getAllMedicalDepartments();
 
        if (depts.isEmpty()) {
            System.out.println("No departments available.");
            return;
        }
 
        String deptId = depts.get(0).getDepartmentId();
 
        MedicalDepartment dept = doctorCtrl.getMedicalDepartmentById(deptId);
 
        if (dept != null) {
            System.out.println("Department found: " + dept.getDepartmentId() + " | " + dept.getDepartmentName());
        } else {
            System.out.println("Test failed: Department not found.");
        }
    }
 
    // =========================================================
    // SEARCH TESTS
    // =========================================================
 
    static void testSearchDoctorShifts() {
 
        System.out.println("\n===== TEST SEARCH DOCTOR SHIFTS (via Controller) =====");
 
        String keyword = "#DOC001";
 
        List<DoctorShift> result = doctorCtrl.searchDoctorShifts(keyword);
 
        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched shifts: " + result.size());
 
        testPrintShifts(result);
    }
 
    static void testSearchMedicalDepartments() {
 
        System.out.println("\n===== TEST SEARCH MEDICAL DEPARTMENTS (via Controller) =====");
 
        String keyword = "Cardio";
 
        List<MedicalDepartment> result = doctorCtrl.searchMedicalDepartments(keyword);
 
        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched departments: " + result.size());
 
        testPrintDepartments(result);
    }
 
    // =========================================================
    // SORT TESTS
    // =========================================================
 
    static void testSortDoctorShiftsById() {
 
        System.out.println("\n===== TEST SORT DOCTOR SHIFTS BY ID (via Controller) =====");
 
        List<DoctorShift> shifts = doctorCtrl.getAllDoctorShifts();
        List<DoctorShift> sorted = doctorCtrl.sortDoctorShiftsById(shifts);
 
        testPrintShifts(sorted);
    }
 
    static void testSortDoctorShiftsByStartDateTime() {
 
        System.out.println("\n===== TEST SORT DOCTOR SHIFTS BY START DATETIME (via Controller) =====");
 
        List<DoctorShift> shifts = doctorCtrl.getAllDoctorShifts();
        List<DoctorShift> sorted = doctorCtrl.sortDoctorShiftsByStartDateTime(shifts);
 
        testPrintShifts(sorted);
    }
 
    static void testSortMedicalDepartmentsById() {
 
        System.out.println("\n===== TEST SORT MEDICAL DEPARTMENTS BY ID (via Controller) =====");
 
        List<MedicalDepartment> depts = doctorCtrl.getAllMedicalDepartments();
        List<MedicalDepartment> sorted = doctorCtrl.sortMedicalDepartmentsById(depts);
 
        testPrintDepartments(sorted);
    }
 
    static void testSortMedicalDepartmentsByName() {
 
        System.out.println("\n===== TEST SORT MEDICAL DEPARTMENTS BY NAME (via Controller) =====");
 
        List<MedicalDepartment> depts = doctorCtrl.getAllMedicalDepartments();
        List<MedicalDepartment> sorted = doctorCtrl.sortMedicalDepartmentsByName(depts);
 
        testPrintDepartments(sorted);
    }
 
    // =========================================================
    // STATUS / CALCULATION TESTS (read-only)
    // =========================================================
 
    static void testGetDoctorAttendanceStatus() {
 
        System.out.println("\n===== TEST GET DOCTOR ATTENDANCE STATUS (via Controller) =====");
 
        Doctor doctor = (Doctor) UserManagement.getDoctorById("#DOC001");
 
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
 
        System.out.println("Attendance Status: " + doctorCtrl.getDoctorAttendanceStatus(doctor));
    }
 
    static void testCalculateAssignedShiftHourByMonth() {
 
        System.out.println("\n===== TEST CALCULATE ASSIGNED SHIFT HOUR BY MONTH (via Controller) =====");
 
        Doctor doctor = (Doctor) UserManagement.getDoctorById("#DOC001");
 
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
 
        System.out.println("Assigned Hours: " + doctorCtrl.calculateAssignedShiftHourByMonth(doctor));
    }
 
    static void testCalculateActualShiftHourByMonth() {
 
        System.out.println("\n===== TEST CALCULATE ACTUAL SHIFT HOUR BY MONTH (via Controller) =====");
 
        Doctor doctor = (Doctor) UserManagement.getDoctorById("#DOC001");
 
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
 
        System.out.println("Actual Hours: " + doctorCtrl.calculateActualShiftHourByMonth(doctor));
    }
 
    // =========================================================
    // CREATE TEST
    // =========================================================
 
    static void testCreateShift() {
 
        System.out.println("\n===== TEST CREATE SHIFT (via Controller) =====");
 
        Doctor doctor = (Doctor) UserManagement.getDoctorById("#DOC001");
 
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
 
        // Adjust date/time format to match whatever your DateTime util expects
        LinkedHashMap<String, String> result = doctorCtrl.createShift(
                doctor,
                "2026-09-01 09:00",
                "2026-09-01 17:00"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Shift ID : " + result.get("newShiftId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    // =========================================================
    // MODIFY TEST
    // =========================================================
 
    static void testCancelShift() {
 
        System.out.println("\n===== TEST CANCEL SHIFT (via Controller) =====");
 
        List<DoctorShift> shifts = doctorCtrl.getAllDoctorShifts();
 
        if (shifts.isEmpty()) {
            System.out.println("No shifts available to cancel.");
            return;
        }
 
        DoctorShift shift = shifts.get(shifts.size() - 1);
 
        LinkedHashMap<String, String> result = doctorCtrl.cancelShift(shift);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
    
    // =========================================================
    // MEDICAL DEPARTMENT CRUD TESTS
    // =========================================================
 
    static void testAddNewDepartment() {
 
        System.out.println("\n===== TEST ADD NEW DEPARTMENT (via Controller) =====");
 
        LinkedHashMap<String, String> result = doctorCtrl.addNewDepartment(
                "Test Department",
                "Department created via TestDoctorController"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Department ID : " + result.get("newDepartmentId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    static void testEditDepartment() {
 
        System.out.println("\n===== TEST EDIT DEPARTMENT (via Controller) =====");
 
        List<MedicalDepartment> depts = doctorCtrl.getAllMedicalDepartments();
 
        if (depts.isEmpty()) {
            System.out.println("No departments available to edit.");
            return;
        }
 
        String departmentId = depts.get(depts.size() - 1).getDepartmentId();
 
        LinkedHashMap<String, String> result = doctorCtrl.editDepartment(
                "Updated Department Name",
                "Updated via TestDoctorController",
                departmentId
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testDeleteDepartment() {
 
        System.out.println("\n===== TEST DELETE DEPARTMENT (via Controller) =====");
 
        List<MedicalDepartment> depts = doctorCtrl.getAllMedicalDepartments();
 
        if (depts.isEmpty()) {
            System.out.println("No departments available to delete.");
            return;
        }
 
        String departmentId = depts.get(depts.size() - 1).getDepartmentId();
 
        LinkedHashMap<String, String> result = doctorCtrl.deleteDepartment(departmentId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}
