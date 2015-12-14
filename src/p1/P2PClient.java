package p1;

import java.io.*;
import java.net.*;

public class P2PClient {
	private String IP_ADDRESS;
	private int PORT_NUM;
	private Socket s;
	private String path;
	private String fileToSend;

	P2PClient(String IP, int port, String directory, String filename) {
		IP_ADDRESS = IP;
		PORT_NUM = port;
		path = directory;
		fileToSend = filename;
	}
	
	public void run() {
		OutputStream out = null;
		DataOutputStream request = null;
		DataInputStream reply = null;
		try {
			s = new Socket(IP_ADDRESS, PORT_NUM);
			out = s.getOutputStream();
			request = new DataOutputStream(out);
			reply = new DataInputStream(s.getInputStream());
			
			sendFile(request);
			
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
	}


	public void sendFile(DataOutputStream request) throws IOException {

		FileInputStream file_stream = null;
		BufferedInputStream buffer_stream = null;
		DataInputStream dataIn_stream = null;
		//dataIn_stream = null;

		String file_path = path + "\\" + fileToSend;
		//String file_path = fileToSend;
		
		File file = new File(file_path);
		byte[] byte_array = new byte[(int) file.length()];

		try {
			file_stream = new FileInputStream(file);
			buffer_stream = new BufferedInputStream(file_stream);
			dataIn_stream = new DataInputStream(buffer_stream);

			dataIn_stream.readFully(byte_array, 0, byte_array.length);

			request.writeUTF(file.getName());
			request.flush();
			request.writeLong((long) byte_array.length);
			request.flush();
			request.write(byte_array, 0, byte_array.length);
			request.flush();
			
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


	/*public static void main(String[] args) {
		P2PClient test = new P2PClient(
				"192.168.2.10",
				45001,
				"C:\\Users\\Andrew\\Test",
				"EpicSetup.exe");
		test.run();
	}*/
	
}
