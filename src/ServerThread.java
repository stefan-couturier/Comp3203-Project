import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
	private Server       server    = null;
	private Socket           socket    = null;
	private int              ID        = -1;
	private DataInputStream  inputStream  =  null;
	private DataOutputStream outputStream = null;

	   public ServerThread(Server _server, Socket _socket){
		   super();
		   server = _server;
		   socket = _socket;
		   ID     = socket.getPort();
	   }
	   public int getID(){
		   return ID;
	   }
	   public void run()
	   {  System.out.println("Server Thread " + ID + " running.");
	      while (true)
	      {  try
	         {  server.handle(ID, inputStream.readUTF());
	         }
	         catch(IOException ioe)
	         {  System.out.println(ID + " ERROR reading: " + ioe.getMessage());
	            server.remove(ID);
	            stop();
	         }
	      }
	   }
	   public void open() throws IOException {
		   inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		   outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	   }
	   public void close() throws IOException{
		   if (socket != null)    socket.close();
		   if (inputStream != null)  inputStream.close();
		   if (outputStream != null) outputStream.close();
	   }
	   
	   
	   public void send(String msg){
		   try
	       {  outputStream.writeUTF(msg);
	       		outputStream.flush();
	       }
	       catch(IOException ioe)
	       {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
	          server.remove(ID);
	          stop();
	       }
	   }
}
