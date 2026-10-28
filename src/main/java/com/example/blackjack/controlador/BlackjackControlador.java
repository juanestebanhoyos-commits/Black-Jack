package com.example.blackjack.controlador;

import com.example.blackjack.modelo.Carta;
import com.example.blackjack.modelo.Juego21;
import com.example.blackjack.modelo.Mano;
import com.example.blackjack.modelo.NodoCarta;
import com.example.blackjack.vista.Dialogos;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * El Controlador: atiende los eventos de la vista, pide operaciones al modelo
 * (com.example.blackjack.modelo) y repinta la ventana con lo que el modelo
 * indica. Las reglas del juego (puntajes, jugadas, resultado y pagos) las
 * decide siempre el modelo; aquí solo se muestran.
 */
public class BlackjackControlador {

    /** Pausa entre cada paso del turno automático del computador. */
    private static final int PAUSA_COMPUTADOR_MS = 900;

    // Las imágenes de las cartas son de 64 × 64 px y la carta ocupa 42 × 60 dentro de ellas:
    // se recorta ese rectángulo y se escala ×2 para que el pixel-art no se vea borroso.
    private static final Rectangle2D RECORTE_CARTA = new Rectangle2D(11, 2, 42, 60);
    private static final double ANCHO_CARTA = 84;
    private static final double ALTO_CARTA = 120;
    private static final double ANCHO_ZONA_CARTAS = 380;
    private static final double SEPARACION_CARTAS = 8;

    private final Juego21 juego = new Juego21();
    private final Map<String, Image> imagenes = new HashMap<>();

    /** Secuencia del turno del computador; null cuando no hay una en marcha. */
    private Timeline secuencia;
    /** true mientras el computador juega: bloquea todas las acciones del jugador. */
    private boolean turnoComputadorEnCurso;
    /** Saldo antes de descontar la apuesta, para mostrar la ganancia neta de la ronda. */
    private int saldoAntesDeApostar;

    @FXML private HBox cartasComputador;
    @FXML private HBox cartasJugador;
    @FXML private Label puntajeComputador;
    @FXML private Label puntajeJugador;
    @FXML private Label saldo;
    @FXML private Label apuestaVigente;
    @FXML private ScrollPane scrollMensajes;
    @FXML private VBox listaMensajes;
    @FXML private Button botonPedir;
    @FXML private Button botonPlantarse;
    @FXML private Button botonNuevaRonda;
    @FXML private Button botonReglas;
    @FXML private Button botonCerrar;

    @FXML
    private void initialize() {
        // El panel de mensajes siempre baja hasta el último mensaje.
        listaMensajes.heightProperty().addListener((obs, antes, ahora) -> scrollMensajes.setVvalue(1.0));

        agregarMensaje("Bienvenido al Blackjack.");
        agregarMensaje("Tu saldo inicial es de " + Dialogos.fichas(Juego21.SALDO_INICIAL) + " fichas.");
        agregarMensaje("Ingresa una apuesta para comenzar la ronda.");
        refrescar();
    }

    // =================== Acciones del jugador ===================

    /** «Nueva ronda»: pide la apuesta en un diálogo y, si es válida, reparte. */
    @FXML
    private void nuevaRonda() {
        if (turnoComputadorEnCurso || juego.getEstado() == Juego21.Estado.TURNO_JUGADOR) {
            return; // no se puede apostar ni empezar otra ronda mientras la actual está activa
        }
        if (juego.getSaldo() < Juego21.APUESTA_MINIMA) {
            agregarMensaje("No tienes fichas suficientes para otra ronda.");
            refrescar();
            return;
        }

        Optional<Integer> apuesta = Dialogos.pedirApuesta(ventana(), juego.getSaldo(), this::validarApuesta);
        if (apuesta.isEmpty()) {
            agregarMensaje("Apuesta cancelada. Ingresa una apuesta para comenzar la ronda.");
            return; // cancelar no cambia nada del juego
        }
        iniciarRonda(apuesta.get());
    }

    private void iniciarRonda(int apuesta) {
        saldoAntesDeApostar = juego.getSaldo();
        juego.iniciarRonda(apuesta);

        limpiarMensajes(); // cada ronda empieza con mensajes renovados
        agregarMensaje("Apuesta registrada: " + Dialogos.fichas(apuesta) + " fichas. Saldo disponible: "
                + Dialogos.fichas(saldoAntesDeApostar - apuesta) + ".");
        agregarMensaje("Recibes " + describir(cartasEnOrden(juego.getManoJugador()))
                + ". Tu puntaje es " + juego.getPuntajeJugador() + ".");
        anunciarJugadaDelJugador();
        if (juego.getEstado() == Juego21.Estado.TURNO_JUGADOR && !juego.jugadorTieneVeinteYMedio()) {
            agregarMensaje("Es tu turno: pide carta o plántate.");
        }
        despuesDeActuar();
    }

