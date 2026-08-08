/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.DoctorShift.DoctorShiftStatus;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.exceptions.DoctorShiftException;

/**
 *
 * @author sheaw
 */
public class DoctorManagement {
    private static final FileHandler handler = new FileHandler(SystemConfiguration.doctorShiftFilePath);
    private static final List<DoctorShift> doctorShifts = new ArrayList<>();
    
    public static void loadDoctorShift() {
        doctorShifts.clear();
        List<LinkedHashMap<String, String>> rawData = handler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // String shiftId, Doctor doctor, LocalDateTime startDateTime, LocalDateTime endDateTime, DoctorShiftStatus status
            // shiftId,doctorId,startDateTime,endDateTime
            DoctorShift doctorShift = new DoctorShift(
                    row.get("shiftId"),
                    UserManagement.getDoctorById(row.get("doctorId")),
                    row.get("startDateTime"),
                    row.get("endDateTime"),
                    getDoctorShiftStatus(row.get("status"))
            );
            doctorShifts.add(doctorShift);
        }
    }
    
    public static List<DoctorShift> getAllDoctorShifts() {
        return doctorShifts;
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
    
    public static DoctorShiftStatus getDoctorShiftStatus(String status) {
//        SCHEDULED, COMPLETED, CANCELLED
        DoctorShiftStatus shiftStaus = null;
        switch (status) {
            case "SCHEDULED": shiftStaus = DoctorShiftStatus.SCHEDULED; break;
            case "COMPLETED": shiftStaus = DoctorShiftStatus.COMPLETED; break;
            case "CANCELLED": shiftStaus = DoctorShiftStatus.CANCELLED; break;
            default: System.out.println("Unknown Doctor Shift Status");
        }
        return shiftStaus;
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
}
