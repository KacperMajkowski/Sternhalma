package com.example.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {

    private final ServerSocket serverSocket;
    private final List<Socket> playerSockets = new ArrayList<>();
    int playersNumber;
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
    }
}