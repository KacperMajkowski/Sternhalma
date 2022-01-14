package Replay;

import database.GameRepository;
import database.MoveRepository;

import database.DatabaseServer;
import org.springframework.boot.SpringApplication;

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
public class ServerReplay extends DatabaseServer {

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
    
    int gameNumber;
    
    public static GameRepository gr;
    public static MoveRepository mr;
    
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
     *
     */
    private void connectPlayer() throws Exception {
        try
        {
            SpringApplication.run(DatabaseServer.class);
            playerSockets.add( serverSocket.accept() );
            in = new BufferedReader( new InputStreamReader( playerSockets.get(0).getInputStream() ) );
            out = new PrintWriter( playerSockets.get(0).getOutputStream(), true );
            gameNumber = Integer.parseInt(in.readLine());
            System.out.println("Server got input: " + gameNumber);
            playersNumber = getPlayerNumber(gameNumber).getPlayersNumber();
            
            for(int s = 0; s < playersNumber-1; s++) {
                playerSockets.add(new Socket());
            }
            
            
            createGame();
        }
        catch( Exception e )
        {
            throw new Exception( "Cannot connect to client: " + e.getMessage() );
        }
    }

    /** Creates game based on playerSockets list */
    private void createGame() {
        new GameReplay(playerSockets, gameNumber);
    }
}