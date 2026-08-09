import java.util.List;
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

    public static void main(String[] args) {

        // Test Update User
//        testUpdateUser();
//         testAppendUser();
//         testDeleteUser();
//         testSearchUser();
//         testSearchUserFromGivenList();
//         testSortUserById();
         testSortUserByName();
//         testGenerateTemporaryPassword();
    }

    public static void testPrintAllUsers(List<User> userList) {
        System.out.printf(
                "%-19s %-19s %-19s %-19s%n",
                "User ID",
                "Name",
                "Password",
                "Role"
        );

        for (User usr : userList) {
            System.out.printf(
                    "%-19s %-19s %-19s %-19s%n",
                    usr.getId(),
                    usr.getName(),
                    usr.getPwd(),
                    usr.getType()
            );
        }
    }

    static void testUpdateUser() {

        // Get the doctor that will be updated
        User usr = UserManagement.getUserById("#DOCd4e8f");

        if (usr == null) {
            System.out.println("Test failed: User '#DOCd4e8f' was not found.");
            return;
        }

        // Create UserManagement using the existing user
        UserManagement test = new UserManagement(usr);

        // Update name
        test.modifyUserName("bug1", usr.getId());

        // Update password
        test.modifyUserPwd("Daidai12!", usr.getId());

        // Reload the updated user
        User updatedUser = UserManagement.getUserById("#DOCd4e8f");

        if (updatedUser == null) {
            System.out.println("Test failed: Updated user '#DOCd4e8f' could not be found.");
            return;
        }

        System.out.println("\nUpdated User:");
        System.out.println("ID       : " + updatedUser.getId());
        System.out.println("Name     : " + updatedUser.getName());
        System.out.println("Password : " + updatedUser.getPwd());
        System.out.println("Role     : " + updatedUser.getType());
    }

    static void testAppendUser() {

        // Get an admin user to perform the operation
        User admin = UserManagement.getUserById("#ADMd4e8f");

        if (admin == null) {
            System.out.println("Test failed: Admin '#ADMd4e8f' was not found.");
            return;
        }

        UserManagement usrMng = new UserManagement(admin);

        usrMng.createUser(
                "Reen",
                "paU12345!",
                "medical manager"
        );

        System.out.println("\nMedical Managers:");
        testPrintAllUsers(usrMng.getAllMedicalManagers());
    }

    static void testDeleteUser() {

        User admin = UserManagement.getUserById("#ADMd4e8f");

        if (admin == null) {
            System.out.println("Test failed: Admin '#ADMd4e8f' was not found.");
            return;
        }

        UserManagement usrMng = new UserManagement(admin);

        usrMng.deleteUser("#PATd4e8f");
    }

    static void testSearchUser() {

        User admin = UserManagement.getUserById("#ADMd4e8f");

        if (admin == null) {
            System.out.println("Test failed: Admin '#ADMd4e8f' was not found.");
            return;
        }

        UserManagement usrMng = new UserManagement(admin);

        System.out.println("\nSearch Result:");
        testPrintAllUsers(
                usrMng.searchUsers("pau")
        );
    }

    static void testSearchUserFromGivenList() {

        User admin = UserManagement.getUserById("#ADMd4e8f");

        if (admin == null) {
            System.out.println("Test failed: Admin '#ADMd4e8f' was not found.");
            return;
        }

        UserManagement usrMng = new UserManagement(admin);

        System.out.println("\nSearch Medical Managers:");
        testPrintAllUsers(
                usrMng.searchUsers(
                        "re",
                        usrMng.getAllMedicalManagers()
                )
        );
    }

    static void testSortUserById() {

        System.out.println("\nUsers Sorted By ID:");

        testPrintAllUsers(
                UserManagement.sortUsersById(
                        UserManagement.getAllUsers()
                )
        );
    }

    static void testSortUserByName() {

        System.out.println("\nUsers Sorted By Name:");

        testPrintAllUsers(
                UserManagement.sortUsersByName(
                        UserManagement.getAllUsers()
                )
        );
    }

    static void testGenerateTemporaryPassword() {

        String password = UserManagement.generateTemporaryPassword();

        System.out.println("\nGenerated Temporary Password:");
        System.out.println(password);
    }
}
