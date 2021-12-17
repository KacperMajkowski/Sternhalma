package com.example.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Game {
	
	//
	private Player[][] board;
	
	Player currentPlayer;
	List<Player> players;
	List<Socket> playerSockets;
	int playersNumber;
	
	public Game(List<Socket> playerSocket) {
		this.playerSockets = playerSocket;
		this.playersNumber = playerSockets.size();
		/*Create list of players */
		this.players = new ArrayList<>();
		
		/* Add players to list based on sockets */
		for(int i = 0; i < playersNumber; i++) {
			players.add(new Player(playerSockets.get(i), i));
		}

		setPlayersColors();

		/* Send START message to all players and start their threads */
		var pool = Executors.newFixedThreadPool(200);
		for(Player p : players) {
			p.output.println(p.playerColor.color.toString());
			p.output.println("START");
			pool.execute(p);
		}
		
		randomFirstPlayer();
		
		/* Create board */
		board = new Player[13][17];
	}

	private void setPlayersColors() {
		for(Player p : players) {
			p.playerColor = PlayerColors.RED;
		}
		//TODO set playerColor for each Player. Remember that the colors will depend on the total players number
	}

	/* Check if game has a winner */
	public boolean hasWinner() {
		//TODO Check if winner
		return false;
	}
	
	/* Check if there are no legal moves */
	public boolean boardFilledUp() {
		//TODO No legal moves
		return false;
	}
	
	/* Check if the move is valid */
	public boolean checkIfMoveValid(int x1, int y1, int x2, int y2) {
		//TODO Check if move valid
		return true;
	}
	
	/* Move the pawn */
	public void movePawn(int x1, int y1, int x2, int y2){
		//TODO Move pawn
	}

	public void randomFirstPlayer() {
		currentPlayer = players.get(0);
		//TODO
	}
	
	/* When player tries to move
	* If can - move
	* If not - throw exception
	* */
	public synchronized void move(int x1, int y1, int x2, int y2, Player player) {
		if (player != currentPlayer) {
			currentPlayer.output.println("Not your turn");
		} else if (player.nextPlayer == null) {
			currentPlayer.output.println("No opponent");
		} else if (!checkIfMoveValid(x1, y1, x2, y2)) {
			currentPlayer.output.println("Invalid move");
		} else {
			movePawn(x1, y1, x2, y2);
			currentPlayer = currentPlayer.nextPlayer;
		}
		
		
	}
	
	/**
	 * A Player is identified by a number from 1 to 6. For
	 * communication with the client the player has a socket and associated Scanner
	 * and PrintWriter.
	 */
	class Player implements Runnable {

		PlayerColors playerColor;
		int thisPlayerNumber;
		Player nextPlayer;
		Socket socket;
		Scanner input;
		PrintWriter output;
		
		public Player(Socket socket, int thisPlayerNumber) {
			this.socket = socket;
			this.thisPlayerNumber = thisPlayerNumber;
		}
		
		@Override
		public void run() {
			try {
				setup();
				processCommands();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (nextPlayer != null && nextPlayer.output != null) {
					sendToOther("PLAYER_LEFT");
				}
				try {
					socket.close();
					} catch (IOException ignored) {
				}
			}
		}
		
		private void setup() throws IOException {
			/* Game start */
			input = new Scanner(socket.getInputStream());
			output = new PrintWriter(socket.getOutputStream(), true);
			
			/* Set up next player for each player */
			for(Player p : players) {
				if(p.thisPlayerNumber == ((thisPlayerNumber + 1) % playersNumber)) {
					nextPlayer = p;
				}
			}
			
			if (thisPlayerNumber == 0) {
				sendToSelf("MESSAGE Your move");
			}
		}
		
		private void processCommands() {
			
			int x1;
			int y1;
			int x2;
			int y2;
			
			while (input.hasNextLine()) {
				/* Client commands */
				String command = input.nextLine();
				if (command.startsWith("QUIT")) {
					return;
				} else if (command.startsWith("MOVE")) {
					
					/* Takes command "MOVE (x1)(y1)(x2)(y2) */
					
					x1 = Integer.parseInt(command.substring(6, 6));
					y1 = Integer.parseInt(command.substring(7, 7));
					x2 = Integer.parseInt(command.substring(8, 8));
					y2 = Integer.parseInt(command.substring(9, 9));
					
					
					processMoveCommand(x1, y1, x2, y2);
				}
			}
		}
		
		/* Command process */
		private void processMoveCommand(int x1, int y1, int x2, int y2) {
			try {
				move(x1, y1, x2, y2, this);
				sendToSelf("VALID_MOVE");
				
				/* Sends command OPPONENT_MOVED (playerNumber) (x1)(y1)(x2)(y2) */
				sendToAll("OPPONENT_MOVED " + currentPlayer.thisPlayerNumber + " " + x1 + "" + y1 + "" + x2 + "" + y2);
				if (hasWinner()) {
					sendToSelf("VICTORY");
					sendToOther("DEFEAT");
				} else if (boardFilledUp()) {
					sendToAll("TIE");
				}
			} catch (IllegalStateException e) {
				sendToSelf("MESSAGE " + e.getMessage());
			}
		}
		
		private void sendToSelf(String message) {
			currentPlayer.output.println(message);
		}
		
		private void sendToAll(String message) {
			for(Player p : players) {
				p.output.println(message);
			}
		}
		
		private void sendToOther(String message) {
			for(Player p : players) {
				if(p != currentPlayer) {
					p.output.println(message);
				}
			}
		}
	}
}
