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
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.models.WardAdmission;
import oodj_hospital_management_system.models.WardAdmission.AdmissionStatus;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author sheaw
 */
public class WardAdmissionManagement {

    private static final FileHandler admissionHandler = new FileHandler(SystemConfiguration.wardAdmissionFilePath);

    private static final List<WardAdmission> allWardAdmissions = new ArrayList<>();

    private User activeUser;

    public WardAdmissionManagement(User usr) {
        this.activeUser = usr;
        loadWardAdmissions();
    }

    // =========================================================
    // LOAD
    // =========================================================

    public static void loadWardAdmissions() {
        allWardAdmissions.clear();

        List<LinkedHashMap<String, String>> rawData = admissionHandler.readFile();

        for (LinkedHashMap<String, String> row : rawData) {
            // admissionId, patientId, wardId, admittingDoctorId, admittedDateTime, dischargedDateTime, status, reason
            WardAdmission admission = new WardAdmission(
                    row.get("admissionId"),
                    row.get("patientId"),
                    row.get("wardId"),
                    row.get("admittingDoctorId"),
                    row.get("admittedDateTime"),
                    row.get("dischargedDateTime"),
                    AdmissionStatus.valueOf(row.get("status")),
                    row.get("reason")
            );
            allWardAdmissions.add(admission);
        }
    }

    // =========================================================
    // ACCESSORS
    // =========================================================

    public static List<WardAdmission> getAllWardAdmissions() {
        loadWardAdmissions();
        return allWardAdmissions;
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    public static WardAdmission getWardAdmissionById(String admissionId) {
        loadWardAdmissions();

        try {
            if (!allWardAdmissions.stream().anyMatch(a -> a.getAdmissionId().equals(admissionId))) {
                throw new Exception(String.format("Invalid Ward Admission ID: Admission ID '%s' is not found", admissionId));
            }

            for (WardAdmission admission : allWardAdmissions) {
                if (admission.getAdmissionId().equals(admissionId)) {
                    return admission;
                }
            }

            return null;

        } catch (Exception e) {
            System.out.println("Error finding ward admission -> " + e.getMessage());
            Logger.errorLog("Error finding ward admission -> " + e.getMessage());
            return null;
        }
    }

    // =========================================================
    // SEARCH
    // =========================================================

    public static List<WardAdmission> searchWardAdmissions(String keyword) {
        loadWardAdmissions();

        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(allWardAdmissions);
        }

        String searchKeyword = keyword.toLowerCase().trim();

        return allWardAdmissions.stream()
                .filter(a -> a.getAdmissionId().toLowerCase().contains(searchKeyword)
                || a.getPatientId().toLowerCase().contains(searchKeyword)
                || a.getWardId().toLowerCase().contains(searchKeyword)
                || a.getAdmittingDoctorId().toLowerCase().contains(searchKeyword)
                || a.getStatus().toString().toLowerCase().contains(searchKeyword)
                || (a.getReason() != null && a.getReason().toLowerCase().contains(searchKeyword)))
                .collect(Collectors.toList());
    }

    // =========================================================
    // SORT
    // =========================================================

