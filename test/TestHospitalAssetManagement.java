/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.AdminStaff;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.services.HospitalAssetManagement;
import oodj_hospital_management_system.services.UserManagement;

/**
 *
 * @author sheaw
 */
public class TestHospitalAssetManagement {

    private static HospitalAssetManagement assetMng;

    // NOTE: Update this to a valid consultation ID that actually exists
    // in your consultations.txt data file before running allocation tests.
    private static final String SAMPLE_CONSULTATION_ID = "#CON001";

    public static void main(String[] args) {

        // Get the active user once - assuming admin staff manages assets
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");

        if (adminStaff == null) {
            System.out.println(
                    "Test failed: Admin Staff '#ADMd4e8f' was not found."
            );
            return;
        }

        // Create HospitalAssetManagement once
        assetMng = new HospitalAssetManagement(adminStaff);

        // Test loading
//        testLoadHospitalAssets();
//        testLoadHospitalAssetAllocations();

        // Test get by ID
//        testGetHospitalAssetById();
//        testGetHospitalAssetAllocationById();

        // Test search
//        testSearchHospitalAssets();

        // Test sort
//        testSortHospitalAssetsByAssetId();
//        testSortHospitalAssetsByAssetType();
//        testSortHospitalAssetsByAssetName();
//        testSortHospitalAssetsByLocation();

        // Test consultation room count
//        testGetConsultationRoomCount();

        // Test ID generation
//        testGenerateHospitalAssetId();
//        testGenerateHospitalAssetAllocationId();

        // Test creation
        // Uncomment when needed because these modify the respective .txt files
//        testCreateHospitalAsset();
//        testCreateWard();
//        testCreateHospitalAssetAllocation();

        // Test modification
        // Uncomment when needed because these modify the respective .txt files
//        testModifyHospitalAsset();
//        testModifyWard();
//        testReleaseHospitalAssetAllocation();

        // Test deletion
        // Uncomment when needed because these modify the respective .txt files
//        testDeleteHospitalAsset();
        testDeleteHospitalAssetAllocation();
    }

    // =========================================================
    // PRINT METHODS
    // =========================================================

