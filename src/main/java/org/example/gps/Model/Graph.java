package org.example.gps.Model;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Graph {
    private Map<Integer, Nodo> mapitaNodo = new HashMap<>();

    public void getInfoCSVNodo(File archive) {
        ReadFileCSV reader = new ReadFileCSV();
        this.mapitaNodo = reader.readCSVNodo(archive);
    }

    public void getInfoCSVAdyacencia(File archive) {
        ReadFileCSV reader = new ReadFileCSV();
        this.mapitaNodo = reader.readCSVAdyacencia(archive);
    }

    public Map<Integer, Nodo> getNodes() {
        return mapitaNodo;
    }

    public int getNodeCount() {
        return mapitaNodo.size();
    }

    // Métodos de ejemplo (deben implementarse según tu lógica)
    public List<Nodo> dijkstraResolution(int start, int end, double baseSpeed, int hour) {
        // Implementación del algoritmo Dijkstra
        return null;
    }

    public double getTimeBetweenNodesInMinutes(Nodo node, Nodo nextNode, double baseSpeed, int hour) {
        // Cálculo del tiempo entre nodos
        return 0.0;
    }
}