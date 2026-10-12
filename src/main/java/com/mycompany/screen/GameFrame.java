/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.screen;

import com.mycompany.grade.GradeManager;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;

/**
 *
 * @author lenovo
 */
public class GameFrame {

    public static final int SCREEN_W = 1280;
    public static final int SCREEN_H = 720;
    private static GradeManager gradeManager = new GradeManager();
    private static CardLayout cardLayout;
    private static JPanel cards;
    private static ResultScreen resultScreen;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Get to A");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            cardLayout = new CardLayout();
            cards = new JPanel(cardLayout);

            //cards.add(new LoadingScreen(), "loading");
            cards.add(new MainMenuScreen(), "mainmenu");
            cards.add(new CharacterSelect(), "avatar");
            cards.add(new GameMap1(), "map1");
            cards.add(new GameMap2(), "map2");
            resultScreen = new ResultScreen();
            cards.add(resultScreen, "result");

            frame.add(cards);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    public static GradeManager getGradeManager() { 
        return gradeManager; 
    }

    public static void switchTo(String name) {
        cardLayout.show(cards, name);
    }

    // อัปเดตคะแนนและเกรดบนหน้าผลลัพธ์ แล้วสลับไปแสดง
    public static void showResult() {
        resultScreen.displayResult();
        switchTo("result");
    }
}
