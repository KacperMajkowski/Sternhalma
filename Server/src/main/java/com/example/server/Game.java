package com.example.server;

import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Game {

	Player currentPlayer;
	List<Player> players;
	List<Socket> playerSockets;
	int playersNumber;
	int rows = 17;
	int columns = 13;
	Board board = new Board(rows, columns);
	
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

		setPlayersColors();
		board.setupBoard(playersNumber);

		/* Send START message to all players and start their threads */
		var pool = Executors.newFixedThreadPool(200);
		for(Player p : players) {
			p.output.println(p.getColor());
			p.output.println(createBoardString(board));
			pool.execute(p);
		}
		
		setFirstPlayer();
	}

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

	public String createBoardString(Board board) {
		String boardString = "";

		for(int c = 0; c < columns; c++) {
			for(int r = 0; r < rows; r++) {
				boardString += board.getColor(r, c) + " " + r + " " + c + " ";
			}
		}
		return boardString;
	}

	/* Check if game has a winner */
	public boolean hasWinner() {
		
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
	
	/* Move the pawn */
	public void movePawn(int x1, int y1, int x2, int y2){
		board.setColor(y1, x1, Color.WHITE);
		board.setColor(y2, x2, currentPlayer.getColor().color);
	}
	
	/* Imo to że czerwony zawsze zaczyna jest ok */
	private void setFirstPlayer() {
		currentPlayer = players.get(0);
	}
	
	/* When player tries to move
	* If can - move, return true
	* If not - throw exception, return false
	* */
	public synchronized boolean moveLegal(int x1, int y1, int x2, int y2) {
		System.out.println("Received move " + x1 + " " + y1 + " " + x2 + " " + y2);
		
		return (oneSpotMove(x1, y1, x2, y2) || jumpMove(x1 ,y1, x2, y2)) && stayInTriangle(x1, y1, x2, y2);
	}
	
	public boolean oneSpotMove(int x1, int y1, int x2, int y2) {
		if(y1 % 2 == 0) {
			if((y2 == y1-1 && ((x2 == x1) || (x2 == x1-1))) ||
					(y2 == y1 && ((x2 == x1-1) || (x2 == x1+1))) ||
					(y2 == y1+1 && ((x2 == x1) || (x2 == x1-1)))){
				return true;
			}
		} else {
			if((y2 == y1-1 && ((x2 == x1) || (x2 == x1+1))) ||
					(y2 == y1 && ((x2 == x1-1) || (x2 == x1+1))) ||
					(y2 == y1+1 && ((x2 == x1) || (x2 == x1+1)))){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean jumpMove(int x1, int y1, int x2, int y2) {
		if(y1 % 2 == 0) {
			if(((y2 == y1-2) && (((x2 == x1-1) && (board.getColor(y1-1,x1-1) != Color.WHITE)) || ((x2 == x1+1) && (board.getColor(y1-1, x1) != Color.WHITE)))) ||
					((y2 == y1) && (((x2 == x1-2) && (board.getColor(y1,x1-1) != Color.WHITE)) || ((x2 == x1+2) && (board.getColor(y1, x1+1) != Color.WHITE)))) ||
					((y2 == y1+2) && (((x2 == x1-1) && (board.getColor(y1+1,x1-1) != Color.WHITE)) || ((x2 == x1+1) && (board.getColor(y1+1, x1) != Color.WHITE))))){
				return true;
			}
		} else {
			if(((y2 == y1-2) && (((x2 == x1-1) && (board.getColor(y1-1, x1) != Color.WHITE)) || ((x2 == x1+1) && (board.getColor(y1-1, x1+1) != Color.WHITE)))) ||
					((y2 == y1) && (((x2 == x1-2) && (board.getColor(y1,x1-1) != Color.WHITE)) || ((x2 == x1+2) && (board.getColor(y1, x1-1) != Color.WHITE)))) ||
					((y2 == y1+2) && (((x2 == x1-1) && (board.getColor(y1+1,x1) != Color.WHITE)) || ((x2 == x1+1) && (board.getColor(y1+1, x1+1) != Color.WHITE))))) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean stayInTriangle(int x1, int y1, int x2, int y2) {
		
		if((board.getTriangle(y1, x1) == currentPlayer.getColor().next().next().next().color)
			&& (board.getTriangle(y2, x2) != currentPlayer.getColor().next().next().next().color)) {
				return false;
		}
		return true;
	}
	
	class Player implements Runnable {
		
		int thisPlayerNumber;
		Player nextPlayer;
		Socket socket;
		Scanner input;
		PrintWriter output;
		PlayerColors color;
		
		public PlayerColors getColor() {
			return color;
		}
		
		public void setColor(PlayerColors color) {
			this.color = color;
		}
		
		public Player(Socket socket, int thisPlayerNumber) throws Exception {
			this.socket = socket;
			this.thisPlayerNumber = thisPlayerNumber;
			this.input = new Scanner(socket.getInputStream());
			this.output = new PrintWriter(socket.getOutputStream(), true);
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
					//sendToOther("PLAYER_LEFT");
				}
				try {
					socket.close();
					} catch (IOException ignored) {
				}
			}
		}
		
		/* Game start */
		private void setup() {
			
			/* Set up next player for each player */
			for(Player p : players) {
				if(p.thisPlayerNumber == ((thisPlayerNumber + 1) % playersNumber)) {
					nextPlayer = p;
				}
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
					if(Objects.equals(words[1], currentPlayer.getColor().color.toString())) {
						mb.clear();
						sendToAll(mb.add("COLOR").add(currentPlayer.nextPlayer.getColor().color).build());
						currentPlayer = currentPlayer.nextPlayer;
					} else {
						mb.clear();
						sendToAll(mb.add("COLOR").add(currentPlayer.getColor().color).build());
					}
				}
			}
		}
		
		/* Command process */
		private void processMoveCommand(int x1, int y1, int x2, int y2) {
			
			MessageBuilder mb = new MessageBuilder();
			
			if(moveLegal(x1, y1, x2, y2)) {
				
				movePawn(x1, y1, x2, y2);
				
				mb.clear();
				sendToAll(mb.add("MOVE").add(x1).add(y1).add(x2).add(y2).build());
				
				mb.clear();
				sendToAll(mb.add("COLOR").add(currentPlayer.nextPlayer.getColor().color).build());
				
				mb.clear();
				if (hasWinner()) {
					System.out.println("WIN " + currentPlayer.getColor());
					sendToAll(mb.add("WIN").add(currentPlayer.getColor().color).build());
				}
				
				currentPlayer = currentPlayer.nextPlayer;
			} else {
				mb.clear();
				sendToAll(mb.add("COLOR").add(currentPlayer.getColor().color).build());
			}
		}
		
		private void sendToAll(String message) {
			for(Player p : players) {
				p.output.println(message);
			}
			System.out.println("Sent: " + message);
		}
	}
}
