/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

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
    public static final String superAdminFilePath = parentPath + "txt/users/superAdmin.txt";
    public static final String userFilePath = parentPath + "txt/users/users.txt";
    public static final String rbcFilePath = parentPath + "txt/users/rbc.txt";
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
    public static String[] userTypes = { "administrative staff", "medical manager", "doctor", "patient" };

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
    
}
