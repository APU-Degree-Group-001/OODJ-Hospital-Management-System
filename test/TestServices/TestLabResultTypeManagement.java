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
import oodj_hospital_management_system.models.LabResultType;
import oodj_hospital_management_system.models.LabResultType.LabResultTypeStatus;
import oodj_hospital_management_system.services.LabResultTypeManagement;

public class TestLabResultTypeManagement {

    private static LabResultTypeManagement labResultTypeMng;

    public static void main(String[] args) {

        // Create LabResultTypeManagement once
        labResultTypeMng = new LabResultTypeManagement();

        // Test loading
//        testLoadLabResultTypes();

        // Test get-all
//        testGetAllLabResultTypes();
//        testGetActiveLabResultTypes();

        // Test get / find by ID
//        testGetLabResultTypeById();
//        testFindLabResultTypeByInvalidId();

        // Test get / find by name
//        testGetLabResultTypeByName();
//        testFindLabResultTypeByInvalidName();

        // Test status parsing
//        testGetLabResultTypeStatus();

        // Test creation
        // Uncomment when needed because this modifies labresulttypes.txt
//        testCreateLabResultType();
//        testCreateLabResultTypeWithMissingFields();
//        testCreateLabResultTypeWithNegativeRate();

        // Test update
        // Uncomment when needed because this modifies labresulttypes.txt
//        testUpdateLabResultType();
//        testUpdateLabResultTypeWithInvalidId();

        // Test deactivate / activate
        // Uncomment when needed because this modifies labresulttypes.txt
//        testDeactivateLabResultType();
//        testActivateLabResultType();
//        testDeactivateAlreadyInactiveLabResultType();

        // Test ID generation
//        testGenerateLabResultTypeId();
    }

    // =========================================================
    // PRINT METHODS
    // =========================================================

    public static void testPrintLabResultTypes(List<LabResultType> labResultTypeList) {

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

        for (LabResultType labResultType : labResultTypeList) {

            System.out.printf(
                    "%-15s %-25s %-12.2f %-10s%n",
                    labResultType.getLabResultTypeId(),
                    labResultType.getTypeName(),
                    labResultType.getBaseRate(),
                    labResultType.getStatus()
            );
        }
    }

    // =========================================================
    // LOAD TESTS
    // =========================================================

    static void testLoadLabResultTypes() {

        System.out.println("\n===== TEST LOAD LAB RESULT TYPES =====");

        LabResultTypeManagement.loadLabResultTypes();

        List<LabResultType> labResultTypes =
                LabResultTypeManagement.getAllLabResultTypes();

        System.out.println("Total Lab Result Types: " + labResultTypes.size());

        testPrintLabResultTypes(labResultTypes);
    }

    // =========================================================
    // GET ALL TESTS
    // =========================================================

    static void testGetAllLabResultTypes() {

        System.out.println("\n===== TEST GET ALL LAB RESULT TYPES =====");

        List<LabResultType> labResultTypes =
                LabResultTypeManagement.getAllLabResultTypes();

        System.out.println("Total Lab Result Types: " + labResultTypes.size());

        testPrintLabResultTypes(labResultTypes);
    }

    static void testGetActiveLabResultTypes() {

        System.out.println("\n===== TEST GET ACTIVE LAB RESULT TYPES =====");

        List<LabResultType> labResultTypes =
                LabResultTypeManagement.getActiveLabResultTypes();

        System.out.println("Total Active Lab Result Types: " + labResultTypes.size());

        testPrintLabResultTypes(labResultTypes);
    }

    // =========================================================
    // GET / FIND BY ID TESTS
    // =========================================================

    static void testGetLabResultTypeById() {

        System.out.println("\n===== TEST GET LAB RESULT TYPE BY ID =====");

        List<LabResultType> labResultTypes =
                LabResultTypeManagement.getAllLabResultTypes();

        if (labResultTypes.isEmpty()) {
            System.out.println("No lab result types available.");
            return;
        }

        String labResultTypeId = labResultTypes.get(0).getLabResultTypeId();

        LabResultType labResultType =
                LabResultTypeManagement.getLabResultTypeById(labResultTypeId);

        if (labResultType != null) {

            System.out.println("Lab Result Type found:");
            System.out.println("Type ID   : " + labResultType.getLabResultTypeId());
            System.out.println("Type Name : " + labResultType.getTypeName());
            System.out.println("Base Rate : " + labResultType.getBaseRate());
            System.out.println("Status    : " + labResultType.getStatus());

        } else {
            System.out.println("Test failed: Lab result type not found.");
        }
    }

    static void testFindLabResultTypeByInvalidId() {

        System.out.println("\n===== TEST FIND LAB RESULT TYPE BY INVALID ID =====");

        LabResultType labResultType =
                LabResultTypeManagement.findLabResultTypeById("#LRTYPEinvalid");

        if (labResultType == null) {
            System.out.println("Test passed: Invalid lab result type ID correctly returned null.");
        } else {
            System.out.println("Test failed: Invalid lab result type ID unexpectedly returned a result.");
        }
    }

