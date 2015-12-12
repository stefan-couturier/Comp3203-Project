package p1;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UsernameDirectoryInfo extends JPanel {

	private JFrame frame;
	private GridBagLayout layout;
	private GridBagConstraints c;
	private JLabel prompt;
	private JLabel usernameLabel;
	private JTextField usernameInput;
	private JLabel directoryLabel;
	private JLabel directorySelectedLabel;
	private JLabel ipLabel;
	private JTextField ipInput;
	private JButton chooseDirectoryButton;
	private JButton proceedButton;

	private ActionListener proceedButtonListener;
	private ActionListener chooseDirectoryButtonListener;

	private JFileChooser fileChooser;
	private File fileChosen;
	private String username;
	private String ip;
	
	private boolean clientInfoReady = false;

	UsernameDirectoryInfo() 
	{
		super();
		
		proceedButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				verifyProceed();
			}
		};

		chooseDirectoryButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				chooseDirectory();
			}
		};

		fileChooser = null;
		fileChosen = null;
		username = "";
		ip = "";

		frame = new JFrame("Welcome to Awesome");
		frame.getContentPane().setLayout(new GridBagLayout());

		c = new GridBagConstraints();
		// these will apply to all components until reset
		c.insets = new Insets(10, 10, 10, 10); 
		// these weights handle resizing.... half-assedly
		c.weightx = 1.0;
		c.weighty = 1.0;

		prompt = new JLabel("Choose a Server to connect with, an Awesome Username and a local Directory... ");
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(prompt, c);

		c.weighty = 0.0; // prevents textfields from growing in y direction
		
		ipLabel = new JLabel("IP address:");
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(ipLabel, c);

		ipInput = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(ipInput, c);
		
		usernameLabel = new JLabel("Username:");
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		frame.getContentPane().add(usernameLabel, c);

		usernameInput = new JTextField();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 2;
		frame.getContentPane().add(usernameInput, c);

		c.weighty = 1.0;
		
		directoryLabel = new JLabel("Diretory:");
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 3;
		frame.getContentPane().add(directoryLabel, c);

		directorySelectedLabel = new JLabel(".....");
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 3;
		frame.getContentPane().add(directorySelectedLabel, c);

		chooseDirectoryButton = new JButton("Choose Directory");
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 4;
		chooseDirectoryButton.addActionListener(chooseDirectoryButtonListener);
		frame.getContentPane().add(chooseDirectoryButton, c);

		proceedButton = new JButton("Proceed");
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 4;
		proceedButton.addActionListener(proceedButtonListener);
		frame.getContentPane().add(proceedButton, c);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public File getFileChosen() {
		return fileChosen;
	}

	public String getUsername() {
		return username;
	}
	
	public String getIP() {
		return ip;
	}

	public void verifyProceed() {
		
		username = usernameInput.getText();
		username = username.trim();
		ip = ipInput.getText();
		ip = ip.trim();
		String path = fileChosen.getAbsolutePath();
		if (username.equals("") || ip.equals("")) {
			JOptionPane.showMessageDialog(frame, "Need a valid Server and Username.");
		}
		else if (fileChosen == null || !fileChosen.isDirectory() || 
				!fileChosen.canWrite() || !fileChosen.canRead()) {
			JOptionPane.showMessageDialog(frame, "You don't have permission to use that Directory");
		} else {
			//String ip = JOptionPane.showInputDialog("Please input IP of the Server: ");
			Client c = new Client(ip, 45000, path, username);
			frame.dispose();
			// good to go
			
		}
	}

	public void chooseDirectory() {
		fileChooser = new JFileChooser(); 
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle("Choose a Directory");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setVisible(true);
		// the condition in this 'if-statement' required extending JPanel
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			fileChosen = fileChooser.getSelectedFile();
			directorySelectedLabel.setText(fileChosen.getAbsolutePath());
		}
	}


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UsernameDirectoryInfo window = new UsernameDirectoryInfo();
					window.frame.pack();
					window.frame.setLocationRelativeTo(null); // puts frame to middle of screen
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public boolean isClientInfoReady() { return clientInfoReady; }

}