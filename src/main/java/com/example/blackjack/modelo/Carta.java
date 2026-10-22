package com.example.blackjack.modelo;

/** Una carta del mazo se crea con su símbolo, su figura y no vuelve a cambiar. */

public class Carta {

    private final Simbolo simbolo;
    private final Rango rango;

    public Carta(Simbolo simbolo, Rango rango) {
        this.simbolo = simbolo;
        this.rango = rango;
    }

    public Simbolo getSimbolo() {
        return simbolo;
    }

    public Rango getRango() {
        return rango;
    }

    public int getValorBase() {
        return rango.getValorBase();
    }

    @Override
    public String toString() {
        return rango + " de " + simbolo;
    }
}
