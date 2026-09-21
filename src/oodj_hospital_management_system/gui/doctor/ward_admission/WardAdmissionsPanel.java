/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.doctor.ward_admission;

import oodj_hospital_management_system.gui.admin.hospitalAssets.*;
import oodj_hospital_management_system.gui.doctor.lab_services.*;
import oodj_hospital_management_system.gui.doctor.shift_roster.*;
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
import oodj_hospital_management_system.controllers.HospitalAssetController;
import oodj_hospital_management_system.controllers.RequestController;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.controllers.WardAdmissionController;
import oodj_hospital_management_system.gui.utils.WrappingCellRenderer;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.Request.RequestStatus;
import oodj_hospital_management_system.models.Request.RequestType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.models.WardAdmission;
import oodj_hospital_management_system.models.WardAdmission.AdmissionStatus;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class WardAdmissionsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private WardAdmissionController admissionCtrl;
    private HospitalAssetController assetCtrl;
    private List<WardAdmission> currentAdmissions;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private javax.swing.JPanel cardContainer;

    /**
     * Creates new form Admin StaffManagement
     */
    public WardAdmissionsPanel(UserController usrCtrl, javax.swing.JPanel cardContainer) {
        this.usrCtrl = usrCtrl;
        this.cardContainer = cardContainer;
        this.admissionCtrl = new WardAdmissionController(usrCtrl.getActiveUser());
        this.assetCtrl = new HospitalAssetController(usrCtrl.getActiveUser());
        initComponents();
        loadAdmissions();
        customStyleHeader();
        requestTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, requestTable.getRowHeight() * requestTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        requestTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        requestTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
 
        for (int i = 0; i < requestTable.getColumnCount(); i++) {
            requestTable.getColumnModel().getColumn(i).setCellRenderer(new WrappingCellRenderer());
        }
    }
 
    public void loadAdmissions() {
        List<WardAdmission> all = getBaseAdmissions();
        currentAdmissions = all;
        populateRequestTable(all);
    }
 
    private String resolveUserName(String userId) {
        if (userId == null || userId.isEmpty()) return "-";
        User u = UserManagement.getUserById(userId);
        return u != null ? u.getName() : userId;
    }
 
    private String resolveAssetName(String wardId) {
        HospitalAsset asset = assetCtrl.getHospitalAssetById(wardId);
        return asset != null ? asset.getAssetName() : "-";
    }
 
    /**
     * Columns: Admission ID, Patient, Ward, Admitted Date Time, Discharged Date Time,
     * Status, Reason
     */
    private void populateRequestTable(List<WardAdmission> admissions) {
        DefaultTableModel model = (DefaultTableModel) requestTable.getModel();
        model.setRowCount(0);
 
        for (WardAdmission adm : admissions) {
            String admittedDt = "-";
            if (adm.getAdmittedDateTime() != null && !adm.getAdmittedDateTime().isEmpty()) {
                admittedDt = DateTime.toDateStringWords(
                    DateTime.getDate(DateTime.toDateTimeObject(adm.getAdmittedDateTime())));
            }
 
            String dischargedDt = "-";
            if (adm.getDischargedDateTime() != null && !adm.getDischargedDateTime().isEmpty()) {
                dischargedDt = DateTime.toDateStringWords(
                    DateTime.getDate(DateTime.toDateTimeObject(adm.getDischargedDateTime())));
            }
 
            String patientName = resolveUserName(adm.getPatientId());
            String wardName = resolveAssetName(adm.getWardId());
            String reason = adm.getReason() != null && !adm.getReason().isEmpty() ? adm.getReason() : "-";
            String status = adm.getStatus() != null ? adm.getStatus().toString() : "-";
 
            model.addRow(new String[]{
                adm.getAdmissionId(), patientName, wardName, admittedDt, dischargedDt, status, reason
            });
        }
    }
 
    /**
     * Admissions belonging to the current doctor. The model only has
     * ADMITTED and DISCHARGED statuses now, so no extra status filtering
     * is needed here.
     */
    private List<WardAdmission> getBaseAdmissions() {
        String currentDoctorId = usrCtrl.getActiveUser().getId();
        List<WardAdmission> filtered = new ArrayList<>();
        for (WardAdmission adm : admissionCtrl.getAllWardAdmissions()) {
            if (currentDoctorId.equals(adm.getAdmittingDoctorId())) {
                filtered.add(adm);
            }
        }
        return filtered;
    }
 
    private WardAdmission getSelectedAdmission() {
        int viewRow = requestTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a request first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = requestTable.convertRowIndexToModel(viewRow);
        String admissionId = (String) requestTable.getModel().getValueAt(modelRow, 0);
        for (WardAdmission a : currentAdmissions) {
            if (a.getAdmissionId().equals(admissionId)) return a;
        }
        return null;
    }
 
    // === Status dropdown (scoped to ADMITTED/DISCHARGED only) ===
    private AdmissionStatus selectStatus() {
        AdmissionStatus[] statuses = { AdmissionStatus.ADMITTED, AdmissionStatus.DISCHARGED };
        javax.swing.JComboBox<AdmissionStatus> statusCombo = new javax.swing.JComboBox<>(statuses);
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Status", JOptionPane.OK_CANCEL_OPTION);
 
        if (result != JOptionPane.OK_OPTION) return null;
        return (AdmissionStatus) statusCombo.getSelectedItem();
    }
 
    private void filterByStatus(AdmissionStatus status) {
        List<WardAdmission> filtered = new ArrayList<>();
        for (WardAdmission adm : getBaseAdmissions()) {
            if (status.equals(adm.getStatus())) {
                filtered.add(adm);
            }
        }
        currentAdmissions = filtered;
        populateRequestTable(filtered);
        updateResultLabel("Showing requests with status \"" + status + "\"", filtered.size());
    }
 
    // === Patient dropdown, built dynamically from the actual base data ===
    private String selectPatient() {
        LinkedHashMap<String, String> labelToId = new LinkedHashMap<>();
        for (WardAdmission adm : getBaseAdmissions()) {
            String patientId = adm.getPatientId();
            if (patientId == null || patientId.isEmpty() || labelToId.containsValue(patientId)) continue;
            labelToId.put(resolveUserName(patientId) + " (" + patientId + ")", patientId);
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
        List<WardAdmission> filtered = new ArrayList<>();
        for (WardAdmission adm : getBaseAdmissions()) {
            if (patientId.equals(adm.getPatientId())) {
                filtered.add(adm);
            }
        }
        currentAdmissions = filtered;
        populateRequestTable(filtered);
        updateResultLabel("Showing requests for patient \"" + resolveUserName(patientId) + "\"", filtered.size());
    }
 
    // === Ward dropdown, built dynamically from the actual base data ===
    private String selectWard() {
        LinkedHashMap<String, String> labelToId = new LinkedHashMap<>();
        for (WardAdmission adm : getBaseAdmissions()) {
            String wardId = adm.getWardId();
            if (wardId == null || wardId.isEmpty() || labelToId.containsValue(wardId)) continue;
            labelToId.put(resolveAssetName(wardId) + " (" + wardId + ")", wardId);
        }
 
        if (labelToId.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No wards found.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
 
        javax.swing.JComboBox<String> wardCombo = new javax.swing.JComboBox<>(labelToId.keySet().toArray(new String[0]));
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Ward:"));
        panel.add(wardCombo);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Ward", JOptionPane.OK_CANCEL_OPTION);
 
        if (result != JOptionPane.OK_OPTION) return null;
        return labelToId.get((String) wardCombo.getSelectedItem());
    }
 
    private void filterByWard(String wardId) {
        List<WardAdmission> filtered = new ArrayList<>();
        for (WardAdmission adm : getBaseAdmissions()) {
            if (wardId.equals(adm.getWardId())) {
                filtered.add(adm);
            }
        }
        currentAdmissions = filtered;
        populateRequestTable(filtered);
        updateResultLabel("Showing requests for ward \"" + resolveAssetName(wardId) + "\"", filtered.size());
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
 
    private void filterByAdmittedDateRange(String start, String end) {
        List<WardAdmission> filtered = new ArrayList<>();
        for (WardAdmission adm : getBaseAdmissions()) {
            if (adm.getAdmittedDateTime() == null || adm.getAdmittedDateTime().isEmpty()) continue;
            LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(adm.getAdmittedDateTime()));
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(adm);
        }
        currentAdmissions = filtered;
        populateRequestTable(filtered);
 
        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing admissions admitted", start, end, display);
        updateResultLabel(label, filtered.size());
    }
 
    private void filterByDischargedDateRange(String start, String end) {
        List<WardAdmission> filtered = new ArrayList<>();
        for (WardAdmission adm : getBaseAdmissions()) {
            if (adm.getDischargedDateTime() == null || adm.getDischargedDateTime().isEmpty()) continue;
            LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(adm.getDischargedDateTime()));
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(adm);
        }
        currentAdmissions = filtered;
        populateRequestTable(filtered);
 
        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing admissions discharged", start, end, display);
        updateResultLabel(label, filtered.size());
    }
    
    private LinkedHashMap<String, String> getAvailableWardsMap() {
        LinkedHashMap<String, String> labelToId = new LinkedHashMap<>();
        for (HospitalAsset asset : assetCtrl.getAllHospitalAssets()) { // confirm method name on your controller
            if (!(asset instanceof Ward)) continue;

            // skip wards not currently usable
            if (!HospitalAsset.STATUS_AVAILABLE.equals(asset.getStatus())) continue;

            String wardId = asset.getAssetId();
            int available = admissionCtrl.getAvailableBedCount(wardId);
            if (available > 0) {
                labelToId.put(asset.getAssetName() + " (" + available + " bed" + (available == 1 ? "" : "s") + " available)", wardId);
            }
        }
        return labelToId;
    }

    private String selectWardForAdmission() {
        LinkedHashMap<String, String> labelToId = getAvailableWardsMap();

        if (labelToId.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No wards currently have available beds.", "No Beds Available", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        javax.swing.JComboBox<String> wardCombo = new javax.swing.JComboBox<>(labelToId.keySet().toArray(new String[0]));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Ward:"));
        panel.add(wardCombo);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Select Ward", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return null;
        return labelToId.get((String) wardCombo.getSelectedItem());
    }
    
    private String selectPatientForAdmission() {
        LinkedHashMap<String, String> labelToId = new LinkedHashMap<>();
        for (User u : UserManagement.getAllUsers()) { // confirm this exists; filter condition below too
            if (!"Patient".equalsIgnoreCase(u.getType())) continue;
            labelToId.put(u.getName() + " (" + u.getId() + ")", u.getId());
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
            "Select Patient", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return null;
        return labelToId.get((String) patientCombo.getSelectedItem());
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
    
    private WardAdmission findOverlappingAdmission(String patientId, LocalDateTime newStart, LocalDateTime newEnd) {
        for (WardAdmission adm : admissionCtrl.getAllWardAdmissions()) {
            if (!patientId.equals(adm.getPatientId())) continue;
            if (adm.getAdmittedDateTime() == null || adm.getAdmittedDateTime().isEmpty()) continue;

            LocalDateTime existingStart = DateTime.toDateTimeObject(adm.getAdmittedDateTime());
            LocalDateTime existingEnd =
                (adm.getDischargedDateTime() == null || adm.getDischargedDateTime().isEmpty())
                    ? null
                    : DateTime.toDateTimeObject(adm.getDischargedDateTime());

            boolean newStartsBeforeExistingEnds = existingEnd == null || newStart.isBefore(existingEnd);
            boolean existingStartsBeforeNewEnds = newEnd == null || existingStart.isBefore(newEnd);

            if (newStartsBeforeExistingEnds && existingStartsBeforeNewEnds) return adm;
        }
        return null;
    }
    
    private void resetScrollToTop() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JScrollPane sp = (javax.swing.JScrollPane)
                javax.swing.SwingUtilities.getAncestorOfClass(javax.swing.JScrollPane.class, cardContainer);
            if (sp != null) {
                javax.swing.SwingUtilities.invokeLater(() ->
                    sp.getViewport().setViewPosition(new java.awt.Point(0, 0)));
            }
        });
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
        requestTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        admitButton = new javax.swing.JButton();
        dischargeButton = new javax.swing.JButton();

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Patient", "Ward", "Admitted Date Time", "Discharged Date Time" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Patient", "Ward", "Admitted Date Time", "Discharged Date Time", "Status" }));
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

        requestTable.setBackground(new java.awt.Color(255, 255, 255));
        requestTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Admission ID", "Patient", "Ward", "Admitted Date Time", "Discharged Date Time", "Status", "Reason"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        requestTable.setAlignmentY(0.0F);
        requestTable.setRowHeight(40);
        requestTable.setRowMargin(5);
        tableScrollPane.setViewportView(requestTable);

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

        admitButton.setText("Admit");
        admitButton.setMaximumSize(new java.awt.Dimension(150, 40));
        admitButton.setPreferredSize(new java.awt.Dimension(150, 40));
        admitButton.addActionListener(this::admitButtonActionPerformed);
        jPanel3.add(admitButton);

        dischargeButton.setText("Discharge");
        dischargeButton.setMaximumSize(new java.awt.Dimension(150, 40));
        dischargeButton.setPreferredSize(new java.awt.Dimension(150, 40));
        dischargeButton.addActionListener(this::dischargeButtonActionPerformed);
        jPanel3.add(dischargeButton);

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
 
        List<WardAdmission> results = new ArrayList<>();
        for (WardAdmission adm : getBaseAdmissions()) {
            String patientName = resolveUserName(adm.getPatientId());
            String wardName = resolveAssetName(adm.getWardId());
 
            boolean match = adm.getAdmissionId().toLowerCase().contains(keyword)
                || patientName.toLowerCase().contains(keyword)
                || wardName.toLowerCase().contains(keyword)
                || (adm.getReason() != null && adm.getReason().toLowerCase().contains(keyword))
                || adm.getStatus().toString().toLowerCase().contains(keyword);
            if (match) results.add(adm);
        }
 
        currentAdmissions = results;
        populateRequestTable(results);
        updateResultLabel("Showing results for \"" + rawKeyword + "\"", results.size());
        resetScrollToTop();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
 
        DefaultTableModel model = (DefaultTableModel) requestTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        requestTable.setRowSorter(sorter);
        int selectedIndex = sortField.getSelectedIndex();
        int columnToSortIndex;
        switch (selectedIndex) {
            case 0: columnToSortIndex = 3; break; // "Default" -> Admitted Date Time
            case 1: columnToSortIndex = 0; break; // "ID"
            case 2: columnToSortIndex = 1; break; // "Patient"
            case 3: columnToSortIndex = 2; break; // "Ward"
            case 4: columnToSortIndex = 3; break; // "Admitted Date Time"
            case 5: columnToSortIndex = 4; break; // "Discharged Date Time"
            default: columnToSortIndex = 3;
        }
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(columnToSortIndex, SortOrder.ASCENDING)));
        resetScrollToTop();
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();
 
        switch (selected) {
            case "Patient": {
                String patientId = selectPatient();
                if (patientId != null) {
                    filterByPatient(patientId);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Ward": {
                String wardId = selectWard();
                if (wardId != null) {
                    filterByWard(wardId);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Admitted Date Time": {
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByAdmittedDateRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Discharged Date Time": {
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByDischargedDateRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Status": {
                AdmissionStatus status = selectStatus();
                if (status != null) {
                    filterByStatus(status);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            default: { // "Default" -> no filter
                List<WardAdmission> base = getBaseAdmissions();
                currentAdmissions = base;
                populateRequestTable(base);
                updateResultLabel("Showing " + base.size() + " admission(s)", base.size());
            }
        }
        resetScrollToTop();
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        searchField.setText("");
        filterByField.setSelectedIndex(0);
        sortField.setSelectedIndex(0);
        requestTable.setRowSorter(null);
        showResultsForLabel.setText("");
        loadAdmissions();
        resetScrollToTop();
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void admitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_admitButtonActionPerformed
        String patientId = selectPatientForAdmission();
        if (patientId == null) return;
        
        WardAdmission conflict = findOverlappingAdmission(patientId, LocalDateTime.now(), null);
        if (conflict != null) {
            JOptionPane.showMessageDialog(parentWindow,
                resolveUserName(patientId) + " is already admitted to "
                    + resolveAssetName(conflict.getWardId()) + " (" + conflict.getAdmissionId() + ").\n"
                    + "Please discharge the patient before creating a new admission.",
                "Overlapping Admission", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String wardId = selectWardForAdmission();
        if (wardId == null) return;

        String reason = JOptionPane.showInputDialog(parentWindow,
            "Reason for admission:", "Admit Patient", JOptionPane.PLAIN_MESSAGE);
        if (reason == null) return; // cancelled

        String doctorId = usrCtrl.getActiveUser().getId();

        // passing null lets createWardAdmission default to "now"
        LinkedHashMap<String, String> resultMap =
            admissionCtrl.createWardAdmission(patientId, wardId, doctorId, null, reason);

        JOptionPane.showMessageDialog(parentWindow, resultMap.get("msg"),
            resultMap.get("success") != null ? "Success" : "Error",
            resultMap.get("success") != null ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        if (resultMap.get("success") != null) {
            loadAdmissions();
        }
    }//GEN-LAST:event_admitButtonActionPerformed

    private void dischargeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dischargeButtonActionPerformed
        WardAdmission selected = getSelectedAdmission();
        if (selected == null) return;
 
        if (!AdmissionStatus.ADMITTED.equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(parentWindow,
                "Only currently admitted patients can be discharged.",
                "Cannot Discharge", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Discharge this patient now?", "Confirm Discharge", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
 
        LinkedHashMap<String, String> resultMap = admissionCtrl.dischargeWardAdmission(selected.getAdmissionId(), null);
        JOptionPane.showMessageDialog(parentWindow, resultMap.get("msg"),
            resultMap.get("success") != null ? "Success" : "Error",
            resultMap.get("success") != null ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        loadAdmissions();
    }//GEN-LAST:event_dischargeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton admitButton;
    private javax.swing.JButton dischargeButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel operationsLabel1;
    private javax.swing.JPanel operationsPanel1;
    private javax.swing.JTable requestTable;
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