    // =========================================================
    // GET / FIND BY NAME TESTS
    // =========================================================

    static void testGetLabResultTypeByName() {

        System.out.println("\n===== TEST GET LAB RESULT TYPE BY NAME =====");

        List<LabResultType> labResultTypes =
                LabResultTypeManagement.getAllLabResultTypes();

        if (labResultTypes.isEmpty()) {
            System.out.println("No lab result types available.");
            return;
        }

        String typeName = labResultTypes.get(0).getTypeName();

        LabResultType labResultType =
                LabResultTypeManagement.getLabResultTypeByName(typeName);

        if (labResultType != null) {

            System.out.println("Lab Result Type found:");
            System.out.println("Type ID   : " + labResultType.getLabResultTypeId());
            System.out.println("Type Name : " + labResultType.getTypeName());
            System.out.println("Base Rate : " + labResultType.getBaseRate());
            System.out.println("Status    : " + labResultType.getStatus());

        } else {
            System.out.println("Test failed: Lab result type not found.");
        }
    }

    static void testFindLabResultTypeByInvalidName() {

        System.out.println("\n===== TEST FIND LAB RESULT TYPE BY INVALID NAME =====");

        LabResultType labResultType =
                LabResultTypeManagement.findLabResultTypeByName("Bogus Test Name");

        if (labResultType == null) {
            System.out.println("Test passed: Invalid lab result type name correctly returned null.");
        } else {
            System.out.println("Test failed: Invalid lab result type name unexpectedly returned a result.");
        }
    }

    // =========================================================
    // STATUS PARSING TEST
    // =========================================================

    static void testGetLabResultTypeStatus() {

        System.out.println("\n===== TEST GET LAB RESULT TYPE STATUS =====");

        String[] statuses = {
            "ACTIVE", "inactive", "Active", "INACTIVE", "bogus", ""
        };

        for (String status : statuses) {
            LabResultTypeStatus result =
                    LabResultTypeManagement.getLabResultTypeStatus(status);

            System.out.println("Input: '" + status + "' -> Parsed: " + result);
        }
    }

    // =========================================================
    // CREATION TESTS
    // =========================================================

    static void testCreateLabResultType() {

        System.out.println("\n===== TEST CREATE LAB RESULT TYPE =====");

        String typeName = "Liver Function Test";
        double baseRate = 60.00;

        LinkedHashMap<String, String> result =
                labResultTypeMng.createLabResultType(typeName, baseRate);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Lab Result Type ID : " + result.get("newLabResultTypeId"));

        if ("true".equals(result.get("success"))) {

            LabResultType created =
                    LabResultTypeManagement.getLabResultTypeById(result.get("newLabResultTypeId"));

            boolean matches = created != null
                    && created.getTypeName().equals(typeName)
                    && created.getBaseRate() == baseRate
                    && created.getStatus() == LabResultTypeStatus.ACTIVE;

            if (matches) {
                System.out.println("Creation test PASSED! Lab result type stored correctly and ACTIVE.");
            } else {
                System.out.println("Creation test FAILED! Lab result type missing or fields incorrect.");
            }
        }
    }

