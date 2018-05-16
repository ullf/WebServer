package ru.marksblog;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable{

    Socket client;
    private InputStream input;
    private int in;
   // User user;

    public ClientHandler(Socket client){
        this.client=client;
        this.client=new Socket();
    }

    public void getData(){
            try {
                input=client.getInputStream();

                while((in=input.read())!=-1){
                    if((byte)in==4) {
                        new ServerApi().receiveFile(client);
                    }
                    if((byte)in==5){
                        new ServerApi().sendFile(client);
                    }
            }
            } catch (IOException e) {
            try {
                input.close();
                client.close();
                for(int i=0;i<Server.users.size();i++){
                    if(client==Server.users.get(i)){
                        Server.users.remove(i);
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while(true){
            getData();
        }
    }


}
