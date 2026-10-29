package com.example.blackjack.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Prueba de la mano: la lista enlazada funciona y el puntaje ajusta los ases. */
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

    /** La lista enlazada crece y la última carta queda al frente; los ases se ajustan. */
    @Test
    void agregaCartasYCalculaElPuntaje() {
        // La mano arranca vacía; el rey llega primero y luego el as: el as queda delante.
        Mano mano = new Mano();
        assertTrue(mano.estaVacia());

        mano.agregarCarta(new Carta(Simbolo.CORAZONES, Rango.REY));
        mano.agregarCarta(new Carta(Simbolo.PICAS, Rango.AS));

        assertFalse(mano.estaVacia());
        assertEquals(2, mano.getCantidad());
        assertEquals(Rango.AS, mano.getPrimera().getCarta().getRango());

        // Rey + As: el as vale 11 y llega exactamente a 21.
        assertEquals(21, mano.getPuntaje());

        // Dos ases: solo uno puede valer 11 (11 + 1); con tres, igual (11 + 1 + 1).
        assertEquals(12, manoCon(Rango.AS, Rango.AS).getPuntaje());
        assertEquals(13, manoCon(Rango.AS, Rango.AS, Rango.AS).getPuntaje());

        // Rey + Rey + 5 + As: aun bajando el as a 1 la mano queda pasada (26).
        assertEquals(26, manoCon(Rango.REY, Rango.REY, Rango.CINCO, Rango.AS).getPuntaje());
    }
}
