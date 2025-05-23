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

    public Socket getSocket() {
        return cs;
    }

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
            new Thread(this).start();
        } catch (IOException e) {
            System.err.println("Constructor CC error1" + e.getMessage());
        }
    }

    public ClientConnect(Socket cs, boolean isServer, String name) {
        this(cs, isServer, BModel.build(), null, -1);
        sendMessage(new Message(MessageType.Action, json.toJson(new ActionMsg(ActionType.SETID, name))));
        System.out.println("CNAME " + name);
    }

    int getID() {
        return this.clientIndex;
    }

    boolean getisServer() {
        return isServer;
    }

    Model getModel() {
        return m;
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
                    Message msg = getMessage();
                    switch (msg.type()) {
//                        case TEST:
//                            System.out.println("CATCH TEST");
//                            break;
                        case DB_QUERY:
                            System.out.println("Получен запрос лидерборда");
                            ArrayList<Player> leaderboard = (ArrayList<Player>) DatabaseInit.getLeaderboard();
                            sendMessage(new Message(MessageType.DB_RESPONSE, json.toJson(leaderboard)));
                            break;
                        case Action:
                            ActionMsg act = json.fromJson(msg.data(), ActionMsg.class);
                            switch (act.getType()) {
                                case UPDMODEL:
                                    System.out.println("Server: Received UPDMODEL");
                                    sendMessage(new Message(MessageType.GameInfo, json.toJson(server.getModel().getAllInfo())));
                                    System.out.println(json.toJson(server.getModel().getAllInfo()));
                                    break;

                                case UPDSC2:
                                    System.out.println("Server: Received UPDSC2");
                                    if (m.getAllInfo().getWinnerId() == -1) {
                                        m.getAllInfo().IncreaseScoreI(this.clientIndex, 2);
                                        if (m.getAllInfo().getScoreI(this.clientIndex) >= 7) {
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
                                        if (m.getAllInfo().getScoreI(this.clientIndex) >= 7) {
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
                                    if (server.getStarting()) {
                                        server.togglePaused();
                                    }
                                    if (!server.getPaused() && server.getFollowing()) {
                                        new Thread(() -> server.moveCircle1(server.getModel().getAllInfo().getDirection1())).start();
                                        new Thread(() -> server.moveCircle2(server.getModel().getAllInfo().getDirection2())).start();
                                    }
                                    server.broadcast();
                                    break;
                                case START:
                                    System.out.println("Server: Received START");
                                    if (server.checkReady() && !server.getStarting()) {
                                        server.setStarting(true);
                                        server.setFollowing(true);
                                        new Thread(() -> server.moveCircle1((byte) 1)).start();
                                        new Thread(() -> server.moveCircle2((byte) 1)).start();
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
                                    System.out.println("SID" + this.clientIndex);
                                    sendMessage(new Message(MessageType.SETID, json.toJson(new ActionMsg(ActionType.SETID, this.clientIndex))));
                                    m.getAllInfo().setName(this.clientIndex, this.playerName);
                                    server.setModel(m);
                                    server.savePlayer(getPlayerName());
                                    server.broadcast();
                                    break;
//                                case SHOT:
//                                    System.out.println("Server: Received SHOT");
//                                    double initialX = act.getInitialX();
//                                    double initialY = act.getInitialY();
//                                    double speedX = act.getSpeedX();
//                                    Bullet bullet = new Bullet(initialX, initialY, speedX, clientIndex);
//                                    // m.getAllInfo().addBullet(bullet);
//                                    server.broadcast(); // Рассылаем обновлённое состояние клиентам
//                                    break;
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
                    Message newInfo = getMessage();
                    switch (newInfo.type()) {
                        case SETID:
                            System.out.println("GET SETID CASE");
                            ActionMsg msg = json.fromJson(newInfo.data(), ActionMsg.class);
                            this.clientIndex = msg.getId();
                            System.out.println("CLIENT NEW ID " + clientIndex);
                            break;
                        case Action:
                            if (json.fromJson(newInfo.data(), ActionMsg.class).getType() == ActionType.UPDMODEL) {
                                System.out.println("WAS UPDMODEL");
                            } else {
                                System.out.println("WASN'T UPDMODEL");
                            }
                            break;
                        case GameInfo:
                            m.setInfo(json.fromJson(newInfo.data(), GameInfo.class));
                            m.event();
                            System.out.println(m.getAllInfo().isGameStarted() + "FOO");
                            break;
                        case DBQuery:
                            break;
                        default:
                            System.out.println("Client: Unknown message");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Run CC error1" + e.getMessage());
        }

    }

    synchronized void sendMessage(Message msg) {
        try {
            String s = json.toJson(msg);
            // System.out.println("Sending JSON: " + s);
            dos.writeUTF(s);
            dos.flush();
        } catch (IOException e) {
            System.err.println("sendMessage CC error1" + e.getMessage());
        }
    }

    Message getMessage() {
        try {
            String s = dis.readUTF();
            //System.out.println("Received JSON: " + s);
            return json.fromJson(s, Message.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}