package com.mycompany.screen;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

/**
 *
 * @author lenovo
 */
public class CharacterSelect extends javax.swing.JPanel {

    /**
     * Creates new form Avatar
     */
    public CharacterSelect() {
        initComponents();

        //เลือกได้ทีละเพศ และเปลี่ยนรูปปุ่มเมื่อถูกเลือก
        javax.swing.ButtonGroup genderGroup = new javax.swing.ButtonGroup();
        genderGroup.add(chickboy);
        genderGroup.add(chickgirl);
        chickboy.addActionListener(e -> showBoxes(false));
        chickgirl.addActionListener(e -> showBoxes(true));
        chickboy.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/male_selected.png")));
        chickgirl.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/female_selected.png")));

        //ช่องตัวละคร เลือกได้ทีละช่อง และเปลี่ยนรูปเมื่อถูกเลือก
        javax.swing.ButtonGroup boxGroup = new javax.swing.ButtonGroup();
        javax.swing.Icon boxSelected = new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_selected.png"));
        for (javax.swing.JToggleButton box : new javax.swing.JToggleButton[] {
            charecter1, charecter2, charecter3, charecter4, charecter5,
            charecter6, charecter7, charecter8, charecter9}) {
            boxGroup.add(box);
            box.setSelectedIcon(boxSelected);
        }

        styleNameField();

        //ค่าเริ่มต้น เพศหญิง และช่องตัวละครช่องแรก
        String[] last = com.mycompany.data.CharacterData.loadSettings();
        boolean startFemale = last == null || !last[0].trim().equalsIgnoreCase("male");
        (startFemale ? chickgirl : chickboy).setSelected(true);
        showBoxes(startFemale);   // เลือกช่องเริ่มต้นให้ในเมธอดนี้
        //framglass.setBackground(new java.awt.Color(255, 255, 255, 120));
        setupGlassPanel();
    }

    //ตัวละครหญิง/ชาย อ่านจาก Data/character_stats.csv
    private final java.util.List<com.mycompany.data.CharacterData> girls =
            com.mycompany.data.CharacterData.load("female");
    private final java.util.List<com.mycompany.data.CharacterData> boys =
            com.mycompany.data.CharacterData.load("male");

    //รายชื่อตัวละครของเพศที่เลือกอยู่ตอนนี้
    private java.util.List<com.mycompany.data.CharacterData> characters = girls;

    //ช่องตัวละครที่เลือกอยู่ตอนนี้ (index ใน characters)
    private int selectedIndex = 0;

    private final java.awt.event.ActionListener[] boxListeners = new java.awt.event.ActionListener[9];

    private String folder() {
        return com.mycompany.data.CharacterData.standyFolder(characters == boys ? "male" : "female");
    }

