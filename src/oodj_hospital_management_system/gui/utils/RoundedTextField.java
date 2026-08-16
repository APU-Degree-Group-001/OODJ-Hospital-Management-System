/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.gui.utils;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author sheaw
 */
public class RoundedTextField extends JTextField {
    private int radius;

    public RoundedTextField(int radius) {
        this.radius = radius;
        setOpaque(false); // stop Swing from painting a default square background
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // just padding, no visible border line
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius); // paint the rounded shape ourselves
        g2.dispose();
        super.paintComponent(g); // let the actual text still render normally on top
    }
}