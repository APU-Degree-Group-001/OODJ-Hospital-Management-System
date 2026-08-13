/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.BillingManagement;
import oodj_hospital_management_system.utils.Logger;

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
    
    // CRUD
    public LinkedHashMap<String, String> createBill(String consultationId, Patient patient, Insurance insurance, double consultationFee) {
        LinkedHashMap<String, String> msg = billingServices.createBill(consultationId, patient, insurance, consultationFee);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Bill {%s} for Patient {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newBillId"), patient.getId(), patient.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a bill for Patient {%s - %s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    patient.getId(), patient.getName(), msg.get("msg")));
        }
        return msg;
    }
    
    public LinkedHashMap<String, String> updateBill(Bill bill, Patient patient, Insurance insurance, double consultationFee) {
        LinkedHashMap<String, String> msg = billingServices.updateBill(bill, patient, insurance, consultationFee);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated Bill {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), bill.getBillId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update Bill {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), bill.getBillId(), msg.get("msg")));
        }
        return msg;
    }
    
    public LinkedHashMap<String, String> cancelBill(Bill bill) {
        LinkedHashMap<String, String> msg = billingServices.cancelBill(bill);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} voided Bill {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), bill.getBillId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to void Bill {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), bill.getBillId(), msg.get("msg")));
        }
        return msg;
    }
    
    public LinkedHashMap<String, String> markBillAsPaid(Bill bill) {
        LinkedHashMap<String, String> msg = billingServices.markBillAsPaid(bill);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} marked Bill {%s} as paid",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), bill.getBillId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to mark Bill {%s} as paid\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), bill.getBillId(), msg.get("msg")));
        }
        return msg;
    }
}
