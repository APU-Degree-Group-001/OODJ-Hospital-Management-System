/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.medical_manager.clinical_departments;

import java.util.ArrayList;
import java.util.Arrays;
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
import oodj_hospital_management_system.models.MedicalDepartment;
import oodj_hospital_management_system.models.MedicalManager;

/**
 *
 * @author sheaw
 */
public class MedicalManagerClinicalDepartmentsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<MedicalDepartment> currentMedicalDepartments;
    private List<MedicalManager> medicalManagers = new ArrayList<>();
    private List<String> medicalManagerLabels = new ArrayList<>();
    private List<MedicalDepartment> medicalDepartments = new ArrayList<>();
    private List<String> medicalDepartmentLabels = new ArrayList<>();
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    String imageFileName = "oodj_hospital_management_system/assets/img/layout/clinical_departments.png";

    /**
     * Creates new form adminDashboardPanel
     */
    public MedicalManagerClinicalDepartmentsPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        loadDepartments();
        customStyleHeader();
        departmentTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, departmentTable.getRowHeight() * departmentTable.getRowCount())
        );
        
        // Top Banner ~
        jPanel1.setOpaque(false);
    }
    
    private void customStyleHeader() {
        departmentTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        departmentTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
        departmentTable.getColumnModel().getColumn(2).setCellRenderer(new WrappingCellRenderer());
    }
    
    public void loadDepartments() {
        DefaultTableModel model = (DefaultTableModel) departmentTable.getModel();
        model.setRowCount(0);
        List<MedicalDepartment> allDepartments = this.usrCtrl.getDoctorController().getAllMedicalDepartments();
        currentMedicalDepartments = allDepartments;
        for (MedicalDepartment dept : allDepartments) {
            String[] rowData = {dept.getDepartmentId(), dept.getDepartmentName(), dept.getDescription()};
            model.addRow(rowData);
        }
    }

    private void populateTable(List<MedicalDepartment> departments) {
        DefaultTableModel model = (DefaultTableModel) departmentTable.getModel();
        model.setRowCount(0);
        for (MedicalDepartment dept : departments) {
            String[] rowData = {dept.getDepartmentId(), dept.getDepartmentName(), dept.getDescription()};
            model.addRow(rowData);
        }
    }

    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }

    private void limitTextFieldLength(JTextField field, int maxLength) {
        ((javax.swing.text.AbstractDocument) field.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                if (fb.getDocument().getLength() + string.length() <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                if (fb.getDocument().getLength() - length + text.length() <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
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
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        dashboardPanel = new javax.swing.JPanel();
        jPanel5 = new oodj_hospital_management_system.gui.utils.BackgroundPanel(this.imageFileName);
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
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
        wrapperPane = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        departmentTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        addDepartmentButton = new javax.swing.JButton();
        modifyDepartmentButton = new javax.swing.JButton();
        deleteDepartmentButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1020, 0));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        dashboardPanel.setBackground(new java.awt.Color(246, 251, 255));
        dashboardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 40, 0));
        dashboardPanel.setPreferredSize(new java.awt.Dimension(1020, 1200));
        dashboardPanel.setRequestFocusEnabled(false);
        dashboardPanel.setLayout(new java.awt.BorderLayout(0, 10));

        jPanel5.setBackground(new java.awt.Color(246, 251, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 60, 0, 60));
        jPanel5.setPreferredSize(new java.awt.Dimension(1020, 120));
        jPanel5.setLayout(new java.awt.BorderLayout(0, 20));

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setBackground(new java.awt.Color(246, 251, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Clinical Departments");
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
        jLabel1.setText("Manage clinical departments and maintain their information to support efficient hospital operations.");
        jLabel1.setMaximumSize(new java.awt.Dimension(946, 45));
        jLabel1.setPreferredSize(new java.awt.Dimension(946, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel1, gridBagConstraints);

        jPanel5.add(jPanel1, java.awt.BorderLayout.NORTH);

        dashboardPanel.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        usrMngContentArea.setBackground(new java.awt.Color(246, 251, 255));
        usrMngContentArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        usrMngContentArea.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(246, 251, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 60, 0, 60));
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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Name", "Description" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

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

        departmentTable.setBackground(new java.awt.Color(255, 255, 255));
        departmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Department ID", "Name", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        departmentTable.setAlignmentY(0.0F);
        departmentTable.setRowHeight(40);
        departmentTable.setRowMargin(5);
        tableScrollPane.setViewportView(departmentTable);

        jPanel3.add(tableScrollPane, java.awt.BorderLayout.CENTER);

        showResultsForPanel.setBackground(new java.awt.Color(246, 251, 255));
        showResultsForPanel.setAlignmentY(0.0F);
        showResultsForPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        showResultsForPanel.add(showResultsForLabel);

        jPanel3.add(showResultsForPanel, java.awt.BorderLayout.NORTH);

        wrapperPane.add(jPanel3);

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

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        jPanel4.setPreferredSize(new java.awt.Dimension(566, 140));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        addDepartmentButton.setText("Add Department");
        addDepartmentButton.setMaximumSize(new java.awt.Dimension(150, 40));
        addDepartmentButton.setPreferredSize(new java.awt.Dimension(150, 40));
        addDepartmentButton.addActionListener(this::addDepartmentButtonActionPerformed);
        jPanel4.add(addDepartmentButton);

        modifyDepartmentButton.setText("Modify Department");
        modifyDepartmentButton.setMaximumSize(new java.awt.Dimension(150, 40));
        modifyDepartmentButton.setPreferredSize(new java.awt.Dimension(150, 40));
        modifyDepartmentButton.addActionListener(this::modifyDepartmentButtonActionPerformed);
        jPanel4.add(modifyDepartmentButton);

        deleteDepartmentButton.setText("Delete Department");
        deleteDepartmentButton.setMaximumSize(new java.awt.Dimension(150, 40));
        deleteDepartmentButton.setMinimumSize(new java.awt.Dimension(150, 40));
        deleteDepartmentButton.setPreferredSize(new java.awt.Dimension(150, 40));
        deleteDepartmentButton.addActionListener(this::deleteDepartmentButtonActionPerformed);
        jPanel4.add(deleteDepartmentButton);

        operationsPanel1.add(jPanel4);

        wrapperPane.add(operationsPanel1);

        jPanel7.add(wrapperPane, java.awt.BorderLayout.CENTER);

        usrMngContentArea.add(jPanel7, java.awt.BorderLayout.CENTER);

        dashboardPanel.add(usrMngContentArea, java.awt.BorderLayout.CENTER);

        jScrollPane2.setViewportView(dashboardPanel);

        add(jScrollPane2);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText();
        List<MedicalDepartment> allDepartments = this.usrCtrl.getDoctorController().getAllMedicalDepartments();
        List<MedicalDepartment> searchResults = this.usrCtrl.getDoctorController().searchMedicalDepartments(keyword, allDepartments);
        currentMedicalDepartments = searchResults;
        populateTable(searchResults);
        updateResultLabel("Showing results for \"" + keyword + "\"", searchResults.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadDepartments();
        searchField.setText("");
        showResultsForLabel.setText("");
        departmentTable.setRowSorter(null);
        sortField.setSelectedIndex(0);
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) sortField.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) departmentTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        departmentTable.setRowSorter(sorter);
        int selectedIndex = sortField.getSelectedIndex();
        int columnToSortIndex;
        switch (selectedIndex) {
            case 0: columnToSortIndex = 0; break; // "Default" -> Department ID
            case 1: columnToSortIndex = 0; break; // "ID" -> Department ID
            case 2: columnToSortIndex = 1; break; // "Name" -> Name
            case 3: columnToSortIndex = 2; break; // "Description" -> Description
            default: columnToSortIndex = 0;
        }
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(columnToSortIndex, SortOrder.ASCENDING)));
    }//GEN-LAST:event_sortFieldActionPerformed

    private void addDepartmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDepartmentButtonActionPerformed
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new java.awt.Dimension(300, 30));
        limitTextFieldLength(nameField, MAX_NAME_LENGTH);
        JTextField descriptionField = new JTextField();
        descriptionField.setPreferredSize(new java.awt.Dimension(300, 30));
        limitTextFieldLength(descriptionField, MAX_DESCRIPTION_LENGTH);

        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel("Department Name:");
        nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        descriptionField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(descriptionLabel);
        panel.add(descriptionField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Add Department", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            resetFieldBorder(nameField);
            resetFieldBorder(descriptionField);

            String deptName = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            boolean hasError = false;

            if (deptName.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Department name cannot be empty", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(nameField);
                hasError = true;
            } else if (deptName.length() > MAX_NAME_LENGTH) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Department name must be " + MAX_NAME_LENGTH + " characters or fewer", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(nameField);
                hasError = true;
            }

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Description cannot be empty", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(descriptionField);
                hasError = true;
            } else if (description.length() > MAX_DESCRIPTION_LENGTH) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Description must be " + MAX_DESCRIPTION_LENGTH + " characters or fewer", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(descriptionField);
                hasError = true;
            }

            if (hasError) {
                continue;
            }

            LinkedHashMap<String, String> msg = this.usrCtrl.getDoctorController().addNewDepartment(deptName, description);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Department '%s' is created!", msg.get("newDepartmentId")));
                loadDepartments();
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Failed to create department:\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_addDepartmentButtonActionPerformed

    private void modifyDepartmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyDepartmentButtonActionPerformed
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first");
            return;
        }

        String departmentId = departmentTable.getValueAt(selectedRow, 0).toString();
        MedicalDepartment selectedDept = this.usrCtrl.getDoctorController().getMedicalDepartmentById(departmentId);

        if (selectedDept == null) {
            JOptionPane.showMessageDialog(parentWindow, "Department not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JRadioButton nameOption = new JRadioButton("Name");
        JRadioButton descriptionOption = new JRadioButton("Description");
        JRadioButton bothOption = new JRadioButton("Name & Description");
        ButtonGroup group = new ButtonGroup();
        group.add(nameOption);
        group.add(descriptionOption);
        group.add(bothOption);
        nameOption.setSelected(true);

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new javax.swing.BoxLayout(choicePanel, javax.swing.BoxLayout.Y_AXIS));
        choicePanel.add(new JLabel("What would you like to modify?"));
        choicePanel.add(nameOption);
        choicePanel.add(descriptionOption);
        choicePanel.add(bothOption);

        int choice = JOptionPane.showConfirmDialog(parentWindow, choicePanel, "Modify Department", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        boolean editName = nameOption.isSelected() || bothOption.isSelected();
        boolean editDescription = descriptionOption.isSelected() || bothOption.isSelected();

        JTextField nameField = new JTextField(selectedDept.getDepartmentName());
        nameField.setPreferredSize(new java.awt.Dimension(300, 30));
        limitTextFieldLength(nameField, MAX_NAME_LENGTH);
        JTextField descriptionField = new JTextField(selectedDept.getDescription());
        descriptionField.setPreferredSize(new java.awt.Dimension(300, 30));
        limitTextFieldLength(descriptionField, MAX_DESCRIPTION_LENGTH);

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new javax.swing.BoxLayout(fieldPanel, javax.swing.BoxLayout.Y_AXIS));
        if (editName) {
            JLabel nameLabel = new JLabel("Department Name:");
            nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(nameLabel);
            fieldPanel.add(nameField);
        }
        if (editDescription) {
            JLabel descriptionLabel = new JLabel("Description:");
            descriptionLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            descriptionField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(descriptionLabel);
            fieldPanel.add(descriptionField);
        }

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, fieldPanel, "Modify Department", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            if (editName) resetFieldBorder(nameField);
            if (editDescription) resetFieldBorder(descriptionField);

            String newName = nameField.getText().trim();
            String newDescription = descriptionField.getText().trim();
            boolean hasError = false;

            if (editName) {
                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Department name cannot be empty", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(nameField);
                    hasError = true;
                } else if (newName.length() > MAX_NAME_LENGTH) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Department name must be " + MAX_NAME_LENGTH + " characters or fewer", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(nameField);
                    hasError = true;
                }
            }

            if (editDescription) {
                if (newDescription.isEmpty()) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Description cannot be empty", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(descriptionField);
                    hasError = true;
                } else if (newDescription.length() > MAX_DESCRIPTION_LENGTH) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Description must be " + MAX_DESCRIPTION_LENGTH + " characters or fewer", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(descriptionField);
                    hasError = true;
                }
            }

            if (hasError) {
                continue;
            }

            String finalName = editName ? newName : selectedDept.getDepartmentName();
            String finalDescription = editDescription ? newDescription : selectedDept.getDescription();

            LinkedHashMap<String, String> msg = this.usrCtrl.getDoctorController().editDepartment(finalName, finalDescription, departmentId);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Department '%s' updated!", departmentId));
                loadDepartments();
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Update failed:\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_modifyDepartmentButtonActionPerformed

    private void deleteDepartmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDepartmentButtonActionPerformed
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentWindow, "Please select a row first", "Advice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String departmentId = departmentTable.getValueAt(selectedRow, 0).toString();
        MedicalDepartment targetDept = this.usrCtrl.getDoctorController().getMedicalDepartmentById(departmentId);

        if (targetDept == null) {
            JOptionPane.showMessageDialog(parentWindow, "Department not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            parentWindow,
            String.format("Are you sure you want to delete department '%s'?\nThis action cannot be undone!", departmentId),
            "Confirm Department Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            LinkedHashMap<String, String> msg = this.usrCtrl.getDoctorController().deleteDepartment(departmentId);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Department '%s' is deleted!", departmentId));
                loadDepartments();
            } else {
                JOptionPane.showMessageDialog(parentWindow, String.format("Failed to delete Department '%s' :(\n", departmentId) + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentWindow, "Department deletion canceled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_deleteDepartmentButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDepartmentButton;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.JButton deleteDepartmentButton;
    private javax.swing.JTable departmentTable;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton modifyDepartmentButton;
    private javax.swing.JLabel operationsLabel1;
    private javax.swing.JPanel operationsPanel1;
    private javax.swing.JButton resetSearchButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPane;
    private javax.swing.JLabel showResultsForLabel;
    private javax.swing.JPanel showResultsForPanel;
    private javax.swing.JLabel sortByLabel;
    private javax.swing.JPanel sortByPane;
    private javax.swing.JComboBox<String> sortField;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JPanel usrMngContentArea;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
