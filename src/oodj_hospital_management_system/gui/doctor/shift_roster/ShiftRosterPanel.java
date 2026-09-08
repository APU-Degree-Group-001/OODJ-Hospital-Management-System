/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.doctor.shift_roster;

import oodj_hospital_management_system.gui.admin.user_management.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.ButtonGroup;
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
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class ShiftRosterPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<DoctorShift> currentDoctorShifts;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * Creates new form Admin StaffManagement
     */
    public ShiftRosterPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        loadDoctorShifts();
        customStyleHeader();
        shiftTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, shiftTable.getRowHeight() * shiftTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        shiftTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        shiftTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
    }
    
    public void loadDoctorShifts() {
        List<DoctorShift> myShifts = getBaseShifts();
        currentDoctorShifts = myShifts;
        populateShiftTable(myShifts);
    }
    
    private void populateShiftTable(List<DoctorShift> shifts) {
        DefaultTableModel model = (DefaultTableModel) shiftTable.getModel();
        model.setRowCount(0);

        for (DoctorShift shift : shifts) {
            LocalDateTime startDt = DateTime.toDateTimeObject(shift.getStartDateTime());
            LocalDateTime endDt = DateTime.toDateTimeObject(shift.getEndDateTime());

            String date = DateTime.toDateStringWords(DateTime.getDate(startDt));
            String scheduledStart = DateTime.toTimeString(DateTime.getTime(startDt));
            String scheduledEnd = DateTime.toTimeString(DateTime.getTime(endDt));

            String actualStart = "-";
            if (shift.getActualStartDateTime() != null && !shift.getActualStartDateTime().isEmpty()) {
                actualStart = DateTime.toTimeString(DateTime.getTime(DateTime.toDateTimeObject(shift.getActualStartDateTime())));
            }

            String actualEnd = "-";
            if (shift.getActualEndDateTime() != null && !shift.getActualEndDateTime().isEmpty()) {
                actualEnd = DateTime.toTimeString(DateTime.getTime(DateTime.toDateTimeObject(shift.getActualEndDateTime())));
            }

            String status = shift.getStatus() != null ? shift.getStatus().toString() : "-";

            model.addRow(new String[]{shift.getShiftId(), date, scheduledStart, scheduledEnd, actualStart, actualEnd, status});
        }
    }
    
    private List<DoctorShift> getBaseShifts() {
        Doctor activeDoctor = (usrCtrl.getActiveUser() instanceof Doctor)
            ? (Doctor) usrCtrl.getActiveUser() : null;

        List<DoctorShift> myShifts = new ArrayList<>();
        if (activeDoctor != null) {
            for (DoctorShift shift : DoctorManagement.getAllDoctorShifts()) {
                if (shift.getDoctor() != null && shift.getDoctor().getId().equals(activeDoctor.getId())) {
                    myShifts.add(shift);
                }
            }
        }
        return myShifts;
    }
    
    private DoctorShift getSelectedShift() {
        int viewRow = shiftTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a shift first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = shiftTable.convertRowIndexToModel(viewRow);
        String shiftId = (String) shiftTable.getModel().getValueAt(modelRow, 0);
        for (DoctorShift s : currentDoctorShifts) {
            if (s.getShiftId().equals(shiftId)) return s;
        }
        return null;
    }
    
    private DoctorShift.DoctorShiftStatus selectStatus() {
        javax.swing.JComboBox<DoctorShift.DoctorShiftStatus> statusCombo =
            new javax.swing.JComboBox<>(DoctorShift.DoctorShiftStatus.values());

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Status", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return null;
        return (DoctorShift.DoctorShiftStatus) statusCombo.getSelectedItem();
    }

    private void filterByStatus(DoctorShift.DoctorShiftStatus status) {
        List<DoctorShift> filtered = new ArrayList<>();
        for (DoctorShift shift : currentDoctorShifts) {
            if (status.equals(shift.getStatus())) {
                filtered.add(shift);
            }
        }
        currentDoctorShifts = filtered;
        populateShiftTable(filtered);
        updateResultLabel("Showing shifts with status \"" + status + "\"", filtered.size());
    }
    
    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }
    
    private LinkedHashMap<String, String> selectDateRange() {
        LinkedHashMap<String, String> dateRange = new LinkedHashMap<>();
        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();
        Date currentDate = new Date();
        startDateChooser.setMaxSelectableDate(currentDate);
        endDateChooser.setMaxSelectableDate(currentDate);

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.add(new javax.swing.JLabel("From:"));
        panel.add(startDateChooser);
        panel.add(new javax.swing.JLabel("To:"));
        panel.add(endDateChooser);

        int result = javax.swing.JOptionPane.showConfirmDialog(parentWindow, panel, "Select Date Range Joined", javax.swing.JOptionPane.OK_CANCEL_OPTION);

        if (result == javax.swing.JOptionPane.OK_OPTION) {
            Date startDateRaw = startDateChooser.getDate();
            Date endDateRaw = endDateChooser.getDate();

            if (startDateRaw == null && endDateRaw == null) {
                javax.swing.JOptionPane.showMessageDialog(parentWindow,
                    "Please select at least one date", "Missing Date",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return dateRange;
            }

            if (startDateRaw != null && endDateRaw != null && endDateRaw.compareTo(startDateRaw) < 0) {
                javax.swing.JOptionPane.showMessageDialog(parentWindow,
                    "Invalid date range", "WARNING", javax.swing.JOptionPane.ERROR_MESSAGE);
                return dateRange;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateRange.put("startDate", startDateRaw != null ? sdf.format(startDateRaw) : null);
            dateRange.put("endDate", endDateRaw != null ? sdf.format(endDateRaw) : null);
        }
        return dateRange;
    }
    
    private void filterByScheduledDateRange(String start, String end) {
        List<DoctorShift> filtered = new ArrayList<>();
        for (DoctorShift shift : currentDoctorShifts) {
            LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(shift.getStartDateTime()));
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(shift);
        }
        currentDoctorShifts = filtered;
        populateShiftTable(filtered);

        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing shifts scheduled", start, end, display);
        updateResultLabel(label, filtered.size());
    }

    private void filterByActualDateRange(String start, String end) {
        List<DoctorShift> filtered = new ArrayList<>();
        for (DoctorShift shift : currentDoctorShifts) {
            if (shift.getActualStartDateTime() == null || shift.getActualStartDateTime().isEmpty()) continue;
            LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(shift.getActualStartDateTime()));
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(shift);
        }
        currentDoctorShifts = filtered;
        populateShiftTable(filtered);

        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing shifts actually started", start, end, display);
        updateResultLabel(label, filtered.size());
    }

    private String buildRangeLabel(String prefix, String start, String end, SimpleDateFormat display) {
        if (start != null && end != null) {
            return prefix + " between " + display.format(java.sql.Date.valueOf(start))
                + " and " + display.format(java.sql.Date.valueOf(end));
        } else if (start != null) {
            return prefix + " after " + display.format(java.sql.Date.valueOf(start));
        } else {
            return prefix + " before " + display.format(java.sql.Date.valueOf(end));
        }
    }
    
    private boolean isScheduledForToday(DoctorShift shift) {
        LocalDate scheduledDate = DateTime.getDate(DateTime.toDateTimeObject(shift.getStartDateTime()));
        LocalDate today = DateTime.getDate(DateTime.currentDateTime());
        return scheduledDate.equals(today);
    }
    
    private boolean isOverdue(DoctorShift shift) {
        LocalDateTime scheduledEnd = DateTime.toDateTimeObject(shift.getEndDateTime());
        LocalDateTime now = DateTime.currentDateTime();
        return now.isAfter(scheduledEnd);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jOptionPane1 = new javax.swing.JOptionPane();
        jPanel1 = new javax.swing.JPanel();
        searchPane = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        resetSearchButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        sortByPane = new javax.swing.JPanel();
        sortByLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JComboBox<>();
        sortByLabel1 = new javax.swing.JLabel();
        filterByField = new javax.swing.JComboBox<>();
        wrapperPane = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        shiftTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        startShiftButton = new javax.swing.JButton();
        endShiftButton = new javax.swing.JButton();
        requestShiftButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1160, 2147483647));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1160, 50));
        jPanel1.setLayout(new java.awt.BorderLayout());

        searchPane.setBackground(new java.awt.Color(255, 255, 255));

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

        jPanel1.add(searchPane, java.awt.BorderLayout.WEST);
        jPanel1.add(filler1, java.awt.BorderLayout.LINE_END);

        sortByPane.setBackground(new java.awt.Color(255, 255, 255));

        sortByLabel.setText("Sort By");
        sortByPane.add(sortByLabel);

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Scheduled Date Time", "Actual Date Time" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Scheduled Date Time", "Actual Date Time", "Status" }));
        filterByField.setPreferredSize(new java.awt.Dimension(150, 30));
        filterByField.setRequestFocusEnabled(false);
        filterByField.addActionListener(this::filterByFieldActionPerformed);
        sortByPane.add(filterByField);

        jPanel1.add(sortByPane, java.awt.BorderLayout.LINE_END);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        wrapperPane.setBackground(new java.awt.Color(255, 255, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 40));
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new java.awt.BorderLayout());

        tableScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        tableScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        shiftTable.setBackground(new java.awt.Color(255, 255, 255));
        shiftTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Shift ID", "Date", "Scheduled Start Time", "Scheduled End Time", "Actual Start Time", "Actual End Time", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        shiftTable.setAlignmentY(0.0F);
        shiftTable.setRowHeight(40);
        shiftTable.setRowMargin(5);
        tableScrollPane.setViewportView(shiftTable);

        jPanel2.add(tableScrollPane, java.awt.BorderLayout.CENTER);

        showResultsForPanel.setBackground(new java.awt.Color(255, 255, 255));
        showResultsForPanel.setAlignmentY(0.0F);
        showResultsForPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        showResultsForPanel.add(showResultsForLabel);

        jPanel2.add(showResultsForPanel, java.awt.BorderLayout.NORTH);

        wrapperPane.add(jPanel2);

        operationsPanel1.setBackground(new java.awt.Color(255, 255, 255));
        operationsPanel1.setAlignmentY(0.0F);
        operationsPanel1.setMaximumSize(new java.awt.Dimension(300, 250));
        operationsPanel1.setPreferredSize(new java.awt.Dimension(300, 250));
        operationsPanel1.setLayout(new javax.swing.BoxLayout(operationsPanel1, javax.swing.BoxLayout.Y_AXIS));

        operationsLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        operationsLabel1.setForeground(new java.awt.Color(102, 102, 102));
        operationsLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        operationsLabel1.setText("Operations");
        operationsPanel1.add(operationsLabel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        jPanel3.setPreferredSize(new java.awt.Dimension(566, 140));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        startShiftButton.setText("Start Shift");
        startShiftButton.setMaximumSize(new java.awt.Dimension(150, 40));
        startShiftButton.setPreferredSize(new java.awt.Dimension(150, 40));
        startShiftButton.addActionListener(this::startShiftButtonActionPerformed);
        jPanel3.add(startShiftButton);

        endShiftButton.setText("End Shift");
        endShiftButton.setMaximumSize(new java.awt.Dimension(150, 40));
        endShiftButton.setPreferredSize(new java.awt.Dimension(150, 40));
        endShiftButton.addActionListener(this::endShiftButtonActionPerformed);
        jPanel3.add(endShiftButton);

        requestShiftButton.setText("Request Cancel Shift");
        requestShiftButton.setMaximumSize(new java.awt.Dimension(150, 40));
        requestShiftButton.setPreferredSize(new java.awt.Dimension(150, 40));
        requestShiftButton.addActionListener(this::requestShiftButtonActionPerformed);
        jPanel3.add(requestShiftButton);

        operationsPanel1.add(jPanel3);

        wrapperPane.add(operationsPanel1);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        if (currentDoctorShifts == null) return;
        String selected = (String) sortField.getSelectedItem();
        List<DoctorShift> sorted = new ArrayList<>(currentDoctorShifts);

        switch (selected) {
            case "ID":
                sorted.sort((a, b) -> a.getShiftId().compareToIgnoreCase(b.getShiftId()));
                break;

            case "Scheduled Date Time":
                sorted.sort(Comparator.comparing(s -> DateTime.toDateTimeObject(s.getStartDateTime())));
                break;

            case "Actual Date Time":
                sorted.sort((a, b) -> {
                    boolean aEmpty = a.getActualStartDateTime() == null || a.getActualStartDateTime().isEmpty();
                    boolean bEmpty = b.getActualStartDateTime() == null || b.getActualStartDateTime().isEmpty();
                    if (aEmpty && bEmpty) return 0;
                    if (aEmpty) return 1;   // shifts not yet started sink to the bottom
                    if (bEmpty) return -1;
                    return DateTime.toDateTimeObject(a.getActualStartDateTime())
                        .compareTo(DateTime.toDateTimeObject(b.getActualStartDateTime()));
                });
                break;

            default: // "Default" -> fall back to shift ID
                sorted.sort((a, b) -> a.getShiftId().compareToIgnoreCase(b.getShiftId()));
        }

        currentDoctorShifts = sorted;
        populateShiftTable(sorted);
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String rawKeyword = searchField.getText();
        String keyword = rawKeyword.trim().toLowerCase();

        List<DoctorShift> results = new ArrayList<>();
        for (DoctorShift shift : getBaseShifts()) {
            boolean match = shift.getShiftId().toLowerCase().contains(keyword)
                || shift.getStatus().toString().toLowerCase().contains(keyword)
                || (shift.getMedicalManager() != null
                    && shift.getMedicalManager().getName().toLowerCase().contains(keyword));
            if (match) results.add(shift);
        }

        currentDoctorShifts = results;
        populateShiftTable(results);
        updateResultLabel("Showing results for \"" + rawKeyword + "\"", results.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) sortField.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) shiftTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        shiftTable.setRowSorter(sorter);
        int selectedIndex = sortField.getSelectedIndex();
        int columnToSortIndex;
        switch (selectedIndex) {
            case 0: columnToSortIndex = 3; break; // "Joined At" -> table column 2
            case 1: columnToSortIndex = 0; break;
            case 2: columnToSortIndex = 1; break;
            case 3: columnToSortIndex = 2; break;
            default: columnToSortIndex = 3;
        }
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(columnToSortIndex, SortOrder.ASCENDING)));
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();

        List<DoctorShift> base = (currentDoctorShifts != null) ? currentDoctorShifts : getBaseShifts();

        switch (selected) {
            case "Scheduled Date Time": {
                currentDoctorShifts = base;
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByScheduledDateRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Actual Date Time": {
                currentDoctorShifts = base;
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByActualDateRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Status": {
                currentDoctorShifts = base;
                DoctorShift.DoctorShiftStatus status = selectStatus();
                if (status != null) {
                    filterByStatus(status);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            default: // "Default" -> no filter
                currentDoctorShifts = base;
                populateShiftTable(base);
                updateResultLabel("Showing " + base.size() + " shift(s)", base.size());
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadDoctorShifts();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void requestShiftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestShiftButtonActionPerformed
        DoctorShift selected = getSelectedShift();
        if (selected == null) return;

        if (DoctorShift.DoctorShiftStatus.CANCELLED.equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(parentWindow,
                "This shift is already cancelled.", "Cannot Request", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (DoctorShift.DoctorShiftStatus.COMPLETED.equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(parentWindow,
                "This shift has already been completed and cannot be cancelled.", "Cannot Request", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (isOverdue(selected)) {
            JOptionPane.showMessageDialog(parentWindow,
                "This shift is overdue and can no longer be cancelled.", "Cannot Request", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField reasonField = new JTextField(20);
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Reason for cancellation:"));
        panel.add(reasonField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Request Shift Cancellation", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String reason = reasonField.getText().trim();
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Please provide a reason.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            LinkedHashMap<String, String> msg = usrCtrl.getRequestController().createRequest(
                Request.RequestType.CANCEL_SHIFT,
                selected.getMedicalManager(),
                selected.getShiftId(),
                reason
            );

            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Cancellation request submitted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_requestShiftButtonActionPerformed

    private void startShiftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startShiftButtonActionPerformed
        DoctorShift selected = getSelectedShift();
        if (selected == null) return;

        if (!isScheduledForToday(selected)) {
            JOptionPane.showMessageDialog(parentWindow,
                "This shift is not scheduled for today. You can only start a shift on its scheduled date.",
                "Cannot Start Shift", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Start this shift now?", "Confirm Start Shift", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        LinkedHashMap<String, String> result = new DoctorManagement(usrCtrl.getActiveUser()).startShift(selected);
        JOptionPane.showMessageDialog(parentWindow, result.get("msg"),
            result.get("success") != null ? "Success" : "Error",
            result.get("success") != null ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        loadDoctorShifts();
    }//GEN-LAST:event_startShiftButtonActionPerformed

    private void endShiftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endShiftButtonActionPerformed
        DoctorShift selected = getSelectedShift();
        if (selected == null) return;

        if (!isScheduledForToday(selected)) {
            JOptionPane.showMessageDialog(parentWindow,
                "This shift is not scheduled for today. You can only end a shift on its scheduled date.",
                "Cannot End Shift", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "End this shift now?", "Confirm End Shift", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        LinkedHashMap<String, String> result = new DoctorManagement(usrCtrl.getActiveUser()).endShift(selected);
        JOptionPane.showMessageDialog(parentWindow, result.get("msg"),
            result.get("success") != null ? "Success" : "Error",
            result.get("success") != null ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        loadDoctorShifts();
    }//GEN-LAST:event_endShiftButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton endShiftButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel operationsLabel1;
    private javax.swing.JPanel operationsPanel1;
    private javax.swing.JButton requestShiftButton;
    private javax.swing.JButton resetSearchButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPane;
    private javax.swing.JTable shiftTable;
    private javax.swing.JLabel showResultsForLabel;
    private javax.swing.JPanel showResultsForPanel;
    private javax.swing.JLabel sortByLabel;
    private javax.swing.JLabel sortByLabel1;
    private javax.swing.JPanel sortByPane;
    private javax.swing.JComboBox<String> sortField;
    private javax.swing.JButton startShiftButton;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
