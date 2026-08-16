/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.AdminStaff;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.DoctorShift.DoctorShiftStatus;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.BillingManagement;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.HospitalAssetManagement;
import oodj_hospital_management_system.services.InsuranceManagement;
import oodj_hospital_management_system.services.RequestManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.exceptions.BillException;
import oodj_hospital_management_system.utils.exceptions.ConsultationException;
import oodj_hospital_management_system.utils.exceptions.DoctorShiftException;
import oodj_hospital_management_system.utils.exceptions.UserException;
import oodj_hospital_management_system.utils.exceptions.HospitalAssetException;
import oodj_hospital_management_system.utils.exceptions.InsuranceException;
import oodj_hospital_management_system.utils.exceptions.RequestException;

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
                } else if (details.get(i).equalsIgnoreCase("email")) {
                    if (data == null || data.isBlank()) {
                        throw new UserException("email cannot be null or empty");
                    } else if (!data.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                        throw new UserException("Invalid email format");
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
    
    public static void doctorShiftErrorHandler(List<String> details, Object... var) throws DoctorShiftException, UserException {
        List<DoctorShift> allDoctorShifts = DoctorManagement.getAllDoctorShifts();

        List<Doctor> allDoctors = UserManagement.getAllDoctors().stream().map(user -> (Doctor) user).toList();
        List<MedicalManager> allMedicalManagers = UserManagement.getAllMedicalManagers().stream().map(user -> (MedicalManager) user).toList();
        List<AdminStaff> allAdminStaffs = UserManagement.getAllAdminStaffs().stream().map(user -> (AdminStaff) user).toList();
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
        Doctor selectedDoctor = null;
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
                                String.format("Doctor Shift ID '%s' already exists", data)
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
                        throw new DoctorShiftException(
                                String.format("Invalid shift status: '%s'", data)
                        );
                    }
                }
            }

            if (object instanceof Doctor) {
                Doctor doctor = (Doctor) object;
                if (details.get(i).equalsIgnoreCase("doctor")) {
                    if (!allDoctors.stream().anyMatch(d -> d.equals(doctor))) {
                        throw new UserException(
                                String.format("Doctor '%s' not found", doctor.getName())
                        );
                    }
                    selectedDoctor = doctor;
                }
            }

            if (object instanceof MedicalManager) {
                MedicalManager medicalManager = (MedicalManager) object;
                if (details.get(i).equalsIgnoreCase("medical manager")) {
                    if (!allMedicalManagers.stream().anyMatch(d -> d.equals(medicalManager))) {
                        throw new UserException(
                                String.format("Medical Manager '%s' not found", medicalManager.getName())
                        );
                    }
                }
            }

            if (object instanceof AdminStaff) {
                AdminStaff staff = (AdminStaff) object;
                if (details.get(i).equalsIgnoreCase("admin staff")) {
                    if (!allAdminStaffs.stream().anyMatch(d -> d.equals(staff))) {
                        throw new UserException(
                                String.format("Admin Staff '%s' not found", staff.getName())
                        );
                    }
                }
            }

            if (details.get(i).equalsIgnoreCase("assignment operation")) {
                if (object == null) {
                    throw new DoctorShiftException("Active User cannot be null");
                }
                if (!(object instanceof User)) {
                    throw new DoctorShiftException("Invalid active user");
                }
                User user = (User) object;
                if (!"admin staff".equalsIgnoreCase(user.getType())) {
                    throw new DoctorShiftException(
                            "Only Admin Staff can perform doctor assignment"
                    );
                }
            }
            
            if (object instanceof DoctorShift) {
                DoctorShift doctorShift = (DoctorShift) object;

                if (details.get(i).equalsIgnoreCase("delete shift")) {
                    boolean hasBookedConsultation = ConsultationManagement.getAllConsultationSlots().stream()
                            .filter(slot -> slot.getDoctorShift() != null && slot.getDoctorShift().equals(doctorShift))
                            .anyMatch(slot -> ConsultationSlot.STATUS_BOOKED.equals(slot.getStatus()));

                    if (hasBookedConsultation) {
                        throw new DoctorShiftException(String.format("Doctor Shift '%s' cannot be deleted because it has booked consultation(s)", doctorShift.getShiftId()));
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
                throw new DoctorShiftException(
                        "End date/time must be after start date/time"
                );
            }
            // Check doctor's own overlapping shift
            if (selectedDoctor != null) {
                Doctor doctorToCheck = selectedDoctor;
                boolean doctorHasOverlappingShift = allDoctorShifts.stream()
                        .filter(shift -> doctorToCheck.equals(shift.getDoctor()))
                        .filter(shift -> DoctorShiftStatus.SCHEDULED.equals(shift.getStatus()))
                        .anyMatch(shift -> {
                            LocalDateTime existingStart = DateTime.toDateTimeObject(shift.getStartDateTime());
                            LocalDateTime existingEnd = DateTime.toDateTimeObject(shift.getEndDateTime());
                            return start.isBefore(existingEnd) && end.isAfter(existingStart);
                        });
                if (doctorHasOverlappingShift) {
                    throw new DoctorShiftException(
                            String.format(
                                    "Doctor '%s' already has an overlapping scheduled shift",
                                    doctorToCheck.getName()
                            )
                    );
                }
            }

            // Count all overlapping scheduled doctor shifts
            long overlappingShifts = allDoctorShifts.stream()
                    .filter(shift -> DoctorShiftStatus.SCHEDULED.equals(shift.getStatus()))
                    .filter(shift -> {
                        LocalDateTime existingStart = DateTime.toDateTimeObject(shift.getStartDateTime());
                        LocalDateTime existingEnd = DateTime.toDateTimeObject(shift.getEndDateTime());
                        return start.isBefore(existingEnd) && end.isAfter(existingStart);
                    })
                    .count();

            // Get number of consultation rooms
            int consultationRoomCount = HospitalAssetManagement.getConsultationRoomCount();
            
            // whyyyyyyyyy
            System.out.println(
                "Consultation Rooms: "
                + HospitalAssetManagement.getConsultationRoomCount()
            );

            // Check consultation room capacity
            if (overlappingShifts >= consultationRoomCount) {
                throw new DoctorShiftException(
                        String.format(
                                "No consultation room is available for this shift. %d overlapping shift(s) already exist and only %d consultation room(s) are available.",
                                overlappingShifts,
                                consultationRoomCount
                        )
                );
            }
        }
    }
    
    public static void consultationErrorHandler(List<String> details, Object... var) throws ConsultationException, UserException {
        List<Consultation> allConsultations = ConsultationManagement.getAllConsultations();
        List<ConsultationSlot> allConsultationSlots = ConsultationManagement.getAllConsultationSlots();
        List<Doctor> allDoctors = UserManagement.getAllDoctors().stream().map(user -> (Doctor) user).toList();
        List<Patient> allPatients = UserManagement.getAllPatients().stream().map(user -> (Patient) user).toList();
        try {
            if (details.size() != var.length) {
                throw new Exception("Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
            return;
        }
        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;
                if (details.get(i).equalsIgnoreCase("consultationId")) {
                    if (data == null || data.equals("")) {
                        throw new ConsultationException("Consultation ID cannot be null or empty");
                    }
                    if (allConsultations.stream().anyMatch(consultation -> consultation.getConsultationId().equals(data))) {
                        throw new ConsultationException(
                                String.format("Consultation ID '%s' already exists", data)
                        );
                    }
                } else if (details.get(i).equalsIgnoreCase("description")) {
                    if (data == null || data.equals("")) {
                        throw new ConsultationException("Consultation description cannot be null or empty");
                    }
                } else if (details.get(i).equalsIgnoreCase("consultationStatus")) {
                    if (data == null || data.equals("")) {
                        throw new ConsultationException("Consultation status cannot be null or empty");
                    }
                    if (!(ConsultationStatus.SCHEDULED.equals(data)
                            || ConsultationStatus.IN_PROGRESS.equals(data)
                            || ConsultationStatus.COMPLETED.equals(data)
                            || ConsultationStatus.CANCELLED.equals(data))) {
                        throw new ConsultationException(
                                String.format("Invalid consultation status: '%s'", data)
                        );
                    }
                }
            }
            if (object instanceof ConsultationSlot) {
                ConsultationSlot consultationSlot = (ConsultationSlot) object;

                if (details.get(i).equalsIgnoreCase("consultation slot")) {
                    if (!allConsultationSlots.stream().anyMatch(slot -> slot.equals(consultationSlot))) {
                        throw new ConsultationException(
                                String.format("Consultation Slot '%s' not found", consultationSlot.getConsultationSlotId())
                        );
                    }
                }
            }
            if (object instanceof Patient) {
                Patient patient = (Patient) object;

                if (details.get(i).equalsIgnoreCase("patient")) {
                    if (!allPatients.stream().anyMatch(p -> p.equals(patient))) {
                        throw new UserException(
                                String.format("Patient '%s' not found", patient.getName())
                        );
                    }
                }
            }
            if (object instanceof Doctor) {
                Doctor doctor = (Doctor) object;

                if (details.get(i).equalsIgnoreCase("doctor")) {
                    if (!allDoctors.stream().anyMatch(d -> d.equals(doctor))) {
                        throw new UserException(
                                String.format("Doctor '%s' not found", doctor.getName())
                        );
                    }
                }
            }
            if (details.get(i).equalsIgnoreCase("consultation operation")) {
                if (object == null) {
                    throw new ConsultationException("Active User cannot be null");
                }
                if (!(object instanceof User)) {
                    throw new ConsultationException("Invalid active user");
                }
                User user = (User) object;
                if (!"doctor".equalsIgnoreCase(user.getType())) {
                    throw new ConsultationException(
                            "Only Doctors can perform consultation operations"
                    );
                }
            }
            i++;
        }
    }
    
    public static void consultationSlotErrorHandler(List<String> details, Object... var) throws ConsultationException {
        List<ConsultationSlot> allConsultationSlots = ConsultationManagement.getAllConsultationSlots();
        List<DoctorShift> allDoctorShifts = DoctorManagement.getAllDoctorShifts();
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
                if (details.get(i).equalsIgnoreCase("consultationSlotId")) {
                    if (data == null || data.equals("")) {
                        throw new ConsultationException("Consultation Slot ID cannot be null or empty");
                    }
                    if (allConsultationSlots.stream().anyMatch(slot -> slot.getConsultationSlotId().equals(data))) {
                        throw new ConsultationException(String.format("Consultation Slot ID '%s' already exists", data));
                    }
                } else if (details.get(i).equalsIgnoreCase("startDateTime")) {
                    if (data == null || data.equals("")) {
                        throw new ConsultationException("Start date/time cannot be null or empty");
                    }
                    startDateTime = data;
                } else if (details.get(i).equalsIgnoreCase("endDateTime")) {
                    if (data == null || data.equals("")) {
                        throw new ConsultationException("End date/time cannot be null or empty");
                    }
                    endDateTime = data;
                }
            }
            if (object instanceof DoctorShift) {
                DoctorShift doctorShift = (DoctorShift) object;
                if (details.get(i).equalsIgnoreCase("doctor shift")) {
                    if (!allDoctorShifts.stream().anyMatch(shift -> shift.equals(doctorShift))) {
                        throw new ConsultationException(String.format("Doctor Shift '%s' not found", doctorShift.getShiftId()));
                    }
                }
            }
            i++;
        }

        if (startDateTime != null && endDateTime != null) {
            LocalDateTime start = DateTime.toDateTimeObject(startDateTime);
            LocalDateTime end = DateTime.toDateTimeObject(endDateTime);
            if (!DateTime.isBefore(start, end)) {
                throw new ConsultationException("End date/time must be after start date/time");
            }
        }
    }
    
    public static void hospitalAssetErrorHandler(List<String> details, Object... var) throws HospitalAssetException {
        List<HospitalAsset> allHospitalAssets = HospitalAssetManagement.getAllHospitalAssets();
        List<HospitalAssetAllocation> allHospitalAssetAllocations = HospitalAssetManagement.getAllHospitalAssetAllocations();

        try {
            if (details.size() != var.length) {
                throw new Exception("Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
            return;
        }

        int i = 0;

        for (Object object : var) {

            if (object instanceof String) {
                String data = (String) object;

                if (details.get(i).equalsIgnoreCase("assetId")) {

                    if (data == null || data.equals("")) {
                        throw new HospitalAssetException("Hospital Asset ID cannot be null or empty");
                    }

                    if (allHospitalAssets.stream().anyMatch(asset -> asset.getAssetId().equals(data))) {
                        throw new HospitalAssetException(
                                String.format("Hospital Asset ID '%s' already exists", data)
                        );
                    }

                } else if (details.get(i).equalsIgnoreCase("assetType")) {

                    if (data == null || data.equals("")) {
                        throw new HospitalAssetException("Hospital Asset type cannot be null or empty");
                    }

                } else if (details.get(i).equalsIgnoreCase("assetName")) {

                    if (data == null || data.equals("")) {
                        throw new HospitalAssetException("Hospital Asset name cannot be null or empty");
                    }

                } else if (details.get(i).equalsIgnoreCase("imagePath")) {

                    if (data == null || data.equals("")) {
                        throw new HospitalAssetException("Image path cannot be null or empty");
                    }

                } else if (details.get(i).equalsIgnoreCase("location")) {

                    if (data == null || data.equals("")) {
                        throw new HospitalAssetException("Hospital Asset location cannot be null or empty");
                    }

                } else if (details.get(i).equalsIgnoreCase("description")) {

                    if (data == null || data.equals("")) {
                        throw new HospitalAssetException("Hospital Asset description cannot be null or empty");
                    }
                }
            }

            if (object instanceof HospitalAsset) {
                HospitalAsset asset = (HospitalAsset) object;

                if (details.get(i).equalsIgnoreCase("hospital asset")) {

                    if (!allHospitalAssets.stream().anyMatch(existingAsset -> existingAsset.equals(asset))) {
                        throw new HospitalAssetException(
                                String.format("Hospital Asset '%s' not found", asset.getAssetId())
                        );
                    }
                }
                if (details.get(i).equalsIgnoreCase("delete hospital asset")) {
                    if (!allHospitalAssets.stream().anyMatch(existingAsset -> existingAsset.equals(asset))) {
                        throw new HospitalAssetException(
                                String.format("Hospital Asset '%s' not found", asset.getAssetId())
                        );
                    }
                    boolean isAllocated = allHospitalAssetAllocations.stream()
                            .anyMatch(allocation -> allocation.getAssetId().equals(asset)
                                    && allocation.getReleasedDateTime().isEmpty());
                    if (isAllocated) {
                        throw new HospitalAssetException(
                                String.format(
                                        "Hospital Asset '%s' cannot be deleted because it is currently allocated",
                                        asset.getAssetId()
                                )
                        );
                    }
                }
            }
            if (details.get(i).equalsIgnoreCase("totalBeds")) {
                if (!(object instanceof Integer)) {
                    throw new HospitalAssetException("Total beds must be a valid number");
                }
                int totalBeds = (Integer) object;
                if (totalBeds <= 0) {
                    throw new HospitalAssetException("Total beds must be greater than 0");
                }
            }
            i++;
        }
    }
    
    public static void billErrorHandler(List<String> details, Object... var) throws BillException, UserException {
        List<Bill> allBills = BillingManagement.getAllBills();
        List<Patient> allPatients = UserManagement.getAllPatients().stream().map(user -> (Patient) user).toList();

        try {
            if (details.size() != var.length) {
                throw new Exception("Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
            return;
        }

        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;
                if (details.get(i).equalsIgnoreCase("consultation")) {
                    if (data == null || data.equals("")) {
                        throw new BillException("Consultation ID cannot be null or empty");
                    }
                    // Prevent double-billing the same consultation
                    boolean alreadyBilled = allBills.stream()
                            .anyMatch(bill -> bill.getConsultationId().equals(data)
                                    && !Bill.BillStatus.VOID.equals(bill.getStatus()));
                    if (alreadyBilled) {
                        throw new BillException(String.format("Consultation '%s' already has an active bill", data));
                    }
                }
            }

            if (object instanceof Double) {
                double fee = (Double) object;
                if (details.get(i).equalsIgnoreCase("consultationFee")) {
                    if (fee <= 0) {
                        throw new BillException("Consultation fee must be greater than 0");
                    }
                }
            }

            if (object instanceof Patient) {
                Patient patient = (Patient) object;
                if (details.get(i).equalsIgnoreCase("patient")) {
                    if (!allPatients.stream().anyMatch(p -> p.equals(patient))) {
                        throw new UserException(String.format("Patient '%s' not found", patient.getName()));
                    }
                }
            }
            
            if (object instanceof Bill) {
                Bill bill = (Bill) object;

                if (details.get(i).equalsIgnoreCase("update bill")) {
                    if (!allBills.stream().anyMatch(b -> b.equals(bill))) {
                        throw new BillException(String.format("Bill '%s' not found", bill.getBillId()));
                    }
                }
                if (details.get(i).equalsIgnoreCase("cancel bill")) {
                    if (!allBills.stream().anyMatch(b -> b.equals(bill))) {
                        throw new BillException(String.format("Bill '%s' not found", bill.getBillId()));
                    }
                }
            }
            if (details.get(i).equalsIgnoreCase("admin staff")) {
                if (object == null) {
                    throw new BillException("Active User cannot be null");
                }
                if (!(object instanceof User)) {
                    throw new BillException("Invalid active user");
                }
                User user = (User) object;
                if (!"admin staff".equalsIgnoreCase(user.getType())) {
                    throw new BillException("Only Admin Staff can manage billing");
                }
            }
            i++;
        }
    }
    
    public static void insuranceErrorHandler(List<String> details, Object... var) throws InsuranceException, UserException {
        List<Insurance> allInsurances = InsuranceManagement.getAllInsurances();

        try {
            if (details.size() != var.length) {
                throw new Exception("Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
            return;
        }

        Insurance insuranceBeingUpdated = null; // NEW: track which insurance (if any) is being updated

        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;

                if (details.get(i).equalsIgnoreCase("insuranceName")) {
                    if (data == null || data.equals("")) {
                        throw new InsuranceException("Insurance name cannot be null or empty");
                    }
                    // Check duplicate insurance name (case-insensitive),
                    // excluding the insurance currently being updated (if any)
                    final Insurance excluded = insuranceBeingUpdated; // effectively-final for lambda
                    boolean nameExists = allInsurances.stream()
                            .anyMatch(insurance -> insurance.getInsuranceName().equalsIgnoreCase(data)
                                    && (excluded == null || !insurance.getInsuranceId().equals(excluded.getInsuranceId())));
                    if (nameExists) {
                        throw new InsuranceException(String.format("Insurance '%s' already exists", data));
                    }
                }
            }

            if (object instanceof Double) {
                double coveragePercentage = (Double) object;
                if (details.get(i).equalsIgnoreCase("coveragePercentage")) {
                    if (coveragePercentage < 0 || coveragePercentage > 1) {
                        throw new InsuranceException("Coverage percentage must be between 0 and 1 (0% - 100%)");
                    }
                }
            }

            if (object instanceof Insurance) {
                Insurance insurance = (Insurance) object;

                if (details.get(i).equalsIgnoreCase("insurance")) {
                    if (!allInsurances.stream().anyMatch(ins -> ins.equals(insurance))) {
                        throw new InsuranceException(String.format("Insurance '%s' not found", insurance.getInsuranceId()));
                    }
                }

                if (details.get(i).equalsIgnoreCase("update insurance")) {
                    if (!allInsurances.stream().anyMatch(ins -> ins.equals(insurance))) {
                        throw new InsuranceException(String.format("Insurance '%s' not found", insurance.getInsuranceId()));
                    }
                    insuranceBeingUpdated = insurance; // NEW: remember this for the name check below
                }
            }

            if (details.get(i).equalsIgnoreCase("admin staff")) {
                if (object == null) {
                    throw new InsuranceException("Active User cannot be null");
                }
                if (!(object instanceof User)) {
                    throw new InsuranceException("Invalid active user");
                }
                User user = (User) object;
                if (!"admin staff".equalsIgnoreCase(user.getType())) {
                    throw new InsuranceException("Only Admin Staff can manage insurance records");
                }
            }

            i++;
        }
    }
    
    public static void requestErrorHandler(List<String> details, Object... var) throws RequestException, UserException {
        List<Request> allRequests = RequestManagement.getAllRequests();

        try {
            if (details.size() != var.length) {
                throw new Exception("Invalid arguments passed: number of details set to check and number of data passed is conflicted");
            }
        } catch (Exception e) {
            System.out.println("Error Handling error: " + e.getMessage());
            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
            return;
        }

        User activeUserRef = null;
        Request requestRef = null;
        int i = 0;
        for (Object object : var) {
            if (object instanceof String) {
                String data = (String) object;

                if (details.get(i).equalsIgnoreCase("referenceId")) {
                    if (data == null || data.equals("")) {
                        throw new RequestException("Reference ID cannot be null or empty");
                    }
                } else if (details.get(i).equalsIgnoreCase("reason")) {
                    if (data == null || data.equals("")) {
                        throw new RequestException("Reason cannot be null or empty");
                    }
                }
            }

            if (object instanceof Request.RequestType) {
                if (details.get(i).equalsIgnoreCase("requestType")) {
                    if (object == null) {
                        throw new RequestException("Request type cannot be null");
                    }
                }
            }

            if (object instanceof User) {
                User user = (User) object;

                if (details.get(i).equalsIgnoreCase("requester")) {
                    if (user == null) {
                        throw new UserException("Active User cannot be null");
                    }
                    activeUserRef = user;
                }
            }

            if (object instanceof Request) {
                Request request = (Request) object;
                requestRef = request;

                if (details.get(i).equalsIgnoreCase("update request")) {
                    if (!allRequests.stream().anyMatch(r -> r.equals(request))) {
                        throw new RequestException(String.format("Request '%s' not found", request.getRequestId()));
                    }
                }

                if (details.get(i).equalsIgnoreCase("withdraw request")) {
                    if (!allRequests.stream().anyMatch(r -> r.equals(request))) {
                        throw new RequestException(String.format("Request '%s' not found", request.getRequestId()));
                    }
                }

                if (details.get(i).equalsIgnoreCase("resolve request")) {
                    if (!allRequests.stream().anyMatch(r -> r.equals(request))) {
                        throw new RequestException(String.format("Request '%s' not found", request.getRequestId()));
                    }
                    if (!Request.RequestStatus.PENDING.equals(request.getStatus())) {
                        throw new RequestException(
                                String.format("Request '%s' has already been resolved (%s)", request.getRequestId(), request.getStatus())
                        );
                    }
                }
            }
            i++;
        }

        // Ownership check: only the original requester can update/withdraw their own request
        if (requestRef != null && activeUserRef != null) {
            if (!requestRef.getRequester().equals(activeUserRef)) {
                throw new RequestException("You can only modify your own requests");
            }
        }
    }
}
