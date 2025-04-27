package org.example.lab1new;

public class ActionMsg {
    private final ActionType type;
    private int id;
    private double initialX; // Начальная X-координата пули
    private double initialY; // Начальная Y-координата пули
    private double speedX;   // Скорость пули по X

    ActionMsg(ActionType type) {
        this.type = type;
    }

    ActionMsg(ActionType type, int idx) {
        this.type = type;
        this.id = idx;
    }
    ActionMsg(ActionType type, double initialX, double initialY, double speedX) {
        this.type = type;
        this.initialX = initialX;
        this.initialY = initialY;
        this.speedX = speedX;
    }
    ActionType getType() {
        return type;
    }

    int getId() {
        return id;
    }
    public double getInitialX() { return initialX; }
    public double getInitialY() { return initialY; }
    public double getSpeedX() { return speedX; }
}
