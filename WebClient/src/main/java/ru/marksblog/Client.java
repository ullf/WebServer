package ru.marksblog;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    Socket client;
    private InputStream input;
    private OutputStream output;
    String message;
    String username;
    String address;
    int port=4444;

    public Client(){

    }

    public String getAddress(){
        return  address;
    }

    public int getPort(){
        return  port;
    }

    public void setPort(int port){
        this.port=port;
    }

    public Client(String address, int port){
        this.address=address;
        this.port=port;
       // UserRegister();
        try {
            client=new Socket(getAddress(),getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        input=client.getInputStream();
                        int in;
                        while((in=input.read())!=-1){
                            System.out.print((char)in);
                        }
                    } catch (IOException e) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }).start();*/
    }

    /*public void UserRegister(){
        System.out.print("Your username: ");
        Scanner sc=new Scanner(System.in);
        username=sc.nextLine();
        System.out.print("Host to connect: ");
        address=sc.nextLine();
        if(address.equals("")){
            address="localhost";
            port=4444;
            System.out.println(address);
        }else{
            String tmp[]=address.split(":");
            address=tmp[0];
            setPort(Integer.parseInt(tmp[1]));
        }
        setUsername(username);
    }*/

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    /*public void sendData(){
        try {
            output=client.getOutputStream();
            Scanner sc=new Scanner(System.in);
            message=sc.nextLine();
            message=getUsername()+": "+ message+"\n";
            output.write(message.getBytes());

        } catch (IOException e) {
            System.out.println("Server is shutdwon!");
        }
    }*/

    @Override
    public void run() {
        //while(true){
            //sendData();
       // }
    }
}
