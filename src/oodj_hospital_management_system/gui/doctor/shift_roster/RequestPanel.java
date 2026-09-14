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
import oodj_hospital_management_system.controllers.RequestController;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.utils.WrappingCellRenderer;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.Request;
import oodj_hospital_management_system.models.Request.RequestStatus;
import oodj_hospital_management_system.models.Request.RequestType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.DoctorManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class RequestPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private RequestController reqCtrl;
    private List<Request> currentRequests;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form Admin StaffManagement
     */
    public RequestPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        this.reqCtrl = new RequestController(usrCtrl.getActiveUser());
        initComponents();
        loadDoctorRequests();
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
 
    public void loadDoctorRequests() {
        List<Request> myRequests = getBaseRequests();
        currentRequests = myRequests;
        populateRequestTable(myRequests);
    }
 
    private void populateRequestTable(List<Request> requests) {
        DefaultTableModel model = (DefaultTableModel) requestTable.getModel();
        model.setRowCount(0);
 
        for (Request req : requests) {
            String createdDt = "-";
            if (req.getCreatedDateTime() != null && !req.getCreatedDateTime().isEmpty()) {
                createdDt = DateTime.toDateStringWords(
                    DateTime.getDate(DateTime.toDateTimeObject(req.getCreatedDateTime())));
            }
 
            String resolvedDt = "-";
            if (req.getResolvedDateTime() != null && !req.getResolvedDateTime().isEmpty()) {
                resolvedDt = DateTime.toDateStringWords(
                    DateTime.getDate(DateTime.toDateTimeObject(req.getResolvedDateTime())));
            }
 
            String status = req.getStatus() != null ? req.getStatus().toString() : "-";
            String resolver = req.getResolver() != null ? req.getResolver().getName() : "-";
 
            model.addRow(new String[]{
                req.getRequestId(), req.getReason(), createdDt, resolvedDt, status, resolver
            });
        }
    }
 
    private List<Request> getBaseRequests() {
        User activeUser = usrCtrl.getActiveUser();
        List<Request> myCancelRequests = new ArrayList<>();
        if (activeUser != null) {
            for (Request req : reqCtrl.getRequestsByRequester(activeUser)) {
                if (RequestType.CANCEL_SHIFT.equals(req.getRequestType())) {
                    myCancelRequests.add(req);
                }
            }
        }
        return myCancelRequests;
    }
 
    private Request getSelectedRequest() {
        int viewRow = requestTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a request first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = requestTable.convertRowIndexToModel(viewRow);
        String requestId = (String) requestTable.getModel().getValueAt(modelRow, 0);
        for (Request r : currentRequests) {
            if (r.getRequestId().equals(requestId)) return r;
        }
        return null;
    }
 
    private RequestStatus selectStatus() {
        javax.swing.JComboBox<RequestStatus> statusCombo =
            new javax.swing.JComboBox<>(RequestStatus.values());
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Status", JOptionPane.OK_CANCEL_OPTION);
 
        if (result != JOptionPane.OK_OPTION) return null;
        return (RequestStatus) statusCombo.getSelectedItem();
    }
 
    private void filterByStatus(RequestStatus status) {
        List<Request> filtered = new ArrayList<>();
        for (Request req : getBaseRequests()) {
            if (status.equals(req.getStatus())) {
                filtered.add(req);
            }
        }
        currentRequests = filtered;
        populateRequestTable(filtered);
        updateResultLabel("Showing requests with status \"" + status + "\"", filtered.size());
    }
 
    private String selectResolverKeyword() {
        javax.swing.JTextField resolverField = new javax.swing.JTextField();
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Resolver name/ID:"));
        panel.add(resolverField);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Resolver", JOptionPane.OK_CANCEL_OPTION);
 
        if (result != JOptionPane.OK_OPTION) return null;
        String keyword = resolverField.getText().trim();
        return keyword.isEmpty() ? null : keyword;
    }
 
    private void filterByResolver(String keyword) {
        String lower = keyword.toLowerCase();
        List<Request> filtered = new ArrayList<>();
        for (Request req : getBaseRequests()) {
            User resolver = req.getResolver();
            if (resolver != null &&
                (resolver.getName().toLowerCase().contains(lower)
                    || resolver.getId().toLowerCase().contains(lower))) {
                filtered.add(req);
            }
        }
        currentRequests = filtered;
        populateRequestTable(filtered);
        updateResultLabel("Showing requests resolved by \"" + keyword + "\"", filtered.size());
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
 
    private void filterByRequestedDateRange(String start, String end) {
        List<Request> filtered = new ArrayList<>();
        for (Request req : getBaseRequests()) {
            if (req.getCreatedDateTime() == null || req.getCreatedDateTime().isEmpty()) continue;
            LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(req.getCreatedDateTime()));
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(req);
        }
        currentRequests = filtered;
        populateRequestTable(filtered);

        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing requests made", start, end, display);
        updateResultLabel(label, filtered.size());
    }
 
    private void filterByResolvedDateRange(String start, String end) {
        List<Request> filtered = new ArrayList<>();
        for (Request req : getBaseRequests()) {
            if (req.getResolvedDateTime() == null || req.getResolvedDateTime().isEmpty()) continue;
            LocalDate date = DateTime.getDate(DateTime.toDateTimeObject(req.getResolvedDateTime()));
            boolean afterStart = start == null || !date.isBefore(LocalDate.parse(start));
            boolean beforeEnd = end == null || !date.isAfter(LocalDate.parse(end));
            if (afterStart && beforeEnd) filtered.add(req);
        }
        currentRequests = filtered;
        populateRequestTable(filtered);

        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label = buildRangeLabel("Showing requests resolved", start, end, display);
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
        withdrawRequestButton = new javax.swing.JButton();

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Requested Date Time", "Resolved Date Time", "Resolver" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Requested Date Time", "Resolved Date Time", "Status", "Resolver" }));
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
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Request ID", "Reason", "Request Date Time", "Resolved Date Time", "Status", "Resolver"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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

        withdrawRequestButton.setText("Wtihdraw Request");
        withdrawRequestButton.setMaximumSize(new java.awt.Dimension(150, 40));
        withdrawRequestButton.setPreferredSize(new java.awt.Dimension(150, 40));
        withdrawRequestButton.addActionListener(this::withdrawRequestButtonActionPerformed);
        jPanel3.add(withdrawRequestButton);

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
 
        List<Request> results = new ArrayList<>();
        for (Request req : getBaseRequests()) {
            boolean match = req.getRequestId().toLowerCase().contains(keyword)
                || (req.getReason() != null && req.getReason().toLowerCase().contains(keyword))
                || req.getStatus().toString().toLowerCase().contains(keyword)
                || (req.getResolver() != null
                    && req.getResolver().getName().toLowerCase().contains(keyword));
            if (match) results.add(req);
        }
 
        currentRequests = results;
        populateRequestTable(results);
        updateResultLabel("Showing results for \"" + rawKeyword + "\"", results.size());
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
            case 0: columnToSortIndex = 2; break; // "Default" -> Request Date Time
            case 1: columnToSortIndex = 0; break; // "ID"
            case 2: columnToSortIndex = 2; break; // "Requested Date Time"
            case 3: columnToSortIndex = 3; break; // "Resolved Date Time"
            case 4: columnToSortIndex = 5; break; // "Resolver"
            default: columnToSortIndex = 2;
        }
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(columnToSortIndex, SortOrder.ASCENDING)));
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();

        switch (selected) {
            case "Requested Date Time": {
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByRequestedDateRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Resolved Date Time": {
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    filterByResolvedDateRange(range.get("startDate"), range.get("endDate"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Status": {
                RequestStatus status = selectStatus();
                if (status != null) {
                    filterByStatus(status);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Resolver": {
                String keyword = selectResolverKeyword();
                if (keyword != null) {
                    filterByResolver(keyword);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            default: { // "Default" -> no filter
                List<Request> base = getBaseRequests();
                currentRequests = base;
                populateRequestTable(base);
                updateResultLabel("Showing " + base.size() + " request(s)", base.size());
            }
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadDoctorRequests();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void withdrawRequestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withdrawRequestButtonActionPerformed
        Request selected = getSelectedRequest();
        if (selected == null) return;
 
        if (!RequestStatus.PENDING.equals(selected.getStatus())) {
            JOptionPane.showMessageDialog(parentWindow,
                "This request has already been resolved and cannot be withdrawn.",
                "Cannot Withdraw", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Withdraw this request?", "Confirm Withdraw", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
 
        LinkedHashMap<String, String> result = reqCtrl.withdrawRequest(selected);
        JOptionPane.showMessageDialog(parentWindow, result.get("msg"),
            result.get("success") != null ? "Success" : "Error",
            result.get("success") != null ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        loadDoctorRequests();
    }//GEN-LAST:event_withdrawRequestButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JButton withdrawRequestButton;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
