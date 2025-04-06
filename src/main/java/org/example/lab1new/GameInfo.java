package org.example.lab1new;

import java.util.ArrayList;

public class GameInfo {
    ArrayList<Integer> scores = new ArrayList<>();
    ArrayList<Integer> shots = new ArrayList<>();

    public ArrayList<Integer> getShots() {
        return shots;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public void IncrementScore(int index) {
        int tmp = scores.get(index) + 1;
        scores.set(index, tmp);
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
