package TestServices;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.List;
import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.BillingManagement;
import oodj_hospital_management_system.services.InsuranceManagement;
import oodj_hospital_management_system.services.UserManagement;

/**
 *
 * @author sheaw
 */
public class TestBillingManagement {

    private static BillingManagement billMng;

    private static final String SAMPLE_CONSULTATION_ID = "#CON003"; // pick one not already billed

    public static void main(String[] args) {

        // Get the active user once - assuming admin staff manages billing
        User adminStaff = UserManagement.getAdminStaffById("#ADMd4e8f");

        if (adminStaff == null) {
            System.out.println(
                    "Test failed: Admin Staff '#ADMd4e8f' was not found."
            );
            return;
        }

        // Create BillingManagement once
        billMng = new BillingManagement(adminStaff);

        // Test loading
//        testLoadBills();

        // Test get by ID
//        testGetBillById();

        // Test search
//        testSearchBills();

        // Test sort
//        testSortBillsById();
//        testSortBillsByTotalAmount();

        // Test ID generation
//        testGenerateBillId();

        // Test creation
        // Uncomment when needed because these modify bills.txt
//        testCreateBill();

        // Test modification
        // Uncomment when needed because these modify bills.txt
//        testUpdateBill();
//        testMarkBillAsPaid();
//        testCancelBill();

        // Test generate bill from consultation
        // Uncomment when needed because these modify bills.txt
//        testGenerateBillFromConsultation();
//        testGenerateBillFromConsultationNoInsurance();
        testGenerateBillFromConsultationInvalidId();
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

    static void testLoadBills() {

        System.out.println("\n===== TEST LOAD BILLS =====");

        List<Bill> bills =
                BillingManagement.getAllBills();

        System.out.println("Total Bills: " + bills.size());

        testPrintBills(bills);
    }

    // =========================================================
    // GET BY ID TEST
    // =========================================================

    static void testGetBillById() {

        System.out.println("\n===== TEST GET BILL BY ID =====");

        List<Bill> bills =
                BillingManagement.getAllBills();

        if (bills.isEmpty()) {
            System.out.println("No bills available.");
            return;
        }

        String billId = bills.get(0).getBillId();

        Bill bill =
                BillingManagement.getBillById(billId);

        if (bill != null) {

            System.out.println("Bill found:");
            System.out.println("Bill ID  : " + bill.getBillId());
            System.out.println("Total    : " + bill.getTotalAmount());
            System.out.println("Payable  : " + bill.getPayableAmount());
            System.out.println("Status   : " + bill.getStatus());

        } else {
            System.out.println("Test failed: Bill not found.");
        }
    }

    // =========================================================
    // SEARCH TEST
    // =========================================================

    static void testSearchBills() {

        System.out.println("\n===== TEST SEARCH BILLS =====");

        String keyword = "#CON001";

        List<Bill> result =
                billMng.searchBills(keyword);

        System.out.println("Search keyword: " + keyword);
        System.out.println("Matched bills: " + result.size());

        testPrintBills(result);
    }

    // =========================================================
    // SORT TESTS
    // =========================================================

    static void testSortBillsById() {

        System.out.println("\n===== TEST SORT BILLS BY ID =====");

        List<Bill> bills =
                BillingManagement.getAllBills();

        List<Bill> sorted =
                billMng.sortBillsById(bills);

        testPrintBills(sorted);
    }

    static void testSortBillsByTotalAmount() {

        System.out.println("\n===== TEST SORT BILLS BY TOTAL AMOUNT =====");

        List<Bill> bills =
                BillingManagement.getAllBills();

        List<Bill> sorted =
                billMng.sortBillsByTotalAmount(bills);

        testPrintBills(sorted);
    }

    // =========================================================
    // GENERATE ID TEST
    // =========================================================

    static void testGenerateBillId() {

        System.out.println("\n===== TEST GENERATE BILL ID =====");

        String billId = BillingManagement.generateBillId();

        System.out.println("Generated Bill ID : " + billId);
    }

    // =========================================================
    // CREATE TEST
    // =========================================================

    static void testCreateBill() {

        System.out.println("\n===== TEST CREATE BILL =====");

        Patient patient = (Patient) UserManagement.getPatientById("#PAT79a39");
        Insurance insurance = InsuranceManagement.getInsuranceById("#INS001");

        LinkedHashMap<String, String> result = billMng.createBill(
                SAMPLE_CONSULTATION_ID,
                patient,
                insurance,
                60.00
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
        System.out.println("New Bill ID : " + result.get("newBillId"));
    }

    // =========================================================
    // MODIFY TESTS
    // =========================================================

    static void testUpdateBill() {

        System.out.println("\n===== TEST UPDATE BILL =====");

        List<Bill> bills =
                BillingManagement.getAllBills();

        if (bills.isEmpty()) {
            System.out.println("No bills available to update.");
            return;
        }

        Bill bill = bills.get(0);

        LinkedHashMap<String, String> result = billMng.updateBill(
                bill,
                bill.getPatient(),
                bill.getInsurance(),
                90.00
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testMarkBillAsPaid() {

        System.out.println("\n===== TEST MARK BILL AS PAID =====");

        List<Bill> bills =
                BillingManagement.getAllBills();

        if (bills.isEmpty()) {
            System.out.println("No bills available to mark as paid.");
            return;
        }

        Bill bill = bills.get(0);

        LinkedHashMap<String, String> result = billMng.markBillAsPaid(bill);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    static void testCancelBill() {

        System.out.println("\n===== TEST CANCEL BILL =====");

        List<Bill> bills =
                BillingManagement.getAllBills();

        if (bills.isEmpty()) {
            System.out.println("No bills available to cancel.");
            return;
        }

        // Pick the last bill so it doesn't collide with the one just marked paid above
        Bill bill = bills.get(bills.size() - 1);

        LinkedHashMap<String, String> result = billMng.cancelBill(bill);

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }

    // =========================================================
    // GENERATE BILL FROM CONSULTATION TESTS
    // =========================================================

    static void testGenerateBillFromConsultation() {

        System.out.println("\n===== TEST GENERATE BILL FROM CONSULTATION =====");

        // #CON001 has lab results (#LAB001, #LAB002, #LAB4f982) and
        // prescriptions (#PRE001, #PRE002, #PREf0b85) attached to it.
        // Remove/void the existing #BILL001 row in bills.txt first, otherwise
        // this creates a duplicate bill on top of the already-paid one.
        LinkedHashMap<String, String> result = billMng.generateBillFromConsultation(
                "#CON001",
                "#INS001"
        );

        System.out.println("Success              : " + result.get("success"));
        System.out.println("Message              : " + result.get("msg"));
        System.out.println("New Bill ID          : " + result.get("newBillId"));
        System.out.println("Consultation Type    : " + result.get("consultationTypeName"));
        System.out.println("Consultation Charge  : " + result.get("consultationCharge"));
        System.out.println("Lab Types            : " + result.get("labTypeNames"));
        System.out.println("Lab Charge           : " + result.get("labCharge"));
        System.out.println("Prescription Types   : " + result.get("prescriptionTypeNames"));
        System.out.println("Prescription Charge  : " + result.get("prescriptionCharge"));
        System.out.println("Total Charge         : " + result.get("totalCharge"));
    }

    static void testGenerateBillFromConsultationNoInsurance() {

        System.out.println("\n===== TEST GENERATE BILL FROM CONSULTATION (NO INSURANCE) =====");

        // #CONd3224 has no existing bill and no lab/prescription records,
        // exercises the empty-breakdown path with insuranceId = null
        LinkedHashMap<String, String> result = billMng.generateBillFromConsultation(
                "#CONd3224",
                null
        );

        System.out.println("Success              : " + result.get("success"));
        System.out.println("Message              : " + result.get("msg"));
        System.out.println("New Bill ID          : " + result.get("newBillId"));
        System.out.println("Consultation Type    : " + result.get("consultationTypeName"));
        System.out.println("Consultation Charge  : " + result.get("consultationCharge"));
        System.out.println("Lab Types            : " + result.get("labTypeNames"));
        System.out.println("Lab Charge           : " + result.get("labCharge"));
        System.out.println("Prescription Types   : " + result.get("prescriptionTypeNames"));
        System.out.println("Prescription Charge  : " + result.get("prescriptionCharge"));
        System.out.println("Total Charge         : " + result.get("totalCharge"));
    }

    static void testGenerateBillFromConsultationInvalidId() {

        System.out.println("\n===== TEST GENERATE BILL FROM CONSULTATION (INVALID CONSULTATION ID) =====");

        LinkedHashMap<String, String> result = billMng.generateBillFromConsultation(
                "#CON999",
                "#INS001"
        );

        System.out.println("Success : " + result.get("success"));
        System.out.println("Message : " + result.get("msg"));
    }
}