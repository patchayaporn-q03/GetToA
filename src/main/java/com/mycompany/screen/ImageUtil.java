package com.mycompany.screen;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/** ตัวช่วยโหลดรูปจาก resource แล้วย่อ/ขยายเป็นขนาดที่ต้องการอย่างคมชัด */
public final class ImageUtil {

    // คลาสตัวช่วยล้วน ๆ ไม่ให้สร้าง object
    private ImageUtil() {
    }

    /** โหลดรูปจาก classpath แล้วปรับเป็น w x h (รูปต้นฉบับเป็นขนาดใดก็ได้ เช่น 1920x1080) */
    public static ImageIcon scaledIcon(String resource, int w, int h) {
        try {
            BufferedImage src = ImageIO.read(ImageUtil.class.getResource(resource));
            // ย่อทีละครึ่งก่อนเพื่อไม่ให้ภาพหยาบ/ขอบแตกเมื่อย่อมาก
            int cw = src.getWidth();
            int ch = src.getHeight();
            BufferedImage cur = src;
            while (cw / 2 >= w && ch / 2 >= h) {
                cw /= 2;
                ch /= 2;
                cur = resize(cur, cw, ch);
            }
            return new ImageIcon(resize(cur, w, h));
        } catch (IOException ex) {
            throw new java.io.UncheckedIOException(ex);
        }
    }

    // วาดรูปลงภาพใหม่ขนาด w x h ด้วย bicubic (คุณภาพสูง)
    private static BufferedImage resize(BufferedImage src, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }
}
