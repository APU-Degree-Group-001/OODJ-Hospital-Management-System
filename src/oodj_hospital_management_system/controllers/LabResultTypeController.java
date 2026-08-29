/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.LabResultType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.LabResultTypeManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author sheaw
 */
public class LabResultTypeController {
    private LabResultTypeManagement labResultTypeServices;
    private User activeUser;
 
    public LabResultTypeController(User activeUser) {
        this.activeUser = activeUser;
        this.labResultTypeServices = new LabResultTypeManagement();
    }
 
    // Get Lab Result Types
    public List<LabResultType> getAllLabResultTypes() {
        return LabResultTypeManagement.getAllLabResultTypes();
    }
 
    public List<LabResultType> getActiveLabResultTypes() {
        return LabResultTypeManagement.getActiveLabResultTypes();
    }
 
    // Lookup by ID
    public LabResultType getLabResultTypeById(String labResultTypeId) {
        return LabResultTypeManagement.getLabResultTypeById(labResultTypeId);
    }
 
    // Lookup by Name
    public LabResultType getLabResultTypeByName(String typeName) {
        return LabResultTypeManagement.getLabResultTypeByName(typeName);
    }
 
    // CRUD
    public LinkedHashMap<String, String> createLabResultType(String typeName, double baseRate) {
        LinkedHashMap<String, String> msg = labResultTypeServices.createLabResultType(typeName, baseRate);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Lab Result Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newLabResultTypeId")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a lab result type\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> updateLabResultType(LabResultType labResultType, String typeName, double baseRate) {
        LinkedHashMap<String, String> msg = labResultTypeServices.updateLabResultType(labResultType, typeName, baseRate);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated Lab Result Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    labResultType.getLabResultTypeId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update Lab Result Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    labResultType.getLabResultTypeId(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> deactivateLabResultType(String labResultTypeId) {
        LinkedHashMap<String, String> msg = labResultTypeServices.deactivateLabResultType(labResultTypeId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} deactivated Lab Result Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), labResultTypeId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to deactivate Lab Result Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), labResultTypeId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> activateLabResultType(String labResultTypeId) {
        LinkedHashMap<String, String> msg = labResultTypeServices.activateLabResultType(labResultTypeId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} activated Lab Result Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), labResultTypeId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to activate Lab Result Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), labResultTypeId, msg.get("msg")));
        }
        return msg;
    }
}