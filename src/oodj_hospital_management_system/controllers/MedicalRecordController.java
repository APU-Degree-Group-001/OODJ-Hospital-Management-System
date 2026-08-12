/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.MedicalRecordManagement;

/**
 *
 * @author paure
 */
public class MedicalRecordController {
    private MedicalRecordManagement medicalRecordServices;
    private User activeUser;

    public MedicalRecordController(User activeUser) {
        this.activeUser = activeUser;
        this.medicalRecordServices = new MedicalRecordManagement(activeUser);
    }
}
