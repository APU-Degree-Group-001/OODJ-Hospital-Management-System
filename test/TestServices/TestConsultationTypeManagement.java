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
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.ConsultationType.ConsultationTypeStatus;
import oodj_hospital_management_system.services.ConsultationTypeManagement;

public class TestConsultationTypeManagement {

    private static ConsultationTypeManagement consultationTypeMng;

    public static void main(String[] args) {

        // Create ConsultationTypeManagement once
        consultationTypeMng = new ConsultationTypeManagement();

        // Test loading
//        testLoadConsultationTypes();

        // Test get-all
//        testGetAllConsultationTypes();
//        testGetActiveConsultationTypes();

        // Test get / find by ID
//        testGetConsultationTypeById();
//        testFindConsultationTypeByInvalidId();

        // Test status parsing
//        testGetConsultationTypeStatus();

        // Test creation
        // Uncomment when needed because this modifies consultationtypes.txt
//        testCreateConsultationType();
//        testCreateConsultationTypeWithMissingFields();
//        testCreateConsultationTypeWithNegativeRate();

        // Test update
        // Uncomment when needed because this modifies consultationtypes.txt
//        testUpdateConsultationType();
//        testUpdateConsultationTypeWithInvalidId();

        // Test deactivate / activate
        // Uncomment when needed because this modifies consultationtypes.txt
//        testDeactivateConsultationType();
//        testActivateConsultationType();
//        testDeactivateAlreadyInactiveConsultationType();

        // Test ID generation
//        testGenerateConsultationTypeId();
    }

    // =========================================================
    // PRINT METHODS
    // =========================================================

    public static void testPrintConsultationTypes(List<ConsultationType> consultationTypeList) {

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

        for (ConsultationType consultationType : consultationTypeList) {

            System.out.printf(
                    "%-15s %-25s %-12.2f %-10s%n",
                    consultationType.getConsultationTypeId(),
                    consultationType.getTypeName(),
                    consultationType.getBaseRate(),
                    consultationType.getStatus()
            );
        }
    }

    // =========================================================
    // LOAD TESTS
    // =========================================================

    static void testLoadConsultationTypes() {

        System.out.println("\n===== TEST LOAD CONSULTATION TYPES =====");

        ConsultationTypeManagement.loadConsultationTypes();

        List<ConsultationType> consultationTypes =
                ConsultationTypeManagement.getAllConsultationTypes();

        System.out.println("Total Consultation Types: " + consultationTypes.size());

        testPrintConsultationTypes(consultationTypes);
    }

    // =========================================================
    // GET ALL TESTS
    // =========================================================

    static void testGetAllConsultationTypes() {

        System.out.println("\n===== TEST GET ALL CONSULTATION TYPES =====");

        List<ConsultationType> consultationTypes =
                ConsultationTypeManagement.getAllConsultationTypes();

        System.out.println("Total Consultation Types: " + consultationTypes.size());

        testPrintConsultationTypes(consultationTypes);
    }

    static void testGetActiveConsultationTypes() {

        System.out.println("\n===== TEST GET ACTIVE CONSULTATION TYPES =====");

        List<ConsultationType> consultationTypes =
                ConsultationTypeManagement.getActiveConsultationTypes();

        System.out.println("Total Active Consultation Types: " + consultationTypes.size());

        testPrintConsultationTypes(consultationTypes);
    }

    // =========================================================
    // GET / FIND BY ID TESTS
    // =========================================================

    static void testGetConsultationTypeById() {

        System.out.println("\n===== TEST GET CONSULTATION TYPE BY ID =====");

        List<ConsultationType> consultationTypes =
                ConsultationTypeManagement.getAllConsultationTypes();

        if (consultationTypes.isEmpty()) {
            System.out.println("No consultation types available.");
            return;
        }

        String consultationTypeId = consultationTypes.get(0).getConsultationTypeId();

        ConsultationType consultationType =
                ConsultationTypeManagement.getConsultationTypeById(consultationTypeId);

        if (consultationType != null) {

            System.out.println("Consultation Type found:");
            System.out.println("Type ID   : " + consultationType.getConsultationTypeId());
            System.out.println("Type Name : " + consultationType.getTypeName());
            System.out.println("Base Rate : " + consultationType.getBaseRate());
            System.out.println("Status    : " + consultationType.getStatus());

        } else {
            System.out.println("Test failed: Consultation type not found.");
        }
    }

