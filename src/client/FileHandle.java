package client;


/* You can change implementation as you like. This is a simple one. */

public class FileHandle 
{
    /* The "filehandle" is simply an integer.  We keep a counter in a
       static variable "cnt" so that no duplication occurs.  When
       filehandle is discarded its number becomes 0. */

    private int index;
    private static int cnt = 1;
    private byte[] cache = null;

    public FileHandle()
    {
	index=cnt++;
    }

    public boolean isAlive()
    {
	return (this.index!=0);
    }

    /* checks two handles are equal or not. */
    public boolean Equals(FileHandle fh) 
    { 
	return (fh.index==this.index);
    }

    /* discarding a filehandle. you do not have to use this. */
    public void discard()
    {
	index=0;
    }
    
    public int getIndex() {
    	return index;
    }
    
    public void setChache(byte[] bytes) {
    	cache = new byte[bytes.length];
    	for (int i = 0;i < bytes.length ; i++)
    		cache[i] = bytes[i];
    }
    
    public byte[] getCache() {
    	return cache;
    }
    
    @Override
    public String toString() {
    	return this.index + "/" + cnt;
    }
}
