package main;

import board.Board;
import board.Field;
import javafx.scene.paint.Color;

public class Player
{
    private Board board;
    private CommunicationManager communicationManager;
    private Color playerColor;
    private Color currentPlayerColor = Color.RED;

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
    public void waitForServerResponse()
    {
        while(!playersTurn()) {
            String response = null;
            try {
                response = communicationManager.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                String[] words = response.split(" ");
                if(words[0].equals("MOVE")) {
                    System.out.println("Words: " + response);
                    board.makeMove(currentPlayerColor, Integer.parseInt(words[1]), Integer.parseInt(words[2]), Integer.parseInt(words[3]), Integer.parseInt(words[4]));
                } else if(words[0].equals("COLOR")) {
        
                    System.out.println("Words: " + response);
                    System.out.println("Words[1]: " + words[1]);
                    System.out.println(Color.valueOf(words[1]));
                    Color color = Color.valueOf(words[1]);
                    setCurrentPlayerColor(color);
                    }
                }
            }
        
        
    }

    /**
     * Handling mouse clicked.
     * @param clickedField
     */
    public void handleMouseClicked(Field clickedField) {
        Field selectedField = board.getSelectedField();
        if (checkIfEventLegal(clickedField.getColor())){
            if (selectedField == clickedField) {
                board.deselectAllFields();
            }
            else if (selectedField == null) {
                if (clickedField.getColor().equals(playerColor)) {
                    board.selectField(clickedField);
                }
            }
            else {
                communicationManager.writeLine("MOVE "+selectedField.getX()+" "+selectedField.getY()+" "+clickedField.getX()+" "+clickedField.getY());
                currentPlayerColor = Color.WHITE;
                waitForServerResponse();
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
}
