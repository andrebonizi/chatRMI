/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

import client.ChatClient3IF;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author André Bonizi, Marcos Yuri, Otavio Nascimento e Rafael Oliveira
 */
public class ChatServer extends UnicastRemoteObject implements ChatServerIF {
    
	String line = "---------------------------------------------\n";
        private Vector<Group> groups;
        private Vector<Chatter> chatters;
        private Vector<User> users;
	private static final long serialVersionUID = 1L;
	
	//Constructor
	public ChatServer() throws RemoteException {
            super();
            groups = new Vector<Group>(10, 1);
            users = new Vector<User>(10, 1);
            chatters = new Vector<Chatter>(10, 1);
	}
	
	//-----------------------------------------------------------
	/**
	 * LOCAL METHODS
	 */	
	public static void main(String[] args) {
            
		startRMIRegistry();	
                //Talvez aqui dê pra controlar os grupos, criando um hostName ou ServiceName para cada grupo
		String hostName = "localhost";
		String serviceName = "GroupChatService";
		
		if(args.length == 2){
			hostName = args[0];
			serviceName = args[1];
		}
		
		try{
			ChatServerIF hello = new ChatServer();
			Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
			System.out.println("RMI Server is running...");
		}
		catch(Exception e){
			System.out.println("Server had problems starting");
		}	
	}

	
	/**
	 * Start the RMI Registry
	 */
	public static void startRMIRegistry() {
		try{
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI Server ready");
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
	}
		
	
	//-----------------------------------------------------------
	/*
	 *   REMOTE METHODS
	 */
	
	/**
	 * Return a message to client
	 */
	public String sayHello(String ClientName) throws RemoteException {
		System.out.println(ClientName + " enviou um mensagem");
		return "Olá " + ClientName + " tudo certo por aí?";
	}
	

	/**
	 * Send a string ( the latest post, mostly ) 
	 * to all connected clients
	 */
	public void updateChat(String name, String nextPost) throws RemoteException {
		String message =  name + " : " + nextPost + "\n";
		sendToAll(message);
	}
	
	/**
	 * Receive a new client remote reference
     * @param ref
     * @throws java.rmi.RemoteException
	 */
	@Override
	public void passIDentity(RemoteRef ref) throws RemoteException {	
		//System.out.println("\n" + ref.remoteToString() + "\n");
		try{
                    System.out.println(line + ref.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}//end passIDentity

	
	/**
	 * Receive a new client and display details to the console
	 * send on to register method
	 */
	@Override
	public void registerListener(String[] details) throws RemoteException {	
		System.out.println(new Date(System.currentTimeMillis()));
                
		System.out.println(details[0] + " entrou na sessão");
		System.out.println(details[0] + " hostname : " + details[1]);
		System.out.println(details[0] + "RMI serviço : " + details[2]);
		try {
                    registerChatter(details);
                }
                catch (RemoteException e) {
                    throw e;
                } catch (MalformedURLException ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
	}

	
	/**
	 * register the clients interface and store it in a reference for 
	 * future messages to be sent to, ie other members messages of the chat session.
	 * send a test message for confirmation / test connection
	 * @param details
	 */
        //o login tem que ser feito aqui
			
	private void registerChatter(String[] details) throws RemoteException, MalformedURLException, NotBoundException {
            try{
                ChatClient3IF nextClient = ( ChatClient3IF )Naming.lookup("rmi://" + details[1] + "/" + details[2]);

                Boolean response = compareUser(details[0], details[3]);
                if (!response){
                    throw new RemoteException("[Server] : Usuário não cadastrado ou senha incorreta ");
                }
                
                Chatter c = new Chatter(details[0], details[3], null);
                
                //nextClient.messageFromServer("[Server] : Bem vindo de volta " + details[0] + " você está no Utf Chat.\n");
                //sendToAll("Você está logado em: "+details[1]);
                //sendToAll("[Server] : " + details[0] + " entrou no grupo! Alguém quer tc?.\n");                

                c.setClient(nextClient);
                
                
                if (chatters.size() > 0){
                    sendToAll("Você está logado em: "+details[1]);
                    sendToAll("[Server] : " + details[0] + " entrou no grupo! Alguém quer tc?.\n");
                    updateUserList();
                }
                chatters.addElement(c);
                
                System.out.println(details[3]);
                //nextClient.messageFromServer("[Server] : Olá " + details[0] + " você está no Utf Chat.\n");
                
                

                		
            }
            catch(RemoteException | MalformedURLException | NotBoundException e){
                    e.printStackTrace();
                    throw e;
            }
	}
	
	/**
	 * Update all clients by remotely invoking their
	 * updateUserList RMI method
	 */
	public void updateUserList() throws RemoteException {
		String[] currentUsers = getUserList();	
		for(Chatter c : chatters){
			try {
				c.getClient().updateUserList(currentUsers);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	

	/**
	 * generate a String array of current users
	 * @return
	 */
	private String[] getUserList(){
		// generate an array of current users
		String[] allUsers = new String[chatters.size()];
		for(int i = 0; i< allUsers.length; i++){
			allUsers[i] = chatters.elementAt(i).getName();
		}
		return allUsers;
	}
	

	/**
	 * Send a message to all users
	 * @param newMessage
	 */
	public void sendToAll(String newMessage){	
		for(Chatter c : chatters){
			try {
				c.getClient().messageFromServer(newMessage);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}

        public void SendFile(byte[] file, String filename){	
		for(Chatter c : chatters){
			try {
                            c.getClient().DownloadFile(file, filename);
                        	
			}catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	/**
	 * remove a client from the list, notify everyone
	 */
	@Override
	public void leaveChat(String userName) throws RemoteException{
		
		for(Chatter c : chatters){
			if(c.getName().equals(userName)){
				System.out.println(line + userName + " saiu do chat... ");
				System.out.println(new Date(System.currentTimeMillis()));
				chatters.remove(c);
				break;
			}
		}		
		if(!chatters.isEmpty()){
			updateUserList();
		}			
	}
        
        public Vector<User> addUser(String login, String pass) throws RemoteException{
            User user = null;
            try {
                user = new User(login, pass);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            users.addElement(user);
            return users;
        }
        
        public Boolean compareUser(String user, String pass) throws RemoteException{
            for (User u : users){
                if (user.equals(u.name) && pass.equals(u.pass)) return true;
            }
            return false;
        }

        
        // ------------ GROUPS ------------
        public void createGroup(String name, Chatter adm) throws RemoteException {
            Group group = new Group(name, adm);
            groups.addElement(group);
            updateGroupList();
        }
        
        //public Vector<Group> getGroups(){
        //    try {
        //        return groups;
        //    }
        //    catch {
        //        
        //    }
        //}
        
        public void sendToGroup(Group group, String newMessage) throws RemoteException {
            for(Chatter c : group.getChatters()){
                try {
                    c.getClient().messageFromServer(newMessage);
                }
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void updateGroupChat(String groupName, String name, String chatMessage) throws RemoteException {
            try {
                String message =  name + " : " + chatMessage + "\n";
                for (Group c: groups) {
                    if (c.getName().equals(groupName)){
                        sendToGroup(c, message);
                    }
                }
            }
            catch (RemoteException e) {
                    e.printStackTrace();
                }
	}
        
        public void removeChatter(String adm, String groupName, String chatterName) throws RemoteException {
            for (Group c: groups) {
                if (c.getName().equals(groupName)){
                    if (c.getAdmName().equals(adm)) c.removeChatter(chatterName);
                    // Alterar na GUI que o chatterName está fora do grupo
                }
            }
        }
        
        public void groupPermission(String groupName, String chatterName) throws RemoteException {
            for (Group c: groups) {
                if (c.getName().equals(groupName)) c.callAdm(groupName, chatterName);
            } 
        }
	
        public void admResponse(String groupName, String chatterName, Boolean response) throws RemoteException{
            if(response){
                for (Group g: groups) {
                    if (g.getName().equals(groupName)){
                        for (Chatter c: chatters){
                            if (c.getName().equals(chatterName)) g.setChatter(c);
                            // retornar para a GUI que o userName está no groupName
                        }
                    }
                }
            }
            else{
                //enviar resposta "não deu filhão"
            }
        }
        
        private void updateGroupList() {
		String[] currentGroups = getGroupList();	
		for(Chatter c : chatters){
			try {
                            c.getClient().updateUserList(currentGroups);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}
        
        private String[] getGroupList(){
		// generate an array of current users
		String[] allGroups = new String[groups.size()];
		for(int i = 0; i< allGroups.length; i++){
			allGroups[i] = groups.elementAt(i).getName();
		}
		return allGroups;
	}

	/**
	 * A method to send a private message to selected clients
	 * The integer array holds the indexes (from the chatters vector) 
	 * of the clients to send the message to
	 */
	@Override
	public void sendPM(List<String> privateGroup, String privateMessage) throws RemoteException{
		Chatter pc;
		// ESSE METODO EU ALTEREI
		for(Chatter i : chatters){
                        for(String j : privateGroup){
                            if(i.getName().equals(j)){
                                pc= i;
                                pc.getClient().messageFromServer(privateMessage);
                            }
                        }
			
		}
	}
}