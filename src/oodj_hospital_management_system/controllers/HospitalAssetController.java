/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.HospitalAssetManagement;

/**
 *
 * @author paure
 */
public class HospitalAssetController {
    private HospitalAssetManagement hospitalAssetServices;
    private User activeUser;

    public HospitalAssetController(User activeUser) {
        this.activeUser = activeUser;
        this.hospitalAssetServices = new HospitalAssetManagement(activeUser);
    }
}
