package com.mycompany.screen;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

/**
 *
 * @author lenovo
 */
public class MainMenuPanelForm extends javax.swing.JPanel {

    /**
     * Creates new form MainMenuPanelForm
     */
    public MainMenuPanelForm() {
        initComponents();
    }

    /**
     * 
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnavatar = new javax.swing.JButton();
        btnexit = new javax.swing.JButton();
        btnplay = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(1280, 720));
        setPreferredSize(new java.awt.Dimension(1280, 720));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnavatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainmenu/buttonavatar.png"))); // NOI18N
        btnavatar.setBorderPainted(false);
        btnavatar.setContentAreaFilled(false);
        btnavatar.addActionListener(this::btnavatarActionPerformed);
        add(btnavatar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 510, 240, 65));

        btnexit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainmenu/buttonexit.png"))); // NOI18N
        btnexit.setBorderPainted(false);
        btnexit.setContentAreaFilled(false);
        btnexit.addActionListener(this::btnexitActionPerformed);
        add(btnexit, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 600, 240, 65));

        btnplay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainmenu/buttonplay.png"))); // NOI18N
        btnplay.setBorderPainted(false);
        btnplay.setContentAreaFilled(false);
        btnplay.addActionListener(this::btnplayActionPerformed);
        add(btnplay, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 420, 240, 65));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainmenu/mapbass.png"))); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1290, 720));
    }// </editor-fold>//GEN-END:initComponents

    private void btnavatarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnavatarActionPerformed
        // TODO add your handling code here: Avatar
        Main.switchTo("avatar");
    }//GEN-LAST:event_btnavatarActionPerformed

    private void btnexitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnexitActionPerformed
        // TODO add your handling code here:
        int choice = javax.swing.JOptionPane.showConfirmDialog(
        this, "Are you sure you want to exit?", "Exit",
        javax.swing.JOptionPane.YES_NO_OPTION);
        if (choice == javax.swing.JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_btnexitActionPerformed

    private void btnplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnplayActionPerformed
        // TODO add your handling code here:
        Main.switchTo("map1");
    }//GEN-LAST:event_btnplayActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnavatar;
    private javax.swing.JButton btnexit;
    private javax.swing.JButton btnplay;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}