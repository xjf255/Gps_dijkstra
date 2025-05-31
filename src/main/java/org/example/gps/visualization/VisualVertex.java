package org.example.gps.visualization;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class VisualVertex {
    private int id;
    public double posX;
    public double posY;
    public static double radius = 15;
    public static boolean displayId = true;
    private Color vColor = Color.rgb(80, 80, 80);
    private Color originalColor = Color.rgb(80, 80, 80);
    private Color borderColor = Color.WHITE;

    public VisualVertex(int id) {
        this.id = id;
        this.originalColor = vColor;
    }

    public void setColor(Color c) {
        vColor = c;
    }

    public void setDefaultColor() {
        vColor = originalColor;
    }

    public void setOriginalColor(Color color) {
        this.originalColor = color;
        if (this.vColor.equals(this.originalColor) || this.vColor == null) { // Only reset if it was the original color
            this.vColor = color;
        }
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

    public void setPos(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void draw(GraphicsContext gc) {
        gc.beginPath();
        gc.arc(posX, posY, radius, radius, 0, 360);
        gc.closePath();

        gc.setStroke(borderColor);
        gc.setLineWidth(2);
        gc.stroke();

        gc.setFill(vColor);
        gc.fill();

        if (displayId) {
            gc.setFill(Color.WHITE);
            gc.setTextBaseline(VPos.CENTER);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(id), posX, posY);
        }
    }

    // Sobrescribir equals y hashCode es buena práctica si los usas en colecciones como HashMaps
    // donde la identidad del objeto es importante más allá de la referencia.
    // Para este caso, la identidad por ID es suficiente para `visualNodeMap`.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisualVertex that = (VisualVertex) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}