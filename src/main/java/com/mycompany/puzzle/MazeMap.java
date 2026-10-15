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
            "/com/mycompany/puzzle/assets/map/map.png";

    // ลด Hitbox เพื่อให้เดินผ่านทางแคบได้ง่ายขึ้น
    private static final int HITBOX_INSET = 10;

    // ตรวจชนละเอียด
    private static final int SAMPLE_STEP = 2;

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
                throw new IOException("ไฟล์ภาพไม่ถูกต้อง");
            }

            background = new BufferedImage(
                    WIDTH,
                    HEIGHT,
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g2 = background.createGraphics();

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            g2.drawImage(src, 0, 0, WIDTH, HEIGHT, null);
            g2.dispose();

            System.out.println("โหลดแผนที่สำเร็จ");

        } catch (IOException e) {
            System.err.println(
                    "โหลดแผนที่ไม่สำเร็จ: " + e.getMessage()
            );

            background = null;
        }
    }

    // ตรวจจับกำแพง โดยลดโอกาสตรวจเฟอร์นิเจอร์เป็นกำแพง
    private void buildWallMask() {
        wall = new boolean[HEIGHT][WIDTH];

        if (background == null) {
            return;
        }

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                int rgb = background.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                /*
                 * กำแพงหลักเป็นสีดำอมฟ้า
                 * เฟอร์นิเจอร์ส่วนใหญ่มีสีน้ำตาลหรือสีสว่างกว่า
                 */
                boolean darkWall =
                        r < 60 &&
                        g < 75 &&
                        b < 95 &&
                        b >= r &&
                        b >= g - 5;

                wall[y][x] = darkWall;
            }
        }
    }

    public int getStartX() {
        return START_X;
    }

    public int getStartY() {
        return START_Y;
    }

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

    // ตรวจว่าตัวละครชนกำแพงหรือไม่
    public boolean isWall(
            int x,
            int y,
            int width,
            int height
    ) {
        int insetX = Math.min(HITBOX_INSET, width / 3);
        int insetY = Math.min(HITBOX_INSET, height / 3);

        int left = x + insetX;
        int right = x + width - 1 - insetX;
        int top = y + insetY;
        int bottom = y + height - 1 - insetY;

        // ตรวจขอบบนและขอบล่าง
        for (int px = left; px <= right; px += SAMPLE_STEP) {
            if (isWallPixel(px, top) ||
                isWallPixel(px, bottom)) {
                return true;
            }
        }

        // ตรวจขอบซ้ายและขอบขวา
        for (int py = top; py <= bottom; py += SAMPLE_STEP) {
            if (isWallPixel(left, py) ||
                isWallPixel(right, py)) {
                return true;
            }
        }

        // ตรวจมุมทั้งสี่
        return isWallPixel(left, top)
                || isWallPixel(right, top)
                || isWallPixel(left, bottom)
                || isWallPixel(right, bottom);
    }

    private boolean isWallPixel(int x, int y) {
        if (x < 0 || y < 0 ||
            x >= WIDTH || y >= HEIGHT) {
            return true;
        }

        return wall[y][x];
    }

    public boolean reachedGoal(
            int x,
            int y,
            int width,
            int height
    ) {
        Rectangle player =
                new Rectangle(x, y, width, height);

        return player.intersects(goal);
    }
}
