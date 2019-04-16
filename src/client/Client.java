package client;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import api.fileSystemAPI;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Map;

import api.Type;
import api.Message;


public class Client implements fileSystemAPI{
	
	private Hashtable fileHandleTable;
	private Socket socket;
	private String serverHost;
	private int serverPort;
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
	
	public Client(String remoteHost,int port) {
		this.fileHandleTable = new Hashtable();
		this.serverHost = remoteHost;
		this.serverPort = port;
		
	}
	
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(getServerHost(),getServerPort());
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public void disconnect() throws IOException {
		out.close();
		in.close();
		socket.close();
	}
	
	public String getServerHost() {
		return serverHost;
	}
	
	public int getServerPort() {
		return serverPort;
	}

	@Override
	public FileHandle open(String filename) throws IOException {
		FileHandle fileHandle = null;
    	String queryString = "open " + filename;
    	Message query = new Message(false,queryString,Type.QUERY);
    	out.writeObject(query);
    	Message response;
    	try {
    		response = (Message) in.readObject();
    		if (response.getStatus() && ! fileHandleTable.containsKey(filename)) {
    			fileHandle = new FileHandle();
    			fileHandleTable.put(filename, fileHandle);
    		}
    		else if (fileHandleTable.containsKey(filename))
    			fileHandle = (FileHandle) fileHandleTable.get(filename);
    		
    		System.out.println("$ " + response.getMessage() + "\t filehandle = " + fileHandle);
    	}
    	catch (ClassNotFoundException ex) {
    		System.out.println(ex.getMessage());
    	}
    	
		return fileHandle;
	}

	@Override
	public int read(FileHandle fh, byte[] data) throws IOException {
		String filename = "";
		for(Object entry: fileHandleTable.entrySet()){
            if(fh.equals( ((Map.Entry) entry).getValue())){
                filename = (String) ((Map.Entry) entry).getKey();
                break;
            }
        }
		
    	String queryString = "read " + filename;
    	System.out.println(queryString);
    	Message query = new Message(false,queryString,Type.QUERY);
    	query.setOffset(0);
    	query.setreadSize(Message.BYTESIZE);
    	out.writeObject(query);
    	Message response = null;
    	try {
    		response = (Message) in.readObject();
    		if (response.getStatus()) {
    			data = response.getMessage().getBytes();
    			System.out.println(data.length + " bytes read\n> " + response.getMessage());
    		}
    		else
    			System.out.println(response.getMessage());

    	}
    	catch (ClassNotFoundException ex) {
    		System.out.println(ex.getMessage());
    	}

		return data != null ? data.length : 0;

	}
	
	@Override
	public boolean write(FileHandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		return false;
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
		String line,command,argument,content;
		System.out.print(HELP);
		do {
			System.out.print("\n$ ");
			line = scanner.nextLine();
			sc = new Scanner(line);
			command = sc.hasNext() ? sc.next().trim() : "";
			argument = sc.hasNext() ? sc.next().trim() : "";
			switch (command) {
				case "open":
					client.connect();
					client.open(argument);
					client.disconnect();
					break;
				case "read":
					FileHandle fh = (FileHandle) client.fileHandleTable.get(argument);
					if (fh != null) {
						client.connect();
						byte[] bytesRead = new byte[Message.BYTESIZE];
						client.read(fh,bytesRead);
						client.disconnect();
					}
					break;
				case "write":
					content = sc.hasNextLine() ? sc.next().trim() : "";
					break;
				default:
					System.out.print("$ Unknown command '" + command + "'. Type 'HELP' for more information");
					break;
			}
			
		}while(! command.equals("quit"));
		scanner.close();
		System.out.println("$ Bye!");
	}

}
