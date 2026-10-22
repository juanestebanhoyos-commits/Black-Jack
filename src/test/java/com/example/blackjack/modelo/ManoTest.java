package com.example.blackjack.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas sencillas de la mano: agregar cartas a la lista enlazada. */
class ManoTest {

    /**
     * Verifica que la mano empieza vacía, crece al agregar cartas
     * y que la última agregada queda al frente de la lista enlazada.
     */
    @Test
    void agregaCartasYLasCuenta() {
        Mano mano = new Mano();
        assertTrue(mano.estaVacia());

        // El rey llega primero, luego el as; el as debe quedar delante.
        mano.agregarCarta(new Carta(Simbolo.CORAZONES, Rango.REY));
        mano.agregarCarta(new Carta(Simbolo.PICAS, Rango.AS));

        assertFalse(mano.estaVacia());
        assertEquals(2, mano.getCantidad());

        NodoCarta primera = mano.getPrimera();
        assertEquals(Rango.AS, primera.getCarta().getRango());
        assertEquals(Rango.REY, primera.getSiguiente().getCarta().getRango());
    }
}
