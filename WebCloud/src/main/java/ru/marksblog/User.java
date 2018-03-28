package ru.marksblog;

import java.io.*;
import java.util.ArrayList;

public class User implements Serializable {

    private String nickname;
    private ArrayList<String> userfiles=new ArrayList<>();

    public String getNickname(){
        return nickname;
    }

    public void setNickname(String nickname){
        this.nickname=nickname;
    }

    public void addFile(String filename){
        userfiles.add(filename);
    }

    public void printAllFiles(){
        for(int i=0;i<userfiles.size();i++){
            System.out.println(userfiles.get(i));
        }
    }
}
