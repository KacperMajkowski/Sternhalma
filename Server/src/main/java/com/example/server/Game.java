package com.example.server;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import database.*;
import org.springframework.boot.SpringApplication;

/**
 * Main game class
 */
public class Game extends DatabaseServer {

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
	
	public static GameRepository gr;
	public static MoveRepository mr;
	
	/**
	 * Game constructor
	 * @param playerSocket List of players sockets
	 */
	public Game(List<Socket> playerSocket) {
		this.playerSockets = playerSocket;
		this.playersNumber = playerSockets.size();
		this.players = new ArrayList<>();
		
		/* Add players to list based on sockets */
		for(int i = 0; i < playersNumber; i++) {
			try {
				players.add(new Player(playerSockets.get(i), i));
			} catch (Exception ignored) {}
		}

		/* Set player colors and creates pawns */
		setPlayersColors();
		board.setupBoard(playersNumber);
		
		/* Set the first player */
		setFirstPlayer();
		
		/* Send to all players their color, game board and start their threads */
		var pool = Executors.newFixedThreadPool(200);
		for(Player p : players) {
			p.output.println(p.getColor());
			p.output.println(createBoardString(board));
			p.output.println("COLOR " + currentPlayer.getColor());
			pool.execute(p);
		}
		
		SpringApplication.run(DatabaseServer.class);
		
		
	}
	
