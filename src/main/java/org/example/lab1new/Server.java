package org.example.lab1new;

import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    private static final int port = 3124;
    private ServerSocket ss;
    private Socket cs;
    InetAddress ip=null;
    Model m =BModel.build();
    Server(){
        try {
            ip=InetAddress.getLocalHost();
            ss=new ServerSocket(port,0,ip);
            System.out.println("Server start\n");

            while(true){
                cs=ss.accept();
                int port = cs.getPort();
                System.out.println("Connect to "+port);
                ClientConnect cc = new ClientConnect(cs,true);
            }
        }
        catch(IOException e){
            System.out.println("Error1");
        }
    }

    public static void main(String[] args) {
        new Server();
    }

}