import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Server implements Runnable{
	private final static int PORT_NUM = 45000;
	public static ServerSocket serv;
	public static Socket clnt;
	public static String root_dir;
	public static String path;
	public static File current_file;

	private ServerThread clients[] = new ServerThread[50];
	private ServerSocket server = null;
	private Thread       thread = null;
	private int clientCount = 0;


	public Server(int port){
		try{
			System.out.println("Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);  
			System.out.println("Server started: " + server);
			start(); 
		}
		catch(IOException ioe){
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); 
		}
	}

	public void run()
	{  while (thread != null)
	{  try
	{  System.out.println("Waiting for a client ..."); 
	addThread(server.accept()); }
	catch(IOException ioe)
	{  System.out.println("Server accept error: " + ioe); stop(); }
	}
	}

	public void start(){
		if (thread == null){
			thread = new Thread(this); 
			thread.start();
		}
	}
	public void stop(){
		if (thread != null){
			thread.stop(); 
			thread = null;
		}
	}
	private int findClient(int ID){
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getID() == ID)
				return i;
		return -1;
	}
	public synchronized void handle(int ID, String input){
		if (input.equals(".bye")){
			clients[findClient(ID)].send(".bye");
			remove(ID); 
		}
		else
			for (int i = 0; i < clientCount; i++)
				clients[i].send(ID + ": " + input);   
	}
	public synchronized void remove(int ID){
		int pos = findClient(ID);
		if (pos >= 0){
			ServerThread toTerminate = clients[pos];
			System.out.println("Removing client thread " + ID + " at " + pos);
			if (pos < clientCount-1)
				for (int i = pos+1; i < clientCount; i++)
					clients[i-1] = clients[i];
			clientCount--;
			try{
				toTerminate.close(); 
			}
			catch(IOException ioe){
				System.out.println("Error closing thread: " + ioe); 
			}
			toTerminate.stop(); 
		}
	}
	private void addThread(Socket socket){
		if (clientCount < clients.length){
			System.out.println("Client accepted: " + socket);
			clients[clientCount] = new ServerThread(this, socket);
			try	{
				clients[clientCount].open(); 
				clients[clientCount].start();  
				clientCount++; 
			}
			catch(IOException ioe){
				System.out.println("Error opening thread: " + ioe); 
			} 
		}
		else
			System.out.println("Client refused: maximum " + clients.length + " reached.");
	}


	public static boolean sendFile(String name, DataOutputStream reply) throws IOException {
		boolean status = true;
		FileInputStream file_stream = null;
		BufferedInputStream buffer_stream = null;
		DataInputStream dataIn_stream = null;
		dataIn_stream = null;

		String file_path = current_file + "\\" + name;

		File file = new File(file_path);
		byte[] byte_array = new byte[(int) file.length()];

		try {
			file_stream = new FileInputStream(file);
			buffer_stream = new BufferedInputStream(file_stream);
			dataIn_stream = new DataInputStream(buffer_stream);

			dataIn_stream.readFully(byte_array, 0, byte_array.length);

			reply.writeBoolean(true);
			reply.writeUTF(file.getName());
			reply.writeLong((long) byte_array.length);
			reply.write(byte_array, 0, byte_array.length);
		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
			status = false;
		} finally {
			try {
				if (file_stream != null)
					file_stream.close();
				if (buffer_stream != null)
					buffer_stream.close();
				if (dataIn_stream != null)
					dataIn_stream.close();
			} catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
		return status;
	}

	private static File findFile(String name){
		String file_path = current_file + "\\" + name;
		File aFile = new File(file_path);
		if (aFile.exists())
			return aFile;
		return null;
	}

	public static boolean receiveFile(String path, DataInputStream request) throws IOException {
		boolean status = true;
		int r_byt = 0;
		String f_name = null;
		long f_size = 0;
		OutputStream writer = null;

		try {
			if (path.equals(root_dir+"\\")){
				request.readUTF();
				f_size = request.readLong();
				request.skip(f_size);
				return false;
			}

			f_name = path.concat((Paths.get(request.readUTF())).getFileName().toString());
			System.out.println(f_name);
			f_size = request.readLong();
			writer = new FileOutputStream(f_name);
			byte[] r_buf = new byte[256];

			while ((r_byt = request.read(r_buf, 0, (int) Math.min(r_buf.length, f_size))) != 1 && f_size > 0) {
				writer.write(r_buf, 0, r_byt);
				f_size -= (long) r_byt;
			}
		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
			status = false;
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
		return status;
	}

	public static void sendMsg(String input, DataOutputStream reply) throws IOException {
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

	public static void receiveCmd(int type, DataInputStream request, DataOutputStream reply) throws IOException {
		try {
			int rec = 0;
			int size = request.readInt();
			byte[] buf = new byte[size];
			while((rec = request.read(buf, 0, size)) != 1 && size > 0) {
				size -= rec;
			}
			String s =  new String(buf, StandardCharsets.UTF_8);
			if (type == 1) {
				if (s.startsWith("ls")) {
					sendMsg("Directory Contents:\n" + ls(current_file), reply);
				} else {
					sendMsg("Command not recognized: " + s, reply);
				}
			} else if (type == 4) {
				if (s.startsWith("cd ")) {
					cd(s.substring(3));
					sendMsg("Change directory executed", reply);
				} else {
					sendMsg("Command not recognized: " + s, reply);
				}
			} else if (type == 5) {
				if (s.startsWith("mkdir ")) {
					if (!mkdir(current_file, s.substring(6)))
						sendMsg("Making Directory Failed", reply);
					else
						sendMsg("Directory " + s.substring(6) + " created successfully", reply);
				} else {
					sendMsg("Command not recognized: " + s, reply);
				}
			}
		} catch(Exception e) {
			System.err.println("--error: " + e.getMessage());
		}
	}

	public static boolean mkdir(File f, String s) {
		File n = new File(f.toString() + "\\" + s);
		// will return false if directory already exists
		return n.mkdir();
	}

	public static boolean cd(String s) {
		boolean status = false;
		// if they wish go up a directory and they're are not already at the root, they can
		if (s.equals("..") && !current_file.getPath().equals(root_dir)) {
			current_file = new File(current_file.getParent());
			status = true;
		} else {
			File[] f = current_file.listFiles();
			for (int i = 0; i < f.length; i++) {
				// go to requested directory if it exists within current directory
				if (s.equals(f[i].getName()) && f[i].isDirectory()){
					current_file = new File(current_file.toString() + "\\" + s);
					status = true;
				}
			}
		}
		return status;
	}

	public static String ls(File f) {
		File[] arr = f.listFiles();
		String s = "";
		for (int i = 0; i < arr.length; i++) {
			s += arr[i].getName();
			s += "\n";
		}
		return s;
	}

	
	public static void main(String args[]){
		Server server = null;
	    if (args.length != 1)
	    	server = new Server(PORT_NUM);
	    else
	    	server = new Server(Integer.parseInt(args[0]));
	}
	
//	public static void main(String[] args) {
//
//
//		File[] roots = File.listRoots();
//		root_dir = roots[0].toString();
//		path = root_dir;
//
//		serv = null;
//		clnt = null;
//		current_file = null;
//		DataInputStream request = null;
//		DataOutputStream reply = null;
//
//		try {
//			serv = new ServerSocket(PORT_NUM);
//			while(true){
//				System.out.println("Waiting for connection on PORT "+PORT_NUM);
//				clnt = serv.accept();
//
//				System.out.println("Connection found");
//				current_file = new File(root_dir);
//
//				boolean running = true;
//				boolean status;
//				request = new DataInputStream(clnt.getInputStream());
//				reply = new DataOutputStream(clnt.getOutputStream());
//				String temp = null;
//				int choice;
//				while (running) {
//
//
//					choice = request.readByte(); // read command
//
//					switch (choice) {
//					case 1: //ls
//						System.out.println("ls");
//
//						//get contents of the directory
//						temp = null;
//						temp = ls(current_file);
//
//						//return success/fail
//						reply.writeBoolean(temp == null);
//
//						//return current contents
//						sendMsg(temp, reply);
//						break;
//
//					case 2: //get
//
//						//get filename
//						String file_name = request.readUTF();
//						System.out.println("get \"" + file_name + "\"");
//
//						//find file
//						if (findFile(file_name)==null){
//							//if not found, reply false
//							reply.writeBoolean(false);
//							System.out.println(file_name+" File NOT found");
//						}
//						else{
//							//if found, reply success and send file
//							status = sendFile(file_name,reply);
//							System.out.println(file_name+" File sent");
//
//						}
//
//
//						break;
//
//					case 3: //put
//						System.out.print("put ");
//
//						if (request.readBoolean()){
//							//get File
//							status = receiveFile(current_file+"\\", request);
//
//							//return success
//							reply.writeBoolean(status);
//						}
//						else{
//							//return false
//							reply.writeBoolean(false);
//							System.out.println();
//						}
//
//
//
//						break;
//
//					case 4: //cd
//
//						//get directory name
//						temp = request.readUTF();
//						System.out.println("cd "+temp);
//
//						//change directory
//						status = cd(temp);
//
//						//return success
//						reply.writeBoolean(status);
//
//						break;
//
//					case 5: //mkdir
//
//						//get directory name
//						temp = request.readUTF();
//
//						System.out.println("mkdir "+temp);
//						//make directory
//						status = mkdir(current_file,temp);
//
//						//return success
//						reply.writeBoolean(status);
//
//						break;
//
//					case 0: //client exits
//						System.out.println("Client exitted. Reopenning Socket");
//
//						//exit loop then close connections
//						running = false;
//						break;
//
//					default: //any other unexpected request
//						System.out.println("Unknown");
//
//						//return false
//						break;
//					}
//				}
//				clnt.close();
//			}
//
//		} catch (Exception e) {
//			System.err.println("--error: " + e.getMessage());
//		} finally {
//			try {
//				if (request != null)
//					request.close();
//				if (reply != null)
//					reply.close();
//				if (clnt != null)
//					clnt.close();
//				if (serv != null)
//					serv.close();
//			} catch (Exception e) {
//				System.err.println("--error: " + e.getMessage());
//			}
//		}
//	}
	
}
