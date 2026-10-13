package com.mycompany.screen;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

/**
 *
 * @author lenovo
 */
public class MainMenuScreen extends javax.swing.JPanel {

    public MainMenuScreen() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        openAvatar = new javax.swing.JButton();
        exitGame = new javax.swing.JButton();
        playGame = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(1280, 720));
        setPreferredSize(new java.awt.Dimension(1280, 720));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        openAvatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainmenu/buttonavatar.png"))); // NOI18N
        openAvatar.setBorderPainted(false);
        openAvatar.setContentAreaFilled(false);
        openAvatar.addActionListener(this::openAvatar);
        add(openAvatar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 510, 240, 65));

        exitGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainmenu/buttonexit.png"))); // NOI18N
        exitGame.setBorderPainted(false);
        exitGame.setContentAreaFilled(false);
        exitGame.addActionListener(this::exitGame);
        add(exitGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 600, 240, 65));

        playGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainmenu/buttonplay.png"))); // NOI18N
        playGame.setBorderPainted(false);
        playGame.setContentAreaFilled(false);
        playGame.addActionListener(this::playGame);
        add(playGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 420, 240, 65));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mainmenu/mapbass.png"))); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1290, 720));
    }// </editor-fold>//GEN-END:initComponents

    private void openAvatar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAvatar
        // TODO add your handling code here: Avatar
        GameFrame.switchTo("avatar");
    }//GEN-LAST:event_openAvatar

    private void exitGame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitGame
        // TODO add your handling code here:
        int choice = javax.swing.JOptionPane.showConfirmDialog(
        this, "Are you sure you want to exit?", "Exit",
        javax.swing.JOptionPane.YES_NO_OPTION);
        if (choice == javax.swing.JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_exitGame

    private void playGame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playGame
        // TODO add your handling code here:
        GameFrame.startNewGame();
    }//GEN-LAST:event_playGame


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exitGame;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton openAvatar;
    private javax.swing.JButton playGame;
    // End of variables declaration//GEN-END:variables

}