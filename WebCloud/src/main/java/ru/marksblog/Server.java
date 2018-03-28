package ru.marksblog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server{

    static ArrayList<Socket> users=new ArrayList<>();
    ServerSocket server;
    Socket client;
    ClientHandler handler;

    public Server(int port){
        try {
            server=new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Can't create server.Probably port is already taken!");
            e.printStackTrace();
        }
    }

    public void waitConnections(){
        handler=new ClientHandler(client);

        try {
            handler.client=server.accept();
            users.add(handler.client);
            System.out.println(users);
            new Thread(handler).start();
            System.out.println("New client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
