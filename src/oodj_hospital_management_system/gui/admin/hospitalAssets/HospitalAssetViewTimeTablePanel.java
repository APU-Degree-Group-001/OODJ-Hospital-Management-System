/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.admin.hospitalAssets;

import oodj_hospital_management_system.gui.doctor.consultation.*;
import oodj_hospital_management_system.gui.medical_manager.billing_management.*;
import com.toedter.calendar.JDateChooser;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.utils.WrappingCellRenderer;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.HospitalAssetAllocation;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.LabResultType;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.PrescriptionType;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.BillingManagement;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class HospitalAssetViewTimeTablePanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private HospitalAsset asset;
    private List<HospitalAssetAllocation> currentAllocations;
    private Runnable onBack;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form PatientManagement
     */
    public HospitalAssetViewTimeTablePanel(UserController usrCtrl, HospitalAsset asset, Runnable onBack) {
        this.usrCtrl = usrCtrl;
        this.asset = asset;
        this.onBack = onBack;
        initComponents();
        applyWrappingRenderers();

        if (isPatientView()) {
            allocationCrudPanel.setVisible(false);
        }

        loadAssetDetails();

        backButton.addActionListener(e -> {
            if (this.onBack != null) this.onBack.run();
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadAssetDetails();
            }
        });
    }
    
    private void loadAssetDetails() {
        if (asset == null) return;
        String assetId = asset.getAssetId();

        // Consultation info
        assetId.setText(assetId);
        assetType.setText(asset.getAssetType());
        assetName.setText(asset.getAssetName());

        loadAssetAllocation(assetId);
    }

    private LocalDateTime resolveConsultationDateTime(Consultation c) {
        if (c == null || c.getConsultationSlot() == null) return null;
        String raw = c.getConsultationSlot().getStartDateTime();
        if (raw == null || raw.isBlank()) return null;
        try {
            return DateTime.toDateTimeObject(raw);
        } catch (Exception e) {
            return null;
        }
    }

    private void loadAssetAllocation(String assetId) {
        List<HospitalAssetAllocation> allocation = usrCtrl.getHospitalAssetController()
            .getHospitalAssetAllocationsByAssetId(assetId);

        this.currentAllocation = allocation;

        boolean isPatient = isPatientView();

        populateTimeTable(allocation != null ? allocation : new ArrayList<>());

        timetablePanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, timetablePanel.getPreferredSize().height));

        timetablePanel.revalidate();
        timetablePanel.repaint();
        wrapperPane.revalidate();
        wrapperPane.repaint();
    }
    
    private boolean isPatientView() {
        return usrCtrl.getActiveUser() != null
            && "patient".equalsIgnoreCase(usrCtrl.getActiveUser().getType());
    }

    private void populateTimeTable(List<HospitalAssetAllocation> allocation) {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) timeTable.getModel();
        model.setRowCount(0);
        for (HospitalAssetAllocation a : allocation) {
            model.addRow(new Object[]{
                a.getAllocationId(),
                getDoctorLabel(a.getDoctorId()),
                a.getScheduledStartDateTime(),
                a.getScheduledEndDateTime(),
                getAllocationStatus(a)
            });
        }
    }
    
    private String getDoctorLabel(String doctorId) {
        return doctorId + " - " + this.usrCtrl.getDoctorById(doctorId).getName();
    }
    
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
    
    private HospitalAssetAllocation getSelectedAllocation() {
        int viewRow = timeTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select an allocation first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = timeTable.convertRowIndexToModel(viewRow);
        String allocationId = (String) timeTable.getModel().getValueAt(modelRow, 0);
        for (HospitalAssetAllocation a : currentAllocations) {
            if (a.getAllocationId().equals(allocationId)) return a;
        }
        return null;
    }
    
    private void applyWrappingRenderers() {
        timeTable.getColumnModel().getColumn(2).setCellRenderer(new WrappingCellRenderer());
        timeTable.getColumnModel().getColumn(3).setCellRenderer(new WrappingCellRenderer());
        timeTable.getColumnModel().getColumn(4).setCellRenderer(new WrappingCellRenderer());
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

        jOptionPane1 = new javax.swing.JOptionPane();
        jPanel1 = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        wrapperPane = new javax.swing.JPanel();
        patientInformationPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        assetType = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        assetId = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        assetName = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        timetablePanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        timeTable = new javax.swing.JTable();
        allocationCrudPanel = new javax.swing.JPanel();
        allocateButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        sortByPane = new javax.swing.JPanel();
        sortByLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JComboBox<>();
        sortByLabel1 = new javax.swing.JLabel();
        filterByField = new javax.swing.JComboBox<>();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1160, 2147483647));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1160, 50));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        backButton.setText("⟵ Back");
        backButton.setMaximumSize(new java.awt.Dimension(150, 40));
        backButton.setPreferredSize(new java.awt.Dimension(150, 40));
        backButton.addActionListener(this::backButtonActionPerformed);
        jPanel1.add(backButton);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.Y_AXIS));

        patientInformationPanel.setBackground(new java.awt.Color(246, 251, 255));
        patientInformationPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        patientInformationPanel.setAlignmentY(0.0F);
        patientInformationPanel.setMaximumSize(new java.awt.Dimension(2147483647, 140));
        patientInformationPanel.setMinimumSize(new java.awt.Dimension(196, 140));
        patientInformationPanel.setPreferredSize(new java.awt.Dimension(235, 140));
        patientInformationPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("TYPE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("ASSET");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        patientInformationPanel.add(jLabel2, gridBagConstraints);

        assetType.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        assetType.setText("TAN");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(assetType, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(jLabel4, gridBagConstraints);

        assetId.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        assetId.setText("#PAT12345");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(assetId, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("NAME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(jLabel3, gridBagConstraints);

        assetName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        assetName.setText("Paureen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(assetName, gridBagConstraints);

        wrapperPane.add(patientInformationPanel);
        wrapperPane.add(filler2);
        wrapperPane.add(filler1);
        wrapperPane.add(filler3);

        timetablePanel.setBackground(new java.awt.Color(246, 251, 255));
        timetablePanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        timetablePanel.setAlignmentY(0.0F);
        timetablePanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        timetablePanel.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("TIMETABLE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        timetablePanel.add(jLabel10, gridBagConstraints);

        timeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Allocation ID", "Doctor", "Schedule Start", "Schedule End", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(timeTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        timetablePanel.add(jScrollPane1, gridBagConstraints);

        allocationCrudPanel.setBackground(new java.awt.Color(255, 255, 255));
        allocationCrudPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));

        allocateButton.setText("Allocate");
        allocateButton.addActionListener(this::allocateButtonActionPerformed);
        allocationCrudPanel.add(allocateButton);

        editButton.setText("Edit");
        editButton.addActionListener(this::editButtonActionPerformed);
        allocationCrudPanel.add(editButton);

        removeButton.setText("Remove");
        removeButton.addActionListener(this::removeButtonActionPerformed);
        allocationCrudPanel.add(removeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        timetablePanel.add(allocationCrudPanel, gridBagConstraints);

        sortByPane.setBackground(new java.awt.Color(246, 251, 255));
        sortByPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));

        sortByLabel.setText("Sort By");
        sortByPane.add(sortByLabel);

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "ID", "Scheduled Date Time" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Doctor", "Scheduled Date Time", "Status", " " }));
        filterByField.setPreferredSize(new java.awt.Dimension(150, 30));
        filterByField.setRequestFocusEnabled(false);
        filterByField.addActionListener(this::filterByFieldActionPerformed);
        sortByPane.add(filterByField);

        timetablePanel.add(sortByPane, new java.awt.GridBagConstraints());

        wrapperPane.add(timetablePanel);
        wrapperPane.add(filler4);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        HospitalAssetAllocation selected = getSelectedAllocation();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Delete this medical assessment? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = usrCtrl.getMedicalRecordController().deleteMedicalAssessment(selected);
        if (success) {
            JOptionPane.showMessageDialog(parentWindow,
                "Medical assessment deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadConsultationDetails();
        } else {
            JOptionPane.showMessageDialog(parentWindow,
                "Failed to delete medical assessment.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void allocateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allocateButtonActionPerformed
    JTextField typeField = new JTextField(20);
        JTextField findingsField = new JTextField(20);
        JTextField diagnosisField = new JTextField(20);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Assessment Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Findings:"));
        panel.add(findingsField);
        panel.add(new JLabel("Diagnosis:"));
        panel.add(diagnosisField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Add Medical Assessment", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String type = typeField.getText().trim();
            String findings = findingsField.getText().trim();
            String diagnosis = diagnosisField.getText().trim();

            if (type.isEmpty() || findings.isEmpty() || diagnosis.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "All fields are required.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String newAssessmentId = oodj_hospital_management_system.services.MedicalRecordManagement
                .generateMedicalAssessmentId();
            MedicalAssessment newAssessment = new MedicalAssessment(
                newAssessmentId, consultation.getConsultationId(), type, findings, diagnosis);

            boolean success = usrCtrl.getMedicalRecordController().createMedicalAssessment(newAssessment);
            if (success) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Medical assessment added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadConsultationDetails();
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    "Failed to add medical assessment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_allocateButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if (this.onBack != null) {
            this.onBack.run();
        }
    }//GEN-LAST:event_backButtonActionPerformed

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

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allocateButton;
    private javax.swing.JPanel allocationCrudPanel;
    private javax.swing.JLabel assetId;
    private javax.swing.JLabel assetName;
    private javax.swing.JLabel assetType;
    private javax.swing.JButton backButton;
    private javax.swing.JButton editButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel patientInformationPanel;
    private javax.swing.JButton removeButton;
    private javax.swing.JLabel sortByLabel;
    private javax.swing.JLabel sortByLabel1;
    private javax.swing.JPanel sortByPane;
    private javax.swing.JComboBox<String> sortField;
    private javax.swing.JTable timeTable;
    private javax.swing.JPanel timetablePanel;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
