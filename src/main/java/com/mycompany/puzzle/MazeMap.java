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

    // ลดขนาด hitbox เพื่อไม่ให้ชนขอบตกแต่งง่ายเกินไป
    private static final int HITBOX_INSET = 10;

    private static final int SAMPLE_STEP = 2;

    // กรองเส้นสีเข้มขนาดเล็กออกจากมาสก์
    private static final int FILTER_RADIUS = 2;

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

    // โหลดแผนที่
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
            System.err.println(e.getMessage());
            background = null;
        }
    }

    // สร้างมาสก์กำแพง
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
                 * ตรวจเฉพาะสีดำอมฟ้าที่เข้ม
                 * ไม่ใช้เงื่อนไขกว้างเกินไป
                 */
                boolean darkWall =
                        r < 65 &&
                        g < 78 &&
                        b < 95 &&
                        b >= r - 8 &&
                        b >= g - 15 &&
                        b - r < 45;

                wall[y][x] = darkWall;
            }
        }

        /*
         * กรองเส้นสีเข้มบาง ๆ ที่อาจทำให้ติดอากาศ
         * หดกำแพงก่อน แล้วขยายกลับ
         */
        wall = morph(wall, false, FILTER_RADIUS);
        wall = morph(wall, true, FILTER_RADIUS);

        System.out.println("สร้างมาสก์กำแพงสำเร็จ");
    }

    // Morphological Opening: หดหรือขยายกำแพง
    private boolean[][] morph(
            boolean[][] src,
            boolean dilate,
            int radius
    ) {
        boolean[][] temp = new boolean[HEIGHT][WIDTH];
        boolean[][] out = new boolean[HEIGHT][WIDTH];

        // ประมวลผลแนวนอน
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                boolean result = dilate ? false : true;

                for (int k = -radius; k <= radius; k++) {
                    int px = x + k;

                    boolean value =
                            px < 0 || px >= WIDTH || src[y][px];

                    if (dilate && value) {
                        result = true;
                        break;
                    }

                    if (!dilate && !value) {
                        result = false;
                        break;
                    }
                }

                temp[y][x] = result;
            }
        }

        // ประมวลผลแนวตั้ง
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                boolean result = dilate ? false : true;

                for (int k = -radius; k <= radius; k++) {
                    int py = y + k;

                    boolean value =
                            py < 0 || py >= HEIGHT || temp[py][x];

                    if (dilate && value) {
                        result = true;
                        break;
                    }

                    if (!dilate && !value) {
                        result = false;
                        break;
                    }
                }

                out[y][x] = result;
            }
        }

        return out;
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

    // ตรวจชนกำแพง
    public boolean isWall(
            int x,
            int y,
            int width,
            int height
    ) {
        if (wall == null || width <= 0 || height <= 0) {
            return true;
        }

        int insetX = Math.min(HITBOX_INSET, (width - 1) / 2);
        int insetY = Math.min(HITBOX_INSET, (height - 1) / 2);

        int left = x + insetX;
        int right = x + width - 1 - insetX;
        int top = y + insetY;
        int bottom = y + height - 1 - insetY;

        // ตรวจด้านบนและด้านล่าง
        for (int px = left; px <= right; px += SAMPLE_STEP) {
            if (isWallPixel(px, top) ||
                isWallPixel(px, bottom)) {
                return true;
            }
        }

        // ตรวจด้านซ้ายและด้านขวา
        for (int py = top; py <= bottom; py += SAMPLE_STEP) {
            if (isWallPixel(left, py) ||
                isWallPixel(right, py)) {
                return true;
            }
        }

        // ตรวจปลายขอบให้ครบ
        return isWallPixel(right, bottom)
                || isWallPixel(right, top)
                || isWallPixel(left, bottom)
                || isWallPixel(left, top);
    }

    private boolean isWallPixel(int x, int y) {
        if (x < 0 || y < 0 ||
            x >= WIDTH || y >= HEIGHT) {
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
        return new Rectangle(x, y, width, height)
                .intersects(goal);
    }
}
