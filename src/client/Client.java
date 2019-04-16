package client;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import api.fileSystemAPI;

import java.util.Date;
import java.util.Hashtable;
import java.util.Scanner;

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
			   + "close <filename> - Closes the local fileHandle for the file\n"
			   + "lm <filename> - Check last modified time of file on server\n"
			   + "ls - Lists the available files\n"
	           + "read <filename> [byteSize] - read bytelen bytes from a file named filename\n"
	           + "write <filename> [-c] <content> - write content to a file named filename\n"
	           + "[ make <filename> <contents> - create a file with the given name ]\n"
	           + "[ rm <filename> - Remove a file ]\n"
	           + "help - displays this message\n" 
	           + "quit/q/exit - Quit the client program\n";
	
	public Client(String remoteHost,int port) {
		this.fileHandleTable = new Hashtable();
		this.fileNameTable = new Hashtable();
		this.serverHost = remoteHost;
		this.serverPort = port;
		
	}
	
	/**
	 * Create a connection with the server, setup socket I/O streams
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(getServerHost(),getServerPort());
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	/**
	 * Disconnect from the server, close streams
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		out.close();
		in.close();
		socket.close();
	}
	
	/**
	 * Returns the IP address of the server
	 * @return serverHost  IP address of server
	 */
	public String getServerHost() {
		return serverHost;
	}
	
	/**
	 * Returns the port of the server application 
	 * @return serverPort  Port number of the server application
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Open a remote file on the server that has name 'filename'.
	 * Associate a FileHandle object with it and return the fileHandle.
	 * @return fileHandle  A handle to the opened file
	 */
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

	/**
	 * Read data from remote file into the provided data
	 * @return bytesRead
	 */
	@Override
	public int read(FileHandle fh, byte[] data) throws IOException {
		if (fh == null || data.length <= 0)
			return 0;
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
    			fh.setLastModified(response.getLastModified());
    			fh.setChache(data);
    			fh.setFlush(false);
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
	
	/**
	 * write specified data into remote file
	 * @return status  True if write was successful
	 */
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

	/**
	 * Discard the filehandle associated with the file
	 * return status  True if closed successfully
	 */
	@Override
	public boolean close(FileHandle fh) throws IOException {
		if (fileNameTable.containsKey(fh)) {
			String filename = fileNameTable.get(fh);
			if (fh.getFlush()) {
				this.connect();
				System.out.println("* Flushing");
				this.write(fh, fh.getCache());		
				this.disconnect();
			}
			fh.discard();
			fileNameTable.remove(fh);
			fileHandleTable.remove(filename);
			System.out.println("Closed: " + filename);
			return true;
		}
		return false;
	}

	@Override
	public boolean isEOF(FileHandle fh) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * List the remote files on the server
	 */
	public void listFS() throws IOException, ClassNotFoundException {
		Message query = new Message(false,"ls",Type.QUERY);
		out.writeObject(query);
    	Message response = (Message) in.readObject();
    	System.out.println("$ " + response.getMessage());
	}
	
	/**
	 * Return the last modified date of the remote file
	 * @return date  Last modified date
	 */
	public Date lastModified(String filename) throws IOException {
		String queryString = "open " + filename;
    	Message query = new Message(false,queryString,Type.QUERY);
    	out.writeObject(query);
    	Message response = null;
    	Date date = null;
    	try {
    		response = (Message) in.readObject();
    		if (response.getStatus()) {
    			Scanner scanner = new Scanner(response.getMessage());
    			if (scanner.hasNext()) {
    				String dateStr1 = scanner.next();
    				if (scanner.hasNext()) {
	    				String dateStr2 = scanner.next();
	    				System.out.println("> Last modified: " + dateStr1 + " " + dateStr2);
	    				date = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").parse(dateStr1 + " " + dateStr2);
	    				return date;
    				}
    			}
    		}
    		else
    			System.out.println(response.getMessage());

    	}
    	catch (ClassNotFoundException ex) {
    		System.out.println(ex.getMessage());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}
	
	/**
	 * This main file is used to run the client
	 */
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
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
						
						client.connect();
						Date lastModified = client.lastModified(argument);
						client.disconnect();
						
						if (fh.getFlush()) {
							System.out.println(fh.getCache().length + " cache bytes read\n> " + new String(fh.getCache()));
							break;
						}
						
						if (fh.getLastModified() != null && fh.getLastModified().compareTo(lastModified) >= 0) {		
							if (fh.getCache() != null && fh.getCache().length >= byteSize) {
								System.out.println(fh.getCache().length + " cache bytes read\n> " + new String(fh.getCache()));
								break;
							}
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
						Scanner sc2 = new Scanner(content);
						if (sc2.hasNext() && sc2.next().equals("-c")) {
							content = sc2.hasNextLine() ? sc2.nextLine() : "";
							fh.setChache(content.getBytes());
							fh.setFlush(true);
							System.out.println(content.getBytes().length + " bytes written to cache!");
						}
						else {
							byte[] bytesRead = content.getBytes();
							client.connect();						
							client.write(fh,bytesRead);
							client.disconnect();
						}
					}
					else
						System.out.println("0 bytes written. File not opened yet!");
					break;
				case "lm":
					client.connect();						
					client.lastModified(argument);
					client.disconnect();
					break;
				case "close":
					fh = (FileHandle) client.fileHandleTable.get(argument);
					if (fh == null)
						System.out.println("Failed: File '" + argument + "' not open!");
					else
						client.close(fh);
					break;
				case "quit": case "exit": case "q":
					command = "quit";
					break;
				case "help":
					System.out.print(HELP);
					break;
				case "ls":
					client.connect();
					client.listFS();
					client.disconnect();
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
