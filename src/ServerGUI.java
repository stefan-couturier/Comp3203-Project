import java.awt.EventQueue;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;


public class ServerGUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
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
	public ServerGUI() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 623, 456);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblServer = new JLabel("Server");
		lblServer.setFont(new Font("Franklin Gothic Medium", Font.BOLD | Font.ITALIC, 20));
		lblServer.setBounds(27, 11, 98, 23);
		frame.getContentPane().add(lblServer);
		
		JButton btnNewConnection = new JButton("New Connection");
		btnNewConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ip = JOptionPane.showInputDialog("Enter the IP Address: ");
				
				//if connection successfull
				//JOptionPane.showMessageDialog(null, "Connection Successful");
				//add this client to the client list
				//else
				//JOptionPane.showMessageDialog(null, "Connection Unsuccessfull");
			}
		});
		btnNewConnection.setBounds(27, 51, 137, 23);
		frame.getContentPane().add(btnNewConnection);
		
		JButton btnNewButton = new JButton("Terminate Connection");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ip = JOptionPane.showInputDialog("Enter the IP Address: ");
				
				//if termination successful
				//JOptionPane.showMessageDialog(null, "Termination Successful");
				//remove client from list
				//else
				//JOptionPane.showMessageDialog(null, "Termination Unsuccessful");

			}
		});
		btnNewButton.setBounds(27, 85, 158, 23);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblServerStatus = new JLabel("Server Status:");
		lblServerStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblServerStatus.setBounds(350, 43, 98, 14);
		frame.getContentPane().add(lblServerStatus);
		
		JLabel lbldisplaysStatus = new JLabel("//Displays status");
		lbldisplaysStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lbldisplaysStatus.setBounds(479, 43, 105, 16);
		frame.getContentPane().add(lbldisplaysStatus);
		
		JButton btnSendAFile = new JButton("Send a File");
		btnSendAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fname = JOptionPane.showInputDialog("Enter file name: ");
				
				//if action successful
				//JOptionPane.showMessageDialog(null, "File sent");
				//else
				//JOptionPane.showMessageDialog(null, "File not sent");
 
			}
		});
		btnSendAFile.setBounds(189, 227, 137, 23);
		frame.getContentPane().add(btnSendAFile);
		
		JButton btnDownloadAFile = new JButton("Download a File");
		btnDownloadAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fname = JOptionPane.showInputDialog("Enter file name: ");
				
				//if action successful
				//JOptionPane.showMessageDialog(null, "File received");
				//add file to list
				//else
				//JOptionPane.showMessageDialog(null, "File not received, try again");
			}
		});
		btnDownloadAFile.setBounds(189, 281, 137, 23);
		frame.getContentPane().add(btnDownloadAFile);
		
		JLabel lblFileList = new JLabel("File List:");
		lblFileList.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFileList.setBounds(27, 152, 69, 14);
		frame.getContentPane().add(lblFileList);
		
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
		list.setBounds(27, 181, 152, 208);
		frame.getContentPane().add(list);
		
		JLabel lblClientList = new JLabel("Client List:");
		lblClientList.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblClientList.setBounds(349, 152, 79, 14);
		frame.getContentPane().add(lblClientList);
		
		JList list_1 = new JList();
		list_1.setModel(new AbstractListModel() {
			String[] values = new String[] {"Client 1", "Client 2", "Client 3"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list_1.setBounds(349, 181, 152, 208);
		frame.getContentPane().add(list_1);
		
		JLabel lblPortNumber = new JLabel("Port Number:");
		lblPortNumber.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPortNumber.setBounds(350, 68, 98, 14);
		frame.getContentPane().add(lblPortNumber);
		
		JLabel label = new JLabel("45000");
		label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		label.setBounds(479, 68, 62, 14);
		frame.getContentPane().add(label);
		
		JLabel lblIpAddress = new JLabel("IP Address: ");
		lblIpAddress.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblIpAddress.setBounds(350, 18, 78, 14);
		frame.getContentPane().add(lblIpAddress);
		
		JLabel label_1 = new JLabel("0.0.0.0");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		label_1.setBounds(479, 18, 46, 14);
		frame.getContentPane().add(label_1);
		
		JLabel lblActiveConnections = new JLabel("Active Connections:");
		lblActiveConnections.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblActiveConnections.setBounds(350, 94, 137, 14);
		frame.getContentPane().add(lblActiveConnections);
		
		JLabel lblNewLabel = new JLabel("#");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(479, 94, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
	}
}
