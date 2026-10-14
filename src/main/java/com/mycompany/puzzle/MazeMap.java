package com.mycompany.puzzle;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MazeMap {

    // ขนาดเฟรมของ Map
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    private static final String MAP_PATH = "/com/mycompany/puzzle/assets/map/map.png";

    // ระยะที่ย่อ hitbox เข้าจากขอบตัวละคร (ให้เดินผ่านทางแคบได้)
    private static final int HITBOX_INSET = 6;

    // ระยะห่างของจุดตรวจชนบนขอบ hitbox
    private static final int SAMPLE_STEP = 4;

    // รัศมีการตัดเส้นบางๆ ออกจากมาสก์กำแพง
    private static final int WALL_OPEN_RADIUS = 4;

    private BufferedImage background;

    // true = กำแพง/เดินไม่ได้ (สร้างจากสีของภาพ map)
    private boolean[][] wall;

    // จุดเริ่มต้น (มุมบนซ้ายของตัวละคร) - ประตูด้านบนซ้าย
    private static final int START_X = 123;
    private static final int START_Y = 32;

    // จุดหมาย - ประตูด้านล่างขวา
    private final Rectangle goal = new Rectangle(1085, 640, 80, 40);

    public MazeMap() {
        loadBackground();
        buildWallMask();
    }

    private void loadBackground() {
        try {
            BufferedImage src = ImageIO.read(getClass().getResource(MAP_PATH));

            // ย่อ/ขยายภาพให้พอดีเฟรม 1280x720
            background = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = background.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(src, 0, 0, WIDTH, HEIGHT, null);
            g2.dispose();

            System.out.println("โหลด map สำเร็จ");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("ไม่พบ map-ver2.png");
            background = null;
        }
    }

    // กำแพงในภาพเป็นสีน้ำเงินเข้ม/ดำ ส่วนพื้นเป็นสีครีม
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
                int max = Math.max(r, Math.max(g, b));

                wall[y][x] = max < 125 && b >= g - 2 && b >= r - 4 && b - r < 35;
            }
        }


        // เปิดมาสก์ (erode แล้ว dilate) เพื่อตัดเส้นขอบบางๆ ของเฟอร์นิเจอร์ออก
        // เหลือเฉพาะกำแพงหนาจริงๆ ของเขาวงกต
        wall = morph(morph(wall, false, WALL_OPEN_RADIUS), true, WALL_OPEN_RADIUS);
    }

    // dilate = true: ขยายกำแพง, false: หดกำแพง (แยกแกน x/y เพื่อความเร็ว)
    private static boolean[][] morph(boolean[][] src, boolean dilate, int radius) {
        boolean[][] tmp = new boolean[HEIGHT][WIDTH];
        boolean[][] out = new boolean[HEIGHT][WIDTH];

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                tmp[y][x] = window(src, x, y, 1, 0, dilate, radius);
            }
        }

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                out[y][x] = window(tmp, x, y, 0, 1, dilate, radius);
            }
        }

        return out;
    }

    private static boolean window(boolean[][] m, int x, int y, int dx, int dy,
            boolean dilate, int radius) {
        for (int i = -radius; i <= radius; i++) {
            int px = x + i * dx;
            int py = y + i * dy;
            // นอกภาพถือเป็นกำแพง
            boolean v = px < 0 || py < 0 || px >= WIDTH || py >= HEIGHT || m[py][px];
            if (v == dilate) {
                return dilate;
            }
        }
        return !dilate;
    }

    public int getStartX() {
        return START_X;
    }

    public int getStartY() {
        return START_Y;
    }

    public void draw(Graphics g) {
        if (background != null) {
            g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.WHITE);
            g.drawString("map-ver2.png not found", 20, 30);
        }
    }

    // ตรวจว่าตำแหน่งของ Player ชนกำแพงหรือไม่
    public boolean isWall(int x, int y, int width, int height) {
        int left = x + HITBOX_INSET;
        int right = x + width - HITBOX_INSET;
        int top = y + HITBOX_INSET;
        int bottom = y + height - HITBOX_INSET;

        for (int px = left; px <= right; px += SAMPLE_STEP) {
            if (isWallPixel(px, top) || isWallPixel(px, bottom)) {
                return true;
            }
        }

        for (int py = top; py <= bottom; py += SAMPLE_STEP) {
            if (isWallPixel(left, py) || isWallPixel(right, py)) {
                return true;
            }
        }

        return isWallPixel(right, bottom) || isWallPixel(right, top);
    }

    private boolean isWallPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) {
            return true;
        }

        return wall[y][x];
    }

    // ตรวจว่า Player ถึง Goal หรือยัง
    public boolean reachedGoal(int x, int y, int width, int height) {
        return new Rectangle(x, y, width, height).intersects(goal);
    }
}
