package ru.marksblog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class FileUpload implements Runnable{

    OutputStream output;
    InputStream input;
    Socket client;
    FileInputStream fin;
    File file;
    double progress;
    int b;
    byte buffer[]=new byte[1024];
    final byte SEND_FILE = 4;
    final byte RECEIVE_FILE = 5;
    private final Logger logger= LoggerFactory.getLogger(FileUpload.class);
    public FileUpload(Socket client){
        this.client=client;
    }

    public void prog(int i,long size){
        progress=(double)i/(double)size;
    }

    public double getProgress(){
        return progress;
    }

    public void sendByte(byte firstbyte){
        try {
            output=client.getOutputStream();
            output.write(firstbyte);
        } catch (IOException e) {
            logger.error("Not connected to the server!");
            e.printStackTrace();
        }
    }

    public void downloadFile(String filename,long size){
        try {
           // output=client.getOutputStream();
            //output.write(5);
            Header header=new Header(filename,size);
            String tmp=String.valueOf(header.getHeader().length());
            output.write(tmp.getBytes());
            output.write("#".getBytes());
            output.write(header.getHeader().getBytes());
            input=client.getInputStream();
            output=new FileOutputStream(filename);
        } catch (IOException e) {
            logger.error("Failed to download file!");
            e.printStackTrace();
        }
        file=new File(filename);
        try {
            fin=new FileInputStream(file);
            for(int i=0;i<size;i++){
                b=input.read();
                output.write(b);
                prog(i,size);
            }
            output.close();
            fin.close();
        } catch (IOException e) {
            logger.error("Failed to save a file!");
            e.printStackTrace();
        }
    }

    public void uploadFile(String filename,String nickname){
        /*try {
            output=client.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        file=new File(filename);
        try {
            fin=new FileInputStream(file);
           // output.write(4);
            Header header=new Header(file.getName(),file.length());
            header.setNickname(nickname);
            String tmp=String.valueOf(header.getHeader().length());
            output.write(tmp.getBytes());
            output.write("#".getBytes());
            output.write(header.getHeader().getBytes());
            for(int i=0;i<file.length();i++){
                for(int j=0;j<buffer.length;j++){
                    buffer[j]=(byte)fin.read();
                    i++;
                }
               // b=fin.read();
                output.write(buffer);
                prog(i,file.length());
            }
            fin.close();
           // progress=0.0;
            System.out.println("File uploaded!");
        } catch (Exception e) {
            logger.error("Failed to upload file!");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
