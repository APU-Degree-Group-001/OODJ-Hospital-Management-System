/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.ErrorHandler;
import oodj_hospital_management_system.utils.FileHandler;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.exceptions.BillException;
import oodj_hospital_management_system.utils.exceptions.UserException;

/**
 *
 * @author sheaw
 */

public class BillingManagement {
    private static final FileHandler billHandler = new FileHandler(SystemConfiguration.billFilePath);
    private static final List<Bill> bills = new ArrayList<>();
    private User activeUser;

    public BillingManagement(User usr) {
        this.activeUser = usr;
        loadBills();
    }

    public static void loadBills() {
        bills.clear();
        List<LinkedHashMap<String, String>> rawData = billHandler.readFile();
        for (LinkedHashMap<String, String> row : rawData) {
            // billId,consultationId,patientId,insuranceId,consultationFee,totalAmount,payableAmount
            String insuranceId = row.get("insuranceId");
            Insurance insurance = (insuranceId == null || insuranceId.isBlank()) ? null : InsuranceManagement.getInsuranceById(insuranceId);
            Bill bill = new Bill(
                    row.get("billId"),
                    row.get("consultationId"),
                    UserManagement.getPatientById(row.get("patientId")),
                    insurance,
                    Double.parseDouble(row.get("consultationFee")),
                    Double.parseDouble(row.get("totalAmount")),
                    Double.parseDouble(row.get("payableAmount")),
                    getBillStatus(row.get("status"))
            );
            bills.add(bill);
        }
    }
    
    public static Bill.BillStatus getBillStatus(String status) {
        Bill.BillStatus billStatus = null;
        switch (status) {
            case "UNPAID": billStatus = Bill.BillStatus.UNPAID; break;
            case "PAID": billStatus = Bill.BillStatus.PAID; break;
            case "VOID": billStatus = Bill.BillStatus.VOID; break;
            default: System.out.println("Unknown Bill Status");
        }
        return billStatus;
    }

    public static List<Bill> getAllBills() {
        loadBills();
        return bills;
    }

    public static Bill getBillById(String billId) {
        loadBills();
        try {
            if (!bills.stream().anyMatch(bill -> bill.getBillId().equals(billId))) {
                throw new BillException(String.format("Invalid Bill ID: Bill ID '%s' is not found", billId));
            }
            for (Bill bill : bills) {
                if (bill.getBillId().equals(billId)) {
                    return bill;
                }
            }
            return null;
        } catch (BillException e) {
            System.out.println("Error finding bill -> " + e.getMessage());
            Logger.errorLog("Error finding bill -> " + e.getMessage());
            return null;
        }
    }
    
    public static Bill findBillByConsultationId(String consultationId) {
        for (Bill bill : bills) {
            if (bill.getConsultationId().equals(consultationId)) {
                return bill;
            }
        }
        return null;
    }
    
    public static Bill getBillByConsultationId(String consultationId) {
        loadBills();
        for (Bill bill : bills) {
            if (bill.getConsultationId().equals(consultationId)) {
                return bill;
            }
        }
        return null;
    }

    // File Handler (Write and Append)
    private static boolean writeBills() {
        ArrayList<LinkedHashMap<String, String>> billMaps = new ArrayList<>();
        for (Bill bill : bills) {
            billMaps.add(bill.getBillMap());
        }
        billHandler.writeFile(billMaps);
        return true;
    }

    private static boolean appendBill(Bill bill) {
        try {
            ArrayList<LinkedHashMap<String, String>> billMaps = new ArrayList<>();
            billMaps.add(bill.getBillMap());
            if (!billHandler.checkFileExistence() && !billHandler.createFile()) {
                System.out.printf("File 'bills.txt' failed to be created, bill '%s' cannot be appended%n", bill.getBillId());
                return false;
            }
            billHandler.appendFile(billMaps);
            System.out.printf("Bill '%s' is appended to bills.txt!%n", bill.getBillId());
            return true;
        } catch (Exception e) {
            System.out.println("Error appending new bill: " + e.getMessage());
            Logger.errorLog("Error appending new bill (System Error) -> " + e.getMessage() + "\n");
            return false;
        }
    }

    public LinkedHashMap<String, String> createBill(String consultationId, Patient patient, Insurance insurance, double consultationFee) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        try {
            ErrorHandler.billErrorHandler(Arrays.asList("admin staff", "consultation", "patient", "consultationFee"), activeUser, consultationId, patient, consultationFee);
        } catch (BillException e) {
            System.out.println("Error creating bill\n" + e.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not created\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            System.out.println("Error creating bill\n" + ue.getMessage());
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not created\n" + ue.getMessage());
            return msgMap;
        }

        double totalAmount = consultationFee;
        double payableAmount = (insurance != null) ? insurance.calculatePayableAmount(totalAmount) : totalAmount;

        String newBillId = generateBillId();
        Bill newBill = new Bill(newBillId, consultationId, patient, insurance, consultationFee, totalAmount, payableAmount, getBillStatus("UNPAID"));
        bills.add(newBill);
        if (billHandler.checkFileExistence()) {
            appendBill(newBill);
        } else {
            writeBills();
        }
        loadBills();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Bill '%s' is created successfully!", newBillId));
        msgMap.put("newBillId", newBillId);
        return msgMap;
    }
    