    @FXML
    private void pedirCarta() {
        if (turnoComputadorEnCurso || juego.getEstado() != Juego21.Estado.TURNO_JUGADOR) {
            return;
        }
        juego.pedirCarta();

        List<Carta> cartas = cartasEnOrden(juego.getManoJugador());
        int puntaje = juego.getPuntajeJugador();
        agregarMensaje("Pides carta y recibes " + nombre(cartas.get(cartas.size() - 1))
                + ". Tu puntaje es " + puntaje + ".");
        if (puntaje > 21) {
            agregarMensaje("Te pasaste de 21: pierdes la ronda y el computador no juega.");
        } else if (puntaje == 21) {
            agregarMensaje("Llegaste a 21: tu turno termina y comienza el del computador.");
        } else {
            anunciarJugadaDelJugador();
        }
        despuesDeActuar();
    }

    @FXML
    private void plantarse() {
        if (turnoComputadorEnCurso || juego.getEstado() != Juego21.Estado.TURNO_JUGADOR) {
            return;
        }
        juego.plantarse();
        agregarMensaje("Te plantas con " + juego.getPuntajeJugador() + " puntos. Es el turno del computador.");
        despuesDeActuar();
    }

    /** Abre las reglas; la partida (saldo, apuesta, manos, turno y resultado) queda intacta. */
    @FXML
    private void verReglas() {
        pausarSecuencia();
        Dialogos.mostrarReglas(ventana());
        reanudarSecuencia();
    }

    /** «Cerrar juego»: termina la aplicación solo si el jugador lo confirma. */
    @FXML
    private void cerrarJuego() {
        if (cierreConfirmado()) {
            Platform.exit();
        }
    }

    /**
     * Pregunta si el jugador confirma terminar el juego.
     * Cancelar devuelve false y conserva exactamente el estado de la partida.
     * Es público porque también lo usa la ✕ de la ventana (BlackjackApp).
     */
    public boolean cierreConfirmado() {
        pausarSecuencia();
        boolean confirmado = Dialogos.confirmarCierre(ventana());
        if (!confirmado) {
            reanudarSecuencia();
        }
        return confirmado;
    }

    // =================== Validación de la apuesta ===================

    /**
     * Revisa el texto escrito en el diálogo de apuesta. Devuelve el mensaje de
     * error que ayuda a corregirlo, o null si es un entero válido.
     * Las reglas de monto (mínimo, múltiplo de 10, saldo) las valida el modelo.
     */
    private String validarApuesta(String texto) {
        String valor = texto == null ? "" : texto.trim();
        if (valor.isEmpty()) {
            return "Escribe la cantidad de fichas que quieres apostar.";
        }
        if (!valor.matches("-?\\d+")) {
            if (valor.matches("[\\d.,]+")) {
                return "La apuesta debe ser un número entero, sin puntos ni comas.";
            }
            return "Escribe solo números enteros, por ejemplo 100.";
        }

        BigInteger numero = new BigInteger(valor);
        if (numero.signum() <= 0) {
            return "La apuesta debe ser mayor que cero. La apuesta mínima es de "
                    + Juego21.APUESTA_MINIMA + " fichas.";
        }
        if (numero.compareTo(BigInteger.valueOf(juego.getSaldo())) > 0) {
            return "La apuesta no puede superar tu saldo (" + Dialogos.fichas(juego.getSaldo()) + " fichas).";
        }
        return juego.validarApuesta(numero.intValue());
    }

    // =================== Avance de la ronda ===================

    /** Después de una acción del jugador: sigue su turno, juega el computador o termina la ronda. */
    private void despuesDeActuar() {
        dibujarJugador();
        if (juego.getEstado() != Juego21.Estado.RONDA_TERMINADA) {
            refrescar();               // sigue el turno del jugador
        } else if (juego.getManoComputador().estaVacia()) {
            terminarRonda();           // el jugador se pasó: el computador no jugó
        } else {
            jugarTurnoComputador();    // el modelo ya resolvió la ronda; se muestra paso a paso
        }
    }

