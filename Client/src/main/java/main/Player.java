package main;

import board.Board;
import board.Field;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Player
{
    private Board board;
    private CommunicationManager communicationManager;
    private Color playerColor;
    private Color currentPlayerColor = Color.RED;
    private int waitForResponses = 0;
    private Field lastClickedField;
    private boolean blockedSelecting;

    public void setCurrentPlayerColor(Color currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }

    public Color getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public Player(Board board, CommunicationManager communicationManager, Color playerColor) {
        this.board = board;
        this.communicationManager = communicationManager;
        this.playerColor = playerColor;

        addStartingPieces();

    }

    /**
     * Adding pieces at the start of the game.
     */
    private void addStartingPieces() {
        String boardString;
        try {
            boardString = communicationManager.readLine();
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
                response = communicationManager.readLine();
                System.out.println("Received response: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                String[] words = response.split(" ");
                if (words[0].equals("MOVE")) {
                    board.makeMove(currentPlayerColor, Integer.parseInt(words[1]), Integer.parseInt(words[2]), Integer.parseInt(words[3]), Integer.parseInt(words[4]));
                }
                else if (words[0].equals("COLOR")) {
                    waitForResponses = 0;
                    Color color = Color.web(words[1]);
                    
                    if (color.equals(currentPlayerColor) && color.equals(playerColor)) {
                        blockedSelecting = false;
                        if (words[2].equals("ANOTHER")) {
                            makeAnotherMove();
                        }
                    } else {
                        board.deselectAllFields();
                    }
                    setCurrentPlayerColor(color);
                }
                else if (words[0].equals("WIN")) {
                    winAlert(words[1]);
                }
            }
        }
    }

    private void makeAnotherMove() {
        board.selectField(lastClickedField);
        blockedSelecting = true;
    }

    /**
     * Creating alert informing that one of the players have won.
     * @param word winning player
     */
    private void winAlert(String word) {
        Color c = Color.web(word);
        if (c.equals(playerColor)) {
            Alert alert = new Alert( Alert.AlertType.INFORMATION);
            alert.setTitle("Win");
            alert.setHeaderText("You have won!");
            alert.show();
            System.exit(0);
        }
        else {
            Alert alert = new Alert( Alert.AlertType.INFORMATION);
            alert.setTitle("Win");
            alert.setHeaderText("Player "+currentPlayerColor+" have won");
            alert.show();
        }
    }

    /**
     * Handling mouse clicked.
     * @param clickedField
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
                communicationManager.writeLine(
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
     * @param color
     * @return
     */
    public boolean checkIfEventLegal(Color color) {
        return ((color.equals(playerColor) || color.equals(Color.WHITE)) && playersTurn());
    }

    /**
     * Checking if it's player's turn
     */
    public boolean playersTurn() {
        return (playerColor.equals(currentPlayerColor));
    }

    /**
     * Send message to server that you want to skip turn.
     */
    public void skipTurn() {
        communicationManager.writeLine(new MessageBuilder().add("SKIP").build());
        waitForResponses = 1;
        readServerMessages();
    }
}
