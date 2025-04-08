package org.example.lab1new;

import java.util.ArrayList;

public class Model {
    ArrayList<IObserver> allServers = new ArrayList<>();
    GameInfo info = new GameInfo();

    public void event() {
        allServers.forEach(IObserver::event);
    }

    public void addChill(){
        this.info.addChill(8);
        event();
    }
    public void addServers(IObserver c) {
        allServers.add(c);
        info.addNewOBS();
        event();
    }

    GameInfo getAllInfo() {
        return info;
    }

    void setInfo(GameInfo in) {
        this.info = in;
        System.out.println("Было дело");
    }

    void setScores(ArrayList<Integer> sc) {
        this.info.setAllScore(sc);
    }

    void setShots(ArrayList<Integer> sh) {
        this.info.setAllShot(sh);
    }


}
