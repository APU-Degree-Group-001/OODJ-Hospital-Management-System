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
public class RoundedButton extends JButton {
    private int radius;
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        normalColor = getBackground();
        hoverColor = normalColor;
        pressedColor = normalColor;
        setOpaque(false); // stop Swing from painting a default square background
        setContentAreaFilled(false); // stop the default button "box" from painting
        setBorderPainted(false); // no default border line
        setFocusPainted(false); // no dotted focus rectangle
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // pointer on hover, feels clickable
    }
    
    public void setButtonColors(Color normal, Color hover, Color pressed) {
        normalColor = normal;
        hoverColor = hover;
        pressedColor = pressed;

        setBackground(normal);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(pressedColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }

        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius); // paint the rounded shape ourselves
        g2.dispose();
        super.paintComponent(g); // let the actual text/icon still render normally on top
    }
}