/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui;

import java.awt.Image;
import java.util.LinkedHashMap;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.models.User;

/**
 *
 * @author sheaw
 */
public class DashboardPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    String imageFileName;
    /**
     * Creates new form adminDashboardPanel
     */
    public DashboardPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;

        User activeUser = usrCtrl.getActiveUser();
        if (activeUser == null) return;

        String fileName;
         switch (activeUser.getType()) {
             case "patient":
                 fileName = "patient_dashboard.png";
                 break;
             case "doctor":
                 fileName = "doctor_dashboard.png";
                 break;
             case "medical manager":
                 fileName = "medical_manager_dashboard.png";
                 break;
             case "admin staff":
                 fileName = "admin_staff_dashboard.png";
                 break;
             default:
                 fileName = "";
         }

         this.imageFileName = "oodj_hospital_management_system/assets/img/dashboard/" + fileName;

         initComponents();
         loadUserDetails();
         loadIcon();
    }

    /**
     * Populates the profile labels from the currently signed-in user.
     */
    private void loadUserDetails() {
        User activeUser = usrCtrl.getActiveUser();
        if (activeUser == null) return;

        userId.setText(activeUser.getId());
        userName.setText(activeUser.getName());
        userEmail.setText(activeUser.getEmail());
        jLabel1.setText("Welcome back, " + activeUser.getName() + "!");
        jLabel4.setText(activeUser.getType() + " Dashboard");
    }
    
    private void loadIcon() {
        java.net.URL profileUrl = getClass().getResource("/oodj_hospital_management_system/assets/img/dashboard/profile.png");
        if (profileUrl != null) {
            Image scaledProfile = new javax.swing.ImageIcon(profileUrl).getImage()
                .getScaledInstance(55, 55, Image.SCALE_SMOOTH);
            jLabel4.setIcon(new javax.swing.ImageIcon(scaledProfile));
        } else {
            System.err.println("profile.png not found on classpath at expected path");
        }
        jLabel4.setText(null); // clear the "jLabel4" placeholder if you want icon-only
        jLabel4.setPreferredSize(new java.awt.Dimension(50, 50));
    }

    /**
     * Step 1: ask which field to update.
     */
    private void showUpdateProfileDialog() {
        String[] fields = {"Name", "Email", "Password"};
        JComboBox<String> fieldCombo = new JComboBox<>(fields);

        JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Field to update:"));
        panel.add(fieldCombo);

        int result = JOptionPane.showConfirmDialog(
            javax.swing.SwingUtilities.getWindowAncestor(this),
            panel, "Update Profile", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        switch ((String) fieldCombo.getSelectedItem()) {
            case "Name":
                promptUpdateName();
                break;
            case "Email":
                promptUpdateEmail();
                break;
            case "Password":
                promptUpdatePassword();
                break;
        }
    }

    /**
     * Step 2a: update name.
     */
    private void promptUpdateName() {
        User activeUser = usrCtrl.getActiveUser();

        String newName = (String) JOptionPane.showInputDialog(
            javax.swing.SwingUtilities.getWindowAncestor(this),
            "Enter new name:", "Update Name",
            JOptionPane.PLAIN_MESSAGE, null, null, activeUser.getName()
        );

        if (newName == null) return; // cancelled
        newName = newName.trim();

        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LinkedHashMap<String, String> msg = usrCtrl.modifyUserName(newName, activeUser.getId());
        handleUpdateResult(msg, "Name");
    }

    /**
     * Step 2b: update email.
     */
    private void promptUpdateEmail() {
        User activeUser = usrCtrl.getActiveUser();

        String newEmail = (String) JOptionPane.showInputDialog(
            javax.swing.SwingUtilities.getWindowAncestor(this),
            "Enter new email:", "Update Email",
            JOptionPane.PLAIN_MESSAGE, null, null, activeUser.getEmail()
        );

        if (newEmail == null) return;
        newEmail = newEmail.trim();

        if (newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email cannot be empty.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!UserController.isValidEmail(newEmail)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LinkedHashMap<String, String> msg = usrCtrl.modifyEmail(newEmail, activeUser.getId());
        handleUpdateResult(msg, "Email");
    }

    /**
     * Step 2c: update password (masked fields, requires confirmation).
     */
    private void promptUpdatePassword() {
        User activeUser = usrCtrl.getActiveUser();

        JPasswordField newPwdField = new JPasswordField();
        JPasswordField confirmPwdField = new JPasswordField();

        JPanel panel = new JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("New Password:"));
        panel.add(newPwdField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPwdField);

        int result = JOptionPane.showConfirmDialog(
            javax.swing.SwingUtilities.getWindowAncestor(this),
            panel, "Update Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        String newPwd = new String(newPwdField.getPassword());
        String confirmPwd = new String(confirmPwdField.getPassword());

        if (newPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPwd.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LinkedHashMap<String, String> msg = usrCtrl.modifyPwd(newPwd, activeUser.getId());
        handleUpdateResult(msg, "Password");
    }

    /**
     * Shared success/error handling + refresh of the displayed profile.
     */
    private void handleUpdateResult(LinkedHashMap<String, String> msg, String fieldLabel) {
        if (msg.get("success") != null) {
            User refreshedUser = usrCtrl.getUserById(usrCtrl.getActiveUser().getId());
            usrCtrl.updateActiveUser(refreshedUser);
            loadUserDetails();
            JOptionPane.showMessageDialog(this, fieldLabel + " updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                msg.get("msg") != null ? msg.get("msg") : "Update failed.",
                "Error", JOptionPane.ERROR_MESSAGE);
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

        dashboardPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new oodj_hospital_management_system.gui.utils.BackgroundPanel(this.imageFileName);
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        userId = new javax.swing.JLabel();
        userName = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        userEmail = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        updateProfileButton = new javax.swing.JToggleButton();

        setLayout(new java.awt.BorderLayout());

        dashboardPanel.setBackground(new java.awt.Color(255, 255, 255));
        dashboardPanel.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(246, 251, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanel3.setPreferredSize(new java.awt.Dimension(1454, 360));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel8.setPreferredSize(new java.awt.Dimension(1426, 300));
        jPanel8.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanel8, java.awt.BorderLayout.CENTER);

        dashboardPanel.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(246, 251, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 40, 10, 1));
        jPanel2.setPreferredSize(new java.awt.Dimension(1454, 75));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel1.setText("Welcome back!");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("Here's your account information");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jLabel2);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel4.setBackground(new java.awt.Color(246, 251, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 40, 80, 40));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanel5.setMaximumSize(new java.awt.Dimension(2147483647, 500));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 0, 10, 1));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oodj_hospital_management_system/assets/img/dashboard/profile.png"))); // NOI18N
        jLabel4.setText("jLabel4");
        jLabel4.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel6.add(jLabel4);
        jPanel6.add(filler1);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel5.setText("My Profile");
        jPanel7.add(jLabel5);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel6.setText("View and manage your account details");
        jPanel7.add(jLabel6);

        jPanel6.add(jPanel7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jPanel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(jSeparator1, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("ID");
        jLabel8.setMaximumSize(new java.awt.Dimension(18, 80));
        jLabel8.setMinimumSize(new java.awt.Dimension(18, 80));
        jLabel8.setPreferredSize(new java.awt.Dimension(18, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 15, 5, 15);
        jPanel5.add(jLabel8, gridBagConstraints);

        userId.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        userId.setText("#PAT12345");
        userId.setMaximumSize(new java.awt.Dimension(92, 80));
        userId.setMinimumSize(new java.awt.Dimension(92, 80));
        userId.setPreferredSize(new java.awt.Dimension(92, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 15, 5, 15);
        jPanel5.add(userId, gridBagConstraints);

        userName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        userName.setText("Paureen");
        userName.setMaximumSize(new java.awt.Dimension(63, 80));
        userName.setMinimumSize(new java.awt.Dimension(63, 80));
        userName.setPreferredSize(new java.awt.Dimension(63, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 15);
        jPanel5.add(userName, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("NAME");
        jLabel9.setMaximumSize(new java.awt.Dimension(48, 80));
        jLabel9.setMinimumSize(new java.awt.Dimension(48, 80));
        jLabel9.setPreferredSize(new java.awt.Dimension(48, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 15);
        jPanel5.add(jLabel9, gridBagConstraints);

        userEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        userEmail.setText("pau@gmail.com");
        userEmail.setMaximumSize(new java.awt.Dimension(130, 80));
        userEmail.setMinimumSize(new java.awt.Dimension(130, 80));
        userEmail.setPreferredSize(new java.awt.Dimension(130, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 15);
        jPanel5.add(userEmail, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("EMAIL");
        jLabel10.setMaximumSize(new java.awt.Dimension(50, 80));
        jLabel10.setMinimumSize(new java.awt.Dimension(50, 80));
        jLabel10.setPreferredSize(new java.awt.Dimension(50, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 15);
        jPanel5.add(jLabel10, gridBagConstraints);

        updateProfileButton.setText("Update Profile");
        updateProfileButton.setPreferredSize(new java.awt.Dimension(200, 40));
        updateProfileButton.addActionListener(this::updateProfileButtonActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 1;
        jPanel5.add(updateProfileButton, gridBagConstraints);

        jPanel4.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

        dashboardPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        add(dashboardPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void updateProfileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateProfileButtonActionPerformed
        updateProfileButton.setSelected(false); // reset toggle visual, we only want click behaviour
        showUpdateProfileDialog();
    }//GEN-LAST:event_updateProfileButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToggleButton updateProfileButton;
    private javax.swing.JLabel userEmail;
    private javax.swing.JLabel userId;
    private javax.swing.JLabel userName;
    // End of variables declaration//GEN-END:variables
}
