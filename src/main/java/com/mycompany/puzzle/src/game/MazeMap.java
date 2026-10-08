package game;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MazeMap {

    public static final int WIDTH = 960;
    public static final int HEIGHT = 640;

    private BufferedImage background;

    // =========================================================
    // ขนาดเขาวงกต
    // =========================================================

    private static final int ROWS = 15;
    private static final int COLS = 18;

    /*
     * # = กำแพง
     * . = ทางเดิน
     *
     * S = จุดเริ่มต้น
     * G = จุดหมาย
     *
     * จุดเริ่มต้น : แถว 2 คอลัมน์ 2
     * จุดหมาย     : แถว 12 คอลัมน์ 15
     */

    private static final String[] MAZE = {

    "##################",
    "####.#############",
    "##...#..........##",
    "##.#.#######.#####",
    "####.......#.#.###",
    "#..######..#.#####",
    "##......##.#....##",
    "#########..####.##",
    "#..#.........#..##",
    "####.#######.#####",
    "##...#.....#.#...#",
    "##.###.#####.#####",
    "##.#...#.......###",
    "####.#########.###",
    "##################"
};


    // =========================================================
    // ตำแหน่งพื้นที่เขาวงกต
    // =========================================================

    private static final double MAZE_X = 135.0;
    private static final double MAZE_Y = 21.0;
    private static final double CELL_SIZE = 37.5;


    // =========================================================
    // จุดเริ่มต้น
    // =========================================================

    private static final int START_ROW = 2;
    private static final int START_COL = 2;

    private int startX;
    private int startY;


    // =========================================================
    // จุดหมาย
    // =========================================================

    private static final int GOAL_ROW = 12;
    private static final int GOAL_COL = 15;

    private Rectangle goal;


    // =========================================================
    // Constructor
    // =========================================================

    public MazeMap() {

        loadBackground();

        // ตำแหน่ง Start
        startX = getCellX(START_COL);
        startY = getCellY(START_ROW);

        // ตำแหน่ง Goal
        goal = new Rectangle(
                getCellX(GOAL_COL),
                getCellY(GOAL_ROW),
                (int) CELL_SIZE,
                (int) CELL_SIZE
        );
    }


    // =========================================================
    // โหลดภาพ Map
    // =========================================================

    private void loadBackground() {

        try {

            background = ImageIO.read(
                    new File("assets/map/map.png")
            );

            System.out.println("โหลด map สำเร็จ");

        } catch (IOException e) {

            System.out.println("ไม่พบ map.png");

            background = null;
        }
    }


    // =========================================================
    // แปลง Column -> X
    // =========================================================

    private int getCellX(int col) {

        return (int) (
                MAZE_X + (col * CELL_SIZE)
        );
    }


    // =========================================================
    // แปลง Row -> Y
    // =========================================================

    private int getCellY(int row) {

        return (int) (
                MAZE_Y + (row * CELL_SIZE)
        );
    }


    // =========================================================
    // Get Start X
    // =========================================================

    public int getStartX() {

        return startX;
    }


    // =========================================================
    // Get Start Y
    // =========================================================

    public int getStartY() {

        return startY;
    }


    // =========================================================
    // วาด Map
    // =========================================================

    public void draw(Graphics g) {

        if (background != null) {

            g.drawImage(
                    background,
                    0,
                    0,
                    WIDTH,
                    HEIGHT,
                    null
            );

        } else {

            g.setColor(Color.BLACK);

            g.fillRect(
                    0,
                    0,
                    WIDTH,
                    HEIGHT
            );

            g.setColor(Color.WHITE);

            g.drawString(
                    "map.png not found",
                    20,
                    30
            );
        }
    }


    // =========================================================
    // ตรวจสอบว่า Cell เป็นกำแพงหรือไม่
    // =========================================================

    private boolean isWallCell(int row, int col) {

        // ป้องกันออกนอก Array
        if (
                row < 0 ||
                row >= ROWS ||
                col < 0 ||
                col >= COLS
        ) {

            return true;
        }

        return MAZE[row].charAt(col) == '#';
    }


    // =========================================================
    // ตรวจสอบตำแหน่ง Player
    // =========================================================

    public boolean isWall(
            int x,
            int y,
            int width,
            int height) {

        // ขอบด้านซ้าย
        int left = x + 2;

        // ขอบด้านขวา
        int right = x + width - 2;

        // ขอบด้านบน
        int top = y + 2;

        // ขอบด้านล่าง
        int bottom = y + height - 2;


        // ตรวจ 4 มุม
        if (isWallPosition(left, top)) {
            return true;
        }

        if (isWallPosition(right, top)) {
            return true;
        }

        if (isWallPosition(left, bottom)) {
            return true;
        }

        if (isWallPosition(right, bottom)) {
            return true;
        }


        // ตรงกลางด้านซ้าย
        if (isWallPosition(
                left,
                y + height / 2)) {

            return true;
        }


        // ตรงกลางด้านขวา
        if (isWallPosition(
                right,
                y + height / 2)) {

            return true;
        }


        // ตรงกลางด้านบน
        if (isWallPosition(
                x + width / 2,
                top)) {

            return true;
        }


        // ตรงกลางด้านล่าง
        if (isWallPosition(
                x + width / 2,
                bottom)) {

            return true;
        }


        return false;
    }


    // =========================================================
    // ตรวจสอบจุดว่าอยู่ในกำแพงหรือไม่
    // =========================================================

    private boolean isWallPosition(
            int x,
            int y) {

        // ถ้าอยู่นอกพื้นที่ Maze
        // ให้ถือว่าเป็นกำแพง

        if (
                x < MAZE_X ||
                y < MAZE_Y ||
                x >= MAZE_X + COLS * CELL_SIZE ||
                y >= MAZE_Y + ROWS * CELL_SIZE
        ) {

            return true;
        }


        // คำนวณ Column
        int col = (int) (
                (x - MAZE_X) / CELL_SIZE
        );


        // คำนวณ Row
        int row = (int) (
                (y - MAZE_Y) / CELL_SIZE
        );


        // ตรวจจากแผนที่
        return isWallCell(row, col);
    }


    // =========================================================
    // ตรวจว่า Player ถึง Goal หรือยัง
    // =========================================================

    public boolean reachedGoal(
            int x,
            int y,
            int width,
            int height) {

        Rectangle player = new Rectangle(
                x,
                y,
                width,
                height
        );

        return player.intersects(goal);
    }
}