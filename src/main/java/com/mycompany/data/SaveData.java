package com.mycompany.data;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SaveData {

    private static final Path PLAYER_FILE =
            Path.of("player_save.csv");

    /**
     * บันทึกผลคะแนนของผู้เล่นลงไฟล์ CSV
     *
     * @param boxScore คะแนนจากการเปิดกล่อง
     * @param puzzleScore คะแนนจากการแก้ปริศนา
     * @param totalScore คะแนนรวมทั้งหมด
     * @param grade เกรดที่ผู้เล่นได้รับ
     */
    public static void savePlayerScore(
            int boxScore,
            int puzzleScore,
            int totalScore,
            String grade) {

        try {
            boolean writeHeader =
                    !Files.exists(PLAYER_FILE)
                    || Files.size(PLAYER_FILE) == 0;

            try (var writer = Files.newBufferedWriter(
                    PLAYER_FILE,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND)) {

                if (writeHeader) {
                    writer.write(
                            "BoxScore,PuzzleScore,TotalScore,Grade");
                    writer.newLine();
                }

                writer.write(
                        boxScore + ","
                        + puzzleScore + ","
                        + totalScore + ","
                        + grade);

                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println(
                    "ไม่สามารถบันทึกคะแนนได้: "
                    + e.getMessage());
        }
    }
}
