/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.AdminStaff;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.ErrorHandler;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.exceptions.UserException;

/**
 *
 * @author sheaw
 */
public class UserManagement {
    private static final FileHandler superAdminhandler = new FileHandler(SystemConfiguration.superAdminFilePath);
    private static final FileHandler handler = new FileHandler(SystemConfiguration.userFilePath);
    // private static List<User> admins = new ArrayList<>();
    private static List<User> allUsers = new ArrayList<>();
    private static List<User> adminStaffs = new ArrayList<>();
    private static List<User> medicalManagers = new ArrayList<>();
    private static List<User> doctors = new ArrayList<>();
    private static List<User> patients = new ArrayList<>();
    private static List<User> superAdmin = new ArrayList<>();

    private static ArrayList<LinkedHashMap<String, String>> permissions = new ArrayList<>();
    private User activeUser;
    
    private static boolean loaded = false;
    private static String loadedFingerprint = null;
    private static int loadVersion = 0;
    
    public UserManagement(User user) {
        this.activeUser = user;
        ensureLoaded();
        loadSuperAdmin();
    }
    
    public static User getSuperAdmin() {
        loadSuperAdmin();
        return UserManagement.superAdmin.get(0);
    }
    
    public User getActiveUser() {
        return this.activeUser;
    }
    
    private static void ensureLoaded() {
        String current = handler.fingerprint();
        if (!loaded || current == null || !current.equals(loadedFingerprint)) {
            loadUsers();
        }
    }
    
    public static int getLoadVersion() {
        ensureLoaded();
        return loadVersion;
    }
    
