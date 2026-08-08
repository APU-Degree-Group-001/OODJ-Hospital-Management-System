/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.List;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;


/**
 *
 * @author sheaw
 */
public class HospitalAssetManagement {
    private static List<HospitalAsset> allHospitalAssets = new ArrayList<>();
    private static List<HospitalAssetAllocation> allHospitalAssetAllocations = new ArrayList<>();
    
    public static void loadHospitalAssets() {
        
    }
    
    public static void loadHospitalAssetAllocations() {
        
    }
    
    public static HospitalAsset getHospitalAssetById(String assetId) {
        return null;
    }
    
    public static HospitalAssetAllocation getHospitalAssetAllocationById(String allocationId) {
        return null;
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
}
