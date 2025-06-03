package org.example.gps.View.animation;

import javafx.application.Platform; // <--- 1. AÑADE ESTE IMPORT
import javafx.scene.paint.Color;
import org.example.gps.View.GraphDisplay;
import org.example.gps.View.VisualEdge;
import org.example.gps.View.VisualVertex;

import java.util.ArrayList;
import java.util.List;

// 2. REVISA EL NOMBRE DE LA CLASE BASE: ¿Debería ser "BaseGraphAnimation"?
public class PathAnimation extends BasedGraphAnimation { // <--- Verifica que "BaseGraphAnimation" sea el nombre correcto de tu clase abstracta
    private List<VisualVertex> pathNodes;
    private List<VisualEdge> pathEdges;
    private Color nodeHighlightColor;
    private Color edgeHighlightColor;

    public PathAnimation(GraphDisplay graphDisplay, List<VisualVertex> pathNodes,
                         Color nodeHighlightColor, Color edgeHighlightColor, int delay) {
        // Asegúrate de que el constructor de la superclase coincida
        super(graphDisplay, "Path Highlighting");
        this.pathNodes = new ArrayList<>(pathNodes);
        this.nodeHighlightColor = nodeHighlightColor;
        this.edgeHighlightColor = edgeHighlightColor;
        setDelay(delay);
        this.pathEdges = findEdgesForPath(pathNodes);
    }

    private List<VisualEdge> findEdgesForPath(List<VisualVertex> nodesInPath) {
        List<VisualEdge> edgesInPath = new ArrayList<>();
        if (nodesInPath == null || nodesInPath.size() < 2 || graphDisplay == null) { // Añadida verificación de graphDisplay
            return edgesInPath;
        }

        for (int i = 0; i < nodesInPath.size() - 1; i++) {
            VisualVertex u = nodesInPath.get(i);
            VisualVertex v = nodesInPath.get(i + 1);

            if (u == null || v == null) continue;

            // Accede a edgeList a través de la instancia graphDisplay heredada
            for (VisualEdge edge : super.graphDisplay.edgeList) {
                if (edge.v1.getId() == u.getId() && edge.v2.getId() == v.getId()) {
                    edgesInPath.add(edge);
                    break;
                }
            }
        }
        return edgesInPath;
    }

    @Override
    public void playAnimation() throws InterruptedException {
        if (pathNodes == null || pathNodes.isEmpty() || graphDisplay == null) {
            return;
        }

        // 1. Resetear el estado visual del grafo (en el hilo de UI)
        Platform.runLater(() -> graphDisplay.resetVisualGraphState());
        delay(); // Pequeña pausa para ver el estado reseteado

        // 2. Animar nodos
        for (VisualVertex nodeInPath : pathNodes) { // Renombrada variable para claridad
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();

            // Obtener la instancia real del VisualVertex del GraphDisplay
            VisualVertex actualNode = graphDisplay.getVisualVertexById(nodeInPath.getId());
            if (actualNode != null) {
                Platform.runLater(() -> {
                    actualNode.setColor(nodeHighlightColor);
                    actualNode.setBorderColor(Color.ORANGE);
                    requestRedraw(); // Llama al método requestRedraw de la clase base
                });
            }
            delay();
        }

        // 3. Animar aristas
        for (VisualEdge edge : pathEdges) {
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();

            // La instancia 'edge' en pathEdges ya es la referencia correcta de graphDisplay.edgeList
            // si findEdgesForPath se implementó como te mostré.
            Platform.runLater(() -> {
                edge.setLineColor(edgeHighlightColor);
                edge.setLineWidth(3.5);
                requestRedraw(); // Llama al método requestRedraw de la clase base
            });
            delay();
        }

        // (Opcional) Dejar los nodos con un color de borde final
        Platform.runLater(() -> {
            for (VisualVertex nodeInPath : pathNodes) {
                VisualVertex actualNode = graphDisplay.getVisualVertexById(nodeInPath.getId());
                if (actualNode != null) {
                    actualNode.setBorderColor(Color.GOLD);
                }
            }
            requestRedraw(); // Llama al método requestRedraw de la clase base
        });
    }
}