package org.example.gps.Model;

import org.example.gps.Utils.*;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class Graph {
    private HashMap<Integer, Nodo> mapNodo;
    private static final HashMap<String, Double> DAY_TRAFFIC_MULTIPLIER = new HashMap<>();
    ReadFileCSV mapBase = new ReadFileCSV();

    static {
        DAY_TRAFFIC_MULTIPLIER.put("MONDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("TUESDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("WEDNESDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("THURSDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("FRIDAY", 2.0);
        DAY_TRAFFIC_MULTIPLIER.put("SATURDAY", 1.0);
        DAY_TRAFFIC_MULTIPLIER.put("SUNDAY", 1.0);
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3; // metres
        double φ1 = lat1 * Math.PI / 180; // φ, λ in radians
        double φ2 = lat2 * Math.PI / 180;
        double Δφ = (lat2 - lat1) * Math.PI / 180;
        double Δλ = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (R * c) / 1000;
    }

    private String getDay() {
        LocalDate today = LocalDate.now();
        return today.getDayOfWeek().toString();
    }

    private double getHourTrafficMultiplier(double hour) {
        int h = (int) hour;

        if (h >= 1 && h <= 6) {
            return 1.0; // Low traffic
        } else if (h >= 7 && h <= 12) {
            return 1.5; // Moderate traffic
        } else if (h >= 13 && h <= 18) {
            return 2.0; // High traffic
        } else {
            return 1.0; // Moderate to low traffic
        }
    }

    private double getLocationTrafficMultiplier(Nodo node) {
        DestinationTypes nodeType = node.getType();

        if (nodeType == null) return 1.0;

        switch (nodeType) {
            case MALL:
                return 1.8;
            case SCHOOL:
                return 1.6;
            case HOSPITAL:
                return 1.4;
            default:
                return 1.0;
        }
    }

    public double getSpeed(double baseSpeed, double hour, Nodo location) {
        String day = getDay();
        double dayMultiplier = DAY_TRAFFIC_MULTIPLIER.getOrDefault(day, 1.0);
        double hourMultiplier = getHourTrafficMultiplier(hour);
        double locationMultiplier = getLocationTrafficMultiplier(location);
        double trafficFactor = dayMultiplier * hourMultiplier * locationMultiplier;
        return baseSpeed / trafficFactor;
    }

    public double getTimeBetweenNodes(Nodo n1, Nodo n2, double hour, double baseSpeed) {
        double distance = getDistance(n1.getLatitud(), n1.getLongitud(), n2.getLatitud(), n2.getLongitud());
        double effectiveSpeed = getSpeed(baseSpeed, hour, n1);
        return distance / effectiveSpeed;
    }

    public double getTimeBetweenNodesInMinutes(Nodo n1, Nodo n2, double baseSpeed, int currentHour) {
        return getTimeBetweenNodes(n1, n2, currentHour,baseSpeed) * 60.0;
    }

    public List<Nodo> dijkstraResolution(int start, int end, double baseSpeed, int hour) {
        // Verify mapNodo
        if (mapNodo == null || mapNodo.isEmpty()) {
            System.out.println("Error: Graph is not loaded. Please load nodes first");
            return new ArrayList<>();
        }

        // Verify Nodo start & Nodo end
        if (!mapNodo.containsKey(start) || !mapNodo.containsKey(end)) {
            System.out.println("Error: Start or end node not found in graph");
            return new ArrayList<>();
        }

        // to save all the distances traveled
        Map<Integer, Double> distances = new HashMap<>();
        // Previous node map for path reconstruction
        Map<Integer, Nodo> previous = new HashMap<>();
        // Priority queue to always process the closest unvisited node
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(
                Comparator.comparingDouble(nd -> nd.distance)
        );
        // Track visited nodes
        Set<Integer> visited = new HashSet<>();

        // Initialize distances
        for (Integer nodeId : mapNodo.keySet()) {
            distances.put(nodeId, Double.MAX_VALUE);
        }

        // Start node has distance 0
        distances.put(start, 0.0);
        pq.offer(new NodeDistance(mapNodo.get(start), 0.0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Nodo currentNode = current.node;
            int currentId = currentNode.getId();

            // Skip if already visited
            if (visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);

            // If we reached the destination, we can stop
            if (currentId == end) {
                break;
            }

            // Check all adjacent nodes
            ArrayList<Nodo> adjacencies = currentNode.getDestino();
            if (adjacencies != null) {
                for (Nodo neighbor : adjacencies) {
                    int neighborId = neighbor.getId();

                    if (visited.contains(neighborId)) {
                        continue;
                    }

                    // Calculate time
                    double travelTime = getTimeBetweenNodesInMinutes(
                            currentNode, neighbor, baseSpeed, hour
                    );
                    double newDistance = distances.get(currentId) + travelTime;

                    // If we found a shorter path, update it
                    if (newDistance < distances.get(neighborId)) {
                        distances.put(neighborId, newDistance);
                        previous.put(neighborId, currentNode);
                        pq.offer(new NodeDistance(neighbor, newDistance));
                    }
                }
            }
        }

        // Reconstruct path from end to start
        return reconstructPath(previous, start, end);
    }

    private List<Nodo> reconstructPath(Map<Integer, Nodo> previous, int start, int end) {
        List<Nodo> path = new ArrayList<>();
        Integer current = end;

        // Build path backwards from end to start
        while (current != null) {
            if (current == start) {
                path.add(mapNodo.get(start));
                break;
            }

            Nodo prevNode = previous.get(current);
            if (prevNode == null) {
                // No path exists
                System.out.println("No path found between nodes " + start + " and " + end);
                return new ArrayList<>();
            }

            path.add(mapNodo.get(current));
            current = prevNode.getId();
        }

        // Reverse to get path from start to end
        Collections.reverse(path);
        return path;
    }

    public void findAndPrintShortestPath(int startId, int endId, double baseSpeed, int hour) {
        System.out.println("\n=== FINDING SHORTEST PATH ===");
        System.out.println("From: " + startId + " To: " + endId);
        System.out.println("Base Speed: " + baseSpeed + " km/h, Hour: " + hour);

        List<Nodo> shortestPath = dijkstraResolution(startId, endId, baseSpeed, hour);

        if (shortestPath.isEmpty()) {
            System.out.println("No path found between the nodes!");
            return;
        }

        System.out.println("\n--- SHORTEST PATH FOUND ---");
        double totalTime = 0.0;

        for (int i = 0; i < shortestPath.size(); i++) {
            Nodo node = shortestPath.get(i);
            System.out.println((i + 1) + ". " + node.getNombre() +
                    " (ID: " + node.getId() +
                    ", Type: " + node.getType() + ")");

            // Calculate time to next node
            if (i < shortestPath.size() - 1) {
                Nodo nextNode = shortestPath.get(i + 1);
                double segmentTime = getTimeBetweenNodesInMinutes(node, nextNode, baseSpeed, hour);
                totalTime += segmentTime;
                System.out.println("   → Time to next: " + String.format("%.2f", segmentTime) + " minutes");
            }
        }

        System.out.println("\nTotal travel time: " + String.format("%.2f", totalTime) + " minutes");
        System.out.println("=== END PATH ===\n");
    }


    public void getInfoCSVNodo(File archive){
        /*
        1- Lee los CSV de Resources y guarda los datos en HashMaps a travez de la Clase "ReadFileCSV"
        2- Cada CSV se guarda en Hashmaps distintos
         */

        mapNodo = mapBase.readCSVNodo(archive);

        //Logica basica para mostrar la info en los HashMaps
        /*
        ReadFileCSV mapita = new ReadFileCSV();
        HashMap<Integer, Nodo> mapNodo = mapita.readCSVNodo();
        HashMap<Integer, Integer> mapAdyacencia = mapita.readCSVAdyacencia();

        System.out.println("[InfoMain001]"+ mapNodo.keySet()); //Este Muestra todos los Key del HashMap
        System.out.println("[InfoMain002]"+ mapNodo.values()); //Este Muestra todos los Values del HashMap

        System.out.println("[InfoMain003]"+mapAdyacencia.keySet()); //Lo mismo que arriba
        System.out.println("[InfoMain004]"+mapAdyacencia.values()); //Lo mismo que arriba
         */
    }

    public void getInfoCSVAdyacencia(File archive){
        mapNodo = mapBase.readCSVAdyacencia(archive);

        for (Nodo node : mapNodo.values()) {
            System.out.println(node.toString());
        }
    }

    public Graph clone(){
        Graph graphClone = new Graph();
        HashMap<Integer, Nodo> mapNodoClone = new HashMap<>();

        //Clonamos solo los nodos sin sus adyacencias
        for(Nodo original : mapNodo.values()){
            Nodo copia = new Nodo(
                    original.getId(),
                    original.getNombre(),
                    original.getType(),
                    original.getLatitud(),
                    original.getLongitud(),
                    original.getAltura()
            );
            mapNodoClone.put(copia.getId(),copia);
        }

        //Clonamos las adyacencias
        for(Nodo original : mapNodo.values()){
            Nodo nodeCloned = mapNodoClone.get(original.getId());

            for(Nodo destinoOriginal : original.getDestino()){
                Nodo destinoCloned = mapNodoClone.get(destinoOriginal.getId());

                if(destinoCloned != null){
                    nodeCloned.pushDestino(destinoCloned);
                }
            }
        }

        graphClone.mapNodo = mapNodoClone;

        return graphClone;
    }

    public HashMap<Integer, Nodo> getMapNodo(){
        return mapNodo;
    }
}