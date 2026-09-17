/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package oodj_hospital_management_system.gui.medical_manager.billing_management;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import oodj_hospital_management_system.gui.utils.RoundedPanel;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.Insurance;
import oodj_hospital_management_system.models.LabResult;
import oodj_hospital_management_system.models.LabResultType;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.Prescription;
import oodj_hospital_management_system.models.PrescriptionType;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 * Hand-coded (non-GroupLayout) bill receipt card.
 *
 * Unlike a Matisse-generated form with one fixed JLabel per field, the
 * "Bill Breakdown" section is built by looping over
 * consultation.getLabResults() and consultation.getPrescriptions() at
 * runtime, so the card grows/shrinks to fit however many items exist -
 * there's no ceiling baked into the layout.
 *
 * Usage:
 *   BillReceipt card = new BillReceipt(bill, () -> openBillDetailsDialog(bill));
 *   billListPanel.add(card);
 *
 * @author sheaw
 */
public class BillReceipt extends RoundedPanel {

    // ---- Palette (tweak to match your app's exact theme) ----
    private static final Color CARD_BG           = Color.WHITE;
    private static final Color TEXT_MUTED         = new Color(110, 118, 129);
    private static final Color TEXT_DARK          = new Color(30, 34, 40);
    private static final Color DIVIDER            = new Color(230, 233, 237);
    private static final Color ACCENT_BLUE        = new Color(37, 99, 235);
    private static final Color STATUS_PAID_BG     = new Color(220, 247, 227);
    private static final Color STATUS_PAID_FG     = new Color(0, 150, 0);
    private static final Color STATUS_UNPAID_BG   = new Color(255, 244, 214);
    private static final Color STATUS_UNPAID_FG   = new Color(200, 140, 0);
    private static final Color STATUS_VOID_BG     = new Color(235, 235, 235);
    private static final Color STATUS_VOID_FG     = Color.GRAY;

    private final Bill bill;
    private final Consultation consultation;
    private final Runnable onUpdateStatus;

    public BillReceipt(Bill bill) {
        this(bill, null);
    }

