package com.example.server;

import database.DatabaseServer;
import database.GameRepository;
import database.MoveRepository;
import javafx.scene.paint.Color;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Main game class
 */
public class GameReplay extends DatabaseServer {

	/** Current player making the move */
	Player currentPlayer;
	/** List of player sockets */
	List<Socket> playerSockets;
	/** List of players based on playerSockets */
	List<Player> players;
	/** Number of players */
	int playersNumber;
	/** Board rows */
	int rows = 17;
	/** Board columns */
	int columns = 13;
	/** The game board */
	Board board = new Board(rows, columns);
	
	Player spectator;
	
	public static GameRepository gr;
	public static MoveRepository mr;
	
	/**
	 * Game constructor
	 * @param playerSockets List of players sockets
	 */
	public GameReplay(List<Socket> playerSockets) {
		this.playerSockets = playerSockets;
		this.playersNumber = playerSockets.size();
		this.players = new ArrayList<>();
		
		/* Add players to list based on sockets */
		for(int i = 0; i < playersNumber; i++) {
			try {
				players.add(new Player(playerSockets.get(i), i));
			} catch (Exception ignored) {}
		}
		
		spectator = players.get(0);

		/* Set player colors and creates pawns */
		board.setupBoard(playersNumber);
		
		/* Send to all players their color, game board and start their threads */
		var pool = Executors.newFixedThreadPool(200);
		sendToSpectator(Color.BLACK.toString());
		sendToSpectator(createBoardString(board));
		//sendToSpectator("COLOR " + Color.BLACK);
		//sendToSpectator("COLOR " + Color.RED);
		
		SpringApplication.run(DatabaseServer.class);
		pool.execute(spectator);
	}
	
	
	/**
	 * Converts the board to a long string of format (COLOR row column)
	 * repeating for every spot on the board.
	 * @param board Board to send
	 * @return Board string
	 */
	private String createBoardString(Board board) {

		MessageBuilder mb = new MessageBuilder();
		for(int c = 0; c < columns; c++) {
			for(int r = 0; r < rows; r++) {
				mb.add(board.getColor(r, c)).add(r).add(c);
			}
		}
		return mb.build();
	}
	
	/**
	 * Sends message to all players
	 * @param message The message to send
	 */
	public void sendToSpectator(String message) {
		System.out.println("Sent: " + message);
		spectator.output.println(message);
	}
	
	
	/** Main player class */
	class Player implements Runnable {
		
		/** Player number */
		int thisPlayerNumber;
		/** Player's socket */
		Socket socket;
		/** Player's input */
		Scanner input;
		/** Player output */
		PrintWriter output;
		/** Player's color */
		PlayerColors color;
		
		MessageBuilder mb = new MessageBuilder();
		
		
		/**
		 * Player constructor
		 * @param socket Player socket
		 * @param thisPlayerNumber Player number (from 0 to number of players-1)
		 * @throws Exception Exception
		 */
		public Player(Socket socket, int thisPlayerNumber) throws Exception {
			this.socket = socket;
			this.thisPlayerNumber = thisPlayerNumber;
			this.input = new Scanner(socket.getInputStream());
			this.output = new PrintWriter(socket.getOutputStream(), true);
		}
		
		
		/**
		 * Starts player thread
		 */
		@Override
		public void run() {
			try {
				processCommands();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
					} catch (IOException ignored) {
				}
			}
		}
		
		public void sendMoves(int x1, int y1, int x2, int y2) {
			mb.clear();
			mb.add("MOVE").add(x1).add(y1).add(x2).add(y2);
			sendToSpectator(mb.build());
		}
		
		/** Interprets the commands from client */
		private void processCommands() throws Exception {
			
			
			
			int x1 = 5;
			int y1 = 13;
			int x2 = 5;
			int y2 = 12;
			
			while (y2 > 5) {
				System.out.println("Sending move...");
				mb.clear();
				sendToSpectator(mb.add("COLOR").add(Color.RED).build());
				sendMoves(x1, y1, x2, y2);
				Thread.sleep(1000);
				y1--;
				y2--;
			}
		}
		
		

	}
}
