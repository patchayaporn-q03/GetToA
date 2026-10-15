package com.mycompany.screen;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * ครอบหน้าจอที่ออกแบบไว้ขนาดคงที่ (เช่น 1280x720) แล้วย่อ/ขยายตามขนาดหน้าต่าง
 * โดยคงสัดส่วนเดิม (เว้นขอบดำ) ไม่ต้องแก้โค้ดของแต่ละหน้าจอ:
 *  - วาด: scale Graphics ก่อนวาดลูก
 *  - repaint: ทุก repaint ของลูกถูกเปลี่ยนเป็นวาดทั้งตัวครอบ (ผ่าน {@link #install()})
 *  - เมาส์: แปลงพิกัดหน้าต่างกลับเป็นพิกัดดีไซน์แล้วส่งให้คอมโพเนนต์ที่ถูกต้องเอง
 */
public class ScaledContainer extends JPanel implements AWTEventListener {

    private final JComponent content;
    private final int designW;
    private final int designH;

    // คอมโพเนนต์ที่เมาส์อยู่ด้านบนตอนนี้ (ไว้ส่ง enter/exit)
    private Component hover;
    // คอมโพเนนต์ที่ถูกกดค้างไว้ (ลากเมาส์ต่อให้ตัวนี้แม้ออกนอกกรอบ จนกว่าจะปล่อย)
    private Component pressTarget;

    // content = หน้าจอจริงที่ออกแบบไว้ขนาด designW x designH; ตัวครอบนี้จะสเกลมันตามขนาดหน้าต่าง
    public ScaledContainer(JComponent content, int designW, int designH) {
        super(null);
        this.content = content;
        this.designW = designW;
        this.designH = designH;
        setOpaque(true);
        setBackground(Color.BLACK);
        add(content);
        content.setBounds(0, 0, designW, designH);
    }

    /** เรียกครั้งเดียวก่อนสร้างหน้าต่าง: ให้ repaint ของลูกทุกตัววาดตัวครอบใหม่ทั้งหมด */
    public static void install() {
        RepaintManager.setCurrentManager(new RepaintManager() {
            @Override
            public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
                Container p = SwingUtilities.getAncestorOfClass(ScaledContainer.class, c);
                if (p instanceof ScaledContainer sc && p != c) {
                    super.addDirtyRegion(sc, 0, 0, sc.getWidth(), sc.getHeight());
                } else {
                    super.addDirtyRegion(c, x, y, w, h);
                }
            }
        });
    }

    /**
     * แสดง popup แบบสเกลได้: ใส่ content (ขนาดดีไซน์ = preferred size ของมัน) ลงใน dialog
     * แล้วขยาย dialog ให้ทับพื้นที่หน้าจอเกมของหน้าต่างหลักพอดี (modal จึงบล็อกจนกว่าจะปิด)
     */
    public static void showDialog(javax.swing.JDialog dialog, JComponent content, Component anchor) {
        Dimension d = content.getPreferredSize();
        dialog.add(new ScaledContainer(content, d.width, d.height));
        Window owner = SwingUtilities.getWindowAncestor(anchor);
        if (owner instanceof javax.swing.RootPaneContainer rpc) {
            Container cp = rpc.getContentPane();
            // ขนาดพอดีกับสัดส่วนดีไซน์ จัดกึ่งกลางพื้นที่เกม (ไม่มีขอบดำให้ dialog ต้องวาดเอง)
            double s = Math.min(cp.getWidth() / (double) d.width, cp.getHeight() / (double) d.height);
            int w = (int) Math.round(d.width * s);
            int h = (int) Math.round(d.height * s);
            Point o = cp.getLocationOnScreen();
            dialog.setBounds(o.x + (cp.getWidth() - w) / 2, o.y + (cp.getHeight() - h) / 2, w, h);
        } else {
            dialog.pack();
            dialog.setLocationRelativeTo(anchor);
        }
        dialog.setVisible(true);
    }

    // ขนาดเริ่มต้นของหน้าต่าง = ขนาดที่ออกแบบไว้
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(designW, designH);
    }

    // ลูกมีขนาดคงที่ตามดีไซน์เสมอ (การย่อ/ขยายทำตอนวาด ไม่ใช่ตอนจัดวาง)
    @Override
    public void doLayout() {
        content.setBounds(0, 0, designW, designH);
    }

    // อัตราส่วนย่อ/ขยาย = ด้านที่แน่นกว่า เพื่อคงสัดส่วน
    private double scale() {
        return Math.min(getWidth() / (double) designW, getHeight() / (double) designH);
    }

    // ระยะเว้นขอบซ้าย (จัดภาพกึ่งกลางแนวนอน)
    private double offsetX() {
        return (getWidth() - designW * scale()) / 2;
    }

    // ระยะเว้นขอบบน (จัดภาพกึ่งกลางแนวตั้ง)
    private double offsetY() {
        return (getHeight() - designH * scale()) / 2;
    }

    // ระบายพื้นหลังสีดำทั้งหมด (ส่วนที่เป็นขอบเมื่อสัดส่วนหน้าต่างไม่ตรง)
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    // วาดลูกโดยย่อ/ขยายและเลื่อนตำแหน่งตาม scale()/offset ใช้ bilinear ให้ภาพเรียบ แล้วตัดไม่ให้วาดเกินพื้นที่ดีไซน์
    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.translate(offsetX(), offsetY());
            double s = scale();
            g2.scale(s, s);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.clipRect(0, 0, designW, designH);
            super.paintChildren(g2);
        } finally {
            g2.dispose();
        }
    }

    // ซ่อนลูกจากการหา target ของ Swing (พิกัดไม่ตรงกับที่ scale) แล้วจัดการเมาส์เองด้านล่าง
    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    // เริ่มรับเหตุการณ์เมาส์ระดับหน้าต่างเมื่อถูกแสดง
    @Override
    public void addNotify() {
        super.addNotify();
        Toolkit.getDefaultToolkit().addAWTEventListener(this,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    // เลิกรับเหตุการณ์เมาส์เมื่อถูกเอาออก (กัน listener ค้าง)
    @Override
    public void removeNotify() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        super.removeNotify();
    }

    // รับเมาส์ระดับหน้าต่าง แปลงพิกัดจอเป็นพิกัดดีไซน์ แล้วส่งให้คอมโพเนนต์ที่ถูกต้อง (press/release/drag/click/enter/exit)
    @Override
    public void eventDispatched(AWTEvent ev) {
        if (!(ev instanceof MouseEvent e) || e instanceof MouseWheelEvent) {
            return;
        }
        // รับเฉพาะเหตุการณ์ระดับหน้าต่าง (เพราะ contains() เป็น false Swing จึงไม่ส่งให้ลูกเอง)
        if (!(e.getSource() instanceof Window w) || w != SwingUtilities.getWindowAncestor(this)) {
            return;
        }
        // ตำแหน่งเมาส์บนจอ → พิกัดของตัวครอบ → พิกัดดีไซน์ (หักขอบดำแล้วหารด้วย scale)
        Point p = e.getLocationOnScreen();
        SwingUtilities.convertPointFromScreen(p, this);
        double s = scale();
        int cx = (int) Math.floor((p.x - offsetX()) / s);
        int cy = (int) Math.floor((p.y - offsetY()) / s);

        switch (e.getID()) {
            // กด: หา target, ย้าย hover, จำ pressTarget ไว้ให้ลากต่อได้ แล้วส่ง PRESSED
            case MouseEvent.MOUSE_PRESSED -> {
                Component t = findTarget(cx, cy);
                updateHover(e, t, cx, cy);
                pressTarget = t;
                send(t, e, MouseEvent.MOUSE_PRESSED, cx, cy);
            }
            // ปล่อย: ส่งให้ตัวที่ถูกกด (ถ้ามี) แล้วเคลียร์ pressTarget เมื่อปล่อยครบทุกปุ่ม
            case MouseEvent.MOUSE_RELEASED -> {
                Component t = pressTarget != null ? pressTarget : findTarget(cx, cy);
                send(t, e, MouseEvent.MOUSE_RELEASED, cx, cy);
                int buttons = InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;
                if ((e.getModifiersEx() & buttons) == 0) {
                    pressTarget = null;
                }
                updateHover(e, findTarget(cx, cy), cx, cy);
            }
            // ลาก: ส่งให้ตัวที่ถูกกดค้าง (แม้เมาส์ออกนอกกรอบของมัน)
            case MouseEvent.MOUSE_DRAGGED -> send(pressTarget, e, MouseEvent.MOUSE_DRAGGED, cx, cy);
            // คลิก (กด+ปล่อยที่เดิม): ส่งให้ตัวที่อยู่ใต้เมาส์
            case MouseEvent.MOUSE_CLICKED -> send(findTarget(cx, cy), e, MouseEvent.MOUSE_CLICKED, cx, cy);
            // เมาส์ออกจากหน้าต่าง: ไม่มี target แล้ว
            case MouseEvent.MOUSE_EXITED -> updateHover(e, null, cx, cy);
            // เมาส์เคลื่อนที่ / เข้าหน้าต่าง: อัปเดต hover แล้วส่ง MOVED
            default -> { // MOVED, ENTERED
                Component t = findTarget(cx, cy);
                updateHover(e, t, cx, cy);
                send(t, e, MouseEvent.MOUSE_MOVED, cx, cy);
            }
        }
    }

    // คอมโพเนนต์ลึกสุดที่ตำแหน่ง (พิกัดดีไซน์) และมี mouse listener (ไล่ขึ้นไปหาพ่อถ้าตัวนั้นไม่มี)
    private Component findTarget(int cx, int cy) {
        if (cx < 0 || cy < 0 || cx >= designW || cy >= designH) {
            return null;
        }
        Component c = SwingUtilities.getDeepestComponentAt(content, cx, cy);
        while (c != null && c.getMouseListeners().length == 0 && c.getMouseMotionListeners().length == 0) {
            if (c == content) {
                return null;
            }
            c = c.getParent();
        }
        return c;
    }

    // ถ้าเมาส์ย้ายไปอยู่บนคอมโพเนนต์ใหม่ ส่ง EXITED ให้ตัวเก่าและ ENTERED ให้ตัวใหม่
    private void updateHover(MouseEvent src, Component target, int cx, int cy) {
        if (target == hover) {
            return;
        }
        Component old = hover;
        hover = target;
        send(old, src, MouseEvent.MOUSE_EXITED, cx, cy);
        send(target, src, MouseEvent.MOUSE_ENTERED, cx, cy);
    }

    // สร้าง MouseEvent ใหม่ในพิกัดของ target (แปลงจากพิกัดดีไซน์) แล้วส่งให้ target
    private void send(Component target, MouseEvent src, int id, int cx, int cy) {
        if (target == null) {
            return;
        }
        Point p = SwingUtilities.convertPoint(content, cx, cy, target);
        target.dispatchEvent(new MouseEvent(target, id, src.getWhen(), src.getModifiersEx(),
                p.x, p.y, src.getXOnScreen(), src.getYOnScreen(),
                src.getClickCount(), src.isPopupTrigger(), src.getButton()));
    }
}
