/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 *
 * @author sheaw
 */

public class Bill {
    public enum BillStatus { UNPAID, PAID, VOID }

    private String billId;
    private String consultationId;
    private Patient patient;
    private Insurance insurance;
    private double consultationFee;
    private double totalAmount;
    private double payableAmount;
    private BillStatus status;

    public Bill() {
        this.billId = "";
        this.consultationId = "";
        this.patient = null;
        this.insurance = null;
        this.consultationFee = 0.0;
        this.totalAmount = 0.0;
        this.payableAmount = 0.0;
        this.status = BillStatus.UNPAID;
    }

    public Bill(
            String billId,
            String consultation,
            Patient patient,
            Insurance insurance,
            double consultationFee,
            double totalAmount,
            double payableAmount,
            BillStatus status
    ) {
        this.billId = billId;
        this.consultationId = consultation;
        this.patient = patient;
        this.insurance = insurance;
        this.consultationFee = consultationFee;
        this.totalAmount = totalAmount;
        this.payableAmount = payableAmount;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Bill {%s - RM %.2f - %s}", this.billId, this.totalAmount, this.status);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bill)) return false;
        if (obj == this) return true;
        Bill billObj = (Bill) obj;
        return Objects.equals(this.billId, billObj.billId);
    }

    // Getters
    public String getBillId() { return billId; }
    public String getConsultationId() { return consultationId; }
    public Patient getPatient() { return patient; }
    public Insurance getInsurance() { return insurance; }
    public double getConsultationFee() { return consultationFee; }
    public double getTotalAmount() { return totalAmount; }
    public double getPayableAmount() { return payableAmount; }
    public BillStatus getStatus() { return status; }

    // Setters
    public void setConsultationId(String consultationId) { this.consultationId = consultationId; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setInsurance(Insurance insurance) { this.insurance = insurance; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setPayableAmount(double payableAmount) { this.payableAmount = payableAmount; }
    public void setStatus(BillStatus status) { this.status = status; }

    public LinkedHashMap<String, String> getBillMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("billId", billId);
        map.put("consultationId", consultationId);
        map.put("patientId", patient.getId());
        map.put("insuranceId", insurance != null ? insurance.getInsuranceId() : "");
        map.put("consultationFee", Double.toString(consultationFee));
        map.put("totalAmount", Double.toString(totalAmount));
        map.put("payableAmount", Double.toString(payableAmount));
        map.put("status", status.toString());
        return map;
    }
}
