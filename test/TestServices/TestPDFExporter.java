package TestServices;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.ReportManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.PDFExporter;

/**
 *
 * @author paure
 */
public class TestPDFExporter {
    private static final String OUTPUT_FOLDER = "C:\\Users\\paure\\Downloads\\OODJ_PDF";
 
    private static User adminStaff;
    private static String generatedBy;
    private static String datetime;
 
    public static void main(String[] args) {
 
        // Get the active user once - assuming admin staff generates reports
        adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        if (adminStaff == null) {
            System.out.println(
                    "Test failed: Admin Staff '#ADMd4e8f' was not found."
            );
            return;
        }
 
        generatedBy = adminStaff.getName();
        datetime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
 
        // Test billing / revenue report
        testExportBillingRevenueReport();
 
        // Test insurance usage report
        testExportInsuranceUsageReport();
 
        // Test consultation report
//        testExportConsultationReport();
 
        // Test doctor shift report
//        testExportDoctorShiftReport();
 
        // Test room / hospital asset utilization report
        testExportRoomUtilizationReport();
    }
 
    // =========================================================
    // BILLING / REVENUE REPORT TEST
    // =========================================================
 
    static void testExportBillingRevenueReport() {
 
        System.out.println("\n===== TEST EXPORT BILLING / REVENUE REPORT =====");
 
        LinkedHashMap<String, String> billingTotals = ReportManagement.getBillingTotals();
        LinkedHashMap<String, Double> revenueByMonth = ReportManagement.getRevenueByMonth();
        LinkedHashMap<String, Double> billStatusBreakdown = ReportManagement.getBillStatusBreakdown();
 
        String filePath = PDFExporter.filePath("BillingRevenueReport", OUTPUT_FOLDER);
 
        try {
            boolean success = PDFExporter.exportBillingRevenueReport(
                    filePath, generatedBy, datetime, billingTotals, revenueByMonth, billStatusBreakdown
            );
 
            System.out.println("Success  : " + success);
            System.out.println("File Path: " + filePath);
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    }
 
    // =========================================================
    // INSURANCE USAGE REPORT TEST
    // =========================================================
 
    static void testExportInsuranceUsageReport() {
 
        System.out.println("\n===== TEST EXPORT INSURANCE USAGE REPORT =====");
 
//        LinkedHashMap<String, String> insuranceTotals = ReportManagement.getInsuranceTotals();
//        LinkedHashMap<String, Double> usageByProvider = ReportManagement.getInsuranceUsageByProvider();
//        LinkedHashMap<String, Double> payableAmountByInsurance = ReportManagement.getPayableAmountByInsurance();
 
        String filePath = PDFExporter.filePath("InsuranceUsageReport", OUTPUT_FOLDER);
 
//        try {
//            boolean success = PDFExporter.exportInsuranceUsageReport(
//                    filePath, generatedBy, datetime, insuranceTotals, usageByProvider, payableAmountByInsurance
//            );
// 
//            System.out.println("Success  : " + success);
//            System.out.println("File Path: " + filePath);
//        } catch (Exception e) {
//            System.out.println("Test failed: " + e.getMessage());
//        }
    }
 
    // =========================================================
    // CONSULTATION REPORT TEST
    // =========================================================
 
//    static void testExportConsultationReport() {
// 
//        System.out.println("\n===== TEST EXPORT CONSULTATION REPORT =====");
// 
//        LinkedHashMap<String, String> consultationTotals = ReportManagement.getConsultationTotals();
//        LinkedHashMap<String, Double> consultationsByStatus = ReportManagement.getConsultationsByStatus();
//        LinkedHashMap<String, Double> consultationsByDoctor = ReportManagement.getConsultationsByDoctor();
//        LinkedHashMap<String, Double> consultationsByMonth = ReportManagement.getConsultationsByMonth();
// 
//        String filePath = PDFExporter.filePath("ConsultationReport", OUTPUT_FOLDER);
// 
//        try {
//            boolean success = PDFExporter.exportConsultationReport(
//                    filePath, generatedBy, datetime, consultationTotals, consultationsByStatus,
//                    consultationsByDoctor, consultationsByMonth
//            );
// 
//            System.out.println("Success  : " + success);
//            System.out.println("File Path: " + filePath);
//        } catch (Exception e) {
//            System.out.println("Test failed: " + e.getMessage());
//        }
//    }
 
    // =========================================================
    // DOCTOR SHIFT REPORT TEST
    // =========================================================
// 
//    static void testExportDoctorShiftReport() {
// 
//        System.out.println("\n===== TEST EXPORT DOCTOR SHIFT REPORT =====");
// 
//        LinkedHashMap<String, String> doctorShiftTotals = ReportManagement.getDoctorShiftTotals();
//        LinkedHashMap<String, Double> shiftsByStatus = ReportManagement.getShiftsByStatus();
//        LinkedHashMap<String, Double> scheduledShiftHoursByDoctor = ReportManagement.getScheduledShiftHoursByDoctor();
// 
//        String filePath = PDFExporter.filePath("DoctorShiftReport", OUTPUT_FOLDER);
// 
//        try {
//            boolean success = PDFExporter.exportDoctorShiftReport(
//                    filePath, generatedBy, datetime, doctorShiftTotals, shiftsByStatus, scheduledShiftHoursByDoctor
//            );
// 
//            System.out.println("Success  : " + success);
//            System.out.println("File Path: " + filePath);
//        } catch (Exception e) {
//            System.out.println("Test failed: " + e.getMessage());
//        }
//    }
 
    // =========================================================
    // ROOM / HOSPITAL ASSET UTILIZATION REPORT TEST
    // =========================================================
 
    static void testExportRoomUtilizationReport() {
 
        System.out.println("\n===== TEST EXPORT ROOM / HOSPITAL ASSET UTILIZATION REPORT =====");
 
        LinkedHashMap<String, String> roomUtilizationTotals = ReportManagement.getRoomUtilizationTotals();
        LinkedHashMap<String, Double> allocationCountByAsset = ReportManagement.getAllocationCountByAsset();
        LinkedHashMap<String, Double> occupiedMinutesByAsset = ReportManagement.getOccupiedMinutesByAsset();
 
        String filePath = PDFExporter.filePath("RoomUtilizationReport", OUTPUT_FOLDER);
 
        try {
            boolean success = PDFExporter.exportRoomUtilizationReport(
                    filePath, generatedBy, datetime, roomUtilizationTotals, allocationCountByAsset, occupiedMinutesByAsset
            );
 
            System.out.println("Success  : " + success);
            System.out.println("File Path: " + filePath);
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    }
}
