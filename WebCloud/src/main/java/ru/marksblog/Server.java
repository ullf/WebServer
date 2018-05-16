package ru.marksblog;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

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

            /*SessionFactory factory=new Configuration().configure().buildSessionFactory();
            Session ses=factory.openSession();
            ses.beginTransaction();
            User b=new User();
            b.setNickname("mark");
            ses.save(b);
            ses.getTransaction().commit();

            Session ses2=factory.openSession();
            ses2.beginTransaction();
            Query sql=ses2.createQuery("SELECT nickname FROM User");
            ses2.getTransaction().commit();
            System.out.println(sql.getResultList().get(0).toString());
            factory.close();*/

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
