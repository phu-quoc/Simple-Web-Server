package httpserver;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Desktop;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainFrame {

	private JFrame frame;
	private JTextField tfPort;
	private HttpServer server;
	private JTextArea txtLogs;
	private JLabel lblStatus;
	private JLabel lblServer;
	private JLabel lblPort;
	private JLabel lblAction;
	private JLabel lblJava;
	private JLabel lblCurrentStatus;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnFile;
	private JButton btnQuit;
	private Thread t;
	private JLabel lblQuantity;
	private JTextField tfQuantity;
	private JTextArea txtClients;
	private JLabel lblPath;
	private String path = "";
	private JButton btnInfo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
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
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Web Server");
		frame.setBounds(100, 100, 745, 577);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane panelLogs = new JScrollPane();
		panelLogs.setBounds(10, 267, 711, 263);
		frame.getContentPane().add(panelLogs);
		
		txtLogs = new JTextArea();
		txtLogs.setEditable(false);
		txtLogs.setLineWrap(true);
		panelLogs.setViewportView(txtLogs);
		
		JPanel panelServers = new JPanel();
		panelServers.setBorder(new TitledBorder(null, "Servers", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelServers.setBounds(10, 10, 616, 76);
		frame.getContentPane().add(panelServers);
		panelServers.setLayout(null);
		
		lblStatus = new JLabel("Status");
		lblStatus.setBounds(100, 20, 45, 13);
		panelServers.add(lblStatus);
		
		lblServer = new JLabel("Server");
		lblServer.setBounds(10, 20, 45, 13);
		panelServers.add(lblServer);
		
		lblPort = new JLabel("Port");
		lblPort.setBounds(200, 20, 45, 13);
		panelServers.add(lblPort);
		
		lblAction = new JLabel("Action");
		lblAction.setBounds(300, 20, 45, 13);
		panelServers.add(lblAction);
		
		tfPort = new JTextField();
		tfPort.setText("443");
		tfPort.setColumns(10);
		tfPort.setBounds(189, 45, 45, 21);
		panelServers.add(tfPort);
		
		lblJava = new JLabel("Java");
		lblJava.setBounds(10, 45, 45, 21);
		panelServers.add(lblJava);
		
		lblCurrentStatus = new JLabel("Off");
		lblCurrentStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentStatus.setOpaque(true);
		lblCurrentStatus.setBounds(85, 45, 65, 21);
		panelServers.add(lblCurrentStatus);
		
		btnStart = new JButton("Start");
		btnStart.setBounds(283, 45, 65, 21);
		panelServers.add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.setBounds(283, 45, 65, 21);
		btnStop.setVisible(false);
		panelServers.add(btnStop);
		
		lblPath = new JLabel("Path");
		lblPath.setBounds(541, 20, 45, 13);
		panelServers.add(lblPath);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File(".\\"));
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
		            File selectedFolder = fileChooser.getSelectedFile();
		            path = selectedFolder.getAbsolutePath()+"\\";
				}
				
			}
		});
		btnBrowse.setBounds(514, 45, 92, 21);
		panelServers.add(btnBrowse);
		
		btnFile = new JButton("File");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File("..\\"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnFile.setBounds(636, 18, 85, 21);
		frame.getContentPane().add(btnFile);
		
		btnQuit = new JButton("Quit");
		btnQuit.setBounds(636, 80, 85, 21);
		frame.getContentPane().add(btnQuit);
		
		JScrollPane panelClients = new JScrollPane();
		panelClients.setBounds(10, 93, 616, 164);
		frame.getContentPane().add(panelClients);
		
		txtClients = new JTextArea();
		txtClients.setEditable(false);
		panelClients.setViewportView(txtClients);
		
		lblQuantity = new JLabel("Clients:");
		lblQuantity.setBounds(636, 120, 45, 13);
		frame.getContentPane().add(lblQuantity);
		
		tfQuantity = new JTextField();
		tfQuantity.setEditable(false);
		tfQuantity.setText("0");
		tfQuantity.setColumns(10);
		tfQuantity.setBounds(636, 138, 45, 21);
		frame.getContentPane().add(tfQuantity);
		
		btnInfo = new JButton("Info");
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Config port\n"
						+ "Start, stop server\n"
						+ "Open file system\n"
						+ "Config file path"
						+ "Support HTTP GET and POST\n"
						+ "Support secure protocol SSL"
						+ "Support Cookies, Connection: keep-alive, Content-Length\n"
						+ "Display current connect informations, numbers, IP address, port of clients\n"
						+ "Display request informations\n"
						+ "Display response informations","Information", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnInfo.setBounds(636, 49, 85, 21);
		frame.getContentPane().add(btnInfo);
		
		btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
		
		btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
            		int port = Integer.parseInt(tfPort.getText());
            		server = new HttpServer(port, txtLogs, tfQuantity, txtClients, path);
            		t = new Thread(server);
          	      	t.start();
          	      	lblCurrentStatus.setText("On");
          	      	lblCurrentStatus.setBackground(new Color(0, 255, 102));
          	      	tfPort.setEditable(false);
          	      	btnStart.setVisible(false);
          	      	btnStop.setVisible(true);
          	      	log("Starting server on port "+tfPort.getText());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
//					log("Java", e1.getMessage());
				}
            }
        });	
		
		btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
          	      	t.interrupt();
          	      	server.stopThread();
          	      	lblCurrentStatus.setText("Off");
          	      	lblCurrentStatus.setBackground(new Color(0, 0, 0, 0));;
          	      	tfPort.setEditable(true);
          	      	btnStart.setVisible(true);
          	      	btnStop.setVisible(false);
          	      	log("Server stopped.");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
//					log("Java", e1.getMessage());
				}
            }
        });	
		
		
	}
	
	public void log(String message) {
		if(message != "")
		{
			txtLogs.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" [Java] "+message+"\n");
		}
	}
}


