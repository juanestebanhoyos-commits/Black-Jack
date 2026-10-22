package com.example.blackjack.modelo;

/** Nodo de la lista enlazada usada por la mano. */
public class NodoCarta {

    private final Carta carta;
    private NodoCarta siguiente;

    public NodoCarta(Carta carta) {
        this.carta = carta;
    }

    public Carta getCarta() {
        return carta;
    }

    public NodoCarta getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoCarta siguiente) {
        this.siguiente = siguiente;
    }
}
