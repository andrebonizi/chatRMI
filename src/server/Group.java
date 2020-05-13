/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Vector;
import client.ChatClient3IF;
import java.rmi.RemoteException;

/**
 *
 * @author olirafa
 */
public class Group {
    private String name;
    private Vector<Chatter> chatters;
    private Chatter adm;
    
    public Group (String name, Chatter adm){
        this.name = name;
        this.adm = adm;
        chatters = new Vector<Chatter>(10, 1);
        setChatter(adm);
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getAdmName(){
        return this.adm.getName();
    }
    
    public void setChatter(Chatter chatter){
        chatters.addElement(chatter);
    }
    
    public Vector<Chatter> getChatters(){
        return chatters;
    }
    
    public void removeChatter(String chatterName){
        for (Chatter c: chatters) {
            if (c.getName().equals(chatterName)){
                chatters.remove(c);
            }
        }
    }
    
    public void callAdm(String groupName, String chatterName) throws RemoteException{
        adm.getClient().callAdm(groupName, chatterName);
    }
}
