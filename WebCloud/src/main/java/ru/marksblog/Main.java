package ru.marksblog;

import java.util.Scanner;

public class Main {

    static int port;

    public static void main(String[] args){
        System.out.print("Port to bind: ");
        Scanner sc=new Scanner(System.in);
        port=sc.nextInt();
        Server server=new Server(port);
        System.out.println(port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    server.waitConnections();
                }
            }
        }).start();
    }
}
