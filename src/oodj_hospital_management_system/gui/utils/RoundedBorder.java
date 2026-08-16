/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.gui.utils;

/**
 *
 * @author sheaw
 */

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundedBorder extends AbstractBorder {
    private int radius;
    private Color borderColor;

    public RoundedBorder(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = borderColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        // Make a copy of the Graphics object as a Graphics2D, so we get access to
        // more advanced drawing features (like anti-aliasing) without affecting
        // the original Graphics object used elsewhere for painting this component
        Graphics2D g2 = (Graphics2D) g.create();

        // Turn on anti-aliasing — this smooths out the curve of the rounded corners
        // so they look soft/curved instead of jagged and pixelated (like stair-steps)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the drawing color to whatever color was passed into the constructor
        // (e.g. gray) — this is the color the border OUTLINE will be drawn in
        g2.setColor(borderColor);

        // Actually draw the rounded rectangle outline:
        // x, y             → top-left starting position of the border (given by Swing)
        // width-1, height-1 → the size of the rectangle (minus 1px so the line isn't
        //                      clipped/cut off right at the component's edge)
        // radius, radius    → how rounded the corners are (bigger number = more curved)
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);

        // Release/clean up the Graphics2D copy we made — always do this after
        // you're done drawing with a Graphics object created via .create(),
        // to free up system resources properly
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(8, 12, 8, 12);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = 8;
        insets.left = 12;
        insets.bottom = 8;
        insets.right = 12;
        return insets;
    }
}