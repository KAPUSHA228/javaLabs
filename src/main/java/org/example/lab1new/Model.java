package org.example.lab1new;

import java.util.ArrayList;

public class Model {
    private ArrayList<IObserver> allServers;
    private GameInfo info;

    Model() {
        info = new GameInfo();
        allServers = new ArrayList<>();
    }

    public void event() {
        allServers.forEach(IObserver::event);
    }

    @Override
    public String toString() {
        return "Model{" +
                "allServers=" + allServers +
                ", info=" + info.toString() +
                '}';
    }

    public void addServers(IObserver c) {
        allServers.add(c);
        info.addNewOBS();
        event();
    }

    GameInfo getAllInfo() {
        return info;
    }

    public ArrayList<IObserver> getAllServers() {
        return allServers;
    }

    void setModel(Model m) {
        this.allServers = m.getAllServers();
        this.info = m.getAllInfo();
    }

    void setInfo(GameInfo in) {
        this.info = in;
    }
}
