/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.InsuranceManagement;

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
}
