package org.educa.ut3.juego.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CircularImageButton extends JButton {
    private final ImageIcon icon;

    public CircularImageButton(String imagePath) {
        icon = new ImageIcon(imagePath);

        setPreferredSize(new Dimension(100, 100));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int borderThickness = 5;

        if (getModel().isPressed()) {
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillOval(0, 0, getWidth(), getHeight());
        } else {
            g2d.setColor(getBackground());
            g2d.fillOval(0, 0, getWidth(), getHeight());
        }

        Ellipse2D.Double circle = new Ellipse2D.Double(borderThickness, borderThickness, getWidth() - 2 * borderThickness, getHeight() - 2 * borderThickness);
        g2d.setClip(circle);

        g2d.drawImage(icon.getImage(), borderThickness, borderThickness, getWidth() - 2 * borderThickness, getHeight() - 2 * borderThickness, null);

        g2d.setStroke(new BasicStroke(borderThickness));

        g2d.setColor(Color.BLACK);
        g2d.drawOval(borderThickness, borderThickness, getWidth() - 2 * borderThickness, getHeight() - 2 * borderThickness);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }
}