    static void testFindConsultationTypeByInvalidId() {

        System.out.println("\n===== TEST FIND CONSULTATION TYPE BY INVALID ID =====");

        ConsultationType consultationType =
                ConsultationTypeManagement.findConsultationTypeById("#CTYPEinvalid");

        if (consultationType == null) {
            System.out.println("Test passed: Invalid consultation type ID correctly returned null.");
        } else {
            System.out.println("Test failed: Invalid consultation type ID unexpectedly returned a result.");
        }
    }

    // =========================================================
    // STATUS PARSING TEST
    // =========================================================

    static void testGetConsultationTypeStatus() {

        System.out.println("\n===== TEST GET CONSULTATION TYPE STATUS =====");

        String[] statuses = {
            "ACTIVE", "inactive", "Active", "INACTIVE", "bogus", ""
        };

        for (String status : statuses) {
            ConsultationTypeStatus result =
                    ConsultationTypeManagement.getConsultationTypeStatus(status);

            System.out.println("Input: '" + status + "' -> Parsed: " + result);
        }
    }

    // =========================================================
    // CREATION TESTS
    // =========================================================

    static void testCreateConsultationType() {

        System.out.println("\n===== TEST CREATE CONSULTATION TYPE =====");

        String typeName = "Follow-up Consultation";
        double baseRate = 35.00;

        LinkedHashMap<String, String> result =
                consultationTypeMng.createConsultationType(typeName, baseRate);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Consultation Type ID : " + result.get("newConsultationTypeId"));

        if ("true".equals(result.get("success"))) {

            ConsultationType created =
                    ConsultationTypeManagement.getConsultationTypeById(result.get("newConsultationTypeId"));

            boolean matches = created != null
                    && created.getTypeName().equals(typeName)
                    && created.getBaseRate() == baseRate
                    && created.getStatus() == ConsultationTypeStatus.ACTIVE;

            if (matches) {
                System.out.println("Creation test PASSED! Consultation type stored correctly and ACTIVE.");
            } else {
                System.out.println("Creation test FAILED! Consultation type missing or fields incorrect.");
            }
        }
    }

