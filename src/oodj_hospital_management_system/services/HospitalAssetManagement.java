/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;


/**
 *
 * @author sheaw
 */
public class HospitalAssetManagement {
    private static final FileHandler assetHandler = new FileHandler(SystemConfiguration.hospitalAssetFilePath);
    private static final FileHandler allocationHandler = new FileHandler(SystemConfiguration.hospitalAssetAllocationFilePath);

    private static final List<HospitalAsset> allHospitalAssets = new ArrayList<>();
    private static final List<HospitalAssetAllocation> allHospitalAssetAllocations = new ArrayList<>();

    public static void loadHospitalAssets() {
        allHospitalAssets.clear();

        List<LinkedHashMap<String, String>> rawData = assetHandler.readFile();

        for (LinkedHashMap<String, String> row : rawData) {
            // assetId, assetType, assetName, imagePath, location, description, totalBeds
                
            if ("Ward".equalsIgnoreCase(row.get("assetType"))) {
                Ward asset = new Ward(
                    row.get("assetId"),
                    row.get("assetType"),
                    row.get("assetName"),
                    row.get("imagePath"),
                    row.get("location"),
                    row.get("description"),
                    Integer.parseInt(row.get("totalBeds"))
                );
                allHospitalAssets.add(asset);
            } else {
                HospitalAsset asset = new HospitalAsset(
                        row.get("assetId"),
                        row.get("assetType"),
                        row.get("assetName"),
                        row.get("imagePath"),
                        row.get("location"),
                        row.get("description")
                );
                allHospitalAssets.add(asset);
            } 
        }
    }

    public static void loadHospitalAssetAllocations() {
        allHospitalAssetAllocations.clear();

        List<LinkedHashMap<String, String>> rawData = allocationHandler.readFile();

        for (LinkedHashMap<String, String> row : rawData) {
            // allocationId, consultationId, assetId, allocatedDateTime
            HospitalAssetAllocation allocation = new HospitalAssetAllocation(
                    row.get("allocationId"),
                    getHospitalAssetById(row.get("assetId")),
                    ConsultationManagement.getConsultationById(row.get("consultationId")),
                    row.get("allocatedDateTime"),
                    row.get("releasedDateTime")
            );

            allHospitalAssetAllocations.add(allocation);
        }
    }

    public static HospitalAsset getHospitalAssetById(String assetId) {
        loadHospitalAssets();
        try {
            if (!allHospitalAssets.stream().anyMatch(asset -> asset.getAssetId().equals(assetId))) {
                throw new Exception(String.format("Invalid Hospital Asset ID: Asset ID '%s' is not found", assetId));
            }
            for (HospitalAsset asset : allHospitalAssets) {
                if (asset.getAssetId().equals(assetId)) {
                    return asset;
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error finding hospital asset -> " + e.getMessage());
            Logger.errorLog("Error finding hospital asset -> " + e.getMessage());
            return null;
        }
    }

    public static HospitalAssetAllocation getHospitalAssetAllocationById(String allocationId) {
        loadHospitalAssetAllocations();
        try {
            if (!allHospitalAssetAllocations.stream().anyMatch(allocation -> allocation.getAllocationId().equals(allocationId))) {
                throw new Exception(String.format("Invalid Hospital Asset Allocation ID: Allocation ID '%s' is not found", allocationId));
            }

            for (HospitalAssetAllocation allocation : allHospitalAssetAllocations) {
                if (allocation.getAllocationId().equals(allocationId)) {
                    return allocation;
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error finding hospital asset allocation -> " + e.getMessage());
            Logger.errorLog("Error finding hospital asset allocation -> " + e.getMessage());
            return null;
        }
    }
    
    // Accessor Method for Hospital Assets
    public static List<HospitalAsset> getAllHospitalAssets() {
        loadHospitalAssets();
        return allHospitalAssets;
    }
    
    public static List<HospitalAssetAllocation> getAllHospitalAssetAllocations() {
        loadHospitalAssetAllocations();
        return allHospitalAssetAllocations;
    }
    
    public static int getConsultationRoomCount() {
        return (int) getAllHospitalAssets().stream()
                .filter(asset -> "Consultation Room".equalsIgnoreCase(asset.getAssetType()))
                .count();
    }
}
