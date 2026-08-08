/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.Feedback;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.FileHandler;

/**
 *
 * @author sheaw
 */

//private String consultationId;
//private ConsultationSlot consultationSlot;
//private Patient patient;
//private String description;
//private MedicalAssessment medicalAssessment;
//private ArrayList<LabResult> labResults;
//private ArrayList<Prescription> prescriptions;
//private HospitalAssetAllocation hospitalAssetAllocation;
//private Feedback feedback;
//private Bill bill;

public class ConsultationManagement {
    private static final FileHandler handler = new FileHandler(SystemConfiguration.consultationFilePath);
    private static final List<Consultation> consultations = new ArrayList<>();
    private static final List<ConsultationSlot> consultationSlots = new ArrayList<>();
    private User activeUser;
    
    public ConsultationManagement(User user) {
        this.activeUser = user;
        loadConsultations();
    }
    
    public void loadConsultations() {
        consultations.clear();
        List<LinkedHashMap<String, String>> rawData = handler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
//            consultationId, consultationSlotId, patientId, description, medicalAssessmentId, labResultIds, prescriptionIds, hospitalAssetAllocationId, feedbackId, billId, status
            Consultation consultation = new Consultation(
                    row.get("consultationId"), 
                    getConsultationSlotById(row.get("consultationSlotId")),
                    UserManagement.getPatientById(row.get("patientId")), 
                    row.get("description"),
                    MedicalRecordManagement.getMedicalAssessmentById(row.get("medicalAssessmentId")),
                    getLabResults(row.get("labResultIds")),
                    getPrescriptions(row.get("prescriptionIds")),
                    HospitalAssetManagement.getHospitalAssetAllocationById(row.get("hospitalAssetAllocationId")),
                    getFeedback(row.get("feedbackId")),
                    BillingManagement.getBillById(row.get("billId")),
                    getConsultationStatus(row.get("status"))
            );
            consultations.add(consultation);
        }
    }
    
    public void loadConsultationSlots() {
        consultationSlots.clear();
        List<LinkedHashMap<String, String>> rawData = handler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // consultationSlotId, doctorShiftId, startDateTime, endDateTime
            ConsultationSlot consultationSlot = new ConsultationSlot(
                    row.get("consultationSlotId"),
                    DoctorManagement.getDoctorShiftById(row.get("doctorShiftId")),
                    row.get("startDateTime"),
                    row.get("endDateTime")
            );
            consultationSlots.add(consultationSlot);
        }
    }
    
    public static List<Consultation> getAllConsultations() {
        return consultations;
    }
    
    public static List<ConsultationSlot> getAllConsultationSlots() {
        return consultationSlots;
    }
    
    public static ConsultationSlot getConsultationSlotById(String consultationSlotId) {
        return null;
    }
    
    public static Feedback getFeedback(String feedbackId) {
        return null;
    }
    
    public List<MedicalAssessment> getMedicalAssessments(String assessmentIds) {
        String[] ids = assessmentIds.split(",");
        List<MedicalAssessment> assessment = new ArrayList<>();
        for (String id : ids) {
            id = id.trim();
            assessment.add(MedicalRecordManagement.getMedicalAssessmentById(id));
        }
        return assessment;
    }
    
    public List<LabResult> getLabResults(String labResultIds) {
        String[] ids = labResultIds.split(",");
        List<LabResult> labResults = new ArrayList<>();
        for (String id : ids) {
            id = id.trim();
            labResults.add(MedicalRecordManagement.getLabResultById(id));
        }
        return labResults;
    }
    
    public List<Prescription> getPrescriptions(String prescriptionIds) {
        String[] ids = prescriptionIds.split(",");
        List<Prescription> prescriptions = new ArrayList<>();
        for (String id : ids) {
            id = id.trim();
            prescriptions.add(MedicalRecordManagement.getPrescriptionById(id));
        }
        return prescriptions;
    }
    
    public ConsultationStatus getConsultationStatus(String status) {
//        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
        ConsultationStatus consultationStatus = null;
        switch (status) {
            case "SCHEDULED": consultationStatus = ConsultationStatus.SCHEDULED; break;
            case "IN_PROGRESS": consultationStatus = ConsultationStatus.IN_PROGRESS; break;
            case "COMPLETED": consultationStatus = ConsultationStatus.COMPLETED; break;
            case "CANCELLED": consultationStatus = ConsultationStatus.CANCELLED; break;
            default: System.out.println("Unknown Consultation Status");
        }
        return consultationStatus;
    }
}
