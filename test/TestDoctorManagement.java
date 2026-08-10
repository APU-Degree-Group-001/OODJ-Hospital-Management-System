/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;

public class TestDoctorManagement {
    private static DoctorManagement doctorMng;
    
    public static void main(String[] args) {
        
        // Get the active user once
        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println(
                    "Test failed: Medical Manager '#MNG74295' was not found."
            );
            return;
        }

        // Create DoctorManagement once
        doctorMng = new DoctorManagement(manager);

        // Test loading
//        testLoadDoctorShifts();
//        testLoadMedicalDepartments();

        // Test searching
//        testSearchDoctorShifts();
//        testSearchDoctorShiftsFromGivenList();
//        testSearchMedicalDepartments();
//        testSearchMedicalDepartmentsFromGivenList();

        // Test sorting
//        testSortDoctorShiftsById();
//        testSortDoctorShiftsByStartDateTime();
//        testSortMedicalDepartmentsById();
//        testSortMedicalDepartmentsByName();

        // Test calculations
//        testCalculateAssignedShiftHours();
//        testCalculateActualShiftHours();
//        testDoctorAttendanceStatus();

        // Test ID generation
//        testGenerateShiftId();

        // Test shift creation
        // Uncomment when needed because this modifies doctorShifts.txt
         testCreateShift();

