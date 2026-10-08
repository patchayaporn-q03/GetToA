package com.mycompany.grade;

public class GradeBox {

    private int boxScore;
    private int puzzleScore;
    private int openedBoxCount;

    public GradeBox() {
        boxScore = 0;
        puzzleScore = 0;
        openedBoxCount = 0;
    }

    /*  คำนวณคะแนนจากการเปิดกล่อง
    *   จำนวนกล่องที่เปิด
    *   ตรวจว่าครบขั้นต่ำ 3 กล่องยัง
    *   ตั้งคะแนน Puzzle
    */

    public boolean addBoxScore(int score) {

        if (openedBoxCount >= 6) {
            return false;
        }

        boxScore += score;
        openedBoxCount++;

        return true;
    }

    public int getOpenedBoxCount() {
        return openedBoxCount;
    }

    public boolean canFinishBox() {
    return openedBoxCount >= 3;
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
}