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
    boolean isServer;
    Gson json = new Gson();
    private int clientIndex;

    public ClientConnect(Socket cs, boolean isServer, Model m, Server server, int id) {
        this.cs = cs;
        this.isServer = isServer;
        this.m = m;
        this.server = server;
        this.clientIndex=id;
        try {
            System.out.println("ORIGIN ID "+ this.clientIndex);
            os = cs.getOutputStream();
            dos = new DataOutputStream(os);
            is = cs.getInputStream();
            dis = new DataInputStream(is);
            new Thread(this).start(); // Запускаем поток для обработки сообщений
        } catch (IOException e) {
            System.out.println("Constructor CC error1");
        }
    }

    public ClientConnect(Socket cs, boolean isServer) {
        this(cs, isServer, BModel.build(), null, -1); // Для клиента создаем свою модель
        sendAction(new ActionMsg(ActionType.SETID));
        this.clientIndex= getAction().getId();
        System.out.println("CLIENT NEW ID "+ this.clientIndex);
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
                    ActionMsg msg = getAction();
                    switch (msg.getType()) {
                        case UPDMODEL:
                            System.out.println("Server: Received UPDMODEL");
                            sendInfo(m.getAllInfo());
                            break;
                        case UPDSC2:
                            System.out.println("Server: Received UPDSC2");
                            m.getAllInfo().IncreaseScoreI(this.clientIndex, 2);
                            server.setModel(m);
                            server.broadcast();
                            break;
                        case UPDSC1:
                            System.out.println("Server: Received UPDSC1");
                            m.getAllInfo().IncreaseScoreI(this.clientIndex, 1);
                            server.setModel(m);
                            server.broadcast();
                            break;
                        case UPDSH:
                            System.out.println("Server: Received UPDSH");
                            m.getAllInfo().IncrementShots(this.clientIndex);
                            server.setModel(m);
                            server.broadcast();
                            break;
                        case END:
                            System.out.println("Server: Received END");
                            m.getAllInfo().setGameStarted(false);
                            server.setFollowing(false);
                            m.getAllInfo().ResetStatistic();
                            server.setModel(m);
                            server.broadcast();
                            break;
                        case STOP:
                            System.out.println("Server: Received STOP");
                            server.togglePaused();
                            if (!server.getPaused() && server.getFollowing()) {
                                new Thread(() -> server.moveCircle1(m.getAllInfo().getDirection1())).start();
                                new Thread(() -> server.moveCircle2(m.getAllInfo().getDirection2())).start();
                            }
                            server.setModel(m);
                            server.broadcast();
                            break;
                        case START:
                            System.out.println("Server: Received START");
                            m.getAllInfo().setGameStarted(true);
                            server.setFollowing(true);
                            new Thread(() -> server.moveCircle1((byte) 1)).start();
                            new Thread(() -> server.moveCircle2((byte) 1)).start();
                            server.setModel(m);
                            server.broadcast();
                            break;
                        case SETID:
                            sendAction(new ActionMsg(ActionType.SETID,this.clientIndex));
                            break;
                        default:
                            System.out.println("Server: Unknown action");
                    }
                } else {
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
