/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.BillingManagement;

/**
 *
 * @author paure
 */
public class BillingController {
    private BillingManagement billingServices;
    private User activeUser;

    public BillingController(User activeUser) {
        this.activeUser = activeUser;
        this.billingServices = new BillingManagement(activeUser);
    }
}
