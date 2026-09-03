/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.medical_manager.reporting;

import oodj_hospital_management_system.gui.medical_manager.billing_management.*;
import oodj_hospital_management_system.gui.admin.user_management.*;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.ReportPDFExporter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author sheaw
 */
public class InsurancePanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final java.time.format.DateTimeFormatter MONTH_LABEL_FMT = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy");
    private LocalDate selectedStartDate;
    private LocalDate selectedEndDate;

    public InsurancePanel(UserController usrCtrl) {
        initComponents();
        this.usrCtrl = usrCtrl;

        LocalDate now = LocalDate.now();
        selectedStartDate = now.withDayOfMonth(1);
        selectedEndDate = now.withDayOfMonth(now.lengthOfMonth());
        jLabel2.setText(selectedStartDate.format(MONTH_LABEL_FMT));

        loadSummaryPanel();
        loadInsuranceByProviderBarChart();
//        loadRevenueByMonthChart();
        loadPayableAmountByInsuranceProvider();
    }
    
    private void loadSummaryPanel() {
        LinkedHashMap<String, String> totals = this.usrCtrl.getReportController().getInsuranceTotals(selectedStartDate, selectedEndDate);

        totalBills.setText(String.format("%s", totals.get("billsWithInsurance")));
        totalBillsWithInsurance.setText(String.format("%s", totals.get("billsWithInsurance")));
        totalBillsWithoutInsurance.setText(String.format("%s", totals.get("billsWithoutInsurance")));
    }

    private void loadInsuranceByProviderBarChart() {
        LinkedHashMap<String, Double> insuranceData = this.usrCtrl.getReportController().getInsuranceUsageByProvider(selectedStartDate, selectedEndDate);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Add insurance providers first
        for (Map.Entry<String, Double> entry : insuranceData.entrySet()) {

            if (!entry.getKey().equals("No Insurance")) {
                dataset.addValue( entry.getValue(), "Bills", entry.getKey());
            }
        }

        // Add No Insurance last
        if (insuranceData.containsKey("No Insurance")) {
            dataset.addValue(
                    insuranceData.get("No Insurance"),
                    "Bills",
                    "No Insurance"
            );
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Bills by Insurance Provider",
                "Insurance Provider",
                "Number of Bills",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);

        billsByInsuranceProviderPanel.removeAll();
        billsByInsuranceProviderPanel.setLayout(new BorderLayout());
        billsByInsuranceProviderPanel.add(
                chartPanel,
                BorderLayout.CENTER
        );

        billsByInsuranceProviderPanel.revalidate();
        billsByInsuranceProviderPanel.repaint();
    }
    
    private void loadPayableAmountByInsuranceProvider() {
        LinkedHashMap<String, Double> payableAmount = this.usrCtrl.getReportController().getPayableAmountByInsurance(selectedStartDate, selectedEndDate);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : payableAmount.entrySet()) {
            dataset.addValue(entry.getValue(), "Payable Amounts", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Payable Amounts by Insurance Provider",
                "Insurance Provider",
                "Payable Amounts",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);

        payableAmountByInsuranceProviderPanel.removeAll();
        payableAmountByInsuranceProviderPanel.setLayout(new BorderLayout());
        payableAmountByInsuranceProviderPanel.add(chartPanel, BorderLayout.CENTER);

        payableAmountByInsuranceProviderPanel.revalidate();
        payableAmountByInsuranceProviderPanel.repaint();
    }

        /** Shows a month/year picker dialog, returns {firstDayOfMonth, lastDayOfMonth} or null if cancelled. */
    private LocalDate[] showMonthPickerDialog() {
        JMonthChooser monthChooser = new com.toedter.calendar.JMonthChooser();
        JYearChooser yearChooser = new com.toedter.calendar.JYearChooser();
        yearChooser.setPreferredSize(new java.awt.Dimension(200, 30));
        yearChooser.setMaximumSize(new java.awt.Dimension(200, 30));

        LocalDate now = LocalDate.now();
        monthChooser.setMonth(now.getMonthValue() - 1);
        yearChooser.setYear(now.getYear());
        monthChooser.setYearChooser(yearChooser);

        JLabel monthLabel = new javax.swing.JLabel("Month:");
        JLabel yearLabel = new javax.swing.JLabel("Year:");

        monthLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        monthChooser.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        yearLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        yearChooser.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.add(monthLabel);
        panel.add(monthChooser);
        panel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        panel.add(yearLabel);
        panel.add(yearChooser);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Select Month",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        int selectedMonth = monthChooser.getMonth() + 1;
        int selectedYear = yearChooser.getYear();

        LocalDate start = LocalDate.of(selectedYear, selectedMonth, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return new LocalDate[]{start, end};
    }
    
    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(java.awt.Color.WHITE);
        if (chart.getTitle() != null) {
            chart.getTitle().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        }
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        }

        org.jfree.chart.plot.Plot plot = chart.getPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(false);

        if (plot instanceof org.jfree.chart.plot.CategoryPlot) {
            org.jfree.chart.plot.CategoryPlot cp = (org.jfree.chart.plot.CategoryPlot) plot;
            cp.setDomainGridlinePaint(new java.awt.Color(230, 233, 237));
            cp.setRangeGridlinePaint(new java.awt.Color(230, 233, 237));
            cp.getDomainAxis().setLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
            cp.getDomainAxis().setTickLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
            cp.getRangeAxis().setLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
            cp.getRangeAxis().setTickLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        } else if (plot instanceof org.jfree.chart.plot.PiePlot) {
            org.jfree.chart.plot.PiePlot pp = (org.jfree.chart.plot.PiePlot) plot;
            pp.setLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
            pp.setLabelBackgroundPaint(new java.awt.Color(255, 255, 230));
            pp.setBackgroundPaint(java.awt.Color.WHITE);
        }
    }
    
    private String selectFolderDir() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Directory to Save Report");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnValue = fileChooser.showDialog(parentWindow, "Select Folder");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            String folderPath = selectedDirectory.getAbsolutePath();
            return folderPath;
        } else {
            return null;
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jOptionPane1 = new javax.swing.JOptionPane();
        jPanel1 = new javax.swing.JPanel();
        searchPane = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        sortByPane3 = new javax.swing.JPanel();
        generateReportButton = new javax.swing.JButton();
        selectMonthButton = new javax.swing.JButton();
        billingContentArea = new javax.swing.JPanel();
        summaryPanel = new javax.swing.JPanel();
        totalBillsPanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel8 = new javax.swing.JLabel();
        totalBills = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        billsWithInsutancePanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel5 = new javax.swing.JLabel();
        totalBillsWithInsurance = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        billsWithoutInsurancePanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel7 = new javax.swing.JLabel();
        totalBillsWithoutInsurance = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        chartPanel = new javax.swing.JPanel();
        billsByInsuranceProviderPanel = new javax.swing.JPanel();
        payableAmountByInsuranceProviderPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1160, 2147483647));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1160, 50));
        jPanel1.setLayout(new java.awt.BorderLayout());

        searchPane.setBackground(new java.awt.Color(246, 251, 255));

        jPanel2.setBackground(new java.awt.Color(246, 251, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(180, 30));

        jLabel1.setBackground(new java.awt.Color(246, 251, 255));
        jLabel1.setText("Month:");
        jPanel2.add(jLabel1);

        jLabel2.setBackground(new java.awt.Color(246, 251, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("August 2026");
        jPanel2.add(jLabel2);

        searchPane.add(jPanel2);

        jPanel1.add(searchPane, java.awt.BorderLayout.WEST);

        sortByPane3.setBackground(new java.awt.Color(246, 251, 255));

        generateReportButton.setText("Generate Report");
        generateReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generateReportButton.setPreferredSize(new java.awt.Dimension(150, 40));
        generateReportButton.addActionListener(this::generateReportButtonActionPerformed);
        sortByPane3.add(generateReportButton);

        selectMonthButton.setText("Select Month");
        selectMonthButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectMonthButton.setPreferredSize(new java.awt.Dimension(150, 40));
        selectMonthButton.addActionListener(this::selectMonthButtonActionPerformed);
        sortByPane3.add(selectMonthButton);

        jPanel1.add(sortByPane3, java.awt.BorderLayout.LINE_END);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        billingContentArea.setBackground(new java.awt.Color(246, 251, 255));
        billingContentArea.setLayout(new javax.swing.BoxLayout(billingContentArea, javax.swing.BoxLayout.Y_AXIS));

        summaryPanel.setBackground(new java.awt.Color(246, 251, 255));
        summaryPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        summaryPanel.setMaximumSize(new java.awt.Dimension(1160, 80));
        summaryPanel.setPreferredSize(new java.awt.Dimension(1160, 100));

        totalBillsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        totalBillsPanel.setPreferredSize(new java.awt.Dimension(160, 60));
        totalBillsPanel.setRequestFocusEnabled(false);
        totalBillsPanel.setLayout(new javax.swing.BoxLayout(totalBillsPanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel8.setText("Total Bills");
        totalBillsPanel.add(jLabel8);

        totalBills.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalBills.setText("wi");
        totalBillsPanel.add(totalBills);

        summaryPanel.add(totalBillsPanel);
        summaryPanel.add(filler4);

        billsWithInsutancePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        billsWithInsutancePanel.setPreferredSize(new java.awt.Dimension(160, 60));
        billsWithInsutancePanel.setRequestFocusEnabled(false);
        billsWithInsutancePanel.setLayout(new javax.swing.BoxLayout(billsWithInsutancePanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel5.setText("Bills with Insurance");
        billsWithInsutancePanel.add(jLabel5);

        totalBillsWithInsurance.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalBillsWithInsurance.setText("wi");
        billsWithInsutancePanel.add(totalBillsWithInsurance);

        summaryPanel.add(billsWithInsutancePanel);
        summaryPanel.add(filler2);

        billsWithoutInsurancePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        billsWithoutInsurancePanel.setPreferredSize(new java.awt.Dimension(160, 60));
        billsWithoutInsurancePanel.setRequestFocusEnabled(false);
        billsWithoutInsurancePanel.setLayout(new javax.swing.BoxLayout(billsWithoutInsurancePanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel7.setText("Bills without Insurance");
        billsWithoutInsurancePanel.add(jLabel7);

        totalBillsWithoutInsurance.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalBillsWithoutInsurance.setText("wi");
        billsWithoutInsurancePanel.add(totalBillsWithoutInsurance);

        summaryPanel.add(billsWithoutInsurancePanel);
        summaryPanel.add(filler3);

        billingContentArea.add(summaryPanel);

        chartPanel.setBackground(new java.awt.Color(246, 251, 255));
        chartPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        chartPanel.setLayout(new java.awt.GridBagLayout());

        billsByInsuranceProviderPanel.setBackground(new java.awt.Color(246, 251, 255));
        billsByInsuranceProviderPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 5);
        chartPanel.add(billsByInsuranceProviderPanel, gridBagConstraints);

        payableAmountByInsuranceProviderPanel.setBackground(new java.awt.Color(246, 251, 255));
        payableAmountByInsuranceProviderPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        chartPanel.add(payableAmountByInsuranceProviderPanel, gridBagConstraints);

        billingContentArea.add(chartPanel);

        add(billingContentArea, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void selectMonthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectMonthButtonActionPerformed
        LocalDate[] range = showMonthPickerDialog();
        if (range == null) {
            return; // user cancelled
        }

        selectedStartDate = range[0];
        selectedEndDate = range[1];

        jLabel2.setText(selectedStartDate.format(MONTH_LABEL_FMT));
        loadSummaryPanel();
        loadInsuranceByProviderBarChart();
        loadPayableAmountByInsuranceProvider();
    }//GEN-LAST:event_selectMonthButtonActionPerformed

//    exportInsuranceUsageReport(String filePath, String generatedBy, String datetime,
//            LinkedHashMap<String, String> insuranceTotals, LinkedHashMap<String, Double> usageByProvider,
//            LinkedHashMap<String, Double> payableAmountByInsurance)
    private void generateReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateReportButtonActionPerformed
        String folderPath = selectFolderDir();
        if (folderPath != null) {
            String fileName = JOptionPane.showInputDialog(parentWindow, "Enter PDF Name: ", "Report PDF Name", JOptionPane.QUESTION_MESSAGE);
            String filePath = ReportPDFExporter.filePath(fileName, folderPath);
            String manager = this.usrCtrl.getActiveUser().getName();
            String date = DateTime.toDateTimeString(DateTime.currentDateTime());
            LinkedHashMap<String, String> insuranceTotals = this.usrCtrl.getReportController().getInsuranceTotals(selectedStartDate, selectedEndDate);
            LinkedHashMap<String, Double> usageByProvider = this.usrCtrl.getReportController().getInsuranceUsageByProvider(selectedStartDate, selectedEndDate);
            LinkedHashMap<String, Double> payableAmountByInsurance = this.usrCtrl.getReportController().getPayableAmountByInsurance(selectedStartDate, selectedEndDate);
            if (fileName != null && !fileName.equals("")) {
                try {
                    if (ReportPDFExporter.exportInsuranceUsageReport(filePath, manager, date, selectedStartDate, selectedEndDate, insuranceTotals, usageByProvider, payableAmountByInsurance)) {
                        JOptionPane.showMessageDialog(parentWindow, "Insurance Usage Report has been exported!", "Report Exporting", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parentWindow, "Fail to export Insurance Usage report :(", "Report Exporting", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    System.getLogger(InsurancePanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Report exporting is cancelled", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentWindow, "No directory selected.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_generateReportButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel billingContentArea;
    private javax.swing.JPanel billsByInsuranceProviderPanel;
    private javax.swing.JPanel billsWithInsutancePanel;
    private javax.swing.JPanel billsWithoutInsurancePanel;
    private javax.swing.JPanel chartPanel;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton generateReportButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel payableAmountByInsuranceProviderPanel;
    private javax.swing.JPanel searchPane;
    private javax.swing.JButton selectMonthButton;
    private javax.swing.JPanel sortByPane3;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JLabel totalBills;
    private javax.swing.JPanel totalBillsPanel;
    private javax.swing.JLabel totalBillsWithInsurance;
    private javax.swing.JLabel totalBillsWithoutInsurance;
    // End of variables declaration//GEN-END:variables
}
