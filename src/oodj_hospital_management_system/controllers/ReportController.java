/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.controllers;

import java.util.LinkedHashMap;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.ReportManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.Logger;
import oodj_hospital_management_system.utils.ReportPDFExporter;

/**
 *
 * @author paure
 */
public class ReportController {
    
    // Billing / Revenue Report
    public LinkedHashMap<String, String> getBillingTotals() {
        return ReportManagement.getBillingTotals();
    }
 
    public LinkedHashMap<String, Double> getRevenueByMonth() {
        return ReportManagement.getRevenueByMonth();
    }
 
    public LinkedHashMap<String, Double> getBillStatusBreakdown() {
        return ReportManagement.getBillStatusBreakdown();
    }
 
    // Insurance Usage Report
    public LinkedHashMap<String, String> getInsuranceTotals() {
        return ReportManagement.getInsuranceTotals();
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
    
    // Export PDF
    public LinkedHashMap<String, String> exportBillingReportToPdf(String fileName, String folderPath, User activeUser) {
        LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
        String filePath = ReportPDFExporter.filePath(fileName, folderPath);
        String datetime = DateTime.formattedLocalDateTime(DateTime.currentDateTime());
 
        boolean success = ReportPDFExporter.exportBillingReport(
                filePath,
                activeUser.getName(),
                datetime,
                getBillingTotals(),
                getRevenueByMonth(),
                getBillStatusBreakdown()
        );
 
        if (success) {
            msgMap.put("success", "true");
            msgMap.put("msg", String.format("Billing report exported to '%s'!", filePath));
            msgMap.put("filePath", filePath);
            System.out.println(msgMap.get("msg"));
            Logger.log(String.format("User {%s - %s - %s} exported Billing Report to '%s'",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), filePath));
        } else {
            msgMap.put("success", null);
            msgMap.put("msg", String.format("Billing report export to '%s' failed", filePath));
            System.out.println(msgMap.get("msg"));
            Logger.errorLog(String.format("User {%s - %s - %s} failed to export Billing Report to '%s'",
                    activeUser.getType(), activeUser.getId(), activeUser.getName(), filePath));
        }
        return msgMap;
    }
}
