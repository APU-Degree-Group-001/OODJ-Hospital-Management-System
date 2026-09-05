/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.exceptions.MedicalAssessmentException;
import oodj_hospital_management_system.utils.exceptions.LabResultException;
import oodj_hospital_management_system.utils.exceptions.PrescriptionException;

/**
 *
 * @author sheaw
 */

public class MedicalRecordManagement {

    private static final FileHandler assessmentHandler = new FileHandler(SystemConfiguration.medicalAssessmentFilePath);
    private static final FileHandler labResultHandler = new FileHandler(SystemConfiguration.labResultFilePath);
    private static final FileHandler prescriptionHandler = new FileHandler(SystemConfiguration.prescriptionFilePath);

    private static final List<MedicalAssessment> medicalAssessments = new ArrayList<>();
    private static final List<LabResult> labResults = new ArrayList<>();
    private static final List<Prescription> prescriptions = new ArrayList<>();

    private User activeUser;

    public MedicalRecordManagement(User usr) {
        this.activeUser = usr;
        loadMedicalAssessments();
        loadLabResults();
        loadPrescriptions();
    }

    public static void loadMedicalAssessments() {
        medicalAssessments.clear();
        List<LinkedHashMap<String, String>> rawData = assessmentHandler.readFile();

        for (LinkedHashMap<String, String> row : rawData) {
            // assessmentId, consultationId, assessmentType, findings, diagnosis
            MedicalAssessment assessment = new MedicalAssessment(
                    row.get("assessmentId"), 
                    row.get("consultationId"), 
                    row.get("assessmentType"), 
                    row.get("findings"), 
                    row.get("diagnosis")
            );
            medicalAssessments.add(assessment);
        }
    }

