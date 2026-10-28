package com.example.blackjack.vista;

import com.example.blackjack.modelo.Juego21;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * Cuadros de diálogo del juego: apuesta, reglas, resultado de la ronda y
 * confirmación de cierre. Son parte de la Vista: solo muestran información y
 * devuelven lo que el jugador eligió; las reglas de negocio las decide el
 * Modelo y las coordina el Controlador.
 *
 * Todos los métodos bloquean (showAndWait) hasta que el jugador cierra la
 * ventana, así que deben llamarse desde un evento de la interfaz o con
 * Platform.runLater(...), nunca dentro del paso de una animación.
 */
public final class Dialogos {

    /** Aspecto del cuadro de resultado. */
    public enum Tono { GANA, PIERDE, EMPATE }

    /** Lo que el controlador entrega para mostrar el resultado de una ronda. */
    public record DatosResultado(
            Tono tono,
            String titulo,              // «¡Ganaste!», «Perdiste», «Empate»
            int puntajeJugador,
            String puntajeComputador,   // número, o «No jugó» si el computador no repartió
            String causa,               // por qué se resolvió así la ronda
            String resultado,           // «Victoria ordinaria», «Derrota», …
            String etiquetaGanancia,    // «Ganancia» o «Pérdida»
            String ganancia,            // «200 fichas»
            String detalleGanancia) {   // «(se devuelve la apuesta y se gana igual)»
    }

    private static final String HOJA_DE_ESTILOS =
            Dialogos.class.getResource("blackjack.css").toExternalForm();

    private static final String REQUISITOS_APUESTA =
            "La apuesta debe ser un número entero, múltiplo de 10, mínimo "
                    + Juego21.APUESTA_MINIMA + " y no puede superar el saldo.";

    /** Copa de 24 × 24: cuerpo, dos asas, tallo y base. */
    private static final String COPA =
            "M6 3 H18 V9 C18 12.3 15.3 15 12 15 C8.7 15 6 12.3 6 9 Z "
                    + "M6 5 H2.5 V8.5 C2.5 11 4.2 12.8 6.6 13.2 L6.4 11.6 C5.2 11.2 4.3 10.2 4.3 8.5 V6.7 H6 Z "
                    + "M18 5 H21.5 V8.5 C21.5 11 19.8 12.8 17.4 13.2 L17.6 11.6 C18.8 11.2 19.7 10.2 19.7 8.5 V6.7 H18 Z "
                    + "M10.5 15 H13.5 V19 H10.5 Z M7 19 H17 V22 H7 Z";

    private static final DecimalFormat FORMATO_FICHAS = crearFormatoFichas();

    private Dialogos() {
    }

    /** Fichas con punto como separador de miles: 1000 → «1.000». */
    public static String fichas(int cantidad) {
        return FORMATO_FICHAS.format(cantidad);
    }

