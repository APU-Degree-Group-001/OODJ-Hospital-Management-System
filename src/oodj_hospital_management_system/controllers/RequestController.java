/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.RequestManagement;

/**
 *
 * @author paure
 */
public class RequestController {
    private RequestManagement requestServices;
    private User activeUser;

    public RequestController(User activeUser) {
        this.activeUser = activeUser;
        this.requestServices = new RequestManagement(activeUser);
    }
}
