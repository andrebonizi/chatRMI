/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.util.Arrays;

import javax.swing.JOptionPane;

import server.ChatServerIF;
/**
 *
 * @author Azkar
 */
public class ChatClient3  extends UnicastRemoteObject implements ChatClient3IF {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7468891722773409712L;
	public ClientRMIGUI chatGUI;
	private String hostName = "localhost";
	private final String serviceName = "GroupChatService";
	private String clientServiceName;
	private String name, pwd;
	protected ChatServerIF serverIF;
	protected boolean connectionProblem = false;

	
	/**
	 * class constructor,
	 * note may also use an overloaded constructor with 
	 * a port no passed in argument to super
	 * @throws RemoteException
	 */
	public ChatClient3(String userName, String password) throws RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException {
		super();
                String cleanedUserName = userName.replaceAll("\\W+","_");
		this.name = cleanedUserName;
                this.pwd = password;
		this.clientServiceName = "ClientListenService_" + userName;
                try{
                    startClient();
                }
                catch (RemoteException e){
                    throw e;
                }
                chatGUI = new ClientRMIGUI(name, this);
                serverIF.updateUserList();
	}
	/**
	 * Register our own listening service/interface
	 * lookup the server RMI interface, then send our details
	 * @throws RemoteException
	 */
	public void startClient() throws RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException {
                
                HashPassword();
                String[] details = {name, hostName, clientServiceName, pwd};

		try {
                    Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
                    serverIF = ( ChatServerIF )Naming.lookup("rmi://" + hostName + "/" + serviceName);	
		} 
		catch (ConnectException  e) {
                    JOptionPane.showMessageDialog(
                                    chatGUI.frame, "The server seems to be unavailable\nPlease try later",
                                    "Connection problem", JOptionPane.ERROR_MESSAGE);
                    connectionProblem = true;
                    e.printStackTrace();
		}
		catch(NotBoundException | MalformedURLException me){
			connectionProblem = true;
			me.printStackTrace();
		}
                if(!connectionProblem){
                    try {
                        registerWithServer(details);
                    }
                    catch(RemoteException e){
                            throw e;
                    }
                }	
                System.out.println("Client Listen RMI Server is running...\n");
	}


	/**
	 * pass our username, hostname and RMI service name to
	 * the server to register out interest in joining the chat
	 * @param details
	 */
	public void registerWithServer(String[] details) throws RemoteException {		
		try{
                    serverIF.passIDentity(this.ref);//now redundant ??
                    serverIF.registerListener(details);			
		}
		catch(Exception e){
                    throw e;
		}
	}

	//=====================================================================
	/**
	 * Receive a string from the chat server
	 * this is the clients RMI method, which will be used by the server 
	 * to send messages to us
	 */
	@Override
	public void messageFromServer(String message) throws RemoteException {
		System.out.println( message );
		chatGUI.textArea.append( message );
		//make the gui display the last appended text, ie scroll to bottom
		chatGUI.textArea.setCaretPosition(chatGUI.textArea.getDocument().getLength());
	}

        
        @Override
	public void DownloadFile(byte[] file, String filename) throws RemoteException{
            System.out.println(file);
            try {
                /*
		File f = new File("C://utfchat//teste.txt");
                System.out.println("Safe");
                OutputStream os = new FileOutputStream(f); 
                os.write(file); 
                System.out.println("Successfully byte inserted"); 
                os.close(); 
                */
                
                new File("C:\\utfchat").mkdir();
                FileOutputStream fos = new FileOutputStream("c:\\utfchat\\"+filename);
                fos.write(file);
                fos.close();
                
                //FileUtils.writeByteArrayToFile(new File("pathname"), file);
            }
                catch (IOException e) {
                    System.out.println("Deu merda...");
            }
            //chatGUI.textArea.append( message );
            //make the gui display the last appended text, ie scroll to bottom
            //chatGUI.textArea.setCaretPosition(chatGUI.textArea.getDocument().getLength());
	}

	/**
	 * A method to update the display of users 
	 * currently connected to the server
	 */
	@Override
	public void updateUserList(String[] currentUsers) throws RemoteException {

		if(currentUsers.length < 2){
			chatGUI.privateMsgButton.setEnabled(false);
		}
		chatGUI.userPanel.remove(chatGUI.clientPanel);
		chatGUI.setClientPanel(currentUsers);
		chatGUI.clientPanel.repaint();
		chatGUI.clientPanel.revalidate();
	}
        
        private void HashPassword() throws NoSuchAlgorithmException, UnsupportedEncodingException{
            
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            byte messageDigest[] = algorithm.digest(pwd.getBytes("ASCII"));
            
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", 0xFF & b));
            }
            
            String senhahex = hexString.toString();
            
            pwd = senhahex;
        }

        public void callAdm(String groupName, String chatterName) throws RemoteException {
            Boolean response = chatGUI.callAdm(groupName, chatterName);
            serverIF.admResponse(groupName, chatterName, response);
        }
        
        public void updateGroupList(String[] currentGroups) throws RemoteException {
            // Modificar GUI
	}

    

    
}
