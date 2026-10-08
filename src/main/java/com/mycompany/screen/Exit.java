package com.mycompany.screen;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

/**
 *
 * @author lenovo
 */
public class Exit extends javax.swing.JPanel {

    /**
     * Creates new form exit
     */
    public Exit() {
        initComponents();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        charecter = new javax.swing.JLabel();
        textexit = new javax.swing.JLabel();
        no = new javax.swing.JButton();
        yes = new javax.swing.JButton();
        framexit = new javax.swing.JLabel();
        mapbass = new javax.swing.JLabel();

        setName(""); // NOI18N
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        charecter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/plate.png"))); // NOI18N
        add(charecter, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 300, 130, 130));

        textexit.setText("Are you sure you want to exit?");
        add(textexit, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 340, 160, 50));

        no.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/buttonred.png"))); // NOI18N
        no.setBorderPainted(false);
        no.setContentAreaFilled(false);
        add(no, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 430, 160, -1));

        yes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/buttongreen.png"))); // NOI18N
        yes.setBorderPainted(false);
        yes.setContentAreaFilled(false);
        yes.addActionListener(this::yesActionPerformed);
        add(yes, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 430, 160, -1));

        framexit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit/framexit.png"))); // NOI18N
        framexit.setText("framexit");
        add(framexit, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 190, 580, 340));

        mapbass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit/mapexit.png"))); // NOI18N
        add(mapbass, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 720));
    }// </editor-fold>//GEN-END:initComponents

    private void yesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_yesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel charecter;
    private javax.swing.JLabel framexit;
    private javax.swing.JLabel mapbass;
    private javax.swing.JButton no;
    private javax.swing.JLabel textexit;
    private javax.swing.JButton yes;
    // End of variables declaration//GEN-END:variables
}
