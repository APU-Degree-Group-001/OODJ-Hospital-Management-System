/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.PrescriptionType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.PrescriptionTypeManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author sheaw
 */
public class PrescriptionTypeController {
    private PrescriptionTypeManagement prescriptionTypeServices;
    private User activeUser;
 
    public PrescriptionTypeController(User activeUser) {
        this.activeUser = activeUser;
        this.prescriptionTypeServices = new PrescriptionTypeManagement();
    }
 
    // Get Prescription Types
    public List<PrescriptionType> getAllPrescriptionTypes() {
        return PrescriptionTypeManagement.getAllPrescriptionTypes();
    }
 
    public List<PrescriptionType> getActivePrescriptionTypes() {
        return PrescriptionTypeManagement.getActivePrescriptionTypes();
    }
 
    // Lookup by ID
    public PrescriptionType getPrescriptionTypeById(String prescriptionTypeId) {
        return PrescriptionTypeManagement.getPrescriptionTypeById(prescriptionTypeId);
    }
 
    // Lookup by Name
    public PrescriptionType getPrescriptionTypeByName(String typeName) {
        return PrescriptionTypeManagement.getPrescriptionTypeByName(typeName);
    }
 
    // CRUD
    public LinkedHashMap<String, String> createPrescriptionType(String typeName, double baseRate) {
        LinkedHashMap<String, String> msg = prescriptionTypeServices.createPrescriptionType(typeName, baseRate);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Prescription Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newPrescriptionTypeId")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a prescription type\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> updatePrescriptionType(PrescriptionType prescriptionType, String typeName, double baseRate) {
        LinkedHashMap<String, String> msg = prescriptionTypeServices.updatePrescriptionType(prescriptionType, typeName, baseRate);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated Prescription Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    prescriptionType.getPrescriptionTypeId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update Prescription Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    prescriptionType.getPrescriptionTypeId(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> deactivatePrescriptionType(String prescriptionTypeId) {
        LinkedHashMap<String, String> msg = prescriptionTypeServices.deactivatePrescriptionType(prescriptionTypeId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} deactivated Prescription Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionTypeId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to deactivate Prescription Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionTypeId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> activatePrescriptionType(String prescriptionTypeId) {
        LinkedHashMap<String, String> msg = prescriptionTypeServices.activatePrescriptionType(prescriptionTypeId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} activated Prescription Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionTypeId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to activate Prescription Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionTypeId, msg.get("msg")));
        }
        return msg;
    }
}