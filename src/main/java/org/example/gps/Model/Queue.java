package org.example.gps.Model;
import java.util.ArrayList;

public class Queue {
    private ArrayList<Nodo> queueList;

    public Queue() {
        this.queueList = new ArrayList<>();
    }

    public void add(Nodo nodo) {
        if (nodo == null) {
            System.out.println("No se puede añadir un nodo nulo a la cola.");
            return;
        }
        queueList.add(nodo);
    }

    public void remove() {
        if (isEmpty()) {
            System.out.println("La cola está vacía. No se puede remover ningún elemento.");
            return;
        }
        int initialSize = queueList.size();
        System.out.println("Tamaño de la cola (inicio dequeue): " + initialSize);
        // En una cola, se remueve el primer elemento (FIFO)
        Nodo removedNodo = queueList.remove(0);
        System.out.println("Nodo removido: " + (removedNodo != null ? removedNodo.toString() : "null"));
        int finalSize = queueList.size();
        System.out.println("Tamaño de la cola (final dequeue): " + finalSize);
    }

    public Nodo peek() {
        if (isEmpty()) {
            System.out.println("La cola está vacía. No hay elemento para mostrar (peek).");
            return null;
        }
        return queueList.get(0);
    }

    public boolean isEmpty() {
        return queueList.isEmpty();
    }

    @Override
    public String toString() {
        int size = queueList.size();
        if (size == 0) {
            return "La cola está vacía.";
        }
        System.out.println("Elementos en la cola (del frente hacia el final):");
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            Nodo currentNodo = queueList.get(i);
            String nodoRepresentation = (currentNodo != null) ? currentNodo.toString() : "Nodo nulo";
            System.out.println(nodoRepresentation); // Imprime cada nodo en la consola
            sb.append(nodoRepresentation);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");

        return "Elementos mostrados en la terminal. Contenido: " + sb.toString();
    }
    //XD
}