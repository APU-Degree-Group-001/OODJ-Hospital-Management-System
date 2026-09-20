/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.doctor.consultation;

import oodj_hospital_management_system.gui.medical_manager.billing_management.*;
import oodj_hospital_management_system.gui.admin.user_management.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
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
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.gui.utils.WrappingCellRenderer;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.services.InsuranceManagement;

/**
 *
 * @author sheaw
 */
public class AvailableConsultationSlotsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private Doctor currentDoctor;
    private List<ConsultationSlot> currentSlots;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form PatientManagement
     */
    public AvailableConsultationSlotsPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        this.currentDoctor = (Doctor) usrCtrl.getActiveUser();
        initComponents();
        loadAvailableSlots();
        customStyleHeader();
        availableSlotsTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, availableSlotsTable.getRowHeight() * availableSlotsTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        availableSlotsTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        availableSlotsTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));

        for (int col = 0; col < availableSlotsTable.getColumnModel().getColumnCount(); col++) {
            availableSlotsTable.getColumnModel().getColumn(col).setCellRenderer(new WrappingCellRenderer());
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        loadAvailableSlots();
    }
    
    public void loadAvailableSlots() {
        currentSlots = usrCtrl.getConsultationController()
            .getAvailableConsultationSlotsForDoctor(currentDoctor.getId());
        populateTable(currentSlots);
    }
    
    private void populateTable(List<ConsultationSlot> slots) {
        DefaultTableModel model = (DefaultTableModel) availableSlotsTable.getModel();
        model.setRowCount(0);
        for (ConsultationSlot slot : slots) {
            LocalDateTime startDateTimeObj = DateTime.toDateTimeObject(slot.getStartDateTime());
            LocalDateTime endDateTimeObj = DateTime.toDateTimeObject(slot.getEndDateTime());
            LocalDate date = DateTime.getDate(startDateTimeObj);
            LocalTime startTime = DateTime.getTime(startDateTimeObj);
            LocalTime endTime = DateTime.getTime(endDateTimeObj);

            String dateStr = DateTime.toDateStringWords(date);
            String timeRange = DateTime.toTimeString(startTime) + " - " + DateTime.toTimeString(endTime);

            String[] rowData = {
                slot.getConsultationSlotId(),
                dateStr,
                timeRange
            };
            model.addRow(rowData);
        }
    }

    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }

    private LinkedHashMap<String, String> selectDateTimeRange() {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();

        javax.swing.SpinnerDateModel startTimeModel = new javax.swing.SpinnerDateModel();
        javax.swing.JSpinner startTimeSpinner = new javax.swing.JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(startTimeSpinner, "hh:mm a"));

        javax.swing.SpinnerDateModel endTimeModel = new javax.swing.SpinnerDateModel();
        javax.swing.JSpinner endTimeSpinner = new javax.swing.JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(endTimeSpinner, "hh:mm a"));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new javax.swing.JLabel("From Date:"));
        panel.add(startDateChooser);
        panel.add(new javax.swing.JLabel("To Date:"));
        panel.add(endDateChooser);
        panel.add(new javax.swing.JLabel("From Time:"));
        panel.add(startTimeSpinner);
        panel.add(new javax.swing.JLabel("To Time:"));
        panel.add(endTimeSpinner);
        panel.add(new javax.swing.JLabel("(Leave any field blank/unchanged if not filtering by it)"));

        // Track whether the user actually touched the time spinners, since
        // JSpinner always holds *some* value (defaults to "now") even if untouched.
        final boolean[] startTimeTouched = {false};
        final boolean[] endTimeTouched = {false};
        startTimeSpinner.addChangeListener(e -> startTimeTouched[0] = true);
        endTimeSpinner.addChangeListener(e -> endTimeTouched[0] = true);

        int dialogResult = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Date / Time", JOptionPane.OK_CANCEL_OPTION);

        if (dialogResult != JOptionPane.OK_OPTION) {
            return result;
        }

        Date startDateRaw = startDateChooser.getDate();
        Date endDateRaw = endDateChooser.getDate();

        if (startDateRaw != null && endDateRaw != null && endDateRaw.compareTo(startDateRaw) < 0) {
            JOptionPane.showMessageDialog(parentWindow,
                "Invalid date range", "WARNING", JOptionPane.ERROR_MESSAGE);
            return result;
        }

        String startTimeStr = null;
        String endTimeStr = null;
        if (startTimeTouched[0] || endTimeTouched[0]) {
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
            startTimeStr = startTimeTouched[0] ? timeSdf.format((Date) startTimeSpinner.getValue()) : null;
            endTimeStr = endTimeTouched[0] ? timeSdf.format((Date) endTimeSpinner.getValue()) : null;

            if (startTimeStr != null && endTimeStr != null
                    && LocalTime.parse(endTimeStr).isBefore(LocalTime.parse(startTimeStr))) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Invalid time range", "WARNING", JOptionPane.ERROR_MESSAGE);
                return result;
            }
        }

        // At least one of the four fields must be set.
        if (startDateRaw == null && endDateRaw == null && startTimeStr == null && endTimeStr == null) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please enter at least one date or time", "Missing Input",
                JOptionPane.INFORMATION_MESSAGE);
            return result;
        }

        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        result.put("startDate", startDateRaw != null ? dateSdf.format(startDateRaw) : null);
        result.put("endDate", endDateRaw != null ? dateSdf.format(endDateRaw) : null);
        result.put("startTime", startTimeStr);
        result.put("endTime", endTimeStr);

        return result;
    }

    private void filterByDateTimeRange(String startDate, String endDate, String startTime, String endTime) {
        List<ConsultationSlot> baseSlots = usrCtrl.getConsultationController()
        .getAvailableConsultationSlotsForDoctor(currentDoctor.getId());

        List<ConsultationSlot> filtered = new ArrayList<>();
        LocalTime startT = startTime != null ? LocalTime.parse(startTime) : null;
        LocalTime endT = endTime != null ? LocalTime.parse(endTime) : null;

        for (ConsultationSlot slot : baseSlots) {
            LocalDateTime dt = DateTime.toDateTimeObject(slot.getStartDateTime());
            LocalDate date = DateTime.getDate(dt);
            LocalTime time = DateTime.getTime(dt);

            boolean afterStartDate = startDate == null || !date.isBefore(LocalDate.parse(startDate));
            boolean beforeEndDate = endDate == null || !date.isAfter(LocalDate.parse(endDate));
            boolean afterStartTime = startT == null || !time.isBefore(startT);
            boolean beforeEndTime = endT == null || !time.isAfter(endT);

            if (afterStartDate && beforeEndDate && afterStartTime && beforeEndTime) {
                filtered.add(slot);
            }
        }

        currentSlots = filtered;
        populateTable(filtered);

        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        StringBuilder label = new StringBuilder("Showing slots");
        if (startDate != null || endDate != null) {
            label.append(" on ");
            label.append(startDate != null ? display.format(java.sql.Date.valueOf(startDate)) : "any date");
            label.append(" to ");
            label.append(endDate != null ? display.format(java.sql.Date.valueOf(endDate)) : "any date");
        }
        if (startT != null || endT != null) {
            label.append(" between ");
            label.append(startT != null ? DateTime.toTimeString(startT) : "any time");
            label.append(" and ");
            label.append(endT != null ? DateTime.toTimeString(endT) : "any time");
        }
        updateResultLabel(label.toString(), filtered.size());
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
        availableSlotsTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1160, 2147483647));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1160, 50));
        jPanel1.setLayout(new java.awt.BorderLayout());

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

        jPanel1.add(searchPane, java.awt.BorderLayout.WEST);
        jPanel1.add(filler1, java.awt.BorderLayout.LINE_END);

        sortByPane.setBackground(new java.awt.Color(246, 251, 255));

        sortByLabel.setText("Sort By");
        sortByPane.add(sortByLabel);

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Slot ID", "Date" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Date Time", " " }));
        filterByField.setPreferredSize(new java.awt.Dimension(150, 30));
        filterByField.setRequestFocusEnabled(false);
        filterByField.addActionListener(this::filterByFieldActionPerformed);
        sortByPane.add(filterByField);

        jPanel1.add(sortByPane, java.awt.BorderLayout.LINE_END);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setBackground(new java.awt.Color(246, 251, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 40));
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new java.awt.BorderLayout());

        tableScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        tableScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        availableSlotsTable.setBackground(new java.awt.Color(255, 255, 255));
        availableSlotsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Consultation Slot ID", "Date", "Time"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        availableSlotsTable.setAlignmentY(0.0F);
        availableSlotsTable.setRowHeight(40);
        availableSlotsTable.setRowMargin(5);
        tableScrollPane.setViewportView(availableSlotsTable);

        jPanel2.add(tableScrollPane, java.awt.BorderLayout.CENTER);

        showResultsForPanel.setBackground(new java.awt.Color(246, 251, 255));
        showResultsForPanel.setAlignmentY(0.0F);
        showResultsForPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        showResultsForPanel.add(showResultsForLabel);

        jPanel2.add(showResultsForPanel, java.awt.BorderLayout.NORTH);

        wrapperPane.add(jPanel2);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        searchButtonActionPerformed(evt);
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String rawKeyword = searchField.getText();
        String keyword = rawKeyword.trim().toLowerCase();
        currentSlots = usrCtrl.getConsultationController()
            .getAvailableConsultationSlotsForDoctor(currentDoctor.getId());

        List<ConsultationSlot> results = new ArrayList<>();
        for (ConsultationSlot slot : currentSlots) {
            if (slot.getConsultationSlotId().toLowerCase().contains(keyword)) {
                results.add(slot);
            }
        }
        currentSlots = results;
        populateTable(results);
        updateResultLabel("Showing results for \"" + rawKeyword + "\"", results.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");

        DefaultTableModel model = (DefaultTableModel) availableSlotsTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

        Comparator<String> dateStringComparator = (a, b) -> {
            if (a.equals("-") && b.equals("-")) return 0;
            if (a.equals("-")) return 1;
            if (b.equals("-")) return -1;
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMMM yyyy");
                LocalDate dateA = LocalDate.parse(a, fmt);
                LocalDate dateB = LocalDate.parse(b, fmt);
                return dateA.compareTo(dateB);
            } catch (java.time.format.DateTimeParseException e) {
                return a.compareTo(b);
            }
        };
        sorter.setComparator(1, dateStringComparator); // "Date" column

        availableSlotsTable.setRowSorter(sorter);
        int selectedIndex = sortField.getSelectedIndex();
        int columnToSortIndex;
        switch (selectedIndex) {
            case 1: columnToSortIndex = 0; break; // Slot ID
            case 2: columnToSortIndex = 1; break; // Date
            default: columnToSortIndex = 0; // Default
        }
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(columnToSortIndex, SortOrder.ASCENDING)));
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) filterByField.getSelectedItem();

        if (selected == null) {
            return;
        }

        switch (selected) {
        case "Date Time": {
            LinkedHashMap<String, String> range = selectDateTimeRange();
            if (range.get("startDate") != null || range.get("endDate") != null
                    || range.get("startTime") != null || range.get("endTime") != null) {
                filterByDateTimeRange(range.get("startDate"), range.get("endDate"),
                    range.get("startTime"), range.get("endTime"));
            } else {
                filterByField.setSelectedIndex(0);
            }
            break;
        }
        default: {
            List<ConsultationSlot> allSlots = usrCtrl.getConsultationController()
                .getAvailableConsultationSlotsForDoctor(currentDoctor.getId());
            currentSlots = allSlots;
            populateTable(allSlots);
            updateResultLabel("Showing " + allSlots.size() + " available slot(s)", allSlots.size());
        }
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        searchField.setText("");
        filterByField.setSelectedIndex(0);
        sortField.setSelectedIndex(0);
        showResultsForLabel.setText("");
        loadAvailableSlots();
    }//GEN-LAST:event_resetSearchButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable availableSlotsTable;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton resetSearchButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPane;
    private javax.swing.JLabel showResultsForLabel;
    private javax.swing.JPanel showResultsForPanel;
    private javax.swing.JLabel sortByLabel;
    private javax.swing.JLabel sortByLabel1;
    private javax.swing.JPanel sortByPane;
    private javax.swing.JComboBox<String> sortField;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
