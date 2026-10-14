package com.mycompany.puzzle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PuzzleScore {

    private static double puzzleTime;
    private static int puzzleScore;

    // บันทึกไฟล์ไว้ในโฟลเดอร์ที่ใช้รันโปรแกรม
    private static final Path CSV_FILE = Path.of("puzzle_scores.csv");

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

        saveToCSV();

        System.out.println("Puzzle Complete!");
        System.out.printf("Time = %.2f seconds%n", puzzleTime);
        System.out.println("Puzzle Score = " + puzzleScore);
        System.out.println("CSV file = " + CSV_FILE.toAbsolutePath());
    }

    private static void saveToCSV() {
        boolean writeHeader = !Files.exists(CSV_FILE);

        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String row = String.format(
                java.util.Locale.US,
                "%s,%.2f,%d%n",
                timestamp,
                puzzleTime,
                puzzleScore
        );

        try {
            if (writeHeader) {
                Files.writeString(
                        CSV_FILE,
                        "date_time,time_seconds,score%n".replace("%n", System.lineSeparator()),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }

            Files.writeString(
                    CSV_FILE,
                    row,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("ไม่สามารถบันทึกคะแนนลง CSV ได้: " + e.getMessage());
        }
    }

    public static double getPuzzleTime() {
        return puzzleTime;
    }

    public static int getPuzzleScore() {
        return puzzleScore;
    }
}
