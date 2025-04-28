package org.example.lab1new;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameInfo {
    private final ArrayList<Integer> scores = new ArrayList<>();
    private final ArrayList<Integer> shots = new ArrayList<>();
    private final ArrayList<Boolean> ready = new ArrayList<>();
    private final ArrayList<String> names = new ArrayList<>();
    private final MyCircle c1 = new MyCircle(380.0, 150.0, 50.0);
    private final MyCircle c2 = new MyCircle(677.0, 150.0, 25.0);
    private boolean isGameStarted = false;
    private boolean isGameFollow = false;
    private boolean isPaused = false;
    private byte direction1;
    private byte direction2;
    private int winnerId = -1; // По умолчанию -1 означает, что победителя нет
    private final List<Bullet> activeBullets = new ArrayList<>();

    public List<Bullet> getActiveBullets() {
        return activeBullets;
    }

    public void addBullet(Bullet bullet) {
        activeBullets.add(bullet);
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void removeBullet(Bullet bullet) {
        activeBullets.remove(bullet);
    }
    public synchronized void setWinner(int id) {
        winnerId = id;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public boolean isGameStarted() {
        return this.isGameStarted;
    }

    public boolean isGameFollow() {
        return this.isGameFollow;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public synchronized void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public synchronized void setDirection2(byte direction2) {
        this.direction2 = direction2;
    }

    public synchronized void setDirection1(byte direction1) {
        this.direction1 = direction1;
    }

    public byte getDirection2() {
        return this.direction2;
    }

    public byte getDirection1() {
        return this.direction1;
    }

    public synchronized void setGameStarted(boolean started) {
        this.isGameStarted = started;
    }

    public synchronized void setGameFollow(boolean follow) {
        this.isGameFollow = follow;
    }
    void setReady(int index, boolean val) {
        ready.set(index, val);
    }
    boolean getReadyI(int index){
        return ready.get(index);
    }
    ArrayList<Boolean> getReady(){
        return ready;
    }
    public MyCircle getC1() {
        return this.c1;
    }

    public MyCircle getC2() {
        return this.c2;
    }

    public synchronized void addClient() {
        this.scores.add(0);
        this.shots.add(0);
        this.ready.add(false);
    }
    public synchronized void addChill() {
        this.scores.add(8);
        this.shots.add(8);
        this.ready.add(false);
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
    public void addName(String name){
        names.add(name);
    }
    public String getNameI(int index){
        return this.names.get(index);
    }

    public synchronized void IncreaseScoreI(int index, int val) {
        int tmp = this.scores.get(index) + val;
        this.scores.set(index, tmp);
    }

    public synchronized void ResetStatistic() {
        Collections.fill(this.scores, 0);
        Collections.fill(this.shots, 0);
        c1.setCenterY(150.0);
        c2.setCenterY(150.0);
        isGameStarted = false;
        isGameFollow = false;
        isPaused = false;
        winnerId = -1;
    }

    public synchronized void IncrementShots(int index) {
        int tmp = this.shots.get(index) + 1;
        this.shots.set(index, tmp);
    }

}