    public static void loadLabResults() {
        labResults.clear();
        List<LinkedHashMap<String, String>> rawData = labResultHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // resultId, consultationId, resultType, result, remarks, resultDateTime
            LabResult result = new LabResult(
                    row.get("resultId"), 
                    row.get("consultationId"), 
                    row.get("resultType"), 
                    row.get("result"), 
                    row.get("remarks"), 
                    row.get("resultDateTime")
            );
            labResults.add(result);
        }
    }

    public static void loadPrescriptions() {
        prescriptions.clear();
        List<LinkedHashMap<String, String>> rawData = prescriptionHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // prescriptionId, consultationId, medicineName, dosage, frequencyPerDay, durationInDays, instructions
            Prescription prescription = new Prescription(
                    row.get("prescriptionId"), 
                    row.get("consultationId"), 
                    row.get("medicineName"), 
                    row.get("dosage"), 
                    row.get("frequencyPerDay"), 
                    row.get("durationInDays"), 
                    row.get("instructions"));
            prescriptions.add(prescription);
        }
    }

    public static List<MedicalAssessment> getAllMedicalAssessments() {
        loadMedicalAssessments();
        return medicalAssessments;
    }

    public static List<LabResult> getAllLabResults() {
        loadLabResults();
        return labResults;
    }

    public static List<Prescription> getAllPrescriptions() {
        loadPrescriptions();
        return prescriptions;
    }

    public static MedicalAssessment getMedicalAssessmentById(String assessmentId) {
        loadMedicalAssessments();
        try {
            if (!medicalAssessments.stream().anyMatch(assessment -> assessment.getAssessmentId().equals(assessmentId))) {
                throw new MedicalAssessmentException(String.format("Invalid Medical Assessment ID: Assessment ID '%s' is not found", assessmentId));
            }
            for (MedicalAssessment assessment : medicalAssessments) {
                if (assessment.getAssessmentId().equals(assessmentId)) {
                    return assessment;
                }
            }
            return null;
        } catch (MedicalAssessmentException e) {
            System.out.println("Error finding medical assessment -> " + e.getMessage());
            Logger.errorLog("Error finding medical assessment -> " + e.getMessage());
            return null;
        }
    }

    public static LabResult getLabResultById(String resultId) {
        loadLabResults();
        try {
            if (!labResults.stream().anyMatch(result -> result.getResultId().equals(resultId))) {
                throw new LabResultException(String.format("Invalid Lab Result ID: Result ID '%s' is not found", resultId));
            }
            for (LabResult result : labResults) {
                if (result.getResultId().equals(resultId)) {
                    return result;
                }
            }
            return null;
        } catch (LabResultException e) {
            System.out.println("Error finding lab result -> " + e.getMessage());
            Logger.errorLog("Error finding lab result -> " + e.getMessage());
            return null;
        }
    }

    public static Prescription getPrescriptionById(String prescriptionId) {
        loadPrescriptions();
        try {
            if (!prescriptions.stream().anyMatch(prescription -> prescription.getPrescriptionId().equals(prescriptionId))) {
                throw new PrescriptionException(String.format("Invalid Prescription ID: Prescription ID '%s' is not found", prescriptionId));
            }
            for (Prescription prescription : prescriptions) {
                if (prescription.getPrescriptionId().equals(prescriptionId)) {
                    return prescription;
                }
            }
            return null;
        } catch (PrescriptionException e) {
            System.out.println("Error finding prescription -> " + e.getMessage());
            Logger.errorLog("Error finding prescription -> " + e.getMessage());
            return null;
        }
    }
    
    public static List<MedicalAssessment> getMedicalAssessmentsByConsultationId(String consultationId) {
        loadMedicalAssessments();
        List<MedicalAssessment> matchedAssessments = new ArrayList<>();
        for (MedicalAssessment assessment : medicalAssessments) {
            if (assessment.getConsultationId().equals(consultationId)) {
                matchedAssessments.add(assessment);
            }
        }
        return matchedAssessments;
    }
    
    public static List<LabResult> getLabResultsByConsultationId(String consultationId) {
        loadLabResults();

        List<LabResult> matchedResults = new ArrayList<>();

        for (LabResult labResult : labResults) {
            if (labResult.getConsultationId().equals(consultationId)) {
                matchedResults.add(labResult);
            }
        }

        return matchedResults;
    }
    
    public static List<Prescription> getPrescriptionsByConsultationId(String consultationId) {
        loadPrescriptions();

        List<Prescription> matchedPrescriptions = new ArrayList<>();

        for (Prescription prescription : prescriptions) {
            if (prescription.getConsultationId().equals(consultationId)) {
                matchedPrescriptions.add(prescription);
            }
        }

        return matchedPrescriptions;
    }

    // File Handler (Write and Append)

    private static boolean writeMedicalAssessments() {
        ArrayList<LinkedHashMap<String, String>> assessments = new ArrayList<>();
        for (MedicalAssessment assessment : medicalAssessments) {
            assessments.add(assessment.getMedicalAssessmentMap());
        }
        assessmentHandler.writeFile(assessments);
        return true;
    }

    private static boolean appendMedicalAssessment(MedicalAssessment assessment) {
        try {
            ArrayList<LinkedHashMap<String, String>> assessments = new ArrayList<>();
            assessments.add(assessment.getMedicalAssessmentMap());
            if (!assessmentHandler.checkFileExistence() && !assessmentHandler.createFile()) {
                System.out.printf("File 'medicalAssessments.txt' failed to be created, assessment '%s' cannot be appended%n", assessment.getAssessmentId());
                return false;
            }
            assessmentHandler.appendFile(assessments);
            System.out.printf("Medical Assessment '%s' is appended to medicalAssessments.txt!%n", assessment.getAssessmentId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new medical assessment: " + e.getMessage());
            Logger.errorLog("Error appending new medical assessment (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }

    private static boolean writeLabResults() {
        ArrayList<LinkedHashMap<String, String>> results = new ArrayList<>();
        for (LabResult result : labResults) {
            results.add(result.getLabResultMap());
        }
        labResultHandler.writeFile(results);
        return true;
    }

    private static boolean appendLabResult(LabResult result) {
        try {
            ArrayList<LinkedHashMap<String, String>> results = new ArrayList<>();
            results.add(result.getLabResultMap());
            if (!labResultHandler.checkFileExistence() && !labResultHandler.createFile()) {
                System.out.printf("File 'labResults.txt' failed to be created, lab result '%s' cannot be appended%n", result.getResultId());
                return false;
            }
            labResultHandler.appendFile(results);
            System.out.printf("Lab Result '%s' is appended to labResults.txt!%n", result.getResultId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new lab result: " + e.getMessage());
            Logger.errorLog("Error appending new lab result (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }

    private static boolean writePrescriptions() {
        ArrayList<LinkedHashMap<String, String>> prescriptionData = new ArrayList<>();
        for (Prescription prescription : prescriptions) {
            prescriptionData.add(prescription.getPrescriptionMap());
        }
        prescriptionHandler.writeFile(prescriptionData);
        return true;
    }

    private static boolean appendPrescription(Prescription prescription) {
        try {
            ArrayList<LinkedHashMap<String, String>> prescriptionData = new ArrayList<>();
            prescriptionData.add(prescription.getPrescriptionMap());
            if (!prescriptionHandler.checkFileExistence() && !prescriptionHandler.createFile()) {
                System.out.printf("File 'prescriptions.txt' failed to be created, prescription '%s' cannot be appended%n", prescription.getPrescriptionId());
                return false;
            }
            prescriptionHandler.appendFile(prescriptionData);
            System.out.printf("Prescription '%s' is appended to prescriptions.txt!%n", prescription.getPrescriptionId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new prescription: " + e.getMessage());
            Logger.errorLog("Error appending new prescription (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }
    
    // Medical Assessment Management
    public boolean createMedicalAssessment(MedicalAssessment assessment) {
        try {
            if (assessment == null) {
                throw new MedicalAssessmentException("Medical assessment cannot be null");
            }

            medicalAssessments.add(assessment);

            if (assessmentHandler.checkFileExistence()) {
                appendMedicalAssessment(assessment);
            } else {
                writeMedicalAssessments();
            }

            loadMedicalAssessments();

            System.out.printf(
                    "Medical Assessment '%s' is created successfully!%n",
                    assessment.getAssessmentId()
            );

            return true;

        } catch (MedicalAssessmentException e) {
            System.out.println("Error creating medical assessment -> " + e.getMessage());
            Logger.errorLog("Error creating medical assessment -> " + e.getMessage());
            return false;

        } catch (Exception e) {
            System.out.println("Error creating medical assessment (System Error) -> " + e.getMessage());
            Logger.errorLog("Error creating medical assessment (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }
    
    public LinkedHashMap<String, String> modifyAssessmentType(String newAssessmentType, String assessmentId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        MedicalAssessment assessment = getMedicalAssessmentById(assessmentId);

        if (assessment == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Medical Assessment '%s' is not found", assessmentId));
            return msgMap;
        }

        if (newAssessmentType == null || newAssessmentType.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Assessment type cannot be null or empty.");
            return msgMap;
        }

        assessment.setAssessmentType(newAssessmentType);
        writeMedicalAssessments();
        loadMedicalAssessments();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Medical Assessment '%s' assessment type is modified!", assessmentId));

        return msgMap;
    }

    public LinkedHashMap<String, String> modifyFindings(String newFindings, String assessmentId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        MedicalAssessment assessment = getMedicalAssessmentById(assessmentId);

        if (assessment == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Medical Assessment '%s' is not found", assessmentId));
            return msgMap;
        }

        if (newFindings == null || newFindings.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Findings cannot be null or empty.");
            return msgMap;
        }

        assessment.setFindings(newFindings);
        writeMedicalAssessments();
        loadMedicalAssessments();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Medical Assessment '%s' findings is modified!", assessmentId));

        return msgMap;
    }

    public LinkedHashMap<String, String> modifyDiagnosis(String newDiagnosis, String assessmentId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        MedicalAssessment assessment = getMedicalAssessmentById(assessmentId);

        if (assessment == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Medical Assessment '%s' is not found", assessmentId));
            return msgMap;
        }

        if (newDiagnosis == null || newDiagnosis.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Diagnosis cannot be null or empty.");
            return msgMap;
        }

        assessment.setDiagnosis(newDiagnosis);
        writeMedicalAssessments();
        loadMedicalAssessments();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Medical Assessment '%s' diagnosis is modified!", assessmentId));

        return msgMap;
    }

    public boolean deleteMedicalAssessment(MedicalAssessment assessment) {
        try {
            if (assessment == null || !medicalAssessments.contains(assessment)) {
                throw new MedicalAssessmentException(
                        "Medical assessment is not found"
                );
            }

            medicalAssessments.remove(assessment);
            writeMedicalAssessments();
            loadMedicalAssessments();

            System.out.printf(
                    "Medical Assessment '%s' is deleted successfully!%n",
                    assessment.getAssessmentId()
            );

            return true;

        } catch (MedicalAssessmentException e) {
            System.out.println("Error deleting medical assessment -> " + e.getMessage());
            Logger.errorLog("Error deleting medical assessment -> " + e.getMessage());
            return false;

        } catch (Exception e) {
            System.out.println("Error deleting medical assessment (System Error) -> " + e.getMessage());
            Logger.errorLog("Error deleting medical assessment (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }


    // Lab Result Management
    public boolean createLabResult(LabResult result) {
        try {
            if (result == null) {
                throw new LabResultException("Lab result cannot be null");
            }

            labResults.add(result);

            if (labResultHandler.checkFileExistence()) {
                appendLabResult(result);
            } else {
                writeLabResults();
            }

            loadLabResults();

            System.out.printf(
                    "Lab Result '%s' is created successfully!%n",
                    result.getResultId()
            );

            return true;

        } catch (LabResultException e) {
            System.out.println("Error creating lab result -> " + e.getMessage());
            Logger.errorLog("Error creating lab result -> " + e.getMessage());
            return false;

        } catch (Exception e) {
            System.out.println("Error creating lab result (System Error) -> " + e.getMessage());
            Logger.errorLog("Error creating lab result (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }
    
    public LinkedHashMap<String, String> modifyLabResultTestType(String newTestType, String resultId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        LabResult result = getLabResultById(resultId);

        if (result == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Lab Result '%s' is not found", resultId));
            return msgMap;
        }

        result.setResultType(newTestType);
        writeLabResults();
        loadLabResults();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Lab Result '%s' test type is modified!", resultId));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyLabResultResult(String newResult, String resultId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        LabResult result = getLabResultById(resultId);

        if (result == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Lab Result '%s' is not found", resultId));
            return msgMap;
        }

        result.setResult(newResult);
        writeLabResults();
        loadLabResults();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Lab Result '%s' result is modified!", resultId));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyLabResultDateTime(String newResultDateTime, String resultId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        LabResult result = getLabResultById(resultId);

        if (result == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Lab Result '%s' is not found", resultId));
            return msgMap;
        }

        result.setResultDateTime(newResultDateTime);
        writeLabResults();
        loadLabResults();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Lab Result '%s' result date/time is modified!", resultId));
        return msgMap;
    }
    
    public LinkedHashMap<String, String> modifyLabResultRemarks(String newRemarks, String resultId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        LabResult result = getLabResultById(resultId);

        if (result == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Lab Result '%s' is not found", resultId));
            return msgMap;
        }

        result.setRemarks(newRemarks);
        writeLabResults();
        loadLabResults();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Lab Result '%s' remarks is modified!", resultId));

        return msgMap;
    }

    public boolean deleteLabResult(LabResult result) {
        try {
            if (result == null || !labResults.contains(result)) {
                throw new LabResultException(
                        "Lab result is not found"
                );
            }

            labResults.remove(result);
            writeLabResults();
            loadLabResults();

            System.out.printf(
                    "Lab Result '%s' is deleted successfully!%n",
                    result.getResultId()
            );

            return true;

        } catch (LabResultException e) {
            System.out.println("Error deleting lab result -> " + e.getMessage());
            Logger.errorLog("Error deleting lab result -> " + e.getMessage());
            return false;

        } catch (Exception e) {
            System.out.println("Error deleting lab result (System Error) -> " + e.getMessage());
            Logger.errorLog("Error deleting lab result (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }


    // Prescription Management
    public boolean createPrescription(Prescription prescription) {
        try {
            if (prescription == null) {
                throw new PrescriptionException("Prescription cannot be null");
            }

            prescriptions.add(prescription);

            if (prescriptionHandler.checkFileExistence()) {
                appendPrescription(prescription);
            } else {
                writePrescriptions();
            }

            loadPrescriptions();

            System.out.printf(
                    "Prescription '%s' is created successfully!%n",
                    prescription.getPrescriptionId()
            );

            return true;

        } catch (PrescriptionException e) {
            System.out.println("Error creating prescription -> " + e.getMessage());
            Logger.errorLog("Error creating prescription -> " + e.getMessage());
            return false;

        } catch (Exception e) {
            System.out.println("Error creating prescription (System Error) -> " + e.getMessage());
            Logger.errorLog("Error creating prescription (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }
    
    public LinkedHashMap<String, String> modifyPrescriptionMedicineName(String newMedicineName, String prescriptionId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Prescription prescription = getPrescriptionById(prescriptionId);

        if (prescription == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Prescription '%s' is not found", prescriptionId));
            return msgMap;
        }

        prescription.setMedicineName(newMedicineName);
        writePrescriptions();
        loadPrescriptions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription '%s' medicine name is modified!", prescriptionId));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyPrescriptionDosage(String newDosage, String prescriptionId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Prescription prescription = getPrescriptionById(prescriptionId);

        if (prescription == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Prescription '%s' is not found", prescriptionId));
            return msgMap;
        }

        prescription.setDosage(newDosage);
        writePrescriptions();
        loadPrescriptions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription '%s' dosage is modified!", prescriptionId));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyPrescriptionFrequencyPerDay(String newFrequencyPerDay, String prescriptionId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Prescription prescription = getPrescriptionById(prescriptionId);

        if (prescription == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Prescription '%s' frequency is not found!", prescriptionId));
            return msgMap;
        }

        prescription.setFrequencyPerDay(newFrequencyPerDay);
        writePrescriptions();
        loadPrescriptions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription '%s' frequency is modified!", prescriptionId));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyPrescriptionDurationInDays(String newDurationInDays, String prescriptionId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Prescription prescription = getPrescriptionById(prescriptionId);

        if (prescription == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Prescription '%s' is not found", prescriptionId));
            return msgMap;
        }

        prescription.setDurationInDays(newDurationInDays);
        writePrescriptions();
        loadPrescriptions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription '%s' duration is modified!", prescriptionId));
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyPrescriptionInstructions(String newInstructions, String prescriptionId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Prescription prescription = getPrescriptionById(prescriptionId);

        if (prescription == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Prescription '%s' instructions is not found!", prescriptionId));
            return msgMap;
        }

        prescription.setInstructions(newInstructions);
        writePrescriptions();
        loadPrescriptions();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Prescription '%s' instructions are modified!", prescriptionId));
        return msgMap;
    }

    public boolean deletePrescription(Prescription prescription) {
        try {
            if (prescription == null || !prescriptions.contains(prescription)) {
                throw new PrescriptionException(
                        "Prescription is not found"
                );
            }

            prescriptions.remove(prescription);
            writePrescriptions();
            loadPrescriptions();

            System.out.printf(
                    "Prescription '%s' is deleted successfully!%n",
                    prescription.getPrescriptionId()
            );

            return true;

        } catch (PrescriptionException e) {
            System.out.println("Error deleting prescription -> " + e.getMessage());
            Logger.errorLog("Error deleting prescription -> " + e.getMessage());
            return false;

        } catch (Exception e) {
            System.out.println("Error deleting prescription (System Error) -> " + e.getMessage());
            Logger.errorLog("Error deleting prescription (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }
    
    // Searching and Sorting
    public List<MedicalAssessment> searchMedicalAssessments(String keywords) {
        List<MedicalAssessment> matchedAssessments = new ArrayList<>();
        for (MedicalAssessment assessment : medicalAssessments) {
            if (assessment.getAssessmentId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || assessment.getAssessmentType().toLowerCase().contains(keywords.toLowerCase().trim())
                    || assessment.getFindings().toLowerCase().contains(keywords.toLowerCase().trim())
                    || assessment.getDiagnosis().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedAssessments.add(assessment);
            }
        }
        return matchedAssessments;
    }

    public List<MedicalAssessment> searchMedicalAssessments(String keywords, List<MedicalAssessment> assessmentList) {
        List<MedicalAssessment> matchedAssessments = new ArrayList<>();
        for (MedicalAssessment assessment : assessmentList) {
            if (assessment.getAssessmentId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || assessment.getAssessmentType().toLowerCase().contains(keywords.toLowerCase().trim())
                    || assessment.getFindings().toLowerCase().contains(keywords.toLowerCase().trim())
                    || assessment.getDiagnosis().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedAssessments.add(assessment);
            }
        }
        return matchedAssessments;
    }

    public List<LabResult> searchLabResults(String keywords) {
        List<LabResult> matchedResults = new ArrayList<>();
        for (LabResult result : labResults) {
            if (result.getResultId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || result.getResultType().toLowerCase().contains(keywords.toLowerCase().trim())
                    || result.getRemarks().toLowerCase().contains(keywords.toLowerCase().trim())
                    || result.getResultDateTime().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedResults.add(result);
            }
        }
        return matchedResults;
    }

    public List<LabResult> searchLabResults(String keywords, List<LabResult> resultList) {
        List<LabResult> matchedResults = new ArrayList<>();
        for (LabResult result : resultList) {
            if (result.getResultId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || result.getResultType().toLowerCase().contains(keywords.toLowerCase().trim())
                    || result.getResult().toLowerCase().contains(keywords.toLowerCase().trim())
                    || result.getRemarks().toLowerCase().contains(keywords.toLowerCase().trim())
                    || result.getResultDateTime().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedResults.add(result);
            }
        }
        return matchedResults;
    }

    public List<Prescription> searchPrescriptions(String keywords) {
        List<Prescription> matchedPrescriptions = new ArrayList<>();
        for (Prescription prescription : prescriptions) {
            if (prescription.getPrescriptionId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getMedicineName().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getDosage().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getFrequencyPerDay().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getDurationInDays().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getInstructions().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedPrescriptions.add(prescription);
            }
        }
        return matchedPrescriptions;
    }

    public List<Prescription> searchPrescriptions(String keywords, List<Prescription> prescriptionList) {
        List<Prescription> matchedPrescriptions = new ArrayList<>();
        for (Prescription prescription : prescriptionList) {
            if (prescription.getPrescriptionId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getMedicineName().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getDosage().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getFrequencyPerDay().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getDurationInDays().toLowerCase().contains(keywords.toLowerCase().trim())
                    || prescription.getInstructions().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedPrescriptions.add(prescription);
            }
        }
        return matchedPrescriptions;
    }

    public List<MedicalAssessment> sortMedicalAssessmentsById(List<MedicalAssessment> list) {
        List<MedicalAssessment> sortedAssessments = list;
        sortedAssessments.sort(Comparator.comparing(MedicalAssessment::getAssessmentId));
        return sortedAssessments;
    }

    public List<MedicalAssessment> sortMedicalAssessmentsByType(List<MedicalAssessment> list) {
        List<MedicalAssessment> sortedAssessments = list;
        sortedAssessments.sort(Comparator.comparing(MedicalAssessment::getAssessmentType));
        return sortedAssessments;
    }

    public List<MedicalAssessment> sortMedicalAssessmentsByDiagnosis(List<MedicalAssessment> list) {
        List<MedicalAssessment> sortedAssessments = list;
        sortedAssessments.sort(Comparator.comparing(MedicalAssessment::getDiagnosis));
        return sortedAssessments;
    }

    public List<LabResult> sortLabResultsById(List<LabResult> list) {
        List<LabResult> sortedResults = list;
        sortedResults.sort(Comparator.comparing(LabResult::getResultId));
        return sortedResults;
    }

    public List<LabResult> sortLabResultsByType(List<LabResult> list) {
        List<LabResult> sortedResults = list;
        sortedResults.sort(Comparator.comparing(LabResult::getResultType));
        return sortedResults;
    }

    public List<LabResult> sortLabResultsByDateTime(List<LabResult> list) {
        List<LabResult> sortedResults = list;
        sortedResults.sort(Comparator.comparing(LabResult::getResultDateTime));
        return sortedResults;
    }

    public List<Prescription> sortPrescriptionsById(List<Prescription> list) {
        List<Prescription> sortedPrescriptions = list;
        sortedPrescriptions.sort(Comparator.comparing(Prescription::getPrescriptionId));
        return sortedPrescriptions;
    }

    public List<Prescription> sortPrescriptionsByMedicine(List<Prescription> list) {
        List<Prescription> sortedPrescriptions = list;
        sortedPrescriptions.sort(Comparator.comparing(Prescription::getMedicineName));
        return sortedPrescriptions;
    }

    public List<Prescription> sortPrescriptionsByDosage(List<Prescription> list) {
        List<Prescription> sortedPrescriptions = list;
        sortedPrescriptions.sort(Comparator.comparing(Prescription::getDosage));
        return sortedPrescriptions;
    }

    public static String generateMedicalAssessmentId() {
        List<String> existingIds = medicalAssessments.stream().map(MedicalAssessment::getAssessmentId).toList();
        return SystemConfiguration.generateId("#MED", existingIds);
    }

    public static String generateLabResultId() {
        List<String> existingIds = labResults.stream().map(LabResult::getResultId).toList();
        return SystemConfiguration.generateId("#LAB", existingIds);
    }

    public static String generatePrescriptionId() {
        List<String> existingIds = prescriptions.stream().map(Prescription::getPrescriptionId).toList();
        return SystemConfiguration.generateId("#PRE", existingIds);
    }
}

