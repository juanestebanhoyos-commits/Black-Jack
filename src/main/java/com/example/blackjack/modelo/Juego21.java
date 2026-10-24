package com.example.blackjack.modelo;

/**
 * El Modelo del juego. Por ahora administra el saldo del jugador y
 * valida su apuesta;
 */
public class Juego21 {

    public static final int SALDO_INICIAL = 1000; // fichas ficticias de arranque
    public static final int APUESTA_MINIMA = 10;  // además debe ser múltiplo de 10

    private int saldo = SALDO_INICIAL;

    public int getSaldo() {
        return saldo;
    }

    /**
     * Revisa las reglas de la apuesta. Devuelve null si es válida;
     * si no, devuelve el mensaje que la vista puede mostrar.
     */
    public String validarApuesta(int apuestaPedida) {
        if (apuestaPedida < APUESTA_MINIMA) {
            return "La apuesta mínima es de 10 fichas";
        }
        if (apuestaPedida % 10 != 0) {
            return "La apuesta debe ser un múltiplo de 10";
        }
        if (apuestaPedida > saldo) {
            return "La apuesta no puede superar tu saldo";
        }
        return null;
    }
}
