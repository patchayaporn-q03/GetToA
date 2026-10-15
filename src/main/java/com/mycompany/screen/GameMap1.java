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
public class GameMap1 extends javax.swing.JPanel {

    /**
     * Creates new form GameMap1
     */
    public GameMap1() {
        initComponents();
        walkable = buildWalkableArea(true);
        setupBoxes();
        setupPlayer();
        setupBackButton();
        // ทุกครั้งที่หน้านี้แสดง ใช้ตัวละครที่ผู้เล่นเลือกล่าสุด
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

    //กล่องสมบัติ 6 กล่อง วางตายตัว: 5 กล่องบนพื้นที่เดินได้ + 1 กล่องบนโต๊ะ (โต๊ะแถวบนขวา)
    // ระบบกล่องใช้ GradeBox (ตำแหน่ง รูป คะแนน และการเปิดกล่องอยู่ในนั้น)
    private final GradeManager gradeManager = GradeManager.getInstance();
    private final GradeBox gradeBox = new GradeBox(gradeManager);
    private final int BOX_SIZE = gradeBox.getBoxSize();

    // ชิ้นส่วนที่ GradeBox เพิ่มลงแมพ (กล่องและป้าย) เก็บไว้เพื่อลบออกหลังผ่าน puzzle
    private final java.util.List<java.awt.Component> boxLabels = new java.util.ArrayList<>();
    private boolean addingBoxes = false;

    private void setupBoxes() {
        addingBoxes = true;
        try {
            gradeBox.setupBoxes(this);
        } finally {
            addingBoxes = false;
        }
    }

    // GradeBox เรียก add(component) โดยไม่มี constraints แต่แมพนี้ใช้ AbsoluteLayout
    // จึงแปลง bounds ของ component เป็น AbsoluteConstraints และวางไว้หน้าพื้นหลัง
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

    // กล่องที่เปิดแล้ว (index ตามลำดับตำแหน่งใน GradeBox)
    private final java.util.Set<Integer> openedIndexes = new java.util.HashSet<>();

    // หากล่องที่ยังไม่เปิดและใกล้ผู้เล่นที่สุด ใช้หลักเดียวกับ GradeBox เพื่อให้ตรงกับกล่องที่ GradeBox เปิดจริง
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
                javax.swing.Icon icon = label.getIcon();
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                        icon.getIconWidth(), icon.getIconHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g = img.createGraphics();
                g.setComposite(java.awt.AlphaComposite.SrcOver.derive(0.3f));
                icon.paintIcon(label, g, 0, 0);
                g.dispose();
                label.setIcon(new javax.swing.ImageIcon(img));
                repaint();
                return;
            }
        }
    }

    // หลังสรุปผลแล้วกล่องหายไปและเปิดไม่ได้อีก
    private boolean boxesRemoved = false;

    private void removeBoxes() {
        boxesRemoved = true;
        for (java.awt.Component c : boxLabels) {
            remove(c);
        }
        boxLabels.clear();
        // กล่องหายแล้ว จึงเดินผ่านตำแหน่งเดิมได้
        for (int[] p : gradeBox.getFloorBoxPositions()) {
            walkable.add(rect(p[0], p[1], BOX_SIZE, BOX_SIZE));
        }
        revalidate();
        repaint();
    }

    private boolean doorOpen = false;

    private static final int PLAYER_SIZE = 60;
    private static final int PLAYER_SPEED = 2;

    //จุดเกิด: ใต้ประตูขวาบน (ให้เท้าตัวละครอยู่บนพื้น)
    private static final int SPAWN_X = 975;
    private static final int SPAWN_Y = 190;

    //ตำแหน่งหนังสือ
    private static final int BOOK_X = 560;
    private static final int BOOK_Y = 390;
    private static final int BOOK_SIZE = 20;

    //กล่องชนอยู่ที่เท้าของตัวละคร
    private static final int HIT_X = 15;
    private static final int HIT_Y = 40;
    private static final int HIT_W = 30;
    private static final int HIT_H = 16;

    //พื้นที่เดินได้ = พื้นไม้ + โถงกระเบื้องขวา + ช่องเชื่อม แล้วหักสิ่งกีดขวางออก
    private java.awt.geom.Area walkable;

    private java.awt.geom.Area buildWalkableArea(boolean withBoxes) {
        java.awt.geom.Area area = new java.awt.geom.Area();
        //พื้นไม้ห้องเรียน
        area.add(rect(195, 215, 700, 417));
        //โถงกระเบื้องมุมขวาบน (ใต้ประตู)
        area.add(rect(915, 228, 160, 124));   // ส่วนบน
        area.add(rect(948, 352, 127, 48));    // ส่วนกลาง
        area.add(rect(948, 400, 172, 135));   // ส่วนล่าง
        //ช่องเชื่อมพื้นไม้กับโถง (ผนังบางๆ ที่ x≈900-910 ถูกเจาะเป็นทางเดิน)
        area.add(rect(890, 235, 30, 75));
        
        //สิ่งกีดขวางบนพื้นไม้ (ครูโต๊ะ ตู้ ต้นไม้ ชั้นหนังสือ)
        area.subtract(rect(528, 215, 122, 45));  // โต๊ะครู
        area.subtract(rect(762, 215, 105, 25));  // ตู้ + ต้นไม้ขวาบน
        area.subtract(rect(180, 215, 45, 85));   // ต้นไม้/แท่นซ้ายบน
        area.subtract(rect(190, 385, 45, 110));  // ตู้เหล็ก + ต้นไม้ซ้าย
        area.subtract(rect(190, 495, 80, 120)); // ต้นไม้ใหญ่มุมซ้ายล่าง
        area.subtract(rect(395, 550, 90, 90));   // ตู้ + ต้นไม้ล่าง
        area.subtract(rect(750, 570, 145, 70));  // ชั้นหนังสือล่างขวา
        area.subtract(rect(852, 540, 43, 40));   // ต้นไม้ล่างขวา
        area.subtract(rect(893, 500, 30, 60));   // ต้นไม้ขวา
        area.subtract(rect(893, 380, 35, 95));   // ตู้ขวา
        // สิ่งกีดขวางในโถงกระเบื้อง
        area.subtract(rect(940, 435, 40, 80));   // ถังขยะ
        // โต๊ะ เดินได้เฉพาะทางเดินระหว่างโต๊ะ
        for (int tx : new int[] {390, 540, 690}) {
            for (int ty : new int[] {300, 390, 480}) {
                area.subtract(rect(tx, ty, 60, 60));
            }
        }
        // กล่องบนพื้น เดินทะลุไม่ได้ (กล่องบนโต๊ะอยู่ในพื้นที่ที่ห้ามเดินอยู่แล้ว)
        if (withBoxes) {
            for (int[] p : gradeBox.getFloorBoxPositions()) {
                area.subtract(rect(p[0], p[1], BOX_SIZE, BOX_SIZE));
            }
        }
        return area;
    }

    private static java.awt.geom.Area rect(int x, int y, int w, int h) {
        return new java.awt.geom.Area(new java.awt.Rectangle(x, y, w, h));
    }

    private boolean canStand(int x, int y) {
        return walkable.contains(new java.awt.Rectangle(x + HIT_X, y + HIT_Y, HIT_W, HIT_H));
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

    private javax.swing.JLabel player;
    private javax.swing.Timer moveTimer;
    private final java.util.Set<String> heldKeys = new java.util.HashSet<>();

    //ตัวละครเดินด้วย W A S D (กดค้างเพื่อเดินต่อเนื่อง, กดสองปุ่มพร้อมกันเดินทแยงได้)
    private void setupPlayer() {
        // ตัวละครเริ่มต้นคือ pikachu.gif (ย่อให้พอดี PLAYER_SIZE) ถ้าเลือกตัวละครไว้จะถูกแทนที่ใน refreshPlayerImage()
        java.awt.Image src = new javax.swing.ImageIcon(getClass().getResource(
                "/com/mycompany/puzzle/assets/character/pikachu.gif")).getImage();
        player = new javax.swing.JLabel(new javax.swing.ImageIcon(
                src.getScaledInstance(PLAYER_SIZE, PLAYER_SIZE, java.awt.Image.SCALE_SMOOTH)));
        // index 0 = วางบนสุด เพื่อให้อยู่หน้าพื้นหลังและโต๊ะ
        add(player, new org.netbeans.lib.awtextra.AbsoluteConstraints(
                SPAWN_X, SPAWN_Y, PLAYER_SIZE, PLAYER_SIZE), 0);

        // ป้าย E ลอยเหนือหนังสือ แสดงเมื่อตัวละครเดินเข้าใกล้
        prompt = new javax.swing.JLabel("E", javax.swing.SwingConstants.CENTER);
        prompt.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 16));
        prompt.setForeground(new java.awt.Color(0x6B4423));
        prompt.setOpaque(true);
        prompt.setBackground(new java.awt.Color(0xFFF6DC));
        prompt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0xD9A441), 2));
        prompt.setVisible(false);
        add(prompt, new org.netbeans.lib.awtextra.AbsoluteConstraints(
                BOOK_X + BOOK_SIZE / 2 - 14, BOOK_Y - 34, 28, 26), 0);

        javax.swing.InputMap in = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        javax.swing.ActionMap act = getActionMap();

        in.put(javax.swing.KeyStroke.getKeyStroke("pressed E"), "interact");
        act.put("interact", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!doorOpen && isNearBook()) {
                    openPuzzleStation();
                } else if (!boxesRemoved) {
                    heldKeys.clear();
                    moveTimer.stop();
                    int target = nearestClosedBoxIndex();
                    if (gradeBox.openNearestBox(GameMap1.this, player) && target >= 0) {
                        openedIndexes.add(target);
                        makeBoxTransparent(target);
                    }
                    heldKeys.clear();
                    gradeBox.updatePrompts(player);
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
            prompt.setVisible(!doorOpen && isNearBook());
            if (!boxesRemoved) {
                gradeBox.updatePrompts(player);
            }

            // เดินถึงประตู (ที่เปิดหลังผ่าน puzzle) แล้วย้ายไป GameMap2
            if (doorOpen && y + HIT_Y <= 160) {
                showMap1Summary();
            }
        });
        prompt.setVisible(!doorOpen && isNearBook());
        gradeBox.updatePrompts(player);
    }

    private static final int INTERACT_DISTANCE = 70;

    private javax.swing.JLabel prompt;

    // ตัวละครอยู่ใกล้หนังสือพอที่จะกด E ไหม วัดจากจุดกึ่งกลาง
    private boolean isNearBook() {
        int px = player.getX() + player.getWidth() / 2;
        int py = player.getY() + player.getHeight() / 2;
        int bx = BOOK_X + BOOK_SIZE / 2;
        int by = BOOK_Y + BOOK_SIZE / 2;
        return Math.hypot(px - bx, py - by) <= INTERACT_DISTANCE;
    }

    private void openPuzzleStation() {
        heldKeys.clear();
        moveTimer.stop();
        javax.swing.JDialog dialog = new javax.swing.JDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this), "Puzzle Station",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        com.mycompany.puzzle.PuzzleStation station =
                new com.mycompany.puzzle.PuzzleStation(dialog::dispose);
        dialog.add(station);
        // ขนาด 1280x720 เท่าหน้าจออื่น จัดกลางหน้าต่างเกม
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        heldKeys.clear();
        // ผู้เล่นปิด popup หลังเล่นจบแล้ว ค่อยเปลี่ยนแมพ
        // กลับมาแมพ 1 แบบเดิม กล่องยังเปิดได้ จนกว่าจะเดินไปที่ประตู
        if (station.isComplete()) {
            applyPassedMap();
        }
    }

    // เดินถึงประตู: ไปหน้าสรุปผลเดิม (เกรด+คะแนน) ปุ่มเขียวไปแมพ 2 ปุ่มแดงกลับหน้าหลัก
    private void showMap1Summary() {
        heldKeys.clear();
        moveTimer.stop();
        removeBoxes();
        GameFrame.showResult("map2", "เล่นแมพ 2", "mainmenu", "หน้าหลัก");
    }

    //เปลี่ยนเป็นแมพหลังผ่าน puzzle: พื้นหลังใหม่ กล่องยังอยู่ให้เปิดต่อได้จนกว่าจะออกประตู
    private void applyPassedMap() {
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/map1-pass.png")));
        walkable.add(rect(955, 130, 100, 100)); // เปิดทางเดินไปที่ประตู
        doorOpen = true;
        revalidate();
        repaint();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        book = new javax.swing.JLabel();
        table1 = new javax.swing.JLabel();
        table2 = new javax.swing.JLabel();
        table3 = new javax.swing.JLabel();
        table4 = new javax.swing.JLabel();
        table5 = new javax.swing.JLabel();
        table6 = new javax.swing.JLabel();
        table7 = new javax.swing.JLabel();
        table8 = new javax.swing.JLabel();
        table9 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(1280, 720));
        setMinimumSize(new java.awt.Dimension(1280, 720));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        book.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/book.png"))); // NOI18N
        add(book, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 390, 20, 20));

        table1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table1.setText("jLabel2");
        add(table1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 300, 60, 60));

        table2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table2.setText("jLabel2");
        add(table2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 300, 60, 60));

        table3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table3.setText("jLabel2");
        add(table3, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 300, 60, 60));

        table4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table4.setText("jLabel2");
        add(table4, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 390, 60, 60));

        table5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table5.setText("jLabel2");
        add(table5, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 390, 60, 60));

        table6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table6.setText("jLabel2");
        add(table6, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 390, 60, 60));

        table7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table7.setText("jLabel2");
        add(table7, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 480, 60, 60));

        table8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table8.setText("jLabel2");
        add(table8, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 480, 60, 60));

        table9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/tablemap1.png"))); // NOI18N
        table9.setText("jLabel2");
        add(table9, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 480, 60, 60));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/map1/map1.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 720));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel book;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel table1;
    private javax.swing.JLabel table2;
    private javax.swing.JLabel table3;
    private javax.swing.JLabel table4;
    private javax.swing.JLabel table5;
    private javax.swing.JLabel table6;
    private javax.swing.JLabel table7;
    private javax.swing.JLabel table8;
    private javax.swing.JLabel table9;
    // End of variables declaration//GEN-END:variables
}