    static void testCreateLabResultTypeWithMissingFields() {

        System.out.println("\n===== TEST CREATE LAB RESULT TYPE WITH MISSING FIELDS =====");

        LinkedHashMap<String, String> result =
                labResultTypeMng.createLabResultType("", 50.00);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Lab result type correctly rejected due to empty name.");
        } else {
            System.out.println("Test failed: Lab result type was created despite missing fields.");
        }
    }

    static void testCreateLabResultTypeWithNegativeRate() {

        System.out.println("\n===== TEST CREATE LAB RESULT TYPE WITH NEGATIVE RATE =====");

        LinkedHashMap<String, String> result =
                labResultTypeMng.createLabResultType("Invalid Rate Type", -10.00);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Lab result type correctly rejected due to negative rate.");
        } else {
            System.out.println("Test failed: Lab result type was created despite negative rate.");
        }
    }

    // =========================================================
    // UPDATE TESTS
    // =========================================================

    static void testUpdateLabResultType() {

        System.out.println("\n===== TEST UPDATE LAB RESULT TYPE =====");

        List<LabResultType> labResultTypes =
                LabResultTypeManagement.getAllLabResultTypes();

        if (labResultTypes.isEmpty()) {
            System.out.println("Test failed: No lab result types available.");
            return;
        }

        LabResultType targetLabResultType = labResultTypes.get(0);

        String newTypeName = targetLabResultType.getTypeName() + " (Updated)";
        double newBaseRate = targetLabResultType.getBaseRate() + 10.00;

        System.out.println("Updating Lab Result Type : " + targetLabResultType.getLabResultTypeId());

        LinkedHashMap<String, String> result =
                labResultTypeMng.updateLabResultType(targetLabResultType, newTypeName, newBaseRate);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        LabResultType updated =
                LabResultTypeManagement.getLabResultTypeById(targetLabResultType.getLabResultTypeId());

        if (updated != null && updated.getTypeName().equals(newTypeName) && updated.getBaseRate() == newBaseRate) {
            System.out.println("Update test PASSED! Lab result type updated correctly.");
        } else {
            System.out.println("Update test FAILED! Lab result type was not updated.");
        }
    }

    static void testUpdateLabResultTypeWithInvalidId() {

        System.out.println("\n===== TEST UPDATE LAB RESULT TYPE WITH INVALID ID =====");

        LabResultType invalidLabResultType = new LabResultType(
                "#LRTYPEinvalid",
                "Ghost Type",
                20.00,
                LabResultTypeStatus.ACTIVE
        );

        LinkedHashMap<String, String> result =
                labResultTypeMng.updateLabResultType(invalidLabResultType, "Renamed", 25.00);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Invalid lab result type correctly rejected.");
        } else {
            System.out.println("Test failed: Invalid lab result type unexpectedly updated.");
        }
    }

    // =========================================================
    // DEACTIVATE / ACTIVATE TESTS
    // =========================================================

    static void testDeactivateLabResultType() {

        System.out.println("\n===== TEST DEACTIVATE LAB RESULT TYPE =====");

        List<LabResultType> activeTypes =
                LabResultTypeManagement.getActiveLabResultTypes();

        if (activeTypes.isEmpty()) {
            System.out.println("Test failed: No active lab result types available.");
            return;
        }

        LabResultType targetLabResultType = activeTypes.get(activeTypes.size() - 1);

        System.out.println("Deactivating Lab Result Type : " + targetLabResultType.getLabResultTypeId());

        LinkedHashMap<String, String> result =
                labResultTypeMng.deactivateLabResultType(targetLabResultType.getLabResultTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        LabResultType updated =
                LabResultTypeManagement.getLabResultTypeById(targetLabResultType.getLabResultTypeId());

        if (updated != null && updated.getStatus() == LabResultTypeStatus.INACTIVE) {
            System.out.println("Deactivation test PASSED! Status is INACTIVE.");
        } else {
            System.out.println("Deactivation test FAILED! Status was not updated.");
        }
    }

    static void testActivateLabResultType() {

        System.out.println("\n===== TEST ACTIVATE LAB RESULT TYPE =====");

        List<LabResultType> allTypes =
                LabResultTypeManagement.getAllLabResultTypes();

        LabResultType inactiveType = null;
        for (LabResultType lrt : allTypes) {
            if (lrt.getStatus() == LabResultTypeStatus.INACTIVE) {
                inactiveType = lrt;
                break;
            }
        }

        if (inactiveType == null) {
            System.out.println("Test failed: No inactive lab result types available. Run deactivate test first.");
            return;
        }

        System.out.println("Activating Lab Result Type : " + inactiveType.getLabResultTypeId());

        LinkedHashMap<String, String> result =
                labResultTypeMng.activateLabResultType(inactiveType.getLabResultTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        LabResultType updated =
                LabResultTypeManagement.getLabResultTypeById(inactiveType.getLabResultTypeId());

        if (updated != null && updated.getStatus() == LabResultTypeStatus.ACTIVE) {
            System.out.println("Activation test PASSED! Status is ACTIVE.");
        } else {
            System.out.println("Activation test FAILED! Status was not updated.");
        }
    }

    static void testDeactivateAlreadyInactiveLabResultType() {

        System.out.println("\n===== TEST DEACTIVATE ALREADY INACTIVE LAB RESULT TYPE =====");

        List<LabResultType> allTypes =
                LabResultTypeManagement.getAllLabResultTypes();

        LabResultType inactiveType = null;
        for (LabResultType lrt : allTypes) {
            if (lrt.getStatus() == LabResultTypeStatus.INACTIVE) {
                inactiveType = lrt;
                break;
            }
        }

        if (inactiveType == null) {
            System.out.println("Test skipped: No inactive lab result types available.");
            return;
        }

        LinkedHashMap<String, String> result =
                labResultTypeMng.deactivateLabResultType(inactiveType.getLabResultTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Already-inactive lab result type correctly rejected.");
        } else {
            System.out.println("Test failed: Already-inactive lab result type was deactivated again.");
        }
    }

    // =========================================================
    // GENERATE ID TEST
    // =========================================================

    static void testGenerateLabResultTypeId() {

        System.out.println("\n===== TEST GENERATE LAB RESULT TYPE ID =====");

        String labResultTypeId = LabResultTypeManagement.generateLabResultTypeId();

        System.out.println("Generated Lab Result Type ID : " + labResultTypeId);
    }
}