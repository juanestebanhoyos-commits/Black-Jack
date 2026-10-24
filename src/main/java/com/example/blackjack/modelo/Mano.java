package com.example.blackjack.modelo;

/**
 * La mano de un participante. Las cartas se guardan en una lista
 * enlazada hecha en clase (no se usa LinkedList de java.util):
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

    // =================== Preguntas que la mano responde ===================

    /** ¿Hay alguna carta con esta figura? (AS, CUATRO, REY…) */
    public boolean contiene(Rango figura) {
        for (NodoCarta actual = inicio; actual != null; actual = actual.getSiguiente()) {
            if (actual.getCarta().getRango() == figura) {
                return true;
            }
        }
        return false;
    }

    /** ¿Hay alguna carta que valga estos puntos? (10, J, Q y K valen 10) */
    public boolean contieneValor(int valor) {
        for (NodoCarta actual = inicio; actual != null; actual = actual.getSiguiente()) {
            if (actual.getCarta().getValorBase() == valor) {
                return true;
            }
        }
        return false;
    }

    /** ¿Hay alguna figura (J, Q o K)? */
    public boolean contieneFigura() {
        for (NodoCarta actual = inicio; actual != null; actual = actual.getSiguiente()) {
            if (actual.getCarta().getRango().esFigura()) {
                return true;
            }
        }
        return false;
    }

    /** Blackjack: un as y una carta que vale 10 en la mano de dos cartas. */
    public boolean esBlackjack() {
        return getCantidad() == 2 && contiene(Rango.AS) && contieneValor(10);
    }

    /** Veinte y medio: figura (J, Q o K) y un 4 entre las dos primeras cartas. */
    public boolean esVeinteYMedio() {
        return getCantidad() == 2 && contieneFigura() && contiene(Rango.CUATRO);
    }
}
