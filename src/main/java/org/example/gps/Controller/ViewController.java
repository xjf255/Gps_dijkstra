package org.example.gps.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.example.gps.Model.Graph;
import org.example.gps.Model.Nodo;
import org.example.gps.Utils.DestinationTypes;

import org.example.gps.visualization.GraphDisplay;
import org.example.gps.visualization.VisualVertex;
import org.example.gps.visualization.VisualDirectedEdge;
import org.example.gps.visualization.animation.AnimationController;
import org.example.gps.visualization.animation.PathAnimation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewController {
    Graph gpsGraphLogic = new Graph();

    @FXML
    private Pane graphDisplayPane;
    @FXML
    private TextField startNodeField;
    @FXML
    private TextField endNodeField;
    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label totalDistanceLabel;

    private GraphDisplay graphDisplay;
    private Map<Integer, VisualVertex> visualNodeMap = new HashMap<>();
    private AnimationController animationController;
    private Label tooltipLabel;

    private double minLat, maxLat, minLon, maxLon;
    private boolean geoBoundsInitialized = false;
    private static final double SCREEN_PADDING = 50;
    private boolean initialRenderDone = false;

    @FXML
    public void initialize() {
        this.graphDisplay = new GraphDisplay(true, false);
        this.animationController = new AnimationController();

        tooltipLabel = new Label();
        tooltipLabel.setStyle("-fx-background-color: rgba(245, 245, 245, 0.9); -fx-text-fill: black; -fx-padding: 6px; -fx-border-color: darkgrey; -fx-border-width: 1px; -fx-border-radius: 4px; -fx-background-radius: 4px;");
        tooltipLabel.setVisible(false);
        tooltipLabel.setMouseTransparent(true);

        graphDisplayPane.getChildren().addAll(graphDisplay.canvas, tooltipLabel);
        graphDisplay.setTooltipLabel(tooltipLabel);

        graphDisplay.canvas.widthProperty().bind(graphDisplayPane.widthProperty());
        graphDisplay.canvas.heightProperty().bind(graphDisplayPane.heightProperty());

        Platform.runLater(() -> {
            if (graphDisplay.canvas.getWidth() > 0 && graphDisplay.canvas.getHeight() > 0) {
                if (isGraphDataLoaded()) {
                    geoBoundsInitialized = false; renderGraphFromModel();
                } else {
                    graphDisplay.draw();
                }
                initialRenderDone = true;
            }
        });

        graphDisplayPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0 && initialRenderDone && isGraphDataLoaded()) {
                geoBoundsInitialized = false; renderGraphFromModel();
            }
        });
        graphDisplayPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0 && initialRenderDone && isGraphDataLoaded()) {
                geoBoundsInitialized = false; renderGraphFromModel();
            }
        });

        if (totalTimeLabel != null) totalTimeLabel.setText("Tiempo Total: -- min");
        if (totalDistanceLabel != null) totalDistanceLabel.setText("Distancia Total: -- km");
    }

    private boolean isGraphDataLoaded() { return gpsGraphLogic.getMapNodo() != null && !gpsGraphLogic.getMapNodo().isEmpty(); }

    private void calculateGeoBounds() {
        if (!isGraphDataLoaded()) {
            geoBoundsInitialized = false; minLat = 14.5; maxLat = 14.7; minLon = -90.8; maxLon = -90.4; return;
        }
        List<Double> validLats = new ArrayList<>(); List<Double> validLons = new ArrayList<>();
        for (Nodo n : gpsGraphLogic.getMapNodo().values()) {
            boolean isLatValid = (n.getLatitud() >= 13.0 && n.getLatitud() <= 18.0);
            boolean isLonValid = (n.getLongitud() >= -92.0 && n.getLongitud() <= -88.0);
            if (isLatValid && isLonValid) { validLats.add(n.getLatitud()); validLons.add(n.getLongitud()); }
        }
        if (validLats.isEmpty()) { minLat = 14.5; maxLat = 14.7; minLon = -90.8; maxLon = -90.4;}
        else {
            minLat = Collections.min(validLats); maxLat = Collections.max(validLats);
            minLon = Collections.min(validLons); maxLon = Collections.max(validLons);
            if (minLat == maxLat) { maxLat += 0.001; minLat -= 0.001; }
            if (minLon == maxLon) { maxLon += 0.001; minLon -= 0.001; }
        }
        geoBoundsInitialized = true;
        System.out.println(String.format("GeoBounds: Lat[%.6f, %.6f], Lon[%.6f, %.6f]", minLat, maxLat, minLon, maxLon));
    }

    private VisualVertex convertNodoToVisualVertex(Nodo nodo) {
        if (!geoBoundsInitialized) calculateGeoBounds();
        double canvasWidth = graphDisplay.canvas.getWidth(); double canvasHeight = graphDisplay.canvas.getHeight();
        double deltaLon = maxLon - minLon; double deltaLat = maxLat - minLat;
        VisualVertex vv;
        if (canvasWidth <= 0 || canvasHeight <= 0 || !geoBoundsInitialized || deltaLat == 0 || deltaLon == 0 ) {
            vv = new VisualVertex(nodo.getId(), nodo.getNombre(), nodo.getAltura(), nodo.getType());
            double effW = Math.max(100, canvasWidth - 2 * SCREEN_PADDING); double effH = Math.max(100, canvasHeight - 2 * SCREEN_PADDING);
            vv.setPos(SCREEN_PADDING + Math.random() * effW, SCREEN_PADDING + Math.random() * effH);
            return vv;
        }
        double targetWidth = canvasWidth - 2 * SCREEN_PADDING; double targetHeight = canvasHeight - 2 * SCREEN_PADDING;
        double screenX = SCREEN_PADDING + ((nodo.getLongitud() - minLon) / deltaLon) * targetWidth;
        double screenY = SCREEN_PADDING + ((maxLat - nodo.getLatitud()) / deltaLat) * targetHeight;
        vv = new VisualVertex(nodo.getId(), nodo.getNombre(), nodo.getAltura(), nodo.getType());
        vv.setPos(screenX, screenY);
        return vv;
    }

    private void renderGraphFromModel() {
        if (!isGraphDataLoaded()) { if (graphDisplay != null) graphDisplay.clearGraph(); return; }
        if (graphDisplay.canvas.getWidth() <= 0 || graphDisplay.canvas.getHeight() <= 0) { initialRenderDone = false; return; }
        graphDisplay.clearGraph(); visualNodeMap.clear(); geoBoundsInitialized = false; calculateGeoBounds();
        if (!geoBoundsInitialized) { System.err.println("Render: Falló geoBounds."); return; }
        for (Nodo nodo : gpsGraphLogic.getMapNodo().values()) {
            VisualVertex vv = convertNodoToVisualVertex(nodo);
            graphDisplay.addVisualVertex(vv); visualNodeMap.put(nodo.getId(), vv);
        }
        for (Nodo sourceNodo : gpsGraphLogic.getMapNodo().values()) {
            VisualVertex sourceVV = visualNodeMap.get(sourceNodo.getId());
            if (sourceNodo.getDestino() != null && sourceVV != null) {
                for (Nodo targetNodo : sourceNodo.getDestino()) {
                    VisualVertex targetVV = visualNodeMap.get(targetNodo.getId());
                    if (targetVV != null) { graphDisplay.addVisualEdge(new VisualDirectedEdge(sourceVV, targetVV)); }
                }
            }
        }
        graphDisplay.draw(); initialRenderDone = true;
    }

    @FXML
    protected void onLoadFile(ActionEvent event) {
        Stage ps = (Stage) ((Node)event.getSource()).getScene().getWindow(); FileChooser fc = new FileChooser();
        fc.setTitle("Nodos CSV"); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showOpenDialog(ps);
        if (file != null) {
            gpsGraphLogic.getInfoCSVNodo(file);
            initialRenderDone = false; Platform.runLater(() -> { if (graphDisplay.canvas.getWidth() > 0) renderGraphFromModel();});
        }
    }

    @FXML
    protected void onLoadAdy(ActionEvent event) {
        Stage ps = (Stage) ((Node)event.getSource()).getScene().getWindow(); FileChooser fc = new FileChooser();
        fc.setTitle("Adyacencias CSV"); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showOpenDialog(ps);
        if (file != null) {
            if (!isGraphDataLoaded()) { showAlert("Error", "Carga nodos primero."); return; }
            gpsGraphLogic.getInfoCSVAdyacencia(file);
            initialRenderDone = false; Platform.runLater(() -> { if (graphDisplay.canvas.getWidth() > 0) renderGraphFromModel();});
        }
    }

    @FXML
    protected void onFindRuta() {
        if (animationController.isAnimationRunning()) animationController.stopCurrentAnimation();
        if (startNodeField.getText().isEmpty() || endNodeField.getText().isEmpty()) {
            showAlert("Entrada", "Ingresa IDs inicio y fin.");
            if (totalTimeLabel != null) totalTimeLabel.setText("Tiempo Total: -- min");
            if (totalDistanceLabel != null) totalDistanceLabel.setText("Distancia Total: -- km");
            return;
        }
        try {
            int startId = Integer.parseInt(startNodeField.getText()); int endId = Integer.parseInt(endNodeField.getText());
            double baseSpeed = 60; int hour = 12;
            List<Nodo> path = gpsGraphLogic.dijkstraResolution(startId, endId, baseSpeed, hour);
            if (path == null || path.isEmpty()) {
                showAlert("Ruta", "No se encontró ruta."); gpsGraphLogic.findAndPrintShortestPath(startId, endId, baseSpeed, hour);
                if (totalTimeLabel != null) totalTimeLabel.setText("Tiempo Total: N/A");
                if (totalDistanceLabel != null) totalDistanceLabel.setText("Distancia Total: N/A");
                return;
            }
            double calcTime = 0, calcDist = 0;
            if (path.size() > 1) {
                for (int i=0; i<path.size()-1; i++) {
                    calcTime += gpsGraphLogic.getTimeBetweenNodesInMinutes(path.get(i), path.get(i+1), baseSpeed, hour);
                    calcDist += gpsGraphLogic.getDistanceBetweenNodes(path.get(i), path.get(i+1));
                }
            }
            if (totalTimeLabel != null) totalTimeLabel.setText(String.format("Tiempo: %.2f min", calcTime));
            if (totalDistanceLabel != null) totalDistanceLabel.setText(String.format("Distancia: %.2f km", calcDist));
            List<VisualVertex> visPath = new ArrayList<>();
            for(Nodo n : path) if(visualNodeMap.containsKey(n.getId())) visPath.add(visualNodeMap.get(n.getId()));
            if (!visPath.isEmpty()) {
                PathAnimation pa = new PathAnimation(graphDisplay, visPath, Color.LIMEGREEN, Color.CYAN, 300);
                setPathControlsDisabled(true);
                animationController.startAnimation(pa, () -> setPathControlsDisabled(false));
            }
            gpsGraphLogic.findAndPrintShortestPath(startId, endId, baseSpeed, hour);
        } catch (NumberFormatException e) { showAlert("Error", "IDs deben ser números."); setPathControlsDisabled(false);
        } catch (Exception e) { showAlert("Error", "Error: " + e.getMessage()); e.printStackTrace(); setPathControlsDisabled(false); }
    }

    private void setPathControlsDisabled(boolean disabled) { if (startNodeField != null) startNodeField.setDisable(disabled); if (endNodeField != null) endNodeField.setDisable(disabled); }
    private void showAlert(String title, String msg) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
    @FXML protected void onToggleVertexIds(ActionEvent event) { if (graphDisplay != null) { VisualVertex.displayId = !VisualVertex.displayId; graphDisplay.draw(); } }
    @FXML protected void onToggleVertexAltitude(ActionEvent event) { if (graphDisplay != null) { VisualVertex.displayAltitude = !VisualVertex.displayAltitude; graphDisplay.draw(); } }
}