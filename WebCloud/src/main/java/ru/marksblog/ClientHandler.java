package ru.marksblog;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable{

    Socket client;
    private OutputStream output[]=new OutputStream[10];
    private FileOutputStream fout;
    //FileOutputStream serializable;
    private FileInputStream fin;
    private InputStream input,input2;
    private String filename,nickname;
    private int in,in2;
    long filesize;
    User user;

    public ClientHandler(Socket client){
        this.client=client;
        this.client=new Socket();
    }

    public void saveUserFiles(){
            try {
                FileWriter fw=new FileWriter(user.getNickname());
                BufferedWriter wr=new BufferedWriter(fw);
               /* for(int i=0;i<user.getList().size();i++){
                    wr.write(user.getList().get(i)+"/");
                }*/
               /*for(Object sizes: user.getList().values()){
                    wr.write(sizes.toString()+" ");
                    System.out.println(sizes.toString());
               }*/
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

    public long getFilesize() throws IOException{
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
          /*  if(arr[i]!='#' && id>0){
                builder2.append(arr[i]);
            }*/
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
        System.out.println(nickname);
        return filesize;
    }

    public void getData(){
            try {
                input=client.getInputStream();
                user=new User();
                while((in=input.read())!=-1){
                    if((byte)in==4) {
                        //new Thread(new Runnable() {
                          //  @Override
                           // public void run() {
                                try {
                                    filesize=getFilesize();
                                   // if(user.getNickname().equals(null)){
                                        user.setNickname(nickname);
                                   // }
                                    fout=new FileOutputStream(filename);
                                    input2 = client.getInputStream();
                                    for(int i=0;i<filesize;i++){
                                        in2 = input2.read();
                                        fout.write(in2);
                                    }
                                    fout.close();

                                    ArrayList <String> tmp=readUserFiles();
                                    for(int i=0;i<tmp.size();i++){
                                        // if(!user.getList().contains(user.getList().get(i))){
                                        File ftmp=new File(tmp.get(i));
                                        user.addFile(tmp.get(i),(int)ftmp.length());
                                        // }
                                    }

                                    File ftmp=new File(filename);
                                    user.addFile(filename,(int)ftmp.length());
                                    saveUserFiles();
                                    //user.printAllFiles();
                                    try {
                                        FileOutputStream serializable=new FileOutputStream(user.getNickname()+".ser");
                                        ObjectOutputStream obj=new ObjectOutputStream(serializable);
                                        obj.writeObject(user);
                                        obj.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (SecurityException e) {
                                        e.printStackTrace();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        //}).start();

                   // }

                    if((byte)in==5){
                        int b,tmp=-1;
                        long filesize=getFilesize();
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
                    }
                    System.out.print((char)in);
                   /* for(int i=0;i<Server.users.size();i++){
                        output[i]=Server.users.get(i).getOutputStream();
                        output[i].write(in);
                    }*/
                //}
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
