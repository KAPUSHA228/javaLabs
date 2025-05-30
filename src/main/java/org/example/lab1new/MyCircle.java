package org.example.lab1new;

public class MyCircle {
    private double r;
    private double centerX;
    private double centerY;

    public MyCircle(double centerX, double centerY, double r) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.r = r;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getRadius() {
        return r;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setRadius(double r) {
        this.r = r;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public double getCenterY() {
        return centerY;
    }

}
