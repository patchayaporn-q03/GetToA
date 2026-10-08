package com.mycompany.puzzle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author lenovo
 */
public class PuzzleStation extends JPanel {

    private static final Color CREAM = new Color(0xFFF6DC);
    private static final Color GOLD = new Color(0xD9A441);
    private static final Color BROWN = new Color(0x6B4423);

    public PuzzleStation(Runnable onClose) {
        setPreferredSize(new Dimension(640, 420));
        setLayout(new BorderLayout(0, 12));
        setBackground(CREAM);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BROWN, 3),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GOLD, 4),
                        BorderFactory.createEmptyBorder(16, 20, 16, 20))));

        JLabel title = new JLabel("Puzzle Station", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setForeground(BROWN);
        add(title, BorderLayout.NORTH);

        JLabel body = new JLabel("(ยังไม่มีเนื้อหาปริศนา)", SwingConstants.CENTER);
        body.setFont(new Font("Tahoma", Font.PLAIN, 18));
        body.setForeground(BROWN);
        add(body, BorderLayout.CENTER);

        JButton close = new JButton("ปิด");
        close.setFont(new Font("Tahoma", Font.BOLD, 16));
        close.setForeground(CREAM);
        close.setBackground(new Color(0x3E6B4A));
        close.setFocusPainted(false);
        close.addActionListener(e -> onClose.run());
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(close);
        add(south, BorderLayout.SOUTH);
    }
}
