package com.example.server;

import javafx.scene.paint.Color;

public class MoveTester implements TestMoveValidity {
	
	int x1;
	int y1;
	int x2;
	int y2;
	boolean afterJump;
	Game.Player currentPlayer;
	Board board;
	
	MoveTester(int x1, int y1, int x2, int y2, boolean afterJump, Game.Player currentPlayer, Board board) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.afterJump = afterJump;
		this.currentPlayer = currentPlayer;
		this.board = board;
	}
	
	public boolean testMove() {
		return moveLegal(this.x1, this.y1, this.x2, this.y2, this.afterJump);
	}
	
	public boolean isJumpMove() {
		return jumpMove(this.x1, this.y1, this.x2, this.y2);
	}
	
	
	/* Determine weather player can perform a move
	 * If can - return true
	 * If not - return false
	 * Depends on weather player just jumped over another pawn
	 * */
	public synchronized boolean moveLegal(int x1, int y1, int x2, int y2 ,boolean afterJump) {
		if(afterJump) {
			return jumpMove(x1, y1, x2, y2) && stayInTriangle(x1, y1, x2, y2);
		} else {
			return (oneSpotMove(x1, y1, x2, y2) || jumpMove(x1 ,y1, x2, y2)) && stayInTriangle(x1, y1, x2, y2);
		}
	}
	
	/* Determines if given move is a legal one spot move */
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
	
	/* Determines if given move is a legal jump move */
	public boolean jumpMove(int x1, int y1, int x2, int y2) {
		if(y1 % 2 == 0) {
			if(((y2 == y1-2) && (((x2 == x1-1) && (board.getColor(y1-1,x1-1) != Color.WHITE)) || ((x2 == x1+1) && (board.getColor(y1-1, x1) != Color.WHITE)))) ||
					((y2 == y1) && (((x2 == x1-2) && (board.getColor(y1,x1-1) != Color.WHITE)) || ((x2 == x1+2) && (board.getColor(y1, x1+1) != Color.WHITE)))) ||
					((y2 == y1+2) && (((x2 == x1-1) && (board.getColor(y1+1,x1-1) != Color.WHITE)) || ((x2 == x1+1) && (board.getColor(y1+1, x1) != Color.WHITE))))){
				return true;
			}
		} else {
			if(((y2 == y1-2) && (((x2 == x1-1) && (board.getColor(y1-1, x1) != Color.WHITE)) || ((x2 == x1+1) && (board.getColor(y1-1, x1+1) != Color.WHITE)))) ||
					((y2 == y1) && (((x2 == x1-2) && (board.getColor(y1,x1-1) != Color.WHITE)) || ((x2 == x1+2) && (board.getColor(y1, x1+1) != Color.WHITE)))) ||
					((y2 == y1+2) && (((x2 == x1-1) && (board.getColor(y1+1,x1) != Color.WHITE)) || ((x2 == x1+1) && (board.getColor(y1+1, x1+1) != Color.WHITE))))) {
				return true;
			}
		}
		
		return false;
	}
	
	/* Determines weather move is legal based on not leaving the goal triangle */
	public boolean stayInTriangle(int x1, int y1, int x2, int y2) {
		
		if((board.getTriangle(y1, x1) == currentPlayer.getColor().next().next().next().color)
				&& (board.getTriangle(y2, x2) != currentPlayer.getColor().next().next().next().color)) {
			return false;
		}
		return true;
	}
	
}
