package main;

import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * Class responsible for building messages which are sent to the Server.
 */
public class MessageBuilder {
	
	String message;

	/**
	 * Add string addon to the message.
	 * @param addon string addon
	 * @return message with addon
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
	 * Add integer addon to the message.
	 * @param addon integer addon
	 * @return message with addon
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
	 * Add color addon to the message.
	 * @param addon color addon
	 * @return message with addon
	 */
	public MessageBuilder add(Color addon) {
		if(Objects.equals(message, null)){
			this.message = String.valueOf(addon);
		} else {
			this.message = message + " " + addon;
		}
		return this;
	}

	/**
	 * Clear the message.
	 */
	public void clear() {
		this.message = "";
	}

	/**
	 * Build the message so that it's ready to be sent.
	 * @return message
	 */
	public String build() {
		return message;
	}
	
}
