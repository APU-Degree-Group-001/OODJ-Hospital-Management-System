/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.ConsultationManagement;

/**
 *
 * @author paure
 */
public class ConsultationController {
    private ConsultationManagement consultationServices;
    private User activeUser;

    public ConsultationController(User activeUser) {
        this.activeUser = activeUser;
        this.consultationServices = new ConsultationManagement(activeUser);
    }
}
