module com.example.blackjack {
    requires javafx.controls;
    requires javafx.fxml;

    // FXML accede por reflexión al controlador y javafx.graphics
    // construye BlackjackApp al arrancar (no borrar: el arranque falla).
    opens com.example.blackjack.controlador to javafx.fxml;

    exports com.example.blackjack;
}
