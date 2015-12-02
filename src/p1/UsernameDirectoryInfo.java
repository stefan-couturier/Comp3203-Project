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
	private GridBagConstraints c;
	private JLabel prompt;
	private JLabel usernameLabel;
	private JTextField usernameInput;
	private JLabel directoryLabel;
	private JLabel directorySelectedLabel;
	private JButton chooseDirectoryButton;
	private JButton proceedButton;

	private ActionListener proceedButtonListener;
	private ActionListener chooseDirectoryButtonListener;

	private JFileChooser fileChooser;
	private File fileChosen;
	private String username;
	
	private boolean clientInfoReady = false;

	UsernameDirectoryInfo() 
	{
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

		frame = new JFrame("Welcome to Awesome");
		frame.getContentPane().setLayout(new GridBagLayout());

		c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10); // this will apply to all components until reset

		prompt = new JLabel("Choose an Awesome username and select a home directory... ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(prompt, c);

		usernameLabel = new JLabel("Username:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(usernameLabel, c);

		usernameInput = new JTextField();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(usernameInput, c);

		directoryLabel = new JLabel("Diretory:");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		frame.getContentPane().add(directoryLabel, c);

		directorySelectedLabel = new JLabel(".....");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 2;
		frame.getContentPane().add(directorySelectedLabel, c);

		chooseDirectoryButton = new JButton("Choose Directory");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 3;
		chooseDirectoryButton.addActionListener(chooseDirectoryButtonListener);
		frame.getContentPane().add(chooseDirectoryButton, c);

		proceedButton = new JButton("Proceed");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 3;
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

	public void verifyProceed() {
		
		username = usernameInput.getText();
		username = username.trim();
		String path = fileChosen.getAbsolutePath();
		if (username.equals("")) {
			JOptionPane.showMessageDialog(frame, "Need valid username.");
		}
		else if (fileChosen == null || !fileChosen.isDirectory() || 
				!fileChosen.canWrite() || !fileChosen.canRead()) {
			// can't use that directory
		} else {
			String ip = JOptionPane.showInputDialog("Please input IP of the Server: ");
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