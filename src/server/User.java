/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author andre
 */
public class User {
    String name;
    String pass;
    public User(String name, String pass)throws NoSuchAlgorithmException, UnsupportedEncodingException{
        this.name = name;
        this.pass = pass;
        HashPassword();
    }
    
    private void HashPassword() throws NoSuchAlgorithmException, UnsupportedEncodingException{
            
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            byte messageDigest[] = algorithm.digest(pass.getBytes("ASCII"));
            
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", 0xFF & b));
            }
            
            String senhahex = hexString.toString();
            
            pass = senhahex;
        }
}
