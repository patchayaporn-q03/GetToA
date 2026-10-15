
package com.mycompany.puzzle;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class MazeMap {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    private static final String MAP_PATH =
            "/com/mycompany/puzzle/assets/map.png";

    // ลดขอบ Hitbox เพื่อไม่ให้ติดขอบตกแต่งง่ายเกินไป
    private static final int HITBOX_INSET = 5;

    private BufferedImage background;
    private boolean[][] wall;

    private static final int START_X = 123;
    private static final int START_Y = 32;

    private final Rectangle goal =
            new Rectangle(1085, 640, 80, 40);

    public MazeMap() {
        loadBackground();
        buildWallMask();
    }

    // โหลดภาพแผนที่
    private void loadBackground() {
        try {
            URL resource = getClass().getResource(MAP_PATH);

            if (resource == null) {
                throw new IOException(
                        "ไม่พบไฟล์แผนที่: " + MAP_PATH
                );
            }

            BufferedImage src = ImageIO.read(resource);

            if (src == null) {
                throw new IOException(
                        "ไฟล์ภาพแผนที่ไม่ถูกต้อง"
                );
            }

            background = new BufferedImage(
                    WIDTH,
                    HEIGHT,
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g2 = background.createGraphics();

            // ใช้การย่อภาพแบบเดียวกับภาพที่นำไปแสดง
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            g2.drawImage(src, 0, 0, WIDTH, HEIGHT, null);
            g2.dispose();

            System.out.println(
                    "โหลดแผนที่สำเร็จ: "
                    + src.getWidth() + "x" + src.getHeight()
            );

        } catch (IOException e) {
            System.err.println(e.getMessage());
            background = null;
        }
    }

    // สร้างมาสก์กำแพง
    private void buildWallMask() {
        wall = new boolean[HEIGHT][WIDTH];

        if (background == null) {
            System.err.println(
                    "ไม่สามารถสร้าง Wall Mask ได้"
            );
            return;
        }

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                int rgb = background.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                /*
                 * ตรวจเฉพาะสีแกนกลางของกำแพงสีดำอมฟ้า
                 * ไม่ใช้เกณฑ์สีเข้มที่กว้างเกินไป
                 * เพื่อลดการตรวจจับเงาและเฟอร์นิเจอร์ผิดพลาด
                 */
                boolean darkWall =
                        r < 55 &&
                        g < 68 &&
                        b < 85 &&
                        b - r < 35 &&
                        g - r < 30;

                wall[y][x] = darkWall;
            }
        }

        System.out.println("สร้าง Wall Mask สำเร็จ");
    }

    public int getStartX() {
        return START_X;
    }

    public int getStartY() {
        return START_Y;
    }

    // วาดแผนที่
    public void draw(Graphics g) {
        if (background != null) {
            g.drawImage(
                    background,
                    0, 0,
                    WIDTH, HEIGHT,
                    null
            );
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.WHITE);
            g.drawString("map.png not found", 20, 30);
        }
    }

    /*
     * ตรวจชนตลอดพื้นที่ Hitbox
     * ไม่สุ่มตรวจทีละจุด จึงไม่ข้ามพิกเซลกำแพงบาง ๆ
     */
    public boolean isWall(
            int x,
            int y,
            int width,
            int height
    ) {
        if (background == null ||
                wall == null ||
                width <= 0 ||
                height <= 0) {
            return true;
        }

        int insetX = Math.min(
                HITBOX_INSET,
                (width - 1) / 2
        );

        int insetY = Math.min(
                HITBOX_INSET,
                (height - 1) / 2
        );

        int left = x + insetX;
        int right = x + width - 1 - insetX;
        int top = y + insetY;
        int bottom = y + height - 1 - insetY;

        // ป้องกันตัวละครออกนอกขอบแผนที่
        if (left < 0 ||
                top < 0 ||
                right >= WIDTH ||
                bottom >= HEIGHT) {
            return true;
        }

        // ตรวจทุกพิกเซลใน Hitbox
        for (int py = top; py <= bottom; py++) {
            for (int px = left; px <= right; px++) {
                if (wall[py][px]) {
                    return true;
                }
            }
        }

        return false;
    }

    // ตรวจพิกเซลกำแพงแต่ละตำแหน่ง
    private boolean isWallPixel(int x, int y) {
        if (background == null ||
                wall == null ||
                x < 0 ||
                y < 0 ||
                x >= WIDTH ||
                y >= HEIGHT) {
            return true;
        }

        return wall[y][x];
    }

    // ตรวจถึงประตูทางออก
    public boolean reachedGoal(
            int x,
            int y,
            int width,
            int height
    ) {
        if (width <= 0 || height <= 0) {
            return false;
        }

        return new Rectangle(x, y, width, height)
                .intersects(goal);
    }
}
