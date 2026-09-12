/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package oodj_hospital_management_system.gui.admin.hospitalAssets;

import java.awt.Image;
import java.io.File;
import java.util.LinkedHashMap;
import javax.swing.ImageIcon;
import oodj_hospital_management_system.controllers.UserController;
import oodj_hospital_management_system.gui.utils.RoundedPanel;
import oodj_hospital_management_system.models.HospitalAsset;
import oodj_hospital_management_system.models.Ward;

/**
 *
 * @author sheaw
 */
public class HospitalAssetPanel extends RoundedPanel {
    private UserController usrCtrl;
    private HospitalAsset asset;
    private final Runnable onViewAllocation;
    private final Runnable onChanged;
    private static final int IMG_WIDTH = 215;
    private static final int IMG_HEIGHT = 215;

    /**
     * Creates new form HospitalAssetPanel
     */
    public HospitalAssetPanel(UserController usrCtrl, HospitalAsset asset, Runnable onViewAllocation, Runnable onChanged) {
//        [246,251,255]
//        [229,243,255]
        super(15, new java.awt.Color(229,243,255), 10, 15, 10, 15);
        this.usrCtrl = usrCtrl;
        initComponents();
        this.asset = asset;
        this.onViewAllocation = onViewAllocation;
        this.onChanged = onChanged;
        loadAssetData();
        if (!isAdminView()) {
            modifyButton.setVisible(false);
            deleteButton.setVisible(false);
            maintenanceButton.setVisible(false);
        }
        imageHolder.setMaximumSize(new java.awt.Dimension(215, 215));
        imageHolder.setPreferredSize(new java.awt.Dimension(215, 215));
    }
    
    private boolean isAdminView() {
        return usrCtrl.getActiveUser() != null
            && "admin staff".equalsIgnoreCase(usrCtrl.getActiveUser().getType());
    }
    
    private void loadAssetData() {
        // Text fields
        typeLabel.setText(asset.getAssetType() != null ? asset.getAssetType().toUpperCase() : "");
        nameLabel.setText(asset.getAssetName());
        locationLabel.setText("Location: " + asset.getLocation());
        statusLabel.setText("Status: " + asset.getStatus());
        descriptionField.setText(asset.getDescription());

        switch (asset.getStatus()) {
        case HospitalAsset.STATUS_AVAILABLE:
            statusLabel.setText("<html>Status: <b><font color='#009600'>" 
                    + asset.getStatus() + "</font></b></html>");
            break;
        case HospitalAsset.STATUS_OCCUPIED:
            statusLabel.setText("<html>Status: <b><font color='#C80000'>" 
                    + asset.getStatus() + "</font></b></html>");
            break;
        case HospitalAsset.STATUS_MAINTENANCE:
            statusLabel.setText("<html>Status: <b><font color='#C88C00'>" 
                    + asset.getStatus() + "</font></b></html>");
            break;
        case HospitalAsset.STATUS_INACTIVE:
            statusLabel.setText("<html>Status: <b><font color='gray'>" 
                    + asset.getStatus() + "</font></b></html>");
            break;
        default:
            statusLabel.setText("Status: " + asset.getStatus());
    }

        // Image loading
        loadImage(asset.getImagePath());
    }
    
