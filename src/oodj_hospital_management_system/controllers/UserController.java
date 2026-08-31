/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.AdminStaff;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.Patient;

/**
 *
 * @author paure
 */
public class UserController {
    private UserManagement userServices;
    private BillingController billingController;
    private ConsultationController consultationController;
    private DoctorController doctorController;
    private HospitalAssetController hospitalAssetController;
    private InsuranceController insuranceController;
    private MedicalRecordController medicalRecordController;
    private ReportController reportController;
    private RequestController requestController;
    private ConsultationTypeController consultationTypeController;
    private LabResultTypeController labResultTypeController;
    private PrescriptionTypeController prescriptionTypeController;
    private User activeUser;
    
    public UserController() {
        this.activeUser = null;
        this.userServices = null;
        this.billingController = null;
        this.consultationController = null;
        this.doctorController = null;
        this.hospitalAssetController = null;
        this.insuranceController = null;
        this.medicalRecordController = null;
        this.reportController = null;
        this.requestController = null;
        this.consultationTypeController = null;
        this.labResultTypeController = null;
        this.prescriptionTypeController = null;
    }
    
    public User getActiveUser() {
        return this.activeUser;
    }

    public UserManagement getUserServices() {
        return this.userServices;
    }

    public BillingController getBillingController() {
        return this.billingController;
    }

    public ConsultationController getConsultationController() {
        return this.consultationController;
    }

    public DoctorController getDoctorController() {
        return this.doctorController;
    }
    
    public HospitalAssetController getHospitalAssetController() {
        return this.hospitalAssetController;
    }
    
    public InsuranceController getInsuranceController() {
        return this.insuranceController;
    }
    
    public MedicalRecordController getMedicalRecordController() {
        return this.medicalRecordController;
    }
    
    public ReportController getReportController() {
        return this.reportController;
    }
    
    public RequestController getRequestController() {
        return this.requestController;
    }
    
    public ConsultationTypeController getConsultationTypeController() {
        return this.consultationTypeController;
    }
    
    public LabResultTypeController getLabResultController() {
        return this.labResultTypeController;
    }
    
    public PrescriptionTypeController getPrescriptionTypeController() {
        return this.prescriptionTypeController;
    }
    
    // Get All Users
    public List<User> getAllUsers() {
        return UserManagement.getAllUsers();
    }
    
    public List<User> getAllAdminStaffs() {
        return UserManagement.getAllAdminStaffs();
    }
 
    public List<User> getAllMedicalManagers() {
        return UserManagement.getAllMedicalManagers();
    }
 
    public List<User> getAllDoctors() {
        return UserManagement.getAllDoctors();
    }
 
    public List<User> getAllPatients() {
        return UserManagement.getAllPatients();
    }
    
    // Lookup by ID
    public User getUserById(String userId) {
        return UserManagement.getUserById(userId);
    }
 
    public AdminStaff getAdminStaffById(String adminId) {
        return UserManagement.getAdminStaffById(adminId);
    }
 
    public MedicalManager getMedicalManagerById(String managerId) {
        return UserManagement.getMedicalManagerById(managerId);
    }
 
    public Doctor getDoctorById(String doctorId) {
        return UserManagement.getDoctorById(doctorId);
    }
 
    public Patient getPatientById(String patientId) {
        return UserManagement.getPatientById(patientId);
    }
    
    // Search and Sort
    public List<User> searchUsers(String keywords) {
        return userServices.searchUsers(keywords);
    }
    
    public List<User> searchUsers(String keywords, List<User> billList) {
        return userServices.searchUsers(keywords, billList);
    }
    
    public List<User> sortUsersById(List<User> list) {
        return UserManagement.sortUsersById(list);
    }

    public List<User> sortUsersByName(List<User> list) {
        return UserManagement.sortUsersByName(list);
    }

