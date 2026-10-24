package com.example.blackjack.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Pruebas sencillas del juego: la regla de la apuesta. */
class Juego21Test {

    /**
     * La apuesta debe ser múltiplo de 10, al menos de 10 fichas y no
     * superar el saldo (el enunciado arranca con 1.000).
     */
    @Test
    void validaLaApuestaDelJugador() {
        Juego21 juego = new Juego21();

        // Apuestas correctas: el mínimo y la totalidad del saldo.
        assertNull(juego.validarApuesta(10));
        assertNull(juego.validarApuesta(1000));
        assertEquals(1000, juego.getSaldo());

        // Muy pequeña, no múltiplo de 10 o mayor que el saldo: se rechazan.
        assertNotNull(juego.validarApuesta(5));
        assertNotNull(juego.validarApuesta(15));
        assertNotNull(juego.validarApuesta(1010));
    }
}