    public LinkedHashMap<String, String> updateBill(Bill bill, Patient patient, Insurance insurance, double consultationFee) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.billErrorHandler(Arrays.asList("admin staff", "update bill", "patient", "consultationFee"), activeUser, bill, patient, consultationFee);
        } catch (BillException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not updated\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not updated\n" + ue.getMessage());
            return msgMap;
        }

        Bill targetBill = getBillById(bill.getBillId());
        if (targetBill == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not found");
            return msgMap;
        }

        if (Bill.BillStatus.PAID.equals(targetBill.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Bill '%s' is already paid and cannot be modified!", targetBill.getBillId()));
            return msgMap;
        }
        if (Bill.BillStatus.VOID.equals(targetBill.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Bill '%s' is voided and cannot be modified!", targetBill.getBillId()));
            return msgMap;
        }

        double totalAmount = consultationFee;
        double payableAmount = (insurance != null) ? insurance.calculatePayableAmount(totalAmount) : totalAmount;

        targetBill.setPatient(patient);
        targetBill.setInsurance(insurance);
        targetBill.setConsultationFee(consultationFee);
        targetBill.setTotalAmount(totalAmount);
        targetBill.setPayableAmount(payableAmount);

        writeBills();
        loadBills();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Bill '%s' is updated successfully!", targetBill.getBillId()));
        return msgMap;
    }

    public LinkedHashMap<String, String> cancelBill(Bill bill) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        try {
            ErrorHandler.billErrorHandler(Arrays.asList("admin staff", "cancel bill"), activeUser, bill);
        } catch (BillException e) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not cancelled\n" + e.getMessage());
            return msgMap;
        } catch (UserException ue) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not cancelled\n" + ue.getMessage());
            return msgMap;
        }

        Bill targetBill = getBillById(bill.getBillId());
        if (targetBill == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not found");
            return msgMap;
        }

        if (Bill.BillStatus.VOID.equals(targetBill.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Bill '%s' is already voided!", targetBill.getBillId()));
            return msgMap;
        }
        if (Bill.BillStatus.PAID.equals(targetBill.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Bill '%s' has already been paid and cannot be cancelled!", targetBill.getBillId()));
            return msgMap;
        }

        targetBill.setStatus(Bill.BillStatus.VOID);
        writeBills();
        loadBills();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Bill '%s' is voided!", targetBill.getBillId()));
        return msgMap;
    }

    public LinkedHashMap<String, String> markBillAsPaid(Bill bill) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Bill targetBill = getBillById(bill.getBillId());
        if (targetBill == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not found");
            return msgMap;
        }

        if (Bill.BillStatus.PAID.equals(targetBill.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Bill '%s' is already paid!", targetBill.getBillId()));
            return msgMap;
        }
        if (Bill.BillStatus.VOID.equals(targetBill.getStatus())) {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Bill '%s' is voided and cannot be paid!", targetBill.getBillId()));
            return msgMap;
        }

        targetBill.setStatus(Bill.BillStatus.PAID);
        writeBills();
        loadBills();

        msgMap.put("success", "true");
        msgMap.put("msg", String.format("Bill '%s' is marked as paid!", targetBill.getBillId()));
        return msgMap;
    }

    // Searching and Sorting
    public List<Bill> searchBills(String keywords) {
        List<Bill> matchedBills = new ArrayList<>();
        for (Bill bill : bills) {
            if (bill.getBillId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || bill.getConsultationId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || bill.getPatient().getName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedBills.add(bill);
            }
        }
        return matchedBills;
    }

    public List<Bill> searchBills(String keywords, List<Bill> billList) {
        List<Bill> matchedBills = new ArrayList<>();
        for (Bill bill : billList) {
            if (bill.getBillId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || bill.getConsultationId().toLowerCase().contains(keywords.toLowerCase().trim())
                    || bill.getPatient().getName().toLowerCase().contains(keywords.toLowerCase().trim())) {
                matchedBills.add(bill);
            }
        }
        return matchedBills;
    }

    public List<Bill> sortBillsById(List<Bill> list) {
        List<Bill> sortedBills = list;
        sortedBills.sort(Comparator.comparing(Bill::getBillId));
        return sortedBills;
    }

    public List<Bill> sortBillsByTotalAmount(List<Bill> list) {
        List<Bill> sortedBills = list;
        sortedBills.sort(Comparator.comparingDouble(Bill::getTotalAmount));
        return sortedBills;
    }

    // Generate ID
    public static String generateBillId() {
        List<String> existingIds = bills.stream().map(Bill::getBillId).toList();
        return SystemConfiguration.generateId("#BILL", existingIds);
    }
}
