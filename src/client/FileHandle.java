package client;


/* You can change implementation as you like. This is a simple one. */

public class FileHandle 
{
    /* The "filehandle" is simply an integer.  We keep a counter in a
       static variable "cnt" so that no duplication occurs.  When
       filehandle is discarded its number becomes 0. */

    private int index;
    private static int cnt = 1;

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
}
