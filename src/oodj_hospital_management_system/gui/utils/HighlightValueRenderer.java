/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.gui.utils;

import javax.swing.JTable;

/**
 *
 * @author sheaw
 */
public class HighlightValueRenderer extends javax.swing.table.DefaultTableCellRenderer {
    private final String targetValue;
    private final java.awt.Color highlightColor;

    public HighlightValueRenderer(String targetValue) {
        this(targetValue, new java.awt.Color(200, 30, 30));
    }

    public HighlightValueRenderer(String targetValue, java.awt.Color highlightColor) {
        this.targetValue = targetValue;
        this.highlightColor = highlightColor;
    }

    @Override
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (targetValue.equals(value)) {
            c.setForeground(isSelected ? java.awt.Color.WHITE : highlightColor);
        } else {
            c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        }
        return c;
    }
}
