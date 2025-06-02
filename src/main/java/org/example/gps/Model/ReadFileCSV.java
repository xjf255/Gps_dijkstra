package org.example.gps.Model;

import org.example.gps.Utils.DestinationTypes;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ReadFileCSV {
    private Map<Integer, Nodo> mapitaNodo = new HashMap<>();

    //Para leer CSV de Nodos
    public Map<Integer, Nodo> readCSVNodo(File archive) {
        try(BufferedReader br = new BufferedReader(new FileReader(archive))) {
            String line;
            br.readLine(); // Saltar encabezados

            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                int id = Integer.parseInt(split[0]);
                String name = split[1];
                DestinationTypes type = DestinationTypes.valueOf(split[2].toUpperCase());
                double latitud = Double.parseDouble(split[3]);
                double longitud = Double.parseDouble(split[4]);
                double altura = Double.parseDouble(split[5]);

                Nodo nodo = new Nodo(id, name, type, latitud, longitud, altura);
                mapitaNodo.put(id, nodo);
            }
            return mapitaNodo;
        } catch (Exception e) {
            System.out.println("[ERROR]:" + e.getMessage());
        }
        return null;
    }

    //Para los CSV de Adyacencias
    public Map<Integer, Nodo> readCSVAdyacencia(File archive) {
        try(BufferedReader br = new BufferedReader(new FileReader(archive))) {
            String line;
            br.readLine(); // Saltar encabezados

            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                int origenId = Integer.parseInt(split[0]);
                int destinoId = Integer.parseInt(split[1]);

                Nodo origen = mapitaNodo.get(origenId);
                Nodo destino = mapitaNodo.get(destinoId);

                if (origen != null && destino != null) {
                    origen.pushDestino(destino);
                }
            }
            return mapitaNodo;
        } catch (Exception e) {
            System.out.println("[ERROR]:" + e.getMessage());
        }
        return null;
    }
}