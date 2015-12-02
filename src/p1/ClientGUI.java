package p1;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class ClientGUI {

	private JFrame frame;
	private JLabel lblClient;
	private JButton btnInitiateConnection;
	private JButton btnTerminateConnection;
	private JLabel lblIpAddress;
	private JLabel label;
	private JLabel lblPortNumber;
	private JLabel label_1;
	private JLabel lblConnectionStatus;
	private JLabel lblFilesOnServer;
	private JList<String> serverFileList;
	private JList<String> clientFileList;
	private JList<String> peerList;
	private JButton btnDownloadAFile;
	private JLabel lblFilesOnSystem;
	private JButton btnUploadAFile;
	private JLabel lblOnlinePeers;
	private JButton btnChat;
	
	private String IPAddress = "0.0.0.0";
	private int portNumber = 0;
	
	
	private String selectedServerFile;
	private String selectedClientFile;
	private String selectedPeer;
	private boolean requestingDownload;
	private boolean requestingUpload;
	private boolean requestingRefresh;
	
	private ArrayList<String> serverFiles;
	private ArrayList<String> clientFiles;
	private ArrayList<String> peers;
	
	private ActionListener initiateButtonListener;
	private ActionListener terminateButtonListener;
	private ActionListener downloadButtonListener;
	private ActionListener uploadButtonListener;
	private ListSelectionListener serverFileListSelectionListener;
	private ListSelectionListener clientFileListSelectionListener;
	private ListSelectionListener peerListSelectionListener;
	

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
		serverFiles = new ArrayList<String>();
		clientFiles = new ArrayList<String>();
		peers = new ArrayList<String>();
		
		peers.add("//TODO:");
		
		selectedServerFile = null;
		selectedClientFile = null;
		selectedPeer = null;
		requestingDownload = false;
		requestingUpload = false;
		requestingRefresh = true;
		
		initiateButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
			}
		};
		
		terminateButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				requestRefresh();
			}
		};
		
		downloadButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				requestDownload();
			}
		};
		
		uploadButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				requestUpload();
			}
		};
		
		serverFileListSelectionListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				selectServerFile();
			}
		};
		
		clientFileListSelectionListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				selectClientFile();
			}
		};
		
		peerListSelectionListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				selectPeer();
			}
		};
		
		initialize();
		this.frame.setVisible(true);
		enableListeners();
	}
	
	
	public synchronized boolean isRequestingDownload() {
		return requestingDownload;
	}
	
	public synchronized boolean isRequestingUpload() {
		return requestingUpload;
	}
	
	public synchronized boolean isRequestingRefresh() {
		return requestingRefresh;
	}
	
	public synchronized void setRequestingDownload(boolean b) {
		requestingDownload = b;
	}
	
	public synchronized void setRequestingUpload(boolean b) {
		requestingUpload = b;
	}
	
	public synchronized void setRequestingRefresh(boolean b) {
		requestingRefresh = b;
	}
	
	public void requestDownload() {
		if (selectedServerFile != null)
			requestingDownload = true;
	}
	
	
	public void requestUpload() {
		if (selectedClientFile != null)
			requestingUpload = true;
	}
	
	public void requestRefresh() {
		System.out.println("Refresh");
		requestingRefresh = true;
	}
	
	public void selectServerFile() {
		selectedServerFile = (String) serverFileList.getSelectedValue();
	}
	
	
	public void selectClientFile() {
		selectedClientFile = (String) clientFileList.getSelectedValue();
	}
	
	
	public void selectPeer() {
		selectedPeer = (String) peerList.getSelectedValue();
	}
	
	public String getSelectedServerFile() {
		return selectedServerFile;
	}
	
	public String getSelectedClientFile() {
		return selectedClientFile;
	}
	
	public String getSelectedPeer() {
		return selectedPeer;
	}
	
	public String getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
		label.setText(IPAddress);
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
		label_1.setText(portNumber+"");
		
	}
	
	public JFrame getFrame(){
		return frame;
	}

	// Update lists
	@SuppressWarnings("unchecked")
	public void updateLists(ArrayList<String> s, ArrayList<String> c, ArrayList<String> p) {
		System.out.println("CLIENT GUI:\tupdateLists");
		disableListeners();
		selectedServerFile = null;
		selectedClientFile = null;
		selectedPeer = null;
		
		serverFiles = new ArrayList<String>(s);
		clientFiles = new ArrayList<String>(c);
		peers = new ArrayList<String>(p);

		String strServer[] = new String[1]; // needed to establish array type for next line
		serverFileList.setListData((String[]) serverFiles.toArray(strServer));
		String strClient[] = new String[1]; // needed to establish array type for next line
		clientFileList.setListData((String[]) clientFiles.toArray(strClient));
		String strPeer[] = new String[1]; // needed to establish array type for next line
		peerList.setListData((String[]) peers.toArray(strPeer));

		enableListeners();
	}


	// Enable listeners
	private void enableListeners() {
		btnInitiateConnection.addActionListener(initiateButtonListener);
		btnTerminateConnection.addActionListener(terminateButtonListener);
		btnDownloadAFile.addActionListener(downloadButtonListener);
		btnUploadAFile.addActionListener(uploadButtonListener);
		serverFileList.addListSelectionListener(serverFileListSelectionListener);
		clientFileList.addListSelectionListener(clientFileListSelectionListener);
		peerList.addListSelectionListener(peerListSelectionListener);
	}


	// Disable listeners
	private void disableListeners() {
		btnInitiateConnection.removeActionListener(initiateButtonListener);
		btnTerminateConnection.removeActionListener(terminateButtonListener);
		btnDownloadAFile.removeActionListener(downloadButtonListener);
		btnUploadAFile.removeActionListener(uploadButtonListener);
		serverFileList.removeListSelectionListener(serverFileListSelectionListener);
		clientFileList.removeListSelectionListener(clientFileListSelectionListener);
		peerList.removeListSelectionListener(peerListSelectionListener);
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 691, 451);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		lblClient = new JLabel("Client");
		lblClient.setFont(new Font("Franklin Gothic Medium", Font.BOLD | Font.ITALIC, 20));
		lblClient.setBounds(27, 22, 81, 24);
		frame.getContentPane().add(lblClient);
		
		btnInitiateConnection = new JButton("Initiate Connection");
		/*btnInitiateConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ip = JOptionPane.showInputDialog("Enter the IP Address: ");
				
				//if connection successful
				//JOptionPane.showMessageDialog(null, "Connection Successful");
				//add this client to the client list
				//else
				//JOptionPane.showMessageDialog(null, "Connection Unsuccessful");
			}
		});*/
		btnInitiateConnection.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnInitiateConnection.setBounds(27, 57, 138, 23);
		frame.getContentPane().add(btnInitiateConnection);
		
		btnTerminateConnection = new JButton("Refresh");
		/*btnTerminateConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//if termination successful
				JOptionPane.showMessageDialog(null, "Termination Successful");
				//remove client from list
				//else
				//JOptionPane.showMessageDialog(null, "Termination Unsuccessful");
				
			}
		});*/
		btnTerminateConnection.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnTerminateConnection.setBounds(27, 98, 169, 23);
		frame.getContentPane().add(btnTerminateConnection);
		
		lblIpAddress = new JLabel("IP Address: ");
		lblIpAddress.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblIpAddress.setBounds(400, 30, 81, 14);
		frame.getContentPane().add(lblIpAddress);
		
		label = new JLabel(IPAddress);
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label.setBounds(550, 29, 98, 14);
		frame.getContentPane().add(label);
		
		lblPortNumber = new JLabel("Port Number: ");
		lblPortNumber.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPortNumber.setBounds(400, 60, 81, 14);
		frame.getContentPane().add(lblPortNumber);
		
		label_1 = new JLabel(portNumber+"");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label_1.setBounds(550, 61, 46, 14);
		frame.getContentPane().add(label_1);
		
		lblConnectionStatus = new JLabel("Connection Status: ");
		lblConnectionStatus.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblConnectionStatus.setBounds(400, 90, 125, 14);
		frame.getContentPane().add(lblConnectionStatus);
		
		lblFilesOnServer = new JLabel("Files on Server");
		lblFilesOnServer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFilesOnServer.setBounds(27, 166, 109, 14);
		frame.getContentPane().add(lblFilesOnServer);
		
		serverFileList = new JList<String>();
		serverFileList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		//serverFileList.setPrototypeCellValue("--------------------------");
		serverFileList.setBounds(27, 191, 138, 146);
		frame.getContentPane().add(serverFileList);
		
		btnDownloadAFile = new JButton("Download a File");
		/*btnDownloadAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fname = JOptionPane.showInputDialog("Enter file name: ");
				
				//if action successful
				//JOptionPane.showMessageDialog(null, "Download Successful");
				//add file to list
				//else
				//JOptionPane.showMessageDialog(null, "Download Unsuccessful");
			}
		});*/
		btnDownloadAFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDownloadAFile.setBounds(27, 354, 155, 23);
		frame.getContentPane().add(btnDownloadAFile);
		
		lblFilesOnSystem = new JLabel("Files on System");
		lblFilesOnSystem.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFilesOnSystem.setBounds(243, 166, 109, 14);
		frame.getContentPane().add(lblFilesOnSystem);
		
		clientFileList = new JList<String>();
		clientFileList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		clientFileList.setBounds(243, 191, 138, 146);
		frame.getContentPane().add(clientFileList);
		
		btnUploadAFile = new JButton("Upload a File");
		/*btnUploadAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fname = JOptionPane.showInputDialog("Enter file name: ");
				
				//if action successful
				//JOptionPane.showMessageDialog(null, "Upload successful");
				//else
				//JOptionPane.showMessageDialog(null, "Upload Unsuccessful");
			}
		});*/
		btnUploadAFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnUploadAFile.setBounds(243, 354, 155, 23);
		frame.getContentPane().add(btnUploadAFile);
		
		lblOnlinePeers = new JLabel("Online Peers");
		lblOnlinePeers.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblOnlinePeers.setBounds(452, 166, 109, 14);
		frame.getContentPane().add(lblOnlinePeers);
		
		peerList = new JList<String>();
		peerList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		peerList.setBounds(452, 191, 138, 146);
		frame.getContentPane().add(peerList);
		
		btnChat = new JButton("Peer-to-Peer ");
		btnChat.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnChat.setBounds(452, 355, 125, 23);
		frame.getContentPane().add(btnChat);
	}
	
	
}
