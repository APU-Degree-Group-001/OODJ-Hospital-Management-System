/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.Feedback;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.ErrorHandler;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.exceptions.ConsultationException;
import oodj_hospital_management_system.utils.exceptions.UserException;

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
    private static final FileHandler consultationHandler = new FileHandler(SystemConfiguration.consultationFilePath);
    private static final FileHandler consultationSlotHandler = new FileHandler(SystemConfiguration.consultationSlotFilePath);
    private static final List<Consultation> consultations = new ArrayList<>();
    private static final List<ConsultationSlot> consultationSlots = new ArrayList<>();
    private User activeUser;
    
    public ConsultationManagement(User user) {
        this.activeUser = user;
        loadConsultations();
    }
    
    public static void loadConsultations() {
        consultations.clear();
        List<LinkedHashMap<String, String>> rawData = consultationHandler.readFile();
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
    
    public static void loadConsultationSlots() {
        consultationSlots.clear();
        List<LinkedHashMap<String, String>> rawData = consultationSlotHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // consultationSlotId, doctorShiftId, startDateTime, endDateTime
            ConsultationSlot consultationSlot = new ConsultationSlot(
                    row.get("consultationSlotId"),
                    DoctorManagement.getDoctorShiftById(row.get("doctorShiftId")),
                    row.get("startDateTime"),
                    row.get("endDateTime"),
                    ConsultationSlot.STATUS_AVAILABLE
            );
            consultationSlots.add(consultationSlot);
        }
    }
    
    public static List<Consultation> getAllConsultations() {
        loadConsultations();
        return consultations;
    }
    
    public static List<ConsultationSlot> getAllConsultationSlots() {
        loadConsultationSlots();
        return consultationSlots;
    }
    
    public static ConsultationSlot getConsultationSlotById(String consultationSlotId) {
        return null;
    }
    
    public static Feedback getFeedback(String feedbackId) {
        return null;
    }
    
    public static List<MedicalAssessment> getMedicalAssessments(String assessmentIds) {
        String[] ids = assessmentIds.split(",");
        List<MedicalAssessment> assessment = new ArrayList<>();
        for (String id : ids) {
            id = id.trim();
            assessment.add(MedicalRecordManagement.getMedicalAssessmentById(id));
        }
        return assessment;
    }
    
    public static List<LabResult> getLabResults(String labResultIds) {
        String[] ids = labResultIds.split(",");
        List<LabResult> labResults = new ArrayList<>();
        for (String id : ids) {
            id = id.trim();
            labResults.add(MedicalRecordManagement.getLabResultById(id));
        }
        return labResults;
    }
    
    public static List<Prescription> getPrescriptions(String prescriptionIds) {
        String[] ids = prescriptionIds.split(",");
        List<Prescription> prescriptions = new ArrayList<>();
        for (String id : ids) {
            id = id.trim();
            prescriptions.add(MedicalRecordManagement.getPrescriptionById(id));
        }
        return prescriptions;
    }
    
    public static ConsultationStatus getConsultationStatus(String status) {
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
    
    private static boolean writeConsultations() {
        ArrayList<LinkedHashMap<String, String>> consultationData = new ArrayList<>();
        for (Consultation consultation : consultations) {
            consultationData.add(consultation.getConsultationMap());
        }
        consultationHandler.writeFile(consultationData);
        return true;
    }

    private static boolean appendConsultation(Consultation consultation) {
        try {
            ArrayList<LinkedHashMap<String, String>> consultationData = new ArrayList<>();
            consultationData.add(consultation.getConsultationMap());

            if (!consultationHandler.checkFileExistence() && !consultationHandler.createFile()) {
                System.out.printf("File 'consultations.txt' failed to be created, consultation '%s' cannot be appended%n", consultation.getConsultationId());
                return false;
            }

            consultationHandler.appendFile(consultationData);
            System.out.printf("Consultation '%s' is appended to consultations.txt!%n", consultation.getConsultationId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new consultation: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean writeConsultationSlots() {
        ArrayList<LinkedHashMap<String, String>> consultationSlotData = new ArrayList<>();
        for (ConsultationSlot consultationSlot : consultationSlots) consultationSlotData.add(consultationSlot.getConsultationSlotMap());
        consultationSlotHandler.writeFile(consultationSlotData);
        return true;
    }
    
    private static boolean appendConsultationSlot(ConsultationSlot consultationSlot) {
        try {
            ArrayList<LinkedHashMap<String, String>> consultationSlotData = new ArrayList<>();
            consultationSlotData.add(consultationSlot.getConsultationSlotMap());

            if (!consultationSlotHandler.checkFileExistence() && !consultationSlotHandler.createFile()) {
                System.out.printf("File 'consultationSlots.txt' failed to be created, consultation slot '%s' cannot be appended%n", consultationSlot.getConsultationSlotId());
                return false;
            }

            consultationSlotHandler.appendFile(consultationSlotData);
            System.out.printf("Consultation slot '%s' is appended to consultationSlots.txt!%n", consultationSlot.getConsultationSlotId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new consultation slot: " + e.getMessage());
            return false;
        }
    }
    
    public LinkedHashMap<String, String> createConsultation(ConsultationSlot consultationSlot, Patient patient, String description) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.consultationErrorHandler(
                    Arrays.asList("consultation slot", "patient", "description"),
                    consultationSlot, patient, description
            );
        } catch (ConsultationException e) {
            System.out.println("Error creating consultation\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not created\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            System.out.println("Error creating consultation\n" + ue.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not created\n" + ue.getMessage());
            return msgMap;
        }
        String newConsultationId = generateConsultationId();
        Consultation newConsultation = new Consultation(
                newConsultationId,
                consultationSlot,
                patient,
                description,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null,
                null,
                ConsultationStatus.SCHEDULED
        );

        consultations.add(newConsultation);
        loadConsultations();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation '%s' is created and stored!", newConsultationId));
        msgMap.put("newConsultationId", newConsultationId);
        return msgMap;
    }
    
    public LinkedHashMap<String, String> deleteConsultation(String consultationId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        Consultation deleteConsultation = null;
        for (Consultation consultation : consultations) {
            if (consultation.getConsultationId().equals(consultationId)) {
                deleteConsultation = consultation;
                break;
            }
        }
        if (deleteConsultation == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Consultation '%s' not found", consultationId));
            return msgMap;
        }
        consultations.removeIf(consultation -> consultation.getConsultationId().equals(consultationId));
        writeConsultations();
        loadConsultations();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation '%s' is deleted!", consultationId));
        msgMap.put("deleteConsultationId", deleteConsultation.getConsultationId());
        return msgMap;
    }
    
    public LinkedHashMap<String, String> createConsultationSlot(DoctorShift doctorShift, String startDateTime, String endDateTime) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.consultationSlotErrorHandler(Arrays.asList("doctor shift", "startDateTime", "endDateTime"), doctorShift, startDateTime, endDateTime);
        } catch (ConsultationException e) {
            System.out.println("Error creating consultation slot\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation slot is not created\n" + e.getMessage());
            return msgMap;
        }
        String newConsultationSlotId = generateConsultationSlotId();
        ConsultationSlot newConsultationSlot = new ConsultationSlot(
                newConsultationSlotId,
                doctorShift,
                startDateTime,
                endDateTime,
                ConsultationSlot.STATUS_AVAILABLE
        );
        consultationSlots.add(newConsultationSlot);
        writeConsultationSlots();
        loadConsultationSlots();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation slot '%s' is created and stored!", newConsultationSlotId));
        msgMap.put("newConsultationSlotId", newConsultationSlotId);
        return msgMap;
    }
    
    public static String generateConsultationId() {
        List<String> existingIds = consultations.stream().map(Consultation::getConsultationId).toList();
        return SystemConfiguration.generateId("#CON", existingIds);
    }

    public static String generateConsultationSlotId() {
        List<String> existingIds = consultationSlots.stream().map(ConsultationSlot::getConsultationSlotId).toList();
        return SystemConfiguration.generateId("#SLOT", existingIds);
    }
}
