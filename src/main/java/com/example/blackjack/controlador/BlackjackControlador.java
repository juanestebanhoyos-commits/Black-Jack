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
