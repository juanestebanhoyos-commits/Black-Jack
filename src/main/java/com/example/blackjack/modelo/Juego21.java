package com.example.blackjack.modelo;

/**
 * El Modelo del juego: administra el mazo, las manos, el saldo, la
 * apuesta, el turno y el resultado de cada ronda. El controlador
 * llamará a sus métodos y la vista se limitará a mostrar su estado.
 */
public class Juego21 {

    public static final int SALDO_INICIAL = 1000; // fichas ficticias de arranque
    public static final int APUESTA_MINIMA = 10;  // además debe ser múltiplo de 10

    /** Fases de la ronda: la vista las usa para habilitar los botones. */
    public enum Estado { SIN_RONDA, TURNO_JUGADOR, RONDA_TERMINADA }

    /** Resultado del duelo de la ronda. */
    public enum Resultado { VICTORIA, VICTORIA_BLACKJACK, DERROTA, EMPATE }

    private final Mazo mazoFijo; // solo lo fijan las pruebas para reproducir jugadas

    private Mazo mazo;
    private Mano manoJugador = new Mano();
    private Mano manoComputador = new Mano();
    private int saldo = SALDO_INICIAL;
    private int apuesta;
    private Estado estado = Estado.SIN_RONDA;
    private Resultado resultado;

    public Juego21() {
        mazoFijo = null;
    }

    /** Solo para las pruebas: permite fijar el orden de las cartas. */
    Juego21(Mazo mazoFijo) {
        this.mazoFijo = mazoFijo;
    }

    // =================== Consultas y reglas de la apuesta ===================

    public int getSaldo() {
        return saldo;
    }

    public int getApuesta() {
        return apuesta;
    }

    public Estado getEstado() {
        return estado;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public Mano getManoJugador() {
        return manoJugador;
    }

    public Mano getManoComputador() {
        return manoComputador;
    }

    public int getPuntajeJugador() {
        return manoJugador.getPuntaje();
    }

    public int getPuntajeComputador() {
        return manoComputador.getPuntaje();
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

    // =================== Jugadas especiales (para los mensajes de la vista) ====

    /** 21 sin cumplir la condición de Blackjack. */
    public boolean jugadorTieneVeintiuno() {
        return getPuntajeJugador() == 21 && !manoJugador.esBlackjack();
    }

    /** Figura + 4 en las dos primeras cartas: solo avisa, no paga extra. */
    public boolean jugadorTieneVeinteYMedio() {
        return manoJugador.esVeinteYMedio();
    }

    // =================== La ronda ===================

    /** Comienza la ronda: descuenta la apuesta y reparte al jugador. */
    public void iniciarRonda(int apuestaPedida) {
        // No se puede iniciar otra ronda antes de terminar la actual.
        if (estado == Estado.TURNO_JUGADOR) {
            return;
        }

        // Una apuesta inválida (muy baja, no múltiplo o mayor al saldo) no arranca nada.
        if (validarApuesta(apuestaPedida) != null) {
            return;
        }

        apuesta = apuestaPedida;
        saldo -= apuesta; // la apuesta se descuenta al comenzar

        // Cada ronda arranca con el mazo completo y mezclado (las pruebas lo fijan).
        mazo = (mazoFijo != null) ? mazoFijo : new Mazo();
        manoJugador = new Mano();
        manoComputador = new Mano();

        for (int i = 0; i < 2; i++) { // dos cartas iniciales del jugador
            manoJugador.agregarCarta(mazo.repartirCarta());
        }

        if (manoJugador.esBlackjack()) {
            turnoDeLaBanca();     // el Blackjack no se pregunta: la banca juega al instante
        } else {
            estado = Estado.TURNO_JUGADOR;
        }
    }

    /** El jugador pide una carta. */
    public void pedirCarta() {
        // Fuera del turno del jugador no hay acción.
        if (estado != Estado.TURNO_JUGADOR) {
            return;
        }
        manoJugador.agregarCarta(mazo.repartirCarta());

        int puntaje = getPuntajeJugador();
        if (puntaje > 21) {
            terminarRonda(Resultado.DERROTA); // pasarse pierde: la banca no juega
        } else if (puntaje == 21) {
            turnoDeLaBanca();                 // llegar a 21 cierra el turno
        }
        // Con otro puntaje (incluido el 20 y medio) el turno continúa.
    }

    /** El jugador se planta y la banca desarrolla su turno. */
    public void plantarse() {
        if (estado != Estado.TURNO_JUGADOR) {
            return;
        }
        turnoDeLaBanca();
    }

    // =================== Reparto interno ===================

    /**
     * Turno de la banca: recibe dos cartas y pide mientras su puntaje sea
     * menor que 17. Con 17 a 21 se planta; su decisión depende solo de su mano.
     */
    private void turnoDeLaBanca() {
        for (int i = 0; i < 2; i++) {
            manoComputador.agregarCarta(mazo.repartirCarta());
        }

        while (getPuntajeComputador() < 17) {
            manoComputador.agregarCarta(mazo.repartirCarta());
        }

        terminarRonda(resolver());
    }

    /** Compara las manos respetando la prioridad del Blackjack. */
    private Resultado resolver() {
        if (manoJugador.esBlackjack() && manoComputador.esBlackjack()) {
            return Resultado.EMPATE;              // ambos lograron Blackjack
        }
        if (manoJugador.esBlackjack()) {
            return Resultado.VICTORIA_BLACKJACK;
        }
        if (manoComputador.esBlackjack()) {
            return Resultado.DERROTA;
        }
        if (getPuntajeComputador() > 21) {
            return Resultado.VICTORIA;            // la banca se pasó
        }

        // =====regla agregada Veinte y medio (figura + 4 en las dos primeras cartas) =====
        boolean jugador20yMedio = manoJugador.esVeinteYMedio();
        boolean computador20yMedio = manoComputador.esVeinteYMedio();
        if (jugador20yMedio && computador20yMedio) {
            return Resultado.EMPATE;              // ambos tienen veinte y medio
        }
        if (jugador20yMedio) {
            // Le gana a cualquier mano menor de 21; pierde contra un 21.
            return getPuntajeComputador() == 21 ? Resultado.DERROTA : Resultado.VICTORIA;
        }
        if (computador20yMedio) {
            return getPuntajeJugador() == 21 ? Resultado.VICTORIA : Resultado.DERROTA;
        }
        // ========================================================================



        // Veintiuno y veinte y medio se comparan por puntaje normal.
        if (getPuntajeJugador() > getPuntajeComputador()) {
            return Resultado.VICTORIA;
        }
        if (getPuntajeJugador() == getPuntajeComputador()) {
            return Resultado.EMPATE;
        }
        return Resultado.DERROTA;
    }

    /** Cierra la ronda: el saldo se ajusta una sola vez. */
    private void terminarRonda(Resultado resultado) {
        this.resultado = resultado;
        estado = Estado.RONDA_TERMINADA;

        // La apuesta ya fue descontada al iniciar; aquí se acredita lo que toque.
        switch (resultado) {
            case EMPATE             -> saldo += apuesta;
            case VICTORIA           -> saldo += apuesta * 2;              // devuelve y gana lo mismo
            case VICTORIA_BLACKJACK -> saldo += apuesta * 5 / 2;          // devuelve + la mitad extra
            case DERROTA            -> { /* la apuesta queda perdida */ }
        }
    }
}
