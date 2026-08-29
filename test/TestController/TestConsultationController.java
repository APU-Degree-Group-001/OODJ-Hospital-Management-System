/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestController;

/**
 *
 * @author paure
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.controllers.ConsultationController;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.Feedback;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.ConsultationTypeManagement;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;

public class TestConsultationController {
    private static ConsultationController consultationCtrl;
 
    public static void main(String[] args) {
 
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        if (adminStaff == null) {
            System.out.println("Test failed: Admin Staff '#ADMd4e8f' was not found.");
            return;
        }
 
        consultationCtrl = new ConsultationController(adminStaff);
 
        // Test loading
//        testGetAllConsultations();
//        testGetAllConsultationSlots();
//        testGetAllFeedback();
 
        // Test lookup
        testGetConsultationById();
        testGetConsultationSlotById();
        testGetFeedbackById();
        testGetFeedbackByConsultationId();
 
        // Test creation - Uncomment when needed, these modify the .txt files AND write to the log file
//        testCreateConsultationSlot();
//        testCreateConsultationSlotInBulk();
//        testCreateConsultation();
 
        // Test deletion
//        testDeleteConsultation();
//        testDeleteConsultationSlotsInBulk();
    }
 
    // =========================================================
    // LOAD TESTS
    // =========================================================
 
    static void testGetAllConsultations() {
 
        System.out.println("\n===== TEST GET ALL CONSULTATIONS (via Controller) =====");
 
        List<Consultation> consultations = consultationCtrl.getAllConsultations();
 
        System.out.println("Total Consultations: " + consultations.size());
 
        for (Consultation c : consultations) {
            System.out.println(c.getConsultationId() + " | " + c.getDescription());
        }
    }
 
    static void testGetAllConsultationSlots() {
 
        System.out.println("\n===== TEST GET ALL CONSULTATION SLOTS (via Controller) =====");
 
        List<ConsultationSlot> slots = consultationCtrl.getAllConsultationSlots();
 
        System.out.println("Total Slots: " + slots.size());
 
        for (ConsultationSlot s : slots) {
            System.out.println(s.getConsultationSlotId() + " | " + s.getStartDateTime() + " - " + s.getEndDateTime());
        }
    }
 
    static void testGetAllFeedback() {
 
        System.out.println("\n===== TEST GET ALL FEEDBACK (via Controller) =====");
 
        List<Feedback> feedbackList = consultationCtrl.getAllFeedback();
 
        System.out.println("Total Feedback: " + feedbackList.size());
 
        for (Feedback f : feedbackList) {
            System.out.println(f.getFeedbackId() + " | " + f.getConsultationId());
        }
    }
 
    // =========================================================
    // LOOKUP TESTS
    // =========================================================
 
    static void testGetConsultationById() {
 
        System.out.println("\n===== TEST GET CONSULTATION BY ID (via Controller) =====");
 
        List<Consultation> consultations = consultationCtrl.getAllConsultations();
 
        if (consultations.isEmpty()) {
            System.out.println("No consultations available.");
            return;
        }
 
        String id = consultations.get(0).getConsultationId();
        Consultation consultation = consultationCtrl.getConsultationById(id);
 
        System.out.println(consultation != null ? "Found: " + consultation.getConsultationId() : "Test failed: not found.");
    }
 
    static void testGetConsultationSlotById() {
 
        System.out.println("\n===== TEST GET CONSULTATION SLOT BY ID (via Controller) =====");
 
        List<ConsultationSlot> slots = consultationCtrl.getAllConsultationSlots();
 
        if (slots.isEmpty()) {
            System.out.println("No slots available.");
            return;
        }
 
        String id = slots.get(0).getConsultationSlotId();
        ConsultationSlot slot = consultationCtrl.getConsultationSlotById(id);
 
        System.out.println(slot != null ? "Found: " + slot.getConsultationSlotId() : "Test failed: not found.");
    }
 
    static void testGetFeedbackById() {
 
        System.out.println("\n===== TEST GET FEEDBACK BY ID (via Controller) =====");
 
        List<Feedback> feedbackList = consultationCtrl.getAllFeedback();
 
        if (feedbackList.isEmpty()) {
            System.out.println("No feedback available.");
            return;
        }
 
        String id = feedbackList.get(0).getFeedbackId();
        Feedback feedback = consultationCtrl.getFeedbackById(id);
 
        System.out.println(feedback != null ? "Found: " + feedback.getFeedbackId() : "Test failed: not found.");
    }
 
    static void testGetFeedbackByConsultationId() {
 
        System.out.println("\n===== TEST GET FEEDBACK BY CONSULTATION ID (via Controller) =====");
 
        String consultationId = "#CON001"; // replace with one that actually has feedback
 
        Feedback feedback = consultationCtrl.getFeedbackByConsultationId(consultationId);
 
        System.out.println(feedback != null ? "Found: " + feedback.getFeedbackId() : "No feedback for " + consultationId);
    }
 
    // =========================================================
    // CREATE TESTS
    // =========================================================
 
    static void testCreateConsultationSlot() {
 
        System.out.println("\n===== TEST CREATE CONSULTATION SLOT (via Controller) =====");
 
        DoctorShift shift = DoctorManagement.getDoctorShiftById("#SHIFT001"); // replace with a real shift ID
 
        if (shift == null) {
            System.out.println("Doctor Shift not found.");
            return;
        }
 
        LinkedHashMap<String, String> result = consultationCtrl.createConsultationSlot(
                shift,
                "2026-09-01 09:00",
                "2026-09-01 09:30"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Slot ID : " + result.get("newConsultationSlotId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    static void testCreateConsultationSlotInBulk() {
 
        System.out.println("\n===== TEST CREATE CONSULTATION SLOT IN BULK (via Controller) =====");
 
        DoctorShift shift = DoctorManagement.getDoctorShiftById("#SHIFT001");
 
        if (shift == null) {
            System.out.println("Doctor Shift not found.");
            return;
        }
 
        LinkedHashMap<String, String> result = consultationCtrl.createConsultationSlotInBulk(
                shift,
                "2026-09-01 09:00",
                "2026-09-01 17:00"
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("Slot Count : " + result.get("slotCount"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    static void testCreateConsultation() {

        System.out.println("\n===== TEST CREATE CONSULTATION (via Controller) =====");

        List<ConsultationSlot> slots = consultationCtrl.getAllConsultationSlots();
        Patient patient = (Patient) UserManagement.getPatientById("#PAT79a39");
        ConsultationType consultationType = ConsultationTypeManagement.getConsultationTypeById("#CT001"); // replace with a real ID

        if (slots.isEmpty() || patient == null || consultationType == null) {
            System.out.println("No available slot, patient, or consultation type not found.");
            return;
        }

        ConsultationSlot slot = slots.get(0);

        LinkedHashMap<String, String> result = consultationCtrl.createConsultation(
                slot,
                patient,
                consultationType,
                "Testing consultation creation via controller"
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Consultation ID : " + result.get("newConsultationId"));

        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    // =========================================================
    // DELETE TESTS
    // =========================================================
 
    static void testDeleteConsultation() {
 
        System.out.println("\n===== TEST DELETE CONSULTATION (via Controller) =====");
 
        List<Consultation> consultations = consultationCtrl.getAllConsultations();
 
        if (consultations.isEmpty()) {
            System.out.println("No consultations available to delete.");
            return;
        }
 
        String consultationId = consultations.get(consultations.size() - 1).getConsultationId();
 
        LinkedHashMap<String, String> result = consultationCtrl.deleteConsultation(consultationId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testDeleteConsultationSlotsInBulk() {
 
        System.out.println("\n===== TEST DELETE CONSULTATION SLOTS IN BULK (via Controller) =====");
 
        String doctorShiftId = "#SHIFT001"; // replace with a real shift ID that has slots to clear
 
        boolean success = consultationCtrl.deleteConsultationSlotsInBulk(doctorShiftId);
 
        System.out.println("Success : " + success);
    }
}
