/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.medical_manager.doctor_shift_rosters;

import com.toedter.calendar.JDateChooser;
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
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.ReportPDFExporter;

/**
 *
 * @author sheaw
 */
public class MedicalManagerShiftRosterPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    private LocalDate currentWeekStart;
    private List<Doctor> allDoctors = new ArrayList<>();
    private List<Doctor> currentDoctors = new ArrayList<>();
    private List<Doctor> assignedDoctors = new ArrayList<>();

    private static final DateTimeFormatter DAY_HEADER_FMT = DateTimeFormatter.ofPattern("d MMMM yyyy");

    private ShiftRosterGridPanel rosterGrid;

    /**
     * Creates new form adminDashboardPanel
     */
    public MedicalManagerShiftRosterPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        initComponents();

        rosterGrid = new ShiftRosterGridPanel();
        rosterGrid.setShiftClickListener(this::handleShiftCardClick);
        rosterGridContainer.setLayout(new java.awt.BorderLayout());
        rosterGridContainer.add(rosterGrid, java.awt.BorderLayout.NORTH);
        rosterGridContainer.setPreferredSize(null); // let it size to rosterGrid's content instead of the fixed 1160x500 from Design view

        allDoctors = loadAllDoctors();
        refreshRoster();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                allDoctors = loadAllDoctors();
                refreshRoster();
            }
        });
    }
    
    private List<Doctor> loadAllDoctors() {
        return UserManagement.getAllDoctors().stream()
            .filter(usr -> usr instanceof Doctor)
            .map(usr -> (Doctor) usr)
            .toList();
    }
    
    private void refreshRoster() {
        updateWeekLabels();

        List<DoctorShift> allShifts =
            usrCtrl.getDoctorController().getAllDoctorShifts();

        MedicalManager currentManager =
            (MedicalManager) usrCtrl.getActiveUser();

        // Get doctors assigned to the current Medical Manager
        assignedDoctors = allDoctors.stream()
            .filter(doctor -> doctor.getMedicalManager().equals(currentManager))
            .toList();

        currentDoctors = getFilteredSortedDoctors(allShifts);

        rosterGrid.updateData(currentDoctors, allShifts, currentWeekStart);

        updateResultLabel(currentDoctors.size());
    }

    /** Search (Doctor ID / Name) -> Filter (All / Assigned this week) -> Sort (ID / Name). */
    private List<Doctor> getFilteredSortedDoctors(List<DoctorShift> allShifts) {
        String keyword = searchField.getText().trim().toLowerCase();
        String filterSelected = (String) sortField1.getSelectedItem();
        String sortSelected = (String) sortField.getSelectedItem();
        LocalDate weekEnd = currentWeekStart.plusDays(6);

        List<Doctor> result = new ArrayList<>();
        for (Doctor d : allDoctors) {
            if (!keyword.isEmpty()) {
                boolean matchesDoctor = d.getId().toLowerCase().contains(keyword)
                        || d.getName().toLowerCase().contains(keyword);
                boolean matchesShift = allShifts.stream()
                        .anyMatch(s -> s.getDoctor().equals(d)
                                && s.getShiftId().toLowerCase().contains(keyword));
                if (!matchesDoctor && !matchesShift) continue;
            }
            if ("Assigned Doctors".equals(filterSelected)) {
                boolean hasShiftThisWeek = allShifts.stream()
                    .filter(s -> s.getDoctor().equals(d))
                    .anyMatch(s -> {
                        LocalDate shiftDate = DateTime.toDateTimeObject(s.getStartDateTime()).toLocalDate();
                        return !shiftDate.isBefore(currentWeekStart) && !shiftDate.isAfter(weekEnd);
                    });
                if (!hasShiftThisWeek) continue;
            }
            result.add(d);
        }

        if ("ID".equals(sortSelected)) {
            result.sort(Comparator.comparing(Doctor::getId));
        } else {
            result.sort(Comparator.comparing(Doctor::getName));
        }

        return result;
    }

    private void updateResultLabel(int count) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showResultsForPanel.setVisible(false);
        } else {
            showResultsForPanel.setVisible(true);
            showResultsForLabel.setText("Showing results for \"" + keyword + "\" (" + count + " found)");
        }
    }
    
    private void updateWeekLabels() {
        mondayLabel.setText(currentWeekStart.format(DAY_HEADER_FMT));
        sundayLabel.setText(currentWeekStart.plusDays(6).format(DAY_HEADER_FMT));
    }
    
    private void handleShiftCardClick(DoctorShift shift) {
        MedicalManager currentManager = (MedicalManager) usrCtrl.getActiveUser();
        MedicalManager assignedManager = shift.getDoctor().getMedicalManager();

        boolean isOwnDoctor = assignedManager != null
                && assignedManager.getId() != null
                && !assignedManager.getId().isEmpty()
                && assignedManager.getId().equals(currentManager.getId());

        if (!isOwnDoctor) {
            JOptionPane.showMessageDialog(parentWindow,
                String.format("Dr. %s is not assigned to you, so you can't manage this shift.", shift.getDoctor().getName()),
                "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (shift.getStatus() == DoctorShiftStatus.CANCELLED || shift.getStatus() == DoctorShiftStatus.COMPLETED) {
            JOptionPane.showMessageDialog(parentWindow,
                String.format("Shift '%s' is already %s and cannot be modified.", shift.getShiftId(), shift.getStatus().name().toLowerCase()),
                "Shift Locked", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        java.time.LocalDateTime shiftStart = DateTime.toDateTimeObject(shift.getStartDateTime());
        if (shiftStart.isBefore(java.time.LocalDateTime.now())) {
            JOptionPane.showMessageDialog(parentWindow,
                String.format("Shift '%s' already started at %s and is overdue, so it can no longer be cancelled.",
                    shift.getShiftId(), shiftStart.format(java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a"))),
                "Shift Overdue", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            parentWindow,
            String.format("Cancel shift '%s' for Dr. %s?\nThis action cannot be undone.", shift.getShiftId(), shift.getDoctor().getName()),
            "Confirm Cancel Shift",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            LinkedHashMap<String, String> msg = usrCtrl.getDoctorController().cancelShift(shift);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, msg.get("msg"));
                refreshRoster();
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Failed to cancel shift:\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
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

        jScrollPane2 = new javax.swing.JScrollPane();
        dashboardPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        createShiftButton = new javax.swing.JButton();
        generateReportButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        usrMngContentArea = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        searchPane = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        resetSearchButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        sortByPane = new javax.swing.JPanel();
        sortByLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JComboBox<>();
        sortByLabel1 = new javax.swing.JLabel();
        sortField1 = new javax.swing.JComboBox<>();
        wrapperPane = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        previousWeekButton = new javax.swing.JToggleButton();
        mondayLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        sundayLabel = new javax.swing.JLabel();
        nextWeekButton = new javax.swing.JToggleButton();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        rosterGridContainer = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1020, 0));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        dashboardPanel.setBackground(new java.awt.Color(246, 251, 255));
        dashboardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 60, 40, 60));
        dashboardPanel.setPreferredSize(new java.awt.Dimension(1020, 1200));
        dashboardPanel.setRequestFocusEnabled(false);
        dashboardPanel.setLayout(new java.awt.BorderLayout(0, 10));

        jPanel5.setBackground(new java.awt.Color(246, 251, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(1020, 100));
        jPanel5.setLayout(new java.awt.BorderLayout(0, 20));

        jPanel8.setBackground(new java.awt.Color(246, 251, 255));

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(700, 77));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setBackground(new java.awt.Color(246, 251, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
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

        createShiftButton.setText("Create Shift");
        createShiftButton.setMaximumSize(new java.awt.Dimension(115, 23));
        createShiftButton.setPreferredSize(new java.awt.Dimension(150, 40));
        createShiftButton.addActionListener(this::createShiftButtonActionPerformed);
        jPanel8.add(createShiftButton);

        generateReportButton.setText("Generate Report");
        generateReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        generateReportButton.setPreferredSize(new java.awt.Dimension(150, 40));
        generateReportButton.addActionListener(this::generateReportButtonActionPerformed);
        jPanel8.add(generateReportButton);

        jPanel5.add(jPanel8, java.awt.BorderLayout.CENTER);

        jSeparator1.setBackground(new java.awt.Color(210, 212, 220));
        jSeparator1.setForeground(new java.awt.Color(210, 212, 220));
        jPanel5.add(jSeparator1, java.awt.BorderLayout.PAGE_END);

        dashboardPanel.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        usrMngContentArea.setBackground(new java.awt.Color(246, 251, 255));
        usrMngContentArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        usrMngContentArea.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(246, 251, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(246, 251, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1160, 50));
        jPanel2.setLayout(new java.awt.BorderLayout());

        searchPane.setBackground(new java.awt.Color(246, 251, 255));

        searchField.setMinimumSize(new java.awt.Dimension(250, 22));
        searchField.setPreferredSize(new java.awt.Dimension(250, 40));
        searchField.addActionListener(this::searchFieldActionPerformed);
        searchPane.add(searchField);

        searchButton.setText("Search");
        searchButton.addActionListener(this::searchButtonActionPerformed);
        searchPane.add(searchButton);

        resetSearchButton.setText("Reset");
        resetSearchButton.addActionListener(this::resetSearchButtonActionPerformed);
        searchPane.add(resetSearchButton);

        jPanel2.add(searchPane, java.awt.BorderLayout.WEST);
        jPanel2.add(filler1, java.awt.BorderLayout.LINE_END);

        sortByPane.setBackground(new java.awt.Color(246, 251, 255));

        sortByLabel.setText("Sort By");
        sortByPane.add(sortByLabel);

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID", "Name" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        sortField1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Doctors", "Assigned Doctors" }));
        sortField1.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField1.setRequestFocusEnabled(false);
        sortField1.addActionListener(this::sortField1ActionPerformed);
        sortByPane.add(sortField1);

        jPanel2.add(sortByPane, java.awt.BorderLayout.LINE_END);

        jPanel7.add(jPanel2, java.awt.BorderLayout.NORTH);

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setBackground(new java.awt.Color(246, 251, 255));

        previousWeekButton.setText("<");
        previousWeekButton.addActionListener(this::previousWeekButtonActionPerformed);
        jPanel6.add(previousWeekButton);

        mondayLabel.setText("24 August 2026");
        jPanel6.add(mondayLabel);

        jLabel4.setText("-");
        jPanel6.add(jLabel4);

        sundayLabel.setText("30 August 2026");
        jPanel6.add(sundayLabel);

        nextWeekButton.setText(">");
        nextWeekButton.addActionListener(this::nextWeekButtonActionPerformed);
        jPanel6.add(nextWeekButton);

        wrapperPane.add(jPanel6);

        showResultsForPanel.setBackground(new java.awt.Color(246, 251, 255));
        showResultsForPanel.setAlignmentY(0.0F);
        showResultsForPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        showResultsForPanel.add(showResultsForLabel);

        wrapperPane.add(showResultsForPanel);

        jPanel3.setBackground(new java.awt.Color(246, 251, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 40));
        jPanel3.setAlignmentY(0.0F);
        jPanel3.setLayout(new java.awt.BorderLayout());

        rosterGridContainer.setBackground(new java.awt.Color(246, 251, 255));
        rosterGridContainer.setPreferredSize(new java.awt.Dimension(1160, 500));
        jPanel3.add(rosterGridContainer, java.awt.BorderLayout.NORTH);

        wrapperPane.add(jPanel3);

        jPanel7.add(wrapperPane, java.awt.BorderLayout.CENTER);

        usrMngContentArea.add(jPanel7, java.awt.BorderLayout.CENTER);

        dashboardPanel.add(usrMngContentArea, java.awt.BorderLayout.CENTER);

        jScrollPane2.setViewportView(dashboardPanel);

        add(jScrollPane2);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        refreshRoster();
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        refreshRoster();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        searchField.setText("");
        sortField.setSelectedIndex(0);
        refreshRoster();
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        refreshRoster();
    }//GEN-LAST:event_sortFieldActionPerformed

    private void createShiftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createShiftButtonActionPerformed
        if (assignedDoctors.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow, "No doctors available to assign a shift to.", "Cannot Create Shift", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] doctorItems = assignedDoctors.stream()
            .map(d -> d.getId() + " - " + d.getName())
            .toArray(String[]::new);
        javax.swing.JComboBox<String> doctorCombo = new javax.swing.JComboBox<>(doctorItems);

        com.toedter.calendar.JDateChooser dateChooser = new com.toedter.calendar.JDateChooser();
        dateChooser.setDate(new java.util.Date());

        javax.swing.JSpinner startTimeSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerDateModel());
        startTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(startTimeSpinner, "HH:mm"));

        javax.swing.JSpinner endTimeSpinner = new javax.swing.JSpinner(new javax.swing.SpinnerDateModel());
        endTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.add(new javax.swing.JLabel("Doctor:"));
        panel.add(doctorCombo);
        panel.add(new javax.swing.JLabel("Shift Date:"));
        panel.add(dateChooser);
        panel.add(new javax.swing.JLabel("Start Time:"));
        panel.add(startTimeSpinner);
        panel.add(new javax.swing.JLabel("End Time:"));
        panel.add(endTimeSpinner);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Create Shift", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            java.util.Date shiftDate = dateChooser.getDate();
            if (shiftDate == null) {
                JOptionPane.showMessageDialog(parentWindow, "Please select a shift date", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            LocalDate date = shiftDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalTime startTime = ((java.util.Date) startTimeSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
            java.time.LocalTime endTime = ((java.util.Date) endTimeSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

            java.time.LocalDateTime startDateTime = java.time.LocalDateTime.of(date, startTime);
            java.time.LocalDateTime endDateTime = java.time.LocalDateTime.of(date, endTime);

            if (!endDateTime.isAfter(startDateTime)) {
                JOptionPane.showMessageDialog(parentWindow, "End time must be after start time", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            Doctor selectedDoctor = assignedDoctors.get(doctorCombo.getSelectedIndex());
            String startStr = startDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endStr = endDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            LinkedHashMap<String, String> msg = usrCtrl.getDoctorController().createShift(selectedDoctor, startStr, endStr);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, msg.get("msg"));
                refreshRoster();
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Failed to create shift:\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_createShiftButtonActionPerformed

    private void previousWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousWeekButtonActionPerformed
        previousWeekButton.setSelected(false); // JToggleButton — don't let it stay pressed
        currentWeekStart = currentWeekStart.minusWeeks(1);
        refreshRoster();
    }//GEN-LAST:event_previousWeekButtonActionPerformed

    private void nextWeekButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextWeekButtonActionPerformed
        nextWeekButton.setSelected(false); // JToggleButton — don't let it stay pressed
        currentWeekStart = currentWeekStart.plusWeeks(1);
        refreshRoster();
    }//GEN-LAST:event_nextWeekButtonActionPerformed

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

    private void sortField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortField1ActionPerformed
        refreshRoster();
    }//GEN-LAST:event_sortField1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createShiftButton;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton generateReportButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel mondayLabel;
    private javax.swing.JToggleButton nextWeekButton;
    private javax.swing.JToggleButton previousWeekButton;
    private javax.swing.JButton resetSearchButton;
    private javax.swing.JPanel rosterGridContainer;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPane;
    private javax.swing.JLabel showResultsForLabel;
    private javax.swing.JPanel showResultsForPanel;
    private javax.swing.JLabel sortByLabel;
    private javax.swing.JLabel sortByLabel1;
    private javax.swing.JPanel sortByPane;
    private javax.swing.JComboBox<String> sortField;
    private javax.swing.JComboBox<String> sortField1;
    private javax.swing.JLabel sundayLabel;
    private javax.swing.JPanel usrMngContentArea;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
