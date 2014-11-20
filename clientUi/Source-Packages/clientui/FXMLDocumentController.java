/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientui;

import cmd.cmd;
import java.awt.Dialog;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import static java.util.Collections.list;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;

/**
 *
 * @author Administrator
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField ipServer;
    @FXML
    private TextField portServer;
    @FXML
    private TextField nameUser;
    @FXML
    private Label status;
    @FXML
    Label sendSukses;
    @FXML
    Label downloadSukses;
    @FXML
    private Label downloadStatus;
    @FXML
    private Label sendStatus;
    @FXML
    private final ListView<String> listUser;
    @FXML
    private final ListView<String> listFile;
    @FXML
    private ProgressBar pBar;
    @FXML
    private ProgressBar pBarF;
    @FXML
    private ComboBox speedSelected;
    private ObservableList<String> names;
    private ObservableList<String> files;
    private ObjectInputStream objectinput;
    private socketio sockclient;
    private File file;
    cmd command;
    cmd response;
    
    public FXMLDocumentController() {
      
        this.listUser = new ListView<>();
        this.listFile = new ListView<>();
        command = new cmd();
        threadable music =new threadable("MUSIC");
        Thread playmusic=new Thread(music);
        playmusic.start();
        speedSelected =new ComboBox();
        
        
       
    }
    
    @FXML
    public void send() 
    {
        try {
            sendStatus.setText("Mengirim file...");
            sendStatus.setVisible(true);
            if(file==null)
            {
                System.out.println("file kosong\n");
                sendStatus.setText("File belum diinputkan");
            }
            
            else if(listUser.getSelectionModel().getSelectedItem()==null)
            {
                System.out.println("tujuan kosong\n");
                sendStatus.setText("Pilih tujuan pengiriman");
            }
            else
            {
                command.setCommand("SEND");
                command.setArgument(listUser.getSelectionModel().getSelectedItem());
                String tujuan =command.getArgument();
                command.setNameFile(file.getName());
                this.sockclient.sendobject(command);
                response =(cmd) sockclient.readobject();
                if(response.getCommand().equals("SEND") && response.getArgument().equals("OK"))
                {
                    
                    command.setCommand("SEND");
                    command.setArgument("START");
                    command.setNameFile(file.getName());
                    this.sendSukses.setText("File terkirim ke " + tujuan);
                    this.sendSukses.setVisible(false);
                    this.sockclient.sendobject(command);
                    threadable sendfile = new threadable(sockclient, file, command,pBar,tujuan,sendStatus,sendSukses,"SEND");
                    Thread sendf=new Thread(sendfile);
                    sendf.start();
                    pBar.setVisible(true);
                    
                                        
                }
                
            }
            
        } catch (IOException |ClassNotFoundException|OutOfMemoryError ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            command.setCommand("SEND");
            command.setArgument("FAILED");
            try {
                this.sockclient.sendobject(command);
            } catch (IOException ex1) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex1);
            }
            sendStatus.setText("File terlalu besar, tidak boleh melebihi 500 MB");
            System.out.println("file gagal");
        }
    }
    @FXML
    public void download()
    {
        if(this.listFile.getSelectionModel().getSelectedItem()==null)
        {
            downloadStatus.setText("Pilih file yang akan didownload");
        }
        else
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Resource File");
            System.out.println("ok");
            fileChooser.setInitialFileName(this.listFile.getSelectionModel().getSelectedItem());
            file = fileChooser.showSaveDialog(null);
            System.out.println(file.getAbsolutePath());

        }
        if(this.listFile.getSelectionModel().getSelectedItem()!=null)
        {
            cmd command=new cmd();
            command.setCommand("DOWNLOAD");
            command.setArgument("FILE");
            command.setNameFile(this.listFile.getSelectionModel().getSelectedItem());
            
            try {
                this.sockclient.sendobject(command);
                response =(cmd) sockclient.readobject();
                downloadStatus.setText("");
                if(response.getCommand().equals("DOWNLOAD") && response.getArgument().equals("OK"))
                {
                    downloadStatus.setText("Download Sedang diproses....");
                    downloadStatus.setVisible(true);
                    threadable downfile = new threadable(sockclient, file,pBarF,downloadStatus,downloadSukses,"DOWNLOAD");
                    Thread sendf=new Thread(downfile);
                    sendf.start();
                    downloadSukses.setText("Download Selesai");
                    downloadSukses.setVisible(false);
                    pBarF.setVisible(true);
                }
            } catch (ClassNotFoundException |IOException ex ) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                downloadStatus.setText("Download gagal");
                
            } 
        }
        
    }
    @FXML
    public void browse()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        file = fileChooser.showOpenDialog(null);
        System.out.println(file.getAbsolutePath());
        sendStatus.setText("File " + file.getName() +" siap dikirim");
        
        
    }
    
    @FXML
    public void refresh() throws ClassNotFoundException
    {
        command.setCommand("LIST");
        command.setArgument("USER");
        this.names =listUser.getItems();
        names.clear();
        try {
            sockclient.sendobject(command);
            response =(cmd) sockclient.readobject();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(response.getCommand().equals("LIST") && response.getArgument().equals("USER OK"))
        {
             List<String> list;
             list =new ArrayList<>();
            try {
                list = sockclient.getList();
                
                names.addAll(list);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    @FXML
    public void refreshFile() throws ClassNotFoundException
    {
        command.setCommand("LIST");
        command.setArgument("FILE");
        this.files =listFile.getItems();
        files.clear();
        try {
            sockclient.sendobject(command);
            response =(cmd) sockclient.readobject();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(response.getCommand().equals("LIST") && response.getArgument().equals("FILE OK"))
        {
             List<String> list;
             list =new ArrayList<>();
            try {
                list = sockclient.getList();
                
                files.addAll(list);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    @FXML
    public void FXMLdisconnect() throws IOException
    {
        this.sockclient.disconnect();
        this.ipServer.setDisable(false);
        this.portServer.setDisable(false);
        this.nameUser.setDisable(false);
        this.speedSelected.setDisable(false);
        this.status.setText("Koneksi Terputus");
    }
    @FXML
    public void FXMLconnect() throws ClassNotFoundException
    {
        int speedchoice=1024;
        String speed =speedSelected.getSelectionModel().getSelectedItem().toString();
        if(speed.equals("normal"))
        {
            speedchoice=1024;
        }
        else if(speed.equals("2x"))
        {
            speedchoice=2048;
        }
        else if(speed.equals("10x"))
        {
            speedchoice=10240;
        }
        else if(speed.equals("100x"))
        {
            speedchoice=102400;
        }
        
        ipServer.setDisable(true);
        portServer.setDisable(true);
        speedSelected.setDisable(true);
        nameUser.setDisable(true);
        List<String> list;
        String nama ="anonymous";
        cmd command =new cmd();
        if(!this.nameUser.getText().isEmpty())nama=this.nameUser.getText();
        
        list =new ArrayList<>();
        System.out.println("aplikasi berjaan\n");
       
        try {
            this.sockclient = new socketio(ipServer.getText(),Integer.parseInt(portServer.getText()),speedchoice);
            System.out.println("socket created\n");
            command.setCommand("LOGIN");
            command.setArgument(nama);
            command.setSpeed(speedchoice);
            this.sockclient.sendobject(command);
            cmd response=(cmd) this.sockclient.readobject();
            if(response.getCommand().equals("LOGIN") && response.getArgument().equals("OK"))
            {
                status.setText("Terkoneksi");
            }
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            status.setText("Koneksi Gagal");
            
        }      
    }
    @Override
    @SuppressWarnings("empty-statement")
    public void initialize(URL url, ResourceBundle rb) {
        // TODO      
        
    }    
    
}
