package client;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import api.fileSystemAPI;
import java.util.Hashtable;
import java.util.Scanner;

import api.Type;
import api.Message;


public class Client implements fileSystemAPI{
	
	private Hashtable fileHandleTable;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	public static final String HELP = "Available commands:\n" 
			   + "ls - Lists the available files\n"
	           + "make <filename> <contents> - create a file with the given name \n"
	           + "read <filename> [bytelen] - read bytelen bytes from a file named filename\n"
	           + "write <filename> <content> - write content to a file named filename\n"
	           + "rm <filename> - Remove a file\n"
	           + "help - displays this message\n" 
	           + "exit - Exits the client\n";
	
	public Client(String remoteHost,int port) throws UnknownHostException, IOException {
		this.fileHandleTable = new Hashtable();
		socket = new Socket(remoteHost,port);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public FileHandle open(String filename) throws IOException {
    	FileHandle fileHandle = new FileHandle();
    	String queryString = "open " + filename;
    	Message query = new Message(false,queryString,Type.QUERY);
    	System.out.println("Composed: " + queryString);
    	out.writeObject(query);
    	System.out.println("Written: " + queryString);
    	//out.writeUTF("open " + filename);
    	Message response;
    	try {
    		response = (Message) in.readObject();
    		System.out.println("$ " + response.getMessage() + "\t filehandle = " + fileHandle.getIndex());
    		fileHandleTable.put(fileHandle, filename);
    	}
    	catch (ClassNotFoundException ex) {
    		System.out.println(ex.getMessage());
    	}
    	
		return fileHandle;
	}

	@Override
	public boolean write(FileHandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int read(FileHandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean close(FileHandle fh) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEOF(FileHandle fh) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		String serverHost = args.length > 0 ? args[0] : "localhost";
		int serverPort = args.length > 1 ? Integer.parseInt(args[1]) : 4444;
		Client client = new Client(serverHost,serverPort);
		Scanner scanner = new Scanner(System.in);
		Scanner sc;
		String line,command,argument;
		System.out.print(HELP);
		do {
			System.out.print("\n$ ");
			line = scanner.nextLine();
			sc = new Scanner(line);
			command = sc.hasNext() ? sc.next().trim() : "";
			argument = sc.hasNext() ? sc.next().trim() : "";
			switch (command) {
				case "open":
					System.out.println("opening");
					client.open(argument);
					System.out.println("opened");
					break;
				case "read":
					
					break;
				default:
					//System.out.print("$ Unknown command '" + command + "'. Type 'HELP' for more information");
					break;
			}
			
		}while(scanner.hasNextLine() && ! command.equals("quit"));
		scanner.close();
		System.out.println("$ Bye!");
	}

}
