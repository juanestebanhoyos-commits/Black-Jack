package com.example.blackjack.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * El Controlador: atiende los eventos de la vista, pide operaciones
 * al modelo (com.example.blackjack.modelo) y actualiza la vista.
 * La conexión con los botones llega en la fase de controlador.
 */
public class BlackjackControlador {

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
        // Comprobación de la fase de vista: la mesa ya acomoda imágenes.
        cartasJugador.getChildren().add(vistaDeCarta("dorso"));
        cartasComputador.getChildren().add(vistaDeCarta("dorso"));
    }

    /**
     * Crea la vista de una carta a partir del nombre de su archivo:
     * el nombre se arma con el palo y el rango del modelo en minúsculas
     * y sin tildes, por ejemplo «corazones_REY.png».
     */
    private ImageView vistaDeCarta(String nombreImagen) {
        String ruta = "/com/example/blackjack/img/" + nombreImagen + ".png";
        ImageView vista = new ImageView(new Image(getClass().getResourceAsStream(ruta)));
        vista.setFitWidth(110); // todas las cartas al mismo tamaño en la mesa
        vista.setPreserveRatio(true);
        return vista;
    }
}
