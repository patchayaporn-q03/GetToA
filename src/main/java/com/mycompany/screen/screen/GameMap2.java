/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.screen;

/**
 *
 * @author lenovo
 */
public class GameMap2 extends javax.swing.JPanel {

    /**
     * Creates new form GameMap2
     */
    public GameMap2() {
        initComponents();
    }

    /**
     *
     *
     * 
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        table4 = new javax.swing.JLabel();
        table1 = new javax.swing.JLabel();
        table3 = new javax.swing.JLabel();
        table2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1280, 720));
        setMinimumSize(new java.awt.Dimension(1280, 720));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        table4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/tablemap2.png")));
        add(table4, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 440, 180, 70));

        table1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/tablemap2.png"))); 
        add(table1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 310, 180, 70));

        table3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/tablemap2.png"))); 
        add(table3, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 440, 180, 70));

        table2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/tablemap2.png"))); 
        add(table2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 310, 180, 70));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/map2.png")));
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 720));
    }


    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel table1;
    private javax.swing.JLabel table2;
    private javax.swing.JLabel table3;
    private javax.swing.JLabel table4;
}
