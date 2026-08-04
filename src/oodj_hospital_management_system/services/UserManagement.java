/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

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
    private static final FileHandler handler = new FileHandler(SystemConfiguration.userFilePath);
    // private static List<User> admins = new ArrayList<>();
    private static List<User> allUsers = new ArrayList<>();
    private static List<User> adminStaff = new ArrayList<>();
    private static List<User> medicalManager = new ArrayList<>();
    private static List<User> doctor = new ArrayList<>();
    private static List<User> patient = new ArrayList<>();
    
    public static void main(String[] args) {
        // Test Update Name
//        Doctor usr = new Doctor("#DOCd4e8f", "waka waka", "hehe123", "doctor", "2026-07-30 14:22:07");
//        UserManagement test = new UserManagement(usr);
//        test.modifyUserName("Shakira Dai Dai", usr.getId());
//        
//        User updatedUser = test.getUserById("#DOCd4e8f");
//        test = new UserManagement(updatedUser);
//   
//        System.out.println(updatedUser.getName());

        // Test Append New User
        AdminStaff admin = new AdminStaff("#ADMd4e8f", "uehehe", "hehe456", "admin staff", "2026-07-30 14:22:07");
        UserManagement usrMng = new UserManagement(admin);
//        usrMng.createUser("Paureen", "pau123", "patient");
//        UserManagement.testPrintAllUsers(usrMng.getAllUsers());
        UserManagement.testPrintAllUsers(usrMng.getAllPatients());
    }

    private static ArrayList<LinkedHashMap<String, String>> permissions = new ArrayList<>();
    private User activeUser;
    
    public UserManagement(User user) {
        this.activeUser = user;
        loadUsers();
    }
    
    public static void testPrintAllUsers(List<User> userList) {
        System.out.printf("%-19s %-19s %-19s %-19s%n", "User ID", "Name", "Password","Role");
         for (User usr : userList) {
             System.out.printf("%-19s %-19s %-19s %-19s%n", usr.getId(), usr.getName(), usr.getPwd(), usr.getType());
         }
    }
    
    public User getActiveUser() {
        return this.activeUser;
    }
    
    public static void loadUsers() {
        allUsers.clear();
        adminStaff.clear();
        medicalManager.clear();
        doctor.clear();
        patient.clear();
        List<LinkedHashMap<String, String>> rawData = handler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            String type = row.get("userType");
            User user;
            switch (type) {
                case "admin staff":
                    user = new AdminStaff(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"));
                    adminStaff.add(user);
                    break;
                case "medical manager":
                    user = new MedicalManager(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"));
                    medicalManager.add(user);
                    break;
                case "doctor":
                    user = new Doctor(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"));
                    doctor.add(user);
                    break;
                case "patient":
                    user = new Patient(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"));
                    patient.add(user);
                    break;
                default:
                    System.out.println("Unknown user type '" + type + "' for user " + row.get("userId") + ", skipping.");
                    continue;
            }
            allUsers.add(user);
        }
    }
    
    // Accessor Method for Users
    public static List<User> getAllUsers() {
        loadUsers();
        return allUsers;
    }
    
    public static List<User> getAllAdminStaffs() {
        loadUsers();
        return adminStaff;
    }
    
    public static List<User> getAllMedicalManagers() {
        loadUsers();
        return medicalManager;
    }
    
    public static List<User> getAllDoctors() {
        loadUsers();
        return doctor;
    }
    
    public static List<User> getAllPatients() {
        loadUsers();
        return patient;
    }
    
    // File Handler (Write and Append)
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
    public LinkedHashMap<String, String> createUser(String name, String pwd, String type) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.userErrorHandler(Arrays.asList("name", "pwd", "type"), name, pwd, type);
        } catch (UserException e) {
            System.out.println("Error creating user\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' is not created\n", name) + e.getMessage());
            return msgMap;
        }
        if (checkPermission(type)) {
            String newUserId = generateUserId(type);
            String currentDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
            User newUser;
            switch (type) {
                case "doctor":
                    newUser = new Doctor(newUserId, name, pwd, type, currentDateTime);
                    break;
                case "patient":
                    newUser = new Patient(newUserId, name, pwd, type, currentDateTime);
                    break;
                case "medicalManager":
                    newUser = new MedicalManager(newUserId, name, pwd, type, currentDateTime);
                    break;
                case "adminStaff":
                    newUser = new AdminStaff(newUserId, name, pwd, type, currentDateTime);
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
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            User user = getUserById(userId);
            ErrorHandler.userErrorHandler(Arrays.asList("name", "user"), newName, user);
        } catch (UserException e) {
            System.out.println("Error modifying user name\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s name is not modified\n", userId) + e.getMessage());
            return msgMap;
        }
        
        User modifyUser = null;
        for (User user : allUsers) {
            if (user.getId().equals(userId)) {
                modifyUser = user;
                break;
            }
        }
        
        if (modifyUser == null) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s' not found", userId));
            return msgMap;
        }
        
        if (checkPermission(modifyUser.getType()) || this.activeUser.equals(getUserById(userId))) {
//            modifyUser.modifyName(newName);
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

    public LinkedHashMap<String, String> modifyUserPwd(String newPwd, String userId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            User user = getUserById(userId);
            ErrorHandler.userErrorHandler(Arrays.asList("pwd", "user"), newPwd, user);
        } catch (UserException e) {
            System.out.println("Error modifying user password\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", String.format("User '%s''s password is not modified\n", userId) + e.getMessage());
            return msgMap;
        }
        User modifyUser = null;
        for (User user : allUsers) {
            if (user.getId().equals(userId)) {
                modifyUser = user;
                break;
            }
        }
        if (checkPermission(modifyUser.getType()) || this.activeUser.equals(getUserById(userId))) {
//            modifyUser.modifyPwd(newPwd);
            for (User user : allUsers) {
                if (user.getId().equals(modifyUser.getId())) {
                        user.setPwd(newPwd);
                    break;
                }
            }
            writeUsers();
            loadUsers();            
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("User '%s' password is modified!", userId, modifyUser.getType()));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg",
                    String.format("User '%s' password is not modified: you have no access to modify '%s' users", userId,
                            modifyUser.getType()));
        }
        return msgMap;
    }
    
    public LinkedHashMap<String, String> deleteUser(String userId) {
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
    
    public User getUserById(String userId) {
        loadUsers();
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

    // Searching and Sorting
    public List<User> searchUsers(String keywords) {
        List<User> matchedUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || user.getName().toLowerCase().contains(keywords.toLowerCase().trim()) || user.getType().contains(keywords.toLowerCase().trim())) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }

    public List<User> searchUsers(String keywords, ArrayList<User> userList) {
        List<User> matchedUsers = new ArrayList<>();
        for (User user : userList) {
            if (user.getId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || user.getName().toLowerCase().contains(keywords.toLowerCase().trim()) || user.getType().contains(keywords.toLowerCase().trim())) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }
    
    public List<User> sortUsersById() {
        List<User> sortedUsers = allUsers;
        sortedUsers.sort(Comparator.comparing(User::getId));
        return sortedUsers;
    }
    
    public List<User> sortUsersList(List<User> list) {
        List<User> sortedUsers = list;
        sortedUsers.sort(Comparator.comparing(User::getId));
        return sortedUsers;
    }

    public List<User> sortUsersByName() {
        List<User> sortedUsers = allUsers;
        sortedUsers.sort(Comparator.comparing(User::getName));
        return sortedUsers;
    }
    
    // Generate ID annd Temp Password
    public static String generateUserId(String userType) {
        if (!Arrays.asList(SystemConfiguration.userTypes).contains(userType)) {
            throw new IllegalArgumentException("Invalid user type");
        }
        String newUserId = "";
        boolean loop;
        do {
            switch (userType.toLowerCase()) {
                case "admin staff":
                    newUserId = "#Adm" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
                    break;
                case "medical manager":
                    newUserId = "#MNG" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
                    break;
                case "doctor":
                    newUserId = "#DOC" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
                    break;
                case "patient":
                    newUserId = "#PAT" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
                    break;
                default:
                    throw new IllegalStateException("Invalid User ID: User ID cannot be null or empty");
            }

            String userId = newUserId;
            loop = newUserId != null && allUsers.stream().anyMatch(user -> Objects.equals(user.getId(), userId));

        } while (loop);
        return newUserId;
    }
    
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
        } while (!(pwd.matches(SystemConfiguration.pwdPattern1) && pwd.matches(SystemConfig.pwdPattern2) && pwd.matches(SystemConfig.pwdPattern3) && pwd.matches(SystemConfig.pwdPattern4)));
        return password.toString();
    }
    
    public User login(String userId, String password) {
        return null;
    }

    private boolean checkPermission(String type) {
        if (this.activeUser == null) {
            return false;
        }
        String activeType = this.activeUser.getType();

        switch (activeType) {
            case "admin staff":
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
    
}
