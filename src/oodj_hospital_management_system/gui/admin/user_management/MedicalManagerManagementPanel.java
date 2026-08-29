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
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class MedicalManagerManagementPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<User> currentMedicalManagers;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * Creates new form Medical ManagerManagement
     */
    public MedicalManagerManagementPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        loadMedicalManagers();
        customStyleHeader();
        medicalManagerTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, medicalManagerTable.getRowHeight() * medicalManagerTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        medicalManagerTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        medicalManagerTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
    }
    
    public void loadMedicalManagers() {
        DefaultTableModel model = (DefaultTableModel) medicalManagerTable.getModel();
        model.setRowCount(0);
//        UserManagement usrServices = this.usrCtrl.getUserServices();
        List<User> allMedicalManagers = UserManagement.getAllMedicalManagers();
        currentMedicalManagers = allMedicalManagers;
        for (User usr: allMedicalManagers) {
            java.time.LocalDateTime dateTimeObj = DateTime.toDateTimeObject(usr.getJoinedDateTime());
            LocalDate date = DateTime.getDate(dateTimeObj);
            LocalTime time = DateTime.getTime(dateTimeObj);
            String joinedDate = DateTime.toDateStringWords(date);
            String joinedTime = DateTime.toTimeString(time);
            String joinedDateTime = joinedDate + " " + joinedTime;
            String[] medicalManagerData = {usr.getId(), usr.getName(), usr.getEmail(), joinedDateTime};
            model.addRow(medicalManagerData);
        }
//        userTable = new JTable(model);
    }
    
    private void populateTable(List<User> users) {
        DefaultTableModel model = (DefaultTableModel) medicalManagerTable.getModel();
        model.setRowCount(0);
        for (User usr: users) {
            java.time.LocalDateTime dateTimeObj = DateTime.toDateTimeObject(usr.getJoinedDateTime());
            LocalDate date = DateTime.getDate(dateTimeObj);
            LocalTime time = DateTime.getTime(dateTimeObj);
            String joinedDate = DateTime.toDateStringWords(date);
            String joinedTime = DateTime.toTimeString(time);
            String joinedDateTime = joinedDate + " " + joinedTime;
            String[] medicalManagerData = {usr.getId(), usr.getName(), usr.getEmail(), joinedDateTime};
            model.addRow(medicalManagerData);
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
        List<User> filtered = UserManagement.searchUsersByDateRange(start, end, currentMedicalManagers);
        DefaultTableModel model = (DefaultTableModel) medicalManagerTable.getModel();
        model.setRowCount(0);
        for (User usr : filtered) {
            java.time.LocalDateTime dateTimeObj = DateTime.toDateTimeObject(usr.getJoinedDateTime());
            LocalDate date = DateTime.getDate(dateTimeObj);
            LocalTime time = DateTime.getTime(dateTimeObj);
            String joinedDate = DateTime.toDateStringWords(date);
            String joinedTime = DateTime.toTimeString(time);
            String joinedDateTime = joinedDate + " " + joinedTime;
            String[] medicalManagerData = {usr.getId(), usr.getName(), usr.getEmail(),joinedDateTime};
            model.addRow(medicalManagerData);
        }
        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        String label;
        if (start != null && end != null) {
            label = "Showing MedicalManagers joined between " + display.format(java.sql.Date.valueOf(start)) + " and " + display.format(java.sql.Date.valueOf(end));
        } else if (start != null) {
            label = "Showing MedicalManagers joined after " + display.format(java.sql.Date.valueOf(start));
        } else {
            label = "Showing MedicalManagers joined before " + display.format(java.sql.Date.valueOf(end));
        }
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
        medicalManagerTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        addUserButton = new javax.swing.JButton();
        modifyUserButton = new javax.swing.JButton();
        resetPasswordButton = new javax.swing.JButton();
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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Name", "Email" }));
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

        medicalManagerTable.setBackground(new java.awt.Color(255, 255, 255));
        medicalManagerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Medical Manager ID", "Name", "Email", "Joined At"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        medicalManagerTable.setAlignmentY(0.0F);
        medicalManagerTable.setRowHeight(40);
        medicalManagerTable.setRowMargin(5);
        tableScrollPane.setViewportView(medicalManagerTable);

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

        addUserButton.setText("Add Medical Manager");
        addUserButton.setMaximumSize(new java.awt.Dimension(180, 40));
        addUserButton.setPreferredSize(new java.awt.Dimension(180, 40));
        addUserButton.addActionListener(this::addUserButtonActionPerformed);
        jPanel3.add(addUserButton);

        modifyUserButton.setText("Modify Medical Manager");
        modifyUserButton.setMaximumSize(new java.awt.Dimension(180, 40));
        modifyUserButton.setPreferredSize(new java.awt.Dimension(180, 40));
        modifyUserButton.addActionListener(this::modifyUserButtonActionPerformed);
        jPanel3.add(modifyUserButton);

        resetPasswordButton.setText("Reset Password");
        resetPasswordButton.setMaximumSize(new java.awt.Dimension(180, 40));
        resetPasswordButton.setMinimumSize(new java.awt.Dimension(150, 40));
        resetPasswordButton.setPreferredSize(new java.awt.Dimension(180, 40));
        resetPasswordButton.addActionListener(this::resetPasswordButtonActionPerformed);
        jPanel3.add(resetPasswordButton);

        deleteUserButton.setText("Delete Medical Manager");
        deleteUserButton.setMaximumSize(new java.awt.Dimension(180, 40));
        deleteUserButton.setMinimumSize(new java.awt.Dimension(150, 40));
        deleteUserButton.setPreferredSize(new java.awt.Dimension(180, 40));
        deleteUserButton.addActionListener(this::deleteUserButtonActionPerformed);
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
        currentMedicalManagers = UserManagement.getAllMedicalManagers();
        List<User> searchResults = this.usrCtrl.searchUsers(keyword, currentMedicalManagers);
        currentMedicalManagers = searchResults;
        populateTable(searchResults);
        updateResultLabel("Showing results for \"" + keyword + "\"", searchResults.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) sortField.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) medicalManagerTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        medicalManagerTable.setRowSorter(sorter);
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
            populateTable(currentMedicalManagers);
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadMedicalManagers();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void deleteUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteUserButtonActionPerformed
        int selectedRow = medicalManagerTable.getSelectedRow();
        if (selectedRow != -1) {
            String usrId = medicalManagerTable.getValueAt(selectedRow, 0).toString();
            int choice = JOptionPane.showConfirmDialog(
                parentWindow,
                "Are you sure you want to delete this user?\nThis action cannot be undone!",
                "Confirm User Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
                );

            if (choice == JOptionPane.YES_OPTION) {
                LinkedHashMap<String, String> msg = this.usrCtrl.deleteUser(usrId);
                if (msg.get("success") != null) {                
                        JOptionPane.showMessageDialog(parentWindow, String.format("Medical Manager '%s' is deleted!", usrId));
                        loadMedicalManagers();
                } else {
                    JOptionPane.showMessageDialog(parentWindow, String.format("Fail to delete Medical Manager '%s' :(", usrId) + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Medical Manager deletion canceled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentWindow, "Please select a row first", "Advice", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_deleteUserButtonActionPerformed

    private void addUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserButtonActionPerformed
        java.awt.Frame parent = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        CreateNewUser dialog = new CreateNewUser(parent, true, "medical manager", usrCtrl, this::loadMedicalManagers);
        dialog.setVisible(true);
    }//GEN-LAST:event_addUserButtonActionPerformed

    private void modifyUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyUserButtonActionPerformed
        int selectedRow = medicalManagerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first");
            return;
        }

        String usrId = medicalManagerTable.getValueAt(selectedRow, 0).toString();
        User selectedUsr = UserManagement.getUserById(usrId);

        // Choose what to modify ：D
        JRadioButton nameOption = new JRadioButton("Name");
        JRadioButton emailOption = new JRadioButton("Email");
        JRadioButton bothOption = new JRadioButton("Name & Email");
        ButtonGroup group = new ButtonGroup();
        group.add(nameOption);
        group.add(emailOption);
        group.add(bothOption);
        nameOption.setSelected(true); // default

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new javax.swing.BoxLayout(choicePanel, javax.swing.BoxLayout.Y_AXIS));
        choicePanel.add(new JLabel("What would you like to modify?"));
        choicePanel.add(nameOption);
        choicePanel.add(emailOption);
        choicePanel.add(bothOption);

        int choice = JOptionPane.showConfirmDialog(parentWindow, choicePanel, "Modify Medical Manager", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        boolean editName = nameOption.isSelected() || bothOption.isSelected();
        boolean editEmail = emailOption.isSelected() || bothOption.isSelected();

        JTextField nameField = new JTextField(selectedUsr.getName());
        nameField.setPreferredSize(new java.awt.Dimension(300, 30));
        JTextField emailField = new JTextField(selectedUsr.getEmail());
        emailField.setPreferredSize(new java.awt.Dimension(300, 30));

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new javax.swing.BoxLayout(fieldPanel, javax.swing.BoxLayout.Y_AXIS));
        if (editName) {
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(nameLabel);
            fieldPanel.add(nameField);
        }
        if (editEmail) {
            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            emailField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(emailLabel);
            fieldPanel.add(emailField);
        }

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, fieldPanel, "Modify Medical Manager", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            if (editName) resetFieldBorder(nameField);
            if (editEmail) resetFieldBorder(emailField);

            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();
            boolean hasError = false;

            if (editName) {
                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Name cannot be empty", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(nameField);
                    hasError = true;
                } else if (newName.length() > MAX_NAME_LENGTH) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Name must be " + MAX_NAME_LENGTH + " characters or fewer", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(nameField);
                    hasError = true;
                }
            }

            if (editEmail) {
                if (newEmail.isEmpty()) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Email cannot be empty", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(emailField);
                    hasError = true;
                } else if (!this.usrCtrl.isValidEmail(newEmail)) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Please enter a valid email address", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(emailField);
                    hasError = true;
                }
            }

            if (hasError) {
                continue;
            }

            StringBuilder errors = new StringBuilder();
            boolean anyUpdated = false;

            if (editName && !selectedUsr.getName().equals(newName)) {
                LinkedHashMap<String, String> msg = this.usrCtrl.modifyUserName(newName, usrId);
                if (msg.get("success") != null) {
                    anyUpdated = true;
                } else {
                    errors.append("Name: ").append(msg.get("msg")).append("\n");
                }
            }

            if (editEmail && !selectedUsr.getEmail().equals(newEmail)) {
                LinkedHashMap<String, String> msg = this.usrCtrl.modifyEmail(newEmail, usrId);
                if (msg.get("success") != null) {
                    anyUpdated = true;
                } else {
                    errors.append("Email: ").append(msg.get("msg")).append("\n");
                }
            }

            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(parentWindow, "Some updates failed:\n" + errors, "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (anyUpdated) {
                JOptionPane.showMessageDialog(parentWindow, String.format("User '%s' updated!", usrId));
                loadMedicalManagers();
            }
            return;
        }
    }//GEN-LAST:event_modifyUserButtonActionPerformed

    private void resetPasswordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPasswordButtonActionPerformed
        int selectedRow = medicalManagerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first");
            return;
        }

        String usrId = medicalManagerTable.getValueAt(selectedRow, 0).toString();
        User selectedUsr = UserManagement.getUserById(usrId);

        int choice = JOptionPane.showConfirmDialog(
            parentWindow,
            String.format("Reset password for '%s'?\nA new temporary password will be generated.", selectedUsr.getName()),
            "Confirm Password Reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String tempPwd = UserManagement.generateTemporaryPassword();
        LinkedHashMap<String, String> msg = this.usrCtrl.modifyPwd(tempPwd, usrId);

        if (msg.get("success") != null) {
            String showNewPwd = String.format("User ID: %s\nNew Temporary Password: %s\n\nPlease notify the user to update the password for security", usrId, tempPwd);
            JOptionPane.showMessageDialog(parentWindow, showNewPwd, "Password Reset", JOptionPane.INFORMATION_MESSAGE);
            // IMPORTANT !!!!! send email~~~~~
        } else {
            JOptionPane.showMessageDialog(parentWindow, String.format("Failed to reset password for '%s' :(\n", usrId) + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_resetPasswordButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addUserButton;
    private javax.swing.JButton deleteUserButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTable medicalManagerTable;
    private javax.swing.JButton modifyUserButton;
    private javax.swing.JLabel operationsLabel1;
    private javax.swing.JPanel operationsPanel1;
    private javax.swing.JButton resetPasswordButton;
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
