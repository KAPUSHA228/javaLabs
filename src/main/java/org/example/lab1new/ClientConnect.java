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

    void run() {
        try {
            is = cs.getInputStream();
            dis = new DataInputStream(is);
            while (true) {
                if (isServer) {
                    ActionMsg msg = getAction();
                    if (msg.getType() == ActionType.SET) {
                        System.out.println("POPADOS");
                    }
                } else {
                    System.out.println("POPADOS2");
                    //break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void sendAction(ActionMsg msg) {
        try {
            String s = json.toJson(msg);
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
}
