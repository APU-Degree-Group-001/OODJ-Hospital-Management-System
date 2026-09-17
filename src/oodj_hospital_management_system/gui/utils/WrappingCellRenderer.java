/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.gui.utils;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class WrappingCellRenderer extends javax.swing.JTextArea implements javax.swing.table.TableCellRenderer {

    // Tracks the tallest preferred height seen so far for each row, across all
    // columns, per table instance. Cleared each time the table's model/structure
    // changes so stale heights don't linger after data reloads.
    private final Map<JTable, Map<Integer, Integer>> rowHeightsByTable = new HashMap<>();

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
        int columnWidth = table.getColumnModel().getColumn(column).getWidth();
        setSize(columnWidth, Short.MAX_VALUE);

        int preferredHeight = Math.max(getPreferredSize().height, 40);

        Map<Integer, Integer> rowHeights = rowHeightsByTable.computeIfAbsent(table, t -> new HashMap<>());
        int tallestSoFar = rowHeights.getOrDefault(row, 0);

        if (preferredHeight > tallestSoFar) {
            rowHeights.put(row, preferredHeight);
            // Defer: don't resize the row while we're still in the middle of
            // painting this pass, or the layout can get out of sync / flicker.
            SwingUtilities.invokeLater(() -> {
                if (row < table.getRowCount() && table.getRowHeight(row) != preferredHeight) {
                    table.setRowHeight(row, preferredHeight);
                }
            });
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
