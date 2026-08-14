/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.InsuranceManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author paure
 */
public class InsuranceController {
    private InsuranceManagement insuranceServices;
    private User activeUser;

    public InsuranceController(User activeUser) {
        this.activeUser = activeUser;
        this.insuranceServices = new InsuranceManagement(activeUser);
    }
    
    // Get Insurances
    public List<Insurance> getAllInsurances() {
        return InsuranceManagement.getAllInsurances();
    }
 
    // Lookup by ID
    public Insurance getInsuranceById(String insuranceId) {
        return InsuranceManagement.getInsuranceById(insuranceId);
    }
 
    // Search and Sort
    public List<Insurance> searchInsurances(String keywords) {
        return insuranceServices.searchInsurances(keywords);
    }
 
    public List<Insurance> searchInsurances(String keywords, List<Insurance> insuranceList) {
        return insuranceServices.searchInsurances(keywords, insuranceList);
    }
 
    public List<Insurance> sortInsurancesById(List<Insurance> list) {
        return insuranceServices.sortInsurancesById(list);
    }
 
    public List<Insurance> sortInsurancesByName(List<Insurance> list) {
        return insuranceServices.sortInsurancesByName(list);
    }
 
    // CRUD
    public LinkedHashMap<String, String> createInsurance(String insuranceName, boolean accepted, double coveragePercentage) {
        LinkedHashMap<String, String> msg = insuranceServices.createInsurance(insuranceName, accepted, coveragePercentage);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Insurance {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newInsuranceId"), insuranceName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create Insurance {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), insuranceName, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> updateInsurance(Insurance insurance, String insuranceName, boolean accepted, double coveragePercentage) {
        LinkedHashMap<String, String> msg = insuranceServices.updateInsurance(insurance, insuranceName, accepted, coveragePercentage);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated Insurance {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    insurance.getInsuranceId(), insuranceName));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update Insurance {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    insurance != null ? insurance.getInsuranceId() : "?", msg.get("msg")));
        }
        return msg;
    }
}
