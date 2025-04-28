package org.example.lab1new;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnect implements Runnable {
    private final Model m;
    private final Socket cs;
    private InputStream is;
    private DataInputStream dis;
    private DataOutputStream dos;
    private final Server server;
    private final boolean isServer;
    private final Gson json = new Gson();
    private int clientIndex;
    private String playerName;

    public ClientConnect(Socket cs, boolean isServer, Model m, Server server, int id) {
        this.cs = cs;
        this.isServer = isServer;
        this.m = m;
        this.server = server;
        this.clientIndex = id;
        try {
            System.out.println("ORIGIN ID " + this.clientIndex);
            OutputStream os = cs.getOutputStream();
            dos = new DataOutputStream(os);
            is = cs.getInputStream();
            dis = new DataInputStream(is);
            new Thread(this).start(); // Запускаем поток для обработки сообщений
        } catch (IOException e) {
            System.out.println("Constructor CC error1");
        }
    }

    public ClientConnect(Socket cs, boolean isServer, String name) {
        this(cs, isServer, BModel.build(), null, -1); // Для клиента создаем свою модель
        sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.SETID, name))));
        System.out.println("CNAME " + name);
        System.out.println("GAMADRILLA1");
        this.clientIndex = json.fromJson(getMessage().getData(), ActionMsg.class).getId();
        System.out.println("CLIENT NEW ID " + this.clientIndex);
    }

    int getID() {
        return this.clientIndex;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    @Override
    public void run() {
        try {
            is = cs.getInputStream();
            dis = new DataInputStream(is);
            while (true) {
                if (isServer) {
                    System.out.println("GAMADRILLA2");
                    Message msg = getMessage();
                    switch (msg.getType()) {
                        case Action:
                            ActionMsg act = json.fromJson(msg.getData(), ActionMsg.class);
                            switch (act.getType()) {
                                case UPDMODEL:
                                    System.out.println("Server: Received UPDMODEL");
                                    sendMessage(new Message(MessageType.GameInfo, json.toJson(server.getModel().getAllInfo())));
                                    break;
                                case UPDSC2:
                                    System.out.println("Server: Received UPDSC2");
                                    if (m.getAllInfo().getWinnerId() == -1) {
                                        m.getAllInfo().IncreaseScoreI(this.clientIndex, 2);
                                        if (m.getAllInfo().getScoreI(this.clientIndex) >= 6) {
                                            m.getAllInfo().setWinner(this.clientIndex);
                                            m.getAllInfo().setGameStarted(false);
                                            m.getAllInfo().setGameFollow(false);
                                            server.incrementWins(getPlayerName());
                                        }
                                        server.setModel(m);
                                        server.broadcast();
                                    }
                                    break;
                                case UPDSC1:
                                    System.out.println("Server: Received UPDSC1");
                                    if (m.getAllInfo().getWinnerId() == -1) {
                                        m.getAllInfo().IncreaseScoreI(this.clientIndex, 1);
                                        if (m.getAllInfo().getScoreI(this.clientIndex) >= 6) {
                                            m.getAllInfo().setWinner(this.clientIndex);
                                            m.getAllInfo().setGameStarted(false);
                                            m.getAllInfo().setGameFollow(false);
                                            server.incrementWins(getPlayerName());
                                        }
                                        server.setModel(m);
                                        server.broadcast();
                                    }
                                    break;
                                case UPDSH:
                                    System.out.println("Server: Received UPDSH");
                                    m.getAllInfo().IncrementShots(this.clientIndex);
                                    server.setModel(m);
                                    server.broadcast();
                                    break;
                                case END:
                                    System.out.println("Server: Received END");
                                    server.end();
                                    server.broadcast();
                                    break;
                                case STOP:
                                    System.out.println("Server: Received STOP");
                                    if (m.getAllInfo().isGameStarted()) {
                                        server.togglePaused();
                                    }
                                    if (!server.getPaused() && server.getFollowing()) {
                                        new Thread(() -> server.moveCircle1(m.getAllInfo().getDirection1())).start();
                                        new Thread(() -> server.moveCircle2(m.getAllInfo().getDirection2())).start();
                                    }
                                    server.setModel(m);
                                    server.broadcast();
                                    break;
                                case START:
                                    System.out.println("Server: Received START");
                                    if (server.checkReady() && !m.getAllInfo().isGameStarted()) {
                                        server.setStarting(true);
                                        server.setFollowing(true);
                                        new Thread(() -> server.moveCircle1((byte) 1)).start();
                                        new Thread(() -> server.moveCircle2((byte) 1)).start();
                                        server.setModel(m);
                                        server.broadcast();
                                    }
                                    break;
                                case READY:
                                    System.out.println("Server: Received READY");
                                    m.getAllInfo().setReady(getID(), true);
                                    server.setModel(m);
                                    server.broadcast();
                                    break;
                                case SETID:
                                    System.out.println("Server: Received SETID");
                                    this.playerName = act.getName();
                                    System.out.println("SNAME " + this.playerName);
                                    sendMessage(new Message(MessageType.SETID, json.toJson(new ActionMsg(ActionType.SETID, this.clientIndex))));
                                    m.getAllInfo().addName(this.playerName);
                                    server.setModel(m);
                                    server.savePlayer(getPlayerName());
                                    server.broadcast();
                                    break;
                                case SHOT:
                                    System.out.println("Server: Received SHOT");
                                    double initialX = act.getInitialX();
                                    double initialY = act.getInitialY();
                                    double speedX = act.getSpeedX();
                                    Bullet bullet = new Bullet(initialX, initialY, speedX, clientIndex);
                                    // m.getAllInfo().addBullet(bullet);
                                    server.broadcast(); // Рассылаем обновлённое состояние клиентам
                                    break;
                                default:
                                    System.out.println("Server: Unknown action");
                            }
                            break;
                        case GameInfo:
                            break;
                        case DBQuery:
                            System.out.println("Server: Received GETDB");
                            ArrayList<Player> tmp = server.getLeaderboard();
                            System.out.println("CC SZ " + tmp.size());
                            sendMessage(new Message(MessageType.DBQuery, json.toJson(new ActionMsg(ActionType.GETDB, tmp))));
                            break;
                        default:
                            System.out.println("Server: Unknown message");
                    }
                } else {
                    System.out.println("GAMADRILLA3");
                    Message newInfo = getMessage();
                    switch (newInfo.getType()) {
                        case SETID:
                            ActionMsg msg = json.fromJson(newInfo.getData(), ActionMsg.class);
                            this.clientIndex = msg.getId();
                            break;
                        case Action:
                            System.out.println("PUPA");
                            //json.fromJson(newInfo.getData(), ActionMsg.class);
                            break;
                        case GameInfo:
                            m.setInfo(json.fromJson(newInfo.getData(), GameInfo.class));
                            m.event();
                            break;
                        case DBQuery:
                            System.out.println("FULL GOVNA POEL");
                            break;
                        default:
                            System.out.println("Client: Unknown message");
                    }

                }
            }
        } catch (IOException e) {
            System.err.println("Run CC error1");
        }

    }

    synchronized void sendMessage(Message msg) {
        try {
            String s = json.toJson(msg);
            System.out.println("Sending JSON: " + s);
            dos.writeUTF(s);
            dos.flush();
        } catch (IOException e) {
            System.err.println("sendMessage CC error1");
        }
    }

    Message getMessage() {
        try {
            String s = dis.readUTF();
            System.out.println("Received JSON: " + s);
            return json.fromJson(s, Message.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}