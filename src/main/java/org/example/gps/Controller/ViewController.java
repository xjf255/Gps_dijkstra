package org.example.gps.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.gps.Model.Graph;
import org.example.gps.Model.Nodo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Added for stream operations

public class ViewController {
    private Graph graph = new Graph();

    @FXML private Label nodeFileLabel;
    @FXML private Label adjFileLabel;
    @FXML private TextField startNodeField;
    @FXML private TextField endNodeField;
    @FXML private TextField baseSpeedField;
    @FXML private TextField hourField;
    @FXML private TextArea resultArea;
    @FXML private TextArea graphArea;

    @FXML
    public void initialize() {
        // Initial welcome message and setup
        resultArea.setText("Bienvenido al Sistema GPS\n\n"
                + "1. Cargue primero el archivo de Nodos (CSV)\n"
                + "2. Luego cargue el archivo de Adyacencias (CSV)\n"
                + "3. Ingrese los parámetros y calcule la ruta\n");

        // Initialize labels
        nodeFileLabel.setText("No seleccionado");
        adjFileLabel.setText("No seleccionado");

        // Set default values for speed and hour if they aren't already set in FXML
        if (baseSpeedField.getText().isEmpty()) {
            baseSpeedField.setText("20");
        }
        if (hourField.getText().isEmpty()) {
            hourField.setText("4");
        }
    }

