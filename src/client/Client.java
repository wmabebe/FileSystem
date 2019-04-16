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
	
	private Hashtable<String,FileHandle> fileHandleTable;
	private Hashtable<FileHandle,String> fileNameTable;
	private Socket socket;
	private String serverHost;
	private int serverPort;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	public static final String HELP = "Available commands:\n"
			   + "open <filename> - Opens a remote file and associates a local fileHandle\n"
			   + "ls - Lists the available files\n"
	           + "make <filename> <contents> - create a file with the given name \n"
	           + "read <filename> [byteSize] - read bytelen bytes from a file named filename\n"
	           + "write <filename> <content> - write content to a file named filename\n"
	           + "rm <filename> - Remove a file\n"
	           + "help - displays this message\n" 
	           + "exit - Exits the client\n";
	
	public Client(String remoteHost,int port) {
		this.fileHandleTable = new Hashtable();
		this.fileNameTable = new Hashtable();
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
    			fileNameTable.put(fileHandle, filename);
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
		String filename = fileNameTable.get(fh) != null ? (String) fileNameTable.get(fh) : "";
		if (filename.equals("")) {
			System.out.println("0 bytes read. File not opened!\n>");
			return 0;
		}
    	String queryString = "read " + filename;
    	Message query = new Message(false,queryString,Type.QUERY);
    	query.setreadSize(data.length);
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
		String filename = fileNameTable.get(fh) != null ? (String) fileNameTable.get(fh) : "";
		if (filename.equals("")) {
			System.out.println("0 bytes written. File not opened!\n>");
			return false;
		}
		String content = new String(data);
		String queryString = "write " + filename + " " + content;
    	Message query = new Message(false,queryString,Type.QUERY);
    	out.writeObject(query);
    	Message response = null;
    	try {
    		response = (Message) in.readObject();
    		if (response.getStatus()) {
    			System.out.println(data.length + " bytes written to "+ filename + " @ " + response.getMessage());
    			return true;
    		}
    		else
    			System.out.println(response.getMessage());

    	}
    	catch (ClassNotFoundException ex) {
    		System.out.println(ex.getMessage());
    	}
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
		FileHandle fh;
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
					fh = (FileHandle) client.fileHandleTable.get(argument);
					if (fh != null) {
						int byteSize = Message.BYTESIZE;
						if (sc.hasNext()) {
							String size = sc.next().trim();
							byteSize = size.matches("\\d+") ? Integer.parseInt(size) : byteSize;
						}
						byte[] bytesRead = new byte[byteSize];
						client.connect();
						client.read(fh,bytesRead);
						client.disconnect();
					}
					else
						System.out.println("0 bytes read. File not opened yet!");
					break;
				case "write":
					fh = (FileHandle) client.fileHandleTable.get(argument);
					if (fh != null) {
						content = sc.hasNextLine() ? sc.nextLine().trim() : "";
						byte[] bytesRead = content.getBytes();
						client.connect();						
						client.write(fh,bytesRead);
						client.disconnect();
					}
					else
						System.out.println("0 bytes written. File not opened yet!");
					break;
				default:
					System.out.print("$ Unknown command '" + command + "'. Type 'HELP' for more information");
					break;
			}
			sc.close();
		}while(! command.equals("quit"));
		scanner.close();
		System.out.println("$ Bye!");
	}

}
