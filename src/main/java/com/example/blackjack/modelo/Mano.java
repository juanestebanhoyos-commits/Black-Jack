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

    /**
     * Calcula el puntaje de la mano. Cada as se suma primero como 1;
     * mientras quede un as que pueda valer 11 sin superar 21, se le suma 10.
     */

    public int getPuntaje() {
        int puntaje = 0;
        int ases = 0;

        // Recorre la lista enlazada desde inicio
        for (NodoCarta actual = inicio; actual != null; actual = actual.getSiguiente()) {
            puntaje += actual.getCarta().getValorBase();
            if (actual.getCarta().getRango() == Rango.AS) {
                ases++;
            }
        }

        // El as ya contó como 1; darle valor 11 equivale a sumarle 10.
        while (ases > 0 && puntaje + 10 <= 21) {
            puntaje += 10;
            ases--;
        }

        return puntaje;
    }
}
