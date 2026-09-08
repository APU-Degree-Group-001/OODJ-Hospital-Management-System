/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.WardAdmission;
import oodj_hospital_management_system.services.WardAdmissionManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author sheaw
 */
public class WardAdmissionController {
    private WardAdmissionManagement wardAdmissionServices;
    private User activeUser;

    public WardAdmissionController(User activeUser) {
        this.activeUser = activeUser;
        this.wardAdmissionServices = new WardAdmissionManagement(activeUser);
    }

    // Get Admissions
    public List<WardAdmission> getAllWardAdmissions() {
        return WardAdmissionManagement.getAllWardAdmissions();
    }

    // Lookup by ID
    public WardAdmission getWardAdmissionById(String admissionId) {
        return WardAdmissionManagement.getWardAdmissionById(admissionId);
    }

    // Search and Sort
    public List<WardAdmission> searchWardAdmissions(String keyword) {
        return WardAdmissionManagement.searchWardAdmissions(keyword);
    }

    public List<WardAdmission> sortWardAdmissions(String sortBy, boolean ascending) {
        return WardAdmissionManagement.sortWardAdmissions(sortBy, ascending);
    }

    // Bed Availability
    public int getActiveAdmissionCount(String wardId) {
        return WardAdmissionManagement.getActiveAdmissionCount(wardId);
    }

    public int getAvailableBedCount(String wardId) {
        return WardAdmissionManagement.getAvailableBedCount(wardId);
    }

    // CRUD
    public LinkedHashMap<String, String> createWardAdmission(String patientId, String wardId, String admittingDoctorId, String admittedDateTime, String reason) {
        LinkedHashMap<String, String> msg = wardAdmissionServices.createWardAdmission(patientId, wardId, admittingDoctorId, admittedDateTime, reason);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Ward Admission {%s} for Patient {%s} in Ward {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newAdmissionId"), patientId, wardId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create Ward Admission for Patient {%s} in Ward {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    patientId, wardId, msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> dischargeWardAdmission(String admissionId, String dischargedDateTime) {
        LinkedHashMap<String, String> msg = wardAdmissionServices.dischargeWardAdmission(admissionId, dischargedDateTime);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} discharged Ward Admission {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), admissionId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to discharge Ward Admission {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), admissionId, msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> modifyWardAdmissionReason(String admissionId, String reason) {
        LinkedHashMap<String, String> msg = wardAdmissionServices.modifyWardAdmissionReason(admissionId, reason);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated reason for Ward Admission {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), admissionId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update reason for Ward Admission {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), admissionId, msg.get("msg")));
        }
        return msg;
    }

    // For Admin Staff only
    public LinkedHashMap<String, String> deleteWardAdmission(String admissionId) {
        LinkedHashMap<String, String> msg = wardAdmissionServices.deleteWardAdmission(admissionId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} deleted Ward Admission {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("deleteAdmissionId")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to delete Ward Admission {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), admissionId, msg.get("msg")));
        }
        return msg;
    }
}
