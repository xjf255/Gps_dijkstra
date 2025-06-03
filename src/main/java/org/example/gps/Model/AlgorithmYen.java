package org.example.gps.Model;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.gps.Model.*;

public class AlgorithmYen {
    private int start;
    private int end;
    private double speed;
    private int hour;
    private Graph graph;
    private List<Nodo> pathOne;
    private Map<Integer, List<Nodo>> mapPaths;

    /*
    //Para pruebas, luego se va sin ruta porque dentro de la clase GRAPH se llama a esta clase con los archivos ya cargados
    File nodos = new File("src/main/resources/mocks/nodos_grafo_umg_torre_tigo_PRUEBA.csv");
    File adyacencias = new File("src/main/resources/mocks/adyacencias_grafo_umg_torre_tigo_PRUEBA.csv");
     */

    //Para cargar los nodos y adyacencias
    public void loadNodes(File file){
        graph.getInfoCSVNodo(file);
    }
    public void loadAdyacencias(File file){
        graph.getInfoCSVAdyacencia(file);
    }

    //Este constructor se tiene que llamar luego de cargar el recorrido (para cargar el recorrido se llama a los metodos de arriba)
    //Se le mandan desde donde se quiere empezar, a donde se quiere llegar y el resto de esa informacion para poder calcular las posibles otras rutas
    public AlgorithmYen(int start, int end, double speed, int hour){
        this.graph = new Graph();
        this.mapPaths = new HashMap<>();
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.hour = hour;

        /*
        //Para poder trabajar y hacer pruebas. Si se implementa dentro del graph, ya tendrian que estar cargados los archivos
        graph.getInfoCSVNodo(nodos);
        graph.getInfoCSVAdyacencia(adyacencias);
         */

        //Solo para calcular la ruta mas corta por Dijkstra y guardarla en un Hashmap de las rutas posibles(kPaths)
        this.pathOne = graph.dijkstraResolution(start,end,speed,hour);
        mapPaths.put(1, pathOne);
    }

    //Se le envia la cantidad de otras rutas se desean encontrar
    public void findOthersPaths(int x){
        if(x == 1){
            System.out.println("[INFYEN06]Ingrese un numero igual o mayor a 2 para mostrarle rutas alternas");
            return;
        }

        List<Nodo> previousPath = mapPaths.get(1);
        System.out.println("[INFYEN02]PreviousPath:"+previousPath);

        for(int i=2;i<=x;i++){
            //System.out.println("DEBUG01: entra aqui?");
            Graph graphClone = graph.clone();

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
