package org.example.lab1new;

import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    private static final int port = 3124;
    private ServerSocket ss;
    private Socket cs;
    private int score;
    InetAddress ip = null;
    Model m = BModel.build();
    ArrayList<ClientConnect> list = new ArrayList<>();
    Server() {
        try {
            System.out.println("Before addChill: " + m.getAllInfo().getScores());
            m.addChill();
            System.out.println("After addChill: " + m.getAllInfo().getScores());
            ip = InetAddress.getLocalHost();
            ss = new ServerSocket(port, 0, ip);
            System.out.println("Server start\n");

            while (true) {
                cs = ss.accept();

                int port = cs.getPort();
                System.out.println("Connect to " + port);
                ClientConnect cc = new ClientConnect(cs, true);
                list.add(cc);
                m.addServers(() -> {
                    GameInfo info = m.getAllInfo();
                    if (info.getScores().isEmpty()) {
                        System.out.println("WARN: Scores list is empty!"); // Логирование
                    }
                    list.forEach(client -> client.sendInfo(info));
                });

            }
        } catch (IOException e) {
            System.out.println("Error1");
        }
    }

    public static void main(String[] args) {
        new Server();
    }

}