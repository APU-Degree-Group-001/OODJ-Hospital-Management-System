/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

/**
 *
 * @author sheaw
 */
public class Patient extends User {
    public Patient() {
        super();
    }
    
    public Patient(
            String id, 
            String name, 
            String pwd, 
            String type, 
            String joinedDateTime,
            String email
    ) {
        super(id, name, pwd, type, joinedDateTime, email);
    }
}
