/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oodj_hospital_management_system.gui.doctor;

import java.io.IOException;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.DashboardPanel;
import oodj_hospital_management_system.gui.doctor.consultation.PatientConsultationsPanel;
import oodj_hospital_management_system.gui.doctor.lab_services.DoctorLabServicesPanel;
import oodj_hospital_management_system.gui.doctor.rating_feedback.RatingFeedbackPanel;
import oodj_hospital_management_system.gui.doctor.shift_roster.DoctorShiftRosterPanel;
import oodj_hospital_management_system.gui.doctor.ward_admission.DoctorWardAdmissionPanel;
import oodj_hospital_management_system.gui.utils.file_listener.FileWatcher;

/**
 *
 * @author sheaw
 */
public class DoctorGUI extends javax.swing.JFrame {
    private UserController usrCtrl;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DoctorGUI.class.getName());

    /**
     * Creates new form AdminStaffDashboard
     */
    public DoctorGUI(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        // Add cards to contentArea
        DashboardPanel doctorDashboardPanel = new DashboardPanel(this.usrCtrl);
        DoctorShiftRosterPanel doctorShiftRosterPanel = new DoctorShiftRosterPanel(this.usrCtrl);
        PatientConsultationsPanel patientConsultationsPanel = new PatientConsultationsPanel(this.usrCtrl);
        DoctorLabServicesPanel doctorLabServicesPanel = new DoctorLabServicesPanel(this.usrCtrl);
        DoctorWardAdmissionPanel doctorWardAdmissionPanel = new DoctorWardAdmissionPanel(this.usrCtrl);
        RatingFeedbackPanel doctorRatingFeedbackPanel = new RatingFeedbackPanel(this.usrCtrl);

        contentArea.add(doctorDashboardPanel, "doctorDashboard");
        contentArea.add(doctorShiftRosterPanel, "doctorShiftRoster");
        contentArea.add(patientConsultationsPanel, "doctorConsultations");
        contentArea.add(doctorLabServicesPanel, "doctorLabServices");
        contentArea.add(doctorWardAdmissionPanel, "doctorWardAdmission");
        contentArea.add(doctorRatingFeedbackPanel, "doctorRatingFeedback");
    //        contentArea.add(new MedicalManagerShiftRosterPanel(this.usrCtrl), "doctorHospitalAssets");

        // Show default card
        ((java.awt.CardLayout) contentArea.getLayout()).show(contentArea, "doctorDashboard");

        try {

            // Watches txt/consultations/ — confirmed reload methods
            FileWatcher consultationsWatcher = new FileWatcher(SystemConfiguration.consultationFilePath);

            consultationsWatcher.addListener(
                SystemConfiguration.consultationFilePath,
                () -> {
                    patientConsultationsPanel.availablePanel.loadAvailableSlots();
                    patientConsultationsPanel.bookedPanel.loadBookedConsultations();
                    patientConsultationsPanel.completedPanel.loadCompletedConsultations();
                }
            );

            consultationsWatcher.start();

            // Watches txt/users/ — doctor shifts + cancel-shift requests
            FileWatcher usersWatcher = new FileWatcher(SystemConfiguration.doctorShiftFilePath);

            usersWatcher.addListener(
                SystemConfiguration.doctorShiftFilePath,
                () -> { /* TODO: doctorShiftRosterPanel.shiftRosterPanel.<reload method> */ }
            );

            usersWatcher.addListener(
                SystemConfiguration.requestFilePath,
                () -> { /* TODO: doctorShiftRosterPanel.requestPanel.<reload method> */ }
            );

            usersWatcher.start();

            // Watches txt/hospital_assets/ — assets, allocations, ward admissions
            FileWatcher hospitalAssetsWatcher = new FileWatcher(SystemConfiguration.hospitalAssetFilePath);

            hospitalAssetsWatcher.addListener(
                SystemConfiguration.hospitalAssetFilePath,
                () -> { /* TODO: doctorLabServicesPanel.hospitalAssetsPanel.<reload method> */ }
            );

            hospitalAssetsWatcher.addListener(
                SystemConfiguration.hospitalAssetAllocationFilePath,
                () -> { /* TODO: doctorLabServicesPanel.scheduledPanel.<reload method> */ }
            );

            hospitalAssetsWatcher.addListener(
                SystemConfiguration.wardAdmissionFilePath,
                () -> { /* TODO: doctorWardAdmissionPanel.wardsPanel/.wardAdmissionsPanel.<reload method> */ }
            );

            hospitalAssetsWatcher.start();

        } catch (IOException e) {
            e.printStackTrace();
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

        contentArea = new javax.swing.JPanel();
        sideBar = new oodj_hospital_management_system.gui.utils.side_bar.SideBar(this.usrCtrl, "doctor", contentArea);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1280, 800));

        contentArea.setLayout(new java.awt.CardLayout());
        getContentPane().add(contentArea, java.awt.BorderLayout.CENTER);

        sideBar.setBackground(new java.awt.Color(255, 255, 255));
        sideBar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(217, 217, 217)), javax.swing.BorderFactory.createEmptyBorder(20, 10, 10, 20)));
        sideBar.setPreferredSize(new java.awt.Dimension(260, 0));
        getContentPane().add(sideBar, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentArea;
    private javax.swing.JPanel sideBar;
    // End of variables declaration//GEN-END:variables
}
