package com.example.blackjack.modelo;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** Prueba del mazo: las 52 cartas salen sin repetir y mezclar repone el mazo completo. */
class MazoTest {

    /**
     * El enunciado dice que una misma carta no puede repartirse dos veces
     * durante la ronda. Aquí se reparte el mazo completo y se revisa que
     * salgan las 52 cartas, todas distintas, y que después quede vacío.
     */
    @Test
    void reparteSinRepetirYReinicia() {
        Mazo mazo = new Mazo();
        Set<String> repartidas = new HashSet<>(); // recuerda las cartas vistas

        // Reparte el mazo apilando cada carta en el conjunto.
        for (int i = 0; i < Mazo.TOTAL_CARTAS; i++) {
            repartidas.add(mazo.repartirCarta().toString());
        }

        // Si quedara alguna carta o alguna se repitiera, el conjunto lo delata.
        assertFalse(mazo.tieneCartas());
        assertEquals(52, repartidas.size());

        // Mezclar devuelve al mazo sus 52 cartas: cada ronda arranca con el mazo completo.
        mazo.mezclar();
        int repartidasDeNuevo = 0;
        while (mazo.tieneCartas()) {
            mazo.repartirCarta();
            repartidasDeNuevo++;
        }
        assertEquals(52, repartidasDeNuevo);
    }
}
