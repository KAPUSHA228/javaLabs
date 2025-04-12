package org.example.lab1new;

import java.util.ArrayList;

public class Model {
    private final ArrayList<IObserver> allServers;
    private GameInfo info;
    Model(){
        info= new GameInfo();
        allServers = new ArrayList<>();
    }
    public void event() {
        allServers.forEach(IObserver::event);
    }

    public void addServers(IObserver c) {
        allServers.add(c);
        info.addNewOBS();
        System.out.println("KOLVO"+ info.getScores());
        event();
    }

    GameInfo getAllInfo() {
        return info;
    }

    void setInfo(GameInfo in) {
        this.info = in;
        System.out.println("Было дело");
    }
    }
