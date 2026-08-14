/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.MedicalRecordManagement;
import oodj_hospital_management_system.utils.Logger;

/**
 *
 * @author paure
 */
public class MedicalRecordController {
    private MedicalRecordManagement medicalRecordServices;
    private User activeUser;

    public MedicalRecordController(User activeUser) {
        this.activeUser = activeUser;
        this.medicalRecordServices = new MedicalRecordManagement(activeUser);
    }
    
    // Get All
    public List<MedicalAssessment> getAllMedicalAssessments() {
        return MedicalRecordManagement.getAllMedicalAssessments();
    }
 
    public List<LabResult> getAllLabResults() {
        return MedicalRecordManagement.getAllLabResults();
    }
 
    public List<Prescription> getAllPrescriptions() {
        return MedicalRecordManagement.getAllPrescriptions();
    }
 
    // Lookup by ID
    public MedicalAssessment getMedicalAssessmentById(String assessmentId) {
        return MedicalRecordManagement.getMedicalAssessmentById(assessmentId);
    }
 
    public LabResult getLabResultById(String resultId) {
        return MedicalRecordManagement.getLabResultById(resultId);
    }
 
    public Prescription getPrescriptionById(String prescriptionId) {
        return MedicalRecordManagement.getPrescriptionById(prescriptionId);
    }
 
    // Lookup by Consultation ID
    public MedicalAssessment getMedicalAssessmentByConsultationId(String consultationId) {
        return MedicalRecordManagement.getMedicalAssessmentByConsultationId(consultationId);
    }
 
    public List<LabResult> getLabResultsByConsultationId(String consultationId) {
        return MedicalRecordManagement.getLabResultsByConsultationId(consultationId);
    }
 
    public List<Prescription> getPrescriptionsByConsultationId(String consultationId) {
        return MedicalRecordManagement.getPrescriptionsByConsultationId(consultationId);
    }
 
    // Search and Sort
    public List<MedicalAssessment> searchMedicalAssessments(String keywords) {
        return medicalRecordServices.searchMedicalAssessments(keywords);
    }
 
    public List<MedicalAssessment> searchMedicalAssessments(String keywords, List<MedicalAssessment> assessmentList) {
        return medicalRecordServices.searchMedicalAssessments(keywords, assessmentList);
    }
 
    public List<MedicalAssessment> sortMedicalAssessmentsById(List<MedicalAssessment> list) {
        return medicalRecordServices.sortMedicalAssessmentsById(list);
    }
 
    public List<MedicalAssessment> sortMedicalAssessmentsByType(List<MedicalAssessment> list) {
        return medicalRecordServices.sortMedicalAssessmentsByType(list);
    }
 
    public List<MedicalAssessment> sortMedicalAssessmentsByDiagnosis(List<MedicalAssessment> list) {
        return medicalRecordServices.sortMedicalAssessmentsByDiagnosis(list);
    }
 
    // Search and Sort
    public List<LabResult> searchLabResults(String keywords) {
        return medicalRecordServices.searchLabResults(keywords);
    }
 
    public List<LabResult> searchLabResults(String keywords, List<LabResult> resultList) {
        return medicalRecordServices.searchLabResults(keywords, resultList);
    }
 
    public List<LabResult> sortLabResultsById(List<LabResult> list) {
        return medicalRecordServices.sortLabResultsById(list);
    }
 
    public List<LabResult> sortLabResultsByType(List<LabResult> list) {
        return medicalRecordServices.sortLabResultsByType(list);
    }
 
    public List<LabResult> sortLabResultsByDateTime(List<LabResult> list) {
        return medicalRecordServices.sortLabResultsByDateTime(list);
    }
 
    // Search and Sort
    public List<Prescription> searchPrescriptions(String keywords) {
        return medicalRecordServices.searchPrescriptions(keywords);
    }
 
    public List<Prescription> searchPrescriptions(String keywords, List<Prescription> prescriptionList) {
        return medicalRecordServices.searchPrescriptions(keywords, prescriptionList);
    }
 
    public List<Prescription> sortPrescriptionsById(List<Prescription> list) {
        return medicalRecordServices.sortPrescriptionsById(list);
    }
 
    public List<Prescription> sortPrescriptionsByMedicine(List<Prescription> list) {
        return medicalRecordServices.sortPrescriptionsByMedicine(list);
    }
 
    public List<Prescription> sortPrescriptionsByDosage(List<Prescription> list) {
        return medicalRecordServices.sortPrescriptionsByDosage(list);
    }
 
    // CRUD: Medical Assessment Management
    public boolean createMedicalAssessment(MedicalAssessment assessment) {
        boolean success = medicalRecordServices.createMedicalAssessment(assessment);
        if (success) {
            Logger.log(String.format("User {%s - %s - %s} created Medical Assessment {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    assessment != null ? assessment.getAssessmentId() : "?"));
        } else {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a Medical Assessment",
                    activeUser.getType(), activeUser.getId(), activeUser.getName()));
        }
        return success;
    }
 
