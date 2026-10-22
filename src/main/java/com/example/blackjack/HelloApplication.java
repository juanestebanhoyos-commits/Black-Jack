package com.example.blackjack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Punto de arranque provisional de la aplicación.
 * La ventana definitiva se construirá con FXML en la fase de vista.
 */
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("21 Blackjack");
        stage.setScene(new Scene(new Label("Proyecto en construcción"), 360, 240));
        stage.show();
    }
}
