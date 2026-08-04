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
    public enum ShiftStatus {
      ON_SHIFT,
      OFF_SHIFT
    }
    private ShiftStatus shiftStatus;
    private int assignedShiftHourByMonth;
    
    public Doctor() {
        super();
    }
    
    public Doctor(String id, String name, String pwd, String type, String joinedDateTime) {
        this(id, name, pwd, type, joinedDateTime, 0, ShiftStatus.OFF_SHIFT); // calls the constructor above
    }
    
    public Doctor(
            String id, 
            String name, 
            String pwd, 
            String type, 
            String joinedDateTime,
            int assignedShiftHourByMonth,
            ShiftStatus shiftStatus
    ) {
        super(id, name, pwd, type, joinedDateTime);
        this.assignedShiftHourByMonth = assignedShiftHourByMonth;
        this.shiftStatus = shiftStatus;
    }
    
    public int getAssignedShiftHourByMonth() {
        return this.assignedShiftHourByMonth;
    }
    
    public void setAssignedShiftHourByMonth(int assignedShiftHourByMonth) {
        this.assignedShiftHourByMonth = assignedShiftHourByMonth;
    }

    public void startShift() {
        if (shiftStatus != ShiftStatus.OFF_SHIFT) {
            throw new IllegalStateException(
                "Started shift unable to be started"
            );
        }
        shiftStatus = ShiftStatus.ON_SHIFT;
    }

    public void endShift() {
        if (shiftStatus != ShiftStatus.ON_SHIFT) {
            throw new IllegalStateException(
                "Off shift unable to be ended"
            );
        }
        shiftStatus = ShiftStatus.OFF_SHIFT;
    }
}
