package org.example.gps.Model;

import org.example.gps.Utils.*;

import java.util.ArrayList;

public class Nodo {
    int id;
    private String nombre;
    DestinationTypes type;
    private double latitud;
    private double longitud;
    private double altura;
    ArrayList<Nodo> destino;
    private boolean visitado;  // visitado o activo

    public Nodo( int id,String nombre, DestinationTypes type, double latitud, double longitud, double altura) {
        this.nombre = nombre;
        this.id = id;
        this.type = type;
        this.latitud = latitud;
        this.longitud = longitud;
        this.destino = new ArrayList<Nodo>();
        this.visitado = false;
        this.altura = altura;
    }

    public Nodo() {
        destino = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Nodo> getDestino() {
        return destino;
    }

    public void pushDestino(Nodo destino) {
        this.destino.add(destino);
    }

    public DestinationTypes getType() {
        return type;
    }

    public void setType(DestinationTypes type) {this.type = type;}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean vas) {
        this.visitado = vas;
    }

    @Override
    public String toString() {
        return "Nodo{" +
                "nombre='" + nombre + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", altura=" + altura +
                ", visitado=" + visitado +
                '}';
    }
}