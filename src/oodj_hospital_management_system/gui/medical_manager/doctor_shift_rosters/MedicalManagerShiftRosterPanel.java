/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.medical_manager.doctor_shift_rosters;

import com.toedter.calendar.JDateChooser;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import oodj_hospital_management_system.gui.medical_manager.clinical_departments.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.utils.WrappingCellRenderer;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.DoctorShift.DoctorShiftStatus;
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.ReportPDFExporter;

/**
 *
 * @author sheaw
 */
public class MedicalManagerShiftRosterPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private CardLayout cl;
    public ShiftRosterPanel rosterGrid;
    public RequestPanel requestPanel;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    private LocalDate currentWeekStart;
    private List<Doctor> allDoctors = new ArrayList<>();
    private List<Doctor> currentDoctors = new ArrayList<>();
    private List<Doctor> assignedDoctors = new ArrayList<>();

    private static final DateTimeFormatter DAY_HEADER_FMT = DateTimeFormatter.ofPattern("d MMMM yyyy");
    String imageFileName = "oodj_hospital_management_system/assets/img/layout/doctor_shift.png";

    /**
     * Creates new form adminDashboardPanel
     */
    public MedicalManagerShiftRosterPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;

        initComponents();

        navigationPane.setPreferredSize(null);

        // Cap dashboardPanel's WIDTH only; height stays dynamic so vertical
        // scrolling keeps working as content grows/shrinks.
        javax.swing.JPanel cappedDashboard = new javax.swing.JPanel(new java.awt.BorderLayout(0, 10)) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.min(d.width, 1020), d.height);
            }
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        cappedDashboard.setBackground(dashboardPanel.getBackground());
        cappedDashboard.setBorder(dashboardPanel.getBorder());
        cappedDashboard.add(jPanel5, java.awt.BorderLayout.PAGE_START);
        cappedDashboard.add(shiftRosterContentArea, java.awt.BorderLayout.CENTER);

        dashboardPanel = cappedDashboard;
        jScrollPane2.setViewportView(dashboardPanel);

        this.allDoctors = loadAllDoctors();
        this.assignedDoctors = loadAssignedDoctors();
        
        this.rosterGrid = new ShiftRosterPanel(usrCtrl);
        this.requestPanel = new RequestPanel(usrCtrl);

        shiftRosterContentArea.add(rosterGrid, "shiftRoster");
        shiftRosterContentArea.add(requestPanel, "cancellationRequests");

        setNavButtonActive(rosterButton, true);
        setNavButtonActive(requestsButton, false);

        // Top Banner ~
        jPanel1.setOpaque(false);
        jPanel8.setOpaque(false);
        navigationPane.setOpaque(false);

        this.cl = (CardLayout) shiftRosterContentArea.getLayout();
        cl.show(shiftRosterContentArea, "shiftRoster");
        resetScrollToTop();
    }
    
    private void setNavButtonActive(javax.swing.JButton button, boolean active) {
        oodj_hospital_management_system.gui.utils.RoundedButton rb = (oodj_hospital_management_system.gui.utils.RoundedButton) button;

        java.awt.Color lightGrey = new java.awt.Color(230, 230, 230);
        java.awt.Color hoverGrey = new java.awt.Color(200, 200, 200);
        java.awt.Color pressedGrey = new java.awt.Color(180, 180, 180);

        java.awt.Color blue = new java.awt.Color(41, 121, 255);
        java.awt.Color hoverBlue = new java.awt.Color(25, 100, 230);
        java.awt.Color pressedBlue = new java.awt.Color(15, 85, 210);

        if (active) {
            rb.setButtonColors(blue, hoverBlue, pressedBlue);
            rb.setForeground(java.awt.Color.WHITE);
            rb.setFont(rb.getFont().deriveFont(java.awt.Font.BOLD));
        } else {
            rb.setButtonColors(lightGrey, hoverGrey, pressedGrey);
            rb.setForeground(new java.awt.Color(60, 60, 60));
            rb.setFont(rb.getFont().deriveFont(java.awt.Font.PLAIN));
        }
    }

    private void resetScrollToTop() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                jScrollPane2.getViewport().setViewPosition(new java.awt.Point(0, 0));
            });
        });
    }

    private List<Doctor> loadAllDoctors() {
        return UserManagement.getAllDoctors().stream()
            .filter(usr -> usr instanceof Doctor)
            .map(usr -> (Doctor) usr)
            .toList();
    }
    
    private List<Doctor> loadAssignedDoctors() {
        User activeUser = usrCtrl.getActiveUser();
        if (!(activeUser instanceof MedicalManager)) {
            return new ArrayList<>();
        }
        MedicalManager activeManager = (MedicalManager) activeUser;

        List<Doctor> assigned = new ArrayList<>();
        for (DoctorShift shift : DoctorManagement.getAllDoctorShifts()) {
            MedicalManager shiftManager = shift.getMedicalManager();
            Doctor doctor = shift.getDoctor();
            if (shiftManager == null || doctor == null) continue;
            if (!shiftManager.getId().equals(activeManager.getId())) continue;

            boolean alreadyAdded = assigned.stream().anyMatch(d -> d.getId().equals(doctor.getId()));
            if (!alreadyAdded) {
                assigned.add(doctor);
            }
        }
        return assigned;
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

        jScrollPane2 = new javax.swing.JScrollPane();
        dashboardPanel = new javax.swing.JPanel();
        jPanel5 = new oodj_hospital_management_system.gui.utils.BackgroundPanel(this.imageFileName);
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 0), new java.awt.Dimension(40, 32767));
        generateReportButton = new javax.swing.JButton();
        navigationPane = new javax.swing.JPanel();
        shiftRosterContentArea = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1020, 0));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        dashboardPanel.setBackground(new java.awt.Color(246, 251, 255));
        dashboardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 40, 0));
        dashboardPanel.setPreferredSize(new java.awt.Dimension(1020, 1200));
        dashboardPanel.setRequestFocusEnabled(false);
        dashboardPanel.setLayout(new java.awt.BorderLayout(0, 10));

        jPanel5.setBackground(new java.awt.Color(246, 251, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 60, 20, 60));
        jPanel5.setPreferredSize(new java.awt.Dimension(1020, 200));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jPanel8.setBackground(new java.awt.Color(246, 251, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(1020, 140));

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(700, 77));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setBackground(new java.awt.Color(246, 251, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Shift Roster");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Generates shift rosters for assigned doctors to support efficient scheduling.");
        jLabel1.setMaximumSize(new java.awt.Dimension(946, 45));
        jLabel1.setPreferredSize(new java.awt.Dimension(946, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel1, gridBagConstraints);

        jPanel8.add(jPanel1);
        jPanel8.add(filler1);
        jPanel8.add(filler2);
        jPanel8.add(filler3);

        generateReportButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        generateReportButton.setText("Generate Report");
        generateReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generateReportButton.setPreferredSize(new java.awt.Dimension(150, 40));
        generateReportButton.addActionListener(this::generateReportButtonActionPerformed);
        jPanel8.add(generateReportButton);

        jPanel5.add(jPanel8);

        navigationPane.setBackground(new java.awt.Color(246, 251, 255));
        navigationPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 50, 0, 50));
        navigationPane.setPreferredSize(new java.awt.Dimension(16897, 200));
        navigationPane.setLayout(new java.awt.GridBagLayout());

        rosterButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        rosterButton.setText("Doctor Shift Roster");
        rosterButton.setMaximumSize(new java.awt.Dimension(250, 50));
        rosterButton.setMinimumSize(new java.awt.Dimension(250, 50));
        rosterButton.setPreferredSize(new java.awt.Dimension(180, 45));
        rosterButton.setSelected(true);
        rosterButton.addActionListener(this::rosterButtonActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        navigationPane.add(rosterButton, gridBagConstraints);

        requestsButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        requestsButton.setText("Cancellation Requests");
        requestsButton.setMaximumSize(new java.awt.Dimension(250, 50));
        requestsButton.setMinimumSize(new java.awt.Dimension(250, 50));
        requestsButton.setPreferredSize(new java.awt.Dimension(200, 45));
        requestsButton.addActionListener(this::requestsButtonActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        navigationPane.add(requestsButton, gridBagConstraints);

        jPanel5.add(navigationPane);

        dashboardPanel.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        shiftRosterContentArea.setBackground(new java.awt.Color(246, 251, 255));
        shiftRosterContentArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        shiftRosterContentArea.setLayout(new java.awt.CardLayout());
        dashboardPanel.add(shiftRosterContentArea, java.awt.BorderLayout.CENTER);

        jScrollPane2.setViewportView(dashboardPanel);

        add(jScrollPane2);
    }// </editor-fold>//GEN-END:initComponents

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
    
    private void generateReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateReportButtonActionPerformed
        String folderPath = selectFolderDir();
        if (folderPath == null) {
            JOptionPane.showMessageDialog(parentWindow, "No directory selected.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Enter PDF name
        String fileName = JOptionPane.showInputDialog(parentWindow, "Enter PDF Name: ", "Report PDF Name", JOptionPane.QUESTION_MESSAGE);

        if (fileName == null || fileName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow, "Report exporting is cancelled", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Select date range
        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();
        Date currentDate = new Date();

        startDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        startDateChooser.setMaxSelectableDate(currentDate);
        endDateChooser.setMaxSelectableDate(currentDate);

        JPanel datePanel = new JPanel();
        datePanel.add(new JLabel("From:"));
        datePanel.add(startDateChooser);
        datePanel.add(new JLabel("To:"));
        datePanel.add(endDateChooser);

        int dateChoice = JOptionPane.showConfirmDialog(parentWindow, datePanel, "Select Report Date Range", JOptionPane.OK_CANCEL_OPTION);

        if (dateChoice != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(parentWindow, "Report exporting is cancelled", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Date startDateRaw = startDateChooser.getDate();
        Date endDateRaw = endDateChooser.getDate();

        if (startDateRaw == null || endDateRaw == null) {
            JOptionPane.showMessageDialog(parentWindow, "Please select both start and end dates.", "Missing Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        LocalDate selectedStartDate = LocalDate.parse(sdf.format(startDateRaw));
        LocalDate selectedEndDate = LocalDate.parse(sdf.format(endDateRaw));

        // Validate date range
        if (selectedEndDate.isBefore(selectedStartDate)) {
            JOptionPane.showMessageDialog(parentWindow, "End date cannot be before start date.", "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Select doctor scope
        String[] doctorOptions = {
            "All Doctors",
            "Assigned Doctors"
        };

        int doctorChoice = JOptionPane.showOptionDialog(parentWindow, "Select doctors to include in the report:", "Doctor Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, doctorOptions, doctorOptions[0]);

        if (doctorChoice == JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(parentWindow, "Report exporting is cancelled", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Determine doctors for the report
        List<Doctor> doctorsForReport;

        String doctorScope = "";
        if (doctorChoice == 0) {
            // All Doctors
            doctorsForReport = allDoctors;
            doctorScope = "All Doctors";
        } else {
            doctorsForReport = assignedDoctors;
            doctorScope = "Assigned Doctors";
        }

        // Generate report data
        String filePath = ReportPDFExporter.filePath(fileName, folderPath);

        String manager = this.usrCtrl.getActiveUser().getName();
        String date = DateTime.toDateTimeString(DateTime.currentDateTime());

        LinkedHashMap<String, String> doctorShiftTotals = this.usrCtrl.getReportController().getDoctorShiftTotals(doctorsForReport, selectedStartDate, selectedEndDate);

        LinkedHashMap<String, Double> shiftsByStatus = this.usrCtrl.getReportController().getShiftsByStatus(doctorsForReport, selectedStartDate, selectedEndDate);

        LinkedHashMap<String, Double> scheduledShiftHours = this.usrCtrl.getReportController().getScheduledShiftHoursByDoctor(doctorsForReport, selectedStartDate, selectedEndDate);

        try {
            if (ReportPDFExporter.exportDoctorShiftReport(filePath, manager, date, selectedStartDate, selectedEndDate, doctorScope, doctorShiftTotals, shiftsByStatus, scheduledShiftHours)) {
                JOptionPane.showMessageDialog(parentWindow, "Doctor Shift Report has been exported!", "Report Exporting", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Fail to export Doctor Shift report :(", "Report Exporting", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            System.getLogger(MedicalManagerShiftRosterPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_generateReportButtonActionPerformed

    private void rosterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rosterButtonActionPerformed
        setNavButtonActive(rosterButton, true);
        setNavButtonActive(requestsButton, false);
        cl.show(shiftRosterContentArea, "shiftRoster");
    }//GEN-LAST:event_rosterButtonActionPerformed

    private void requestsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestsButtonActionPerformed
        setNavButtonActive(rosterButton, false);
        setNavButtonActive(requestsButton, true);
        cl.show(shiftRosterContentArea, "cancellationRequests");
    }//GEN-LAST:event_requestsButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton generateReportButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel navigationPane;
    private final javax.swing.JButton requestsButton = new oodj_hospital_management_system.gui.utils.RoundedButton("Doctor", 15);
    private final javax.swing.JButton rosterButton = new oodj_hospital_management_system.gui.utils.RoundedButton("Patient", 15);
    private javax.swing.JPanel shiftRosterContentArea;
    // End of variables declaration//GEN-END:variables
}
