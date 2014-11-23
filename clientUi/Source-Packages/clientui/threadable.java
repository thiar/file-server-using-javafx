/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientui;

import cmd.cmd;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Administrator
 */
public class threadable implements Runnable{
    
    socketio sockclient;
    File file;
    cmd command;
    ProgressBar pBar;
    String tujuan;
    Label Status;
    Label Sukses;
    String mode;
    public threadable(socketio sockclient,File file,cmd command,ProgressBar pBar,String tujuan,Label sendStatus,Label sendSukses,String mode)
    {
        this.sockclient=sockclient;
        this.file=file;
        this.command=command;
        this.pBar=pBar;
        this.tujuan=tujuan;
        this.Sukses=sendSukses;
        this.Status=sendStatus;
        this.mode=mode;
    }
    public threadable(socketio sockclient,File file,ProgressBar pBarF,Label downloadStatus,Label downloadSukses,String mode)
    {
        this.sockclient=sockclient;
        this.file=file;
        this.pBar=pBarF;
        this.Status=downloadStatus;
        this.Sukses=downloadSukses;
        this.mode=mode;
    }
    public threadable(String mode)
    {
        this.mode=mode;
    }
    public void thread_sendfile() throws IOException
    {
        this.sockclient.sendFile(file, command);
    }
    public void thread_downloadfile() throws IOException, ClassNotFoundException
    {
        this.sockclient.readfile(file);
    }
    public void playMusic(String nama)
    {
        final URL resource = getClass().getResource(nama);
        final Media media = new Media(resource.toString());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
    @Override
    public void run() {
        if(this.mode.equals("SEND"))
        {
            try {
                this.thread_sendfile();
                this.pBar.setVisible(false);
                this.Status.setVisible(false);
                this.Sukses.setVisible(true);
                playMusic("coc.mp3");
            } catch (IOException ex) {
                Logger.getLogger(threadable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(this.mode.equals("DOWNLOAD"))
        {
            try {
                this.thread_downloadfile();
                this.pBar.setVisible(false);
                this.Status.setVisible(false);
                this.Sukses.setVisible(true);
                playMusic("coc.mp3");
            } catch (IOException ex) {
                Logger.getLogger(threadable.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(threadable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(this.mode.equals("MUSIC"))
        {
            playMusic("naruto.mp3");
        }
    }
    
}
