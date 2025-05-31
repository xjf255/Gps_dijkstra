package org.example.gps.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.gps.Model.Graph;
import org.example.gps.Model.Nodo;
import org.example.gps.visualization.GraphDisplay;
import org.example.gps.visualization.VisualVertex;
import org.example.gps.visualization.VisualDirectedEdge;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewController {
    Graph gpsGraphLogic = new Graph();

    @FXML
    private Pane graphDisplayPane;
    private GraphDisplay graphDisplay;

    @FXML
    private TextField startNodeField;
    @FXML
    private TextField endNodeField;

    private Map<Integer, VisualVertex> visualNodeMap = new HashMap<>();

    private double minLat, maxLat, minLon, maxLon;
    private boolean geoBoundsInitialized = false;
    private static final double SCREEN_PADDING = 40; // Aumentado para más margen

    @FXML
    public void initialize() {
        this.graphDisplay = new GraphDisplay(true, false); // Grafo es dirigido

        graphDisplay.canvas.widthProperty().bind(graphDisplayPane.widthProperty());
        graphDisplay.canvas.heightProperty().bind(graphDisplayPane.heightProperty());

        graphDisplayPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (graphDisplay.canvas.getWidth() > 0 && isGraphDataLoaded()) {
                geoBoundsInitialized = false;
                renderGraphFromModel();
            }
        });
        graphDisplayPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (graphDisplay.canvas.getHeight() > 0 && isGraphDataLoaded()) {
                geoBoundsInitialized = false;
                renderGraphFromModel();
            }
        });

        this.graphDisplayPane.getChildren().add(graphDisplay.canvas);
        graphDisplay.draw();
    }

    private boolean isGraphDataLoaded() {
        return gpsGraphLogic.getMapNodo() != null && !gpsGraphLogic.getMapNodo().isEmpty();
    }

    private void calculateGeoBounds() {
        if (!isGraphDataLoaded()) {
            geoBoundsInitialized = false;
            return;
        }
        minLat = Double.MAX_VALUE; maxLat = Double.MIN_VALUE;
        minLon = Double.MAX_VALUE; maxLon = Double.MIN_VALUE;

        for (Nodo n : gpsGraphLogic.getMapNodo().values()) {
            if (n.getLatitud() != 0 || n.getLongitud() != 0) { // Considerar solo nodos con coordenadas válidas
                minLat = Math.min(minLat, n.getLatitud());
                maxLat = Math.max(maxLat, n.getLatitud());
                minLon = Math.min(minLon, n.getLongitud());
                maxLon = Math.max(maxLon, n.getLongitud());
            }
        }
        // Si todos los nodos tienen lat/lon 0 o no hay nodos con coordenadas válidas
        if (minLat == Double.MAX_VALUE) {
            minLat = -0.001; maxLat = 0.001; // Pequeño rango por defecto alrededor de 0
            minLon = -0.001; maxLon = 0.001;
        }

        geoBoundsInitialized = true;
    }

    private VisualVertex convertNodoToVisualVertex(Nodo nodo) {
        if (!geoBoundsInitialized) {
            calculateGeoBounds();
        }
        // Si aún no está inicializado (p.ej. no hay nodos o todos son 0,0)
        if (!geoBoundsInitialized || (maxLat == minLat && maxLon == minLon) ) {
            // Colocar en el centro o posición aleatoria si no hay rango geo
            VisualVertex vvFallback = new VisualVertex(nodo.getId());
            double canvasWidth = graphDisplay.canvas.getWidth();
            double canvasHeight = graphDisplay.canvas.getHeight();
            vvFallback.setPos(
                    (canvasWidth > 0) ? canvasWidth / 2 + (Math.random() - 0.5) * 100 : SCREEN_PADDING,
                    (canvasHeight > 0) ? canvasHeight / 2 + (Math.random() - 0.5) * 100 : SCREEN_PADDING
            );
            return vvFallback;
        }


        double targetWidth = graphDisplay.canvas.getWidth() - 2 * SCREEN_PADDING;
        double targetHeight = graphDisplay.canvas.getHeight() - 2 * SCREEN_PADDING;

        if (targetWidth <= 0) targetWidth = 1;
        if (targetHeight <= 0) targetHeight = 1;

        double deltaLon = maxLon - minLon;
        double deltaLat = maxLat - minLat;

        double screenX, screenY;

        if (deltaLon == 0) {
            screenX = SCREEN_PADDING + targetWidth / 2; // Centrar si no hay variación de longitud
        } else {
            screenX = SCREEN_PADDING + ((nodo.getLongitud() - minLon) / deltaLon) * targetWidth;
        }

        if (deltaLat == 0) {
            screenY = SCREEN_PADDING + targetHeight / 2; // Centrar si no hay variación de latitud
        } else {
            screenY = SCREEN_PADDING + ((maxLat - nodo.getLatitud()) / deltaLat) * targetHeight; // (maxLat - lat) para invertir eje Y
        }

        VisualVertex vv = new VisualVertex(nodo.getId());
        vv.setPos(screenX, screenY);
        return vv;
    }

    private void renderGraphFromModel() {
        if (!isGraphDataLoaded() || graphDisplay.canvas.getWidth() <= 0 || graphDisplay.canvas.getHeight() <= 0) {
            graphDisplay.clearGraph();
            return;
        }

        graphDisplay.clearGraph();
        visualNodeMap.clear();
        geoBoundsInitialized = false; // Forzar recálculo de límites geográficos y posiciones

        // Calcular límites geográficos antes de convertir nodos
        calculateGeoBounds();

        for (Nodo nodo : gpsGraphLogic.getMapNodo().values()) {
            VisualVertex vv = convertNodoToVisualVertex(nodo);
            graphDisplay.addVisualVertex(vv);
            visualNodeMap.put(nodo.getId(), vv);
        }

        for (Nodo sourceNodo : gpsGraphLogic.getMapNodo().values()) {
            VisualVertex sourceVV = visualNodeMap.get(sourceNodo.getId());
            if (sourceNodo.getDestino() != null && sourceVV != null) {
                for (Nodo targetNodo : sourceNodo.getDestino()) {
                    VisualVertex targetVV = visualNodeMap.get(targetNodo.getId());
                    if (targetVV != null) {
                        graphDisplay.addVisualEdge(new VisualDirectedEdge(sourceVV, targetVV));
                    }
                }
            }
        }
        graphDisplay.draw();
    }

    @FXML
    protected void onLoadFile(ActionEvent event) {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo CSV de Nodos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            gpsGraphLogic.getInfoCSVNodo(selectedFile); // Carga nodos en tu modelo lógico
            System.out.println((gpsGraphLogic.getMapNodo() != null ? gpsGraphLogic.getMapNodo().size() : 0) + " nodos cargados en el modelo lógico.");

            // Renderizar si las adyacencias ya están cargadas, o solo los nodos.
            if (areAdjacenciesLoaded()) { // Comprueba si hay alguna arista en el modelo lógico
                renderGraphFromModel();
            } else { // Dibuja solo los nodos si no hay adyacencias aún
                graphDisplay.clearGraph();
                visualNodeMap.clear();
                geoBoundsInitialized = false; // Para recalcular límites
                calculateGeoBounds();
                if (isGraphDataLoaded()) {
                    for (Nodo nodo : gpsGraphLogic.getMapNodo().values()) {
                        VisualVertex vv = convertNodoToVisualVertex(nodo);
                        graphDisplay.addVisualVertex(vv);
                        visualNodeMap.put(nodo.getId(), vv);
                    }
                }
                graphDisplay.draw();
            }
        }
    }

    private boolean areAdjacenciesLoaded() {
        if (!isGraphDataLoaded()) return false;
        return gpsGraphLogic.getMapNodo().values().stream()
                .anyMatch(nodo -> nodo.getDestino() != null && !nodo.getDestino().isEmpty());
    }


    @FXML
    protected void onLoadAdy(ActionEvent event) {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo CSV de Adyacencias");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            if (!isGraphDataLoaded()) {
                showAlert("Error de Carga", "Por favor, carga primero el archivo de nodos.");
                return;
            }
            gpsGraphLogic.getInfoCSVAdyacencia(selectedFile);
            System.out.println("Adyacencias procesadas en el modelo lógico.");
            renderGraphFromModel(); // Ahora dibuja el grafo completo
        }
    }

    @FXML
    protected void onFindRuta() {
        graphDisplay.resetVisualGraphState();

        if (startNodeField.getText().isEmpty() || endNodeField.getText().isEmpty()) {
            showAlert("Entrada Inválida", "Ingresa IDs para nodo de inicio y fin.");
            return;
        }

        try {
            int startId = Integer.parseInt(startNodeField.getText());
            int endId = Integer.parseInt(endNodeField.getText());

            double baseSpeed = 60; // km/h (configurable si quieres)
            int hour = 12;       // 12 PM (configurable si quieres)

            List<Nodo> shortestPathNodos = gpsGraphLogic.dijkstraResolution(startId, endId, baseSpeed, hour);

            if (shortestPathNodos == null || shortestPathNodos.isEmpty()) {
                showAlert("Ruta no Encontrada", "No se encontró ruta entre nodo " + startId + " y nodo " + endId + ".");
                gpsGraphLogic.findAndPrintShortestPath(startId, endId, baseSpeed, hour); // Muestra mensaje de consola
                return;
            }

            List<VisualVertex> pathVisualVertices = new ArrayList<>();
            for(Nodo n : shortestPathNodos) {
                if(visualNodeMap.containsKey(n.getId())) {
                    pathVisualVertices.add(visualNodeMap.get(n.getId()));
                }
            }

            // Colores para el resaltado (puedes personalizarlos)
            graphDisplay.highlightPath(pathVisualVertices, Color.LIMEGREEN, Color.AQUA);

            gpsGraphLogic.findAndPrintShortestPath(startId, endId, baseSpeed, hour); // Imprime en consola

        } catch (NumberFormatException e) {
            showAlert("Error de Entrada", "Los IDs de los nodos deben ser números.");
        } catch (Exception e) {
            showAlert("Error Inesperado", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}