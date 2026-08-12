/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.ErrorHandler;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.exceptions.InsuranceException;
import oodj_hospital_management_system.utils.exceptions.UserException;

/**
 *
 * @author sheaw
 */

public class InsuranceManagement {
    private static final FileHandler insuranceHandler = new FileHandler(SystemConfiguration.insuranceFilePath);
    private static final List<Insurance> insurances = new ArrayList<>();
    private User activeUser;

    public InsuranceManagement(User usr) {
        this.activeUser = usr;
        loadInsurances();
    }

    public static void loadInsurances() {
        insurances.clear();
        List<LinkedHashMap<String, String>> rawData = insuranceHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // insuranceId,insuranceName,accepted,coveragePercentage
            Insurance insurance = new Insurance(
                    row.get("insuranceId"),
                    row.get("insuranceName"),
                    Boolean.parseBoolean(row.get("accepted")),
                    Double.parseDouble(row.get("coveragePercentage"))
            );
            insurances.add(insurance);
        }
    }

    public static List<Insurance> getAllInsurances() {
        loadInsurances();
        return insurances;
    }

    public static Insurance getInsuranceById(String insuranceId) {
        loadInsurances();
        try {
            if (!insurances.stream().anyMatch(insurance -> insurance.getInsuranceId().equals(insuranceId))) {
                throw new InsuranceException(String.format("Invalid Insurance ID: Insurance ID '%s' is not found", insuranceId));
            }
            for (Insurance insurance : insurances) {
                if (insurance.getInsuranceId().equals(insuranceId)) {
                    return insurance;
                }
            }
            return null;
        } catch (InsuranceException e) {
            System.out.println("Error finding insurance -> " + e.getMessage());
            Logger.errorLog("Error finding insurance -> " + e.getMessage());
            return null;
        }
    }

    // File Handler (Write and Append)
    private static boolean writeInsurances() {
        ArrayList<LinkedHashMap<String, String>> insuranceMaps = new ArrayList<>();
        for (Insurance insurance : insurances) {
            insuranceMaps.add(insurance.getInsuranceMap());
        }
        insuranceHandler.writeFile(insuranceMaps);
        return true;
    }

    private static boolean appendInsurance(Insurance insurance) {
        try {
            ArrayList<LinkedHashMap<String, String>> insuranceMaps = new ArrayList<>();
            insuranceMaps.add(insurance.getInsuranceMap());
            if (!insuranceHandler.checkFileExistence() && !insuranceHandler.createFile()) {
                System.out.printf("File 'insurances.txt' failed to be created, insurance '%s' cannot be appended%n", insurance.getInsuranceId());
                return false;
            }
            insuranceHandler.appendFile(insuranceMaps);
            System.out.printf("Insurance '%s' is appended to insurances.txt!%n", insurance.getInsuranceId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new insurance: " + e.getMessage());
            Logger.errorLog("Error appending new insurance (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }

    public LinkedHashMap<String, String> createInsurance(String insuranceName, boolean accepted, double coveragePercentage) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.insuranceErrorHandler(Arrays.asList("admin staff", "insuranceName", "coveragePercentage"), activeUser, insuranceName, coveragePercentage);
        } catch (InsuranceException e) {
            System.out.println("Error creating insurance\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Insurance is not created\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            System.out.println("Error creating insurance\n" + ue.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Insurance is not created\n" + ue.getMessage());
            return msgMap;
        }

        String newInsuranceId = generateInsuranceId();
        Insurance newInsurance = new Insurance(newInsuranceId, insuranceName, accepted, coveragePercentage);
        insurances.add(newInsurance);
        if (insuranceHandler.checkFileExistence()) {
            appendInsurance(newInsurance);
        } else {
            writeInsurances();
        }
        loadInsurances();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Insurance '%s' is created successfully!", newInsuranceId));
        msgMap.put("newInsuranceId", newInsuranceId);
        return msgMap;
    }

    public LinkedHashMap<String, String> updateInsurance(Insurance insurance, String insuranceName, boolean accepted, double coveragePercentage) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.insuranceErrorHandler(Arrays.asList("admin staff", "update insurance", "insuranceName", "coveragePercentage"), activeUser, insurance, insuranceName, coveragePercentage);
        } catch (InsuranceException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Insurance is not updated\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            msgMap.put("success", null);
            msgMap.put("msg", "Insurance is not updated\n" + ue.getMessage());
            return msgMap;
        }

        // Re-fetch the live object from the current list — the 'insurance' param
        // may be stale/detached because ErrorHandler's validation reloads the list
        Insurance targetInsurance = getInsuranceById(insurance.getInsuranceId());
        if (targetInsurance == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Insurance is not found");
            return msgMap;
        }

        targetInsurance.setInsuranceName(insuranceName);
        targetInsurance.setAccepted(accepted);
        targetInsurance.setCoveragePercentage(coveragePercentage);
        writeInsurances();
        loadInsurances();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Insurance '%s' is updated successfully!", targetInsurance.getInsuranceId()));
        return msgMap;
    }

    // No hard delete (matches Bill's "no delete on financial-linked records" reasoning) -
    // toggling 'accepted' to false is the soft-disable path via updateInsurance()

    // Searching and Sorting
    public List<Insurance> searchInsurances(String keywords) {
        List<Insurance> matched = new ArrayList<>();
        for (Insurance insurance : insurances) {
            if (insurance.getInsuranceId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || insurance.getInsuranceName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matched.add(insurance);
            }
        }
        return matched;
    }

    public List<Insurance> searchInsurances(String keywords, List<Insurance> insuranceList) {
        List<Insurance> matched = new ArrayList<>();
        for (Insurance insurance : insuranceList) {
            if (insurance.getInsuranceId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || insurance.getInsuranceName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matched.add(insurance);
            }
        }
        return matched;
    }

    public List<Insurance> sortInsurancesById(List<Insurance> list) {
        List<Insurance> sorted = list;
        sorted.sort(Comparator.comparing(Insurance::getInsuranceId));
        return sorted;
    }

    public List<Insurance> sortInsurancesByName(List<Insurance> list) {
        List<Insurance> sorted = list;
        sorted.sort(Comparator.comparing(Insurance::getInsuranceName));
        return sorted;
    }

    // Generate ID
    public static String generateInsuranceId() {
        List<String> existingIds = insurances.stream().map(Insurance::getInsuranceId).toList();
        return SystemConfiguration.generateId("#INS", existingIds);
    }
}