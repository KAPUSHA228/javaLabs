package org.example.lab1new;

import java.util.ArrayList;

public class GameInfo {
    private ArrayList<Integer> scores = new ArrayList<>();
    private ArrayList<Integer> shots = new ArrayList<>();
    public void addChill(){
        this.scores.add(8);
        this.shots.add(8);
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
        this.scores.add(0);
        this.shots.add(0);
    }
    public void IncreaseScoreI(int index, int val) {
        int tmp = this.scores.get(index) + val;
        this.scores.set(index, tmp);
    }

    public void SetScoreI(int index, int val) {
        this.scores.set(index, val);
    }

    public void ResetStatistic() {
        this.scores.replaceAll(ignored -> 0);
        this.shots.replaceAll(ignored -> 0);
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
