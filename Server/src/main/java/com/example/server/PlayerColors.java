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

    private PlayerColors(Color color) {
        this.color = color;
    }


    private static PlayerColors[] vals = values();
    public PlayerColors next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }
}
