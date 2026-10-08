package game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameFrame extends JFrame {

    private GamePanel panel;

    public GameFrame() {

        setTitle("Get to A - Maze");

        setSize(
                MazeMap.WIDTH,
                MazeMap.HEIGHT
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setResizable(false);

        panel = new GamePanel();

        add(panel);

        setLocationRelativeTo(null);

        panel.requestFocusInWindow();
    }

    class GamePanel extends JPanel implements KeyListener {

        private MazeMap map;
        private Character character;
        private Player player;

        private Image playerImage;

        // เวลาเริ่มเกม
        private long startTime;

        // เวลาที่หยุดเมื่อถึงเส้นชัย
        private long finishTime;

        private boolean complete = false;

        private Timer timer;

        public GamePanel() {

            setFocusable(true);
            addKeyListener(this);

            map = new MazeMap();

            character = new Character(
                    map.getStartX(),
                    map.getStartY()
            );

            player = new Player(character);

            // โหลดรูปตัวละคร
            playerImage = new ImageIcon(
                    "assets/character/pikachu.gif"
            ).getImage();

            // เริ่มจับเวลา
            startTime = System.currentTimeMillis();

            // กำหนดค่าเริ่มต้นของเวลาจบ
            finishTime = 0;

            timer = new Timer(
                    16,
                    e -> gameUpdate()
            );

            timer.start();
        }

        private void gameUpdate() {

            // ถ้ายังไม่จบเกม ให้ผู้เล่นเคลื่อนที่
            if (!complete) {

                player.update(map);

                checkGoal();
            }

            repaint();
        }

        private void checkGoal() {

            // ตรวจว่าตัวละครถึงเส้นชัยหรือยัง
            if (map.reachedGoal(
                    character.getX(),
                    character.getY(),
                    character.getWidth(),
                    character.getHeight()
            )) {

                // บันทึกเวลาทันทีที่ถึงเส้นชัย
                finishTime = System.currentTimeMillis();

                // จบเกมทันที
                complete = true;

                // หยุด Timer
                timer.stop();

                // คำนวณเวลาที่ใช้จริง
                double time =
                        (finishTime - startTime)
                        / 1000.0;

                // ส่งเวลาไปสรุปผล
                PuzzleStation.puzzleComplete(time);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            // วาดแผนที่
            map.draw(g);

            // วาดตัวละคร
            if (playerImage != null) {

                g.drawImage(
                        playerImage,
                        character.getX(),
                        character.getY(),
                        character.getWidth(),
                        character.getHeight(),
                        this
                );

            } else {

                // ถ้าหารูปไม่เจอ ให้ใช้วงกลมสีแดงแทน
                g.setColor(Color.RED);

                g.fillOval(
                        character.getX(),
                        character.getY(),
                        character.getWidth(),
                        character.getHeight()
                );
            }

            /*
             * ============================
             * คำนวณเวลาที่ใช้
             * ============================
             */

            double time;

            if (complete) {

                // ถ้าจบแล้ว ใช้เวลาที่บันทึกไว้
                time =
                        (finishTime - startTime)
                        / 1000.0;

            } else {

                // ถ้ายังไม่จบ ให้นับเวลาปัจจุบัน
                long currentTime =
                        System.currentTimeMillis();

                time =
                        (currentTime - startTime)
                        / 1000.0;
            }

            /*
             * ============================
             * HUD
             * ============================
             */

            g.setColor(Color.BLACK);

            g.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            16
                    )
            );

            g.drawString(
                    "W A S D = Move",
                    30,
                    30
            );

            g.drawString(
                    String.format(
                            "Time : %.1f sec",
                            time
                    ),
                    750,
                    30
            );

            /*
             * ============================
             * เมื่อยังไม่จบเกม
             * ============================
             *
             * ไม่มี Minimum time แล้ว
             */

            /*
             * ============================
             * เมื่อจบ Puzzle
             * ============================
             */

            if (complete) {

                g.setColor(
                        new Color(
                                0,
                                0,
                                0,
                                180
                        )
                );

                g.fillRect(
                        250,
                        250,
                        460,
                        120
                );

                g.setColor(Color.WHITE);

                g.setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                30
                        )
                );

                g.drawString(
                        "PUZZLE COMPLETE!",
                        325,
                        300
                );

                g.setFont(
                        new Font(
                                "Arial",
                                Font.PLAIN,
                                20
                        )
                );

                g.drawString(
                        String.format(
                                "Time : %.1f seconds",
                                time
                        ),
                        390,
                        340
                );
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {

            // ไม่รับการเคลื่อนที่หลังจากจบเกม
            if (!complete) {
                player.keyPressed(e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            // ไม่รับการเคลื่อนที่หลังจากจบเกม
            if (!complete) {
                player.keyReleased(e);
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }
    }
}