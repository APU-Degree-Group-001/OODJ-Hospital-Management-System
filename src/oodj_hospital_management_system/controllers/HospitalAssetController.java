/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.services.HospitalAssetManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author paure
 */
public class HospitalAssetController {
    private HospitalAssetManagement hospitalAssetServices;
    private User activeUser;

    public HospitalAssetController(User activeUser) {
        this.activeUser = activeUser;
        this.hospitalAssetServices = new HospitalAssetManagement(activeUser);
    }
    
    // Get Assets, Allocations
    public List<HospitalAsset> getAllHospitalAssets() {
        return HospitalAssetManagement.getAllHospitalAssets();
    }
 
    public List<HospitalAssetAllocation> getAllHospitalAssetAllocations() {
        return HospitalAssetManagement.getAllHospitalAssetAllocations();
    }
 
    // Lookup by ID
    public HospitalAsset getHospitalAssetById(String assetId) {
        return HospitalAssetManagement.getHospitalAssetById(assetId);
    }
 
    public HospitalAssetAllocation getHospitalAssetAllocationById(String allocationId) {
        return HospitalAssetManagement.getHospitalAssetAllocationById(allocationId);
    }
    
    public List<HospitalAssetAllocation> getHospitalAssetAllocationsByAssetId(String assetId) {
        return HospitalAssetManagement.getHospitalAssetAllocationsByAssetId(assetId);
    }
 
    // Search and Sort
    public List<HospitalAsset> searchHospitalAssets(String keyword) {
        return HospitalAssetManagement.searchHospitalAssets(keyword);
    }
 
    public List<HospitalAsset> sortHospitalAssets(String sortBy, boolean ascending) {
        return HospitalAssetManagement.sortHospitalAssets(sortBy, ascending);
    }
 
    // Consultation Room Count
    public int getConsultationRoomCount() {
        return HospitalAssetManagement.getConsultationRoomCount();
    }
 
    // CRUD
    public LinkedHashMap<String, String> createHospitalAsset(String assetType, String assetName, String imagePath, String location, String description) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.createHospitalAsset(assetType, assetName, imagePath, location, description);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Hospital Asset {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newAssetId"), assetName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create Hospital Asset {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assetName, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> createWard(String assetType, String assetName, String imagePath, String location, String description, int totalBeds) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.createWard(assetType, assetName, imagePath, location, description, totalBeds);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Ward {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newAssetId"), assetName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create Ward {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assetName, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyHospitalAsset(HospitalAsset asset, String assetType, String assetName, String imagePath, String location, String description) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.modifyHospitalAsset(asset, assetType, assetName, imagePath, location, description);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified Hospital Asset {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    asset.getAssetId(), assetName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify Hospital Asset {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    asset != null ? asset.getAssetId() : "?", msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyWard(Ward ward, String assetType, String assetName, String imagePath, String location, String description, int totalBeds) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.modifyWard(ward, assetType, assetName, imagePath, location, description, totalBeds);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified Ward {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    ward.getAssetId(), assetName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify Ward {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    ward != null ? ward.getAssetId() : "?", msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> deleteHospitalAsset(String assetId) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.deleteHospitalAsset(assetId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} deleted Hospital Asset {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("deleteAssetId")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to delete Hospital Asset {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assetId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> createHospitalAssetAllocation(String assetId, String doctorId, String scheduledStartDateTime, String scheduledEndDateTime, String allocatedDateTime) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.createHospitalAssetAllocation(assetId, doctorId, scheduledStartDateTime, scheduledEndDateTime, allocatedDateTime);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Hospital Asset Allocation {%s} for Asset {%s} for Doctor {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newAllocationId"), assetId, doctorId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create Hospital Asset Allocation for Asset {%s} for Doctor {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    assetId, doctorId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> setHospitalAssetAllocationSchedule(String allocationId, String scheduledStartDateTime, String scheduledEndDateTime) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.setHospitalAssetAllocationSchedule(allocationId, scheduledStartDateTime, scheduledEndDateTime);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated schedule for Hospital Asset Allocation {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), allocationId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update schedule for Hospital Asset Allocation {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), allocationId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> startHospitalAssetAllocation(String allocationId, String allocatedDateTime) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.startHospitalAssetAllocation(allocationId, allocatedDateTime);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} started Hospital Asset Allocation {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), allocationId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to start Hospital Asset Allocation {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), allocationId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> releaseHospitalAssetAllocation(String allocationId) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.releaseHospitalAssetAllocation(allocationId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} released Hospital Asset Allocation {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), allocationId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to release Hospital Asset Allocation {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), allocationId, msg.get("msg")));
        }
        return msg;
    }
 
    // For Admin Staff only
    public LinkedHashMap<String, String> deleteHospitalAssetAllocation(String allocationId) {
        LinkedHashMap<String, String> msg = hospitalAssetServices.deleteHospitalAssetAllocation(allocationId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} deleted Hospital Asset Allocation {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("deleteAllocationId")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to delete Hospital Asset Allocation {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), allocationId, msg.get("msg")));
        }
        return msg;
    }
}
