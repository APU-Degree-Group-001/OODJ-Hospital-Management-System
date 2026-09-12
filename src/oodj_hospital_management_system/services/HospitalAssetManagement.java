/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.utils.DateTime;
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
    
    private User activeUser;
    
    public HospitalAssetManagement(User usr) {
        this.activeUser = usr;
        loadHospitalAssets();
        loadHospitalAssetAllocations();
    }

    // =========================================================
    // LOAD
    // =========================================================

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
                        row.get("status"),
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
                        row.get("description"),
                        row.get("status")
                );
                allHospitalAssets.add(asset);
            }
        }
        loadHospitalAssetAllocations(); // ensure allocations are current before recomputing
        boolean changed = recomputeStatuses();
        if (changed) {
            writeHospitalAssets();
        }
    }

    private static boolean recomputeStatuses() {
        boolean changed = false;

        for (HospitalAsset asset : allHospitalAssets) {
            if (HospitalAsset.STATUS_MAINTENANCE.equals(asset.getStatus())) {
                continue; // manual override — leave it as-is
            }
            boolean occupied = allHospitalAssetAllocations.stream()
                .anyMatch(alloc -> alloc.getAssetId().equals(asset.getAssetId())
                    && alloc.getAllocationDateTime() != null && !alloc.getAllocationDateTime().isEmpty()
                    && (alloc.getReleasedDateTime() == null || alloc.getReleasedDateTime().isEmpty()));

            String newStatus = occupied ? HospitalAsset.STATUS_OCCUPIED : HospitalAsset.STATUS_AVAILABLE;

            if (!newStatus.equals(asset.getStatus())) {
                asset.setStatus(newStatus);
                changed = true;
            }
        }

        return changed;
    }
    
    public static void loadHospitalAssetAllocations() {
        allHospitalAssetAllocations.clear();

        List<LinkedHashMap<String, String>> rawData = allocationHandler.readFile();

        for (LinkedHashMap<String, String> row : rawData) {
            // allocationId, doctorId, assetId, scheduledStartDateTime, scheduledEndDateTime, allocatedDateTime, releasedDateTime
            HospitalAssetAllocation allocation = new HospitalAssetAllocation(
                    row.get("allocationId"),
                    row.get("assetId"),
                    row.get("doctorId"),
                    row.get("scheduledStartDateTime"),
                    row.get("scheduledEndDateTime"),
                    row.get("allocatedDateTime"),
                    row.get("releasedDateTime")
            );

            allHospitalAssetAllocations.add(allocation);
        }
    }

    // =========================================================
    // ACCESSORS
    // =========================================================

    public static List<HospitalAssetAllocation> getAllHospitalAssetAllocations() {
        loadHospitalAssetAllocations();
        return new ArrayList<>(allHospitalAssetAllocations);
    }

    public static List<HospitalAsset> getAllHospitalAssets() {
        loadHospitalAssets();
        return new ArrayList<>(allHospitalAssets);
    }

    // =========================================================
    // GET BY ID
    // =========================================================

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
        if (allocationId == null || allocationId.isEmpty()) {
            return null;
        }
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
    
    // =========================================================
    // GET HOSPITAL ASSET ALLOCATION BY ASSET ID
    // =========================================================

    public static List<HospitalAssetAllocation> getHospitalAssetAllocationsByAssetId(String assetId) {
        loadHospitalAssetAllocations();

        return allHospitalAssetAllocations.stream()
                .filter(allocation -> allocation.getAssetId().equals(assetId))
                .collect(Collectors.toList());
    }

    // =========================================================
    // SEARCH
    // =========================================================

    public static List<HospitalAsset> searchHospitalAssets(String keyword) {
        loadHospitalAssets();

        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(allHospitalAssets);
        }

        String searchKeyword = keyword.toLowerCase().trim();

        return allHospitalAssets.stream()
                .filter(asset -> asset.getAssetId().toLowerCase().contains(searchKeyword)
                || asset.getAssetType().toLowerCase().contains(searchKeyword)
                || asset.getAssetName().toLowerCase().contains(searchKeyword)
                || asset.getLocation().toLowerCase().contains(searchKeyword)
                || asset.getDescription().toLowerCase().contains(searchKeyword))
                .collect(Collectors.toList());
    }

    // =========================================================
    // SORT
    // =========================================================

    public static List<HospitalAsset> sortHospitalAssets(String sortBy, boolean ascending) {
        loadHospitalAssets();

        List<HospitalAsset> sortedAssets = new ArrayList<>(allHospitalAssets);

        Comparator<HospitalAsset> comparator;

        switch (sortBy.toLowerCase()) {
            case "assetid":
                comparator = Comparator.comparing(HospitalAsset::getAssetId);
                break;

            case "assettype":
                comparator = Comparator.comparing(HospitalAsset::getAssetType);
                break;

            case "assetname":
                comparator = Comparator.comparing(HospitalAsset::getAssetName);
                break;

            case "location":
                comparator = Comparator.comparing(HospitalAsset::getLocation);
                break;

            default:
                return sortedAssets;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sortedAssets.sort(comparator);

        return sortedAssets;
    }

    // =========================================================
    // CONSULTATION ROOM COUNT
    // =========================================================

    public static int getConsultationRoomCount() {
        return (int) getAllHospitalAssets().stream()
                .filter(asset -> "Consultation Room".equalsIgnoreCase(asset.getAssetType()))
                .count();
    }

    // =========================================================
    // FILE HANDLER - HOSPITAL ASSET
    // =========================================================

    private static boolean writeHospitalAssets() {
        ArrayList<LinkedHashMap<String, String>> assetData = new ArrayList<>();

        for (HospitalAsset asset : allHospitalAssets) {
            assetData.add(asset.getHospitalAssetMap());
        }

        assetHandler.writeFile(assetData);

        return true;
    }

    private static boolean appendHospitalAsset(HospitalAsset asset) {
        try {
            ArrayList<LinkedHashMap<String, String>> assetData = new ArrayList<>();
            assetData.add(asset.getHospitalAssetMap());

            if (!assetHandler.checkFileExistence() && !assetHandler.createFile()) {
                System.out.printf("File 'hospitalAssets.txt' failed to be created, hospital asset '%s' cannot be appended%n", asset.getAssetId());
                return false;
            }

            assetHandler.appendFile(assetData);

            System.out.printf("Hospital asset '%s' is appended to hospitalAssets.txt!%n", asset.getAssetId());

            return true;

        } catch (Exception e) {
            System.out.println("Error appending new hospital asset: " + e.getMessage());
            Logger.errorLog("Error appending new hospital asset (System Error) -> " + e.getMessage());
            return false;
        }
    }
    
    // =========================================================
    // FILE HANDLER - HOSPITAL ASSET ALLOCATION
    // =========================================================

    private static boolean writeHospitalAssetAllocations() {
        ArrayList<LinkedHashMap<String, String>> allocationData = new ArrayList<>();

        for (HospitalAssetAllocation allocation : allHospitalAssetAllocations) {
            allocationData.add(allocation.getHospitalAssetAllocationMap());
        }

        allocationHandler.writeFile(allocationData);

        return true;
    }

    private static boolean appendHospitalAssetAllocation(HospitalAssetAllocation allocation) {
        try {
            ArrayList<LinkedHashMap<String, String>> allocationData = new ArrayList<>();
            allocationData.add(allocation.getHospitalAssetAllocationMap());

            if (!allocationHandler.checkFileExistence() && !allocationHandler.createFile()) {
                System.out.printf("File 'hospitalAssetAllocations.txt' failed to be created, allocation '%s' cannot be appended%n", allocation.getAllocationId());
                return false;
            }

            allocationHandler.appendFile(allocationData);

            System.out.printf("Hospital asset allocation '%s' is appended to hospitalAssetAllocations.txt!%n", allocation.getAllocationId());

            return true;

        } catch (Exception e) {
            System.out.println("Error appending new hospital asset allocation: " + e.getMessage());
            Logger.errorLog("Error appending new hospital asset allocation (System Error) -> " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // CREATE HOSPITAL ASSET
    // =========================================================

    public LinkedHashMap<String, String> createHospitalAsset(String assetType, String assetName, String imagePath, String location, String description) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        String newAssetId = generateHospitalAssetId();

        HospitalAsset newAsset = new HospitalAsset(
                newAssetId,
                assetType,
                assetName,
                imagePath,
                location,
                description,
                HospitalAsset.STATUS_AVAILABLE
        );

        allHospitalAssets.add(newAsset);

        if (assetHandler.checkFileExistence()) {
            appendHospitalAsset(newAsset);
        } else {
            writeHospitalAssets();
        }

        loadHospitalAssets();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset '%s' is created and stored!", newAssetId));
        msgMap.put("newAssetId", newAssetId);

        return msgMap;
    }

    // =========================================================
    // CREATE WARD
    // =========================================================

    public LinkedHashMap<String, String> createWard(String assetType, String assetName, String imagePath, String location, String description, int totalBeds) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        String newAssetId = generateHospitalAssetId();

        Ward newWard = new Ward(
                newAssetId,
                assetType,
                assetName,
                imagePath,
                location,
                description,
                HospitalAsset.STATUS_AVAILABLE,
                totalBeds
        );

        allHospitalAssets.add(newWard);

        if (assetHandler.checkFileExistence()) {
            appendHospitalAsset(newWard);
        } else {
            writeHospitalAssets();
        }

        loadHospitalAssets();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Ward '%s' is created and stored!", newAssetId));
        msgMap.put("newAssetId", newAssetId);

        return msgMap;
    }

    // =========================================================
    // MODIFY HOSPITAL ASSET
    // =========================================================

    public LinkedHashMap<String, String> modifyHospitalAsset(HospitalAsset asset, String assetType, String assetName, String imagePath, String location, String description) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        if (asset == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Hospital asset is not found");
            return msgMap;
        }

        HospitalAsset targetAsset = getHospitalAssetById(asset.getAssetId());
        if (targetAsset == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Hospital asset is not found");
            return msgMap;
        }

        targetAsset.setAssetType(assetType);
        targetAsset.setAssetName(assetName);
        targetAsset.setImagePath(imagePath);
        targetAsset.setLocation(location);
        targetAsset.setDescription(description);

        writeHospitalAssets();
        loadHospitalAssets();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset '%s' is modified!", targetAsset.getAssetId()));

        return msgMap;
    }

    // =========================================================
    // MODIFY WARD
    // =========================================================

    public LinkedHashMap<String, String> modifyWard(Ward ward, String assetType, String assetName, String imagePath, String location, String description, int totalBeds) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        if (ward == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Ward is not found");
            return msgMap;
        }

        HospitalAsset found = getHospitalAssetById(ward.getAssetId());
        if (!(found instanceof Ward)) {
            msgMap.put("success", null);
            msgMap.put("msg", "Ward is not found");
            return msgMap;
        }
        Ward targetWard = (Ward) found;

        targetWard.setAssetType(assetType);
        targetWard.setAssetName(assetName);
        targetWard.setImagePath(imagePath);
        targetWard.setLocation(location);
        targetWard.setDescription(description);
        targetWard.setTotalBeds(totalBeds);

        writeHospitalAssets();
        loadHospitalAssets();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Ward '%s' is modified!", targetWard.getAssetId()));

        return msgMap;
    }

    // =========================================================
    // DELETE HOSPITAL ASSET
    // =========================================================

    public LinkedHashMap<String, String> deleteHospitalAsset(String assetId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        HospitalAsset deleteAsset = getHospitalAssetById(assetId);

        if (deleteAsset == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset '%s' not found", assetId));
            return msgMap;
        }

        allHospitalAssets.removeIf(asset -> asset.getAssetId().equals(assetId));

        writeHospitalAssets();
        loadHospitalAssets();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset '%s' is deleted!", assetId));
        msgMap.put("deleteAssetId", assetId);

        return msgMap;
    }

    // =========================================================
    // CREATE HOSPITAL ASSET ALLOCATION
    // =========================================================

    /**
     * Creates a new allocation.
     * scheduledStartDateTime / scheduledEndDateTime are optional (pass "" or null if not planning ahead).
     * allocatedDateTime (actual start) defaults to "now" if not supplied.
     */
    public LinkedHashMap<String, String> createHospitalAssetAllocation(
        String assetId,
        String doctorId,
        String scheduledStartDateTime,
        String scheduledEndDateTime,
        String allocatedDateTime
    ) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        loadHospitalAssetAllocations();

        if (assetId == null || assetId.isBlank() || getHospitalAssetById(assetId) == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset allocation is not created\nInvalid asset ID: '%s' is not found", assetId));
            return msgMap;
        }

        if (doctorId == null || doctorId.isBlank()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Hospital asset allocation is not created\nDoctor ID is required");
            return msgMap;
        }

        boolean hasSchedule = scheduledStartDateTime != null && !scheduledStartDateTime.isBlank()
                && scheduledEndDateTime != null && !scheduledEndDateTime.isBlank();

        if (hasSchedule) {
            java.time.LocalDateTime newStart;
            java.time.LocalDateTime newEnd;
            try {
                newStart = DateTime.toDateTimeObject(scheduledStartDateTime);
                newEnd = DateTime.toDateTimeObject(scheduledEndDateTime);
            } catch (Exception e) {
                msgMap.put("success", null);
                msgMap.put("msg", "Hospital asset allocation is not created\nInvalid scheduled date/time format");
                return msgMap;
            }

            if (!newEnd.isAfter(newStart)) {
                msgMap.put("success", null);
                msgMap.put("msg", "Hospital asset allocation is not created\nScheduled end must be after scheduled start");
                return msgMap;
            }

            if (newStart.isBefore(DateTime.currentDateTime())) {
                msgMap.put("success", null);
                msgMap.put("msg", "Hospital asset allocation is not created\nScheduled start cannot be in the past");
                return msgMap;
            }

            // Overlap check: same asset, overlapping scheduled window
            boolean hasOverlap = allHospitalAssetAllocations.stream()
                    .filter(a -> a.getAssetId().equals(assetId))
                    .filter(a -> a.getScheduledStartDateTime() != null && !a.getScheduledStartDateTime().isEmpty()
                            && a.getScheduledEndDateTime() != null && !a.getScheduledEndDateTime().isEmpty())
                    .anyMatch(a -> {
                        java.time.LocalDateTime existingStart = DateTime.toDateTimeObject(a.getScheduledStartDateTime());
                        java.time.LocalDateTime existingEnd = DateTime.toDateTimeObject(a.getScheduledEndDateTime());
                        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
                    });

            if (hasOverlap) {
                msgMap.put("success", null);
                msgMap.put("msg", String.format(
                        "Hospital asset allocation is not created\nAsset '%s' already has an overlapping scheduled allocation",
                        assetId));
                return msgMap;
            }
        }

        String newAllocationId = generateHospitalAssetAllocationId();

        HospitalAssetAllocation newAllocation = new HospitalAssetAllocation(
                newAllocationId,
                assetId,
                doctorId,
                scheduledStartDateTime != null ? scheduledStartDateTime : "",
                scheduledEndDateTime != null ? scheduledEndDateTime : "",
                (allocatedDateTime == null || allocatedDateTime.isBlank())
                        ? DateTime.formattedLocalDateTime(DateTime.currentDateTime())
                        : allocatedDateTime,
                ""
        );

        allHospitalAssetAllocations.add(newAllocation);

        if (allocationHandler.checkFileExistence()) {
            appendHospitalAssetAllocation(newAllocation);
        } else {
            writeHospitalAssetAllocations();
        }

        loadHospitalAssets();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset allocation '%s' is created and stored!", newAllocationId));
        msgMap.put("newAllocationId", newAllocationId);

        return msgMap;
    }

    // =========================================================
    // MODIFY SCHEDULED START / END
    // =========================================================

    public LinkedHashMap<String, String> setHospitalAssetAllocationSchedule(String allocationId, String scheduledStartDateTime, String scheduledEndDateTime) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        HospitalAssetAllocation allocation = getHospitalAssetAllocationById(allocationId);

        if (allocation == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset allocation '%s' is not found", allocationId));
            return msgMap;
        }

        allocation.setScheduledStartDateTime(scheduledStartDateTime != null ? scheduledStartDateTime : "");
        allocation.setScheduledEndDateTime(scheduledEndDateTime != null ? scheduledEndDateTime : "");

        writeHospitalAssetAllocations();
        loadHospitalAssetAllocations();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset allocation '%s' schedule is updated!", allocationId));

        return msgMap;
    }

    // =========================================================
    // START (ACTUAL) HOSPITAL ASSET ALLOCATION
    // =========================================================

    /**
     * Marks the actual start of an allocation (allocatedDateTime), separate from when the record was created.
     * Useful if an allocation was created ahead of time (scheduled) and the asset is only actually taken now.
     */
    public LinkedHashMap<String, String> startHospitalAssetAllocation(String allocationId, String allocatedDateTime) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        HospitalAssetAllocation allocation = getHospitalAssetAllocationById(allocationId);

        if (allocation == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset allocation '%s' is not found", allocationId));
            return msgMap;
        }

        if (!allocation.getReleasedDateTime().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset allocation '%s' is already released", allocationId));
            return msgMap;
        }

        allocation.setAllocationDateTime(
                (allocatedDateTime == null || allocatedDateTime.isBlank())
                        ? DateTime.formattedLocalDateTime(DateTime.currentDateTime())
                        : allocatedDateTime
        );

        writeHospitalAssetAllocations();
        loadHospitalAssets(); // reloads allocations internally, recomputes asset status, writes txt if it changed

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset allocation '%s' actual start is updated!", allocationId));

        return msgMap;
    }

    // =========================================================
    // RELEASE (ACTUAL END) HOSPITAL ASSET ALLOCATION
    // =========================================================

    public LinkedHashMap<String, String> releaseHospitalAssetAllocation(String allocationId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        HospitalAssetAllocation allocation = getHospitalAssetAllocationById(allocationId);

        if (allocation == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset allocation '%s' is not found", allocationId));
            return msgMap;
        }

        if (!allocation.getReleasedDateTime().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset allocation '%s' is already released", allocationId));
            return msgMap;
        }

        allocation.setReleasedDateTime(DateTime.formattedLocalDateTime(DateTime.currentDateTime()));

        writeHospitalAssetAllocations();
        loadHospitalAssets(); // reloads allocations internally, recomputes asset status, writes txt if it changed

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset allocation '%s' is released!", allocationId));

        return msgMap;
    }
    
    // =========================================================
    // SET HOSPITAL ASSET MAINTENANCE STATUS
    // =========================================================

    public LinkedHashMap<String, String> setHospitalAssetMaintenance(String assetId, boolean maintenance) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        HospitalAsset asset = getHospitalAssetById(assetId);

        if (asset == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset '%s' not found", assetId));
            return msgMap;
        }

        if (maintenance) {
            boolean hasActiveAllocation = getHospitalAssetAllocationsByAssetId(assetId).stream()
                    .anyMatch(a -> a.getAllocationDateTime() != null && !a.getAllocationDateTime().isEmpty()
                            && (a.getReleasedDateTime() == null || a.getReleasedDateTime().isEmpty()));

            if (hasActiveAllocation) {
                msgMap.put("success", null);
                msgMap.put("msg", String.format("Hospital asset '%s' has an active allocation and cannot be set to maintenance", assetId));
                return msgMap;
            }
        }

        if (maintenance && HospitalAsset.STATUS_MAINTENANCE.equals(asset.getStatus())) {
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("Hospital asset '%s' is already under maintenance.", assetId));
            return msgMap;
        }

        asset.setStatus(maintenance ? HospitalAsset.STATUS_MAINTENANCE : HospitalAsset.STATUS_AVAILABLE);

        writeHospitalAssets();
        loadHospitalAssets(); // will recompute Available/Occupied correctly if maintenance was turned off

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset '%s' is now %s.", assetId, maintenance ? "under maintenance" : "available"));

        return msgMap;
    }
    
//    FOR ADMIN STAFF ONLY
    public LinkedHashMap<String, String> deleteHospitalAssetAllocation(String allocationId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        HospitalAssetAllocation deleteAllocation = getHospitalAssetAllocationById(allocationId);

        if (deleteAllocation == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Hospital asset allocation '%s' not found", allocationId));
            return msgMap;
        }

        allHospitalAssetAllocations.removeIf(allocation -> allocation.getAllocationId().equals(allocationId));

        writeHospitalAssetAllocations();
        loadHospitalAssetAllocations();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Hospital asset allocation '%s' is deleted!", allocationId));
        msgMap.put("deleteAllocationId", allocationId);

        return msgMap;
    }

    // =========================================================
    // GENERATE ID
    // =========================================================

    public static String generateHospitalAssetId() {
        List<String> existingIds = allHospitalAssets.stream()
                .map(HospitalAsset::getAssetId)
                .toList();

        return SystemConfiguration.generateId("#AST", existingIds);
    }

    public static String generateHospitalAssetAllocationId() {
        List<String> existingIds = allHospitalAssetAllocations.stream()
                .map(HospitalAssetAllocation::getAllocationId)
                .toList();

        return SystemConfiguration.generateId("#ALLOC", existingIds);
    }
}