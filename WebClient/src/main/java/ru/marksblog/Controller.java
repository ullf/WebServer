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

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

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
    User user;
    ObservableList <FileData>list=FXCollections.observableArrayList();
    File file;

    public Controller(){

    }

    public void login(){
        nickname=nick.getText();
        synchronize();
        //desynchronize();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void synchronize(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                file=new File(nickname+".ser");
                try {
                    if(!file.exists()){
                        file.createNewFile();
                        //System.out.println(file.getAbsolutePath());
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
                    e.printStackTrace();
                }


            }
        }).start();

    }

    public void desynchronize(){
         new Thread(new Runnable() {
            @Override
            public void run() {
                file=new File(nickname+".ser");
                up=new FileUpload(client);
                up.sendByte(up.RECEIVE_FILE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        up.downloadFile(file.getName(),2048);
                        try {
                            ObjectInputStream oos=new ObjectInputStream(new FileInputStream(file));
                            user=(User)oos.readObject();
                            oos.close();
                            user.printAllFiles();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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
                //event.consume();
                //System.out.println(file.getPath());
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
                            //System.out.println(up.getProgress());
                        }
                    }
                }).start();
                //new FileUpload(client).uploadFile(file.getAbsolutePath());
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
       // up.downloadFile(file.getFilename(),size);
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
                desynchronize();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (up.getProgress()<=0.999){
                    progress.setProgress(up.getProgress());
                    //System.out.println(up.getProgress());
                }
            }
        }).start();
        //new FileUpload(client).uploadFile(file.getAbsolutePath());
        String size=String.valueOf(file.length());
        FileData filedata;
        filedata=new FileData(file.getName().toString(),size+" bytes");
        list.add(filedata);
        table.setItems(list);


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //table.getColumns().addAll(filename,filesize);
        filename.setCellValueFactory(new PropertyValueFactory<FileData,String>("filename"));
        filesize.setCellValueFactory(new PropertyValueFactory<FileData,String>("size"));
        table.setItems(list);
    }
}