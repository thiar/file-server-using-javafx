/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class socketio {
    private ObjectInputStream objectinput;
    private ObjectOutputStream objectoutput;
    private Socket socketcli;
    
    public socketio(String Ip,int port)
    {
        try {
            this.socketcli= new Socket(Ip,port);
            this.objectinput=new ObjectInputStream(this.socketcli.getInputStream());
            this.objectoutput=new ObjectOutputStream(this.socketcli.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(socketio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object getList() throws IOException, ClassNotFoundException
    {
        return this.objectinput.readObject();
    }
    
}
