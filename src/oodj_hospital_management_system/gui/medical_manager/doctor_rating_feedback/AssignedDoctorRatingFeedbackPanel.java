/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.medical_manager.doctor_rating_feedback;

import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import oodj_hospital_management_system.gui.medical_manager.clinical_departments.*;
import java.util.ArrayList;
import java.util.Arrays;
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
import oodj_hospital_management_system.gui.utils.WrappingCellRenderer;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.Feedback;
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.MedicalManager;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class AssignedDoctorRatingFeedbackPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<Feedback> currentFeedback;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form adminDashboardPanel
     */
    public AssignedDoctorRatingFeedbackPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        loadFeedback();
        customStyleHeader();
        ratingTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, ratingTable.getRowHeight() * ratingTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        ratingTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        ratingTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
        for (int i = 0; i < ratingTable.getColumnCount(); i++) {
            ratingTable.getColumnModel().getColumn(i).setCellRenderer(new WrappingCellRenderer());
        }
    }
 
    /**
     * Resolves the Doctor tied to a Feedback record, by following
     * Feedback -> Consultation -> ConsultationSlot -> DoctorShift -> Doctor.
     */
    private Doctor resolveDoctorForFeedback(Feedback fb) {
        if (fb == null || fb.getConsultationId() == null) return null;
        Consultation c = usrCtrl.getConsultationController().getConsultationById(fb.getConsultationId());
        if (c == null || c.getConsultationSlot() == null) return null;
        if (c.getConsultationSlot().getDoctorShift() == null) return null;
        return c.getConsultationSlot().getDoctorShift().getDoctor();
    }
 
    /**
     * All feedback belonging to consultations handled by doctors assigned
     * to the signed-in medical manager.
     */
    private List<Feedback> getBaseFeedback() {
        MedicalManager activeManager = (MedicalManager) usrCtrl.getActiveUser();
        String managerId = activeManager != null ? activeManager.getId() : null;
 
        List<Feedback> filtered = new ArrayList<>();
        if (managerId == null) return filtered;
 
        // Snapshot the list before iterating — getAllFeedback() can be backed by a
        // live collection that gets reloaded/mutated elsewhere mid-iteration,
        // which throws ConcurrentModificationException.
        List<Feedback> allFeedbackSnapshot = new ArrayList<>(usrCtrl.getConsultationController().getAllFeedback());
        for (Feedback fb : allFeedbackSnapshot) {
            Doctor doctor = resolveDoctorForFeedback(fb);
            if (doctor != null && doctor.getMedicalManager() != null
                    && managerId.equals(doctor.getMedicalManager().getId())) {
                filtered.add(fb);
            }
        }
        return filtered;
    }
 
    public void loadFeedback() {
        List<Feedback> all = getBaseFeedback();
        currentFeedback = all;
        populateTable(all);
    }
 
    private String formatUserLabel(String id, String name) {
        if (id == null || id.isEmpty()) return "-";
        return id + " - " + (name != null && !name.isEmpty() ? name : "Unknown");
    }
 
    private void populateTable(List<Feedback> feedbackList) {
        DefaultTableModel model = (DefaultTableModel) ratingTable.getModel();
        model.setRowCount(0);
 
        for (Feedback fb : feedbackList) {
            Doctor doctor = resolveDoctorForFeedback(fb);
            Patient patient = fb.getPatient();
 
            String doctorLabel = doctor != null ? formatUserLabel(doctor.getId(), doctor.getName()) : "-";
            String patientLabel = patient != null ? formatUserLabel(patient.getId(), patient.getName()) : "-";
            String comment = fb.getComment() != null && !fb.getComment().isEmpty() ? fb.getComment() : "-";
            String submittedDt = fb.getSubmittedDateTime() != null && !fb.getSubmittedDateTime().isEmpty()
                ? fb.getSubmittedDateTime() : "-";
 
            model.addRow(new String[]{
                fb.getFeedbackId(),
                doctorLabel,
                patientLabel,
                String.valueOf(fb.getRating()),
                comment,
                submittedDt
            });
        }
    }
 
    private Feedback getSelectedFeedback() {
        int viewRow = ratingTable.getSelectedRow();
        if (viewRow == -1) return null;
        int modelRow = ratingTable.convertRowIndexToModel(viewRow);
        String feedbackId = (String) ratingTable.getModel().getValueAt(modelRow, 0);
        for (Feedback fb : currentFeedback) {
            if (fb.getFeedbackId().equals(feedbackId)) return fb;
        }
        return null;
    }
 
    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }
 
    // === Filter by Doctor — dropdown built from this manager's own feedback data ===
    private String selectDoctor() {
        LinkedHashMap<String, String> labelToId = new LinkedHashMap<>();
        for (Feedback fb : getBaseFeedback()) {
            Doctor doctor = resolveDoctorForFeedback(fb);
            if (doctor == null || doctor.getId() == null || doctor.getId().isEmpty()) continue;
            if (labelToId.containsValue(doctor.getId())) continue;
            labelToId.put(formatUserLabel(doctor.getId(), doctor.getName()), doctor.getId());
        }
 
        if (labelToId.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No doctors found.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
 
        javax.swing.JComboBox<String> doctorCombo = new javax.swing.JComboBox<>(labelToId.keySet().toArray(new String[0]));
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Doctor:"));
        panel.add(doctorCombo);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Doctor", JOptionPane.OK_CANCEL_OPTION);
 
        if (result != JOptionPane.OK_OPTION) return null;
        return labelToId.get((String) doctorCombo.getSelectedItem());
    }
 
    private void filterByDoctor(String doctorId) {
        List<Feedback> filtered = new ArrayList<>();
        String doctorName = doctorId;
        for (Feedback fb : getBaseFeedback()) {
            Doctor doctor = resolveDoctorForFeedback(fb);
            if (doctor != null && doctorId.equals(doctor.getId())) {
                filtered.add(fb);
                doctorName = doctor.getName();
            }
        }
        currentFeedback = filtered;
        populateTable(filtered);
        updateResultLabel("Showing feedback for doctor \"" + doctorName + "\"", filtered.size());
    }
 
    // === Filter by Patient — dropdown built from this manager's own feedback data ===
    private String selectPatient() {
        LinkedHashMap<String, String> labelToId = new LinkedHashMap<>();
        for (Feedback fb : getBaseFeedback()) {
            Patient patient = fb.getPatient();
            if (patient == null || patient.getId() == null || patient.getId().isEmpty()) continue;
            if (labelToId.containsValue(patient.getId())) continue;
            labelToId.put(formatUserLabel(patient.getId(), patient.getName()), patient.getId());
        }
 
        if (labelToId.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No patients found.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
 
        javax.swing.JComboBox<String> patientCombo = new javax.swing.JComboBox<>(labelToId.keySet().toArray(new String[0]));
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Patient:"));
        panel.add(patientCombo);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Patient", JOptionPane.OK_CANCEL_OPTION);
 
        if (result != JOptionPane.OK_OPTION) return null;
        return labelToId.get((String) patientCombo.getSelectedItem());
    }
 
    private void filterByPatient(String patientId) {
        List<Feedback> filtered = new ArrayList<>();
        String patientName = patientId;
        for (Feedback fb : getBaseFeedback()) {
            Patient patient = fb.getPatient();
            if (patient != null && patientId.equals(patient.getId())) {
                filtered.add(fb);
                patientName = patient.getName();
            }
        }
        currentFeedback = filtered;
        populateTable(filtered);
        updateResultLabel("Showing feedback from patient \"" + patientName + "\"", filtered.size());
    }
 
    // === Filter by Rating ===
    private Integer selectRating() {
        Integer[] ratings = {1, 2, 3, 4, 5};
        javax.swing.JComboBox<Integer> ratingCombo = new javax.swing.JComboBox<>(ratings);
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Rating:"));
        panel.add(ratingCombo);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Rating", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return null;
        return (Integer) ratingCombo.getSelectedItem();
    }
 
    private void filterByRating(int rating) {
        List<Feedback> filtered = new ArrayList<>();
        for (Feedback fb : getBaseFeedback()) {
            if (fb.getRating() == rating) filtered.add(fb);
        }
        currentFeedback = filtered;
        populateTable(filtered);
        updateResultLabel("Showing feedback with rating \"" + rating + "\"", filtered.size());
    }
 
    // === Filter by Date (submittedDateTime) — uses JDateChooser ===
    private LinkedHashMap<String, String> selectDateRange() {
        LinkedHashMap<String, String> dateRange = new LinkedHashMap<>();
        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();
        Date currentDate = new Date();
        startDateChooser.setMaxSelectableDate(currentDate);
        endDateChooser.setMaxSelectableDate(currentDate);
 
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.add(new JLabel("From:"));
        panel.add(startDateChooser);
        panel.add(new JLabel("To:"));
        panel.add(endDateChooser);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Select Date Range", JOptionPane.OK_CANCEL_OPTION);
 
        if (result == JOptionPane.OK_OPTION) {
            Date startDateRaw = startDateChooser.getDate();
            Date endDateRaw = endDateChooser.getDate();
 
            if (startDateRaw == null && endDateRaw == null) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Please select at least one date", "Missing Date", JOptionPane.INFORMATION_MESSAGE);
                return dateRange;
            }
 
            if (startDateRaw != null && endDateRaw != null && endDateRaw.compareTo(startDateRaw) < 0) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Invalid date range", "WARNING", JOptionPane.ERROR_MESSAGE);
                return dateRange;
            }
 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateRange.put("startDate", startDateRaw != null ? sdf.format(startDateRaw) : null);
            dateRange.put("endDate", endDateRaw != null ? sdf.format(endDateRaw) : null);
        }
        return dateRange;
    }
 
    private void filterByDateTimeRange(String start, String end) {
        List<Feedback> filtered = new ArrayList<>();
        for (Feedback fb : getBaseFeedback()) {
            if (fb.getSubmittedDateTime() == null || fb.getSubmittedDateTime().isEmpty()) continue;
            LocalDate date = DateTime.toDateTimeObject(fb.getSubmittedDateTime()).toLocalDate();
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(fb);
        }
        currentFeedback = filtered;
        populateTable(filtered);
 
        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing feedback submitted", start, end, display);
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
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
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
        filterByField = new javax.swing.JComboBox<>();
        wrapperPane = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        ratingTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();

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

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setBackground(new java.awt.Color(246, 251, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Rating and Feedback");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel1.setBackground(new java.awt.Color(246, 251, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("View patient ratings and feedback for assigned doctors to monitor service quality and support performance evaluation.");
        jLabel1.setMaximumSize(new java.awt.Dimension(946, 45));
        jLabel1.setPreferredSize(new java.awt.Dimension(946, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel1, gridBagConstraints);

        jPanel5.add(jPanel1, java.awt.BorderLayout.NORTH);

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Slot ID", "Patient", "Doctor", "Rating" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Patient", "Doctor", "Rating", "Date" }));
        filterByField.setPreferredSize(new java.awt.Dimension(150, 30));
        filterByField.setRequestFocusEnabled(false);
        filterByField.addActionListener(this::filterByFieldActionPerformed);
        sortByPane.add(filterByField);

        jPanel2.add(sortByPane, java.awt.BorderLayout.LINE_END);

        jPanel7.add(jPanel2, java.awt.BorderLayout.NORTH);

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.LINE_AXIS));

        jPanel3.setBackground(new java.awt.Color(246, 251, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 40));
        jPanel3.setAlignmentY(0.0F);
        jPanel3.setLayout(new java.awt.BorderLayout());

        tableScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        tableScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        ratingTable.setBackground(new java.awt.Color(255, 255, 255));
        ratingTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Slot ID", "Doctor", "Patient", "Rating", "Feedback", "Date Time"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ratingTable.setAlignmentY(0.0F);
        ratingTable.setRowHeight(40);
        ratingTable.setRowMargin(5);
        tableScrollPane.setViewportView(ratingTable);

        jPanel3.add(tableScrollPane, java.awt.BorderLayout.CENTER);

        showResultsForPanel.setBackground(new java.awt.Color(246, 251, 255));
        showResultsForPanel.setAlignmentY(0.0F);
        showResultsForPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        showResultsForPanel.add(showResultsForLabel);

        jPanel3.add(showResultsForPanel, java.awt.BorderLayout.NORTH);

        wrapperPane.add(jPanel3);

        jPanel7.add(wrapperPane, java.awt.BorderLayout.CENTER);

        usrMngContentArea.add(jPanel7, java.awt.BorderLayout.CENTER);

        dashboardPanel.add(usrMngContentArea, java.awt.BorderLayout.CENTER);

        jScrollPane2.setViewportView(dashboardPanel);

        add(jScrollPane2);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        searchButtonActionPerformed(evt);
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String rawKeyword = searchField.getText();
        String keyword = rawKeyword.trim().toLowerCase();
 
        List<Feedback> results = new ArrayList<>();
        for (Feedback fb : getBaseFeedback()) {
            Doctor doctor = resolveDoctorForFeedback(fb);
            Patient patient = fb.getPatient();
 
            boolean match = fb.getFeedbackId().toLowerCase().contains(keyword)
                || (doctor != null && (doctor.getName().toLowerCase().contains(keyword) || doctor.getId().toLowerCase().contains(keyword)))
                || (patient != null && (patient.getName().toLowerCase().contains(keyword) || patient.getId().toLowerCase().contains(keyword)))
                || (fb.getComment() != null && fb.getComment().toLowerCase().contains(keyword))
                || String.valueOf(fb.getRating()).equals(keyword);
            if (match) results.add(fb);
        }
 
        currentFeedback = results;
        populateTable(results);
        updateResultLabel("Showing results for \"" + rawKeyword + "\"", results.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadFeedback();
        searchField.setText("");
        showResultsForLabel.setText("");
        ratingTable.setRowSorter(null);
        sortField.setSelectedIndex(0);
        filterByField.setSelectedIndex(0);
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        if (currentFeedback == null) return;
        String selected = (String) sortField.getSelectedItem();
        List<Feedback> sorted = new ArrayList<>(currentFeedback);
 
        switch (selected) {
            case "Feedback ID":
                sorted.sort((a, b) -> a.getFeedbackId().compareToIgnoreCase(b.getFeedbackId()));
                break;
 
            case "Doctor":
                sorted.sort((a, b) -> {
                    Doctor da = resolveDoctorForFeedback(a);
                    Doctor db = resolveDoctorForFeedback(b);
                    String na = da != null ? da.getName() : "";
                    String nb = db != null ? db.getName() : "";
                    return na.compareToIgnoreCase(nb);
                });
                break;
 
            case "Patient":
                sorted.sort((a, b) -> {
                    String na = a.getPatient() != null ? a.getPatient().getName() : "";
                    String nb = b.getPatient() != null ? b.getPatient().getName() : "";
                    return na.compareToIgnoreCase(nb);
                });
                break;
 
            case "Rating":
                sorted.sort((a, b) -> Integer.compare(a.getRating(), b.getRating()));
                break;
 
            case "Date":
                sorted.sort((a, b) -> {
                    boolean aEmpty = a.getSubmittedDateTime() == null || a.getSubmittedDateTime().isEmpty();
                    boolean bEmpty = b.getSubmittedDateTime() == null || b.getSubmittedDateTime().isEmpty();
                    if (aEmpty && bEmpty) return 0;
                    if (aEmpty) return 1;
                    if (bEmpty) return -1;
                    return DateTime.toDateTimeObject(a.getSubmittedDateTime())
                        .compareTo(DateTime.toDateTimeObject(b.getSubmittedDateTime()));
                });
                break;
 
            default:
                sorted.sort((a, b) -> a.getFeedbackId().compareToIgnoreCase(b.getFeedbackId()));
        }
 
        currentFeedback = sorted;
        populateTable(sorted);
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();
 
        switch (selected) {
            case "Doctor": {
                String doctorId = selectDoctor();
                if (doctorId != null) {
                    filterByDoctor(doctorId);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Patient": {
                String patientId = selectPatient();
                if (patientId != null) {
                    filterByPatient(patientId);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Rating": {
                Integer rating = selectRating();
                if (rating != null) {
                    filterByRating(rating);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Date": {
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByDateTimeRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            default: {
                List<Feedback> base = getBaseFeedback();
                currentFeedback = base;
                populateTable(base);
                updateResultLabel("Showing " + base.size() + " feedback record(s)", base.size());
            }
        }
    }//GEN-LAST:event_filterByFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable ratingTable;
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
    private javax.swing.JPanel usrMngContentArea;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
