package com.example.server;
import org.junit.Test;

import java.util.Objects;

public class playerColorTest {
	
	@Test
	public void testNextColor2Players() {
		PlayerColors color = PlayerColors.RED;
		
		assert Objects.equals(color.nextPlayer(2), PlayerColors.GREEN);
	}
	
	@Test
	public void testNextColor3Players() {
		PlayerColors color = PlayerColors.BLUE;
		
		assert Objects.equals(color.nextPlayer(3), PlayerColors.YELLOW);
	}
	
	@Test
	public void testNextColor4Players() {
		PlayerColors color = PlayerColors.GREEN;
		
		assert Objects.equals(color.nextPlayer(4), PlayerColors.YELLOW);
		
		color = PlayerColors.YELLOW;
		
		assert Objects.equals(color.nextPlayer(4), PlayerColors.RED);
	}
	
	@Test
	public void testNextColor6Players() {
		PlayerColors color = PlayerColors.RED;
		
		assert Objects.equals(color.nextPlayer(6), PlayerColors.PURPLE);
	}
}
