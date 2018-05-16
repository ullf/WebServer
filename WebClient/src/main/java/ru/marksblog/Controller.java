package ru.marksblog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class Controller  extends Client implements Initializable{

    private Socket client;
    @FXML private TextField nick;
    @FXML private TableView table;
    @FXML private TableColumn filename;
    @FXML private TableColumn filesize;
    @FXML private ProgressBar progress;
    @FXML private Pane pane;
    @FXML private TextField port,ipaddr;
    private FileUpload up;
    private String nickname;
    private boolean canSync=true;
    User user;
    ObservableList <FileData>list=FXCollections.observableArrayList();
    File file;
    private final Logger logger= LoggerFactory.getLogger(Controller.class);

    public Controller(){
        logger.info("Started!");
    }

    public void login(){
        nickname=nick.getText();
       /* try {
            OutputStream output=client.getOutputStream();
            output.write((byte)3);
            output.write(nickname.getBytes());
        } catch (IOException e) {
        }*/
        logger.info("Nickname have been set");
        synchronize();
    }

    public void connect(){
        String ipport=port.getText();
        String ip=ipaddr.getText();
        if(ip.equals(null) || ip.equals(" ")){
            ip="localhost";
        }
        if(ipport.equals(null) || ipport.equals(" ")){
            ipport="4444";
        }
        try {
            client=new Socket(ip,Integer.valueOf(ipport));
            logger.info("Connected!");
        } catch (IOException e) {
            logger.error("Failed to connect");
        }
    }

    public void synchronize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    file=new File(nickname+".ser");
                    if(!file.exists()){
                        file.createNewFile();
                        up=new FileUpload(client);
                        up.sendByte(up.SEND_FILE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                up.uploadFile(file.getAbsolutePath(),nickname);
                            }
                        }).start();
                    }
                } catch (IOException e) {
                    logger.error("Not connected to the server!");
                }


            }
        }).start();

    }

    public void desynchronize(){

                System.out.println(canSync);
                up=new FileUpload(client);
                up.sendByte(up.RECEIVE_FILE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        file=new File(nickname+".ser");
                        System.out.println(file.getAbsolutePath());
                        up.downloadFile(file.getName(),2048);
                        try {
                            ObjectInputStream oos=new ObjectInputStream(new FileInputStream(file));
                            user=(User)oos.readObject();
                            oos.close();
                            HashMap l=user.getList();
                            Object arr[]=null;
                            for(int i=0;i<l.size();i++){
                                FileData filedata;
                                arr=l.keySet().toArray();
                                filedata=new FileData(arr[i].toString(),user.getList().values().toArray()[i].toString()+" bytes");
                                System.out.println(l.keySet().toString());
                                if(canSync){
                                    list.add(filedata);
                                }
                            }
                            if(canSync){
                                table.setItems(list);
                            }
                            canSync=false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
    }

    public void over(){
        pane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                Dragboard drag=event.getDragboard();
                file=drag.getFiles().get(0);
            }
        });
    }


    public void dropit(){
        pane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                up=new FileUpload(client);
                up.sendByte(up.SEND_FILE);
                System.out.println(file.getPath());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        up.uploadFile(file.getAbsolutePath(),nickname);
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (up.getProgress()<=0.999){
                            progress.setProgress(up.getProgress());
                        }
                    }
                }).start();
            }
        });
    }

    public void exited(){
        pane.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                String size=String.valueOf(file.length());
                list.add(new FileData(file.getName().toString(),size+" bytes"));
                table.setItems(list);
                canSync=true;
            }
        });
    }

    public void downloadFile(){
        FileData file=(FileData)table.getSelectionModel().getSelectedItem();
        String tmp=file.getSize().split(" ")[0];
        Long size=Long.valueOf(tmp);
        up=new FileUpload(client);
        up.sendByte(up.RECEIVE_FILE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                up.downloadFile(file.getFilename(),size);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (up.getProgress()<=0.999){
                    progress.setProgress(up.getProgress());
                }
            }
        }).start();
        FileData filed=(FileData)table.getSelectionModel().getSelectedItem();
        table.getItems().remove(filed);
        System.out.println(file.getFilename());
    }

    public void sendFile() {
        System.out.println(client.getRemoteSocketAddress());
        JFileChooser chooser=new JFileChooser();
        chooser.showOpenDialog(null);
        file=chooser.getSelectedFile();
        up=new FileUpload(client);
        up.sendByte(up.SEND_FILE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                up.uploadFile(file.getAbsolutePath(),nickname);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (up.getProgress()<=0.999){
                    progress.setProgress(up.getProgress());
                }
            }
        }).start();
        String size=String.valueOf(file.length());
        FileData filedata;
        filedata=new FileData(file.getName().toString(),size+" bytes");
        list.add(filedata);
        table.setItems(list);
        canSync=true;


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //table.getColumns().addAll(filename,filesize);
        filename.setCellValueFactory(new PropertyValueFactory<FileData,String>("filename"));
        filesize.setCellValueFactory(new PropertyValueFactory<FileData,String>("size"));
        table.setItems(list);
    }
}
