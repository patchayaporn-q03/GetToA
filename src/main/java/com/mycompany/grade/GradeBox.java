package com.mycompany.grade;

public class GradeBox {

    private int boxScore;
    private int openedBoxCount;

    public GradeBox() {
        boxScore = 0;
        openedBoxCount = 0;
    }

    /**
     * @param addBoxScore คำนวณคะแนนจากการเปิดกล่อง
     * @param getOpenedBoxCount จำนวนกล่องที่เปิด
     * @param canFinishBox ตรวจว่าครบขั้นต่ำ 3 กล่องยัง
     * @param getBoxScore คะแนนจากกล่องทั้งหมด
     */

    public boolean addBoxScore(int score) {

        if (openedBoxCount >= 6) {
            return false;
        }
        boxScore += score;
        openedBoxCount++;
        if (boxScore > 30) {
            boxScore = 30;
        }

        return true;
    }

    public int getOpenedBoxCount() {
        return openedBoxCount;
    }

    public boolean canFinishBox() {
    return openedBoxCount >= 3;
    }

    public int getBoxScore() {
        return boxScore;
    }

}