    // Login & Logout
    public boolean userLogin(String userId, String pwd) {
        List<User> allUsers = UserManagement.getAllUsers();
        for (User user : allUsers) {
            if (user.getId().equals(userId) && user.getPwd().equals(pwd)) {
                User loginUser = user;
                this.activeUser = loginUser;
                this.userServices = new UserManagement(this.activeUser);
                this.billingController = new BillingController(this.activeUser);
                this.consultationController = new ConsultationController(this.activeUser);
                this.doctorController = new DoctorController(this.activeUser);
                this.hospitalAssetController = new HospitalAssetController(this.activeUser);
                this.insuranceController = new InsuranceController(this.activeUser);
                this.medicalRecordController = new MedicalRecordController(this.activeUser);
                this.reportController = new ReportController(this.activeUser);
                this.requestController = new RequestController(this.activeUser);
                this.consultationTypeController = new ConsultationTypeController(this.activeUser);
                this.labResultTypeController = new LabResultTypeController(this.activeUser);
                this.prescriptionTypeController = new PrescriptionTypeController(this.activeUser);
                Logger.log(String.format("User {%s - %s - %s} logged in", activeUser.getType(), userId, activeUser.getName()));
                return true;
            }
        }
//        if (UserManagement.getSuperAdmin().getId().equals(userId) && UserManagement.getSuperAdmin().getPwd().equals(pwd)) {
//            this.activeUser = UserManagement.getSuperAdmin();
//            this.userServices = new UserManagement(this.activeUser);
//            this.transactionController = new TransactionController(this.activeUser);
//            this.ppeController = new PPEController(this.activeUser);
//            this.supplierController = new SupplierController(this.activeUser);
//            this.hospitalController = new HospitalController(this.activeUser);
//            Logger.log(String.format("User {%s - %s - %s} logged in", this.activeUser.type, userId, this.activeUser.getName()));
//            return true;
//        }
        Logger.errorLog(String.format("User with ID '%s' failed to logged in\n\tErrorMsg: %s", userId, "Incorrect Login Credentials"));
        return false;
    }
    
    // update active user upon modifications
    public boolean updateActiveUser(User updatedActiveUser) {
        this.userServices = new UserManagement(this.activeUser);
        this.billingController = new BillingController(this.activeUser);
        this.consultationController = new ConsultationController(this.activeUser);
        this.doctorController = new DoctorController(this.activeUser);
        this.hospitalAssetController = new HospitalAssetController(this.activeUser);
        this.insuranceController = new InsuranceController(this.activeUser);
        this.medicalRecordController = new MedicalRecordController(this.activeUser);
        //                this.reportController = new ReportController(this.activeUser);
        this.requestController = new RequestController(this.activeUser);
        this.consultationTypeController = new ConsultationTypeController(this.activeUser);
        this.labResultTypeController = new LabResultTypeController(this.activeUser);
        this.prescriptionTypeController = new PrescriptionTypeController(this.activeUser);
        return true;
    }

    // used User as argument
    public boolean userLogout(User logoutUser) {
        Logger.log(String.format("User {%s - %s - %s} logged out", logoutUser.getType(), logoutUser.getId(), logoutUser.getName()));
        this.userServices = new UserManagement(this.activeUser);
        this.billingController = new BillingController(this.activeUser);
        this.consultationController = new ConsultationController(this.activeUser);
        this.doctorController = new DoctorController(this.activeUser);
        this.hospitalAssetController = new HospitalAssetController(this.activeUser);
        this.insuranceController = new InsuranceController(this.activeUser);
        this.medicalRecordController = new MedicalRecordController(this.activeUser);
        //        this.reportController = new ReportController(this.activeUser);
        this.requestController = new RequestController(this.activeUser);
        this.consultationTypeController = new ConsultationTypeController(this.activeUser);
        this.labResultTypeController = new LabResultTypeController(this.activeUser);
        this.prescriptionTypeController = new PrescriptionTypeController(this.activeUser);
        return true;
    }
    
