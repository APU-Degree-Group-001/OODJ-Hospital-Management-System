/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.admin.billing_settings;

import oodj_hospital_management_system.gui.admin.user_management.*;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
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
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.LabResultType;
import oodj_hospital_management_system.models.LabResultType.LabResultTypeStatus;
import oodj_hospital_management_system.models.PrescriptionType;
import oodj_hospital_management_system.models.PrescriptionType.PrescriptionTypeStatus;

/**
 *
 * @author sheaw
 */
public class InsuranceNetworkPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<Insurance> currentInsurances;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * Creates new form DoctorManagement
     */
    public InsuranceNetworkPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        loadInsurances();
        customStyleHeader();
        insuranceTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, insuranceTable.getRowHeight() * insuranceTable.getRowCount())
        );
    }
    
    private void customStyleHeader() {
        insuranceTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        insuranceTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
    }
    
    public void loadInsurances() {
        DefaultTableModel model = (DefaultTableModel) insuranceTable.getModel();
        model.setRowCount(0);
        List<Insurance> allInsurances = this.usrCtrl.getInsuranceController().getAllInsurances();
        currentInsurances = allInsurances;
        for (Insurance ins : allInsurances) {
            String[] rowData = {
                ins.getInsuranceId(),
                ins.getInsuranceName(),
                ins.isAccepted() ? "Accepted" : "Not Accepted",
                String.format("%.0f", ins.getCoveragePercentage() * 100)
            };
            model.addRow(rowData);
        }
    }
    
    private void populateTable(List<Insurance> insurances) {
        DefaultTableModel model = (DefaultTableModel) insuranceTable.getModel();
        model.setRowCount(0);
        for (Insurance ins : insurances) {
            String[] rowData = {
                ins.getInsuranceId(),
                ins.getInsuranceName(),
                ins.isAccepted() ? "Accepted" : "Not Accepted",
                String.format("%.0f", ins.getCoveragePercentage() * 100)
            };
            model.addRow(rowData);
        }
    }

    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }
    
    private LinkedHashMap<String, String> selectCoverageRange() {
        LinkedHashMap<String, String> coverageRange = new LinkedHashMap<>();
        JTextField minField = new JTextField();
        JTextField maxField = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.add(new JLabel("Min Coverage Rate (%):"));
        panel.add(minField);
        panel.add(new JLabel("Max Coverage Rate (%):"));
        panel.add(maxField);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Filter By Coverage Rate", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String minText = minField.getText().trim();
            String maxText = maxField.getText().trim();

            if (minText.isEmpty() && maxText.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Please enter at least one value", "Missing Value",
                    JOptionPane.INFORMATION_MESSAGE);
                return coverageRange;
            }

            try {
                if (!minText.isEmpty()) { Double.parseDouble(minText); }
                if (!maxText.isEmpty()) { Double.parseDouble(maxText); }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Coverage rate must be a valid number", "Invalid Value",
                    JOptionPane.ERROR_MESSAGE);
                return coverageRange;
            }

            coverageRange.put("minCoverage", minText.isEmpty() ? null : minText);
            coverageRange.put("maxCoverage", maxText.isEmpty() ? null : maxText);
        }
        return coverageRange;
    }

    private void filterByCoverageRange(String minCoverage, String maxCoverage) {
        Double min = minCoverage != null ? Double.parseDouble(minCoverage) / 100.0 : null;
        Double max = maxCoverage != null ? Double.parseDouble(maxCoverage) / 100.0 : null;

        List<Insurance> filtered = new java.util.ArrayList<>();
        for (Insurance ins : currentInsurances) {
            double coverage = ins.getCoveragePercentage();
            if ((min == null || coverage >= min) && (max == null || coverage <= max)) {
                filtered.add(ins);
            }
        }

        populateTable(filtered);

        String label;
        if (min != null && max != null) {
            label = "Showing insurances with coverage between " + minCoverage + "% and " + maxCoverage + "%";
        } else if (min != null) {
            label = "Showing insurances with coverage at least " + minCoverage + "%";
        } else {
            label = "Showing insurances with coverage at most " + maxCoverage + "%";
        }
        updateResultLabel(label, filtered.size());
    }
    
    private void filterByAcceptance(boolean accepted) {
        List<Insurance> filtered = new java.util.ArrayList<>();
        for (Insurance ins : currentInsurances) {
            if (ins.isAccepted() == accepted) {
                filtered.add(ins);
            }
        }
        populateTable(filtered);
        updateResultLabel("Showing " + (accepted ? "accepted" : "not accepted") + " insurances", filtered.size());
    }
    
    private List<Insurance> searchInsurances(String keyword, List<Insurance> source) {
        List<Insurance> matched = new java.util.ArrayList<>();
        String kw = keyword.toLowerCase().trim();
        for (Insurance ins : source) {
            if (ins.getInsuranceId().toLowerCase().contains(kw)
                    || ins.getInsuranceName().toLowerCase().contains(kw)
                    || (ins.isAccepted() ? "accepted" : "not accepted").contains(kw)) {
                matched.add(ins);
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
        insuranceTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        addInsuranceButton = new javax.swing.JButton();
        modifyInsuranceButton = new javax.swing.JButton();
        activeButton = new javax.swing.JButton();

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

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Acceptance", "Coverage Rate (%)", " " }));
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

        insuranceTable.setBackground(new java.awt.Color(255, 255, 255));
        insuranceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Type ID", "Name", "Acceptance", "Coverage Rate (%)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        insuranceTable.setAlignmentY(0.0F);
        insuranceTable.setRowHeight(40);
        insuranceTable.setRowMargin(5);
        tableScrollPane.setViewportView(insuranceTable);

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

        addInsuranceButton.setText("Add Insurance");
        addInsuranceButton.setMaximumSize(new java.awt.Dimension(150, 40));
        addInsuranceButton.setPreferredSize(new java.awt.Dimension(150, 40));
        addInsuranceButton.addActionListener(this::addInsuranceButtonActionPerformed);
        jPanel3.add(addInsuranceButton);

        modifyInsuranceButton.setText("Modify Insurance");
        modifyInsuranceButton.setMaximumSize(new java.awt.Dimension(150, 40));
        modifyInsuranceButton.setPreferredSize(new java.awt.Dimension(150, 40));
        modifyInsuranceButton.addActionListener(this::modifyInsuranceButtonActionPerformed);
        jPanel3.add(modifyInsuranceButton);

        activeButton.setText("Active / Inactive");
        activeButton.setMaximumSize(new java.awt.Dimension(150, 40));
        activeButton.setMinimumSize(new java.awt.Dimension(150, 40));
        activeButton.setPreferredSize(new java.awt.Dimension(150, 40));
        activeButton.addActionListener(this::activeButtonActionPerformed);
        jPanel3.add(activeButton);

        operationsPanel1.add(jPanel3);

        wrapperPane.add(operationsPanel1);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText();
        List<Insurance> allInsurances = this.usrCtrl.getInsuranceController().getAllInsurances();
        List<Insurance> searchResults = searchInsurances(keyword, allInsurances);
        currentInsurances = searchResults;
        populateTable(searchResults);
        updateResultLabel("Showing results for \"" + keyword + "\"", searchResults.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        searchField.setText("");
        showResultsForLabel.setText("");
        String selected = (String) sortField.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) insuranceTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        insuranceTable.setRowSorter(sorter);
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

        if ("Coverage Rate (%)".equals(selected)) {
            LinkedHashMap<String, String> coverageRange = selectCoverageRange();
            if (coverageRange.get("minCoverage") != null || coverageRange.get("maxCoverage") != null) {
                filterByCoverageRange(coverageRange.get("minCoverage"), coverageRange.get("maxCoverage"));
            } else {
                filterByField.setSelectedIndex(0);
            }
            return;
        } else if ("Acceptance".equals(selected)) {
            Object[] options = { "Accepted", "Not Accepted" };
            int choice = JOptionPane.showOptionDialog(parentWindow,
                "Filter by which status?", "Filter By Acceptance",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

            if (choice == 0) {
                filterByAcceptance(true);
            } else if (choice == 1) {
                filterByAcceptance(false);
            } else {
                filterByField.setSelectedIndex(0);
            }
            return;
        } else {
            loadInsurances();
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadInsurances();
        searchField.setText("");
        showResultsForLabel.setText("");
        insuranceTable.setRowSorter(null);
        sortField.setSelectedIndex(0);
        filterByField.setSelectedIndex(0); 
    }//GEN-LAST:event_resetSearchButtonActionPerformed

    private void activeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activeButtonActionPerformed
        int selectedRow = insuranceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentWindow, "Please select a row first", "Advice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String insId = insuranceTable.getValueAt(selectedRow, 0).toString();
        Insurance targetIns = this.usrCtrl.getInsuranceController().getInsuranceById(insId);

        if (targetIns == null) {
            JOptionPane.showMessageDialog(parentWindow, "Insurance not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isAccepted = targetIns.isAccepted();
        String action = isAccepted ? "mark as not accepted" : "mark as accepted";

        int choice = JOptionPane.showConfirmDialog(
            parentWindow,
            String.format("This will %s insurance '%s'.\nContinue?", action, insId),
            "Confirm " + (isAccepted ? "Deactivation" : "Activation"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            LinkedHashMap<String, String> msg = this.usrCtrl.getInsuranceController().updateInsurance(
                                                        targetIns,
                                                        targetIns.getInsuranceName(),
                                                        !isAccepted,
                                                        targetIns.getCoveragePercentage()
                                                );

            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Insurance '%s' is now %s!", insId, isAccepted ? "not accepted" : "accepted"));
                loadInsurances();
            } else {
                JOptionPane.showMessageDialog(parentWindow, String.format("Failed to %s '%s' :(\n", action, insId) + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentWindow, "Action cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_activeButtonActionPerformed

    private void addInsuranceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addInsuranceButtonActionPerformed
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new java.awt.Dimension(300, 30));
        limitTextFieldLength(nameField, MAX_NAME_LENGTH);
        JTextField coverageField = new JTextField();
        coverageField.setPreferredSize(new java.awt.Dimension(300, 30));
        JCheckBox acceptedCheckBox = new JCheckBox("Accepted");
        acceptedCheckBox.setSelected(true);

        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel("Insurance Name:");
        nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JLabel coverageLabel = new JLabel("Coverage Rate (%):");
        coverageLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        coverageField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        acceptedCheckBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(coverageLabel);
        panel.add(coverageField);
        panel.add(acceptedCheckBox);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Add Insurance", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            resetFieldBorder(nameField);
            resetFieldBorder(coverageField);

            String insName = nameField.getText().trim();
            String coverageText = coverageField.getText().trim();
            boolean accepted = acceptedCheckBox.isSelected();
            boolean hasError = false;

            if (insName.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Insurance name cannot be empty", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(nameField);
                hasError = true;
            } else if (insName.length() > MAX_NAME_LENGTH) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Insurance name must be " + MAX_NAME_LENGTH + " characters or fewer", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(nameField);
                hasError = true;
            }

            double coveragePercentage = 0;
            try {
                coveragePercentage = Double.parseDouble(coverageText) / 100.0;
                if (coveragePercentage < 0 || coveragePercentage > 1) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Coverage rate must be between 0 and 100", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(coverageField);
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Coverage rate must be a valid number", "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                setFieldError(coverageField);
                hasError = true;
            }

            if (hasError) {
                continue;
            }

            LinkedHashMap<String, String> msg = this.usrCtrl.getInsuranceController().createInsurance(insName, accepted, coveragePercentage);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Insurance '%s' is created!", msg.get("newInsuranceId")));
                loadInsurances();
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Failed to create insurance:\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_addInsuranceButtonActionPerformed

    private void modifyInsuranceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyInsuranceButtonActionPerformed
        int selectedRow = insuranceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first");
            return;
        }

        String insId = insuranceTable.getValueAt(selectedRow, 0).toString();
        Insurance selectedIns = this.usrCtrl.getInsuranceController().getInsuranceById(insId);

        if (selectedIns == null) {
            JOptionPane.showMessageDialog(parentWindow, "Insurance not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JRadioButton nameOption = new JRadioButton("Name");
        JRadioButton coverageOption = new JRadioButton("Coverage Rate");
        JRadioButton acceptedOption = new JRadioButton("Acceptance");
        JRadioButton allOption = new JRadioButton("All");
        ButtonGroup group = new ButtonGroup();
        group.add(nameOption);
        group.add(coverageOption);
        group.add(acceptedOption);
        group.add(allOption);
        nameOption.setSelected(true);

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new javax.swing.BoxLayout(choicePanel, javax.swing.BoxLayout.Y_AXIS));
        choicePanel.add(new JLabel("What would you like to modify?"));
        choicePanel.add(nameOption);
        choicePanel.add(coverageOption);
        choicePanel.add(acceptedOption);
        choicePanel.add(allOption);

        int choice = JOptionPane.showConfirmDialog(parentWindow, choicePanel, "Modify Insurance", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        boolean editName = nameOption.isSelected() || allOption.isSelected();
        boolean editCoverage = coverageOption.isSelected() || allOption.isSelected();
        boolean editAccepted = acceptedOption.isSelected() || allOption.isSelected();
        
        JTextField nameField = new JTextField(selectedIns.getInsuranceName());
        nameField.setPreferredSize(new java.awt.Dimension(300, 30));
        limitTextFieldLength(nameField, MAX_NAME_LENGTH);
        JTextField coverageField = new JTextField(String.valueOf(selectedIns.getCoveragePercentage() * 100));
        coverageField.setPreferredSize(new java.awt.Dimension(300, 30));
        JCheckBox acceptedCheckBox = new JCheckBox("Accepted");
        acceptedCheckBox.setSelected(selectedIns.isAccepted());

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new javax.swing.BoxLayout(fieldPanel, javax.swing.BoxLayout.Y_AXIS));
        if (editName) {
            JLabel nameLabel = new JLabel("Insurance Name:");
            nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(nameLabel);
            fieldPanel.add(nameField);
        }
        if (editCoverage) {
            JLabel coverageLabel = new JLabel("Coverage Rate (%):");
            coverageLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            coverageField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(coverageLabel);
            fieldPanel.add(coverageField);
        }
        if (editAccepted) {
            acceptedCheckBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            fieldPanel.add(acceptedCheckBox);
        }

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, fieldPanel, "Modify Insurance", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            if (editName) resetFieldBorder(nameField);
            if (editCoverage) resetFieldBorder(coverageField);

            String newName = nameField.getText().trim();
            String newCoverageText = coverageField.getText().trim();
            boolean newAccepted = acceptedCheckBox.isSelected();
            boolean hasError = false;

            if (editName) {
                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Insurance name cannot be empty", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(nameField);
                    hasError = true;
                } else if (newName.length() > MAX_NAME_LENGTH) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Insurance name must be " + MAX_NAME_LENGTH + " characters or fewer", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(nameField);
                    hasError = true;
                }
            }

            double newCoveragePercentage = selectedIns.getCoveragePercentage();
            if (editCoverage) {
                try {
                    newCoveragePercentage = Double.parseDouble(newCoverageText) / 100.0;
                    if (newCoveragePercentage < 0 || newCoveragePercentage > 1) {
                        JOptionPane.showMessageDialog(parentWindow,
                            "Coverage rate must be between 0 and 100", "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                        setFieldError(coverageField);
                        hasError = true;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Coverage rate must be a valid number", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    setFieldError(coverageField);
                    hasError = true;
                }
            }

            if (hasError) {
                continue;
            }

            String finalName = editName ? newName : selectedIns.getInsuranceName();
            double finalCoverage = editCoverage ? newCoveragePercentage : selectedIns.getCoveragePercentage();
            boolean finalAccepted = editAccepted ? newAccepted : selectedIns.isAccepted();

            LinkedHashMap<String, String> msg = this.usrCtrl.getInsuranceController().updateInsurance(selectedIns, finalName, finalAccepted, finalCoverage);
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow, String.format("Insurance '%s' updated!", insId));
                loadInsurances();
            } else {
                JOptionPane.showMessageDialog(parentWindow, "Update failed:\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_modifyInsuranceButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton activeButton;
    private javax.swing.JButton addInsuranceButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JTable insuranceTable;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton modifyInsuranceButton;
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
