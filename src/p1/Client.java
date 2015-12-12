package p1;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Client extends JPanel implements Runnable {
	private final static int PORT_NUM = 45000;
	private static String IP_ADDRESS = "000.000.00.000";
	public static Socket s;
	public static String path = "";
	public static String username = "";
	
	private Thread thread = null;
	private ClientThread clientThread = null;
	private Socket  socket   = null;
	private DataInputStream  console   = null;
	private DataOutputStream streamOut = null;
	private ClientGUI gui = null;
	private UsernameDirectoryInfo udi = null;
	
	private String selectedClientFile;
	private boolean isInitializing = true;
	
	
	public Client(String serverName, int serverPort){
		gui = new ClientGUI();
		
		System.out.println("Establishing connection. Please wait ...");
	      try
	      {  socket = new Socket(serverName, serverPort);
	         System.out.println("Connected: " + socket);
	         start();
	      }
	      catch(UnknownHostException uhe)
	      {  System.out.println("Host unknown: " + uhe.getMessage()); }
	      catch(IOException ioe)
	      {  System.out.println("Unexpected exception: " + ioe.getMessage()); }
	}
	
	public Client(String serverName, int serverPort, String PATH, String user){
		path = PATH;
		username = user;
		gui = new ClientGUI();
		gui.getFrame().setTitle(user);
		System.out.println("Establishing connection. Please wait ...");
	      try
	      {  socket = new Socket(serverName, serverPort);
	         System.out.println("Connected: " + socket);
	         start();
	      }
	      catch(UnknownHostException uhe)
	      {  System.out.println("Host unknown: " + uhe.getMessage()); }
	      catch(IOException ioe)
	      {  System.out.println("Unexpected exception: " + ioe.getMessage()); }
	}
	
	public static void main(String args[]) {
		Client client = null;
	    if (args.length != 2)
	    	client = new Client(IP_ADDRESS, PORT_NUM);
	    else
	        client = new Client(args[0], Integer.parseInt(args[1]));
	}
	
	
	public void run(){
		while (thread != null){
			try{
				// handle gui input and pass commands to ServerThread
				
				// this 'if' only done once at initialization
				if (isInitializing) {
					streamOut.writeUTF("username");
					streamOut.flush();
					streamOut.writeUTF(username);
					streamOut.flush();
					sendFileList();
					streamOut.writeUTF("update");
					isInitializing = false; 
				} 
				
				if (gui == null){
					streamOut.writeUTF(".bye");
					System.out.println("CLIENT sent:\t.bye");
					stop();
				}
				else if (gui.isRequestingRefresh()) {
					streamOut.writeUTF("update"); // requests updated lists from server
					System.out.println("CLIENT sent:\tupdate");
					
					//send local file list
					sendFileList();
					
					gui.setRequestingRefresh(false);
				}
				else if (gui.isRequestingDownload()) {
					System.out.println("Thread:\tDownload");
					String filename = gui.getSelectedServerFile();
					if (filename != null && verifyDownload(filename)) {
						streamOut.writeUTF("download"); // requests to download from server
						System.out.println("CLIENT sent:\tdownload");
						streamOut.writeUTF(filename);
						System.out.println("CLIENT sent:\t"+filename);
					}
					gui.setRequestingDownload(false);
				}
				else if (gui.isRequestingUpload()) {
					selectedClientFile = gui.getSelectedClientFile();
					streamOut.writeUTF("upload"); // requests to upload to server
					System.out.println("CLIENT sent:\tupload");
					streamOut.writeUTF(selectedClientFile);
					System.out.println("CLIENT sent:\t"+selectedClientFile);
					gui.setRequestingUpload(false);
				}
				else if(gui.isRequestingSendToChat()){
					String message = gui.getChatMessage();
					streamOut.writeUTF("message"); // requests to post on chat
					System.out.println("CLIENT sent:\tmessage");
					streamOut.writeUTF(username + ": "+message);
					System.out.println("CLIENT sent:\t"+message);
					gui.setRequestingSendToChat(false);
				}
				else if (gui.isRequestingPeerFileList()){
					String peerName = gui.getSelectedPeer();
					if (peerName!=null){
						streamOut.writeUTF("getPeerFileList");
						System.out.println("CLIENT sent:\tgetPeerFileList");
						// mark this file request as "PENDING" status to notify waiting client 
						streamOut.writeUTF(peerName);
						streamOut.flush();
					}
					gui.setRequestingPeerFileList(false);
				}
				//streamOut.writeUTF(console.readLine());
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
	
	public void uploadFile() {
		try {
			sendFile(selectedClientFile, streamOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void start() throws IOException{
		console   = new DataInputStream(System.in);
	    streamOut = new DataOutputStream(socket.getOutputStream());
	    gui.setIPAddress(IP_ADDRESS);
	    gui.setPortNumber(PORT_NUM);
	    System.out.println(1);
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
	
	public ClientGUI getGUI() {
		return gui;
	}
	
	public DataOutputStream getOutputStream() {
		return streamOut;
	}
	
	
	public void updateGUILists(DataInputStream inputStream) {
		ArrayList<String> serverList = new ArrayList<String>();
		ArrayList<String> clientList = new ArrayList<String>(getFileList());
		ArrayList<String> peerList = new ArrayList<String>();
		ArrayList<String> requestList = new ArrayList<String>();
		int listSize;
		try {
			listSize = inputStream.readInt();
			System.out.println("CLIENT recieved:\t"+listSize+" files on Server");
			for (int i = 0; i < listSize; i++){
				serverList.add(inputStream.readUTF());
				System.out.println("CLIENT Recieved:\tFile: "+serverList.get(i));
			}
			listSize = inputStream.readInt();
			System.out.println("CLIENT recieved:\t"+listSize+" peers on Server");
			for (int j = 0; j < listSize; j++){
				peerList.add(inputStream.readUTF());
				System.out.println("CLIENT Recieved:\tPeer: "+peerList.get(j));
			}
			listSize = inputStream.readInt();
			System.out.println("CLIENT recieved:\t"+listSize+" requests on Server");
			for (int j = 0; j < listSize; j++){
				requestList.add(inputStream.readUTF());
				System.out.println("CLIENT Recieved:\tRequest: "+peerList.get(j));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("CLIENT:\tFiles on System: "+clientList);
		gui.updateLists(serverList, clientList, peerList, requestList);
		
	}
	
	public void recievePeerFileList(DataInputStream inputStream){
		ArrayList<String> peerFileList = new ArrayList<String>();
		int listSize;
		try {
			listSize = inputStream.readInt();
			System.out.println("CLIENT recieved:\t"+listSize+" files on Peer");
			for (int i = 0; i < listSize; i++){
				peerFileList.add(inputStream.readUTF());
				System.out.println("CLIENT Recieved:\tFile: "+peerFileList.get(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		gui.updatePeerFileList(peerFileList);
	}
	
	public void sendFileList(){
		try{
			streamOut.writeUTF("fileList");
			System.out.println("CLIENT SENT:\tfilelist");
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendArrayList(getFileList(),streamOut);
	}
	
	public void sendArrayList(ArrayList<String> list, DataOutputStream outputStream) {
		try {
			// so client knows how many file names to expect
			outputStream.writeInt(list.size());
			System.out.println("CLIENT Sent:\t"+list.size());
			for (int i=0; i < list.size(); i++){
				outputStream.writeUTF(list.get(i));
				System.out.println("CLIENT Sent:\t"+list.get(i));
			}
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	// gets current file list of clients directory
	public ArrayList<String> getFileList() {
		File f = new File(path);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		ArrayList<String> list = new ArrayList<String>();
		for(int x = 0; x < files.size(); x++) {
			if (!files.get(x).isDirectory()) 
				list.add(files.get(x).getName());
		}
		return list;
	}
	
	// verify that client doesn't already have the file it's attempting to download
	public boolean verifyDownload(String s) {
		ArrayList<String> list = new ArrayList<String>(getFileList());
		if (!list.contains(s))
			return true;
		else return false;
	}

	
	/***************
	 * sendFile() takes a file name, ie:"test.txt" with extension and sends the
	 * file to the client
	 *******************/
	public synchronized void sendFile(String name, DataOutputStream request) throws IOException {

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
				request.flush();
				System.out.println("CLIENT\tsent:\t\tFile:"+name);
			} catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
	}
	
	private File findFile(String name){
		String file_path = path + "\\" + name;
		File aFile = new File(file_path);
		if (aFile.exists())
			return aFile;
		return null;
	}
	
	public void receiveP2PFile() {
		P2PServer p2p = new P2PServer(60000, path);
		p2p.run();
		gui.updateClientList(getFileList());
	}
	
	public void sendP2PFile(String ipAdd) throws IOException {
		String IP = ipAdd;
		int port = 60000;
		
		File fileChosen = null;
		String filePath = "";
		JFileChooser fileChooser = new JFileChooser(); 
		fileChooser.setCurrentDirectory(new java.io.File(path));
		fileChooser.setDialogTitle("Choose a File to send");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setVisible(true);
		// the condition in this 'if-statement' required extending JPanel
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			fileChosen = fileChooser.getSelectedFile();
			filePath = fileChosen.getAbsolutePath();
		}
		P2PClient p2p = new P2PClient(IP, port, "", filePath);
		p2p.run();
	}

	public void receiveFile(DataInputStream request) throws IOException {
		int r_byt = 0;
		String f_name = null;
		long f_size = 0;
		OutputStream writer = null;

		try {
			//f_name = path.concat("\\" + (Paths.get(request.readUTF())).getFileName().toString());
			f_name = path + "\\" + request.readUTF();
			f_size = request.readLong();
			writer = new FileOutputStream(f_name);
			byte[] r_buf = new byte[4096*16];

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
				System.out.println("CLIENT\t recieved:\t\tFile:"+f_name);
			} catch(Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
	}

	public void sendCmd(String input, DataOutputStream reply) throws IOException {
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

	public void receiveMsg(DataInputStream request) throws IOException {
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
	
	private void createDirectory(Scanner input){
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
	
	private void serverConnect(Scanner input){
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

	public void recieveMessage(String m) {
		gui.appendChat(m);
	}

	
//	public static void main(String[] args) {
//	
//	File[] roots = File.listRoots();
//	path = roots[0].toString();
//	
//	Scanner inputIP = new Scanner(System.in);
//	serverConnect(inputIP);
//	
//	createDirectory(inputIP);
//	
//	//s = null;
//	OutputStream out = null;
//	DataOutputStream request = null;
//	DataInputStream reply = null;
//	try {
//		//System.out.print("Connecting to Server...");
//		//s = new Socket(IP_ADDRESS, PORT_NUM);
//		//System.out.println("\t\t...Connected!\n\n");
//		int i = 1;
//		String user_input, file_name, direc;
//		String user_args[];
//		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//		System.out.println("\t\tWelcome to the Client-Server FTP");
//		
//		out = s.getOutputStream();
//		request = new DataOutputStream(out);
//		reply = new DataInputStream(s.getInputStream());
//		
//		while (i != 0) {
//			
//			//Read command
//			System.out.print("\t\tPlease enter a command:");
//			user_input = in.readLine();
//			user_args = user_input.trim().split(" ");
//			
//			//Validate command
//			if (user_args.length >2){
//				//error too many args
//				System.out.println("Too many args");
//			}
//			else{
//				switch (user_args[0]) {
//				case "ls":
//					request.writeByte(1);// send ls
//
//					// receive success response
//					
//					reply.readBoolean();
//					
//					System.out.println("Contents of directory:\n");
//					// receive list
//					receiveMsg(reply);
//					
//
//					break;
//				case "get":
//					if (user_args.length!=2){
//						//error, not enough args
//						System.out.println("ERROR:\tNot enough args");
//						break;
//					}
//					file_name = user_args[1];
//
//					request.writeByte(2);// send get signal
//					request.writeUTF(file_name);
//
//					// get success response
//					if(reply.readBoolean()){
//						System.out.println("Success");
//						// receive file
//						receiveFile(reply);
//					}
//					else
//						System.out.println("Failed");
//
//
//					break;
//				case "put":
//					
//					if (user_args.length!=2){
//						//error, not enough args
//						System.out.println("ERROR:\tNot enough args");
//						break;
//					}
//					
//
//					// find file
//					file_name = user_args[1];
//					
//
//					request.writeByte(3);// send put signal
//
//					// send file
//					File aFile = findFile(file_name);
//					if (aFile == null)
//						request.writeBoolean(false);
//					else{
//						request.writeBoolean(true);
//						sendFile(file_name, request);
//					}
//					
//					
//					// get success response
//					if(reply.readBoolean())
//						System.out.println("Success");
//					else
//						System.out.println("Failed");
//					
//
//					break;
//					
//				case "cd":
//					if (user_args.length!=2){
//						//error, not enough args
//						System.out.println("ERROR:\tNot enough args");
//						break;
//					}
//					direc = user_args[1];
//
//					request.writeByte(4);// send cd signal
//
//					// send directory name
//					request.writeUTF(direc);
//					
//					// get success response
//					if(reply.readBoolean())
//						System.out.println("Success");
//					else
//						System.out.println("Failed");
//					
//					break;
//				case "mkdir":
//					
//					if (user_args.length!=2){
//						//error, not enough args
//						System.out.println("ERROR:\tNot enough args");
//						break;
//					}
//					direc = user_args[1];
//
//					request.writeByte(5);// send mkdir signal
//
//					// send directory name
//					request.writeUTF(direc);
//					
//					
//					// get success response
//					if(reply.readBoolean())
//						System.out.println("Success");
//					else
//						System.out.println("Failed");
//					
//					break;
//				case "exit":
//					System.out.println("\nThank you for using this service!!");
//
//					request.writeByte(0);// send exit signal
//
//					i = 0;// exit loop
//					break;
//				default:
//					System.out.println("\tWrong input, please try again.");
//
//				}
//			}
//		}
//	} catch (Exception e) {
//		System.err.println("--error: " + e.getMessage());
//	} finally {
//		try {
//			if (reply != null)
//				reply.close();
//			if (request != null)
//				request.close();
//			if (out != null)
//				out.close();
//			if (s != null)
//				s.close();
//		} catch (Exception e) {
//			System.err.println("--error: " + e.getMessage());
//		}
//	}
//	inputIP.close();
//}
	
}