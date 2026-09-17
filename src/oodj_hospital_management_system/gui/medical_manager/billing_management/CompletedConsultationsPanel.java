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
        User activeUser = usrCtrl.getActiveUser();
        List<Consultation> completed = new ArrayList<>();
        for (Consultation c : all) {
            if (c.getConsultationStatus() != Consultation.ConsultationStatus.COMPLETED
                    || c.getBill() != null) {
                continue;
            }
            Doctor doctor = getDoctorForConsultation(c);
            boolean isAssignedDoctor = doctor != null
                    && doctor.getMedicalManager() != null
                    && doctor.getMedicalManager().getId().equals(activeUser.getId());
            if (isAssignedDoctor) {
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
    
    private LinkedHashMap<String, String> selectDateTimeRange() {
        LinkedHashMap<String, String> range = new LinkedHashMap<>();
        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();
        Date currentDate = new Date();
        startDateChooser.setMaxSelectableDate(currentDate);
        endDateChooser.setMaxSelectableDate(currentDate);

        javax.swing.SpinnerDateModel startTimeModel = new javax.swing.SpinnerDateModel();
        javax.swing.JSpinner startTimeSpinner = new javax.swing.JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(startTimeSpinner, "HH:mm"));

        javax.swing.SpinnerDateModel endTimeModel = new javax.swing.SpinnerDateModel();
        javax.swing.JSpinner endTimeSpinner = new javax.swing.JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        // Track whether the user actually changed each spinner from its
        // untouched default - if not, treat that side's time as unspecified.
        boolean[] startTimeTouched = { false };
        boolean[] endTimeTouched = { false };
        startTimeSpinner.addChangeListener(e -> startTimeTouched[0] = true);
        endTimeSpinner.addChangeListener(e -> endTimeTouched[0] = true);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(4, 2, 10, 8));
        panel.add(new javax.swing.JLabel("From Date:"));
        panel.add(startDateChooser);
        panel.add(new javax.swing.JLabel("From Time:"));
        panel.add(startTimeSpinner);
        panel.add(new javax.swing.JLabel("To Date:"));
        panel.add(endDateChooser);
        panel.add(new javax.swing.JLabel("To Time:"));
        panel.add(endTimeSpinner);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Select Date & Time Range", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                startTimeSpinner.commitEdit();
                endTimeSpinner.commitEdit();
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Invalid time entered.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return range;
            }

            Date startDateRaw = startDateChooser.getDate();
            Date endDateRaw = endDateChooser.getDate();

            if (startDateRaw == null && endDateRaw == null) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Please select at least one date", "Missing Date", JOptionPane.INFORMATION_MESSAGE);
                return range;
            }

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

            LocalDateTime startBound = null;
            if (startDateRaw != null) {
                LocalDate startDate = LocalDate.parse(sdfDate.format(startDateRaw));
                LocalTime startTime = startTimeTouched[0]
                    ? LocalTime.parse(sdfTime.format((Date) startTimeSpinner.getValue()))
                    : LocalTime.MIN;
                startBound = LocalDateTime.of(startDate, startTime);
            }

            LocalDateTime endBound = null;
            if (endDateRaw != null) {
                LocalDate endDate = LocalDate.parse(sdfDate.format(endDateRaw));
                LocalTime endTime = endTimeTouched[0]
                    ? LocalTime.parse(sdfTime.format((Date) endTimeSpinner.getValue()))
                    : LocalTime.MAX;
                endBound = LocalDateTime.of(endDate, endTime);
            }

            if (startBound != null && endBound != null && endBound.isBefore(startBound)) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Invalid date/time range", "WARNING", JOptionPane.ERROR_MESSAGE);
                return range;
            }

            range.put("start", startBound != null ? startBound.toString() : null);
            range.put("end", endBound != null ? endBound.toString() : null);
        }
        return range;
    }

    private void filterByDateTimeRange(String startStr, String endStr) {
        LocalDateTime start = startStr != null ? LocalDateTime.parse(startStr) : null;
        LocalDateTime end = endStr != null ? LocalDateTime.parse(endStr) : null;

        List<Consultation> filtered = new ArrayList<>();
        for (Consultation c : getCompletedConsultations()) {
            LocalDateTime dt = DateTime.toDateTimeObject(c.getConsultationSlot().getStartDateTime());
            boolean afterStart = start == null || !dt.isBefore(start);
            boolean beforeEnd = end == null || !dt.isAfter(end);
            if (afterStart && beforeEnd) {
                filtered.add(c);
            }
        }

        currentConsultations = filtered;
        populateTable(filtered);

        java.time.format.DateTimeFormatter displayFmt =
            java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy, hh:mm a");

        String label = String.format("Showing consultations between %s and %s",
            start != null ? start.format(displayFmt) : "any time",
            end != null ? end.format(displayFmt) : "any time");
        updateResultLabel(label, filtered.size());
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

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Date Time" }));
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
        searchButtonActionPerformed(evt);
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
            case "Date Time": {
                LinkedHashMap<String, String> range = selectDateTimeRange();
                if (range.get("start") != null || range.get("end") != null) {
                    filterByDateTimeRange(range.get("start"), range.get("end"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
            default: {
                List<Consultation> allConsultations = getCompletedConsultations();
                currentConsultations = allConsultations;
                populateTable(allConsultations);
                updateResultLabel("Showing " + allConsultations.size() + " consultation(s)", allConsultations.size());
            }
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
