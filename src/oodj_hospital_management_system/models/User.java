/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *
 * @author sheaw
 * Description : User Object
 */
public class User {
    private LinkedHashMap<String, String> userDetails = new LinkedHashMap<>();
    private String id;
    private String name;
    private String pwd;
    public String type;
    private String joinedDateTime;

    public User() {
        this.id = "";
        this.name = "";
        this.pwd = ""; // later learn how to hash pwd, use java API (avoid external API larr)
        this.type = "";
        this.joinedDateTime = "";
    }

    public User(String id, String name, String pwd, String type, String joinedDateTime) {
        this.id = id;
        this.name = name;
        this.pwd = pwd; // haven't add hashing yet
        this.type = type;
        this.joinedDateTime = joinedDateTime;

        if (this.type.equalsIgnoreCase("super admin")) {
            this.userDetails.put("userId", this.id);
            this.userDetails.put("userName", this.name);
            this.userDetails.put("userPwd", this.pwd);
            this.userDetails.put("userType", this.type);
        } else {
            this.userDetails.put("userId", this.id);
            this.userDetails.put("userName", this.name);
            this.userDetails.put("userPwd", this.pwd);
            this.userDetails.put("userType", this.type);
            this.userDetails.put("joinedDateTime", this.joinedDateTime);
        }
    }

    // public void deleteUser(User user) {
    //     user = null;
    // }

    public void invalidateUser() {
        this.id = null;
        this.name = null;
        this.pwd = null;
        this.type = null;
        if (userDetails != null) {
            userDetails.clear();
        }
    }

    @Override
    public String toString() {
        // return String.format("User ID: %s%nUser Name: %s%nPassword: %s%nUser Type: %s%nJoined Date & Time: %s", this.id, this.name, this.pwd, this.type, this.joinedDateTime);
        return String.format("User {%s - %s - %s}", this.type, this.id, this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        User userObj = (User) obj;
        return (Objects.equals(this.id, userObj.id) && Objects.equals(this.name, userObj.name) &&
        Objects.equals(this.pwd, userObj.pwd) && Objects.equals(this.type, userObj.type));
    }

    // accessor method for user id
    public String getId() {
        return this.id;
    }

    // accessor method for user name
    public String getName() {
        return this.name;
    }

    // accessor method for user password
    public String getPwd() {
        return this.pwd;
    }

    // accessor method for user joined date time
    public String  getJoinedDateTime() {
        return this.joinedDateTime;
    }

    public LinkedHashMap<String, String> getUserMap() {
        return this.userDetails;
    }

    // mutator method for user name
    public void modifyName(String newName) {
        this.name = newName;
        userDetails.put("userName", this.name);
    }

    // mutator method for user password
    public void modifyPwd(String newPwd) {
        this.pwd = newPwd;
        userDetails.put("userPwd", this.pwd);
    }

    // mutator method for user type
    public void modifyType(String newType) {
        this.type = newType;
        userDetails.put("userType", this.type);
    }
}
