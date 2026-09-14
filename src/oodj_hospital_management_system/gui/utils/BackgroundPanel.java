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

public class BackgroundPanel extends JPanel {

    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        setImage(imagePath);
    }

    public void setImage(String imagePath) {
        java.net.URL url = getClass().getClassLoader().getResource(imagePath);
        if (url == null) {
            System.err.println("BackgroundPanel: resource not found on classpath: " + imagePath);
            backgroundImage = null;
        } else {
            backgroundImage = new ImageIcon(url).getImage();
        }
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(
            backgroundImage,  // what to draw
            0,                // x position
            0,                // y position
            getWidth(),       // width
            getHeight(),      // height
            this              // ImageObserver
        );
    }
}