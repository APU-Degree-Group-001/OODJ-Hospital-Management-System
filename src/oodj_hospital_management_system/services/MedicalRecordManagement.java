/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.FileHandler;

/**
 *
 * @author sheaw
 */
public class MedicalRecordManagement {
    private static final FileHandler assessmentHandler = new FileHandler(SystemConfiguration.medicalAssessmentFilePath);
    private static final FileHandler labResultHandler = new FileHandler(SystemConfiguration.labResultFilePath);
    private static final FileHandler prescriptionHandler = new FileHandler(SystemConfiguration.prescriptionFilePath);
    private static List<MedicalAssessment> medicalAssessments = new ArrayList<>();
    private User activeUser;
    
    public MedicalRecordManagement(User usr) {
        this.activeUser = usr;
    }
    
    public static void loadMedicalAssessments() {
        medicalAssessments.clear();
        List<LinkedHashMap<String, String>> rawData = assessmentHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
//            medicalAssessmentId, consultationId, doctorId, assessmentType, findings, diagnosis
//            MedicalAssessment assessment = new MedicalAssessment(
//                    row.get("medicalAssessmentId"), 
//                    ConsultationManagement.getConsultationById(row.get("consultationId")),
//                    UserManagement.getDoctorById(row.get("doctorId")), 
//                    row.get("assessmentType"),
//                    row.get("findings"), 
//                    row.get("diagnosis")
//            );
//            consultations.add(consultation);
        }
    }
    
    public static MedicalAssessment getMedicalAssessmentById(String assessmentId) {
        return null;
    }
    
    public static LabResult getLabResultById(String resultId) {
        return null;
    }
    
    public static Prescription getPrescriptionById(String prescriptionId) {
        return null;
    }
}