    static void testCreateConsultationTypeWithMissingFields() {

        System.out.println("\n===== TEST CREATE CONSULTATION TYPE WITH MISSING FIELDS =====");

        LinkedHashMap<String, String> result =
                consultationTypeMng.createConsultationType("", 50.00);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Consultation type correctly rejected due to empty name.");
        } else {
            System.out.println("Test failed: Consultation type was created despite missing fields.");
        }
    }

    static void testCreateConsultationTypeWithNegativeRate() {

        System.out.println("\n===== TEST CREATE CONSULTATION TYPE WITH NEGATIVE RATE =====");

        LinkedHashMap<String, String> result =
                consultationTypeMng.createConsultationType("Invalid Rate Type", -10.00);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Consultation type correctly rejected due to negative rate.");
        } else {
            System.out.println("Test failed: Consultation type was created despite negative rate.");
        }
    }

    // =========================================================
    // UPDATE TESTS
    // =========================================================

    static void testUpdateConsultationType() {

        System.out.println("\n===== TEST UPDATE CONSULTATION TYPE =====");

        List<ConsultationType> consultationTypes =
                ConsultationTypeManagement.getAllConsultationTypes();

        if (consultationTypes.isEmpty()) {
            System.out.println("Test failed: No consultation types available.");
            return;
        }

        ConsultationType targetConsultationType = consultationTypes.get(0);

        String newTypeName = targetConsultationType.getTypeName() + " (Updated)";
        double newBaseRate = targetConsultationType.getBaseRate() + 10.00;

        System.out.println("Updating Consultation Type : " + targetConsultationType.getConsultationTypeId());

        LinkedHashMap<String, String> result =
                consultationTypeMng.updateConsultationType(targetConsultationType, newTypeName, newBaseRate);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        ConsultationType updated =
                ConsultationTypeManagement.getConsultationTypeById(targetConsultationType.getConsultationTypeId());

        if (updated != null && updated.getTypeName().equals(newTypeName) && updated.getBaseRate() == newBaseRate) {
            System.out.println("Update test PASSED! Consultation type updated correctly.");
        } else {
            System.out.println("Update test FAILED! Consultation type was not updated.");
        }
    }

    static void testUpdateConsultationTypeWithInvalidId() {

        System.out.println("\n===== TEST UPDATE CONSULTATION TYPE WITH INVALID ID =====");

        ConsultationType invalidConsultationType = new ConsultationType(
                "#CTYPEinvalid",
                "Ghost Type",
                20.00,
                ConsultationTypeStatus.ACTIVE
        );

        LinkedHashMap<String, String> result =
                consultationTypeMng.updateConsultationType(invalidConsultationType, "Renamed", 25.00);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Invalid consultation type correctly rejected.");
        } else {
            System.out.println("Test failed: Invalid consultation type unexpectedly updated.");
        }
    }

    // =========================================================
    // DEACTIVATE / ACTIVATE TESTS
    // =========================================================

    static void testDeactivateConsultationType() {

        System.out.println("\n===== TEST DEACTIVATE CONSULTATION TYPE =====");

        List<ConsultationType> activeTypes =
                ConsultationTypeManagement.getActiveConsultationTypes();

        if (activeTypes.isEmpty()) {
            System.out.println("Test failed: No active consultation types available.");
            return;
        }

        ConsultationType targetConsultationType = activeTypes.get(activeTypes.size() - 1);

        System.out.println("Deactivating Consultation Type : " + targetConsultationType.getConsultationTypeId());

        LinkedHashMap<String, String> result =
                consultationTypeMng.deactivateConsultationType(targetConsultationType.getConsultationTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        ConsultationType updated =
                ConsultationTypeManagement.getConsultationTypeById(targetConsultationType.getConsultationTypeId());

        if (updated != null && updated.getStatus() == ConsultationTypeStatus.INACTIVE) {
            System.out.println("Deactivation test PASSED! Status is INACTIVE.");
        } else {
            System.out.println("Deactivation test FAILED! Status was not updated.");
        }
    }

    static void testActivateConsultationType() {

        System.out.println("\n===== TEST ACTIVATE CONSULTATION TYPE =====");

        List<ConsultationType> allTypes =
                ConsultationTypeManagement.getAllConsultationTypes();

        ConsultationType inactiveType = null;
        for (ConsultationType ct : allTypes) {
            if (ct.getStatus() == ConsultationTypeStatus.INACTIVE) {
                inactiveType = ct;
                break;
            }
        }

        if (inactiveType == null) {
            System.out.println("Test failed: No inactive consultation types available. Run deactivate test first.");
            return;
        }

        System.out.println("Activating Consultation Type : " + inactiveType.getConsultationTypeId());

        LinkedHashMap<String, String> result =
                consultationTypeMng.activateConsultationType(inactiveType.getConsultationTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        ConsultationType updated =
                ConsultationTypeManagement.getConsultationTypeById(inactiveType.getConsultationTypeId());

        if (updated != null && updated.getStatus() == ConsultationTypeStatus.ACTIVE) {
            System.out.println("Activation test PASSED! Status is ACTIVE.");
        } else {
            System.out.println("Activation test FAILED! Status was not updated.");
        }
    }

    static void testDeactivateAlreadyInactiveConsultationType() {

        System.out.println("\n===== TEST DEACTIVATE ALREADY INACTIVE CONSULTATION TYPE =====");

        List<ConsultationType> allTypes =
                ConsultationTypeManagement.getAllConsultationTypes();

        ConsultationType inactiveType = null;
        for (ConsultationType ct : allTypes) {
            if (ct.getStatus() == ConsultationTypeStatus.INACTIVE) {
                inactiveType = ct;
                break;
            }
        }

        if (inactiveType == null) {
            System.out.println("Test skipped: No inactive consultation types available.");
            return;
        }

        LinkedHashMap<String, String> result =
                consultationTypeMng.deactivateConsultationType(inactiveType.getConsultationTypeId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Already-inactive consultation type correctly rejected.");
        } else {
            System.out.println("Test failed: Already-inactive consultation type was deactivated again.");
        }
    }

    // =========================================================
    // GENERATE ID TEST
    // =========================================================

    static void testGenerateConsultationTypeId() {

        System.out.println("\n===== TEST GENERATE CONSULTATION TYPE ID =====");

        String consultationTypeId = ConsultationTypeManagement.generateConsultationTypeId();

        System.out.println("Generated Consultation Type ID : " + consultationTypeId);
    }
}