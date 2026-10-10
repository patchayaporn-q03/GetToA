package com.mycompany.puzzle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Puzzle เขาวงกตที่เล่นใน popup ของ GameMap1
 *
 * @author lenovo
 */
public class PuzzleStation extends JPanel implements KeyListener {

    private final MazeMap map;
    private final Character character;
    private final Player player;
    private final Image playerImage;
    private final Timer timer;

    private final long startTime;
    private long finishTime;
    private boolean complete = false;

    public PuzzleStation(Runnable onClose) {
        setPreferredSize(new Dimension(MazeMap.WIDTH, MazeMap.HEIGHT));
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        map = new MazeMap();
        character = new Character(map.getStartX(), map.getStartY());
        player = new Player(character);
        playerImage = new ImageIcon(
                getClass().getResource("/images/girl.png")
        ).getImage();

        startTime = System.currentTimeMillis();
        timer = new Timer(16, e -> gameUpdate());

        // ปุ่มปิด popup (ไม่รับ focus เพื่อให้ปุ่ม WASD ยังทำงาน)
        JButton close = new JButton("ปิด");
        close.setFont(new Font("Tahoma", Font.BOLD, 14));
        close.setFocusable(false);
        close.setSize(70, 30);
        close.addActionListener(e -> {
            timer.stop();
            onClose.run();
        });
        add(close);
        closeButton = close;
    }

    private JButton closeButton;

    // ขนาดและตำแหน่งที่ย่อ/ขยายเขาวงกตให้พอดีจอ (คงสัดส่วน)
    private double scale() {
        return Math.min(getWidth() / (double) MazeMap.WIDTH,
                getHeight() / (double) MazeMap.HEIGHT);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        closeButton.setLocation(getWidth() - 90, 20);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        timer.start();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    private void gameUpdate() {
        if (!complete) {
            player.update(map);
            checkGoal();
        }
        repaint();
    }

    private void checkGoal() {
        if (map.reachedGoal(character.getX(), character.getY(),
                character.getWidth(), character.getHeight())) {
            finishTime = System.currentTimeMillis();
            complete = true;
            timer.stop();
            PuzzleScore.puzzleComplete(elapsedSeconds());
            repaint();
        }
    }

    private double elapsedSeconds() {
        long end = complete ? finishTime : System.currentTimeMillis();
        return (end - startTime) / 1000.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        double s = scale();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate((getWidth() - MazeMap.WIDTH * s) / 2,
                (getHeight() - MazeMap.HEIGHT * s) / 2);
        g2.scale(s, s);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        paintGame(g2);
        g2.dispose();
    }

    private void paintGame(Graphics g) {
        map.draw(g);

        if (playerImage != null) {
            g.drawImage(playerImage, character.getX(), character.getY(),
                    character.getWidth(), character.getHeight(), this);
        } else {
            g.setColor(Color.RED);
            g.fillOval(character.getX(), character.getY(),
                    character.getWidth(), character.getHeight());
        }

        double time = elapsedSeconds();

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("W A S D = Move", 30, 30);
        g.drawString(String.format("Time : %.1f sec", time), 750, 30);

        if (complete) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(250, 250, 460, 150);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("PUZZLE COMPLETE!", 325, 300);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString(String.format("Time : %.1f seconds", time), 390, 340);
            g.drawString("Score : " + PuzzleScore.getPuzzleScore(), 430, 375);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!complete) {
            player.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!complete) {
            player.keyReleased(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
