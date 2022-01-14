package com.example.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Game server
 */
public class ServerReplay {

    /** The server socket */
    private final ServerSocket serverSocket;
    /** The list of player sockets */
    private final List<Socket> playerSockets = new ArrayList<>();
    /** The number of player */
    int playersNumber;
    /** Input */
    private BufferedReader in;
    /** Output */
    private PrintWriter out;
    
    /**
     * Server constructor
     * @param port Server port
     * @throws Exception Exception
     */
    ServerReplay(int port ) throws Exception
    {
        System.out.println("Launching replay server...");
        try
        {
            serverSocket = new ServerSocket(port);
        } catch (Exception e)
        {
            throw new Exception("Cannot access port: " + port);
        }
        connectPlayer();
    }
    
    /**
     * Connect players recursively until START button is pressed
     * @throws Exception Exception
     */
    private void connectPlayer() throws Exception {
        try
        {
            playerSockets.add( serverSocket.accept() );
            playerSockets.add( new Socket() );
            in = new BufferedReader( new InputStreamReader( playerSockets.get(0).getInputStream() ) );
            out = new PrintWriter( playerSockets.get(0).getOutputStream(), true );
            playersNumber = 2;
            //out.println(playersNumber);
            String read = in.readLine();
            createGame();
        }
        catch( Exception e )
        {
            throw new Exception( "Cannot connect to client: " + e.getMessage() );
        }
    }

    /** Creates game based on playerSockets list */
    private void createGame() {
        new GameReplay(playerSockets);
    }
}