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
    private static GameMap1 map1;
    private static GameMap2 map2;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Get to A");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            cardLayout = new CardLayout();
            cards = new JPanel(cardLayout);

            cards.add(new LoadingScreen(), "loading");
            cards.add(new MainMenuScreen(), "mainmenu");
            cards.add(new CharacterSelect(), "avatar");
            map1 = new GameMap1();
            map2 = new GameMap2();
            cards.add(map1, "map1");
            cards.add(map2, "map2");
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

    // เริ่มเกมใหม่: ล้างคะแนนทั้งหมด และสร้างแมพ 1/2 ใหม่ (กล่อง ประตู และตำแหน่งผู้เล่นกลับเป็นค่าเริ่มต้น)
    public static void startNewGame() {
        GradeManager.getInstance().reset();
        com.mycompany.puzzle.PuzzleScore.reset();
        com.mycompany.puzzle.saifai.WiringTask.reset();
        resultScreen.setNext("mainmenu", "หน้าหลัก");

        cards.remove(map1);
        cards.remove(map2);
        map1 = new GameMap1();
        map2 = new GameMap2();
        cards.add(map1, "map1");
        cards.add(map2, "map2");
        switchTo("map1");
    }

    public static void switchTo(String name) {
        cardLayout.show(cards, name);
    }

    // อัปเดตคะแนนและเกรดบนหน้าผลลัพธ์ แล้วสลับไปแสดง
    public static void showResult() {
        resultScreen.refresh();
        switchTo("result");
    }

    // แสดงหน้าสรุป พร้อมตั้งปุ่มเขียวให้ไปหน้า card ที่กำหนด
    public static void showResult(String nextCard, String nextText) {
        resultScreen.setNext(nextCard, nextText);
        resultScreen.refresh();
        switchTo("result");
    }
}
