package main;

import javafx.scene.paint.Color;

import java.util.Objects;

public class MessageBuilder {
	
	String message;
	
	public MessageBuilder add(String addon) {
		if(Objects.equals(message, null)){
			this.message = addon;
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	public MessageBuilder add(int addon) {
		if(Objects.equals(message, null)){
			this.message = String.valueOf(addon);
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	public MessageBuilder add(Color addon) {
		if(Objects.equals(message, null)){
			this.message = String.valueOf(addon);
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}
	
	public String build() {
		return message;
	}
	
}
