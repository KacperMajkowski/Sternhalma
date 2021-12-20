package com.example.server;

import javafx.scene.paint.Color;

import java.util.Objects;

/* Message builder - builds messages to send to all players */
public class MessageBuilder {
	
	String message;
	
	/* Adds word to message if it's a: */
	/* String */
	public MessageBuilder add(String addon) {
		if(Objects.equals(message, null)){
			this.message = addon;
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	/* Int */
	public MessageBuilder add(int addon) {
		if(Objects.equals(message, null)){
			this.message = String.valueOf(addon);
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	/* Color */
	public MessageBuilder add(Color addon) {
		if(Objects.equals(message, null)){
			this.message = String.valueOf(addon);
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	/* Clears the message */
	public void clear() {
		this.message = null;
	}
	
	/* Builds the message */
	public String build() {
		return message;
	}
	
}
