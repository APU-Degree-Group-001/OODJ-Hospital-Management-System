package TestServices;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.PrescriptionType;
import oodj_hospital_management_system.models.PrescriptionType.PrescriptionTypeStatus;
import oodj_hospital_management_system.services.PrescriptionTypeManagement;

public class TestPrescriptionTypeManagement {

    private static PrescriptionTypeManagement prescriptionTypeMng;

    public static void main(String[] args) {

        // Create PrescriptionTypeManagement once
        prescriptionTypeMng = new PrescriptionTypeManagement();

        // Test loading
//        testLoadPrescriptionTypes();

        // Test get-all
//        testGetAllPrescriptionTypes();
//        testGetActivePrescriptionTypes();

        // Test get / find by ID
//        testGetPrescriptionTypeById();
//        testFindPrescriptionTypeByInvalidId();

        // Test get / find by name
//        testGetPrescriptionTypeByName();
//        testFindPrescriptionTypeByInvalidName();

        // Test status parsing
//        testGetPrescriptionTypeStatus();

        // Test creation
        // Uncomment when needed because this modifies prescriptiontypes.txt
//        testCreatePrescriptionType();
//        testCreatePrescriptionTypeWithMissingFields();
//        testCreatePrescriptionTypeWithNegativeRate();

        // Test update
        // Uncomment when needed because this modifies prescriptiontypes.txt
//        testUpdatePrescriptionType();
//        testUpdatePrescriptionTypeWithInvalidId();

        // Test deactivate / activate
        // Uncomment when needed because this modifies prescriptiontypes.txt
//        testDeactivatePrescriptionType();
//        testActivatePrescriptionType();
//        testDeactivateAlreadyInactivePrescriptionType();

        // Test ID generation
//        testGeneratePrescriptionTypeId();
    }

    // =========================================================
    // PRINT METHODS
    // =========================================================

