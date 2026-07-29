/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils;

import java.util.List;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.exceptions.UserException;

/**
 *
 * @author sheaw
 */
public class ErrorHandler {
    public static void userErrorHandler(List<String> details, Object... var) throws UserException {
//        List<User> allUsers = UserManagement.getAllUsers();
//        List<User> allStaff = UserManagement.getStaffs();
//        List<User> allManagers = UserManagement.getManagers();
//        try {
//            if (details.size() != var.length) {
//                throw new Exception(
//                        "Invalid arguments passed: number of details set to check and number of data passed is conflicted");
//            }
//        } catch (Exception e) {
//            System.out.println("Error Handling error: " + e.getMessage());
//            Logger.errorLog("Error Handling error (System Error) -> " + e.getMessage());
//        }
//        int i = 0;
//        for (Object object : var) {
//            if (object instanceof String) {
//                String data = (String) object;
//                if (details.get(i).equalsIgnoreCase("userid")) {
//                    if (data == null || data.equals("")) {
//                        throw new UserException("User ID cannot be null or empty");
//                    }
//                } else if (details.get(i).equalsIgnoreCase("name")) {
//                    if (data == null || data.equals("")) {
//                        throw new UserException("name cannot be null or empty");
//                    }
//                } else if (details.get(i).equalsIgnoreCase("pwd")) {
//                    // a mix of uppercase and lowercase letters, numbers, and symbols.
//                    if (data == null || data.equals("")) {
//                        throw new UserException("password cannot be null or empty");
//                    } else if (data.length() < 8) {
//                        throw new UserException("Password must be at least 8 characters long");
//                    } else if (!(data.matches(SystemConfig.pwdPattern1) && data.matches(SystemConfig.pwdPattern2)
//                            && data.matches(SystemConfig.pwdPattern3) && data.matches(SystemConfig.pwdPattern4))) {
//                        throw new UserException(
//                                "Password must be a mix of uppercase and lowercase letters, digits, and special characters");
//                    }
//                } else if (details.get(i).equalsIgnoreCase("type")) {
//                    if (!Arrays.asList(SystemConfig.userTypes).contains(data)) {
//                        throw new UserException("Invalid user type");
//                    }
//                }
//            } else if (object instanceof User) {
//                User data = (User) object;
//                if (details.get(i).equalsIgnoreCase("staff")) {
//                    if (!allStaff.stream().anyMatch(user -> user.equals(data))) {
//                        throw new UserException(String.format("Staff '%s' not found", data.getName()));
//                    }
//                } else if (details.get(i).equalsIgnoreCase("manager")) {
//                    if (!allManagers.stream().anyMatch(user -> user.equals(data))) {
//                        throw new UserException(String.format("Manager '%s' not found", data.getName()));
//                    }
//                } else if (details.get(i).equalsIgnoreCase("user")) {
//                    if (!allUsers.stream().anyMatch(user -> user.equals(data))) {
//                        throw new UserException(String.format("User '%s' not found", data.getName()));
//                    }
//                }
//            }
//            i++;
//        }
    }
}
