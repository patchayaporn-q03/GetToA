package com.mycompany.screen;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * แอนิเมชันเดินของตัวละครที่เลือก ตัดจาก sprite sheet ใน images/avatar/animation (หญิง) และ animationboy/standyboy (ชาย)
 * (3 คอลัมน์ x 4 แถว: หญิงแถว 0-3 = หน้า/ซ้าย/ขวา/หลัง ชายแถว 0-3 = หลัง/ซ้าย/หน้า/ขวา
 * คอลัมน์ 0 = ยืน, คอลัมน์ 1-2 = ก้าวเท้า)
 */
public class WalkSprite {

    private static final int COLS = 3;
    private static final int ROWS = 4;
    private static final int DOWN = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int UP = 3;

    // ลำดับเฟรมตอนเดิน (คอลัมน์ในแถว)
    // หญิง: คอลัมน์ 0 = ยืนตรง ชาย: คอลัมน์ 1 = ท่ากลาง (คอลัมน์ 0 และ 2 เป็นท่าก้าวเท้าทั้งคู่)
    private static final int[] FEMALE_CYCLE = {1, 0, 2, 0};
    private static final int[] MALE_CYCLE = {0, 1, 2, 1};
    private static final int FEMALE_IDLE = 0;
    private static final int MALE_IDLE = 1;

    private final int[] cycle;
    private final int idleCol;

    // จำนวนรอบของ timer ต่อ 1 เฟรม (timer 16ms)
    // ตัวละครชายก้าวกว้างกว่า จึงสลับเฟรมถี่กว่า เท้าจะได้ไม่ไถลบนพื้น
    private static final int FEMALE_TICKS_PER_FRAME = 7;
    private static final int MALE_TICKS_PER_FRAME = 5;

    private final int ticksPerFrame;

    // เก็บรูปท่ายืนที่สร้างแล้ว (การตัดภาพจาก sheet ใช้เวลา)
    private static final java.util.Map<String, ImageIcon> STANDING = new java.util.HashMap<>();

    private final ImageIcon[][] frames = new ImageIcon[ROWS][COLS];
    private int direction = DOWN;
    private int tick = 0;

    // พิกเซลที่โปร่งใสกว่านี้ถือว่าเป็นขยะรอบตัวละคร (ขอบดำ/แดงจางๆ ในไฟล์ภาพ) ให้ลบทิ้ง
    private static final int MIN_ALPHA = 60;

    // ชิ้นส่วนเล็กกว่านี้ (พิกเซล) ถือว่าเป็นเศษขยะ ไม่ใช่ตัวละคร
    private static final int MIN_PIECE_AREA = 150;

    // แถวของ sprite sheet ที่เป็นทิศ {DOWN, LEFT, RIGHT, UP} หญิงเรียง หน้า/ซ้าย/ขวา/หลัง ชายเรียง หลัง/ซ้าย/หน้า/ขวา
    private static final int[] FEMALE_ROWS = {0, 1, 2, 3};
    private static final int[] MALE_ROWS = {2, 1, 3, 0};

    // สร้างแอนิเมชันเต็ม (ทั้ง 4 ทิศ) จาก sprite sheet; male = ใช้ลำดับแถว/เฟรมของตัวละครชาย
    private WalkSprite(BufferedImage sheet, int size, boolean male) {
        this(sheet, size, male, false);
    }

