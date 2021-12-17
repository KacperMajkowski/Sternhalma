package com.example.server;

import javafx.scene.paint.Color;

public class Board {
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	int rows;
	int columns;
	Color[][] board;
	
	Board(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		board = new Color[rows][columns];
	}
	
	Color getColor(int row, int column) {
		return board[row][column];
	}
	
	void setColor(int row, int column, Color color) {
		board[row][column] = color;
	}
	
	void setupBoard() {
		
		for(int c = 0; c < columns; c++) {
			for(int r = 0; r < rows; r++) {
				
				setColor(r, c, Color.WHITE);
				
				if((r == 0 && c == 6) ||
						(r == 1 && c == 5) ||
						(r == 1 && c == 6) ||
						(r == 2 && c == 5) ||
						(r == 2 && c == 6) ||
						(r == 2 && c == 7) ||
						(r == 3 && c == 4) ||
						(r == 3 && c == 5) ||
						(r == 3 && c == 6) ||
						(r == 3 && c == 7)) {
					setColor(r, c, PlayerColors.GREEN.color);
				}
				
				if((r == 4 && c == 9) ||
						(r == 4 && c == 10) ||
						(r == 4 && c == 11) ||
						(r == 4 && c == 12) ||
						(r == 5 && c == 9) ||
						(r == 5 && c == 10) ||
						(r == 5 && c == 11) ||
						(r == 6 && c == 10) ||
						(r == 6 && c == 11) ||
						(r == 7 && c == 10)) {
					setColor(r, c, PlayerColors.YELLOW.color);
				}
				
				if((r == 9 && c == 10) ||
						(r == 10 && c == 10) ||
						(r == 10 && c == 11) ||
						(r == 11 && c == 9) ||
						(r == 11 && c == 10) ||
						(r == 11 && c == 11) ||
						(r == 12 && c == 9) ||
						(r == 12 && c == 10) ||
						(r == 12 && c == 11) ||
						(r == 12 && c == 12)) {
					setColor(r, c, PlayerColors.ORANGE.color);
				}
				
				if((r == 13 && c == 4) ||
						(r == 13 && c == 5) ||
						(r == 13 && c == 6) ||
						(r == 13 && c == 7) ||
						(r == 14 && c == 5) ||
						(r == 14 && c == 6) ||
						(r == 14 && c == 7) ||
						(r == 15 && c == 5) ||
						(r == 15 && c == 6) ||
						(r == 16 && c == 6)) {
					setColor(r, c, PlayerColors.RED.color);
				}
				
				if((r == 9 && c == 1) ||
						(r == 10 && c == 1) ||
						(r == 10 && c == 2) ||
						(r == 11 && c == 0) ||
						(r == 11 && c == 1) ||
						(r == 11 && c == 2) ||
						(r == 12 && c == 0) ||
						(r == 12 && c == 1) ||
						(r == 12 && c == 2) ||
						(r == 12 && c == 3)) {
					setColor(r, c, PlayerColors.PURPLE.color);
				}
				
				if((r == 4 && c == 0) ||
						(r == 4 && c == 1) ||
						(r == 4 && c == 2) ||
						(r == 4 && c == 3) ||
						(r == 5 && c == 0) ||
						(r == 5 && c == 1) ||
						(r == 5 && c == 2) ||
						(r == 6 && c == 1) ||
						(r == 6 && c == 2) ||
						(r == 7 && c == 1)) {
					setColor(r, c, PlayerColors.BLUE.color);
				}
			}
		}
	}
}
