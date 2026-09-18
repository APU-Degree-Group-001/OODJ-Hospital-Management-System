/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.admin.billing_settings;

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
import oodj_hospital_management_system.models.LabResultType;
import oodj_hospital_management_system.models.LabResultType.LabResultTypeStatus;

/**
 *
 * @author sheaw
 */
public class LabServicesBaseRatePanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<LabResultType> currentLabResultTypes;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final int MAX_TYPE_NAME_LENGTH = 50;

    /**
     * Creates new form DoctorManagement
     */
    public LabServicesBaseRatePanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        loadLabResultTypes();
        customStyleHeader();
        labResultsTypeTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, labResultsTypeTable.getRowHeight() * labResultsTypeTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        labResultsTypeTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        labResultsTypeTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
    }
    
    public void loadLabResultTypes() {
        DefaultTableModel model = (DefaultTableModel) labResultsTypeTable.getModel();
        model.setRowCount(0);
        List<LabResultType> allLabResultTypes = this.usrCtrl.getLabResultTypeController().getAllLabResultTypes();
        currentLabResultTypes = allLabResultTypes;
        for (LabResultType type : allLabResultTypes) {
            String[] rowData = {type.getLabResultTypeId(), type.getTypeName(), String.format("%.2f", type.getBaseRate()), type.getStatus().name()};
            model.addRow(rowData);
        }
//        userTable = new JTable(model);
    }
    
    private void populateTable(List<LabResultType> types) {
        DefaultTableModel model = (DefaultTableModel) labResultsTypeTable.getModel();
        model.setRowCount(0);
        for (LabResultType type : types) {
            String[] rowData = {
                type.getLabResultTypeId(),
                type.getTypeName(),
                String.format("%.2f", type.getBaseRate()),
                type.getStatus().name()
            };
            model.addRow(rowData);
        }
    }
    
    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }
    
    private LinkedHashMap<String, String> selectBaseRateRange() {
        LinkedHashMap<String, String> rateRange = new LinkedHashMap<>();
        JTextField minField = new JTextField();
        JTextField maxField = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.add(new JLabel("Min Base Rate:"));
        panel.add(minField);
        panel.add(new JLabel("Max Base Rate:"));
        panel.add(maxField);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Filter By Base Rate", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String minText = minField.getText().trim();
            String maxText = maxField.getText().trim();

            if (minText.isEmpty() && maxText.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Please enter at least one value", "Missing Value",
                    JOptionPane.INFORMATION_MESSAGE);
                return rateRange;
            }

            try {
                if (!minText.isEmpty()) { Double.parseDouble(minText); }
                if (!maxText.isEmpty()) { Double.parseDouble(maxText); }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Base rate must be a valid number", "Invalid Value",
                    JOptionPane.ERROR_MESSAGE);
                return rateRange;
            }

            rateRange.put("minRate", minText.isEmpty() ? null : minText);
            rateRange.put("maxRate", maxText.isEmpty() ? null : maxText);
        }
        return rateRange;
    }

    private void filterByBaseRateRange(String minRate, String maxRate) {
        Double min = minRate != null ? Double.parseDouble(minRate) : null;
        Double max = maxRate != null ? Double.parseDouble(maxRate) : null;

        List<LabResultType> filtered = new java.util.ArrayList<>();
        for (LabResultType type : currentLabResultTypes) {
            double rate = type.getBaseRate();
            if ((min == null || rate >= min) && (max == null || rate <= max)) {
                filtered.add(type);
            }
        }

        populateTable(filtered);

        String label;
        if (min != null && max != null) {
            label = "Showing types with rate between " + min + " and " + max;
        } else if (min != null) {
            label = "Showing types with rate at least " + min;
        } else {
            label = "Showing types with rate at most " + max;
        }
        updateResultLabel(label, filtered.size());
    }
    
    private void filterByStatus(LabResultTypeStatus status) {
        List<LabResultType> filtered = new java.util.ArrayList<>();
        for (LabResultType type : currentLabResultTypes) {
            if (type.getStatus() == status) {
                filtered.add(type);
            }
        }
        populateTable(filtered);
        updateResultLabel("Showing " + status.name() + " consultation types", filtered.size());
    }
    
    private List<LabResultType> searchLabResultTypes(String keyword, List<LabResultType> source) {
        List<LabResultType> matched = new java.util.ArrayList<>();
        String kw = keyword.toLowerCase().trim();
        for (LabResultType type : source) {
            if (type.getLabResultTypeId().toLowerCase().contains(kw)
                    || type.getTypeName().toLowerCase().contains(kw)
                    || type.getStatus().name().toLowerCase().contains(kw)) {
                matched.add(type);
            }
        }
        return matched;
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
        labResultsTypeTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        addServiceButton = new javax.swing.JButton();
        modifyServiceButton = new javax.swing.JButton();
        deleteServiceButton = new javax.swing.JButton();

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Name" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Base Rate", "Status" }));
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

        labResultsTypeTable.setBackground(new java.awt.Color(255, 255, 255));
        labResultsTypeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Type ID", "Name", "Base Rate", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        labResultsTypeTable.setAlignmentY(0.0F);
        labResultsTypeTable.setRowHeight(40);
        labResultsTypeTable.setRowMargin(5);
        tableScrollPane.setViewportView(labResultsTypeTable);

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

        addServiceButton.setText("Add Service");
        addServiceButton.setMaximumSize(new java.awt.Dimension(150, 40));
        addServiceButton.setPreferredSize(new java.awt.Dimension(150, 40));
        addServiceButton.addActionListener(this::addServiceButtonActionPerformed);
        jPanel3.add(addServiceButton);

        modifyServiceButton.setText("Modify Service");
        modifyServiceButton.setMaximumSize(new java.awt.Dimension(150, 40));
        modifyServiceButton.setPreferredSize(new java.awt.Dimension(150, 40));
        modifyServiceButton.addActionListener(this::modifyServiceButtonActionPerformed);
        jPanel3.add(modifyServiceButton);

        deleteServiceButton.setText("Active / Inactive");
        deleteServiceButton.setMaximumSize(new java.awt.Dimension(150, 40));
        deleteServiceButton.setMinimumSize(new java.awt.Dimension(150, 40));
        deleteServiceButton.setPreferredSize(new java.awt.Dimension(150, 40));
        deleteServiceButton.addActionListener(this::deleteServiceButtonActionPerformed);
        jPanel3.add(deleteServiceButton);

        operationsPanel1.add(jPanel3);

        wrapperPane.add(operationsPanel1);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText();
        List<LabResultType> allTypes = this.usrCtrl.getLabResultTypeController().getAllLabResultTypes();
        List<LabResultType> searchResults = searchLabResultTypes(keyword, allTypes);
        currentLabResultTypes = searchResults;
        populateTable(searchResults);
        updateResultLabel("Showing results for \"" + keyword + "\"", searchResults.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) sortField.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) labResultsTypeTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        labResultsTypeTable.setRowSorter(sorter);
        int selectedIndex = sortField.getSelectedIndex();
        int columnToSortIndex;
        switch (selectedIndex) {
            case 0: columnToSortIndex = 0; break; // "Default" -> Type ID
            case 1: columnToSortIndex = 0; break; // "ID" -> Type ID
            case 2: columnToSortIndex = 1; break; // "Name" -> Name
            default: columnToSortIndex = 0;
        }
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(columnToSortIndex, SortOrder.ASCENDING)));
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) filterByField.getSelectedItem();

        if ("Base Rate".equals(selected)) {
            LinkedHashMap<String, String> rateRange = selectBaseRateRange();
            if (rateRange.get("minRate") != null || rateRange.get("maxRate") != null) {
                filterByBaseRateRange(rateRange.get("minRate"), rateRange.get("maxRate"));
            } else {
                filterByField.setSelectedIndex(0);
            }
            return;
        } else if ("Status".equals(selected)) {
            Object[] options = { "Active", "Inactive" };
            int choice = JOptionPane.showOptionDialog(parentWindow,
                "Filter by which status?", "Filter By Status",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

            if (choice == 0) {
                filterByStatus(LabResultTypeStatus.ACTIVE);
            } else if (choice == 1) {
                filterByStatus(LabResultTypeStatus.INACTIVE);
            } else {
                filterByField.setSelectedIndex(0);
            }
            return;
        } else {
            loadLabResultTypes();
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadLabResultTypes();
        searchField.setText("");
        showResultsForLabel.setText("");
        labResultsTypeTable.setRowSorter(null);
        sortField.setSelectedIndex(0);
        filterByField.setSelectedIndex(0); 
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void deleteServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteServiceButtonActionPerformed
        int selectedRow = labResultsTypeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentWindow, "Please select a row first", "Advice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String typeId = labResultsTypeTable.getValueAt(selectedRow, 0).toString();
        LabResultType targetType = this.usrCtrl.getLabResultTypeController().getLabResultTypeById(typeId);

        if (targetType == null) {
            JOptionPane.showMessageDialog(parentWindow, "Lab result type not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isActive = targetType.getStatus() == LabResultTypeStatus.ACTIVE;
        String action = isActive ? "deactivate" : "activate";

        int choice = JOptionPane.showConfirmDialog(
            parentWindow,
            String.format("This will %s lab result type '%s'.\nContinue?", action, typeId),
            "Confirm " + (isActive ? "Deactivation" : "Activation"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            LinkedHashMap<String, String> msg = isActive
                ? this.usrCtrl.getLabResultTypeController().deactivateLabResultType(typeId)
                : this.usrCtrl.getLabResultTypeController().activateLabResultType(typeId);

            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Lab result type '%s' is %sd!", typeId, action));
                loadLabResultTypes();
            } else {
                JOptionPane.showMessageDialog(parentWindow, String.format("Failed to %s '%s' :(\n", action, typeId) + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentWindow, "Action cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_deleteServiceButtonActionPerformed

    private void addServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addServiceButtonActionPerformed
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new java.awt.Dimension(300, 30));
        limitTextFieldLength(nameField, MAX_TYPE_NAME_LENGTH);
        JTextField rateField = new JTextField();
        rateField.setPreferredSize(new java.awt.Dimension(300, 30));

        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel("Type Name:");
        nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JLabel rateLabel = new JLabel("Base Rate:");
        rateLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        rateField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(rateLabel);
        panel.add(rateField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Add Lab Result Type", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            resetFieldBorder(nameField);
            resetFieldBorder(rateField);

            String typeName = nameField.getText().trim();
            String rateText = rateField.getText().trim();
            boolean hasError = false;

            if (typeName.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Type name cannot be empty", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(nameField);
                hasError = true;
            } else if (typeName.length() > MAX_TYPE_NAME_LENGTH) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Type name must be " + MAX_TYPE_NAME_LENGTH + " characters or fewer", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(nameField);
                hasError = true;
            }

            double baseRate = 0;
            try {
                baseRate = Double.parseDouble(rateText);
                if (baseRate < 0) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Base rate cannot be negative", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(rateField);
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Base rate must be a valid number", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(rateField);
                hasError = true;
            }

            if (hasError) {
                continue;
            }

            LinkedHashMap<String, String> msg = this.usrCtrl.getLabResultTypeController().createLabResultType(typeName, baseRate);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Lab result type '%s' is created!", msg.get("newLabResultTypeId")));
                loadLabResultTypes();
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Failed to create lab result type:\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_addServiceButtonActionPerformed

    private void modifyServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyServiceButtonActionPerformed
        int selectedRow = labResultsTypeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentWindow, "Please select a row first");
            return;
        }

        String typeId = labResultsTypeTable.getValueAt(selectedRow, 0).toString();
        LabResultType selectedType = this.usrCtrl.getLabResultTypeController().getLabResultTypeById(typeId);

        if (selectedType == null) {
            JOptionPane.showMessageDialog(parentWindow, "Lab result type not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JRadioButton nameOption = new JRadioButton("Type Name");
        JRadioButton rateOption = new JRadioButton("Base Rate");
        JRadioButton bothOption = new JRadioButton("Type Name & Base Rate");
        ButtonGroup group = new ButtonGroup();
        group.add(nameOption);
        group.add(rateOption);
        group.add(bothOption);
        nameOption.setSelected(true);

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new javax.swing.BoxLayout(choicePanel, javax.swing.BoxLayout.Y_AXIS));
        choicePanel.add(new JLabel("What would you like to modify?"));
        choicePanel.add(nameOption);
        choicePanel.add(rateOption);
        choicePanel.add(bothOption);

        int choice = JOptionPane.showConfirmDialog(parentWindow, choicePanel, "Modify Lab Result Type", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        boolean editName = nameOption.isSelected() || bothOption.isSelected();
        boolean editRate = rateOption.isSelected() || bothOption.isSelected();

        JTextField nameField = new JTextField(selectedType.getTypeName());
        nameField.setPreferredSize(new java.awt.Dimension(300, 30));
        limitTextFieldLength(nameField, MAX_TYPE_NAME_LENGTH);
        JTextField rateField = new JTextField(String.valueOf(selectedType.getBaseRate()));
        rateField.setPreferredSize(new java.awt.Dimension(300, 30));

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new javax.swing.BoxLayout(fieldPanel, javax.swing.BoxLayout.Y_AXIS));
        if (editName) {
            JLabel nameLabel = new JLabel("Type Name:");
            nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(nameLabel);
            fieldPanel.add(nameField);
        }
        if (editRate) {
            JLabel rateLabel = new JLabel("Base Rate:");
            rateLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            rateField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(rateLabel);
            fieldPanel.add(rateField);
        }

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, fieldPanel, "Modify Lab Result Type", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            if (editName) resetFieldBorder(nameField);
            if (editRate) resetFieldBorder(rateField);

            String newTypeName = nameField.getText().trim();
            String newRateText = rateField.getText().trim();
            boolean hasError = false;

            if (editName) {
                if (newTypeName.isEmpty()) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Type name cannot be empty", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(nameField);
                    hasError = true;
                } else if (newTypeName.length() > MAX_TYPE_NAME_LENGTH) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Type name must be " + MAX_TYPE_NAME_LENGTH + " characters or fewer", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(nameField);
                    hasError = true;
                }
            }

            double newBaseRate = selectedType.getBaseRate();
            if (editRate) {
                try {
                    newBaseRate = Double.parseDouble(newRateText);
                    if (newBaseRate < 0) {
                        JOptionPane.showMessageDialog(parentWindow,
                            "Base rate cannot be negative", "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                        setFieldError(rateField);
                        hasError = true;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Base rate must be a valid number", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(rateField);
                    hasError = true;
                }
            }

            if (hasError) {
                continue;
            }

            String finalTypeName = editName ? newTypeName : selectedType.getTypeName();
            double finalBaseRate = editRate ? newBaseRate : selectedType.getBaseRate();

            LinkedHashMap<String, String> msg = this.usrCtrl.getLabResultTypeController().updateLabResultType(selectedType, finalTypeName, finalBaseRate);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Lab result type '%s' updated!", typeId));
                loadLabResultTypes();
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Update failed:\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_modifyServiceButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addServiceButton;
    private javax.swing.JButton deleteServiceButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTable labResultsTypeTable;
    private javax.swing.JButton modifyServiceButton;
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
