
package com.mycompany.grade;

import com.mycompany.data.SaveData;

public class GradeManager {

    // คะแนนกล่องและคะแนนปริศนา
    private int boxScore;
    private int puzzleScore;

    // ตัวจัดการคะแนนกลางของเกม ใช้ร่วมกันทุกแมพและทุกหน้าจอ
    // เรียกใช้: GradeManager.getInstance()
    private static final GradeManager INSTANCE = new GradeManager();

    public static GradeManager getInstance() {
        return INSTANCE;
    }

    // ยังสร้าง instance แยกได้ เช่นใช้ทดสอบ หรือแมพที่ต้องการคะแนนของตัวเอง
    public GradeManager() {
        boxScore = 0;
        puzzleScore = 0;
    }

    // ล้างคะแนนทั้งหมด ใช้ตอนเริ่มเกมใหม่
    public void reset() {
        boxScore = 0;
        puzzleScore = 0;
    }

    // รับคะแนนกล่องแต่ละครั้งมาสะสม
    // เก็บคะแนนดิบไว้ เพื่อให้คะแนนกับดัก -10 มีผลถูกต้อง
    public void addBoxScore(int score) {
        boxScore += score;
    }

    // ใช้กำหนดคะแนนกล่องโดยตรง หากจำเป็น
    public void setBoxScore(int score) {
        boxScore = score;
    }

    // คะแนนกล่องที่ใช้แสดงและคำนวณจริง อยู่ระหว่าง 0 ถึง 30
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

    // บันทึกผลคะแนนสุดท้าย
    public void saveResult() {
        SaveData.savePlayerScore(
                getBoxScore(),
                getPuzzleScore(),
                getTotalScore(),
                getGrade()
        );
    }
}
