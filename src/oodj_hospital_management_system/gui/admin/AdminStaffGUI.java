/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oodj_hospital_management_system.gui.admin;

import java.io.IOException;
import oodj_hospital_management_system.config.SystemConfiguration;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.DashboardPanel;
import oodj_hospital_management_system.gui.admin.billing_settings.AdminBillingSettingsPanel;
import oodj_hospital_management_system.gui.admin.doctorAssignment.AdminDoctorAssignmentPanel;
import oodj_hospital_management_system.gui.admin.hospitalAssets.AdminHospitalAssetsPanel;
import oodj_hospital_management_system.gui.admin.user_management.AdminUserManagementPanel;
import oodj_hospital_management_system.gui.utils.file_listener.FileWatcher;

/**
 *
 * @author sheaw
 */
public class AdminStaffGUI extends javax.swing.JFrame {
    private UserController usrCtrl;
    private DashboardPanel adminDashboard;
    private AdminDoctorAssignmentPanel adminDoctorAssignment;
    private AdminUserManagementPanel adminUserManagement;
    private AdminHospitalAssetsPanel adminHospitalAssets;
    private AdminBillingSettingsPanel adminBillingSettings;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminStaffGUI.class.getName());

    /**
     * Creates new form AdminStaffDashboard
     */
    public AdminStaffGUI(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        adminDashboard = new DashboardPanel(this.usrCtrl);
        adminDoctorAssignment = new AdminDoctorAssignmentPanel(this.usrCtrl);
        adminUserManagement = new AdminUserManagementPanel(this.usrCtrl, adminDoctorAssignment::loadDoctors);
        adminHospitalAssets = new AdminHospitalAssetsPanel(this.usrCtrl);
        adminBillingSettings = new AdminBillingSettingsPanel(this.usrCtrl);

        // Add cards to contentArea
        contentArea.add(adminDashboard, "adminStaffDashboard");
        contentArea.add(adminUserManagement, "adminStaffUserManagement");
        contentArea.add(adminDoctorAssignment, "adminStaffDoctorAssignment");
        contentArea.add(adminHospitalAssets, "adminStaffHospitalAssets");
        contentArea.add(adminBillingSettings, "adminStaffBillingSettings");

        // Show default card
        ((java.awt.CardLayout) contentArea.getLayout()).show(contentArea, "adminStaffDashboard");

        try {

            // Watches txt/users/ — users, doctor shifts, medical departments, requests
            FileWatcher usersWatcher = new FileWatcher(SystemConfiguration.userFilePath);

            usersWatcher.addListener(
                SystemConfiguration.userFilePath,
                () -> {
                    adminDoctorAssignment.loadDoctors();
                    adminUserManagement.patientManagementPanel.loadPatients();
                    adminUserManagement.doctorManagementPanel.loadDoctors();
                    adminUserManagement.medicalManagerManagementPanel.loadMedicalManagers();
                    adminUserManagement.adminStaffManagementPanel.loadAdminStaffs();
                }
            );

            usersWatcher.addListener(
                SystemConfiguration.medicalDepartmentFilePath,
                () -> adminDoctorAssignment.loadSelections()
            );

            usersWatcher.addListener(
                SystemConfiguration.requestFilePath,
                () -> adminHospitalAssets.requestPanel.loadDoctorRequests()
            );

            usersWatcher.start();

            // Watches txt/hospital_assets/ — assets + allocations
            FileWatcher hospitalAssetsWatcher = new FileWatcher(SystemConfiguration.hospitalAssetFilePath);

            hospitalAssetsWatcher.addListener(
                SystemConfiguration.hospitalAssetFilePath,
                () -> adminHospitalAssets.hospitalAssetsPanel.loadAssets()
            );

            hospitalAssetsWatcher.start();

            // Watches txt/consultations/ — consultation base rates + insurance network
            FileWatcher consultationsWatcher = new FileWatcher(SystemConfiguration.consultationTypesFilePath);

            consultationsWatcher.addListener(
                SystemConfiguration.consultationTypesFilePath,
                () -> adminBillingSettings.consultationBaseRatePanel.loadConsultationTypes()
            );

            consultationsWatcher.addListener(
                SystemConfiguration.insuranceFilePath,
                () -> adminBillingSettings.insuranceNetworkPanel.loadInsurances()
            );

            consultationsWatcher.start();

            // Watches txt/medical_records/ — lab result + prescription base rates
            FileWatcher medicalRecordsWatcher = new FileWatcher(SystemConfiguration.labResultTypesFilePath);

            medicalRecordsWatcher.addListener(
                SystemConfiguration.labResultTypesFilePath,
                () -> adminBillingSettings.labServicesBaseRatePanel.loadLabResultTypes()
            );

            medicalRecordsWatcher.addListener(
                SystemConfiguration.prescriptionTypesFilePath,
                () -> adminBillingSettings.prescriptionsBaseRatePanel.loadPrescriptionTypes()
            );

            medicalRecordsWatcher.start();

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
        sideBar = new oodj_hospital_management_system.gui.utils.side_bar.SideBar(this.usrCtrl, "admin staff", contentArea);

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