        // Test shift cancellation
        // Uncomment when needed because this modifies doctorShifts.txt
//         testCancelShift();
    }

    // =========================================================
    // PRINT METHODS
    // =========================================================

    public static void testPrintDoctorShifts(List<DoctorShift> shiftList) {

        System.out.printf(
                "%-18s %-20s %-20s %-20s %-15s %-20s%n",
                "Shift ID",
                "Doctor",
                "Start DateTime",
                "End DateTime",
                "Status",
                "Medical Manager"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (DoctorShift shift : shiftList) {

            System.out.printf(
                    "%-18s %-20s %-20s %-20s %-15s %-20s%n",
                    shift.getShiftId(),
                    shift.getDoctor().getName(),
                    shift.getStartDateTime(),
                    shift.getEndDateTime(),
                    shift.getStatus(),
                    shift.getMedicalManager().getName()
            );
        }
    }

    public static void testPrintMedicalDepartments(
            List<MedicalDepartment> departmentList) {

        System.out.printf(
                "%-15s %-20s %-70s%n",
                "Department ID",
                "Department Name",
                "Description"
        );

        System.out.println(
                "------------------------------------------------------------------------------------------------"
        );

        for (MedicalDepartment department : departmentList) {

            System.out.printf(
                    "%-15s %-20s %-70s%n",
                    department.getDepartmentId(),
                    department.getDepartmentName(),
                    department.getDescription()
            );
        }
    }

    // =========================================================
    // LOAD TESTS
    // =========================================================

    static void testLoadDoctorShifts() {

        System.out.println("\n===== TEST LOAD DOCTOR SHIFTS =====");

        List<DoctorShift> shifts =
                DoctorManagement.getAllDoctorShifts();

        System.out.println("Total Doctor Shifts: " + shifts.size());

        testPrintDoctorShifts(shifts);
    }

    static void testLoadMedicalDepartments() {

        System.out.println("\n===== TEST LOAD MEDICAL DEPARTMENTS =====");

        List<MedicalDepartment> departments =
                DoctorManagement.getAllMedicalDepartments();

        System.out.println(
                "Total Medical Departments: "
                + departments.size()
        );

        testPrintMedicalDepartments(departments);
    }

    // =========================================================
    // GET BY ID TESTS
    // =========================================================

    static void testGetDoctorShiftById() {

        System.out.println("\n===== TEST GET DOCTOR SHIFT BY ID =====");

        List<DoctorShift> shifts =
                DoctorManagement.getAllDoctorShifts();

        if (shifts.isEmpty()) {
            System.out.println("No doctor shifts available.");
            return;
        }

        String shiftId = shifts.get(0).getShiftId();

        DoctorShift shift =
                DoctorManagement.getDoctorShiftById(shiftId);

        if (shift != null) {

            System.out.println("Shift found:");
            System.out.println("Shift ID : " + shift.getShiftId());
            System.out.println("Doctor   : " + shift.getDoctor().getName());
            System.out.println("Status   : " + shift.getStatus());

        } else {
            System.out.println("Test failed: Shift not found.");
        }
    }

    static void testGetMedicalDepartmentById() {

        System.out.println("\n===== TEST GET MEDICAL DEPARTMENT BY ID =====");

        MedicalDepartment department =
                DoctorManagement.getMedicalDepartmentById("#DEPT001");

        if (department != null) {

            System.out.println("Department found:");
            System.out.println(
                    "ID   : " + department.getDepartmentId()
            );
            System.out.println(
                    "Name : " + department.getDepartmentName()
            );

        } else {
            System.out.println("Test failed: Department not found.");
        }
    }

    // =========================================================
    // SEARCH TESTS
    // =========================================================

    static void testSearchDoctorShifts() {

        System.out.println("\n===== TEST SEARCH DOCTOR SHIFTS =====");

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        String keyword = "bug";

        List<DoctorShift> result =
                doctorMng.searchDoctorShifts(keyword);

        System.out.println(
                "Search keyword: " + keyword
        );

        System.out.println(
                "Matched shifts: " + result.size()
        );

        testPrintDoctorShifts(result);
    }

    static void testSearchDoctorShiftsFromGivenList() {

        System.out.println(
                "\n===== TEST SEARCH DOCTOR SHIFTS FROM GIVEN LIST ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        List<DoctorShift> shifts =
                doctorMng.getAllDoctorShifts();

        List<DoctorShift> result =
                doctorMng.searchDoctorShifts(
                        "completed",
                        shifts
                );

        System.out.println(
                "Search keyword: completed"
        );

        System.out.println(
                "Matched shifts: " + result.size()
        );

        testPrintDoctorShifts(result);
    }


    static void testSearchMedicalDepartments() {

        System.out.println(
                "\n===== TEST SEARCH MEDICAL DEPARTMENTS ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        String keyword = "heart";

        List<MedicalDepartment> result =
                doctorMng.searchMedicalDepartments(keyword);

        System.out.println(
                "Search keyword: " + keyword
        );

        System.out.println(
                "Matched departments: " + result.size()
        );

        testPrintMedicalDepartments(result);
    }

    static void testSearchMedicalDepartmentsFromGivenList() {

        System.out.println(
                "\n===== TEST SEARCH MEDICAL DEPARTMENTS FROM GIVEN LIST ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        // Custom list containing only selected departments
        List<MedicalDepartment> departments = new ArrayList<>();

        departments.add(
                doctorMng.getMedicalDepartmentById("#DEPT001")
        );

        departments.add(
                doctorMng.getMedicalDepartmentById("#DEPT002")
        );

        List<MedicalDepartment> result =
                doctorMng.searchMedicalDepartments(
                        "medical",
                        departments
                );

        System.out.println(
                "Search keyword: medical"
        );

        System.out.println(
                "Matched departments: " + result.size()
        );

        testPrintMedicalDepartments(result);
    }

    // =========================================================
    // SORT TESTS
    // =========================================================

    static void testSortDoctorShiftsById() {

        System.out.println(
                "\n===== TEST SORT DOCTOR SHIFTS BY ID ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        List<DoctorShift> shifts =
                doctorMng.getAllDoctorShifts();

        List<DoctorShift> sorted =
                doctorMng.sortDoctorShiftsById(shifts);

        testPrintDoctorShifts(sorted);
    }

    static void testSortDoctorShiftsByStartDateTime() {

        System.out.println(
                "\n===== TEST SORT DOCTOR SHIFTS BY START DATETIME ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        List<DoctorShift> shifts =
                doctorMng.getAllDoctorShifts();

        List<DoctorShift> sorted =
                doctorMng.sortDoctorShiftsByStartDateTime(shifts);

        testPrintDoctorShifts(sorted);
    }

    static void testSortMedicalDepartmentsById() {

        System.out.println(
                "\n===== TEST SORT MEDICAL DEPARTMENTS BY ID ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        List<MedicalDepartment> departments =
                doctorMng.getAllMedicalDepartments();

        List<MedicalDepartment> sorted =
                doctorMng.sortMedicalDepartmentsById(departments);

        testPrintMedicalDepartments(sorted);
    }

    static void testSortMedicalDepartmentsByName() {

        System.out.println(
                "\n===== TEST SORT MEDICAL DEPARTMENTS BY NAME ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        List<MedicalDepartment> departments =
                doctorMng.getAllMedicalDepartments();

        List<MedicalDepartment> sorted =
                doctorMng.sortMedicalDepartmentsByName(departments);

        testPrintMedicalDepartments(sorted);
    }

    // =========================================================
    // SHIFT CALCULATION TESTS
    // =========================================================

    static void testCalculateAssignedShiftHours() {

        System.out.println(
                "\n===== TEST CALCULATE ASSIGNED SHIFT HOURS ====="
        );

        Doctor doctor =
                UserManagement.getDoctorById("#DOCd4e8f");

        if (doctor == null) {
            System.out.println("Test failed: Doctor not found.");
            return;
        }

        int hours =
                DoctorManagement.calculateAssignedShiftHourByMonth(
                        doctor
                );

        System.out.println(
                "Doctor : " + doctor.getName()
        );

        System.out.println(
                "Assigned Hours This Month : " + hours
        );
    }

    static void testCalculateActualShiftHours() {

        System.out.println(
                "\n===== TEST CALCULATE ACTUAL SHIFT HOURS ====="
        );

        Doctor doctor =
                UserManagement.getDoctorById("#DOCd4e8f");

        if (doctor == null) {
            System.out.println("Test failed: Doctor not found.");
            return;
        }

        String hours =
                DoctorManagement.calculateActualShiftHourByMonth(
                        doctor
                );

        System.out.println(
                "Doctor : " + doctor.getName()
        );

        System.out.println(
                "Actual Hours This Month : " + hours
        );
    }

    static void testDoctorAttendanceStatus() {

        System.out.println(
                "\n===== TEST DOCTOR ATTENDANCE STATUS ====="
        );

        Doctor doctor =
                UserManagement.getDoctorById("#DOCd4e8f");

        if (doctor == null) {
            System.out.println("Test failed: Doctor not found.");
            return;
        }

        Doctor.ShiftStatus status =
                DoctorManagement.getDoctorAttendanceStatus(
                        doctor
                );

        System.out.println(
                "Doctor : " + doctor.getName()
        );

        System.out.println(
                "Attendance Status : " + status
        );
    }

    // =========================================================
    // GENERATE ID TEST
    // =========================================================

    static void testGenerateShiftId() {

        System.out.println(
                "\n===== TEST GENERATE SHIFT ID ====="
        );

        String shiftId =
                DoctorManagement.generateShiftId();

        System.out.println(
                "Generated Shift ID : " + shiftId
        );
    }

    // =========================================================
    // CREATE SHIFT TEST
    // =========================================================

    static void testCreateShift() {

        System.out.println(
                "\n===== TEST CREATE DOCTOR SHIFT ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        Doctor doctor =
                UserManagement.getDoctorById("#DOCd4e8f");

        if (manager == null) {
            System.out.println(
                    "Test failed: Medical Manager not found."
            );
            return;
        }

        if (doctor == null) {
            System.out.println(
                    "Test failed: Doctor not found."
            );
            return;
        }

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        /*
         * Use a future date so the newly created shift
         * will initially have SCHEDULED status.
         */
        String startDateTime = "2026-08-16 09:00:00";
        String endDateTime = "2026-08-16 17:00:00";

        LinkedHashMap<String, String> result =
                doctorMng.createShift(
                        doctor,
                        startDateTime,
                        endDateTime
                );

        System.out.println(
                "Success : " + result.get("success")
        );

        System.out.println(
                "Message : " + result.get("msg")
        );

        System.out.println(
                "New Shift ID : " + result.get("newShiftId")
        );
    }

    // =========================================================
    // CANCEL SHIFT TEST
    // =========================================================

    static void testCancelShift() {

        System.out.println(
                "\n===== TEST CANCEL DOCTOR SHIFT ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println(
                    "Test failed: Medical Manager not found."
            );
            return;
        }

        DoctorManagement doctorMng =
                new DoctorManagement(manager);

        List<DoctorShift> shifts =
                doctorMng.getAllDoctorShifts();

        if (shifts.isEmpty()) {
            System.out.println(
                    "No doctor shifts available to cancel."
            );
            return;
        }

        /*
         * Find a shift that has not already been completed
         * or cancelled.
         */
        DoctorShift targetShift = null;

        for (DoctorShift shift : shifts) {

            if (shift.getStatus()
                    != DoctorShift.DoctorShiftStatus.COMPLETED
                    && shift.getStatus()
                    != DoctorShift.DoctorShiftStatus.CANCELLED) {

                targetShift = shift;
                break;
            }
        }

        if (targetShift == null) {
            System.out.println(
                    "No cancellable doctor shift found."
            );
            return;
        }

        System.out.println(
                "Cancelling Shift : "
                + targetShift.getShiftId()
        );

        LinkedHashMap<String, String> result =
                doctorMng.cancelShift(targetShift);

        System.out.println(
                "Success : " + result.get("success")
        );

        System.out.println(
                "Message : " + result.get("msg")
        );
    }
}
