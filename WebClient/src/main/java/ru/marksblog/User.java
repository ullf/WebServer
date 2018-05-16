package ru.marksblog;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {

    private String nickname;
    private int id;
    // private ArrayList<String> userfiles=new ArrayList<>();
    private HashMap<String,Integer> userfiles=new HashMap<>();

    public String getNickname(){
        return nickname;
    }

    public void setNickname(String nickname){
        this.nickname=nickname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addFile(String filename,int size){
        //userfiles.add(filename);
        userfiles.put(filename,size);
    }

    public void printAllFiles(){
        for(int i=0;i<userfiles.size();i++){
            System.out.println(userfiles.get(i));
        }
    }

    public int getFilesize(int index){
        return userfiles.get(index);
    }

    public HashMap getList(){
        return userfiles;
    }
}
