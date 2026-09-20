/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.PrescriptionType;
import oodj_hospital_management_system.models.PrescriptionType.PrescriptionTypeStatus;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author sheaw
 */
public class PrescriptionTypeManagement {

    private static final FileHandler prescriptionTypeHandler = new FileHandler(SystemConfiguration.prescriptionTypesFilePath);

    private static final List<PrescriptionType> prescriptionTypes = new ArrayList<>();

    private static boolean loaded = false;
    private static String loadedFingerprint = null;
    private static int loadVersion = 0;

    public PrescriptionTypeManagement() {
        ensureLoaded();
    }

    private static void ensureLoaded() {
        String current = prescriptionTypeHandler.fingerprint();
        if (!loaded || current == null || !current.equals(loadedFingerprint)) {
            loadPrescriptionTypes();
        }
    }

    public static int getLoadVersion() {
        ensureLoaded();
        return loadVersion;
    }

    public static void loadPrescriptionTypes() {
        String fingerprint = prescriptionTypeHandler.fingerprint();
        prescriptionTypes.clear();
        List<LinkedHashMap<String, String>> rawData = prescriptionTypeHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            PrescriptionType prescriptionType = new PrescriptionType(
                    row.get("prescriptionTypeId"),
                    row.get("typeName"),
                    Double.parseDouble(row.get("baseRate")),
                    getPrescriptionTypeStatus(row.get("status"))
            );
            prescriptionTypes.add(prescriptionType);
        }
        loaded = true;
        loadedFingerprint = fingerprint;
        loadVersion++;
    }

    public static List<PrescriptionType> getAllPrescriptionTypes() {
        ensureLoaded();
        return prescriptionTypes;
    }

    public static List<PrescriptionType> getActivePrescriptionTypes() {
        ensureLoaded();
        List<PrescriptionType> active = new ArrayList<>();
        for (PrescriptionType prescriptionType : prescriptionTypes) {
            if (prescriptionType.getStatus() == PrescriptionTypeStatus.ACTIVE) {
                active.add(prescriptionType);
            }
        }
        return active;
    }

    public static PrescriptionType getPrescriptionTypeById(String prescriptionTypeId) {
        ensureLoaded();
        return findPrescriptionTypeById(prescriptionTypeId);
    }

    public static PrescriptionType findPrescriptionTypeById(String prescriptionTypeId) {
        try {
            if (prescriptionTypeId == null || prescriptionTypeId.isEmpty()) {
                throw new Exception("Prescription Type ID cannot be null or empty");
            }
            for (PrescriptionType prescriptionType : prescriptionTypes) {
                if (prescriptionTypeId.equals(prescriptionType.getPrescriptionTypeId())) {
                    return prescriptionType;
                }
            }
            throw new Exception(String.format("Invalid Prescription Type ID: Prescription Type ID '%s' is not found", prescriptionTypeId));
        } catch (Exception e) {
            System.out.println("Error finding prescription type -> " + e.getMessage());
            Logger.errorLog("Error finding prescription type -> " + e.getMessage());
            return null;
        }
    }

    public static PrescriptionType getPrescriptionTypeByName(String typeName) {
        ensureLoaded();
        return findPrescriptionTypeByName(typeName);
    }

    public static PrescriptionType findPrescriptionTypeByName(String typeName) {
        try {
            if (typeName == null || typeName.isEmpty()) {
                throw new Exception("Prescription Type name cannot be null or empty");
            }
            for (PrescriptionType prescriptionType : prescriptionTypes) {
                if (typeName.equalsIgnoreCase(prescriptionType.getTypeName())) {
                    return prescriptionType;
                }
            }
            throw new Exception(String.format("Invalid Prescription Type Name: '%s' is not found", typeName));
        } catch (Exception e) {
            System.out.println("Error finding prescription type -> " + e.getMessage());
            Logger.errorLog("Error finding prescription type -> " + e.getMessage());
            return null;
        }
    }

    public static PrescriptionTypeStatus getPrescriptionTypeStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        switch (status.toUpperCase()) {
            case "ACTIVE":
                return PrescriptionTypeStatus.ACTIVE;
            case "INACTIVE":
                return PrescriptionTypeStatus.INACTIVE;
            default:
                System.out.println("Unknown Prescription Type Status: " + status);
                return null;
        }
    }

    // File Handler (Write and Append)
    private static boolean writePrescriptionTypes() {
        ArrayList<LinkedHashMap<String, String>> prescriptionTypeData = new ArrayList<>();

        for (PrescriptionType prescriptionType : prescriptionTypes) {
            prescriptionTypeData.add(prescriptionType.getPrescriptionTypeMap());
        }

        prescriptionTypeHandler.writeFile(prescriptionTypeData);
        return true;
    }

    private static boolean appendPrescriptionType(PrescriptionType prescriptionType) {
        try {
            ArrayList<LinkedHashMap<String, String>> prescriptionTypeData = new ArrayList<>();
            prescriptionTypeData.add(prescriptionType.getPrescriptionTypeMap());

            if (!prescriptionTypeHandler.checkFileExistence() && !prescriptionTypeHandler.createFile()) {
                System.out.printf("File 'prescriptiontypes.txt' failed to be created, prescription type '%s' cannot be appended%n", prescriptionType.getPrescriptionTypeId());
                return false;
            }

            prescriptionTypeHandler.appendFile(prescriptionTypeData);

            System.out.printf("Prescription type '%s' is appended to prescriptiontypes.txt!%n", prescriptionType.getPrescriptionTypeId());

            return true;

        } catch (Exception e) {
            System.out.println("Error appending new prescription type: " + e.getMessage());
            Logger.errorLog("Error appending new prescription type (System Error) -> " + e.getMessage());
            return false;
        }
    }

    public LinkedHashMap<String, String> createPrescriptionType(String typeName, double baseRate) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        if (typeName == null || typeName.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Prescription type is not created\nType name cannot be empty.");
            return msgMap;
        }

        if (baseRate < 0) {
            msgMap.put("success", null);
            msgMap.put("msg", "Prescription type is not created\nBase rate cannot be negative.");
            return msgMap;
        }

        String newPrescriptionTypeId = generatePrescriptionTypeId();

        PrescriptionType newPrescriptionType = new PrescriptionType(
                newPrescriptionTypeId,
                typeName,
                baseRate,
                PrescriptionTypeStatus.ACTIVE
        );

        prescriptionTypes.add(newPrescriptionType);

        if (prescriptionTypeHandler.checkFileExistence()) {
            appendPrescriptionType(newPrescriptionType);
        } else {
            writePrescriptionTypes();
        }

        loadPrescriptionTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription type '%s' is created and stored!", newPrescriptionTypeId));
        msgMap.put("newPrescriptionTypeId", newPrescriptionTypeId);

        return msgMap;
    }

    public LinkedHashMap<String, String> updatePrescriptionType(PrescriptionType prescriptionType, String typeName, double baseRate) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        // Validate on the passed-in object first, then re-fetch via ID before mutating
        // (loadPrescriptionTypes() inside getPrescriptionTypeById reloads the list,
        // orphaning any reference fetched before this call)
        if (prescriptionType == null || findPrescriptionTypeById(prescriptionType.getPrescriptionTypeId()) == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Prescription type is not updated\nPrescription type not found.");
            return msgMap;
        }

        if (typeName == null || typeName.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Prescription type is not updated\nType name cannot be empty.");
            return msgMap;
        }

        if (baseRate < 0) {
            msgMap.put("success", null);
            msgMap.put("msg", "Prescription type is not updated\nBase rate cannot be negative.");
            return msgMap;
        }

        PrescriptionType targetPrescriptionType = findPrescriptionTypeById(prescriptionType.getPrescriptionTypeId());
        targetPrescriptionType.setTypeName(typeName);
        targetPrescriptionType.setBaseRate(baseRate);

        writePrescriptionTypes();
        loadPrescriptionTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription type '%s' is updated!", targetPrescriptionType.getPrescriptionTypeId()));

        return msgMap;
    }

    public LinkedHashMap<String, String> deactivatePrescriptionType(String prescriptionTypeId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        PrescriptionType targetPrescriptionType = findPrescriptionTypeById(prescriptionTypeId);

        if (targetPrescriptionType == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Prescription type '%s' not found", prescriptionTypeId));
            return msgMap;
        }

        try {
            targetPrescriptionType.deactivate();
        } catch (IllegalStateException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Prescription type is not deactivated\n" + e.getMessage());
            return msgMap;
        }

        writePrescriptionTypes();
        loadPrescriptionTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription type '%s' is deactivated!", prescriptionTypeId));

        return msgMap;
    }

    public LinkedHashMap<String, String> activatePrescriptionType(String prescriptionTypeId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        PrescriptionType targetPrescriptionType = findPrescriptionTypeById(prescriptionTypeId);

        if (targetPrescriptionType == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Prescription type '%s' not found", prescriptionTypeId));
            return msgMap;
        }

        try {
            targetPrescriptionType.activate();
        } catch (IllegalStateException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Prescription type is not activated\n" + e.getMessage());
            return msgMap;
        }

        writePrescriptionTypes();
        loadPrescriptionTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription type '%s' is activated!", prescriptionTypeId));

        return msgMap;
    }

    public static String generatePrescriptionTypeId() {
        ensureLoaded();
        List<String> existingIds = prescriptionTypes.stream().map(PrescriptionType::getPrescriptionTypeId).toList();
        return SystemConfiguration.generateId("#PTYPE", existingIds);
    }
}