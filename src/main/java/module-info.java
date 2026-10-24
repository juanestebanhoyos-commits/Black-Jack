module com.example.blackjack {
    requires javafx.controls;
    requires javafx.fxml;

    // FXML accede por reflexión a la vista y al controlador.
    opens com.example.blackjack.controlador to javafx.fxml;
    opens com.example.blackjack.vista to javafx.fxml;

    exports com.example.blackjack;
}