    /**
     * El modelo ya repartió y resolvió el turno del computador de una sola vez;
     * aquí solo se muestra como una secuencia breve: primero sus dos cartas y
     * luego cada carta que pidió, con una pausa entre pasos. El saldo no cambia
     * en pantalla hasta que termina la secuencia.
     */
    private void jugarTurnoComputador() {
        turnoComputadorEnCurso = true;
        dibujarComputador();           // aún sin cartas: se revelan en la secuencia
        actualizarMarcadores();
        actualizarBotones();

        List<Carta> cartas = cartasEnOrden(juego.getManoComputador());
        secuencia = new Timeline();
        for (int visibles = 2; visibles <= cartas.size(); visibles++) {
            final int cantidad = visibles;
            secuencia.getKeyFrames().add(new KeyFrame(
                    Duration.millis(PAUSA_COMPUTADOR_MS * (visibles - 1)),
                    evento -> revelarCartasDelComputador(cartas, cantidad)));
        }
        // Los diálogos no pueden abrirse dentro de una animación: se difiere al final del paso.
        secuencia.getKeyFrames().add(new KeyFrame(
                Duration.millis(PAUSA_COMPUTADOR_MS * cartas.size()),
                evento -> Platform.runLater(this::terminarRonda)));
        secuencia.play();
    }

    private void revelarCartasDelComputador(List<Carta> cartas, int cantidad) {
        List<Carta> visibles = cartas.subList(0, cantidad);
        dibujarCartas(cartasComputador, visibles);

        Mano parcial = new Mano();
        for (Carta carta : visibles) {
            parcial.agregarCarta(carta);
        }
        int puntaje = parcial.getPuntaje();
        puntajeComputador.setText(String.valueOf(puntaje));

        if (cantidad == 2) {
            String mensaje = "El computador recibe " + describir(visibles) + ". Su puntaje es " + puntaje + ".";
            agregarMensaje(parcial.esBlackjack() ? mensaje + " ¡Blackjack del computador!" : mensaje);
        } else {
            agregarMensaje("El computador pide carta y recibe " + nombre(visibles.get(cantidad - 1))
                    + ". Su puntaje es " + puntaje + ".");
        }

        if (cantidad == cartas.size()) {
            agregarMensaje(puntaje > 21
                    ? "El computador se pasó de 21."
                    : "El computador se planta con " + puntaje + " puntos.");
        }
    }

    /** Cierra la ronda en pantalla: saldo final, mensaje y cuadro de resultado. */
    private void terminarRonda() {
        secuencia = null;
        turnoComputadorEnCurso = false;
        refrescar(); // muestra las manos completas y el saldo que dejó la liquidación

        Dialogos.DatosResultado datos = datosDelResultado();
        String titulo = datos.titulo().endsWith("!") ? datos.titulo() : datos.titulo() + ".";
        agregarMensaje(titulo + " " + datos.causa() + " " + datos.resultado() + ": "
                + datos.etiquetaGanancia().toLowerCase() + " de " + datos.ganancia() + ". Saldo: "
                + Dialogos.fichas(juego.getSaldo()) + " fichas.");
        Dialogos.mostrarResultado(ventana(), datos);

        if (juego.getSaldo() < Juego21.APUESTA_MINIMA) {
            agregarMensaje("No tienes fichas suficientes para seguir apostando. "
                    + "Puedes consultar las reglas o cerrar el juego.");
        } else {
            agregarMensaje("Pulsa «Nueva ronda» para apostar de nuevo.");
        }
        refrescar();
    }

    /** Traduce el resultado del modelo a los textos del cuadro de resultado. */
    private Dialogos.DatosResultado datosDelResultado() {
        int apuesta = juego.getApuesta();
        int neto = juego.getSaldo() - saldoAntesDeApostar; // ganancia (o pérdida) de la ronda
        String puntajeRival = juego.getManoComputador().estaVacia()
                ? "No jugó"
                : String.valueOf(juego.getPuntajeComputador());
        int pj = juego.getPuntajeJugador();

        return switch (juego.getResultado()) {
            case VICTORIA -> new Dialogos.DatosResultado(Dialogos.Tono.GANA, "¡Ganaste!", pj, puntajeRival,
                    causa(), "Victoria ordinaria", "Ganancia", Dialogos.fichas(neto) + " fichas",
                    "(se devuelve la apuesta y se gana igual)");
            case VICTORIA_BLACKJACK -> new Dialogos.DatosResultado(Dialogos.Tono.GANA, "¡Ganaste!", pj, puntajeRival,
                    causa(), "Victoria con Blackjack", "Ganancia", Dialogos.fichas(neto) + " fichas",
                    "(se devuelve la apuesta y se gana 1,5 veces)");
            case EMPATE -> new Dialogos.DatosResultado(Dialogos.Tono.EMPATE, "Empate", pj, puntajeRival,
                    causa(), "Empate", "Ganancia", Dialogos.fichas(neto) + " fichas",
                    "(se devuelve la apuesta)");
            case DERROTA -> new Dialogos.DatosResultado(Dialogos.Tono.PIERDE, "Perdiste", pj, puntajeRival,
                    causa(), "Derrota", "Pérdida", Dialogos.fichas(apuesta) + " fichas",
                    "(se pierde la apuesta)");
        };
    }

