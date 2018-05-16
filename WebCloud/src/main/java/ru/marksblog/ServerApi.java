package ru.marksblog;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerApi {

    private OutputStream output[]=new OutputStream[10];
    private FileOutputStream fout;
    private FileInputStream fin;
    private InputStream input,input2;
    private String filename,nickname;
    private int in,in2;
    private long filesize;
    private User user;
    DB db=new DB();

    public long getFilesize(Socket client) throws IOException {
        int id=0;
        input=client.getInputStream();
        StringBuilder builder=new StringBuilder();
        StringBuilder builder2=new StringBuilder();
        StringBuilder builder3=new StringBuilder();
        StringBuilder nickbuilder=new StringBuilder();
        char arr[]=new char[512];
        int headersize=0;
        for(int i=0;i<512;i++){
            in=input.read();
            if((char)in!='#'){
                builder3.append((char)in);
            }
            else{
                headersize=Integer.valueOf(builder3.toString());
                break;
            }
        }
        for(int i=0;i<headersize;i++){
            in=input.read();
            arr[i]=(char)in;
            if(arr[i]!='#' && id==0){
                builder.append(arr[i]);
            }
            if(arr[i]=='#' && id==0){
                id++;
                continue;
            }
            if(arr[i]!='#' && id==1){
                builder2.append(arr[i]);
            }
            if(arr[i]=='#' &&  id==1){
                id++;
                continue;
            }

            if(arr[i]!='#' && id>1){
                nickbuilder.append(arr[i]);
            }
        }
        filesize=Integer.valueOf(builder.toString());
        filename=builder2.toString();
        nickname=nickbuilder.toString();
        return filesize;
    }


    public void saveUserFiles(){
        try {
            FileWriter fw=new FileWriter(user.getNickname());
            BufferedWriter wr=new BufferedWriter(fw);
            for ( Object key : user.getList().keySet() ) {
                wr.write(key.toString()+"/");
            }
            wr.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList readUserFiles(){
        ArrayList <String>list=new ArrayList<>();
        try {
            FileReader fr=new FileReader(user.getNickname());
            BufferedReader br=new BufferedReader(fr);
            try {
                String str[]=br.readLine().split("/");
                for(int i=0;i<str.length;i++){
                    list.add(str[i]);
                }
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void receiveFile(Socket client){
        try {
            filesize = getFilesize(client);
            user=new User();
            user.setNickname(nickname);
            if(!db.getValue(user.getNickname()).equals("null")){
                fout = new FileOutputStream(filename);
                input2 = client.getInputStream();
                for (int i = 0; i < filesize; i++) {
                    in2 = input2.read();
                    fout.write(in2);
                }
                fout.close();

                ArrayList<String> tmp = readUserFiles();
                for (int i = 0; i < tmp.size(); i++) {
                    File ftmp = new File(tmp.get(i));
                    user.addFile(tmp.get(i), (int) ftmp.length());
                }

                File ftmp = new File(filename);
                user.addFile(filename, (int) ftmp.length());
                saveUserFiles();
                try {
                    FileOutputStream serializable = new FileOutputStream(user.getNickname() + ".ser");
                    ObjectOutputStream obj = new ObjectOutputStream(serializable);
                    obj.writeObject(user);
                    obj.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(Socket client){
        int b,tmp=-1;
        long filesize;
        try {
            filesize = getFilesize(client);
            System.out.println(filename+" "+filesize);
            for(int j=0;j<Server.users.size();j++) {
                if (client == Server.users.get(j)) {
                    output[j]=client.getOutputStream();
                    tmp=j;
                }
            }
            System.out.println(tmp);
            fin=new FileInputStream(filename);
            for(int i=0;i<filesize;i++){
                b=fin.read();
                output[tmp].write(b);
            }
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
