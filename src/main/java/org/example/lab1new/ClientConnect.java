package org.example.lab1new;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class ClientConnect implements Runnable {
    private final Model m;
    Socket cs;
    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;
    private final Server server;
    boolean isServer = false;
    Gson json = new Gson();
    private int clientIndex;

    public ClientConnect(Socket cs, boolean isServer, Model m, Server server) {
        this.cs = cs;
        this.isServer = isServer;
        this.m = m;
        this.server = server;
        try {
            OutputStream os = cs.getOutputStream();
            dos = new DataOutputStream(os);
            InputStream is = cs.getInputStream();
            dis = new DataInputStream(is);
            new Thread(this).start(); // Запускаем поток для обработки сообщений
        } catch (IOException e) {
            System.out.println("Constructor CC error1");
        }
    }

    public ClientConnect(Socket cs, boolean isServer) {
        this(cs, isServer, BModel.build(), null); // Для клиента создаем свою модель

    }

    Model getModel() {
        return m;
    }
    int getID() {
        return clientIndex;
    }

    @Override
    public void run() {
        try {
            is = cs.getInputStream();
            dis = new DataInputStream(is);
            while (true) {
                if (isServer) {
                    System.out.println("eblo");
                    ActionMsg msg = getAction();
                    switch (msg.getType()) {
                        case UPDMODEL:
                            System.out.println("Server: Received UPDMODEL");
                            sendInfo(m.getAllInfo());
                            break;
                        case UPDSC2:
                            System.out.println("Server: Received UPDSC2");
                            m.getAllInfo().IncreaseScoreI(0, 2);
                            server.broadcast(m.getAllInfo());
                            break;
                        case UPDSC1:
                            System.out.println("Server: Received UPDSC1");
                            m.getAllInfo().IncreaseScoreI(0, 1);
                            server.broadcast(m.getAllInfo());
                            break;
                        case UPDSH:
                            System.out.println("Server: Received UPDSH");
                            m.getAllInfo().IncrementShots(0);
                            server.broadcast(m.getAllInfo());
                            break;
                        case END:
                            System.out.println("Server: Received END");
                            m.getAllInfo().setGameStarted(false);
                            server.setPaused();
                            server.setFollowing();
                            m.getAllInfo().ResetStatistic();
                            server.broadcast(m.getAllInfo());
                            break;
                        case STOP:
                            // m.getAllInfo().togglePause();
                            server.broadcast(m.getAllInfo());
                            server.setPaused();
                            if (!server.getPaused() && server.getFollowing()) {
                                new Thread(() -> server.moveCircle1((byte) 1)).start();
                                new Thread(() -> server.moveCircle2((byte) 1)).start();
                            }
                            server.broadcast(m.getAllInfo());
                            break;
                        case START:
                            System.out.println("Server: Received START");
                            m.getAllInfo().setGameStarted(true);
                            server.setFollowing();
                            new Thread(() -> server.moveCircle1((byte) 1)).start();
                            new Thread(() -> server.moveCircle2((byte) 1)).start();
                            server.broadcast(m.getAllInfo());
                            break;
                        default:
                            System.out.println("Server: Unknown action");
                    }
                } else {
                    System.out.println("eblo2");
                    GameInfo newInfo = getInfo();
                    m.setInfo(newInfo);
                    m.event();
                }
            }
        } catch (IOException e) {
            System.out.println("Run CC error1");
        }

    }

    void sendAction(ActionMsg msg) {
        try {
            String s = json.toJson(msg);
            System.out.println("HI");
            dos.writeUTF(s);
            dos.flush();
        } catch (IOException e) {
            System.out.println("sendAction CC error1");

        }
    }

    ActionMsg getAction() {
        try {
            String s = dis.readUTF();
            return json.fromJson(s, ActionMsg.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void sendInfo(GameInfo msg) {
        try {
            String s = json.toJson(msg);
            System.out.println("HI2");
            dos.writeUTF(s);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    GameInfo getInfo() {
        try {
            String s = dis.readUTF();
            return json.fromJson(s, GameInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
