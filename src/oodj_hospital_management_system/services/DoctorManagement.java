/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.DoctorShift.DoctorShiftStatus;
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.ErrorHandler;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.exceptions.DoctorShiftException;
import oodj_hospital_management_system.utils.exceptions.MedicalDepartmentException;
import oodj_hospital_management_system.utils.exceptions.UserException;

/**
 *
 * @author sheaw
 */
public class DoctorManagement {
    private static final FileHandler shiftHandler = new FileHandler(SystemConfiguration.doctorShiftFilePath);
    private static final FileHandler departmentHandler = new FileHandler(SystemConfiguration.medicalDepartmentFilePath);
    private static final List<DoctorShift> doctorShifts = new ArrayList<>();
    private static final List<MedicalDepartment> medicalDepartments = new ArrayList<>();
    private User activeUser;
    
    public DoctorManagement(User usr) {
        this.activeUser = usr;
        loadDoctorShift();
        loadMedicalDepartment();
    }
    
    public static void loadDoctorShift() {
        doctorShifts.clear();
        List<LinkedHashMap<String, String>> rawData = shiftHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // String shiftId, Doctor doctor, LocalDateTime startDateTime, LocalDateTime endDateTime, DoctorShiftStatus status
            // shiftId,doctorId,startDateTime,endDateTime
            DoctorShift doctorShift = new DoctorShift(
                    row.get("shiftId"),
                    UserManagement.getDoctorById(row.get("doctorId")),
                    row.get("startDateTime"),
                    row.get("endDateTime"),
                    row.get("actualStartDateTime"),
                    row.get("actualEndDateTime"),
                    getDoctorShiftStatus(row.get("status")),
                    UserManagement.getMedicalManagerById(row.get("medicalManagerId"))
            );
            doctorShifts.add(doctorShift);
        }
    }
    
    public static void loadMedicalDepartment() {
        medicalDepartments.clear();
        List<LinkedHashMap<String, String>> rawData = departmentHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // departmentId,departmentName,description
            MedicalDepartment department = new MedicalDepartment(
                    row.get("departmentId"),
                    row.get("departmentName"),
                    row.get("description")
        );
            medicalDepartments.add(department);
        }
    }
    
    public static List<DoctorShift> getAllDoctorShifts() {
        loadDoctorShift();
        return doctorShifts;
    }
    
    public static List<MedicalDepartment> getAllMedicalDepartments() {
        loadMedicalDepartment();
        return medicalDepartments;
    }
    
    public static DoctorShift getDoctorShiftById(String shiftId) {
        loadDoctorShift();
        try {
            if (!doctorShifts.stream().anyMatch(shift -> shift.getShiftId().equals(shiftId))) {
                throw new DoctorShiftException(String.format("Invalid Doctor Shift ID: Shift ID '%s' is not found", shiftId));
            }
            for (DoctorShift shift : doctorShifts) {
                if (shift.getShiftId().equals(shiftId)) {
                    return shift;
                }
            }
            return null;
        } catch (DoctorShiftException e) {
            System.out.println("Error finding user -> " + e.getMessage());
            Logger.errorLog("Error finding user -> " + e.getMessage());
            return null;
        }
    }
    
    public static MedicalDepartment getMedicalDepartmentById(String departmentId) {
        loadMedicalDepartment();
        try {
            if (!medicalDepartments.stream().anyMatch(department -> department.getDepartmentId().equals(departmentId))) {
                throw new MedicalDepartmentException(String.format("Invalid Medical Department ID: Department ID '%s' is not found", departmentId));
            }
            for (MedicalDepartment department : medicalDepartments) {
                if (department.getDepartmentId().equals(departmentId)) {
                    return department;
                }
            }
            return null;
        } catch (MedicalDepartmentException e) {
            System.out.println("Error finding medical department -> " + e.getMessage());
            Logger.errorLog("Error finding medical department -> " + e.getMessage());
            return null;
        }
    }
    
    public static DoctorShiftStatus getDoctorShiftStatus(String status) {
//        SCHEDULED, COMPLETED, CANCELLED
        DoctorShiftStatus shiftStaus = null;
        switch (status) {
            case "SCHEDULED": shiftStaus = DoctorShiftStatus.SCHEDULED; break;
            case "COMPLETED": shiftStaus = DoctorShiftStatus.COMPLETED; break;
            case "IN_PROGRESS": shiftStaus = DoctorShiftStatus.IN_PROGRESS; break;
            case "CANCELLED": shiftStaus = DoctorShiftStatus.CANCELLED; break;
            default: System.out.println("Unknown Doctor Shift Status");
        }
        return shiftStaus;
    }
    
    // File Handler (Write and Append)
    private static boolean writeDoctorShifts() {
        ArrayList<LinkedHashMap<String, String>> shifts = new ArrayList<>();
        for (DoctorShift shift : doctorShifts) {
            updateShiftStatus(shift);
            shifts.add(shift.getDoctorShiftMap());
        }
        shiftHandler.writeFile(shifts);
        return true;
    }

    private static boolean appendDoctorShift(DoctorShift shift) {
        try {
            ArrayList<LinkedHashMap<String, String>> shifts = new ArrayList<>();
            updateShiftStatus(shift);
            shifts.add(shift.getDoctorShiftMap());
            if (!shiftHandler.checkFileExistence() && !shiftHandler.createFile()) {
                System.out.printf("File 'doctorShifts.txt' failed to be created, shift '%s' cannot be appended%n", shift.getShiftId());
                return false;
            }
            shiftHandler.appendFile(shifts);
            System.out.printf("Doctor Shift '%s' is appended to doctorShifts.txt!%n", shift.getShiftId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new doctor shift: " + e.getMessage());
            Logger.errorLog("Error appending new doctor shift (System Error) -> "+ e.getMessage() + "\n");
            return false;
        }
    }
    
    public LinkedHashMap<String, String> createShift(Doctor doctor, String startDateTime, String endDateTime) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.doctorShiftErrorHandler(Arrays.asList(
                    "medical manager", "doctor", "startDateTime", "endDateTime"),
                    activeUser, doctor, startDateTime, endDateTime
            );
        } catch (DoctorShiftException e) {
            System.out.println("Error creating doctor shift\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg","Doctor shift is not created\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            System.out.println("Error creating doctor shift\n" + ue.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg","Doctor shift is not created\n" + ue.getMessage());
            return msgMap;
        }
        MedicalManager medicalManager = (MedicalManager) activeUser;
        String newShiftId = generateShiftId();
        DoctorShift newShift = new DoctorShift(newShiftId, doctor, startDateTime, endDateTime, "", "", DoctorShiftStatus.SCHEDULED, medicalManager);
        doctorShifts.add(newShift);
        if (shiftHandler.checkFileExistence()) {
            appendDoctorShift(newShift);
        } else {
            writeDoctorShifts();
        }
        loadDoctorShift();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Doctor shift '%s' is created and stored!", newShiftId));
        msgMap.put("newShiftId", newShiftId);
        return msgMap;
    }
    
    public LinkedHashMap<String, String> cancelShift(DoctorShift shift) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        // Already cancelled
        if (DoctorShiftStatus.CANCELLED.equals(shift.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Doctor shift '%s' is already cancelled!", shift.getShiftId()));
            return msgMap;
        }
        // Cannot cancel a completed shift
        if (DoctorShiftStatus.COMPLETED.equals(shift.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Doctor shift '%s' has already been completed and cannot be cancelled!", shift.getShiftId()));
            return msgMap;
        }
        shift.setStatus(DoctorShiftStatus.CANCELLED);
        writeDoctorShifts();
        loadDoctorShift();
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Doctor shift '%s' is cancelled!",shift.getShiftId()));
        return msgMap;
    }
    
    // Shift Management
    public static Doctor.ShiftStatus getDoctorAttendanceStatus(Doctor doctor) {
        loadDoctorShift();
        LocalDateTime now = DateTime.currentDateTime();
        for (DoctorShift shift : doctorShifts) {
            if (!shift.getDoctor().equals(doctor)) {
                continue;
            }
            // Doctor has not signed in yet
            if (shift.getActualStartDateTime() == null
                    || shift.getActualStartDateTime().isEmpty()) {
                continue;
            }
            LocalDateTime actualStart = DateTime.toDateTimeObject(shift.getActualStartDateTime());
            // Doctor has already signed out
            if (shift.getActualEndDateTime() != null && !shift.getActualEndDateTime().isEmpty()) {
                LocalDateTime actualEnd = DateTime.toDateTimeObject(shift.getActualEndDateTime());
                if (!DateTime.isBefore(now, actualEnd)) {
                    continue;
                }
            }
            // Doctor has signed in and has not signed out
            if (!DateTime.isBefore(now, actualStart)) {
                return Doctor.ShiftStatus.ON_SHIFT;
            }
        }
        return Doctor.ShiftStatus.OFF_SHIFT;
    }
    
    public static void updateShiftStatus(DoctorShift shift) {
        if (DoctorShiftStatus.CANCELLED.equals(shift.getStatus())) {
            return;
        }
        LocalDateTime now = DateTime.currentDateTime();
        LocalDateTime start = DateTime.toDateTimeObject(shift.getStartDateTime());
        LocalDateTime end = DateTime.toDateTimeObject(shift.getEndDateTime());

        if (DateTime.isBefore(now, start)) {
            shift.setStatus(DoctorShiftStatus.SCHEDULED);
        } else if (DateTime.isBefore(now, end)) {
            shift.setStatus(DoctorShiftStatus.IN_PROGRESS);
        } else {
            shift.setStatus(DoctorShiftStatus.COMPLETED);
        }
    }

    // Dynamically Calculate Monthly Assigned Shift Hours
    public static int calculateAssignedShiftHourByMonth(Doctor doctor) {
        loadDoctorShift();
        LocalDateTime now = DateTime.currentDateTime();
        int totalHours = 0;
        for (DoctorShift shift : doctorShifts) {
            if (!shift.getDoctor().equals(doctor)) {
                continue;
            }
            // Exclude cancelled shifts
            if (DoctorShiftStatus.CANCELLED.equals(shift.getStatus())) {
                continue;
            }
            LocalDateTime start = DateTime.toDateTimeObject(shift.getStartDateTime());
            LocalDateTime end = DateTime.toDateTimeObject(shift.getEndDateTime());
            // Only count shifts from the current month
            if (start.getYear() == now.getYear() && start.getMonth() == now.getMonth()) {
                totalHours += DateTime.calculateDurationInHours(start, end);
            }
        }
        return totalHours;
    }

    // Dynamically Calculate Monthly Actual Shift Hours
    public static String calculateActualShiftHourByMonth(Doctor doctor) {
        loadDoctorShift();
        LocalDateTime now = DateTime.currentDateTime();
        int totalMinutes = 0;
        for (DoctorShift shift : doctorShifts) {
            if (!shift.getDoctor().equals(doctor)) {
                continue;
            }
            // Exclude cancelled shifts
            if (DoctorShiftStatus.CANCELLED.equals(shift.getStatus())) {
                continue;
            }
            // Only calculate shifts that have actually started and ended
            if (shift.getActualStartDateTime() == null
                    || shift.getActualStartDateTime().isEmpty()
                    || shift.getActualEndDateTime() == null
                    || shift.getActualEndDateTime().isEmpty()) {
                continue;
            }
            LocalDateTime start = DateTime.toDateTimeObject(shift.getActualStartDateTime());
            LocalDateTime end = DateTime.toDateTimeObject(shift.getActualEndDateTime());
            // Only count shifts from the current month
            if (start.getYear() == now.getYear() && start.getMonth() == now.getMonth()) {
                totalMinutes += DateTime.calculateDurationInMinutes(start, end);
            }
        }
        String duration = DateTime.formatHourMinuteDuration(totalMinutes);
        return duration;
    }
    
    // Assign Doctor to Medical Manager - Hnadled in UserManagement because involving updating users.txt :3
    
    // Searching and Sorting
    public List<DoctorShift> searchDoctorShifts(String keywords) {
        List<DoctorShift> matchedShifts = new ArrayList<>();
        for (DoctorShift shift : doctorShifts) {
            if (shift.getShiftId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || shift.getDoctor().getName().toLowerCase().contains(keywords.toLowerCase().trim())
                    || shift.getStatus().toString().toLowerCase().contains(keywords.toLowerCase().trim())
                    || shift.getMedicalManager().getName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedShifts.add(shift);
            }
        }
        return matchedShifts;
    }

    public List<DoctorShift> searchDoctorShifts(String keywords, List<DoctorShift> shiftList) {
        List<DoctorShift> matchedShifts = new ArrayList<>();
        for (DoctorShift shift : shiftList) {
            if (shift.getShiftId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || shift.getDoctor().getName().toLowerCase().contains(keywords.toLowerCase().trim())
                    || shift.getStatus().toString().toLowerCase().contains(keywords.toLowerCase().trim())
                    || shift.getMedicalManager().getName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedShifts.add(shift);
            }
        }
        return matchedShifts;
    }

    public List<DoctorShift> sortDoctorShiftsById(List<DoctorShift> list) {
        List<DoctorShift> sortedShifts = list;
        sortedShifts.sort(Comparator.comparing(DoctorShift::getShiftId));
        return sortedShifts;
    }

    public List<DoctorShift> sortDoctorShiftsByStartDateTime(List<DoctorShift> list) {
        List<DoctorShift> sortedShifts = list;
        sortedShifts.sort(Comparator.comparing(shift -> DateTime.toDateTimeObject(shift.getStartDateTime())));
        return sortedShifts;
    }

    public List<MedicalDepartment> searchMedicalDepartments(String keywords) {
        List<MedicalDepartment> matchedDepartments = new ArrayList<>();
        for (MedicalDepartment department : medicalDepartments) {
            if (department.getDepartmentId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || department.getDepartmentName().toLowerCase().contains(keywords.toLowerCase().trim())
                    || department.getDescription().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedDepartments.add(department);
            }
        }
        return matchedDepartments;
    }

    public List<MedicalDepartment> searchMedicalDepartments(String keywords, List<MedicalDepartment> departmentList) {
        List<MedicalDepartment> matchedDepartments = new ArrayList<>();
        for (MedicalDepartment department : departmentList) {
            if (department.getDepartmentId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || department.getDepartmentName().toLowerCase().contains(keywords.toLowerCase().trim())
                    || department.getDescription().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedDepartments.add(department);
            }
        }
        return matchedDepartments;
    }

    public List<MedicalDepartment> sortMedicalDepartmentsById(List<MedicalDepartment> list) {
        List<MedicalDepartment> sortedDepartments = list;
        sortedDepartments.sort(Comparator.comparing(MedicalDepartment::getDepartmentId));
        return sortedDepartments;
    }

    public List<MedicalDepartment> sortMedicalDepartmentsByName(List<MedicalDepartment> list) {
        List<MedicalDepartment> sortedDepartments = list;
        sortedDepartments.sort(Comparator.comparing(MedicalDepartment::getDepartmentName));
        return sortedDepartments;
    }
    
    // Generate ID
    public static String generateShiftId() {
        List<String> existingIds = doctorShifts.stream().map(DoctorShift::getShiftId).toList();
        return SystemConfiguration.generateId("#SHIFT", existingIds);
    }
}
