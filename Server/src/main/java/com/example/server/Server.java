package com.example.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {

    private final ServerSocket serverSocket;
    private final List<Socket> playerSockets = new ArrayList<>();
    int playersNumber;
    private BufferedReader in;
    private PrintWriter out;

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
            in = new BufferedReader( new InputStreamReader( playerSockets.get(playersNumber).getInputStream() ) );
            out = new PrintWriter( playerSockets.get(playersNumber).getOutputStream(), true );
            playersNumber++;
            out.println(playersNumber);
            String read = in.readLine();
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