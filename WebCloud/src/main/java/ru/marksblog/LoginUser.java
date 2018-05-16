package ru.marksblog;

import java.io.*;
import java.net.Socket;

public class LoginUser{

    public String getNickname(Socket client){
        String nickname=null;
        try {
            DataInputStream input=new DataInputStream(client.getInputStream());
            nickname=input.readUTF();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return nickname;
    }
}
