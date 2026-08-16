/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestController;

/**
 *
 * @author paure
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.models.AdminStaff;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;

public class TestUserController {
    private static UserController userCtrl;
 
    private static final String SAMPLE_USER_ID = "#ADMd4e8f";
    private static final String SAMPLE_PWD = "hehe456";
 
    public static void main(String[] args) {
 
        userCtrl = new UserController();
 
        testUserLogin();
 
        // Test loading
//        testGetAllUsers();
//        testGetAllAdminStaffs();
//        testGetAllMedicalManagers();
//        testGetAllDoctors();
//        testGetAllPatients();
 
        // Test lookup
//        testGetUserById();
//        testGetAdminStaffById();
//        testGetMedicalManagerById();
//        testGetDoctorById();
//        testGetPatientById();
 
        // Test search
//        testSearchUsers();
 
        // Test sort
//        testSortUsersById();
//        testSortUsersByName();
 
        // Test sub-controller wiring (confirms login correctly built the child controllers)
//        testSubControllersWired();
 
        // Test creation
        // Uncomment when needed because these modify users.txt AND write to the log file
        testCreateUser();
 
        // Test modification
        // Uncomment when needed because these modify users.txt AND write to the log file
//        testModifyUserName();
//        testModifyPwd();
//        testModifyEmail();
//        testAssignDoctorToMedicalManager();
//        testDeleteUser();
 
        // Test logout (do this last)
//        testUserLogout();
    }
 
    // =========================================================
    // LOGIN / LOGOUT TESTS
    // =========================================================
 
    static void testUserLogin() {
 
        System.out.println("\n===== TEST USER LOGIN (via Controller) =====");
 
        boolean success = userCtrl.userLogin(SAMPLE_USER_ID, SAMPLE_PWD);
 
        System.out.println("Login Success : " + success);
 
        if (success) {
            User active = userCtrl.getActiveUser();
            System.out.println("Active User   : " + active.getType() + " - " + active.getId() + " - " + active.getName());
        } else {
            System.out.println("Test failed: could not log in with the given credentials.");
        }
    }
 
    static void testUserLogout() {
 
        System.out.println("\n===== TEST USER LOGOUT (via Controller) =====");
 
        User active = userCtrl.getActiveUser();
 
        if (active == null) {
            System.out.println("No active user to log out.");
            return;
        }
 
        boolean success = userCtrl.userLogout(active);
 
        System.out.println("Logout Success : " + success);
        System.out.println("Active User after logout (should be null) : " + userCtrl.getActiveUser());
    }
 
    // =========================================================
    // LOAD TESTS
    // =========================================================
 
    static void testGetAllUsers() {
 
        System.out.println("\n===== TEST GET ALL USERS (via Controller) =====");
 
        List<User> users = userCtrl.getAllUsers();
 
        System.out.println("Total Users: " + users.size());
 
        for (User u : users) {
            System.out.println(u.getType() + " | " + u.getId() + " | " + u.getName());
        }
    }
 
    static void testGetAllAdminStaffs() {
 
        System.out.println("\n===== TEST GET ALL ADMIN STAFFS (via Controller) =====");
 
        List<User> staffs = userCtrl.getAllAdminStaffs();
 
        System.out.println("Total Admin Staffs: " + staffs.size());
    }
 
    static void testGetAllMedicalManagers() {
 
        System.out.println("\n===== TEST GET ALL MEDICAL MANAGERS (via Controller) =====");
 
        List<User> managers = userCtrl.getAllMedicalManagers();
 
        System.out.println("Total Medical Managers: " + managers.size());
    }
 
    static void testGetAllDoctors() {
 
        System.out.println("\n===== TEST GET ALL DOCTORS (via Controller) =====");
 
        List<User> doctors = userCtrl.getAllDoctors();
 
        System.out.println("Total Doctors: " + doctors.size());
    }
 
    static void testGetAllPatients() {
 
        System.out.println("\n===== TEST GET ALL PATIENTS (via Controller) =====");
 
        List<User> patients = userCtrl.getAllPatients();
 
        System.out.println("Total Patients: " + patients.size());
    }
 
    // =========================================================
    // LOOKUP TESTS
    // =========================================================
 
    static void testGetUserById() {
 
        System.out.println("\n===== TEST GET USER BY ID (via Controller) =====");
 
        User user = userCtrl.getUserById(SAMPLE_USER_ID);
 
        System.out.println(user != null ? "Found: " + user.getId() + " | " + user.getName() : "Test failed: not found.");
    }
 
    static void testGetAdminStaffById() {
 
        System.out.println("\n===== TEST GET ADMIN STAFF BY ID (via Controller) =====");
 
        AdminStaff staff = userCtrl.getAdminStaffById(SAMPLE_USER_ID);
 
        System.out.println(staff != null ? "Found: " + staff.getId() : "Test failed: not found.");
    }
 
    static void testGetMedicalManagerById() {
 
        System.out.println("\n===== TEST GET MEDICAL MANAGER BY ID (via Controller) =====");
 
        MedicalManager manager = userCtrl.getMedicalManagerById("#MMxxxxx"); // replace with a real ID
 
        System.out.println(manager != null ? "Found: " + manager.getId() : "No medical manager found for that ID.");
    }
 
    static void testGetDoctorById() {
 
        System.out.println("\n===== TEST GET DOCTOR BY ID (via Controller) =====");
 
        Doctor doctor = userCtrl.getDoctorById("#DOC001"); // replace with a real ID
 
        System.out.println(doctor != null ? "Found: " + doctor.getId() : "No doctor found for that ID.");
    }
 
    static void testGetPatientById() {
 
        System.out.println("\n===== TEST GET PATIENT BY ID (via Controller) =====");
 
        Patient patient = userCtrl.getPatientById("#PAT79a39"); // replace with a real ID
 
        System.out.println(patient != null ? "Found: " + patient.getId() : "No patient found for that ID.");
    }
 
    // =========================================================
    // SEARCH TEST
    // =========================================================
 
    static void testSearchUsers() {
 
        System.out.println("\n===== TEST SEARCH USERS (via Controller) =====");
 
        String keyword = "John"; // replace with a name/id that actually exists in your data
 
        List<User> result = userCtrl.searchUsers(keyword);
 
        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched users: " + result.size());
    }
 
    // =========================================================
    // SORT TESTS
    // =========================================================
 
    static void testSortUsersById() {
 
        System.out.println("\n===== TEST SORT USERS BY ID (via Controller) =====");
 
        List<User> sorted = userCtrl.sortUsersById(userCtrl.getAllUsers());
 
        for (User u : sorted) {
            System.out.println(u.getId());
        }
    }
 
    static void testSortUsersByName() {
 
        System.out.println("\n===== TEST SORT USERS BY NAME (via Controller) =====");
 
        List<User> sorted = userCtrl.sortUsersByName(userCtrl.getAllUsers());
 
        for (User u : sorted) {
            System.out.println(u.getName());
        }
    }
 
    // =========================================================
    // SUB-CONTROLLER WIRING TEST
    // =========================================================
 
    static void testSubControllersWired() {
 
        System.out.println("\n===== TEST SUB-CONTROLLERS ARE WIRED AFTER LOGIN (via Controller) =====");
 
        // Confirms userLogin() correctly instantiated each child controller with the active user.
        // Note: consultationController and hospitalAssetController are NOT built in userLogin()
        // in your current UserController - they stay null unless you wire them up too.
        System.out.println("BillingController        : " + (userCtrl.getBillingController() != null));
        System.out.println("ConsultationController    : " + (userCtrl.getConsultationController() != null) + "  (expected false - not wired in userLogin)");
        System.out.println("DoctorController          : " + (userCtrl.getDoctorController() != null));
        System.out.println("HospitalAssetController   : " + (userCtrl.getHospitalAssetController() != null) + "  (expected false - not wired in userLogin)");
        System.out.println("InsuranceController       : " + (userCtrl.getInsuranceController() != null));
        System.out.println("MedicalRecordController   : " + (userCtrl.getMedicalRecordController() != null));
        System.out.println("RequestController         : " + (userCtrl.getRequestController() != null));
    }
 
    // =========================================================
    // CREATE TEST
    // =========================================================
 
    static void testCreateUser() {
 
        System.out.println("\n===== TEST CREATE USER (via Controller) =====");
 
        LinkedHashMap<String, String> result = userCtrl.createUser(
                "chuaa",
                "Testpwd@123",
                "patient",
                "chua1@gmail.com"
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New User ID : " + result.get("newUserId"));
 
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    // =========================================================
    // MODIFY TESTS
    // =========================================================
 
    static void testModifyUserName() {
 
        System.out.println("\n===== TEST MODIFY USER NAME (via Controller) =====");
 
        List<User> users = userCtrl.getAllUsers();
 
        if (users.isEmpty()) {
            System.out.println("No users available to modify.");
            return;
        }
 
        String userId = users.get(users.size() - 1).getId();
 
        LinkedHashMap<String, String> result = userCtrl.modifyUserName("Updated Name via Test", userId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testModifyPwd() {
 
        System.out.println("\n===== TEST MODIFY PASSWORD (via Controller) =====");
 
        List<User> users = userCtrl.getAllUsers();
 
        if (users.isEmpty()) {
            System.out.println("No users available to modify.");
            return;
        }
 
        String userId = users.get(users.size() - 1).getId();
 
        LinkedHashMap<String, String> result = userCtrl.modifyPwd("newpassword456", userId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
    
    static void testModifyEmail() {

        System.out.println("\n===== TEST MODIFY EMAIL (via Controller) =====");

        List<User> users = userCtrl.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No users available to modify.");
            return;
        }

        String userId = users.get(users.size() - 1).getId();

        LinkedHashMap<String, String> result = userCtrl.modifyEmail("updatedemail@example.com", userId);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testAssignDoctorToMedicalManager() {
 
        System.out.println("\n===== TEST ASSIGN DOCTOR TO MEDICAL MANAGER (via Controller) =====");
 
        Doctor doctor = userCtrl.getDoctorById("#DOC001"); // replace with real IDs
        MedicalManager manager = userCtrl.getMedicalManagerById("#MMxxxxx");
 
        if (doctor == null || manager == null) {
            System.out.println("Doctor or Medical Manager not found.");
            return;
        }
 
        LinkedHashMap<String, String> result = userCtrl.assignDoctorToMedicalManager(doctor, manager);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testDeleteUser() {
 
        System.out.println("\n===== TEST DELETE USER (via Controller) =====");
 
        List<User> users = userCtrl.getAllUsers();
 
        if (users.isEmpty()) {
            System.out.println("No users available to delete.");
            return;
        }
 
        String userId = users.get(users.size() - 1).getId();
 
        LinkedHashMap<String, String> result = userCtrl.deleteUser(userId);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}
