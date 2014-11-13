/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SONY VAIO
 */
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ServerSocket MyService = null;
        Socket clientsocket = null;
        try {
            MyService = new ServerSocket(5000);
            System.out.println("server socket dibuat");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            clientsocket = MyService.accept();
            System.out.println("acept");
        //    BufferedOutputStream my = new BufferedOutputStream(clientsocket.getOutputStream());
          //  my.write("tes".getBytes());
          //  my.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ArrayList<String> my = new ArrayList<String>();
        my.add("Wahyu");
        my.add("Thiar");
        ObjectOutputStream objectOutput = null;
        try {
            objectOutput = new ObjectOutputStream(clientsocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            objectOutput.writeObject(my);
            objectOutput.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        try {
            clientsocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}


/*
            DataInputStream input;
            try {
            input = new DataInputStream(clientsocket.getInputStream());
            } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }*/


    /*
A
    */
