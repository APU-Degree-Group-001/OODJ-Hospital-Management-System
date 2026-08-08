/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

/**
 *
 * @author sheaw
 */
public class Ward extends HospitalAsset {
    private int totalBeds;
    
    public Ward(
            String assetId,
            String assetName,
            String imagePath,
            String location,
            String description,
            int totalBeds
    ) {
        super(assetId, assetName, imagePath, location, description);
        this.totalBeds = totalBeds;
    }
    
    public int getTotalBeds() {
        return totalBeds;
    }
    
    public void setTotalBeds(int totalBeds) {
        this.totalBeds = totalBeds;
    }
}
