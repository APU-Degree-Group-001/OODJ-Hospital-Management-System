/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

/**
 *
 * @author sheaw
 */
public abstract class HospitalAsset {
    private String assetId;
    private String assetName;
    private String imagePath;
    private String location;
    private String description;
    
    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_OCCUPIED = "Occupied";
    public static final String STATUS_MAINTENANCE = "Maintenance";
    public static final String STATUS_INACTIVE = "Inactive";
    
    private String status;
    
    public HospitalAsset(
            String assetId,
            String assetName,
            String imagePath,
            String location,
            String description
    ) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.imagePath = imagePath;
        this.location = location;
        this.description = description;
        this.status = STATUS_AVAILABLE;
    }
    
    public String getAssetId() {
        return this.assetId;
    }
    
    public String getAssetName() {
        return this.assetName;
    }
    
    public String getImagePath() {
        return this.imagePath;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
    
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setStatus(String status) {
        if (STATUS_AVAILABLE.equals(status)
            || STATUS_OCCUPIED.equals(status)
            || STATUS_MAINTENANCE.equals(status)
            || STATUS_INACTIVE.equals(status)) {
            this.status = status;
        }
    }
}

class ConsultationRoom extends HospitalAsset {
    public ConsultationRoom(
            String assetId,
            String assetName,
            String imagePath,
            String location,
            String description
    ) {
        super(assetId, assetName, imagePath, location, description);
    }
}

class Ward extends HospitalAsset {
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

//class Bed {
//    public String bedId;
//    public boolean available;
//    
//    public Bed(
//            String bedId,
//            boolean available
//    ) {
//        this.bedId = bedId;
//        this.available = available;
//    }
//}

class Laboratory extends HospitalAsset {
    public Laboratory(
            String assetId,
            String assetName,
            String imagePath,
            String location,
            String description
    ) {
        super(assetId, assetName, imagePath, location, description);
    }
}

class XRayRoom extends HospitalAsset {
    public XRayRoom(
            String assetId,
            String assetName,
            String imagePath,
            String location,
            String description
    ) {
        super(assetId, assetName, imagePath, location, description);
    }
}

class ImagingRoom extends HospitalAsset {
    public ImagingRoom(
            String assetId,
            String assetName,
            String imagePath,
            String location,
            String description
    ) {
        super(assetId, assetName, imagePath, location, description);
    }
}