    @FXML
    protected void onLoadFile(ActionEvent event) {
        File selectedFile = showFileChooser(event, "Seleccionar archivo de nodos");
        if (selectedFile != null) {
            try {
                graph.getInfoCSVNodo(selectedFile);
                nodeFileLabel.setText(selectedFile.getName());
                appendResult("✓ Nodos cargados desde: " + selectedFile.getName());
                appendResult("   Total nodos cargados: " + graph.getNodeCount());
            } catch (Exception e) {
                appendResult("Error al cargar nodos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onLoadAdy(ActionEvent event) {
        File selectedFile = showFileChooser(event, "Seleccionar archivo de adyacencias");
        if (selectedFile != null) {
            try {
                graph.getInfoCSVAdyacencia(selectedFile);
                adjFileLabel.setText(selectedFile.getName());
                appendResult("✓ Adyacencias cargadas desde: " + selectedFile.getName());
                appendResult("   Relaciones establecidas correctamente");
            } catch (Exception e) {
                appendResult("Error al cargar adyacencias: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private File showFileChooser(ActionEvent event, String title) {
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow(); // Correct casting for Node
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        return fileChooser.showOpenDialog(primaryStage);
    }

    @FXML
    protected void onFindRuta() {
        // Clear previous error styles
        resetFieldStyles();
        resultArea.clear(); // Clear previous results for a fresh calculation

        try {
            // Validate file loading first
            if (nodeFileLabel.getText().equals("No seleccionado")) {
                appendResult("Error: Primero cargue el archivo de nodos.");
                return;
            }
            if (adjFileLabel.getText().equals("No seleccionado")) {
                appendResult("Error: Primero cargue el archivo de adyacencias.");
                return;
            }
            if (graph.getNodes().isEmpty()) {
                appendResult("Error: No se encontraron nodos en los archivos cargados.");
                return;
            }


            // --- Input Parsing and Validation ---
            int startNodeId;
            int endNodeId;
            double baseSpeed;
            int hour;

            try {
                startNodeId = Integer.parseInt(startNodeField.getText().trim());
            } catch (NumberFormatException e) {
                appendResult("Error: El ID del nodo de inicio debe ser un número entero.");
                startNodeField.setStyle("-fx-border-color: red;");
                return;
            }

            try {
                endNodeId = Integer.parseInt(endNodeField.getText().trim());
            } catch (NumberFormatException e) {
                appendResult("Error: El ID del nodo de fin debe ser un número entero.");
                endNodeField.setStyle("-fx-border-color: red;");
                return;
            }

            try {
                baseSpeed = Double.parseDouble(baseSpeedField.getText().trim());
                if (baseSpeed <= 0) {
                    appendResult("Error: La velocidad base debe ser un número positivo.");
                    baseSpeedField.setStyle("-fx-border-color: red;");
                    return;
                }
            } catch (NumberFormatException e) {
                appendResult("Error: La velocidad base debe ser un número válido.");
                baseSpeedField.setStyle("-fx-border-color: red;");
                return;
            }

            try {
                hour = Integer.parseInt(hourField.getText().trim());
                if (hour < 0 || hour > 23) {
                    appendResult("Error: La hora actual debe ser un número entre 0 y 23.");
                    hourField.setStyle("-fx-border-color: red;");
                    return;
                }
            } catch (NumberFormatException e) {
                appendResult("Error: La hora actual debe ser un número entero válido.");
                hourField.setStyle("-fx-border-color: red;");
                return;
            }

            // Validate if start and end nodes exist in the graph
            if (!graph.getNodes().containsKey(startNodeId)) {
                appendResult("Error: El nodo de inicio (ID: " + startNodeId + ") no existe.");
                startNodeField.setStyle("-fx-border-color: red;");
                return;
            }
            if (!graph.getNodes().containsKey(endNodeId)) {
                appendResult("Error: El nodo de fin (ID: " + endNodeId + ") no existe.");
                endNodeField.setStyle("-fx-border-color: red;");
                return;
            }
            if (startNodeId == endNodeId) {
                appendResult("Error: El nodo de inicio y el nodo de fin no pueden ser el mismo.");
                return;
            }


            appendResult("\n=== CALCULANDO RUTA MÁS CORTA ===");
            appendResult("Desde nodo: " + startNodeId + " (" + graph.getNodes().get(startNodeId).getNombre() + ") " +
                    "→ Hasta nodo: " + endNodeId + " (" + graph.getNodes().get(endNodeId).getNombre() + ")");
            appendResult("Velocidad base: " + baseSpeed + " km/h, Hora: " + hour + "h");

            // Calculate route
            List<Nodo> shortestPath = graph.dijkstraResolution(startNodeId, endNodeId, baseSpeed, hour);

            if (shortestPath == null || shortestPath.isEmpty()) {
                appendResult("✗ No se encontró ruta entre los nodos especificados.");
                return;
            }

            appendResult("\n✓ RUTA ENCONTRADA (" + shortestPath.size() + " nodos)");
            double totalTimeInMinutes = 0.0;

            // Display the path and calculate total time
            for (int i = 0; i < shortestPath.size(); i++) {
                Nodo currentNode = shortestPath.get(i);
                appendResult((i + 1) + ". " + currentNode.getNombre() +
                        " (ID: " + currentNode.getId() +
                        ", Tipo: " + (currentNode.getType() != null ? currentNode.getType().name() : "N/A") + ")"); // Handle null type gracefully

                if (i < shortestPath.size() - 1) {
                    Nodo nextNode = shortestPath.get(i + 1);
                    // It's crucial to check if there's an adjacency defined for getTimeBetweenNodesInMinutes
                    // The dijkstra should have already found a path, so this should generally exist.
                    // However, adding a check here can make it more robust if getTimeBetweenNodesInMinutes can fail silently.
                    double segmentTime = graph.getTimeBetweenNodesInMinutes(currentNode, nextNode, baseSpeed, hour);
                    totalTimeInMinutes += segmentTime;
                    appendResult("   → Tiempo al siguiente nodo: " +
                            String.format("%.2f", segmentTime) + " minutos");
                }
            }

            appendResult("\n--- RESUMEN ---");
            appendResult("Ruta: " + shortestPath.stream()
                    .map(Nodo::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(" -> ")));
            appendResult("⏱ Tiempo total de viaje estimado: " +
                    String.format("%.2f", totalTimeInMinutes) + " minutos");
            appendResult("=== FIN DE LA RUTA ===\n");

        } catch (Exception e) {
            // Catch any unexpected exceptions
            appendResult("Se produjo un error inesperado: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging purposes
        }
    }

    @FXML
    protected void onShowNodes() {
        graphArea.clear();
        Map<Integer, Nodo> nodes = graph.getNodes();

        if (nodes == null || nodes.isEmpty()) {
            graphArea.setText("No hay nodos cargados. Por favor, cargue primero el archivo de nodos.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Nodos cargados (").append(nodes.size()).append("):\n\n");

        for (Nodo node : nodes.values()) {
            sb.append("ID: ").append(node.getId())
                    .append(" | Nombre: ").append(node.getNombre())
                    .append(" | Tipo: ").append(node.getType() != null ? node.getType().name() : "N/A") // Handle null type
                    .append("\nCoordenadas: (Lat: ").append(node.getLatitud())
                    .append(", Lon: ").append(node.getLongitud())
                    .append(") | Altura: ").append(node.getAltura())
                    .append("m\n\n");
        }

        graphArea.setText(sb.toString());
    }

    // Helper method to append text to the result area
    private void appendResult(String text) {
        resultArea.appendText(text + "\n");
    }

    // Helper method to reset styling of input fields
    private void resetFieldStyles() {
        startNodeField.setStyle(null);
        endNodeField.setStyle(null);
        baseSpeedField.setStyle(null);
        hourField.setStyle(null);
    }
}