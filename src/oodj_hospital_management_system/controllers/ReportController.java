/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.time.LocalDate;
import java.util.LinkedHashMap;
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
 
    public LinkedHashMap<String, Double> getInsuranceUsageByProvider() {
        return ReportManagement.getInsuranceUsageByProvider();
    }
 
    public LinkedHashMap<String, Double> getPayableAmountByInsurance() {
        return ReportManagement.getPayableAmountByInsurance();
    }
 
    // Consultation Report
    public LinkedHashMap<String, String> getConsultationTotals() {
        return ReportManagement.getConsultationTotals();
    }
 
    public LinkedHashMap<String, Double> getConsultationsByStatus() {
        return ReportManagement.getConsultationsByStatus();
    }
 
    public LinkedHashMap<String, Double> getConsultationsByDoctor() {
        return ReportManagement.getConsultationsByDoctor();
    }
 
    public LinkedHashMap<String, Double> getConsultationsByMonth() {
        return ReportManagement.getConsultationsByMonth();
    }
 
    // Doctor Shift Report
    public LinkedHashMap<String, String> getDoctorShiftTotals() {
        return ReportManagement.getDoctorShiftTotals();
    }
 
    public LinkedHashMap<String, Double> getShiftsByStatus() {
        return ReportManagement.getShiftsByStatus();
    }
 
    public LinkedHashMap<String, Double> getScheduledShiftHoursByDoctor() {
        return ReportManagement.getScheduledShiftHoursByDoctor();
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
