package TestServices;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.services.MedicalRecordManagement;
import oodj_hospital_management_system.services.UserManagement;

public class TestMedicalRecordManagement {

    private static MedicalRecordManagement recordMng;

    // NOTE: Update this to a valid consultation ID that actually exists
    // in your consultations.txt data file before running these tests.
    private static final String SAMPLE_CONSULTATION_ID = "#CON001";

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

        // Create MedicalRecordManagement once
        recordMng = new MedicalRecordManagement(manager);

        // Test loading
//        testLoadMedicalAssessments();
//        testLoadLabResults();
//        testLoadPrescriptions();

        // Test get by ID
//        testGetMedicalAssessmentById();
//        testGetLabResultById();
//        testGetPrescriptionById();

        // Test get by consultation ID
//        testGetMedicalAssessmentByConsultationId();
//        testGetLabResultsByConsultationId();
//        testGetPrescriptionsByConsultationId();

        // Test searching
//        testSearchMedicalAssessments();
//        testSearchMedicalAssessmentsFromGivenList();
//        testSearchLabResults();
//        testSearchLabResultsFromGivenList();
//        testSearchPrescriptions();
//        testSearchPrescriptionsFromGivenList();

        // Test sorting
//        testSortMedicalAssessmentsById();
//        testSortMedicalAssessmentsByType();
//        testSortMedicalAssessmentsByDiagnosis();
//        testSortLabResultsById();
//        testSortLabResultsByType();
//        testSortLabResultsByDateTime();
//        testSortPrescriptionsById();
//        testSortPrescriptionsByMedicine();
//        testSortPrescriptionsByDosage();

        // Test ID generation
//        testGenerateMedicalAssessmentId();
//        testGenerateLabResultId();
//        testGeneratePrescriptionId();

        // Test creation
        // Uncomment when needed because these modify the respective .txt files
//         testCreateMedicalAssessment();
//         testCreateLabResult();
         testCreatePrescription();

        // Test modification
        // Uncomment when needed because these modify the respective .txt files
//         testModifyMedicalAssessment();
//         testModifyLabResult();
//         testModifyPrescription();