	/**
	 * Assigns players their colors
	 */
	private void setPlayersColors() {
		if(playersNumber == 2) {
			players.get(0).setColor(PlayerColors.RED);
			players.get(1).setColor(PlayerColors.GREEN);
		} else if (playersNumber == 3) {
			players.get(0).setColor(PlayerColors.RED);
			players.get(1).setColor(PlayerColors.BLUE);
			players.get(2).setColor(PlayerColors.YELLOW);
		} else if (playersNumber == 4) {
			players.get(0).setColor(PlayerColors.RED);
			players.get(1).setColor(PlayerColors.BLUE);
			players.get(2).setColor(PlayerColors.GREEN);
			players.get(3).setColor(PlayerColors.ORANGE);
		} else if (playersNumber == 6) {
			players.get(0).setColor(PlayerColors.RED);
			players.get(1).setColor(PlayerColors.PURPLE);
			players.get(2).setColor(PlayerColors.BLUE);
			players.get(3).setColor(PlayerColors.GREEN);
			players.get(4).setColor(PlayerColors.YELLOW);
			players.get(5).setColor(PlayerColors.ORANGE);
		}
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
	 * Check if the player has won
	 * @return If the player has won
	 */
	private boolean hasWinner() {
		
		for(int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				if(Objects.equals(board.getColor(r, c), currentPlayer.getColor().color)) {
					if(!Objects.equals(board.getTriangle(r, c), currentPlayer.getColor().next().next().next().color)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Move the pawn and update the board
	 * @param x1 Starting x
	 * @param y1 Starting y
	 * @param x2 Ending x
	 * @param y2 Ending y
	 */
	private void movePawn(int x1, int y1, int x2, int y2){
		board.setColor(y1, x1, Color.WHITE);
		board.setColor(y2, x2, currentPlayer.getColor().color);
		InsertMove();
		System.out.println(gr.findAll());
		System.out.println(mr.findAll());
	}
	
	
	/**
	 * Selects first player at random
	 */
	private void setFirstPlayer() {
		Random rand = new Random();
		int p = rand.nextInt(playersNumber);
		currentPlayer = players.get(p);
	}
	
	/**
	 * Reassigns next players
	 */
	private void reassignPlayers() {
		for(Player p : players) {
			p.reassignNextPlayer();
		}
	}
	
	/** Main player class */
	class Player implements Runnable {
		
		/** Player number */
		int thisPlayerNumber;
		/** The next player on the list */
		Player nextPlayer;
		/** Player's socket */
		Socket socket;
		/** Player's input */
		Scanner input;
		/** Player output */
		PrintWriter output;
		/** Player's color */
		PlayerColors color;
		/** Has player just jumped? */
		boolean afterJump;
		/** Has the player already won? */
		boolean alreadyWon = false;
		/** How many players already won */
		int playersWon = 0;
		
		/**
		 * Returns the name of the color
		 * @return Name of the color
		 */
		private PlayerColors getColor() {
			return color;
		}
		
		/**
		 * Sets player color
		 * @param color Color to be set
		 */
		private void setColor(PlayerColors color) {
			this.color = color;
		}
		
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
				setup();
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
		
		/** Runs at a game start */
		private void setup() {
			
			/* Set up next player for each player */
			for(Player p : players) {
				if(p.thisPlayerNumber == ((thisPlayerNumber + 1) % playersNumber)) {
					nextPlayer = p;
				}
			}
		}
		
		/**
		 * Reassigns next players after someone wins
		 */
		private void reassignNextPlayer() {
			if(nextPlayer.alreadyWon) {
				nextPlayer = nextPlayer.nextPlayer;
			}
		}
		
		/** Interprets the commands from client */
		private void processCommands() {
			
			int x1;
			int y1;
			int x2;
			int y2;
			
			while (input.hasNextLine()) {
				/* Client commands */
				String command = input.nextLine();
				String[] words = command.split(" ");
				MessageBuilder mb = new MessageBuilder();
				if (command.startsWith("QUIT")) {
					return;
				} else if (command.startsWith("MOVE")) {
					
					/* Takes command "MOVE (x1) (y1) (x2) (y2) */
					
					x1 = Integer.parseInt(words[1]);
					y1 = Integer.parseInt(words[2]);
					x2 = Integer.parseInt(words[3]);
					y2 = Integer.parseInt(words[4]);
					
					processMoveCommand(x1, y1, x2, y2);
				} else if(command.startsWith("SKIP")) {
					/* Skips turn */
						mb.clear();
						sendToAll(mb.add("COLOR").add(currentPlayer.nextPlayer.getColor().color).build());
						afterJump = false;
						currentPlayer = currentPlayer.nextPlayer;
				}
			}
		}
		
		
		/**
		 * Processes MOVE command
		 * @param x1 Starting x
		 * @param y1 Starting y
		 * @param x2 Target x
		 * @param y2 Target y
		 */
		private void processMoveCommand(int x1, int y1, int x2, int y2) {
			
			MessageBuilder mb = new MessageBuilder();
			MoveTester mt = new MoveTester(x1, y1, x2, y2, currentPlayer.color, afterJump, board);
			
			/* Checks if the move is legal */
			if(mt.testMove()) {
				
				movePawn(x1, y1, x2, y2);
				
				mb.clear();
				mb.add("MOVE").add(x1).add(y1).add(x2).add(y2);
				sendToAll(mb.build());
				
				/* Sends command weather the move is jump move or not */
				mb.clear();
				if(mt.isJumpMove()) {
					afterJump = true;
					mb.add("COLOR").add(currentPlayer.getColor().color).add("ANOTHER");
				} else {
					afterJump = false;
					mb.add("COLOR").add(currentPlayer.nextPlayer.getColor().color);
				}
				sendToAll(mb.build());
				
				/* Sends command if game has a winner */
				mb.clear();
				if (hasWinner()) {
					alreadyWon = true;
					playersWon += 1;
					reassignPlayers();
					System.out.println("WIN " + currentPlayer.getColor());
					sendToAll(mb.add("WIN").add(currentPlayer.getColor().color).add(playersWon).build());
				}
				
				/* Sets current player to next player if move wasn't a jump-move */
				if(!afterJump) {
					currentPlayer = currentPlayer.nextPlayer;
				}
				
				
			} else {
				/*Sends back the same player color if the move was illegal */
				if(afterJump) {
					mb.clear();
					sendToAll(mb.add("COLOR").add(currentPlayer.getColor().color).add("ANOTHER").build());
				} else {
					mb.clear();
					sendToAll(mb.add("COLOR").add(currentPlayer.getColor().color).build());
				}
			}
		}
		
		/**
		 * Sends message to all players
		 * @param message The message to send
		 */
		private void sendToAll(String message) {
			for(Player p : players) {
				p.output.println(message);
			}
			System.out.println("Sent: " + message);
		}
	}
}
