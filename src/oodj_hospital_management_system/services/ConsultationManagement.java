/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.Feedback;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.ErrorHandler;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.exceptions.ConsultationException;
import oodj_hospital_management_system.utils.exceptions.UserException;

/**
 *
 * @author sheaw
 */
public class ConsultationManagement {

    private static final FileHandler consultationHandler = new FileHandler(SystemConfiguration.consultationFilePath);
    private static final FileHandler consultationSlotHandler = new FileHandler(SystemConfiguration.consultationSlotFilePath);
    private static final FileHandler feedbackHandler = new FileHandler(SystemConfiguration.feedbackFilePath);

    private static final List<Consultation> consultations = new ArrayList<>();
    private static final List<ConsultationSlot> consultationSlots = new ArrayList<>();
    private static final List<Feedback> allFeedback = new ArrayList<>();

    private User activeUser;

    public ConsultationManagement(User user) {
        this.activeUser = user;
        HospitalAssetManagement.loadHospitalAssets();
        ConsultationTypeManagement.loadConsultationTypes();
        loadAllFeedback();
        loadConsultationSlots();
        loadConsultations();
    }

    public static void loadConsultations() {
        consultations.clear();
        List<LinkedHashMap<String, String>> rawData = consultationHandler.readFile();
        boolean statusChanged = false;

        for (LinkedHashMap<String, String> row : rawData) {
            String consultationId = row.get("consultationId");

            Consultation consultation = new Consultation(
                    consultationId,
                    findConsultationSlotById(row.get("consultationSlotId")),
                    UserManagement.getPatientById(row.get("patientId")),
                    ConsultationTypeManagement.getConsultationTypeById(row.get("consultationTypeId")),
                    row.get("description"),
                    MedicalRecordManagement.getMedicalAssessmentsByConsultationId(consultationId),
                    MedicalRecordManagement.getLabResultsByConsultationId(consultationId),
                    MedicalRecordManagement.getPrescriptionsByConsultationId(consultationId),
                    HospitalAssetManagement.getHospitalAssetAllocationById(row.get("hospitalAssetAllocationId")),
                    findFeedbackByConsultationId(row.get("consultationId")),
                    BillingManagement.findBillByConsultationId(row.get("consultationId")),
                    getConsultationStatus(row.get("status"))
            );

            if (autoUpdateStatusByTime(consultation)) {
                statusChanged = true;
            }

            consultations.add(consultation);
        }

        if (statusChanged) {
            writeConsultations();
        }
    }

    public static void loadConsultationSlots() {
        consultationSlots.clear();
        List<LinkedHashMap<String, String>> rawData = consultationSlotHandler.readFile();
        boolean statusChanged = false;

        for (LinkedHashMap<String, String> row : rawData) {
            ConsultationSlot consultationSlot = new ConsultationSlot(
                    row.get("consultationSlotId"),
                    DoctorManagement.getDoctorShiftById(row.get("doctorShiftId")),
                    row.get("startDateTime"),
                    row.get("endDateTime"),
                    row.get("status")
            );

            if (autoUpdateSlotStatusByTime(consultationSlot)) {
                statusChanged = true;
            }

            consultationSlots.add(consultationSlot);
        }

        if (statusChanged) {
            writeConsultationSlots();
        }
    }

