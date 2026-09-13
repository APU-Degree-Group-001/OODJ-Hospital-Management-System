/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.patient.consultation;

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
public class ConsultationViewDetailsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private Consultation consultation;
    private List<MedicalAssessment> currentAssessments;
    private List<LabResult> currentLabResults;
    private List<Prescription> currentPrescriptions;
    private Runnable onBack;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form PatientManagement
     */
    public ConsultationViewDetailsPanel(UserController usrCtrl, Consultation consultation, Runnable onBack) {
        this.usrCtrl = usrCtrl;
        this.consultation = consultation;
        this.onBack = onBack;
        initComponents();
        applyWrappingRenderers();

        if (isPatientView()) {
            medicalAssessmentCrudPanel.setVisible(false);
            labResultsCrudPanel.setVisible(false);
            prescriptionsCrudPanel.setVisible(false);
        }
        
        if (loadBill(consultation.getConsultationId())) {
            medicalAssessmentCrudPanel.setVisible(false);
            labResultsCrudPanel.setVisible(false);
            prescriptionsCrudPanel.setVisible(false);
        }

        loadConsultationDetails();

        backButton.addActionListener(e -> {
            if (this.onBack != null) this.onBack.run();
        });

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadConsultationDetails();
            }
        });
    }
    
    private void loadConsultationDetails() {
        if (consultation == null) return;
        String consultationId = consultation.getConsultationId();

        // --- Patient info ---
        Patient patient = consultation.getPatient(); // confirm: eager ref vs lazy patientId lookup
        if (patient != null) {
            patientName.setText(patient.getName());
            patientID.setText(patient.getId());
        }

        // --- Doctor info ---
        Doctor doctor = resolveDoctor(consultation);
        if (doctor != null) {
            doctorName.setText(doctor.getName());
            doctorID.setText(doctor.getId());
        }

        // --- Consultation info ---
        consultationID.setText(consultationId);
        ConsultationType type = consultation.getConsultationType();
        consultationType.setText(type != null ? type.getTypeName() : "N/A");
        consultationDescription.setText(consultation.getDescription());

        LocalDateTime dt = resolveConsultationDateTime(consultation);
        if (dt != null) {
            consultationDate.setText(dt.toLocalDate()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")));
            consultationTime.setText(dt.toLocalTime()
                .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));
        }

        loadMedicalRecords(consultationId);
    }
    
    private Doctor resolveDoctor(Consultation c) {
        if (c == null || c.getConsultationSlot() == null) return null;
        if (c.getConsultationSlot().getDoctorShift() == null) return null;
        return c.getConsultationSlot().getDoctorShift().getDoctor();
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

    private void loadMedicalRecords(String consultationId) {
        List<MedicalAssessment> assessments = usrCtrl.getMedicalRecordController()
            .getMedicalAssessmentsByConsultationId(consultationId);
        List<LabResult> labResults = usrCtrl.getMedicalRecordController()
            .getLabResultsByConsultationId(consultationId);
        List<Prescription> prescriptions = usrCtrl.getMedicalRecordController()
            .getPrescriptionsByConsultationId(consultationId);

        this.currentAssessments = assessments;
        this.currentLabResults = labResults;
        this.currentPrescriptions = prescriptions;

        boolean isPatient = isPatientView();
        boolean hasAssessment = assessments != null && !assessments.isEmpty();
        boolean hasLabResults = labResults != null && !labResults.isEmpty();
        boolean hasPrescriptions = prescriptions != null && !prescriptions.isEmpty();

        boolean showAssessmentSection = isPatient ? hasAssessment : true;
        jLabel12.setVisible(showAssessmentSection);
        jScrollPane1.setVisible(showAssessmentSection);
        populateAssessmentsTable(assessments != null ? assessments : new ArrayList<>());

        boolean showLabResultsSection = isPatient ? hasLabResults : true;
        jLabel17.setVisible(showLabResultsSection);
        jScrollPane2.setVisible(showLabResultsSection);
        populateLabResultsTable(labResults != null ? labResults : new ArrayList<>());

        boolean showPrescriptionsSection = isPatient ? hasPrescriptions : true;
        jLabel19.setVisible(showPrescriptionsSection);
        jScrollPane3.setVisible(showPrescriptionsSection);
        populatePrescriptionsTable(prescriptions != null ? prescriptions : new ArrayList<>());

        boolean hasAnyRecords = showAssessmentSection || showLabResultsSection || showPrescriptionsSection;
        jLabel10.setVisible(hasAnyRecords);
        medicalRecordsPanel.setVisible(hasAnyRecords);

        medicalRecordsPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, medicalRecordsPanel.getPreferredSize().height));

        medicalRecordsPanel.revalidate();
        medicalRecordsPanel.repaint();
        wrapperPane.revalidate();
        wrapperPane.repaint();
    }
    
    private boolean isPatientView() {
        return usrCtrl.getActiveUser() != null
            && "patient".equalsIgnoreCase(usrCtrl.getActiveUser().getType());
    }

    private void populateAssessmentsTable(List<MedicalAssessment> assessments) {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) medicalAssessmentsTable.getModel();
        model.setRowCount(0);
        for (MedicalAssessment assessment : assessments) {
            model.addRow(new Object[]{
                assessment.getAssessmentId(),
                assessment.getAssessmentType(),
                assessment.getFindings(),
                assessment.getDiagnosis()
            });
        }
    }

    private void populateLabResultsTable(List<LabResult> labResults) {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) labResultsTable.getModel();
        model.setRowCount(0);
        for (LabResult r : labResults) {
            model.addRow(new Object[]{
                r.getResultId(),
                r.getResultType(),
                r.getResult(),
                r.getRemarks(),
                r.getResultDateTime()   // swapped in for "Modified By" - that field doesn't exist on LabResult
            });
        }
    }

    private void populatePrescriptionsTable(List<Prescription> prescriptions) {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) prescriptionsTable.getModel();
        model.setRowCount(0);
        for (Prescription p : prescriptions) {
            model.addRow(new Object[]{
                p.getPrescriptionId(),
                p.getMedicineName(),   // "Prescription Type" column, but model only has medicineName
                p.getDosage(),
                p.getFrequencyPerDay(),
                p.getDurationInDays(),
                p.getInstructions()
            });
        }
    }

    private boolean loadBill(String consultationId) {
        Bill bill = usrCtrl.getBillingController().getBillByConsultationId(consultationId);
        boolean hasBill = bill != null;

        jLabel20.setVisible(hasBill);  // "BILL" title
        billPanel.setVisible(hasBill);

        jPanel2.removeAll();
        if (hasBill) {
            jPanel2.setLayout(new java.awt.BorderLayout());
            jPanel2.add(new BillReceipt(bill), java.awt.BorderLayout.CENTER);
        }
        jPanel2.revalidate();
        jPanel2.repaint();

        // billPanel had an explicit setPreferredSize(73, 120) baked in from the form
        // designer. Once a preferred size is explicitly set, Swing locks
        // getPreferredSize() to that fixed value forever and stops asking
        // GridBagLayout to recompute it from real children. Clear it first so the
        // layout manager can calculate the actual size BillReceipt needs.
        billPanel.setPreferredSize(null);
        billPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, billPanel.getPreferredSize().height));

        billPanel.revalidate();
        billPanel.repaint();
        wrapperPane.revalidate();
        wrapperPane.repaint();
        return hasBill;
    }

    private LabResult getSelectedLabResult() {
        int viewRow = labResultsTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a lab result first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = labResultsTable.convertRowIndexToModel(viewRow);
        String resultId = (String) labResultsTable.getModel().getValueAt(modelRow, 0);
        for (LabResult r : currentLabResults) {
            if (r.getResultId().equals(resultId)) return r;
        }
        return null;
    }
    
    private MedicalAssessment getSelectedAssessment() {
        int viewRow = medicalAssessmentsTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a medical assessment first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = medicalAssessmentsTable.convertRowIndexToModel(viewRow);
        String assessmentId = (String) medicalAssessmentsTable.getModel().getValueAt(modelRow, 0);
        for (MedicalAssessment a : currentAssessments) {
            if (a.getAssessmentId().equals(assessmentId)) return a;
        }
        return null;
    }

    private Prescription getSelectedPrescription() {
        int viewRow = prescriptionsTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please select a prescription first", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modelRow = prescriptionsTable.convertRowIndexToModel(viewRow);
        String prescriptionId = (String) prescriptionsTable.getModel().getValueAt(modelRow, 0);
        for (Prescription p : currentPrescriptions) {
            if (p.getPrescriptionId().equals(prescriptionId)) return p;
        }
        return null;
    }
    
    private void applyWrappingRenderers() {
        // Findings (col 2), Diagnosis (col 3)
        medicalAssessmentsTable.getColumnModel().getColumn(2).setCellRenderer(new WrappingCellRenderer());
        medicalAssessmentsTable.getColumnModel().getColumn(3).setCellRenderer(new WrappingCellRenderer());

        // Results (col 2), Remark (col 3)
        labResultsTable.getColumnModel().getColumn(2).setCellRenderer(new WrappingCellRenderer());
        labResultsTable.getColumnModel().getColumn(3).setCellRenderer(new WrappingCellRenderer());

        // Instructions (col 5)
        prescriptionsTable.getColumnModel().getColumn(5).setCellRenderer(new WrappingCellRenderer());
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
        patientName = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        patientID = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        patientInformationPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        doctorName = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        doctorID = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        consultationInformationPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        consultationType = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        consultationID = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        consultationDate = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        consultationTime = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        consultationDescription = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        medicalRecordsPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        medicalAssessmentsTable = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        labResultsTable = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        prescriptionsTable = new javax.swing.JTable();
        prescriptionsCrudPanel = new javax.swing.JPanel();
        addPrescriptionButton = new javax.swing.JButton();
        editPrescriptionButton = new javax.swing.JButton();
        deletePrescriptionButton = new javax.swing.JButton();
        medicalAssessmentCrudPanel = new javax.swing.JPanel();
        addAssessmentButton = new javax.swing.JButton();
        editAssessmentButton = new javax.swing.JButton();
        deleteAssessmentButton = new javax.swing.JButton();
        labResultsCrudPanel = new javax.swing.JPanel();
        addResultButton = new javax.swing.JButton();
        editResultButton = new javax.swing.JButton();
        deleteResultButton = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        billPanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();

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
        patientInformationPanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        patientInformationPanel.setPreferredSize(new java.awt.Dimension(235, 130));
        patientInformationPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("NAME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("PATIENT INFORMATION");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        patientInformationPanel.add(jLabel2, gridBagConstraints);

        patientName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        patientName.setText("Paureen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(patientName, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(jLabel4, gridBagConstraints);

        patientID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        patientID.setText("#PAT12345");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel.add(patientID, gridBagConstraints);

        wrapperPane.add(patientInformationPanel);
        wrapperPane.add(filler2);

        patientInformationPanel1.setBackground(new java.awt.Color(246, 251, 255));
        patientInformationPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        patientInformationPanel1.setAlignmentY(0.0F);
        patientInformationPanel1.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        patientInformationPanel1.setPreferredSize(new java.awt.Dimension(240, 130));
        patientInformationPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("NAME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel1.add(jLabel13, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("DOCTOR INFORMATION");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        patientInformationPanel1.add(jLabel14, gridBagConstraints);

        doctorName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        doctorName.setText("Paureen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel1.add(doctorName, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        patientInformationPanel1.add(jLabel16, gridBagConstraints);

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
        wrapperPane.add(filler1);

        consultationInformationPanel.setBackground(new java.awt.Color(246, 251, 255));
        consultationInformationPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        consultationInformationPanel.setAlignmentY(0.0F);
        consultationInformationPanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        consultationInformationPanel.setLayout(new java.awt.GridBagLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("CONSULTATION TYPE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("CONSULTATION INFORMATION");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        consultationInformationPanel.add(jLabel7, gridBagConstraints);

        consultationType.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        consultationType.setText("General Consultation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(consultationType, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("CONSULTATION ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(jLabel9, gridBagConstraints);

        consultationID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        consultationID.setText("#CON12345");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(consultationID, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("DATE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        consultationInformationPanel.add(jLabel11, gridBagConstraints);

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

        medicalRecordsPanel.setBackground(new java.awt.Color(246, 251, 255));
        medicalRecordsPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        medicalRecordsPanel.setAlignmentY(0.0F);
        medicalRecordsPanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        medicalRecordsPanel.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("MEDICAL RECORDS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        medicalRecordsPanel.add(jLabel10, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Medical Assessments");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        medicalRecordsPanel.add(jLabel12, gridBagConstraints);

        medicalAssessmentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Assessment ID", "Assessment Type", "Findings", "Diagnosis"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(medicalAssessmentsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        medicalRecordsPanel.add(jScrollPane1, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Lab Results");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        medicalRecordsPanel.add(jLabel17, gridBagConstraints);

        labResultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Result ID", "Result Type", "Results", "Remark", "Modified By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(labResultsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        medicalRecordsPanel.add(jScrollPane2, gridBagConstraints);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setText("Prescriptions");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        medicalRecordsPanel.add(jLabel19, gridBagConstraints);

        prescriptionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Prescription ID", "Prescription Type", "Dosage", "Frequency per Day", "Duration in Days", "Instructions"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(prescriptionsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        medicalRecordsPanel.add(jScrollPane3, gridBagConstraints);

        prescriptionsCrudPanel.setBackground(new java.awt.Color(255, 255, 255));
        prescriptionsCrudPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));

        addPrescriptionButton.setText("Add Prescription");
        addPrescriptionButton.addActionListener(this::addPrescriptionButtonActionPerformed);
        prescriptionsCrudPanel.add(addPrescriptionButton);

        editPrescriptionButton.setText("Edit Prescription");
        editPrescriptionButton.addActionListener(this::editPrescriptionButtonActionPerformed);
        prescriptionsCrudPanel.add(editPrescriptionButton);

        deletePrescriptionButton.setText("Delete Prescription");
        deletePrescriptionButton.addActionListener(this::deletePrescriptionButtonActionPerformed);
        prescriptionsCrudPanel.add(deletePrescriptionButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        medicalRecordsPanel.add(prescriptionsCrudPanel, gridBagConstraints);

        medicalAssessmentCrudPanel.setBackground(new java.awt.Color(255, 255, 255));
        medicalAssessmentCrudPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));

        addAssessmentButton.setText("Add Assessment");
        addAssessmentButton.addActionListener(this::addAssessmentButtonActionPerformed);
        medicalAssessmentCrudPanel.add(addAssessmentButton);

        editAssessmentButton.setText("Edit Assessment");
        editAssessmentButton.addActionListener(this::editAssessmentButtonActionPerformed);
        medicalAssessmentCrudPanel.add(editAssessmentButton);

        deleteAssessmentButton.setText("Delete Assessment");
        deleteAssessmentButton.addActionListener(this::deleteAssessmentButtonActionPerformed);
        medicalAssessmentCrudPanel.add(deleteAssessmentButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        medicalRecordsPanel.add(medicalAssessmentCrudPanel, gridBagConstraints);

        labResultsCrudPanel.setBackground(new java.awt.Color(255, 255, 255));
        labResultsCrudPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));

        addResultButton.setText("Add Result");
        addResultButton.addActionListener(this::addResultButtonActionPerformed);
        labResultsCrudPanel.add(addResultButton);

        editResultButton.setText("Edit Result");
        editResultButton.addActionListener(this::editResultButtonActionPerformed);
        labResultsCrudPanel.add(editResultButton);

        deleteResultButton.setText("Delete Result");
        deleteResultButton.addActionListener(this::deleteResultButtonActionPerformed);
        labResultsCrudPanel.add(deleteResultButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        medicalRecordsPanel.add(labResultsCrudPanel, gridBagConstraints);

        wrapperPane.add(medicalRecordsPanel);
        wrapperPane.add(filler4);

        billPanel.setBackground(new java.awt.Color(246, 251, 255));
        billPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        billPanel.setAlignmentY(0.0F);
        billPanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        billPanel.setPreferredSize(new java.awt.Dimension(73, 120));
        billPanel.setLayout(new java.awt.GridBagLayout());

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel20.setText("BILL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        billPanel.add(jLabel20, gridBagConstraints);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        billPanel.add(jPanel2, gridBagConstraints);

        wrapperPane.add(billPanel);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void deletePrescriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePrescriptionButtonActionPerformed
        Prescription selected = getSelectedPrescription();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Delete this prescription? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = usrCtrl.getMedicalRecordController().deletePrescription(selected);
        if (success) {
            JOptionPane.showMessageDialog(parentWindow,
                "Prescription deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadConsultationDetails();
        } else {
            JOptionPane.showMessageDialog(parentWindow,
                "Failed to delete prescription.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_deletePrescriptionButtonActionPerformed

    private void editPrescriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPrescriptionButtonActionPerformed
        Prescription selected = getSelectedPrescription();
        if (selected == null) return;

        List<PrescriptionType> prescriptionTypes = usrCtrl.getPrescriptionTypeController().getActivePrescriptionTypes();
        JComboBox<PrescriptionType> medicineCombo = new JComboBox<>(prescriptionTypes.toArray(new PrescriptionType[0]));
        medicineCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PrescriptionType) {
                    setText(((PrescriptionType) value).getTypeName());
                }
                return this;
            }
        });
        for (PrescriptionType type : prescriptionTypes) {
            if (type.getTypeName().equals(selected.getMedicineName())) {
                medicineCombo.setSelectedItem(type);
                break;
            }
        }

        JTextField dosageField = new JTextField(selected.getDosage(), 20);
        JTextField frequencyField = new JTextField(selected.getFrequencyPerDay(), 20);
        JTextField durationField = new JTextField(selected.getDurationInDays(), 20);
        JTextField instructionsField = new JTextField(selected.getInstructions(), 20);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Medicine:"));
        panel.add(medicineCombo);
        panel.add(new JLabel("Dosage:"));
        panel.add(dosageField);
        panel.add(new JLabel("Frequency per Day:"));
        panel.add(frequencyField);
        panel.add(new JLabel("Duration (days):"));
        panel.add(durationField);
        panel.add(new JLabel("Instructions:"));
        panel.add(instructionsField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Edit Prescription", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            PrescriptionType selectedType = (PrescriptionType) medicineCombo.getSelectedItem();
            String dosage = dosageField.getText().trim();
            String frequency = frequencyField.getText().trim();
            String duration = durationField.getText().trim();
            String instructions = instructionsField.getText().trim();

            if (selectedType == null || dosage.isEmpty() || frequency.isEmpty() || duration.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Medicine, Dosage, Frequency, and Duration are required.",
                    "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            try {
                Integer.parseInt(frequency);
                Integer.parseInt(duration);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Frequency and Duration must be whole numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String prescriptionId = selected.getPrescriptionId();
            LinkedHashMap<String, String> medMsg = usrCtrl.getMedicalRecordController()
                .modifyPrescriptionMedicineName(selectedType.getTypeName(), prescriptionId);
            LinkedHashMap<String, String> dosageMsg = usrCtrl.getMedicalRecordController()
                .modifyPrescriptionDosage(dosage, prescriptionId);
            LinkedHashMap<String, String> freqMsg = usrCtrl.getMedicalRecordController()
                .modifyPrescriptionFrequencyPerDay(frequency, prescriptionId);
            LinkedHashMap<String, String> durationMsg = usrCtrl.getMedicalRecordController()
                .modifyPrescriptionDurationInDays(duration, prescriptionId);
            LinkedHashMap<String, String> instrMsg = usrCtrl.getMedicalRecordController()
                .modifyPrescriptionInstructions(instructions, prescriptionId);

            boolean allSuccess = medMsg.get("success") != null
                && dosageMsg.get("success") != null
                && freqMsg.get("success") != null
                && durationMsg.get("success") != null
                && instrMsg.get("success") != null;

            if (allSuccess) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Prescription updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadConsultationDetails();
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    "Some fields failed to update. Check the log for details.",
                    "Partial Failure", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_editPrescriptionButtonActionPerformed

    private void editAssessmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAssessmentButtonActionPerformed
        MedicalAssessment selected = getSelectedAssessment();
        if (selected == null) return;

        JTextField typeField = new JTextField(selected.getAssessmentType(), 20);
        JTextField findingsField = new JTextField(selected.getFindings(), 20);
        JTextField diagnosisField = new JTextField(selected.getDiagnosis(), 20);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Assessment Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Findings:"));
        panel.add(findingsField);
        panel.add(new JLabel("Diagnosis:"));
        panel.add(diagnosisField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Edit Medical Assessment", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String type = typeField.getText().trim();
            String findings = findingsField.getText().trim();
            String diagnosis = diagnosisField.getText().trim();

            if (type.isEmpty() || findings.isEmpty() || diagnosis.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "All fields are required.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String assessmentId = selected.getAssessmentId();
            LinkedHashMap<String, String> typeMsg = usrCtrl.getMedicalRecordController()
                .modifyAssessmentType(type, assessmentId);
            LinkedHashMap<String, String> findingsMsg = usrCtrl.getMedicalRecordController()
                .modifyFindings(findings, assessmentId);
            LinkedHashMap<String, String> diagnosisMsg = usrCtrl.getMedicalRecordController()
                .modifyDiagnosis(diagnosis, assessmentId);

            boolean allSuccess = typeMsg.get("success") != null
                && findingsMsg.get("success") != null
                && diagnosisMsg.get("success") != null;

            if (allSuccess) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Medical assessment updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadConsultationDetails();
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    "Some fields failed to update. Check the log for details.",
                    "Partial Failure", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_editAssessmentButtonActionPerformed

    private void deleteAssessmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAssessmentButtonActionPerformed
        MedicalAssessment selected = getSelectedAssessment();
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
    }//GEN-LAST:event_deleteAssessmentButtonActionPerformed

    private void editResultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editResultButtonActionPerformed
        LabResult selected = getSelectedLabResult();
        if (selected == null) return;

        List<LabResultType> resultTypes = usrCtrl.getLabResultTypeController().getActiveLabResultTypes();
        JComboBox<LabResultType> typeCombo = new JComboBox<>(resultTypes.toArray(new LabResultType[0]));
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LabResultType) {
                    setText(((LabResultType) value).getTypeName());
                }
                return this;
            }
        });
        // Pre-select whichever type matches the existing record's stored name.
        for (LabResultType type : resultTypes) {
            if (type.getTypeName().equals(selected.getResultType())) {
                typeCombo.setSelectedItem(type);
                break;
            }
        }

        JTextField resultField = new JTextField(selected.getResult(), 20);
        JTextField remarksField = new JTextField(selected.getRemarks(), 20);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Result Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Result:"));
        panel.add(resultField);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarksField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Edit Lab Result", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            LabResultType selectedType = (LabResultType) typeCombo.getSelectedItem();
            String resultValue = resultField.getText().trim();
            String remarks = remarksField.getText().trim();

            if (selectedType == null || resultValue.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Result Type and Result are required.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String resultId = selected.getResultId();
            LinkedHashMap<String, String> typeMsg = usrCtrl.getMedicalRecordController()
                .modifyLabResultTestType(selectedType.getTypeName(), resultId);
            LinkedHashMap<String, String> resultMsg = usrCtrl.getMedicalRecordController()
                .modifyLabResultResult(resultValue, resultId);
            LinkedHashMap<String, String> remarksMsg = usrCtrl.getMedicalRecordController()
                .modifyLabResultRemarks(remarks, resultId);

            boolean allSuccess = typeMsg.get("success") != null
                && resultMsg.get("success") != null
                && remarksMsg.get("success") != null;

            if (allSuccess) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Lab result updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadConsultationDetails();
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    "Some fields failed to update. Check the log for details.",
                    "Partial Failure", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_editResultButtonActionPerformed

    private void deleteResultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteResultButtonActionPerformed
        LabResult selected = getSelectedLabResult();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(parentWindow,
            "Delete this lab result? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = usrCtrl.getMedicalRecordController().deleteLabResult(selected);
        if (success) {
            JOptionPane.showMessageDialog(parentWindow,
                "Lab result deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadConsultationDetails();
        } else {
            JOptionPane.showMessageDialog(parentWindow,
                "Failed to delete lab result.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_deleteResultButtonActionPerformed

    private void addAssessmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAssessmentButtonActionPerformed
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
    }//GEN-LAST:event_addAssessmentButtonActionPerformed

    private void addResultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addResultButtonActionPerformed
        List<LabResultType> resultTypes = usrCtrl.getLabResultTypeController().getActiveLabResultTypes();
        if (resultTypes.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No active lab result types are configured.", "Cannot Add", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<LabResultType> typeCombo = new JComboBox<>(resultTypes.toArray(new LabResultType[0]));
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LabResultType) {
                    setText(((LabResultType) value).getTypeName());
                }
                return this;
            }
        });

        JTextField resultField = new JTextField(20);
        JTextField remarksField = new JTextField(20);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Result Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Result:"));
        panel.add(resultField);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarksField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Add Lab Result", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            LabResultType selectedType = (LabResultType) typeCombo.getSelectedItem();
            String resultValue = resultField.getText().trim();
            String remarks = remarksField.getText().trim();

            if (selectedType == null || resultValue.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Result Type and Result are required.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String newResultId = oodj_hospital_management_system.services.MedicalRecordManagement
                .generateLabResultId();
            String nowStr = DateTime.formattedLocalDateTime(LocalDateTime.now());
            LabResult newResult = new LabResult(
                newResultId, consultation.getConsultationId(), selectedType.getTypeName(), resultValue, remarks, nowStr);

            boolean success = usrCtrl.getMedicalRecordController().createLabResult(newResult);
            if (success) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Lab result added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadConsultationDetails();
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    "Failed to add lab result.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_addResultButtonActionPerformed

    private void addPrescriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPrescriptionButtonActionPerformed
        List<PrescriptionType> prescriptionTypes = usrCtrl.getPrescriptionTypeController().getActivePrescriptionTypes();
        if (prescriptionTypes.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No active prescription types are configured.", "Cannot Add", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<PrescriptionType> medicineCombo = new JComboBox<>(prescriptionTypes.toArray(new PrescriptionType[0]));
        medicineCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PrescriptionType) {
                    setText(((PrescriptionType) value).getTypeName());
                }
                return this;
            }
        });

        JTextField dosageField = new JTextField(20);
        JTextField frequencyField = new JTextField(20);
        JTextField durationField = new JTextField(20);
        JTextField instructionsField = new JTextField(20);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Medicine:"));
        panel.add(medicineCombo);
        panel.add(new JLabel("Dosage:"));
        panel.add(dosageField);
        panel.add(new JLabel("Frequency per Day:"));
        panel.add(frequencyField);
        panel.add(new JLabel("Duration (days):"));
        panel.add(durationField);
        panel.add(new JLabel("Instructions:"));
        panel.add(instructionsField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Add Prescription", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            PrescriptionType selectedType = (PrescriptionType) medicineCombo.getSelectedItem();
            String dosage = dosageField.getText().trim();
            String frequency = frequencyField.getText().trim();
            String duration = durationField.getText().trim();
            String instructions = instructionsField.getText().trim();

            if (selectedType == null || dosage.isEmpty() || frequency.isEmpty() || duration.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Medicine, Dosage, Frequency, and Duration are required.",
                    "Missing Information", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            try {
                Integer.parseInt(frequency);
                Integer.parseInt(duration);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Frequency and Duration must be whole numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String newPrescriptionId = oodj_hospital_management_system.services.MedicalRecordManagement
                .generatePrescriptionId();

            Prescription newPrescription = new Prescription(
                newPrescriptionId, consultation.getConsultationId(), selectedType.getTypeName(), dosage,
                frequency, duration, instructions);

            boolean success = usrCtrl.getMedicalRecordController().createPrescription(newPrescription);
            if (success) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Prescription added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadConsultationDetails();
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    "Failed to add prescription.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_addPrescriptionButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        if (this.onBack != null) {
            this.onBack.run();
        }
    }//GEN-LAST:event_backButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAssessmentButton;
    private javax.swing.JButton addPrescriptionButton;
    private javax.swing.JButton addResultButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel billPanel;
    private javax.swing.JLabel consultationDate;
    private javax.swing.JLabel consultationDescription;
    private javax.swing.JLabel consultationID;
    private javax.swing.JPanel consultationInformationPanel;
    private javax.swing.JLabel consultationTime;
    private javax.swing.JLabel consultationType;
    private javax.swing.JButton deleteAssessmentButton;
    private javax.swing.JButton deletePrescriptionButton;
    private javax.swing.JButton deleteResultButton;
    private javax.swing.JLabel doctorID;
    private javax.swing.JLabel doctorName;
    private javax.swing.JButton editAssessmentButton;
    private javax.swing.JButton editPrescriptionButton;
    private javax.swing.JButton editResultButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
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
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel labResultsCrudPanel;
    private javax.swing.JTable labResultsTable;
    private javax.swing.JPanel medicalAssessmentCrudPanel;
    private javax.swing.JTable medicalAssessmentsTable;
    private javax.swing.JPanel medicalRecordsPanel;
    private javax.swing.JLabel patientID;
    private javax.swing.JPanel patientInformationPanel;
    private javax.swing.JPanel patientInformationPanel1;
    private javax.swing.JLabel patientName;
    private javax.swing.JPanel prescriptionsCrudPanel;
    private javax.swing.JTable prescriptionsTable;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
