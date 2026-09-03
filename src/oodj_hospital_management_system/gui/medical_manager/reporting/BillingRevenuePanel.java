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
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author sheaw
 */
public class BillingRevenuePanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final java.time.format.DateTimeFormatter MONTH_LABEL_FMT = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy");
    private LocalDate selectedStartDate;
    private LocalDate selectedEndDate;

    public BillingRevenuePanel(UserController usrCtrl) {
        initComponents();
        this.usrCtrl = usrCtrl;

        LocalDate now = LocalDate.now();
        selectedStartDate = now.withDayOfMonth(1);
        selectedEndDate = now.withDayOfMonth(now.lengthOfMonth());
        jLabel2.setText(selectedStartDate.format(MONTH_LABEL_FMT));

        loadSummaryPanel();
        loadRevenueByWeekChart();
//        loadRevenueByMonthChart();
        loadBillStatusChart();
    }
    
    private void loadSummaryPanel() {
        LinkedHashMap<String, String> totals = usrCtrl.getReportController()
            .getBillingTotals(selectedStartDate, selectedEndDate);

        totalRevenue.setText(String.format("RM %s", totals.get("totalRevenue")));
        paidRevenue.setText(String.format("RM %s", totals.get("totalPaid")));
        unpaidRevenue.setText(String.format("RM %s", totals.get("totalUnpaid")));
    }

    private void loadRevenueByWeekChart() {
        LinkedHashMap<String, Double> revenueData = this.usrCtrl.getReportController()
                .getRevenueByWeek(selectedStartDate, selectedEndDate);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            dataset.addValue(entry.getValue(), "Revenue", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Revenue by Week",
                "Week",
                "Revenue (RM)",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);

        revenueByMonthChartPanel.removeAll();
        revenueByMonthChartPanel.setLayout(new BorderLayout());
        revenueByMonthChartPanel.add(chartPanel, BorderLayout.CENTER);

        revenueByMonthChartPanel.revalidate();
        revenueByMonthChartPanel.repaint();
    }
    
    private void loadRevenueByMonthChart() {
        LinkedHashMap<String, Double> revenueData = this.usrCtrl.getReportController().getRevenueByMonth();

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {

            dataset.addValue(
                    entry.getValue(),
                    "Revenue",
                    entry.getKey()
            );
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Revenue by Month",
                "Month",
                "Revenue (RM)",
                dataset
        );

        styleChart(chart);
        ChartPanel chartPanel = new ChartPanel(chart);

        revenueByMonthChartPanel.removeAll();
        revenueByMonthChartPanel.setLayout(new BorderLayout());
        revenueByMonthChartPanel.add(
                chartPanel,
                BorderLayout.CENTER
        );

        revenueByMonthChartPanel.revalidate();
        revenueByMonthChartPanel.repaint();
    }
    
    private void loadBillStatusChart() {
        LinkedHashMap<String, Double> statusData = this.usrCtrl.getReportController()
            .getBillStatusBreakdown(selectedStartDate, selectedEndDate);   // <-- pass the selected range

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        for (Map.Entry<String, Double> entry : statusData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createRingChart("Bill Status", dataset, true, true, false);

        styleChart(chart);
        ChartPanel chartPanel = new ChartPanel(chart);

        billStatusChartPanel.removeAll();
        billStatusChartPanel.setLayout(new BorderLayout());
        billStatusChartPanel.add(chartPanel, BorderLayout.CENTER);

        billStatusChartPanel.revalidate();
        billStatusChartPanel.repaint();
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

        int returnValue = fileChooser.showDialog(null, "Select Folder");
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
        totalRevenuePanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel5 = new javax.swing.JLabel();
        totalRevenue = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        paidRevenuePanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel7 = new javax.swing.JLabel();
        paidRevenue = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        unpaidRevenuePanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel9 = new javax.swing.JLabel();
        unpaidRevenue = new javax.swing.JLabel();
        chartPanel = new javax.swing.JPanel();
        revenueByMonthChartPanel = new javax.swing.JPanel();
        billStatusChartPanel = new javax.swing.JPanel();

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

        totalRevenuePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        totalRevenuePanel.setPreferredSize(new java.awt.Dimension(160, 60));
        totalRevenuePanel.setRequestFocusEnabled(false);
        totalRevenuePanel.setLayout(new javax.swing.BoxLayout(totalRevenuePanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel5.setText("Total Revenue");
        totalRevenuePanel.add(jLabel5);

        totalRevenue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalRevenue.setText("RM 1000.00");
        totalRevenuePanel.add(totalRevenue);

        summaryPanel.add(totalRevenuePanel);
        summaryPanel.add(filler2);

        paidRevenuePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        paidRevenuePanel.setPreferredSize(new java.awt.Dimension(160, 60));
        paidRevenuePanel.setRequestFocusEnabled(false);
        paidRevenuePanel.setLayout(new javax.swing.BoxLayout(paidRevenuePanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel7.setText("Paid Revenue");
        paidRevenuePanel.add(jLabel7);

        paidRevenue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        paidRevenue.setText("RM 600.00");
        paidRevenuePanel.add(paidRevenue);

        summaryPanel.add(paidRevenuePanel);
        summaryPanel.add(filler3);

        unpaidRevenuePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        unpaidRevenuePanel.setPreferredSize(new java.awt.Dimension(160, 60));
        unpaidRevenuePanel.setRequestFocusEnabled(false);
        unpaidRevenuePanel.setLayout(new javax.swing.BoxLayout(unpaidRevenuePanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel9.setText("Unpaid Revenue");
        unpaidRevenuePanel.add(jLabel9);

        unpaidRevenue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        unpaidRevenue.setText("RM 400.00");
        unpaidRevenuePanel.add(unpaidRevenue);

        summaryPanel.add(unpaidRevenuePanel);

        billingContentArea.add(summaryPanel);

        chartPanel.setBackground(new java.awt.Color(246, 251, 255));
        chartPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        chartPanel.setLayout(new java.awt.GridBagLayout());

        revenueByMonthChartPanel.setBackground(new java.awt.Color(246, 251, 255));
        revenueByMonthChartPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 5);
        chartPanel.add(revenueByMonthChartPanel, gridBagConstraints);

        billStatusChartPanel.setBackground(new java.awt.Color(246, 251, 255));
        billStatusChartPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        chartPanel.add(billStatusChartPanel, gridBagConstraints);

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
        loadRevenueByWeekChart();
        loadBillStatusChart();
    }//GEN-LAST:event_selectMonthButtonActionPerformed

    private void generateReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateReportButtonActionPerformed
        String folderPath = selectFolderDir();
        if (folderPath != null) {
            String fileName = JOptionPane.showInputDialog(parentWindow, "Enter PDF Name: ", "Report PDF Name", JOptionPane.QUESTION_MESSAGE);
            String filePath = ReportPDFExporter.filePath(fileName, folderPath);
            String manager = this.usrCtrl.getActiveUser().getName();
            String date = DateTime.toDateTimeString(DateTime.currentDateTime());
            LinkedHashMap<String, String> billingTotal = this.usrCtrl.getReportController().getBillingTotals();
            LinkedHashMap<String, Double> revenueByMonth = this.usrCtrl.getReportController().getRevenueByWeek(selectedStartDate, selectedEndDate);
            LinkedHashMap<String, Double> billStatusBreakdown = this.usrCtrl.getReportController().getBillStatusBreakdown(selectedStartDate, selectedEndDate);
            if (fileName != null && !fileName.equals("")) {
                try {
                    if (ReportPDFExporter.exportBillingRevenueReport(filePath, manager, date, selectedStartDate, selectedEndDate, billingTotal, revenueByMonth, billStatusBreakdown)) {
                        JOptionPane.showMessageDialog(parentWindow, "Billing and Revenue Report has been exported!", "Report Exporting", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parentWindow, "Fail to export Billing and Revenue report :(", "Report Exporting", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    System.getLogger(BillingRevenuePanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Report exporting is cancelled", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentWindow, "No directory selected.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_generateReportButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel billStatusChartPanel;
    private javax.swing.JPanel billingContentArea;
    private javax.swing.JPanel chartPanel;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton generateReportButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel paidRevenue;
    private javax.swing.JPanel paidRevenuePanel;
    private javax.swing.JPanel revenueByMonthChartPanel;
    private javax.swing.JPanel searchPane;
    private javax.swing.JButton selectMonthButton;
    private javax.swing.JLabel sortByLabel;
    private javax.swing.JLabel sortByLabel1;
    private javax.swing.JLabel sortByLabel2;
    private javax.swing.JPanel sortByPane;
    private javax.swing.JPanel sortByPane1;
    private javax.swing.JPanel sortByPane2;
    private javax.swing.JPanel sortByPane3;
    private javax.swing.JComboBox<String> sortField;
    private javax.swing.JComboBox<String> sortField1;
    private javax.swing.JComboBox<String> sortField2;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JLabel totalRevenue;
    private javax.swing.JPanel totalRevenuePanel;
    private javax.swing.JLabel unpaidRevenue;
    private javax.swing.JPanel unpaidRevenuePanel;
    // End of variables declaration//GEN-END:variables
}
