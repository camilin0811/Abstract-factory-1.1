package com.autoshop.mechanic;

import javax.swing.*;
import java.awt.*;

/** A rounded, colored toggle button used for the vehicle-type selector. */
class PillToggleButton extends JToggleButton {
    private final Color selectedBg;
    private final Color selectedFg;
    private final Color idleFg;

    PillToggleButton(String text, Color selectedBg, Color selectedFg, Color idleFg) {
        super(text);
        this.selectedBg = selectedBg;
        this.selectedFg = selectedFg;
        this.idleFg = idleFg;
        setFont(getFont().deriveFont(Font.BOLD, 13f));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setForeground(idleFg);
        setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        addChangeListener(e -> setForeground(isSelected() ? selectedFg : idleFg));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isSelected() ? selectedBg : new Color(0xEE, 0xF1, 0xF5));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
