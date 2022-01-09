package com.example.server;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Game {

	/* Current player making the move */
	Player currentPlayer;
	/* List of player sockets */
	List<Socket> playerSockets;
	/* List of players based on playerSockets */
	List<Player> players;
	/* Number of players */
	int playersNumber;
	/* Board rows */
	int rows = 17;
	/* Board columns */
	int columns = 13;
	/* The game board */
	Board board = new Board(rows, columns);
	
	/* Main game class */
	public Game(List<Socket> playerSocket) {
		this.playerSockets = playerSocket;
		this.playersNumber = playerSockets.size();
		/*Create list of players */
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

		/* Send to all players their color, game board and start their threads */
		var pool = Executors.newFixedThreadPool(200);
		for(Player p : players) {
			p.output.println(p.getColor());
			p.output.println(createBoardString(board));
			pool.execute(p);
		}
		
		/* Set the first player */
		setFirstPlayer();
	}

	/* Assigns players their colors */
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

	/* Converts the board to a long string of format (COLOR row column)
	* repeating for every spot on the board.
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

	/* Check if game has a winner */
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
	
	/* Move the pawn and update the board */
	private void movePawn(int x1, int y1, int x2, int y2){
		board.setColor(y1, x1, Color.WHITE);
		board.setColor(y2, x2, currentPlayer.getColor().color);
	}
	
	/* Sets the starting player to a player playing RED
	* Might change in the future
	*  */
	private void setFirstPlayer() {
		currentPlayer = players.get(0);
	}
	
	private void reassignPlayers() {
		for(Player p : players) {
			p.reassignNextPlayer();
		}
	}
	
	/* Main player class */
	class Player implements Runnable {
		
		/* Player number */
		int thisPlayerNumber;
		/* The next player on the list */
		Player nextPlayer;
		/* Player's socket */
		Socket socket;
		/* Player's way to communicate with client */
		Scanner input;
		PrintWriter output;
		/* Player's color */
		PlayerColors color;
		/* Has player just jumped? */
		boolean afterJump;
		/**Has the player already won? */
		boolean alreadyWon = false;
		/**How many players already won */
		int playersWon = 0;
		
		/* Returns player color */
		private PlayerColors getColor() {
			return color;
		}
		
		/* Sets player color */
		private void setColor(PlayerColors color) {
			this.color = color;
		}
		
		public Player(Socket socket, int thisPlayerNumber) throws Exception {
			this.socket = socket;
			this.thisPlayerNumber = thisPlayerNumber;
			this.input = new Scanner(socket.getInputStream());
			this.output = new PrintWriter(socket.getOutputStream(), true);
		}
		
		/* Method player calls as a thread
		* If everything is ok - calls setup(),
		* then for the rest of the game - processCommands();
		* */
		@Override
		public void run() {
			try {
				setup();
				processCommands();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				/* if (nextPlayer != null && nextPlayer.output != null) {
					sendToOther("PLAYER_LEFT");
				}*/
				try {
					socket.close();
					} catch (IOException ignored) {
				}
			}
		}
		
		/* Runs at a game start */
		private void setup() {
			
			/* Set up next player for each player */
			for(Player p : players) {
				if(p.thisPlayerNumber == ((thisPlayerNumber + 1) % playersNumber)) {
					nextPlayer = p;
				}
			}
		}
		
		private void reassignNextPlayer() {
			if(nextPlayer.alreadyWon) {
				nextPlayer = nextPlayer.nextPlayer;
			}
		}
		
		/* Interprets the commands from client */
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
		
		
		
		/* Process the MOVE command */
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
		
		/* Send given message to all players */
		private void sendToAll(String message) {
			for(Player p : players) {
				p.output.println(message);
			}
			System.out.println("Sent: " + message);
		}
	}
}
