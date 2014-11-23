/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientui;

import cmd.cmd;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


/**
 *
 * @author Administrator
 */
public class socketio {
    private ObjectInputStream objectinput;
    private ObjectOutputStream objectoutput;

    private Socket socketcli;
    private int speed =1024;
    
    public socketio(String Ip,int port,int speed) throws IOException
    {
        
            this.socketcli= new Socket(Ip,port);            
            this.objectoutput=new ObjectOutputStream(this.socketcli.getOutputStream());
            this.objectinput=new ObjectInputStream(this.socketcli.getInputStream());
            this.speed=speed;
            if(speed>this.socketcli.getSendBufferSize())this.socketcli.setSendBufferSize(speed);
            if(speed>this.socketcli.getReceiveBufferSize())this.socketcli.setReceiveBufferSize(speed);
    }
    
    public ArrayList<cmd> getListUser() throws IOException, ClassNotFoundException
    {
        ArrayList<cmd> allUser;
        allUser=(ArrayList<cmd>) this.readobject();
        System.out.println(allUser.get(0).getIp());
        return allUser;
    }
    public ArrayList<cmd> getListFile() throws IOException, ClassNotFoundException
    {
        ArrayList<cmd> allFile;
        allFile=(ArrayList<cmd>) this.readobject();
        System.out.println(allFile.get(0).getIp());
        return allFile;
    }
    public void readfile(File inputFile) throws IOException, ClassNotFoundException
    {
        cmd request= (cmd) this.readobject();
        FileOutputStream fos = new FileOutputStream(inputFile);
        if(request.getCommand().equals("SEND") && request.getArgument().equals("FAILED"))
        {
            System.out.println("SEND CANCELED");
        }
        else
        {
            cmd file =new cmd();
            if(request.getCommand().equals("SEND") && request.getArgument().equals("START"))
            {
                long start =System.currentTimeMillis();
                int i=0;
                while((file=(cmd) this.objectinput.readObject())!=null)
                {
                    if(file.getCommand().equals("SEND") && file.getArgument().equals("FINISH"))
                    {
                        System.out.println("File tersimpan");
                        break;
                    }
                    
                    fos.write(file.getBytefile());
                    i++;
                    //System.out.println(file.getBytefile());
                }
                System.out.println(System.currentTimeMillis()- start + " "  +i);
                
            }
            
        }
        
        System.out.println("file has writen\n");

        fos.close();
    }
    public void sendFile(File inputFile,cmd command) throws IOException,OutOfMemoryError{
        FileInputStream FileInput;
        cmd file=new cmd();
        file.setNameFile(inputFile.getName());
        file.setArgument(command.getArgument());
        file.setCommand(command.getCommand());
        file.setFile(inputFile);
        
        byte[] bytefile= new byte[this.speed];
        int byteread;
        FileInputStream fis =new FileInputStream(inputFile);
        
        while((byteread = fis.read(bytefile)) >=0)
        {
            file.setBytefile(bytefile);
            this.sendobject(file);
            //System.out.println(bytefile);
            
        }
        file.setCommand("SEND");
        file.setArgument("FINISH");
        this.sendobject(file);
        System.out.println("file terkirim");

        
    }
    public void sendobject(Object objekKirim) throws IOException
    {        
        this.objectoutput.writeObject(objekKirim);
        this.objectoutput.flush();
        this.objectoutput.reset();       
    }
    public void disconnect() throws IOException
    {
        this.objectinput.close();
        this.objectoutput.close();
        this.socketcli.close();
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
