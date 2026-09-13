/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.patient.rating_feedback;

import oodj_hospital_management_system.gui.patient.consultation.*;
import oodj_hospital_management_system.gui.medical_manager.billing_management.*;
import oodj_hospital_management_system.gui.admin.hospitalAssets.*;
import oodj_hospital_management_system.gui.admin.user_management.*;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.admin.dashboard.*;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class PatientRatingFeedbackPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private CardLayout cl;

    private List<Consultation> currentUnratedConsultations;
    private LinkedHashMap<String, Consultation> comboLabelToConsultation;

    private final java.awt.Color selectedColor = new java.awt.Color(230, 240, 250);
    private final java.awt.Color defaultColor = java.awt.Color.WHITE;
    private final java.awt.Color selectedTextColor = new java.awt.Color(41, 98, 255);
    private final java.awt.Color defaultTextColor = new java.awt.Color(60, 60, 60);
    /**
     * Creates new form adminDashboardPanel
     */
    public PatientRatingFeedbackPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();

        // Rating spinner: 1-5, default 3
        rating.setModel(new javax.swing.SpinnerNumberModel(3, 1, 5, 1));

        loadConsultationDropdown();

        // Cap dashboardPanel's WIDTH only, so it (and everything CardLayout/
        // BorderLayout stretch beneath it) never fills the full window width.
        // Height stays dynamic (from super.getPreferredSize()) so vertical
        // scrolling keeps working as bills are added/removed.
        javax.swing.JPanel cappedDashboard = new javax.swing.JPanel(new java.awt.BorderLayout(0, 10)) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.min(d.width, 1020), d.height);
            }
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        cappedDashboard.setBackground(dashboardPanel.getBackground());
        cappedDashboard.setBorder(dashboardPanel.getBorder());
        cappedDashboard.add(jPanel5, java.awt.BorderLayout.PAGE_START);
        cappedDashboard.add(consultationContentArea, java.awt.BorderLayout.CENTER);

        dashboardPanel = cappedDashboard;
        jScrollPane2.setViewportView(dashboardPanel);
    }
    
    private List<Consultation> getUnratedCompletedConsultations() {
        Patient activePatient = (Patient) usrCtrl.getActiveUser();
        String patientId = activePatient != null ? activePatient.getId() : null;

        List<Consultation> result = new ArrayList<>();
        if (patientId == null) return result;

        for (Consultation c : usrCtrl.getConsultationController().getAllConsultations()) {
            if (c.getConsultationStatus() != Consultation.ConsultationStatus.COMPLETED) continue;
            if (c.getPatient() == null || !patientId.equals(c.getPatient().getId())) continue;
            if (usrCtrl.getConsultationController().getFeedbackByConsultationId(c.getConsultationId()) != null) continue; // already rated

            result.add(c);
        }
        return result;
    }

    private Doctor resolveDoctor(Consultation c) {
        if (c == null || c.getConsultationSlot() == null) return null;
        if (c.getConsultationSlot().getDoctorShift() == null) return null;
        return c.getConsultationSlot().getDoctorShift().getDoctor();
    }

    private void loadConsultationDropdown() {
        currentUnratedConsultations = getUnratedCompletedConsultations();
        comboLabelToConsultation = new LinkedHashMap<>();

        for (Consultation c : currentUnratedConsultations) {
            String label = c.getConsultationId() + " - "
                + (c.getConsultationType() != null ? c.getConsultationType().getTypeName() : "Consultation");
            comboLabelToConsultation.put(label, c);
        }

        if (comboLabelToConsultation.isEmpty()) {
            consultationCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"No completed consultations to rate"}));
            consultationCombo.setEnabled(false);
            clearConsultationDetails();
            submitButton.setEnabled(false);
            return;
        }

        consultationCombo.setEnabled(true);
        submitButton.setEnabled(true);
        consultationCombo.setModel(new javax.swing.DefaultComboBoxModel<>(comboLabelToConsultation.keySet().toArray(new String[0])));
        populateConsultationDetails(getSelectedConsultation());
    }

    private Consultation getSelectedConsultation() {
        if (comboLabelToConsultation == null) return null;
        String label = (String) consultationCombo.getSelectedItem();
        return comboLabelToConsultation.get(label);
    }

    private void populateConsultationDetails(Consultation c) {
        if (c == null) {
            clearConsultationDetails();
            return;
        }

        ConsultationSlot slot = c.getConsultationSlot();
        Doctor doctor = resolveDoctor(c);

        consultationID.setText(c.getConsultationId());
        consultationType.setText(c.getConsultationType() != null ? c.getConsultationType().getTypeName() : "-");
        consultationDescription.setText(c.getDescription() != null ? c.getDescription() : "-");

        if (slot != null) {
            LocalDateTime startDt = DateTime.toDateTimeObject(slot.getStartDateTime());
            LocalDateTime endDt = DateTime.toDateTimeObject(slot.getEndDateTime());
            consultationDate.setText(startDt != null ? DateTime.toDateStringWords(DateTime.getDate(startDt)) : "-");
            consultationTime.setText(
                (startDt != null ? DateTime.toTimeString(DateTime.getTime(startDt)) : "-")
                + " - "
                + (endDt != null ? DateTime.toTimeString(DateTime.getTime(endDt)) : "-")
            );
        } else {
            consultationDate.setText("-");
            consultationTime.setText("-");
        }

        doctorName.setText(doctor != null ? doctor.getName() : "-");
        doctorID.setText(doctor != null ? doctor.getId() : "-");

        rating.setValue(3);
        feedback.setText("");
    }

    private void clearConsultationDetails() {
        consultationID.setText("-");
        consultationType.setText("-");
        consultationDescription.setText("-");
        consultationDate.setText("-");
        consultationTime.setText("-");
        doctorName.setText("-");
        doctorID.setText("-");
        feedback.setText("");
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
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        consultationContentArea = new javax.swing.JPanel();
        wrapperPane = new javax.swing.JPanel();
        chooseConsultation = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        consultationCombo = new javax.swing.JComboBox<>();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        consultationInformationPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        consultationType = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        consultationID = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        consultationDate = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        consultationTime = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        consultationDescription = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        patientInformationPanel1 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        doctorName = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        doctorID = new javax.swing.JLabel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        patientInformationPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        rating = new javax.swing.JSpinner();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        feedbackPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        feedback = new javax.swing.JTextArea();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        patientInformationPanel3 = new javax.swing.JPanel();
        submitButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1020, 0));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        dashboardPanel.setBackground(new java.awt.Color(246, 251, 255));
        dashboardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 60, 40, 60));
        dashboardPanel.setPreferredSize(new java.awt.Dimension(1020, 1200));
        dashboardPanel.setRequestFocusEnabled(false);
        dashboardPanel.setLayout(new java.awt.BorderLayout(0, 10));

        jPanel5.setBackground(new java.awt.Color(246, 251, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(1020, 100));
        jPanel5.setLayout(new java.awt.BorderLayout(0, 20));

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Rate Your Consultation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Your feedback help us improve our service.");
        jLabel1.setMaximumSize(new java.awt.Dimension(946, 45));
        jLabel1.setPreferredSize(new java.awt.Dimension(946, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel1, gridBagConstraints);

        jPanel5.add(jPanel1, java.awt.BorderLayout.NORTH);

        jSeparator1.setBackground(new java.awt.Color(246, 251, 255));
        jSeparator1.setForeground(new java.awt.Color(210, 212, 220));
        jPanel5.add(jSeparator1, java.awt.BorderLayout.PAGE_END);

        dashboardPanel.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        consultationContentArea.setBackground(new java.awt.Color(246, 251, 255));
        consultationContentArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        consultationContentArea.setLayout(new javax.swing.BoxLayout(consultationContentArea, javax.swing.BoxLayout.Y_AXIS));

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.Y_AXIS));

        chooseConsultation.setBackground(new java.awt.Color(246, 251, 255));
        chooseConsultation.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        chooseConsultation.setAlignmentY(0.0F);
        chooseConsultation.setMaximumSize(new java.awt.Dimension(2147483647, 90));
        chooseConsultation.setMinimumSize(new java.awt.Dimension(196, 90));
        chooseConsultation.setPreferredSize(new java.awt.Dimension(235, 90));
        chooseConsultation.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("CONSULTATION : ");
        chooseConsultation.add(jLabel6);

        consultationCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        consultationCombo.setPreferredSize(new java.awt.Dimension(400, 40));
        consultationCombo.addActionListener(this::consultationComboActionPerformed);
        chooseConsultation.add(consultationCombo);

        wrapperPane.add(chooseConsultation);
        wrapperPane.add(filler2);

        consultationInformationPanel.setBackground(new java.awt.Color(246, 251, 255));
        consultationInformationPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        consultationInformationPanel.setAlignmentY(0.0F);
        consultationInformationPanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        consultationInformationPanel.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("CONSULTATION TYPE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(jLabel10, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("CONSULTATION INFORMATION");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        consultationInformationPanel.add(jLabel11, gridBagConstraints);

        consultationType.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        consultationType.setText("General Consultation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(consultationType, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("CONSULTATION ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(jLabel12, gridBagConstraints);

        consultationID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        consultationID.setText("#CON12345");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(consultationID, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("DATE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(jLabel13, gridBagConstraints);

        consultationDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        consultationDate.setText("04 September 2026");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(consultationDate, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("TIME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(jLabel15, gridBagConstraints);

        consultationTime.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        consultationTime.setText("08:30 AM  - 09:00 AM");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(consultationTime, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setText("DESCRIPTION");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(jLabel18, gridBagConstraints);

        consultationDescription.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        consultationDescription.setText("xxxxxxxxxxxxxxxxxxx");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(consultationDescription, gridBagConstraints);

        wrapperPane.add(consultationInformationPanel);
        wrapperPane.add(filler3);

        patientInformationPanel1.setBackground(new java.awt.Color(246, 251, 255));
        patientInformationPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        patientInformationPanel1.setAlignmentY(0.0F);
        patientInformationPanel1.setMaximumSize(new java.awt.Dimension(2147483647, 130));
        patientInformationPanel1.setMinimumSize(new java.awt.Dimension(240, 130));
        patientInformationPanel1.setPreferredSize(new java.awt.Dimension(240, 130));
        patientInformationPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("NAME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel1.add(jLabel14, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("DOCTOR INFORMATION");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        patientInformationPanel1.add(jLabel16, gridBagConstraints);

        doctorName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        doctorName.setText("Paureen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel1.add(doctorName, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel1.add(jLabel17, gridBagConstraints);

        doctorID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        doctorID.setText("#PAT12345");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel1.add(doctorID, gridBagConstraints);

        wrapperPane.add(patientInformationPanel1);
        wrapperPane.add(filler4);

        patientInformationPanel2.setBackground(new java.awt.Color(246, 251, 255));
        patientInformationPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        patientInformationPanel2.setAlignmentY(0.0F);
        patientInformationPanel2.setMaximumSize(new java.awt.Dimension(2147483647, 90));
        patientInformationPanel2.setMinimumSize(new java.awt.Dimension(196, 90));
        patientInformationPanel2.setPreferredSize(new java.awt.Dimension(235, 90));
        patientInformationPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("RATING : ");
        patientInformationPanel2.add(jLabel7);

        rating.setPreferredSize(new java.awt.Dimension(200, 40));
        patientInformationPanel2.add(rating);

        wrapperPane.add(patientInformationPanel2);
        wrapperPane.add(filler5);

        feedbackPanel.setBackground(new java.awt.Color(246, 251, 255));
        feedbackPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        feedbackPanel.setAlignmentY(0.0F);
        feedbackPanel.setMaximumSize(new java.awt.Dimension(2147483647, 250));
        feedbackPanel.setMinimumSize(new java.awt.Dimension(196, 250));
        feedbackPanel.setPreferredSize(new java.awt.Dimension(235, 250));
        java.awt.GridBagLayout feedbackPanelLayout = new java.awt.GridBagLayout();
        feedbackPanelLayout.rowHeights = new int[] {5};
        feedbackPanel.setLayout(feedbackPanelLayout);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("FEEDBACK : ");
        jLabel8.setAlignmentY(0.0F);
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        feedbackPanel.add(jLabel8, gridBagConstraints);

        feedback.setColumns(20);
        feedback.setLineWrap(true);
        feedback.setRows(10);
        feedback.setWrapStyleWord(true);
        feedback.setMinimumSize(new java.awt.Dimension(232, 40));
        feedback.setPreferredSize(new java.awt.Dimension(232, 200));
        jScrollPane1.setViewportView(feedback);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        feedbackPanel.add(jScrollPane1, gridBagConstraints);

        wrapperPane.add(feedbackPanel);
        wrapperPane.add(filler6);
        wrapperPane.add(filler7);

        patientInformationPanel3.setBackground(new java.awt.Color(246, 251, 255));
        patientInformationPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 20));
        patientInformationPanel3.setAlignmentY(0.0F);
        patientInformationPanel3.setMaximumSize(new java.awt.Dimension(2147483647, 90));
        patientInformationPanel3.setMinimumSize(new java.awt.Dimension(196, 90));
        patientInformationPanel3.setPreferredSize(new java.awt.Dimension(235, 90));
        patientInformationPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        submitButton.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        submitButton.setText("SUBMIT");
        submitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        submitButton.setPreferredSize(new java.awt.Dimension(200, 40));
        submitButton.addActionListener(this::submitButtonActionPerformed);
        patientInformationPanel3.add(submitButton);

        wrapperPane.add(patientInformationPanel3);

        consultationContentArea.add(wrapperPane);

        dashboardPanel.add(consultationContentArea, java.awt.BorderLayout.CENTER);

        jScrollPane2.setViewportView(dashboardPanel);

        add(jScrollPane2);
    }// </editor-fold>//GEN-END:initComponents

    private void consultationComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consultationComboActionPerformed
        populateConsultationDetails(getSelectedConsultation());
    }//GEN-LAST:event_consultationComboActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        Consultation selected = getSelectedConsultation();
        if (selected == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please select a consultation to rate.", "No Selection", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int ratingValue = (Integer) rating.getValue();
        String comment = feedback.getText().trim();

        if (comment.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please write some feedback before submitting.", "Missing Feedback", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        LinkedHashMap<String, String> msg = usrCtrl.getConsultationController()
            .createFeedback(selected.getConsultationId(), (Patient) usrCtrl.getActiveUser(), ratingValue, comment);

        if (msg.get("success") != null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Thank you for your feedback!", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            loadConsultationDropdown(); // refresh: this consultation should now drop out of the list
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Failed to submit feedback.\n" + msg.get("msg"), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_submitButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chooseConsultation;
    private javax.swing.JComboBox<String> consultationCombo;
    private javax.swing.JPanel consultationContentArea;
    private javax.swing.JLabel consultationDate;
    private javax.swing.JLabel consultationDescription;
    private javax.swing.JLabel consultationID;
    private javax.swing.JPanel consultationInformationPanel;
    private javax.swing.JLabel consultationTime;
    private javax.swing.JLabel consultationType;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.JLabel doctorID;
    private javax.swing.JLabel doctorName;
    private javax.swing.JTextArea feedback;
    private javax.swing.JPanel feedbackPanel;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel patientInformationPanel1;
    private javax.swing.JPanel patientInformationPanel2;
    private javax.swing.JPanel patientInformationPanel3;
    private javax.swing.JSpinner rating;
    private javax.swing.JButton submitButton;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
