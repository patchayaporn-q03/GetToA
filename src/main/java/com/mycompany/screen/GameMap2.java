/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.screen;

/**
 *
 * @author lenovo
 */
public class GameMap2 extends javax.swing.JPanel {

    /**
     * Creates new form GameMap2
     */
    public GameMap2() {
        initComponents();
        setupBoxes();
        setupPlayer();
        setupBackButton();
    }

    //กล่องสมบัติ วางตายตัว: กล่องบนพื้นที่เดินได้ + กล่องบนโต๊ะ
    private static final int BOX_SIZE = 40;
    private static final int[][] FLOOR_BOXES = {
        {220, 230},  // ซ้ายบน
        {230, 420},  // ซ้ายกลาง
        {520, 330},  // กลาง ระหว่างโต๊ะ
        {810, 260},  // ขวาบน
        {800, 480},  // ขวาล่าง
    };
    private static final int[][] TABLE_BOXES = {
        {630, 320},  // บนโต๊ะขวาบน
        {400, 475},  // บนโต๊ะซ้ายล่าง
    };

    private void setupBoxes() {
        java.awt.image.BufferedImage src;
        try {
            src = javax.imageio.ImageIO.read(getClass().getResource("/images/map2/boxmap2.png"));
        } catch (java.io.IOException ex) {
            throw new java.io.UncheckedIOException(ex);
        }
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                BOX_SIZE, BOX_SIZE, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, BOX_SIZE, BOX_SIZE, null);
        g.dispose();

