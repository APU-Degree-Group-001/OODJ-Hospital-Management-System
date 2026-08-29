/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.ConsultationTypeManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author sheaw
 */
public class ConsultationTypeController {
    private ConsultationTypeManagement consultationTypeServices;
    private User activeUser;
 
    public ConsultationTypeController(User activeUser) {
        this.activeUser = activeUser;
        this.consultationTypeServices = new ConsultationTypeManagement();
    }
 
    // Get Consultation Types
    public List<ConsultationType> getAllConsultationTypes() {
        return ConsultationTypeManagement.getAllConsultationTypes();
    }
 
    public List<ConsultationType> getActiveConsultationTypes() {
        return ConsultationTypeManagement.getActiveConsultationTypes();
    }
 
    // Lookup by ID
    public ConsultationType getConsultationTypeById(String consultationTypeId) {
        return ConsultationTypeManagement.getConsultationTypeById(consultationTypeId);
    }
 
    // CRUD
    public LinkedHashMap<String, String> createConsultationType(String typeName, double baseRate) {
        LinkedHashMap<String, String> msg = consultationTypeServices.createConsultationType(typeName, baseRate);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Consultation Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newConsultationTypeId")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a consultation type\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> updateConsultationType(ConsultationType consultationType, String typeName, double baseRate) {
        LinkedHashMap<String, String> msg = consultationTypeServices.updateConsultationType(consultationType, typeName, baseRate);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated Consultation Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    consultationType.getConsultationTypeId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update Consultation Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    consultationType.getConsultationTypeId(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> deactivateConsultationType(String consultationTypeId) {
        LinkedHashMap<String, String> msg = consultationTypeServices.deactivateConsultationType(consultationTypeId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} deactivated Consultation Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), consultationTypeId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to deactivate Consultation Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), consultationTypeId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> activateConsultationType(String consultationTypeId) {
        LinkedHashMap<String, String> msg = consultationTypeServices.activateConsultationType(consultationTypeId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} activated Consultation Type {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), consultationTypeId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to activate Consultation Type {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), consultationTypeId, msg.get("msg")));
        }
        return msg;
    }
}
 
