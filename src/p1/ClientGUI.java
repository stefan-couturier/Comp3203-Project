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
	private JLabel lblFilesOnPeer;
	private JList<String> serverFileList;
	private JList<String> clientFileList;
	private JList<String> peerList;
	private JList<String> peerFileList;
	private JScrollPane serverFileScrollPane;
	private JScrollPane clientFileScrollPane;
	private JScrollPane peerScrollPane;
	private JScrollPane peerFileScrollPane;
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
	private ArrayList<String> peerFiles;
	
	private ActionListener initiateButtonListener;
	private ActionListener terminateButtonListener;
	private ActionListener downloadButtonListener;
	private ActionListener uploadButtonListener;
	private ListSelectionListener serverFileListSelectionListener;
	private ListSelectionListener clientFileListSelectionListener;
	private ListSelectionListener peerListSelectionListener;
	
	private JLabel lblFileRequests;
	private JList<String> fileRequestList;
	private JScrollPane fileRequestScrollPane;
	private JButton btnPostRequest;
	private JButton btnRespondToRequest;
	private ArrayList<String> fileRequests;
	private String selectedFileRequest;
	private boolean requestingPost;
	private boolean requestingResponse;
	private ActionListener postButtonListener;
	private ActionListener respondButtonListener;
	private ListSelectionListener fileRequestListSelectionListener;
	
	Timer timer;
	private ActionListener timerTick;

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
		fileRequests = new ArrayList<String>();
		
		timer = new Timer(5000, null);	
		//peers.add("//TODO:");
		
		selectedServerFile = null;
		selectedClientFile = null;
		selectedPeer = null;
		requestingDownload = false;
		requestingUpload = false;
		selectedFileRequest = null;
		requestingPost = false;
		requestingResponse = false;
		
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
		
		postButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				requestPost();
			}
		};
		
		respondButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				requestResponse();
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
		
		fileRequestListSelectionListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				selectFileRequest();
			}
		};
		
		timerTick = new ActionListener() {
			public void actionPerformed(ActionEvent event){
				requestRefresh();
			}
		};
		
		initialize();
		this.frame.setVisible(true);
		enableListeners();
		timer.start();
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
	
	public synchronized boolean isRequestingPost() {
		return requestingPost;
	}
	
	public synchronized boolean isRequestingResponse() {
		return requestingResponse;
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
	
	public synchronized void setRequestingPost(boolean b) {
		requestingPost = b;
	}
	
	public synchronized void setRequestingResponse(boolean b) {
		requestingResponse = b;
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
	
	public void requestPost() {
		requestingPost = true;
	}
	
	public void requestResponse() {
		if (selectedFileRequest != null)
			requestingResponse = true;
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
	
	public void selectFileRequest() {
		selectedFileRequest = (String) fileRequestList.getSelectedValue();
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
	
	public String getSelectedFileRequest() {
		return selectedFileRequest;
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
	public void updateLists(ArrayList<String> s, ArrayList<String> c, 
			ArrayList<String> p, ArrayList<String> r) {
		System.out.println("CLIENT GUI:\tupdateLists");
		disableListeners();
		selectedServerFile = null;
		selectedClientFile = null;
		selectedPeer = null;
		selectedFileRequest = null;
		
		serverFiles = new ArrayList<String>(s);
		clientFiles = new ArrayList<String>(c);
		peers = new ArrayList<String>(p);
		fileRequests = new ArrayList<String>(r);

		String strServer[] = new String[1]; // needed to establish array type for next line
		serverFileList.setListData((String[]) serverFiles.toArray(strServer));
		String strClient[] = new String[1]; // needed to establish array type for next line
		clientFileList.setListData((String[]) clientFiles.toArray(strClient));
		String strPeer[] = new String[1]; // needed to establish array type for next line
		peerList.setListData((String[]) peers.toArray(strPeer));
		String strRequests[] = new String[1]; // needed to establish array type for next line
		fileRequestList.setListData((String[]) fileRequests.toArray(strRequests));

		enableListeners();
	}


	// Update client list only 
	// (can be done more frequently since it doesn't require a server request)
	public void updateClientList(ArrayList<String> c) {
		System.out.println("CLIENT GUI:\tupdateClientList");
		disableListeners();
		selectedClientFile = null;

		clientFiles = new ArrayList<String>(c);

		String strClient[] = new String[1]; // needed to establish array type for next line
		clientFileList.setListData((String[]) clientFiles.toArray(strClient));

		enableListeners();
	}
	
	// Update peer file list only 
		
	public void updatePeerFileList(ArrayList<String> c) {
		System.out.println("CLIENT GUI:\tupdatePeerFileList");
		disableListeners();
		//selectedClientFile = null;

		peerFiles = new ArrayList<String>(c);

		String strClient[] = new String[1]; // needed to establish array type for next line
		peerFileList.setListData((String[]) peerFiles.toArray(strClient));
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
		btnPostRequest.addActionListener(postButtonListener);
		btnRespondToRequest.addActionListener(respondButtonListener);
		fileRequestList.addListSelectionListener(fileRequestListSelectionListener);
		
		timer.addActionListener(timerTick);
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
		btnPostRequest.removeActionListener(postButtonListener);
		btnRespondToRequest.removeActionListener(respondButtonListener);
		fileRequestList.removeListSelectionListener(fileRequestListSelectionListener);
		
		timer.removeActionListener(timerTick);
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 691, 680);
		frame.setLocation(20, 20);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		lblClient = new JLabel("Client");
		lblClient.setFont(new Font("Franklin Gothic Medium", Font.BOLD | Font.ITALIC, 20));
		lblClient.setBounds(27, 22, 81, 24);
		frame.getContentPane().add(lblClient);
		
		btnInitiateConnection = new JButton("Initiate Connection");
		btnInitiateConnection.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnInitiateConnection.setBounds(27, 57, 138, 23);
		frame.getContentPane().add(btnInitiateConnection);
		
		btnTerminateConnection = new JButton("Refresh");
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
		//serverFileList.setBounds(27, 191, 160, 150);
		serverFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//frame.getContentPane().add(serverFileList);
		serverFileScrollPane = new JScrollPane(serverFileList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		serverFileScrollPane.setBounds(27, 191, 190, 146);
		frame.getContentPane().add(serverFileScrollPane);
		
		btnDownloadAFile = new JButton("Download a File");
		btnDownloadAFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDownloadAFile.setBounds(27, 354, 155, 23);
		frame.getContentPane().add(btnDownloadAFile);
		
		lblFilesOnSystem = new JLabel("Files on System");
		lblFilesOnSystem.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFilesOnSystem.setBounds(243, 166, 109, 14);
		frame.getContentPane().add(lblFilesOnSystem);
		
		clientFileList = new JList<String>();
		clientFileList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		//clientFileList.setBounds(243, 191, 160, 146);
		clientFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//frame.getContentPane().add(clientFileList);
		clientFileScrollPane = new JScrollPane(clientFileList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		clientFileScrollPane.setBounds(243, 191, 190, 146);
		frame.getContentPane().add(clientFileScrollPane);
		
		btnUploadAFile = new JButton("Upload a File");
		btnUploadAFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnUploadAFile.setBounds(243, 354, 155, 23);
		frame.getContentPane().add(btnUploadAFile);
		
		lblOnlinePeers = new JLabel("Online Peers");
		lblOnlinePeers.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblOnlinePeers.setBounds(460, 166, 109, 14);
		frame.getContentPane().add(lblOnlinePeers);
		
		peerList = new JList<String>();
		peerList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		//peerList.setBounds(460, 191, 160, 146);
		peerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//frame.getContentPane().add(peerList);
		peerScrollPane = new JScrollPane(peerList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		peerScrollPane.setBounds(460, 191, 190, 146);
		frame.getContentPane().add(peerScrollPane);
		
		btnChat = new JButton("Peer-to-Peer ");
		btnChat.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnChat.setBounds(460, 355, 125, 23);
		frame.getContentPane().add(btnChat);
		
		
		///////////////////////////////////////
		lblFilesOnPeer = new JLabel("Peer's files");
		lblFilesOnPeer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFilesOnPeer.setBounds(460+(460-243), 166, 109, 14);
		frame.getContentPane().add(lblFilesOnPeer);
		
		peerFileList = new JList<String>();
		peerFileList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		//peerList.setBounds(460, 191, 160, 146);
		peerFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//frame.getContentPane().add(peerList);
		peerFileScrollPane = new JScrollPane(peerFileList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		peerFileScrollPane.setBounds(460+460-243, 191, 190, 146);
		frame.getContentPane().add(peerFileScrollPane);
		
//		btnChat = new JButton("Peer-to-Peer ");
//		btnChat.setFont(new Font("Tahoma", Font.PLAIN, 13));
//		btnChat.setBounds(460, 355, 125, 23);
//		frame.getContentPane().add(btnChat);
		
		///////////////////////////////////////
		
		lblFileRequests = new JLabel("File Requests");
		lblFileRequests.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFileRequests.setBounds(27, 400, 109, 14);
		frame.getContentPane().add(lblFileRequests);
		
		fileRequestList = new JList<String>();
		fileRequestList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		fileRequestList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileRequestScrollPane = new JScrollPane(fileRequestList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fileRequestScrollPane.setBounds(27, 425, 330, 146);
		frame.getContentPane().add(fileRequestScrollPane);
		
		btnPostRequest = new JButton("Post a Request");
		btnPostRequest.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnPostRequest.setBounds(27, 600, 155, 23);
		frame.getContentPane().add(btnPostRequest);
		
		btnRespondToRequest = new JButton("Respond to Request");
		btnRespondToRequest.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnRespondToRequest.setBounds(200, 600, 155, 23);
		frame.getContentPane().add(btnRespondToRequest);
	}
}
