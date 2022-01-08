package com.example.server;

import javafx.scene.paint.Color;

public interface TestMoveValidity {
	
	/* Determine weather player can perform a move
	 * If can - return true
	 * If not - return false
	 * Depends on weather player just jumped over another pawn
	 * */
	boolean moveLegal(int x1, int y1, int x2, int y2, boolean afterJump);
	
	/* Determines if given move is a legal one spot move */
	boolean oneSpotMove(int x1, int y1, int x2, int y2);
	
	/* Determines if given move is a legal jump move */
	boolean jumpMove(int x1, int y1, int x2, int y2);
	
	/* Determines weather move is legal based on not leaving the goal triangle */
	boolean stayInTriangle(int x1, int y1, int x2, int y2);
	
}
