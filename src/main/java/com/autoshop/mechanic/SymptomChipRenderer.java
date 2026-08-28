package com.autoshop.mechanic;

import javax.swing.*;
import java.awt.*;

/** Renders each symptom in the list as a small rounded chip row. */
class SymptomChipRenderer extends JLabel implements ListCellRenderer<String> {

    private boolean selected;

    SymptomChipRenderer() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        setFont(getFont().deriveFont(13f));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value,
            int index, boolean isSelected, boolean cellHasFocus) {
        setText("•  " + value);
        this.selected = isSelected;
        setForeground(isSelected ? Color.WHITE : new Color(0x1F, 0x25, 0x30));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(selected ? new Color(0x2D, 0x6C, 0xDF) : new Color(0xF3, 0xF5, 0xF8));
        g2.fillRoundRect(2, 1, getWidth() - 4, getHeight() - 3, 9, 9);
        g2.dispose();
        super.paintComponent(g);
    }
}
