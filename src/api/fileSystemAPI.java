package api;

import client.FileHandle;

/* This is the interface to the client-side file system. We assume
   that, once you open the file, you keep a pointer, and whenever
   you write or read, that pointer advances. */

/* I hide the representation of the "filehandle" (given in a separate
   class file). You can change its implementation as you like. */


public interface fileSystemAPI  
{ 
    /* url has form IP:port/path. */
    public abstract FileHandle open(String url) 
	throws java.io.IOException; 

    /* write data starting from the current pointer. return true on success and false on failure. */
    public abstract boolean write(FileHandle fh, byte[] data)
	throws java.io.IOException; 

    /* read data.length bytes from the current position; return the number of bytes actually read */
    public abstract int read(FileHandle fh, byte [] data)
	throws java.io.IOException; 

    /* close file. you should discard the file handle. */  
    public abstract boolean close(FileHandle fh)
	throws java.io.IOException; 

    /* check if it is at the end-of-file. */
    public abstract boolean isEOF(FileHandle fh)
	throws java.io.IOException; 

} 
    
	
