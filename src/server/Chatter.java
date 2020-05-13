/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.ChatClient3IF;

/**
 *
 * @author Azkar
 */
public class Chatter {

	private String name;
        private String pwd;
	private ChatClient3IF client;
	
	//constructor
	public Chatter(String name, String pwd, ChatClient3IF client){
		this.name = name;
                this.pwd = pwd;
		this.client = client;
        }
	
	//getters and setters
	public String getName(){
		return name;
	}
	public ChatClient3IF getClient(){
		return client;
	}
        
        public void setClient(ChatClient3IF client){
            this.client = client;
        }
	
	
}