    public static void testPrintPrescriptionTypes(List<PrescriptionType> prescriptionTypeList) {

        System.out.printf(
                "%-15s %-25s %-12s %-10s%n",
                "Type ID",
                "Type Name",
                "Base Rate",
                "Status"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (PrescriptionType prescriptionType : prescriptionTypeList) {

            System.out.printf(
                    "%-15s %-25s %-12.2f %-10s%n",
                    prescriptionType.getPrescriptionTypeId(),
                    prescriptionType.getTypeName(),
                    prescriptionType.getBaseRate(),
                    prescriptionType.getStatus()
            );
        }
    }

    // =========================================================
    // LOAD TESTS
    // =========================================================

    static void testLoadPrescriptionTypes() {

        System.out.println("\n===== TEST LOAD PRESCRIPTION TYPES =====");

        PrescriptionTypeManagement.loadPrescriptionTypes();

        List<PrescriptionType> prescriptionTypes =
                PrescriptionTypeManagement.getAllPrescriptionTypes();

        System.out.println("Total Prescription Types: " + prescriptionTypes.size());

        testPrintPrescriptionTypes(prescriptionTypes);
    }

    // =========================================================
    // GET ALL TESTS
    // =========================================================

    static void testGetAllPrescriptionTypes() {

        System.out.println("\n===== TEST GET ALL PRESCRIPTION TYPES =====");

        List<PrescriptionType> prescriptionTypes =
                PrescriptionTypeManagement.getAllPrescriptionTypes();

        System.out.println("Total Prescription Types: " + prescriptionTypes.size());

        testPrintPrescriptionTypes(prescriptionTypes);
    }

    static void testGetActivePrescriptionTypes() {

        System.out.println("\n===== TEST GET ACTIVE PRESCRIPTION TYPES =====");

        List<PrescriptionType> prescriptionTypes =
                PrescriptionTypeManagement.getActivePrescriptionTypes();

        System.out.println("Total Active Prescription Types: " + prescriptionTypes.size());

        testPrintPrescriptionTypes(prescriptionTypes);
    }

    // =========================================================
    // GET / FIND BY ID TESTS
    // =========================================================

    static void testGetPrescriptionTypeById() {

        System.out.println("\n===== TEST GET PRESCRIPTION TYPE BY ID =====");

        List<PrescriptionType> prescriptionTypes =
                PrescriptionTypeManagement.getAllPrescriptionTypes();

        if (prescriptionTypes.isEmpty()) {
            System.out.println("No prescription types available.");
            return;
        }

        String prescriptionTypeId = prescriptionTypes.get(0).getPrescriptionTypeId();

        PrescriptionType prescriptionType =
                PrescriptionTypeManagement.getPrescriptionTypeById(prescriptionTypeId);

        if (prescriptionType != null) {

            System.out.println("Prescription Type found:");
            System.out.println("Type ID   : " + prescriptionType.getPrescriptionTypeId());
            System.out.println("Type Name : " + prescriptionType.getTypeName());
            System.out.println("Base Rate : " + prescriptionType.getBaseRate());
            System.out.println("Status    : " + prescriptionType.getStatus());

        } else {
            System.out.println("Test failed: Prescription type not found.");
        }
    }

    static void testFindPrescriptionTypeByInvalidId() {

        System.out.println("\n===== TEST FIND PRESCRIPTION TYPE BY INVALID ID =====");

        PrescriptionType prescriptionType =
                PrescriptionTypeManagement.findPrescriptionTypeById("#PTYPEinvalid");

        if (prescriptionType == null) {
            System.out.println("Test passed: Invalid prescription type ID correctly returned null.");
        } else {
            System.out.println("Test failed: Invalid prescription type ID unexpectedly returned a result.");
        }
    }

    // =========================================================
    // GET / FIND BY NAME TESTS
    // =========================================================

    static void testGetPrescriptionTypeByName() {

        System.out.println("\n===== TEST GET PRESCRIPTION TYPE BY NAME =====");

        List<PrescriptionType> prescriptionTypes =
                PrescriptionTypeManagement.getAllPrescriptionTypes();

        if (prescriptionTypes.isEmpty()) {
            System.out.println("No prescription types available.");
            return;
        }

        String typeName = prescriptionTypes.get(0).getTypeName();

        PrescriptionType prescriptionType =
                PrescriptionTypeManagement.getPrescriptionTypeByName(typeName);

        if (prescriptionType != null) {

            System.out.println("Prescription Type found:");
            System.out.println("Type ID   : " + prescriptionType.getPrescriptionTypeId());
            System.out.println("Type Name : " + prescriptionType.getTypeName());
            System.out.println("Base Rate : " + prescriptionType.getBaseRate());
            System.out.println("Status    : " + prescriptionType.getStatus());

        } else {
            System.out.println("Test failed: Prescription type not found.");
        }
    }

    static void testFindPrescriptionTypeByInvalidName() {

        System.out.println("\n===== TEST FIND PRESCRIPTION TYPE BY INVALID NAME =====");

        PrescriptionType prescriptionType =
                PrescriptionTypeManagement.findPrescriptionTypeByName("Bogus Medicine Name");

        if (prescriptionType == null) {
            System.out.println("Test passed: Invalid prescription type name correctly returned null.");
        } else {
            System.out.println("Test failed: Invalid prescription type name unexpectedly returned a result.");
        }
    }

    // =========================================================
    // STATUS PARSING TEST
    // =========================================================

    static void testGetPrescriptionTypeStatus() {

        System.out.println("\n===== TEST GET PRESCRIPTION TYPE STATUS =====");

        String[] statuses = {
            "ACTIVE", "inactive", "Active", "INACTIVE", "bogus", ""
        };

        for (String status : statuses) {
            PrescriptionTypeStatus result =
                    PrescriptionTypeManagement.getPrescriptionTypeStatus(status);

            System.out.println("Input: '" + status + "' -> Parsed: " + result);
        }
    }

    // =========================================================
    // CREATION TESTS
    // =========================================================

    static void testCreatePrescriptionType() {

        System.out.println("\n===== TEST CREATE PRESCRIPTION TYPE =====");

        String typeName = "Amoxicillin";
        double baseRate = 0.80;

        LinkedHashMap<String, String> result =
                prescriptionTypeMng.createPrescriptionType(typeName, baseRate);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Prescription Type ID : " + result.get("newPrescriptionTypeId"));

        if ("true".equals(result.get("success"))) {

            PrescriptionType created =
                    PrescriptionTypeManagement.getPrescriptionTypeById(result.get("newPrescriptionTypeId"));

            boolean matches = created != null
                    && created.getTypeName().equals(typeName)
                    && created.getBaseRate() == baseRate
                    && created.getStatus() == PrescriptionTypeStatus.ACTIVE;

            if (matches) {
                System.out.println("Creation test PASSED! Prescription type stored correctly and ACTIVE.");
            } else {
                System.out.println("Creation test FAILED! Prescription type missing or fields incorrect.");
            }
        }
    }

    static void testCreatePrescriptionTypeWithMissingFields() {

        System.out.println("\n===== TEST CREATE PRESCRIPTION TYPE WITH MISSING FIELDS =====");

        LinkedHashMap<String, String> result =
                prescriptionTypeMng.createPrescriptionType("", 0.50);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Prescription type correctly rejected due to empty name.");
        } else {
            System.out.println("Test failed: Prescription type was created despite missing fields.");
        }
    }

