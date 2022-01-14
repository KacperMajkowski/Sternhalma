package main;

import board.Board;
import board.Field;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller of the board. Also connects to server while initializing.
 */
public class Client {

    /**
     *  Reference to the pane containing Circle objects.
     */
    @FXML
    private Pane boardPane;
    /**
     * Reference to label showing player's color.
     */
    @FXML
    private Label playerColorLabel;
    /**
     * Reference to label showing current player's color.
     */
    @FXML
    private Label currentColorLabel;
    /**
     * Reference to button allowing player to skip turn.
     */
    @FXML
    private Button skipButton;

    private Board board;
    private Player player;
    private List<Field> fields = new ArrayList<>();
    private Color playerColor = null;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Handling clicks on circles.
     */
    private EventHandler<? super MouseEvent> OnFieldClickedListener = new EventHandler<>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Circle circle = (Circle) mouseEvent.getSource();
            Field clickedField = getFieldfromCircle(circle);
            player.handleMouseClicked(Objects.requireNonNull(clickedField));
        }
    };

    /**
     * Setting player color.
     */
    private void setPlayerColor() {
        try {
            System.out.println("Waiting for color...");
            var c = in.readLine();
            System.out.println("Received " + c.toString());
            playerColor = Color.web(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        playerColorLabel.setBackground(new Background(new BackgroundFill(playerColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * Setting label to show current player's color.
     */
    private void setCurrentTurn() {
        while (true) {
            Color color = player.getCurrentPlayerColor();
            currentColorLabel.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     * Getting Field by Circle reference.
     * @param circle Circle from which we take field
     * @return Field.
     */
    private Field getFieldfromCircle(Circle circle) {
        for (Field f: fields){
            if (f.getCircle().equals(circle)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Function launched at the start od the application.
     */
    @FXML
    private void initialize() {

        Thread turn = new Thread(this::setCurrentTurn);
        Thread t = new Thread(this::readMessages);
        
        newConnection();
        assignFields();
        createPlayer();
        turn.start();
        createButton();
        player.addStartingPieces();
        player.setWaitForResponses(1);
        t.start();
    }
    
    private void readMessages() {
        player.readServerMessages();
    }

    /**
     * Adds event handling for skipButton.
     */
    private void createButton() {
        skipButton.setOnAction(actionEvent -> {
            board.deselectAllFields();
            player.skipTurn();
        });
    }

    /**
     * Creating player with board containing all fields.
     */
    private void createPlayer() {
        setPlayerColor();
        board = new Board(fields);
        player = new Player(board, out, in, playerColor);
    }


    /**
     * Adding fields from board to list with correct coordinates.
     * Attaching EventHandler.
     */
    private void assignFields() {
        int x = 0;
        int y = 0;

        for( Node node : boardPane.getChildren() )
        {
            fields.add(new Field(x,y,(Circle) node));
            node.setOnMouseClicked(OnFieldClickedListener);

            if( x == 12 )
            {
                x = 0;
                y++;
            }
            else
            {
                x++;
            }
        }
    }

    /**
     * Creating dialog through which user can connect.
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

        TextField ipField = new TextField();
        ipField.setText( "localhost");
        TextField portField = new TextField();
        portField.setText("4444");

        grid.add(new Label("IP:"), 0, 0);
        grid.add(ipField, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(portField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Node connectButton = dialog.getDialogPane().lookupButton(connectButtonType);

        ipField.textProperty().addListener(
                (observable, oldValue, newValue) -> connectButton.setDisable(newValue.trim().isEmpty())
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                return new Pair<>(ipField.getText(), portField.getText());
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
                if (port == 4445) {
                    connectToReadGamesServer(host,port);
                }
                else {
                    connectToServer(host, port);
                }
            } catch (Exception e) {
                Alert alert = new Alert( Alert.AlertType.ERROR);
                alert.setTitle("Exception");
                alert.setHeaderText(e.getMessage());

                alert.showAndWait();
                System.exit(0);
            }
        }
    }

    private void connectToReadGamesServer(String host, int port) throws Exception {
        Socket socket = new Socket( host, port );
        out = new PrintWriter( socket.getOutputStream(), true );
        in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Establishing a connection");

        ButtonType connectButtonType = new ButtonType("Watch", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipField = new TextField();
        ipField.setText( "0");

        grid.add(new Label("Game number:"), 0, 0);
        grid.add(ipField, 1, 0);
        dialog.getDialogPane().setContent(grid);

        Node connectButton = dialog.getDialogPane().lookupButton(connectButtonType);

        ipField.textProperty().addListener(
                (observable, oldValue, newValue) -> connectButton.setDisable(newValue.trim().isEmpty())
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                return ipField.getText();
            }
            else{
                System.exit(0);
                return null;
            }
        });

        Optional<String> result = dialog.showAndWait();

        if( result.isPresent() ) {
            try {
                String gameNumber = result.get();
                out.println(gameNumber);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Exception");
                alert.setHeaderText("Incorrect values");

                alert.showAndWait();
                System.exit(0);
            }
        }
    }

    /**
     * Connecting to server and creating dialog allowing user to start the game or wait for more players.
     * @param host host
     * @param port port
     * @throws Exception Unable to connect to server
     */
    private void connectToServer(String host, int port) throws Exception {
        Socket socket = new Socket( host, port );
        out = new PrintWriter( socket.getOutputStream(), true );
        in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        int playersNumber = Integer.parseInt(in.readLine());
        if (playersNumber < 2) {
            out.println("WAIT");
            waitForPlayers();
        }
        else {
            Dialog<String> dialog = new Dialog<>();

            dialog.setHeaderText("There are currently " + playersNumber + "/6 players in the lobby");

            ButtonType start_game = new ButtonType("Start game", ButtonBar.ButtonData.OK_DONE);
            ButtonType wait = new ButtonType("Wait for more players", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(start_game, wait);

            dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(Event::consume);

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

            result.ifPresent(s -> out.println(s));
        }
    }

    /**
     * Creating information for the user that he will be waiting for more players to connect.
     */
    private void waitForPlayers() {
        Alert alert = new Alert( Alert.AlertType.INFORMATION);
        alert.setTitle("Waiting for more players");
        alert.setHeaderText("Please wait while other players are connecting");
        alert.getDialogPane().getScene().getWindow().setOnCloseRequest(Event::consume);
        alert.showAndWait();
    }
}
