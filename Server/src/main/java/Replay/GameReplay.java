package Replay;

import com.example.server.Board;
import com.example.server.MessageBuilder;
import com.example.server.PlayerColors;
import database.DatabaseServer;
import database.GameRepository;
import database.Move;
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
	
	int gameNumber;
	
	public static GameRepository gr;
	public static MoveRepository mr;
	
	/**
	 * Game constructor
	 * @param playerSockets List of players sockets
	 * @param gameNumber Game number
	 */
	public GameReplay(List<Socket> playerSockets, int gameNumber) {
		this.playerSockets = playerSockets;
		this.playersNumber = playerSockets.size();
		this.players = new ArrayList<>();
		this.gameNumber = gameNumber;
		
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
		
		//SpringApplication.run(DatabaseServer.class);
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
		
		List<Move> moves;
		
		
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
			
			moves = getMoves(gameNumber);
			
			for(Move m : moves) {
				System.out.println("Sending move...");
				int x1 = m.getX1();
				int y1 = m.getY1();
				int x2 = m.getX2();
				int y2 = m.getY2();
				String command = m.getCommand();
				String color = m.getColor();
				mb.clear();
				if(command.equals("COLOR")) {
					sendToSpectator(mb.add("COLOR").add(color).build());
				} else if(command.equals("MOVE")) {
					sendMoves(x1, y1, x2, y2);
					Thread.sleep(1000);
				}
			}
		}
	}
}
