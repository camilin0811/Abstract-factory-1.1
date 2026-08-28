package com.autoshop.mechanic;

import javax.swing.*;
import java.awt.*;

/** A JPanel painted as a rounded card, optionally with a thin border. */
class RoundedPanel extends JPanel {
    private final int radius;
    private final Color background;
    private final Color borderColor;

    RoundedPanel(int radius, Color background, Color borderColor) {
        this.radius = radius;
        this.background = background;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
