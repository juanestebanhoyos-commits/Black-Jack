package com.example.blackjack.modelo;

/**
 * La mano de un participante. Las cartas se guardan en una lista enlazada
 * cada carta nueva se coloca al frente de la fila.
 */

public class Mano {

    private NodoCarta inicio;
    private int cantidad;

    /** Agrega una carta al frente de la lista. */
    public void agregarCarta(Carta carta) {
        NodoCarta nodo = new NodoCarta(carta);
        nodo.setSiguiente(inicio);
        inicio = nodo;
        cantidad++;
    }

    /** Devuelve el primer nodo de la lista (null si la mano está vacía). */
    public NodoCarta getPrimera() {
        return inicio;
    }

    public int getCantidad() {
        return cantidad;
    }

    /** true mientras no hayan llegado cartas. */
    public boolean estaVacia() {
        return inicio == null;
    }
}
