/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.medical_manager.billing_management;

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
import oodj_hospital_management_system.models.Doctor;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.services.BillingManagement;
import oodj_hospital_management_system.services.ConsultationManagement;
import oodj_hospital_management_system.utils.DateTime;

/**
 *
 * @author sheaw
 */
public class BillReceiptsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private BillingManagement billingMgmt;
    private List<Bill> currentBills;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

    /**
     * Creates new form PatientManagement
     */
    public BillReceiptsPanel(UserController usrCtrl) {
        this.usrCtrl = usrCtrl;
        this.billingMgmt = new BillingManagement(usrCtrl.getActiveUser());
        initComponents();
        loadBills();

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
        widthCappedPanel.add(billReceiptsPanelWrapper, java.awt.BorderLayout.NORTH);

        wrapperPane.remove(jPanel2);
        wrapperPane.add(widthCappedPanel);
        jPanel2 = widthCappedPanel;
    }
    
    public void loadBills() {
        List<Bill> allBills = BillingManagement.getAllBills();
        List<Bill> assignedBills = new ArrayList<>();
        for (Bill bill : allBills) {
            if (isAssignedDoctorBill(bill)) {
                assignedBills.add(bill);
            }
        }
        currentBills = assignedBills;
        rebuildCards(assignedBills);
    }
    
    private boolean isAssignedDoctorBill(Bill bill) {
        Doctor doctor = resolveDoctor(bill);
        User activeUser = usrCtrl.getActiveUser();
        return doctor != null
                && doctor.getMedicalManager() != null
                && doctor.getMedicalManager().getId().equals(activeUser.getId());
    }
    
    private void markBillAsPaid(Bill bill) {
        if (!isAssignedDoctorBill(bill)) {
            JOptionPane.showMessageDialog(parentWindow,
                "This bill belongs to a doctor who is not assigned to you, so you cannot mark it as paid.",
                "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LinkedHashMap<String, String> result = billingMgmt.markBillAsPaid(bill);
        if (!"true".equals(result.get("success"))) {
            JOptionPane.showMessageDialog(parentWindow, result.get("msg"), "Update Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadBills();
    }
 
    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }
    
    /** Clears assetPanelWrapper and re-adds one BillReceipt card per bill. */
    private void rebuildCards(List<Bill> bills) {
        billReceiptsPanelWrapper.removeAll();
        for (Bill bill : bills) {
            BillReceipt billPanel = new BillReceipt(bill, () -> markBillAsPaid(bill), this.usrCtrl);
            billReceiptsPanelWrapper.add(billPanel);
            billReceiptsPanelWrapper.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }
        jPanel2.revalidate();      // let jPanel2 recompute its BorderLayout size
        wrapperPane.revalidate();  // let the outer BoxLayout row recompute too
        billReceiptsPanelWrapper.revalidate();
        billReceiptsPanelWrapper.repaint();
    }
 
    // Bill -> Consultation -> ConsultationSlot -> DoctorShift -> Doctor
    private Doctor resolveDoctor(Bill bill) {
        Consultation c = ConsultationManagement.getConsultationById(bill.getConsultationId());
        if (c == null || c.getConsultationSlot() == null) return null;
        if (c.getConsultationSlot().getDoctorShift() == null) return null;
        return c.getConsultationSlot().getDoctorShift().getDoctor();
    }
    
    // Bill -> Consultation -> ConsultationType -> name
    private String resolveConsultationTypeName(Bill bill) {
        Consultation c = ConsultationManagement.getConsultationById(bill.getConsultationId());
        if (c == null || c.getConsultationType() == null) return null;
        return c.getConsultationType().getTypeName();
    }
 
    // Bill -> Consultation -> ConsultationSlot -> startDateTime (parsed)
    private LocalDateTime resolveConsultationDateTime(Bill bill) {
        Consultation c = ConsultationManagement.getConsultationById(bill.getConsultationId());
        if (c == null || c.getConsultationSlot() == null) return null;
        String raw = c.getConsultationSlot().getStartDateTime();
        if (raw == null || raw.isBlank()) return null;
        try {
            return DateTime.toDateTimeObject(raw);
        } catch (Exception e) {
            return null;
        }
    }
    
    private LinkedHashMap<String, String> selectDateRange() {
        LinkedHashMap<String, String> dateRange = new LinkedHashMap<>();
        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.add(new javax.swing.JLabel("From:"));
        panel.add(startDateChooser);
        panel.add(new javax.swing.JLabel("To:"));
        panel.add(endDateChooser);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Select Consultation Date Range", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Date startDateRaw = startDateChooser.getDate();
            Date endDateRaw = endDateChooser.getDate();

            if (startDateRaw == null && endDateRaw == null) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Please select at least one date", "Missing Date", JOptionPane.INFORMATION_MESSAGE);
                return dateRange;
            }
            if (startDateRaw != null && endDateRaw != null && endDateRaw.compareTo(startDateRaw) < 0) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Invalid date range", "WARNING", JOptionPane.ERROR_MESSAGE);
                return dateRange;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateRange.put("startDate", startDateRaw != null ? sdf.format(startDateRaw) : null);
            dateRange.put("endDate", endDateRaw != null ? sdf.format(endDateRaw) : null);
        }
        return dateRange;
    }

    private LinkedHashMap<String, String> selectAmountRange() {
        LinkedHashMap<String, String> amountRange = new LinkedHashMap<>();
        javax.swing.JTextField minField = new javax.swing.JTextField(10);
        javax.swing.JTextField maxField = new javax.swing.JTextField(10);

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.add(new javax.swing.JLabel("Min (RM):"));
        panel.add(minField);
        panel.add(new javax.swing.JLabel("Max (RM):"));
        panel.add(maxField);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel, "Select Payable Amount Range", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String minStr = minField.getText().trim();
            String maxStr = maxField.getText().trim();

            if (minStr.isEmpty() && maxStr.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Please enter at least one amount", "Missing Amount", JOptionPane.INFORMATION_MESSAGE);
                return amountRange;
            }
            if (minStr.isEmpty() && maxStr.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Do not enter negative values", "Negative Value", JOptionPane.INFORMATION_MESSAGE);
                return amountRange;
            }
            try {
                double minVal = minStr.isEmpty() ? Double.NEGATIVE_INFINITY : Double.parseDouble(minStr);
                double maxVal = maxStr.isEmpty() ? Double.POSITIVE_INFINITY : Double.parseDouble(maxStr);

                if (minVal < 0 || maxVal < 0) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Amount cannot be negative", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return amountRange;
                }
                if (!minStr.isEmpty() && !maxStr.isEmpty() && maxVal < minVal) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Invalid amount range", "WARNING", JOptionPane.ERROR_MESSAGE);
                    return amountRange;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Please enter valid numbers", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return amountRange;
            }

            amountRange.put("min", minStr.isEmpty() ? null : minStr);
            amountRange.put("max", maxStr.isEmpty() ? null : maxStr);
        }
        return amountRange;
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
        billReceiptsPanelWrapper = new javax.swing.JPanel();

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Patient", "Doctor", "Consultation Date", "Payable Amount" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Payment Status", "Consultation Type", "Consultation Date", "Total Charges" }));
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

        billReceiptsPanelWrapper.setBackground(new java.awt.Color(246, 251, 255));
        billReceiptsPanelWrapper.setLayout(new javax.swing.BoxLayout(billReceiptsPanelWrapper, javax.swing.BoxLayout.Y_AXIS));
        jPanel2.add(billReceiptsPanelWrapper, java.awt.BorderLayout.NORTH);

        wrapperPane.add(jPanel2);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        searchButtonActionPerformed(evt);
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText();
        List<Bill> assignedBills = new ArrayList<>();
        for (Bill bill : BillingManagement.getAllBills()) {
            if (isAssignedDoctorBill(bill)) {
                assignedBills.add(bill);
            }
        }
        List<Bill> searchResults = billingMgmt.searchBills(keyword, assignedBills);
        currentBills = searchResults;
        billReceiptsPanelWrapper.removeAll();
        for (Bill bill : searchResults) {
            BillReceipt billPanel = new BillReceipt(bill, () -> markBillAsPaid(bill), this.usrCtrl);
            billReceiptsPanelWrapper.add(billPanel);
            billReceiptsPanelWrapper.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }
        updateResultLabel("Showing results for \"" + keyword + "\"", searchResults.size());
        jPanel2.revalidate();
        wrapperPane.revalidate();
        billReceiptsPanelWrapper.revalidate();
        billReceiptsPanelWrapper.repaint();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        if (currentBills == null) return;
        String selected = (String) sortField.getSelectedItem();
        List<Bill> sorted = new ArrayList<>(currentBills);
 
        switch (selected) {
            case "Patient":
                sorted.sort((a, b) -> {
                    String nameA = (a.getPatient() != null) ? a.getPatient().getName() : "";
                    String nameB = (b.getPatient() != null) ? b.getPatient().getName() : "";
                    return nameA.compareToIgnoreCase(nameB);
                });
                break;
 
            case "Doctor":
                sorted.sort((a, b) -> {
                    Doctor doctorA = resolveDoctor(a);
                    Doctor doctorB = resolveDoctor(b);
                    String nameA = (doctorA != null) ? doctorA.getName() : "";
                    String nameB = (doctorB != null) ? doctorB.getName() : "";
                    return nameA.compareToIgnoreCase(nameB);
                });
                break;
 
            case "Consultation Date":
                sorted.sort((a, b) -> {
                    LocalDateTime dateA = resolveConsultationDateTime(a);
                    LocalDateTime dateB = resolveConsultationDateTime(b);
                    if (dateA == null && dateB == null) return 0;
                    if (dateA == null) return 1;  // bills with no date sink to the bottom
                    if (dateB == null) return -1;
                    return dateA.compareTo(dateB);
                });
                break;
 
            case "Payable Amount":
                sorted.sort((a, b) -> Double.compare(a.getPayableAmount(), b.getPayableAmount()));
                break;
 
            default: // "Default" -> fall back to the existing Bill ID sort
                sorted = billingMgmt.sortBillsById(sorted);
        }
 
        currentBills = sorted;
        rebuildCards(sorted);
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();
        // Filter from whatever currentBills already is - so a filter applied
        // after a sort keeps that order, and a filter applied after another
        // filter narrows further, instead of both silently resetting to the
        // full unsorted/unfiltered list.
        List<Bill> base = (currentBills != null) ? currentBills : BillingManagement.getAllBills();
        List<Bill> filtered;
 
        switch (selected) {
            case "Default":
                // No-op filter - leaves whatever's already in currentBills alone.
                filtered = base;
                updateResultLabel("Showing " + filtered.size() + " bill(s)", filtered.size());
                break;
 
            case "Payment Status": {
                String[] statusOptions = { "UNPAID", "PAID", "VOID" };
                String statusChoice = (String) JOptionPane.showInputDialog(
                        parentWindow, "Filter by status:", "Payment Status",
                        JOptionPane.PLAIN_MESSAGE, null, statusOptions, statusOptions[0]);
                if (statusChoice == null) {
                    filterByField.setSelectedIndex(0);
                    return;
                }
                Bill.BillStatus targetStatus = Bill.BillStatus.valueOf(statusChoice);
                filtered = new ArrayList<>();
                for (Bill bill : base) {
                    if (bill.getStatus() == targetStatus) filtered.add(bill);
                }
                updateResultLabel("Showing " + statusChoice + " bills", filtered.size());
                break;
            }
            
            case "Consultation Type": {
                // Collect distinct type names actually present, so the picker only
                // shows types that exist rather than a hardcoded list that can drift.
                List<String> typeOptions = base.stream()
                        .map(this::resolveConsultationTypeName)
                        .filter(name -> name != null)
                        .distinct()
                        .sorted()
                        .toList();

                if (typeOptions.isEmpty()) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "No consultation types found among current bills.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                    filterByField.setSelectedIndex(0);
                    return;
                }

                String typeChoice = (String) JOptionPane.showInputDialog(
                        parentWindow, "Filter by consultation type:", "Consultation Type",
                        JOptionPane.PLAIN_MESSAGE, null, typeOptions.toArray(), typeOptions.get(0));
                if (typeChoice == null) {
                    filterByField.setSelectedIndex(0);
                    return;
                }

                filtered = new ArrayList<>();
                for (Bill bill : base) {
                    if (typeChoice.equals(resolveConsultationTypeName(bill))) filtered.add(bill);
                }
                updateResultLabel("Showing " + typeChoice + " bills", filtered.size());
                break;
            }
 
            case "Consultation Date": {
                LinkedHashMap<String, String> dateRange = selectDateRange();
                String startStr = dateRange.get("startDate");
                String endStr = dateRange.get("endDate");
                if (startStr == null && endStr == null) {
                    filterByField.setSelectedIndex(0);
                    return;
                }
                LocalDate start = startStr != null ? LocalDate.parse(startStr) : LocalDate.MIN;
                LocalDate end = endStr != null ? LocalDate.parse(endStr) : LocalDate.MAX;
                filtered = new ArrayList<>();
                for (Bill bill : base) {
                    LocalDateTime dt = resolveConsultationDateTime(bill);
                    if (dt == null) continue;
                    LocalDate date = dt.toLocalDate();
                    if (!date.isBefore(start) && !date.isAfter(end)) filtered.add(bill);
                }
                String label = (startStr != null && endStr != null)
                    ? "Showing bills from " + startStr + " to " + endStr
                    : (startStr != null ? "Showing bills after " + startStr : "Showing bills before " + endStr);
                updateResultLabel(label, filtered.size());
                break;
            }

            case "Total Charges": {
                LinkedHashMap<String, String> amountRange = selectAmountRange();
                String minStr = amountRange.get("min");
                String maxStr = amountRange.get("max");
                if (minStr == null && maxStr == null) {
                    filterByField.setSelectedIndex(0);
                    return;
                }
                double min = minStr != null ? Double.parseDouble(minStr) : Double.NEGATIVE_INFINITY;
                double max = maxStr != null ? Double.parseDouble(maxStr) : Double.POSITIVE_INFINITY;
                filtered = new ArrayList<>();
                for (Bill bill : base) {
                    double amt = bill.getTotalAmount();
                    if (amt >= min && amt <= max) filtered.add(bill);
                }
                String label = (minStr != null && maxStr != null)
                    ? String.format("Showing bills RM%s - RM%s", minStr, maxStr)
                    : (minStr != null ? "Showing bills ≥ RM" + minStr : "Showing bills ≤ RM" + maxStr);
                updateResultLabel(label, filtered.size());
                break;
            }
 
            default:
                filtered = base;
                updateResultLabel("Showing " + filtered.size() + " bill(s)", filtered.size());
        }
 
        currentBills = filtered;
        rebuildCards(filtered);
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadBills();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel billReceiptsPanelWrapper;
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
