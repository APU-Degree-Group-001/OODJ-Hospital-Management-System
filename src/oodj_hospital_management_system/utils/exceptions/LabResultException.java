/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.utils.exceptions;

/**
 *
 * @author sheaw
 */
public class LabResultException extends Exception {
    public LabResultException(String errorMsg) {
        super("Lab Result Handling Error: " + errorMsg);
    }
}
