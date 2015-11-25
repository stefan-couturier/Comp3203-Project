import java.awt.EventQueue;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientGUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 691, 451);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblClient = new JLabel("Client");
		lblClient.setFont(new Font("Franklin Gothic Medium", Font.BOLD | Font.ITALIC, 20));
		lblClient.setBounds(27, 22, 81, 24);
		frame.getContentPane().add(lblClient);
		
		JButton btnInitiateConnection = new JButton("Initiate Connection");
		btnInitiateConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ip = JOptionPane.showInputDialog("Enter the IP Address: ");
				
				//if connection successful
				//JOptionPane.showMessageDialog(null, "Connection Successful");
				//add this client to the client list
				//else
				//JOptionPane.showMessageDialog(null, "Connection Unsuccessful");
			}
		});
		btnInitiateConnection.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnInitiateConnection.setBounds(27, 57, 138, 23);
		frame.getContentPane().add(btnInitiateConnection);
		
		JButton btnTerminateConnection = new JButton("Terminate Connection");
		btnTerminateConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//if termination successful
				JOptionPane.showMessageDialog(null, "Termination Successful");
				//remove client from list
				//else
				//JOptionPane.showMessageDialog(null, "Termination Unsuccessful");
				
			}
		});
		btnTerminateConnection.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnTerminateConnection.setBounds(27, 98, 169, 23);
		frame.getContentPane().add(btnTerminateConnection);
		
		JLabel lblIpAddress = new JLabel("IP Address: ");
		lblIpAddress.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblIpAddress.setBounds(400, 30, 81, 14);
		frame.getContentPane().add(lblIpAddress);
		
		JLabel label = new JLabel("0.0.0.0");
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label.setBounds(550, 29, 88, 14);
		frame.getContentPane().add(label);
		
		JLabel lblPortNumber = new JLabel("Port Number: ");
		lblPortNumber.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPortNumber.setBounds(400, 60, 81, 14);
		frame.getContentPane().add(lblPortNumber);
		
		JLabel label_1 = new JLabel("45000");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label_1.setBounds(550, 61, 46, 14);
		frame.getContentPane().add(label_1);
		
		JLabel lblConnectionStatus = new JLabel("Connection Status: ");
		lblConnectionStatus.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblConnectionStatus.setBounds(400, 90, 125, 14);
		frame.getContentPane().add(lblConnectionStatus);
		
		JLabel lblFilesOnServer = new JLabel("Files on Server");
		lblFilesOnServer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFilesOnServer.setBounds(27, 166, 109, 14);
		frame.getContentPane().add(lblFilesOnServer);
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {"File 1", "File 2", "File 3"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list.setBounds(27, 191, 138, 146);
		frame.getContentPane().add(list);
		
		JButton btnDownloadAFile = new JButton("Download a File");
		btnDownloadAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fname = JOptionPane.showInputDialog("Enter file name: ");
				
				//if action successful
				//JOptionPane.showMessageDialog(null, "Download Successful");
				//add file to list
				//else
				//JOptionPane.showMessageDialog(null, "Download Unsuccessful");
			}
		});
		btnDownloadAFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDownloadAFile.setBounds(27, 354, 155, 23);
		frame.getContentPane().add(btnDownloadAFile);
		
		JLabel lblFilesOnSystem = new JLabel("Files on System");
		lblFilesOnSystem.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFilesOnSystem.setBounds(243, 166, 109, 14);
		frame.getContentPane().add(lblFilesOnSystem);
		
		JList list_1 = new JList();
		list_1.setModel(new AbstractListModel() {
			String[] values = new String[] {"File 1", "File 2", "File 3"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list_1.setBounds(243, 191, 138, 146);
		frame.getContentPane().add(list_1);
		
		JButton btnUploadAFile = new JButton("Upload a File");
		btnUploadAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fname = JOptionPane.showInputDialog("Enter file name: ");
				
				//if action successful
				//JOptionPane.showMessageDialog(null, "Upload successful");
				//else
				//JOptionPane.showMessageDialog(null, "Upload Unsuccessful");
			}
		});
		btnUploadAFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnUploadAFile.setBounds(243, 354, 155, 23);
		frame.getContentPane().add(btnUploadAFile);
		
		JLabel lblOnlinePeers = new JLabel("Online Peers");
		lblOnlinePeers.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblOnlinePeers.setBounds(452, 166, 109, 14);
		frame.getContentPane().add(lblOnlinePeers);
		
		JList list_2 = new JList();
		list_2.setModel(new AbstractListModel() {
			String[] values = new String[] {"Peer 1", "Peer 2", "Peer 3"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list_2.setBounds(452, 191, 138, 146);
		frame.getContentPane().add(list_2);
		
		JButton btnChat = new JButton("Peer-to-Peer ");
		btnChat.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnChat.setBounds(452, 355, 125, 23);
		frame.getContentPane().add(btnChat);
	}
}
