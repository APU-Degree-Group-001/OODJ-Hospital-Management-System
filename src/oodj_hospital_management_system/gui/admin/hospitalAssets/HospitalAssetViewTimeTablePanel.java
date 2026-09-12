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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
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
import oodj_hospital_management_system.services.UserManagement;
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

        if (!isAdminView()) {
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
        String id = asset.getAssetId();

        // Consultation info
        assetId.setText(id);
        assetType.setText(asset.getAssetType());
        assetName.setText(asset.getAssetName());

        loadAssetAllocations(id);
    }

    private void loadAssetAllocations(String id) {
        List<HospitalAssetAllocation> allocation = usrCtrl.getHospitalAssetController()
            .getHospitalAssetAllocationsByAssetId(id);
 
        this.currentAllocations = allocation != null ? allocation : new ArrayList<>();
 
        populateTimeTable(this.currentAllocations);
        
        System.out.println("Asset ID: " + id);
        System.out.println("Allocations found: " +
                (allocation == null ? "NULL" : allocation.size()));
 
        timetablePanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, timetablePanel.getPreferredSize().height));
 
        timetablePanel.revalidate();
        timetablePanel.repaint();
        wrapperPane.revalidate();
        wrapperPane.repaint();
    }
    
    private boolean isAdminView() {
        return usrCtrl.getActiveUser() != null
            && "admin staff".equalsIgnoreCase(usrCtrl.getActiveUser().getType());
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
        Doctor d = this.usrCtrl.getDoctorById(doctorId);
        return doctorId + " - " + (d != null ? d.getName() : "Unknown");
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
    
    private List<HospitalAssetAllocation> getBaseAllocations() {
        List<HospitalAssetAllocation> base = usrCtrl.getHospitalAssetController()
            .getHospitalAssetAllocationsByAssetId(asset.getAssetId());
        return base != null ? base : new ArrayList<>();
    }
 
    private LinkedHashMap<String, String> selectDateRange() {
        JDateChooser startChooser = new JDateChooser();
        JDateChooser endChooser = new JDateChooser();
        startChooser.setDateFormatString("yyyy-MM-dd");
        endChooser.setDateFormatString("yyyy-MM-dd");
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("From:"));
        panel.add(startChooser);
        panel.add(new JLabel("To:"));
        panel.add(endChooser);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Select Date Range", JOptionPane.OK_CANCEL_OPTION);
 
        LinkedHashMap<String, String> range = new LinkedHashMap<>();
        if (result != JOptionPane.OK_OPTION) {
            return range; // both entries null -> caller treats as "cancelled"
        }
 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = startChooser.getDate();
        Date endDate = endChooser.getDate();
        range.put("startDate", startDate != null ? sdf.format(startDate) : null);
        range.put("endDate", endDate != null ? sdf.format(endDate) : null);
        return range;
    }
 
    private List<HospitalAssetAllocation> filterByScheduledDateRange(
            List<HospitalAssetAllocation> base, String startDate, String endDate) {
        List<HospitalAssetAllocation> filtered = new ArrayList<>();
        for (HospitalAssetAllocation a : base) {
            String raw = a.getScheduledStartDateTime();
            if (raw == null || raw.isEmpty()) continue;
            LocalDate date = DateTime.toDateTimeObject(raw).toLocalDate();
            if (startDate != null && date.isBefore(LocalDate.parse(startDate))) continue;
            if (endDate != null && date.isAfter(LocalDate.parse(endDate))) continue;
            filtered.add(a);
        }
        return filtered;
    }
 
    private List<HospitalAssetAllocation> filterByActualDateRange(
            List<HospitalAssetAllocation> base, String startDate, String endDate) {
        List<HospitalAssetAllocation> filtered = new ArrayList<>();
        for (HospitalAssetAllocation a : base) {
            String raw = a.getAllocationDateTime();
            if (raw == null || raw.isEmpty()) continue;
            LocalDate date = DateTime.toDateTimeObject(raw).toLocalDate();
            if (startDate != null && date.isBefore(LocalDate.parse(startDate))) continue;
            if (endDate != null && date.isAfter(LocalDate.parse(endDate))) continue;
            filtered.add(a);
        }
        return filtered;
    }
 
    private String selectAllocationStatus() {
        String[] statuses = {"Scheduled", "In Progress", "Overrunning", "Overdue", "Completed"};
        return (String) JOptionPane.showInputDialog(parentWindow, "Select status:",
            "Filter by Status", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
    }
 
    private List<HospitalAssetAllocation> filterByStatus(List<HospitalAssetAllocation> base, String status) {
        List<HospitalAssetAllocation> filtered = new ArrayList<>();
        for (HospitalAssetAllocation a : base) {
            if (getAllocationStatus(a).equals(status)) filtered.add(a);
        }
        return filtered;
    }
 
    private String selectDoctor(List<HospitalAssetAllocation> base) {
        // why?
        LinkedHashSet<String> doctorIds = new LinkedHashSet<>();
        for (HospitalAssetAllocation a : base) doctorIds.add(a.getDoctorId());
 
        if (doctorIds.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No doctors found for this asset.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
 
        LinkedHashMap<String, String> labelToId = new LinkedHashMap<>();
        for (String id : doctorIds) {
            Doctor d = usrCtrl.getDoctorById(id);
            labelToId.put(id + " - " + (d != null ? d.getName() : "Unknown"), id);
        }
 
        String[] labels = labelToId.keySet().toArray(new String[0]);
        String choice = (String) JOptionPane.showInputDialog(parentWindow, "Select doctor:",
            "Filter by Doctor", JOptionPane.QUESTION_MESSAGE, null, labels, labels[0]);
 
        return choice != null ? labelToId.get(choice) : null;
    }
 
    private List<HospitalAssetAllocation> filterByDoctor(List<HospitalAssetAllocation> base, String doctorId) {
        List<HospitalAssetAllocation> filtered = new ArrayList<>();
        for (HospitalAssetAllocation a : base) {
            if (doctorId.equals(a.getDoctorId())) filtered.add(a);
        }
        return filtered;
    }
    
    private LocalDateTime combineDateAndTime(JDateChooser dateChooser, JSpinner timeSpinner) {
        Date datePart = dateChooser.getDate();
        if (datePart == null) return null;

        Date timePart = (Date) timeSpinner.getValue();

        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(datePart);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(timePart);

        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, 0);

        return LocalDateTime.ofInstant(dateCal.toInstant(), java.time.ZoneId.systemDefault());
    }
    
    private JComboBox<Doctor> buildDoctorComboBox(String preselectDoctorId) {
        List<Doctor> doctors = UserManagement.getAllDoctors().stream()
            .filter(d -> d instanceof Doctor)
            .map(d -> (Doctor) d)
            .toList();

        if (doctors.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No active doctors are available.", "Cannot Proceed", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        JComboBox<Doctor> doctorCombo = new JComboBox<>(doctors.toArray(new Doctor[0]));
        doctorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doctor) {
                    setText(((Doctor) value).getName());
                }
                return this;
            }
        });

        if (preselectDoctorId != null) {
            for (Doctor d : doctors) {
                if (d.getId().equals(preselectDoctorId)) {
                    doctorCombo.setSelectedItem(d);
                    break;
                }
            }
        }

        return doctorCombo;
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
        resetButton = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
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

        resetButton.setText("Reset Table");
        resetButton.addActionListener(this::resetButtonActionPerformed);
        sortByPane.add(resetButton);
        sortByPane.add(filler5);

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
            "Delete this allocation? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
 
        String success = usrCtrl.getHospitalAssetController().deleteHospitalAssetAllocation(selected.getAllocationId()).get("success");
        if (success != null) {
            JOptionPane.showMessageDialog(parentWindow,
                "Allocation deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAssetDetails();
        } else {
            JOptionPane.showMessageDialog(parentWindow,
                "Failed to delete allocation.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void allocateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allocateButtonActionPerformed
        JComboBox<Doctor> doctorCombo = buildDoctorComboBox(null);
        if (doctorCombo == null) return;

        JDateChooser startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        JSpinner startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));

        JDateChooser endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        JSpinner endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Doctor:"));
        panel.add(doctorCombo);
        panel.add(new JLabel("Scheduled Start Date:"));
        panel.add(startDateChooser);
        panel.add(new JLabel("Scheduled Start Time:"));
        panel.add(startTimeSpinner);
        panel.add(new JLabel("Scheduled End Date:"));
        panel.add(endDateChooser);
        panel.add(new JLabel("Scheduled End Time:"));
        panel.add(endTimeSpinner);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Allocate Asset", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
            LocalDateTime startDateTime = combineDateAndTime(startDateChooser, startTimeSpinner);
            LocalDateTime endDateTime = combineDateAndTime(endDateChooser, endTimeSpinner);

            if (selectedDoctor == null || startDateTime == null || endDateTime == null) {
                JOptionPane.showMessageDialog(parentWindow,
                    "All fields are required.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!endDateTime.isAfter(startDateTime)) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Scheduled end must be after scheduled start.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            LinkedHashMap<String, String> msg =
            usrCtrl.getHospitalAssetController().createHospitalAssetAllocation(
                    asset.getAssetId(),
                    selectedDoctor.getId(),
                    DateTime.formattedLocalDateTime(startDateTime),
                    DateTime.formattedLocalDateTime(endDateTime),
                    DateTime.formattedLocalDateTime(LocalDateTime.now())
            );

            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow,
                        "Allocation added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssetDetails();
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                        "Failed to add allocation.\n" + msg.get("msg"),
                        "Error", JOptionPane.ERROR_MESSAGE);
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
        populateTimeTable(sorted);
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();
        List<HospitalAssetAllocation> base = getBaseAllocations();
 
        switch (selected) {
            case "Doctor": {
                String doctorId = selectDoctor(base);
                if (doctorId != null) {
                    currentAllocations = filterByDoctor(base, doctorId);
                    populateTimeTable(currentAllocations);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Scheduled Date Time": {
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    currentAllocations = filterByScheduledDateRange(base, range.get("startDate"), range.get("endDate"));
                    populateTimeTable(currentAllocations);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Actual Date Time": {
                LinkedHashMap<String, String> range = selectDateRange();
                if (range.get("startDate") != null || range.get("endDate") != null) {
                    currentAllocations = filterByActualDateRange(base, range.get("startDate"), range.get("endDate"));
                    populateTimeTable(currentAllocations);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            case "Status": {
                String status = selectAllocationStatus();
                if (status != null) {
                    currentAllocations = filterByStatus(base, status);
                    populateTimeTable(currentAllocations);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }
 
            default: // "Default" -> no filter
                currentAllocations = base;
                populateTimeTable(base);
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        HospitalAssetAllocation selected = getSelectedAllocation();
        if (selected == null) return;

        JComboBox<Doctor> doctorCombo = buildDoctorComboBox(selected.getDoctorId());
        if (doctorCombo == null) return;

        JDateChooser startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        JSpinner startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));

        JDateChooser endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        JSpinner endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        if (selected.getScheduledStartDateTime() != null && !selected.getScheduledStartDateTime().isEmpty()) {
            LocalDateTime startDt = DateTime.toDateTimeObject(selected.getScheduledStartDateTime());
            Date asDate = Date.from(startDt.atZone(java.time.ZoneId.systemDefault()).toInstant());
            startDateChooser.setDate(asDate);
            startTimeSpinner.setValue(asDate);
        }
        if (selected.getScheduledEndDateTime() != null && !selected.getScheduledEndDateTime().isEmpty()) {
            LocalDateTime endDt = DateTime.toDateTimeObject(selected.getScheduledEndDateTime());
            Date asDate = Date.from(endDt.atZone(java.time.ZoneId.systemDefault()).toInstant());
            endDateChooser.setDate(asDate);
            endTimeSpinner.setValue(asDate);
        }

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Doctor:"));
        panel.add(doctorCombo);
        panel.add(new JLabel("Scheduled Start Date:"));
        panel.add(startDateChooser);
        panel.add(new JLabel("Scheduled Start Time:"));
        panel.add(startTimeSpinner);
        panel.add(new JLabel("Scheduled End Date:"));
        panel.add(endDateChooser);
        panel.add(new JLabel("Scheduled End Time:"));
        panel.add(endTimeSpinner);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Edit Allocation", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
            LocalDateTime startDt = combineDateAndTime(startDateChooser, startTimeSpinner);
            LocalDateTime endDt = combineDateAndTime(endDateChooser, endTimeSpinner);

            if (selectedDoctor == null || startDt == null || endDt == null) {
                JOptionPane.showMessageDialog(parentWindow,
                    "All fields are required.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!endDt.isAfter(startDt)) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Scheduled end must be after scheduled start.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String allocationId = selected.getAllocationId();
            LinkedHashMap<String, String> scheduleMsg = usrCtrl.getHospitalAssetController()
                .setHospitalAssetAllocationSchedule(allocationId,
                    DateTime.formattedLocalDateTime(startDt),
                    DateTime.formattedLocalDateTime(endDt));

            boolean success = scheduleMsg.get("success") != null;

            if (success) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Allocation updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssetAllocations(asset.getAssetId());
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    "Some fields failed to update. Check the log for details.",
                    "Partial Failure", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        loadAssetAllocations(asset.getAssetId());
    }//GEN-LAST:event_resetButtonActionPerformed


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
    private javax.swing.Box.Filler filler5;
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
    private javax.swing.JButton resetButton;
    private javax.swing.JLabel sortByLabel;
    private javax.swing.JLabel sortByLabel1;
    private javax.swing.JPanel sortByPane;
    private javax.swing.JComboBox<String> sortField;
    private javax.swing.JTable timeTable;
    private javax.swing.JPanel timetablePanel;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