    /**
     * @param onUpdateStatus called when the "Mark as Paid" button is clicked
     *                       on an UNPAID bill. Wire this to something like
     *                       billingMgmt.markBillAsPaid(bill) followed by a
     *                       reload of the list.
     */
    public BillReceipt(Bill bill, Runnable onUpdateStatus) {
        // NOTE: confirm the exact param order your RoundedPanel constructor expects -
        // this mirrors the pattern already used elsewhere in your codebase:
        // (cornerRadius, backgroundColor, padTop, padLeft, padBottom, padRight)
        super(15, new Color(248, 249, 250), new Color(230, 233, 237), 15, 20, 15, 20);
        this.bill = bill;
        this.consultation = ConsultationManagement.getConsultationById(bill.getConsultationId());
        this.onUpdateStatus = onUpdateStatus;
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        // Column 1 - quick facts (Bill ID, date, patient, doctor, type)
        gbc.gridx = 0;
        gbc.weightx = 0.22;
        gbc.insets = new Insets(0, 0, 0, 20);
        add(buildInfoColumn(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 20);
        add(new VerticalDivider(), gbc);

        // Column 2 - Bill Breakdown (the dynamic, loop-built part)
        gbc.gridx = 2;
        gbc.weightx = 0.48;
        gbc.insets = new Insets(0, 0, 0, 20);
        add(buildBreakdownColumn(), gbc);

        // Column 3 - Bill Summary (totals / insurance / status)
        gbc.gridx = 3;
        gbc.weightx = 0.30;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(buildSummaryColumn(), gbc);
    }

    // ---------------------------------------------------------------
    // Column 1: Bill ID + quick facts
    // ---------------------------------------------------------------
    private JPanel buildInfoColumn() {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        col.add(sectionTag("BILL ID"));
        col.add(Box.createVerticalStrut(2));
        col.add(boldLabel(bill.getBillId(), 14));
        col.add(Box.createVerticalStrut(14));

        if (consultation != null && consultation.getConsultationSlot() != null) {
            col.add(infoRow("Consultation Date",
                    formatDateTime(consultation.getConsultationSlot().getStartDateTime())));
            col.add(Box.createVerticalStrut(10));
        }

        Patient patient = bill.getPatient();
        col.add(infoRow("Patient", patient != null
                ? "#" + patient.getId() + " - " + patient.getName()
                : "-"));
        col.add(Box.createVerticalStrut(10));

        Doctor doctor = resolveDoctor();
        col.add(infoRow("Doctor", doctor != null
                ? "#" + doctor.getId() + " - " + doctor.getName()
                : "-"));
        col.add(Box.createVerticalStrut(10));

        ConsultationType type = (consultation != null) ? consultation.getConsultationType() : null;
        col.add(infoRow("Consultation Type", type != null ? type.getTypeName() : "-"));

        col.add(Box.createVerticalGlue());
        return col;
    }

    private Doctor resolveDoctor() {
        if (consultation == null) return null;
        if (consultation.getConsultationSlot() == null) return null;
        if (consultation.getConsultationSlot().getDoctorShift() == null) return null;
        return consultation.getConsultationSlot().getDoctorShift().getDoctor();
    }

    // ---------------------------------------------------------------
    // Column 2: Bill Breakdown - Consultation / Lab Results / Prescriptions
    // Every list here is built with a for-loop, so any number of items
    // (0, 1, or 20) is handled without touching this code.
    // ---------------------------------------------------------------
    private JPanel buildBreakdownColumn() {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        col.add(sectionTag("BILL BREAKDOWN"));
        col.add(Box.createVerticalStrut(10));

        double consultationCharge = 0.0;
        if (consultation != null && consultation.getConsultationType() != null) {
            ConsultationType type = consultation.getConsultationType();
            consultationCharge = type.getBaseRate();
            col.add(subheading("Consultation"));
            col.add(lineItemRow(type.getTypeName(), consultationCharge));
            col.add(Box.createVerticalStrut(12));
        }

        double labCharge = 0.0;
        List<LabResult> labResults = (consultation != null) ? consultation.getLabResults() : null;
        if (labResults != null && !labResults.isEmpty()) {
            col.add(subheading("Lab Results"));
            for (LabResult lr : labResults) {
                LabResultType lrt = lr.getLabResultType();
                double price = (lrt != null) ? lrt.getBaseRate() : 0.0;
                labCharge += price;
                col.add(lineItemRow(lr.getResultType(), price));
            }
            col.add(Box.createVerticalStrut(12));
        }

        double prescriptionCharge = 0.0;
        List<Prescription> prescriptions = (consultation != null) ? consultation.getPrescriptions() : null;
        if (prescriptions != null && !prescriptions.isEmpty()) {
            col.add(subheading("Prescriptions"));
            for (Prescription p : prescriptions) {
                PrescriptionType pt = p.getPrescriptionType();
                double rate = (pt != null) ? pt.getBaseRate() : 0.0;
                int units = safeParseInt(p.getFrequencyPerDay()) * safeParseInt(p.getDurationInDays());
                double price = rate * units;
                prescriptionCharge += price;
                String label = String.format("%s (%s, %d unit%s)",
                        p.getMedicineName(), p.getDosage(), units, units == 1 ? "" : "s");
                col.add(lineItemRow(label, price));
            }
            col.add(Box.createVerticalStrut(12));
        }

        col.add(new JSeparator());
        col.add(Box.createVerticalStrut(8));
        double total = consultationCharge + labCharge + prescriptionCharge;
        col.add(totalRow("Total Charge", total));

        col.add(Box.createVerticalGlue());
        return col;
    }

    // ---------------------------------------------------------------
    // Column 3: Bill Summary - totals, insurance, payable, status
    // ---------------------------------------------------------------
    private JPanel buildSummaryColumn() {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        col.add(sectionTag("BILL SUMMARY"));
        col.add(Box.createVerticalStrut(10));
        col.add(mutedLabel("Total Charge"));
        col.add(boldLabel(money(bill.getTotalAmount()), 16));
        col.add(Box.createVerticalStrut(14));

        JLabel insuranceHeading = boldLabel("INSURANCE", 12);
        insuranceHeading.setForeground(ACCENT_BLUE);
        col.add(insuranceHeading);
        col.add(Box.createVerticalStrut(6));

        Insurance insurance = bill.getInsurance();
        if (insurance != null && insurance.isAccepted()) {
            col.add(infoRow("Insurance Provider", insurance.getInsuranceName()));
            col.add(Box.createVerticalStrut(6));
            col.add(infoRow("Coverage", String.format("%.0f%%", insurance.getCoveragePercentage() * 100)));
            col.add(Box.createVerticalStrut(6));
            double coveredAmount = bill.getTotalAmount() - bill.getPayableAmount();
            col.add(infoRow("Insurance Coverage", "- " + money(coveredAmount)));
        } else {
            col.add(infoRow("Insurance Provider", "None"));
            col.add(Box.createVerticalStrut(6));
            col.add(infoRow("Coverage", "N/A"));
            col.add(Box.createVerticalStrut(6));
            col.add(infoRow("Insurance Coverage", money(0)));
        }

        col.add(Box.createVerticalStrut(14));
        col.add(new JSeparator());
        col.add(Box.createVerticalStrut(10));

        col.add(mutedLabel("Payable Amount"));
        JLabel payable = boldLabel(money(bill.getPayableAmount()), 18);
        payable.setForeground(new Color(0, 150, 0));
        col.add(payable);
        col.add(Box.createVerticalStrut(10));

        col.add(statusRow());
        col.add(Box.createVerticalStrut(10));

        // Only an UNPAID bill can move to PAID (matches BillingManagement's
        // markBillAsPaid, which rejects already-PAID or VOID bills).
        if (bill.getStatus() == Bill.BillStatus.UNPAID) {
            JButton updateStatusBtn = new JButton("Mark as Paid");
            updateStatusBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            updateStatusBtn.setFocusPainted(false);
            updateStatusBtn.addActionListener(e -> {
                if (onUpdateStatus != null) onUpdateStatus.run();
            });
            col.add(updateStatusBtn);
        }

        col.add(Box.createVerticalGlue());
        return col;
    }

    private JPanel statusRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(mutedLabel("Status  "));

        Color bg;
        Color fg;
        switch (bill.getStatus()) {
            case PAID:
                bg = STATUS_PAID_BG;
                fg = STATUS_PAID_FG;
                break;
            case VOID:
                bg = STATUS_VOID_BG;
                fg = STATUS_VOID_FG;
                break;
            default: // UNPAID
                bg = STATUS_UNPAID_BG;
                fg = STATUS_UNPAID_FG;
        }
        RoundedPanel pill = new RoundedPanel(12, bg, 3, 12, 3, 12);
        pill.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel pillLabel = new JLabel(bill.getStatus().name());
        pillLabel.setForeground(fg);
        pillLabel.setFont(pillLabel.getFont().deriveFont(Font.BOLD, 11f));
        pill.add(pillLabel);
        row.add(pill);
        return row;
    }