    public static List<WardAdmission> sortWardAdmissions(String sortBy, boolean ascending) {
        loadWardAdmissions();

        List<WardAdmission> sorted = new ArrayList<>(allWardAdmissions);

        Comparator<WardAdmission> comparator;

        switch (sortBy.toLowerCase()) {
            case "admissionid":
                comparator = Comparator.comparing(WardAdmission::getAdmissionId);
                break;

            case "patientid":
                comparator = Comparator.comparing(WardAdmission::getPatientId);
                break;

            case "wardid":
                comparator = Comparator.comparing(WardAdmission::getWardId);
                break;

            case "admitteddatetime":
                comparator = Comparator.comparing(WardAdmission::getAdmittedDateTime);
                break;

            case "status":
                comparator = Comparator.comparing(a -> a.getStatus().toString());
                break;

            default:
                return sorted;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        sorted.sort(comparator);

        return sorted;
    }

    // =========================================================
    // BED AVAILABILITY (derived, not stored on Ward)
    // =========================================================

    public static int getActiveAdmissionCount(String wardId) {
        return (int) getAllWardAdmissions().stream()
                .filter(a -> a.getWardId().equals(wardId) && a.getStatus() == AdmissionStatus.ADMITTED)
                .count();
    }

    public static int getAvailableBedCount(String wardId) {
        HospitalAsset asset = HospitalAssetManagement.getHospitalAssetById(wardId);

        if (!(asset instanceof Ward)) {
            return 0;
        }

        int totalBeds = ((Ward) asset).getTotalBeds();
        int occupied = getActiveAdmissionCount(wardId);

        return Math.max(0, totalBeds - occupied);
    }

    // =========================================================
    // FILE HANDLER
    // =========================================================

    private static boolean writeWardAdmissions() {
        ArrayList<LinkedHashMap<String, String>> admissionData = new ArrayList<>();

        for (WardAdmission admission : allWardAdmissions) {
            admissionData.add(admission.getWardAdmissionMap());
        }

        admissionHandler.writeFile(admissionData);

        return true;
    }

    private static boolean appendWardAdmission(WardAdmission admission) {
        try {
            ArrayList<LinkedHashMap<String, String>> admissionData = new ArrayList<>();
            admissionData.add(admission.getWardAdmissionMap());

            if (!admissionHandler.checkFileExistence() && !admissionHandler.createFile()) {
                System.out.printf("File 'wardAdmissions.txt' failed to be created, ward admission '%s' cannot be appended%n", admission.getAdmissionId());
                return false;
            }

            admissionHandler.appendFile(admissionData);

            System.out.printf("Ward admission '%s' is appended to wardAdmissions.txt!%n", admission.getAdmissionId());

            return true;

        } catch (Exception e) {
            System.out.println("Error appending new ward admission: " + e.getMessage());
            Logger.errorLog("Error appending new ward admission (System Error) -> " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // CREATE (ADMIT)
    // =========================================================

    public LinkedHashMap<String, String> createWardAdmission(String patientId, String wardId, String admittingDoctorId, String admittedDateTime, String reason) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        loadWardAdmissions(); // keep in-memory list synced with disk before mutating

        HospitalAsset asset = HospitalAssetManagement.getHospitalAssetById(wardId);
        if (!(asset instanceof Ward)) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Ward admission is not created\nInvalid ward ID: '%s' is not found", wardId));
            return msgMap;
        }

        if (patientId == null || patientId.isBlank()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Ward admission is not created\nPatient ID is required");
            return msgMap;
        }

        if (getAvailableBedCount(wardId) <= 0) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Ward admission is not created\nWard '%s' has no available beds", wardId));
            return msgMap;
        }

        String newAdmissionId = generateWardAdmissionId();

        WardAdmission newAdmission = new WardAdmission(
                newAdmissionId,
                patientId,
                wardId,
                admittingDoctorId,
                (admittedDateTime == null || admittedDateTime.isBlank())
                        ? DateTime.formattedLocalDateTime(DateTime.currentDateTime())
                        : admittedDateTime,
                "",
                AdmissionStatus.ADMITTED,
                reason
        );

        allWardAdmissions.add(newAdmission);

        if (admissionHandler.checkFileExistence()) {
            appendWardAdmission(newAdmission);
        } else {
            writeWardAdmissions();
        }

        loadWardAdmissions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Ward admission '%s' is created and stored!", newAdmissionId));
        msgMap.put("newAdmissionId", newAdmissionId);

        return msgMap;
    }

    // =========================================================
    // DISCHARGE
    // =========================================================

    public LinkedHashMap<String, String> dischargeWardAdmission(String admissionId, String dischargedDateTime) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        WardAdmission admission = getWardAdmissionById(admissionId);

        if (admission == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Ward admission '%s' is not found", admissionId));
            return msgMap;
        }

        if (admission.getStatus() == AdmissionStatus.DISCHARGED) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Ward admission '%s' is already discharged", admissionId));
            return msgMap;
        }

        admission.setDischargedDateTime(
                (dischargedDateTime == null || dischargedDateTime.isBlank())
                        ? DateTime.formattedLocalDateTime(DateTime.currentDateTime())
                        : dischargedDateTime
        );
        admission.setStatus(AdmissionStatus.DISCHARGED);

        writeWardAdmissions();
        loadWardAdmissions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Ward admission '%s' is discharged!", admissionId));

        return msgMap;
    }

    // =========================================================
    // MODIFY REASON
    // =========================================================

    public LinkedHashMap<String, String> modifyWardAdmissionReason(String admissionId, String reason) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        WardAdmission admission = getWardAdmissionById(admissionId);

        if (admission == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Ward admission '%s' is not found", admissionId));
            return msgMap;
        }

        admission.setReason(reason);

        writeWardAdmissions();
        loadWardAdmissions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Ward admission '%s' reason is updated!", admissionId));

        return msgMap;
    }

    // =========================================================
    // DELETE
    // =========================================================

//    FOR ADMIN STAFF ONLY
    public LinkedHashMap<String, String> deleteWardAdmission(String admissionId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        WardAdmission deleteAdmission = getWardAdmissionById(admissionId);

        if (deleteAdmission == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Ward admission '%s' not found", admissionId));
            return msgMap;
        }

        allWardAdmissions.removeIf(a -> a.getAdmissionId().equals(admissionId));

        writeWardAdmissions();
        loadWardAdmissions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Ward admission '%s' is deleted!", admissionId));
        msgMap.put("deleteAdmissionId", admissionId);

        return msgMap;
    }

    // =========================================================
    // GENERATE ID
    // =========================================================

    public static String generateWardAdmissionId() {
        List<String> existingIds = allWardAdmissions.stream()
                .map(WardAdmission::getAdmissionId)
                .toList();

        return SystemConfiguration.generateId("#WA", existingIds);
    }
}
