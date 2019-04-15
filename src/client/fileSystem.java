package client;



/* This is a simple example implementation of fileSystemAPI, 
   using local file system calls. 
*/

/* standard java classes. */
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import api.fileSystemAPI;

public class fileSystem implements fileSystemAPI
{ 
    /* It needs a table relating filehandles and real files. */
    private Hashtable fileHandleTable;
    
    public fileSystem() throws RemoteException, NotBoundException {
    	
    	fileHandleTable = new Hashtable();
    	//Registry registry = LocateRegistry.getRegistry(host,port);
    	//fileSystemAPI stub2 = (fileSystemAPI) registry.lookup("fileSystemAPI");
    }

    /* url SHOULD HAVE form IP:port/path, but here simply a file name.*/
    
    public FileHandle open(String url) throws UnknownHostException, IOException 
    {
			FileInputStream in = new FileInputStream(new File(url));
			filehandle fh = new filehandle(); 
	        tbl.put(fh, in);
	        return fh;
    	
//    	int colonIndex = url.indexOf(':');
//    	int slashIndex = url.indexOf('/');
//    	String host = url.substring(0, colonIndex);
//    	int port = Integer.parseInt( url.substring(colonIndex + 1,slashIndex) );
//    	
//    	Socket socket = null;
//        socket = new Socket(host, port);
//
//        File file = new File("testfile.txt");
//        // Get the size of the file
//        long length = file.length();
//        byte[] bytes = new byte[16 * 1024];
//        
//        
//        InputStream in = socket.getInputStream();
//        OutputStream out = socket.getOutputStream();
//        filehandle fh = new filehandle(); 
//
//        fhIn.put(fh, in);
//        fhOut.put(fh, out);
//        
//        out.close();
//        in.close();
//        socket.close();
    	
    	return fh;
    }
	
    /* write is not implemented. */
    public boolean write(FileHandle fh, byte[] data)
	throws java.io.IOException
    {
		return true;
    }

    /* read bytes from the current position. returns the number of bytes read. */
    public int read(FileHandle fh, byte[] data)
	throws java.io.IOException
    {
		FileInputStream in = (FileInputStream) fhIn.get(fh); 
		int res = in.read(data);
		return res;
    }

    /* close file. */  
    public boolean close(FileHandle fh)
	throws java.io.IOException
    {
		((FileInputStream) fhIn.get(fh)).close(); 
		((FileOutputStream) fhOut.get(fh)).close(); 
		fhIn.remove(fh);
		fhOut.remove(fh);
		fh.discard();
		return true;
    }

    /* check if it is the end-of-file. */
    public boolean isEOF(FileHandle fh)
	throws java.io.IOException
    {
		byte[] dummy={0};
		return (((FileInputStream) fhIn.get(fh)).available()==0);
    }

	@Override
	public api.FileHandle open(String url) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean write(api.FileHandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int read(api.FileHandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean close(api.FileHandle fh) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEOF(api.FileHandle fh) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}
} 
    
	
