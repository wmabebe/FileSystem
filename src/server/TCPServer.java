package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is the multithreaded TCP server
 * @author wmabebe
 */

public class TCPServer {
	
	/**
	 * This TCP server listens on a specified port 
	 * and spawns a thread to handle client connections.
	 * 
	 * @param args  arg[0] optional port argument. (Default port=4444)
	 * @throws IOException
	 */
    
	public static void main(String[] args) throws IOException {
        
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 4444;
		ServerSocket serverSocket = new ServerSocket(port);
        try {
        	System.out.println("Listening on " + port + " ...");
        	while (true)
        	{	
        		Socket clientSocket = serverSocket.accept();
        		ServerThread thread = new ServerThread(clientSocket);
        	}
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        finally {
        	serverSocket.close();
        }
    }
}