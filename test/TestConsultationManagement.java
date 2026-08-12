/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.Feedback;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;

public class TestConsultationManagement {

    private static ConsultationManagement consultationMng;

    public static void main(String[] args) {

        // Get the active user once
        MedicalManager manager =
                UserManagement.getMedicalManagerById("#MNG74295");

        if (manager == null) {
            System.out.println(
                    "Test failed: Medical Manager '#MNG74295' was not found."
            );
            return;
        }

        // Create ConsultationManagement once
        consultationMng = new ConsultationManagement(manager);

        // Test loading
        testLoadConsultations();
//        testLoadConsultationSlots();
//        testLoadAllFeedback();

        // Test get-all
//        testGetAllConsultations();
//        testGetAllConsultationSlots();
//        testGetAllFeedback();

        // Test get / find by ID
//        testGetConsultationById();
//        testFindConsultationByInvalidId();
//        testGetConsultationSlotById();
//        testGetFeedbackById();

        // Test status parsing
//        testGetConsultationStatus();

        // Test single consultation slot creation (non-bulk)
//        testCreateConsultationSlot();

        // Test consultation creation
        // Uncomment when needed because this modifies consultations.txt
//         testCreateConsultation();
//        testCreateConsultationWithMissingFields();

        // Test consultation deletion
        // Uncomment when needed because this modifies consultations.txt
//         testDeleteConsultation();

        // Test ID generation
//        testGenerateConsultationId();
        testGenerateConsultationSlotId();
    }

    // =========================================================
    // PRINT METHODS
    // =========================================================

