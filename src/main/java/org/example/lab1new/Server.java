package org.example.lab1new;

import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    static int clientIndex=0;
    private double circle1Y = 150;
    private double circle2Y = 150;
    private byte direction1 = 1;
    private byte direction2 = 1;
    private static final int port = 3124;
    private ServerSocket ss;
    private Socket cs;
    private int score;
    InetAddress ip = null;
    Model m = BModel.build();
    ArrayList<ClientConnect> list = new ArrayList<>();
    Server() {
        try {
            ip = InetAddress.getLocalHost();
            ss = new ServerSocket(port, 0, ip);
            System.out.println("Server started\n");

            // Общий наблюдатель для всех клиентов
            m.addServers(() -> {
                GameInfo currentInfo = m.getAllInfo();
                list.forEach(client -> client.sendInfo(currentInfo));
            });

            while (true) {
                Socket cs = ss.accept();
                ClientConnect cc = new ClientConnect(cs, true, m, this, clientIndex);
                System.out.println( "VIVOD ID CONTROL "+ clientIndex);
                int port = cs.getPort();
                System.out.println("SERVER's MODEL "+m.getAllInfo().getScores());
                System.out.println("Connected to: " + port);
                list.add(cc);
                new Thread(cc).start();
                cc.sendAction(new ActionMsg(ActionType.SETID, clientIndex));
                clientIndex++;
                System.out.println("MY SERVER ID "+ cc.getID());
                cc.sendInfo(m.getAllInfo()); // Отправляем данные сразу после подключения
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
    public void broadcast(GameInfo info) {
        list.forEach(client -> client.sendInfo(info));
    }
    public static void main(String[] args) {
        new Server();
    }

}