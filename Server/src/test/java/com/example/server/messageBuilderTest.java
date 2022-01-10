package com.example.server;
import javafx.scene.paint.Color;
import org.junit.Test;

import java.util.Objects;

public class messageBuilderTest {
	
	@Test
	public void testSimpleMessage() {
		MessageBuilder mb = new MessageBuilder();
		mb.add("TEST");
		
		String word = mb.build();
		
		assert Objects.equals(word, "TEST");
	}
	
	@Test
	public void testComplexMessage() {
		MessageBuilder mb = new MessageBuilder();
		mb.add("TEST").add(1).add(Color.RED);
		
		String word = mb.build();
		
		System.out.println(word);
		Color red = Color.RED;
		assert Objects.equals(word, "TEST 1 " + red);
	}
	
	@Test
	public void testClearBuilder() {
		MessageBuilder mb = new MessageBuilder();
		mb.add("TEST1");
		mb.clear();
		mb.add("TEST2");
		String word = mb.build();
		assert Objects.equals(word, "TEST2");
	}
}