    /** Explica por qué se resolvió así la ronda, sin inventar una mano si el computador no jugó. */
    private String causa() {
        int pj = juego.getPuntajeJugador();
        int pc = juego.getPuntajeComputador();
        boolean computadorJugo = !juego.getManoComputador().estaVacia();
        boolean blackjackJugador = juego.getManoJugador().esBlackjack();
        boolean blackjackComputador = computadorJugo && juego.getManoComputador().esBlackjack();

        if (pj > 21) {
            return "Te pasaste de 21.";
        }
        if (blackjackJugador && blackjackComputador) {
            return "Ambos tienen Blackjack.";
        }
        if (blackjackJugador) {
            return "Tienes Blackjack y el computador no.";
        }
        if (blackjackComputador) {
            return "El computador tiene Blackjack.";
        }
        if (pc > 21) {
            return "El computador se pasó de 21.";
        }
        return switch (juego.getResultado()) {
            case VICTORIA, VICTORIA_BLACKJACK -> "Tu mano (" + pj + ") vence a la del computador (" + pc + ").";
            case DERROTA -> "La mano del computador (" + pc + ") vence a la tuya (" + pj + ").";
            case EMPATE -> "Ambas manos empatan con " + pj + " puntos.";
        };
    }

    /** Avisa de las jugadas especiales del jugador; las reconoce el modelo. */
    private void anunciarJugadaDelJugador() {
        if (juego.getManoJugador().esBlackjack()) {
            agregarMensaje("¡Blackjack! Comienza el turno del computador.");
        } else if (juego.jugadorTieneVeinteYMedio()) {
            agregarMensaje("¡Veinte y medio! Aún puedes pedir carta o plantarte.");
        } else if (juego.jugadorTieneVeintiuno()) {
            agregarMensaje("¡Veintiuno! Tu turno termina y comienza el del computador.");
        }
    }

    // =================== Secuencia del computador y diálogos ===================

    private void pausarSecuencia() {
        if (secuencia != null && secuencia.getStatus() == Animation.Status.RUNNING) {
            secuencia.pause();
        }
    }

    private void reanudarSecuencia() {
        if (secuencia != null && secuencia.getStatus() == Animation.Status.PAUSED) {
            secuencia.play();
        }
    }

    private Window ventana() {
        return botonReglas.getScene().getWindow();
    }

    // =================== Repintado de la vista ===================

    /** Dibuja toda la ventana con el estado actual del modelo. */
    private void refrescar() {
        dibujarJugador();
        dibujarComputador();
        actualizarMarcadores();
        actualizarBotones();
    }

    private void actualizarMarcadores() {
        // Mientras el computador juega, el saldo sigue mostrando lo que quedó tras apostar:
        // la liquidación solo se hace visible al terminar la secuencia.
        int saldoVisible = turnoComputadorEnCurso
                ? saldoAntesDeApostar - juego.getApuesta()
                : juego.getSaldo();
        saldo.setText(Dialogos.fichas(saldoVisible));
        apuestaVigente.setText(Dialogos.fichas(
                juego.getEstado() == Juego21.Estado.SIN_RONDA ? 0 : juego.getApuesta()));
    }

    /** Habilita cada botón solo cuando la acción es válida en el estado actual. */
    private void actualizarBotones() {
        boolean turnoDelJugador = juego.getEstado() == Juego21.Estado.TURNO_JUGADOR && !turnoComputadorEnCurso;
        botonPedir.setDisable(!turnoDelJugador);
        botonPlantarse.setDisable(!turnoDelJugador);
        botonNuevaRonda.setDisable(turnoComputadorEnCurso
                || juego.getEstado() == Juego21.Estado.TURNO_JUGADOR
                || juego.getSaldo() < Juego21.APUESTA_MINIMA);
        // «Ver reglas» y «Cerrar juego» siempre están disponibles.
    }

    private void dibujarJugador() {
        Mano mano = juego.getManoJugador();
        dibujarCartas(cartasJugador, cartasEnOrden(mano));
        puntajeJugador.setText(mano.estaVacia() ? "—" : String.valueOf(mano.getPuntaje()));
    }