        java.util.List<int[]> all = new java.util.ArrayList<>(java.util.Arrays.asList(FLOOR_BOXES));
        all.addAll(java.util.Arrays.asList(TABLE_BOXES));
        for (int[] p : all) {
            add(new javax.swing.JLabel(new javax.swing.ImageIcon(img)),
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(p[0], p[1], BOX_SIZE, BOX_SIZE), 0);
        }
    }

    //ปุ่มกลับหน้าหลัก
    private void setupBackButton() {
        int w = 161;
        int h = 56;
        javax.swing.ImageIcon src = new javax.swing.ImageIcon(
                getClass().getResource("/images/button/home.png"));
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src.getImage(), 0, 0, w, h, null);
        g.dispose();

        javax.swing.JButton back = new javax.swing.JButton(new javax.swing.ImageIcon(img));
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setFocusPainted(false);
        back.setFocusable(false);
        back.setRolloverEnabled(false);
        back.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        back.addActionListener(e -> {
            heldKeys.clear();
            moveTimer.stop();
            Main.switchTo("mainmenu");
        });
        add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, w, h), 0);
    }

    private static final int PLAYER_SIZE = 60;
    private static final int PLAYER_SPEED = 4;

    //จุดเกิด: ยืนอยู่ที่ประตูล่าง (ประตูคู่กลางด้านล่างของแมพ)
    private static final int SPAWN_X = 585;
    private static final int SPAWN_Y = 535;

    //กล่องชนอยู่ที่เท้าของตัวละคร
    private static final int HIT_X = 15;
    private static final int HIT_Y = 40;
    private static final int HIT_W = 30;
    private static final int HIT_H = 16;

    //พื้นที่เดินได้ = พื้นไม้ + ทางเดินที่ประตู แล้วหักเคาน์เตอร์กับโต๊ะออก
    private final java.awt.geom.Area walkable = buildWalkableArea();

    private static java.awt.geom.Area buildWalkableArea() {
        java.awt.geom.Area area = new java.awt.geom.Area();
        area.add(new java.awt.geom.Area(new java.awt.Rectangle(180, 190, 680, 390))); // พื้นไม้
        area.add(new java.awt.geom.Area(new java.awt.Rectangle(550, 580, 130, 90)));  // ทางเดินที่ประตู
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(365, 190, 430, 55))); // เคาน์เตอร์
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(330, 290, 130, 90))); // โต๊ะซ้ายบน
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(330, 460, 130, 90))); // โต๊ะซ้ายล่าง
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(610, 300, 130, 90))); // โต๊ะขวาบน
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(610, 450, 130, 90))); // โต๊ะขวาล่าง
        // กล่องบนพื้น เดินทะลุไม่ได้ (กล่องบนโต๊ะอยู่ในพื้นที่ที่ห้ามเดินอยู่แล้ว)
        for (int[] p : FLOOR_BOXES) {
            area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(p[0], p[1], BOX_SIZE, BOX_SIZE)));
        }
        return area;
    }

    private boolean canStand(int x, int y) {
        return walkable.contains(new java.awt.Rectangle(x + HIT_X, y + HIT_Y, HIT_W, HIT_H));
    }

    private javax.swing.JLabel player;
    private javax.swing.Timer moveTimer;
    private final java.util.Set<String> heldKeys = new java.util.HashSet<>();

    //ตัวละครเดินด้วย W A S D
    private void setupPlayer() {
        java.awt.image.BufferedImage src;
        try {
            src = javax.imageio.ImageIO.read(getClass().getResource("/images/girl.png"));
        } catch (java.io.IOException ex) {
            throw new java.io.UncheckedIOException(ex);
        }
        java.awt.image.BufferedImage big = new java.awt.image.BufferedImage(
                PLAYER_SIZE, PLAYER_SIZE, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = big.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, PLAYER_SIZE, PLAYER_SIZE, null);
        g.dispose();
        player = new javax.swing.JLabel(new javax.swing.ImageIcon(big));
        add(player, new org.netbeans.lib.awtextra.AbsoluteConstraints(
                SPAWN_X, SPAWN_Y, PLAYER_SIZE, PLAYER_SIZE), 0);

        // ป้าย E ลอยเหนือจานอาหาร แสดงเมื่อตัวละครเดินเข้าใกล้
        prompt = new javax.swing.JLabel("E", javax.swing.SwingConstants.CENTER);
        prompt.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 16));
        prompt.setForeground(new java.awt.Color(0x6B4423));
        prompt.setOpaque(true);
        prompt.setBackground(new java.awt.Color(0xFFF6DC));
        prompt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0xD9A441), 2));
        prompt.setVisible(false);
        add(prompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(
                PLATE_X + PLATE_SIZE / 2 - 14, PLATE_Y - 34, 28, 26), 0);

        javax.swing.InputMap in = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        javax.swing.ActionMap act = getActionMap();
        in.put(javax.swing.KeyStroke.getKeyStroke("pressed E"), "interact");
        act.put("interact", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (isNearPlate()) {
                    heldKeys.clear();
                    moveTimer.stop();
                    // TODO: เปลี่ยนเป็นเปิด puzzle จริงเมื่อเพื่อนทำเสร็จ
                    javax.swing.JOptionPane.showMessageDialog(GameMap2.this,
                            "กด E ที่จานอาหารแล้ว (puzzle กำลังจะมาเร็วๆ นี้)");
                    heldKeys.clear();
                }
            }
        });
        for (String k : new String[] {"W", "A", "S", "D"}) {
            in.put(javax.swing.KeyStroke.getKeyStroke("pressed " + k), "press" + k);
            in.put(javax.swing.KeyStroke.getKeyStroke("released " + k), "release" + k);
            act.put("press" + k, new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    heldKeys.add(k);
                    moveTimer.start();
                }
            });
            act.put("release" + k, new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    heldKeys.remove(k);
                }
            });
        }

        moveTimer = new javax.swing.Timer(16, e -> {
            if (heldKeys.isEmpty()) {
                moveTimer.stop();
                return;
            }
            int dx = 0;
            int dy = 0;
            if (heldKeys.contains("A")) dx -= PLAYER_SPEED;
            if (heldKeys.contains("D")) dx += PLAYER_SPEED;
            if (heldKeys.contains("W")) dy -= PLAYER_SPEED;
            if (heldKeys.contains("S")) dy += PLAYER_SPEED;

            int x = player.getX();
            int y = player.getY();
            if (dx != 0 && canStand(x + dx, y)) {
                x += dx;
            }
            if (dy != 0 && canStand(x, y + dy)) {
                y += dy;
            }
            player.setLocation(x, y);

            ((java.awt.LayoutManager2) getLayout()).addLayoutComponent(player,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, PLAYER_SIZE, PLAYER_SIZE));
            prompt.setVisible(isNearPlate());
        });
    }

    //ตำแหน่งจานอาหาร (จุด puzzle)
    private static final int PLATE_X = 410;
    private static final int PLATE_Y = 320;
    private static final int PLATE_SIZE = 30;
    private static final int INTERACT_DISTANCE = 80;

    private javax.swing.JLabel prompt;

    // ตัวละครอยู่ใกล้จานพอที่จะกด E ไหม วัดจากจุดกึ่งกลาง
    private boolean isNearPlate() {
        int px = player.getX() + player.getWidth() / 2;
        int py = player.getY() + player.getHeight() / 2;
        int bx = PLATE_X + PLATE_SIZE / 2;
        int by = PLATE_Y + PLATE_SIZE / 2;
        return Math.hypot(px - bx, py - by) <= INTERACT_DISTANCE;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(1280, 720));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/plate_food_30x30.png"))); // NOI18N
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 320, 30, 30));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/tablemap2_190x120.png"))); // NOI18N
        jLabel3.setText("jLabel2");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 460, 190, 100));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/tablemap2_190x120.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 290, 190, 110));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/tablemap2_190x120.png"))); // NOI18N
        jLabel4.setText("jLabel2");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 300, 190, 110));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/tablemap2_190x120.png"))); // NOI18N
        jLabel5.setText("jLabel2");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 450, 190, 110));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map2/map2.png"))); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables
}
