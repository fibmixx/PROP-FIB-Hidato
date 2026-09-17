package edu.upc.prop.clusterxx.presentation.paneles;

import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints;

public class TileButton extends JButton {
    /**
     * @brief Enum para representar los tipos de formas disponibles para el botón.
     */
    public enum ShapeType {SQUARE, TRIANGLE, HEXAGON}
    /**
     * @brief El tipo de forma del botón, determina cómo se dibuja y cómo se detectan los clicks.
     */
    private ShapeType shapeType;
    /**
     * @brief El polígono que representa la forma personalizada del botón, usado para dibujar y detectar clicks.
     */
    private Polygon polygon;
    /**
     * @brief La fila del botón en la cuadrícula, usada para determinar la orientación de los triángulos.
     */
    private int row;
    /**
     * @brief La columna del botón en la cuadrícula, usada para determinar la orientación de los triángulos.
     */
    private int col;

    /**
     * Creadora de TileButton.
     * @param text El texto a mostrar en el botón.
     * @param shape El tipo de forma del botón ("square", "triangle", "hexagon").
     * @param row La fila del botón en la cuadrícula.
     * @param col La columna del botón en la cuadrícula.
     */
    public TileButton(String text, String shape, int row, int col) {
        super(text);
        this.row = row;
        this.col = col;
        this.shapeType = parseShape(shape);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    // VIENE CON OTRO FORMATO DE LA ADJACENCIA
    /**
     * Parsea el string de forma a un ShapeType.
     * @param s El string que representa la forma.
     * @return El ShapeType correspondiente al string, o SQUARE por defecto.
     */
    private ShapeType parseShape(String s) {
        if (s == null) return ShapeType.SQUARE;
        switch (s.toLowerCase()) {
            case "triangle": return ShapeType.TRIANGLE;
            case "hexagon": return ShapeType.HEXAGON;
            default: return ShapeType.SQUARE;
        }
    }

    /**
     * Override del método paintComponent para dibujar el botón con la forma personalizada.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            int w = getWidth();
            int h = getHeight();

            int padding = 5;
            polygon = createPolygon(shapeType, padding, w, h, row, col);

            Color fill = getBackground();

            if (!isEnabled() && fill != Color.BLACK) fill = Color.ORANGE; // Los # siguen en negro, los fijados se ponen naranja

            g2.setColor(fill);
            g2.fill(polygon);

            g2.setColor(getForeground());
            g2.draw(polygon);

            // Poner texto centrado
            String text = getText();
            if (text != null && !text.isEmpty()) {
                FontMetrics fm = g2.getFontMetrics();
                Rectangle b = polygon.getBounds();
                int tx = b.x + (b.width - fm.stringWidth(text)) / 2;
                int ty = b.y + (b.height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, tx, ty);
            }
        } finally {
            g2.dispose();
        }
    }

    /**
     * Crea un polígono según el tipo de forma.
     * @param type El tipo de forma.
     * @param padding El padding alrededor del botón.
     * @param w El ancho del botón.
     * @param h El alto del botón.
     * @param row La fila del botón en la cuadrícula.
     * @param col La columna del botón en la cuadrícula.
     * @return El polígono creado.
     */
    private Polygon createPolygon(ShapeType type, int padding, int w, int h, int row, int col) {
        // El espacio útil para dibujar respetando el padding
        int availableWidth = w - (2 * padding);
        int availableHeight = h - (2 * padding);

        int left = padding;
        int top = padding;
        int right = padding + availableWidth;
        int bottom = padding + availableHeight;

        switch (type) {
            case TRIANGLE:
                if (((row + col) & 1) == 0) {
                    //Punta arriba
                    int[] xs = {left + availableWidth / 2, left, right};
                    int[] ys = {top, bottom, bottom};
                    return new Polygon(xs, ys, 3);
                } else {
                    // Punta abajo
                    int[] xs = {left, right, left + availableWidth / 2};
                    int[] ys = {top, top, bottom};
                    return new Polygon(xs, ys, 3);
                }

            case HEXAGON:
                // Centro local real del botón
                int cx = left + availableWidth / 2;
                int cy = top + availableHeight / 2;

                // El radio máximo para que no se salga nunca de los márgenes
                int rx = (int) (availableWidth * 0.5);
                int ry = (int) (availableHeight * 0.5);

                Polygon hex = new Polygon();
                // FORMA HEXAGONAL: 6 puntos a 60 grados de diferencia
                for (int i = 0; i < 6; i++) {
                    double angle = Math.toRadians(60 * i);
                    int px = cx + (int) (rx * Math.cos(angle));
                    int py = cy + (int) (ry * Math.sin(angle));
                    hex.addPoint(px, py);
                }
                return hex;

            case SQUARE:
            default:
                return new Polygon(new int[]{left, right, right, left}, new int[]{top, top, bottom, bottom}, 4);
        }
    }

    /**
     * Override del método contains para que el botón solo responda a clicks dentro de su forma personalizada.
     */
    @Override
    public boolean contains(int x, int y) {
        if (polygon == null) {
            polygon = createPolygon(shapeType, 5, Math.max(10, getWidth()), Math.max(10, getHeight()), row, col);
        }
        return polygon.contains(x, y);
    }
}