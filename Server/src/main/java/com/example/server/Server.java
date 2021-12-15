package com.example.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    
    public static void main(String[] args) throws Exception {
    
        try (var listener = new ServerSocket(58901)) {
            System.out.println("Sternhalma server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);
            
            /* Add 2 players*/ //TODO add more players
            while (true) {
                Game game = new Game();
                pool.execute(game.new Player(listener.accept(), 1));
                pool.execute(game.new Player(listener.accept(), 2));
            }
        }
    
    }
}