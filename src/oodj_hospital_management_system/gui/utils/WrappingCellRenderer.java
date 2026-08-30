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
public class WrappingCellRenderer extends javax.swing.JTextArea implements javax.swing.table.TableCellRenderer {
    public WrappingCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value != null ? value.toString() : "");
        setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);

        int preferredHeight = getPreferredSize().height;
        if (table.getRowHeight(row) != preferredHeight) {
            table.setRowHeight(row, Math.max(preferredHeight, 40));
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        return this;
    }
}
