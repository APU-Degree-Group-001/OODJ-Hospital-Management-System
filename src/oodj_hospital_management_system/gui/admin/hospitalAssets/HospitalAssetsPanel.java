/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.admin.hospitalAssets;

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
import oodj_hospital_management_system.services.HospitalAssetManagement;
import oodj_hospital_management_system.services.UserManagement;
import oodj_hospital_management_system.utils.DateTime;
import oodj_hospital_management_system.utils.WordProcessing;

/**
 *
 * @author sheaw
 */
public class HospitalAssetsPanel extends javax.swing.JPanel {
    private UserController usrCtrl;
    private List<HospitalAsset> currentAssets;
    private java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    private java.awt.CardLayout cardLayout;
    private javax.swing.JPanel cardContainer;

    /**
     * Creates new form PatientManagement
     */
    public HospitalAssetsPanel(UserController usrCtrl, javax.swing.JPanel cardContainer) {
        this.usrCtrl = usrCtrl;
        this.cardLayout = (java.awt.CardLayout) cardContainer.getLayout();
        this.cardContainer = cardContainer;
        initComponents();
        loadAssets();
        if (!isAdminView()) {
            operationsPanel.setVisible(false);
        }
        jPanel2.setPreferredSize(null);
        jPanel2.setMaximumSize(new java.awt.Dimension(900, Integer.MAX_VALUE));
    }
    
    private boolean isAdminView() {
        return usrCtrl.getActiveUser() != null
            && "admin staff".equalsIgnoreCase(usrCtrl.getActiveUser().getType());
    }
    
    public void loadAssets() {
        assetPanelWrapper.removeAll(); // clear old panels before reloading
    
        List<HospitalAsset> allAssets = HospitalAssetManagement.getAllHospitalAssets(); // adjust to your actual service method
        currentAssets = allAssets;

        for (HospitalAsset asset : allAssets) {
            HospitalAssetPanel assetPanel = new HospitalAssetPanel(usrCtrl, asset, () -> openAssetAllocation(asset), this::loadAssets);
            assetPanelWrapper.add(assetPanel);
            assetPanelWrapper.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }

        jPanel2.revalidate();      // let jPanel2 recompute its BorderLayout size
        wrapperPane.revalidate();  // let the outer BoxLayout row recompute too
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
        return allAssets != null ? allAssets : new ArrayList<>();
    }
    
    private void openAssetAllocation(HospitalAsset asset) {
        String cardName = "assetAllocation-" + asset.getAssetId();

        // Remove any stale panel previously registered under this same card name
        for (java.awt.Component c : cardContainer.getComponents()) {
            if (cardName.equals(cardContainer.getLayout() instanceof java.awt.CardLayout
                    ? c.getName() : null)) {
                cardContainer.remove(c);
            }
        }

        HospitalAssetViewTimeTablePanel detailsPanel = new HospitalAssetViewTimeTablePanel(
            usrCtrl,
            asset,
            () -> cardLayout.show(cardContainer, "hospitalAssets")
        );

        cardContainer.add(detailsPanel, cardName);
        cardLayout.show(cardContainer, cardName);
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
        operationsPanel = new oodj_hospital_management_system.gui.utils.RoundedPanel(
            15, new java.awt.Color(210,212,220), 10, 15, 10, 15
        );
        operationsLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        addAssetButton = new javax.swing.JButton();

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

        operationsPanel.setBackground(new java.awt.Color(255, 255, 255));
        operationsPanel.setAlignmentY(0.0F);
        operationsPanel.setMaximumSize(new java.awt.Dimension(300, 250));
        operationsPanel.setPreferredSize(new java.awt.Dimension(300, 250));
        operationsPanel.setLayout(new javax.swing.BoxLayout(operationsPanel, javax.swing.BoxLayout.Y_AXIS));

        operationsLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        operationsLabel1.setForeground(new java.awt.Color(102, 102, 102));
        operationsLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        operationsLabel1.setText("Operations");
        operationsPanel.add(operationsLabel1);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        jPanel3.setPreferredSize(new java.awt.Dimension(566, 140));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        addAssetButton.setText("Add New Assets");
        addAssetButton.setMaximumSize(new java.awt.Dimension(150, 40));
        addAssetButton.setPreferredSize(new java.awt.Dimension(150, 40));
        addAssetButton.addActionListener(this::addAssetButtonActionPerformed);
        jPanel3.add(addAssetButton);

        operationsPanel.add(jPanel3);

        wrapperPane.add(operationsPanel);

        add(wrapperPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String keyword = searchField.getText();
        List<HospitalAsset> searchResults = this.usrCtrl.getHospitalAssetController().searchHospitalAssets(keyword);
        currentAssets = searchResults;
        assetPanelWrapper.removeAll();
        for (HospitalAsset asset : searchResults) {
            HospitalAssetPanel assetPanel = new HospitalAssetPanel(usrCtrl, asset, () -> openAssetAllocation(asset), this::loadAssets);
            assetPanelWrapper.add(assetPanel);
            assetPanelWrapper.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(0, 10)));
        }
        updateResultLabel("Showing results for \"" + keyword + "\"", searchResults.size());
        jPanel2.revalidate();      // let jPanel2 recompute its BorderLayout size
        wrapperPane.revalidate();  // let the outer BoxLayout row recompute too
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

