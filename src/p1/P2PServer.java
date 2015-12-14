package p1;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;

public class P2PServer {
	private int PORT_NUM;
	public ServerSocket serv;
	public Socket clnt;
	public String root_dir;

	P2PServer(int port, String directory) {
		PORT_NUM = port;
		root_dir = directory;
	}
	
	public void run() {
		DataInputStream request = null;
		DataOutputStream reply = null;

		try {
			serv = new ServerSocket(PORT_NUM);
			System.out.println("Waiting for P2P connection on PORT "+PORT_NUM);
			clnt = serv.accept();
			System.out.println("P2P Connection found");

			request = new DataInputStream(clnt.getInputStream());
			reply = new DataOutputStream(clnt.getOutputStream());

			receiveFile(request);


		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
		} finally {
			try {
				if (request != null)
					request.close();
				if (reply != null)
					reply.close();
				if (serv != null)
					serv.close();
				if (clnt != null)
					clnt.close();
			} catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
	}


	public boolean receiveFile(DataInputStream request) throws IOException {
		boolean status = true;
		int r_byt = 0;
		String f_name = null;
		long f_size = 0;
		OutputStream writer = null;

		try {
			f_name = root_dir.concat("\\" + (Paths.get(request.readUTF())).getFileName().toString());
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


	/*public static void main(String[] args) {
		P2PServer test = new P2PServer(45001, "C:\\Users\\Andrew\\Test\\Things");
		test.run();
	}*/
	
}