    public static void testPrintHospitalAssets(List<HospitalAsset> assetList) {

        System.out.printf(
                "%-10s %-18s %-20s %-15s %-30s %-10s%n",
                "Asset ID",
                "Type",
                "Name",
                "Location",
                "Description",
                "Beds"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (HospitalAsset asset : assetList) {

            String beds = (asset instanceof Ward)
                    ? String.valueOf(((Ward) asset).getTotalBeds())
                    : "-";

            System.out.printf(
                    "%-10s %-18s %-20s %-15s %-30s %-10s%n",
                    asset.getAssetId(),
                    asset.getAssetType(),
                    asset.getAssetName(),
                    asset.getLocation(),
                    asset.getDescription(),
                    beds
            );
        }
    }

    public static void testPrintHospitalAssetAllocations(List<HospitalAssetAllocation> allocationList) {

        System.out.printf(
                "%-12s %-12s %-15s %-22s %-22s%n",
                "Alloc. ID",
                "Asset ID",
                "Consult. ID",
                "Allocated DateTime",
                "Released DateTime"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (HospitalAssetAllocation allocation : allocationList) {

            System.out.printf(
                    "%-12s %-12s %-15s %-22s %-22s%n",
                    allocation.getAllocationId(),
                    allocation.getAssetId(),
                    allocation.getConsultationId(),
                    allocation.getAllocationDateTime(),
                    allocation.getReleasedDateTime()
            );
        }
    }

    // =========================================================
    // LOAD TESTS
    // =========================================================

    static void testLoadHospitalAssets() {

        System.out.println("\n===== TEST LOAD HOSPITAL ASSETS =====");

        List<HospitalAsset> assets =
                HospitalAssetManagement.getAllHospitalAssets();

        System.out.println("Total Hospital Assets: " + assets.size());

        testPrintHospitalAssets(assets);
    }

    static void testLoadHospitalAssetAllocations() {

        System.out.println("\n===== TEST LOAD HOSPITAL ASSET ALLOCATIONS =====");

        List<HospitalAssetAllocation> allocations =
                HospitalAssetManagement.getAllHospitalAssetAllocations();

        System.out.println("Total Hospital Asset Allocations: " + allocations.size());

        testPrintHospitalAssetAllocations(allocations);
    }

    // =========================================================
    // GET BY ID TESTS
    // =========================================================

    static void testGetHospitalAssetById() {

        System.out.println("\n===== TEST GET HOSPITAL ASSET BY ID =====");

        List<HospitalAsset> assets =
                HospitalAssetManagement.getAllHospitalAssets();

        if (assets.isEmpty()) {
            System.out.println("No hospital assets available.");
            return;
        }

        String assetId = assets.get(0).getAssetId();

        HospitalAsset asset =
                HospitalAssetManagement.getHospitalAssetById(assetId);

        if (asset != null) {

            System.out.println("Hospital Asset found:");
            System.out.println("Asset ID : " + asset.getAssetId());
            System.out.println("Type     : " + asset.getAssetType());
            System.out.println("Name     : " + asset.getAssetName());

        } else {
            System.out.println("Test failed: Hospital Asset not found.");
        }
    }

    static void testGetHospitalAssetAllocationById() {

        System.out.println("\n===== TEST GET HOSPITAL ASSET ALLOCATION BY ID =====");

        List<HospitalAssetAllocation> allocations =
                HospitalAssetManagement.getAllHospitalAssetAllocations();

        if (allocations.isEmpty()) {
            System.out.println("No hospital asset allocations available.");
            return;
        }

        String allocationId = allocations.get(0).getAllocationId();

        HospitalAssetAllocation allocation =
                HospitalAssetManagement.getHospitalAssetAllocationById(allocationId);

        if (allocation != null) {

            System.out.println("Hospital Asset Allocation found:");
            System.out.println("Allocation ID : " + allocation.getAllocationId());
            System.out.println("Asset ID      : " + allocation.getAssetId());
            System.out.println("Consult. ID   : " + allocation.getConsultationId());

        } else {
            System.out.println("Test failed: Hospital Asset Allocation not found.");
        }
    }

    // =========================================================
    // SEARCH TESTS
    // =========================================================

    static void testSearchHospitalAssets() {

        System.out.println("\n===== TEST SEARCH HOSPITAL ASSETS =====");

        String keyword = "ward";

        List<HospitalAsset> result =
                HospitalAssetManagement.searchHospitalAssets(keyword);

        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched assets: " + result.size());

        testPrintHospitalAssets(result);
    }

    // =========================================================
    // SORT TESTS
    // =========================================================

    static void testSortHospitalAssetsByAssetId() {

        System.out.println("\n===== TEST SORT HOSPITAL ASSETS BY ASSET ID =====");

        List<HospitalAsset> sorted =
                HospitalAssetManagement.sortHospitalAssets("assetid", true);

        testPrintHospitalAssets(sorted);
    }

    static void testSortHospitalAssetsByAssetType() {

        System.out.println("\n===== TEST SORT HOSPITAL ASSETS BY ASSET TYPE =====");

        List<HospitalAsset> sorted =
                HospitalAssetManagement.sortHospitalAssets("assettype", true);

        testPrintHospitalAssets(sorted);
    }

    static void testSortHospitalAssetsByAssetName() {

        System.out.println("\n===== TEST SORT HOSPITAL ASSETS BY ASSET NAME =====");

        List<HospitalAsset> sorted =
                HospitalAssetManagement.sortHospitalAssets("assetname", true);

        testPrintHospitalAssets(sorted);
    }

    static void testSortHospitalAssetsByLocation() {

        System.out.println("\n===== TEST SORT HOSPITAL ASSETS BY LOCATION =====");

        List<HospitalAsset> sorted =
                HospitalAssetManagement.sortHospitalAssets("location", false);

        testPrintHospitalAssets(sorted);
    }

    // =========================================================
    // CONSULTATION ROOM COUNT TEST
    // =========================================================

    static void testGetConsultationRoomCount() {

        System.out.println("\n===== TEST GET CONSULTATION ROOM COUNT =====");

        int count = HospitalAssetManagement.getConsultationRoomCount();

        System.out.println("Total Consultation Rooms: " + count);
    }

    // =========================================================
    // GENERATE ID TESTS
    // =========================================================

    static void testGenerateHospitalAssetId() {

        System.out.println("\n===== TEST GENERATE HOSPITAL ASSET ID =====");

        String assetId = HospitalAssetManagement.generateHospitalAssetId();

        System.out.println("Generated Asset ID : " + assetId);
    }

    static void testGenerateHospitalAssetAllocationId() {

        System.out.println("\n===== TEST GENERATE HOSPITAL ASSET ALLOCATION ID =====");

        String allocationId = HospitalAssetManagement.generateHospitalAssetAllocationId();

        System.out.println("Generated Allocation ID : " + allocationId);
    }

    // =========================================================
    // CREATE TESTS
    // =========================================================

    static void testCreateHospitalAsset() {

        System.out.println("\n===== TEST CREATE HOSPITAL ASSET =====");

        LinkedHashMap<String, String> result = assetMng.createHospitalAsset(
                "Consultation Room",
                "Room A",
                "images/roomA.png",
                "Level 2, Block B",
                "Standard consultation room with basic equipment"
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Asset ID : " + result.get("newAssetId"));
    }

    static void testCreateWard() {

        System.out.println("\n===== TEST CREATE WARD =====");

        LinkedHashMap<String, String> result = assetMng.createWard(
                "Ward",
                "General Ward 1",
                "images/ward1.png",
                "Level 3, Block A",
                "General ward for stable patients",
                20
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Asset ID : " + result.get("newAssetId"));
    }

    static void testCreateHospitalAssetAllocation() {

        System.out.println("\n===== TEST CREATE HOSPITAL ASSET ALLOCATION =====");

        List<HospitalAsset> assets =
                HospitalAssetManagement.getAllHospitalAssets();

        if (assets.isEmpty()) {
            System.out.println("No hospital assets available to allocate.");
            return;
        }

        String assetId = assets.get(0).getAssetId();

        LinkedHashMap<String, String> result =
                assetMng.createHospitalAssetAllocation(assetId, SAMPLE_CONSULTATION_ID);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Allocation ID : " + result.get("newAllocationId"));
    }

    // =========================================================
    // MODIFY TESTS
    // =========================================================

    static void testModifyHospitalAsset() {

        System.out.println("\n===== TEST MODIFY HOSPITAL ASSET =====");

        List<HospitalAsset> assets =
                HospitalAssetManagement.getAllHospitalAssets();

        if (assets.isEmpty()) {
            System.out.println("No hospital assets available to modify.");
            return;
        }

        HospitalAsset asset = assets.get(0);

        LinkedHashMap<String, String> result = assetMng.modifyHospitalAsset(
                asset,
                asset.getAssetType(),
                "Updated Asset Name",
                asset.getImagePath(),
                asset.getLocation(),
                "Updated description for testing"
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testModifyWard() {

        System.out.println("\n===== TEST MODIFY WARD =====");

        List<HospitalAsset> assets =
                HospitalAssetManagement.getAllHospitalAssets();

        Ward ward = null;
        for (HospitalAsset asset : assets) {
            if (asset instanceof Ward) {
                ward = (Ward) asset;
                break;
            }
        }

        if (ward == null) {
            System.out.println("No wards available to modify.");
            return;
        }

        LinkedHashMap<String, String> result = assetMng.modifyWard(
                ward,
                ward.getAssetType(),
                ward.getAssetName(),
                ward.getImagePath(),
                ward.getLocation(),
                ward.getDescription(),
                30
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testReleaseHospitalAssetAllocation() {

        System.out.println("\n===== TEST RELEASE HOSPITAL ASSET ALLOCATION =====");

        List<HospitalAssetAllocation> allocations =
                HospitalAssetManagement.getAllHospitalAssetAllocations();

        if (allocations.isEmpty()) {
            System.out.println("No hospital asset allocations available to release.");
            return;
        }

        String allocationId = allocations.get(0).getAllocationId();

        LinkedHashMap<String, String> result =
                assetMng.releaseHospitalAssetAllocation(allocationId);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    // =========================================================
    // DELETE TESTS
    // =========================================================

    static void testDeleteHospitalAsset() {

        System.out.println("\n===== TEST DELETE HOSPITAL ASSET =====");

        List<HospitalAsset> assets =
                HospitalAssetManagement.getAllHospitalAssets();

        if (assets.isEmpty()) {
            System.out.println("No hospital assets available to delete.");
            return;
        }

        HospitalAsset target = assets.get(assets.size() - 1);

        System.out.println("Deleting Hospital Asset : " + target.getAssetId());

        LinkedHashMap<String, String> result =
                assetMng.deleteHospitalAsset(target.getAssetId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testDeleteHospitalAssetAllocation() {

        System.out.println("\n===== TEST DELETE HOSPITAL ASSET ALLOCATION =====");

        List<HospitalAssetAllocation> allocations =
                HospitalAssetManagement.getAllHospitalAssetAllocations();

        if (allocations.isEmpty()) {
            System.out.println("No hospital asset allocations available to delete.");
            return;
        }

        HospitalAssetAllocation target = allocations.get(allocations.size() - 1);

        System.out.println("Deleting Hospital Asset Allocation : " + target.getAllocationId());

        LinkedHashMap<String, String> result =
                assetMng.deleteHospitalAssetAllocation(target.getAllocationId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}