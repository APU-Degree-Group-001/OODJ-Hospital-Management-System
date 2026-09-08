/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.utils.side_bar;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.utils.RoundedButton;
import oodj_hospital_management_system.gui.utils.RoundedPanel;

/**
 *
 * @author sheaw
 */
public class SideBar extends JPanel {
    private UserController usrCtrl;
    private String role;
    private JPanel contentArea;   // the CardLayout container it controls
    private List<NavItem> items;
    private LogoutButton logoutButton;
    private RoundedButton selectedItem;
    private final Color selectedColor = new Color(230, 240, 250);
    private final Color hoverColor = new Color(245, 248, 251);
    private final Color defaultColor = Color.WHITE;

    /**
     * Creates new form SideBar
     */
    public SideBar(UserController usrCtrl, String role, JPanel contentArea) {
        this.usrCtrl = usrCtrl;
        this.role = role;
        this.contentArea = contentArea;
        this.items = new ArrayList<>();
        initComponents();
        addNavItemByRole(role);
        navItems.addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override
        public void componentResized(java.awt.event.ComponentEvent e) {
                System.out.println("navItems RESIZED -> " + navItems.getSize());
                for (NavItem item : items) {
                    System.out.println("   " + item.getText() + " -> " + item.getSize());
                }
            }
        });
    }
    
    
    private void addNavItemByRole(String role) {
        if (role.equals("admin staff")) {
            addNavItem("Dashboard", "oodj_hospital_management_system/assets/img/icons/house_1.png", "adminStaffDashboard");
            addNavItem("User Management", "", "adminStaffUserManagement");
            addNavItem("Doctor Assignment", "", "adminStaffDoctorAssignment");
            addNavItem("Hospital Assets", "", "adminStaffHospitalAssets");
            addNavItem("Billing Settings", "", "adminStaffBillingSettings");
            navItems.add(Box.createVerticalGlue());
            addLogoutButton("Logout", "", "");
            // ...
        } else if (role.equals("medical manager")) {
            addNavItem("Dashboard", "oodj_hospital_management_system/assets/img/icons/house_1.png", "medicalManagerDashboard");
            addNavItem("Clinical Departments", "", "medicalManagerClinicalDepartments");
            addNavItem("Doctor Shift Rosters", "", "medicalManagerDoctorShiftRosters");
            addNavItem("Billing Management", "", "medicalManagerBillingManagement");
            addNavItem("Reporting", "", "medicalManagerReporting");
            navItems.add(Box.createVerticalGlue());
            addLogoutButton("Logout", "", "");
            // ...
        } else if (role.equals("doctor")) {
            addNavItem("Dashboard", "oodj_hospital_management_system/assets/img/icons/house_1.png", "doctorDashboard");
            addNavItem("Shift Roster", "", "doctorShiftRoster");
            addNavItem("Consultations", "", "doctorConsultations");
            addNavItem("Lab Services", "", "doctorLabServices");
            addNavItem("Ward Admission", "", "doctorWardAdmission");
            navItems.add(Box.createVerticalGlue());
            addLogoutButton("Logout", "", "");
            // ...
        } else if (role.equals("patient")) {
            addNavItem("", "", "");
            // ...
        }
    }
    
    private void addNavItem(String label, String iconPath, String cardName) {
        NavItem item = new NavItem(label, 12, iconPath);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.addActionListener(e -> selectNavItem(item, cardName));
        navItems.add(Box.createVerticalStrut(8));
        items.add(item);
        navItems.add(item);
    }
    
    private void addLogoutButton(String label, String iconPath, String cardName) {
        LogoutButton item = new LogoutButton(this.usrCtrl, label, 12, iconPath);
        item.setAlignmentX(Component.CENTER_ALIGNMENT);
        item.addActionListener(e -> selectLogoutButton(item));
        this.logoutButton = item;
        navItems.add(logoutButton);
    }
    
    private void selectNavItem(NavItem clicked, String cardName) {
        for (NavItem item : items) {
            item.setActive(item == clicked); // if item == clicked (true), then will set active yo~
        }
        selectedItem = clicked;

        if (contentArea != null) {
            CardLayout cl = (CardLayout) contentArea.getLayout();
            cl.show(contentArea, cardName);
        }
    }

    private void selectLogoutButton(LogoutButton clicked) {
        // TODO: logout
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

        welcomePane = new oodj_hospital_management_system.gui.utils.side_bar.WelcomePane(role, this.usrCtrl.getActiveUser().getName());
        jSeparator3 = new javax.swing.JSeparator();
        navItems = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 0, 40, 0));
        setLayout(new java.awt.GridBagLayout());

        welcomePane.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 20, 20, 20));
        welcomePane.setLayout(new javax.swing.BoxLayout(welcomePane, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        add(welcomePane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jSeparator3, gridBagConstraints);

        navItems.setBackground(new java.awt.Color(255, 255, 255));
        navItems.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 0, 10));
        navItems.setLayout(new javax.swing.BoxLayout(navItems, javax.swing.BoxLayout.Y_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(navItems, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPanel navItems;
    private javax.swing.JPanel welcomePane;
    // End of variables declaration//GEN-END:variables
}
