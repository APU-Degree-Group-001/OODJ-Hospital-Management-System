package TestController;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author paure
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.controllers.BillingController;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.InsuranceManagement;
import oodj_hospital_management_system.services.UserManagement;

public class TestBillingController {
    private static BillingController billCtrl;
 
    private static final String SAMPLE_CONSULTATION_ID = "#CON003"; // pick one not already billed
 
    public static void main(String[] args) {
 
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");
 
        if (adminStaff == null) {
            System.out.println(
                    "Test failed: Admin Staff '#ADMd4e8f' was not found."
            );
            return;
        }
 
        billCtrl = new BillingController(adminStaff);
 
        // Test loading
//        testGetAllBills();
 
        // Test lookup
//        testGetBillByConsultationId();
 
        // Test search
//        testSearchBills();
 
        // Test sort
//        testSortBillsById();
//        testSortBillsByTotalAmount();
 
        // Test creation
        // Uncomment when needed because these modify bills.txt AND write to the log file
//        testCreateBill();
 
        // Test modification
        // Uncomment when needed because these modify bills.txt AND write to the log file
//        testUpdateBill();
//        testMarkBillAsPaid();
//        testCancelBill();
    }
 
    // =========================================================
    // PRINT METHOD
    // =========================================================
 
    public static void testPrintBills(List<Bill> billList) {
 
        System.out.printf(
                "%-10s %-12s %-10s %-10s %-14s %-14s %-14s %-10s%n",
                "Bill ID",
                "Consult. ID",
                "Patient",
                "Insurance",
                "Consult. Fee",
                "Total",
                "Payable",
                "Status"
        );
 
        System.out.println(
                "-----------------------------------------------------------------------------------------------"
        );
 
        for (Bill bill : billList) {
 
            System.out.printf(
                    "%-10s %-12s %-10s %-10s %-14.2f %-14.2f %-14.2f %-10s%n",
                    bill.getBillId(),
                    bill.getConsultationId(),
                    bill.getPatient() != null ? bill.getPatient().getId() : "-",
                    bill.getInsurance() != null ? bill.getInsurance().getInsuranceId() : "-",
                    bill.getConsultationFee(),
                    bill.getTotalAmount(),
                    bill.getPayableAmount(),
                    bill.getStatus()
            );
        }
    }
 
    // =========================================================
    // LOAD TEST
    // =========================================================
 
    static void testGetAllBills() {
 
        System.out.println("\n===== TEST GET ALL BILLS (via Controller) =====");
 
        List<Bill> bills = billCtrl.getAllBills();
 
        System.out.println("Total Bills: " + bills.size());
 
        testPrintBills(bills);
    }
 
    // =========================================================
    // LOOKUP TEST
    // =========================================================
 
    static void testGetBillByConsultationId() {
 
        System.out.println("\n===== TEST GET BILL BY CONSULTATION ID (via Controller) =====");
 
        Bill bill = billCtrl.getBillByConsultationId(SAMPLE_CONSULTATION_ID);
 
        if (bill != null) {
            System.out.println("Bill found:");
            System.out.println("Bill ID  : " + bill.getBillId());
            System.out.println("Total    : " + bill.getTotalAmount());
            System.out.println("Payable  : " + bill.getPayableAmount());
            System.out.println("Status   : " + bill.getStatus());
        } else {
            System.out.println("No bill found for consultation " + SAMPLE_CONSULTATION_ID);
        }
    }
 
    // =========================================================
    // SEARCH TEST
    // =========================================================
 
    static void testSearchBills() {
 
        System.out.println("\n===== TEST SEARCH BILLS (via Controller) =====");
 
        String keyword = "#CON001";
 
        List<Bill> result = billCtrl.searchBills(keyword);
 
        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched bills: " + result.size());
 
        testPrintBills(result);
    }
 
    // =========================================================
    // SORT TESTS
    // =========================================================
 
    static void testSortBillsById() {
 
        System.out.println("\n===== TEST SORT BILLS BY ID (via Controller) =====");
 
        List<Bill> bills = billCtrl.getAllBills();
        List<Bill> sorted = billCtrl.sortBillsById(bills);
 
        testPrintBills(sorted);
    }
 
    static void testSortBillsByTotalAmount() {
 
        System.out.println("\n===== TEST SORT BILLS BY TOTAL AMOUNT (via Controller) =====");
 
        List<Bill> bills = billCtrl.getAllBills();
        List<Bill> sorted = billCtrl.sortBillsByTotalAmount(bills);
 
        testPrintBills(sorted);
    }
 
    // =========================================================
    // CREATE TEST
    // =========================================================
 
    static void testCreateBill() {
 
        System.out.println("\n===== TEST CREATE BILL (via Controller) =====");
 
        Patient patient = (Patient) UserManagement.getPatientById("#PAT79a39");
        Insurance insurance = InsuranceManagement.getInsuranceById("#INS001");
 
        LinkedHashMap<String, String> result = billCtrl.createBill(
                SAMPLE_CONSULTATION_ID,
                patient,
                insurance,
                60.00
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Bill ID : " + result.get("newBillId"));
 
        // Controller-specific check: confirm the log entry was written with the
        // correct active-user context - open your log file and look for it.
        System.out.println(">> Now check logs.txt for the audit entry.");
    }
 
    // =========================================================
    // MODIFY TESTS
    // =========================================================
 
    static void testUpdateBill() {
 
        System.out.println("\n===== TEST UPDATE BILL (via Controller) =====");
 
        List<Bill> bills = billCtrl.getAllBills();
 
        if (bills.isEmpty()) {
            System.out.println("No bills available to update.");
            return;
        }
 
        Bill bill = bills.get(5);
 
        LinkedHashMap<String, String> result = billCtrl.updateBill(
                bill,
                bill.getPatient(),
                bill.getInsurance(),
                90.00
        );
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testMarkBillAsPaid() {
 
        System.out.println("\n===== TEST MARK BILL AS PAID (via Controller) =====");
 
        List<Bill> bills = billCtrl.getAllBills();
 
        if (bills.isEmpty()) {
            System.out.println("No bills available to mark as paid.");
            return;
        }
 
        Bill bill = bills.get(5);
 
        LinkedHashMap<String, String> result = billCtrl.markBillAsPaid(bill);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
 
    static void testCancelBill() {
 
        System.out.println("\n===== TEST CANCEL BILL (via Controller) =====");
 
        List<Bill> bills = billCtrl.getAllBills();
 
        if (bills.isEmpty()) {
            System.out.println("No bills available to cancel.");
            return;
        }
 
        Bill bill = bills.get(bills.size() - 1);
 
        LinkedHashMap<String, String> result = billCtrl.cancelBill(bill);
 
        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}