    public static void loadAllFeedback() {
        allFeedback.clear();
        List<LinkedHashMap<String, String>> rawData = feedbackHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            Feedback feedback = new Feedback(
                    row.get("feedbackId"),
                    row.get("consultationId"),
                    UserManagement.getPatientById(row.get("patientId")),
                    Integer.parseInt(row.get("rating")),
                    row.get("comment"),
                    row.get("submittedDateTime")
            );
            allFeedback.add(feedback);
        }
    }

    public static List<Consultation> getAllConsultations() {
        HospitalAssetManagement.loadHospitalAssets();
        ConsultationTypeManagement.loadConsultationTypes();
        loadAllFeedback();
        loadConsultationSlots();
        loadConsultations();
        return consultations;
    }

    public static List<ConsultationSlot> getAllConsultationSlots() {
        HospitalAssetManagement.loadHospitalAssets();
        loadAllFeedback();
        loadConsultationSlots();
        return consultationSlots;
    }

    public static List<Feedback> getAllFeedback() {
        loadAllFeedback();
        return allFeedback;
    }

    public static Consultation getConsultationById(String consultationId) {
        HospitalAssetManagement.loadHospitalAssets();
        ConsultationTypeManagement.loadConsultationTypes();
        loadAllFeedback();
        loadConsultationSlots();
        loadConsultations();
        return findConsultationById(consultationId);
    }

    public static Consultation findConsultationById(String consultationId) {
        try {
            if (consultationId == null || consultationId.isEmpty()) {
                throw new Exception("Consultation ID cannot be null or empty");
            }
            for (Consultation consultation : consultations) {
                if (consultationId.equals(consultation.getConsultationId())) {
                    return consultation;
                }
            }
            throw new Exception(String.format("Invalid Consultation ID: Consultation ID '%s' is not found", consultationId));
        } catch (Exception e) {
            System.out.println("Error finding consultation -> " + e.getMessage());
            Logger.errorLog("Error finding consultation -> " + e.getMessage());
            return null;
        }
    }

    public static ConsultationSlot getConsultationSlotById(String consultationSlotId) {
        HospitalAssetManagement.loadHospitalAssets();
        loadAllFeedback();
        loadConsultationSlots();
        loadConsultations();
        return findConsultationSlotById(consultationSlotId);
    }

    public static ConsultationSlot findConsultationSlotById(String consultationSlotId) {
        try {
            if (consultationSlotId == null || consultationSlotId.isEmpty()) {
                throw new Exception("Consultation Slot ID cannot be null or empty");
            }
            for (ConsultationSlot consultationSlot : consultationSlots) {
                if (consultationSlotId.equals(consultationSlot.getConsultationSlotId())) {
                    return consultationSlot;
                }
            }
            throw new Exception(String.format("Invalid Consultation Slot ID: Consultation Slot ID '%s' is not found", consultationSlotId));
        } catch (Exception e) {
            System.out.println("Error finding consultation slot -> " + e.getMessage());
            Logger.errorLog("Error finding consultation slot -> " + e.getMessage());
            return null;
        }
    }

    public static Feedback getFeedbackById(String feedbackId) {
        loadAllFeedback();
        return findFeedbackById(feedbackId);
    }

    public static Feedback findFeedbackById(String feedbackId) {
        if (feedbackId == null || feedbackId.isEmpty()) {
            return null;
        }
        try {
            for (Feedback feedback : allFeedback) {
                if (feedbackId.equals(feedback.getFeedbackId())) {
                    return feedback;
                }
            }
            throw new Exception(String.format("Invalid Feedback ID: Feedback ID '%s' is not found", feedbackId));
        } catch (Exception e) {
            System.out.println("Error finding feedback -> " + e.getMessage());
            Logger.errorLog("Error finding feedback -> " + e.getMessage());
            return null;
        }
    }
    
    public static Feedback findFeedbackByConsultationId(String consultationId) {
        for (Feedback feedback : allFeedback) {
            if (feedback.getConsultationId().equals(consultationId)) {
                return feedback;
            }
        }
        return null;
    }
    
    public static Feedback getFeedbackByConsultationId(String consultationId) {
        loadAllFeedback();
        for (Feedback feedback : allFeedback) {
            if (feedback.getConsultationId().equals(consultationId)) {
                return feedback;
            }
        }
        return null;
    }

    public static ConsultationStatus getConsultationStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }
        switch (status.toUpperCase()) {
            case "SCHEDULED":
                return ConsultationStatus.SCHEDULED;
            case "IN_PROGRESS":
                return ConsultationStatus.IN_PROGRESS;
            case "COMPLETED":
                return ConsultationStatus.COMPLETED;
            case "CANCELLED":
                return ConsultationStatus.CANCELLED;
            default:
                System.out.println("Unknown Consultation Status: " + status);
                return null;
        }
    }

    // File Handler （Write and Append)
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
            Logger.errorLog("Error appending new consultation (System Error) -> " + e.getMessage());
            return false;
        }
    }

    private static boolean writeConsultationSlots() {
        ArrayList<LinkedHashMap<String, String>> consultationSlotData = new ArrayList<>();

        for (ConsultationSlot consultationSlot : consultationSlots) {
            consultationSlotData.add(consultationSlot.getConsultationSlotMap());
        }

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
            Logger.errorLog("Error appending new consultation slot (System Error) -> " + e.getMessage());
            return false;
        }
    }

    public LinkedHashMap<String, String> createConsultation(ConsultationSlot consultationSlot, Patient patient, ConsultationType consultationType, String description) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.consultationErrorHandler(Arrays.asList("consultation slot", "patient", "description"), consultationSlot, patient, description);

        } catch (ConsultationException e) {
            System.out.println("Error creating consultation\n" + e.getMessage());

            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not created\n" + e.getMessage());

            return msgMap;

        } catch (UserException e) {
            System.out.println("Error creating consultation\n" + e.getMessage());

            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not created\n" + e.getMessage());

            return msgMap;
        }

        if (consultationType == null) {
            System.out.println("Error creating consultation\nConsultation type is required.");

            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not created\nConsultation type is required.");

            return msgMap;
        }
        
        if (isConsultationSlotTaken(consultationSlot.getConsultationSlotId())) {
            System.out.println("Error creating consultation\nConsultation slot is already booked.");
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not created\nConsultation slot is already booked.");
            return msgMap;
        }

        // resolve the live reference before mutating, same pattern used elsewhere
        ConsultationSlot targetSlot = findConsultationSlotById(consultationSlot.getConsultationSlotId());

        String newConsultationId = generateConsultationId();

        Consultation newConsultation = new Consultation(
                newConsultationId,
                targetSlot,          // use the live reference, not the passed-in one
                patient,
                consultationType,
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

        if (consultationHandler.checkFileExistence()) {
            appendConsultation(newConsultation);
        } else {
            writeConsultations();
        }

        // mark the slot as booked and persist it BEFORE reloading consultations,
        // so findConsultationSlotById() picks up the updated status downstream
        targetSlot.setStatus(ConsultationSlot.STATUS_BOOKED);
        writeConsultationSlots();
        loadConsultationSlots();

        loadConsultations();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation '%s' is created and stored!", newConsultationId));
        msgMap.put("newConsultationId", newConsultationId);

        return msgMap;
    }

    public LinkedHashMap<String, String> updateConsultation(Consultation consultation, ConsultationType newConsultationType, String newDescription) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
 
        // Validate on the passed-in object first, then re-fetch via ID before mutating
        // (findConsultationById relies on the current in-memory list, so always
        // resolve the live reference before setting fields)
        if (consultation == null || findConsultationById(consultation.getConsultationId()) == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not updated\nConsultation not found.");
            return msgMap;
        }
 
        if (newConsultationType == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not updated\nConsultation type is required.");
            return msgMap;
        }
 
        if (newDescription == null || newDescription.trim().isEmpty()) {
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation is not updated\nDescription cannot be empty.");
            return msgMap;
        }
 
        Consultation targetConsultation = findConsultationById(consultation.getConsultationId());
        targetConsultation.setConsultationType(newConsultationType);
        targetConsultation.setDescription(newDescription);
 
        writeConsultations();
        loadConsultations();
 
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation '%s' is updated!", targetConsultation.getConsultationId()));
 
        return msgMap;
    }

    public LinkedHashMap<String, String> deleteConsultation(String consultationId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Consultation deleteConsultation = findConsultationById(consultationId);

        if (deleteConsultation == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Consultation '%s' not found", consultationId));
            return msgMap;
        }

        consultations.removeIf(consultation -> consultation.getConsultationId().equals(consultationId));
        writeConsultations();

        // free up the slot again, if it still exists
        if (deleteConsultation.getConsultationSlot() != null) {
            ConsultationSlot targetSlot = findConsultationSlotById(deleteConsultation.getConsultationSlot().getConsultationSlotId());
            if (targetSlot != null) {
                targetSlot.setStatus(ConsultationSlot.STATUS_AVAILABLE);
                writeConsultationSlots();
                loadConsultationSlots();
            }
        }

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

        if (consultationSlotHandler.checkFileExistence()) {
            appendConsultationSlot(newConsultationSlot);
        } else {
            writeConsultationSlots();
        }

        loadConsultationSlots();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Consultation slot '%s' is created and stored!", newConsultationSlotId));
        msgMap.put("newConsultationSlotId", newConsultationSlotId);

        return msgMap;
    }

    // =========================================================
    // CREATE CONSULTATION SLOTS IN BULK
    // IMPORTANT:
    // Called from DoctorManagement when a Doctor Shift is created.
    // The supplied start/end time is already the Doctor Shift time.
    // Therefore, every generated 30-minute slot is automatically
    // within the Doctor Shift.
    // =========================================================

    public LinkedHashMap<String, String> createConsultationSlotInBulk(DoctorShift doctorShift, String startDateTime, String endDateTime) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.consultationSlotErrorHandler(Arrays.asList("doctor shift", "startDateTime", "endDateTime"), doctorShift, startDateTime, endDateTime);

        } catch (ConsultationException e) {
            System.out.println("Error creating consultation slots\n" + e.getMessage());

            msgMap.put("success", null);
            msgMap.put("msg", "Consultation slots are not created\n" + e.getMessage());

            return msgMap;
        }

        LocalDateTime slotStart = DateTime.toDateTimeObject(startDateTime);
        LocalDateTime shiftEnd = DateTime.toDateTimeObject(endDateTime);

        int slotCount = 0;

        while (slotStart.plusMinutes(30).compareTo(shiftEnd) <= 0) {
            LocalDateTime slotEnd = slotStart.plusMinutes(30);

            String newConsultationSlotId = generateConsultationSlotId();

            ConsultationSlot newConsultationSlot = new ConsultationSlot(
                    newConsultationSlotId,
                    doctorShift,
                    DateTime.formattedLocalDateTime(slotStart),
                    DateTime.formattedLocalDateTime(slotEnd),
                    ConsultationSlot.STATUS_AVAILABLE
            );

            consultationSlots.add(newConsultationSlot);

            slotStart = slotEnd;
            slotCount++;
        }

        writeConsultationSlots();
        loadConsultationSlots();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("%d consultation slots are created and stored for Doctor Shift '%s'!", slotCount, doctorShift.getShiftId()));
        msgMap.put("slotCount", String.valueOf(slotCount));

        return msgMap;
    }
    
    public static LinkedHashMap<String, String> deleteConsultationSlotsInBulk(String doctorShiftId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            List<ConsultationSlot> affectedSlots = consultationSlots.stream()
                    .filter(slot -> slot.getDoctorShift() != null
                            && slot.getDoctorShift().getShiftId().equals(doctorShiftId))
                    .toList();

            if (affectedSlots.isEmpty()) {
                msgMap.put("success", "true");
                msgMap.put("msg", String.format("No consultation slots found for Doctor Shift '%s'; nothing to delete.", doctorShiftId));
                msgMap.put("slotCount", "0");
                msgMap.put("cancelledConsultationCount", "0");
                return msgMap;
            }

            List<String> affectedSlotIds = affectedSlots.stream()
                    .map(ConsultationSlot::getConsultationSlotId)
                    .toList();

            // Cancel any non-terminal consultations on these slots BEFORE touching the slots,
            // so we know which slots have a consultation history worth keeping.
            int cancelledCount = 0;
            for (Consultation consultation : consultations) {
                if (consultation.getConsultationSlot() != null
                        && affectedSlotIds.contains(consultation.getConsultationSlot().getConsultationSlotId())
                        && consultation.getConsultationStatus() != ConsultationStatus.CANCELLED
                        && consultation.getConsultationStatus() != ConsultationStatus.COMPLETED) {
                    consultation.setConsultationStatus(ConsultationStatus.CANCELLED);
                    cancelledCount++;
                }
            }
            if (cancelledCount > 0) {
                writeConsultations();
            }

            // A slot with ANY consultation (even a completed/already-cancelled one) is kept
            // as CANCELLED so consultation history can still resolve its date/time.
            // A slot with no consultation at all is removed outright.
            int removedCount = 0;
            int keptCount = 0;

            for (ConsultationSlot slot : affectedSlots) {
                boolean hasConsultation = consultations.stream().anyMatch(c ->
                        c.getConsultationSlot() != null
                        && c.getConsultationSlot().getConsultationSlotId().equals(slot.getConsultationSlotId()));

                if (hasConsultation) {
                    slot.setStatus(ConsultationSlot.STATUS_CANCELLED);
                    keptCount++;
                } else {
                    removedCount++;
                }
            }

            consultationSlots.removeIf(slot ->
                    affectedSlotIds.contains(slot.getConsultationSlotId())
                    && consultations.stream().noneMatch(c ->
                            c.getConsultationSlot() != null
                            && c.getConsultationSlot().getConsultationSlotId().equals(slot.getConsultationSlotId())));

            writeConsultationSlots();
            loadConsultationSlots();
            loadConsultations();

            System.out.printf("Doctor Shift '%s': %d slot(s) removed, %d slot(s) marked CANCELLED (had bookings), %d consultation(s) cancelled!%n",
                    doctorShiftId, removedCount, keptCount, cancelledCount);

            msgMap.put("success", "true");
            msgMap.put("msg", String.format(
                    "%d consultation slot(s) removed, %d slot(s) kept as cancelled (had bookings), %d consultation(s) cancelled for Doctor Shift '%s'.",
                    removedCount, keptCount, cancelledCount, doctorShiftId));
            msgMap.put("slotCount", String.valueOf(affectedSlots.size()));
            msgMap.put("cancelledConsultationCount", String.valueOf(cancelledCount));
            return msgMap;

        } catch (Exception e) {
            System.out.println("Error deleting consultation slots in bulk: " + e.getMessage());
            Logger.errorLog("Error deleting consultation slots in bulk (System Error) -> " + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Consultation slots are not deleted\n" + e.getMessage());
            return msgMap;
        }
    }
    
    public static boolean isConsultationSlotTaken(String consultationSlotId) {
        for (Consultation consultation : consultations) {
            if (consultation.getConsultationSlot() != null
                    && consultationSlotId.equals(consultation.getConsultationSlot().getConsultationSlotId())
                    && consultation.getConsultationStatus() != ConsultationStatus.CANCELLED) {
                return true;
            }
        }
        return false;
    }
    
    // To help with auto and timely update of consultation status
    private static boolean autoUpdateStatusByTime(Consultation consultation) {
        if (consultation.getConsultationStatus() != ConsultationStatus.SCHEDULED) {
            return false;
        }

        ConsultationSlot slot = consultation.getConsultationSlot();
        if (slot == null) {
            return false;
        }

        LocalDateTime slotEnd = DateTime.toDateTimeObject(slot.getEndDateTime());
        if (slotEnd == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(slotEnd)) {
            consultation.setConsultationStatus(ConsultationStatus.COMPLETED);
            return true;
        }

        return false;
    }
    
    private static boolean autoUpdateSlotStatusByTime(ConsultationSlot slot) {
        String currentStatus = slot.getStatus();

        // ENDED and COMPLETED are terminal; only AVAILABLE/BOOKED auto-transition.
        boolean isAvailable = ConsultationSlot.STATUS_AVAILABLE.equals(currentStatus);
        boolean isBooked = ConsultationSlot.STATUS_BOOKED.equals(currentStatus);
        if (!isAvailable && !isBooked) {
            return false;
        }

        LocalDateTime slotEnd = DateTime.toDateTimeObject(slot.getEndDateTime());
        if (slotEnd == null || !LocalDateTime.now().isAfter(slotEnd)) {
            return false;
        }

        slot.setStatus(isAvailable ? ConsultationSlot.STATUS_ENDED : ConsultationSlot.STATUS_COMPLETED);
        return true;
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