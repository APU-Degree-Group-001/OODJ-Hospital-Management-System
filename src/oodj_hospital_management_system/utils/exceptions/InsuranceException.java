/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils.exceptions;

/**
 *
 * @author sheaw
 */
public class InsuranceException extends Exception {
    public InsuranceException(String errorMsg) {
        super("Insurance Handling Error: " + errorMsg);
    }
}
