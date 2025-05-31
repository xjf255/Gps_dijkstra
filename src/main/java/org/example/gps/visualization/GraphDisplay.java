package org.example.gps.visualization;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.util.List;

public class GraphDisplay {
    public ObservableList<VisualEdge> edgeList = FXCollections.observableArrayList();
    public ObservableList<VisualVertex> vertexList = FXCollections.observableArrayList();
    public Canvas canvas;
    private GraphicsContext gc;
    private VisualVertex selectedVertex;

    private double scaleFactor = 1.0; // Para el zoom
    private double translateX = 0;    // Para panning
    private double translateY = 0;    // Para panning

    private Color backgroundColor = Color.rgb(40, 40, 40);
    public boolean isDirected; // Se establece en el constructor

    private Double lastPanMouseX, lastPanMouseY;

    public GraphDisplay(boolean isDirected, boolean isWeighted) { // isWeighted no se usa mucho aquí, pero se mantiene por si acaso
        this.isDirected = isDirected;
        this.canvas = new Canvas(); // El tamaño se enlazará desde ViewController
        this.gc = canvas.getGraphicsContext2D();

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnMouseMoved(this::handleMouseMoved);
        canvas.setOnScroll(this::handleScroll);

        // Listener para redibujar si el tamaño del canvas cambia
        canvas.widthProperty().addListener(obs -> draw());
        canvas.heightProperty().addListener(obs -> draw());

        draw(); // Dibujo inicial del fondo
    }

    public void clearGraph() {
        vertexList.clear();
        edgeList.clear();
        selectedVertex = null;
        translateX = 0;
        translateY = 0;
        scaleFactor = 1.0;
        draw();
    }

    public void addVisualVertex(VisualVertex v) {
        if (v != null && getVisualVertexById(v.getId()) == null) {
            vertexList.add(v);
        }
    }

    public void addVisualEdge(VisualEdge e) {
        if (e == null || e.v1 == null || e.v2 == null) return;
        boolean exists = edgeList.stream().anyMatch(existingEdge ->
                (existingEdge.v1.getId() == e.v1.getId() && existingEdge.v2.getId() == e.v2.getId()) ||
                        (!isDirected && existingEdge.v1.getId() == e.v2.getId() && existingEdge.v2.getId() == e.v1.getId())
        );
        if (!exists) {
            edgeList.add(e);
        }
    }

    public VisualVertex getVisualVertexById(int id) {
        for (VisualVertex v : vertexList) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    public void draw() {
        gc.save(); // Guardar estado actual del contexto

        // Limpiar canvas
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Aplicar transformaciones de pan y zoom
        gc.translate(translateX, translateY);
        gc.scale(scaleFactor, scaleFactor);

        // Dibujar aristas primero para que los vértices queden encima
        for (VisualEdge e : edgeList) {
            e.draw(gc);
        }
        for (VisualVertex v : vertexList) {
            v.draw(gc);
        }

        gc.restore(); // Restaurar el estado del contexto
    }

    private void handleMousePressed(MouseEvent event) {
        // Convertir coordenadas del evento al sistema de coordenadas del grafo (con pan y zoom)
        double graphX = (event.getX() - translateX) / scaleFactor;
        double graphY = (event.getY() - translateY) / scaleFactor;

        if (event.getButton() == MouseButton.PRIMARY) {
            selectedVertex = null;
            for (VisualVertex v : vertexList) {
                // Distancia al cuadrado para eficiencia
                double distSq = Math.pow(graphX - v.posX, 2) + Math.pow(graphY - v.posY, 2);
                if (distSq <= Math.pow(VisualVertex.radius, 2)) {
                    selectedVertex = v;
                    // Traer al frente (opcional, requiere reordenar la lista de dibujo)
                    break;
                }
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            lastPanMouseX = event.getX();
            lastPanMouseY = event.getY();
        }
        // No es necesario redibujar aquí, se hará en el drag o release si hay cambios
    }

    private void handleMouseDragged(MouseEvent event) {
        double graphX = (event.getX() - translateX) / scaleFactor;
        double graphY = (event.getY() - translateY) / scaleFactor;

        if (selectedVertex != null && event.getButton() == MouseButton.PRIMARY) {
            selectedVertex.setPos(graphX, graphY);
            draw();
        } else if (lastPanMouseX != null && event.getButton() == MouseButton.SECONDARY) {
            translateX += event.getX() - lastPanMouseX;
            translateY += event.getY() - lastPanMouseY;
            lastPanMouseX = event.getX();
            lastPanMouseY = event.getY();
            draw();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        // Si se estaba arrastrando un vértice, ya no está seleccionado para el próximo clic
        // selectedVertex = null; // Comentar esto permite que un vértice siga "seleccionado" conceptualmente

        if (event.getButton() == MouseButton.SECONDARY) {
            lastPanMouseX = null;
            lastPanMouseY = null;
        }
    }

    private void handleMouseMoved(MouseEvent event) {
        double graphX = (event.getX() - translateX) / scaleFactor;
        double graphY = (event.getY() - translateY) / scaleFactor;
        boolean onVertex = false;
        for (VisualVertex v : vertexList) {
            double distSq = Math.pow(graphX - v.posX, 2) + Math.pow(graphY - v.posY, 2);
            if (distSq <= Math.pow(VisualVertex.radius, 2)) {
                onVertex = true;
                break;
            }
        }
        canvas.setCursor(onVertex ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        double zoomIntensity = 0.05; // Controla cuánto zoom se aplica por scroll
        double zoomFactor = 1 + (delta > 0 ? zoomIntensity : -zoomIntensity);

        // Coordenadas del ratón en el canvas (punto de anclaje del zoom)
        double mouseX = event.getX();
        double mouseY = event.getY();

        // Actualizar transformaciones
        translateX = (translateX - mouseX) * zoomFactor + mouseX;
        translateY = (translateY - mouseY) * zoomFactor + mouseY;
        scaleFactor *= zoomFactor;

        // Opcional: Limitar el zoom
        // scaleFactor = Math.max(0.1, Math.min(scaleFactor, 10.0));

        draw();
    }

    public void resetVisualGraphState() {
        for (VisualVertex v : vertexList) {
            v.setDefaultColor();
            v.setBorderColor(Color.WHITE);
        }
        for (VisualEdge e : edgeList) {
            e.setDefaultLineColor();
            e.setLineWidth(2.0); // Ancho de línea por defecto
        }
        draw();
    }

    public void highlightPath(List<VisualVertex> pathNodes, Color nodeHighlightColor, Color edgeHighlightColor) {
        resetVisualGraphState(); // Limpia resaltados previos

        // Resaltar nodos
        for (VisualVertex pv : pathNodes) {
            VisualVertex actualNode = getVisualVertexById(pv.getId());
            if (actualNode != null) {
                actualNode.setColor(nodeHighlightColor);
                actualNode.setBorderColor(Color.GOLD);
            }
        }

        // Resaltar aristas
        if (pathNodes.size() > 1) {
            for (int i = 0; i < pathNodes.size() - 1; i++) {
                VisualVertex u = getVisualVertexById(pathNodes.get(i).getId());
                VisualVertex v = getVisualVertexById(pathNodes.get(i + 1).getId());

                if (u == null || v == null) continue;

                for (VisualEdge edge : edgeList) {
                    if (edge.v1.getId() == u.getId() && edge.v2.getId() == v.getId()) {
                        edge.setLineColor(edgeHighlightColor);
                        edge.setLineWidth(3.0); // Hacer la arista del camino más gruesa
                        break;
                    }
                }
            }
        }
        draw();
    }
}