    private void addAssetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAssetButtonActionPerformed
        JTextField typeField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        javax.swing.JTextArea descriptionArea = new javax.swing.JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JTextField totalBedsField = new JTextField(20);
 
        JRadioButton standardOption = new JRadioButton("Standard Asset", true);
        JRadioButton wardOption = new JRadioButton("Ward");
        ButtonGroup assetKindGroup = new ButtonGroup();
        assetKindGroup.add(standardOption);
        assetKindGroup.add(wardOption);
 
        JPanel kindPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        kindPanel.add(standardOption);
        kindPanel.add(wardOption);
 
        // Path is tracked here rather than in an editable field, so the user
        // can only set it via the file picker below — never by typing a path.
        final String[] imagePathHolder = { null };
        JLabel imageFileNameLabel = new JLabel(fileNameOf(null));
        javax.swing.JButton browseButton = new javax.swing.JButton("Browse...");
        JPanel imagePathRow = new JPanel(new java.awt.BorderLayout(5, 0));
        imagePathRow.add(imageFileNameLabel, java.awt.BorderLayout.CENTER);
        imagePathRow.add(browseButton, java.awt.BorderLayout.EAST);
        browseButton.addActionListener(e -> {
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "png", "jpg", "jpeg", "gif"));
            int chooserResult = chooser.showOpenDialog(parentWindow);
            if (chooserResult != javax.swing.JFileChooser.APPROVE_OPTION) return;
 
            String copiedPath = copyImageToAssetsFolder(chooser.getSelectedFile());
            if (copiedPath != null) {
                imagePathHolder[0] = copiedPath;
                imageFileNameLabel.setText(fileNameOf(copiedPath));
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    "Failed to copy the selected image into the assets folder.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
 
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(4, 4, 4, 4);
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        int row = 0;
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Kind:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(kindPanel, gbc);
        row++;
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Asset Type:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(typeField, gbc);
        row++;
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Asset Name:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(nameField, gbc);
        row++;
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(locationField, gbc);
        row++;
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(imagePathRow, gbc);
        row++;
 
        JLabel totalBedsLabel = new JLabel("Total Beds:");
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(totalBedsLabel, gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(totalBedsField, gbc);
        row++;
 
        // Total Beds only applies to Wards — hidden until "Ward" is picked.
        totalBedsLabel.setVisible(false);
        totalBedsField.setVisible(false);
        wardOption.addActionListener(e -> {
            totalBedsLabel.setVisible(true);
            totalBedsField.setVisible(true);
            panel.revalidate();
            panel.repaint();
        });
        standardOption.addActionListener(e -> {
            totalBedsLabel.setVisible(false);
            totalBedsField.setVisible(false);
            panel.revalidate();
            panel.repaint();
        });
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.BOTH; gbc.weightx = 1; gbc.weighty = 1;
        javax.swing.JScrollPane descriptionScroll = new javax.swing.JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new java.awt.Dimension(220, 70));
        panel.add(descriptionScroll, gbc);
 
        while (true) {
            int result = JOptionPane.showConfirmDialog(parentWindow, panel,
                "Add New Asset", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;
 
            boolean ward = wardOption.isSelected();
            String type = WordProcessing.capitalizeEachWord(typeField.getText().trim());            
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String imagePath = imagePathHolder[0];
            String description = descriptionArea.getText().trim();
 
            if (type.isEmpty() || name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Asset Type, Name, and Location are required.", "Missing Information",
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }
 
            int totalBeds = 0;
            if (ward) {
                try {
                    totalBeds = Integer.parseInt(totalBedsField.getText().trim());
                    if (totalBeds <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(parentWindow,
                        "Total Beds must be a positive whole number.", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            }
 
            LinkedHashMap<String, String> msg = ward
                ? usrCtrl.getHospitalAssetController().createWard(type, name, imagePath, location, description, totalBeds)
                : usrCtrl.getHospitalAssetController().createHospitalAsset(type, name, imagePath, location, description);
 
            if (msg.get("success") != null) {
                JOptionPane.showMessageDialog(parentWindow,
                    "Asset added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssets();
            } else {
                JOptionPane.showMessageDialog(parentWindow,
                    msg.get("msg") != null ? msg.get("msg") : "Failed to add asset.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
    }//GEN-LAST:event_addAssetButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAssetButton;
    private javax.swing.JPanel assetPanelWrapper;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> filterByField;
    private javax.swing.JOptionPane jOptionPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel operationsLabel1;
    private javax.swing.JPanel operationsPanel;
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
