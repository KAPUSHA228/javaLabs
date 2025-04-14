package org.example.lab1new;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private boolean isRunning1;
    private boolean isRunning2;
    private static final int port = 3124;
    private final Model m = BModel.build();
    private final ArrayList<ClientConnect> list = new ArrayList<>();
    private final ArrayList<Boolean> ready = new ArrayList<>();

    Server() {
        m.getAllInfo().setGameFollow(false);
        isRunning1 = false;
        isRunning2 = false;
        m.getAllInfo().setPaused(false);
        m.getAllInfo().setDirection1((byte) 1);
        m.getAllInfo().setDirection2((byte) 1);
        new Thread(() -> moveCircle1(m.getAllInfo().getDirection1())).start();
        new Thread(() -> moveCircle2(m.getAllInfo().getDirection2())).start();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            ServerSocket ss = new ServerSocket(port, 0, ip);
            System.out.println("Server started\n");
            while (true) {
                Socket cs = ss.accept();
                if(list.size()<4) {
                    m.addServers(() -> {
                    });
                    int clientIndex = list.size();
                    ClientConnect cc = new ClientConnect(cs, true, m, this, clientIndex);
                    int port = cs.getPort();
                    System.out.println("SERVER's MODEL " + m.getAllInfo().getScores());
                    System.out.println("Connected to: " + port);
                    synchronized (list) {
                        list.add(cc);
                    }
                    synchronized (ready) {
                        ready.add(false);
                    }
                    new Thread(cc).start();
                    cc.sendAction(new ActionMsg(ActionType.UPDMODEL));
                    cc.sendInfo(m.getAllInfo()); // Отправляем данные сразу после подключения
                }
                else{
                    cs.close(); // Отклоняем лишние подключения
                    System.out.println("Server is full. Connection rejected.");
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    void setModel(Model m) {
        this.m.setModel(m);
    }

    void setReady(int index) {
        ready.set(index, true);
    }

    public void setFollowing(boolean k) {
        m.getAllInfo().setGameFollow(k);
    }

    public void togglePaused() {
        m.getAllInfo().setPaused(!m.getAllInfo().isPaused());
        System.out.println("SERVER PAUSE " + m.getAllInfo().isPaused());
    }

    public boolean getFollowing() {
        return m.getAllInfo().isGameFollow();
    }

    public boolean getPaused() {
        return m.getAllInfo().isPaused();
    }

    public void moveCircle1(byte direction) {

        byte speed = 5;
        byte direction1 = direction;
        if (!isRunning1) {
            isRunning1 = true;

            while (m.getAllInfo().isGameFollow() && !m.getAllInfo().isPaused()) {
                double newY = m.getAllInfo().getC1().getCenterY() + speed * direction1;
                if (newY <= 0 || newY >= 286.4 - 30) {
                    direction1 *= -1;
                }
                m.getAllInfo().getC1().setCenterY(newY);
                broadcast();
                m.getAllInfo().setDirection1(direction1);
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
        byte direction2 = direction;
        if (!isRunning2) {
            isRunning2 = true;

            while (m.getAllInfo().isGameFollow() && !m.getAllInfo().isPaused()) {
                double newY = m.getAllInfo().getC2().getCenterY() + speed * direction2;
                if (newY <= 0 || newY >= 286.4 - 50) {
                    direction2 *= -1;
                }
                m.getAllInfo().getC2().setCenterY(newY);
                broadcast();
                m.getAllInfo().setDirection2(direction2);
                try {
                    Thread.sleep(16); // ~60 FPS (1000ms / 60 = 16ms)
                } catch (InterruptedException e) {
                    System.out.println("Sleep error3");
                }
            }
            isRunning2 = false;
        }
    }

    public void broadcast() {
        list.forEach(client -> client.sendInfo(m.getAllInfo()));
    }

    public static void main(String[] args) {
        new Server();
    }

    public boolean checkReady() {
        for (Boolean i : ready)
            if (!i) return false;
        return true;

    }
}