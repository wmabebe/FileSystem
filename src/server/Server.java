package server;
import java.rmi.registry.Registry; 
import java.rmi.registry.LocateRegistry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner; 
import api.fileSystemAPI;

/**
 * 
 * @author Waqwoya Abebe
 *
 */

public class Server {
   static Hashtable tbl = new Hashtable();
   static Hashtable tbl2 = new Hashtable();
   static File dir;
   public Server() {} 
   public static void main(String args[]) { 
	  
	   dir = new File("Test");
	   if (!dir.exists()) {
	       if (dir.mkdir()) {
	           System.out.println("Test directory created");
	   } else {
	       System.err.println("Failed to create Test directory");
	       }
	   }
	   
	   
      try { 
    	  	if(args.length < 1) {
    	  		 System.err.println("Run file like: java Server port");
    	  		 System.exit(-1);
    	  	}
    	  	
  	  	int port = Integer.parseInt(args[0]);
    	  	
         Server obj = new Server();
         fileSystemAPI stub = (fileSystemAPI) UnicastRemoteObject.exportObject(obj, 0);  
         Registry registry = LocateRegistry.createRegistry(port);
         
         registry.bind("fileSystemAPI", stub);  
         System.out.println("Server ready"); 
      } catch (Exception e) { 
         System.err.println("Server exception: " + e.toString()); 
         e.printStackTrace(); 
      } 
   }
   
   /**
    * ls - lists the contents of the directory
    */
   
   public String ls() {
	   String ls = "";
	   for (File file :  dir.listFiles()) {
	       if (file.isFile()) {
	           ls += file.getName() + "\n";
	       }
	   }
	   return ls.trim();
   }
   
   /**
    * open2 - opens a file with an outputstream and maps it with a filehandle
    */
   
	public filehandle open2(String url) throws IOException, RemoteException, FileNotFoundException {
		String fileName = url.substring(url.indexOf("/") + 1);
		System.out.println("Opening: Test/" + fileName);
		File file = new File(dir.getName() +"/" + fileName);
		FileOutputStream out = new FileOutputStream(file);
		filehandle fh = new filehandle(); 
	    tbl2.put(fh.getIndex(), out);
		return fh;
	}
   
   /**
    * open - opens a file with an inputstream and maps it with a filehandle
    */

	@Override
	public filehandle open(String url) throws IOException, RemoteException, FileNotFoundException {
		String fileName = url.substring(url.indexOf("/") + 1);
		System.out.println("Opening: Test/" + fileName);
		File file = new File(dir.getName() +"/" + fileName);
		FileInputStream in = new FileInputStream(file);
		filehandle fh = new filehandle(); 
	    tbl.put(fh.getIndex(), in);
		return fh;
	}
	
	/**
	  * write - writes data to the file associated with the given filehandle
	  */
	
	@Override
	public boolean write(filehandle fh, byte[] data) throws IOException, RemoteException {
		if (tbl2.containsKey(fh.getIndex())) {
			FileOutputStream out = (FileOutputStream)tbl2.get(fh.getIndex());
			out.write(data);
			return true;
		}
		return false;
	}
	
	/**
	  * read - reads data from the file associated with the given filehandle, 
	  * returns read bytes
	  */
	
	@Override
	public int read(filehandle fh, byte[] data) throws IOException, RemoteException {
		FileInputStream in = (FileInputStream)tbl.get(fh.getIndex());
		int res = in.read(data);
		return res;
	}
	
	/**
	  * read - reads data from the file associated with the given filehandle
	  * returns read string
	  */
	
	@Override
	public String readFile(filehandle fh, byte[] data) throws IOException, RemoteException {
		int pointer = read(fh,data);
		return new String(data);
	}
	
	/**
	  * close - closes the inputstream
	  */
	
	@Override
	public boolean close(filehandle fh) throws IOException, RemoteException {
		if (tbl.containsKey(fh.getIndex())) {
			FileInputStream in = (FileInputStream)tbl.get(fh.getIndex());
			in.close();
			return true;
		}
		return false;
	}
	
	/**
	  * close - closes the outputstream
	  */
	
	@Override
	public boolean close2(filehandle fh) throws IOException, RemoteException {
		if (tbl2.containsKey(fh.getIndex())) {
			FileOutputStream out = (FileOutputStream)tbl2.get(fh.getIndex());
			out.close();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isEOF(filehandle fh) throws IOException, RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	  * make - create file
	  */
	
	@Override
	public boolean make(String url) throws RemoteException, IOException {
		String fileName = url.substring(url.indexOf("/") + 1);
		System.out.println("Creating: Test/" + fileName);
		File file = new File(dir.getName() + "/" + fileName);
		return file.createNewFile();
	}
	
	/**
	  * rm - delete file
	  */
	
	@Override
	public boolean remove(String url) throws RemoteException, IOException {
		String fileName = url.substring(url.indexOf("/") + 1);
		System.out.println("Removing: Test/" + fileName);
		File file = new File(dir.getName() + "/" + fileName);
		if(file.exists()) {
			return file.delete();
		}
		return false;
	} 
}
