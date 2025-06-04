package org.example.gps.Model;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmYen {
    private Graph graph;
    private List<Nodo> pathOne;
    private Map<Integer, List<Nodo>> mapPaths;

    //Se le mandan desde donde se quiere empezar, a donde se quiere llegar y el resto de esa informacion para poder calcular las posibles otras rutas
    public AlgorithmYen(){
        this.graph = new Graph();
        this.mapPaths = new HashMap<>();
    }

    public AlgorithmYen(HashMap<Integer, Nodo> mapNodo) {
        this.graph = new Graph();
        this.mapPaths = new HashMap<>();

        // Verificar que mapNodo no sea null antes de asignarlo
        if (mapNodo != null) {
            graph.setMapNodo(mapNodo);
        }
    }

    // Método para actualizar el grafo cuando se carguen los datos
    public void updateGraph(HashMap<Integer, Nodo> mapNodo) {
        if (mapNodo != null) {
            this.graph.setMapNodo(mapNodo);
        }
    }

    //Se le envia la cantidad de otras rutas se puede aplicar
    public Map<Integer,List<Nodo>> findOthersPaths(int start, int end, double speed, int hour){
        if (graph.getMapNodo() == null || graph.getMapNodo().isEmpty()) {
            System.err.println("[ERRORYEN00] El grafo no tiene datos cargados.");
            return new HashMap<>();
        }

        int x = 5;
        if(x == 1){
            System.out.println("[INFYEN06]Ingrese un numero igual o mayor a 2 para mostrarle rutas alternas");
            return null;
        }

        this.mapPaths.clear();

        this.pathOne = graph.dijkstraResolution(start,end,speed,hour);

        if (pathOne == null || pathOne.isEmpty()) {
            System.out.println("[ERRORYEN00] No se pudo encontrar la ruta principal.");
            return new HashMap<>();
        }

        mapPaths.put(1, pathOne);

        List<Nodo> previousPath = mapPaths.get(1);
        System.out.println("[INFYEN02]PreviousPath:"+previousPath);

        for(int i=2;i<=x;i++){
            // Verificar que previousPath no sea null o vacío
            if (previousPath == null || previousPath.size() < 2) {
                System.out.println("[ERRORYEN02] Ruta anterior inválida para continuar búsqueda.");
                break;
            }

            Graph graphClone = graph.clone();

            if (graphClone == null || graphClone.getMapNodo() == null) {
                System.out.println("[ERRORYEN03] Error al clonar el grafo.");
                break;
            }

            for(int j=0;j<previousPath.size()-1;j++){
                int idOrigin = previousPath.get(j).getId();
                int idDestino = previousPath.get(j+1).getId();

                Nodo nodoClone = graphClone.getMapNodo().get(idOrigin);
                if(nodoClone != null){
                    nodoClone.removeDestinoById(idDestino);
                }
            }

            List<Nodo> newPath = graphClone.dijkstraResolution(start,end,speed,hour);
             if(newPath ==null || newPath.isEmpty()){
                 System.out.println("[ERRORYEN01]No se encontraron mas caminos");
                 System.out.println("[INFYEN04]\n"+printAllPaths());
                 break;
             }

             System.out.println("[INFYEN03]PreviousPath:"+newPath);
             if(!isDuplicate(newPath)){
                 mapPaths.put(i,newPath);
                 previousPath = newPath;
                 System.out.println("[INFYEN05]\n"+printAllPaths());
             }
        }
        return mapPaths;
    }

    public String printAllPaths(){
        StringBuilder stringPaths = new StringBuilder();

        for(Map.Entry<Integer, List<Nodo>> entry : mapPaths.entrySet()){
            int pathNumber = entry.getKey();
            List<Nodo> path = entry.getValue();
            stringPaths.append("→ Camino #"+pathNumber+":\n");
            for(int i=0;i<path.size();i++){
                Nodo node = path.get(i);
                stringPaths.append(" ").append(i+1).append(". ")
                        .append(node.getNombre())
                        .append(" (ID: ").append(node.getId())
                        .append(", Tipo: ").append(node.getType())
                        .append(")\n");
            }
        }
        return stringPaths.toString();
    }

    public boolean isDuplicate(List<Nodo> path){
        for(List<Nodo> existPath : mapPaths.values()){
            if(existPath.size() != path.size()) continue;
            boolean same = true;
            for (int i=0;i<existPath.size();i++){
                if(existPath.get(i).getId() != path.get(i).getId()){
                    same = false;
                    break;
                }
            }
            if(same) return true;
        }
        return false;
    }
}
