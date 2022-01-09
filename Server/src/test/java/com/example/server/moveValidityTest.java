package com.example.server;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class moveValidityTest {
	
	Board setUpTestBoard() {
		Board board = new Board(5, 5);
		for(int r = 0; r < 5; r++) {
			for(int c = 0; c < 5; c++) {
				board.setColor(r, c, Color.WHITE);
			}
		}
		return board;
	}
	
	@Test
	void testColor() {
		Board board = setUpTestBoard();
		board.setColor(1, 1, Color.RED);
		
		Assertions.assertSame(board.getColor(1, 1), Color.RED);
	}
	
	@Test
	void testOneSpotMoveTrue() {
		Board board = setUpTestBoard();
		MoveTester mt = new MoveTester(0, 0, 1, 0, PlayerColors.RED, false, board);
		
		assert mt.testMove();
	}
	
	@Test
	void testOneSpotMoveFalse() {
		Board board = setUpTestBoard();
		MoveTester mt = new MoveTester(0, 0, 2, 0, PlayerColors.RED, false, board);
		
		assert !mt.testMove();
	}
	
	@Test
	void testJumpMoveTrue() {
		Board board = setUpTestBoard();
		board.setColor(0, 1, Color.RED);
		MoveTester mt = new MoveTester(0, 0, 2, 0, PlayerColors.RED, false, board);
		
		assert mt.testMove();
	}
	
	@Test
	void testJumpMoveFalse() {
		Board board = setUpTestBoard();
		MoveTester mt = new MoveTester(0, 0, 2, 0, PlayerColors.RED, false, board);
		
		assert !mt.testMove();
	}
	
	@Test
	void testCantLeaveOpposingTriangle() {
		Board board = new Board(17, 13);
		board.setupBoard(2);
		MoveTester mt = new MoveTester(5, 3, 5, 4, PlayerColors.RED, false, board);

		assert !mt.testMove();
	}
	
	@Test
	void testCanLeaveOwnTriangle() {
		Board board = new Board(17, 13);
		board.setupBoard(2);
		MoveTester mt = new MoveTester(5, 3, 5, 4, PlayerColors.GREEN, false, board);
		
		assert mt.testMove();
	}
}
