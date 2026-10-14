package com.mycompany.screen;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * แอนิเมชันเดินของตัวละครที่เลือก ตัดจาก sprite sheet ใน images/avatar/animation
 * (3 คอลัมน์ x 4 แถว: แถว 0 = หันหน้า, 1 = หันซ้าย, 2 = หันขวา, 3 = หันหลัง
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
    private static final int[] CYCLE = {1, 0, 2, 0};

    // จำนวนรอบของ timer ต่อ 1 เฟรม (timer 16ms)
    private static final int TICKS_PER_FRAME = 9;

    // เก็บรูปท่ายืนที่สร้างแล้ว (การตัดภาพจาก sheet ใช้เวลา)
    private static final java.util.Map<String, ImageIcon> STANDING = new java.util.HashMap<>();

    private final ImageIcon[][] frames = new ImageIcon[ROWS][COLS];
    private int direction = DOWN;
    private int tick = 0;

    // พิกเซลที่โปร่งใสกว่านี้ถือว่าเป็นขยะรอบตัวละคร (ขอบดำ/แดงจางๆ ในไฟล์ภาพ) ให้ลบทิ้ง
    private static final int MIN_ALPHA = 60;

    // ชิ้นส่วนเล็กกว่านี้ (พิกเซล) ถือว่าเป็นเศษขยะ ไม่ใช่ตัวละคร
    private static final int MIN_PIECE_AREA = 150;

    private WalkSprite(BufferedImage sheet, int size) {
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

        for (int start = 0; start < sw * sh; start++) {
            if (seen[start] || (argb[start] >>> 24) < MIN_ALPHA) {
                continue;
            }
            java.util.List<Integer> piece = new java.util.ArrayList<>();
            int top = 0;
            stack[top++] = start;
            seen[start] = true;
            long sx = 0;
            long sy = 0;
            while (top > 0) {
                int p = stack[--top];
                piece.add(p);
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
            if (piece.size() < MIN_PIECE_AREA) {
                continue;
            }
            int col = Math.min(COLS - 1, (int) (sx / piece.size()) / cw);
            int row = Math.min(ROWS - 1, (int) (sy / piece.size()) / ch);
            for (int p : piece) {
                cellOf[p] = row * COLS + col;
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
                            sumX += x;
                            count++;
                        }
                    }
                }
                // ให้กึ่งกลางตัวและเท้าอยู่ที่เดิมทุกเฟรม ตัวละครจะไม่สั่นตอนสลับเฟรม
                double centerX = sumX / count;
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
            frames[k / COLS][k % COLS] = new ImageIcon(out);
        }
    }

    // รูปท่ายืนตรง (หันหน้า) ของตัวละครตามชื่อรูป ใช้ในหน้าเลือกตัวละคร ให้ตรงกับตัวที่เดินในเกม
    // คืน null ถ้าตัวละครนั้นไม่มี sheet (ผู้เรียกใช้รูป standy เดิมแทน)
    public static ImageIcon standingIcon(String image, int size) {
        String key = image + ":" + size;
        if (!STANDING.containsKey(key)) {
            BufferedImage sheet = com.mycompany.data.CharacterData.loadWalkSheet(image);
            STANDING.put(key, sheet == null ? null : new WalkSprite(sheet, size).frames[DOWN][0]);
        }
        return STANDING.get(key);
    }

    // แอนิเมชันของตัวละครที่เลือกล่าสุด หรือ null ถ้าไม่มี sheet ให้ใช้ (เช่น ตัวละครชาย)
    public static WalkSprite forSelected(int size) {
        BufferedImage sheet = com.mycompany.data.CharacterData.loadSelectedWalkSheet();
        return sheet == null ? null : new WalkSprite(sheet, size);
    }

    public ImageIcon idle() {
        tick = 0;
        return frames[direction][0];
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
        int step = (tick++ / TICKS_PER_FRAME) % CYCLE.length;
        return frames[direction][CYCLE[step]];
    }
}
