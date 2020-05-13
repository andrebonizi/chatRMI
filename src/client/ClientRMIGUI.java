
package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

                import java.io.RandomAccessFile;

/**
 *
 * @author Azkar and André Bonizi
 */
public class ClientRMIGUI extends JFrame implements ActionListener{
	
    private static final long serialVersionUID = 1L;	
    private JPanel textPanel, inputPanel;
    private JTextField textField;
    private String name, message;
    private Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);
    private Border blankBorder = BorderFactory.createEmptyBorder(30,30,30,30);//top,r,b,l
    private ChatClient3 chatClient;
        
    private JList<String> list;
    private DefaultListModel<String> listModel;
    
    protected JTextArea textArea, userArea;
    protected JFrame frame;
    protected JButton privateMsgButton, startButton, sendButton, fileButton;
    protected JPanel clientPanel, userPanel;

	/**
	 * Main method to start client GUI app.
	 * @param args
	 */
	public static void main(String args[]){
		//set the look and feel to 'Nimbus'
		try{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
				if("Nimbus".equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch(Exception e){
                    }
		}
	
	
	/**
	 * GUI Constructor
	 */
	public ClientRMIGUI(String userName, ChatClient3 chatClient) throws RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException{
                this.chatClient = chatClient;
		frame = new JFrame("UTF CHAT");	
                
                name = userName;
		//-----------------------------------------
		/*
		 * intercept close method, inform server we are leaving
		 * then let the system exit.
		 */
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        
		    	if(chatClient != null){
                            try {
                                sendMessage("Tchau! Estou saindo...");
                                chatClient.serverIF.leaveChat(name);
                                }
                            catch (RemoteException e) {
                                e.printStackTrace();
                            }		        	
		        }
		        System.exit(0);  
		    }   
		});
		//-----------------------------------------
		//remove window buttons and border frame
		//to force user to exit on a button
		//- one way to control the exit behaviour
	
		Container c = getContentPane();
		JPanel outerPanel = new JPanel(new BorderLayout());
		
		outerPanel.add(getInputPanel(), BorderLayout.CENTER);
		outerPanel.add(getTextPanel(), BorderLayout.NORTH);
		
		c.setLayout(new BorderLayout());
		c.add(outerPanel, BorderLayout.CENTER);
		c.add(getUsersPanel(), BorderLayout.WEST);

		frame.add(c);
		frame.pack();
		//frame.setAlwaysOnTop(true);
		frame.setLocation(100, 150);
		textField.requestFocus();
	
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
                
                
	}
	
	
	/**
	 * Method to set up the JPanel to display the chat text
	 * @return
	 */
	public JPanel getTextPanel(){
		//String welcome = "Digite seu nome e clique em 'Entrar!'.\n";
		textArea = new JTextArea(null, 14, 34);
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setFont(meiryoFont);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textPanel = new JPanel();
		textPanel.add(scrollPane);
	
		textPanel.setFont(new Font("Meiryo", Font.PLAIN, 14));
		return textPanel;
	}
	
	/**
	 * Method to build the panel with input field
	 * @return inputPanel
	 */
	public JPanel getInputPanel(){
		inputPanel = new JPanel(new GridLayout(1, 1, 5, 1));
		inputPanel.setBorder(blankBorder);
              
		textField = new JTextField();
		textField.setFont(meiryoFont);
		inputPanel.add(textField);
		return inputPanel;
	}

	/**
	 * Method to build the panel displaying currently connected users
	 * with a call to the button panel building method
	 * @return
	 */
	public JPanel getUsersPanel(){
		
		userPanel = new JPanel(new BorderLayout());
                String  userStr = " Quem está logado?      ";
		JLabel userLabel = new JLabel(userStr, JLabel.CENTER);

		userPanel.add(userLabel, BorderLayout.NORTH);	
		userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));

		String[] noClientsYet = {"Ainda não tem ninguém..."};
		setClientPanel(noClientsYet);

		clientPanel.setFont(meiryoFont);
		userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);		
		userPanel.setBorder(blankBorder);

		return userPanel;		
	}

	/**
	 * Populate current user panel with a 
	 * selectable list of currently connected users
	 * @param currClients
	 */
    public void setClientPanel(String[] currClients) {  	
    	clientPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<String>();
        
        for(String s : currClients){
                if(!s.equals(name))
                    listModel.addElement(s);
        }
        if(currClients.length > 1){
        	privateMsgButton.setEnabled(true);
        }
        
        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }
	
	/**
	 * Make the buttons and add the listener
	 * @return
	 */
	public JPanel makeButtonPanel() {		
		sendButton = new JButton("Enviar no Grupo");
		sendButton.addActionListener(this);
		sendButton.setEnabled(true);
                fileButton = new JButton("Enviar Arquivo");
                fileButton.addActionListener(this);
		fileButton.setEnabled(true);
                
                privateMsgButton = new JButton("Enviar no Privado");
                privateMsgButton.addActionListener(this);
                privateMsgButton.setEnabled(true);
		
		JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
		
                buttonPanel.add(fileButton);
                buttonPanel.add(new JLabel(""));
                buttonPanel.add(privateMsgButton);
		buttonPanel.add(sendButton);
		
		return buttonPanel;
	}
	public void chooseFile() throws IOException{
            final JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(this);
            try {
                // Open an input stream
                //Scanner reader = new Scanner(fc.getSelectedFile());
                File file = fc.getSelectedFile();
                String filename = file.getName();
                RandomAccessFile f = new RandomAccessFile(file, "r");
                byte[] b = new byte[(int)f.length()];
                f.readFully(b);
                sendFile(b, filename);
            }catch(RemoteException re){
                System.out.println("Problema de Conexão");
            }catch(FileNotFoundException nf){
                System.out.println(nf);
            }
        }
        /**
	 * Action handling on the buttons
	 */
	@Override
	public void actionPerformed(ActionEvent e){
		try {
                    if(e.getSource() == sendButton){
                        message = textField.getText();
                        textField.setText("");
                        sendMessage(message);
                        System.out.println("Enviando Mensagem : " + message);
                    }

                    if(e.getSource() == fileButton){
                        
                        System.out.println("Enviando arquivo");
                        chooseFile();

                        
                    }
                    //send a private message, to selected users
                    if(e.getSource() == privateMsgButton){
                        List<String> privateList = list.getSelectedValuesList();

                        for(int i=0; i<privateList.size(); i++){
                                System.out.println("selected index :" + privateList.get(i));
                        }
                        message = textField.getText();
                        textField.setText("");
                        sendPrivate(privateList);
                    }
		}
		catch (RemoteException remoteExc) {			
			remoteExc.printStackTrace();	
		} catch (IOException ex) {
            Logger.getLogger(ClientRMIGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}//end actionPerformed

	// --------------------------------------------------------------------
	
	/**
	 * Send a message, to be relayed to all chatters
	 * @param chatMessage
	 * @throws RemoteException
	 */
	private void sendMessage(String chatMessage) throws RemoteException {
		chatClient.serverIF.updateChat(name, chatMessage);
	}
        
        private void sendFile(byte[] file, String filename) throws RemoteException {
		chatClient.serverIF.SendFile(file, filename);
	}
	/**
	 * Send a message, to be relayed, only to selected chatters
	 * @param chatMessage
	 * @throws RemoteException
	 */
	private void sendPrivate(List<String> privateList) throws RemoteException {
		String privateMessage = "[DM " + name + "] :" + message + "\n";
		chatClient.serverIF.sendPM(privateList, privateMessage);
	}
	/**
	 * Make the connection to the chat server
	 * @param userName
	 * @throws RemoteException
	 */
	//private void getConnected(String userName, String password) throws RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException{
//		//remove whitespace and non word characters to avoid malformed url
//		String cleanedUserName = userName.replaceAll("\\s+","_");
//		cleanedUserName = userName.replaceAll("\\W+","_");	
  //              chatClient = new ChatClient3(this, cleanedUserName, password);
    //            try {
      //              chatClient.startClient();
        //        }
         //       catch (RemoteException e){
         //           throw e;
        //        }
	//}
        
        public Boolean callAdm(String groupName, String chatterName){
            //Printar na GUI que o userName está requisitando acesso ao groupName
            //Editar response para enviar a resposta de volta ao servidor
            Boolean response = false;
            return response;
        }

}
