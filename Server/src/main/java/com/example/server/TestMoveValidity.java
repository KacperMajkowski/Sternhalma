package com.example.server;

import javafx.scene.paint.Color;

/**
 * Interface to test move validity
 */
public interface TestMoveValidity {
	
	/** Determine weather player can perform a move
	 * If can - return true
	 * If not - return false
	 * Depends on weather player just jumped over another pawn
	 * */
	boolean moveLegal();
	
	/** Determines if given move is a legal one spot move */
	boolean oneSpotMove();
	
	/** Determines if given move is a legal jump move */
	boolean jumpMove();
	
	/** Determines weather move is legal based on not leaving the goal triangle */
	boolean stayInTriangle();
	
}
