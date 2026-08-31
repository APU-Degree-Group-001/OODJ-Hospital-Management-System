/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.medical_manager.billing_management;

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
public class CompletedConsultationsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<Consultation> currentConsultations;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * Creates new form PatientManagement
     */
    public CompletedConsultationsPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        loadConsultations();
        customStyleHeader();
        completedConsultationsTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, completedConsultationsTable.getRowHeight() * completedConsultationsTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        completedConsultationsTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        completedConsultationsTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));

        for (int col = 0; col < completedConsultationsTable.getColumnModel().getColumnCount(); col++) {
            completedConsultationsTable.getColumnModel().getColumn(col).setCellRenderer(new WrappingCellRenderer());
        }
    }
    
    private List<Consultation> getCompletedConsultations() {
        List<Consultation> all = usrCtrl.getConsultationController().getAllConsultations();
        List<Consultation> completed = new ArrayList<>();
        for (Consultation c : all) {
            // Only completed consultations that don't already have a bill.
            if (c.getConsultationStatus() == Consultation.ConsultationStatus.COMPLETED
                    && c.getBill() == null) {
                completed.add(c);
            }
        }
        return completed;
    }
 
    public void loadConsultations() {
        currentConsultations = getCompletedConsultations();
        populateTable(currentConsultations);
    }
    
    private String formatPatient(Patient p) {
        if (p == null) return "";
        return p.getId() + " - " + p.getName();
    }
 
    private String formatDoctor(Doctor d) {
        if (d == null) return "";
        return d.getId() + " - " + d.getName();
    }
    
    private Doctor getDoctorForConsultation(Consultation c) {
        ConsultationSlot slot = c.getConsultationSlot();
        return slot.getDoctorShift().getDoctor();
    }
    
    private void populateTable(List<Consultation> consultations) {
        DefaultTableModel model = (DefaultTableModel) completedConsultationsTable.getModel();
        model.setRowCount(0);
        for (Consultation c : consultations) {
            ConsultationSlot slot = c.getConsultationSlot();
            Patient patient = c.getPatient();
            Doctor doctor = getDoctorForConsultation(c);
 
            LocalDateTime startDateTimeObj = DateTime.toDateTimeObject(slot.getStartDateTime());
            LocalDateTime endDateTimeObj = DateTime.toDateTimeObject(slot.getEndDateTime());
            LocalDate date = DateTime.getDate(startDateTimeObj);
            LocalTime startTime = DateTime.getTime(startDateTimeObj);
            LocalTime endTime = DateTime.getTime(endDateTimeObj);
 
            String dateStr = DateTime.toDateStringWords(date);
            String timeRange = DateTime.toTimeString(startTime) + " - " + DateTime.toTimeString(endTime);
 
            // ADJUST: getConsultationType().getTypeName() if named differently
            String typeStr = c.getConsultationType() != null ? c.getConsultationType().getTypeName() : "";
 
            String[] rowData = {
                c.getConsultationId(),
                typeStr,
                formatPatient(patient),
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
 
        int result = javax.swing.JOptionPane.showConfirmDialog(parentWindow, panel, "Select Date Range", javax.swing.JOptionPane.OK_CANCEL_OPTION);
 
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
 
    private void filterByDateRange(String start, String end) {
        List<Consultation> filtered = new ArrayList<>();
        for (Consultation c : currentConsultations) {
            LocalDateTime dt = DateTime.toDateTimeObject(c.getConsultationSlot().getStartDateTime());
            LocalDate date = DateTime.getDate(dt);
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) {
                filtered.add(c);
            }
        }
        populateTable(filtered);
        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label;
        if (start != null && end != null) {
            label = "Showing consultations between " + display.format(java.sql.Date.valueOf(start)) + " and " + display.format(java.sql.Date.valueOf(end));
        } else if (start != null) {
            label = "Showing consultations after " + display.format(java.sql.Date.valueOf(start));
        } else {
            label = "Showing consultations before " + display.format(java.sql.Date.valueOf(end));
        }
        updateResultLabel(label, filtered.size());
    }
 
    private void filterByPatient(String keyword) {
        String kw = keyword.toLowerCase();
        List<Consultation> filtered = new ArrayList<>();
        for (Consultation c : currentConsultations) {
            Patient p = c.getPatient();
            if (p != null && (p.getId().toLowerCase().contains(kw) || p.getName().toLowerCase().contains(kw))) {
                filtered.add(c);
            }
        }
        populateTable(filtered);
        updateResultLabel("Showing consultations for patient \"" + keyword + "\"", filtered.size());
    }
 
    private void filterByDoctor(String keyword) {
        String kw = keyword.toLowerCase();
        List<Consultation> filtered = new ArrayList<>();
        for (Consultation c : currentConsultations) {
            Doctor d = getDoctorForConsultation(c);
            if (d != null && (d.getId().toLowerCase().contains(kw) || d.getName().toLowerCase().contains(kw))) {
                filtered.add(c);
            }
        }
        populateTable(filtered);
        updateResultLabel("Showing consultations for doctor \"" + keyword + "\"", filtered.size());
    }
    
    private LinkedHashMap<String, String> selectTimeRange() {
        LinkedHashMap<String, String> timeRange = new LinkedHashMap<>();

        javax.swing.SpinnerDateModel startModel = new javax.swing.SpinnerDateModel();
        javax.swing.JSpinner startTimeSpinner = new javax.swing.JSpinner(startModel);
        startTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(startTimeSpinner, "hh:mm a"));

        javax.swing.SpinnerDateModel endModel = new javax.swing.SpinnerDateModel();
        javax.swing.JSpinner endTimeSpinner = new javax.swing.JSpinner(endModel);
        endTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(endTimeSpinner, "hh:mm a"));

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.add(new javax.swing.JLabel("From:"));
        panel.add(startTimeSpinner);
        panel.add(new javax.swing.JLabel("To:"));
        panel.add(endTimeSpinner);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Select Time Range", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String startStr = sdf.format((Date) startTimeSpinner.getValue());
            String endStr = sdf.format((Date) endTimeSpinner.getValue());

            if (LocalTime.parse(endStr).isBefore(LocalTime.parse(startStr))) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Invalid time range", "WARNING", JOptionPane.ERROR_MESSAGE);
                return timeRange;
            }

            timeRange.put("startTime", startStr);
            timeRange.put("endTime", endStr);
        }
        return timeRange;
    }

    private void filterByTimeRange(String start, String end) {
        List<Consultation> filtered = new ArrayList<>();
        LocalTime startTime = start != null ? LocalTime.parse(start) : null;
        LocalTime endTime = end != null ? LocalTime.parse(end) : null;

        for (Consultation c : currentConsultations) {
            LocalDateTime dt = DateTime.toDateTimeObject(c.getConsultationSlot().getStartDateTime());
            LocalTime slotTime = DateTime.getTime(dt);

            boolean afterStart = startTime == null || !slotTime.isBefore(startTime);
            boolean beforeEnd = endTime == null || !slotTime.isAfter(endTime);
            if (afterStart && beforeEnd) {
                filtered.add(c);
            }
        }

        populateTable(filtered);

        String startLabel = startTime != null ? DateTime.toTimeString(startTime) : "any time";
        String endLabel = endTime != null ? DateTime.toTimeString(endTime) : "any time";
        String label = String.format("Showing consultations between %s and %s", startLabel, endLabel);
        updateResultLabel(label, filtered.size());
    }
 
    private void resetFieldBorder(JTextField field) {
        field.setBorder(javax.swing.UIManager.getBorder("TextField.border"));
    }
 
    private void setFieldError(JTextField field) {
        field.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED));
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
        completedConsultationsTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        generateBill = new javax.swing.JButton();

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Patient", "Doctor" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Assigned Doctor", "Date", "Time", " " }));
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

        completedConsultationsTable.setBackground(new java.awt.Color(255, 255, 255));
        completedConsultationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Consultation ID", "Type", "Patient", "Doctor", "Date", "Time", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        completedConsultationsTable.setAlignmentY(0.0F);
        completedConsultationsTable.setRowHeight(40);
        completedConsultationsTable.setRowMargin(5);
        tableScrollPane.setViewportView(completedConsultationsTable);

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

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        jPanel3.setPreferredSize(new java.awt.Dimension(566, 140));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        generateBill.setText("Generate Bill");
        generateBill.setMaximumSize(new java.awt.Dimension(150, 40));
        generateBill.setPreferredSize(new java.awt.Dimension(150, 40));
        generateBill.addActionListener(this::generateBillActionPerformed);
        jPanel3.add(generateBill);

        operationsPanel1.add(jPanel3);

        wrapperPane.add(operationsPanel1);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String rawKeyword = searchField.getText();
        String keyword = rawKeyword.trim().toLowerCase();
        currentConsultations = getCompletedConsultations();
 
        List<Consultation> results = new ArrayList<>();
        for (Consultation c : currentConsultations) {
            Patient p = c.getPatient();
            Doctor d = getDoctorForConsultation(c);
            String type = c.getConsultationType() != null ? c.getConsultationType().getTypeName() : "";
            String description = c.getDescription() != null ? c.getDescription() : "";
 
            boolean match = c.getConsultationId().toLowerCase().contains(keyword)
                || type.toLowerCase().contains(keyword)
                || (p != null && (p.getId().toLowerCase().contains(keyword) || p.getName().toLowerCase().contains(keyword)))
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
 
        DefaultTableModel model = (DefaultTableModel) completedConsultationsTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        completedConsultationsTable.setRowSorter(sorter);
        int selectedIndex = sortField.getSelectedIndex();
        int columnToSortIndex;
        switch (selectedIndex) {
            case 1: columnToSortIndex = 0; break; // ID -> Consultation ID
            case 2: columnToSortIndex = 2; break; // Patient
            case 3: columnToSortIndex = 3; break; // Doctor
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
        case "Assigned Doctor": {
            String keyword = JOptionPane.showInputDialog(parentWindow, "Enter Doctor ID or Name:", "Filter by Doctor", JOptionPane.PLAIN_MESSAGE);
            if (keyword != null && !keyword.trim().isEmpty()) {
                filterByDoctor(keyword.trim());
            } else {
                filterByField.setSelectedIndex(0);
            }
            break;
        }
        case "Date": {
            LinkedHashMap<String, String> dateRange = selectDateRange();
            if (dateRange.get("startDate") != null || dateRange.get("endDate") != null) {
                filterByDateRange(dateRange.get("startDate"), dateRange.get("endDate"));
            } else {
                filterByField.setSelectedIndex(0);
            }
            break;
        }
        case "Time": {
            LinkedHashMap<String, String> timeRange = selectTimeRange();
            if (timeRange.get("startTime") != null || timeRange.get("endTime") != null) {
                filterByTimeRange(timeRange.get("startTime"), timeRange.get("endTime"));
            } else {
                filterByField.setSelectedIndex(0);
            }
            break;
        }
        default:
            populateTable(currentConsultations);
    }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadConsultations();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void generateBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateBillActionPerformed
        int selectedRow = completedConsultationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a consultation to generate a bill for.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = completedConsultationsTable.convertRowIndexToModel(selectedRow);
        String consultationId = (String) completedConsultationsTable.getModel().getValueAt(modelRow, 0);

        // ADJUST: swap for whatever your billing/insurance service actually exposes
        List<Insurance> insurances = InsuranceManagement.getAllInsurances();

        List<String> options = new ArrayList<>();
        List<String> insuranceIds = new ArrayList<>();
        options.add("None");
        insuranceIds.add(null);

        for (Insurance ins : insurances) {
            if (!ins.isAccepted()) {
                continue;
            }
            options.add(String.format("%s - %s (%.0f%% covered)",
                ins.getInsuranceId(), ins.getInsuranceName(), ins.getCoveragePercentage() * 100));
            insuranceIds.add(ins.getInsuranceId());
        }

        JComboBox<String> insuranceDropdown = new JComboBox<>(options.toArray(new String[0]));
        insuranceDropdown.setPreferredSize(new java.awt.Dimension(280, 30));

        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Select Insurance:");
        label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        insuranceDropdown.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(insuranceDropdown);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Generate Bill", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return; // user cancelled
        }

        String selectedInsuranceId = insuranceIds.get(insuranceDropdown.getSelectedIndex());

        LinkedHashMap<String, String> msg = usrCtrl.getBillingController()
            .generateBillFromConsultation(consultationId, selectedInsuranceId);

        if (msg.get("success") != null) {
            JOptionPane.showMessageDialog(parentWindow, msg.get("msg"), "Success", JOptionPane.INFORMATION_MESSAGE);
            loadConsultations();
        } else {
            JOptionPane.showMessageDialog(parentWindow, msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_generateBillActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable completedConsultationsTable;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JButton generateBill;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
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
