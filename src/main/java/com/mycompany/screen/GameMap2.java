/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.screen;

import com.mycompany.grade.GradeBox;
import com.mycompany.grade.GradeManager;

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
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshPlayerImage();
                gradeBox.syncManager();
            }
        });
        refreshPlayerImage();
    }

    // แอนิเมชันเดินของตัวละครที่เลือก (null = ไม่มี sheet ใช้รูปนิ่งแทน)
    private WalkSprite walkSprite;

    //เปลี่ยนรูปผู้เล่นเป็นตัวละครที่เลือกจากหน้าเลือกตัวละคร (ถ้าไม่มีข้อมูล ใช้รูปเดิม)
    private void refreshPlayerImage() {
        walkSprite = WalkSprite.forSelected(PLAYER_SIZE);
        if (walkSprite != null) {
            player.setIcon(walkSprite.idle());
            return;
        }
        javax.swing.ImageIcon icon = com.mycompany.data.CharacterData.loadSelectedIcon(PLAYER_SIZE);
        if (icon != null) {
            player.setIcon(icon);
        }
    }

    //กล่องสมบัติ วางตายตัว: กล่องบนพื้นที่เดินได้ + กล่องบนโต๊ะ
    // ระบบกล่องใช้ GradeBox (ต้องการ 6 ตำแหน่ง: 5 กล่องบนพื้น + 1 กล่องบนโต๊ะ)
    private static final int[][] BOX_POSITIONS = {
        {220, 230},  // ซ้ายบน
        {230, 420},  // ซ้ายกลาง
        {520, 330},  // กลาง ระหว่างโต๊ะ
        {810, 260},  // ขวาบน
        {800, 480},  // ขวาล่าง
        {630, 320},  // บนโต๊ะขวาบน
    };
    private final GradeBox gradeBox = new GradeBox(GradeManager.getInstance(), BOX_POSITIONS);
    private final int BOX_SIZE = gradeBox.getBoxSize();

    private final java.util.List<java.awt.Component> boxLabels = new java.util.ArrayList<>();
    private final java.util.Set<Integer> openedIndexes = new java.util.HashSet<>();
    private boolean addingBoxes = false;

    private void setupBoxes() {
        addingBoxes = true;
        try {
            gradeBox.setupBoxes(this);
        } finally {
            addingBoxes = false;
        }
        // GradeBox ใช้รูปกล่องของแมพ 1 จึงสลับเป็นรูปกล่องของแมพ 2
        boxImage = loadBoxImage();
        for (java.awt.Component c : boxLabels) {
            if (c instanceof javax.swing.JLabel label && label.getIcon() != null) {
                label.setIcon(new javax.swing.ImageIcon(boxImage));
            }
        }
    }

    private java.awt.image.BufferedImage boxImage;

    private java.awt.image.BufferedImage loadBoxImage() {
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
        return img;
    }

    // GradeBox เรียก add(component) โดยไม่มี constraints แต่แมพนี้ใช้ AbsoluteLayout
    @Override
    protected void addImpl(java.awt.Component comp, Object constraints, int index) {
        if (addingBoxes && constraints == null) {
            java.awt.Rectangle r = comp.getBounds();
            constraints = new org.netbeans.lib.awtextra.AbsoluteConstraints(r.x, r.y, r.width, r.height);
            index = 0;
            boxLabels.add(comp);
        }
        super.addImpl(comp, constraints, index);
    }

    // หากล่องที่ยังไม่เปิดและใกล้ผู้เล่นที่สุด ใช้หลักเดียวกับ GradeBox
    private int nearestClosedBoxIndex() {
        int[][] pos = gradeBox.getAllBoxPositions();
        int px = player.getX() + player.getWidth() / 2;
        int py = player.getY() + player.getHeight() / 2;
        int best = -1;
        double bestDist = Double.MAX_VALUE;
        for (int i = 0; i < pos.length; i++) {
            if (openedIndexes.contains(i)) {
                continue;
            }
            double d = Math.hypot(px - (pos[i][0] + BOX_SIZE / 2), py - (pos[i][1] + BOX_SIZE / 2));
            if (d < bestDist) {
                bestDist = d;
                best = i;
            }
        }
        return bestDist <= GradeBox.INTERACT_DISTANCE ? best : -1;
    }

    // เปลี่ยนรูปกล่องที่เปิดแล้วให้เป็นกล่องใสๆ
    private void makeBoxTransparent(int index) {
        int[] p = gradeBox.getAllBoxPositions()[index];
        for (java.awt.Component c : boxLabels) {
            if (c instanceof javax.swing.JLabel label
                    && label.getIcon() != null
                    && label.getX() == p[0] && label.getY() == p[1]
                    && label.getWidth() == BOX_SIZE && label.getHeight() == BOX_SIZE) {
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                        BOX_SIZE, BOX_SIZE, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g = img.createGraphics();
                g.setComposite(java.awt.AlphaComposite.SrcOver.derive(0.3f));
                g.drawImage(boxImage, 0, 0, null);
                g.dispose();
                label.setIcon(new javax.swing.ImageIcon(img));
                repaint();
                return;
            }
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
            GameFrame.switchTo("mainmenu");
        });
        add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, w, h), 0);
    }

    private static final int PLAYER_SIZE = 60;
    private static final int PLAYER_SPEED = 2;

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

    private java.awt.geom.Area buildWalkableArea() {
        java.awt.geom.Area area = new java.awt.geom.Area();
        area.add(new java.awt.geom.Area(new java.awt.Rectangle(180, 190, 680, 390))); // พื้นไม้
        area.add(new java.awt.geom.Area(new java.awt.Rectangle(550, 580, 130, 90)));  // ทางเดินที่ประตู
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(365, 190, 430, 55))); // เคาน์เตอร์
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(330, 290, 130, 90))); // โต๊ะซ้ายบน
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(330, 460, 130, 90))); // โต๊ะซ้ายล่าง
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(610, 300, 130, 90))); // โต๊ะขวาบน
        area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(610, 450, 130, 90))); // โต๊ะขวาล่าง
        // กล่องบนพื้น เดินทะลุไม่ได้ (กล่องบนโต๊ะอยู่ในพื้นที่ที่ห้ามเดินอยู่แล้ว)
        for (int[] p : gradeBox.getFloorBoxPositions()) {
            area.subtract(new java.awt.geom.Area(new java.awt.Rectangle(p[0], p[1], BOX_SIZE, BOX_SIZE)));
        }
        return area;
    }

    private boolean canStand(int x, int y) {
        return walkable.contains(new java.awt.Rectangle(x + HIT_X, y + HIT_Y, HIT_W, HIT_H));
    }

    private boolean wiringDone = false;

    // เปิด WiringTask ใน popup ต่อสายครบแล้วไปหน้าสรุปคะแนน (กด Esc เพื่อปิดโดยไม่เล่นจบ)
    private void openWiringTask() {
        if (wiringDone) {
            return;
        }
        javax.swing.JDialog dialog = new javax.swing.JDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this), "Wiring Task",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.add(new com.mycompany.puzzle.saifai.WiringTask(dialog::dispose));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        heldKeys.clear();
        if (com.mycompany.puzzle.saifai.WiringTask.isPlayed()) {
            wiringDone = true;
            GameFrame.showResult("mainmenu", "หน้าหลัก");
        }
    }

    private javax.swing.JLabel player;
    private javax.swing.Timer moveTimer;
    private final java.util.Set<String> heldKeys = new java.util.HashSet<>();

    //ตัวละครเดินด้วย W A S D
    private void setupPlayer() {
        // ตัวละครเริ่มต้นคือ pikachu.gif (ย่อให้พอดี PLAYER_SIZE) ถ้าเลือกตัวละครไว้จะถูกแทนที่ใน refreshPlayerImage()
        java.awt.Image src = new javax.swing.ImageIcon(getClass().getResource(
                "/com/mycompany/puzzle/assets/character/pikachu.gif")).getImage();
        player = new javax.swing.JLabel(new javax.swing.ImageIcon(
                src.getScaledInstance(PLAYER_SIZE, PLAYER_SIZE, java.awt.Image.SCALE_SMOOTH)));
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
                heldKeys.clear();
                moveTimer.stop();
                if (isNearPlate()) {
                    openWiringTask();
                } else {
                    int target = nearestClosedBoxIndex();
                    if (gradeBox.openNearestBox(GameMap2.this, player) && target >= 0) {
                        openedIndexes.add(target);
                        makeBoxTransparent(target);
                    }
                }
                heldKeys.clear();
                gradeBox.updatePrompts(player);
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
                if (walkSprite != null) {
                    player.setIcon(walkSprite.idle());
                }
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
            if (walkSprite != null) {
                player.setIcon(walkSprite.walk(dx, dy));
            }

            ((java.awt.LayoutManager2) getLayout()).addLayoutComponent(player,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, PLAYER_SIZE, PLAYER_SIZE));
            prompt.setVisible(isNearPlate());
            gradeBox.updatePrompts(player);
        });
        gradeBox.updatePrompts(player);
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