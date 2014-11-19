/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientui;

import cmd.cmd;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
    
    public socketio(String Ip,int port) throws IOException
    {
        
            this.socketcli= new Socket(Ip,port);            
            this.objectoutput=new ObjectOutputStream(this.socketcli.getOutputStream());
            this.objectinput=new ObjectInputStream(this.socketcli.getInputStream());
                   
        
    }
    
    public String getName()
    {
        String name = this.socketcli.getInetAddress().toString() + "-" + this.socketcli.getPort();
        return name;
    }
    public void readfile(File inputFile) throws IOException, ClassNotFoundException
    {
        cmd file= (cmd) this.readobject();
        FileOutputStream fos = new FileOutputStream(inputFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        bos.write(file.getBytefile());
        System.out.println("file has writen\n");
        bos.close();
    }
    public void sendFile(File inputFile,cmd command) throws IOException,OutOfMemoryError{
        FileInputStream FileInput;
        cmd file=new cmd();
        file.setNameFile(inputFile.getName());
        file.setArgument(command.getArgument());
        file.setCommand(command.getCommand());
        file.setFile(inputFile);
        
        byte[] bytefile= new byte[(int) inputFile.length()];
        
        
        
        FileInput =new FileInputStream(inputFile);
        BufferedInputStream is= new BufferedInputStream(FileInput);
        is.read(bytefile,0,bytefile.length);
        file.setBytefile(bytefile);
        System.out.println(bytefile);
        this.sendobject(file);

        
    }
    public void sendobject(Object objekKirim) throws IOException
    {        
        this.objectoutput.writeObject(objekKirim);
        this.objectoutput.flush();
        this.objectoutput.reset();       
    }
    
    public Object readobject() throws IOException, ClassNotFoundException
    {
        return this.objectinput.readObject();
    }
    
    public ArrayList<String> getList() throws IOException, ClassNotFoundException
    {
        return (ArrayList<String>)this.objectinput.readObject();
    }
    
}