    static void testCreatePrescriptionTypeWithNegativeRate() {

        System.out.println("\n===== TEST CREATE PRESCRIPTION TYPE WITH NEGATIVE RATE =====");

        LinkedHashMap<String, String> result =
                prescriptionTypeMng.createPrescriptionType("Invalid Rate Type", -0.50);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Prescription type correctly rejected due to negative rate.");
        } else {
            System.out.println("Test failed: Prescription type was created despite negative rate.");
        }
    }

    // =========================================================
    // UPDATE TESTS
    // =========================================================

    static void testUpdatePrescriptionType() {

        System.out.println("\n===== TEST UPDATE PRESCRIPTION TYPE =====");

        List<PrescriptionType> prescriptionTypes =
                PrescriptionTypeManagement.getAllPrescriptionTypes();

        if (prescriptionTypes.isEmpty()) {
            System.out.println("Test failed: No prescription types available.");
            return;
        }

        PrescriptionType targetPrescriptionType = prescriptionTypes.get(0);

        String newTypeName = targetPrescriptionType.getTypeName() + " (Updated)";
        double newBaseRate = targetPrescriptionType.getBaseRate() + 0.10;

        System.out.println("Updating Prescription Type : " + targetPrescriptionType.getPrescriptionTypeId());

        LinkedHashMap<String, String> result =
                prescriptionTypeMng.updatePrescriptionType(targetPrescriptionType, newTypeName, newBaseRate);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        PrescriptionType updated =
                PrescriptionTypeManagement.getPrescriptionTypeById(targetPrescriptionType.getPrescriptionTypeId());

        if (updated != null && updated.getTypeName().equals(newTypeName) && updated.getBaseRate() == newBaseRate) {
            System.out.println("Update test PASSED! Prescription type updated correctly.");
        } else {
            System.out.println("Update test FAILED! Prescription type was not updated.");
        }
    }

    static void testUpdatePrescriptionTypeWithInvalidId() {

        System.out.println("\n===== TEST UPDATE PRESCRIPTION TYPE WITH INVALID ID =====");

        PrescriptionType invalidPrescriptionType = new PrescriptionType(
                "#PTYPEinvalid",
                "Ghost Type",
                0.20,
                PrescriptionTypeStatus.ACTIVE
        );

        LinkedHashMap<String, String> result =
                prescriptionTypeMng.updatePrescriptionType(invalidPrescriptionType, "Renamed", 0.25);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Invalid prescription type correctly rejected.");
        } else {
            System.out.println("Test failed: Invalid prescription type unexpectedly updated.");
        }
    }

    // =========================================================
    // DEACTIVATE / ACTIVATE TESTS
    // =========================================================

    static void testDeactivatePrescriptionType() {

        System.out.println("\n===== TEST DEACTIVATE PRESCRIPTION TYPE =====");

        List<PrescriptionType> activeTypes =
                PrescriptionTypeManagement.getActivePrescriptionTypes();

        if (activeTypes.isEmpty()) {
            System.out.println("Test failed: No active prescription types available.");
            return;
        }

        PrescriptionType targetPrescriptionType = activeTypes.get(activeTypes.size() - 1);

        System.out.println("Deactivating Prescription Type : " + targetPrescriptionType.getPrescriptionTypeId());

        LinkedHashMap<String, String> result =
                prescriptionTypeMng.deactivatePrescriptionType(targetPrescriptionType.getPrescriptionTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        PrescriptionType updated =
                PrescriptionTypeManagement.getPrescriptionTypeById(targetPrescriptionType.getPrescriptionTypeId());

        if (updated != null && updated.getStatus() == PrescriptionTypeStatus.INACTIVE) {
            System.out.println("Deactivation test PASSED! Status is INACTIVE.");
        } else {
            System.out.println("Deactivation test FAILED! Status was not updated.");
        }
    }

    static void testActivatePrescriptionType() {

        System.out.println("\n===== TEST ACTIVATE PRESCRIPTION TYPE =====");

        List<PrescriptionType> allTypes =
                PrescriptionTypeManagement.getAllPrescriptionTypes();

        PrescriptionType inactiveType = null;
        for (PrescriptionType pt : allTypes) {
            if (pt.getStatus() == PrescriptionTypeStatus.INACTIVE) {
                inactiveType = pt;
                break;
            }
        }

        if (inactiveType == null) {
            System.out.println("Test failed: No inactive prescription types available. Run deactivate test first.");
            return;
        }

        System.out.println("Activating Prescription Type : " + inactiveType.getPrescriptionTypeId());

        LinkedHashMap<String, String> result =
                prescriptionTypeMng.activatePrescriptionType(inactiveType.getPrescriptionTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        PrescriptionType updated =
                PrescriptionTypeManagement.getPrescriptionTypeById(inactiveType.getPrescriptionTypeId());

        if (updated != null && updated.getStatus() == PrescriptionTypeStatus.ACTIVE) {
            System.out.println("Activation test PASSED! Status is ACTIVE.");
        } else {
            System.out.println("Activation test FAILED! Status was not updated.");
        }
    }

    static void testDeactivateAlreadyInactivePrescriptionType() {

        System.out.println("\n===== TEST DEACTIVATE ALREADY INACTIVE PRESCRIPTION TYPE =====");

        List<PrescriptionType> allTypes =
                PrescriptionTypeManagement.getAllPrescriptionTypes();

        PrescriptionType inactiveType = null;
        for (PrescriptionType pt : allTypes) {
            if (pt.getStatus() == PrescriptionTypeStatus.INACTIVE) {
                inactiveType = pt;
                break;
            }
        }

        if (inactiveType == null) {
            System.out.println("Test skipped: No inactive prescription types available.");
            return;
        }

        LinkedHashMap<String, String> result =
                prescriptionTypeMng.deactivatePrescriptionType(inactiveType.getPrescriptionTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Already-inactive prescription type correctly rejected.");
        } else {
            System.out.println("Test failed: Already-inactive prescription type was deactivated again.");
        }
    }

    // =========================================================
    // GENERATE ID TEST
    // =========================================================

    static void testGeneratePrescriptionTypeId() {

        System.out.println("\n===== TEST GENERATE PRESCRIPTION TYPE ID =====");

        String prescriptionTypeId = PrescriptionTypeManagement.generatePrescriptionTypeId();

        System.out.println("Generated Prescription Type ID : " + prescriptionTypeId);
    }
}