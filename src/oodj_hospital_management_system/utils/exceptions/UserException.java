/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils.exceptions;

/**
 *
 * @author sheaw
 */
public class UserException extends Exception {
    public UserException(String errorMsg) {
        super("User Handling Error: " + errorMsg);
    }
}
