package org.example.lab1new;

import java.util.ArrayList;
import java.util.Collections;

public class GameInfo {

    private ArrayList<Integer> scores = new ArrayList<>();
    private ArrayList<Integer> shots = new ArrayList<>();
    private final MyCircle c1 = new MyCircle(380.0,150.0,50.0);
    private final MyCircle c2 = new MyCircle(677.0,150.0,25.0);
    private  ArrayList<MyCircle> bullets = new ArrayList<>();
    private boolean isGameStarted = false;

    public boolean isGameStarted() {
        return this.isGameStarted;
    }

    public void setGameStarted(boolean started) {
        this.isGameStarted = started;
    }
    public MyCircle getC1() {
        return this.c1;
    }

    public MyCircle getC2() {
        return this.c2;
    }

    public void addClient(){
        this.scores.add(0);
        this.shots.add(0);
    }
    public void addChill(Integer k){
        this.scores.add(k);
        this.shots.add(k);
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

    public void SetScoreI(int index, int val) {
        this.scores.set(index, val);
    }

    public void ResetStatistic() {
        Collections.fill(this.scores, 0);
        Collections.fill(this.shots, 0);
    }

    public void IncrementShots(int index) {
        int tmp = this.shots.get(index) + 1;
        this.shots.set(index, tmp);
    }

    public void setAllScore(ArrayList<Integer> scores) {
        this.scores = scores;
    }

    public void setAllShot(ArrayList<Integer> shots) {
        this.shots = shots;
    }
}