    // idleOnly = สร้างเฉพาะแถวหันหน้า (หน้าเลือกตัวละครใช้แค่ท่ายืน) ประหยัดเวลาวาดย่อ
    private WalkSprite(BufferedImage sheet, int size, boolean male, boolean idleOnly) {
        int[] sheetRows = male ? MALE_ROWS : FEMALE_ROWS;
        cycle = male ? MALE_CYCLE : FEMALE_CYCLE;
        idleCol = male ? MALE_IDLE : FEMALE_IDLE;
        ticksPerFrame = male ? MALE_TICKS_PER_FRAME : FEMALE_TICKS_PER_FRAME;
        int sw = sheet.getWidth();
        int sh = sheet.getHeight();
        int cw = sw / COLS;
        int ch = sh / ROWS;

        // แยกภาพเป็นชิ้นที่ติดกัน (connected components) แล้วจัดชิ้นเข้าเฟรมตามจุดกึ่งกลางของชิ้น
        // วิธีนี้ไม่ปนกับเฟรมข้างเคียง แม้ผมหรือแขนล้ำเส้นแบ่งช่อง และตัดเศษขยะทิ้งได้
        int[] argb = sheet.getRGB(0, 0, sw, sh, null, 0, sw);
        int[] cellOf = new int[sw * sh];
        java.util.Arrays.fill(cellOf, -1);
        boolean[] seen = new boolean[sw * sh];
        int[] stack = new int[sw * sh];
        int[] piece = new int[sw * sh];

        for (int start = 0; start < sw * sh; start++) {
            if (seen[start] || (argb[start] >>> 24) < MIN_ALPHA) {
                continue;
            }
            int pieceSize = 0;
            int top = 0;
            stack[top++] = start;
            seen[start] = true;
            long sx = 0;
            long sy = 0;
            while (top > 0) {
                int p = stack[--top];
                piece[pieceSize++] = p;
                int x = p % sw;
                int y = p / sw;
                sx += x;
                sy += y;
                for (int d = 0; d < 4; d++) {
                    int nx = x + (d == 0 ? 1 : d == 1 ? -1 : 0);
                    int ny = y + (d == 2 ? 1 : d == 3 ? -1 : 0);
                    if (nx < 0 || ny < 0 || nx >= sw || ny >= sh) {
                        continue;
                    }
                    int n = ny * sw + nx;
                    if (!seen[n] && (argb[n] >>> 24) >= MIN_ALPHA) {
                        seen[n] = true;
                        stack[top++] = n;
                    }
                }
            }
            if (pieceSize < MIN_PIECE_AREA) {
                continue;
            }
            int col = Math.min(COLS - 1, (int) (sx / pieceSize) / cw);
            int row = Math.min(ROWS - 1, (int) (sy / pieceSize) / ch);
            for (int i = 0; i < pieceSize; i++) {
                cellOf[piece[i]] = row * COLS + col;
            }
        }

        // ขอบเขตของตัวละครในแต่ละเฟรม
        int cells = ROWS * COLS;
        int[] minX = new int[cells];
        int[] minY = new int[cells];
        int[] maxX = new int[cells];
        int[] maxY = new int[cells];
        java.util.Arrays.fill(minX, Integer.MAX_VALUE);
        java.util.Arrays.fill(minY, Integer.MAX_VALUE);
        java.util.Arrays.fill(maxX, -1);
        java.util.Arrays.fill(maxY, -1);
        for (int p = 0; p < cellOf.length; p++) {
            int k = cellOf[p];
            if (k >= 0) {
                int x = p % sw;
                int y = p / sw;
                minX[k] = Math.min(minX[k], x);
                maxX[k] = Math.max(maxX[k], x);
                minY[k] = Math.min(minY[k], y);
                maxY[k] = Math.max(maxY[k], y);
            }
        }

        // ทุกเฟรมย่อด้วยสัดส่วนเดียวกัน (อิงเฟรมที่ใหญ่สุด)
        int maxW = 1;
        int maxH = 1;
        for (int k = 0; k < cells; k++) {
            if (maxX[k] >= 0) {
                maxW = Math.max(maxW, maxX[k] - minX[k] + 1);
                maxH = Math.max(maxH, maxY[k] - minY[k] + 1);
            }
        }
        double scale = Math.min((double) size / maxW, (double) size / maxH);

        for (int k = 0; k < cells; k++) {
            if (idleOnly && sheetRows[DOWN] != k / COLS) {
                continue;
            }
            BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB_PRE);
            if (maxX[k] >= 0) {
                int bw = maxX[k] - minX[k] + 1;
                int bh = maxY[k] - minY[k] + 1;

                BufferedImage cell = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB_PRE);
                double sumX = 0;
                long count = 0;
                for (int y = 0; y < bh; y++) {
                    for (int x = 0; x < bw; x++) {
                        int p = (minY[k] + y) * sw + (minX[k] + x);
                        if (cellOf[p] == k) {
                            cell.setRGB(x, y, argb[p]);
                            // วัดกึ่งกลางจากส่วนบน (หัวและลำตัว) เท่านั้น ขาจะได้ขยับได้อิสระ
                            if (y < bh / 2) {
                                sumX += x;
                                count++;
                            }
                        }
                    }
                }
                // ให้กึ่งกลางตัวและเท้าอยู่ที่เดิมทุกเฟรม ตัวละครจะไม่สั่นตอนสลับเฟรม
                double centerX = count == 0 ? bw / 2.0 : sumX / count;
                int dx = (int) Math.round(size / 2.0 - centerX * scale);
                int dy = (int) Math.round(size - bh * scale);

                Graphics2D g = out.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                g.drawImage(cell, dx, dy,
                        Math.max(1, (int) Math.round(bw * scale)),
                        Math.max(1, (int) Math.round(bh * scale)), null);
                g.dispose();
            }
            for (int dir = 0; dir < ROWS; dir++) {
                if (sheetRows[dir] == k / COLS) {
                    frames[dir][k % COLS] = new ImageIcon(out);
                }
            }
        }
    }

    //รูปท่ายืนตรง (หันหน้า) ของตัวละครตามชื่อรูป ใช้ในหน้าเลือกตัวละคร ให้ตรงกับตัวที่เดินในเกม
    public static synchronized ImageIcon standingIcon(String image, boolean male, int size) {
        String key = (male ? "m:" : "f:") + image + ":" + size;
        if (!STANDING.containsKey(key)) {
            BufferedImage sheet = com.mycompany.data.CharacterData.loadWalkSheet(image, male ? "male" : "female");
            STANDING.put(key, sheet == null ? null : new WalkSprite(sheet, size, male, true).idle());
        }
        return STANDING.get(key);
    }

    // แอนิเมชันของตัวละครที่เลือกล่าสุด หรือ null ถ้าไม่มี sheet ให้ใช้ (เช่น ตัวละครชาย)
    public static WalkSprite forSelected(int size) {
        BufferedImage sheet = com.mycompany.data.CharacterData.loadSelectedWalkSheet();
        if (sheet == null) {
            return null;
        }
        String[] s = com.mycompany.data.CharacterData.loadSettings();
        return new WalkSprite(sheet, size, s != null && s[0].trim().equalsIgnoreCase("male"));
    }

    // เฟรมท่ายืน (ทิศล่าสุดที่หัน) และรีเซ็ตตัวนับแอนิเมชัน
    public ImageIcon idle() {
        tick = 0;
        return frames[direction][idleCol];
    }

    // เฟรมถัดไปตามทิศที่กำลังเดิน (ตามแนวนอนมาก่อนเมื่อกดทแยง)
    public ImageIcon walk(int dx, int dy) {
        if (dx < 0) {
            direction = LEFT;
        } else if (dx > 0) {
            direction = RIGHT;
        } else if (dy < 0) {
            direction = UP;
        } else if (dy > 0) {
            direction = DOWN;
        }
        int step = (tick++ / ticksPerFrame) % cycle.length;
        return frames[direction][cycle[step]];
    }
}
