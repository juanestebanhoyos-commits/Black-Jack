package com.example.blackjack.modelo;

/**
 * Figura de una carta y su valor base.
 * El as vale 1 como base; la mano decidirá después si un as cuenta como 11.
 * Las figuras J, Q y K valen 10.
 */

public enum Rango {
    AS(1),
    DOS(2),
    TRES(3),
    CUATRO(4),
    CINCO(5),
    SEIS(6),
    SIETE(7),
    OCHO(8),
    NUEVE(9),
    DIEZ(10),
    JOTA(10),
    REINA(10),
    REY(10);

    private final int valorBase;

    Rango(int valorBase) {
        this.valorBase = valorBase;
    }

    public int getValorBase() {
        return valorBase;
    }

    /** J, Q o K. */
    public boolean esFigura() {
        return this == JOTA || this == REINA || this == REY;
    }
}
