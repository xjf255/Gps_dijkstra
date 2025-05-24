package org.example.gps;

public class Nodo {
    private String nombre;
    int id;
    int destino;
    int peso;
    private double latitud;
    private double longitud;
    private double altura;
    private boolean visitado;  // visitado o activo
    private double x;
    private double y;

    public Nodo(String nombre, double latitud, double longitud, double altura, boolean vas, double x, double y,int destino,int peso) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.altura = altura;
        this.visitado = vas;
        this.x = x;
        this.y = y;
        this.destino=destino;
        this.peso=peso;
    }
    //HACER METODO CALCULAR DISTANCIA
    public int compararnodo(Nodo otro){
        return Integer.compare(this.peso, otro.peso);
    }

    public enum Restaurantes {
        PolloCampero,
        McDonalds,
        BurgerKing,
        TacoBell,
        LaFondaDeLaCalleReal, // Ejemplo local de Antigua
        Welten, // Ejemplo local de Antigua
        Fridas
    }

    public enum universidades{
        umg, // Universidad Mariano Gálvez
        usac, // Universidad de San Carlos de Guatemala
        url, // Universidad Rafael Landívar
        uvg, // Universidad del Valle de Guatemala
        unphu // Si aplica para otro contexto (ejemplo genérico)
    }

    public enum hoteles{
        hotelAntigua,
        PortaHotelAntigua,
        HotelCaminoRealAntigua,
        HotelCasaSantoDomingo,
        ElConventoBoutiqueHotel,
        GoodHotelAntigua
    }

    public enum tiendas{
        LaTorre, // Supermercado
        Walmart,
        Cemaco, // Tienda por departamento
        Siman, // Tienda por departamento
        MaxiDespensa, // Supermercado
        TiendaDeBarrio, // Genérico para tiendas pequeñas
        FarmaciaGaleno, // Farmacia
        LibreriaProgreso // Librería
    }

    public int getdestino() {
        return destino;
    }

    public void setdestino(int destino) {
        this.destino = destino;
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