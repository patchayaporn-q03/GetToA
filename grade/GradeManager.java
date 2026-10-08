package com.mycompany.grade;
import com.mycompany.data.SaveData;

public class GradeManager {

    private int boxScore;
    private int puzzleScore;

    public GradeManager() {
        boxScore = 0;
        puzzleScore = 0;
    }

    public void addBoxScore(int score) {
        boxScore += score;

        if (boxScore > 30) {
            boxScore = 30;
        }
    }

    public void setPuzzleScore(int score) {
        puzzleScore = score;
    }

    public int getBoxScore() {
        return boxScore;
    }

    public int getPuzzleScore() {
        return puzzleScore;
    }

    public int getTotalScore() {
        return boxScore + puzzleScore;
    }

    public String getGrade() {

        int total = getTotalScore();

        if (total >= 80) {
            return "A";
        } else if (total >= 70) {
            return "B";
        } else if (total >= 60) {
            return "C";
        } else if (total >= 50) {
            return "D";
        } else {
            return "F";
        }
 
    }

    public SaveData.savePlayerScore{
        getBoxScore();
        getPuzzleScore();
        getTotalScore();
        getGrade();
    }
}

