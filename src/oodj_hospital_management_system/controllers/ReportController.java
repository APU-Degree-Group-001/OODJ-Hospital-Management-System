/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.ReportManagement;

/**
 *
 * @author paure
 */
public class ReportController {
    private ReportManagement reportServices;
    private User activeUser;
    
    // Billing / Revenue Report
    public ReportController(User activeUser) {
        this.activeUser = activeUser;
        this.reportServices = new ReportManagement();
    }
 
    public LinkedHashMap<String, Double> getRevenueByMonth() {
        return ReportManagement.getRevenueByMonth();
    }
 
    public LinkedHashMap<String, Double> getBillStatusBreakdown() {
        return ReportManagement.getBillStatusBreakdown();
    }
    
    public LinkedHashMap<String, String> getBillingTotals() {
        return ReportManagement.getBillingTotals();
    }

    public LinkedHashMap<String, String> getBillingTotals(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getBillingTotals(startDate, endDate);
    }

    public LinkedHashMap<String, Double> getBillStatusBreakdown(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getBillStatusBreakdown(startDate, endDate);
    }
    
    public LinkedHashMap<String, Double> getRevenueByWeek(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getRevenueByWeek(startDate, endDate);
    }
 
    // Insurance Usage Report
    public LinkedHashMap<String, String> getInsuranceTotals(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getInsuranceTotals(startDate, endDate);
    }
 
    public LinkedHashMap<String, Double> getInsuranceUsageByProvider(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getInsuranceUsageByProvider(startDate, endDate);
    }
 
    public LinkedHashMap<String, Double> getPayableAmountByInsurance(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getPayableAmountByInsurance(startDate, endDate);
    }
 
    // Consultation Report
    public LinkedHashMap<String, String> getConsultationTotals(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getConsultationTotals(startDate, endDate);
    }
 
    public LinkedHashMap<String, Double> getConsultationsByStatus(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getConsultationsByStatus(startDate, endDate);
    }
 
    public LinkedHashMap<String, Double> getConsultationsByDoctor(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getConsultationsByDoctor(startDate, endDate);
    }
 
    public LinkedHashMap<String, Double> getConsultationsByMonth(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getConsultationsByMonth(startDate, endDate);
    }
 
    public LinkedHashMap<String, Double> getConsultationsByWeek(LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getConsultationsByWeek(startDate, endDate);
    }
 
    // Doctor Shift Report
    // Doctor Shift Report
    public LinkedHashMap<String, String> getDoctorShiftTotals(List<Doctor> doctors, LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getDoctorShiftTotals(doctors, startDate, endDate);
    }

    public LinkedHashMap<String, Double> getShiftsByStatus(List<Doctor> doctors, LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getShiftsByStatus(doctors, startDate, endDate);
    }

    public LinkedHashMap<String, Double> getScheduledShiftHoursByDoctor(List<Doctor> doctors, LocalDate startDate, LocalDate endDate) {
        return ReportManagement.getScheduledShiftHoursByDoctor(doctors, startDate, endDate);
    }
 
    // Room / Hospital Asset Utilization Report
    public LinkedHashMap<String, String> getRoomUtilizationTotals() {
        return ReportManagement.getRoomUtilizationTotals();
    }
 
    public LinkedHashMap<String, Double> getAllocationCountByAsset() {
        return ReportManagement.getAllocationCountByAsset();
    }
 
    public LinkedHashMap<String, Double> getOccupiedMinutesByAsset() {
        return ReportManagement.getOccupiedMinutesByAsset();
    }
}
