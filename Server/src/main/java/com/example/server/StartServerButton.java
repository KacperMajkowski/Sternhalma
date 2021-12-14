package com.example.server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StartServerButton {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Server running!");
    }
}