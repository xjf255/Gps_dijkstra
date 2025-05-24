package org.example.gps.Model;

import org.example.gps.Utils.DestinationTypes;

import java.util.ArrayList;

public class Nodo {
    private String nombre;
    int id;
    DestinationTypes type;
    private double latitud;
    private double longitud;
    private double altura;
    ArrayList<Nodo> destino;
    int peso; // creo que no nos servira
    private boolean visitado;  // visitado o activo
    private double x;
    private double y;

    public Nodo() {
        destino = new ArrayList<>();
    }

    //HACER METODO CALCULAR DISTANCIA
    public int compararnodo(Nodo otro) {
        return Integer.compare(this.peso, otro.peso);
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

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public DestinationTypes getType() {
        return type;
    }

    public void setType(DestinationTypes type) {
        this.type = type;
    }

    public int getPeso() {
        return peso;
    }

    public void setpeso(int peso) {
        this.peso = peso;
    }

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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Nodo{" +
                "nombre='" + nombre + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", altura=" + altura +
                ", vas=" + visitado +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}