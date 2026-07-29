/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.ErrorHandler;
import oodj_hospital_management_system.utils.FileHandler;

/**
 *
 * @author sheaw
 */
public class UserManagement {
    private static final FileHandler handler = new FileHandler(SystemConfiguration.userFilePath);
    // private static List<User> admins = new ArrayList<>();
    private static List<User> adminStaff = new ArrayList<>();
    private static List<User> medicalManager = new ArrayList<>();
    private static List<User> doctor = new ArrayList<>();
    private static List<User> patient = new ArrayList<>();

    private static ArrayList<LinkedHashMap<String, String>> permissions = new ArrayList<>();
    private User activeUser;
    
    public UserManagement(User user) {
        this.activeUser = user;
        loadUsers();
    }
    
    public static void loadUsers() {
        //
    }
    
    public static ArrayList<User> getAllUsers() {
        return null;
    }
    
    private static boolean writeUsers() {
        return false;
    }
    
    private static boolean appendUser(User user) {
        return false;
    }
    
    // CRUD - mutator methods
    public LinkedHashMap<String, String> createUser(String name, String pwd, String type) {
//        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
//        try {
//            ErrorHandler.userErrorHandler(Arrays.asList("name", "pwd", "type"), name, pwd, type);
//        } catch (UserException e) {
//            System.out.println("Error creating user\n" + e.getMessage());
//            msgMap.put("success", null);
//            msgMap.put("msg", String.format("User '%s' is not created\n", name) + e.getMessage());
//            return msgMap;
//        }
//        if (checkPermission(type)) {
//                String newUserId = generateId(type);
//                String currentDateTime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
//                User newUser = new User(newUserId, name, pwd, type, currentDateTime);
//                allUsers.add(newUser);
//                if (handler.checkFileExistence()) {
//                    appendNewUser(newUser);
//                } else {
//                    writeUsers();
//                }
//                loadUsers();
//                msgMap.put("success", "true");
//                msgMap.put("msg", String.format("User '%s' is created and stored!", name));
//                msgMap.put("newUserId", newUserId);
//                // msgMap.put("newUserName", name);
//                return msgMap;
//        } else {
//            msgMap.put("success", null);
//            msgMap.put("msg",
//                    String.format("User '%s' is not created: you have no access to create '%s' users", name, type));
//            return msgMap;
//        }
        return null;
    }

    public LinkedHashMap<String, String> modifyUserName(String newName, String userId) {
//        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
//        try {
//            User user = getUserById(userId);
//            ErrorHandler.userErrorHandler(Arrays.asList("name", "user"), newName, user);
//        } catch (UserException e) {
//            System.out.println("Error modifying user name\n" + e.getMessage());
//            msgMap.put("success", null);
//            msgMap.put("msg", String.format("User '%s''s name is not modified\n", userId) + e.getMessage());
//            return msgMap;
//        }
//        User modifyUser = new User();
//        for (User user : allUsers) {
//            if (user.getId().equals(userId)) {
//                modifyUser = user;
//                break;
//            }
//        }
//        if (checkPermission(modifyUser.type) || this.activeUser.equals(getUserById(userId))) {
////            modifyUser.modifyName(newName);
//            for (User user : allUsers) {
//                if (user.getId().equals(modifyUser.getId())) {
//                    user.modifyName(newName);
//                    break;
//                }
//            }
//            writeUsers();
//            loadUsers();
//            msgMap.put("success", "true");
//            msgMap.put("msg", String.format("User '%s' name is modified!", userId));
//        } else {
//            msgMap.put("success", null);
//            msgMap.put("msg", String.format("User '%s' name is not modified: you have no access to modify '%s' users",
//                    userId, modifyUser.type));
//        }
//        return msgMap;
        return null;
    }

    public LinkedHashMap<String, String> modifyUserPwd(String newPwd, String userId) {
//        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
//        try {
//            User user = getUserById(userId);
//            ErrorHandler.userErrorHandler(Arrays.asList("pwd", "user"), newPwd, user);
//        } catch (UserException e) {
//            System.out.println("Error modifying user password\n" + e.getMessage());
//            msgMap.put("success", null);
//            msgMap.put("msg", String.format("User '%s''s password is not modified\n", userId) + e.getMessage());
//            return msgMap;
//        }
//        User modifyUser = new User();
//        for (User user : allUsers) {
//            if (user.getId().equals(userId)) {
//                modifyUser = user;
//                break;
//            }
//        }
//        if (checkPermission(modifyUser.type) || this.activeUser.equals(getUserById(userId))) {
////            modifyUser.modifyPwd(newPwd);
//            for (User user : allUsers) {
//                if (user.getId().equals(modifyUser.getId())) {
//                        user.modifyPwd(newPwd);
//                    break;
//                }
//            }
//            writeUsers();
//            loadUsers();
//            msgMap.put("success", "true");
//            msgMap.put("msg", String.format("User '%s' password is modified!", userId, modifyUser.type));
//        } else {
//            msgMap.put("success", null);
//            msgMap.put("msg",
//                    String.format("User '%s' password is not modified: you have no access to modify '%s' users", userId,
//                            modifyUser.type));
//        }
//        return msgMap;
          return null;
    }
    
    public LinkedHashMap<String, String> deleteUser(String userId) {
        return null;
    }
    
    public User getUserById(String userId) {
        return null;
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
    
}
