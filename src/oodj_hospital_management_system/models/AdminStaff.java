/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

/**
 *
 * @author sheaw
 */
public class AdminStaff extends User {
    public AdminStaff() {
        super();
    }
    
    public AdminStaff(
            String id, 
            String name, 
            String pwd, 
            String type, 
            String joinedDateTime
    ) {
        super(id, name, pwd, type, joinedDateTime);
    }
}
