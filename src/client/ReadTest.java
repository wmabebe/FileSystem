package client;


/* standard java classes. */
import java.io.*;
import java.net.Socket;
import java.util.*;

/* fileSystemAPI should be implemented by your client-side file system. */

public class ReadTest implements fileSystemAPI{

	public static void main(String[] args) throws java.lang.InterruptedException, java.io.IOException {
		
		String Filename = "127.0.0.1:7777/largedata.txt"; // file to read
		
		String localhost = "localhost";
		int portNum = 7777;
		 		 
		System.out.println("Defalt IP: localhost; Default Server PortNum: 7777");
				
		/* Initialise the client-side file system. The following line should be replaced by something like:
	     fileSystemAPI fs = new YourClientSideFileSystem() in your version.
		 */
		 
	    ReadTest fs; 
				
		filehandle fh = fs.open(Filename);
		
		long startTime, endTime;
		long turnAround;
		
		int count = 1;
		int totalTime = 0;
		byte[] data = new byte[1024];

		// repeat reading remote data and displaying turnaround time.
		while (true) {
			if (count == 11) {
				break;
			}

			// open file.
			fh = fs.open(Filename);

			// read the whole file, check the time needed.
			startTime = Calendar.getInstance().getTime().getTime();
			int res;
			while (!fs.isEOF(fh)) {

				// read data.
				res = fs.read(fh, data);
				
			}
			endTime = Calendar.getInstance().getTime().getTime();
			turnAround = endTime - startTime;
			
			// print the turnaround time.
			System.out.println("");
			System.out.println("Round "+ count+", This round took " + turnAround + " ms.");
			totalTime += turnAround;

			// wait a bit.
			Thread.sleep(500);
			count++;
		}
		
		System.out.println("Total 10 rounds, Average turnaround time: " + totalTime/10 +" ms");

	}

	@Override
	public filehandle open(String url) throws IOException {
		String fileName = url.substring(url.indexOf("/") + 1);
		System.out.println("Opening: Test/" + fileName);
		File file = new File(dir.getName() +"/" + fileName);
		FileInputStream in = new FileInputStream(file);
		filehandle fh = new filehandle(); 
	    tbl.put(fh.getIndex(), in);
		return fh;
	}

	@Override
	public boolean write(filehandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int read(filehandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean close(filehandle fh) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEOF(filehandle fh) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

}