    private javax.swing.ImageIcon loadPic(String name, int size) {
        // ใช้ท่ายืนตรงจากแอนิเมชัน (ตัวเดียวกับที่เดินในเกม) ถ้าไม่มีใช้รูป standy เดิม
        javax.swing.ImageIcon standing = WalkSprite.standingIcon(name, size);
        if (standing != null) {
            return standing;
        }
        java.awt.Image img = new javax.swing.ImageIcon(
                getClass().getResource("/images/avatar/" + folder() + "/" + name + ".png")).getImage();
        return new javax.swing.ImageIcon(img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
    }

    private void showBoxes(boolean female) {
        characters = female ? girls : boys;
        String gender = female ? "female" : "male";
        javax.swing.JToggleButton[] boxes = {
            charecter1, charecter2, charecter3, charecter4, charecter5,
            charecter6, charecter7, charecter8, charecter9};
        for (int i = 0; i < boxes.length; i++) {
            if (boxListeners[i] != null) {
                boxes[i].removeActionListener(boxListeners[i]);
                boxListeners[i] = null;
            }
            boxes[i].setVisible(i < characters.size());
        }
        for (int i = 0; i < boxes.length && i < characters.size(); i++) {
            final int index = i;
            String image = characters.get(i).getImage();
            // วาดตัวละครทับบนกรอบ ให้อยู่ในขอบเขตกล่อง 100x100 (ตัวละคร 80x80 อยู่กึ่งกลาง)
            boxes[i].setIcon(boxWithPic("box_norma", image));
            boxes[i].setSelectedIcon(boxWithPic("box_selected", image));
            // กดช่องแล้วแสดงตัวละครตัวใหญ่ด้านซ้าย และใส่ชื่อตัวละครในช่องชื่อ
            boxListeners[i] = e -> selectCharacter(index, null);
            boxes[i].addActionListener(boxListeners[i]);
        }

        // ค่าเริ่มต้น: ตัวที่เคยเลือกไว้ใน settings.csv ถ้าไม่มีใช้ช่องแรก
        int start = 0;
        String savedName = null;
        String[] saved = com.mycompany.data.CharacterData.loadSettings();
        if (saved != null && saved[0].trim().equalsIgnoreCase(gender)) {
            for (int i = 0; i < characters.size() && i < boxes.length; i++) {
                if (String.valueOf(characters.get(i).getId()).equals(saved[1].trim())) {
                    start = i;
                    savedName = saved[2];
                }
            }
        }
        boxes[start].setSelected(true);
        selectCharacter(start, savedName);
    }

    //แสดงตัวละครที่เลือก name = ชื่อที่ผู้เล่นเคยตั้ง (null = ใช้ชื่อตัวละครจาก csv)
    private void selectCharacter(int index, String name) {
        selectedIndex = index;
        com.mycompany.data.CharacterData c = characters.get(index);
        charecterpic.setIcon(loadPic(c.getImage(), 200));
        editname.setText(name != null && !name.isBlank() ? name : c.getName());
    }

    //รวมรูปกรอบกล่องกับรูปตัวละครเป็นภาพเดียว
    private javax.swing.ImageIcon boxWithPic(String frameName, String picName) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                100, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(new javax.swing.ImageIcon(
                getClass().getResource("/images/charecter/" + frameName + ".png")).getImage(), 0, 0, 100, 100, null);
        javax.swing.ImageIcon standing = WalkSprite.standingIcon(picName, 80);
        g.drawImage(standing != null ? standing.getImage() : new javax.swing.ImageIcon(
                getClass().getResource("/images/avatar/" + folder() + "/" + picName + ".png")).getImage(), 10, 10, 80, 80, null);
        g.dispose();
        return new javax.swing.ImageIcon(img);
    }



