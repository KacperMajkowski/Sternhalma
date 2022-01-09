package com.example.server;

import javafx.scene.paint.Color;

public class MoveTester implements TestMoveValidity {
	
	int x1;
	int y1;
	int x2;
	int y2;
	boolean afterJump;
	PlayerColors color;
	Board board;
	
	MoveTester(int x1, int y1, int x2, int y2, PlayerColors color, boolean afterJump, Board board) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
		this.afterJump = afterJump;
		this.board = board;
	}
	
	public boolean testMove() {
		return moveLegal(this.x1, this.y1, this.x2, this.y2, this.afterJump, this.color);
	}
	
	public boolean isJumpMove() {
		return jumpMove(this.x1, this.y1, this.x2, this.y2);
	}
	
	
	/* Determine weather player can perform a move
	 * If can - return true
	 * If not - return false
	 * Depends on weather player just jumped over another pawn
	 * */
	public synchronized boolean moveLegal(int x1, int y1, int x2, int y2 , boolean afterJump, PlayerColors color) {
		if(afterJump) {
			return jumpMove(x1, y1, x2, y2) && stayInTriangle(x1, y1, x2, y2, color);
		} else {
			return (oneSpotMove(x1, y1, x2, y2) || jumpMove(x1 ,y1, x2, y2)) && stayInTriangle(x1, y1, x2, y2, color);
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
	public boolean stayInTriangle(int x1, int y1, int x2, int y2, PlayerColors color) {
		
		if((board.getTriangle(y1, x1) == color.next().next().next().color)
				&& (board.getTriangle(y2, x2) != color.next().next().next().color)) {
			return false;
		}
		return true;
	}
	
}
