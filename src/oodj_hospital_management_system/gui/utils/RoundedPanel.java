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
public class RoundedPanel extends JPanel {
    private int radius;
    private Color borderColor = null;
    private int borderWidth = 1;

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false); // stop Swing from painting a default square background
    }

    public RoundedPanel(int radius, LayoutManager layout) {
        super(layout);
        this.radius = radius;
        setOpaque(false);
    }
    
    public RoundedPanel(int radius, Color borderColor, int top, int left, int bottom, int right) {
        this.radius = radius;
        this.borderColor = borderColor;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right)); // padding only, safe on rounded shapes
    }
    
    public RoundedPanel(int radius, Color backgroundColor, Color borderColor,
        int top, int left, int bottom, int right) {
        this.radius = radius;
        this.borderColor = borderColor;
        setBackground(backgroundColor);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // fill rounded background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        // draw rounded border, if set +D
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }

        g2.dispose();
        super.paintComponent(g); // let child components render normally on top
    }
}