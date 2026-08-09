/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;
import java.util.Objects;

// import own package :3
import oodj_hospital_management_system.config.SystemConfiguration;

/**
 *
 * @author sheaw
 * Description : User Object
 */
public abstract class User {
    private String id;
    private String name;
    private String pwd;
    private String type;
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
    }

    // public void deleteUser(User user) {
    //     user = null;
    // }

//    public void invalidateUser() {
//        this.id = null;
//        this.name = null;
//        this.pwd = null;
//        this.type = null;
//        if (userDetails != null) {
//            userDetails.clear();
//        }
//    }

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
        return Objects.equals(this.id, userObj.id);
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

    // accessor method for user type
    public String getType() {
        return this.type;
    }

    // accessor method for user joined date time
    public String getJoinedDateTime() {
        return this.joinedDateTime;
    }

    public LinkedHashMap<String, String> getUserMap() {
        LinkedHashMap<String, String> userDetails = new LinkedHashMap<>();
        userDetails.put("userId", this.id);
        userDetails.put("userName", this.name);
        userDetails.put("userPwd", this.pwd);
        userDetails.put("userType", this.type);
        userDetails.put("joinedDateTime", this.joinedDateTime);
        userDetails.put("departmentId", "");
        userDetails.put("medicalManagerId", "");
        
        return userDetails;
    }

    // mutator method for user name
    public void setName(String newName) {
        this.name = newName;
    }

    // mutator method for user password
    public void setPwd(String newPwd) {
        this.pwd = newPwd;
    }

    // mutator method for user type
    public void setType(String newType) {
        this.type = newType;
    }
}