    public static void loadUsers() {
        String fingerprint = handler.fingerprint();
        allUsers.clear();
        adminStaffs.clear();
        medicalManagers.clear();
        doctors.clear();
        patients.clear();
        List<LinkedHashMap<String, String>> rawData = handler.readFile();
        
        // STEP 1: Create all basic User objects
        for (LinkedHashMap<String, String> row : rawData) {
            String type = row.get("userType");
            switch (type) {
                case "admin staff":
                    AdminStaff admin = new AdminStaff(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"), row.get("email"));
                    adminStaffs.add(admin);
                    allUsers.add(admin);
                    break;
                case "medical manager":
                    MedicalManager manager = new MedicalManager(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"), row.get("email"));
                    medicalManagers.add(manager);
                    allUsers.add(manager);
                    break;
                case "doctor":
                    Doctor doctor = new Doctor(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"), row.get("email"));
                    doctors.add(doctor);
                    allUsers.add(doctor);
                    break;
                case "patient":
                    Patient patient = new Patient(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"), row.get("email"));
                    patients.add(patient);
                    allUsers.add(patient);
                    break;
                default:
                    System.out.println("Unknown user type '" + type + "' for user " + row.get("userId") + ", skipping.");
                    break;
            }
        }

        // STEP 2: Restore Doctor-specific information
        for (LinkedHashMap<String, String> row : rawData) {
            if (!"doctor".equals(row.get("userType"))) {
                continue;
            }
            Doctor doctor = findDoctorByIdWithoutReload(row.get("userId"));
            if (doctor == null) {
                continue;
            }
            // Restore Medical Manager
            String medicalManagerId = row.get("medicalManagerId");
            if (medicalManagerId != null && !medicalManagerId.isEmpty()) {
                MedicalManager manager = findMedicalManagerByIdWithoutReload(medicalManagerId);
                doctor.setMedicalManager(manager);
            }
            // Restore Medical Department
            String departmentId = row.get("departmentId");
            if (departmentId != null && !departmentId.isEmpty()) {
                doctor.setMedicalDepartment(DoctorManagement.getMedicalDepartmentById(departmentId));
            }
        }
        loaded = true;
        loadedFingerprint = fingerprint;
        loadVersion++;
    }
    
    public static void loadSuperAdmin() {
        superAdmin.clear();
        List<LinkedHashMap<String, String>> rawData = superAdminhandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            AdminStaff superAdm = new AdminStaff(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), "", row.get("email"));
            superAdmin.add(superAdm);
        }
    }
    
    public static boolean isSuperAdmin(User user) {
        return (user.getId().equals(superAdmin.get(0).getId()));
    }

    // Accessor Method for Users
    public static List<User> getAllUsers() {
        ensureLoaded();
        return allUsers;
    }
    
    public static List<User> getAllAdminStaffs() {
        ensureLoaded();
        return adminStaffs;
    }
    
    public static List<User> getAllMedicalManagers() {
        ensureLoaded();
        return medicalManagers;
    }
    
    public static List<User> getAllDoctors() {
        ensureLoaded();
        return doctors;
    }
    
    public static List<User> getAllPatients() {
        ensureLoaded();
        return patients;
    }
    
    // File Handler (Write and Append)
    private static boolean writeSuperAdmin() {
        ArrayList<LinkedHashMap<String, String>> admins = new ArrayList<>();
        for (User admin : superAdmin) {
            admins.add(admin.getUserMap());
        }
        superAdminhandler.writeFile(admins);
        return true;
    }
    
    private static boolean writeUsers() {
        ArrayList<LinkedHashMap<String, String>> users = new ArrayList<>();
        for (User user : allUsers) {
            users.add(user.getUserMap());
        }
        handler.writeFile(users);
        return true;
    }
    
    private static boolean appendUser(User user) {
        try {
            ArrayList<LinkedHashMap<String, String>> users = new ArrayList<>();
            users.add(user.getUserMap());
            if (!handler.checkFileExistence() && !handler.createFile()) {
                System.out.printf("File 'users.txt' failed to be created, user '%s' cannot be appended%n",
                        user.getName());
                return false;
            }
            handler.appendFile(users);
            System.out.printf("User '%s' is appended to users.txt!%n", user.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new user: " + e.getMessage());
            Logger.errorLog("Error appending new user (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }
    
    // CRUD - mutator methods
    public LinkedHashMap<String, String> createUser(String name, String pwd, String type, String email) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.userErrorHandler(Arrays.asList("name", "pwd", "type", "email"), name, pwd, type, email);
        } catch (UserException e) {
            System.out.println("Error creating user\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' is not created\n", name) + e.getMessage());
            return msgMap;
        }
        if (allUsers.stream().anyMatch(user -> email.equalsIgnoreCase(user.getEmail()))) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' is not created: email '%s' is already in use", name, email));
            return msgMap;
        }
        if (checkPermission(type)) {
            String newUserId = generateUserId(type);
            String currentDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
            User newUser;
            switch (type) {
                case "doctor":
                    newUser = new Doctor(newUserId, name, pwd, type, currentDateTime, email);
                    break;
                case "patient":
                    newUser = new Patient(newUserId, name, pwd, type, currentDateTime, email);
                    break;
                case "medical manager":
                    newUser = new MedicalManager(newUserId, name, pwd, type, currentDateTime, email);
                    break;
                case "admin staff":
                    newUser = new AdminStaff(newUserId, name, pwd, type, currentDateTime, email);
                    break;
                default:
                    msgMap.put("success", null);
                    msgMap.put("msg", String.format("User '%s' is not created: unknown user type '%s'", name, type));
                    return msgMap;
            }

            allUsers.add(newUser);
            if (handler.checkFileExistence()) {
                appendUser(newUser);
            } else {
                writeUsers();
            }
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' is created and stored!", name));
            msgMap.put("newUserId", newUserId);
            return msgMap;
        } else {
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("User '%s' is not created: you have no access to create '%s' users", name, type));
            return msgMap;
        }
    }

    public LinkedHashMap<String, String> modifyUserName(String newName, String userId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        // Handle super admin separately — not part of allUsers
        if (superAdmin.get(0).getId().equals(userId)) {
            try {
                ErrorHandler.userErrorHandler(Arrays.asList("name"), newName);
            } catch (UserException e) {
                System.out.println("Error modifying user name\n" + e.getMessage());
                msgMap.put("success", null);
                msgMap.put("msg", String.format("User '%s''s name is not modified\n", userId) + e.getMessage());
                return msgMap;
            }
            superAdmin.get(0).setName(newName);
            writeSuperAdmin();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' name is modified!", userId));
            return msgMap;
        }

        try {
            User user = getUserById(userId);
            ErrorHandler.userErrorHandler(Arrays.asList("name", "user"), newName, user);
        } catch (UserException e) {
            System.out.println("Error modifying user name\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s name is not modified\n", userId) + e.getMessage());
            return msgMap;
        }
        User modifyUser = getUserById(userId);
        if (modifyUser == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' not found", userId));
            return msgMap;
        }
        if (checkPermission(modifyUser.getType()) || this.activeUser.equals(modifyUser)) {
            for (User user : allUsers) {
                if (user.getId().equals(modifyUser.getId())) {
                    user.setName(newName);
                    break;
                }
            }
            writeUsers();
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' name is modified!", userId));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' name is not modified: you have no access to modify '%s' users",
                    userId, modifyUser.getType()));
        }
        return msgMap;
    }

    public LinkedHashMap<String, String> modifyUserEmail(String newEmail, String userId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        boolean isSuperAdminTarget = superAdmin.get(0).getId().equals(userId);

        try {
            ErrorHandler.userErrorHandler(Arrays.asList("email"), newEmail);
        } catch (UserException e) {
            System.out.println("Error modifying user email\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s email is not modified\n", userId) + e.getMessage());
            return msgMap;
        }

        // also guard against colliding with the super admin's own email
        boolean emailTaken = allUsers.stream().anyMatch(user -> !user.getId().equals(userId) && newEmail.equalsIgnoreCase(user.getEmail()))
                || (!isSuperAdminTarget && newEmail.equalsIgnoreCase(superAdmin.get(0).getEmail()));
        if (emailTaken) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s email is not modified: email '%s' is already in use", userId, newEmail));
            return msgMap;
        }

        if (isSuperAdminTarget) {
            superAdmin.get(0).setEmail(newEmail);
            writeSuperAdmin();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' email is modified!", userId));
            return msgMap;
        }

        User modifyUser = getUserById(userId);
        if (modifyUser == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' not found", userId));
            return msgMap;
        }
        if (checkPermission(modifyUser.getType()) || this.activeUser.equals(modifyUser)) {
            for (User user : allUsers) {
                if (user.getId().equals(modifyUser.getId())) {
                    user.setEmail(newEmail);
                    break;
                }
            }
            writeUsers();
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' email is modified!", userId));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' email is not modified: you have no access to modify '%s' users",
                    userId, modifyUser.getType()));
        }
        return msgMap;
    }
 
    public LinkedHashMap<String, String> modifyUserPwd(String newPwd, String userId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        if (superAdmin.get(0).getId().equals(userId)) {
            try {
                ErrorHandler.userErrorHandler(Arrays.asList("pwd"), newPwd);
            } catch (UserException e) {
                System.out.println("Error modifying user password\n" + e.getMessage());
                msgMap.put("success", null);
                msgMap.put("msg", String.format("User '%s''s password is not modified\n", userId) + e.getMessage());
                return msgMap;
            }
            superAdmin.get(0).setPwd(newPwd);
            writeSuperAdmin();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' password is modified!", userId));
            return msgMap;
        }

        try {
            User user = getUserById(userId);
            ErrorHandler.userErrorHandler(Arrays.asList("pwd", "user"), newPwd, user);
        } catch (UserException e) {
            System.out.println("Error modifying user password\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s password is not modified\n", userId) + e.getMessage());
            return msgMap;
        }
        User modifyUser = getUserById(userId);
        if (modifyUser == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' not found", userId));
            return msgMap;
        }
        if (checkPermission(modifyUser.getType()) || this.activeUser.equals(modifyUser)) {
            for (User user : allUsers) {
                if (user.getId().equals(modifyUser.getId())) {
                    user.setPwd(newPwd);
                    break;
                }
            }
            writeUsers();
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' password is modified!", userId));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("User '%s' password is not modified: you have no access to modify '%s' users", userId,
                            modifyUser.getType()));
        }
        return msgMap;
    }
    
    public LinkedHashMap<String, String> updateDoctorDepartment(String doctorId, String departmentId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Doctor doctor = getDoctorById(doctorId);
        if (doctor == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Doctor department is not updated: Doctor '%s' not found", doctorId));
            return msgMap;
        }

        MedicalDepartment department = DoctorManagement.getMedicalDepartmentById(departmentId);
        if (department == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Doctor department is not updated: Medical Department '%s' not found", departmentId));
            return msgMap;
        }

        // Update doctor in allUsers
        for (User user : allUsers) {
            if (user instanceof Doctor) {
                Doctor storedDoctor = (Doctor) user;
                if (storedDoctor.equals(doctor)) {
                    storedDoctor.setMedicalDepartment(department);
                    break;
                }
            }
        }
        // Save updated user information
        writeUsers();
        loadUsers();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Doctor '%s' has been assigned to Medical Department '%s'!", doctor.getName(), department.getDepartmentName()));
        return msgMap;
    }
    
    public static LinkedHashMap<String, String> clearDoctorDepartment(String departmentId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        int clearedCount = 0;
        for (User user : allUsers) {
            if (user instanceof Doctor) {
                Doctor storedDoctor = (Doctor) user;
                MedicalDepartment doctorDept = storedDoctor.getMedicalDepartment();
                if (doctorDept != null
                        && doctorDept.getDepartmentId() != null
                        && doctorDept.getDepartmentId().equals(departmentId)) {
                    storedDoctor.setMedicalDepartment(new MedicalDepartment()); // placeholder, matches no-arg Doctor() constructor convention
                    clearedCount++;
                }
            }
        }
        if (clearedCount > 0) {
            writeUsers();
            loadUsers();
        }
        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Cleared department '%s' from %d doctor(s)", departmentId, clearedCount));
        msgMap.put("clearedCount", String.valueOf(clearedCount));
        return msgMap;
    }
    
    public LinkedHashMap<String, String> deleteUser(String userId) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        User deleteUser = null;
        for (User user : allUsers) {
            if (user.getId().equals(userId)) {
                deleteUser = user;
                break;
            }
        }
        if (checkPermission(deleteUser.getType())) {
            allUsers.removeIf(user -> user.getId().equals(userId));
            writeUsers();
            loadUsers();
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' is deleted!", userId));
            msgMap.put("deleteUserType", deleteUser.getType());
            msgMap.put("deleteUserId", deleteUser.getId());
            msgMap.put("deleteUserName", deleteUser.getName());
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' is not deleted: you have no access to delete '%s' users", userId,
                    deleteUser.getType()));
        }
        return msgMap;
    }
    
    public static User getUserOrSuperAdminById(String userId) {
        if (getSuperAdmin().getId().equals(userId)) {
            return getSuperAdmin();
        }
        return getUserById(userId);
    }
    
    public static User getUserById(String userId) {
        ensureLoaded();
        try {
            if (!allUsers.stream().anyMatch(user -> user.getId().equals(userId))) {
                throw new UserException(String.format("Invalid User ID: User ID '%s' is not found", userId));
            }
            for (User user : allUsers) {
                if (user.getId().equals(userId)) {
                    return user;
                }
            }
            return null;
        } catch (UserException e) {
            System.out.println("Error finding user -> " + e.getMessage());
            Logger.errorLog("Error finding user -> " + e.getMessage());
            return null;
        }
    }
    
    public static AdminStaff getAdminStaffById(String adminId) {
        ensureLoaded();
        try {
            if (!adminStaffs.stream().anyMatch(
                    adminStaff -> adminStaff.getId().equals(adminId))) {
                throw new UserException(
                        String.format("Invalid Admin Staff ID: Admin Staff ID '%s' is not found", adminId)
                );
            }
            for (User adminStaff : adminStaffs) {
                if (adminStaff.getId().equals(adminId)) {
                    AdminStaff a = (AdminStaff) adminStaff;
                    return a;
                }
            }
            return null;
        } catch (UserException e) {
            System.out.println("Error finding medical manager -> " + e.getMessage());
            Logger.errorLog("Error finding medical manager -> " + e.getMessage());
            return null;
        }
    }
    
    public static MedicalManager getMedicalManagerById(String managerId) {
        ensureLoaded();
        try {
            if (!medicalManagers.stream().anyMatch(
                    medicalManager -> medicalManager.getId().equals(managerId))) {
                throw new UserException(
                        String.format("Invalid Medical Manager ID: Medical Manager ID '%s' is not found", managerId)
                );
            }
            for (User medicalManager : medicalManagers) {
                if (medicalManager.getId().equals(managerId)) {
                    MedicalManager m = (MedicalManager) medicalManager;
                    return m;
                }
            }
            return null;
        } catch (UserException e) {
            System.out.println("Error finding medical manager -> " + e.getMessage());
            Logger.errorLog("Error finding medical manager -> " + e.getMessage());
            return null;
        }
    }
    
    public static Doctor getDoctorById(String doctorId) {
        ensureLoaded();
        try {
            if (!doctors.stream().anyMatch(
                    doctor -> doctor.getId().equals(doctorId))) {
                throw new UserException(
                        String.format("Invalid Doctor ID: Doctor ID '%s' is not found", doctorId)
                );
            }
            for (User doctor : doctors) {
                if (doctor.getId().equals(doctorId)) {
                    Doctor d = (Doctor) doctor;
                    return d;
                }
            }
            return null;
        } catch (UserException e) {
            System.out.println("Error finding doctor -> " + e.getMessage());
            Logger.errorLog("Error finding doctor -> " + e.getMessage());
            return null;
        }
    }
    
    public static Patient getPatientById(String patientId) {
        ensureLoaded();
        try {
            if (!patients.stream().anyMatch(
                    patient -> patient.getId().equals(patientId))) {
                throw new UserException(
                        String.format("Invalid Patient ID: Patient ID '%s' is not found", patientId)
                );
            }
            for (User patient : patients) {
                if (patient.getId().equals(patientId)) {
                    Patient p = (Patient) patient;
                    return p;
                }
            }
            return null;
        } catch (UserException e) {
            System.out.println("Error finding patient -> " + e.getMessage());
            Logger.errorLog("Error finding patient -> " + e.getMessage());
            return null;
        }
    }
    
    private static MedicalManager findMedicalManagerByIdWithoutReload(String managerId) {
        for (User manager : medicalManagers) {
            if (manager.getId().equals(managerId)) {
                return (MedicalManager) manager;
            }
        }
        return null;
    }
    
    private static Doctor findDoctorByIdWithoutReload(String doctorId) {
        for (User doctor : doctors) {
            if (doctor.getId().equals(doctorId)) {
                return (Doctor)doctor;
            }
        }
        return null;
    }

    // Searching and Sorting
    public List<User> searchUsers(String keywords) {
        ensureLoaded();
        List<User> matchedUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || user.getName().toLowerCase().contains(keywords.toLowerCase().trim()) || user.getEmail().contains(keywords.toLowerCase().trim())) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }

    public static List<User> searchUsers(String keywords, List<User> userList) {
        List<User> matchedUsers = new ArrayList<>();
        for (User user : userList) {
            if (user.getId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || user.getName().toLowerCase().contains(keywords.toLowerCase().trim()) || user.getEmail().contains(keywords.toLowerCase().trim())) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }
    
    public static List<User> searchUsersByDateRange(String startDate, String endDate, List<User> users) {
        List<User> results = new ArrayList<>();
        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : null;

        for (User usr : users) {
            LocalDateTime joined = DateTime.toDateTimeObject(usr.getJoinedDateTime());
            LocalDate joinedDate = joined.toLocalDate();

            boolean afterStart = (start == null) || !joinedDate.isBefore(start);
            boolean beforeEnd = (end == null) || !joinedDate.isAfter(end);

            if (afterStart && beforeEnd) {
                results.add(usr);
            }
        }
        return results;
    }
    
    public static List<User> sortUsersById(List<User> list) {
        List<User> sortedUsers = list;
        sortedUsers.sort(Comparator.comparing(User::getId));
        return sortedUsers;
    }

    public static List<User> sortUsersByName(List<User> list) {
        List<User> sortedUsers = list;
        sortedUsers.sort(Comparator.comparing(User::getName));
        return sortedUsers;
    }
    
    // Generate ID annd Temp Password
    public static String generateUserId(String userType) {
        ensureLoaded();
        if (!Arrays.asList(SystemConfiguration.userTypes).contains(userType)) {
            throw new IllegalArgumentException("Invalid user type");
        }

        String prefix;

        switch (userType.toLowerCase()) {
            case "admin staff":
                prefix = "#Adm";
                break;
            case "medical manager":
                prefix = "#MNG";
                break;
            case "doctor":
                prefix = "#DOC";
                break;
            case "patient":
                prefix = "#PAT";
                break;
            default:
                throw new IllegalStateException("Invalid User ID: User ID cannot be null or empty");
        }

        List<String> existingIds = allUsers.stream().map(User::getId).toList();

        return SystemConfiguration.generateId(prefix, existingIds);
    }
    
    // this havent check
    public static String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        int passwordLength = 8;
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        String pwd;
        do {
            password = new StringBuilder();
            for (int i = 0; i < passwordLength; i++) {
                password.append(characters.charAt(random.nextInt(characters.length())));
            }
            pwd = password.toString();
        } while (!(pwd.matches(SystemConfiguration.pwdPattern1) 
                && pwd.matches(SystemConfiguration.pwdPattern2) 
                && pwd.matches(SystemConfiguration.pwdPattern3) 
                && pwd.matches(SystemConfiguration.pwdPattern4)));
        return password.toString();
    }

    private boolean checkPermission(String type) {
        if (this.activeUser == null) {
            return false;
        }
        String activeType = this.activeUser.getType();

        switch (activeType) {
            case "admin staff":
            case "super admin":
                return true; // admin can manage everyone
            case "medical manager":
                return type.equals("doctor") || type.equals("patient"); // manager can manage doctors/patients only
            case "doctor":
            case "patient":
                return false; // regular users can't manage other users' accounts
            default:
                return false;
        }
    }
    
    public LinkedHashMap<String, String> assignDoctorToMedicalManager(Doctor doctor, MedicalManager medicalManager) {
        ensureLoaded();
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.doctorShiftErrorHandler(Arrays.asList(
                    "doctor",
                    "medical manager",
                    "assignment operation"
                    ),
                    doctor,
                    medicalManager,
                    activeUser
            );
            // Update doctor in allDoctors
            for (User user : allUsers) {
                if (user instanceof Doctor) {
                    Doctor storedDoctor = (Doctor) user;

                    if (storedDoctor.equals(doctor)) {
                        storedDoctor.setMedicalManager(medicalManager);
                        break;
                    }
                }
            }
            // Save updated user information
            writeUsers();
            loadUsers();

        } catch (Exception e) {
            System.out.println("Error assigning doctor to medical manager\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Doctor assignment failed\n" + e.getMessage());
            return msgMap;
        }
        msgMap.put("success", "true");
        msgMap.put("msg",String.format("Doctor '%s' has been assigned to Medical Manager '%s'!", doctor.getName(),medicalManager.getName()));
        return msgMap;
    }
    
}
