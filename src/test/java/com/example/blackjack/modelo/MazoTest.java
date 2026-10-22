package com.example.blackjack.modelo;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas sencillas del mazo: repartir sin repetir y mezclar que reinicia. */

class MazoTest {

    /**
     * El enunciado dice que una misma carta no puede repartirse dos veces
     * durante la ronda. Aquí se reparte el mazo completo y se revisa que
     * salgan las 52 cartas, todas distintas, y que después quede vacío.
     */

    @Test
    void reparteLas52CartasSinRepetir() {
        Mazo mazo = new Mazo();
        Set<String> repartidas = new HashSet<>(); // recuerda las cartas vistas

        //Reparte el mazo apilando cada carta en el conjunto.
        for (int i = 0; i < Mazo.TOTAL_CARTAS; i++) {
            repartidas.add(mazo.repartirCarta().toString());
        }

        // Si quedara alguna carta por repartir fallaría,
        // y si alguna se repitiera el conjunto tendría menos de 52.
        assertFalse(mazo.tieneCartas());
        assertEquals(52, repartidas.size());
    }

    /**
     * Cada ronda necesita un mazo completo otra vez, así que mezclar
     * debe devolver al mazo sus 52 cartas aunque ya se hayan repartido.
     */
    @Test
    void mezclarReiniciaElMazo() {
        Mazo mazo = new Mazo();
        // Se reparte un par de cartas antes de volver a mezclar.
        mazo.repartirCarta();
        mazo.repartirCarta();
        mazo.mezclar();

        // Tras mezclar, el mazo debe poder repartir 52 de nuevo.
        int repartidas = 0;
        while (mazo.tieneCartas()) {
            mazo.repartirCarta();
            repartidas++;
        }
        assertEquals(52, repartidas);
    }
}
