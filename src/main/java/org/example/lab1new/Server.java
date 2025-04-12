package org.example.lab1new;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private boolean isFollowing;
    private boolean isRunning1;
    private boolean isRunning2;
    private byte direction1;
    private byte direction2;
    private boolean isPaused;
    private static final int port = 3124;
    private Socket cs;
    private int score;
    InetAddress ip = null;
    Model m = BModel.build();
    ArrayList<ClientConnect> list = new ArrayList<>();

    Server() {
        isFollowing = false;
        isRunning1 = false;
        isRunning2 = false;
        isPaused = false;
        direction1 = 1;
        direction2 = 1;
        new Thread(() -> moveCircle1(direction1)).start();
        new Thread(() -> moveCircle2(direction2)).start();
        try {
            ip = InetAddress.getLocalHost();
            ServerSocket ss = new ServerSocket(port, 0, ip);
            System.out.println("Server started\n");
            while (true) {
                Socket cs = ss.accept();
                m.addServers(() -> {
                });
                int clientIndex = list.size();
                ClientConnect cc = new ClientConnect(cs, true, m, this, clientIndex);

                int port = cs.getPort();
                System.out.println("SERVER's MODEL " + m.getAllInfo().getScores());
                System.out.println("Connected to: " + port);
                list.add(cc);
                new Thread(cc).start();
                cc.sendAction(new ActionMsg(ActionType.UPDMODEL));
                cc.sendInfo(m.getAllInfo()); // Отправляем данные сразу после подключения
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public void setFollowing() {
        isFollowing = !isFollowing;
    }

    public void setPaused() {
        isPaused = !isPaused;
    }

    public boolean getFollowing() {
        return isFollowing;
    }

    public boolean getPaused() {
        return isPaused;
    }

    public void moveCircle1(byte direction) {

        byte speed = 5;
        direction1 = direction;
        if (!isRunning1) {
            isRunning1 = true;

            while (isFollowing && !isPaused) {
                double newY = m.getAllInfo().getC1().getCenterY() + speed * direction1;
                if (newY <= 0 || newY >= 286.4 - 30) {
                    direction1 *= -1;
                }
                m.getAllInfo().getC1().setCenterY(newY);
                broadcast((m.getAllInfo()));
                try {
                    Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
                } catch (InterruptedException e) {
                    System.out.println("Sleep error2");
                }
            }
            isRunning1 = false;
        }
    }

    public void moveCircle2(byte direction) {
        byte speed = 2;
        direction2 = direction;
        if (!isRunning2) {
            isRunning2 = true;

            while (isFollowing && !isPaused) {
                double newY = m.getAllInfo().getC2().getCenterY() + speed * direction2;
                if (newY <= 0 || newY >= 286.4 - 50) {
                    direction2 *= -1;
                }
                m.getAllInfo().getC2().setCenterY(newY);
                broadcast(m.getAllInfo());
                try {
                    Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
                } catch (InterruptedException e) {
                    System.out.println("Sleep error3");
                }
            }
            isRunning2 = false;
        }
    }

    public void broadcast(GameInfo info) {
        list.forEach(client -> client.sendInfo(info));
    }

    public static void main(String[] args) {
        new Server();
    }

}