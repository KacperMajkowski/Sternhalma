package com.example.server;

import javafx.scene.paint.Color;

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


    private static PlayerColors[] vals = values();
    public PlayerColors next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }
    
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