    // ---------------------------------------------------------------
    // Small reusable row/label builders
    // ---------------------------------------------------------------
    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(mutedLabel(label));
        row.add(boldLabel(value, 13));
        return row;
    }

    private JPanel lineItemRow(String name, double price) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(TEXT_DARK);
        JLabel priceLabel = new JLabel(money(price));
        priceLabel.setForeground(TEXT_DARK);
        row.add(nameLabel, BorderLayout.WEST);
        row.add(priceLabel, BorderLayout.EAST);
        return row;
    }

    private JPanel totalRow(String label, double amount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(boldLabel(label, 14), BorderLayout.WEST);
        row.add(boldLabel(money(amount), 14), BorderLayout.EAST);
        return row;
    }

    private JLabel sectionTag(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel subheading(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        return label;
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

    // ---------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------
    private static String money(double amount) {
        return String.format("RM %.2f", amount);
    }

    private static int safeParseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String formatDateTime(String raw) {
        if (raw == null || raw.isBlank()) return "-";
        try {
            // Mirrors the formatting already used in DoctorShift.toString() -
            // confirm these DateTime helper signatures match your utils package.
            java.time.LocalDateTime dt = DateTime.toDateTimeObject(raw);
            return DateTime.toDateStringWords(DateTime.getDate(dt)) + ", "
                    + DateTime.toTimeString(DateTime.getTime(dt));
        } catch (Exception e) {
            return raw;
        }
    }

    /** Thin vertical divider, replaces the jSeparator1 from the old Matisse form. */
    private static class VerticalDivider extends JPanel {
        VerticalDivider() {
            setOpaque(false);
            setPreferredSize(new Dimension(1, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(DIVIDER);
            g.fillRect(0, 0, 1, getHeight());
        }
    }
}