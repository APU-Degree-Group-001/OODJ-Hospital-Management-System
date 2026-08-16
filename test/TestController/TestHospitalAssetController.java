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
import oodj_hospital_management_system.controllers.HospitalAssetController;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.services.UserManagement;

public class TestHospitalAssetController {
    private static HospitalAssetController assetCtrl;
 
    public static void main(String[] args) {
 
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        if (adminStaff == null) {
            System.out.println("Test failed: Admin Staff '#ADMd4e8f' was not found.");
            return;
        }
 
        assetCtrl = new HospitalAssetController(adminStaff);
 
        // Test loading
//        testGetAllHospitalAssets();
//        testGetAllHospitalAssetAllocations();
 
        // Test lookup
//        testGetHospitalAssetById();
//        testGetHospitalAssetAllocationById();
 
        // Test search / sort / count
//        testSearchHospitalAssets();
//        testSortHospitalAssets();
//        testGetConsultationRoomCount();
 
        // Test creation - Uncomment when needed, these modify the .txt files AND write to the log file
//        testCreateHospitalAsset();
//        testCreateWard();
 
        // Test modification
//        testModifyHospitalAsset();
//        testModifyWard();
 
        // Test allocation lifecycle
//        testCreateHospitalAssetAllocation();
//        testSetHospitalAssetAllocationSchedule();
//        testStartHospitalAssetAllocation();
//        testReleaseHospitalAssetAllocation();
 
        // Test deletion (admin staff only)
//        testDeleteHospitalAsset();
//        testDeleteHospitalAssetAllocation();
    }
 
    // =========================================================
    // PRINT METHOD
    // =========================================================
 
    public static void testPrintAssets(List<HospitalAsset> assetList) {
 
        System.out.printf("%-10s %-20s %-14s %-14s%n", "Asset ID", "Name", "Type", "Location");
        System.out.println("-----------------------------------------------------------------------");
 
        for (HospitalAsset asset : assetList) {
            System.out.printf("%-10s %-20s %-14s %-14s%n",
                    asset.getAssetId(), asset.getAssetName(), asset.getAssetType(), asset.getLocation());
        }
    }
 
    // =========================================================
    // LOAD TESTS
    // =========================================================
 
    static void testGetAllHospitalAssets() {
 
        System.out.println("\n===== TEST GET ALL HOSPITAL ASSETS (via Controller) =====");
 
        List<HospitalAsset> assets = assetCtrl.getAllHospitalAssets();
 
        System.out.println("Total Assets: " + assets.size());
 
        testPrintAssets(assets);
    }
 
    static void testGetAllHospitalAssetAllocations() {
 
        System.out.println("\n===== TEST GET ALL HOSPITAL ASSET ALLOCATIONS (via Controller) =====");
 
        List<HospitalAssetAllocation> allocations = assetCtrl.getAllHospitalAssetAllocations();
 
        System.out.println("Total Allocations: " + allocations.size());
 
        for (HospitalAssetAllocation a : allocations) {
            System.out.println(a.getAllocationId() + " | " + a.getConsultationId());
        }
    }
 
    // =========================================================
    // LOOKUP TESTS
    // =========================================================
 
    static void testGetHospitalAssetById() {
 
        System.out.println("\n===== TEST GET HOSPITAL ASSET BY ID (via Controller) =====");
 
        List<HospitalAsset> assets = assetCtrl.getAllHospitalAssets();
 
        if (assets.isEmpty()) {
            System.out.println("No assets available.");
            return;
        }
 
        String id = assets.get(0).getAssetId();
        HospitalAsset asset = assetCtrl.getHospitalAssetById(id);
 
        System.out.println(asset != null ? "Found: " + asset.getAssetId() + " | " + asset.getAssetName() : "Test failed: not found.");
    }
 
    static void testGetHospitalAssetAllocationById() {
 
        System.out.println("\n===== TEST GET HOSPITAL ASSET ALLOCATION BY ID (via Controller) =====");
 
        List<HospitalAssetAllocation> allocations = assetCtrl.getAllHospitalAssetAllocations();
 
        if (allocations.isEmpty()) {
            System.out.println("No allocations available.");
            return;
        }
 
        String id = allocations.get(0).getAllocationId();
        HospitalAssetAllocation allocation = assetCtrl.getHospitalAssetAllocationById(id);
 
        System.out.println(allocation != null ? "Found: " + allocation.getAllocationId() : "Test failed: not found.");
    }
 
    // =========================================================
    // SEARCH / SORT / COUNT TESTS
    // =========================================================
 
    static void testSearchHospitalAssets() {
 
        System.out.println("\n===== TEST SEARCH HOSPITAL ASSETS (via Controller) =====");
 
        String keyword = "Room";
 
        List<HospitalAsset> result = assetCtrl.searchHospitalAssets(keyword);
 
        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched assets: " + result.size());
 
        testPrintAssets(result);
    }
 
    static void testSortHospitalAssets() {
 
        System.out.println("\n===== TEST SORT HOSPITAL ASSETS (via Controller) =====");
 
        List<HospitalAsset> sorted = assetCtrl.sortHospitalAssets("name", true);
 
        testPrintAssets(sorted);
    }
 
    static void testGetConsultationRoomCount() {
 
        System.out.println("\n===== TEST GET CONSULTATION ROOM COUNT (via Controller) =====");
 
        System.out.println("Consultation Room Count: " + assetCtrl.getConsultationRoomCount());
    }
 
    // =========================================================
    // CREATE TESTS
    // =========================================================
 
