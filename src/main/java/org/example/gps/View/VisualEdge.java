package org.example.gps.View;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class VisualEdge {
    public VisualVertex v1, v2;
    public boolean isWeighted; // Puedes usar esto para mostrar pesos si lo deseas
    public double weight;
    protected Color lineColor = Color.rgb(150, 150, 150); // Gris más claro
    protected Color originalLineColor = Color.rgb(150, 150, 150);
    protected double lineWidth = 2.0;

    public VisualEdge(VisualVertex v1, VisualVertex v2) {
        this.v1 = v1;
        this.v2 = v2;
        this.isWeighted = false;
        this.originalLineColor = this.lineColor;
    }

    public VisualEdge(VisualVertex v1, VisualVertex v2, double weight) {
        this(v1, v2);
        this.weight = weight;
        this.isWeighted = true;
    }

    public void setLineColor(Color color) {
        this.lineColor = color;
    }

    public void setDefaultLineColor() {
        this.lineColor = this.originalLineColor;
    }

    public void setLineWidth(double width) {
        this.lineWidth = width;
    }

    public String toString() {
        String s = v1.getId() + "-" + v2.getId();
        if (isWeighted) {
            s += "   w: " + String.format("%.2f", weight);
        }
        return s;
    }

    public void draw(GraphicsContext gc) {
        if (v1 == null || v2 == null) return; // Seguridad
        gc.setStroke(lineColor);
        gc.setLineWidth(lineWidth);
        gc.strokeLine(v1.posX, v1.posY, v2.posX, v2.posY);

        // Opcional: dibujar el peso
        // if (isWeighted) {
        //     gc.setFill(Color.LIGHTSALMON);
        //     gc.fillText(String.format("%.1f", weight), (v1.posX + v2.posX) / 2, (v1.posY + v2.posY) / 2 - 7);
        // }
    }
}