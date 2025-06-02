package org.example.gps.visualization;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label; // Importar Label
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.util.List; // Para highlightPath

public class GraphDisplay {
    public ObservableList<VisualEdge> edgeList = FXCollections.observableArrayList();
    public ObservableList<VisualVertex> vertexList = FXCollections.observableArrayList();
    public Canvas canvas;
    private GraphicsContext gc;
    private VisualVertex selectedVertex; // Para arrastrar nodos
    private VisualVertex hoveredVertex = null; // NUEVO: Para saber qué nodo está bajo el cursor

    private double scaleFactor = 1.0;
    private double translateX = 0;
    private double translateY = 0;

    private Color backgroundColor = Color.rgb(40, 40, 40);
    public boolean isDirected;

    private Double lastPanMouseX, lastPanMouseY;

    private Label tooltipLabel; // NUEVO: Referencia al Label del tooltip

    public GraphDisplay(boolean isDirected, boolean isWeighted) {
        this.isDirected = isDirected;
        this.canvas = new Canvas();
        this.gc = canvas.getGraphicsContext2D();

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseMoved(this::handleMouseMoved); // Aquí manejaremos el hover
        canvas.setOnScroll(this::handleScroll);

        canvas.widthProperty().addListener(obs -> draw());
        canvas.heightProperty().addListener(obs -> draw());

        draw();
    }

    // NUEVO: Método para que ViewController pase el Label del tooltip
    public void setTooltipLabel(Label label) {
        this.tooltipLabel = label;
    }

