package com.example.blackjack.controlador;

import com.example.blackjack.modelo.Carta;
import com.example.blackjack.modelo.Juego21;
import com.example.blackjack.modelo.Mano;
import com.example.blackjack.modelo.NodoCarta;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML private Button botonReglas;
    @FXML private Button botonCerrar;

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
    }

    /** Vacía la zona y vuelve a colocar una imagen por cada carta. */
    private void dibujarMano(HBox zona, Mano mano) {
        zona.getChildren().clear();
        for (NodoCarta actual = mano.getPrimera(); actual != null; actual = actual.getSiguiente()) {
            zona.getChildren().add(vistaDeCarta(actual.getCarta()));
        }
    }

    private String mensaje() {
        switch (juego.getEstado()) {
            case SIN_RONDA:
                return "Elige tu apuesta y presiona «Iniciar ronda»";
            case TURNO_JUGADOR:
                return juego.jugadorTieneVeinteYMedio()
                        ? "¡Veinte y medio! Puedes pedir carta o plantarte"
                        : "Tu turno: pide carta o plantarte";
            case RONDA_TERMINADA:
                return mensajeDeResultado();
            default:
                return "";
        }
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
