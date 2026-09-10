/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.doctor.lab_services;

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
import oodj_hospital_management_system.controllers.ConsultationController;
import oodj_hospital_management_system.controllers.HospitalAssetController;
import oodj_hospital_management_system.controllers.RequestController;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.utils.WrappingCellRenderer;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.Request.RequestStatus;
import oodj_hospital_management_system.models.Request.RequestType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class ScheduledPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private HospitalAssetController assetCtrl;
    private List<HospitalAssetAllocation> currentAllocations;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form Admin StaffManagement
     */
    public ScheduledPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        this.assetCtrl = new HospitalAssetController(usrCtrl.getActiveUser());
        initComponents();
        loadDoctorAllocations();
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
 
    public void loadDoctorAllocations() {
        List<HospitalAssetAllocation> myAllocations = getBaseAllocations();
        currentAllocations = myAllocations;
        populateAllocationTable(myAllocations);
        updateResultLabel("Showing " + myAllocations.size() + " allocation(s)", myAllocations.size());
    }

    private void populateAllocationTable(List<HospitalAssetAllocation> allocations) {
        DefaultTableModel model = (DefaultTableModel) requestTable.getModel();
        model.setRowCount(0);

        for (HospitalAssetAllocation alloc : allocations) {
            HospitalAsset asset = assetCtrl.getHospitalAssetById(alloc.getAssetId());
            String assetName = asset != null ? asset.getAssetName() : "-";

            model.addRow(new String[]{
                alloc.getAllocationId(),
                assetName,
                formatDateTimeOrDash(alloc.getScheduledStartDateTime()),
                formatDateTimeOrDash(alloc.getScheduledEndDateTime()),
                formatDateTimeOrDash(alloc.getAllocationDateTime()),
                formatDateTimeOrDash(alloc.getReleasedDateTime()),
                getAllocationStatus(alloc)
            });
        }
    }

    private String formatDateTimeOrDash(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) return "-";
        LocalDateTime dt = DateTime.toDateTimeObject(dateTime);
        return DateTime.toDateStringWords(dt.toLocalDate()) + ", "
            + dt.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
    }

    /**
    * "Scheduled" = not yet started, still within scheduled end time.
    * "Overdue" = not yet started, scheduled end time has already passed.
    * "In Progress" = actual start recorded, not yet released, still within scheduled end.
    * "Overrunning" = actual start recorded, not yet released, scheduled end has already passed.
    * "Completed" = released.
    */
   private String getAllocationStatus(HospitalAssetAllocation alloc) {
       if (alloc.getReleasedDateTime() != null && !alloc.getReleasedDateTime().isEmpty()) {
           return "Completed";
       }

       boolean pastScheduledEnd = false;
       if (alloc.getScheduledEndDateTime() != null && !alloc.getScheduledEndDateTime().isEmpty()) {
           LocalDateTime scheduledEnd = DateTime.toDateTimeObject(alloc.getScheduledEndDateTime());
           pastScheduledEnd = scheduledEnd.isBefore(DateTime.currentDateTime());
       }

       if (alloc.getAllocationDateTime() != null && !alloc.getAllocationDateTime().isEmpty()) {
           return pastScheduledEnd ? "Overrunning" : "In Progress";
       }

       return pastScheduledEnd ? "Overdue" : "Scheduled";
   }

    /**
     * Allocations belonging to the signed-in doctor, resolved via their consultations
     * (HospitalAssetAllocation only stores a consultationId, not a doctorId).
     * Wards are excluded — bed/ward usage is tracked via Ward Admission instead.
     */
    private List<HospitalAssetAllocation> getBaseAllocations() {
        List<HospitalAssetAllocation> myAllocations = new ArrayList<>();
        User activeUser = usrCtrl.getActiveUser();

        if (!(activeUser instanceof Doctor)) {
            return myAllocations;
        }
        String doctorId = activeUser.getId();

        for (HospitalAssetAllocation alloc : assetCtrl.getAllHospitalAssetAllocations()) {
            if (!doctorId.equals(alloc.getDoctorId())) continue;

            HospitalAsset asset = assetCtrl.getHospitalAssetById(alloc.getAssetId());
            if (asset instanceof Ward) continue; // wards handled via Ward Admission

            myAllocations.add(alloc);
        }
        return myAllocations;
    }

    private HospitalAssetAllocation getSelectedAllocation() {
        int viewRow = requestTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select an allocation first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = requestTable.convertRowIndexToModel(viewRow);
        String allocationId = (String) requestTable.getModel().getValueAt(modelRow, 0);
        for (HospitalAssetAllocation a : currentAllocations) {
            if (a.getAllocationId().equals(allocationId)) return a;
        }
        return null;
    }

    private String selectAllocationStatus() {
        String[] statuses = {"Scheduled", "Overdue", "In Progress", "Completed"};
        javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(statuses);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Status", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return null;
        return (String) statusCombo.getSelectedItem();
    }

    private void filterByStatus(String status) {
        List<HospitalAssetAllocation> filtered = new ArrayList<>();
        for (HospitalAssetAllocation alloc : currentAllocations) {
            if (status.equals(getAllocationStatus(alloc))) {
                filtered.add(alloc);
            }
        }
        currentAllocations = filtered;
        populateAllocationTable(filtered);
        updateResultLabel("Showing allocations with status \"" + status + "\"", filtered.size());
    }

    private String selectAssetKeyword() {
        javax.swing.JTextField assetField = new javax.swing.JTextField();

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Asset name/ID:"));
        panel.add(assetField);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Asset", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return null;
        String keyword = assetField.getText().trim();
        return keyword.isEmpty() ? null : keyword;
    }

    private void filterByAsset(String keyword) {
        String lower = keyword.toLowerCase();
        List<HospitalAssetAllocation> filtered = new ArrayList<>();
        for (HospitalAssetAllocation alloc : currentAllocations) {
            HospitalAsset asset = assetCtrl.getHospitalAssetById(alloc.getAssetId());
            if (asset != null &&
                (asset.getAssetName().toLowerCase().contains(lower)
                    || asset.getAssetId().toLowerCase().contains(lower))) {
                filtered.add(alloc);
            }
        }
        currentAllocations = filtered;
        populateAllocationTable(filtered);
        updateResultLabel("Showing allocations for asset \"" + keyword + "\"", filtered.size());
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

    private void filterByScheduledDateRange(String start, String end) {
        List<HospitalAssetAllocation> filtered = new ArrayList<>();
        for (HospitalAssetAllocation alloc : currentAllocations) {
            if (alloc.getScheduledStartDateTime() == null || alloc.getScheduledStartDateTime().isEmpty()) continue;
            LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(alloc.getScheduledStartDateTime()));
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(alloc);
        }
        currentAllocations = filtered;
        populateAllocationTable(filtered);

        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing allocations scheduled", start, end, display);
        updateResultLabel(label, filtered.size());
    }

    private void filterByActualDateRange(String start, String end) {
        List<HospitalAssetAllocation> filtered = new ArrayList<>();
        for (HospitalAssetAllocation alloc : currentAllocations) {
            if (alloc.getAllocationDateTime() == null || alloc.getAllocationDateTime().isEmpty()) continue;
            LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(alloc.getAllocationDateTime()));
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(alloc);
        }
        currentAllocations = filtered;
        populateAllocationTable(filtered);

        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing allocations started", start, end, display);
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
        startButton = new javax.swing.JButton();
        endButton = new javax.swing.JButton();

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

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Scheduled Date Time", "Actual Date Time", "Status", "Asset" }));
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

        requestTable.setBackground(new java.awt.Color(255, 255, 255));
        requestTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Allocation ID", "Asset", "Scheduled Start", "Schedule End", "Actual Start", "Actual End", "Status"
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

        startButton.setText("Session Start");
        startButton.setMaximumSize(new java.awt.Dimension(150, 40));
        startButton.setPreferredSize(new java.awt.Dimension(150, 40));
        startButton.addActionListener(this::startButtonActionPerformed);
        jPanel3.add(startButton);

        endButton.setText("Session End");
        endButton.setMaximumSize(new java.awt.Dimension(150, 40));
        endButton.setPreferredSize(new java.awt.Dimension(150, 40));
        endButton.addActionListener(this::endButtonActionPerformed);
        jPanel3.add(endButton);

        operationsPanel1.add(jPanel3);

        wrapperPane.add(operationsPanel1);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        performSearch();
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        performSearch();
    }//GEN-LAST:event_searchButtonActionPerformed
    
    private void performSearch() {
        String rawKeyword = searchField.getText();
        String keyword = rawKeyword.trim().toLowerCase();

        List<HospitalAssetAllocation> results = new ArrayList<>();
        for (HospitalAssetAllocation alloc : getBaseAllocations()) {
            HospitalAsset asset = assetCtrl.getHospitalAssetById(alloc.getAssetId());
            String assetName = asset != null ? asset.getAssetName() : "";

            boolean match = alloc.getAllocationId().toLowerCase().contains(keyword)
                            || alloc.getDoctorId().toLowerCase().contains(keyword)
                            || assetName.toLowerCase().contains(keyword)
                            || getAllocationStatus(alloc).toLowerCase().contains(keyword);
        }

        currentAllocations = results;
        populateAllocationTable(results);
        updateResultLabel("Showing results for \"" + rawKeyword + "\"", results.size());
    }
    
    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        if (currentAllocations == null) return;
        String selected = (String) sortField.getSelectedItem();
        List<HospitalAssetAllocation> sorted = new ArrayList<>(currentAllocations);

        switch (selected) {
            case "ID":
                sorted.sort((a, b) -> a.getAllocationId().compareToIgnoreCase(b.getAllocationId()));
                break;

            case "Scheduled Date Time":
                sorted.sort((a, b) -> {
                    boolean aEmpty = a.getScheduledStartDateTime() == null || a.getScheduledStartDateTime().isEmpty();
                    boolean bEmpty = b.getScheduledStartDateTime() == null || b.getScheduledStartDateTime().isEmpty();
                    if (aEmpty && bEmpty) return 0;
                    if (aEmpty) return 1;   // unscheduled allocations sink to the bottom
                    if (bEmpty) return -1;
                    return DateTime.toDateTimeObject(a.getScheduledStartDateTime())
                        .compareTo(DateTime.toDateTimeObject(b.getScheduledStartDateTime()));
                });
                break;

            case "Actual Date Time":
                sorted.sort((a, b) -> {
                    boolean aEmpty = a.getAllocationDateTime() == null || a.getAllocationDateTime().isEmpty();
                    boolean bEmpty = b.getAllocationDateTime() == null || b.getAllocationDateTime().isEmpty();
                    if (aEmpty && bEmpty) return 0;
                    if (aEmpty) return 1;   // not-yet-started allocations sink to the bottom
                    if (bEmpty) return -1;
                    return DateTime.toDateTimeObject(a.getAllocationDateTime())
                        .compareTo(DateTime.toDateTimeObject(b.getAllocationDateTime()));
                });
                break;

            default: // "Default" -> fall back to allocation ID
                sorted.sort((a, b) -> a.getAllocationId().compareToIgnoreCase(b.getAllocationId()));
        }

        currentAllocations = sorted;
        populateAllocationTable(sorted);
    }//GEN-LAST:event_sortFieldActionPerformed
    
    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();

        List<HospitalAssetAllocation> base = (currentAllocations != null) ? currentAllocations : getBaseAllocations();

        switch (selected) {
            case "Scheduled Date Time": {
                currentAllocations = base;
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByScheduledDateRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Actual Date Time": {
                currentAllocations = base;
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByActualDateRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Status": {
                currentAllocations = base;
                String status = selectAllocationStatus();
                if (status != null) {
                    filterByStatus(status);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Asset": {
                currentAllocations = base;
                String keyword = selectAssetKeyword();
                if (keyword != null) {
                    filterByAsset(keyword);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            default: // "Default" -> no filter
                currentAllocations = base;
                populateAllocationTable(base);
                updateResultLabel("Showing " + base.size() + " allocation(s)", base.size());
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadDoctorAllocations();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        HospitalAssetAllocation selected = getSelectedAllocation();
        if (selected == null) return;

        if (selected.getReleasedDateTime() != null && !selected.getReleasedDateTime().isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "This allocation has already ended and cannot be started again.",
                "Cannot Start", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selected.getAllocationDateTime() != null && !selected.getAllocationDateTime().isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "This allocation has already been started.",
                "Cannot Start", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Mark this allocation as started now?", "Confirm Start", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        LinkedHashMap<String, String> result = assetCtrl.startHospitalAssetAllocation(selected.getAllocationId(), null);
        JOptionPane.showMessageDialog(parentWindow, result.get("msg"),
            result.get("success") != null ? "Success" : "Error",
            result.get("success") != null ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        loadDoctorAllocations();
    }

    private void endButtonActionPerformed() {
        
    }//GEN-LAST:event_startButtonActionPerformed

    private void endButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endButtonActionPerformed
        HospitalAssetAllocation selected = getSelectedAllocation();
        if (selected == null) return;

        if (selected.getReleasedDateTime() != null && !selected.getReleasedDateTime().isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "This allocation has already ended.",
                "Cannot End", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selected.getAllocationDateTime() == null || selected.getAllocationDateTime().isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "This allocation has not been started yet and cannot be ended.",
                "Cannot End", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Mark this allocation as ended now?", "Confirm End", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        LinkedHashMap<String, String> result = assetCtrl.releaseHospitalAssetAllocation(selected.getAllocationId());
        JOptionPane.showMessageDialog(parentWindow, result.get("msg"),
            result.get("success") != null ? "Success" : "Error",
            result.get("success") != null ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        loadDoctorAllocations();
    }//GEN-LAST:event_endButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton endButton;
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
    private javax.swing.JButton startButton;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
