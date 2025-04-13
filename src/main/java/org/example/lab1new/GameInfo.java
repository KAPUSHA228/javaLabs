package org.example.lab1new;

import java.util.ArrayList;
import java.util.Collections;

public class GameInfo {
    private ArrayList<Integer> scores = new ArrayList<>();
    private ArrayList<Integer> shots = new ArrayList<>();
    private final MyCircle c1 = new MyCircle(380.0, 150.0, 50.0);
    private final MyCircle c2 = new MyCircle(677.0, 150.0, 25.0);
    private boolean isGameStarted = false;
    private boolean isGameFollow = false;
    private boolean isPaused = false;
    private byte direction1;
    private byte direction2;

    public boolean isGameStarted() {
        return this.isGameStarted;
    }

    public boolean isGameFollow() {
        return this.isGameFollow;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public void setDirection2(byte direction2) {
        this.direction2 = direction2;
    }

    public void setDirection1(byte direction1) {
        this.direction1 = direction1;
    }

    public byte getDirection2() {
        return this.direction2;
    }

    public byte getDirection1() {
        return this.direction1;
    }

    public void setGameStarted(boolean started) {
        this.isGameStarted = started;
    }

    public void setGameFollow(boolean follow) {
        this.isGameFollow = follow;
    }

    public MyCircle getC1() {
        return this.c1;
    }

    public MyCircle getC2() {
        return this.c2;
    }

    public void addClient() {
        this.scores.add(0);
        this.shots.add(0);
    }

    public ArrayList<Integer> getShots() {
        return this.shots;
    }

    public ArrayList<Integer> getScores() {
        return this.scores;
    }

    public Integer getScoreI(int index) {
        return this.scores.get(index);
    }

    public Integer getShotI(int index) {
        return this.shots.get(index);
    }

    public void addNewOBS() {
        System.out.println("Adding new OBS entry"); // Логирование
        addClient();
    }

    public void IncreaseScoreI(int index, int val) {
        int tmp = this.scores.get(index) + val;
        this.scores.set(index, tmp);
    }

    public void ResetStatistic() {
        Collections.fill(this.scores, 0);
        Collections.fill(this.shots, 0);
    }

    public void IncrementShots(int index) {
        int tmp = this.shots.get(index) + 1;
        this.shots.set(index, tmp);
    }

}
