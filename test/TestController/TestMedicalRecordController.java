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
import oodj_hospital_management_system.controllers.MedicalRecordController;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;

public class TestMedicalRecordController {
    private static MedicalRecordController recordCtrl;
 
    public static void main(String[] args) {
 
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        if (adminStaff == null) {
            System.out.println("Test failed: Admin Staff '#ADMd4e8f' was not found.");
            return;
        }
 
        recordCtrl = new MedicalRecordController(adminStaff);
 
        // Test loading
//        testGetAllMedicalAssessments();
//        testGetAllLabResults();
//        testGetAllPrescriptions();
 
        // Test lookup
//        testGetMedicalAssessmentById();
//        testGetLabResultById();
//        testGetPrescriptionById();
//        testGetMedicalAssessmentByConsultationId();
//        testGetLabResultsByConsultationId();
//        testGetPrescriptionsByConsultationId();
 
        // Test search
//        testSearchMedicalAssessments();
//        testSearchLabResults();
//        testSearchPrescriptions();
 
        // Test sort
//        testSortMedicalAssessmentsById();
//        testSortLabResultsById();
//        testSortPrescriptionsById();
 
        // Test creation - Uncomment when needed, these modify the .txt files AND write to the log file
        // NOTE: you'll need to build a real MedicalAssessment/LabResult/Prescription object
        // (with a valid consultationId) before these will actually pass - see the TODOs inside.
//        testCreateMedicalAssessment();
//        testCreateLabResult();
//        testCreatePrescription();
 
        // Test modification (one representative field per entity - same pattern applies
        // to the other modifyXxx() methods on the controller)
//        testModifyDiagnosis();
//        testModifyLabResultResult();
//        testModifyPrescriptionDosage();
 
        // Test deletion
//        testDeleteMedicalAssessment();
//        testDeleteLabResult();
//        testDeletePrescription();
    }
 
    // =========================================================
    // LOAD TESTS
    // =========================================================
 
    static void testGetAllMedicalAssessments() {
 
        System.out.println("\n===== TEST GET ALL MEDICAL ASSESSMENTS (via Controller) =====");
 
        List<MedicalAssessment> assessments = recordCtrl.getAllMedicalAssessments();
 
        System.out.println("Total Assessments: " + assessments.size());
 
        for (MedicalAssessment a : assessments) {
            System.out.println(a.getAssessmentId() + " | " + a.getAssessmentType() + " | " + a.getDiagnosis());
        }
    }
 
    static void testGetAllLabResults() {
 
        System.out.println("\n===== TEST GET ALL LAB RESULTS (via Controller) =====");
 
        List<LabResult> results = recordCtrl.getAllLabResults();
 
        System.out.println("Total Lab Results: " + results.size());
 
        for (LabResult r : results) {
            System.out.println(r.getResultId() + " | " + r.getResultType() + " | " + r.getResult());
        }
    }
 
    static void testGetAllPrescriptions() {
 
        System.out.println("\n===== TEST GET ALL PRESCRIPTIONS (via Controller) =====");
 
        List<Prescription> prescriptions = recordCtrl.getAllPrescriptions();
 
        System.out.println("Total Prescriptions: " + prescriptions.size());
 
        for (Prescription p : prescriptions) {
            System.out.println(p.getPrescriptionId() + " | " + p.getMedicineName() + " | " + p.getDosage());
        }
    }
 
    // =========================================================
    // LOOKUP BY ID TESTS
    // =========================================================
 
    static void testGetMedicalAssessmentById() {
 
        System.out.println("\n===== TEST GET MEDICAL ASSESSMENT BY ID (via Controller) =====");
 
        List<MedicalAssessment> assessments = recordCtrl.getAllMedicalAssessments();
 
        if (assessments.isEmpty()) {
            System.out.println("No assessments available.");
            return;
        }
 
        String id = assessments.get(0).getAssessmentId();
        MedicalAssessment assessment = recordCtrl.getMedicalAssessmentById(id);
 
        System.out.println(assessment != null ? "Found: " + assessment.getAssessmentId() : "Test failed: not found.");
    }
 
    static void testGetLabResultById() {
 
        System.out.println("\n===== TEST GET LAB RESULT BY ID (via Controller) =====");
 
        List<LabResult> results = recordCtrl.getAllLabResults();
 
        if (results.isEmpty()) {
            System.out.println("No lab results available.");
            return;
        }
 
        String id = results.get(0).getResultId();
        LabResult result = recordCtrl.getLabResultById(id);
 
        System.out.println(result != null ? "Found: " + result.getResultId() : "Test failed: not found.");
    }
 
