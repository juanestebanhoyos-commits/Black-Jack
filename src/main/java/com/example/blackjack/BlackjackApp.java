package com.example.blackjack;

import com.example.blackjack.controlador.BlackjackControlador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de arranque de la vista: carga como ventana principal
 * la mesa descrita en vista/blackjack.fxml.
 */
public class BlackjackApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader cargador = new FXMLLoader(BlackjackApp.class.getResource("vista/blackjack.fxml"));
        stage.setTitle("Blackjack");
        stage.setScene(new Scene(cargador.load(), 1000, 640));
        stage.setMinWidth(900);
        stage.setMinHeight(620);

        // La ✕ de la ventana pide la misma confirmación que el botón «Cerrar juego».
        stage.setOnCloseRequest(evento -> {
            if (!((BlackjackControlador) cargador.getController()).cierreConfirmado()) {
                evento.consume();
            }
        });

        stage.show();
    }
}
