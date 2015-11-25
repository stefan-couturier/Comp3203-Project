import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.net.*;
import java.util.Scanner;

public class Client implements Runnable{
	private final static int PORT_NUM = 45000;
	private static String IP_ADDRESS = "000.0.00.00";
	public static Socket s;
	public static String path;
	
	private Thread thread = null;
	private ClientThread clientThread = null;
	private Socket  socket   = null;
	private DataInputStream  console   = null;
	private DataOutputStream streamOut = null;
	
	

	public static void main(String[] args) {
		
		File[] roots = File.listRoots();
		path = roots[0].toString();
		
		Scanner inputIP = new Scanner(System.in);
		serverConnect(inputIP);
		
		createDirectory(inputIP);
		
		//s = null;
		OutputStream out = null;
		DataOutputStream request = null;
		DataInputStream reply = null;
		try {
			//System.out.print("Connecting to Server...");
			//s = new Socket(IP_ADDRESS, PORT_NUM);
			//System.out.println("\t\t...Connected!\n\n");
			int i = 1;
			String user_input, file_name, direc;
			String user_args[];
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("\t\tWelcome to the Client-Server FTP");
			
			out = s.getOutputStream();
				request = new DataOutputStream(out);
				reply = new DataInputStream(s.getInputStream());
			
			while (i != 0) {
				
				//Read command
				System.out.print("\t\tPlease enter a command:");
				user_input = in.readLine();
				user_args = user_input.trim().split(" ");
				
				//Validate command
				if (user_args.length >2){
					//error too many args
					System.out.println("Too many args");
				}
				else{
					switch (user_args[0]) {
					case "ls":
						request.writeByte(1);// send ls
	
						// receive success response
						
						reply.readBoolean();
						
						System.out.println("Contents of directory:\n");
						// receive list
						receiveMsg(reply);
						
	
						break;
					case "get":
						if (user_args.length!=2){
							//error, not enough args
							System.out.println("ERROR:\tNot enough args");
							break;
						}
						file_name = user_args[1];
	
						request.writeByte(2);// send get signal
						request.writeUTF(file_name);
	
						// get success response
						if(reply.readBoolean()){
							System.out.println("Success");
							// receive file
							receiveFile(reply);
						}
						else
							System.out.println("Failed");
	
	
						break;
					case "put":
						
						if (user_args.length!=2){
							//error, not enough args
							System.out.println("ERROR:\tNot enough args");
							break;
						}
						
	
						// find file
						file_name = user_args[1];
						
	
						request.writeByte(3);// send put signal
	
						// send file
						File aFile = findFile(file_name);
						if (aFile == null)
							request.writeBoolean(false);
						else{
							request.writeBoolean(true);
							sendFile(file_name, request);
						}
						
						
						// get success response
						if(reply.readBoolean())
							System.out.println("Success");
						else
							System.out.println("Failed");
						
	
						break;
						
					case "cd":
						if (user_args.length!=2){
							//error, not enough args
							System.out.println("ERROR:\tNot enough args");
							break;
						}
						direc = user_args[1];
	
						request.writeByte(4);// send cd signal
	
						// send directory name
						request.writeUTF(direc);
						
						// get success response
						if(reply.readBoolean())
							System.out.println("Success");
						else
							System.out.println("Failed");
						
						break;
					case "mkdir":
						
						if (user_args.length!=2){
							//error, not enough args
							System.out.println("ERROR:\tNot enough args");
							break;
						}
						direc = user_args[1];
	
						request.writeByte(5);// send mkdir signal
	
						// send directory name
						request.writeUTF(direc);
						
						
						// get success response
						if(reply.readBoolean())
							System.out.println("Success");
						else
							System.out.println("Failed");
						
						break;
					case "exit":
						System.out.println("\nThank you for using this service!!");
	
						request.writeByte(0);// send exit signal
	
						i = 0;// exit loop
						break;
					default:
						System.out.println("\tWrong input, please try again.");
	
					}
				}
			}
		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
		} finally {
			try {
				if (reply != null)
					reply.close();
				if (request != null)
					request.close();
				if (out != null)
					out.close();
				if (s != null)
					s.close();
			} catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
		inputIP.close();
	}
	
	
	public void run(){
		while (thread != null){
			try{
				streamOut.writeUTF(console.readLine());
	            streamOut.flush();
	         }
	         catch(IOException ioe){
	        	 System.out.println("Sending error: " + ioe.getMessage());
	        	 stop();
	         }
		}
	}
	
	public void handle(String msg){
		if (msg.equals(".bye")){
			System.out.println("Good bye. Press RETURN to exit ...");
	        stop();
	    }
	    else
	    	System.out.println(msg);
	}
	
	public void start() throws IOException{
		console   = new DataInputStream(System.in);
	    streamOut = new DataOutputStream(socket.getOutputStream());
	    if (thread == null){
	    	clientThread = new ClientThread(this, socket);
	        thread = new Thread(this);                   
	        thread.start();
	    }
	}
	
	public void stop(){
		if (thread != null){
			thread.stop();  
	        thread = null;
	    }
	    try{
	    	if (console   != null)  console.close();
	        if (streamOut != null)  streamOut.close();
	        if (socket    != null)  socket.close();
	    }
	    catch(IOException ioe){
	    	System.out.println("Error closing ..."); 
	    }
	    clientThread.close();  
	    clientThread.stop();
	}


	/***************
	 * sendFile() takes a file name, ie:"test.txt" with extension and sends the
	 * file to the client
	 *******************/
	public static void sendFile(String name, DataOutputStream request) throws IOException {

		FileInputStream file_stream = null;
		BufferedInputStream buffer_stream = null;
		DataInputStream dataIn_stream = null;
		dataIn_stream = null;

		String file_path = path + "\\" + name;

		File file = new File(file_path);
		byte[] byte_array = new byte[(int) file.length()];

		try {
			file_stream = new FileInputStream(file);
			buffer_stream = new BufferedInputStream(file_stream);
			dataIn_stream = new DataInputStream(buffer_stream);

			dataIn_stream.readFully(byte_array, 0, byte_array.length);

			request.writeUTF(file.getName());
			request.writeLong((long) byte_array.length);
			request.write(byte_array, 0, byte_array.length);
		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
		} finally {
			try {
				if (dataIn_stream != null)
					dataIn_stream.close();
				if (buffer_stream != null)
					buffer_stream.close();
				if (file_stream != null)
					file_stream.close();
			} catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
	}
	
	private static File findFile(String name){
		String file_path = path + "\\" + name;
		File aFile = new File(file_path);
		if (aFile.exists())
			return aFile;
		return null;
	}

	public static void receiveFile(DataInputStream request) throws IOException {
		int r_byt = 0;
		String f_name = null;
		long f_size = 0;
		OutputStream writer = null;

		try {
			f_name = path.concat((Paths.get(request.readUTF())).getFileName().toString());
			f_size = request.readLong();
			writer = new FileOutputStream(f_name);
			byte[] r_buf = new byte[256];

			while ((r_byt = request.read(r_buf, 0, (int) Math.min(r_buf.length, f_size))) != 1 && f_size > 0) {
				writer.write(r_buf, 0, r_byt);
				f_size -= (long) r_byt;
			}
		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch(Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
	}

	public static void sendCmd(String input, DataOutputStream reply) throws IOException {
		try {
			byte[] buf = new byte[input.length()];
			for (int i = 0; i < input.length(); i++)
				buf[i] = (byte) input.charAt(i);
			reply.writeInt(buf.length);
			reply.write(buf, 0, buf.length);
		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
		}
	}

	public static void receiveMsg(DataInputStream request) throws IOException {
		try {
			int rec = 0;
			int size = request.readInt();
			byte[] buf = new byte[size];
			while((rec = request.read(buf, 0, size)) != 1 && size > 0) {
				size -= rec;
			}
			System.out.println(new String(buf, StandardCharsets.UTF_8)); 
		} catch(Exception e) {
			System.err.println("--error: " + e.getMessage());
		}
	}
	
	private static void createDirectory(Scanner input){
		boolean success = false;
		String d_name = "";
		while (!success){
			//prompt for location
			System.out.println("\nPlease enter forlder name to store files from server.");
			System.out.println("If it doesn't exist then it will be created.");
			System.out.print(path);
			d_name = input.nextLine();
			//if exists
			File aFile = new File(path + d_name);
			if (aFile.exists()){
				//Check permissions
				if (aFile.canWrite()){
					success = true;
					System.out.println("\tdirectory " + aFile.getAbsolutePath() +" already exists and can write");
				}
				else{
					System.out.println("\tdirectory " + aFile.getAbsolutePath() +" already exists and cannot write");
				}
					
				
				
			}
			else{
				success = aFile.mkdir();
				if (success)
					System.out.println("\tDirectory " + aFile.getAbsolutePath() +" Created");
				else
					System.out.println("\tDirectory " + aFile.getAbsolutePath() +" failed to be created");
			}
		}
		path = path + d_name + "\\";
		
	}
	
	private static void serverConnect(Scanner input){
		boolean success = false;
		
		while (!success){
		
			System.out.println("\nPlease enter the IP Address of the Server.");
			IP_ADDRESS = input.nextLine();
			
			try{
				System.out.print("Connecting to Server...");
				s = new Socket(IP_ADDRESS, PORT_NUM);
				System.out.println("\t\t...Connected!\n\n");
				success = true;
				
			}
			catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
	}

}