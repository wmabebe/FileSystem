package server;

import java.io.ObjectOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import api.Message;
import api.Type;

/**
 * This is a server thread class that is instantiated
 * to handle client connections.
 * @author mac
 *
 */

public class ServerThread extends Thread {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	Socket clientSocket;
	private final static String FILESYSTEM = "filesystem";
	
	public ServerThread(Socket cSock) throws IOException {
		clientSocket = cSock;
		out =new ObjectOutputStream( clientSocket.getOutputStream());
		in = new ObjectInputStream( clientSocket.getInputStream());
		this.start();
	}
	
	/**
	 * This method is called whenever start() is invoked in the constructor.
	 * It listens and switches client commands. 
	 */
	
	@Override
	public void run() {
		String readLine;
		try {
			Message query = (Message) in.readObject();
			Message response;
			System.out.println("Query msg: " + query.getMessage());
			Scanner scanner = new Scanner(query.getMessage());
			String command = scanner.hasNext() ? scanner.next().trim() : "" ;
			String filename = scanner.hasNext() ? scanner.next().trim() : "" ;
			String content = scanner.hasNextLine() ? scanner.nextLine().trim() : "" ;
			
			switch(command) {
			case "open":
					response = open(filename);
					out.writeObject(response);
				break;
			case "read":
					response = read(filename,query.getOffset(),query.getReadSize());
					out.writeObject(response);
				break;
			case "write":
					response = write(filename,query.getOffset(),content.getBytes());
					out.writeObject(response);
				break;
			case "ls":
				File file = new File (FILESYSTEM + "/");
				String list = "";
				for (String fname : file.list())
					list += fname + " ";
				response = new Message(true,list,Type.RESPONSE);
				out.writeObject(response);
				break;
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
		finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		File file = new File (FILESYSTEM + "/" + filename);
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
	 * @param offset  Offset value starting from where in the byte array content will be written to
	 * @param readSize  Size in bytes of the byteArray (Content to be read)
	 * @return
	 * @throws IOException, FileNotFoundException 
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 */
	
	private Message read(String filename,int offset,int readSize) throws IOException, FileNotFoundException {
		File file = new File (FILESYSTEM + "/" + filename);
		if (file.exists()) {
			FileInputStream fStream = new FileInputStream(file);
			byte[] data = new byte [readSize];
			if (offset < file.length())
				fStream.skip(offset);
			else
				return  new Message(false,"EOF!",Type.RESPONSE);
			fStream.read(data);
			System.out.println("Read: " + new String(data));
			Message message = new Message(true,new String(data),Type.RESPONSE);
			String lastModifiedStr = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date(file.lastModified()));
			Date lastModified;
			try {
				lastModified = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").parse(lastModifiedStr);
				message.setLastModified(lastModified);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			fStream.close();
			return message;
		}
		return new Message(false,"Failed to locate file " + filename,Type.RESPONSE);
	}
	
	/**
	 * This method writes the specified data, starting from the offset
	 * value in the byte array data, to the specified byte array.
	 * 
	 * @param filename
	 * @param offset
	 * @param data
	 * @return
	 * @throws IOException 
	 */
	private Message write (String filename,int offset,byte[] data) throws IOException {
		File file = new File (FILESYSTEM + "/" + filename);
		if (file.exists()) {
			FileOutputStream fStream = new FileOutputStream(file);
			fStream.write(data, offset, data.length);
			String lastModified = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date(file.lastModified()));
			Message message = new Message(true,lastModified,Type.RESPONSE);
			fStream.close();
			return message;
		}
		return new Message(false,"Failed to locate file " + filename,Type.RESPONSE);
	}

}
