/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.admin.user_management;

import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.WordProcessing;

/**
 *
 * @author sheaw
 */
public class AdminStaffManagementPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<User> currentAdminStaffs;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form AdminStaffManagement
     */
    public AdminStaffManagementPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        loadAdminStaffs();
        customStyleHeader();
        adminStaffTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, adminStaffTable.getRowHeight() * adminStaffTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        adminStaffTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        adminStaffTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
    }
    
    public void loadAdminStaffs() {
        DefaultTableModel model = (DefaultTableModel) adminStaffTable.getModel();
        model.setRowCount(0);
//        UserManagement usrServices = this.usrCtrl.getUserServices();
        List<User> allAdminStaffs = UserManagement.getAllAdminStaffs();
        currentAdminStaffs = allAdminStaffs;
        for (User usr: allAdminStaffs) {
            java.time.LocalDateTime dateTimeObj = DateTime.toDateTimeObject(usr.getJoinedDateTime());
            LocalDate date = DateTime.getDate(dateTimeObj);
            LocalTime time = DateTime.getTime(dateTimeObj);
            String joinedDate = DateTime.toDateStringWords(date);
            String joinedTime = DateTime.toTimeString(time);
            String joinedDateTime = joinedDate + " " + joinedTime;
            String[] adminStaffData = {usr.getId(), WordProcessing.capitalizeEachWord(usr.getName()), joinedDateTime};
            model.addRow(adminStaffData);
        }
//        userTable = new JTable(model);
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
    
    private void filterByDateRange(String start, String end) {
        List<User> filtered = UserManagement.searchUsersByDateRange(start, end, currentAdminStaffs);
        DefaultTableModel model = (DefaultTableModel) adminStaffTable.getModel();
        model.setRowCount(0);
        for (User usr : filtered) {
            java.time.LocalDateTime dateTimeObj = DateTime.toDateTimeObject(usr.getJoinedDateTime());
            LocalDate date = DateTime.getDate(dateTimeObj);
            LocalTime time = DateTime.getTime(dateTimeObj);
            String joinedDate = DateTime.toDateStringWords(date);
            String joinedTime = DateTime.toTimeString(time);
            String joinedDateTime = joinedDate + " " + joinedTime;
            String[] adminStaffData = {usr.getId(), WordProcessing.capitalizeEachWord(usr.getName()), joinedDateTime};
            model.addRow(adminStaffData);
        }
        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label;
        if (start != null && end != null) {
            label = "Showing AdminStaffs joined between " + display.format(java.sql.Date.valueOf(start)) + " and " + display.format(java.sql.Date.valueOf(end));
        } else if (start != null) {
            label = "Showing AdminStaffs joined after " + display.format(java.sql.Date.valueOf(start));
        } else {
            label = "Showing AdminStaffs joined before " + display.format(java.sql.Date.valueOf(end));
        }
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
        adminStaffTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new javax.swing.JPanel();
        operationsLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        addUserButton3 = new javax.swing.JButton();
        modifyUserButton = new javax.swing.JButton();
        deleteUserButton = new javax.swing.JButton();

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Name" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Joined At" }));
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

        adminStaffTable.setBackground(new java.awt.Color(255, 255, 255));
        adminStaffTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Admin Staff ID", "Name", "Joined At"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        adminStaffTable.setAlignmentY(0.0F);
        adminStaffTable.setRowHeight(40);
        adminStaffTable.setRowMargin(5);
        tableScrollPane.setViewportView(adminStaffTable);

        jPanel2.add(tableScrollPane, java.awt.BorderLayout.NORTH);

        showResultsForPanel.setBackground(new java.awt.Color(255, 255, 255));
        showResultsForPanel.setAlignmentY(0.0F);
        showResultsForPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        showResultsForPanel.add(showResultsForLabel);

        jPanel2.add(showResultsForPanel, java.awt.BorderLayout.WEST);

        wrapperPane.add(jPanel2);

        operationsPanel1.setBackground(new java.awt.Color(255, 255, 255));
        operationsPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)), javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        operationsPanel1.setAlignmentY(0.0F);
        operationsPanel1.setMaximumSize(new java.awt.Dimension(400, 177));
        operationsPanel1.setPreferredSize(new java.awt.Dimension(400, 52));
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

        addUserButton3.setText("Add Admin Staff");
        addUserButton3.setMaximumSize(new java.awt.Dimension(150, 40));
        addUserButton3.setPreferredSize(new java.awt.Dimension(150, 40));
        jPanel3.add(addUserButton3);

        modifyUserButton.setText("Modify Admin Staff");
        modifyUserButton.setMaximumSize(new java.awt.Dimension(150, 40));
        modifyUserButton.setPreferredSize(new java.awt.Dimension(150, 40));
        modifyUserButton.addActionListener(this::modifyUserButtonActionPerformed);
        jPanel3.add(modifyUserButton);

        deleteUserButton.setText("Delete Admin Staff");
        deleteUserButton.setMaximumSize(new java.awt.Dimension(150, 40));
        deleteUserButton.setMinimumSize(new java.awt.Dimension(150, 40));
        deleteUserButton.setPreferredSize(new java.awt.Dimension(150, 40));
        jPanel3.add(deleteUserButton);

        operationsPanel1.add(jPanel3);

        wrapperPane.add(operationsPanel1);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText();
        currentAdminStaffs = UserManagement.getAllAdminStaffs();
        List<User> searchResults = this.usrCtrl.searchUsers(keyword, currentAdminStaffs);
        currentAdminStaffs = searchResults;
        DefaultTableModel model = (DefaultTableModel) adminStaffTable.getModel();
        model.setRowCount(0);
        for (User usr: searchResults) {
            java.time.LocalDateTime dateTimeObj = DateTime.toDateTimeObject(usr.getJoinedDateTime());
            LocalDate date = DateTime.getDate(dateTimeObj);
            LocalTime time = DateTime.getTime(dateTimeObj);
            String joinedDate = DateTime.toDateStringWords(date);
            String joinedTime = DateTime.toTimeString(time);
            String joinedDateTime = joinedDate + " " + joinedTime;
            String[] adminStaffData = {usr.getId(), WordProcessing.capitalizeEachWord(usr.getName()), joinedDateTime};
            model.addRow(adminStaffData);
        }
        updateResultLabel("Showing results for \"" + keyword + "\"", searchResults.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) sortField.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) adminStaffTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        adminStaffTable.setRowSorter(sorter);
        int selectedIndex = sortField.getSelectedIndex();
        int columnToSortIndex;
        switch (selectedIndex) {
            case 0: columnToSortIndex = 2; break; // "Joined At" -> table column 2
            case 1: columnToSortIndex = 0; break; // "AdminStaff ID" -> table column 0
            case 2: columnToSortIndex = 1; break; // "Name" -> table column 1
            default: columnToSortIndex = 2;
        }
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(columnToSortIndex, SortOrder.ASCENDING)));
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) filterByField.getSelectedItem();

        if ("Joined At".equals(selected)) {
            LinkedHashMap<String, String> dateRange = selectDateRange();
            if (dateRange.get("startDate") != null || dateRange.get("endDate") != null) {
                filterByDateRange(dateRange.get("startDate"), dateRange.get("endDate"));
            } else {
                // user cancelled or picked invalid range -> reset dropdown
                filterByField.setSelectedIndex(0);
            }
            return;
        } else {
            DefaultTableModel model = (DefaultTableModel) adminStaffTable.getModel();
            model.setRowCount(0);
            for (User usr: currentAdminStaffs) {
                java.time.LocalDateTime dateTimeObj = DateTime.toDateTimeObject(usr.getJoinedDateTime());
                LocalDate date = DateTime.getDate(dateTimeObj);
                LocalTime time = DateTime.getTime(dateTimeObj);
                String joinedDate = DateTime.toDateStringWords(date);
                String joinedTime = DateTime.toTimeString(time);
                String joinedDateTime = joinedDate + " " + joinedTime;
                String[] adminStaffData = {usr.getId(), WordProcessing.capitalizeEachWord(usr.getName()), joinedDateTime};
                model.addRow(adminStaffData);
            }
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadAdminStaffs();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void modifyUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyUserButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_modifyUserButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addUserButton3;
    private javax.swing.JTable adminStaffTable;
    private javax.swing.JButton deleteUserButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton modifyUserButton;
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
