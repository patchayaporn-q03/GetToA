package com.mycompany.grade;

import com.mycompany.puzzle.PuzzleScore;

/**
 * สรุปผลเกมทั้งหมด (คะแนนปริศนา + คะแนนกล่อง + เกรด)
 * เรียกใช้: GameResult.current() จะดึงคะแนนล่าสุดจาก PuzzleScore และ GradeManager มาสรุปให้
 */
public class GameResult {

    public static final int MAX_PUZZLE_SCORE = 70;
    public static final int MAX_BOX_SCORE = 30;
    public static final int MAX_TOTAL_SCORE = MAX_PUZZLE_SCORE + MAX_BOX_SCORE;

    private final int puzzleScore;
    private final int boxScore;
    private final int totalScore;
    private final String grade;

    private GameResult(int puzzleScore, int boxScore, int totalScore, String grade) {
        this.puzzleScore = puzzleScore;
        this.boxScore = boxScore;
        this.totalScore = totalScore;
        this.grade = grade;
    }

    // รวมคะแนนจากทุกแมพ ณ ตอนนี้ แล้วคำนวณเกรดด้วย GradeManager
    // แมพ 2: สรุปเฉพาะคะแนน WiringTask + กล่องของแมพ 2 ไม่รวมคะแนน puzzle แมพ 1
    private static boolean map2Mode = false;

    public static void setMap2Mode(boolean on) {
        map2Mode = on;
    }

    public static GameResult current() {
        GradeManager manager = GradeManager.getInstance();
        manager.setPuzzleScore(map2Mode
                ? com.mycompany.puzzle.saifai.WiringTask.getPuzzleScore()
                : PuzzleScore.getPuzzleScore());
        return new GameResult(
                manager.getPuzzleScore(),
                manager.getBoxScore(),
                manager.getTotalScore(),
                manager.getGrade());
    }

    public int getPuzzleScore() {
        return puzzleScore;
    }

    public int getBoxScore() {
        return boxScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getGrade() {
        return grade;
    }

    public boolean isPassed() {
        return !"F".equals(grade);
    }
}
