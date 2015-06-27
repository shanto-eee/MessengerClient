import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class Client extends JFrame {
	
	private JTextField enterField;
	private JTextArea displayArea; 
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String chatServer;
	private Socket client;
	
	
	// constructor (setting GUI)
	public Client(String host)
	{
		
		chatServer = host;
		
		this.setTitle("Client");
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(290, 460);
		this.setResizable(false);
		
		enterField = new JTextField();
		enterField.setBounds(0, 0, 284, 40);
		enterField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				sendData(event.getActionCommand());
				enterField.setText("");
				
			}
		});
		getContentPane().setLayout(null);
		enterField.setEditable(false);
		getContentPane().add(enterField);
		
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(displayArea);
		scrollPane.setBounds(0, 40, 284, 392);
		getContentPane().add(scrollPane);
		
		repaint();
		
	}
	
	private void runClient()
	{
		try   // set up server to receive connection
		{
			connectToServer();
			getStream();
			processConnection();			
		} //  try
		catch(EOFException eofException)
		{
			displayMessage("\nClient terminated connection");
		}  
		catch(IOException ioException)
		{
			System.out.println("TEST-io");
			ioException.printStackTrace();
		} // end catch
		finally
		{
			closeConnection();
		}
	} // end method runClient()
	
	
	private void connectToServer() throws IOException
	{
		
		displayMessage("Attemting connection ...\n");
		
		client = new Socket(InetAddress.getByName(chatServer), 12346);
		
		displayMessage("Connected to: " + client.getInetAddress().getHostName());
		
	} // end method connectToServer()
	
	private void getStream() throws IOException
	{
		
		System.out.println("TEST");
		output = new ObjectOutputStream(client.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(client.getInputStream());
		System.out.println("TEST");
		displayMessage("\nGot I/O stream\n");
		System.out.println("TEST");
	} // end method getStream()
	
	
	private void processConnection() throws IOException
	{
				
		setTextFieldEdiable(true);
		
		do
		{
			try
			{
				message = (String) input.readObject();
				displayMessage("\n" + message);
			}
			catch (ClassNotFoundException classNotFoundException)
			{
				displayMessage("\nUnknown object type received");
			}
		} while(!message.equals("SERVER>>> TERMINATE"));
	} // end method processConnection()
	
	
	private void closeConnection() 
	{
		displayMessage("\nTerminating Connection ...\n");
		setTextFieldEdiable(false);
		
		try
		{
			output.close();
			input.close();
			client.close();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		
	} // end method closeConnection()
	
	private void sendData(String message)
	{
		try
		{
			output.writeObject("CLIENT>>> " + message);
			output.flush();
			displayMessage("\nCLIENT>>> " + message);
		}
		catch(IOException ioException)
		{
			displayArea.append("\nError writing object");
		}
	} // end method sendData()
	
	private void displayMessage(final String messageToDisplay)
	{
		SwingUtilities.invokeLater(
				
				new Runnable()
				{
					public void run()
					{
						displayArea.append(messageToDisplay);
					}
				}
				
		);
	} // end method displayMessage(final String messageToDisplay)
	
	private void setTextFieldEdiable(final boolean editable)
	{
		SwingUtilities.invokeLater(
		
				new Runnable()
				{
					public void run()
					{
						enterField.setEditable(editable);
					}
				}
				
		);
	}
	
	public static void main(String[] args)
	{
		Client appliClient;
		
		if(args.length == 0)
		{
			//appliClient = new Client("127.0.0.1");
			appliClient = new Client("192.168.0.101");
		}
		else
		{
			appliClient = new Client(args[0]);
		}
		
		appliClient.runClient();
	}
	

}
