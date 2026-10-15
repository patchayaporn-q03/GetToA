/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.screen;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.Timer;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * หน้าโหลดที่วาดเอง (ชื่อเกม + จุด loading + ตัวละครเด้ง) ตอนนี้ปิดไว้ใน GameFrame
 * @author lenovo
 */
public class LoadingScreen extends JPanel {
    private static final int LOADING_TIME_MS = 3000;
    private static final String NEXT_SCREEN = "mainmenu";
    private static final String CHARACTER_IMAGE = "/images/loading/charecterloading.png";
    private int elapsed = 0;
    private Timer timer;
    private Image character;

    // โหลดรูปตัวละคร และสร้าง Timer 40 ms นับเวลา+วาดใหม่ ครบเวลาแล้วสลับไปหน้าถัดไป
    public LoadingScreen() {
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.WHITE);

        URL url = getClass().getResource(CHARACTER_IMAGE);
        if (url != null) {
            character = new ImageIcon(url).getImage();
        } else {
            System.out.println("หารูปไม่เจอ: " + CHARACTER_IMAGE);
        }

        timer = new Timer(40,e -> {
            elapsed += 40;
            repaint();
            if (elapsed >= LOADING_TIME_MS) {
                timer.stop();
                GameFrame.switchTo(NEXT_SCREEN);
            }
        });
        timer.start();
    }

    // วาดหน้าโหลดทั้งหมด: พื้นหลัง ชื่อเกม ข้อความ loading พร้อมจุด และตัวละครที่เด้งขึ้นลง
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font(GameFrame.FONT_NAME, Font.BOLD, 84));
        g2d.setColor(Color.BLACK);
        drawCentered(g2d, "Get to A", getHeight() / 2 - 90);

        int dots = (elapsed / 400)% 4;
        g2d.setFont(new Font(GameFrame.FONT_NAME, Font.PLAIN, 24));
        g2d.setColor(Color.GRAY);
        drawCentered(g2d, "loading" + ".".repeat(dots), getHeight() / 2 - 40);

        int bob = (int) (Math.sin(elapsed / 150.0) * 6);
        drawCharacter(g2d, getWidth() / 2, getHeight() / 2 + 30 + bob);
    }

    // วาดข้อความให้อยู่กึ่งกลางแนวนอนที่ความสูง y
    private void drawCentered(Graphics g2, String Text, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(Text)) / 2;
        g2.drawString(Text, x, y);
    }

    // วาดตัวละครกึ่งกลางที่ cx (ถ้าไม่มีรูปวาดวงกลมแทน)
    private void drawCharacter(Graphics2D g2, int cx, int topY) {
        int size = 120;
        if (character != null) {
            g2.drawImage(character, cx - size / 2, topY, size, size, this);
        } else {
            g2.setColor(new Color(0xF0C29B));
            g2.fillOval(cx - 22, topY, 44, 44);
            g2.setColor(new Color(0x3E6B4A));
            g2.fillRoundRect(cx - 30, topY + 44, 60, 64, 16, 16);
        }
    }
}
