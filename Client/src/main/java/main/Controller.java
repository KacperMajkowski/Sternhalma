package main;

import javafx.application.Platform;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Controller
{
    /** Referencja do panelu, w którym znajdują się pola typu Circle */
    @FXML
    private Pane boardPane;
    @FXML
    private Label infoBar;

    private CommunicationManager communicationManager;

    private boolean guiBlocked = true;

    /**
     * Funkcja uruchamiana przy starcie aplikacji
     */
    @FXML
    private void initialize()
    {
        newConnection();
        //showAlert( "Połącz się z serwerem, aby zagrać" );
    }

    /**
     * Tworzenie nowego okna dialogowego na wpisanie parametrów połączenia
     */
    private void newConnection()
    {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Establishing a connection");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipAddressField = new TextField();
        ipAddressField.setText( "localhost");
        TextField portField = new TextField();
        portField.setText("4444");

        grid.add(new Label("IP:"), 0, 0);
        grid.add(ipAddressField, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(portField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Node connectButton = dialog.getDialogPane().lookupButton(connectButtonType);

        ipAddressField.textProperty().addListener(
                (observable, oldValue, newValue) -> connectButton.setDisable(newValue.trim().isEmpty())
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                return new Pair<>(ipAddressField.getText(), portField.getText());
            }
            else{
                System.exit(0);
                return null;
            }
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if( result.isPresent() )
        {
            int port;
            String host = result.get().getKey();
            try
            {
                port = Integer.parseInt(result.get().getValue());
            }
            catch( Exception e )
            {
                Alert alert = new Alert( Alert.AlertType.ERROR);
                alert.setTitle("Exception");
                alert.setHeaderText("Incorrect values");

                alert.showAndWait();
                System.exit(0);
                return;
            }

            try {
                connectToServer(host, port);
            } catch (Exception e) {
                Alert alert = new Alert( Alert.AlertType.ERROR);
                alert.setTitle("Exception");
                alert.setHeaderText("Cannot connect to server");

                alert.showAndWait();
                System.exit(0);
            }
        }
    }

    private void connectToServer(String host, int port) throws Exception {

        communicationManager = new CommunicationManager(host, port);
        int playersNumber = Integer.parseInt(communicationManager.readLine()); //czyta liczbe graczy

        if (playersNumber < 2) {
            waitForPlayers();
        }

        Dialog<String> dialog = new Dialog<>();

        dialog.setHeaderText("There are currently" + playersNumber + "player in the lobby");

        ButtonType start_game = new ButtonType("Start game", ButtonBar.ButtonData.OK_DONE);
        ButtonType wait = new ButtonType("Wait for more players", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(start_game, wait);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == start_game) {
                return "START";
            }
            else {
                try {
                    waitForPlayers();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "WAIT";
            }
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(s -> communicationManager.writeLine(s));

    }

    private void waitForPlayers() throws Exception {

        Alert alert = new Alert( Alert.AlertType.INFORMATION);
        alert.setTitle("Waiting for more players");
        alert.setHeaderText("Please wait while other players are connecting");

        if (communicationManager.readLine() != null) {
            alert.close();
        }
    }
}
