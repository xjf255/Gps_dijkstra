package org.example.gps;

public class Nodo {
    private String nombre;
    private double latitud;
    private double longitud;
    private double altura;
    private boolean visitado;  // visitado o activo
    private double x;
    private double y;

    public Nodo(String nombre, double latitud, double longitud, double altura, boolean vas, double x, double y) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.altura = altura;
        this.visitado = vas;
        this.x = x;
        this.y = y;
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
