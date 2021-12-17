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
    private String boardString;
    
    public void setBoardString(String boardString) {
        this.boardString = boardString;
    }
    
    public String getBoardString() {
        return boardString;
    }
    
    String input = "";

    public void setCurrentPlayerColor(Color currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }

    public Color getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public Player(Board board, CommunicationManager communicationManager, Color playerColor, String boardString) {
        this.board = board;
        this.communicationManager = communicationManager;
        this.playerColor = playerColor;
        
        System.out.println(boardString);
        String[] words = boardString.split(" ");
    
        for(int i = 0; i < words.length; i+=3) {
            board.addPiece(Color.valueOf(words[i]), Integer.parseInt(words[i+2]), Integer.parseInt(words[i+1]));
        }
    }

    public void waitForServerResponse()
    {
        while(!playersTurn()) {
            String response = null;
            try {
                response = communicationManager.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] words = response.split(" ");
            switch (words[0]) {
                case "MOVE":
                    board.makeMove(currentPlayerColor,Integer.parseInt(words[1]),Integer.parseInt(words[2]),Integer.parseInt(words[3]),Integer.parseInt(words[4]));
                case "COLOR":
                    Color color = Color.web(words[1]);
                    setCurrentPlayerColor(color);
                case "ADD":
                    board.addPiece(Color.web(words[1]),Integer.parseInt(words[2]),Integer.parseInt(words[3]));
                default:

            }
        }
    }

    public void handleMouseClicked(Field clickedField) {
        Field selectedField = board.getSelectedField();
        if (checkIfEventLegal(clickedField.getColor())){
            if (selectedField == clickedField) {
                board.deselectAllFields();
            }
            else if (selectedField == null) {
                board.selectField(clickedField);
            }
            else {
                communicationManager.writeLine("MOVE "+selectedField.getX()+" "+selectedField.getY()+" "+clickedField.getX()+" "+clickedField.getY());
            }
            waitForServerResponse();
        }
    }

    public boolean checkIfEventLegal(Color color) {
        return (color == playerColor && playersTurn());
    }

    public boolean playersTurn() {
        return (playerColor.equals(currentPlayerColor));
    }
}
