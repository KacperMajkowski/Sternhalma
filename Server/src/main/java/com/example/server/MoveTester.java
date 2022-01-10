package com.example.server;

import javafx.scene.paint.Color;

/**
 * Test if the move is legal
 */
public class MoveTester implements TestMoveValidity {
	
	/** Starting x */
	int x1;
	/** Starting y */
	int y1;
	/** Target x */
	int x2;
	/** Target y */
	int y2;
	/** Has the player just jumped? */
	boolean afterJump;
	/** Player color */
	PlayerColors color;
	/** Current board */
	Board board;
	
	/**
	 * @param x1 Starting x
	 * @param y1 Starting y
	 * @param x2 Target x
	 * @param y2 Target y
	 * @param color Player color
	 * @param afterJump Has the player just jumped?
	 * @param board Current board
	 */
	MoveTester(int x1, int y1, int x2, int y2, PlayerColors color, boolean afterJump, Board board) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
		this.afterJump = afterJump;
		this.board = board;
	}
	
	/**
	 * Call to test a move
	 * @return Weather move is legal
	 */
	public boolean testMove() {
		return moveLegal();
	}
	
	/**
	 * Call to check if the move is a jump move
	 * @return Weather the move is a jump move
	 */
	public boolean isJumpMove() {
		return jumpMove();
	}
	
	
	/**
	 * Check if player can make a move
	 * @return If the move is legal
	 */
	public synchronized boolean moveLegal() {
		if(afterJump) {
			return jumpMove() && stayInTriangle();
		} else {
			return (oneSpotMove() || jumpMove()) && stayInTriangle();
		}
	}
	
	
	/**
	 * Checks if it is a valid one-spot move
	 * @return If the move is legal
	 */
	public boolean oneSpotMove() {
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
	
	/**
	 * Checks if the move is a legal jump-move
	 * @return If the move is legal
	 */
	public boolean jumpMove() {
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
	
	/**
	 * Checks weather player leaves the opposing triangle
	 * @return If the move is legal (in that context)
	 */
	public boolean stayInTriangle() {
		
		if((board.getTriangle(y1, x1) == color.next().next().next().color)
				&& (board.getTriangle(y2, x2) != color.next().next().next().color)) {
			return false;
		}
		return true;
	}
	
}
