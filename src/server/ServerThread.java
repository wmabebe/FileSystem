package server;

import java.io.ObjectOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import api.Message;
import api.Type;


public class ServerThread extends Thread {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	Socket clientSocket;
	private final String fileSystem = "filesystem";
	
	public ServerThread(Socket cSock) throws IOException {
		clientSocket = cSock;
		out =new ObjectOutputStream( clientSocket.getOutputStream());
		//out.flush();
		in = new ObjectInputStream( clientSocket.getInputStream());
		this.start();
	}
	
	@Override
	public void run() {
		String readLine;
		try {
			Message query = (Message) in.readObject();
			System.out.println("Query msg: " + query.getMessage());
			Scanner scanner = new Scanner(query.getMessage());
			String command = scanner.hasNext() ? scanner.next().trim() : "" ;
			String filename = scanner.hasNext() ? scanner.next().trim() : "" ;
			String content = scanner.hasNextLine() ? scanner.nextLine().trim() : "" ;
			
			switch(command) {
			case "open":
					Message response = open(filename);
					out.writeObject(response);
				break;
			case "hello":
				out.flush();
			default:
				break;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Checks if file exists and returns True. If not,
	 * returns False. File is supposed to be in the
	 * virtual filesystem/directory.
	 * 
	 * @param filename  The file that is 
	 * @return True if the file exists
	 */
	private Message open(String filename) {
		File file = new File (fileSystem + "/" + filename);
		if (file.exists()) {
			String lastModified = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date(file.lastModified()));
			return new Message(true,lastModified + " : " + filename,Type.RESPONSE);
		}
		return new Message(false,"File " + filename + " doesn't exist!",Type.RESPONSE);
	}
	
	/**
	 * Reads the file
	 * 
	 * @param filename  File to be read
	 * @param bytes  Bytes read from file
	 * @return
	 */
	
	private Message read(String filename,byte[] bytes) {
		return null;
	}

}