    public void clearGraph() { /* ... como antes ... */ vertexList.clear(); edgeList.clear(); selectedVertex = null; translateX = 0; translateY = 0; scaleFactor = 1.0; if (tooltipLabel != null) tooltipLabel.setVisible(false); hoveredVertex = null; draw(); }
    public void addVisualVertex(VisualVertex v) { /* ... como antes ... */ if (v != null && getVisualVertexById(v.getId()) == null) vertexList.add(v); }
    public void addVisualEdge(VisualEdge e) { /* ... como antes ... */ if (e == null || e.v1 == null || e.v2 == null) return; boolean exists = edgeList.stream().anyMatch(exE -> (exE.v1.getId() == e.v1.getId() && exE.v2.getId() == e.v2.getId()) || (!isDirected && exE.v1.getId() == e.v2.getId() && exE.v2.getId() == e.v1.getId())); if (!exists) edgeList.add(e); }
    public VisualVertex getVisualVertexById(int id) { /* ... como antes ... */ for (VisualVertex v : vertexList) if (v.getId() == id) return v; return null; }
    public void draw() { /* ... como antes ... */ gc.save(); gc.setFill(backgroundColor); gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); gc.translate(translateX, translateY); gc.scale(scaleFactor, scaleFactor); for (VisualEdge e : edgeList) e.draw(gc); for (VisualVertex v : vertexList) v.draw(gc); gc.restore(); }
    private void handleMousePressed(MouseEvent event) { /* ... como antes ... */
        double graphX = (event.getX() - translateX) / scaleFactor; double graphY = (event.getY() - translateY) / scaleFactor;
        if (event.getButton() == MouseButton.PRIMARY) {
            selectedVertex = null;
            for (VisualVertex v : vertexList) {
                double distSq = Math.pow(graphX - v.posX, 2) + Math.pow(graphY - v.posY, 2);
                if (distSq <= Math.pow(VisualVertex.radius, 2)) { selectedVertex = v; break; }
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            lastPanMouseX = event.getX(); lastPanMouseY = event.getY();
        }
    }
    private void handleMouseDragged(MouseEvent event) { /* ... como antes ... */
        double graphX = (event.getX() - translateX) / scaleFactor; double graphY = (event.getY() - translateY) / scaleFactor;
        if (selectedVertex != null && event.getButton() == MouseButton.PRIMARY) {
            selectedVertex.setPos(graphX, graphY); draw();
        } else if (lastPanMouseX != null && event.getButton() == MouseButton.SECONDARY) {
            translateX += event.getX() - lastPanMouseX; translateY += event.getY() - lastPanMouseY;
            lastPanMouseX = event.getX(); lastPanMouseY = event.getY(); draw();
        }
    }
    private void handleMouseReleased(MouseEvent event) { /* ... como antes ... */ if (event.getButton() == MouseButton.SECONDARY) { lastPanMouseX = null; lastPanMouseY = null; } }
    private void handleScroll(ScrollEvent event) { /* ... como antes ... */
        double delta = event.getDeltaY(); double zoomIntensity = 0.05; double zoomFactor = 1 + (delta > 0 ? zoomIntensity : -zoomIntensity);
        double mouseX = event.getX(); double mouseY = event.getY();
        translateX = (translateX - mouseX) * zoomFactor + mouseX; translateY = (translateY - mouseY) * zoomFactor + mouseY;
        scaleFactor *= zoomFactor; draw();
    }
    public void resetVisualGraphState() { /* ... como antes ... */ for (VisualVertex v : vertexList) { v.setDefaultColor(); v.setBorderColor(Color.WHITE); } for (VisualEdge e : edgeList) { e.setDefaultLineColor(); e.setLineWidth(2.0); } draw(); }
    public void highlightPath(List<VisualVertex> pathNodes, Color nodeHighlightColor, Color edgeHighlightColor) { /* ... como antes ... */
        resetVisualGraphState();
        for (VisualVertex pv : pathNodes) { VisualVertex actualNode = getVisualVertexById(pv.getId()); if (actualNode != null) { actualNode.setColor(nodeHighlightColor); actualNode.setBorderColor(Color.GOLD); } }
        if (pathNodes.size() > 1) {
            for (int i = 0; i < pathNodes.size() - 1; i++) {
                VisualVertex u = getVisualVertexById(pathNodes.get(i).getId()); VisualVertex v = getVisualVertexById(pathNodes.get(i + 1).getId());
                if (u == null || v == null) continue;
                for (VisualEdge edge : edgeList) { if (edge.v1.getId() == u.getId() && edge.v2.getId() == v.getId()) { edge.setLineColor(edgeHighlightColor); edge.setLineWidth(3.0); break; } }
            }
        }
        draw();
    }


    // --- MÉTODO handleMouseMoved ACTUALIZADO ---
    private void handleMouseMoved(MouseEvent event) {
        if (tooltipLabel == null) return; // No hacer nada si no hay tooltipLabel configurado

        double eventX = event.getX();
        double eventY = event.getY();

        // Convertir coordenadas del evento del ratón (relativas al canvas)
        // a coordenadas del grafo (teniendo en cuenta pan y zoom)
        double graphX = (eventX - translateX) / scaleFactor;
        double graphY = (eventY - translateY) / scaleFactor;

        VisualVertex newlyHoveredVertex = null;
        for (VisualVertex v : vertexList) {
            // Distancia al cuadrado para eficiencia
            double distSq = Math.pow(graphX - v.posX, 2) + Math.pow(graphY - v.posY, 2);
            if (distSq <= Math.pow(VisualVertex.radius, 2)) {
                newlyHoveredVertex = v;
                break;
            }
        }

        if (newlyHoveredVertex != null) {
            if (newlyHoveredVertex != hoveredVertex) { // Si es un nuevo hover o se movió a otro nodo
                hoveredVertex = newlyHoveredVertex;
                tooltipLabel.setText(hoveredVertex.getTooltipText());
            }
            // Actualizar posición del tooltipLabel respecto al cursor en el Pane/Canvas
            // El +10,+10 es un pequeño offset para que no esté justo debajo del cursor
            tooltipLabel.setLayoutX(eventX + 10);
            tooltipLabel.setLayoutY(eventY + 10);

            // Asegurar que el tooltip no se salga de los bordes del Pane (graphDisplayPane)
            // Esto requiere acceso a las dimensiones del Pane. Es más simple si el tooltip es pequeño.
            // O puedes usar Tooltip de JavaFX que maneja esto automáticamente.
            // Por ahora, lo dejamos simple.

            tooltipLabel.setVisible(true);
            canvas.setCursor(Cursor.HAND);
        } else {
            if (hoveredVertex != null) { // Si antes había un hover pero ahora no
                tooltipLabel.setVisible(false);
            }
            hoveredVertex = null;
            canvas.setCursor(Cursor.DEFAULT);
        }
    }
}