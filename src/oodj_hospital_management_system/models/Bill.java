/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.models;

import java.util.Objects;

/**
 *
 * @author sheaw
 */
public class Bill {
    private String billId;
    private Consultation consultation;

    private double consultationFee;
    private double insuranceCoverage;
    private double labFee;
    private double totalAmount;

    private boolean paid;

    public Bill() {
        this.billId = "";
        this.consultation = null;
        this.consultationFee = 0.0;
        this.insuranceCoverage = 0.0;
        this.labFee = 0.0;
        this.totalAmount = 0.0;
        this.paid = false;
    }

    public Bill(
            String billId,
            Consultation consultation,
            double consultationFee,
            double insuranceCoverage,
            double labFee,
            double totalAmount,
            boolean paid
    ) {
        this.billId = billId;
        this.consultation = consultation;
        this.consultationFee = consultationFee;
        this.insuranceCoverage = insuranceCoverage;
        this.labFee = labFee;
        this.totalAmount = totalAmount;
        this.paid = paid;
    }

    @Override
    public String toString() {
        return String.format(
                "Bill {%s - RM %.2f}",
                this.billId,
                this.totalAmount
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bill)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        Bill billObj = (Bill) obj;
        return Objects.equals(this.billId, billObj.billId);
    }

    // Getters

    public String getBillId() {
        return billId;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public double getInsuranceCoverage() {
        return insuranceCoverage;
    }

    public double getLabFee() {
        return labFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    // Setters

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public void setInsuranceCoverage(double insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }

    public void setLabFee(double labFee) {
        this.labFee = labFee;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
