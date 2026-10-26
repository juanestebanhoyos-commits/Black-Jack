package com.example.blackjack.controlador;

import com.example.blackjack.modelo.Carta;
import com.example.blackjack.modelo.Juego21;
import com.example.blackjack.modelo.Mano;
import com.example.blackjack.modelo.NodoCarta;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * El Controlador: atiende los eventos de la vista, pide operaciones
 * al modelo (com.example.blackjack.modelo) y repinta toda la mesa
 * con refrescar().
 */
public class BlackjackControlador {

    private final Juego21 juego = new Juego21();

    @FXML private HBox cartasComputador;
    @FXML private HBox cartasJugador;
    @FXML private Label puntajeComputador;
    @FXML private Label puntajeJugador;
    @FXML private Label saldo;
    @FXML private Label apuestaVigente;
    @FXML private TextField campoApuesta;
    @FXML private Label mensajes;
    @FXML private Button botonIniciar;
    @FXML private Button botonPedir;
    @FXML private Button botonPlantarse;

    @FXML
    private void initialize() {
        refrescar();
    }

    @FXML
    private void iniciarRonda() {
        int apuestaPedida = 0;
        String error;
        try {
            apuestaPedida = Integer.parseInt(campoApuesta.getText().trim());
            error = juego.validarApuesta(apuestaPedida);
        } catch (NumberFormatException e) {
            error = "Escribe la apuesta como un número (por ejemplo 50)";
        }
        if (error != null) {
            mensajes.setText(error);
            return;
        }
        juego.iniciarRonda(apuestaPedida);
        refrescar();
    }

    @FXML
    private void pedirCarta() {
        juego.pedirCarta();
        refrescar();
    }

    @FXML
    private void plantarse() {
        juego.plantarse();
        refrescar();
    }

    /** Las reglas del enunciado, resumidas tal como se aplican en el juego. */
    private static final String REGLAS = """
            • El mazo de 52 cartas se baraja completo al inicio de cada ronda.

            Valores: del 2 al 10 su número; J, Q y K valen 10; el as vale 1 u 11,
            ajustado para lograr el mayor puntaje sin pasar de 21. Los palos no cambian el puntaje.

            Apuesta: mínimo 10 fichas, múltiplo de 10 y nunca mayor a tu saldo.
            Se descuenta al iniciar la ronda y no se puede cambiar durante ella.

            Pagos: si pierdes, la apuesta queda perdida; si empatas, la recuperas;
            victoria ordinaria te acredita el doble; victoria con Blackjack, 2,5 veces.

            Jugadas especiales con las dos primeras cartas:
            • Blackjack: un as con un 10 (la carta 10, J, Q o K). Gana todo menos otro Blackjack.
            • Veintiuno: llegar a 21 sin ser Blackjack.
            • Veinte y medio: una figura (J, Q o K) junto a un 4. Solo avisa, no paga extra.

            Tu turno: pides cartas o te plantas. Al llegar a 21 termina tu turno;
            si te pasas de 21, pierdes al instante.

            El computador recibe sus dos cartas cuando te plantas: pedirá cartas
            mientras tenga menos de 17 y se plantará con 17 o más.""";

    /** Muestra las reglas en una ventana aparte; la partida queda igual. */
    @FXML
    private void verReglas() {
        Alert venta = new Alert(Alert.AlertType.INFORMATION, REGLAS);
        venta.setHeaderText("Reglas del 21 Blackjack");
        venta.setTitle("Ver reglas");
        venta.show();
    }

    /** Cierra el juego con confirmación del jugador. */
    @FXML
    private void cerrarJuego() {
        if (cierreConfirmado()) {
            Platform.exit();
        }
    }

    /**
     * Pregunta si el jugador confirma terminar el juego.
     * Cancelar devuelve false y así conserva el estado de la partida.
     * Es público porque también lo usa la ✕ de la ventana (BlackjackApp).
     */
    public boolean cierreConfirmado() {
        Alert pregunta = new Alert(Alert.AlertType.CONFIRMATION,
                "El estado del juego se perderá si cierras.");
        pregunta.setTitle("Cerrar juego");
        pregunta.setHeaderText("¿Seguro que quieres cerrar el juego?");
        return pregunta.showAndWait().filter(boton -> boton == ButtonType.OK).isPresent();
    }

