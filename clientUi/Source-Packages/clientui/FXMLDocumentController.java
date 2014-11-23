/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientui;

import cmd.cmd;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private final ListView<String> listFile;
    @FXML
    private TableView<cmd> tableUser;
    private TableColumn<cmd,String> name;
    private TableColumn<cmd,String> ip;
    private TableColumn<cmd,String> login;
    @FXML
    private TableView<cmd> tableFile;
    private TableColumn<cmd,String> nameFile;
    private TableColumn<cmd,String> size;
    private TableColumn<cmd,String> lastModified;
    
    @FXML
    private ProgressBar pBar;
    @FXML
    private ProgressBar pBarF;
    @FXML
    private Button connect;
    @FXML
    private ComboBox speedSelected;
    private ObservableList<cmd> alluser;
    private ObservableList<cmd> allfile;
    private ObservableList<String> files;
   
    private socketio sockclient;
    private File file;
    cmd command;
    cmd response;
    
    
    public FXMLDocumentController() {
      
        
        this.listFile = new ListView<>();
        tableUser =new TableView<>();
        tableFile=new TableView<>();
        command = new cmd();
        
         
    }
    
    @FXML
    public void send() 
    {
        try {
            sendStatus.setText("Mengirim file...");
            sendStatus.setVisible(true);
            sendSukses.setVisible(false);
            
            if(file==null)
            {
                System.out.println("file kosong\n");
                sendStatus.setText("File belum diinputkan");
            }
            
            else if(tableUser.getSelectionModel().isEmpty())
            {
                System.out.println("tujuan kosong\n");
                sendStatus.setText("Pilih tujuan pengiriman");
            }
            else
            {
                command.setCommand("SEND");
                command.setArgument(tableUser.getSelectionModel().getSelectedItem().getNamaUser());
                String tujuan =command.getArgument();
                System.out.println(tujuan);
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
    public void sendBroadcast() 
    {
        try {
            sendStatus.setText("Mengirim file...");
            sendStatus.setVisible(true);
            sendSukses.setVisible(false);
            
            if(file==null)
            {
                System.out.println("file kosong\n");
                sendStatus.setText("File belum diinputkan");
            }
            else
            {
                command.setCommand("SEND");
                command.setArgument("BROADCAST");
                String tujuan ="BROADCAST";
                
                command.setNameFile(file.getName());
                this.sockclient.sendobject(command);
                response =(cmd) sockclient.readobject();
                if(response.getCommand().equals("SEND") && response.getArgument().equals("OK"))
                {
                    
                    command.setCommand("SEND");
                    command.setArgument("START");
                    command.setNameFile(file.getName());
                    this.sendSukses.setText("File terkirim ke semua yang sedang aktif" );
                    this.sendSukses.setVisible(false);
                    this.sockclient.sendobject(command);
                    threadable sendbffile = new threadable(sockclient, file, command,pBar,tujuan,sendStatus,sendSukses,"SEND");
                    Thread sendf=new Thread(sendbffile);
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
        downloadSukses.setVisible(false);
        if(tableFile.getSelectionModel().isEmpty())
        {
            downloadStatus.setText("Pilih file yang akan didownload");
        }
        else
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Resource File");
            System.out.println("ok");
            //fileChooser.setInitialFileName(this.listFile.getSelectionModel().getSelectedItem());
            file = fileChooser.showSaveDialog(null);
            System.out.println(file.getAbsolutePath());

        }
        if(!tableFile.getSelectionModel().isEmpty())
        {
            cmd command=new cmd();
            command.setCommand("DOWNLOAD");
            command.setArgument("FILE");
            command.setNameFile(tableFile.getSelectionModel().getSelectedItem().getNameFile());
            
            try {
                this.sockclient.sendobject(command);
                response =(cmd) sockclient.readobject();
                downloadStatus.setText("");
                if(response.getCommand().equals("DOWNLOAD") && response.getArgument().equals("OK"))
                {
                    
                    downloadStatus.setVisible(true);
                    threadable downfile = new threadable(sockclient, file,pBarF,downloadStatus,downloadSukses,"DOWNLOAD");
                    Thread sendf=new Thread(downfile);
                    sendf.start();
                    downloadSukses.setText("Download Selesai");
                    
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
        
        
        alluser=tableUser.getItems();
        alluser.clear();
        try {
            sockclient.sendobject(command);
            response =(cmd) sockclient.readobject();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(response.getCommand().equals("LIST") && response.getArgument().equals("USER OK"))
        {
            ArrayList<cmd> list;
            try {
                list = sockclient.getListUser();
                                
                alluser.addAll(list);
                System.out.println(list.get(0).getLoginDate());
                tableUser.setItems(alluser);              
                tableUser.getColumns().setAll(name,ip,login);
                
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

        allfile=tableFile.getItems();
        allfile.clear();
        try {
            sockclient.sendobject(command);
            response =(cmd) sockclient.readobject();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(response.getCommand().equals("LIST") && response.getArgument().equals("FILE OK"))
        {
             
              ArrayList<cmd> list;
            try {
                list = sockclient.getListFile();
                allfile.addAll(list);
                
                tableFile.setItems(allfile);              
                tableFile.getColumns().setAll(nameFile,size,lastModified);
                
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
        this.connect.setDisable(false);
        this.status.setText("Koneksi Terputus");
    }
    @FXML
    public void FXMLconnect() throws ClassNotFoundException
    {
        int speedchoice=1024;
        String speed =speedSelected.getSelectionModel().getSelectedItem().toString();
        if(speed.equals("Lambat"))
        {
            speedchoice=1024;
        }
        else if(speed.equals("Normal"))
        {
            speedchoice=2048;
        }
        else if(speed.equals("Cepat"))
        {
            speedchoice=5012;
        }
        else if(speed.equals("Super Cepat"))
        {
            speedchoice=10240;
        }
        
        ipServer.setDisable(true);
        portServer.setDisable(true);
        speedSelected.setDisable(true);
        nameUser.setDisable(true);
        connect.setDisable(true);
        
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
        name=new TableColumn<>("Nama");
        ip=new TableColumn<>("IP");
        login=new TableColumn<>("Login time");
        name.setCellValueFactory(new PropertyValueFactory("namaUser"));
        ip.setCellValueFactory(new PropertyValueFactory("ip"));
        login.setCellValueFactory(new PropertyValueFactory("loginDate"));
        tableUser.setPlaceholder(new ProgressBar());
       
        tableUser.getColumns().setAll(name,ip,login);
        
        nameFile=new TableColumn<>("Name File");
        size=new TableColumn<>("Size");
        lastModified=new TableColumn<>("Last Modified");
        nameFile.setCellValueFactory(new PropertyValueFactory("nameFile"));
        size.setCellValueFactory(new PropertyValueFactory("fileSize"));
        lastModified.setCellValueFactory(new PropertyValueFactory("lastModified"));
        tableFile.setPlaceholder(new ProgressBar());
       
        tableFile.getColumns().setAll(nameFile,size,lastModified);
        
        
    }    

    
    
}
