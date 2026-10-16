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

    private Component hover;
    private Component pressTarget;

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

    public static void showDialog(javax.swing.JDialog dialog, JComponent content, Component anchor) {
        Dimension d = content.getPreferredSize();
        dialog.add(new ScaledContainer(content, d.width, d.height));
        Window owner = SwingUtilities.getWindowAncestor(anchor);
        if (owner instanceof javax.swing.RootPaneContainer rpc) {
            Container cp = rpc.getContentPane();
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

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(designW, designH);
    }

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

    private static final String BACKDROP = "ScaledContainer.backdrop";

    public static void useAsBackdrop(JComponent screen, javax.swing.JLabel background) {
        background.setVisible(false);
        screen.setOpaque(false);
        screen.putClientProperty(BACKDROP, background);
    }

    private javax.swing.Icon currentBackdrop() {
        Component screen = content;
        if (content.getClientProperty(BACKDROP) == null) {
            screen = null;
            for (Component c : content.getComponents()) {
                if (c.isVisible()) {
                    screen = c;
                    break;
                }
            }
        }
        if (screen instanceof JComponent jc && jc.getClientProperty(BACKDROP) instanceof javax.swing.JLabel bg) {
            return bg.getIcon();
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        javax.swing.Icon bg = currentBackdrop();
        if (bg == null || bg.getIconWidth() <= 0 || bg.getIconHeight() <= 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            double s = Math.max(getWidth() / (double) bg.getIconWidth(), getHeight() / (double) bg.getIconHeight());
            g2.translate((getWidth() - bg.getIconWidth() * s) / 2, (getHeight() - bg.getIconHeight() * s) / 2);
            g2.scale(s, s);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            bg.paintIcon(this, g2, 0, 0);
        } finally {
            g2.dispose();
        }
    }

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

    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Toolkit.getDefaultToolkit().addAWTEventListener(this,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void removeNotify() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        super.removeNotify();
    }

    @Override
    public void eventDispatched(AWTEvent ev) {
        if (!(ev instanceof MouseEvent e) || e instanceof MouseWheelEvent) {
            return;
        }
        if (!(e.getSource() instanceof Window w) || w != SwingUtilities.getWindowAncestor(this)) {
            return;
        }
        Point p = e.getLocationOnScreen();
        SwingUtilities.convertPointFromScreen(p, this);
        double s = scale();
        int cx = (int) Math.floor((p.x - offsetX()) / s);
        int cy = (int) Math.floor((p.y - offsetY()) / s);

        switch (e.getID()) {
            case MouseEvent.MOUSE_PRESSED -> {
                Component t = findTarget(cx, cy);
                updateHover(e, t, cx, cy);
                pressTarget = t;
                send(t, e, MouseEvent.MOUSE_PRESSED, cx, cy);
            }
            case MouseEvent.MOUSE_RELEASED -> {
                Component t = pressTarget != null ? pressTarget : findTarget(cx, cy);
                send(t, e, MouseEvent.MOUSE_RELEASED, cx, cy);
                int buttons = InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;
                if ((e.getModifiersEx() & buttons) == 0) {
                    pressTarget = null;
                }
                updateHover(e, findTarget(cx, cy), cx, cy);
            }
            case MouseEvent.MOUSE_DRAGGED -> send(pressTarget, e, MouseEvent.MOUSE_DRAGGED, cx, cy);
            case MouseEvent.MOUSE_CLICKED -> send(findTarget(cx, cy), e, MouseEvent.MOUSE_CLICKED, cx, cy);
            case MouseEvent.MOUSE_EXITED -> updateHover(e, null, cx, cy);
            default -> { // MOVED, ENTERED
                Component t = findTarget(cx, cy);
                updateHover(e, t, cx, cy);
                send(t, e, MouseEvent.MOUSE_MOVED, cx, cy);
            }
        }
    }

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

    private void updateHover(MouseEvent src, Component target, int cx, int cy) {
        if (target == hover) {
            return;
        }
        Component old = hover;
        hover = target;
        send(old, src, MouseEvent.MOUSE_EXITED, cx, cy);
        send(target, src, MouseEvent.MOUSE_ENTERED, cx, cy);
    }

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