    // CRUD
    public LinkedHashMap<String, String> createUser(String name, String pwd, String type, String email) {
        LinkedHashMap<String, String> msg = userServices.createUser(name, pwd, type, email);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            User newUser = UserManagement.getUserById(msg.get("newUserId"));
            Logger.log(String.format("User {%s - %s - %s} created a new '%s' account, User {%s - %s}", activeUser.getType(), activeUser.getId(), activeUser.getName(), newUser.getType(), newUser.getId(), newUser.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to create a new user account\n\tErrorMsg: %s", activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("msg")));
        }
        return msg;
    }
    
    public LinkedHashMap<String, String> modifyUserName(String newName, String userId) {
        User user = UserManagement.getUserById(userId);
        LinkedHashMap<String, String> msg = userServices.modifyUserName(newName, userId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified name for User {%s - %s - %s}", activeUser.getType(), activeUser.getId(), activeUser.getName(), user.getType(), user.getId(), user.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify name for User {%s - %s - %s}\n\tErrorMsg: %s", activeUser.getType(), activeUser.getId(), activeUser.getName(), user.getType(), user.getId(), user.getName(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> modifyPwd(String newPwd, String userId) {
        User user = UserManagement.getUserById(userId);
        LinkedHashMap<String, String> msg = userServices.modifyUserPwd(newPwd, userId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified password for User {%s - %s - %s}", activeUser.getType(), activeUser.getId(), activeUser.getName(), user.getType(), user.getId(), user.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify password for User {%s - %s - %s}\n\tErrorMsg: %s", activeUser.getType(), activeUser.getId(), activeUser.getName(), user.getType(), user.getId(), user.getName(), msg.get("msg")));
        }
        return msg;
    }
    
    public LinkedHashMap<String, String> modifyEmail(String newEmail, String userId) {
        User user = UserManagement.getUserById(userId);
        LinkedHashMap<String, String> msg = userServices.modifyUserEmail(newEmail, userId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} modified email for User {%s - %s - %s}", activeUser.getType(), activeUser.getId(), activeUser.getName(), user.getType(), user.getId(), user.getName()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to modify email for User {%s - %s - %s}\n\tErrorMsg: %s", activeUser.getType(), activeUser.getId(), activeUser.getName(), user.getType(), user.getId(), user.getName(), msg.get("msg")));
        }
        return msg;
    }
 
    public LinkedHashMap<String, String> deleteUser(String userId) {
        LinkedHashMap<String, String> msg = userServices.deleteUser(userId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} removed User {%s - %s - %s}", activeUser.getType(), activeUser.getId(), activeUser.getName(), msg.get("deleteUserType"), msg.get("deleteUserId"), msg.get("deleteUserName")));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to remove User {%s - %s - %s}\n\tErrorMsg: %s", activeUser.getType(), activeUser.getId(), activeUser.getName(), "?", userId, "?", msg.get("msg")));
        }
        return msg;
    }
    
    public LinkedHashMap<String, String> updateDoctorDepartment(String doctorId, String departmentId) {
        LinkedHashMap<String, String> msg = userServices.updateDoctorDepartment(doctorId, departmentId);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} updated Doctor {%s}'s Medical Department to {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), doctorId, departmentId));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to update Doctor {%s}'s Medical Department\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), doctorId, msg.get("msg")));
        }
        return msg;
    }
    
    public LinkedHashMap<String, String> assignDoctorToMedicalManager(Doctor doctor, MedicalManager medicalManager) {
        LinkedHashMap<String, String> msg = userServices.assignDoctorToMedicalManager(doctor, medicalManager);
        if (msg.get("success") != null) {
            System.out.println(msg.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} assigned Doctor {%s} to Medical Manager {%s}",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    doctor.getId(), medicalManager.getId()));
        } else {
            System.out.println(msg.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to assign Doctor {%s} to Medical Manager {%s}\n\tErrorMsg: %s",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(),
                    doctor.getId(), medicalManager.getId(), msg.get("msg")));
        }
        return msg;
    }
    
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
