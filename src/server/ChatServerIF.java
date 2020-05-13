/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Azkar
 */
public interface ChatServerIF extends Remote {
		
	public void updateChat(String userName, String chatMessage) throws RemoteException;
	
	public void passIDentity(RemoteRef ref) throws RemoteException;
	
	public void registerListener(String[] details) throws RemoteException;
	
	public void leaveChat(String userName) throws RemoteException;
	
	public void sendPM(List<String> privateGroup, String privateMessage)throws RemoteException;
        
        public void updateGroupChat(String groupName, String userName, String chatMessage) throws RemoteException;
        
        public void createGroup(String name, Chatter adm) throws RemoteException;
        
        public void removeChatter(String adm, String groupName, String chatterName) throws RemoteException;
        
        public void groupPermission(String groupName, String userName) throws RemoteException;
        
        public void admResponse(String groupName, String userName, Boolean response) throws RemoteException;

        public Vector<User> addUser(String login, String pass) throws RemoteException;

        public void updateUserList() throws RemoteException;
        
        public void SendFile(byte[] file, String filename) throws RemoteException;
        
}