        // Test deletion
        // Uncomment when needed because these modify the respective .txt files
//         testDeleteMedicalAssessment();
//         testDeleteLabResult();
//         testDeletePrescription();
    }

    // =========================================================
    // PRINT METHODS
    // =========================================================

    public static void testPrintMedicalAssessments(
            List<MedicalAssessment> assessmentList) {

        System.out.printf(
                "%-12s %-15s %-15s %-30s %-30s%n",
                "Assess. ID",
                "Consult. ID",
                "Type",
                "Findings",
                "Diagnosis"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (MedicalAssessment assessment : assessmentList) {

            System.out.printf(
                    "%-12s %-15s %-15s %-30s %-30s%n",
                    assessment.getAssessmentId(),
                    assessment.getConsultationId(),
                    assessment.getAssessmentType(),
                    assessment.getFindings(),
                    assessment.getDiagnosis()
            );
        }
    }

    public static void testPrintLabResults(List<LabResult> resultList) {

        System.out.printf(
                "%-12s %-15s %-15s %-15s %-20s %-20s%n",
                "Result ID",
                "Consult. ID",
                "Type",
                "Result",
                "Remarks",
                "Result DateTime"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (LabResult result : resultList) {

            System.out.printf(
                    "%-12s %-15s %-15s %-15s %-20s %-20s%n",
                    result.getResultId(),
                    result.getConsultationId(),
                    result.getResultType(),
                    result.getResult(),
                    result.getRemarks(),
                    result.getResultDateTime()
            );
        }
    }

    public static void testPrintPrescriptions(
            List<Prescription> prescriptionList) {

        System.out.printf(
                "%-12s %-15s %-20s %-10s %-10s %-10s %-25s%n",
                "Presc. ID",
                "Consult. ID",
                "Medicine",
                "Dosage",
                "Freq/Day",
                "Duration",
                "Instructions"
        );

        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );

        for (Prescription prescription : prescriptionList) {

            System.out.printf(
                    "%-12s %-15s %-20s %-10s %-10s %-10s %-25s%n",
                    prescription.getPrescriptionId(),
                    prescription.getConsultationId(),
                    prescription.getMedicineName(),
                    prescription.getDosage(),
                    prescription.getFrequencyPerDay(),
                    prescription.getDurationInDays(),
                    prescription.getInstructions()
            );
        }
    }

    // =========================================================
    // LOAD TESTS
    // =========================================================

    static void testLoadMedicalAssessments() {

        System.out.println("\n===== TEST LOAD MEDICAL ASSESSMENTS =====");

        List<MedicalAssessment> assessments =
                MedicalRecordManagement.getAllMedicalAssessments();

        System.out.println("Total Medical Assessments: " + assessments.size());

        testPrintMedicalAssessments(assessments);
    }

    static void testLoadLabResults() {

        System.out.println("\n===== TEST LOAD LAB RESULTS =====");

        List<LabResult> results =
                MedicalRecordManagement.getAllLabResults();

        System.out.println("Total Lab Results: " + results.size());

        testPrintLabResults(results);
    }

    static void testLoadPrescriptions() {

        System.out.println("\n===== TEST LOAD PRESCRIPTIONS =====");

        List<Prescription> prescriptions =
                MedicalRecordManagement.getAllPrescriptions();

        System.out.println("Total Prescriptions: " + prescriptions.size());

        testPrintPrescriptions(prescriptions);
    }

    // =========================================================
    // GET BY ID TESTS
    // =========================================================

    static void testGetMedicalAssessmentById() {

        System.out.println("\n===== TEST GET MEDICAL ASSESSMENT BY ID =====");

        List<MedicalAssessment> assessments =
                MedicalRecordManagement.getAllMedicalAssessments();

        if (assessments.isEmpty()) {
            System.out.println("No medical assessments available.");
            return;
        }

        String assessmentId = assessments.get(0).getAssessmentId();

        MedicalAssessment assessment =
                MedicalRecordManagement.getMedicalAssessmentById(assessmentId);

        if (assessment != null) {

            System.out.println("Assessment found:");
            System.out.println("Assessment ID : " + assessment.getAssessmentId());
            System.out.println("Type          : " + assessment.getAssessmentType());
            System.out.println("Diagnosis     : " + assessment.getDiagnosis());

        } else {
            System.out.println("Test failed: Assessment not found.");
        }
    }

    static void testGetLabResultById() {

        System.out.println("\n===== TEST GET LAB RESULT BY ID =====");

        List<LabResult> results =
                MedicalRecordManagement.getAllLabResults();

        if (results.isEmpty()) {
            System.out.println("No lab results available.");
            return;
        }

        String resultId = results.get(0).getResultId();

        LabResult result =
                MedicalRecordManagement.getLabResultById(resultId);

        if (result != null) {

            System.out.println("Lab Result found:");
            System.out.println("Result ID : " + result.getResultId());
            System.out.println("Type      : " + result.getResultType());
            System.out.println("Result    : " + result.getResult());

        } else {
            System.out.println("Test failed: Lab Result not found.");
        }
    }

    static void testGetPrescriptionById() {

        System.out.println("\n===== TEST GET PRESCRIPTION BY ID =====");

        List<Prescription> prescriptions =
                MedicalRecordManagement.getAllPrescriptions();

        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions available.");
            return;
        }

        String prescriptionId = prescriptions.get(0).getPrescriptionId();

        Prescription prescription =
                MedicalRecordManagement.getPrescriptionById(prescriptionId);

        if (prescription != null) {

            System.out.println("Prescription found:");
            System.out.println("Prescription ID : " + prescription.getPrescriptionId());
            System.out.println("Medicine        : " + prescription.getMedicineName());
            System.out.println("Dosage          : " + prescription.getDosage());

        } else {
            System.out.println("Test failed: Prescription not found.");
        }
    }

    // =========================================================
    // GET BY CONSULTATION ID TESTS
    // =========================================================

    static void testGetMedicalAssessmentByConsultationId() {

        System.out.println(
                "\n===== TEST GET MEDICAL ASSESSMENT BY CONSULTATION ID ====="
        );

        MedicalAssessment assessment =
                MedicalRecordManagement.getMedicalAssessmentByConsultationId(
                        SAMPLE_CONSULTATION_ID
                );

        if (assessment != null) {
            System.out.println("Assessment found for consultation " + SAMPLE_CONSULTATION_ID + ":");
            System.out.println("Assessment ID : " + assessment.getAssessmentId());
        } else {
            System.out.println(
                    "No assessment found for consultation " + SAMPLE_CONSULTATION_ID
            );
        }
    }

    static void testGetLabResultsByConsultationId() {

        System.out.println(
                "\n===== TEST GET LAB RESULTS BY CONSULTATION ID ====="
        );

        List<LabResult> results =
                MedicalRecordManagement.getLabResultsByConsultationId(
                        SAMPLE_CONSULTATION_ID
                );

        System.out.println(
                "Matched lab results for consultation " + SAMPLE_CONSULTATION_ID
                + ": " + results.size()
        );

        testPrintLabResults(results);
    }

    static void testGetPrescriptionsByConsultationId() {

        System.out.println(
                "\n===== TEST GET PRESCRIPTIONS BY CONSULTATION ID ====="
        );

        List<Prescription> prescriptions =
                MedicalRecordManagement.getPrescriptionsByConsultationId(
                        SAMPLE_CONSULTATION_ID
                );

        System.out.println(
                "Matched prescriptions for consultation " + SAMPLE_CONSULTATION_ID
                + ": " + prescriptions.size()
        );

        testPrintPrescriptions(prescriptions);
    }

    // =========================================================
    // SEARCH TESTS
    // =========================================================

    static void testSearchMedicalAssessments() {

        System.out.println("\n===== TEST SEARCH MEDICAL ASSESSMENTS =====");

        String keyword = "no";

        List<MedicalAssessment> result =
                recordMng.searchMedicalAssessments(keyword);

        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched assessments: " + result.size());

        testPrintMedicalAssessments(result);
    }

    static void testSearchMedicalAssessmentsFromGivenList() {

        System.out.println(
                "\n===== TEST SEARCH MEDICAL ASSESSMENTS FROM GIVEN LIST ====="
        );

        List<MedicalAssessment> assessments =
                MedicalRecordManagement.getAllMedicalAssessments();

        List<MedicalAssessment> result =
                recordMng.searchMedicalAssessments("cardiac", assessments);

        System.out.println("Search keyword: cardiac");
        System.out.println("Matched assessments: " + result.size());

        testPrintMedicalAssessments(result);
    }

    static void testSearchLabResults() {

        System.out.println("\n===== TEST SEARCH LAB RESULTS =====");

        String keyword = "blood";

        List<LabResult> result =
                recordMng.searchLabResults(keyword);

        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched lab results: " + result.size());

        testPrintLabResults(result);
    }

    static void testSearchLabResultsFromGivenList() {

        System.out.println(
                "\n===== TEST SEARCH LAB RESULTS FROM GIVEN LIST ====="
        );

        List<LabResult> results =
                MedicalRecordManagement.getAllLabResults();

        List<LabResult> result =
                recordMng.searchLabResults("normal", results);

        System.out.println("Search keyword: normal");
        System.out.println("Matched lab results: " + result.size());

        testPrintLabResults(result);
    }

    static void testSearchPrescriptions() {

        System.out.println("\n===== TEST SEARCH PRESCRIPTIONS =====");

        String keyword = "paracetamol";

        List<Prescription> result =
                recordMng.searchPrescriptions(keyword);

        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched prescriptions: " + result.size());

        testPrintPrescriptions(result);
    }

    static void testSearchPrescriptionsFromGivenList() {

        System.out.println(
                "\n===== TEST SEARCH PRESCRIPTIONS FROM GIVEN LIST ====="
        );

        List<Prescription> prescriptions =
                MedicalRecordManagement.getAllPrescriptions();

        List<Prescription> result =
                recordMng.searchPrescriptions("mg", prescriptions);

        System.out.println("Search keyword: mg");
        System.out.println("Matched prescriptions: " + result.size());

        testPrintPrescriptions(result);
    }

    // =========================================================
    // SORT TESTS
    // =========================================================

    static void testSortMedicalAssessmentsById() {

        System.out.println("\n===== TEST SORT MEDICAL ASSESSMENTS BY ID =====");

        List<MedicalAssessment> assessments =
                MedicalRecordManagement.getAllMedicalAssessments();

        List<MedicalAssessment> sorted =
                recordMng.sortMedicalAssessmentsById(assessments);

        testPrintMedicalAssessments(sorted);
    }

    static void testSortMedicalAssessmentsByType() {

        System.out.println("\n===== TEST SORT MEDICAL ASSESSMENTS BY TYPE =====");

        List<MedicalAssessment> assessments =
                MedicalRecordManagement.getAllMedicalAssessments();

        List<MedicalAssessment> sorted =
                recordMng.sortMedicalAssessmentsByType(assessments);

        testPrintMedicalAssessments(sorted);
    }

    static void testSortMedicalAssessmentsByDiagnosis() {

        System.out.println(
                "\n===== TEST SORT MEDICAL ASSESSMENTS BY DIAGNOSIS ====="
        );

        List<MedicalAssessment> assessments =
                MedicalRecordManagement.getAllMedicalAssessments();

        List<MedicalAssessment> sorted =
                recordMng.sortMedicalAssessmentsByDiagnosis(assessments);

        testPrintMedicalAssessments(sorted);
    }

    static void testSortLabResultsById() {

        System.out.println("\n===== TEST SORT LAB RESULTS BY ID =====");

        List<LabResult> results =
                MedicalRecordManagement.getAllLabResults();

        List<LabResult> sorted =
                recordMng.sortLabResultsById(results);

        testPrintLabResults(sorted);
    }

    static void testSortLabResultsByType() {

        System.out.println("\n===== TEST SORT LAB RESULTS BY TYPE =====");

        List<LabResult> results =
                MedicalRecordManagement.getAllLabResults();

        List<LabResult> sorted =
                recordMng.sortLabResultsByType(results);

        testPrintLabResults(sorted);
    }

    static void testSortLabResultsByDateTime() {

        System.out.println("\n===== TEST SORT LAB RESULTS BY DATETIME =====");

        List<LabResult> results =
                MedicalRecordManagement.getAllLabResults();

        List<LabResult> sorted =
                recordMng.sortLabResultsByDateTime(results);

        testPrintLabResults(sorted);
    }

    static void testSortPrescriptionsById() {

        System.out.println("\n===== TEST SORT PRESCRIPTIONS BY ID =====");

        List<Prescription> prescriptions =
                MedicalRecordManagement.getAllPrescriptions();

        List<Prescription> sorted =
                recordMng.sortPrescriptionsById(prescriptions);

        testPrintPrescriptions(sorted);
    }

    static void testSortPrescriptionsByMedicine() {

        System.out.println("\n===== TEST SORT PRESCRIPTIONS BY MEDICINE =====");

        List<Prescription> prescriptions =
                MedicalRecordManagement.getAllPrescriptions();

        List<Prescription> sorted =
                recordMng.sortPrescriptionsByMedicine(prescriptions);

        testPrintPrescriptions(sorted);
    }

    static void testSortPrescriptionsByDosage() {

        System.out.println("\n===== TEST SORT PRESCRIPTIONS BY DOSAGE =====");

        List<Prescription> prescriptions =
                MedicalRecordManagement.getAllPrescriptions();

        List<Prescription> sorted =
                recordMng.sortPrescriptionsByDosage(prescriptions);

        testPrintPrescriptions(sorted);
    }

    // =========================================================
    // GENERATE ID TESTS
    // =========================================================

    static void testGenerateMedicalAssessmentId() {

        System.out.println("\n===== TEST GENERATE MEDICAL ASSESSMENT ID =====");

        String assessmentId = MedicalRecordManagement.generateMedicalAssessmentId();

        System.out.println("Generated Assessment ID : " + assessmentId);
    }

    static void testGenerateLabResultId() {

        System.out.println("\n===== TEST GENERATE LAB RESULT ID =====");

        String resultId = MedicalRecordManagement.generateLabResultId();

        System.out.println("Generated Lab Result ID : " + resultId);
    }

    static void testGeneratePrescriptionId() {

        System.out.println("\n===== TEST GENERATE PRESCRIPTION ID =====");

        String prescriptionId = MedicalRecordManagement.generatePrescriptionId();

        System.out.println("Generated Prescription ID : " + prescriptionId);
    }

    // =========================================================
    // CREATE TESTS
    // =========================================================

    static void testCreateMedicalAssessment() {

        System.out.println("\n===== TEST CREATE MEDICAL ASSESSMENT =====");

        String newAssessmentId = MedicalRecordManagement.generateMedicalAssessmentId();

        MedicalAssessment assessment = new MedicalAssessment(
                newAssessmentId,
                SAMPLE_CONSULTATION_ID,
                "Physical Examination",
                "Mild swelling on left ankle",
                "Suspected sprain"
        );

        boolean success = recordMng.createMedicalAssessment(assessment);

        System.out.println("Success : " + success);
        System.out.println("New Assessment ID : " + newAssessmentId);
    }

    static void testCreateLabResult() {

        System.out.println("\n===== TEST CREATE LAB RESULT =====");

        String newResultId = MedicalRecordManagement.generateLabResultId();

        LabResult result = new LabResult(
                newResultId,
                SAMPLE_CONSULTATION_ID,
                "Blood Test",
                "Normal",
                "No abnormalities detected",
                "2026-08-16 10:30:00"
        );

        boolean success = recordMng.createLabResult(result);

        System.out.println("Success : " + success);
        System.out.println("New Result ID : " + newResultId);
    }

    static void testCreatePrescription() {

        System.out.println("\n===== TEST CREATE PRESCRIPTION =====");

        String newPrescriptionId = MedicalRecordManagement.generatePrescriptionId();

        Prescription prescription = new Prescription(
                newPrescriptionId,
                SAMPLE_CONSULTATION_ID,
                "Paracetamol",
                "500mg",
                "3",
                "5",
                "Take after meals"
        );

        boolean success = recordMng.createPrescription(prescription);

        System.out.println("Success : " + success);
        System.out.println("New Prescription ID : " + newPrescriptionId);
    }

    // =========================================================
    // MODIFY TESTS
    // =========================================================

    static void testModifyMedicalAssessment() {

        System.out.println("\n===== TEST MODIFY MEDICAL ASSESSMENT =====");

        List<MedicalAssessment> assessments =
                MedicalRecordManagement.getAllMedicalAssessments();

        if (assessments.isEmpty()) {
            System.out.println("No medical assessments available to modify.");
            return;
        }

        String assessmentId = assessments.get(0).getAssessmentId();

        LinkedHashMap<String, String> typeResult =
                recordMng.modifyAssessmentType("Follow-up Examination", assessmentId);
        System.out.println("Modify Type Success : " + typeResult.get("success"));
        System.out.println("Message             : " + typeResult.get("msg"));

        LinkedHashMap<String, String> findingsResult =
                recordMng.modifyFindings("Swelling reduced significantly", assessmentId);
        System.out.println("Modify Findings Success : " + findingsResult.get("success"));
        System.out.println("Message                 : " + findingsResult.get("msg"));

        LinkedHashMap<String, String> diagnosisResult =
                recordMng.modifyDiagnosis("Recovering well", assessmentId);
        System.out.println("Modify Diagnosis Success : " + diagnosisResult.get("success"));
        System.out.println("Message                  : " + diagnosisResult.get("msg"));
    }

    static void testModifyLabResult() {

        System.out.println("\n===== TEST MODIFY LAB RESULT =====");

        List<LabResult> results =
                MedicalRecordManagement.getAllLabResults();

        if (results.isEmpty()) {
            System.out.println("No lab results available to modify.");
            return;
        }

        String resultId = results.get(0).getResultId();

        LinkedHashMap<String, String> typeResult =
                recordMng.modifyLabResultTestType("Urine Test", resultId);
        System.out.println("Modify Test Type Success : " + typeResult.get("success"));
        System.out.println("Message                  : " + typeResult.get("msg"));

        LinkedHashMap<String, String> resultResult =
                recordMng.modifyLabResultResult("Abnormal", resultId);
        System.out.println("Modify Result Success : " + resultResult.get("success"));
        System.out.println("Message               : " + resultResult.get("msg"));

        LinkedHashMap<String, String> dateTimeResult =
                recordMng.modifyLabResultDateTime("2026-08-17 09:00:00", resultId);
        System.out.println("Modify DateTime Success : " + dateTimeResult.get("success"));
        System.out.println("Message                 : " + dateTimeResult.get("msg"));

        LinkedHashMap<String, String> remarksResult =
                recordMng.modifyLabResultRemarks("Recommend re-test in 2 weeks", resultId);
        System.out.println("Modify Remarks Success : " + remarksResult.get("success"));
        System.out.println("Message                : " + remarksResult.get("msg"));
    }

    static void testModifyPrescription() {

        System.out.println("\n===== TEST MODIFY PRESCRIPTION =====");

        List<Prescription> prescriptions =
                MedicalRecordManagement.getAllPrescriptions();

        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions available to modify.");
            return;
        }

        String prescriptionId = prescriptions.get(0).getPrescriptionId();

        LinkedHashMap<String, String> nameResult =
                recordMng.modifyPrescriptionMedicineName("Ibuprofen", prescriptionId);
        System.out.println("Modify Medicine Name Success : " + nameResult.get("success"));
        System.out.println("Message                      : " + nameResult.get("msg"));

        LinkedHashMap<String, String> dosageResult =
                recordMng.modifyPrescriptionDosage("200mg", prescriptionId);
        System.out.println("Modify Dosage Success : " + dosageResult.get("success"));
        System.out.println("Message               : " + dosageResult.get("msg"));

        LinkedHashMap<String, String> frequencyResult =
                recordMng.modifyPrescriptionFrequencyPerDay("2", prescriptionId);
        System.out.println("Modify Frequency Success : " + frequencyResult.get("success"));
        System.out.println("Message                  : " + frequencyResult.get("msg"));

        LinkedHashMap<String, String> durationResult =
                recordMng.modifyPrescriptionDurationInDays("7", prescriptionId);
        System.out.println("Modify Duration Success : " + durationResult.get("success"));
        System.out.println("Message                 : " + durationResult.get("msg"));

        LinkedHashMap<String, String> instructionsResult =
                recordMng.modifyPrescriptionInstructions("Take before meals", prescriptionId);
        System.out.println("Modify Instructions Success : " + instructionsResult.get("success"));
        System.out.println("Message                     : " + instructionsResult.get("msg"));
    }

    // =========================================================
    // DELETE TESTS
    // =========================================================

    static void testDeleteMedicalAssessment() {

        System.out.println("\n===== TEST DELETE MEDICAL ASSESSMENT =====");

        List<MedicalAssessment> assessments =
                MedicalRecordManagement.getAllMedicalAssessments();

        if (assessments.isEmpty()) {
            System.out.println("No medical assessments available to delete.");
            return;
        }

        MedicalAssessment target = assessments.get(assessments.size() - 1);

        System.out.println("Deleting Assessment : " + target.getAssessmentId());

        boolean success = recordMng.deleteMedicalAssessment(target);

        System.out.println("Success : " + success);
    }

    static void testDeleteLabResult() {

        System.out.println("\n===== TEST DELETE LAB RESULT =====");

        List<LabResult> results =
                MedicalRecordManagement.getAllLabResults();

        if (results.isEmpty()) {
            System.out.println("No lab results available to delete.");
            return;
        }

        LabResult target = results.get(results.size() - 1);

        System.out.println("Deleting Lab Result : " + target.getResultId());

        boolean success = recordMng.deleteLabResult(target);

        System.out.println("Success : " + success);
    }

    static void testDeletePrescription() {

        System.out.println("\n===== TEST DELETE PRESCRIPTION =====");

        List<Prescription> prescriptions =
                MedicalRecordManagement.getAllPrescriptions();

        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions available to delete.");
            return;
        }

        Prescription target = prescriptions.get(prescriptions.size() - 1);

        System.out.println("Deleting Prescription : " + target.getPrescriptionId());

        boolean success = recordMng.deletePrescription(target);

        System.out.println("Success : " + success);
    }
}