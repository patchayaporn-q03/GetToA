package com.mycompany.puzzle;

public class PuzzleScore {

    private static double puzzleTime;
    private static int puzzleScore;

    public static void puzzleComplete(double time) {

        puzzleTime = time;

        if (time <= 20) {
            puzzleScore = 70;
        } else if (time <= 30) {
            puzzleScore = 65;
        } else if (time <= 60) {
            puzzleScore = 55;
        } else {
            puzzleScore = 45;
        }

        System.out.println("Puzzle Complete!");
        System.out.println("Time = " + puzzleTime + " seconds");
        System.out.println("Puzzle Score = " + puzzleScore);
    }

    public static double getPuzzleTime() {
        return puzzleTime;
    }

    public static int getPuzzleScore() {
        return puzzleScore;
    }
}
