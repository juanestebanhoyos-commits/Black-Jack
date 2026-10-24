package com.example.blackjack;

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
        stage.setTitle("21 Blackjack — duelo contra el computador");
        stage.setScene(new Scene(cargador.load(), 800, 600));
        stage.show();
    }
}
