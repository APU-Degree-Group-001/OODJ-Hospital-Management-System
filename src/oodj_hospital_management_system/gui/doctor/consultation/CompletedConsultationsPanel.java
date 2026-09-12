/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.doctor.consultation;

import oodj_hospital_management_system.gui.medical_manager.billing_management.*;
import com.toedter.calendar.JDateChooser;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JOptionPane;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.models.Bill;
import oodj_hospital_management_system.models.Consultation;
import oodj_hospital_management_system.models.Consultation.ConsultationStatus;
import oodj_hospital_management_system.models.ConsultationType;
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.Patient;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.BillingManagement;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class CompletedConsultationsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<Consultation> currentConsultations;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private javax.swing.JPanel cardContainer;
    private java.awt.CardLayout cardLayout;

    /**
     * Creates new form PatientManagement
     */
    public CompletedConsultationsPanel(UserController usrCtrl, javax.swing.JPanel cardContainer) {
        this.usrCtrl = usrCtrl;
        this.cardContainer = cardContainer;
        this.cardLayout = (java.awt.CardLayout) cardContainer.getLayout();;
        initComponents();
        loadCompletedConsultations();

        // Refresh data every time this panel is shown (e.g. via CardLayout),
        // not just on first construction - addNotify() only fires once if the
        // panel stays in the container hierarchy and is just toggled with .show().
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadCompletedConsultations();
            }
        });

        showResultsForPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, showResultsForPanel.getPreferredSize().height));
        
        // jPanel2 (built by initComponents) currently has no width cap and gets
        // stretched wide by BoxLayout in wrapperPane. Swap it for a version that
        // caps its own preferred width, carrying over its real content.
        javax.swing.JPanel widthCappedPanel = new javax.swing.JPanel(new java.awt.BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.min(d.width, 900), d.height);
            }
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        widthCappedPanel.setBackground(jPanel2.getBackground());
        widthCappedPanel.setBorder(jPanel2.getBorder());
        widthCappedPanel.setAlignmentY(0.0F);
        widthCappedPanel.add(consultationCardsPanelWrapper, java.awt.BorderLayout.NORTH);

        wrapperPane.remove(jPanel2);
        wrapperPane.add(widthCappedPanel);
        jPanel2 = widthCappedPanel;
        wrapperPane.revalidate();
        wrapperPane.repaint();
    }
    
    public void loadCompletedConsultations() {
        List<Consultation> completed = getBaseCompletedConsultations();
        currentConsultations = completed;
        rebuildCards(completed);
    }

    private void openConsultationDetails(Consultation consultation) {
        ConsultationViewDetailsPanel detailsPanel = new ConsultationViewDetailsPanel(
            usrCtrl,
            consultation,
            () -> {
                // Back button: drop this card and return to the completed-consultations list
                cardLayout.show(cardContainer, "completedConsultationSlots");
            }
        );

        String cardName = "consultationDetails-" + consultation.getConsultationId();
        cardContainer.add(detailsPanel, cardName);
        cardLayout.show(cardContainer, cardName);
    }
    
    private List<Consultation> getBaseCompletedConsultations() {
        List<Consultation> all = usrCtrl.getConsultationController().getAllConsultations();
        List<Consultation> completed = new ArrayList<>();

        Doctor activeDoctor = (Doctor) usrCtrl.getActiveUser();
        String activeDoctorId = activeDoctor != null ? activeDoctor.getId() : null;

        for (Consultation c : all) {
            if (c.getConsultationStatus() != ConsultationStatus.COMPLETED) continue;
            Doctor doctor = resolveDoctor(c);
            if (doctor != null && activeDoctorId != null && doctor.getId().equals(activeDoctorId)) {
                completed.add(c);
            }
        }
        return completed;
    }

    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }

    /** Clears consultationCardsPanelWrapper and re-adds one ConsultationCard per consultation. */
    private void rebuildCards(List<Consultation> consultations) {
        consultationCardsPanelWrapper.removeAll();
        for (Consultation c : consultations) {
            ConsultationCard card = new ConsultationCard(c, () -> openConsultationDetails(c));
            consultationCardsPanelWrapper.add(card);
            consultationCardsPanelWrapper.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }
        jPanel2.revalidate();      // let jPanel2 recompute its BorderLayout size
        wrapperPane.revalidate();  // let the outer BoxLayout row recompute too
        consultationCardsPanelWrapper.revalidate();
        consultationCardsPanelWrapper.repaint();
    }

    // Consultation -> ConsultationSlot -> DoctorShift -> Doctor
    private Doctor resolveDoctor(Consultation c) {
        if (c == null || c.getConsultationSlot() == null) return null;
        if (c.getConsultationSlot().getDoctorShift() == null) return null;
        return c.getConsultationSlot().getDoctorShift().getDoctor();
    }

    // Consultation -> ConsultationSlot -> startDateTime (parsed)
    private LocalDateTime resolveConsultationDateTime(Consultation c) {
        if (c == null || c.getConsultationSlot() == null) return null;
        String raw = c.getConsultationSlot().getStartDateTime();
        if (raw == null || raw.isBlank()) return null;
        try {
            return DateTime.toDateTimeObject(raw);
        } catch (Exception e) {
            return null;
        }
    }

    private LinkedHashMap<String, String> selectDateTimeRange() {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();

        javax.swing.SpinnerDateModel startTimeModel = new javax.swing.SpinnerDateModel();
        javax.swing.JSpinner startTimeSpinner = new javax.swing.JSpinner(startTimeModel);
        startTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(startTimeSpinner, "hh:mm a"));

        javax.swing.SpinnerDateModel endTimeModel = new javax.swing.SpinnerDateModel();
        javax.swing.JSpinner endTimeSpinner = new javax.swing.JSpinner(endTimeModel);
        endTimeSpinner.setEditor(new javax.swing.JSpinner.DateEditor(endTimeSpinner, "hh:mm a"));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new javax.swing.JLabel("From Date:"));
        panel.add(startDateChooser);
        panel.add(new javax.swing.JLabel("To Date:"));
        panel.add(endDateChooser);
        panel.add(new javax.swing.JLabel("From Time:"));
        panel.add(startTimeSpinner);
        panel.add(new javax.swing.JLabel("To Time:"));
        panel.add(endTimeSpinner);
        panel.add(new javax.swing.JLabel("(Leave any field blank/unchanged if not filtering by it)"));

        // Track whether the user actually touched the time spinners, since
        // JSpinner always holds *some* value (defaults to "now") even if untouched.
        final boolean[] startTimeTouched = {false};
        final boolean[] endTimeTouched = {false};
        startTimeSpinner.addChangeListener(e -> startTimeTouched[0] = true);
        endTimeSpinner.addChangeListener(e -> endTimeTouched[0] = true);

        int dialogResult = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Date / Time", JOptionPane.OK_CANCEL_OPTION);

        if (dialogResult != JOptionPane.OK_OPTION) {
            return result;
        }

        Date startDateRaw = startDateChooser.getDate();
        Date endDateRaw = endDateChooser.getDate();

        if (startDateRaw != null && endDateRaw != null && endDateRaw.compareTo(startDateRaw) < 0) {
            JOptionPane.showMessageDialog(parentWindow,
                "Invalid date range", "WARNING", JOptionPane.ERROR_MESSAGE);
            return result;
        }

        String startTimeStr = null;
        String endTimeStr = null;
        if (startTimeTouched[0] || endTimeTouched[0]) {
            SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
            startTimeStr = startTimeTouched[0] ? timeSdf.format((Date) startTimeSpinner.getValue()) : null;
            endTimeStr = endTimeTouched[0] ? timeSdf.format((Date) endTimeSpinner.getValue()) : null;

            if (startTimeStr != null && endTimeStr != null
                    && java.time.LocalTime.parse(endTimeStr).isBefore(java.time.LocalTime.parse(startTimeStr))) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Invalid time range", "WARNING", JOptionPane.ERROR_MESSAGE);
                return result;
            }
        }

        if (startDateRaw == null && endDateRaw == null && startTimeStr == null && endTimeStr == null) {
            JOptionPane.showMessageDialog(parentWindow,
                "Please enter at least one date or time", "Missing Input",
                JOptionPane.INFORMATION_MESSAGE);
            return result;
        }

        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        result.put("startDate", startDateRaw != null ? dateSdf.format(startDateRaw) : null);
        result.put("endDate", endDateRaw != null ? dateSdf.format(endDateRaw) : null);
        result.put("startTime", startTimeStr);
        result.put("endTime", endTimeStr);

        return result;
    }
    
    private ConsultationType selectConsultationType(List<Consultation> source) {
        LinkedHashMap<String, ConsultationType> distinctTypes = new LinkedHashMap<>();
        for (Consultation c : source) {
            ConsultationType t = c.getConsultationType();
            if (t != null && !distinctTypes.containsKey(t.getTypeName())) {
                distinctTypes.put(t.getTypeName(), t);
            }
        }

        if (distinctTypes.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No consultation types available to filter by", "No Data",
                JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        javax.swing.JComboBox<String> typeCombo =
            new javax.swing.JComboBox<>(distinctTypes.keySet().toArray(new String[0]));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new javax.swing.JLabel("Consultation Type:"));
        panel.add(typeCombo);

        int dialogResult = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Consultation Type", JOptionPane.OK_CANCEL_OPTION);

        if (dialogResult != JOptionPane.OK_OPTION) {
            return null;
        }

        String selectedName = (String) typeCombo.getSelectedItem();
        return distinctTypes.get(selectedName);
    }
    
    private void filterByDateTimeRange(String startDate, String endDate, String startTime, String endTime) {
        List<Consultation> baseConsultations = getBaseCompletedConsultations();
        List<Consultation> filtered = new ArrayList<>();
        java.time.LocalTime startT = startTime != null ? java.time.LocalTime.parse(startTime) : null;
        java.time.LocalTime endT = endTime != null ? java.time.LocalTime.parse(endTime) : null;

        for (Consultation c : baseConsultations) {
            LocalDateTime dt = resolveConsultationDateTime(c);
            if (dt == null) continue;
            LocalDate date = dt.toLocalDate();
            java.time.LocalTime time = dt.toLocalTime();

            boolean afterStartDate = startDate == null || !date.isBefore(LocalDate.parse(startDate));
            boolean beforeEndDate = endDate == null || !date.isAfter(LocalDate.parse(endDate));
            boolean afterStartTime = startT == null || !time.isBefore(startT);
            boolean beforeEndTime = endT == null || !time.isAfter(endT);

            if (afterStartDate && beforeEndDate && afterStartTime && beforeEndTime) {
                filtered.add(c);
            }
        }

        currentConsultations = filtered;
        rebuildCards(filtered);

        SimpleDateFormat display = new SimpleDateFormat("dd MMM yyyy");
        StringBuilder label = new StringBuilder("Showing consultations");
        if (startDate != null || endDate != null) {
            label.append(" on ");
            label.append(startDate != null ? display.format(java.sql.Date.valueOf(startDate)) : "any date");
            label.append(" to ");
            label.append(endDate != null ? display.format(java.sql.Date.valueOf(endDate)) : "any date");
        }
        if (startT != null || endT != null) {
            label.append(" between ");
            label.append(startT != null ? DateTime.toTimeString(startT) : "any time");
            label.append(" and ");
            label.append(endT != null ? DateTime.toTimeString(endT) : "any time");
        }
        updateResultLabel(label.toString(), filtered.size());
    }
    
    private void filterByConsultationType(ConsultationType type) {
        List<Consultation> filtered = new ArrayList<>();
        for (Consultation c : getBaseCompletedConsultations()) {
            ConsultationType t = c.getConsultationType();
            if (t != null && t.getTypeName().equals(type.getTypeName())) {
                filtered.add(c);
            }
        }
        currentConsultations = filtered;
        rebuildCards(filtered);
        updateResultLabel("Showing consultations of type \"" + type.getTypeName() + "\"", filtered.size());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jOptionPane1 = new javax.swing.JOptionPane();
        jPanel1 = new javax.swing.JPanel();
        searchPane = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        resetSearchButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        sortByPane = new javax.swing.JPanel();
        sortByLabel = new javax.swing.JLabel();
        sortField = new javax.swing.JComboBox<>();
        sortByLabel1 = new javax.swing.JLabel();
        filterByField = new javax.swing.JComboBox<>();
        wrapperPane = new javax.swing.JPanel();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        consultationCardsPanelWrapper = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1160, 2147483647));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(246, 251, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1160, 50));
        jPanel1.setLayout(new java.awt.BorderLayout());

        searchPane.setBackground(new java.awt.Color(246, 251, 255));

        searchField.setMinimumSize(new java.awt.Dimension(250, 22));
        searchField.setPreferredSize(new java.awt.Dimension(250, 40));
        searchField.addActionListener(this::searchFieldActionPerformed);
        searchPane.add(searchField);

        searchButton.setText("Search");
        searchButton.addActionListener(this::searchButtonActionPerformed);
        searchPane.add(searchButton);

        resetSearchButton.setText("Reset");
        resetSearchButton.addActionListener(this::resetSearchButtonActionPerformed);
        searchPane.add(resetSearchButton);

        jPanel1.add(searchPane, java.awt.BorderLayout.WEST);
        jPanel1.add(filler1, java.awt.BorderLayout.LINE_END);

        sortByPane.setBackground(new java.awt.Color(246, 251, 255));

        sortByLabel.setText("Sort By");
        sortByPane.add(sortByLabel);

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Patient", "Consultation Date Time", "Consultation Type" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Consultation Type", "Consultation Date Time" }));
        filterByField.setPreferredSize(new java.awt.Dimension(150, 30));
        filterByField.setRequestFocusEnabled(false);
        filterByField.addActionListener(this::filterByFieldActionPerformed);
        sortByPane.add(filterByField);

        jPanel1.add(sortByPane, java.awt.BorderLayout.LINE_END);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.Y_AXIS));

        showResultsForPanel.setBackground(new java.awt.Color(246, 251, 255));
        showResultsForPanel.setAlignmentY(0.0F);
        showResultsForPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        showResultsForPanel.add(showResultsForLabel);

        wrapperPane.add(showResultsForPanel);

        jPanel2.setBackground(new java.awt.Color(246, 251, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 40));
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new java.awt.BorderLayout());

        consultationCardsPanelWrapper.setBackground(new java.awt.Color(246, 251, 255));
        consultationCardsPanelWrapper.setLayout(new javax.swing.BoxLayout(consultationCardsPanelWrapper, javax.swing.BoxLayout.Y_AXIS));
        jPanel2.add(consultationCardsPanelWrapper, java.awt.BorderLayout.NORTH);

        wrapperPane.add(jPanel2);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        searchButtonActionPerformed(evt);
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String rawKeyword = searchField.getText();
        String keyword = rawKeyword.trim().toLowerCase();

        Doctor activeDoctor = (Doctor) usrCtrl.getActiveUser();
        String activeDoctorId = activeDoctor != null ? activeDoctor.getId() : null;

        List<Consultation> all = usrCtrl.getConsultationController().getAllConsultations();
        List<Consultation> results = new ArrayList<>();
        for (Consultation c : all) {
            if (c.getConsultationStatus() != ConsultationStatus.COMPLETED) continue;

            Doctor doctor = resolveDoctor(c);
            if (doctor == null || activeDoctorId == null || !doctor.getId().equals(activeDoctorId)) continue;

            Patient p = c.getPatient();
            String description = c.getDescription() != null ? c.getDescription() : "";

            boolean match = c.getConsultationId().toLowerCase().contains(keyword)
                || (p != null && (p.getId().toLowerCase().contains(keyword) || p.getName().toLowerCase().contains(keyword)))
                || doctor.getName().toLowerCase().contains(keyword)
                || description.toLowerCase().contains(keyword);

            if (match) {
                results.add(c);
            }
        }
        currentConsultations = results;
        rebuildCards(results);
        updateResultLabel("Showing results for \"" + rawKeyword + "\"", results.size());
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        if (currentConsultations == null) return;
        String selected = (String) sortField.getSelectedItem();
        List<Consultation> sorted = new ArrayList<>(currentConsultations);
 
        switch (selected) {
            case "Patient":
                sorted.sort((a, b) -> {
                    String nameA = (a.getPatient() != null) ? a.getPatient().getName() : "";
                    String nameB = (b.getPatient() != null) ? b.getPatient().getName() : "";
                    return nameA.compareToIgnoreCase(nameB);
                });
                break;

            case "Consultation Date Time":
                sorted.sort((a, b) -> {
                    LocalDateTime dateA = resolveConsultationDateTime(a);
                    LocalDateTime dateB = resolveConsultationDateTime(b);
                    if (dateA == null && dateB == null) return 0;
                    if (dateA == null) return 1;  // consultations with no date sink to the bottom
                    if (dateB == null) return -1;
                    return dateA.compareTo(dateB);
                });
                break;
 
            case "Consultation Type":
                sorted.sort((a, b) -> {
                    ConsultationType typeA = a.getConsultationType();
                    ConsultationType typeB = b.getConsultationType();
                    String nameA = (typeA != null) ? typeA.getTypeName() : "";
                    String nameB = (typeB != null) ? typeB.getTypeName() : "";
                    return nameA.compareToIgnoreCase(nameB);
                });
                break;
 
            default: // "Default" -> fall back to sorting by Consultation ID
                sorted.sort((a, b) -> a.getConsultationId().compareToIgnoreCase(b.getConsultationId()));
        }
 
        currentConsultations = sorted;
        rebuildCards(sorted);
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();
        List<Consultation> base = getBaseCompletedConsultations();

        switch (selected) {
            case "Consultation Date Time": {
                LinkedHashMap<String, String> range = selectDateTimeRange();
                if (range.get("startDate") != null || range.get("endDate") != null
                        || range.get("startTime") != null || range.get("endTime") != null) {
                    filterByDateTimeRange(range.get("startDate"), range.get("endDate"),
                        range.get("startTime"), range.get("endTime"));
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Consultation Type": {
                ConsultationType selectedType = selectConsultationType(base); // dropdown always shows all types
                if (selectedType != null) {
                    filterByConsultationType(selectedType);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            default: // "Default" -> no filter
                currentConsultations = base;
                rebuildCards(base);
                updateResultLabel("Showing " + base.size() + " consultation(s)", base.size());
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadCompletedConsultations();
        searchField.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel consultationCardsPanelWrapper;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton resetSearchButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel searchPane;
    private javax.swing.JLabel showResultsForLabel;
    private javax.swing.JPanel showResultsForPanel;
    private javax.swing.JLabel sortByLabel;
    private javax.swing.JLabel sortByLabel1;
    private javax.swing.JPanel sortByPane;
    private javax.swing.JComboBox<String> sortField;
    private javax.swing.JPanel wrapperPane;
    // End of variables declaration//GEN-END:variables
}