    // =================== Repintado de la vista ===================

    /** Dibuja la mesa completa con el estado actual del modelo. */
    private void refrescar() {
        dibujarMano(cartasJugador, juego.getManoJugador());
        dibujarMano(cartasComputador, juego.getManoComputador());
        puntajeJugador.setText("Puntaje: " + juego.getPuntajeJugador());
        puntajeComputador.setText("Puntaje: " + juego.getPuntajeComputador());
        saldo.setText(String.valueOf(juego.getSaldo()));
        apuestaVigente.setText(juego.getApuesta() == 0 ? "—" : String.valueOf(juego.getApuesta()));
        mensajes.setText(mensaje());
        deshabilitarSegunEstado();
    }

    /** Ajusta qué botones y el campo de apuesta funcionan según la fase de la ronda. */
    private void deshabilitarSegunEstado() {
        boolean turnoDelJugador = juego.getEstado() == Juego21.Estado.TURNO_JUGADOR;
        botonPedir.setDisable(!turnoDelJugador);
        botonPlantarse.setDisable(!turnoDelJugador);
        botonIniciar.setDisable(turnoDelJugador || juego.getSaldo() < Juego21.APUESTA_MINIMA);
        campoApuesta.setDisable(turnoDelJugador);
    }

    /** Vacía la zona y vuelve a colocar una imagen por cada carta. */
    private void dibujarMano(HBox zona, Mano mano) {
        zona.getChildren().clear();
        for (NodoCarta actual = mano.getPrimera(); actual != null; actual = actual.getSiguiente()) {
            zona.getChildren().add(vistaDeCarta(actual.getCarta()));
        }
    }

    private String mensaje() {
        return switch (juego.getEstado()) {
            case SIN_RONDA -> "Elige tu apuesta y presiona «Iniciar ronda»";
            case TURNO_JUGADOR -> juego.jugadorTieneVeinteYMedio()
                    ? "¡Veinte y medio! Puedes pedir carta o plantarte"
                    : "Tu turno: pide carta o plantarte";
            case RONDA_TERMINADA -> mensajeDeResultado();
        };
    }

    private String mensajeDeResultado() {
        int puntajeJ = juego.getPuntajeJugador();
        int puntajeC = juego.getPuntajeComputador();
        String nota = juego.jugadorTieneVeintiuno() ? "¡Veintiuno! "
                : juego.jugadorTieneVeinteYMedio() ? "¡Veinte y medio! " : "";
        return switch (juego.getResultado()) {
            case VICTORIA_BLACKJACK -> "¡Blackjack! La banca te paga 2,5 veces la apuesta (" + puntajeJ + " a " + puntajeC + ")";
            case VICTORIA -> nota + "Ganaste la ronda (" + puntajeJ + " a " + puntajeC + ")";
            case EMPATE -> nota + "Empate: recuperas tu apuesta (" + puntajeJ + " a " + puntajeC + ")";
            case DERROTA -> "Perdiste la ronda (" + puntajeJ + " a " + puntajeC + ")";
        };
    }

    /**
     * Crea la vista de una carta según su nombre de archivo:
     * símbolo del modelo en minúsculas + rango tal como se llama,
     * por ejemplo «corazones_REY.png».
     */
    private ImageView vistaDeCarta(Carta carta) {
        String archivo = carta.getSimbolo().name().toLowerCase() + "_" + carta.getRango().name();
        String ruta = "/com/example/blackjack/img/" + archivo + ".png";
        ImageView vista = new ImageView(new Image(getClass().getResourceAsStream(ruta)));
        vista.setFitWidth(110); // todas las cartas al mismo tamaño en la mesa
        vista.setPreserveRatio(true);
        return vista;
    }
}
