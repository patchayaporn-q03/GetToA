package com.mycompany.data;
import java.io.FileWriter;
import java.io.IOException;

public class SaveData {

    // บันทึกผลคะแนนของผู้เล่น
    public static void savePlayerScore(int boxScore,int puzzleScore,int totalScore,String grade) {

        try (FileWriter writer = new FileWriter("player_save.csv", true)) {

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

    // บันทึกตัวละครที่ผู้เล่นเลือก
    public static void saveCharacter(
            int character,
            String gender) {

        try (FileWriter writer = new FileWriter("settings.csv", true)) {

            writer.write(
                character + "," +
                gender + "\n"
            );

        } catch (IOException e) {
            System.out.println("ไม่สามารถบันทึกตัวละครได้");
            e.printStackTrace();
        }
    }
}
