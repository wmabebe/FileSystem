# FileSystem
A stateless file server and client

Run:
  Best way is to import directly into eclipse. Otherwise get into project folder and
    - javac $(find ./* | grep .java) (Haven't tried it myself)
    then
    - java TCPServer [port=4444]
    - java Client [host=localhost] [port=4444]
    - Optionally java ClientTester [host=localhost] [port=4444]
    
Experiments:
  Type the commands in the Client console
  
  1. Listing files in server filesystem directory
    $ ls
  
  2. Last modified time of file
    $ lm testfile.txt
  
  3. Open one of the server files
    $ open testfile.txt
  
  4. Read file contents from server
    $ read testfile.txt [byteSize=128] 
    => Will fail if you haven't opened the file first.
  
  5. Read file from cache
    $ read testfile.txt -c [bytes=128]
    => If you manually modify 'testdata.txt', stale cache gets discarded and new content is read from server again.
    => Remember successive reads from remote have an offset value that starts at 0
    If you want to reset the offset, do
      $ reset testfile.txt
    => Notice you are reading certain number of bytes (default 128) at a time, until you reach EOF and can no longer read.
    => To ignore offset and read everythin in cache, do
    $ read testfile.txt -ca
  
  6. Write to remote file
    $ write testfile.txt This content will be written!
    => 'This content will be written!' will be written to remote file
  
  7. Write to cache
    $ write testfile.txt -c Write this to cache first before flushing!
    $ close testfile.txt
    => Cache contents flushed to server
  
  Running ClientTester.java you can observe the following experimental result
  
  "10 Readings of 'largedata.txt' size = 10,046  bytes
    Remote milliseconds: [299, 176, 130, 137, 169, 126, 216, 145, 129, 151]
    Cache milliseconds: [1, 0, 0, 0, 0, 0, 0, 0, 0, 0]"
    
  In the experiments, I do 10 reads on the remote file 128 bytes at a time, and 10 reads on the cache 128 bytes at a time;
  As expected, caching is much quicker than reading from the server. This goes to show that caching files is quite important
  in saving network bandwidth and improving the application's performance.
    
