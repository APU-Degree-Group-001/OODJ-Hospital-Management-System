package TestServices;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.LinkedHashMap;
import java.util.Map;
import oodj_hospital_management_system.services.ReportManagement;

/**
 *
 * @author sheaw
 */
public class TestReportManagement {

    public static void main(String[] args) {

        // Billing / Revenue Report
//        testBillingTotals();
//        testRevenueByMonth();
//        testBillStatusBreakdown();

        // Insurance Usage Report
//        testInsuranceTotals();
//        testInsuranceUsageByProvider();
//        testPayableAmountByInsurance();

        // Consultation Report
//        testConsultationTotals();
//        testConsultationsByStatus();
//        testConsultationsByDoctor();
//        testConsultationsByMonth();

        // Doctor Shift Report
//        testDoctorShiftTotals();
//        testShiftsByStatus();
//        testScheduledShiftHoursByDoctor();

        // Room / Hospital Asset Utilization Report
//        testRoomUtilizationTotals();
//        testAllocationCountByAsset();
//        testOccupiedMinutesByAsset();
    }

    // =========================================================
    // GENERIC PRINT HELPERS
    // These are the "how to call it and show it" part -
    // any GUI chart component would just consume the same
    // ReportManagement return values instead of printing text.
    // =========================================================

    // Prints a totals map (LinkedHashMap<String, String>) as a simple key : value list
    public static void printTotals(String title, LinkedHashMap<String, String> totals) {
        System.out.println("\n===== " + title + " =====");
        for (Map.Entry<String, String> entry : totals.entrySet()) {
            System.out.printf("%-25s : %s%n", entry.getKey(), entry.getValue());
        }
    }

    // Renders a grouped breakdown (LinkedHashMap<String, Double>) as an ASCII bar chart.
    // This is a stand-in for a real chart widget - a GUI layer would take the same
    // label -> value map and feed it straight into a bar/pie chart component instead.
    public static void printBarChart(String title, LinkedHashMap<String, Double> data, String unit) {
        System.out.println("\n===== " + title + " =====");

        if (data.isEmpty()) {
            System.out.println("(no data)");
            return;
        }

        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        if (max == 0) {
            max = 1.0; // avoid divide-by-zero if every value is 0
        }

        int maxBarLength = 40; // widest bar, in characters

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String label = entry.getKey();
            double value = entry.getValue();

            int barLength = (int) Math.round((value / max) * maxBarLength);
            String bar = "#".repeat(Math.max(barLength, value > 0 ? 1 : 0));

            System.out.printf("%-20s | %-" + maxBarLength + "s %.2f%s%n", label, bar, value, unit);
        }
    }

    // =========================================================
    // BILLING / REVENUE REPORT
    // =========================================================

    static void testBillingTotals() {
        LinkedHashMap<String, String> totals = ReportManagement.getBillingTotals();
        printTotals("BILLING TOTALS", totals);
    }

    static void testRevenueByMonth() {
        LinkedHashMap<String, Double> data = ReportManagement.getRevenueByMonth();
        printBarChart("REVENUE BY MONTH", data, "");
    }

    static void testBillStatusBreakdown() {
        LinkedHashMap<String, Double> data = ReportManagement.getBillStatusBreakdown();
        printBarChart("BILL STATUS BREAKDOWN (count)", data, " bill(s)");
    }

    // =========================================================
    // INSURANCE USAGE REPORT
    // =========================================================

    static void testInsuranceTotals() {
//        LinkedHashMap<String, String> totals = ReportManagement.getInsuranceTotals();
//        printTotals("INSURANCE TOTALS", totals);
    }

    static void testInsuranceUsageByProvider() {
//        LinkedHashMap<String, Double> data = ReportManagement.getInsuranceUsageByProvider();
//        printBarChart("BILLS BY INSURANCE PROVIDER (count)", data, " bill(s)");
    }

    static void testPayableAmountByInsurance() {
//        LinkedHashMap<String, Double> data = ReportManagement.getPayableAmountByInsurance();
//        printBarChart("PAYABLE AMOUNT BY INSURANCE PROVIDER", data, "");
    }

    // =========================================================
    // CONSULTATION REPORT
    // =========================================================
//
//    static void testConsultationTotals() {
//        LinkedHashMap<String, String> totals = ReportManagement.getConsultationTotals();
//        printTotals("CONSULTATION TOTALS", totals);
//    }
//
//    static void testConsultationsByStatus() {
//        LinkedHashMap<String, Double> data = ReportManagement.getConsultationsByStatus();
//        printBarChart("CONSULTATIONS BY STATUS (count)", data, "");
//    }
//
//    static void testConsultationsByDoctor() {
//        LinkedHashMap<String, Double> data = ReportManagement.getConsultationsByDoctor();
//        printBarChart("CONSULTATIONS BY DOCTOR (count)", data, "");
//    }
//
//    static void testConsultationsByMonth() {
//        LinkedHashMap<String, Double> data = ReportManagement.getConsultationsByMonth();
//        printBarChart("CONSULTATIONS BY MONTH (count)", data, "");
//    }

    // =========================================================
    // DOCTOR SHIFT REPORT
    // =========================================================

//    static void testDoctorShiftTotals() {
//        LinkedHashMap<String, String> totals = ReportManagement.getDoctorShiftTotals();
//        printTotals("DOCTOR SHIFT TOTALS", totals);
//    }
//
//    static void testShiftsByStatus() {
//        LinkedHashMap<String, Double> data = ReportManagement.getShiftsByStatus();
//        printBarChart("SHIFTS BY STATUS (count)", data, "");
//    }
//
//    static void testScheduledShiftHoursByDoctor() {
//        LinkedHashMap<String, Double> data = ReportManagement.getScheduledShiftHoursByDoctor();
//        printBarChart("SCHEDULED SHIFT HOURS BY DOCTOR", data, "h");
//    }

    // =========================================================
    // ROOM / HOSPITAL ASSET UTILIZATION REPORT
    // =========================================================

    static void testRoomUtilizationTotals() {
        LinkedHashMap<String, String> totals = ReportManagement.getRoomUtilizationTotals();
        printTotals("ROOM UTILIZATION TOTALS", totals);
    }

    static void testAllocationCountByAsset() {
        LinkedHashMap<String, Double> data = ReportManagement.getAllocationCountByAsset();
        printBarChart("ALLOCATION COUNT BY ASSET", data, "");
    }

    static void testOccupiedMinutesByAsset() {
        LinkedHashMap<String, Double> data = ReportManagement.getOccupiedMinutesByAsset();
        printBarChart("OCCUPIED MINUTES BY ASSET", data, "min");
    }
}