    public LinkedHashMap<String, String> modifyAssessmentType(String newAssessmentType, String assessmentId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyAssessmentType(newAssessmentType, assessmentId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified assessment type for Medical Assessment {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assessmentId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify assessment type for Medical Assessment {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assessmentId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyFindings(String newFindings, String assessmentId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyFindings(newFindings, assessmentId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified findings for Medical Assessment {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assessmentId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify findings for Medical Assessment {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assessmentId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyDiagnosis(String newDiagnosis, String assessmentId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyDiagnosis(newDiagnosis, assessmentId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified diagnosis for Medical Assessment {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assessmentId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify diagnosis for Medical Assessment {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), assessmentId, msg.get("msg")));
        }
        return msg;
    }
 
    public boolean deleteMedicalAssessment(MedicalAssessment assessment) {
        boolean success = medicalRecordServices.deleteMedicalAssessment(assessment);
        if (success) {
            Logger.log(String.format("User {%s - %s - %s} deleted Medical Assessment {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    assessment != null ? assessment.getAssessmentId() : "?"));
        } else {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to delete Medical Assessment {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    assessment != null ? assessment.getAssessmentId() : "?"));
        }
        return success;
    }
 
    // CRUD: Lab Result Management
    public boolean createLabResult(LabResult result) {
        boolean success = medicalRecordServices.createLabResult(result);
        if (success) {
            Logger.log(String.format("User {%s - %s - %s} created Lab Result {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    result != null ? result.getResultId() : "?"));
        } else {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a Lab Result",
                    activeUser.getType(), activeUser.getId(), activeUser.getName()));
        }
        return success;
    }
 
    public LinkedHashMap<String, String> modifyLabResultTestType(String newTestType, String resultId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyLabResultTestType(newTestType, resultId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified test type for Lab Result {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), resultId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify test type for Lab Result {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), resultId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyLabResultResult(String newResult, String resultId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyLabResultResult(newResult, resultId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified result for Lab Result {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), resultId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify result for Lab Result {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), resultId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyLabResultDateTime(String newResultDateTime, String resultId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyLabResultDateTime(newResultDateTime, resultId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified result date/time for Lab Result {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), resultId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify result date/time for Lab Result {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), resultId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyLabResultRemarks(String newRemarks, String resultId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyLabResultRemarks(newRemarks, resultId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified remarks for Lab Result {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), resultId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify remarks for Lab Result {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), resultId, msg.get("msg")));
        }
        return msg;
    }
 
    public boolean deleteLabResult(LabResult result) {
        boolean success = medicalRecordServices.deleteLabResult(result);
        if (success) {
            Logger.log(String.format("User {%s - %s - %s} deleted Lab Result {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    result != null ? result.getResultId() : "?"));
        } else {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to delete Lab Result {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    result != null ? result.getResultId() : "?"));
        }
        return success;
    }
 
    // CRUD - Prescription
    public boolean createPrescription(Prescription prescription) {
        boolean success = medicalRecordServices.createPrescription(prescription);
        if (success) {
            Logger.log(String.format("User {%s - %s - %s} created Prescription {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    prescription != null ? prescription.getPrescriptionId() : "?"));
        } else {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a Prescription",
                    activeUser.getType(), activeUser.getId(), activeUser.getName()));
        }
        return success;
    }
 
    public LinkedHashMap<String, String> modifyPrescriptionMedicineName(String newMedicineName, String prescriptionId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyPrescriptionMedicineName(newMedicineName, prescriptionId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified medicine name for Prescription {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify medicine name for Prescription {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyPrescriptionDosage(String newDosage, String prescriptionId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyPrescriptionDosage(newDosage, prescriptionId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified dosage for Prescription {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify dosage for Prescription {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyPrescriptionFrequencyPerDay(String newFrequencyPerDay, String prescriptionId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyPrescriptionFrequencyPerDay(newFrequencyPerDay, prescriptionId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified frequency per day for Prescription {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify frequency per day for Prescription {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyPrescriptionDurationInDays(String newDurationInDays, String prescriptionId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyPrescriptionDurationInDays(newDurationInDays, prescriptionId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified duration (days) for Prescription {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify duration (days) for Prescription {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId, msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyPrescriptionInstructions(String newInstructions, String prescriptionId) {
        LinkedHashMap<String, String> msg = medicalRecordServices.modifyPrescriptionInstructions(newInstructions, prescriptionId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified instructions for Prescription {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify instructions for Prescription {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), prescriptionId, msg.get("msg")));
        }
        return msg;
    }
 
    public boolean deletePrescription(Prescription prescription) {
        boolean success = medicalRecordServices.deletePrescription(prescription);
        if (success) {
            Logger.log(String.format("User {%s - %s - %s} deleted Prescription {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    prescription != null ? prescription.getPrescriptionId() : "?"));
        } else {
            Logger.errorLog(String.format("User {%s - %s - %s} failed to delete Prescription {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    prescription != null ? prescription.getPrescriptionId() : "?"));
        }
        return success;
    }
}
