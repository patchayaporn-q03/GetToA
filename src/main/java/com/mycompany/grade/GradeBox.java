package com.mycompany.grade;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class GradeBox {

    public static final int BOX_COUNT = 6;
    public static final int TRAP_SCORE = -10;
    public static final int MAX_SCORE = 30;
    public static final int INTERACT_DISTANCE = 70;
    public static final int BOX_SIZE = 40;

    // ตำแหน่งเริ่มต้นของแมพ 1
    private static final int[][] DEFAULT_BOX_POSITIONS = {
            { 260, 235 },
            { 300, 450 },
            { 330, 585 },
            { 800, 300 },
            { 800, 470 },
            { 700, 285 }
    };

    private final GradeManager gradeManager;
    private final int[][] boxPositions;

    private int boxScore;
    private int openedBoxCount;

    private boolean boxesCreated;

    private final JLabel[] boxLabels = new JLabel[BOX_COUNT];
    private final JLabel[] promptLabels = new JLabel[BOX_COUNT];
    private final boolean[] openedBoxes = new boolean[BOX_COUNT];
    private final int[] boxScores = new int[BOX_COUNT];

    private ImageIcon closedIcon;
    private ImageIcon openedIcon;

    /**
     * สร้างระบบกล่องสำหรับแมพที่กำหนด
     * 
     * @param gradeManager ตัวจัดการคะแนนรวมของเกม
     * @param boxPositions ตำแหน่งกล่องทั้ง 6 กล่องในแมพ
     *                     สร้างกล่องทั้งหมดลงบนแผนที่
     * @param map          JPanel ของแมพที่ใช้แสดงกล่อง
     * @param player       JLabel ของตัวละครผู้เล่น
     *                     เพิ่มคะแนนจากกล่องที่เปิด
     * @param score        คะแนนที่ได้รับจากกล่อง อาจเป็นคะแนนติดลบจากกล่องกับดัก
     * @param player       JLabel ของตัวละครผู้เล่น
     */

    public GradeBox() {
        this(null, DEFAULT_BOX_POSITIONS);
    }

    public GradeBox(GradeManager gradeManager) {
        this(gradeManager, DEFAULT_BOX_POSITIONS);
    }

    public GradeBox(
            GradeManager gradeManager,
            int[][] boxPositions) {

        this.gradeManager = gradeManager;

        if (boxPositions == null
                || boxPositions.length != BOX_COUNT) {
            throw new IllegalArgumentException(
                    "ต้องกำหนดตำแหน่งกล่องให้ครบ 6 ตำแหน่ง");
        }

        this.boxPositions = new int[BOX_COUNT][2];

        for (int i = 0; i < BOX_COUNT; i++) {
            if (boxPositions[i] == null
                    || boxPositions[i].length < 2) {
                throw new IllegalArgumentException(
                        "ตำแหน่งกล่องไม่ถูกต้อง");
            }

            this.boxPositions[i][0] = boxPositions[i][0];
            this.boxPositions[i][1] = boxPositions[i][1];
        }

        boxScore = 0;
        openedBoxCount = 0;
        boxesCreated = false;

        int[] scores = { 4, 5, 6, 7, 8, TRAP_SCORE };

        List<Integer> shuffledScores = new ArrayList<>();

        for (int score : scores) {
            shuffledScores.add(score);
        }

        Collections.shuffle(shuffledScores);

        for (int i = 0; i < BOX_COUNT; i++) {
            boxScores[i] = shuffledScores.get(i);
        }
    }

    public void setupBoxes(JPanel map) {

        if (map == null || boxesCreated) {
            return;
        }

        java.net.URL imageURL = getClass().getResource(
                "/images/map1/boxmap1.png");

        if (imageURL == null) {
            JOptionPane.showMessageDialog(
                    map,
                    "ไม่พบรูป /images/map1/boxmap1.png");
            return;
        }

        ImageIcon originalIcon = new ImageIcon(imageURL);

        Image scaledImage = originalIcon.getImage()
                .getScaledInstance(
                        BOX_SIZE,
                        BOX_SIZE,
                        Image.SCALE_SMOOTH);

        closedIcon = new ImageIcon(scaledImage);

        Image openedImage = scaledImage.getScaledInstance(
                BOX_SIZE,
                BOX_SIZE,
                Image.SCALE_SMOOTH);

        openedIcon = new ImageIcon(openedImage);

        for (int i = 0; i < BOX_COUNT; i++) {

            int x = boxPositions[i][0];
            int y = boxPositions[i][1];

            JLabel box = new JLabel(closedIcon);
            box.setBounds(x, y, BOX_SIZE, BOX_SIZE);

            JLabel prompt = new JLabel(
                    "กด E เพื่อเปิดกล่อง",
                    SwingConstants.CENTER);

            prompt.setBounds(
                    x - 45,
                    y - 25,
                    BOX_SIZE + 90,
                    20);

            prompt.setVisible(false);

            boxLabels[i] = box;
            promptLabels[i] = prompt;

            map.add(prompt);
            map.add(box);
        }

        map.repaint();
        boxesCreated = true;
    }

    public int[][] getFloorBoxPositions() {

        int[][] positions = new int[5][2];

        for (int i = 0; i < 5; i++) {
            positions[i][0] = boxPositions[i][0];
            positions[i][1] = boxPositions[i][1];
        }

        return positions;
    }

    public int getBoxSize() {
        return BOX_SIZE;
    }

    // คืนตำแหน่งกล่องทั้งหมด 6 กล่อง
    public int[][] getAllBoxPositions() {

        int[][] positions = new int[BOX_COUNT][2];

        for (int i = 0; i < BOX_COUNT; i++) {
            positions[i][0] = boxPositions[i][0];
            positions[i][1] = boxPositions[i][1];
        }

        return positions;
    }

    private int nearestClosedBox(JLabel player) {

        if (player == null) {
            return -1;
        }

        int playerX = player.getX() + player.getWidth() / 2;
        int playerY = player.getY() + player.getHeight() / 2;

        int nearestIndex = -1;
        double nearestDistance = Double.MAX_VALUE;

        for (int i = 0; i < BOX_COUNT; i++) {

            if (openedBoxes[i] || boxLabels[i] == null) {
                continue;
            }

            int boxX = boxPositions[i][0] + BOX_SIZE / 2;
            int boxY = boxPositions[i][1] + BOX_SIZE / 2;

            double distance = Math.sqrt(
                    Math.pow(playerX - boxX, 2)
                            + Math.pow(playerY - boxY, 2));

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestIndex = i;
            }
        }

        if (nearestDistance <= INTERACT_DISTANCE) {
            return nearestIndex;
        }

        return -1;
    }

    public void updatePrompts(JLabel player) {

        if (!boxesCreated || player == null) {
            return;
        }

        int nearestIndex = nearestClosedBox(player);

        for (int i = 0; i < BOX_COUNT; i++) {

            if (promptLabels[i] != null) {
                promptLabels[i].setVisible(
                        i == nearestIndex
                                && !openedBoxes[i]);
            }
        }
    }

    public boolean openNearestBox(
            JPanel map,
            JLabel player) {

        if (!boxesCreated
                || map == null
                || player == null) {
            return false;
        }

        int index = nearestClosedBox(player);

        if (index == -1) {
            return false;
        }

        if (!addBoxScore(boxScores[index])) {
            return false;
        }

        openedBoxes[index] = true;

        if (boxLabels[index] != null) {
            boxLabels[index].setIcon(openedIcon);
        }

        if (promptLabels[index] != null) {
            promptLabels[index].setVisible(false);
        }

        map.repaint();

        int score = boxScores[index];

        String message;

        if (score == TRAP_SCORE) {
            message = "คุณเปิดกล่องกับดัก!\n"
                    + "คะแนนกล่อง: " + score;
        } else {
            message = "คุณเปิดกล่องสำเร็จ!\n"
                    + "คะแนนกล่อง: +" + score;
        }

        message += "\nเปิดกล่องแล้ว "
                + openedBoxCount + "/" + BOX_COUNT
                + " กล่อง";

        message += "\nคะแนนกล่องรวม: " + getBoxScore();

        if (canFinishBox()) {
            message += "\nคุณสามารถเลือกหยุดเปิดกล่องได้แล้ว";
        }

        JOptionPane.showMessageDialog(map, message);

        return true;
    }

    public boolean addBoxScore(int score) {

        if (openedBoxCount >= BOX_COUNT) {
            return false;
        }

        // เก็บคะแนนดิบไว้ก่อน เพื่อให้กับดักหักคะแนนได้ถูกต้อง
        if (gradeManager != null) {
            gradeManager.addBoxScore(score);
        } else {
            boxScore += score;
        }

        openedBoxCount++;

        return true;
    }

    public int getOpenedBoxCount() {
        return openedBoxCount;
    }

    public boolean canFinishBox() {
        return openedBoxCount >= 3;
    }

    // คะแนนที่แสดงต้องอยู่ระหว่าง 0 ถึง 30
    public int getBoxScore() {

        int score = gradeManager != null
                ? gradeManager.getBoxScore()
                : boxScore;

        return Math.max(0, Math.min(score, MAX_SCORE));
    }
}
