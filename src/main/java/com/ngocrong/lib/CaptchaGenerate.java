package com.ngocrong.lib;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

public class CaptchaGenerate {

    private CaptchaGenerate() {
    }

    /**
     * Generates a random alpha-numeric string of eight characters.
     *
     * @return random alpha-numeric string of eight characters.
     */
    public static String generateText() {
        return new StringTokenizer(UUID.randomUUID().toString(), "-").nextToken();
    }

    /**
     * Generates a PNG image of text 180 pixels wide, 40 pixels high with white
     * background.
     *
     * @param text expects string size eight (8) characters.
     * @return byte array that is a PNG image generated with text displayed.
     */
    public static byte[] generateImage(String text, byte zoomLevel) {
        int w = 90 * zoomLevel, h = 30 * zoomLevel;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Serif", Font.PLAIN, 20 * zoomLevel));
        g.setColor(Color.blue);
        int start = 10;
        byte[] bytes = text.getBytes();

        Random random = new Random();
        for (int i = 0; i < bytes.length; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawString(new String(new byte[]{bytes[i]}), (start + (i * 15)) * zoomLevel, (int) ((Math.random() * 15 + 15) * zoomLevel));
        }
        g.setColor(Color.white);
        g.setStroke(new BasicStroke(zoomLevel));
        int sOval = 30 * zoomLevel;
        for (int i = 0; i < 8; i++) {
            g.drawOval((int) ((Math.random() * 80) * zoomLevel), (int) ((Math.random() * 10) * zoomLevel), sOval, sOval);
        }
        g.dispose();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bout);
        } catch (Exception e) {
            
            throw new RuntimeException(e);
        }
        return bout.toByteArray();
    }
}
