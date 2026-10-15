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
 * จุดเริ่มโปรแกรม: สร้างหน้าต่างหลัก รวมทุกหน้าไว้ใน CardLayout และสลับหน้า/เริ่มเกมใหม่/แสดงหน้าสรุปผล
 * @author lenovo
 */
public class GameFrame {

    public static final int SCREEN_W = 1280;
    public static final int SCREEN_H = 720;

    // ฟอนต์ของทั้งโปรเจกต์ ถ้าภาษาไทยเป็นช่องสี่เหลี่ยมให้เปลี่ยนเป็น "Tahoma"
    public static final String FONT_NAME = "Tahoma";
    private static GradeManager gradeManager = new GradeManager();
    // ตัวสลับหน้า (cardLayout/cards) และหน้าที่ต้องอ้างถึงภายหลัง: หน้าสรุป หน้าเลือกตัวละคร และแมพ (สร้างใหม่ตอนเริ่มเกม)
    private static CardLayout cardLayout;
    private static JPanel cards;
    private static ResultScreen resultScreen;
    private static CharacterSelect characterSelect;
    private static GameMap1 map1;
    private static GameMap2 map2;

    // จุดเริ่มโปรแกรม: สร้างหน้าต่างเดียว วางทุกหน้าเป็น card ใน CardLayout แล้วครอบด้วย ScaledContainer ให้ย่อ/ขยายได้
    public static void main(String[] args) {
        useThaiFont();
        ScaledContainer.install();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Get to A");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            cardLayout = new CardLayout();
            cards = new JPanel(cardLayout);

            //cards.add(new LoadingScreen(), "loading");
            cards.add(new MainMenuScreen(), "mainmenu");
            characterSelect = new CharacterSelect();
            cards.add(characterSelect, "avatar");
            map1 = new GameMap1();
            map2 = new GameMap2();
            cards.add(map1, "map1");
            cards.add(map2, "map2");
            resultScreen = new ResultScreen();
            cards.add(resultScreen, "result");

            // หน้าจอทั้งหมดออกแบบที่ SCREEN_W x SCREEN_H: ครอบด้วย ScaledContainer เพื่อย่อ/ขยายตามหน้าต่างโดยคงสัดส่วน
            cards.setPreferredSize(new java.awt.Dimension(SCREEN_W, SCREEN_H));
            frame.add(new ScaledContainer(cards, SCREEN_W, SCREEN_H));
            frame.setMinimumSize(new java.awt.Dimension(SCREEN_W / 2, SCREEN_H / 2));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    

    // ตั้งฟอนต์เริ่มต้นของ Swing (ปุ่ม, JOptionPane ฯลฯ) เป็น FONT_NAME
    private static void useThaiFont() {
        java.util.Enumeration<Object> keys = javax.swing.UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (javax.swing.UIManager.get(key) instanceof java.awt.Font font) {
                javax.swing.UIManager.put(key, new javax.swing.plaf.FontUIResource(FONT_NAME, font.getStyle(), font.getSize()));
            }
        }
    }
    // คืน GradeManager (หมายเหตุ: ตัวที่ใช้เก็บคะแนนจริงคือ GradeManager.getInstance())
    public static GradeManager getGradeManager() {
        return gradeManager;
    }

    // เริ่มเกมใหม่: ล้างคะแนนทั้งหมด และสร้างแมพ 1/2 ใหม่ (กล่อง ประตู และตำแหน่งผู้เล่นกลับเป็นค่าเริ่มต้น)
    public static void startNewGame() {
        // บันทึกตัวละครที่เลือกบนหน้าเลือกตัวละคร เพื่อให้แมพใช้ตัวละครตรงกัน
        characterSelect.saveCurrentSelection();
        GradeManager.getInstance().reset();
        //com.mycompany.puzzle.PuzzleScore.reset();
        //com.mycompany.puzzle.saifai.WiringTask.reset();
        resultScreen.setNext("mainmenu", "หน้าหลัก");

        cards.remove(map1);
        cards.remove(map2);
        map1 = new GameMap1();
        map2 = new GameMap2();
        cards.add(map1, "map1");
        cards.add(map2, "map2");
        switchTo("map1");
    }

    // สลับไปแสดงหน้าตามชื่อ card (mainmenu, avatar, map1, map2, result)
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

    // แสดงหน้าสรุป พร้อมตั้งปุ่มเขียวและปุ่มแดงให้ไปหน้า card ที่กำหนด
    public static void showResult(String nextCard, String nextText, String backCard, String backText) {
        resultScreen.setNext(nextCard, nextText, backCard, backText);
        resultScreen.refresh();
        switchTo("result");
    }
}