    private static DecimalFormat crearFormatoFichas() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.ROOT);
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        return new DecimalFormat("#,##0", simbolos);
    }

    // =================== Ingreso de apuesta ===================

    /**
     * Pide la apuesta. Mientras el validador devuelva un mensaje de error, la
     * ventana sigue abierta y lo muestra; cuando devuelve null, el texto es un
     * entero válido y se entrega. Cancelar (o la ✕) devuelve Optional.empty().
     *
     * @param validar recibe el texto escrito y devuelve el error, o null si es válido
     */
    public static Optional<Integer> pedirApuesta(Window padre, int saldo, Function<String, String> validar) {
        Label instruccion = new Label("Ingresa la cantidad de fichas para apostar:");

        TextField campo = new TextField();
        campo.setPromptText("Ej.: 100");
        campo.getStyleClass().add("campo-apuesta");

        Label saldoDisponible = new Label("Saldo disponible: " + fichas(saldo));
        saldoDisponible.getStyleClass().add("dialogo-secundario");

        Label icono = new Label("i");
        icono.getStyleClass().add("icono-circulo");
        Label texto = new Label(REQUISITOS_APUESTA);
        texto.setWrapText(true);
        HBox caja = new HBox(10, icono, texto);
        caja.setAlignment(Pos.CENTER_LEFT);
        caja.getStyleClass().add("caja-info");

        Button aceptar = new Button("Aceptar");
        aceptar.getStyleClass().add("dialogo-boton-verde");
        aceptar.setDefaultButton(true);
        Button cancelar = new Button("Cancelar");
        cancelar.getStyleClass().add("dialogo-boton-gris");
        cancelar.setCancelButton(true);
        HBox botones = new HBox(10, aceptar, cancelar);
        botones.setAlignment(Pos.CENTER_RIGHT);

        VBox raiz = new VBox(12, instruccion, campo, saldoDisponible, caja, botones);
        raiz.getStyleClass().add("dialogo");
        raiz.setPrefWidth(400);

        Stage ventana = crearVentana(padre, "Apuesta", raiz);
        int[] elegida = {-1};

        // Al corregir el texto, el aviso vuelve a mostrar los requisitos.
        campo.textProperty().addListener((obs, antes, ahora) -> {
            texto.setText(REQUISITOS_APUESTA);
            icono.setText("i");
            caja.getStyleClass().remove("caja-error");
        });

        aceptar.setOnAction(evento -> {
            String error = validar.apply(campo.getText());
            if (error != null) {
                // Rechazo: se explica el motivo y no se cierra; nada cambia en el juego.
                texto.setText(error);
                icono.setText("!");
                if (!caja.getStyleClass().contains("caja-error")) {
                    caja.getStyleClass().add("caja-error");
                }
                campo.requestFocus();
                campo.selectAll();
                return;
            }
            elegida[0] = Integer.parseInt(campo.getText().trim());
            ventana.close();
        });
        cancelar.setOnAction(evento -> ventana.close());

        ventana.setOnShown(evento -> campo.requestFocus());
        ventana.showAndWait();
        return elegida[0] < 0 ? Optional.empty() : Optional.of(elegida[0]);
    }

    // =================== Reglas del juego ===================

    /** Muestra las reglas del juego; solo informa, no cambia nada de la partida. */
    public static void mostrarReglas(Window padre) {
        VBox contenido = new VBox(14);
        contenido.getStyleClass().add("reglas-contenido");
        contenido.getChildren().addAll(
                seccion("Objetivo",
                        "Llegar a 21 puntos, o lo más cerca posible sin pasarte, y superar la mano del computador."),
                seccion("Valores de las cartas",
                        "Del 2 al 10: su valor numérico.",
                        "J, Q y K: 10 puntos.",
                        "As: 1 u 11 puntos (se ajusta automáticamente al mayor puntaje sin pasar de 21).",
                        "Los palos no modifican el puntaje."),
                seccion("Jugadas especiales",
                        "Blackjack: As + una carta de 10 puntos en las dos cartas iniciales.",
                        "Veintiuno: 21 sin ser Blackjack (un 21 de tres o más cartas no es Blackjack).",
                        "Veinte y medio: las dos primeras cartas suman 14. Puedes pedir o plantarte.",
                        "Pasarse: puntaje mayor a 21 después de ajustar los ases."),
                seccion("Turno del computador",
                        "Recibe sus dos cartas cuando termina tu turno.",
                        "Pide carta mientras tenga menos de 17 y se planta con 17 a 21.",
                        "Si se pasa de 21, pierde. Decide solo por su propia mano."),
                seccion("Quién gana",
                        "Blackjack le gana a cualquier mano que no sea Blackjack; dos Blackjack empatan.",
                        "Veinte y medio le gana a cualquier mano de 20 o menos, pierde contra cualquier 21 "
                                + "(incluido el Blackjack) y empata con otro Veinte y medio.",
                        "En los demás casos gana el puntaje mayor; con puntajes iguales hay empate.",
                        "Si te pasas de 21, pierdes de inmediato y el computador no juega."),
                seccion("Apuestas y saldo",
                        "Saldo inicial: " + fichas(Juego21.SALDO_INICIAL) + " fichas.",
                        "Apuesta mínima: " + Juego21.APUESTA_MINIMA + " fichas.",
                        "Debe ser un múltiplo de 10.",
                        "No puede superar el saldo.",
                        "Se descuenta al iniciar la ronda y no cambia durante ella."),
                seccion("Resultado de la ronda",
                        "Derrota: pierdes la apuesta.",
                        "Empate: se devuelve la apuesta.",
                        "Victoria ordinaria: se devuelve la apuesta y se gana igual.",
                        "Victoria con Blackjack: se devuelve la apuesta y se gana 1,5 veces."));

        ScrollPane desplazable = new ScrollPane(contenido);
        desplazable.setFitToWidth(true);
        desplazable.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(desplazable, Priority.ALWAYS);

        Button cerrar = new Button("Cerrar");
        cerrar.getStyleClass().add("dialogo-boton-gris");
        cerrar.setDefaultButton(true);
        cerrar.setCancelButton(true);
        HBox botones = new HBox(cerrar);
        botones.setAlignment(Pos.CENTER_RIGHT);

        VBox raiz = new VBox(12, desplazable, botones);
        raiz.getStyleClass().add("dialogo");
        raiz.setPrefSize(500, 620);

        Stage ventana = crearVentana(padre, "Reglas del juego", raiz);
        cerrar.setOnAction(evento -> ventana.close());
        ventana.showAndWait();
    }

    private static Node seccion(String titulo, String... lineas) {
        Label encabezado = new Label(titulo);
        encabezado.getStyleClass().add("regla-seccion-titulo");
        VBox caja = new VBox(4, encabezado);
        for (String linea : lineas) {
            Label punto = new Label("•");
            Label texto = new Label(linea);
            texto.setWrapText(true);
            texto.getStyleClass().add("regla-linea");
            HBox fila = new HBox(8, punto, texto);
            fila.setAlignment(Pos.TOP_LEFT);
            HBox.setHgrow(texto, Priority.ALWAYS);
            caja.getChildren().add(fila);
        }
        return caja;
    }

    // =================== Resultado de la ronda ===================

    /** Muestra el resultado de la ronda y espera a que el jugador pulse «Continuar». */
    public static void mostrarResultado(Window padre, DatosResultado datos) {
        String claseTono = switch (datos.tono()) {
            case GANA -> "tono-gana";
            case PIERDE -> "tono-pierde";
            case EMPATE -> "tono-empate";
        };

        Label titulo = new Label(datos.titulo());
        titulo.getStyleClass().add("titulo-resultado");
        HBox cabecera = new HBox(12, iconoResultado(datos.tono()), titulo);
        cabecera.setAlignment(Pos.CENTER);

        Label causa = new Label(datos.causa());
        causa.setWrapText(true);

        Label detalleGanancia = new Label(datos.detalleGanancia());
        detalleGanancia.setWrapText(true);
        detalleGanancia.getStyleClass().add("detalle-secundario");
        VBox detalle = new VBox(4,
                linea("Resultado: ", datos.resultado()),
                linea(datos.etiquetaGanancia() + ": ", datos.ganancia()),
                detalleGanancia);
        detalle.getStyleClass().add("caja-detalle");

        VBox panel = new VBox(10,
                cabecera,
                linea("Tu puntaje: ", String.valueOf(datos.puntajeJugador())),
                linea("Puntaje del computador: ", datos.puntajeComputador()),
                causa,
                detalle);
        panel.getStyleClass().addAll("panel-resultado", claseTono);

        Button continuar = new Button("Continuar");
        continuar.getStyleClass().add("dialogo-boton-azul");
        continuar.setDefaultButton(true);
        continuar.setCancelButton(true);
        continuar.setMaxWidth(Double.MAX_VALUE);

        VBox raiz = new VBox(14, panel, continuar);
        raiz.getStyleClass().add("dialogo");
        raiz.setPrefWidth(390);

        Stage ventana = crearVentana(padre, "Resultado", raiz);
        continuar.setOnAction(evento -> ventana.close());
        ventana.showAndWait();
    }

    private static HBox linea(String etiqueta, String valor) {
        Label parteFija = new Label(etiqueta);
        Label parteVariable = new Label(valor);
        parteVariable.getStyleClass().add("detalle-fuerte");
        parteVariable.setWrapText(true);
        HBox fila = new HBox(parteFija, parteVariable);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private static Node iconoResultado(Dialogos.Tono tono) {
        if (tono == Tono.GANA) {
            SVGPath copa = new SVGPath();
            copa.setContent(COPA);
            copa.setFill(Color.web("#e0a800"));
            copa.setScaleX(2.3);
            copa.setScaleY(2.3);
            StackPane contenedor = new StackPane(copa);
            contenedor.setMinSize(54, 54);
            contenedor.setPrefSize(54, 54);
            contenedor.setMaxSize(54, 54);
            return contenedor;
        }
        Label simbolo = new Label(tono == Tono.PIERDE ? "✕" : "=");
        simbolo.getStyleClass().add("icono-resultado");
        return simbolo;
    }

    // =================== Confirmación de cierre ===================

    /** Pregunta si de verdad se quiere cerrar el juego. Devuelve true solo si el jugador pulsa «Sí». */
    public static boolean confirmarCierre(Window padre) {
        Label icono = new Label("?");
        icono.getStyleClass().add("icono-pregunta");

        Label pregunta = new Label("¿Estás seguro de que deseas cerrar el juego?");
        pregunta.getStyleClass().add("dialogo-titulo");
        pregunta.setStyle("-fx-font-size: 16px;");
        pregunta.setWrapText(true);
        Label aviso = new Label("Tu progreso se perderá si confirmas.");
        aviso.getStyleClass().add("dialogo-secundario");
        VBox textos = new VBox(6, pregunta, aviso);
        textos.setPrefWidth(280);

        HBox fila = new HBox(14, icono, textos);
        fila.setAlignment(Pos.CENTER_LEFT);

        Button si = new Button("Sí");
        si.getStyleClass().add("dialogo-boton-azul");
        Button no = new Button("No");
        no.getStyleClass().add("dialogo-boton-gris");
        no.setCancelButton(true);
        for (Button boton : new Button[]{si, no}) {
            boton.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(boton, Priority.ALWAYS);
        }
        HBox botones = new HBox(10, si, no);

        VBox raiz = new VBox(18, fila, botones);
        raiz.getStyleClass().add("dialogo");
        raiz.setPrefWidth(400);

        Stage ventana = crearVentana(padre, "Cerrar juego", raiz);
        boolean[] confirmado = {false};
        si.setOnAction(evento -> {
            confirmado[0] = true;
            ventana.close();
        });
        no.setOnAction(evento -> ventana.close());

        // El foco empieza en «No» para que un Enter accidental no cierre el juego.
        ventana.setOnShown(evento -> no.requestFocus());
        ventana.showAndWait();
        return confirmado[0];
    }

    // =================== Utilidades ===================

    /** Ventana modal sobre la principal, con el estilo común de los diálogos. */
    private static Stage crearVentana(Window padre, String titulo, Parent raiz) {
        Stage ventana = new Stage();
        ventana.initOwner(padre);
        ventana.initModality(Modality.WINDOW_MODAL);
        ventana.setTitle(titulo);
        ventana.setResizable(false);
        Scene escena = new Scene(raiz);
        escena.getStylesheets().add(HOJA_DE_ESTILOS);
        ventana.setScene(escena);
        return ventana;
    }
}
