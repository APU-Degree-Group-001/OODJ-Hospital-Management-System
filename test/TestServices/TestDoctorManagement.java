package TestServices;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.HospitalAssetManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.FileHandler;

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
//         testCreateShift();
//           testHospitalAssetCount();
//        testRawHospitalAssetFile();
//        testFindConsultationSlot();

        // Test shift cancellation
        // Uncomment when needed because this modifies doctorShifts.txt
//         testCancelShift();

        // Test medical department CRUD
        // Uncomment when needed because these modify medicalDepartments.txt / users.txt
//        testAddNewDepartment();
//        testEditDepartment();
//        testDeleteDepartment();
//        testUpdateDoctorDepartment();

//         testStep1_CreateThrowawayDepartment();
//         testStep2_AssignDoctorsToDepartment();
//         testStep3_DeleteDepartment();
//         testStep4_VerifyDoctorsCleared();
    }
    
    public void testLoadConsultationSlots() {
        
    }
    
    static void testRawHospitalAssetFile() {

        FileHandler handler =
        new FileHandler(SystemConfiguration.hospitalAssetFilePath);

        System.out.println("File Path: " + handler.getFilePath());

        File file = new File(handler.getFilePath());

        System.out.println("Absolute Path: " + file.getAbsolutePath());
        System.out.println("Exists: " + file.exists());
        System.out.println("File Size: " + file.length());

        ArrayList<LinkedHashMap<String, String>> assets =
                handler.readFile();

        System.out.println("Rows Read: " + assets.size());

        for (LinkedHashMap<String, String> asset : assets) {
            System.out.println(asset);
        }
    }
    
    static void testHospitalAssetCount() {

        System.out.println("\n===== TEST HOSPITAL ASSETS =====");

        List<HospitalAsset> assets =
                HospitalAssetManagement.getAllHospitalAssets();

        System.out.println("Total assets: " + assets.size());

        for (HospitalAsset asset : assets) {
            System.out.println(
                    asset.getAssetId()
                    + " | "
                    + asset.getAssetType()
                    + " | "
                    + asset.getAssetName()
            );
        }

        System.out.println(
                "Consultation Rooms: "
                + HospitalAssetManagement.getConsultationRoomCount()
        );
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
        System.out.println("\n===== TEST CREATE DOCTOR SHIFT =====");

        MedicalManager manager = UserManagement.getMedicalManagerById("#MNG74295");
        Doctor doctor = UserManagement.getDoctorById("#DOCd4e8f");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        if (doctor == null) {
            System.out.println("Test failed: Doctor not found.");
            return;
        }

        DoctorManagement doctorMng = new DoctorManagement(manager);

        String startDateTime = "2026-08-16 09:00:00";
        String endDateTime = "2026-08-16 17:00:00";

        LinkedHashMap<String, String> result = doctorMng.createShift(doctor, startDateTime, endDateTime);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Shift ID : " + result.get("newShiftId"));

        if (!"true".equals(result.get("success"))) {
            return;
        }

        String newShiftId = result.get("newShiftId");

        List<ConsultationSlot> slots = ConsultationManagement.getAllConsultationSlots();

        long shiftSlotCount = slots.stream()
                .filter(slot -> slot.getDoctorShift() != null && slot.getDoctorShift().getShiftId().equals(newShiftId))
                .count();

        System.out.println("Consultation Slots Created : " + shiftSlotCount);
        System.out.println("Expected Consultation Slots : 16");

        if (shiftSlotCount == 16) {
            System.out.println("Slot creation test PASSED!");
        } else {
            System.out.println("Slot creation test FAILED!");
        }
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
    
        // =========================================================
    // MEDICAL DEPARTMENT CRUD TESTS
    // =========================================================

    static void testAddNewDepartment() {

        System.out.println(
                "\n===== TEST ADD NEW MEDICAL DEPARTMENT ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        DoctorManagement doctorMng = new DoctorManagement(manager);

        String departmentName = "Dermatology";
        String description = "Provides diagnosis and treatment for skin, hair, and nail conditions.";

        LinkedHashMap<String, String> result =
                doctorMng.addNewDepartment(departmentName, description);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Department ID : " + result.get("newDepartmentId"));

        if ("true".equals(result.get("success"))) {
            MedicalDepartment created =
                    DoctorManagement.getMedicalDepartmentById(result.get("newDepartmentId"));

            if (created != null) {
                System.out.println("Verified in file:");
                testPrintMedicalDepartments(List.of(created));
            } else {
                System.out.println("Test failed: created department not found after reload.");
            }
        }
    }

    static void testEditDepartment() {

        System.out.println(
                "\n===== TEST EDIT MEDICAL DEPARTMENT ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        DoctorManagement doctorMng = new DoctorManagement(manager);

        String targetDepartmentId = "#DEPT005";
        String newName = "Orthopedics";
        String newDescription = "Provides diagnosis, treatment, and rehabilitation for bones, joints, muscles, and the musculoskeletal system.";

        LinkedHashMap<String, String> result =
                doctorMng.editDepartment(newName, newDescription, targetDepartmentId);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if ("true".equals(result.get("success"))) {
            MedicalDepartment edited =
                    DoctorManagement.getMedicalDepartmentById(targetDepartmentId);

            if (edited != null) {
                System.out.println("Verified in file:");
                testPrintMedicalDepartments(List.of(edited));
            } else {
                System.out.println("Test failed: edited department not found after reload.");
            }
        }
    }

    static void testDeleteDepartment() {

        System.out.println(
                "\n===== TEST DELETE MEDICAL DEPARTMENT ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        DoctorManagement doctorMng = new DoctorManagement(manager);

        // Add a throwaway department first so deletion doesn't destroy seed data
        LinkedHashMap<String, String> addResult =
                doctorMng.addNewDepartment("Temp Test Department", "Created only to be deleted by this test.");

        if (!"true".equals(addResult.get("success"))) {
            System.out.println("Test failed: could not create throwaway department to delete.");
            System.out.println("Message : " + addResult.get("msg"));
            return;
        }

        String targetDepartmentId = addResult.get("newDepartmentId");
        System.out.println("Deleting Department : " + targetDepartmentId);

        LinkedHashMap<String, String> result =
                doctorMng.deleteDepartment(targetDepartmentId);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("Deleted Department ID : " + result.get("deleteDepartmentId"));
        System.out.println("Deleted Department Name : " + result.get("deleteDepartmentName"));

        MedicalDepartment shouldBeGone =
                DoctorManagement.getMedicalDepartmentById(targetDepartmentId);

        if (shouldBeGone == null) {
            System.out.println("Deletion verified: department no longer found.");
        } else {
            System.out.println("Test failed: department still exists after deletion.");
        }
    }

    // =========================================================
    // UPDATE DOCTOR DEPARTMENT TEST (UserManagement)
    // =========================================================

    static void testUpdateDoctorDepartment() {

        System.out.println(
                "\n===== TEST UPDATE DOCTOR DEPARTMENT ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        String doctorId = "#DOCd4e8f";
        String newDepartmentId = "#DEPT004";

        Doctor doctorBefore = UserManagement.getDoctorById(doctorId);
        if (doctorBefore == null) {
            System.out.println("Test failed: Doctor not found.");
            return;
        }

        System.out.println("Doctor : " + doctorBefore.getName());
        System.out.println("Current Department : "
                + (doctorBefore.getMedicalDepartment() != null
                        ? doctorBefore.getMedicalDepartment().getDepartmentName()
                        : "None"));
        System.out.println("Assigning to Department ID : " + newDepartmentId);

        UserManagement userMng = new UserManagement(manager);

        LinkedHashMap<String, String> result =
                userMng.updateDoctorDepartment(doctorId, newDepartmentId);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        Doctor reloadedDoctor = UserManagement.getDoctorById(doctorId);

        if (reloadedDoctor != null && reloadedDoctor.getMedicalDepartment() != null
                && reloadedDoctor.getMedicalDepartment().getDepartmentId().equals(newDepartmentId)) {
            System.out.println("Verified: doctor's department updated in file.");
        } else {
            System.out.println("Test failed: doctor's department was not updated correctly.");
        }
    }
    
    // =========================================================
    // DELETE DEPARTMENT CLEARS DOCTOR ASSIGNMENT — STEP TESTS
    // =========================================================

    static void testStep1_CreateThrowawayDepartment() {

        System.out.println(
                "\n===== STEP 1: CREATE THROWAWAY DEPARTMENT ====="
        );

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        DoctorManagement doctorMng = new DoctorManagement(manager);

        LinkedHashMap<String, String> addResult =
                doctorMng.addNewDepartment("Temp Multi-Doctor Test Department", "Created only to test deletion clearing multiple doctors.");

        System.out.println("Success : " + addResult.get("success"));
        System.out.println("Message : " + addResult.get("msg"));
        System.out.println("New Department ID : " + addResult.get("newDepartmentId"));
        System.out.println("\n>>> Copy this department ID into testStep2/3/4 below before running them.");
    }

    static void testStep2_AssignDoctorsToDepartment() {

        System.out.println(
                "\n===== STEP 2: ASSIGN DOCTORS TO DEPARTMENT ====="
        );

        // TODO: paste the department ID printed by testStep1_CreateThrowawayDepartment()
        String targetDepartmentId = "#DEPT43f41";

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        UserManagement userMng = new UserManagement(manager);

        String doctorId1 = "#DOCd4e8f";
        String doctorId2 = "#DOCd4e8c";

        LinkedHashMap<String, String> assign1 = userMng.updateDoctorDepartment(doctorId1, targetDepartmentId);
        LinkedHashMap<String, String> assign2 = userMng.updateDoctorDepartment(doctorId2, targetDepartmentId);

        System.out.println("Assign Doctor 1 (" + doctorId1 + ") Success : " + assign1.get("success"));
        System.out.println("Assign Doctor 1 Message : " + assign1.get("msg"));
        System.out.println("Assign Doctor 2 (" + doctorId2 + ") Success : " + assign2.get("success"));
        System.out.println("Assign Doctor 2 Message : " + assign2.get("msg"));

        Doctor doctor1 = UserManagement.getDoctorById(doctorId1);
        Doctor doctor2 = UserManagement.getDoctorById(doctorId2);

        System.out.println("Doctor 1 Department Now : "
                + (doctor1.getMedicalDepartment() != null ? doctor1.getMedicalDepartment().getDepartmentId() : "None"));
        System.out.println("Doctor 2 Department Now : "
                + (doctor2.getMedicalDepartment() != null ? doctor2.getMedicalDepartment().getDepartmentId() : "None"));
    }

    static void testStep3_DeleteDepartment() {

        System.out.println(
                "\n===== STEP 3: DELETE DEPARTMENT ====="
        );

        // TODO: paste the same department ID used in testStep2
        String targetDepartmentId = "#DEPT43f41";

        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println("Test failed: Medical Manager not found.");
            return;
        }

        DoctorManagement doctorMng = new DoctorManagement(manager);

        LinkedHashMap<String, String> deleteResult = doctorMng.deleteDepartment(targetDepartmentId);

        System.out.println("Delete Success : " + deleteResult.get("success"));
        System.out.println("Delete Message : " + deleteResult.get("msg"));
        System.out.println("Deleted Department ID : " + deleteResult.get("deleteDepartmentId"));
        System.out.println("Deleted Department Name : " + deleteResult.get("deleteDepartmentName"));

        MedicalDepartment shouldBeGone = DoctorManagement.getMedicalDepartmentById(targetDepartmentId);
        if (shouldBeGone == null) {
            System.out.println("Verified: department no longer exists.");
        } else {
            System.out.println("Test failed: department still exists after deletion.");
        }
    }

    static void testStep4_VerifyDoctorsCleared() {

        System.out.println(
                "\n===== STEP 4: VERIFY DOCTORS' DEPARTMENT CLEARED ====="
        );

        String doctorId1 = "#DOCd4e8f";
        String doctorId2 = "#DOCd4e8c";

        Doctor doctor1 = UserManagement.getDoctorById(doctorId1);
        Doctor doctor2 = UserManagement.getDoctorById(doctorId2);

        if (doctor1 == null || doctor2 == null) {
            System.out.println("Test failed: one or both doctors not found.");
            return;
        }

        boolean doctor1Cleared = doctor1.getMedicalDepartment() == null
                || doctor1.getMedicalDepartment().getDepartmentId() == null
                || doctor1.getMedicalDepartment().getDepartmentId().isEmpty();

        boolean doctor2Cleared = doctor2.getMedicalDepartment() == null
                || doctor2.getMedicalDepartment().getDepartmentId() == null
                || doctor2.getMedicalDepartment().getDepartmentId().isEmpty();

        System.out.println("Doctor 1 (" + doctorId1 + ") Department : "
                + (doctor1.getMedicalDepartment() != null ? "'" + doctor1.getMedicalDepartment().getDepartmentId() + "'" : "null"));
        System.out.println("Doctor 2 (" + doctorId2 + ") Department : "
                + (doctor2.getMedicalDepartment() != null ? "'" + doctor2.getMedicalDepartment().getDepartmentId() + "'" : "null"));

        if (doctor1Cleared && doctor2Cleared) {
            System.out.println("Test PASSED: both doctors' department assignments were correctly cleared.");
        } else {
            System.out.println("Test FAILED: at least one doctor's department was NOT cleared.");
        }
    }
}
