package com.example.blackjack.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas sencillas de la mano: agregar cartas y calcular el puntaje. */
class ManoTest {

    /**
     * Arma una mano con las figuras indicadas. El símbolo no altera
     * el puntaje, así que se usa siempre el mismo para simplificar.
     */
    private Mano manoCon(Rango... rangos) {
        Mano mano = new Mano();
        for (Rango rango : rangos) {
            mano.agregarCarta(new Carta(Simbolo.CORAZONES, rango));
        }
        return mano;
    }

    /** Chequea la estructura: crecer, contar y el orden de la lista enlazada. */
    @Test
    void agregaCartasYLasCuenta() {
        Mano mano = new Mano();
        assertTrue(mano.estaVacia());

        // El rey llega primero, luego el as: el as debe quedar delante.
        mano.agregarCarta(new Carta(Simbolo.CORAZONES, Rango.REY));
        mano.agregarCarta(new Carta(Simbolo.PICAS, Rango.AS));

        assertFalse(mano.estaVacia());
        assertEquals(2, mano.getCantidad());

        NodoCarta primera = mano.getPrimera();
        assertEquals(Rango.AS, primera.getCarta().getRango());
        assertEquals(Rango.REY, primera.getSiguiente().getCarta().getRango());
    }

    /** Si un as puede valer 11 sin pasar de 21, el puntaje lo aprovecha. */
    @Test
    void elAsVale11CuandoCabe() {
        // Rey + as: el as vale 11 y llega exactamente a 21.
        assertEquals(21, manoCon(Rango.REY, Rango.AS).getPuntaje());
        // Dos ases: uno vale 11 y el otro queda valiendo 1.
        assertEquals(12, manoCon(Rango.AS, Rango.AS).getPuntaje());
        // Tres ases: solo uno puede valer 11 (11 + 1 + 1).
        assertEquals(13, manoCon(Rango.AS, Rango.AS, Rango.AS).getPuntaje());
    }

    /** Cuando el as con valor 11 haría pasar la mano, queda valiendo 1. */
    @Test
    void elAsVale1CuandoYaNoCabe() {
        // Rey + as + as: el segundo as baja a 1 (10 + 11 + 1).
        assertEquals(12, manoCon(Rango.REY, Rango.AS, Rango.AS).getPuntaje());
        // Rey + rey + 5 + as: aun bajando el as a 1, la mano queda pasada (26).
        assertEquals(26, manoCon(Rango.REY, Rango.REY, Rango.CINCO, Rango.AS).getPuntaje());
    }
}
