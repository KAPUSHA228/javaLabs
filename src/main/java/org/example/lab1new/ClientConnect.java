package org.example.lab1new;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

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

    Model getModel() {
        return m;
    }

    void run() {
        try {
            is = cs.getInputStream();
            dis = new DataInputStream(is);
            while (true) {
                if (isServer) {
                    System.out.println("eblo");
                    ActionMsg msg = getAction();
                    if (msg.getType() == ActionType.GET) {
                        System.out.println("POIMAL1");
                        GameInfo msg2 = m.getAllInfo();
                        System.out.println("WAS1 " + m.getAllInfo().getScoreI(0));
                        System.out.println("IS1 " + msg2.getScoreI(0));
                        sendInfo(msg2);
                    }
                    else if (msg.getType() == ActionType.UPDSC) {
                        System.out.println("score");
                    }
                } else {
                    System.out.println("eblo2");
                    ActionMsg msg = getAction();
                    if (msg.getType() == ActionType.GET) {
                        System.out.println("POIMAL2");
                        GameInfo newInfo = getInfo();
                        GameInfo currentInfo = m.getAllInfo();
                        currentInfo.setAllScore(newInfo.getScores()); // Обновляем scores
                        currentInfo.setAllShot(newInfo.getShots());   // Обновляем shots
                        System.out.println("WAS2 " + m.getAllInfo().getScoreI(0));

                    }
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
