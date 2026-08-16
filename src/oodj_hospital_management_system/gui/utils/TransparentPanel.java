package oodj_hospital_management_system.gui.utils;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sheaw
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TransparentPanel extends JPanel {

    private Color glassColor = new Color(255, 255, 255, 140);
    private int cornerRadius = 25;

    public TransparentPanel() {
        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        // Enable smooth rounded edges
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Glass background
        g2.setColor(glassColor);

        g2.fillRoundRect(
            0,
            0,
            getWidth(),
            getHeight(),
            cornerRadius,
            cornerRadius
        );

        g2.dispose();

        super.paintComponent(g);
    }


    // Allow changing transparency later
    public void setGlassColor(Color color) {
        this.glassColor = color;
        repaint();
    }


    // Allow changing corner size
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
