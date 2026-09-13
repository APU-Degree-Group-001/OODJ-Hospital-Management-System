/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.patient.consultation;

import oodj_hospital_management_system.gui.medical_manager.billing_management.*;
import oodj_hospital_management_system.gui.admin.user_management.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
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
public class BookedConsultationSlotsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private Patient currentPatient;
    private List<Consultation> currentConsultations;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form PatientManagement
     */
    public BookedConsultationSlotsPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        this.currentPatient = (Patient) usrCtrl.getActiveUser();
        initComponents();
        loadBookedConsultations();
        customStyleHeader();
        bookedSlotsTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, bookedSlotsTable.getRowHeight() * bookedSlotsTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        bookedSlotsTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        bookedSlotsTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
 
        for (int col = 0; col < bookedSlotsTable.getColumnModel().getColumnCount(); col++) {
            bookedSlotsTable.getColumnModel().getColumn(col).setCellRenderer(new WrappingCellRenderer());
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        loadBookedConsultations();
    }
    
    public void loadBookedConsultations() {
        currentConsultations = usrCtrl.getConsultationController()
            .getBookedConsultationsForPatient(currentPatient.getId());
        populateTable(currentConsultations);
    }
 
    private Doctor resolveDoctor(Consultation c) {
        if (c == null || c.getConsultationSlot() == null) return null;
        if (c.getConsultationSlot().getDoctorShift() == null) return null;
        return c.getConsultationSlot().getDoctorShift().getDoctor();
    }
 
    private String formatDoctor(Doctor d) {
        if (d == null) return "-";
        return d.getId() + " - " + d.getName();
    }
 
    private void populateTable(List<Consultation> consultations) {
        DefaultTableModel model = (DefaultTableModel) bookedSlotsTable.getModel();
        model.setRowCount(0);
        for (Consultation c : consultations) {
            ConsultationSlot slot = c.getConsultationSlot();
            Doctor doctor = resolveDoctor(c);
 
            LocalDateTime startDateTimeObj = DateTime.toDateTimeObject(slot.getStartDateTime());
            LocalDateTime endDateTimeObj = DateTime.toDateTimeObject(slot.getEndDateTime());
            LocalDate date = DateTime.getDate(startDateTimeObj);
            LocalTime startTime = DateTime.getTime(startDateTimeObj);
            LocalTime endTime = DateTime.getTime(endDateTimeObj);
 
            String dateStr = DateTime.toDateStringWords(date);
            String timeRange = DateTime.toTimeString(startTime) + " - " + DateTime.toTimeString(endTime);
 
            String[] rowData = {
                slot.getConsultationSlotId(),
                c.getConsultationId(),
                formatDoctor(doctor),
                dateStr,
                timeRange,
                c.getDescription()
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
        List<Consultation> baseConsultations = usrCtrl.getConsultationController()
        .getBookedConsultationsForPatient(currentPatient.getId());
 
        List<Consultation> filtered = new ArrayList<>();
        LocalTime startT = startTime != null ? LocalTime.parse(startTime) : null;
        LocalTime endT = endTime != null ? LocalTime.parse(endTime) : null;
 
        for (Consultation c : baseConsultations) {
            LocalDateTime dt = DateTime.toDateTimeObject(c.getConsultationSlot().getStartDateTime());
            LocalDate date = DateTime.getDate(dt);
            LocalTime time = DateTime.getTime(dt);
 
            boolean afterStartDate = startDate == null || !date.isBefore(LocalDate.parse(startDate));
            boolean beforeEndDate = endDate == null || !date.isAfter(LocalDate.parse(endDate));
            boolean afterStartTime = startT == null || !time.isBefore(startT);
            boolean beforeEndTime = endT == null || !time.isAfter(endT);
 
            if (afterStartDate && beforeEndDate && afterStartTime && beforeEndTime) {
                filtered.add(c);
            }
        }
 
        currentConsultations = filtered;   // keep state in sync
        populateTable(filtered);
 
        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        StringBuilder label = new StringBuilder("Showing consultations");
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
    
    private Consultation getSelectedConsultation() {
        int viewRow = bookedSlotsTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a consultation first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = bookedSlotsTable.convertRowIndexToModel(viewRow);
        String consultationId = (String) bookedSlotsTable.getModel().getValueAt(modelRow, 1); // "Consultation ID" column
        for (Consultation c : currentConsultations) {
            if (c.getConsultationId().equals(consultationId)) return c;
        }
        return null;
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
        bookedSlotsTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Slot ID", "ID", "Date Time", "Doctor" }));
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

        bookedSlotsTable.setBackground(new java.awt.Color(255, 255, 255));
        bookedSlotsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Consultation Slot ID", "Consultation ID", "Doctor", "Date", "Time", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bookedSlotsTable.setAlignmentY(0.0F);
        bookedSlotsTable.setRowHeight(40);
        bookedSlotsTable.setRowMargin(5);
        tableScrollPane.setViewportView(bookedSlotsTable);

        jPanel2.add(tableScrollPane, java.awt.BorderLayout.CENTER);

        showResultsForPanel.setBackground(new java.awt.Color(246, 251, 255));
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

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        jPanel6.setPreferredSize(new java.awt.Dimension(566, 140));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.Y_AXIS));

        cancelButton.setText("Cancel");
        cancelButton.setMaximumSize(new java.awt.Dimension(150, 40));
        cancelButton.setPreferredSize(new java.awt.Dimension(150, 40));
        cancelButton.addActionListener(this::cancelButtonActionPerformed);
        jPanel6.add(cancelButton);

        operationsPanel1.add(jPanel6);

        wrapperPane.add(operationsPanel1);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        searchButtonActionPerformed(evt);
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String rawKeyword = searchField.getText();
        String keyword = rawKeyword.trim().toLowerCase();
        currentConsultations = usrCtrl.getConsultationController()
            .getBookedConsultationsForPatient(currentPatient.getId());
 
        List<Consultation> results = new ArrayList<>();
        for (Consultation c : currentConsultations) {
            Doctor d = resolveDoctor(c);
            String description = c.getDescription() != null ? c.getDescription() : "";
 
            boolean match = c.getConsultationSlot().getConsultationSlotId().toLowerCase().contains(keyword)
                || c.getConsultationId().toLowerCase().contains(keyword)
                || (d != null && (d.getId().toLowerCase().contains(keyword) || d.getName().toLowerCase().contains(keyword)))
                || description.toLowerCase().contains(keyword);
 
            if (match) {
                results.add(c);
            }
        }
        currentConsultations = results;
        populateTable(results);
        updateResultLabel("Showing results for \"" + rawKeyword + "\"", results.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
 
        DefaultTableModel model = (DefaultTableModel) bookedSlotsTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        bookedSlotsTable.setRowSorter(sorter);
 
        String selected = (String) sortField.getSelectedItem();
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        switch (selected) {
            case "Slot ID":
                sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                break;
            case "ID":
                sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                break;
            case "Date Time":
                // Date column first, then Time column, so same-day rows stay ordered.
                sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
                sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
                break;
            case "Doctor":
                sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
                break;
            default:
                sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        }
        sorter.setSortKeys(sortKeys);
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
                List<Consultation> allConsultations = usrCtrl.getConsultationController()
                    .getBookedConsultationsForPatient(currentPatient.getId());
                currentConsultations = allConsultations;
                populateTable(allConsultations);
                updateResultLabel("Showing " + allConsultations.size() + " booked consultation(s)", allConsultations.size());
            }
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadBookedConsultations();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        Consultation selected = getSelectedConsultation();
        if (selected == null) return;

        ConsultationSlot slot = selected.getConsultationSlot();
        if (slot == null || slot.getStartDateTime() == null || slot.getStartDateTime().isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "This consultation has no scheduled time and cannot be cancelled here.",
                "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime slotStart = DateTime.toDateTimeObject(slot.getStartDateTime());
        LocalDateTime now = DateTime.currentDateTime();

        if (!slotStart.isAfter(now.plusHours(1))) {
            JOptionPane.showMessageDialog(parentWindow,
                "This consultation starts within 1 hour and can no longer be cancelled.",
                "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Cancel this consultation?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        LinkedHashMap<String, String> msg = usrCtrl.getConsultationController()
            .cancelConsultation(selected.getConsultationId());

        if (msg.get("success") != null) {
            JOptionPane.showMessageDialog(parentWindow,
                "Consultation cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBookedConsultations();
        } else {
            JOptionPane.showMessageDialog(parentWindow,
                "Failed to cancel consultation.\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

        private void endButtonActionPerformed() {
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable bookedSlotsTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel operationsLabel1;
    private javax.swing.JPanel operationsPanel1;
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
