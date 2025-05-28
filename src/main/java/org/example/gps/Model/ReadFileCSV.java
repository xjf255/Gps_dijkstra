package org.example.gps.Model;

import org.example.gps.Utils.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadFileCSV {
    private Map<Integer, Nodo> mapitaNodo;
    private Map<Integer, Integer> mapitaAdyacencia;

    public ReadFileCSV(){
        this.mapitaNodo = new HashMap<>();
        this.mapitaAdyacencia = new HashMap<>();
    }

    //Para leer CSV de Nodos
    public HashMap<Integer,Nodo> readCSVNodo(){
        Nodo nodo;
        String pathArchive = "src/main/resources/mocks/nodos.csv";

         try(BufferedReader br = new BufferedReader(new FileReader(pathArchive))) {
             String line;

             //Para leer la primera linea (los encabezados vaya)
             br.readLine();

             //Empieza a leer linea por linea y las asigna al HashMap
             while ((line = br.readLine()) != null){
                 //System.out.println("[Info001]:"+line);
                 String[] split = line.split(",");
                 //System.out.println("ID:"+split[0]+", Nombre:"+split[1]+", Tipo:"+split[2]);
                 int id = Integer.parseInt(split[0]);
                 String name = split[1];
                 DestinationTypes type = DestinationTypes.valueOf(split[2].toUpperCase());
                 double latitud = Double.parseDouble(split[3]);
                 double longitud = Double.parseDouble(split[4]);
                 double altura = Double.parseDouble(split[5]);
                 //System.out.println("[Info002]"+id+", "+name+", "+type+", "+latitud+", "+longitud+", "+altura);
                 nodo = new Nodo(id,name,type,latitud,longitud,altura);
                 mapitaNodo.put(id,nodo);
             }
             return (HashMap<Integer, Nodo>) mapitaNodo;
         } catch (FileNotFoundException e) {
             System.out.println("[ERRORFILE001]:"+e);
         } catch (IOException e) {
             System.out.println("[ERRORIO001]"+e);
         } ;
         return null;
    }

    //Para los CSV de Adyacencias
    public HashMap<Integer, Integer> readCSVAdyacencia(){
        String pathArchive = "src/main/resources/mocks/adyacencias.csv";

        try(BufferedReader br = new BufferedReader(new FileReader(pathArchive))) {
            String line;

            //Para leer la primera linea (los encabezados vaya)
            br.readLine();

            //Empieza a leer linea por linea y guarda en HashMap
            while ((line = br.readLine()) != null){
                //System.out.println("[Info003]:"+line);
                String[] split = line.split(",");
                //System.out.println("Origen:"+split[0]+", Destino:"+split[1]);
                Nodo ogn = mapitaNodo.get(Integer.parseInt(split[0]));
                Nodo dst = mapitaNodo.get(Integer.parseInt(split[1]));
                ogn.pushDestino(dst);
                mapitaNodo.put(Integer.parseInt(split[0]),ogn);
                int origen = Integer.parseInt(split[0]);
                int destino = Integer.parseInt(split[1]);
                //System.out.println("[Info004]"+"Origen:"+origen+", Destino:"+destino);
                mapitaAdyacencia.put(origen, destino);
            }
            return (HashMap<Integer, Integer>) mapitaAdyacencia;
        } catch (FileNotFoundException e) {
            System.out.println("[ERRORFILE001]:"+e);
        } catch (IOException e) {
            System.out.println("[ERRORIO001]"+e);
        } ;
        return null;
    }
}