    private void dibujarComputador() {
        Mano mano = juego.getManoComputador();
        // Durante la secuencia las cartas las va mostrando revelarCartasDelComputador.
        List<Carta> cartas = turnoComputadorEnCurso ? List.of() : cartasEnOrden(mano);
        dibujarCartas(cartasComputador, cartas);
        puntajeComputador.setText(cartas.isEmpty() ? "—" : String.valueOf(mano.getPuntaje()));
    }

    /** Vacía la zona y coloca una imagen por cada carta, en el orden en que se repartieron. */
    private void dibujarCartas(HBox zona, List<Carta> cartas) {
        zona.getChildren().clear();

        if (cartas.isEmpty()) {
            Label aviso = new Label(textoZonaVacia(zona));
            aviso.getStyleClass().add("zona-vacia");
            zona.getChildren().add(aviso);
            zona.setSpacing(SEPARACION_CARTAS);
            return;
        }

        for (Carta carta : cartas) {
            zona.getChildren().add(vistaDeCarta(carta));
        }

        // Si hay tantas cartas que no caben, se solapan un poco para no salirse de la zona.
        double separacion = SEPARACION_CARTAS;
        int cantidad = cartas.size();
        if (cantidad > 1 && cantidad * ANCHO_CARTA + (cantidad - 1) * separacion > ANCHO_ZONA_CARTAS) {
            separacion = (ANCHO_ZONA_CARTAS - cantidad * ANCHO_CARTA) / (cantidad - 1);
        }
        zona.setSpacing(separacion);
    }

    private String textoZonaVacia(HBox zona) {
        if (zona != cartasComputador) {
            return "Sin cartas";
        }
        return switch (juego.getEstado()) {
            case SIN_RONDA -> "Sin cartas";
            case TURNO_JUGADOR -> "Esperando su turno";
            case RONDA_TERMINADA -> turnoComputadorEnCurso ? "Recibiendo cartas…" : "No jugó en esta ronda";
        };
    }

    /**
     * Crea la vista de una carta según su nombre de archivo: símbolo del modelo
     * en minúsculas + figura tal como se llama, por ejemplo «corazones_REY.png».
     */
    private ImageView vistaDeCarta(Carta carta) {
        String archivo = carta.getSimbolo().name().toLowerCase() + "_" + carta.getRango().name();
        String ruta = "/com/example/blackjack/img/" + archivo + ".png";
        Image imagen = imagenes.computeIfAbsent(ruta, r -> new Image(getClass().getResourceAsStream(r)));

        ImageView vista = new ImageView(imagen);
        vista.setViewport(RECORTE_CARTA);
        vista.setFitWidth(ANCHO_CARTA);
        vista.setFitHeight(ALTO_CARTA);
        vista.setPreserveRatio(true);
        vista.setSmooth(false); // escala por píxeles: bordes nítidos
        vista.getStyleClass().add("carta");
        return vista;
    }

    // =================== Mensajes ===================

    private void agregarMensaje(String texto) {
        Label mensaje = new Label(texto);
        mensaje.setWrapText(true);
        mensaje.getStyleClass().add("mensaje");
        listaMensajes.getChildren().add(mensaje);
    }

    private void limpiarMensajes() {
        listaMensajes.getChildren().clear();
    }

    // =================== Cartas en texto ===================

    /**
     * Las cartas de la mano en el orden en que se repartieron. La lista enlazada
     * del modelo coloca cada carta nueva al frente, por eso se invierte aquí.
     */
    private List<Carta> cartasEnOrden(Mano mano) {
        List<Carta> cartas = new ArrayList<>();
        for (NodoCarta actual = mano.getPrimera(); actual != null; actual = actual.getSiguiente()) {
            cartas.add(actual.getCarta());
        }
        Collections.reverse(cartas);
        return cartas;
    }

    /** «K de picas», «As de corazones», «7 de tréboles»… */
    private String nombre(Carta carta) {
        String figura = switch (carta.getRango()) {
            case AS -> "As";
            case JOTA -> "J";
            case REINA -> "Q";
            case REY -> "K";
            default -> String.valueOf(carta.getValorBase());
        };
        String palo = switch (carta.getSimbolo()) {
            case PICAS -> "picas";
            case CORAZONES -> "corazones";
            case DIAMANTES -> "diamantes";
            case TREBOLES -> "tréboles";
        };
        return figura + " de " + palo;
    }

    /** «K de picas y 7 de corazones»; con más cartas las separa con comas. */
    private String describir(List<Carta> cartas) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < cartas.size(); i++) {
            if (i > 0) {
                texto.append(i == cartas.size() - 1 ? " y " : ", ");
            }
            texto.append(nombre(cartas.get(i)));
        }
        return texto.toString();
    }
}

