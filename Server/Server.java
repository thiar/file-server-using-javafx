/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Server {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        
        ArrayList<handler> alhandler;
        new File("cache").mkdir();
        ServerSocket sServer = null;
        try {
            alhandler =new ArrayList<>();
            sServer = new ServerSocket(9000);
            while(true){
                synchronized(alhandler)
                {
                    handler client;
                    client = new handler(sServer.accept(), alhandler);
                    alhandler.add(client);
                    Thread t = new Thread(client);
                    t.start();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true){
        try {
            
            Socket cSocket = sServer.accept();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        //req.close();
    }
    
}
