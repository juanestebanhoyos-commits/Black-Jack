package com.example.blackjack.modelo;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de la ronda: apuesta, turnos con saldo y jugadas especiales. */
class Juego21Test {

    // =================== Ayudantes para armar casos exactos ===================

    /**
     * Arma un mazo de 52 cuyo orden empieza con las figuras indicadas,
     * así cada caso reproduce una jugada concreta sin depender del azar.
     */
    private Mazo mazoFijo(Rango... primeras) {
        // El mazo se llena con todas las cartas menos las que el caso fijará…
        List<Rango> fijas = Arrays.asList(primeras);
        Carta[] cartas = new Carta[Mazo.TOTAL_CARTAS];
        int i = 0;
        for (Simbolo simbolo : Simbolo.values()) {
            for (Rango figura : Rango.values()) {
                if (!(simbolo == Simbolo.CORAZONES && fijas.contains(figura))) {
                    cartas[i++] = new Carta(simbolo, figura);
                }
            }
        }
        // …y las fijadas se colocan al frente, en el orden en que se repartirán.
        for (int j = 0; j < primeras.length; j++) {
            cartas[j] = new Carta(Simbolo.CORAZONES, primeras[j]);
        }
        return new Mazo(cartas);
    }

    /** Crea el juego con un mazo de orden fijo y una apuesta de 100 fichas. */
    private Juego21 juegoCon(Rango... primerasFiguras) {
        return new Juego21(mazoFijo(primerasFiguras));
    }

    // =================== Pruebas ===================

    /** La apuesta: mínimo 10, múltiplo de 10 y dentro del saldo (1.000 al inicio). */
    @Test
    void validaLaApuestaDelJugador() {
        Juego21 juego = new Juego21();

        assertNull(juego.validarApuesta(10));
        assertNull(juego.validarApuesta(1000));
        assertEquals(1000, juego.getSaldo());

        // Muy pequeña, no múltiplo de 10 o mayor que el saldo: se rechazan.
        assertNotNull(juego.validarApuesta(5));
        assertNotNull(juego.validarApuesta(15));
        assertNotNull(juego.validarApuesta(1010));
    }

    /**
     * Los dos turnos: en el normal la banca pide con menos de 17 y se planta
     * con 17 a 21; cuando el jugador se pasa, pierde y la banca ni juega.
     */
    @Test
    void losTurnosAjustanElSaldo() {
        // Victoria normal: banca 3 + 4 = 7 → pide el 10 → queda en 17.
        Juego21 victoria = juegoCon(Rango.REY, Rango.REY, Rango.TRES, Rango.CUATRO, Rango.DIEZ);
        victoria.iniciarRonda(100); // jugador: rey + rey = 20
        victoria.plantarse();

        assertEquals(Juego21.Estado.RONDA_TERMINADA, victoria.getEstado());
        assertEquals(17, victoria.getPuntajeComputador());
        assertEquals(Juego21.Resultado.VICTORIA, victoria.getResultado());
        assertEquals(1100, victoria.getSaldo()); // 1000 - 100 (apuesta) + 200

        // 10 + 10 + 10 = 30: derrota inmediata con la banca sin cartas.
        Juego21 pasada = juegoCon(Rango.REY, Rango.REY, Rango.REY);
        pasada.iniciarRonda(100);
        pasada.pedirCarta();

        assertTrue(pasada.getManoComputador().estaVacia());
        assertEquals(Juego21.Resultado.DERROTA, pasada.getResultado());
        assertEquals(900, pasada.getSaldo()); // la apuesta quedó descontada

        // La banca se pasa: con Siete + Ocho (15) pide el Nueve y queda en 24 → gana el jugador.
        Juego21 bancaPasada = juegoCon(Rango.REY, Rango.REINA, Rango.SIETE, Rango.OCHO, Rango.NUEVE);
        bancaPasada.iniciarRonda(100); // jugador: 20
        bancaPasada.plantarse();

        assertEquals(Juego21.Resultado.VICTORIA, bancaPasada.getResultado()); // 24 > 21
        assertEquals(1100, bancaPasada.getSaldo()); // 1000 - 100 + 200
    }

    /** Blackjack, doble Blackjack, veintiuno, veinte y medio y empate normal. */
    @Test
    void reconoceLasJugadasEspeciales() {
        // Blackjack del jugador: la banca juega al instante y no iguala → 2,5 la apuesta.
        Juego21 blackjack = juegoCon(Rango.AS, Rango.REY, Rango.AS, Rango.NUEVE);
        blackjack.iniciarRonda(100);

        assertEquals(Juego21.Resultado.VICTORIA_BLACKJACK, blackjack.getResultado());
        assertEquals(1150, blackjack.getSaldo()); // 1000 - 100 + 250

        // Doble Blackjack: empate y la apuesta se devuelve.
        Juego21 doble = juegoCon(Rango.AS, Rango.REY, Rango.AS, Rango.REY);
        doble.iniciarRonda(100);

        assertEquals(Juego21.Resultado.EMPATE, doble.getResultado());
        assertEquals(1000, doble.getSaldo());

        // Llegar a 21 pidiendo cierra el turno (veintiuno: tres cartas, no Blackjack).
        Juego21 veintiuno = juegoCon(Rango.DIEZ, Rango.CUATRO, Rango.SIETE, Rango.REY, Rango.NUEVE);
        veintiuno.iniciarRonda(100); // 10 + 4 = 14
        veintiuno.pedirCarta();      // 14 + 7 = 21

        assertTrue(veintiuno.jugadorTieneVeintiuno());
        assertEquals(Juego21.Resultado.VICTORIA, veintiuno.getResultado()); // 21 > 19

        // Veinte y medio (figura + 4): no gana automático; la banca lo supera.
        Juego21 medio = juegoCon(Rango.REINA, Rango.CUATRO, Rango.DIEZ, Rango.SEIS, Rango.CUATRO);
        medio.iniciarRonda(100); // reina + 4 = 14

        assertTrue(medio.jugadorTieneVeinteYMedio());
        medio.plantarse(); // banca: 10 + 6 = 16 → pide 4 → 20

        assertEquals(Juego21.Resultado.DERROTA, medio.getResultado()); // 14 < 20

        // Empate puntaje a puntaje, sin Blackjack involucrado (20 = 20).
        Juego21 empate = juegoCon(Rango.REY, Rango.REY, Rango.AS, Rango.NUEVE);
        empate.iniciarRonda(100);
        empate.plantarse();

        assertEquals(Juego21.Resultado.EMPATE, empate.getResultado());
        assertEquals(1000, empate.getSaldo()); // 1000 - 100 + 100

        // Blackjack de la banca (as + JOTA) contra la mano normal del jugador: pierde este.
        Juego21 bancaBlackjack = juegoCon(Rango.REY, Rango.REINA, Rango.AS, Rango.JOTA);
        bancaBlackjack.iniciarRonda(100); // jugador: 20
        bancaBlackjack.plantarse();

        assertTrue(bancaBlackjack.getManoComputador().esBlackjack());
        assertEquals(Juego21.Resultado.DERROTA, bancaBlackjack.getResultado());
        assertEquals(900, bancaBlackjack.getSaldo()); // la apuesta queda perdida
    }
}
