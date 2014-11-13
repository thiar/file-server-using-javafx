/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientui;

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
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Label status;
    @FXML
    private final ListView<String> listUser;
    private ObservableList<String> names;
    private Socket sockcli;
    private ObjectInputStream objectinput;
    private socketio sockclient;
    
    public FXMLDocumentController() {
      
        this.listUser = new ListView<>();
       
    }

    /**
     *
     */
    @FXML
    public void FXMLconnect()
    {
        List<String> list;
        list =new ArrayList<>();
        System.out.println("aplikasi berjaan\n");
        try {
            this.sockclient = new socketio(ipServer.getText(),Integer.parseInt(portServer.getText()));
            System.out.println("socket created\n");            
            list = (List<String>) sockclient.getList();
            status.setText("Terkoneksi");
            //sockcli.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            status.setText("Koneksi Gagal");
        }
        this.names=listUser.getItems();
        names.addAll(list);
    }
    @Override
    @SuppressWarnings("empty-statement")
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
    }    
    
}
