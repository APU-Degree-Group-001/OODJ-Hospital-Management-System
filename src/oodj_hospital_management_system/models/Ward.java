/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;

/**
 *
 * @author sheaw
 */
public class Ward extends HospitalAsset {
    private int totalBeds;
    
    public Ward(
            String assetId,
            String assetName,
            String assetType,
            String imagePath,
            String location,
            String description,
            String status,
            int totalBeds
    ) {
        super(assetId, assetName, assetType, imagePath, location, description, status);
        this.totalBeds = totalBeds;
    }
    
    public int getTotalBeds() {
        return totalBeds;
    }
    
    public void setTotalBeds(int totalBeds) {
        this.totalBeds = totalBeds;
    }
    
    @Override
    public LinkedHashMap<String, String> getHospitalAssetMap() {
        LinkedHashMap<String, String> map = super.getHospitalAssetMap();
        map.put("totalBeds", Integer.toString(totalBeds));
        return map;
    }
}
