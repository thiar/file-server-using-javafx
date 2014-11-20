/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import cmd.cmd;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class handler implements Runnable{

    private Socket sockcli;
    private ObjectOutputStream objectoutput;
    private ObjectInputStream objectinput;
 
    private String addr;
    private final ArrayList<handler> alhandler;
    private ArrayList<String> aluser;
    
    public handler(Socket cli, ArrayList<handler> alhandler) throws IOException
    {
        this.alhandler=alhandler;
        this.sockcli = cli;
        this.objectoutput=new ObjectOutputStream(cli.getOutputStream());
        this.objectinput =new ObjectInputStream(cli.getInputStream());
        
        aluser =new ArrayList<>();
        
     
    }
    
    public synchronized void ListUser() throws IOException
    {
        this.aluser.clear();
        for(int i=0;i<this.alhandler.size();i++)
        {
            handler client =this.alhandler.get(i);
            System.out.println(client.addr);
            aluser.add(client.addr);
        }
        
        this.sendobject(aluser);
        
    }
    public void ListFile() throws IOException
    {
        File[] files = new File("cache\\" + this.addr).listFiles();
        List<String> results = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        this.sendobject(results);
    }
    public void sendobject(Object objeckKirim) throws IOException
    {
        this.objectoutput.writeObject(objeckKirim);
        this.objectoutput.flush();
        this.objectoutput.reset();
        
    }
    
    public void readfile(cmd request) throws IOException
    {
        
        FileOutputStream fos = new FileOutputStream("cache" + "\\" + request.getArgument() + "\\" + request.getNameFile());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        bos.write(request.getBytefile());
        System.out.println("file has writen\n");

        bos.close();
    }
    public void createDefFile(cmd request) throws FileNotFoundException, IOException
    {
        
        FileOutputStream fos = new FileOutputStream("cache" + "\\" + request.getArgument() + "\\" + "ini tempat menyimpan file.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        bos.write("ini folder penyimpanan file anda".getBytes());
        System.out.println("default file has writen\n");

        bos.close();
    }
    
     public void sendFile(File inputFile,cmd request) throws IOException{
        FileInputStream FileInput;
        cmd file=new cmd();
        file.setNameFile(inputFile.getName());
        file.setArgument(request.getArgument());
        file.setFile(inputFile);
        byte[] bytefile= new byte[(int) inputFile.length()];
        
        try {
            FileInput =new FileInputStream(inputFile);
            BufferedInputStream is= new BufferedInputStream(FileInput);
            is.read(bytefile,0,bytefile.length);
            file.setBytefile(bytefile);
            System.out.println(bytefile);
            this.sendobject(file);
            System.out.println("file has been send");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(handler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    @Override
    public void run() {
        
        cmd request;
        cmd command;
        System.out.println("connected\n");
        try {
            while((request=(cmd) this.objectinput.readObject())!=null)
            {

                System.out.println(request.getCommand() + " " +  request.getArgument());
                //this.objectinput.reset();
                if(request.getCommand().equals("LIST") && request.getArgument().equals("USER"))
                {
                    command = new cmd();
                    command.setCommand("LIST");
                    command.setArgument("USER OK");
                    this.sendobject(command);
                    this.ListUser();
                }
                else if(request.getCommand().equals("SEND"))
                {
                    command = new cmd();
                    command.setCommand("SEND");
                    command.setArgument("OK");
                    new File("cache" + "\\" + request.getArgument()).mkdir();
                    this.createDefFile(request);
                    this.sendobject(command);

                    cmd file=(cmd) this.objectinput.readObject();
                    if(file.getCommand().equals("SEND") && file.getArgument().equals("FAILED"))
                    {
                        System.out.println("SEND CANCELED");
                    }
                    else
                    {
                        System.out.println(file.getNameFile());
                        System.out.println(file.getBytefile().length);
                        this.readfile(file);
                    }
                    

                }
                else if(request.getCommand().equals("LOGIN"))
                {
                    command = new cmd();
                    command.setCommand("LOGIN");
                    command.setArgument("OK");
                    new File("cache" + "\\" + request.getArgument()).mkdir();
                    this.createDefFile(request);
                    this.sendobject(command);
                    this.addr=request.getArgument();
                }
                else if(request.getCommand().equals("LIST") && request.getArgument().equals("FILE"))
                {
                    command = new cmd();
                    command.setCommand("LIST");
                    command.setArgument("FILE OK");
                    this.sendobject(command);
                    this.ListFile();
                }
                else if(request.getCommand().equals("DOWNLOAD") && request.getArgument().equals("FILE"))
                {
                    command = new cmd();
                    command.setCommand("DOWNLOAD");
                    command.setArgument("OK");
                    this.sendobject(command);
                    File file =new File("cache\\" + this.addr+ "\\" + request.getNameFile());
                    System.out.println(file.getAbsoluteFile());
                    this.sendFile(file, request);
                }

            }

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("connection lost\n");
        this.alhandler.remove(this);
        System.out.println("remove thread\n");

        try {            
            this.sockcli.close();
            this.objectoutput.close();
            this.objectinput.close();
        } catch (IOException ex) {
            Logger.getLogger(handler.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
        System.out.println("end of connection\n");          
       
    }
}
