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

    private static final int WIDTH = 900;
    private static final int HEIGHT = 700;
    private static final int TOTAL_WIRES = 10;

    private Color[] colors = {
        Color.RED,
        Color.BLUE,
        Color.YELLOW,
        Color.MAGENTA,
        Color.GREEN,
        Color.ORANGE,
        Color.GRAY,
        Color.WHITE,
        Color.BLACK,
        Color.PINK
    };

    private String[] names = {
        "RED", "BLUE", "YELLOW", "MAGENTA", "GREEN",
        "ORANGE", "GRAY", "WHITE", "BLACK", "PINK"
    };

    private int[] rightOrder = {
        2, 4, 0, 5, 1, 3, 8, 6, 9, 7
    };

    private boolean[] connected = new boolean[TOTAL_WIRES];

    private int selected = -1;
    private int mouseX, mouseY;
    private int completed = 0;
    private boolean won = false;

    private int seconds = 0;
    private Timer gameTimer;

    private int leftX = 180;
    private int rightX = 770;

    private int[] ys = {
        145, 190, 235, 280, 325,
        370, 415, 460, 505, 550
    };

    private static double puzzleTime;
    private static int puzzleScore;

    private static boolean played = false;
    private final Runnable onFinish;

    public WiringTask() {
        this(null);
    }

    // onFinish ถูกเรียกหลังต่อสายครบและโชว์หน้าสำเร็จครู่หนึ่ง หรือเมื่อกด Esc (ใช้ปิด popup)
    public WiringTask(Runnable onFinish) {
        this.onFinish = onFinish;
        if (onFinish != null) {
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close");
            getActionMap().put("close", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    gameTimer.stop();
                    onFinish.run();
                }
            });
        }
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(25, 30, 45));

        addMouseListener(new MouseAdapter() {

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
                        if (WiringTask.this.onFinish != null) {
                            Timer t = new Timer(2500, ev -> WiringTask.this.onFinish.run());
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

            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });

        gameTimer = new Timer(1000, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!won) {
                    seconds++;
                    repaint();
                }
            }
        });

        gameTimer.start();
    }

    // คำนวณคะแนนเมื่อเล่นจบ
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

        // บันทึกข้อมูลลงไฟล์ CSV
        saveToCSV(puzzleTime, puzzleScore);
    }

    // บันทึกวัน เวลา และคะแนนลง CSV
    private static void saveToCSV(double time, int score) {
        String fileName = com.mycompany.data.CharacterData.DIR + "/wiringtask.csv";
        File file = new File(fileName);

        try {
            boolean newFile = !file.exists() || file.length() == 0;

            FileWriter writer = new FileWriter(file, true);

            // เพิ่มหัวตารางเมื่อไฟล์ยังว่าง
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

            writer.close();

            System.out.println("CSV saved successfully!");
            System.out.println("File location: "
                    + file.getAbsolutePath());

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

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("WIRING TASK", 330, 45);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("TIME: "
                + String.format("%02d:%02d",
                seconds / 60, seconds % 60), 700, 45);

        g.setColor(new Color(45, 52, 70));
        g.fillRoundRect(45, 75, 810, 545, 25, 25);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("CONNECT THE WIRES", 335, 105);

        // วาดสายไฟที่เชื่อมต่อแล้ว
        for (int i = 0; i < TOTAL_WIRES; i++) {
            if (connected[i]) {
                drawWire(g, leftX, ys[i],
                        rightX, ys[rightOrderIndex(i)], colors[i]);
            }
        }

        // วาดสายไฟขณะลาก
        if (selected != -1 && !won) {
            drawWire(g, leftX, ys[selected],
                    mouseX, mouseY, colors[selected]);
        }

        // วาดช่องสีฝั่งซ้าย
        for (int i = 0; i < TOTAL_WIRES; i++) {
            drawSocket(g, leftX, ys[i], colors[i], names[i]);
        }

        // วาดช่องสีฝั่งขวา
        for (int i = 0; i < TOTAL_WIRES; i++) {
            int colorIndex = rightOrder[i];

            drawSocket(g, rightX, ys[i],
                    colors[colorIndex], names[colorIndex]);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("CONNECTED: " + completed
                + " / " + TOTAL_WIRES, 355, 655);

        // หน้าจอเมื่อเล่นสำเร็จ
        if (won) {
            g.setColor(new Color(0, 0, 0, 210));
            g.fillRoundRect(220, 235, 460, 200, 25, 25);

            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("TASK COMPLETED!", 320, 285);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Time: " + puzzleTime + " seconds",
                    360, 330);

            g.drawString("Puzzle Score: "
                    + puzzleScore + " / 70", 360, 370);

            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("save", 445, 405);
        }
    }

    private int rightOrderIndex(int colorIndex) {
        for (int i = 0; i < TOTAL_WIRES; i++) {
            if (rightOrder[i] == colorIndex) {
                return i;
            }
        }

        return -1;
    }

    // วาดชื่อสีไว้ทางซ้ายของช่องสี
    private void drawSocket(Graphics g, int x, int y,
                            Color color, String name) {

        int size = 24;

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(name);
        int textX = x - 40 - textWidth;

        g.drawString(name, textX, y + 5);

        // วาดช่องสี
        g.setColor(color);
        g.fillOval(x - size / 2, y - size / 2, size, size);

        // วาดขอบช่องสี
        g.setColor(Color.WHITE);
        g.drawOval(x - size / 2, y - size / 2,
                size, size);
    }

    private void drawWire(Graphics g, int x1, int y1,
                          int x2, int y2, Color color) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setStroke(new BasicStroke(5,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        g2.setColor(color);
        g2.drawLine(x1, y1, x2, y2);

        g2.setStroke(new BasicStroke(1));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame("Wiring Task");

                frame.setDefaultCloseOperation(
                        JFrame.EXIT_ON_CLOSE);

                frame.add(new WiringTask());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);
            }
        });
    }
}
