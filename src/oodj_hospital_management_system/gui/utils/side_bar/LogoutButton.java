/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.gui.utils.side_bar;

import javax.swing.*;
import java.awt.*;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.utils.RoundedButton;
import oodj_hospital_management_system.models.User;

/**
 *
 * @author sheaw
 */
public class LogoutButton extends RoundedButton {
    private UserController usrCtrl;

    public LogoutButton(UserController usrCtrl, String text, int radius, String iconPath) {
        super(text, radius); // handles the rounded background + button text
        this.usrCtrl = usrCtrl;

        // Load and attach the icon — JButton renders icon+text together automatically
//        if (iconPath != null) {
//            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(iconPath));
//            setIcon(icon);
//        } 
//        else {
//            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("oodj_hospital_management_system/assets/img/icons/house.png"));
//            setIcon(icon);
//        }

        // Positioning/spacing
        setHorizontalAlignment(SwingConstants.CENTER); // icon+text block sits on the left, not centered
//        setIconTextGap(15);                          // space between icon and text
        setHorizontalTextPosition(SwingConstants.CENTER); // text sits to the RIGHT of the icon
        setVerticalTextPosition(SwingConstants.CENTER);  // text vertically centered with icon

        setMinimumSize(new Dimension(0, 50));
//        setPreferredSize(new Dimension(200, 50)); // default
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // allow horizontal stretch :D
        setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        setForeground(new Color(80, 80, 80));
//        setBackground(Color.WHITE);
        setBackground(new Color(255, 235, 235));
        setForeground(new Color(210, 60, 60));
        setFont(getFont().deriveFont(Font.BOLD));

        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // padding ~
        
        // Logout button colors
        setButtonColors(
            new Color(255, 235, 235),  // normal - light red
            new Color(255, 215, 215),  // hover - slightly darker red
            new Color(255, 195, 195)   // pressed
        );
        
        
        logOut();
    }
    
    @Override
    public Dimension getMaximumSize() {
//        Integer.MAX_VALUE - infinite ~
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
    
    private void logOut() {
        this.addActionListener(evt -> {
            int choice = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                User activeUser = this.usrCtrl.getActiveUser();
                this.usrCtrl.userLogout(activeUser);

                java.awt.Window currentWindow = SwingUtilities.getWindowAncestor(this);

                switch (activeUser.getType().toLowerCase()) {
                    case "patient":
                    case "admin staff":
                    case "medical manager":
                    case "doctor":
                    default:
                        oodj_hospital_management_system.gui.LoginGUI loginGui =
                            new oodj_hospital_management_system.gui.LoginGUI();
                        loginGui.setVisible(true);
                        break;
                }

                if (currentWindow != null) {
                    currentWindow.dispose();
                }
            }
        });
    }
}