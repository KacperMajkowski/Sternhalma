package com.example.server;

import javafx.scene.paint.Color;


/**
 * Enum storing all players' colors
 */
public enum PlayerColors {
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    PURPLE(Color.PURPLE),
    BLUE(Color.BLUE);

    public final Color color;
    
    /**
     * Enum constructor
     * @param color Current color
     */
    PlayerColors(Color color) {
        this.color = color;
    }
    
    
    /**
     * List of values of this enum
     */
    private static final PlayerColors[] vals = values();
    /** Returns the next color on the list, loops if last */
    public PlayerColors next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }
    
    /**
     * Returns the next color based on the number of players
     * @param playersNumber Number of players
     * @return The color of next player
     */
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
