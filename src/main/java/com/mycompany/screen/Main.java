/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.screen;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author lenovo
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Get to A");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.add(new MainMenuPanelForm());  // เอา panel ใส่ในหน้าต่าง
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);              // สั่งโชว์ที่ตัว JFrame แทน
        });
    }
}
