package org.example.lab1new;

import java.util.ArrayList;

public class GameInfo {
    private ArrayList<Integer> scores = new ArrayList<>();
    private ArrayList<Integer> shots = new ArrayList<>();

    public ArrayList<Integer> getShots() {
        return shots;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public Integer getScoreI(int index) {
        return scores.get(index);
    }

    public Integer getShotI(int index) {
        return shots.get(index);
    }

    public void addNewOBS(){
        scores.add(0);
        shots.add(0);
    }
    public void IncreaseScoreI(int index, int val) {
        int tmp = scores.get(index) + val;
        scores.set(index, tmp);
    }

    public void SetScoreI(int index, int val) {
        scores.set(index, val);
    }

    public void ResetStatistic() {
        scores.replaceAll(ignored -> 0);
        shots.replaceAll(ignored -> 0);
    }

    public void IncrementShots(int index) {
        int tmp = shots.get(index) + 1;
        shots.set(index, tmp);
    }

    public void setAllScore(ArrayList<Integer> scores) {
        this.scores = scores;
    }

    public void setAllShot(ArrayList<Integer> shots) {
        this.shots = shots;
    }
}
