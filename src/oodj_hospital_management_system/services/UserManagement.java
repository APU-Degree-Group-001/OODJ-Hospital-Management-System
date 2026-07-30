/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
        Doctor usr = new Doctor("#DOCd4e8f", "haha", "hehe123", "doctor", DateTime.formattedLocalDateTime(DateTime.currentDateTime()));
        UserManagement test = new UserManagement(usr);
        
        test.modifyUserName("waka waka", usr.getId());
        
        // forgotten how to update User object gei name, but the txt updated le
        System.out.println(usr.getName());
    }

    private static ArrayList<LinkedHashMap<String, String>> permissions = new ArrayList<>();
    private User activeUser;
    
    public UserManagement(User user) {
        this.activeUser = user;
        loadUsers();
    }
    
    public static void loadUsers() {
        allUsers.clear(); // <-- add this line
        List<LinkedHashMap<String, String>> rawData = handler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            String type = row.get("userType");
            User user;
            switch (type) {
                case "doctor":
                    user = new Doctor(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"));
                    break;
                case "patient":
                    user = new Patient(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"));
                    break;
                case "medicalManager":
                    user = new MedicalManager(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"));
                    break;
                case "adminStaff":
                    user = new AdminStaff(row.get("userId"), row.get("userName"), row.get("userPwd"), row.get("userType"), row.get("joinedDateTime"));
                    break;
                default:
                    System.out.println("Unknown user type '" + type + "' for user " + row.get("userId") + ", skipping.");
                    continue;
            }
            allUsers.add(user);
        }
    }
    
    public static ArrayList<User> getAllUsers() {
        return null;
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
        return false;
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
                User newUser = new User(newUserId, name, pwd, type, currentDateTime) {};
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
                // msgMap.put("newUserName", name);
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
        return null;
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

    public ArrayList<User> searchUsers(String keyword) {
        return null;
    }

    public ArrayList<User> searchUsers(
            String keyword,
            ArrayList<User> users
    ) {
        return null;
    }
    
    public ArrayList<User> sortUsersById() {
        return null;
    }

    public ArrayList<User> sortUsersByName() {
        return null;
    }

    public ArrayList<User> sortUsersByType() {
        return null;
    }
    
    public boolean isUserIdExists(String userId) {
        return false;
    }

    public boolean isUsernameExists(String username) {
        return false;
    }
    
    public static String generateUserId(String userType) {
        return null;
    }
    
    public User login(String userId, String password) {
        return null;
    }

    public boolean verifyPassword(User user, String password) {
        return false;
    }

    public LinkedHashMap<String, String> changePassword(
            User user,
            String oldPassword,
            String newPassword
    ) {
        return null;
    }

    private boolean checkPermission(String type) {
        if (this.activeUser == null) {
            return false;
        }
        String activeType = this.activeUser.getType();

        switch (activeType) {
            case "adminStaff":
                return true; // admin can manage everyone
            case "medicalManager":
                return type.equals("doctor") || type.equals("patient"); // manager can manage doctors/patients only
            case "doctor":
            case "patient":
                return false; // regular users can't manage other users' accounts
            default:
                return false;
        }
    }
    
}
