package com.mycompany.data;
import java.io.FileWriter;
import java.io.IOException;

public class SaveData {

    // บันทึกผลคะแนนของผู้เล่น
    public static void savePlayerScore(int boxScore,int puzzleScore,int totalScore,String grade) {

        try (FileWriter writer = new FileWriter(CharacterData.DIR + "/player_save.csv", true)) {

            writer.write(
                boxScore + "," +
                puzzleScore + "," +
                totalScore + "," +
                grade + "\n"
            );

        } catch (IOException e) {
            System.out.println("ไม่สามารถบันทึกคะแนนได้");
            e.printStackTrace();
        }
    }
}
