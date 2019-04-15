package api;

import java.io.Serializable;

public class Message implements Serializable{
	private boolean status;
	private String message;
	private Type type;
	
	public Message(boolean st, String msg, Type t) {
		status = st;
		message = msg;
		this.type = t;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Type getType() {
		return type;
	}
}
