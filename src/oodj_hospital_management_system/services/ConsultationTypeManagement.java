/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.ConsultationType.ConsultationTypeStatus;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author sheaw
 */
public class ConsultationTypeManagement {

    private static final FileHandler consultationTypeHandler = new FileHandler(SystemConfiguration.consultationTypesFilePath);

    private static final List<ConsultationType> consultationTypes = new ArrayList<>();

    private static boolean loaded = false;
    private static String loadedFingerprint = null;
    private static int loadVersion = 0;

    public ConsultationTypeManagement() {
        ensureLoaded();
    }

    private static void ensureLoaded() {
        String current = consultationTypeHandler.fingerprint();
        if (!loaded || current == null || !current.equals(loadedFingerprint)) {
            loadConsultationTypes();
        }
    }

    public static int getLoadVersion() {
        ensureLoaded();
        return loadVersion;
    }

    public static void loadConsultationTypes() {
        String fingerprint = consultationTypeHandler.fingerprint();
        consultationTypes.clear();
        List<LinkedHashMap<String, String>> rawData = consultationTypeHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            ConsultationType consultationType = new ConsultationType(
                    row.get("consultationTypeId"),
                    row.get("typeName"),
                    Double.parseDouble(row.get("baseRate")),
                    getConsultationTypeStatus(row.get("status"))
            );
            consultationTypes.add(consultationType);
        }
        loaded = true;
        loadedFingerprint = fingerprint;
        loadVersion++;
    }

    public static List<ConsultationType> getAllConsultationTypes() {
        ensureLoaded();
        return consultationTypes;
    }

    public static List<ConsultationType> getActiveConsultationTypes() {
        ensureLoaded();
        List<ConsultationType> active = new ArrayList<>();
        for (ConsultationType consultationType : consultationTypes) {
            if (consultationType.getStatus() == ConsultationTypeStatus.ACTIVE) {
                active.add(consultationType);
            }
        }
        return active;
    }

    public static ConsultationType getConsultationTypeById(String consultationTypeId) {
        ensureLoaded();
        return findConsultationTypeById(consultationTypeId);
    }

    public static ConsultationType findConsultationTypeById(String consultationTypeId) {
        try {
            if (consultationTypeId == null || consultationTypeId.isEmpty()) {
                throw new Exception("Consultation Type ID cannot be null or empty");
            }
            for (ConsultationType consultationType : consultationTypes) {
                if (consultationTypeId.equals(consultationType.getConsultationTypeId())) {
                    return consultationType;
                }
            }
            throw new Exception(String.format("Invalid Consultation Type ID: Consultation Type ID '%s' is not found", consultationTypeId));
        } catch (Exception e) {
            System.out.println("Error finding consultation type -> " + e.getMessage());
            Logger.errorLog("Error finding consultation type -> " + e.getMessage());
            return null;
        }
    }

    public static ConsultationTypeStatus getConsultationTypeStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        switch (status.toUpperCase()) {
            case "ACTIVE":
                return ConsultationTypeStatus.ACTIVE;
            case "INACTIVE":
                return ConsultationTypeStatus.INACTIVE;
            default:
                System.out.println("Unknown Consultation Type Status: " + status);
                return null;
        }
    }

    // File Handler (Write and Append)
    private static boolean writeConsultationTypes() {
        ArrayList<LinkedHashMap<String, String>> consultationTypeData = new ArrayList<>();

        for (ConsultationType consultationType : consultationTypes) {
            consultationTypeData.add(consultationType.getConsultationTypeMap());
        }

        consultationTypeHandler.writeFile(consultationTypeData);
        return true;
    }

    private static boolean appendConsultationType(ConsultationType consultationType) {
        try {
            ArrayList<LinkedHashMap<String, String>> consultationTypeData = new ArrayList<>();
            consultationTypeData.add(consultationType.getConsultationTypeMap());

            if (!consultationTypeHandler.checkFileExistence() && !consultationTypeHandler.createFile()) {
                System.out.printf("File 'consultationtypes.txt' failed to be created, consultation type '%s' cannot be appended%n", consultationType.getConsultationTypeId());
                return false;
            }

            consultationTypeHandler.appendFile(consultationTypeData);

            System.out.printf("Consultation type '%s' is appended to consultationtypes.txt!%n", consultationType.getConsultationTypeId());

            return true;

        } catch (Exception e) {
            System.out.println("Error appending new consultation type: " + e.getMessage());
            Logger.errorLog("Error appending new consultation type (System Error) -> " + e.getMessage());
            return false;
        }
    }

    public LinkedHashMap<String, String> createConsultationType(String typeName, double baseRate) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        if (typeName == null || typeName.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation type is not created\nType name cannot be empty.");
            return msgMap;
        }

        if (baseRate < 0) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation type is not created\nBase rate cannot be negative.");
            return msgMap;
        }

        String newConsultationTypeId = generateConsultationTypeId();

        ConsultationType newConsultationType = new ConsultationType(
                newConsultationTypeId,
                typeName,
                baseRate,
                ConsultationTypeStatus.ACTIVE
        );

        consultationTypes.add(newConsultationType);

        if (consultationTypeHandler.checkFileExistence()) {
            appendConsultationType(newConsultationType);
        } else {
            writeConsultationTypes();
        }

        loadConsultationTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation type '%s' is created and stored!", newConsultationTypeId));
        msgMap.put("newConsultationTypeId", newConsultationTypeId);

        return msgMap;
    }

    public LinkedHashMap<String, String> updateConsultationType(ConsultationType consultationType, String typeName, double baseRate) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        // Validate on the passed-in object first, then re-fetch via ID before mutating
        // (loadConsultationTypes() inside getConsultationTypeById reloads the list,
        // orphaning any reference fetched before this call)
        if (consultationType == null || findConsultationTypeById(consultationType.getConsultationTypeId()) == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation type is not updated\nConsultation type not found.");
            return msgMap;
        }

        if (typeName == null || typeName.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation type is not updated\nType name cannot be empty.");
            return msgMap;
        }

        if (baseRate < 0) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation type is not updated\nBase rate cannot be negative.");
            return msgMap;
        }

        ConsultationType targetConsultationType = findConsultationTypeById(consultationType.getConsultationTypeId());
        targetConsultationType.setTypeName(typeName);
        targetConsultationType.setBaseRate(baseRate);

        writeConsultationTypes();
        loadConsultationTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation type '%s' is updated!", targetConsultationType.getConsultationTypeId()));

        return msgMap;
    }

    public LinkedHashMap<String, String> deactivateConsultationType(String consultationTypeId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        ConsultationType targetConsultationType = findConsultationTypeById(consultationTypeId);

        if (targetConsultationType == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Consultation type '%s' not found", consultationTypeId));
            return msgMap;
        }

        try {
            targetConsultationType.deactivate();
        } catch (IllegalStateException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation type is not deactivated\n" + e.getMessage());
            return msgMap;
        }

        writeConsultationTypes();
        loadConsultationTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation type '%s' is deactivated!", consultationTypeId));

        return msgMap;
    }

    public LinkedHashMap<String, String> activateConsultationType(String consultationTypeId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        ConsultationType targetConsultationType = findConsultationTypeById(consultationTypeId);

        if (targetConsultationType == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Consultation type '%s' not found", consultationTypeId));
            return msgMap;
        }

        try {
            targetConsultationType.activate();
        } catch (IllegalStateException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation type is not activated\n" + e.getMessage());
            return msgMap;
        }

        writeConsultationTypes();
        loadConsultationTypes();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation type '%s' is activated!", consultationTypeId));

        return msgMap;
    }

    public static String generateConsultationTypeId() {
        ensureLoaded();
        List<String> existingIds = consultationTypes.stream().map(ConsultationType::getConsultationTypeId).toList();
        return SystemConfiguration.generateId("#CTYPE", existingIds);
    }
}