/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.DoctorManagement;

/**
 *
 * @author paure
 */
public class DoctorController {
    private DoctorManagement doctorServices;
    private User activeUser;

    public DoctorController(User activeUser) {
        this.activeUser = activeUser;
        this.doctorServices = new DoctorManagement(activeUser);
    }
}
