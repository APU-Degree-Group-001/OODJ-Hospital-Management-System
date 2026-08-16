/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestController;

/**
 *
 * @author paure
 */

import java.util.LinkedHashMap;
import java.util.Map;
import oodj_hospital_management_system.controllers.ReportController;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;

public class TestReportController {
    private static ReportController reportCtrl;
    private static User adminStaff;
 
    public static void main(String[] args) {
        reportCtrl = new ReportController();
        adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
        
        // Billing / Revenue Report
//        testGetBillingTotals();
//        testGetRevenueByMonth();
//        testGetBillStatusBreakdown();

        // Insurance Usage Report
//        testGetInsuranceTotals();
//        testGetInsuranceUsageByProvider();
//        testGetPayableAmountByInsurance();
 
        // Consultation Report
//        testGetConsultationTotals();
//        testGetConsultationsByStatus();
//        testGetConsultationsByDoctor();
//        testGetConsultationsByMonth();
 
        // Doctor Shift Report
//        testGetDoctorShiftTotals();
//        testGetShiftsByStatus();
//        testGetScheduledShiftHoursByDoctor();
 
        // Room / Hospital Asset Utilization Report
//        testGetRoomUtilizationTotals();
//        testGetAllocationCountByAsset();
//        testGetOccupiedMinutesByAsset();
    }
 
    // =========================================================
    // PRINT HELPERS
    // =========================================================
 
    static void printStringMap(String title, LinkedHashMap<String, String> map) {
        System.out.println("\n===== " + title + " =====");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
 
    static void printDoubleMap(String title, LinkedHashMap<String, Double> map) {
        System.out.println("\n===== " + title + " =====");
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.printf("%-20s : %.2f%n", entry.getKey(), entry.getValue());
        }
    }
 
    // =========================================================
    // BILLING / REVENUE REPORT
    // =========================================================
 
    static void testGetBillingTotals() {
        printStringMap("TEST GET BILLING TOTALS (via Controller)", reportCtrl.getBillingTotals());
    }
 
    static void testGetRevenueByMonth() {
        printDoubleMap("TEST GET REVENUE BY MONTH (via Controller)", reportCtrl.getRevenueByMonth());
    }
 
    static void testGetBillStatusBreakdown() {
        printDoubleMap("TEST GET BILL STATUS BREAKDOWN (via Controller)", reportCtrl.getBillStatusBreakdown());
    }
 
    // =========================================================
    // INSURANCE USAGE REPORT
    // =========================================================
 
    static void testGetInsuranceTotals() {
        printStringMap("TEST GET INSURANCE TOTALS (via Controller)", reportCtrl.getInsuranceTotals());
    }
 
    static void testGetInsuranceUsageByProvider() {
        printDoubleMap("TEST GET INSURANCE USAGE BY PROVIDER (via Controller)", reportCtrl.getInsuranceUsageByProvider());
    }
 
    static void testGetPayableAmountByInsurance() {
        printDoubleMap("TEST GET PAYABLE AMOUNT BY INSURANCE (via Controller)", reportCtrl.getPayableAmountByInsurance());
    }
 
    // =========================================================
    // CONSULTATION REPORT
    // =========================================================
 
    static void testGetConsultationTotals() {
        printStringMap("TEST GET CONSULTATION TOTALS (via Controller)", reportCtrl.getConsultationTotals());
    }
 
    static void testGetConsultationsByStatus() {
        printDoubleMap("TEST GET CONSULTATIONS BY STATUS (via Controller)", reportCtrl.getConsultationsByStatus());
    }
 
    static void testGetConsultationsByDoctor() {
        printDoubleMap("TEST GET CONSULTATIONS BY DOCTOR (via Controller)", reportCtrl.getConsultationsByDoctor());
    }
 
    static void testGetConsultationsByMonth() {
        printDoubleMap("TEST GET CONSULTATIONS BY MONTH (via Controller)", reportCtrl.getConsultationsByMonth());
    }
 
    // =========================================================
    // DOCTOR SHIFT REPORT
    // =========================================================
 
    static void testGetDoctorShiftTotals() {
        printStringMap("TEST GET DOCTOR SHIFT TOTALS (via Controller)", reportCtrl.getDoctorShiftTotals());
    }
 
    static void testGetShiftsByStatus() {
        printDoubleMap("TEST GET SHIFTS BY STATUS (via Controller)", reportCtrl.getShiftsByStatus());
    }
 
    static void testGetScheduledShiftHoursByDoctor() {
        printDoubleMap("TEST GET SCHEDULED SHIFT HOURS BY DOCTOR (via Controller)", reportCtrl.getScheduledShiftHoursByDoctor());
    }
 
    // =========================================================
    // ROOM / HOSPITAL ASSET UTILIZATION REPORT
    // =========================================================
 
    static void testGetRoomUtilizationTotals() {
        printStringMap("TEST GET ROOM UTILIZATION TOTALS (via Controller)", reportCtrl.getRoomUtilizationTotals());
    }
 
    static void testGetAllocationCountByAsset() {
        printDoubleMap("TEST GET ALLOCATION COUNT BY ASSET (via Controller)", reportCtrl.getAllocationCountByAsset());
    }
 
    static void testGetOccupiedMinutesByAsset() {
        printDoubleMap("TEST GET OCCUPIED MINUTES BY ASSET (via Controller)", reportCtrl.getOccupiedMinutesByAsset());
    }
    
}
