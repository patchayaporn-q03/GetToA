/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.screen;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;

/**
 *
 * @author lenovo
 */
public class Main {

    private static CardLayout cardLayout;
    private static JPanel cards;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Get to A");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            cardLayout = new CardLayout();
            cards = new JPanel(cardLayout);

            cards.add(new LoadingScreen(), "loading");
            cards.add(new MainMenuPanelForm(), "mainmenu");
            cards.add(new Avatar(), "avatar");
            cards.add(new GameMap1(), "map1");

            frame.add(cards);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static void switchTo(String name) {
        cardLayout.show(cards, name);
    }
    //cards.add(new LoadingScreen(), "loading");
}
