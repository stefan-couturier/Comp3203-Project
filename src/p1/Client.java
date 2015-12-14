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
	private int currPeerPortNum;
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
		IP_ADDRESS = serverName;
		currPeerPortNum=60000;
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
					sendFileList();
					streamOut.writeUTF("update"); // requests updated lists from server
					System.out.println("CLIENT sent:\tupdate");
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
						streamOut.writeUTF(peerName);
						streamOut.flush();
					}
					gui.setRequestingPeerFileList(false);
				}
				else if (gui.isRequestingPeerFile()) {
					String peerFile = gui.getSelectedPeerFile();
					if (peerFile != null) {
						streamOut.writeUTF("getPeerFile");
						streamOut.writeUTF(gui.getSelectedPeer());
						streamOut.writeUTF(peerFile);
						streamOut.writeUTF(InetAddress.getLocalHost().getHostAddress());
						streamOut.writeInt(currPeerPortNum);
						streamOut.flush();
					}
					gui.setRequestingPeerFile(false);
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
			gui.disable("Uploading");
			sendFile(selectedClientFile, streamOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		gui.enable();
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
		System.out.println("Try");
	    try{
	    	if (console   != null)  console.close();
	        if (streamOut != null)  streamOut.close();
	        if (socket    != null)  socket.close();
	    }
	    catch(IOException ioe){
	    	System.out.println("Error closing ..."); 
	    }
	    gui.getFrame().dispose();
	    System.out.println("Try again");
	    clientThread.close();  
	    clientThread.stop();
	}
	
	public void terminate(){
		gui.terminate();
		stop();
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
		//ArrayList<String> requestList = new ArrayList<String>();
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
			/*listSize = inputStream.readInt();
			System.out.println("CLIENT recieved:\t"+listSize+" requests on Server");
			for (int j = 0; j < listSize; j++){
				requestList.add(inputStream.readUTF());
				System.out.println("CLIENT Recieved:\tRequest: "+peerList.get(j));
			}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("CLIENT:\tFiles on System: "+clientList);
		gui.updateLists(serverList, clientList, peerList);
		
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
	
	
	public void receiveP2PFile() throws IOException {
		P2PServer p2p = new P2PServer(currPeerPortNum++, path);
		System.out.println("starting p2p receive");
		gui.disable("Downloading From Peer");
		p2p.run();
		gui.enable();
		System.out.println("updating client list after p2p receive");
		gui.updateClientList(getFileList());
		System.out.println("finished p2p receive");
	}
	
	public void sendP2PFile(String ipAdd, String filename, int portNum) throws IOException {
		P2PClient p2p = new P2PClient(ipAdd, portNum, path, filename);
		System.out.println("starting p2p send");
		p2p.run();
		System.out.println("finished p2p send");
	}

	public void receiveFile(DataInputStream request) throws IOException {
		gui.disable("Downloading");
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
		gui.enable();
	}
	
	public void recieveMessage(String m) {
		gui.appendChat(m);
	}

	public void requestRefresh() {
		try{
			sendFileList();
			streamOut.writeUTF("update"); // requests updated lists from server
			System.out.println("CLIENT sent:\tupdate");
			gui.setRequestingRefresh(false);
		}
		catch (Exception e){
			System.err.println("--error: Unable to request update --Message:" + e.getMessage());
		}
	}
}