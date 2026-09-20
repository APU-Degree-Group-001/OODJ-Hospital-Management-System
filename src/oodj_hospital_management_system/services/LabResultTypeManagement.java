/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.LabResultType;
import oodj_hospital_management_system.models.LabResultType.LabResultTypeStatus;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author sheaw
 */
public class LabResultTypeManagement {

    private static final FileHandler labResultTypeHandler = new FileHandler(SystemConfiguration.labResultTypesFilePath);

    private static final List<LabResultType> labResultTypes = new ArrayList<>();

    private static boolean loaded = false;
    private static String loadedFingerprint = null;
    private static int loadVersion = 0;

    public LabResultTypeManagement() {
        ensureLoaded();
    }

    private static void ensureLoaded() {
        String current = labResultTypeHandler.fingerprint();
        if (!loaded || current == null || !current.equals(loadedFingerprint)) {
            loadLabResultTypes();
        }
    }

    public static int getLoadVersion() {
        ensureLoaded();
        return loadVersion;
    }

    public static void loadLabResultTypes() {
        String fingerprint = labResultTypeHandler.fingerprint();
        labResultTypes.clear();
        List<LinkedHashMap<String, String>> rawData = labResultTypeHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            LabResultType labResultType = new LabResultType(
                    row.get("labResultTypeId"),
                    row.get("typeName"),
                    Double.parseDouble(row.get("baseRate")),
                    getLabResultTypeStatus(row.get("status"))
            );
            labResultTypes.add(labResultType);
        }
        loaded = true;
        loadedFingerprint = fingerprint;
        loadVersion++;
    }

    public static List<LabResultType> getAllLabResultTypes() {
        ensureLoaded();
        return labResultTypes;
    }

    public static List<LabResultType> getActiveLabResultTypes() {
        ensureLoaded();
        List<LabResultType> active = new ArrayList<>();
        for (LabResultType labResultType : labResultTypes) {
            if (labResultType.getStatus() == LabResultTypeStatus.ACTIVE) {
                active.add(labResultType);
            }
        }
        return active;
    }

    public static LabResultType getLabResultTypeById(String labResultTypeId) {
        ensureLoaded();
        return findLabResultTypeById(labResultTypeId);
    }

    public static LabResultType findLabResultTypeById(String labResultTypeId) {
        try {
            if (labResultTypeId == null || labResultTypeId.isEmpty()) {
                throw new Exception("Lab Result Type ID cannot be null or empty");
            }
            for (LabResultType labResultType : labResultTypes) {
                if (labResultTypeId.equals(labResultType.getLabResultTypeId())) {
                    return labResultType;
                }
            }
            throw new Exception(String.format("Invalid Lab Result Type ID: Lab Result Type ID '%s' is not found", labResultTypeId));
        } catch (Exception e) {
            System.out.println("Error finding lab result type -> " + e.getMessage());
            Logger.errorLog("Error finding lab result type -> " + e.getMessage());
            return null;
        }
    }

    public static LabResultType getLabResultTypeByName(String typeName) {
        ensureLoaded();
        return findLabResultTypeByName(typeName);
    }

    public static LabResultType findLabResultTypeByName(String typeName) {
        try {
            if (typeName == null || typeName.isEmpty()) {
                throw new Exception("Lab Result Type name cannot be null or empty");
            }
            for (LabResultType labResultType : labResultTypes) {
                if (typeName.equalsIgnoreCase(labResultType.getTypeName())) {
                    return labResultType;
                }
            }
            throw new Exception(String.format("Invalid Lab Result Type Name: '%s' is not found", typeName));
        } catch (Exception e) {
            System.out.println("Error finding lab result type -> " + e.getMessage());
            Logger.errorLog("Error finding lab result type -> " + e.getMessage());
            return null;
        }
    }

    public static LabResultTypeStatus getLabResultTypeStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        switch (status.toUpperCase()) {
            case "ACTIVE":
                return LabResultTypeStatus.ACTIVE;
            case "INACTIVE":
                return LabResultTypeStatus.INACTIVE;
            default:
                System.out.println("Unknown Lab Result Type Status: " + status);
                return null;
        }
    }

    // File Handler (Write and Append)
    private static boolean writeLabResultTypes() {
        ArrayList<LinkedHashMap<String, String>> labResultTypeData = new ArrayList<>();

        for (LabResultType labResultType : labResultTypes) {
            labResultTypeData.add(labResultType.getLabResultTypeMap());
        }

        labResultTypeHandler.writeFile(labResultTypeData);
        return true;
    }

    private static boolean appendLabResultType(LabResultType labResultType) {
        try {
            ArrayList<LinkedHashMap<String, String>> labResultTypeData = new ArrayList<>();
            labResultTypeData.add(labResultType.getLabResultTypeMap());

            if (!labResultTypeHandler.checkFileExistence() && !labResultTypeHandler.createFile()) {
                System.out.printf("File 'labresulttypes.txt' failed to be created, lab result type '%s' cannot be appended%n", labResultType.getLabResultTypeId());
                return false;
            }

            labResultTypeHandler.appendFile(labResultTypeData);

            System.out.printf("Lab result type '%s' is appended to labresulttypes.txt!%n", labResultType.getLabResultTypeId());

            return true;

        } catch (Exception e) {
            System.out.println("Error appending new lab result type: " + e.getMessage());
            Logger.errorLog("Error appending new lab result type (System Error) -> " + e.getMessage());
            return false;
        }
    }

    public LinkedHashMap<String, String> createLabResultType(String typeName, double baseRate) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        if (typeName == null || typeName.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Lab result type is not created\nType name cannot be empty.");
            return msgMap;
        }

        if (baseRate < 0) {
            msgMap.put("success", null);
            msgMap.put("msg", "Lab result type is not created\nBase rate cannot be negative.");
            return msgMap;
        }

        String newLabResultTypeId = generateLabResultTypeId();

        LabResultType newLabResultType = new LabResultType(
                newLabResultTypeId,
                typeName,
                baseRate,
                LabResultTypeStatus.ACTIVE
        );

        labResultTypes.add(newLabResultType);

        if (labResultTypeHandler.checkFileExistence()) {
            appendLabResultType(newLabResultType);
        } else {
            writeLabResultTypes();
        }

        loadLabResultTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Lab result type '%s' is created and stored!", newLabResultTypeId));
        msgMap.put("newLabResultTypeId", newLabResultTypeId);

        return msgMap;
    }

    public LinkedHashMap<String, String> updateLabResultType(LabResultType labResultType, String typeName, double baseRate) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        // Validate on the passed-in object first, then re-fetch via ID before mutating
        // (loadLabResultTypes() inside getLabResultTypeById reloads the list,
        // orphaning any reference fetched before this call)
        if (labResultType == null || findLabResultTypeById(labResultType.getLabResultTypeId()) == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Lab result type is not updated\nLab result type not found.");
            return msgMap;
        }

        if (typeName == null || typeName.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Lab result type is not updated\nType name cannot be empty.");
            return msgMap;
        }

        if (baseRate < 0) {
            msgMap.put("success", null);
            msgMap.put("msg", "Lab result type is not updated\nBase rate cannot be negative.");
            return msgMap;
        }

        LabResultType targetLabResultType = findLabResultTypeById(labResultType.getLabResultTypeId());
        targetLabResultType.setTypeName(typeName);
        targetLabResultType.setBaseRate(baseRate);

        writeLabResultTypes();
        loadLabResultTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Lab result type '%s' is updated!", targetLabResultType.getLabResultTypeId()));

        return msgMap;
    }

    public LinkedHashMap<String, String> deactivateLabResultType(String labResultTypeId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        LabResultType targetLabResultType = findLabResultTypeById(labResultTypeId);

        if (targetLabResultType == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Lab result type '%s' not found", labResultTypeId));
            return msgMap;
        }

        try {
            targetLabResultType.deactivate();
        } catch (IllegalStateException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Lab result type is not deactivated\n" + e.getMessage());
            return msgMap;
        }

        writeLabResultTypes();
        loadLabResultTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Lab result type '%s' is deactivated!", labResultTypeId));

        return msgMap;
    }

    public LinkedHashMap<String, String> activateLabResultType(String labResultTypeId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        LabResultType targetLabResultType = findLabResultTypeById(labResultTypeId);

        if (targetLabResultType == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Lab result type '%s' not found", labResultTypeId));
            return msgMap;
        }

        try {
            targetLabResultType.activate();
        } catch (IllegalStateException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Lab result type is not activated\n" + e.getMessage());
            return msgMap;
        }

        writeLabResultTypes();
        loadLabResultTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Lab result type '%s' is activated!", labResultTypeId));

        return msgMap;
    }

    public static String generateLabResultTypeId() {
        ensureLoaded();
        List<String> existingIds = labResultTypes.stream().map(LabResultType::getLabResultTypeId).toList();
        return SystemConfiguration.generateId("#LRTYPE", existingIds);
    }
}