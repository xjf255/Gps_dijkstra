package org.example.gps.Model;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmYen {
    private int start;
    private int end;
    private double speed;
    private int hour;
    private Graph graph;
    private List<Nodo> pathOne;
    private Map<Integer, List<Nodo>> mapPaths;

    //Para pruebas, luego se va sin ruta porque dentro de la clase GRAPH se llama a esta clase con los archivos ya cargados
    File nodos = new File("src/main/resources/mocks/nodos_grafo_umg_torre_tigo_PRUEBA.csv");
    File adyacencias = new File("src/main/resources/mocks/adyacencias_grafo_umg_torre_tigo_PRUEBA.csv");

    public AlgorithmYen(int start, int end, double speed, int hour){
        this.graph = new Graph();
        this.mapPaths = new HashMap<>();
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.hour = hour;

        //Para poder trabajar y hacer pruebas. Si se implementa dentro del graph, ya tendrian que estar cargados los archivos
        graph.getInfoCSVNodo(nodos);
        graph.getInfoCSVAdyacencia(adyacencias);

        //Se tendria que cambiar solo el destino que se le manda a dijktra si se cambia la lista de nodos
        //Solo para calcular la ruta mas corta por Dijkstra y guardarla en un Hashmap de las rutas posibles(kPaths)
        this.pathOne = graph.dijkstraResolution(start,end,speed,hour);
        mapPaths.put(1, pathOne);
    }

    public void findOthersPaths(int x){
        List<Nodo> previousPath = mapPaths.get(1);
        System.out.println("[INFYEN02]PreviousPath:"+previousPath);

        for(int i=2;i<=x;i++){
            System.out.println("DEBUG01: entra aqui?");
            Graph graphClone = graph.clone();
            //ver por que se pone .size()-1
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
             mapPaths.put(i,newPath);
             previousPath = newPath;
            System.out.println("[INFYEN05]\n"+printAllPaths());
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
}
