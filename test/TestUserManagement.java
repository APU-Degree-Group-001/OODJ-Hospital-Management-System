
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.AdminStaff;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */
public class TestUserManagement {
    private static User admin = UserManagement.getUserById("#ADMd4e8f");
    private static UserManagement usrMng = new UserManagement(admin);
    
    public static void main(String[] args) {
//        testUpdateUser();
//        testAppendUser();
//        testDeleteUser();
//        testSearchUser();
//        testSearchUserFromGivenList();
//        testSortUserById();
        testSortUserByName();
    }
    
    public static void testPrintAllUsers(List<User> userList) {
        System.out.printf("%-19s %-19s %-19s %-19s%n", "User ID", "Name", "Password","Role");
         for (User usr : userList) {
             System.out.printf("%-19s %-19s %-19s %-19s%n", usr.getId(), usr.getName(), usr.getPwd(), usr.getType());
         }
    }
    
    static void testUpdateUser() {
        // Test Update Name
        User usr = UserManagement.getUserById("#DOCd4e8f");
        UserManagement test = new UserManagement(usr);
        test.modifyUserName("Dai Dai", usr.getId());
        test.modifyUserPwd("daidai123", usr.getId());
        
        User updatedUser = test.getUserById("#DOCd4e8f");
        test = new UserManagement(updatedUser);
   
        System.out.println(updatedUser.getName());
    }
    
    static void testAppendUser() {
        // Test Append New User
        usrMng.createUser("Reen", "pau123", "medical manager");
//        UserManagement.testPrintAllUsers(usrMng.getAllUsers());
        testPrintAllUsers(usrMng.getAllMedicalManagers());
    }
    
    static void testDeleteUser() {
        // Test Delete User
        usrMng.deleteUser("#PATd0bef");
    }
    
    static void testSearchUser() {
        testPrintAllUsers(usrMng.searchUsers("pau"));
    }
    
    static void testSearchUserFromGivenList() {
        testPrintAllUsers(usrMng.searchUsers("re", usrMng.getAllMedicalManagers()));
    }
    
    static void testSortUserById() {
        testPrintAllUsers(usrMng.sortUsersById(UserManagement.getAllUsers()));
    }
    
    static void testSortUserByName() {
        testPrintAllUsers(usrMng.sortUsersByName(UserManagement.getAllUsers()));
    }
    
    static void testGenerateTemporaryPassword() {
        //not tested yet~ dunno what to test, maybe ltr i will know :D
    }
}
