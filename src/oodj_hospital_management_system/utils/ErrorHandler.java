/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.DoctorShift.DoctorShiftStatus;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.exceptions.DoctorShiftException;
import oodj_hospital_management_system.utils.exceptions.UserException;

/**
 *
 * @author sheaw
 */
public class ErrorHandler {
    public static void userErrorHandler(List<String> details, Object... var) throws UserException {
        List<User> allUsers = UserManagement.getAllUsers();
        List<User> adminStaff = UserManagement.getAllAdminStaffs();
        List<User> medicalManagers = UserManagement.getAllMedicalManagers();
        List<User> doctors = UserManagement.getAllDoctors();
        List<User> patients = UserManagement.getAllPatients();
        try {
            if (details.size() != var.length) {
                throw new Exception(
                        "Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
        }
        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;
                if (details.get(i).equalsIgnoreCase("userid")) {
                    if (data == null || data.equals("")) {
                        throw new UserException("User ID cannot be null or empty");
                    }
                } else if (details.get(i).equalsIgnoreCase("name")) {
                    if (data == null || data.equals("")) {
                        throw new UserException("name cannot be null or empty");
                    }
                } else if (details.get(i).equalsIgnoreCase("pwd")) {
                    // a mix of uppercase and lowercase letters, numbers, and symbols.
                    if (data == null || data.equals("")) {
                        throw new UserException("password cannot be null or empty");
                    } else if (data.length() < 8) {
                        throw new UserException("Password must be at least 8 characters long");
                    } else if (!(data.matches(SystemConfiguration.pwdPattern1) && data.matches(SystemConfiguration.pwdPattern2)
                            && data.matches(SystemConfiguration.pwdPattern3) && data.matches(SystemConfiguration.pwdPattern4))) {
                        throw new UserException(
                                "Password must be a mix of uppercase and lowercase letters, digits, and special characters");
                    }
                } else if (details.get(i).equalsIgnoreCase("type")) {
                    if (!Arrays.asList(SystemConfiguration.userTypes).contains(data)) {
                        throw new UserException("Invalid user type");
                    }
                }
            } else if (object instanceof User) {
                User data = (User) object;
                if (details.get(i).equalsIgnoreCase("admin staff")) {
                    if (!adminStaff.stream().anyMatch(user -> user.equals(data))) {
                        throw new UserException(String.format("Admin Staff '%s' not found", data.getName()));
                    }
                } else if (details.get(i).equalsIgnoreCase("medical manager")) {
                    if (!medicalManagers.stream().anyMatch(user -> user.equals(data))) {
                        throw new UserException(String.format("Medical Manager '%s' not found", data.getName()));
                    }
                } else if (details.get(i).equalsIgnoreCase("doctor")) {
                    if (!allUsers.stream().anyMatch(user -> user.equals(data))) {
                        throw new UserException(String.format("Doctor '%s' not found", data.getName()));
                    }
                } else if (details.get(i).equalsIgnoreCase("patient")) {
                    if (!allUsers.stream().anyMatch(user -> user.equals(data))) {
                        throw new UserException(String.format("Patient '%s' not found", data.getName()));
                    }
                }
            }
            i++;
        }
    }
    
    public static void doctorShiftErrorHandler(List<String> details, Object... var) throws DoctorShiftException {
        List<DoctorShift> allDoctorShifts =DoctorManagement.getAllDoctorShifts();

        List<Doctor> allDoctors = UserManagement.getAllDoctors().stream().map(user -> (Doctor) user).toList();

        try {
            if (details.size() != var.length) {
                throw new Exception("Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
            return;
        }
        String startDateTime = null;
        String endDateTime = null;
        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;
                if (details.get(i).equalsIgnoreCase("shiftId")) {
                    if (data == null || data.equals("")) {
                        throw new DoctorShiftException("Doctor Shift ID cannot be null or empty");
                    }
                    // Check duplicate Shift ID
                    if (allDoctorShifts.stream().anyMatch(shift -> shift.getShiftId().equals(data))) {
                        throw new DoctorShiftException(
                                String.format("Doctor Shift ID '%s' already exists",data)
                        );
                    }
                } else if (details.get(i).equalsIgnoreCase("startDateTime")) {
                    if (data == null || data.equals("")) {
                        throw new DoctorShiftException("Start date/time cannot be null or empty");
                    }
                    startDateTime = data;
                } else if (details.get(i).equalsIgnoreCase("endDateTime")) {
                    if (data == null || data.equals("")) {
                        throw new DoctorShiftException("End date/time cannot be null or empty");
                    }
                    endDateTime = data;
                } else if (details.get(i).equalsIgnoreCase("shiftStatus")) {
                    if (data == null || data.equals("")) {
                        throw new DoctorShiftException("Shift status cannot be null or empty");
                    }
                    if (!(DoctorShiftStatus.SCHEDULED.equals(data)
                        || DoctorShiftStatus.COMPLETED.equals(data)
                        || DoctorShiftStatus.CANCELLED.equals(data))) {
                        throw new DoctorShiftException(String.format("Invalid shift status: '%s'",data));
                    }
                }
            } else if (object instanceof Doctor) {
                Doctor doctor = (Doctor) object;
                if (details.get(i).equalsIgnoreCase("doctor")) {
                    if (!allDoctors.stream().anyMatch(d -> d.equals(doctor))) {
                        throw new DoctorShiftException(String.format("Doctor '%s' not found",doctor.getName()));
                    }
                }
            }
            i++;
        }

        // Validate start and end date/time
        if (startDateTime != null && endDateTime != null) {
            LocalDateTime start = DateTime.toDateTimeObject(startDateTime);
            LocalDateTime end = DateTime.toDateTimeObject(endDateTime);
            if (!DateTime.isBefore(start, end)) {
                throw new DoctorShiftException("End date/time must be after start date/time");
            }
        }
    }
}
