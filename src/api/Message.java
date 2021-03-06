package api;

import java.io.Serializable;
import java.util.Date;

/**
 * This serializable  Message class is used to send messages
 * to and from the server.
 * @author wmabebe
 *
 */

public class Message implements Serializable{
	private boolean status;
	private String message;
	private Type type;
	private int offset, readSize;
	public final static int BYTESIZE = 128;
	private Date lastModified;
	
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
	
	public void setOffset(int o) {
		offset = o;
	}
	
	public void setreadSize(int n) {
		readSize = n;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getReadSize() {
		return readSize;
	}
	
	public void setLastModified(Date date) {
		lastModified = date;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
}
