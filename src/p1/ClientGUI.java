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
	private JButton btnRefreshLists;
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
	private JButton btnDownloadPeerFile;
	
	private String IPAddress = "0.0.0.0";
	private int portNumber = 0;
	
	
	private String selectedServerFile;
	private String selectedClientFile;
	private String selectedPeer;
	private String selectedPeerFile;
	private boolean requestingDownload;
	private boolean requestingUpload;
	private boolean requestingRefresh;
	private boolean requestingPeerFile;
	private boolean requestingSendToChat;
	private boolean requestingPeerFileList;
	
	private ArrayList<String> serverFiles;
	private ArrayList<String> clientFiles;
	private ArrayList<String> peers;
	private ArrayList<String> peerFiles;
	
	private ActionListener refreshButtonListener;
	private ActionListener downloadButtonListener;
	private ActionListener uploadButtonListener;
	private ActionListener downloadPeerFileButtonListener;
	private ListSelectionListener serverFileListSelectionListener;
	private ListSelectionListener clientFileListSelectionListener;
	private ListSelectionListener peerListSelectionListener;
	private ListSelectionListener peerFilesListSelectionListener;
	private ActionListener sendButtonListener;
	
	private JLabel lblChat;
	private JScrollPane chatScrollPane;
	private JButton btnSend;
	//private ArrayList<String> fileRequests;
	//private String selectedFileRequest;
	//private ListSelectionListener fileRequestListSelectionListener;
	
	//Timer timer;
	//private ActionListener timerTick;
	public JTextField tbSend;
	private String chatMessage;
	private JTextArea chatArea;

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
		peerFiles = new ArrayList<String>();
		//fileRequests = new ArrayList<String>();
		
		//timer = new Timer(5000, null);	
		//peers.add("//TODO:");
		
		selectedServerFile = null;
		selectedClientFile = null;
		selectedPeer = null;
		selectedPeerFile = null;
		requestingDownload = false;
		requestingUpload = false;
		//selectedFileRequest = null;
		requestingPeerFile = false;
		
		
		refreshButtonListener = new ActionListener() {
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
		
		downloadPeerFileButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				requestPeerFile();
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
		
		peerFilesListSelectionListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				selectPeerFile();
			}
		};
		
		/*fileRequestListSelectionListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
			}
		};*/
		
		/*timerTick = new ActionListener() {
			public void actionPerformed(ActionEvent event){
				requestRefresh();

				System.out.println("tick");

			}
		};*/
		
		sendButtonListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				sendToChat();
			}
		};
		
		
		
		initialize();
		this.frame.setVisible(true);
		enableListeners();
		///timer.start();
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

	public synchronized boolean isRequestingPeerFile() {
		return requestingPeerFile;
	}
	
	public synchronized boolean isRequestingPeerFileList() {
		return requestingPeerFileList;
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
	
	public synchronized void setRequestingPeerFile(boolean b) {
		requestingPeerFile = b;
	}
	
	public synchronized boolean isRequestingSendToChat(){
		return requestingSendToChat;
	}
	
	public synchronized void setRequestingSendToChat(boolean b){
		requestingSendToChat = b;
		tbSend.setText("");
	}
	
	public synchronized void setRequestingPeerFileList(boolean b) {
		requestingPeerFileList = b;
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
		requestingRefresh = true;
	}

	public void requestPeerFile() {
		if (selectedPeerFile != null) {
			requestingPeerFile = true;
		}
	}
	
	public void requestPeerFileList() {
		if (selectedPeer != null) {
			requestingPeerFileList = true;
		}
	}
	
	public void selectServerFile() {
		selectedServerFile = (String) serverFileList.getSelectedValue();
	}
	
	
	public void selectClientFile() {
		selectedClientFile = (String) clientFileList.getSelectedValue();
	}
	
	public void selectPeer() {
		if((String) peerList.getSelectedValue() != null){
			selectedPeer = (String) peerList.getSelectedValue();
			requestPeerFileList();
		}
	}
	
	public void selectPeerFile() {
		selectedPeerFile = (String) peerFileList.getSelectedValue();
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
	
	public String getSelectedPeerFile() {
		return selectedPeerFile;
	}
	
	/*public String getSelectedFileRequest() {
		return selectedFileRequest;
	}*/
	
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
	
	public String getChatMessage(){
		return chatMessage;
	}
	
	public void setChatMessage(String m){
		chatMessage = m;
	}
	
	public void sendToChat(){
		chatMessage = "";
		chatMessage = tbSend.getText();
		requestingSendToChat = true;
	}
	
	public JFrame getFrame(){
		return frame;
	}

	// Update lists
	@SuppressWarnings("unchecked")
	public void updateLists(ArrayList<String> s, ArrayList<String> c, 
			ArrayList<String> p) {
		System.out.println("CLIENT GUI:\tupdateLists");
		disableListeners();
		selectedServerFile = null;
		selectedClientFile = null;
		//selectedPeer = null;
		//selectedFileRequest = null;
		
		serverFiles = new ArrayList<String>(s);
		clientFiles = new ArrayList<String>(c);
		peers = new ArrayList<String>(p);
		//fileRequests = new ArrayList<String>(r);

		String strServer[] = new String[1]; // needed to establish array type for next line
		serverFileList.setListData((String[]) serverFiles.toArray(strServer));
		String strClient[] = new String[1]; // needed to establish array type for next line
		clientFileList.setListData((String[]) clientFiles.toArray(strClient));
		String strPeer[] = new String[1]; // needed to establish array type for next line
		peerList.setListData((String[]) peers.toArray(strPeer));
		//String strRequests[] = new String[1]; // needed to establish array type for next line

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
		btnRefreshLists.addActionListener(refreshButtonListener);
		btnDownloadAFile.addActionListener(downloadButtonListener);
		btnUploadAFile.addActionListener(uploadButtonListener);
		serverFileList.addListSelectionListener(serverFileListSelectionListener);
		clientFileList.addListSelectionListener(clientFileListSelectionListener);
		peerList.addListSelectionListener(peerListSelectionListener);
		peerFileList.addListSelectionListener(peerFilesListSelectionListener);
		btnSend.addActionListener(sendButtonListener);
		btnDownloadPeerFile.addActionListener(downloadPeerFileButtonListener);
		//timer.addActionListener(timerTick);
	}


	// Disable listeners
	private void disableListeners() {
		btnRefreshLists.removeActionListener(refreshButtonListener);
		btnDownloadAFile.removeActionListener(downloadButtonListener);
		btnUploadAFile.removeActionListener(uploadButtonListener);
		serverFileList.removeListSelectionListener(serverFileListSelectionListener);
		clientFileList.removeListSelectionListener(clientFileListSelectionListener);
		peerList.removeListSelectionListener(peerListSelectionListener);
		peerFileList.removeListSelectionListener(peerFilesListSelectionListener);
		btnSend.removeActionListener(sendButtonListener);
		btnDownloadPeerFile.removeActionListener(downloadPeerFileButtonListener);
		//timer.removeActionListener(timerTick);
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 700, 700);
		frame.setLocation(20, 20);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		lblClient = new JLabel("Client");
		lblClient.setFont(new Font("Franklin Gothic Medium", Font.BOLD | Font.ITALIC, 20));
		lblClient.setBounds(27, 22, 81, 24);
		frame.getContentPane().add(lblClient);
		
		btnRefreshLists = new JButton("Refresh");
		btnRefreshLists.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRefreshLists.setToolTipText(
				"<html>Refresh your lists of Server files" + 
		        "<br/>and Online Peers.</html>");
		btnRefreshLists.setBounds(27, 98, 160, 23);
		frame.getContentPane().add(btnRefreshLists);
		
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
		serverFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		serverFileScrollPane = new JScrollPane(serverFileList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		serverFileScrollPane.setBounds(27, 191, 190, 146);
		frame.getContentPane().add(serverFileScrollPane);
		
		btnDownloadAFile = new JButton("Download a File");
		btnDownloadAFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDownloadAFile.setToolTipText(
				"<html>Download the file you have selected" +
				"<br/>from the Server.</html>");
		btnDownloadAFile.setBounds(27, 354, 160, 23);
		frame.getContentPane().add(btnDownloadAFile);
		
		lblFilesOnSystem = new JLabel("Files on System");
		lblFilesOnSystem.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFilesOnSystem.setBounds(243, 166, 109, 14);
		frame.getContentPane().add(lblFilesOnSystem);
		
		clientFileList = new JList<String>();
		clientFileList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		clientFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		clientFileScrollPane = new JScrollPane(clientFileList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		clientFileScrollPane.setBounds(243, 191, 190, 146);
		frame.getContentPane().add(clientFileScrollPane);
		
		btnUploadAFile = new JButton("Upload a File");
		btnUploadAFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnUploadAFile.setToolTipText(
				"<html>Upload your local file that you" +
		        "<br/>have selected to the Server.</html>");
		btnUploadAFile.setBounds(243, 354, 160, 23);
		frame.getContentPane().add(btnUploadAFile);
		
		lblOnlinePeers = new JLabel("Online Peers");
		lblOnlinePeers.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblOnlinePeers.setBounds(460, 166, 109, 14);
		frame.getContentPane().add(lblOnlinePeers);
		
		peerList = new JList<String>();
		peerList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		peerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		peerScrollPane = new JScrollPane(peerList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		peerScrollPane.setBounds(460, 191, 190, 146);
		frame.getContentPane().add(peerScrollPane);
		
		btnDownloadPeerFile = new JButton("Download a Peer File");
		/*btnDownloadPeerFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});*/
		btnDownloadPeerFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnDownloadPeerFile.setToolTipText(
				"<html>Download the file you have selected" +
		        "<br/>from your peer's file list.</html>");
		btnDownloadPeerFile.setBounds(460, 355, 160, 23);
		frame.getContentPane().add(btnDownloadPeerFile);
		
		
		///////////////////////////////////////
		lblFilesOnPeer = new JLabel("Peer's files");
		lblFilesOnPeer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFilesOnPeer.setToolTipText(
				"<html>These are the files that the currently selected" +
				"<br/>peer has to share. To download one, select it and" + 
				"<br/>hit 'Download a Peer File' above.</html>");
		lblFilesOnPeer.setBounds(460, 400, 109, 14);
		frame.getContentPane().add(lblFilesOnPeer);
		
		peerFileList = new JList<String>();
		peerFileList.setFont(new Font("Tahoma", Font.PLAIN, 13));
		peerFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		peerFileScrollPane = new JScrollPane(peerFileList,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		peerFileScrollPane.setBounds(460, 425, 190, 146);
		frame.getContentPane().add(peerFileScrollPane);
		
		///////////////////////////////////////
		
		lblChat = new JLabel("Chat");
		lblChat.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblChat.setBounds(27, 400, 109, 14);
		frame.getContentPane().add(lblChat);
		chatScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		chatScrollPane.setBounds(27, 425, 406, 146);
		frame.getContentPane().add(chatScrollPane);
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatScrollPane.setViewportView(chatArea);
		
		btnSend = new JButton("Send");
		btnSend.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSend.setBounds(27, 627, 81, 23);
		frame.getContentPane().add(btnSend);
		
		tbSend = new JTextField();
		tbSend.setBounds(27, 596, 406, 20);
		tbSend.setToolTipText("Type a message here to post to the Chat Board.");
		frame.getContentPane().add(tbSend);
		tbSend.setColumns(10);
		
		JButton btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame,
						"Welcome to the File Transfer System,\nThis application allows you to transfer files between yourself and the server or " +
					    "yourself and another user who is currently connected.\nIf you are unsure what a button does hover over it with your mouse "+
					    "and a description box will appear.\n\t\tPlease enjoy our program !",
					    "Help",
					    JOptionPane.PLAIN_MESSAGE);
			}
			
		});
		btnHelp.setBounds(27, 64, 169, 23);
		frame.getContentPane().add(btnHelp);
	}
	
	public void appendChat(String message){
		chatArea.append(message+"\n");
	}
}
