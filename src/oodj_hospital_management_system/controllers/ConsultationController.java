/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.Feedback;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.utils.Logger;

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
    
    // Get Consultations, Slots, Feedback
    public List<Consultation> getAllConsultations() {
        return ConsultationManagement.getAllConsultations();
    }
 
    public List<ConsultationSlot> getAllConsultationSlots() {
        return ConsultationManagement.getAllConsultationSlots();
    }
 
    public List<Feedback> getAllFeedback() {
        return ConsultationManagement.getAllFeedback();
    }
 
    // Lookup by ID
    public Consultation getConsultationById(String consultationId) {
        return ConsultationManagement.getConsultationById(consultationId);
    }
 
    public ConsultationSlot getConsultationSlotById(String consultationSlotId) {
        return ConsultationManagement.getConsultationSlotById(consultationSlotId);
    }
 
    public Feedback getFeedbackById(String feedbackId) {
        return ConsultationManagement.getFeedbackById(feedbackId);
    }
 
    public Feedback getFeedbackByConsultationId(String consultationId) {
        return ConsultationManagement.getFeedbackByConsultationId(consultationId);
    }
 
    // CRUD
    public LinkedHashMap<String, String> createConsultation(ConsultationSlot consultationSlot, Patient patient, String description) {
        LinkedHashMap<String, String> msg = consultationServices.createConsultation(consultationSlot, patient, description);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Consultation {%s} for Patient {%s - %s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newConsultationId"), patient.getId(), patient.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a consultation for Patient {%s - %s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    patient.getId(), patient.getName(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> deleteConsultation(String consultationId) {
        LinkedHashMap<String, String> msg = consultationServices.deleteConsultation(consultationId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} deleted Consultation {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("deleteConsultationId")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to delete Consultation {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), consultationId, msg.get("msg")));
        }
        return msg;
    }

    public LinkedHashMap<String, String> createConsultationSlot(DoctorShift doctorShift, String startDateTime, String endDateTime) {
        LinkedHashMap<String, String> msg = consultationServices.createConsultationSlot(doctorShift, startDateTime, endDateTime);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created Consultation Slot {%s} for Doctor Shift {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("newConsultationSlotId"), doctorShift.getShiftId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a consultation slot for Doctor Shift {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    doctorShift.getShiftId(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> createConsultationSlotInBulk(DoctorShift doctorShift, String startDateTime, String endDateTime) {
        LinkedHashMap<String, String> msg = consultationServices.createConsultationSlotInBulk(doctorShift, startDateTime, endDateTime);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} created %s consultation slots for Doctor Shift {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    msg.get("slotCount"), doctorShift.getShiftId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create consultation slots in bulk for Doctor Shift {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    doctorShift.getShiftId(), msg.get("msg")));
        }
        return msg;
    }
 
    public boolean deleteConsultationSlotsInBulk(String doctorShiftId) {
        boolean success = ConsultationManagement.deleteConsultationSlotsInBulk(doctorShiftId);
        if (success) {
            Logger.log(String.format("User {%s - %s - %s} deleted consultation slots in bulk for Doctor Shift {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), doctorShiftId));
        } else {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to delete consultation slots in bulk for Doctor Shift {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), doctorShiftId));
        }
        return success;
    }
}