    public static void testPrintConsultations(List<Consultation> consultationList) {

        System.out.printf(
                "%-12s %-15s %-20s %-30s %-15s%n",
                "Consult ID",
                "Slot ID",
                "Patient",
                "Description",
                "Status"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (Consultation consultation : consultationList) {

            System.out.printf(
                    "%-12s %-15s %-20s %-30s %-15s%n",
                    consultation.getConsultationId(),
                    consultation.getConsultationSlot() != null
                            ? consultation.getConsultationSlot().getConsultationSlotId() : "N/A",
                    consultation.getPatient() != null
                            ? consultation.getPatient().getName() : "N/A",
                    consultation.getDescription(),
                    consultation.getConsultationStatus()
            );
        }
    }

    public static void testPrintConsultationSlots(List<ConsultationSlot> slotList) {

        System.out.printf(
                "%-15s %-18s %-20s %-20s %-15s%n",
                "Slot ID",
                "Doctor Shift ID",
                "Start DateTime",
                "End DateTime",
                "Status"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (ConsultationSlot slot : slotList) {

            System.out.printf(
                    "%-15s %-18s %-20s %-20s %-15s%n",
                    slot.getConsultationSlotId(),
                    slot.getDoctorShift() != null
                            ? slot.getDoctorShift().getShiftId() : "N/A",
                    slot.getStartDateTime(),
                    slot.getEndDateTime(),
                    slot.getStatus()
            );
        }
    }

    public static void testPrintFeedback(List<Feedback> feedbackList) {

        System.out.printf(
                "%-12s %-12s %-20s %-8s %-30s%n",
                "Feedback ID",
                "Consult ID",
                "Patient",
                "Rating",
                "Comment"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (Feedback feedback : feedbackList) {

            System.out.printf(
                    "%-12s %-12s %-20s %-8s %-30s%n",
                    feedback.getFeedbackId(),
                    feedback.getConsultationId(),
                    feedback.getPatient() != null
                            ? feedback.getPatient().getName() : "N/A",
                    feedback.getRating(),
                    feedback.getComment()
            );
        }
    }

    // =========================================================
    // LOAD TESTS
    // =========================================================

    static void testLoadConsultations() {

        System.out.println("\n===== TEST LOAD CONSULTATIONS =====");

        ConsultationManagement.loadConsultations();

        List<Consultation> consultations =
                ConsultationManagement.getAllConsultations();

        System.out.println("Total Consultations: " + consultations.size());

        testPrintConsultations(consultations);
    }

    static void testLoadConsultationSlots() {

        System.out.println("\n===== TEST LOAD CONSULTATION SLOTS =====");

        ConsultationManagement.loadConsultationSlots();

        List<ConsultationSlot> slots =
                ConsultationManagement.getAllConsultationSlots();

        System.out.println("Total Consultation Slots: " + slots.size());

        testPrintConsultationSlots(slots);
    }

    static void testLoadAllFeedback() {

        System.out.println("\n===== TEST LOAD ALL FEEDBACK =====");

        ConsultationManagement.loadAllFeedback();

        List<Feedback> feedbackList =
                ConsultationManagement.getAllFeedback();

        System.out.println("Total Feedback: " + feedbackList.size());

        testPrintFeedback(feedbackList);
    }

    // =========================================================
    // GET ALL TESTS
    // =========================================================

    static void testGetAllConsultations() {

        System.out.println("\n===== TEST GET ALL CONSULTATIONS =====");

        List<Consultation> consultations =
                ConsultationManagement.getAllConsultations();

        System.out.println("Total Consultations: " + consultations.size());

        testPrintConsultations(consultations);
    }

    static void testGetAllConsultationSlots() {

        System.out.println("\n===== TEST GET ALL CONSULTATION SLOTS =====");

        List<ConsultationSlot> slots =
                ConsultationManagement.getAllConsultationSlots();

        System.out.println("Total Consultation Slots: " + slots.size());

        testPrintConsultationSlots(slots);
    }

    static void testGetAllFeedback() {

        System.out.println("\n===== TEST GET ALL FEEDBACK =====");

        List<Feedback> feedbackList =
                ConsultationManagement.getAllFeedback();

        System.out.println("Total Feedback: " + feedbackList.size());

        testPrintFeedback(feedbackList);
    }

    // =========================================================
    // GET / FIND BY ID TESTS
    // =========================================================

    static void testGetConsultationById() {

        System.out.println("\n===== TEST GET CONSULTATION BY ID =====");

        List<Consultation> consultations =
                ConsultationManagement.getAllConsultations();

        if (consultations.isEmpty()) {
            System.out.println("No consultations available.");
            return;
        }

        String consultationId = consultations.get(0).getConsultationId();

        Consultation consultation =
                ConsultationManagement.getConsultationById(consultationId);

        if (consultation != null) {

            System.out.println("Consultation found:");
            System.out.println("Consultation ID : " + consultation.getConsultationId());
            System.out.println("Description     : " + consultation.getDescription());
            System.out.println("Status          : " + consultation.getConsultationStatus());

        } else {
            System.out.println("Test failed: Consultation not found.");
        }
    }

    static void testFindConsultationByInvalidId() {

        System.out.println("\n===== TEST FIND CONSULTATION BY INVALID ID =====");

        Consultation consultation =
                ConsultationManagement.findConsultationById("#CONinvalid");

        if (consultation == null) {
            System.out.println("Test passed: Invalid consultation ID correctly returned null.");
        } else {
            System.out.println("Test failed: Invalid consultation ID unexpectedly returned a result.");
        }
    }

    static void testGetConsultationSlotById() {

        System.out.println("\n===== TEST GET CONSULTATION SLOT BY ID =====");

        List<ConsultationSlot> slots =
                ConsultationManagement.getAllConsultationSlots();

        if (slots.isEmpty()) {
            System.out.println("No consultation slots available.");
            return;
        }

        String slotId = slots.get(0).getConsultationSlotId();

        ConsultationSlot slot =
                ConsultationManagement.getConsultationSlotById(slotId);

        if (slot != null) {

            System.out.println("Consultation Slot found:");
            System.out.println("Slot ID : " + slot.getConsultationSlotId());
            System.out.println("Status  : " + slot.getStatus());

        } else {
            System.out.println("Test failed: Consultation slot not found.");
        }
    }

    static void testGetFeedbackById() {

        System.out.println("\n===== TEST GET FEEDBACK BY ID =====");

        List<Feedback> feedbackList =
                ConsultationManagement.getAllFeedback();

        if (feedbackList.isEmpty()) {
            System.out.println("No feedback available.");
            return;
        }

        String feedbackId = feedbackList.get(0).getFeedbackId();

        Feedback feedback =
                ConsultationManagement.getFeedbackById(feedbackId);

        if (feedback != null) {

            System.out.println("Feedback found:");
            System.out.println("Feedback ID : " + feedback.getFeedbackId());
            System.out.println("Rating      : " + feedback.getRating());
            System.out.println("Comment     : " + feedback.getComment());

        } else {
            System.out.println("Test failed: Feedback not found.");
        }
    }

    // =========================================================
    // STATUS PARSING TEST
    // =========================================================

    static void testGetConsultationStatus() {

        System.out.println("\n===== TEST GET CONSULTATION STATUS =====");

        String[] statuses = {
            "SCHEDULED", "in_progress", "Completed", "CANCELLED", "bogus", ""
        };

        for (String status : statuses) {
            ConsultationStatus result =
                    ConsultationManagement.getConsultationStatus(status);

            System.out.println("Input: '" + status + "' -> Parsed: " + result);
        }
    }

    // =========================================================
    // SINGLE CONSULTATION SLOT CREATION TEST (NON-BULK)
    // =========================================================

    static void testCreateConsultationSlot() {

        System.out.println("\n===== TEST CREATE CONSULTATION SLOT (SINGLE) =====");

        List<DoctorShift> shifts = DoctorManagement.getAllDoctorShifts();

        if (shifts.isEmpty()) {
            System.out.println("Test failed: No doctor shifts available.");
            return;
        }

        DoctorShift shift = shifts.get(0);

        String startDateTime = "2026-08-20 09:00:00";
        String endDateTime = "2026-08-20 09:30:00";

        LinkedHashMap<String, String> result =
                consultationMng.createConsultationSlot(shift, startDateTime, endDateTime);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Slot ID : " + result.get("newConsultationSlotId"));
    }

    // =========================================================
    // CONSULTATION CREATION TESTS
    // =========================================================

    static void testCreateConsultation() {

        System.out.println("\n===== TEST CREATE CONSULTATION =====");

        List<ConsultationSlot> slots =
                ConsultationManagement.getAllConsultationSlots();

        Patient patient = UserManagement.getPatientById("#PAT3fad4");

        if (slots.isEmpty()) {
            System.out.println("Test failed: No consultation slots available.");
            return;
        }

        if (patient == null) {
            System.out.println("Test failed: Patient not found.");
            return;
        }

        ConsultationSlot slot = slots.get(0);
        String description = "General check-up for recurring headaches.";

        LinkedHashMap<String, String> result =
                consultationMng.createConsultation(slot, patient, description);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Consultation ID : " + result.get("newConsultationId"));

        if ("true".equals(result.get("success"))) {

            Consultation created =
                    ConsultationManagement.getConsultationById(result.get("newConsultationId"));

            if (created != null && created.getConsultationStatus() == ConsultationStatus.SCHEDULED) {
                System.out.println("Creation test PASSED! Status is SCHEDULED as expected.");
            } else {
                System.out.println("Creation test FAILED! Consultation missing or status incorrect.");
            }
        }
    }

    static void testCreateConsultationWithMissingFields() {

        System.out.println("\n===== TEST CREATE CONSULTATION WITH MISSING FIELDS =====");

        LinkedHashMap<String, String> result =
                consultationMng.createConsultation(null, null, "");

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Consultation correctly rejected due to missing fields.");
        } else {
            System.out.println("Test failed: Consultation was created despite missing fields.");
        }
    }

    // =========================================================
    // CONSULTATION DELETION TEST
    // =========================================================

    static void testDeleteConsultation() {

        System.out.println("\n===== TEST DELETE CONSULTATION =====");

        List<Consultation> consultations =
                ConsultationManagement.getAllConsultations();

        if (consultations.isEmpty()) {
            System.out.println("No consultations available to delete.");
            return;
        }

        /*
         * Delete the most recently created consultation
         * (last in the list) rather than an arbitrary one,
         * to avoid removing seeded/important test data.
         */
        Consultation targetConsultation = consultations.get(consultations.size() - 1);

        System.out.println("Deleting Consultation : " + targetConsultation.getConsultationId());

        LinkedHashMap<String, String> result =
                consultationMng.deleteConsultation(targetConsultation.getConsultationId());

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        Consultation checkDeleted =
                ConsultationManagement.getConsultationById(targetConsultation.getConsultationId());

        if (checkDeleted == null) {
            System.out.println("Deletion test PASSED! Consultation no longer found.");
        } else {
            System.out.println("Deletion test FAILED! Consultation still exists.");
        }
    }

    static void testDeleteConsultationInvalidId() {

        System.out.println("\n===== TEST DELETE CONSULTATION WITH INVALID ID =====");

        LinkedHashMap<String, String> result =
                consultationMng.deleteConsultation("#CONinvalid");

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));

        if (result.get("success") == null) {
            System.out.println("Test passed: Invalid consultation ID correctly rejected.");
        } else {
            System.out.println("Test failed: Invalid consultation ID unexpectedly deleted.");
        }
    }

    // =========================================================
    // GENERATE ID TESTS
    // =========================================================

    static void testGenerateConsultationId() {

        System.out.println("\n===== TEST GENERATE CONSULTATION ID =====");

        String consultationId = ConsultationManagement.generateConsultationId();

        System.out.println("Generated Consultation ID : " + consultationId);
    }

    static void testGenerateConsultationSlotId() {

        System.out.println("\n===== TEST GENERATE CONSULTATION SLOT ID =====");

        String slotId = ConsultationManagement.generateConsultationSlotId();

        System.out.println("Generated Consultation Slot ID : " + slotId);
    }
}