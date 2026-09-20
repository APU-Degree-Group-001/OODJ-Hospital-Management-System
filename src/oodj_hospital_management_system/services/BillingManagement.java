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
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.LabResultType;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.PrescriptionType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.utils.DateTime;
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

    // Cache state: bills are only re-read when bills.txt changed,
    // or when the users were reloaded (each Bill holds a Patient object from UserManagement)
    private static boolean loaded = false;
    private static String loadedFingerprint = null;
    private static int loadedUsersVersion = -1;

    public BillingManagement(User usr) {
        this.activeUser = usr;
        ensureLoaded();
    }

    private static void ensureLoaded() {
        String current = billHandler.fingerprint();
        int usersVersion = UserManagement.getLoadVersion();
        if (!loaded
                || current == null
                || !current.equals(loadedFingerprint)
                || usersVersion != loadedUsersVersion) {
            loadBills();
        }
    }

    public static void loadBills() {
        String fingerprint = billHandler.fingerprint();
        int usersVersion = UserManagement.getLoadVersion();
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
                    getBillStatus(row.get("status")),
                    row.get("generatedDateTime")
            );
            bills.add(bill);
        }
        loaded = true;
        loadedFingerprint = fingerprint;
        loadedUsersVersion = usersVersion;
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
        ensureLoaded();
        // return a copy so sorting/filtering in the UI can't reorder or damage the cached list
        return new ArrayList<>(bills);
    }

    public static Bill getBillById(String billId) {
        ensureLoaded();
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
        ensureLoaded();
        for (Bill bill : bills) {
            if (bill.getConsultationId().equals(consultationId)) {
                return bill;
            }
        }
        return null;
    }
    
    public static Bill getBillByConsultationId(String consultationId) {
        ensureLoaded();
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
        ensureLoaded();
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
        String generatedDateTime = DateTime.toDateTimeString(DateTime.currentDateTime());
        Bill newBill = new Bill(newBillId, consultationId, patient, insurance, consultationFee, totalAmount, payableAmount, getBillStatus("UNPAID"), generatedDateTime);
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

        // getBillById() calls ensureLoaded(), so 'bills' is fresh from here on
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

        // getBillById() calls ensureLoaded(), so 'bills' is fresh from here on
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

        // getBillById() calls ensureLoaded(), so 'bills' is fresh from here on
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
    
    // Generate Bills
    public LinkedHashMap<String, String> generateBillFromConsultation(String consultationId, String insuranceId) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();

        Consultation consultation = ConsultationManagement.getConsultationById(consultationId);
        if (consultation == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not generated\nConsultation is not found");
            return msgMap;
        }

        Patient patient = consultation.getPatient();
        if (patient == null) {
            msgMap.put("success", null);
            msgMap.put("msg", "Bill is not generated\nPatient is not found");
            return msgMap;
        }

        Insurance insurance = (insuranceId == null || insuranceId.isBlank())
                ? null
                : InsuranceManagement.getInsuranceById(insuranceId);

        // Itemized breakdown — each computed separately so we can report the
        // type names alongside their charges, even though Bill/bills.txt only
        // stores the combined total
        ConsultationType consultationType = consultation.getConsultationType();
        double consultationCharge = (consultationType != null) ? consultationType.getBaseRate() : 0.0;

        LinkedHashMap<String, String> labBreakdown = calculateLabCharges(consultation);
        double labCharge = Double.parseDouble(labBreakdown.get("labCharge"));

        LinkedHashMap<String, String> prescriptionBreakdown = calculatePrescriptionCharges(consultation);
        double prescriptionCharge = Double.parseDouble(prescriptionBreakdown.get("prescriptionCharge"));

        double totalCharge = consultationCharge + labCharge + prescriptionCharge;

        // createBill()'s "consultationFee" param is really used internally as the
        // pre-insurance totalAmount (see: double totalAmount = consultationFee;)
        // so passing the combined total here matches existing behaviour
        msgMap = createBill(consultationId, patient, insurance, totalCharge);

        // Attach the itemized breakdown on top of createBill()'s own success/msg/newBillId
        msgMap.put("consultationTypeName", (consultationType != null) ? consultationType.getTypeName() : "");
        msgMap.put("consultationCharge", String.valueOf(consultationCharge));
        msgMap.put("labItems", labBreakdown.get("labItems"));
        msgMap.put("labCharge", String.valueOf(labCharge));
        msgMap.put("prescriptionItems", prescriptionBreakdown.get("prescriptionItems"));
        msgMap.put("prescriptionCharge", String.valueOf(prescriptionCharge));
        msgMap.put("totalCharge", String.valueOf(totalCharge));

        return msgMap;
    }

    private LinkedHashMap<String, String> calculateLabCharges(Consultation consultation) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        double labCharge = 0.0;
        List<String> labItems = new ArrayList<>();

        List<LabResult> labResults = consultation.getLabResults();
        if (labResults != null) {
            for (LabResult labResult : labResults) {
                if (labResult.getResultId() != null) {
                    LabResultType labResultType = LabResultTypeManagement.getLabResultTypeByName(labResult.getResultType());
                    if (labResultType != null) {
                        double itemCharge = labResultType.getBaseRate();
                        labCharge += itemCharge;
                        labItems.add(String.format("%s: RM%.2f", labResultType.getTypeName(), itemCharge));
                    }
                }
            }
        }

        result.put("labCharge", String.valueOf(labCharge));
        result.put("labItems", String.join(", ", labItems));
        return result;
    }

    private LinkedHashMap<String, String> calculatePrescriptionCharges(Consultation consultation) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        double prescriptionCharge = 0.0;
        List<String> prescriptionItems = new ArrayList<>();

        List<Prescription> prescriptions = consultation.getPrescriptions();
        if (prescriptions != null) {
            for (Prescription prescription : prescriptions) {
                if (prescription.getPrescriptionId() != null) {
                    PrescriptionType prescriptionType = PrescriptionTypeManagement.getPrescriptionTypeByName(prescription.getMedicineName());
                    if (prescriptionType != null) {
                        int frequencyPerDay = Integer.parseInt(prescription.getFrequencyPerDay());
                        int durationInDays = Integer.parseInt(prescription.getDurationInDays());
                        int totalUnits = frequencyPerDay * durationInDays;
                        double itemCharge = prescriptionType.getBaseRate() * totalUnits;
                        prescriptionCharge += itemCharge;
                        prescriptionItems.add(String.format("%s (x%d): RM%.2f", prescriptionType.getTypeName(), totalUnits, itemCharge));
                    }
                }
            }
        }

        result.put("prescriptionCharge", String.valueOf(prescriptionCharge));
        result.put("prescriptionItems", String.join(", ", prescriptionItems));
        return result;
    }

    // Searching and Sorting
    public List<Bill> searchBills(String keywords) {
        ensureLoaded();
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
        ensureLoaded();
        List<String> existingIds = bills.stream().map(Bill::getBillId).toList();
        return SystemConfiguration.generateId("#BILL", existingIds);
    }
}