package com.example.server;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Test1 {
	
	@Test
	void testColor() {
		Board board = new Board(3, 3);
		for(int r = 0; r < 3; r++) {
			for(int c = 0; c < 3; c++) {
				board.setColor(r, c, Color.WHITE);
			}
		}
		board.setColor(1, 1, Color.RED);
		
		Assertions.assertSame(board.getColor(1, 1), Color.RED);
	}
	
}
