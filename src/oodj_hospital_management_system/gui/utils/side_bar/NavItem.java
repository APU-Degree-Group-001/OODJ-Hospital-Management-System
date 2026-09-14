/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.gui.utils.side_bar;

import javax.swing.*;
import java.awt.*;
import oodj_hospital_management_system.gui.utils.RoundedButton;

/**
 *
 * @author sheaw
 */
public class NavItem extends RoundedButton {
    private final Font normalFont = new Font("Segeo UI", Font.PLAIN, 16);

    public NavItem(String text, int radius, String iconPath) {
        super(text, radius); // handles the rounded background + button text

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
        setHorizontalAlignment(SwingConstants.CENTER); // icon+text block sits on the left, not centered // i removed icon le :(
//        setIconTextGap(15);                          // space between icon and text
        setHorizontalTextPosition(SwingConstants.RIGHT); // text sits to the RIGHT of the icon
        setVerticalTextPosition(SwingConstants.CENTER);  // text vertically centered with icon

        setMinimumSize(new Dimension(0, 50));
//        setPreferredSize(new Dimension(200, 50)); // default
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // allow horizontal stretch :D
        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setForeground(new Color(80, 80, 80));
        setBackground(new Color(245, 245, 245));

        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // padding ~
        
        // Initialize inactive state
        setButtonColors(
            new Color(245, 245, 245),          // normal (was Color.WHITE)
            new Color(220, 237, 250),          // hover
            new Color(220, 237, 250)           // pressed
        );
    }
    
    @Override
    public Dimension getMaximumSize() {
//        Integer.MAX_VALUE - infinite ~
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    public void setActive(boolean active) {
        Color activeColor = new Color(220, 237, 250);
        if (active) {
            setButtonColors(
                activeColor,  // normal
                activeColor,  // hover
                activeColor   // pressed
            );
            setForeground(new Color(30, 110, 180));
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            setButtonColors(
                new Color(245, 245, 245),  // normal
                activeColor,  // hover
                activeColor   // pressed
            );
            setForeground(new Color(80, 80, 80));
            setFont(getFont().deriveFont(Font.PLAIN));
        }
        repaint();
    }
}