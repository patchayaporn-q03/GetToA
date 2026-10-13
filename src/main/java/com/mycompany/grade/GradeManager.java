
package com.mycompany.grade;

import com.mycompany.data.SaveData;

public class GradeManager {
    /**
     * @param boxScore คะแนนจากกล่อง
     * @param puzzleScore คะแนนจาก puzzle
     * GradeManager.getInstance() จัดการคะแนนกลางของเกม ใช้ร่วมกันทุกแมพและทุกหน้าจอ หรือแมพที่มีคะแนนเป็นของแมพนั้นๆ
     * @param reset ล้างคะแนนทั้งหมดถ้าเริ่มเกมใหม่
     * @param addBoxScore รับคะแนนกล่องแต่ละครั้งมาสะสม และเก็บคะแนนไว้หากได้กล่องกับดักจะได้เอามาลบได้
     * @param setBoxScore กำหนดคะแนนกล่องโดยตรง
     * @param getBoxScore คะแนนกล่องที่ใช้แสดงและคำนวณจริง มีค่า 0 - 30
     * @param setPuzzleScore กำหนดคะแนนจาก puzzle 70 คะแนน
     * @param getPuzzleScore คะแนนจาก puzzle
     * @param getTotalScore กำหนดคะแนนรวมมาจาก กล่อง + puzzle
     * @param getGrade กำหนดเกรด
     * @param saveResult บันทึกผลคะแนนสุดท้าย
    */

    private int boxScore;
    private int puzzleScore;

    private static final GradeManager INSTANCE = new GradeManager();

    public static GradeManager getInstance() {
        return INSTANCE;
    }

    public GradeManager() {
        boxScore = 0;
        puzzleScore = 0;
    }

    public void reset() {
        boxScore = 0;
        puzzleScore = 0;
    }

    public void addBoxScore(int score) {
        boxScore += score;
    }

    public void setBoxScore(int score) {
        boxScore = score;
    }

    public int getBoxScore() {
        return Math.max(0, Math.min(boxScore, 30));
    }

    public void setPuzzleScore(int score) {
        puzzleScore = Math.max(0, Math.min(score, 70));
    }

    public int getPuzzleScore() {
        return puzzleScore;
    }

    public int getTotalScore() {
        return getBoxScore() + getPuzzleScore();
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

    public void saveResult() {
        SaveData.savePlayerScore(
                getBoxScore(),
                getPuzzleScore(),
                getTotalScore(),
                getGrade()
        );
    }
}
