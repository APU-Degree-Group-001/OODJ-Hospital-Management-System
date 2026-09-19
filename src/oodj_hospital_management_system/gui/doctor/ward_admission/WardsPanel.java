/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.doctor.ward_admission;

import oodj_hospital_management_system.gui.admin.hospitalAssets.*;
import oodj_hospital_management_system.gui.admin.user_management.*;
import com.toedter.calendar.JDateChooser;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.User;
import oodj_hospital_management_system.models.Ward;
import oodj_hospital_management_system.services.HospitalAssetManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.WordProcessing;

/**
 *
 * @author sheaw
 */
public class WardsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<HospitalAsset> currentAssets;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private java.awt.CardLayout cardLayout;
    private javax.swing.JPanel cardContainer;

    /**
     * Creates new form PatientManagement
     */
    public WardsPanel(UserController usrCtrl, javax.swing.JPanel cardContainer) {
        this.usrCtrl = usrCtrl;
        this.cardLayout = (java.awt.CardLayout) cardContainer.getLayout();
        this.cardContainer = cardContainer;
        initComponents();
        loadAssets();
        jPanel2.setPreferredSize(null);
        jPanel2.setMaximumSize(new java.awt.Dimension(900, Integer.MAX_VALUE));
    }
    
    public void loadAssets() {
        assetPanelWrapper.removeAll();

        List<HospitalAsset> allAssets = getBaseAssets(); // now wards-only
        currentAssets = allAssets;

        for (HospitalAsset asset : allAssets) {
            HospitalAssetPanel assetPanel = new HospitalAssetPanel(usrCtrl, asset, () -> openAssetAllocation(asset), this::loadAssets);
            assetPanelWrapper.add(assetPanel);
            assetPanelWrapper.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }

        jPanel2.revalidate();
        wrapperPane.revalidate();
        assetPanelWrapper.revalidate();
        assetPanelWrapper.repaint();
    }
    
    private void populateAssetPanels(List<HospitalAsset> assets) {
        assetPanelWrapper.removeAll();
        for (HospitalAsset asset : assets) {
            HospitalAssetPanel assetPanel = new HospitalAssetPanel(usrCtrl, asset, () -> openAssetAllocation(asset), this::loadAssets);
            assetPanelWrapper.add(assetPanel);
            assetPanelWrapper.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }
        jPanel2.revalidate();
        wrapperPane.revalidate();
        assetPanelWrapper.revalidate();
        assetPanelWrapper.repaint();
    }
    
    private List<HospitalAsset> getBaseAssets() {
        List<HospitalAsset> allAssets = HospitalAssetManagement.getAllHospitalAssets();
        if (allAssets == null) return new ArrayList<>();
        return allAssets.stream()
            .filter(a -> a instanceof Ward)
            .collect(java.util.stream.Collectors.toList());
    }
    
    private void openAssetAllocation(HospitalAsset asset) {
        HospitalAssetViewTimeTablePanel detailsPanel = new HospitalAssetViewTimeTablePanel(
            usrCtrl,
            asset,
            () -> {
                // Back button: drop this card and return to the completed-consultations list
                cardLayout.show(cardContainer, "wards");
                resetScrollToTop();
            }
        );

        String cardName = "assetAllocation-" + asset.getAssetId();
        cardContainer.add(detailsPanel, cardName);
        cardLayout.show(cardContainer, cardName);
        resetScrollToTop();
    }
    
    private void resetScrollToTop() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JScrollPane sp = (javax.swing.JScrollPane)
                javax.swing.SwingUtilities.getAncestorOfClass(javax.swing.JScrollPane.class, cardContainer);
            if (sp != null) {
                javax.swing.SwingUtilities.invokeLater(() ->
                    sp.getViewport().setViewPosition(new java.awt.Point(0, 0)));
            }
        });
    }
    
    private void updateResultLabel(String message, int count) {
        showResultsForLabel.setText(message + " (" + count + " found)");
    }
    
    private String fileNameOf(String path) {
        if (path == null || path.isEmpty()) return "(no image)";
        int slashIndex = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return slashIndex == -1 ? path : path.substring(slashIndex + 1);
    }
    
    private String copyImageToAssetsFolder(File sourceFile) {
        try {
            String resourceFolder = "oodj_hospital_management_system/assets/img/hospital_assets/";
            java.net.URL anchorUrl = getClass().getClassLoader()
                .getResource(resourceFolder + "default_room.png");
            if (anchorUrl == null) return null;
 
            File targetDir = new File(anchorUrl.toURI()).getParentFile();
            if (targetDir == null || !targetDir.exists()) return null;
 
            String originalName = sourceFile.getName();
            String extension = "";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex != -1) extension = originalName.substring(dotIndex);
 
            String newFileName = "asset_" + System.currentTimeMillis() + extension;
            File targetFile = new File(targetDir, newFileName);
 
            java.nio.file.Files.copy(sourceFile.toPath(), targetFile.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
 
            return resourceFolder + newFileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private String selectAssetType() {
        List<String> types = getBaseAssets().stream()
            .map(HospitalAsset::getAssetType)
            .filter(t -> t != null && !t.isBlank())
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .collect(java.util.stream.Collectors.toList());

        if (types.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow,
                "No asset types found.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        javax.swing.JComboBox<String> typeCombo = new javax.swing.JComboBox<>(types.toArray(new String[0]));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Asset Type:"));
        panel.add(typeCombo);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Asset Type", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return null;
        return (String) typeCombo.getSelectedItem();
    }

    private void filterByAssetType(String type) {
        List<HospitalAsset> filtered = new ArrayList<>();
        for (HospitalAsset asset : getBaseAssets()) {   // filter from base, not currentAssets
            if (type.equalsIgnoreCase(asset.getAssetType())) {
                filtered.add(asset);
            }
        }
        currentAssets = filtered;
        populateAssetPanels(filtered);
        updateResultLabel("Showing assets of type \"" + type + "\"", filtered.size());
    }
    
    private String selectStatus() {
        String[] statuses = {
            HospitalAsset.STATUS_AVAILABLE,
            HospitalAsset.STATUS_OCCUPIED,
            HospitalAsset.STATUS_MAINTENANCE,
            HospitalAsset.STATUS_INACTIVE
        };
        javax.swing.JComboBox<String> statusCombo = new javax.swing.JComboBox<>(statuses);

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(parentWindow, panel,
            "Filter by Status", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return null;
        return (String) statusCombo.getSelectedItem();
    }
    
    private void filterByStatus(String status) {
        List<HospitalAsset> filtered = new ArrayList<>();
        for (HospitalAsset asset : getBaseAssets()) {   // filter from base, not currentAssets
            if (status.equals(asset.getStatus())) {
                filtered.add(asset);
            }
        }
        currentAssets = filtered;
        populateAssetPanels(filtered);
        updateResultLabel("Showing assets with status \"" + status + "\"", filtered.size());
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
        jPanel2 = new javax.swing.JPanel();
        showResultsForPanel = new javax.swing.JPanel();
        showResultsForLabel = new javax.swing.JLabel();
        assetPanelWrapper = new javax.swing.JPanel();

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

        sortField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Name", "Location" }));
        sortField.setPreferredSize(new java.awt.Dimension(150, 30));
        sortField.setRequestFocusEnabled(false);
        sortField.addActionListener(this::sortFieldActionPerformed);
        sortByPane.add(sortField);

        sortByLabel1.setText("Filter By");
        sortByPane.add(sortByLabel1);

        filterByField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Asset Type", "Status" }));
        filterByField.setPreferredSize(new java.awt.Dimension(150, 30));
        filterByField.setRequestFocusEnabled(false);
        filterByField.addActionListener(this::filterByFieldActionPerformed);
        sortByPane.add(filterByField);

        jPanel1.add(sortByPane, java.awt.BorderLayout.LINE_END);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        wrapperPane.setBackground(new java.awt.Color(246, 251, 255));
        wrapperPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        wrapperPane.setLayout(new javax.swing.BoxLayout(wrapperPane, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setBackground(new java.awt.Color(246, 251, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 40));
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new java.awt.BorderLayout());

        showResultsForPanel.setBackground(new java.awt.Color(246, 251, 255));
        showResultsForPanel.setAlignmentY(0.0F);
        showResultsForPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        showResultsForPanel.add(showResultsForLabel);

        jPanel2.add(showResultsForPanel, java.awt.BorderLayout.NORTH);

        assetPanelWrapper.setBackground(new java.awt.Color(246, 251, 255));
        assetPanelWrapper.setLayout(new javax.swing.BoxLayout(assetPanelWrapper, javax.swing.BoxLayout.Y_AXIS));
        jPanel2.add(assetPanelWrapper, java.awt.BorderLayout.CENTER);

        wrapperPane.add(jPanel2);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText();
        List<HospitalAsset> searchResults = this.usrCtrl.getHospitalAssetController().searchHospitalAssets(keyword)
            .stream()
            .filter(a -> a instanceof Ward)
            .collect(java.util.stream.Collectors.toList());
        currentAssets = searchResults;
        assetPanelWrapper.removeAll();
        for (HospitalAsset asset : searchResults) {
            HospitalAssetPanel assetPanel = new HospitalAssetPanel(usrCtrl, asset, () -> openAssetAllocation(asset), this::loadAssets);
            assetPanelWrapper.add(assetPanel);
            assetPanelWrapper.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }
        updateResultLabel("Showing results for \"" + keyword + "\"", searchResults.size());
        jPanel2.revalidate();
        wrapperPane.revalidate();
        assetPanelWrapper.revalidate();
        assetPanelWrapper.repaint();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void sortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFieldActionPerformed
        if (currentAssets == null) return;
        String selected = (String) sortField.getSelectedItem();
        List<HospitalAsset> sorted = new ArrayList<>(currentAssets);

        switch (selected) {
            case "Name":
                sorted.sort((a, b) -> a.getAssetName().compareToIgnoreCase(b.getAssetName()));
                break;
            case "Location":
                sorted.sort((a, b) -> a.getLocation().compareToIgnoreCase(b.getLocation()));
                break;
            default: // "Default" -> fall back to asset ID
                sorted.sort((a, b) -> a.getAssetId().compareToIgnoreCase(b.getAssetId()));
        }

        currentAssets = sorted;
        populateAssetPanels(sorted);
    }//GEN-LAST:event_sortFieldActionPerformed

    private void filterByFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterByFieldActionPerformed
        String selected = (String) filterByField.getSelectedItem();

        switch (selected) {
            case "Asset Type": {
                String type = selectAssetType();
                if (type != null) {
                    filterByAssetType(type);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            case "Status": {
                String status = selectStatus();
                if (status != null) {
                    filterByStatus(status);
                } else {
                    filterByField.setSelectedIndex(0);
                }
                break;
            }

            default: { // "Default" -> no filter
                List<HospitalAsset> base = getBaseAssets();
                currentAssets = base;
                populateAssetPanels(base);
                updateResultLabel("Showing " + base.size() + " asset(s)", base.size());
            }
        }
    }//GEN-LAST:event_filterByFieldActionPerformed

    private void resetSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSearchButtonActionPerformed
        loadAssets();
        searchField.setText("");
        showResultsForLabel.setText("");
    }//GEN-LAST:event_resetSearchButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel assetPanelWrapper;
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
