package main;

import board.Board;
import board.Field;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Objects;

public class Player
{
    private final Board board;
    private final Color playerColor;
    private final BufferedReader in;
    private final PrintWriter out;
    private Color currentPlayerColor = Color.RED;
    private int waitForResponses = 0;
    private Field lastClickedField;
    private boolean blockedSelecting;

    public Player(Board board, PrintWriter out, BufferedReader in, Color playerColor) {
        this.board = board;
        this.out = out;
        this.in = in;
        this.playerColor = playerColor;
    }

    public void setCurrentPlayerColor(Color currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }

    public Color getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    /**
     * Adding pieces at the start of the game.
     */
    public void addStartingPieces() {
        String boardString;
        try {
            boardString = in.readLine();
            String[] words = boardString.split(" ");

            for(int i = 0; i < words.length; i+=3) {
                board.addPiece(Color.valueOf(words[i]), Integer.parseInt(words[i+2]), Integer.parseInt(words[i+1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Waiting for server responses and handling them.
     */
    public void readServerMessages()
    {
        while (!playersTurn() || waitForResponses >0) {
            waitForResponses--;
            String response = null;
            try {
                response = in.readLine();
                System.out.println("Received response: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                String[] words = response.split(" ");
                executeMessages(words);
            }
        }
    }

    /**
     * Executes server messages
     * @param words server message
     */
    private void executeMessages(String[] words) {
        if (words[0].equals("MOVE")) {
            board.makeMove(currentPlayerColor, Integer.parseInt(words[1]), Integer.parseInt(words[2]), Integer.parseInt(words[3]), Integer.parseInt(words[4]));
        }
        else if (words[0].equals("COLOR")) {
            waitForResponses = 0;
            Color color = Color.web(words[1]);

            blockedSelecting = false;
            if (color.equals(currentPlayerColor) && color.equals(playerColor)) {
                if (words[2].equals("ANOTHER")) {
                    makeAnotherMove();
                }
            } else {

                board.deselectAllFields();
            }
            setCurrentPlayerColor(color);
        }
        else if (words[0].equals("WIN")) {
            winAlert(words[1], words[2]);
        }
    }

    /**
     * After jump select piece player jumped with and block selecting others.
     */
    private void makeAnotherMove() {
        if (lastClickedField.getColor().equals(playerColor)) {
            board.selectField(lastClickedField);
        }
        blockedSelecting = true;
    }

    /**
     * Creating alert informing that one of the players have won.
     * @param color winning player
     */
    private void winAlert(String color, String place) {
        Color c = Color.web(color);
        int p = Integer.parseInt(place);
        if (c.equals(playerColor)) {
            Alert alert = new Alert( Alert.AlertType.INFORMATION);
            alert.setTitle("Win");
            alert.setHeaderText("You have placed "+p);
            alert.showAndWait();
            System.exit(0);
        }
        else {
            Alert alert = new Alert( Alert.AlertType.INFORMATION);
            alert.setTitle("Win");
            alert.setHeaderText("Player "+currentPlayerColor+" has placed "+p);
            alert.showAndWait();
        }
    }

    /**
     * Handling mouse clicked.
     * @param clickedField Clicked field
     */
    public void handleMouseClicked(Field clickedField) {
        lastClickedField = clickedField;
        Field selectedField = board.getSelectedField();
        if (checkIfEventLegal(clickedField.getColor())){
            if (selectedField == clickedField && !blockedSelecting) {
                board.deselectAllFields();
            }
            else if (selectedField == null) {
                if (clickedField.getColor().equals(playerColor)) {
                    board.selectField(clickedField);
                }
            }
            else if(!Objects.equals(clickedField.getColor(), playerColor)){
                MessageBuilder mb = new MessageBuilder();
                out.println(
                    mb.add("MOVE")
                    .add(selectedField.getX())
                    .add(selectedField.getY())
                    .add(clickedField.getX())
                    .add(clickedField.getY())
                    .build());
                waitForResponses = 2;
                Thread thread = new Thread(this::readServerMessages);
                thread.start();
            }
        }
    }

    /**
     * Checking if it's player's turn and field he clicked it's clickable.
     * @param color Clicked field color
     * @return True if event is legal. False otherwise.
     */
    private boolean checkIfEventLegal(Color color) {
        return ((color.equals(playerColor) || color.equals(Color.WHITE)) && playersTurn());
    }

    /**
     * Checking if it's player's turn
     */
    private boolean playersTurn() {
        return (playerColor.equals(currentPlayerColor));
    }

    /**
     * Send message to server that you want to skip turn.
     */
    public void skipTurn() {
        if(playersTurn()) {
            out.println(new MessageBuilder().add("SKIP").build());
            waitForResponses = 1;
            Thread thread = new Thread(this::readServerMessages);
            thread.start();
        }
    }
}
