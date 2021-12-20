package com.example.server;

import javafx.scene.paint.Color;

/* Enum storing all possible player colors */
public enum PlayerColors {
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    PURPLE(Color.PURPLE),
    BLUE(Color.BLUE);

    public final Color color;

    PlayerColors(Color color) {
        this.color = color;
    }


    /* Array of player colors */
    private static PlayerColors[] vals = values();
    /* Returns the next color on the list, loops if last */
    public PlayerColors next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }
    
    /* Returns the color of next player in the game based on the number of players */
    public PlayerColors nextPlayer(int playersNumber) {
        if(playersNumber == 2) {
            return next().next().next();
        }
        else if(playersNumber == 3) {
            return next().next();
        }
        else if(playersNumber == 4) {
            if(this == PlayerColors.RED || this == PlayerColors.GREEN) {
                return next();
            } else {
                return next().next();
            }
        }
        else {
            return next();
        }
    }
}
