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
public class ConsultationsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final java.time.format.DateTimeFormatter MONTH_LABEL_FMT = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy");
    private LocalDate selectedStartDate;
    private LocalDate selectedEndDate;

    public ConsultationsPanel(UserController usrCtrl) {
        initComponents();
        this.usrCtrl = usrCtrl;

        LocalDate now = LocalDate.now();
        selectedStartDate = now.withDayOfMonth(1);
        selectedEndDate = now.withDayOfMonth(now.lengthOfMonth());
        jLabel2.setText(selectedStartDate.format(MONTH_LABEL_FMT));

        loadSummaryPanel();
        loadConsultationsByWeek();
        loadConsultationsByStatus();
        loadConsultationsByDoctor();
    }
    
    private void loadSummaryPanel() {
        LinkedHashMap<String, String> totals = this.usrCtrl.getReportController().getConsultationTotals(selectedStartDate, selectedEndDate);

        totalConsultations.setText(String.format("%s", totals.get("totalConsultations")));
        completedConsultations.setText(String.format("%s", totals.get("completedConsultations")));
        scheduledConsultations.setText(String.format("%s", totals.get("scheduledConsultations")));
    }
    
    private void loadConsultationsByStatus() {
        LinkedHashMap<String, Double> statusData = this.usrCtrl.getReportController().getConsultationsByStatus(selectedStartDate, selectedEndDate);

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        for (Map.Entry<String, Double> entry : statusData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createRingChart("Consultation Status", dataset, true, true, false);

        styleChart(chart);
        ChartPanel chartPanel = new ChartPanel(chart);

        consultationsByStatusPanel.removeAll();
        consultationsByStatusPanel.setLayout(new BorderLayout());
        consultationsByStatusPanel.add(chartPanel, BorderLayout.CENTER);

        consultationsByStatusPanel.revalidate();
        consultationsByStatusPanel.repaint();
    }
    
    private void loadConsultationsByWeek() {
        LinkedHashMap<String, Double> consultationData = this.usrCtrl.getReportController().getConsultationsByWeek(selectedStartDate, selectedEndDate);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : consultationData.entrySet()) {
            dataset.addValue(entry.getValue(), "Consultations", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Consultations by Week",
                "Week",
                "Number of Consultations",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        consultationsByMonthPanel.removeAll();
        consultationsByMonthPanel.setLayout(new BorderLayout());
        consultationsByMonthPanel.add(chartPanel, BorderLayout.CENTER);
        consultationsByMonthPanel.revalidate();
        consultationsByMonthPanel.repaint();
    }

    private void loadConsultationsByDoctor() {
        LinkedHashMap<String, Double> consultationData = this.usrCtrl.getReportController().getConsultationsByDoctor(selectedStartDate, selectedEndDate);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : consultationData.entrySet()) {

            if (!entry.getKey().equals("No Doctor")) {
                dataset.addValue( entry.getValue(), "Consultations", entry.getKey());
            }
        }

        // Add No Insurance last
        if (consultationData.containsKey("No Doctor")) {
            dataset.addValue(
                    consultationData.get("No Doctor"),
                    "Consultations",
                    "No Doctor"
            );
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Consultations by Doctor",
                "Doctor",
                "Number of Consultatuons",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);

        consultationsByDoctorPanel.removeAll();
        consultationsByDoctorPanel.setLayout(new BorderLayout());
        consultationsByDoctorPanel.add(
                chartPanel,
                BorderLayout.CENTER
        );

        consultationsByDoctorPanel.revalidate();
        consultationsByDoctorPanel.repaint();
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
        totalConsultationsPanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel5 = new javax.swing.JLabel();
        totalConsultations = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        completedConsultationsPanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel7 = new javax.swing.JLabel();
        completedConsultations = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        scheduledConsultationsPanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(15);
        jLabel8 = new javax.swing.JLabel();
        scheduledConsultations = new javax.swing.JLabel();
        chartPanel = new javax.swing.JPanel();
        consultationsByStatusPanel = new javax.swing.JPanel();
        consultationsByMonthPanel = new javax.swing.JPanel();
        consultationsByDoctorPanel = new javax.swing.JPanel();

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

        totalConsultationsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        totalConsultationsPanel.setPreferredSize(new java.awt.Dimension(160, 60));
        totalConsultationsPanel.setRequestFocusEnabled(false);
        totalConsultationsPanel.setLayout(new javax.swing.BoxLayout(totalConsultationsPanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel5.setText("Total Consultations");
        totalConsultationsPanel.add(jLabel5);

        totalConsultations.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalConsultations.setText("bibi");
        totalConsultationsPanel.add(totalConsultations);

        summaryPanel.add(totalConsultationsPanel);
        summaryPanel.add(filler2);

        completedConsultationsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        completedConsultationsPanel.setMaximumSize(new java.awt.Dimension(180, 61));
        completedConsultationsPanel.setPreferredSize(new java.awt.Dimension(180, 60));
        completedConsultationsPanel.setRequestFocusEnabled(false);
        completedConsultationsPanel.setLayout(new javax.swing.BoxLayout(completedConsultationsPanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel7.setText("Completed Consultations");
        completedConsultationsPanel.add(jLabel7);

        completedConsultations.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        completedConsultations.setText("labu");
        completedConsultationsPanel.add(completedConsultations);

        summaryPanel.add(completedConsultationsPanel);
        summaryPanel.add(filler3);

        scheduledConsultationsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        scheduledConsultationsPanel.setMaximumSize(new java.awt.Dimension(180, 61));
        scheduledConsultationsPanel.setPreferredSize(new java.awt.Dimension(180, 60));
        scheduledConsultationsPanel.setRequestFocusEnabled(false);
        scheduledConsultationsPanel.setLayout(new javax.swing.BoxLayout(scheduledConsultationsPanel, javax.swing.BoxLayout.Y_AXIS));

        jLabel8.setText("Scheduled Consultations");
        scheduledConsultationsPanel.add(jLabel8);

        scheduledConsultations.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        scheduledConsultations.setText("wiiii");
        scheduledConsultationsPanel.add(scheduledConsultations);

        summaryPanel.add(scheduledConsultationsPanel);

        billingContentArea.add(summaryPanel);

        chartPanel.setBackground(new java.awt.Color(246, 251, 255));
        chartPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        chartPanel.setLayout(new java.awt.GridBagLayout());

        consultationsByStatusPanel.setBackground(new java.awt.Color(246, 251, 255));
        consultationsByStatusPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 5);
        chartPanel.add(consultationsByStatusPanel, gridBagConstraints);

        consultationsByMonthPanel.setBackground(new java.awt.Color(246, 251, 255));
        consultationsByMonthPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 5);
        chartPanel.add(consultationsByMonthPanel, gridBagConstraints);

        consultationsByDoctorPanel.setBackground(new java.awt.Color(246, 251, 255));
        consultationsByDoctorPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        chartPanel.add(consultationsByDoctorPanel, gridBagConstraints);

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
        loadConsultationsByWeek();
        loadConsultationsByStatus();
        loadConsultationsByDoctor();
    }//GEN-LAST:event_selectMonthButtonActionPerformed

    private void generateReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateReportButtonActionPerformed
        String folderPath = selectFolderDir();
        if (folderPath != null) {
            String fileName = JOptionPane.showInputDialog(parentWindow, "Enter PDF Name: ", "Report PDF Name", JOptionPane.QUESTION_MESSAGE);
            String filePath = ReportPDFExporter.filePath(fileName, folderPath);
            String manager = this.usrCtrl.getActiveUser().getName();
            String date = DateTime.toDateTimeString(DateTime.currentDateTime());
            LinkedHashMap<String, String> consultationTotals = this.usrCtrl.getReportController().getConsultationTotals(selectedStartDate, selectedEndDate);
            LinkedHashMap<String, Double> consultationsByStatus = this.usrCtrl.getReportController().getConsultationsByStatus(selectedStartDate, selectedEndDate);
            LinkedHashMap<String, Double> consultationsByDoctor = this.usrCtrl.getReportController().getConsultationsByDoctor(selectedStartDate, selectedEndDate);
            LinkedHashMap<String, Double> consultationsByWeek = this.usrCtrl.getReportController().getConsultationsByWeek(selectedStartDate, selectedEndDate);
            if (fileName != null && !fileName.equals("")) {
                try {
                    if (ReportPDFExporter.exportConsultationReport(filePath, manager, date, selectedStartDate, selectedEndDate, consultationTotals, consultationsByStatus, consultationsByDoctor, consultationsByWeek)) {
                        JOptionPane.showMessageDialog(parentWindow, "Consultation Report has been exported!", "Report Exporting", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parentWindow, "Fail to export Consultation report :(", "Report Exporting", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    System.getLogger(ConsultationsPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
    private javax.swing.JPanel chartPanel;
    private javax.swing.JLabel completedConsultations;
    private javax.swing.JPanel completedConsultationsPanel;
    private javax.swing.JPanel consultationsByDoctorPanel;
    private javax.swing.JPanel consultationsByMonthPanel;
    private javax.swing.JPanel consultationsByStatusPanel;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton generateReportButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel scheduledConsultations;
    private javax.swing.JPanel scheduledConsultationsPanel;
    private javax.swing.JPanel searchPane;
    private javax.swing.JButton selectMonthButton;
    private javax.swing.JPanel sortByPane3;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JLabel totalConsultations;
    private javax.swing.JPanel totalConsultationsPanel;
    // End of variables declaration//GEN-END:variables
}
