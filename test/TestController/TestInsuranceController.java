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
import oodj_hospital_management_system.controllers.InsuranceController;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;

public class TestInsuranceController {
    private static InsuranceController insuranceCtrl;
 
    public static void main(String[] args) {
 
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        if (adminStaff == null) {
            System.out.println("Test failed: Admin Staff '#ADMd4e8f' was not found.");
            return;
        }
 
        insuranceCtrl = new InsuranceController(adminStaff);
 
        // Test loading
//        testGetAllInsurances();
 
        // Test lookup
//        testGetInsuranceById();
 
        // Test search
//        testSearchInsurances();
 
        // Test sort
//        testSortInsurancesById();
//        testSortInsurancesByName();
 
        // Test creation
        // Uncomment when needed because these modify insurances.txt AND write to the log file
//        testCreateInsurance();
 
        // Test modification
        // Uncomment when needed because these modify insurances.txt AND write to the log file
//        testUpdateInsurance();
//        testDisableInsurance();
    }
 
    // =========================================================
    // PRINT METHOD
    // =========================================================
 
    public static void testPrintInsurances(List<Insurance> insuranceList) {
 
        System.out.printf(
                "%-10s %-20s %-10s %-14s%n",
                "Insurance ID", "Name", "Accepted", "Coverage %"
        );
        System.out.println("-----------------------------------------------------------------------");
 
        for (Insurance insurance : insuranceList) {
            System.out.printf(
                    "%-10s %-20s %-10s %-14.2f%n",
                    insurance.getInsuranceId(),
                    insurance.getInsuranceName(),
                    insurance.isAccepted(),
                    insurance.getCoveragePercentage()
            );
        }
    }
 
    // =========================================================
    // LOAD TEST
    // =========================================================
 
    static void testGetAllInsurances() {
 
        System.out.println("\n===== TEST GET ALL INSURANCES (via Controller) =====");
 
        List<Insurance> insurances = insuranceCtrl.getAllInsurances();
 
        System.out.println("Total Insurances: " + insurances.size());
 
        testPrintInsurances(insurances);
    }
 
    // =========================================================
    // LOOKUP TEST
    // =========================================================
 
    static void testGetInsuranceById() {
 
        System.out.println("\n===== TEST GET INSURANCE BY ID (via Controller) =====");
 
        List<Insurance> insurances = insuranceCtrl.getAllInsurances();
 
        if (insurances.isEmpty()) {
            System.out.println("No insurances available.");
            return;
        }
 
        String insuranceId = insurances.get(0).getInsuranceId();
 
        Insurance insurance = insuranceCtrl.getInsuranceById(insuranceId);
 
        if (insurance != null) {
            System.out.println("Insurance found: " + insurance.getInsuranceId() + " | " + insurance.getInsuranceName());
        } else {
            System.out.println("Test failed: Insurance not found.");
        }
    }
 
    // =========================================================
    // SEARCH TEST
    // =========================================================
 
    static void testSearchInsurances() {
 
        System.out.println("\n===== TEST SEARCH INSURANCES (via Controller) =====");
 
        String keyword = "#INS001";
 
        List<Insurance> result = insuranceCtrl.searchInsurances(keyword);
 
        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched insurances: " + result.size());
 
        testPrintInsurances(result);
    }
 
    // =========================================================
    // SORT TESTS
    // =========================================================
 
    static void testSortInsurancesById() {
 
        System.out.println("\n===== TEST SORT INSURANCES BY ID (via Controller) =====");
 
        List<Insurance> insurances = insuranceCtrl.getAllInsurances();
        List<Insurance> sorted = insuranceCtrl.sortInsurancesById(insurances);
 
        testPrintInsurances(sorted);
    }
 
    static void testSortInsurancesByName() {
 
        System.out.println("\n===== TEST SORT INSURANCES BY NAME (via Controller) =====");
 
        List<Insurance> insurances = insuranceCtrl.getAllInsurances();
        List<Insurance> sorted = insuranceCtrl.sortInsurancesByName(insurances);
 
        testPrintInsurances(sorted);
    }
 
    // =========================================================
    // CREATE TEST
    // =========================================================
 
    static void testCreateInsurance() {
 
        System.out.println("\n===== TEST CREATE INSURANCE (via Controller) =====");
 
        LinkedHashMap<String, String> result = insuranceCtrl.createInsurance(
                "Test Insurance Plan",
                true,
                70.00
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Insurance ID : " + result.get("newInsuranceId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    // =========================================================
    // MODIFY TESTS
    // =========================================================
 
    static void testUpdateInsurance() {
 
        System.out.println("\n===== TEST UPDATE INSURANCE (via Controller) =====");
 
        List<Insurance> insurances = insuranceCtrl.getAllInsurances();
 
        if (insurances.isEmpty()) {
            System.out.println("No insurances available to update.");
            return;
        }
 
        Insurance insurance = insurances.get(0);
 
        LinkedHashMap<String, String> result = insuranceCtrl.updateInsurance(
                insurance,
                insurance.getInsuranceName(),
                true,
                80.00
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testDisableInsurance() {
 
        System.out.println("\n===== TEST DISABLE INSURANCE (soft-delete via Controller) =====");
 
        List<Insurance> insurances = insuranceCtrl.getAllInsurances();
 
        if (insurances.isEmpty()) {
            System.out.println("No insurances available to disable.");
            return;
        }
 
        // Pick the last insurance so it doesn't collide with the one just updated above
        Insurance insurance = insurances.get(insurances.size() - 1);
 
        LinkedHashMap<String, String> result = insuranceCtrl.updateInsurance(
                insurance,
                insurance.getInsuranceName(),
                false,
                insurance.getCoveragePercentage()
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}