    private void setupGlassPanel() {
        framglass.setOpaque(false);
        framglass.setBorder(new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int w, int h) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 60; //ทำขอบมน
                g2.setColor(new java.awt.Color(255, 255, 255, 120));
                g2.fillRoundRect(x, y, w - 1, h - 1, arc, arc);
                g2.setColor(new java.awt.Color(255, 255, 255, 180));
                g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
                g2.dispose();
            }
        });
        //ทำให้ปุ่มใน framglass เป็นโปร่งใส
        for (java.awt.Component c : framglass.getComponents()) {
            if (c instanceof javax.swing.AbstractButton b) {
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
                b.setRolloverEnabled(false);
            }
        }
    }

    //ช่องกรอกชื่อ
    private void styleNameField() {
        java.awt.Color cream = new java.awt.Color(0xFFF6DC);
        java.awt.Color gold = new java.awt.Color(0xD9A441);
        java.awt.Color brown = new java.awt.Color(0x6B4423);

        editname.setText("");
        editname.setToolTipText("ใส่ชื่อตัวละคร");
        editname.setFont(new java.awt.Font(GameFrame.FONT_NAME, java.awt.Font.BOLD, 16));
        editname.setForeground(brown);
        editname.setCaretColor(brown);
        editname.setSelectionColor(gold);
        editname.setSelectedTextColor(java.awt.Color.WHITE);
        editname.setBackground(cream);
        editname.setOpaque(true);
        editname.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(brown, 2),
                javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(gold, 3),
                        javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 8))));

        //จำกัดความยาวชื่อ 12 ตัวอักษร
        ((javax.swing.text.AbstractDocument) editname.getDocument()).setDocumentFilter(
                new javax.swing.text.DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int off, int len, String s, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                String add = s == null ? "" : s;
                int room = 12 - (fb.getDocument().getLength() - len);
                if (room <= 0) {
                    return;
                }
                super.replace(fb, off, len, add.length() > room ? add.substring(0, room) : add, a);
            }

            @Override
            public void insertString(FilterBypass fb, int off, String s, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                replace(fb, off, 0, s, a);
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        framglass = new javax.swing.JPanel();
        confirm = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        chickgirl = new javax.swing.JRadioButton();
        chickboy = new javax.swing.JRadioButton();
        editname = new javax.swing.JTextField();
        charecter1 = new javax.swing.JToggleButton();
        charecter2 = new javax.swing.JToggleButton();
        charecter3 = new javax.swing.JToggleButton();
        charecter4 = new javax.swing.JToggleButton();
        charecter5 = new javax.swing.JToggleButton();
        charecter6 = new javax.swing.JToggleButton();
        charecter7 = new javax.swing.JToggleButton();
        charecter8 = new javax.swing.JToggleButton();
        charecter9 = new javax.swing.JToggleButton();
        charecterpic = new javax.swing.JLabel();
        backToMenu = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        framglass.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        framglass.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        confirm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/confirm.png"))); // NOI18N
        confirm.setBorderPainted(false);
        confirm.setContentAreaFilled(false);
        confirm.addActionListener(this::confirm);
        framglass.add(confirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(47, 458, 160, 55));

        cancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/cancel.png"))); // NOI18N
        cancel.setBorderPainted(false);
        cancel.setContentAreaFilled(false);
        cancel.addActionListener(this::cancel);
        framglass.add(cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(237, 458, 148, 53));

        chickgirl.setText("jRadioButton1");
        chickgirl.setContentAreaFilled(false);
        chickgirl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/female_normal.png"))); // NOI18N
        chickgirl.addActionListener(this::chickgirlActionPerformed);
        framglass.add(chickgirl, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 70, 161, 57));

        chickboy.setText("jRadioButton1");
        chickboy.setContentAreaFilled(false);
        chickboy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/male_normal.png"))); // NOI18N
        chickboy.addActionListener(this::chickboyActionPerformed);
        framglass.add(chickboy, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 70, 160, 60));

        editname.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        editname.setText("jTextField1");
        editname.addActionListener(this::editnameActionPerformed);
        framglass.add(editname, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 160, 30));

        charecter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter1.setBorderPainted(false);
        charecter1.setContentAreaFilled(false);
        charecter1.addActionListener(this::charecter1ActionPerformed);
        framglass.add(charecter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 140, 100, 100));

        charecter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter2.setBorderPainted(false);
        charecter2.setContentAreaFilled(false);
        charecter2.addActionListener(this::charecter2ActionPerformed);
        framglass.add(charecter2, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 140, 100, 100));

        charecter3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter3.setBorderPainted(false);
        charecter3.setContentAreaFilled(false);
        charecter3.addActionListener(this::charecter3ActionPerformed);
        framglass.add(charecter3, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 140, 100, 100));

        charecter4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter4.setBorderPainted(false);
        charecter4.setContentAreaFilled(false);
        charecter4.addActionListener(this::charecter4ActionPerformed);
        framglass.add(charecter4, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 260, 100, 100));

        charecter5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter5.setBorderPainted(false);
        charecter5.setContentAreaFilled(false);
        charecter5.addActionListener(this::charecter5ActionPerformed);
        framglass.add(charecter5, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 260, 100, 100));

        charecter6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter6.setBorderPainted(false);
        charecter6.setContentAreaFilled(false);
        charecter6.addActionListener(this::charecter6ActionPerformed);
        framglass.add(charecter6, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 260, 100, 100));

        charecter7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter7.setBorderPainted(false);
        charecter7.setContentAreaFilled(false);
        charecter7.addActionListener(this::charecter7ActionPerformed);
        framglass.add(charecter7, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 380, 100, 100));

        charecter8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter8.setBorderPainted(false);
        charecter8.setContentAreaFilled(false);
        charecter8.addActionListener(this::charecter8ActionPerformed);
        framglass.add(charecter8, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 380, 100, 100));

        charecter9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/charecter/box_norma.png"))); // NOI18N
        charecter9.setBorderPainted(false);
        charecter9.setContentAreaFilled(false);
        charecter9.addActionListener(this::charecter9ActionPerformed);
        framglass.add(charecter9, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 380, 100, 100));

        charecterpic.setBackground(new java.awt.Color(153, 153, 153));
        charecterpic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loading/charecterloading.png"))); // NOI18N
        framglass.add(charecterpic, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 150, 200, 270));

        add(framglass, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 880, 550));

        backToMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/button/home.png"))); // NOI18N
        backToMenu.setBorderPainted(false);
        backToMenu.setContentAreaFilled(false);
        backToMenu.addActionListener(this::backToMenu);
        add(backToMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 330, 230, 80));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit/mapbass.png"))); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 720));
    }// </editor-fold>//GEN-END:initComponents

    // บันทึกตัวละครที่เลือกอยู่บนหน้านี้และชื่อที่ผู้เล่นแก้ลง Data/settings.csv
    // (เรียกตอนกด confirm และตอนกด Play เพื่อให้ตัวละครในเกมตรงกับที่เลือกเสมอ)
    public void saveCurrentSelection() {
        if (characters.isEmpty()) {
            return;
        }
        com.mycompany.data.CharacterData chosen = characters.get(selectedIndex);
        String playerName = editname.getText().isBlank() ? chosen.getName() : editname.getText();
        com.mycompany.data.CharacterData.saveSettings(chosen.getId(), chosen.getGender(), playerName);
    }

    private void confirm(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirm
        //button yes แสดงข้อความ 1 วินาทีแล้วกลับหน้าเมนู
        confirm.setEnabled(false);

        saveCurrentSelection();

        javax.swing.JDialog toast = new javax.swing.JDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this));
        toast.setUndecorated(true);
        javax.swing.JLabel msg = new javax.swing.JLabel("บันทึกตัวละครแล้ว", javax.swing.SwingConstants.CENTER);
        msg.setFont(new java.awt.Font(GameFrame.FONT_NAME, java.awt.Font.BOLD, 28));
        msg.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 48, 24, 48));
        toast.add(msg);
        toast.pack();
        toast.setLocationRelativeTo(this);
        toast.setVisible(true);

        javax.swing.Timer t = new javax.swing.Timer(1000, e -> {
            toast.dispose();
            confirm.setEnabled(true);
            GameFrame.switchTo("mainmenu");
        });
        t.setRepeats(false);
        t.start();
    }//GEN-LAST:event_confirm

    private void chickgirlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chickgirlActionPerformed
        // TODO add your handling code here: radio girl
    }//GEN-LAST:event_chickgirlActionPerformed

    private void editnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editnameActionPerformed
        // TODO add your handling code here: edit name
    }//GEN-LAST:event_editnameActionPerformed

    private void charecter4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter4ActionPerformed
        // TODO add your handling code here: charecter 4
    }//GEN-LAST:event_charecter4ActionPerformed

    private void charecter5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter5ActionPerformed
        // TODO add your handling code here: charecter 5
    }//GEN-LAST:event_charecter5ActionPerformed

    private void charecter6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter6ActionPerformed
        // TODO add your handling code here: charecter 6
    }//GEN-LAST:event_charecter6ActionPerformed

    private void cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel
        //รีเซ็ตกลับค่าเริ่มต้น: เพศเดิมที่เลือกอยู่ ไปที่ตัวแรกและใช้ชื่อตัวละครจาก csv
        if (characters.isEmpty()) {
            return;
        }
        charecter1.setSelected(true);
        selectCharacter(0, null);
    }//GEN-LAST:event_cancel

    private void chickboyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chickboyActionPerformed
        // TODO add your handling code here: radio boy
    }//GEN-LAST:event_chickboyActionPerformed

    private void charecter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter1ActionPerformed
        // TODO add your handling code here: charecter 1
    }//GEN-LAST:event_charecter1ActionPerformed

    private void charecter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter2ActionPerformed
        // TODO add your handling code here: charecter 2
    }//GEN-LAST:event_charecter2ActionPerformed

    private void charecter3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter3ActionPerformed
        // TODO add your handling code here: charecter 3
    }//GEN-LAST:event_charecter3ActionPerformed

    private void charecter7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter7ActionPerformed
        // TODO add your handling code here: charecter 7
    }//GEN-LAST:event_charecter7ActionPerformed

    private void charecter8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter8ActionPerformed
        // TODO add your handling code here: charecter 8
    }//GEN-LAST:event_charecter8ActionPerformed

    private void charecter9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charecter9ActionPerformed
        // TODO add your handling code here: charecter 9
    }//GEN-LAST:event_charecter9ActionPerformed

    private void backToMenu(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backToMenu
        // TODO add your handling code here:
        GameFrame.switchTo("mainmenu");
    }//GEN-LAST:event_backToMenu


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backToMenu;
    private javax.swing.JButton cancel;
    private javax.swing.JToggleButton charecter1;
    private javax.swing.JToggleButton charecter2;
    private javax.swing.JToggleButton charecter3;
    private javax.swing.JToggleButton charecter4;
    private javax.swing.JToggleButton charecter5;
    private javax.swing.JToggleButton charecter6;
    private javax.swing.JToggleButton charecter7;
    private javax.swing.JToggleButton charecter8;
    private javax.swing.JToggleButton charecter9;
    private javax.swing.JLabel charecterpic;
    private javax.swing.JRadioButton chickboy;
    private javax.swing.JRadioButton chickgirl;
    private javax.swing.JButton confirm;
    private javax.swing.JTextField editname;
    private javax.swing.JPanel framglass;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}


