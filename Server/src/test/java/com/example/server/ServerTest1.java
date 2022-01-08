package com.example.server;

import com.example.server.Board;
import com.example.server.Game;
import com.example.server.Server;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.Assert;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerTest1 {
	
	@Test
	public void test1() throws Exception {
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
