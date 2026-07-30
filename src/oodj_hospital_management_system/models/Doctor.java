/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

/**
 *
 * @author sheaw
 */
public class Doctor extends User {
    private int assignedShiftHourByMonth;
    
    public Doctor() {
        super();
    }
    
    public Doctor(String id, String name, String pwd, String type, String joinedDateTime) {
        this(id, name, pwd, type, joinedDateTime, 0); // calls the constructor above
    }
    
    public Doctor(
            String id, 
            String name, 
            String pwd, 
            String type, 
            String joinedDateTime,
            int assignedShiftHourByMonth
    ) {
        super(id, name, pwd, type, joinedDateTime);
        this.assignedShiftHourByMonth = assignedShiftHourByMonth;
    }
    
    public int getAssignedShiftHourByMonth() {
        return this.assignedShiftHourByMonth;
    }
    
    public void setAssignedShiftHourByMonth(int assignedShiftHourByMonth) {
        this.assignedShiftHourByMonth = assignedShiftHourByMonth;
    }
}
