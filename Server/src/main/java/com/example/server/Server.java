package com.example.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;
    private List<Socket> playerSockets = new ArrayList<>();
    int playersNumber;
    ExecutorService pool = Executors.newFixedThreadPool(200);
    CommunicationManager communicationManager;

    Server( int port ) throws Exception
    {
        System.out.println("Launching server...");
        try
        {
            serverSocket = new ServerSocket(port);
        } catch (Exception e)
        {
            throw new Exception("Cannot access port: " + port);
        }
        connectPlayer();
    }

    private void connectPlayer() throws Exception {
        try
        {
            playerSockets.add( serverSocket.accept() );
            communicationManager = new CommunicationManager(playerSockets.get(playersNumber));
            playersNumber++;
            communicationManager.writeLine(String.valueOf(playersNumber));
            String read = communicationManager.readLine();
            System.out.println(read);
            if (read.equals("WAIT")) {
                System.out.println("WAIT");
                connectPlayer();
            }
            else if (read == "START") {
                createGame();
            }

        }
        catch( Exception e )
        {
            throw new Exception( "Cannot connect to client: " + e.getMessage() );
        }
    }

    private void createGame() {
        Game game = new Game();
        for( int i = 0; i < playersNumber; i++ ) {
            pool.execute(game.new Player(playerSockets.get(i), 1));
        }
    }
}