    private void loadImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "oodj_hospital_management_system/assets/img/hospital_assets/default_room.png";
        }

        java.net.URL imgUrl = getClass().getClassLoader().getResource(imagePath);
        if (imgUrl == null) {
            imageHolder.setText("Image Not Found");
            imageHolder.setIcon(null);
            return;
        }

        ImageIcon rawIcon = new ImageIcon(imgUrl);
        Image scaledImage = rawIcon.getImage().getScaledInstance(
                IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH
        );
        imageHolder.setIcon(new ImageIcon(scaledImage));
        imageHolder.setText("");
    }
    
    private boolean isWard() {
        return asset instanceof Ward;
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
 
            String newFileName = asset.getAssetId() + "_" + System.currentTimeMillis() + extension;
            File targetFile = new File(targetDir, newFileName);
 
            java.nio.file.Files.copy(sourceFile.toPath(), targetFile.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
 
            return resourceFolder + newFileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imageHolder = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        locationLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        descriptionField = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        modifyButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        viewButton = new javax.swing.JButton();
        maintenanceButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        imageHolder.setText("jLabel1");
        imageHolder.setMaximumSize(new java.awt.Dimension(40, 40));
        imageHolder.setPreferredSize(new java.awt.Dimension(40, 40));

        typeLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        typeLabel.setText("CONSULTATION ROOM");

        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        nameLabel.setText("Room A");

        locationLabel.setText("Location: Block C Level 2");

        statusLabel.setText("Status: AVAILABLE");

        descriptionField.setEditable(false);
        descriptionField.setBackground(new java.awt.Color(255, 255, 255));
        descriptionField.setColumns(20);
        descriptionField.setLineWrap(true);
        descriptionField.setRows(5);
        descriptionField.setText("Standard consultation room with basic equipment hahahhaha");
        descriptionField.setWrapStyleWord(true);
        descriptionField.setEnabled(false);

        jLabel6.setText("Description:");

        modifyButton.setText("Modify");
        modifyButton.addActionListener(this::modifyButtonActionPerformed);

        deleteButton.setText("Delete");
        deleteButton.addActionListener(this::deleteButtonActionPerformed);

        viewButton.setText("View");
        viewButton.addActionListener(this::viewButtonActionPerformed);

        maintenanceButton.setText("Maintenance");
        maintenanceButton.addActionListener(this::maintenanceButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(imageHolder, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(15, 15, 15)
                        .addComponent(descriptionField))
                    .addComponent(typeLabel)
                    .addComponent(nameLabel)
                    .addComponent(locationLabel)
                    .addComponent(statusLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(viewButton)
                        .addGap(18, 18, 18)
                        .addComponent(modifyButton)
                        .addGap(18, 18, 18)
                        .addComponent(deleteButton)
                        .addGap(18, 18, 18)
                        .addComponent(maintenanceButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameLabel)
                        .addGap(8, 8, 8)
                        .addComponent(locationLabel)
                        .addGap(8, 8, 8)
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(descriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(modifyButton)
                            .addComponent(deleteButton)
                            .addComponent(viewButton)
                            .addComponent(maintenanceButton)))
                    .addComponent(imageHolder, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewButtonActionPerformed
        if (onViewAllocation != null) onViewAllocation.run();
    }//GEN-LAST:event_viewButtonActionPerformed

    private void modifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyButtonActionPerformed
        boolean ward = isWard();
 
        javax.swing.JTextField typeField = new javax.swing.JTextField(asset.getAssetType(), 20);
        javax.swing.JTextField nameField = new javax.swing.JTextField(asset.getAssetName(), 20);
        javax.swing.JTextField locationField = new javax.swing.JTextField(asset.getLocation(), 20);
        // Path is tracked here rather than in an editable field, so the user
        // can only change it via the file picker below — never by typing
        // an arbitrary path directly.
        final String[] imagePathHolder = { asset.getImagePath() };
        javax.swing.JTextArea descriptionArea = new javax.swing.JTextArea(asset.getDescription(), 4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
 
        javax.swing.JTextField totalBedsField = ward
            ? new javax.swing.JTextField(String.valueOf(((Ward) asset).getTotalBeds()), 20)
            : null;
 
        // GridBagLayout instead of GridLayout(0,1): a GridLayout gives every
        // row the same height, so the tall description text area was forcing
        // every label/field row to be just as tall. GridBagLayout lets each
        // row size to its own content, and only the description row grows.
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(4, 4, 4, 4);
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        int row = 0;
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new javax.swing.JLabel("Asset Type:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(typeField, gbc);
        row++;
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new javax.swing.JLabel("Asset Name:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(nameField, gbc);
        row++;
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new javax.swing.JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(locationField, gbc);
        row++;
 
        javax.swing.JLabel imageFileNameLabel = new javax.swing.JLabel(fileNameOf(imagePathHolder[0]));
        javax.swing.JButton browseButton = new javax.swing.JButton("Browse...");
        javax.swing.JPanel imagePathRow = new javax.swing.JPanel(new java.awt.BorderLayout(5, 0));
        imagePathRow.add(imageFileNameLabel, java.awt.BorderLayout.CENTER);
        imagePathRow.add(browseButton, java.awt.BorderLayout.EAST);
        browseButton.addActionListener(e -> {
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "png", "jpg", "jpeg", "gif"));
            int chooserResult = chooser.showOpenDialog(this);
            if (chooserResult != javax.swing.JFileChooser.APPROVE_OPTION) return;
 
            String copiedPath = copyImageToAssetsFolder(chooser.getSelectedFile());
            if (copiedPath != null) {
                imagePathHolder[0] = copiedPath;
                imageFileNameLabel.setText(fileNameOf(copiedPath));
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Failed to copy the selected image into the assets folder.",
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new javax.swing.JLabel("Image:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(imagePathRow, gbc);
        row++;
 
        if (ward) {
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
            panel.add(new javax.swing.JLabel("Total Beds:"), gbc);
            gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            panel.add(totalBedsField, gbc);
            row++;
        }
 
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = java.awt.GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panel.add(new javax.swing.JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = java.awt.GridBagConstraints.BOTH; gbc.weightx = 1; gbc.weighty = 1;
        javax.swing.JScrollPane descriptionScroll = new javax.swing.JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new java.awt.Dimension(220, 70));
        panel.add(descriptionScroll, gbc);
 
        while (true) {
            int result = javax.swing.JOptionPane.showConfirmDialog(this, panel,
                "Modify " + (ward ? "Ward" : "Hospital Asset"), javax.swing.JOptionPane.OK_CANCEL_OPTION);
            if (result != javax.swing.JOptionPane.OK_OPTION) return;
 
            String type = typeField.getText().trim();
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String imagePath = imagePathHolder[0];
            String description = descriptionArea.getText().trim();
 
            if (type.isEmpty() || name.isEmpty() || location.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Asset Type, Name, and Location are required.", "Missing Information",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                continue;
            }
 
            int totalBeds = 0;
            if (ward) {
                try {
                    totalBeds = Integer.parseInt(totalBedsField.getText().trim());
                    if (totalBeds <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Total Beds must be a positive whole number.", "Invalid Input",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            }
 
            LinkedHashMap<String, String> msg = ward
                ? usrCtrl.getHospitalAssetController().modifyWard(
                    (Ward) asset, type, name, imagePath, location, description, totalBeds)
                : usrCtrl.getHospitalAssetController().modifyHospitalAsset(
                    asset, type, name, imagePath, location, description);
 
            if (msg.get("success") != null) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Asset updated.", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                loadAssetData();
                if (onChanged != null) onChanged.run();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    msg.get("msg") != null ? msg.get("msg") : "Failed to update asset.",
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

    }//GEN-LAST:event_modifyButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            "Delete this asset? This cannot be undone.",
            "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (confirm != javax.swing.JOptionPane.YES_OPTION) return;
 
        LinkedHashMap<String, String> msg = usrCtrl.getHospitalAssetController().deleteHospitalAsset(asset.getAssetId());
        if (msg.get("success") != null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Asset deleted.", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            if (onChanged != null) onChanged.run();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                msg.get("msg") != null ? msg.get("msg") : "Failed to delete asset.",
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void maintenanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maintenanceButtonActionPerformed
        boolean currentlyUnderMaintenance = HospitalAsset.STATUS_MAINTENANCE.equals(asset.getStatus());
        boolean settingMaintenance = !currentlyUnderMaintenance;

        if (settingMaintenance && HospitalAsset.STATUS_OCCUPIED.equals(asset.getStatus())) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "This asset is currently occupied and cannot be set to maintenance.",
                "Cannot Set Maintenance", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String confirmMsg = settingMaintenance
            ? "Set this asset to Maintenance? It will be unavailable until taken off maintenance."
            : "Take this asset off Maintenance? It will return to normal availability.";

        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            confirmMsg, "Confirm", javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE);
        if (confirm != javax.swing.JOptionPane.YES_OPTION) return;

        LinkedHashMap<String, String> msg = usrCtrl.getHospitalAssetController()
            .setHospitalAssetMaintenance(asset.getAssetId(), settingMaintenance);

        if (msg.get("success") != null) {
            HospitalAsset refreshed = usrCtrl.getHospitalAssetController().getHospitalAssetById(asset.getAssetId());
            if (refreshed != null) {
                asset = refreshed;
            }
            loadAssetData();
            if (onChanged != null) onChanged.run();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                msg.get("msg") != null ? msg.get("msg") : "Failed to update maintenance status.",
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_maintenanceButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextArea descriptionField;
    private javax.swing.JLabel imageHolder;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JButton maintenanceButton;
    private javax.swing.JButton modifyButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JButton viewButton;
    // End of variables declaration//GEN-END:variables
}
