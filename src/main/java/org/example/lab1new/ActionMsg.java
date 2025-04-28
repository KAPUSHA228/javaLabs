package org.example.lab1new;

import java.util.ArrayList;

public class ActionMsg {
    private final ActionType type;
    private int id =-1;
    private double initialX=0; // Начальная X-координата пули
    private double initialY=0; // Начальная Y-координата пули
    private double speedX=0;   // Скорость пули по X
    private String name="";
    private ArrayList<Player> winners= new ArrayList<>();
    ActionMsg(ActionType type) {
        this.type = type;
    }

    ActionMsg(ActionType type, int idx) {
        this.type = type;
        this.id = idx;
    }
    ActionMsg(ActionType type, ArrayList<Player> p) {
        this.type = type;
        this.winners = p;
        System.out.println("sz "+winners.size());
    }
    ActionMsg(ActionType type, String name) {
        this.type = type;
        this.name=name;
    }
    ActionMsg(ActionType type, double initialX, double initialY, double speedX) {
        this.type = type;
        this.initialX = initialX;
        this.initialY = initialY;
        this.speedX = speedX;
    }
    ActionType getType() {
        return this.type;
    }

    int getId() {
        return this.id;
    }
    public double getInitialX() { return this.initialX; }
    public double getInitialY() { return this.initialY; }
    public double getSpeedX() { return this.speedX; }

    public String getName() {
        return this.name;
    }
    public  ArrayList<Player> getLeaderBoard(){
        return this.winners;
    }
}
