package com.example.server;

import javafx.scene.paint.Color;

import java.util.Objects;

/** Builds messages to send to all players */
public class MessageBuilder {
	
	/**
	 * Message to send
	 */
	String message;
	
	
	/**
	 * Adds string to the message
	 * @param addon string to add
	 * @return modified message
	 */
	public MessageBuilder add(String addon) {
		if(Objects.equals(message, null)){
			this.message = addon;
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	/**
	 * Adds int to the message
	 * @param addon int to add
	 * @return modified message
	 */
	public MessageBuilder add(int addon) {
		if(Objects.equals(message, null)){
			this.message = String.valueOf(addon);
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	/**
	 * Adds color to the message
	 * @param addon color to add
	 * @return modified message
	 */
	public MessageBuilder add(Color addon) {
		if(Objects.equals(message, null)){
			this.message = String.valueOf(addon);
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	/** Clears the message */
	public void clear() {
		this.message = null;
	}
	
	/**
	 * Builds the message
	 * @return the message
	 */
	public String build() {
		return message;
	}
	
}
