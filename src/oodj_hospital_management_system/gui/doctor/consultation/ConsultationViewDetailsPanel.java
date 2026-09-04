/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.doctor.consultation;

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
import javax.swing.JOptionPane;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.MedicalAssessment;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.Prescription;
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
    private Runnable onBack;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form PatientManagement
     */
    public ConsultationViewDetailsPanel(UserController usrCtrl, Consultation consultation, Runnable onBack) {
        this.usrCtrl = usrCtrl;
        this.consultation = consultation;
        initComponents();
        loadConsultationDetails();
        
        jButton1.addActionListener(e -> {
            if (this.onBack != null) this.onBack.run();
        });
        
        // Refresh data every time this panel is shown (e.g. via CardLayout),
        // not just on first construction - addNotify() only fires once if the
        // panel stays in the container hierarchy and is just toggled with .show().
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadConsultationDetails();
            }
        });
        
        // jPanel2 (built by initComponents) currently has no width cap and gets
        // stretched wide by BoxLayout in wrapperPane. Swap it for a version that
        // caps its own preferred width, carrying over its real content.
        javax.swing.JPanel widthCappedPanel = new javax.swing.JPanel(new java.awt.BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.min(d.width, 900), d.height);
            }
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        widthCappedPanel.setBackground(patientInformationPanel.getBackground());
        widthCappedPanel.setBorder(patientInformationPanel.getBorder());
        widthCappedPanel.setAlignmentY(0.0F);
        
        patientInformationPanel.setMaximumSize(
        new Dimension(Integer.MAX_VALUE, patientInformationPanel.getPreferredSize().height));
        consultationInformationPanel.setMaximumSize(
        new Dimension(Integer.MAX_VALUE, consultationInformationPanel.getPreferredSize().height));
        medicalRecordsPanel.setMaximumSize(
        new Dimension(Integer.MAX_VALUE, medicalRecordsPanel.getPreferredSize().height));

        wrapperPane.remove(patientInformationPanel);
        wrapperPane.add(widthCappedPanel);
        patientInformationPanel = widthCappedPanel;
        wrapperPane.revalidate();
        wrapperPane.repaint();
    }
    
    private void loadConsultationDetails() {
        if (consultation == null) return;
        String consultationId = consultation.getConsultationId();

        // --- Patient info ---
        Patient patient = consultation.getPatient(); // confirm: eager ref vs lazy patientId lookup
        if (patient != null) {
            patientName.setText(patient.getName());
            patientID.setText("#" + patient.getId());
        }

        // --- Doctor info ---
        Doctor doctor = resolveDoctor(consultation);
        if (doctor != null) {
            doctorName.setText(doctor.getName());
            doctorID.setText("#" + doctor.getId());
        }

        // --- Consultation info ---
        consultationID.setText("#" + consultationId);
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
        loadBill(consultationId);
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
        // Note: assessment is a single object per consultation, not a list
        MedicalAssessment assessment = usrCtrl.getMedicalRecordController()
            .getMedicalAssessmentByConsultationId(consultationId);
        List<LabResult> labResults = usrCtrl.getMedicalRecordController()
            .getLabResultsByConsultationId(consultationId);
        List<Prescription> prescriptions = usrCtrl.getMedicalRecordController()
            .getPrescriptionsByConsultationId(consultationId);

        boolean hasAssessment = assessment != null;
        boolean hasLabResults = labResults != null && !labResults.isEmpty();
        boolean hasPrescriptions = prescriptions != null && !prescriptions.isEmpty();

        jLabel12.setVisible(hasAssessment);
        jScrollPane1.setVisible(hasAssessment);
        if (hasAssessment) {
            populateAssessmentsTable(assessment);
        }

        jLabel17.setVisible(hasLabResults);
        jScrollPane2.setVisible(hasLabResults);
        if (hasLabResults) {
            populateLabResultsTable(labResults);
        }

        jLabel19.setVisible(hasPrescriptions);
        jScrollPane3.setVisible(hasPrescriptions);
        if (hasPrescriptions) {
            populatePrescriptionsTable(prescriptions);
        }

        boolean hasAnyRecords = hasAssessment || hasLabResults || hasPrescriptions;
        jLabel10.setVisible(hasAnyRecords);      // "MEDICAL RECORDS" title
        medicalRecordsPanel.setVisible(hasAnyRecords);

        medicalRecordsPanel.revalidate();
        medicalRecordsPanel.repaint();
        wrapperPane.revalidate();
        wrapperPane.repaint();
    }

    private void populateAssessmentsTable(MedicalAssessment assessment) {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) medicalAssessmentsTable.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{
            assessment.getAssessmentId(),
            assessment.getAssessmentType(),
            assessment.getFindings(),
            assessment.getDiagnosis()
        });
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

    private void loadBill(String consultationId) {
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

        wrapperPane.revalidate();
        wrapperPane.repaint();
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
        jButton1 = new javax.swing.JButton();
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
        medicalAssessmentCrudPanel = new javax.swing.JPanel();
        addAssessmentButton = new javax.swing.JButton();
        editAssessmentButton = new javax.swing.JButton();
        deleteAssessmentButton = new javax.swing.JButton();
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

        jButton1.setText("⟵ Back");
        jButton1.setMaximumSize(new java.awt.Dimension(150, 40));
        jButton1.setPreferredSize(new java.awt.Dimension(150, 40));
        jPanel1.add(jButton1);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.Y_AXIS));

        patientInformationPanel.setBackground(new java.awt.Color(246, 251, 255));
        patientInformationPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        patientInformationPanel.setAlignmentY(0.0F);
        patientInformationPanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
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

        addAssessmentButton.setText("Add Assessments");
        medicalAssessmentCrudPanel.add(addAssessmentButton);

        editAssessmentButton.setText("Edit Assessments");
        editAssessmentButton.addActionListener(this::editAssessmentButtonActionPerformed);
        medicalAssessmentCrudPanel.add(editAssessmentButton);

        deleteAssessmentButton.setText("Delete Assessments");
        deleteAssessmentButton.addActionListener(this::deleteAssessmentButtonActionPerformed);
        medicalAssessmentCrudPanel.add(deleteAssessmentButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        medicalRecordsPanel.add(medicalAssessmentCrudPanel, gridBagConstraints);

        wrapperPane.add(medicalRecordsPanel);
        wrapperPane.add(filler4);

        billPanel.setBackground(new java.awt.Color(246, 251, 255));
        billPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        billPanel.setAlignmentY(0.0F);
        billPanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
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

    private void deleteAssessmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAssessmentButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteAssessmentButtonActionPerformed

    private void editAssessmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAssessmentButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editAssessmentButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAssessmentButton;
    private javax.swing.JPanel billPanel;
    private javax.swing.JLabel consultationDate;
    private javax.swing.JLabel consultationDescription;
    private javax.swing.JLabel consultationID;
    private javax.swing.JPanel consultationInformationPanel;
    private javax.swing.JLabel consultationTime;
    private javax.swing.JLabel consultationType;
    private javax.swing.JButton deleteAssessmentButton;
    private javax.swing.JLabel doctorID;
    private javax.swing.JLabel doctorName;
    private javax.swing.JButton editAssessmentButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JTable labResultsTable;
    private javax.swing.JPanel medicalAssessmentCrudPanel;
    private javax.swing.JTable medicalAssessmentsTable;
    private javax.swing.JPanel medicalRecordsPanel;
    private javax.swing.JLabel patientID;
    private javax.swing.JPanel patientInformationPanel;
    private javax.swing.JPanel patientInformationPanel1;
    private javax.swing.JLabel patientName;
    private javax.swing.JTable prescriptionsTable;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
