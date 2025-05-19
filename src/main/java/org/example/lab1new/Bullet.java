package org.example.lab1new;

public class Bullet {
    private double x, y;       // Текущая позиция пули
    private final double speedX; // Скорость пули
    private final int ownerId;       // ID игрока, выпустившего пулю
    private double radius = 5; // Радиус пули (для проверки столкновений)

    public Bullet(double x, double y, double speedX, int ownerId) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.ownerId = ownerId;
    }

    public void update() {
        x += speedX;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getOwnerId() { return ownerId; }
    public double getRadius() { return 5; }
    public double getSpeedX() { return speedX; }
}
