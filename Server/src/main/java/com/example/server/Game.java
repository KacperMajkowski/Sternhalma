package com.example.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class Game {
	
	//
	private Player[][] board = new Player[13][17];
	
	Player currentPlayer;
	
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
	
	/* When player tries to move
	* If can - move
	* If not - throw exception
	* */
	public synchronized void move(int x1, int y1, int x2, int y2, Player player) {
		if (player != currentPlayer) {
			throw new IllegalStateException("Not your turn");
		} else if (player.nextPlayer == null) {
			throw new IllegalStateException("You don't have an opponent yet");
		} else if (!checkIfMoveValid(x1, y1, x2, y2)) {
			throw new IllegalStateException("Cell already occupied"); //zamiast tego wyslij message (wlasciwie dowolny)
		}
		
		movePawn(x1, y1, x2, y2);
		currentPlayer = currentPlayer.nextPlayer;
	}
	
	/**
	 * A Player is identified by a number from 1 to 6. For
	 * communication with the client the player has a socket and associated Scanner
	 * and PrintWriter.
	 */
	class Player implements Runnable {
		
		int playerNumber;
		Player nextPlayer;
		Socket socket;
		Scanner input;
		PrintWriter output;
		
		public Player(Socket socket, int playerNumber) {
			this.socket = socket;
			this.playerNumber = playerNumber;
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
					nextPlayer.output.println("OTHER_PLAYER_LEFT");
				}
				try {
					socket.close();
					} catch (IOException e) {
				}
			}
		}
		
		private void setup() throws IOException {
			/* Game start */
			input = new Scanner(socket.getInputStream());
			output = new PrintWriter(socket.getOutputStream(), true);
			if (playerNumber == 1) {
				currentPlayer = this;
				output.println(3);
			} else {
				nextPlayer = currentPlayer;
				nextPlayer.nextPlayer = this; //TODO Change for more than 2 players
				nextPlayer.output.println("MESSAGE Your move");
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
				output.println("VALID_MOVE");
				
				/* Sends command OPPONENT_MOVED (playerNumber) (x1)(y1)(x2)(y2) */
				nextPlayer.output.println("OPPONENT_MOVED " + currentPlayer.playerNumber + " " + x1 + "" + y1 + "" + x2 + "" + y2);
				if (hasWinner()) {
					output.println("VICTORY");
					nextPlayer.output.println("DEFEAT");
				} else if (boardFilledUp()) {
					output.println("TIE");
					nextPlayer.output.println("TIE");
				}
			} catch (IllegalStateException e) {
				output.println("MESSAGE " + e.getMessage());
			}
		}
	}
}
