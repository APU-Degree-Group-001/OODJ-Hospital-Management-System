/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package oodj_hospital_management_system.gui.patient.consultation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import oodj_hospital_management_system.gui.utils.RoundedPanel;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationSlot;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.utils.DateTime;

/**
 * @author sheaw
 */
public class ConsultationCard extends RoundedPanel {

    private static final int CARD_HEIGHT = 120;

    // ---- Palette ----
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(110, 118, 129);
    private static final Color TEXT_DARK  = new Color(30, 34, 40);

    private static final Color STATUS_SCHEDULED_FG   = new Color(37, 99, 235);
    private static final Color STATUS_IN_PROGRESS_FG = new Color(200, 140, 0);
    private static final Color STATUS_COMPLETED_FG   = new Color(0, 150, 0);
    private static final Color STATUS_CANCELLED_FG   = Color.GRAY;

    private final Consultation consultation;
    private final Runnable onViewDetails;

    public ConsultationCard(Consultation consultation) {
        this(consultation, null);
    }

    /**
     * @param onViewDetails called when the "View Details" button is clicked.
     *                      Wire this to open a detail dialog/panel for this
     *                      consultation.
     */
    public ConsultationCard(Consultation consultation, Runnable onViewDetails) {
        // (cornerRadius, backgroundColor, padTop, padLeft, padBottom, padRight)
        super(12, CARD_BG, 14, 20, 14, 20);
        this.consultation = consultation;
        this.onViewDetails = onViewDetails;

        // Width flexible, height fixed.
        setPreferredSize(new Dimension(900, CARD_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, CARD_HEIGHT));
        setMinimumSize(new Dimension(400, CARD_HEIGHT));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(16, 0));

        add(buildInfoSection(), BorderLayout.CENTER);
        add(buildActionSection(), BorderLayout.EAST);
    }

    // ---------------------------------------------------------------
    // Left/center: ID + status, patient, type, date/time
    // ---------------------------------------------------------------
    private JPanel buildInfoSection() {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        col.add(buildHeaderRow());
        col.add(Box.createVerticalStrut(8));

        Patient patient = consultation.getPatient();
        String patientLine = patient != null
                ? patient.getName() + " (#" + patient.getId() + ")"
                : "-";
        col.add(boldLabel(patientLine, 13));
        col.add(Box.createVerticalStrut(2));

        ConsultationType type = consultation.getConsultationType();
        col.add(mutedLabel(type != null ? type.getTypeName() : "-"));
        col.add(Box.createVerticalStrut(2));

        col.add(mutedLabel(formatSlotDateTime()));

        col.add(Box.createVerticalGlue());
        return col;
    }

    private JPanel buildHeaderRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        row.add(mutedLabel("#" + consultation.getConsultationId()));
        row.add(dotLabel());
        row.add(statusLabel());
        return row;
    }

    private JLabel dotLabel() {
        JLabel label = new JLabel("·");
        label.setForeground(TEXT_MUTED);
        label.setFont(label.getFont().deriveFont(12f));
        return label;
    }

    private JLabel statusLabel() {
        ConsultationStatus status = consultation.getConsultationStatus();
        String text = status != null ? status.name().replace("_", " ") : "-";
        Color fg;
        switch (status != null ? status : ConsultationStatus.SCHEDULED) {
            case COMPLETED:
                fg = STATUS_COMPLETED_FG;
                break;
            case IN_PROGRESS:
                fg = STATUS_IN_PROGRESS_FG;
                break;
            case CANCELLED:
                fg = STATUS_CANCELLED_FG;
                break;
            default: // SCHEDULED
                fg = STATUS_SCHEDULED_FG;
        }
        JLabel label = new JLabel(text);
        label.setForeground(fg);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        return label;
    }

    // ---------------------------------------------------------------
    // Right: View Details button
    // ---------------------------------------------------------------
    private JPanel buildActionSection() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setFocusPainted(false);
        viewDetailsBtn.setFont(viewDetailsBtn.getFont().deriveFont(Font.BOLD, 13f));
        viewDetailsBtn.setPreferredSize(new Dimension(140, 42));
        viewDetailsBtn.addActionListener(e -> {
            if (onViewDetails != null) onViewDetails.run();
        });

        wrapper.add(viewDetailsBtn);
        return wrapper;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------
    private String formatSlotDateTime() {
        ConsultationSlot slot = consultation.getConsultationSlot();
        if (slot == null || slot.getStartDateTime() == null || slot.getEndDateTime() == null) {
            return "-";
        }
        try {
            java.time.LocalDateTime start = DateTime.toDateTimeObject(slot.getStartDateTime());
            java.time.LocalDateTime end = DateTime.toDateTimeObject(slot.getEndDateTime());
            String dateStr = DateTime.toDateStringWords(DateTime.getDate(start));
            String timeStr = DateTime.toTimeString(DateTime.getTime(start))
                    + " – " + DateTime.toTimeString(DateTime.getTime(end));
            return dateStr + "  •  " + timeStr;
        } catch (Exception e) {
            return "-";
        }
    }

    private JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        label.setFont(label.getFont().deriveFont(12f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel boldLabel(String text, float size) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_DARK);
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}