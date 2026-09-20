/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.gui.medical_manager.doctor_shift_rosters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.DoctorShift;
import oodj_hospital_management_system.models.DoctorShift.DoctorShiftStatus;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class ShiftRosterGridPanel extends JPanel implements javax.swing.Scrollable {

    /** Implemented by the parent to handle a click on a shift card (e.g. cancel dialog). */
    public interface ShiftClickListener {
        void onShiftClicked(DoctorShift shift);
    }

    private static final DateTimeFormatter TIME_DISPLAY_FMT = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter DAY_HEADER_FMT = DateTimeFormatter.ofPattern("d MMM");

    // Caps so GridLayout cells don't stretch to fill the parent/viewport.
    private static final int DOCTOR_COL_WIDTH = 160;
    private static final int DAY_COL_WIDTH = 130;
    private static final int HEADER_ROW_HEIGHT = 50;
    private static final int DAY_ROW_HEIGHT = 90;
    private static final int MIN_DOCTOR_COL_WIDTH = 160;
    private static final int MIN_DAY_COL_WIDTH = 130;
    
    private List<Doctor> lastDoctors;
    private List<DoctorShift> lastShifts;
    private LocalDate lastWeekStart;
    private javax.swing.Timer resizeDebounceTimer;

    private ShiftClickListener shiftClickListener;
    private int doctorRowCount = 0;

    public ShiftRosterGridPanel() {
        setBackground(java.awt.Color.WHITE);
    }

    public void setShiftClickListener(ShiftClickListener listener) {
        this.shiftClickListener = listener;
    }

    @Override
    public java.awt.Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
        return orientation == javax.swing.SwingConstants.VERTICAL ? DAY_ROW_HEIGHT : DAY_COL_WIDTH;
    }

    @Override
    public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
        return orientation == javax.swing.SwingConstants.VERTICAL ? DAY_ROW_HEIGHT * 3 : DAY_COL_WIDTH * 3;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * Rebuilds the grid for the given doctors/shifts/week.
     * @param doctors   rows to display, already filtered + sorted by the caller
     * @param shifts    all shifts (this panel filters per-doctor/per-day itself)
     * @param weekStart Monday of the week being displayed
     */
    
    private List<JPanel> buildHeaderCells(LocalDate weekStart) {
        List<JPanel> cells = new java.util.ArrayList<>();
        cells.add(makeHeaderCell("Doctor"));
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            String dayName = day.getDayOfWeek().toString().substring(0, 1)
                    + day.getDayOfWeek().toString().substring(1, 3).toLowerCase();
            cells.add(makeHeaderCell(dayName + "\n" + day.format(DAY_HEADER_FMT)));
        }
        return cells;
    }
    
    private List<JPanel> buildDoctorRowCells(Doctor doctor, List<DoctorShift> shifts, LocalDate weekStart) {
        List<JPanel> cells = new java.util.ArrayList<>();
        cells.add(makeDoctorInfoCell(doctor));
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            cells.add(makeDayCell(doctor, day, shifts));
        }
        return cells;
    }
    
    public void handleViewportResized() {
        if (lastDoctors == null) return; // nothing loaded yet
        if (resizeDebounceTimer != null && resizeDebounceTimer.isRunning()) {
            resizeDebounceTimer.restart();
            return;
        }
        resizeDebounceTimer = new javax.swing.Timer(120, e -> updateData(lastDoctors, lastShifts, lastWeekStart));
        resizeDebounceTimer.setRepeats(false);
        resizeDebounceTimer.start();
    }

    public void updateData(List<Doctor> doctors, List<DoctorShift> shifts, LocalDate weekStart) {
        lastDoctors = doctors;
        lastShifts = shifts;
        lastWeekStart = weekStart;

        removeAll();
        doctorRowCount = doctors.size();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        int[] colWidths = computeColumnWidths();

        add(makeRow(buildHeaderCells(weekStart), colWidths));
        for (Doctor doctor : doctors) {
            add(makeRow(buildDoctorRowCells(doctor, shifts, weekStart), colWidths));
        }

        revalidate();
        repaint();
    }

    /** [0]=doctor col, [1..7]=day cols. Grows past the minimum to fill the viewport's width. */
    private int[] computeColumnWidths() {
        int minTotal = MIN_DOCTOR_COL_WIDTH + MIN_DAY_COL_WIDTH * 7;
        int available = (getParent() instanceof javax.swing.JViewport) ? getParent().getWidth() : 0;

        int[] widths = new int[8];
        widths[0] = MIN_DOCTOR_COL_WIDTH;
        for (int i = 1; i <= 7; i++) widths[i] = MIN_DAY_COL_WIDTH;

        if (available > minTotal) {
            int extraPerDay = (available - minTotal) / 7;
            for (int i = 1; i <= 7; i++) widths[i] += extraPerDay;
        }
        return widths;
    }

    private JPanel makeRow(List<JPanel> cells, int[] colWidths) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(java.awt.Color.WHITE);
        row.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        for (int i = 0; i < cells.size(); i++) {
            JPanel cell = cells.get(i);
            int colWidth = colWidths[i];
            cell.setMinimumSize(new java.awt.Dimension(colWidth, cell.getMinimumSize().height));
            cell.setPreferredSize(new java.awt.Dimension(colWidth, cell.getPreferredSize().height));
            cell.setMaximumSize(new java.awt.Dimension(colWidth, Integer.MAX_VALUE));
            row.add(cell);
        }
        return row;
    }

    private JPanel makeHeaderCell(String text) {
        JPanel cell = new JPanel(new java.awt.BorderLayout());
        cell.setBackground(new java.awt.Color(245, 246, 250));
        cell.setBorder(BorderFactory.createLineBorder(new java.awt.Color(225, 227, 232)));
        JLabel label = new JLabel("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        cell.add(label, java.awt.BorderLayout.CENTER);
        cell.setPreferredSize(new java.awt.Dimension(0, 50));
        return cell;
    }

    private JPanel makeDoctorInfoCell(Doctor doctor) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBackground(java.awt.Color.WHITE);
        cell.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(225, 227, 232)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel nameLabel = new JLabel(doctor.getName());
        nameLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));

        String deptName = (doctor.getMedicalDepartment() != null
                && doctor.getMedicalDepartment().getDepartmentId() != null
                && !doctor.getMedicalDepartment().getDepartmentId().isEmpty())
                ? doctor.getMedicalDepartment().getDepartmentName()
                : "Unassigned";
        JLabel deptLabel = new JLabel(deptName);
        deptLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        deptLabel.setForeground(new java.awt.Color(65, 105, 225));

        cell.add(nameLabel);
        cell.add(deptLabel);
        return cell;
    }

    private JPanel makeDayCell(Doctor doctor, LocalDate day, List<DoctorShift> shifts) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBackground(java.awt.Color.WHITE);
        cell.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(225, 227, 232)),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        List<DoctorShift> dayShifts = shifts.stream()
            .filter(s -> s.getDoctor().equals(doctor))
            .filter(s -> DateTime.toDateTimeObject(s.getStartDateTime()).toLocalDate().equals(day))
            .toList();

        if (dayShifts.isEmpty()) {
            JLabel dash = new JLabel("—");
            dash.setForeground(java.awt.Color.LIGHT_GRAY);
            dash.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            cell.add(Box.createVerticalGlue());
            cell.add(dash);
            cell.add(Box.createVerticalGlue());
        } else {
            for (DoctorShift shift : dayShifts) {
                cell.add(makeShiftCard(shift));
                cell.add(Box.createVerticalStrut(4));
            }
        }
        return cell;
    }

    private JPanel makeShiftCard(DoctorShift shift) {
        java.awt.Color bg = getStatusBgColor(shift.getStatus());
        java.awt.Color fg = getStatusTextColor(shift.getStatus());

        JPanel card = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            8, bg, 6, 8, 6, 8
        );
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        card.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new java.awt.Dimension(200, 70));

        LocalDateTime start = DateTime.toDateTimeObject(shift.getStartDateTime());
        LocalDateTime end = DateTime.toDateTimeObject(shift.getEndDateTime());
        JLabel timeLabel = new JLabel(start.format(TIME_DISPLAY_FMT) + " - " + end.format(TIME_DISPLAY_FMT));
        timeLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        timeLabel.setForeground(fg);

        JLabel statusLabel = new JLabel(capitalize(shift.getStatus().name()));
        statusLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        statusLabel.setForeground(fg);

        JLabel idLabel = new JLabel(shift.getShiftId());
        idLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 10));
        idLabel.setForeground(fg);

        card.add(timeLabel);
        card.add(statusLabel);
        card.add(idLabel);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (shiftClickListener != null) {
                    shiftClickListener.onShiftClicked(shift);
                }
            }
        });

        return card;
    }

    private java.awt.Color getStatusBgColor(DoctorShiftStatus status) {
        switch (status) {
            case SCHEDULED: return new java.awt.Color(225, 235, 250);
            case IN_PROGRESS: return new java.awt.Color(225, 245, 230);
            case COMPLETED: return new java.awt.Color(235, 235, 235);
            case CANCELLED: return new java.awt.Color(250, 225, 225);
            default: return java.awt.Color.LIGHT_GRAY;
        }
    }

    private java.awt.Color getStatusTextColor(DoctorShiftStatus status) {
        switch (status) {
            case SCHEDULED: return new java.awt.Color(41, 98, 255);
            case IN_PROGRESS: return new java.awt.Color(40, 140, 70);
            case COMPLETED: return new java.awt.Color(110, 110, 110);
            case CANCELLED: return new java.awt.Color(200, 30, 30);
            default: return java.awt.Color.DARK_GRAY;
        }
    }

    private String capitalize(String s) {
        String withSpaces = s.replace("_", " ").toLowerCase();
        return Character.toUpperCase(withSpaces.charAt(0)) + withSpaces.substring(1);
    }
}