    static void testGetPrescriptionById() {
 
        System.out.println("\n===== TEST GET PRESCRIPTION BY ID (via Controller) =====");
 
        List<Prescription> prescriptions = recordCtrl.getAllPrescriptions();
 
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions available.");
            return;
        }
 
        String id = prescriptions.get(0).getPrescriptionId();
        Prescription prescription = recordCtrl.getPrescriptionById(id);
 
        System.out.println(prescription != null ? "Found: " + prescription.getPrescriptionId() : "Test failed: not found.");
    }
 
    // =========================================================
    // LOOKUP BY CONSULTATION ID TESTS
    // =========================================================
 
    static void testGetMedicalAssessmentByConsultationId() {
 
        System.out.println("\n===== TEST GET MEDICAL ASSESSMENT BY CONSULTATION ID (via Controller) =====");
 
        String consultationId = "#CON001"; // replace with a consultation that actually has an assessment
 
        List<MedicalAssessment> assessment = recordCtrl.getMedicalAssessmentsByConsultationId(consultationId);
 
//        System.out.println(assessment != null ? "Found: " + assessment.getAssessmentId() : "No assessment for " + consultationId);
    }
 
    static void testGetLabResultsByConsultationId() {
 
        System.out.println("\n===== TEST GET LAB RESULTS BY CONSULTATION ID (via Controller) =====");
 
        String consultationId = "#CON001";
 
        List<LabResult> results = recordCtrl.getLabResultsByConsultationId(consultationId);
 
        System.out.println("Lab results found: " + results.size());
    }
 
    static void testGetPrescriptionsByConsultationId() {
 
        System.out.println("\n===== TEST GET PRESCRIPTIONS BY CONSULTATION ID (via Controller) =====");
 
        String consultationId = "#CON001";
 
        List<Prescription> prescriptions = recordCtrl.getPrescriptionsByConsultationId(consultationId);
 
        System.out.println("Prescriptions found: " + prescriptions.size());
    }
 
    // =========================================================
    // SEARCH TESTS
    // =========================================================
 
    static void testSearchMedicalAssessments() {
 
        System.out.println("\n===== TEST SEARCH MEDICAL ASSESSMENTS (via Controller) =====");
 
        List<MedicalAssessment> result = recordCtrl.searchMedicalAssessments("Fever");
 
        System.out.println("Matched assessments: " + result.size());
    }
 
    static void testSearchLabResults() {
 
        System.out.println("\n===== TEST SEARCH LAB RESULTS (via Controller) =====");
 
        List<LabResult> result = recordCtrl.searchLabResults("Blood");
 
        System.out.println("Matched lab results: " + result.size());
    }
 
    static void testSearchPrescriptions() {
 
        System.out.println("\n===== TEST SEARCH PRESCRIPTIONS (via Controller) =====");
 
        List<Prescription> result = recordCtrl.searchPrescriptions("Paracetamol");
 
        System.out.println("Matched prescriptions: " + result.size());
    }
 
    // =========================================================
    // SORT TESTS
    // =========================================================
 
    static void testSortMedicalAssessmentsById() {
 
        System.out.println("\n===== TEST SORT MEDICAL ASSESSMENTS BY ID (via Controller) =====");
 
        List<MedicalAssessment> sorted = recordCtrl.sortMedicalAssessmentsById(recordCtrl.getAllMedicalAssessments());
 
        for (MedicalAssessment a : sorted) {
            System.out.println(a.getAssessmentId());
        }
    }
 
    static void testSortLabResultsById() {
 
        System.out.println("\n===== TEST SORT LAB RESULTS BY ID (via Controller) =====");
 
        List<LabResult> sorted = recordCtrl.sortLabResultsById(recordCtrl.getAllLabResults());
 
        for (LabResult r : sorted) {
            System.out.println(r.getResultId());
        }
    }
 
    static void testSortPrescriptionsById() {
 
        System.out.println("\n===== TEST SORT PRESCRIPTIONS BY ID (via Controller) =====");
 
        List<Prescription> sorted = recordCtrl.sortPrescriptionsById(recordCtrl.getAllPrescriptions());
 
        for (Prescription p : sorted) {
            System.out.println(p.getPrescriptionId());
        }
    }
 
    // =========================================================
    // CREATE TESTS
    // =========================================================
 
    static void testCreateMedicalAssessment() {
 
        System.out.println("\n===== TEST CREATE MEDICAL ASSESSMENT (via Controller) =====");
 
        // TODO: build a real MedicalAssessment object here matching your model's constructor,
        // e.g. new MedicalAssessment(newId, consultationId, assessmentType, findings, diagnosis)
        MedicalAssessment assessment = null;
 
        boolean success = recordCtrl.createMedicalAssessment(assessment);
 
        System.out.println("Success : " + success);
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    static void testCreateLabResult() {
 
        System.out.println("\n===== TEST CREATE LAB RESULT (via Controller) =====");
 
        // TODO: build a real LabResult object here matching your model's constructor
        LabResult result = null;
 
        boolean success = recordCtrl.createLabResult(result);
 
        System.out.println("Success : " + success);
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    static void testCreatePrescription() {
 
        System.out.println("\n===== TEST CREATE PRESCRIPTION (via Controller) =====");
 
        // TODO: build a real Prescription object here matching your model's constructor
        Prescription prescription = null;
 
        boolean success = recordCtrl.createPrescription(prescription);
 
        System.out.println("Success : " + success);
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    // =========================================================
    // MODIFY TESTS (representative - same pattern for the other modifyXxx() methods)
    // =========================================================
 
    static void testModifyDiagnosis() {
 
        System.out.println("\n===== TEST MODIFY DIAGNOSIS (via Controller) =====");
 
        List<MedicalAssessment> assessments = recordCtrl.getAllMedicalAssessments();
 
        if (assessments.isEmpty()) {
            System.out.println("No assessments available to modify.");
            return;
        }
 
        String assessmentId = assessments.get(0).getAssessmentId();
 
        LinkedHashMap<String, String> result = recordCtrl.modifyDiagnosis("Updated diagnosis via test", assessmentId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testModifyLabResultResult() {
 
        System.out.println("\n===== TEST MODIFY LAB RESULT (via Controller) =====");
 
        List<LabResult> results = recordCtrl.getAllLabResults();
 
        if (results.isEmpty()) {
            System.out.println("No lab results available to modify.");
            return;
        }
 
        String resultId = results.get(0).getResultId();
 
        LinkedHashMap<String, String> result = recordCtrl.modifyLabResultResult("Updated result via test", resultId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testModifyPrescriptionDosage() {
 
        System.out.println("\n===== TEST MODIFY PRESCRIPTION DOSAGE (via Controller) =====");
 
        List<Prescription> prescriptions = recordCtrl.getAllPrescriptions();
 
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions available to modify.");
            return;
        }
 
        String prescriptionId = prescriptions.get(0).getPrescriptionId();
 
        LinkedHashMap<String, String> result = recordCtrl.modifyPrescriptionDosage("500mg", prescriptionId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    // =========================================================
    // DELETE TESTS
    // =========================================================
 
    static void testDeleteMedicalAssessment() {
 
        System.out.println("\n===== TEST DELETE MEDICAL ASSESSMENT (via Controller) =====");
 
        List<MedicalAssessment> assessments = recordCtrl.getAllMedicalAssessments();
 
        if (assessments.isEmpty()) {
            System.out.println("No assessments available to delete.");
            return;
        }
 
        MedicalAssessment assessment = assessments.get(assessments.size() - 1);
 
        boolean success = recordCtrl.deleteMedicalAssessment(assessment);
 
        System.out.println("Success : " + success);
    }
 
    static void testDeleteLabResult() {
 
        System.out.println("\n===== TEST DELETE LAB RESULT (via Controller) =====");
 
        List<LabResult> results = recordCtrl.getAllLabResults();
 
        if (results.isEmpty()) {
            System.out.println("No lab results available to delete.");
            return;
        }
 
        LabResult result = results.get(results.size() - 1);
 
        boolean success = recordCtrl.deleteLabResult(result);
 
        System.out.println("Success : " + success);
    }
 
    static void testDeletePrescription() {
 
        System.out.println("\n===== TEST DELETE PRESCRIPTION (via Controller) =====");
 
        List<Prescription> prescriptions = recordCtrl.getAllPrescriptions();
 
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions available to delete.");
            return;
        }
 
        Prescription prescription = prescriptions.get(prescriptions.size() - 1);
 
        boolean success = recordCtrl.deletePrescription(prescription);
 
        System.out.println("Success : " + success);
    }
}
