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

    public HashMap<Integer, Nodo> getMapNodo() {
        return this.mapNodo;
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

    public double getDistanceBetweenNodes(Nodo n1, Nodo n2) {
        if (n1 == null || n2 == null) {
            System.err.println("Error: Uno o ambos nodos son null en getDistanceBetweenNodes.");
            return 0.0; // O podrías lanzar una excepción
        }
        return getDistance(n1.getLatitud(), n1.getLongitud(), n2.getLatitud(), n2.getLongitud());
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
        // Asegurarse de que node no sea null antes de llamar a getType()
        if (node == null) return 1.0;
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
        // Asegurarse de que n1 y n2 no sean null
        if (n1 == null || n2 == null) {
            System.err.println("Error: Uno o ambos nodos son null en getTimeBetweenNodes.");
            return Double.MAX_VALUE; // O manejar el error de otra forma
        }
        double distance = getDistance(n1.getLatitud(), n1.getLongitud(), n2.getLatitud(), n2.getLongitud());
        double effectiveSpeed = getSpeed(baseSpeed, hour, n1); // El tráfico se basa en el nodo de origen
        if (effectiveSpeed == 0) return Double.MAX_VALUE; // Evitar división por cero
        return distance / effectiveSpeed;
    }

    public double getTimeBetweenNodesInMinutes(Nodo n1, Nodo n2, double baseSpeed, int currentHour) {
        return getTimeBetweenNodes(n1, n2, currentHour,baseSpeed) * 60.0;
    }

    public List<Nodo> dijkstraResolution(int start, int end, double baseSpeed, int hour) {
        if (mapNodo == null || mapNodo.isEmpty()) {
            System.out.println("Error: El grafo (mapNodo) no está cargado. Por favor, carga los nodos primero.");
            return new ArrayList<>();
        }

        if (!mapNodo.containsKey(start) || !mapNodo.containsKey(end)) {
            System.out.println("Error: El nodo de inicio o fin no se encuentra en el grafo. Inicio: " + start + ", Fin: " + end);
            return new ArrayList<>();
        }

        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Nodo> previous = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distance));
        Set<Integer> visited = new HashSet<>();

        for (Integer nodeId : mapNodo.keySet()) {
            distances.put(nodeId, Double.MAX_VALUE);
        }

        distances.put(start, 0.0);
        // Asegurarse de que mapNodo.get(start) no sea null
        Nodo startNode = mapNodo.get(start);
        if (startNode == null) {
            System.out.println("Error: El nodo de inicio con ID " + start + " es null en el mapa.");
            return new ArrayList<>();
        }
        pq.offer(new NodeDistance(startNode, 0.0));


        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Nodo currentNode = current.node;

            // Verificación adicional de nulidad
            if (currentNode == null) continue;
            int currentId = currentNode.getId();

            if (visited.contains(currentId)) {
                continue;
            }
            visited.add(currentId);

            if (currentId == end) {
                break;
            }

            ArrayList<Nodo> adjacencies = currentNode.getDestino();
            if (adjacencies != null) {
                for (Nodo neighbor : adjacencies) {
                    if (neighbor == null) continue; // Saltar vecinos nulos
                    int neighborId = neighbor.getId();

                    if (visited.contains(neighborId)) {
                        continue;
                    }

                    double travelTime = getTimeBetweenNodesInMinutes(currentNode, neighbor, baseSpeed, hour);
                    if (travelTime == Double.MAX_VALUE) continue; // No se puede viajar entre estos nodos

                    double newDistance = distances.get(currentId) + travelTime;

                    if (newDistance < distances.getOrDefault(neighborId, Double.MAX_VALUE)) {
                        distances.put(neighborId, newDistance);
                        previous.put(neighborId, currentNode);
                        pq.offer(new NodeDistance(neighbor, newDistance));
                    }
                }
            }
        }
        return reconstructPath(previous, start, end);
    }

    private List<Nodo> reconstructPath(Map<Integer, Nodo> previous, int start, int end) {
        List<Nodo> path = new LinkedList<>(); // Usar LinkedList para inserción eficiente al principio
        Integer currentId = end;

        while (currentId != null) {
            Nodo currentNode = mapNodo.get(currentId);
            if (currentNode == null) { // El ID no está en el mapa, ruta rota
                System.out.println("Error de reconstrucción: Nodo con ID " + currentId + " no encontrado.");
                return new ArrayList<>(); // Ruta inválida
            }
            path.add(0, currentNode); // Añadir al principio

            if (currentId.equals(start)) {
                break; // Hemos llegado al inicio
            }

            Nodo prevNode = previous.get(currentId);
            if (prevNode == null) {
                if (!currentId.equals(start)) { // Si no es el inicio y no hay previo, no hay ruta
                    System.out.println("No se encontró un camino completo entre los nodos " + start + " y " + end + ". Detenido en " + currentId);
                    return new ArrayList<>(); // Ruta incompleta
                }
                break; // Debería haber sido capturado por la condición anterior (currentId.equals(start))
            }
            currentId = prevNode.getId();
        }

        // Verificar si el primer nodo en el path es el nodo de inicio
        if (path.isEmpty() || path.get(0).getId() != start) {
            System.out.println("No se pudo reconstruir la ruta desde " + start + " hasta " + end + " (el camino no comienza en el nodo de inicio).");
            return new ArrayList<>();
        }

        return path;
    }

    public void findAndPrintShortestPath(int startId, int endId, double baseSpeed, int hour) {
        System.out.println("\n=== BUSCANDO RUTA MÁS CORTA ===");
        System.out.println("Desde: " + startId + " Hasta: " + endId);
        System.out.println("Velocidad Base: " + baseSpeed + " km/h, Hora: " + hour + ":00");

        List<Nodo> shortestPath = dijkstraResolution(startId, endId, baseSpeed, hour);

        if (shortestPath.isEmpty()) {
            System.out.println("¡No se encontró ruta entre los nodos!");
            return;
        }

        System.out.println("\n--- RUTA MÁS CORTA ENCONTRADA ---");
        double totalTime = 0.0;
        Nodo previousNodeInPath = null;

        for (int i = 0; i < shortestPath.size(); i++) {
            Nodo node = shortestPath.get(i);
            if (node == null) {
                System.out.println("Error: Nodo nulo encontrado en la ruta.");
                continue;
            }
            System.out.print((i + 1) + ". " + node.getNombre() +
                    " (ID: " + node.getId() +
                    ", Tipo: " + (node.getType() != null ? node.getType().toString() : "N/A") + ")");

            if (i > 0 && previousNodeInPath != null) {
                double segmentTime = getTimeBetweenNodesInMinutes(previousNodeInPath, node, baseSpeed, hour);
                totalTime += segmentTime;
                System.out.println("   → Tiempo desde anterior: " + String.format("%.2f", segmentTime) + " minutos");
            } else if (i == 0) {
                System.out.println(" (Inicio de la ruta)");
            }
            previousNodeInPath = node;
        }

        System.out.println("\nTiempo total de viaje estimado: " + String.format("%.2f", totalTime) + " minutos");
        System.out.println("=== FIN DE LA RUTA ===\n");
    }


    public void getInfoCSVNodo(File archive){
        mapNodo = mapBase.readCSVNodo(archive);
        if (mapNodo == null) {
            System.err.println("Error: mapNodo es null después de leer el CSV de nodos.");
            mapNodo = new HashMap<>(); // Inicializar para evitar NullPointerExceptions posteriores
        }
    }

    public void getInfoCSVAdyacencia(File archive){
        // Esta llamada asume que mapBase.readCSVAdyacencia MODIFICA el mapitaNodo
        // que fue previamente poblado por readCSVNodo y luego retorna esa misma instancia.
        // O, si ReadFileCSV crea una nueva instancia de mapitaNodo cada vez,
        // entonces necesitarías pasarle el mapNodo actual para que lo modifique.
        // La implementación actual de ReadFileCSV usa una instancia `mapitaNodo`
        // que se inicializa en su constructor. Si usas la misma instancia de `mapBase`, está bien.
        HashMap<Integer, Nodo> updatedMap = mapBase.readCSVAdyacencia(archive, this.mapNodo); // Modificado para pasar el mapa actual

        if (updatedMap != null) {
            this.mapNodo = updatedMap; // Asignar el mapa posiblemente actualizado
            // Solo para depuración, puedes quitarlo después
            // System.out.println("Nodos después de cargar adyacencias:");
            // for (Nodo node : mapNodo.values()) {
            //     System.out.println("Nodo ID: " + node.getId() + ", Destinos: " + (node.getDestino() != null ? node.getDestino().size() : "null"));
            // }
        } else {
            System.err.println("Error: readCSVAdyacencia retornó null.");
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
}