package TestServices;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.InsuranceManagement;
import oodj_hospital_management_system.services.UserManagement;

/**
 *
 * @author sheaw
 */
public class TestInsuranceManagement {

    private static InsuranceManagement insMng;

    public static void main(String[] args) {

        // Get the active user once - assuming admin staff manages insurance
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");

        if (adminStaff == null) {
            System.out.println(
                    "Test failed: Admin Staff '#ADMd4e8f' was not found."
            );
            return;
        }

        // Create InsuranceManagement once
        insMng = new InsuranceManagement(adminStaff);

        // Test loading
//        testLoadInsurances();

        // Test get by ID
//        testGetInsuranceById();

        // Test search
//        testSearchInsurances();

        // Test sort
//        testSortInsurancesById();
//        testSortInsurancesByName();

        // Test ID generation
//        testGenerateInsuranceId();

        // Test creation
        // Uncomment when needed because these modify insurances.txt
//        testCreateInsurance();

        // Test modification
        // Uncomment when needed because these modify insurances.txt
//        testUpdateInsurance();
//        testDisableInsurance();
    }

    // =========================================================
    // PRINT METHOD
    // =========================================================

    public static void testPrintInsurances(List<Insurance> insuranceList) {

        System.out.printf(
                "%-10s %-25s %-10s %-14s%n",
                "Ins. ID",
                "Insurance Name",
                "Accepted",
                "Coverage %"
        );

        System.out.println(
                "-----------------------------------------------------------------"
        );

        for (Insurance insurance : insuranceList) {

            System.out.printf(
                    "%-10s %-25s %-10s %-14.2f%n",
                    insurance.getInsuranceId(),
                    insurance.getInsuranceName(),
                    insurance.isAccepted(),
                    insurance.getCoveragePercentage() * 100 // stored as fraction, display as %
            );
        }
    }

    // =========================================================
    // LOAD TEST
    // =========================================================

    static void testLoadInsurances() {

        System.out.println("\n===== TEST LOAD INSURANCES =====");

        List<Insurance> insurances =
                InsuranceManagement.getAllInsurances();

        System.out.println("Total Insurances: " + insurances.size());

        testPrintInsurances(insurances);
    }

    // =========================================================
    // GET BY ID TEST
    // =========================================================

    static void testGetInsuranceById() {

        System.out.println("\n===== TEST GET INSURANCE BY ID =====");

        List<Insurance> insurances =
                InsuranceManagement.getAllInsurances();

        if (insurances.isEmpty()) {
            System.out.println("No insurances available.");
            return;
        }

        String insuranceId = insurances.get(0).getInsuranceId();

        Insurance insurance =
                InsuranceManagement.getInsuranceById(insuranceId);

        if (insurance != null) {

            System.out.println("Insurance found:");
            System.out.println("Insurance ID : " + insurance.getInsuranceId());
            System.out.println("Name         : " + insurance.getInsuranceName());
            System.out.println("Accepted     : " + insurance.isAccepted());
            System.out.println("Coverage %   : " + (insurance.getCoveragePercentage() * 100) + "%");

        } else {
            System.out.println("Test failed: Insurance not found.");
        }
    }

    // =========================================================
    // SEARCH TEST
    // =========================================================

    static void testSearchInsurances() {

        System.out.println("\n===== TEST SEARCH INSURANCES =====");

        String keyword = "#INS001";

        List<Insurance> result =
                insMng.searchInsurances(keyword);

        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched insurances: " + result.size());

        testPrintInsurances(result);
    }

    // =========================================================
    // SORT TESTS
    // =========================================================

    static void testSortInsurancesById() {

        System.out.println("\n===== TEST SORT INSURANCES BY ID =====");

        List<Insurance> insurances =
                InsuranceManagement.getAllInsurances();

        List<Insurance> sorted =
                insMng.sortInsurancesById(insurances);

        testPrintInsurances(sorted);
    }

    static void testSortInsurancesByName() {

        System.out.println("\n===== TEST SORT INSURANCES BY NAME =====");

        List<Insurance> insurances =
                InsuranceManagement.getAllInsurances();

        List<Insurance> sorted =
                insMng.sortInsurancesByName(insurances);

        testPrintInsurances(sorted);
    }

    // =========================================================
    // GENERATE ID TEST
    // =========================================================

    static void testGenerateInsuranceId() {

        System.out.println("\n===== TEST GENERATE INSURANCE ID =====");

        String insuranceId = InsuranceManagement.generateInsuranceId();

        System.out.println("Generated Insurance ID : " + insuranceId);
    }

    // =========================================================
    // CREATE TEST
    // =========================================================

    static void testCreateInsurance() {

        System.out.println("\n===== TEST CREATE INSURANCE =====");

        LinkedHashMap<String, String> result = insMng.createInsurance(
                "Allianz",
                true,
                0.70 // 70% coverage
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Insurance ID : " + result.get("newInsuranceId"));
    }

    // =========================================================
    // MODIFY TESTS
    // =========================================================

    static void testUpdateInsurance() {

        System.out.println("\n===== TEST UPDATE INSURANCE =====");

        List<Insurance> insurances =
                InsuranceManagement.getAllInsurances();

        if (insurances.isEmpty()) {
            System.out.println("No insurances available to update.");
            return;
        }

        Insurance insurance = insurances.get(0);

        LinkedHashMap<String, String> result = insMng.updateInsurance(
                insurance,
                "Banned By Paureen",
                insurance.isAccepted(),
                0.60 // 80% coverage
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testDisableInsurance() {

        System.out.println("\n===== TEST DISABLE INSURANCE (soft-delete via 'accepted' toggle) =====");

        List<Insurance> insurances =
                InsuranceManagement.getAllInsurances();

        if (insurances.isEmpty()) {
            System.out.println("No insurances available to disable.");
            return;
        }

        // Pick the last insurance so it doesn't collide with the one just updated above
        Insurance insurance = insurances.get(insurances.size() - 1);

        LinkedHashMap<String, String> result = insMng.updateInsurance(
                insurance,
                insurance.getInsuranceName(),
                false, // accepted = false -> soft disable
                insurance.getCoveragePercentage()
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}
