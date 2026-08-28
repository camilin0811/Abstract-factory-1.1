package com.autoshop.mechanic;

import javax.swing.*;
import java.awt.*;

/** A hand-painted confidence bar that changes color by risk band (red/amber/green). */
class ConfidenceGauge extends JComponent {
    private int value = 0;

    void setValue(int value) {
        this.value = Math.max(0, Math.min(100, value));
        repaint();
    }

    private Color colorFor(int v) {
        if (v >= 70) {
            return new Color(0x1F, 0x9D, 0x55);
        }
        if (v >= 40) {
            return new Color(0xE0, 0x9F, 0x1B);
        }
        return new Color(0xD1, 0x3B, 0x3B);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int h = getHeight();
        int barW = getWidth() - 48;

        g2.setColor(new Color(0xE7, 0xEA, 0xEE));
        g2.fillRoundRect(0, 0, barW, h, h, h);

        int fillW = (int) (barW * (value / 100.0));
        if (fillW > 0) {
            g2.setColor(colorFor(value));
            g2.fillRoundRect(0, 0, Math.max(fillW, h), h, h, h);
        }

        g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
        g2.setColor(colorFor(value));
        FontMetrics fm = g2.getFontMetrics();
        String text = value + "%";
        g2.drawString(text, barW + 8, (h + fm.getAscent()) / 2 - 2);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(220, 22);
    }
}
