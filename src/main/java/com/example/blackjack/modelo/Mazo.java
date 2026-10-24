package com.example.blackjack.modelo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mazo de 52 cartas guardadas en un arreglo.
 * Cada ronda se crea un Mazo nuevo.
 * de modo que nunca se reparte dos veces la misma carta durante la ronda.
 */

public class Mazo {

    public static final int TOTAL_CARTAS = 52;

    private final Carta[] cartas = new Carta[TOTAL_CARTAS];
    private int siguiente = 0; // índice de la próxima carta por repartir

    public Mazo() {
        llenar();
        mezclar();
    }

    /** Solo para las pruebas: un mazo con el orden de cartas ya decidido (no se mezcla). */
    Mazo(Carta[] ordenFijo) {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = ordenFijo[i];
        }
    }

    /** Coloca las 52 cartas (4 símbolos × 13 figuras) en el arreglo. */
    private void llenar() {
        int i = 0;
        for (Simbolo simbolo : Simbolo.values()) {
            for (Rango rango : Rango.values()) {
                cartas[i] = new Carta(simbolo, rango);
                i++;
            }
        }
    }

    /** Mezcla las 52 cartas con la función integrada de Java y deja el reparto en cero. */
    public void mezclar() {
        List<Carta> lista = Arrays.asList(cartas);
        Collections.shuffle(lista);
        siguiente = 0;
    }

    /** Entrega la siguiente carta del mazo. */
    public Carta repartirCarta() {
        if (!tieneCartas()) {
            throw new IllegalStateException("El mazo está agotado");
        }
        Carta carta = cartas[siguiente];
        siguiente++;
        return carta;
    }

    public boolean tieneCartas() {
        return siguiente < cartas.length;
    }

    public int getCartasRestantes() {
        return cartas.length - siguiente;
    }
}
