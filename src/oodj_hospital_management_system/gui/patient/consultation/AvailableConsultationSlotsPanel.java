/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.patient.consultation;

import oodj_hospital_management_system.gui.medical_manager.billing_management.*;
import oodj_hospital_management_system.gui.admin.user_management.*;
import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.gui.utils.WrappingCellRenderer;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.services.ConsultationTypeManagement;
import oodj_hospital_management_system.services.InsuranceManagement;

/**
 *
 * @author sheaw
 */
public class AvailableConsultationSlotsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private Patient currentPatient;
    private List<ConsultationSlot> currentSlots;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form PatientManagement
     */
    public AvailableConsultationSlotsPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        this.currentPatient = (Patient) usrCtrl.getActiveUser();
        initComponents();
        relabelFilterControls();
        customStyleHeader();
        loadAvailableSlots();
        populateDoctorFilter(currentSlots);
        populateDateFilter(currentSlots);
        availableSlotsTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(1160, availableSlotsTable.getRowHeight() * availableSlotsTable.getRowCount())
        );
    }
    
    private void relabelFilterControls() {
        jLabel3.setText("Doctor");
        jLabel5.setText("Date");
        jPanel4.setVisible(false); // "Department" filter - no backing data available
    }
 
    private void customStyleHeader() {
        availableSlotsTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        availableSlotsTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 45));
 
        for (int col = 0; col < availableSlotsTable.getColumnModel().getColumnCount(); col++) {
            availableSlotsTable.getColumnModel().getColumn(col).setCellRenderer(new WrappingCellRenderer());
        }
    }
 
    @Override
    public void addNotify() {
        super.addNotify();
        loadAvailableSlots();
        populateDoctorFilter(currentSlots);
        populateDateFilter(currentSlots);
    }
 
    /**
     * Loads available consultation slots across ALL doctors (not scoped to
     * a single doctor, since this panel is opened by a Patient browsing for
     * a slot to book).
     */
    public void loadAvailableSlots() {
        currentSlots = usrCtrl.getConsultationController().getAvailableConsultationSlots();
        populateTable(currentSlots);
    }
 
    private void populateTable(List<ConsultationSlot> slots) {
        DefaultTableModel model = (DefaultTableModel) availableSlotsTable.getModel();
        model.setRowCount(0);
        for (ConsultationSlot slot : slots) {
            LocalDateTime startDateTimeObj = DateTime.toDateTimeObject(slot.getStartDateTime());
            LocalDateTime endDateTimeObj = DateTime.toDateTimeObject(slot.getEndDateTime());
            LocalDate date = DateTime.getDate(startDateTimeObj);
            LocalTime startTime = DateTime.getTime(startDateTimeObj);
            LocalTime endTime = DateTime.getTime(endDateTimeObj);
 
            String dateStr = DateTime.toDateStringWords(date);
            String timeRange = DateTime.toTimeString(startTime) + " - " + DateTime.toTimeString(endTime);
            String doctorName = resolveDoctorLabel(slot);
 
            String[] rowData = {
                slot.getConsultationSlotId(),
                doctorName,
                dateStr,
                timeRange
            };
            model.addRow(rowData);
        }
    }
 
    private String resolveDoctorLabel(ConsultationSlot slot) {
        if (slot.getDoctorShift() == null || slot.getDoctorShift().getDoctor() == null) return "-";
        Doctor doctor = slot.getDoctorShift().getDoctor();
        return doctor.getId() + " - " + doctor.getName();
    }
 
    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }
 
    /**
     * Resolves the currently selected table row back to its ConsultationSlot,
     * same pattern as getSelectedAdmission() in WardAdmissionsPanel.
     */
    private ConsultationSlot getSelectedSlot() {
        int viewRow = availableSlotsTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a slot first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = availableSlotsTable.convertRowIndexToModel(viewRow);
        String slotId = (String) availableSlotsTable.getModel().getValueAt(modelRow, 0);
        for (ConsultationSlot s : currentSlots) {
            if (s.getConsultationSlotId().equals(slotId)) return s;
        }
        return null;
    }
 
    // =========================================================
    // FILTER: DOCTOR
    // =========================================================
 
    /**
     * Builds the Doctor dropdown from the doctors actually present in the
     * current available slots, not a static/placeholder list.
     */
    private void populateDoctorFilter(List<ConsultationSlot> baseSlots) {
        LinkedHashMap<String, String> labelToId = new LinkedHashMap<>();
        labelToId.put("All Doctors", null);
        for (ConsultationSlot slot : baseSlots) {
            if (slot.getDoctorShift() == null || slot.getDoctorShift().getDoctor() == null) continue;
            Doctor d = slot.getDoctorShift().getDoctor();
            if (labelToId.containsValue(d.getId())) continue;
            labelToId.put(d.getName() + " (" + d.getId() + ")", d.getId());
        }
        medicalManagerFilter.setModel(new javax.swing.DefaultComboBoxModel<>(labelToId.keySet().toArray(new String[0])));
        medicalManagerFilter.putClientProperty("labelToId", labelToId);
    }
 
    private String getSelectedDoctorId() {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, String> labelToId =
            (LinkedHashMap<String, String>) medicalManagerFilter.getClientProperty("labelToId");
        if (labelToId == null) return null;
        return labelToId.get((String) medicalManagerFilter.getSelectedItem());
    }
 
    // =========================================================
    // FILTER: DATE
    // =========================================================
 
    /**
     * Builds the Date dropdown from the distinct dates that actually have
     * available slots, sorted chronologically.
     */
    private void populateDateFilter(List<ConsultationSlot> baseSlots) {
        LinkedHashMap<String, String> labelToDate = new LinkedHashMap<>();
        labelToDate.put("All Dates", null);
 
        TreeSet<LocalDate> distinctDates = new TreeSet<>();
        for (ConsultationSlot slot : baseSlots) {
            LocalDateTime start = DateTime.toDateTimeObject(slot.getStartDateTime());
            if (start != null) distinctDates.add(DateTime.getDate(start));
        }
 
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (LocalDate date : distinctDates) {
            String label = DateTime.toDateStringWords(date);
            String iso = isoFormat.format(java.sql.Date.valueOf(date));
            labelToDate.put(label, iso);
        }
 
        medicalDepartmentFilter1.setModel(new javax.swing.DefaultComboBoxModel<>(labelToDate.keySet().toArray(new String[0])));
        medicalDepartmentFilter1.putClientProperty("labelToDate", labelToDate);
    }
 
    private String getSelectedDateIso() {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, String> labelToDate =
            (LinkedHashMap<String, String>) medicalDepartmentFilter1.getClientProperty("labelToDate");
        if (labelToDate == null) return null;
        return labelToDate.get((String) medicalDepartmentFilter1.getSelectedItem());
    }
 
    // =========================================================
    // BOOKING
    // =========================================================
 
    private void bookSlot(ConsultationSlot slot) {
        List<ConsultationType> types = ConsultationTypeManagement.getAllConsultationTypes();
        if (types == null || types.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No consultation types are configured. Cannot book.", "Cannot Proceed", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        JComboBox<ConsultationType> typeCombo = new JComboBox<>(types.toArray(new ConsultationType[0]));
        JTextField descriptionField = new JTextField();
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Doctor: " + resolveDoctorLabel(slot)));
        panel.add(new JLabel("Consultation Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Reason / Description:"));
        panel.add(descriptionField);
 
        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Book Consultation Slot", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;
 
        ConsultationType selectedType = (ConsultationType) typeCombo.getSelectedItem();
        String description = descriptionField.getText();
 
        if (description == null || description.isBlank()) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please provide a reason for the consultation.", "Missing Information", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        LinkedHashMap<String, String> msg = usrCtrl.getConsultationController()
            .createConsultation(slot, currentPatient, selectedType, description);
 
        if (msg.get("success") != null) {
            JOptionPane.showMessageDialog(parentWindow,
                "Consultation booked!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAvailableSlots();
        } else {
            JOptionPane.showMessageDialog(parentWindow,
                "Failed to book consultation.\n" + msg.get("msg"), "Error", JOptionPane.ERROR_MESSAGE);
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
        java.awt.GridBagConstraints gridBagConstraints;

        jOptionPane1 = new javax.swing.JOptionPane();
        wrapperPane = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        availableSlotsTable = new javax.swing.JTable();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        operationsPanel1 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        bookButton = new javax.swing.JButton();
        filterPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        medicalManagerFilter = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        medicalDepartmentFilter = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 5), new java.awt.Dimension(32767, 5));
        medicalDepartmentFilter1 = new javax.swing.JComboBox<>();
        searchFilterButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        resetFilterButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1160, 2147483647));
        setLayout(new java.awt.BorderLayout());

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setBackground(new java.awt.Color(246, 251, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 40));
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new java.awt.BorderLayout());

        tableScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        tableScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        availableSlotsTable.setBackground(new java.awt.Color(255, 255, 255));
        availableSlotsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Consultation Slot ID", "Doctor", "Date", "Time"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        availableSlotsTable.setAlignmentY(0.0F);
        availableSlotsTable.setRowHeight(40);
        availableSlotsTable.setRowMargin(5);
        tableScrollPane.setViewportView(availableSlotsTable);

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

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        jPanel6.setPreferredSize(new java.awt.Dimension(566, 140));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.Y_AXIS));

        bookButton.setText("Book");
        bookButton.setMaximumSize(new java.awt.Dimension(150, 40));
        bookButton.setPreferredSize(new java.awt.Dimension(150, 40));
        bookButton.addActionListener(this::bookButtonActionPerformed);
        jPanel6.add(bookButton);

        operationsPanel1.add(jPanel6);

        wrapperPane.add(operationsPanel1);

        add(wrapperPane, java.awt.BorderLayout.CENTER);

        filterPanel.setBackground(new java.awt.Color(246, 251, 255));
        filterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 50, 0, 50));
        filterPanel.setPreferredSize(new java.awt.Dimension(16897, 100));
        filterPanel.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBackground(new java.awt.Color(246, 251, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 20));
        jPanel3.setAlignmentX(0.0F);
        jPanel3.setMaximumSize(new java.awt.Dimension(250, 50));
        jPanel3.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jLabel3.setBackground(new java.awt.Color(246, 251, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Medical Manager");
        jLabel3.setAlignmentX(1.0F);
        jLabel3.setAlignmentY(0.0F);
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel3.setIconTextGap(0);
        jLabel3.setMaximumSize(new java.awt.Dimension(280, 16));
        jLabel3.setMinimumSize(new java.awt.Dimension(300, 16));
        jLabel3.setPreferredSize(new java.awt.Dimension(300, 16));
        jPanel3.add(jLabel3);
        jPanel3.add(filler1);

        medicalManagerFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(medicalManagerFilter);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(jPanel3, gridBagConstraints);

        jPanel4.setBackground(new java.awt.Color(246, 251, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 20));
        jPanel4.setAlignmentX(0.0F);
        jPanel4.setMaximumSize(new java.awt.Dimension(250, 50));
        jPanel4.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        jLabel4.setBackground(new java.awt.Color(246, 251, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Department");
        jLabel4.setAlignmentX(1.0F);
        jLabel4.setAlignmentY(0.0F);
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel4.setIconTextGap(0);
        jLabel4.setMaximumSize(new java.awt.Dimension(250, 16));
        jLabel4.setPreferredSize(new java.awt.Dimension(250, 16));
        jPanel4.add(jLabel4);
        jPanel4.add(filler2);

        medicalDepartmentFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel4.add(medicalDepartmentFilter);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(jPanel4, gridBagConstraints);

        jPanel5.setBackground(new java.awt.Color(246, 251, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 20));
        jPanel5.setAlignmentX(0.0F);
        jPanel5.setMaximumSize(new java.awt.Dimension(250, 50));
        jPanel5.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Date");
        jLabel5.setAlignmentX(1.0F);
        jLabel5.setAlignmentY(0.0F);
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel5.setIconTextGap(0);
        jLabel5.setMaximumSize(new java.awt.Dimension(250, 16));
        jLabel5.setPreferredSize(new java.awt.Dimension(250, 16));
        jPanel5.add(jLabel5);
        jPanel5.add(filler3);

        medicalDepartmentFilter1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel5.add(medicalDepartmentFilter1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        filterPanel.add(jPanel5, gridBagConstraints);

        searchFilterButton.setText("Search");
        searchFilterButton.addActionListener(this::searchFilterButtonActionPerformed);
        filterPanel.add(searchFilterButton, new java.awt.GridBagConstraints());
        filterPanel.add(filler4, new java.awt.GridBagConstraints());

        resetFilterButton.setText("Reset");
        resetFilterButton.addActionListener(this::resetFilterButtonActionPerformed);
        filterPanel.add(resetFilterButton, new java.awt.GridBagConstraints());

        add(filterPanel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFilterButtonActionPerformed
        List<ConsultationSlot> base = usrCtrl.getConsultationController().getAvailableConsultationSlots();

        String doctorId = getSelectedDoctorId();
        String dateIso = getSelectedDateIso();

        List<ConsultationSlot> filtered = new ArrayList<>();
        for (ConsultationSlot slot : base) {
            boolean matchesDoctor = doctorId == null
                || (slot.getDoctorShift() != null && slot.getDoctorShift().getDoctor() != null
                    && doctorId.equals(slot.getDoctorShift().getDoctor().getId()));

            boolean matchesDate = true;
            if (dateIso != null) {
                LocalDateTime start = DateTime.toDateTimeObject(slot.getStartDateTime());
                matchesDate = start != null && DateTime.getDate(start).toString().equals(dateIso);
            }

            if (matchesDoctor && matchesDate) {
                filtered.add(slot);
            }
        }

        currentSlots = filtered;
        populateTable(filtered);

        String doctorLabel = (String) medicalManagerFilter.getSelectedItem();
        String dateLabel = (String) medicalDepartmentFilter1.getSelectedItem();
        updateResultLabel("Showing results for Doctor: \"" + doctorLabel + "\", Date: \"" + dateLabel + "\"", filtered.size());
    }//GEN-LAST:event_searchFilterButtonActionPerformed

    private void resetFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetFilterButtonActionPerformed
        loadAvailableSlots();
        populateDoctorFilter(currentSlots);
        populateDateFilter(currentSlots);
        medicalManagerFilter.setSelectedIndex(0);
        medicalDepartmentFilter1.setSelectedIndex(0);
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetFilterButtonActionPerformed

    private void bookButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookButtonActionPerformed
        ConsultationSlot selected = getSelectedSlot();
        if (selected == null) return;
 
        bookSlot(selected);
    }

        private void endButtonActionPerformed() {

    }//GEN-LAST:event_bookButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable availableSlotsTable;
    private javax.swing.JButton bookButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JComboBox<String> medicalDepartmentFilter;
    private javax.swing.JComboBox<String> medicalDepartmentFilter1;
    private javax.swing.JComboBox<String> medicalManagerFilter;
    private javax.swing.JLabel operationsLabel1;
    private javax.swing.JPanel operationsPanel1;
    private javax.swing.JButton resetFilterButton;
    private javax.swing.JButton searchFilterButton;
    private javax.swing.JLabel showResultsForLabel;
    private javax.swing.JPanel showResultsForPanel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