    static void testCreateHospitalAsset() {
 
        System.out.println("\n===== TEST CREATE HOSPITAL ASSET (via Controller) =====");
 
        LinkedHashMap<String, String> result = assetCtrl.createHospitalAsset(
                "EQUIPMENT",
                "Test X-Ray Machine",
                "",
                "Level 2",
                "Created via controller test"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Asset ID : " + result.get("newAssetId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    static void testCreateWard() {
 
        System.out.println("\n===== TEST CREATE WARD (via Controller) =====");
 
        LinkedHashMap<String, String> result = assetCtrl.createWard(
                "WARD",
                "Test General Ward",
                "",
                "Level 3",
                "Created via controller test",
                10
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Asset ID : " + result.get("newAssetId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    // =========================================================
    // MODIFY TESTS
    // =========================================================
 
    static void testModifyHospitalAsset() {
 
        System.out.println("\n===== TEST MODIFY HOSPITAL ASSET (via Controller) =====");
 
        List<HospitalAsset> assets = assetCtrl.getAllHospitalAssets();
 
        if (assets.isEmpty()) {
            System.out.println("No assets available to modify.");
            return;
        }
 
        HospitalAsset asset = assets.get(0);
 
        LinkedHashMap<String, String> result = assetCtrl.modifyHospitalAsset(
                asset,
                asset.getAssetType(),
                asset.getAssetName(),
                "",
                "Level 2 - Updated",
                "Updated via controller test"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testModifyWard() {
 
        System.out.println("\n===== TEST MODIFY WARD (via Controller) =====");
 
        List<HospitalAsset> assets = assetCtrl.getAllHospitalAssets();
 
        Ward ward = null;
        for (HospitalAsset asset : assets) {
            if (asset instanceof Ward) {
                ward = (Ward) asset;
                break;
            }
        }
 
        if (ward == null) {
            System.out.println("No ward available to modify.");
            return;
        }
 
        LinkedHashMap<String, String> result = assetCtrl.modifyWard(
                ward,
                ward.getAssetType(),
                ward.getAssetName(),
                "",
                ward.getLocation(),
                "Updated via controller test",
                15
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    // =========================================================
    // ALLOCATION LIFECYCLE TESTS
    // =========================================================
 
    static void testCreateHospitalAssetAllocation() {
 
        System.out.println("\n===== TEST CREATE HOSPITAL ASSET ALLOCATION (via Controller) =====");
 
        List<HospitalAsset> assets = assetCtrl.getAllHospitalAssets();
 
        if (assets.isEmpty()) {
            System.out.println("No assets available.");
            return;
        }
 
        String assetId = assets.get(0).getAssetId();
 
        LinkedHashMap<String, String> result = assetCtrl.createHospitalAssetAllocation(
                assetId,
                "#CON003", // replace with a real consultation ID
                "2026-09-01 09:00",
                "2026-09-01 09:30",
                ""
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Allocation ID : " + result.get("newAllocationId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    static void testSetHospitalAssetAllocationSchedule() {
 
        System.out.println("\n===== TEST SET HOSPITAL ASSET ALLOCATION SCHEDULE (via Controller) =====");
 
        List<HospitalAssetAllocation> allocations = assetCtrl.getAllHospitalAssetAllocations();
 
        if (allocations.isEmpty()) {
            System.out.println("No allocations available.");
            return;
        }
 
        String allocationId = allocations.get(0).getAllocationId();
 
        LinkedHashMap<String, String> result = assetCtrl.setHospitalAssetAllocationSchedule(
                allocationId,
                "2026-09-01 10:00",
                "2026-09-01 10:30"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testStartHospitalAssetAllocation() {
 
        System.out.println("\n===== TEST START HOSPITAL ASSET ALLOCATION (via Controller) =====");
 
        List<HospitalAssetAllocation> allocations = assetCtrl.getAllHospitalAssetAllocations();
 
        if (allocations.isEmpty()) {
            System.out.println("No allocations available.");
            return;
        }
 
        String allocationId = allocations.get(0).getAllocationId();
 
        LinkedHashMap<String, String> result = assetCtrl.startHospitalAssetAllocation(allocationId, "");
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testReleaseHospitalAssetAllocation() {
 
        System.out.println("\n===== TEST RELEASE HOSPITAL ASSET ALLOCATION (via Controller) =====");
 
        List<HospitalAssetAllocation> allocations = assetCtrl.getAllHospitalAssetAllocations();
 
        if (allocations.isEmpty()) {
            System.out.println("No allocations available.");
            return;
        }
 
        String allocationId = allocations.get(0).getAllocationId();
 
        LinkedHashMap<String, String> result = assetCtrl.releaseHospitalAssetAllocation(allocationId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    // =========================================================
    // DELETE TESTS (admin staff only)
    // =========================================================
 
    static void testDeleteHospitalAsset() {
 
        System.out.println("\n===== TEST DELETE HOSPITAL ASSET (via Controller) =====");
 
        List<HospitalAsset> assets = assetCtrl.getAllHospitalAssets();
 
        if (assets.isEmpty()) {
            System.out.println("No assets available to delete.");
            return;
        }
 
        String assetId = assets.get(assets.size() - 1).getAssetId();
 
        LinkedHashMap<String, String> result = assetCtrl.deleteHospitalAsset(assetId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testDeleteHospitalAssetAllocation() {
 
        System.out.println("\n===== TEST DELETE HOSPITAL ASSET ALLOCATION (via Controller) =====");
 
        List<HospitalAssetAllocation> allocations = assetCtrl.getAllHospitalAssetAllocations();
 
        if (allocations.isEmpty()) {
            System.out.println("No allocations available to delete.");
            return;
        }
 
        String allocationId = allocations.get(allocations.size() - 1).getAllocationId();
 
        LinkedHashMap<String, String> result = assetCtrl.deleteHospitalAssetAllocation(allocationId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}
