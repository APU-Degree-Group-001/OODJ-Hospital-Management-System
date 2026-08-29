/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author sheaw
 * 
 * Description: Store system wide constants
 */
public class SystemConfiguration {
    // TXT File Path
    public static final String parentPath = "src/oodj_hospital_management_system/";
    public static final String logFilePath = parentPath + "txt/log/log.txt";
    public static final String errorLogFilePath = parentPath + "txt/log/error_log.txt";
    
    public static final String userFilePath = parentPath + "txt/users/users.txt";
    public static final String doctorShiftFilePath = parentPath + "txt/users/doctorShifts.txt";
    public static final String medicalDepartmentFilePath = parentPath + "txt/users/medicalDepartments.txt";
    public static final String requestFilePath = parentPath + "txt/users/requests.txt";
    
    public static final String billFilePath = parentPath + "txt/consultations/bills.txt";
    public static final String insuranceFilePath = parentPath + "txt/consultations/insurance.txt";
    public static final String consultationFilePath = parentPath + "txt/consultations/consultations.txt";
    public static final String consultationTypesFilePath = parentPath + "txt/consultations/consultationTypes.txt";
    public static final String consultationSlotFilePath = parentPath + "txt/consultations/consultationSlots.txt";
    public static final String feedbackFilePath = parentPath + "txt/consultations/feedback.txt";
    
    public static final String hospitalAssetAllocationFilePath = parentPath + "txt/hospital_assets/hospitalAssetAllocation.txt";
    public static final String hospitalAssetFilePath = parentPath + "txt/hospital_assets/hospitalAssets.txt";
    
    public static final String labResultFilePath = parentPath + "txt/medical_records/labResults.txt";
    public static final String labResultTypesFilePath = parentPath + "txt/medical_records/labResultTypes.txt";
    public static final String medicalAssessmentFilePath = parentPath + "txt/medical_records/medicalAssessments.txt";
    public static final String prescriptionFilePath = parentPath + "txt/medical_records/prescriptions.txt";
    public static final String prescriptionTypesFilePath = parentPath + "txt/medical_records/prescriptionTypes.txt";
    
    public static final String backupDir = parentPath + "txt/backups/";
    
    // Default Hospital Assets
//    public static final ArrayList<LinkedHashMap<String, String>> defaultHospitalAssets = new ArrayList<>(Arrays.asList(
//        new LinkedHashMap<String, String>() {{put("assetCode", "CR"); put("assetName", "Consultation Room");}},
//        new LinkedHashMap<String, String>() {{put("assetCode", "WD"); put("assetName", "Ward");}},
//        new LinkedHashMap<String, String>() {{put("assetCode", "LB"); put("assetName", "Laboratory");}},
//        new LinkedHashMap<String, String>() {{put("assetCode", "XR"); put("assetName", "X-Ray Room");}},
//        new LinkedHashMap<String, String>() {{put("assetCode", "IR"); put("assetName", "Imaging Room");}}
//    ));
    
    // Default User Type
    public static String[] userTypes = { "admin staff", "medical manager", "doctor", "patient" };

    // Default Escape Delimiter (replace ',' with '#|')
    public static final String txtDelimiter = "#|";

    // Strong Password Pattern
    public static final String pwdPattern1 = ".*\\d+.*"; // one or more digits
    public static final String pwdPattern2 = ".*[a-z]+.*"; // one or more lowercase letters
    public static final String pwdPattern3 = ".*[A-Z]+.*"; // one or more uppercase letters
    public static final String pwdPattern4 = ".*\\W+.*"; // one or more special characters
    
    // Default Shift Hour
    public static final int dailyMaxShiftHours = 8;
    public static final int monthlyMinShiftHours = 80;
    public static final int monthlyMaxShiftHours = 160;
    
    public static String generateId(String prefix, List<String> existingIds) {
        String newId = "";
        boolean loop;

        do {
            newId = prefix + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
            String id = newId;
            loop = newId != null && existingIds.stream().anyMatch(existingId -> Objects.equals(existingId, id));
        } while (loop);

        return newId;
}
}
