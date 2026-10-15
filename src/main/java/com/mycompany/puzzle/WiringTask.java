
package com.mycompany.puzzle.saifai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WiringTask extends JPanel {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int TOTAL_WIRES = 10;

    private final Color[] colors = {
        Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA,
        Color.GREEN, Color.ORANGE, Color.GRAY, Color.WHITE,
        Color.BLACK, Color.PINK
    };

    private final String[] names = {
        "RED", "BLUE", "YELLOW", "MAGENTA", "GREEN",
        "ORANGE", "GRAY", "WHITE", "BLACK", "PINK"
    };

    private final int[] rightOrder = {
        2, 4, 0, 5, 1, 3, 8, 6, 9, 7
    };

    private final boolean[] connected = new boolean[TOTAL_WIRES];

    private int selected = -1;
    private int mouseX, mouseY;
    private int completed = 0;
    private boolean won = false;

    private int seconds = 0;
    private Timer gameTimer;

    // ปรับตำแหน่งสายไฟให้เหมาะกับหน้าต่าง 1280 x 720
    private final int leftX = 300;
    private final int rightX = 980;

    private final int[] ys = {
        155, 200, 245, 290, 335,
        380, 425, 470, 515, 560
    };

    private static double puzzleTime;
    private static int puzzleScore;
    private static boolean played = false;

    private final Runnable onFinish;

    public WiringTask() {
        this(null);
    }

    public WiringTask(Runnable onFinish) {
        this.onFinish = onFinish;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(25, 30, 45));
        setFocusable(true);

        if (onFinish != null) {
            getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close");

            getActionMap().put("close", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameTimer != null) {
                        gameTimer.stop();
                    }
                    onFinish.run();
                }
            });
        }

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (won) {
                    return;
                }

                mouseX = e.getX();
                mouseY = e.getY();

                for (int i = 0; i < TOTAL_WIRES; i++) {
                    int dx = mouseX - leftX;
                    int dy = mouseY - ys[i];

                    if (!connected[i]
                            && dx * dx + dy * dy <= 25 * 25) {
                        selected = i;
                        break;
                    }
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selected == -1 || won) {
                    return;
                }

                int target = -1;

                for (int i = 0; i < TOTAL_WIRES; i++) {
                    int dx = e.getX() - rightX;
                    int dy = e.getY() - ys[i];

                    if (dx * dx + dy * dy <= 25 * 25) {
                        target = i;
                        break;
                    }
                }

                if (target != -1
                        && rightOrder[target] == selected
                        && !connected[selected]) {

                    connected[selected] = true;
                    completed++;

                    if (completed == TOTAL_WIRES) {
                        won = true;
                        gameTimer.stop();
                        puzzleComplete(seconds);

                        if (onFinish != null) {
                            Timer t = new Timer(2500, ev -> onFinish.run());
                            t.setRepeats(false);
                            t.start();
                        }
                    }
                }

                selected = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });

        gameTimer = new Timer(1000, e -> {
            if (!won) {
                seconds++;
                repaint();
            }
        });

        gameTimer.start();
    }

    // คำนวณคะแนนและบันทึกข้อมูลเมื่อเล่นจบ
    public static void puzzleComplete(double time) {
        puzzleTime = time;
        played = true;

        if (time <= 20) {
            puzzleScore = 70;
        } else if (time <= 30) {
            puzzleScore = 65;
        } else if (time <= 60) {
            puzzleScore = 55;
        } else {
            puzzleScore = 45;
        }

        System.out.println("Puzzle Complete!");
        System.out.println("Time = " + puzzleTime + " seconds");
        System.out.println("Puzzle Score = " + puzzleScore);

        saveToCSV(puzzleTime, puzzleScore);
    }

    private static void saveToCSV(double time, int score) {
        // บันทึกไว้ในโฟลเดอร์โปรเจกต์ หากไม่ได้กำหนด DIR
        File file = new File("wiringtask.csv");

        try {
            File parent = file.getAbsoluteFile().getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            boolean newFile = !file.exists() || file.length() == 0;

            try (FileWriter writer = new FileWriter(file, true)) {
                if (newFile) {
                    writer.append("Date,Time_Seconds,Score");
                    writer.append(System.lineSeparator());
                }

                String date = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );

                writer.append(date).append(",");
                writer.append(String.valueOf(time)).append(",");
                writer.append(String.valueOf(score));
                writer.append(System.lineSeparator());
            }

            System.out.println("CSV saved successfully!");
            System.out.println("File location: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Cannot save CSV file!");
            e.printStackTrace();
        }
    }

    public static boolean isPlayed() {
        return played;
    }

    public static double getPuzzleTime() {
        return puzzleTime;
    }

    public static int getPuzzleScore() {
        return puzzleScore;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // หัวข้อ
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        drawCentered(g2, "WIRING TASK", WIDTH / 2, 45);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.drawString(
            "TIME: " + String.format(
                "%02d:%02d", seconds / 60, seconds % 60
            ),
            1020, 45
        );

        // พื้นที่เกม
        g2.setColor(new Color(45, 52, 70));
        g2.fillRoundRect(100, 75, 1080, 555, 25, 25);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        drawCentered(g2, "CONNECT THE WIRES", WIDTH / 2, 110);

        // วาดสายไฟที่เชื่อมต่อแล้ว
        for (int i = 0; i < TOTAL_WIRES; i++) {
            if (connected[i]) {
                drawWire(
                    g2, leftX, ys[i],
                    rightX, ys[rightOrderIndex(i)],
                    colors[i]
                );
            }
        }

        // วาดสายไฟระหว่างลาก
        if (selected != -1 && !won) {
            drawWire(
                g2, leftX, ys[selected],
                mouseX, mouseY, colors[selected]
            );
        }

        // ช่องสีฝั่งซ้าย
        for (int i = 0; i < TOTAL_WIRES; i++) {
            drawSocket(g2, leftX, ys[i], colors[i], names[i]);
        }

        // ช่องสีฝั่งขวา
        for (int i = 0; i < TOTAL_WIRES; i++) {
            int colorIndex = rightOrder[i];

            drawSocket(
                g2, rightX, ys[i],
                colors[colorIndex], names[colorIndex]
            );
        }

        // สถานะการเชื่อมต่อ
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        drawCentered(
            g2, "CONNECTED: " + completed + " / " + TOTAL_WIRES,
            WIDTH / 2, 665
        );

        // หน้าจอเมื่อเล่นสำเร็จ
        if (won) {
            g2.setColor(new Color(0, 0, 0, 220));
            g2.fillRoundRect(350, 220, 580, 240, 25, 25);

            g2.setColor(Color.GREEN);
            g2.setFont(new Font("Arial", Font.BOLD, 34));
            drawCentered(g2, "TASK COMPLETED!", WIDTH / 2, 275);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            drawCentered(
                g2, "Time: " + puzzleTime + " seconds",
                WIDTH / 2, 330
            );

            drawCentered(
                g2, "Puzzle Score: " + puzzleScore + " / 70",
                WIDTH / 2, 375
            );

            g2.setFont(new Font("Arial", Font.PLAIN, 17));
            drawCentered(g2, "Returning...", WIDTH / 2, 420);
        }

        g2.dispose();
    }

    private void drawCentered(Graphics g, String text, int centerX, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, centerX - fm.stringWidth(text) / 2, y);
    }

    private int rightOrderIndex(int colorIndex) {
        for (int i = 0; i < TOTAL_WIRES; i++) {
            if (rightOrder[i] == colorIndex) {
                return i;
            }
        }
        return -1;
    }

    private void drawSocket(
            Graphics g, int x, int y, Color color, String name) {

        int size = 24;

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);

        FontMetrics fm = g.getFontMetrics();
        int textX = x - 40 - fm.stringWidth(name);
        g.drawString(name, textX, y + 5);

        g.setColor(color);
        g.fillOval(x - size / 2, y - size / 2, size, size);

        g.setColor(Color.WHITE);
        g.drawOval(x - size / 2, y - size / 2, size, size);
    }

    private void drawWire(
            Graphics g, int x1, int y1,
            int x2, int y2, Color color) {

        Graphics2D g2 = (Graphics2D) g;

        Stroke oldStroke = g2.getStroke();

        g2.setStroke(new BasicStroke(
            5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
        ));

        g2.setColor(color);
        g2.drawLine(x1, y1, x2, y2);

        g2.setStroke(oldStroke);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Wiring Task");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new WiringTask());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}
