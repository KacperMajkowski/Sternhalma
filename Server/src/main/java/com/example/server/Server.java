package com.example.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ServerSocket serverSocket;
    private final List<Socket> playerSockets = new ArrayList<>();
    int playersNumber;
    //ExecutorService pool = Executors.newFixedThreadPool(200);
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
            communicationManager.writeLine(PlayerColors.RED.color.toString());
            String read = communicationManager.readLine();
            if (read.equals("WAIT")) {
                connectPlayer();
            }
            else if (read.equals("START")) {
                createGame();
            }
        }
        catch( Exception e )
        {
            throw new Exception( "Cannot connect to client: " + e.getMessage() );
        }
    }

    private void createGame() {
        
        new Game(playerSockets);
        
        /* <Moved to Game constructor>
        
        for( int i = 0; i < playersNumber; i++ ) {
            communicationManager = new CommunicationManager(playerSockets.get(i));
            communicationManager.writeLine("START");
            System.out.println("Player " + i + " connected");
            pool.execute(game.new Player(playerSockets.get(i), i));
        }*/
    }
}