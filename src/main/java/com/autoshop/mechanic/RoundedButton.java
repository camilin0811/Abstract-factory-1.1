package com.autoshop.mechanic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A rounded button with a hover highlight, used for the primary/secondary actions. */
class RoundedButton extends JButton {
    private Color base;
    private Color hover;
    private boolean hovering = false;

    RoundedButton(String text, Color base, Color hover, Color fg) {
        super(text);
        this.base = base;
        this.hover = hover;
        setForeground(fg);
        setFont(getFont().deriveFont(Font.BOLD, 13.5f));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }

    void setAccent(Color base, Color hover) {
        this.base = base;
        this.hover = hover;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = isEnabled() ? (hovering ? hover : base) : new Color(0xC7, 0xCC, 0xD1);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
