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

    public ClientConnect(Socket cs, boolean isServer, Model m, Server server, int id) {
        this.cs = cs;
        this.isServer = isServer;
        this.m = m;
        this.server = server;
        this.clientIndex = id;
        try {
            OutputStream os = cs.getOutputStream();
            dos = new DataOutputStream(os);
            InputStream is = cs.getInputStream();
            dis = new DataInputStream(is);
            new Thread(this::run).start(); // Запускаем поток для обработки сообщений
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientConnect(Socket cs, boolean isServer) {
        this(cs, isServer, BModel.build(), null, 0);
        this.clientIndex = getAction().getID();
        System.out.println("MY CLIENT ID " + clientIndex);
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
                            m.getAllInfo().IncreaseScoreI(clientIndex, 2); // Обновляем общую модель
                            server.broadcast(m.getAllInfo());// Уведомляем всех (рассылка через наблюдателя)
                            break;
                        case UPDSC1:
                            System.out.println("Server: Received UPDSC1");
                            m.getAllInfo().IncreaseScoreI(clientIndex, 1);
                            server.broadcast(m.getAllInfo());// Уведомляем всех (рассылка через наблюдателя)
                            break;
                        case UPDSH:
                            System.out.println("Server: Received UPDSH");
                            m.getAllInfo().IncrementShots(clientIndex);
                            server.broadcast(m.getAllInfo());// Уведомляем всех (рассылка через наблюдателя)
                            break;
                        case SETID:
                            sendAction(new ActionMsg(ActionType.SETID, this.clientIndex));
                            System.out.println("Server: Received SETID");
                            break;
                        default:
                            System.out.println("Server: Unknown action");
                    }
                } else {
                    System.out.println("eblo2");
                    //ActionMsg msg = getAction();
                    //if (msg.getType() == ActionType.UPDMODEL) {
                    System.out.println("POIMAL8");
                    GameInfo newInfo = getInfo();
                    m.setInfo(newInfo);
                    //System.out.println("WAS8 " + m.getAllInfo().getScoreI(0));
                    m.event();
                    //}
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void sendAction(ActionMsg msg) {
        try {
            String s = json.toJson(msg);
            System.out.println("HI");
            dos.writeUTF(s);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
