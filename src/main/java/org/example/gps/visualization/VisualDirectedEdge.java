package org.example.gps.visualization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class VisualDirectedEdge extends VisualEdge {
    private static final double ARROW_ANGLE_DEG = 25; // Ángulo de la punta de flecha en grados
    private static final double ARROW_LENGTH = 12;
    private Color arrowColor;

    public VisualDirectedEdge(VisualVertex v1, VisualVertex v2) {
        super(v1, v2);
        this.arrowColor = this.lineColor; // Por defecto, mismo color que la línea
    }

    public VisualDirectedEdge(VisualVertex v1, VisualVertex v2, double weight) {
        super(v1, v2, weight);
        this.arrowColor = this.lineColor;
    }

    @Override
    public void setLineColor(Color color) {
        super.setLineColor(color);
        this.arrowColor = color; // Sincronizar color de flecha
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (v1 == null || v2 == null) return;
        // Dibuja la línea base
        super.draw(gc); // Llama al draw de VisualEdge para la línea y el peso

        // Dibuja la flecha
        drawArrowHead(gc);
    }

    private void drawArrowHead(GraphicsContext gc) {
        double lineAngleRad = Math.atan2(v2.posY - v1.posY, v2.posX - v1.posX);

        // Calcular punto de intersección con el borde del círculo del vértice destino
        double intersectionX = v2.posX - VisualVertex.radius * Math.cos(lineAngleRad);
        double intersectionY = v2.posY - VisualVertex.radius * Math.sin(lineAngleRad);

        gc.setStroke(this.arrowColor);
        gc.setLineWidth(this.lineWidth);

        // Punto 1 de la flecha
        double angle1Rad = lineAngleRad - Math.PI + Math.toRadians(ARROW_ANGLE_DEG);
        double x1 = intersectionX + ARROW_LENGTH * Math.cos(angle1Rad);
        double y1 = intersectionY + ARROW_LENGTH * Math.sin(angle1Rad);
        gc.strokeLine(intersectionX, intersectionY, x1, y1);

        // Punto 2 de la flecha
        double angle2Rad = lineAngleRad - Math.PI - Math.toRadians(ARROW_ANGLE_DEG);
        double x2 = intersectionX + ARROW_LENGTH * Math.cos(angle2Rad);
        double y2 = intersectionY + ARROW_LENGTH * Math.sin(angle2Rad);
        gc.strokeLine(intersectionX, intersectionY, x2, y2);
    }
}