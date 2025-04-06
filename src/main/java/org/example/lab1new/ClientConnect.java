package org.example.lab1new;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnect {
    Model m = BModel.build();
    Socket cs;
    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;
    boolean isServer = false;
    Gson json = new Gson();

    public ClientConnect(Socket cs, boolean isServer) {
        this.cs = cs;
        this.isServer = isServer;
        try {
            os = cs.getOutputStream();
            dos = new DataOutputStream(os);
            new Thread(this::run).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void run() {
        try {
            is = cs.getInputStream();
            dis = new DataInputStream(is);
            while (true) {
                if (isServer) {
                    System.out.println("eblo");
                    ActionMsg msg = getAction();
                    if (msg.getType() == ActionType.READY) {
                        System.out.println("POIMAL");
                    }
                    if (msg.getType() == ActionType.UPDATESCORE) {
                        System.out.println("score");
                    }
                } else {
                    System.out.println("eblo2");
                    GameInfo msg = getInfo();
                    m.setInfo(msg);
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
