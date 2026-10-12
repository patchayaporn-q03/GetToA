package com.mycompany.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ข้อมูลตัวละครที่ให้ผู้เล่นเลือก อ่านจาก Data/character_stats.csv
 * (ถ้ายังไม่มีไฟล์ จะสร้างไฟล์ค่าเริ่มต้นให้เอง)
 */
public class CharacterData {

    // ถ้ารันจากโฟลเดอร์โปรเจกต์ จะเก็บไว้ในโฟลเดอร์ data ของ mycompany
    // ถ้าไม่มีโฟลเดอร์นั้น (เช่น รันจาก .jar) จะเก็บในโฟลเดอร์ Data ข้างโปรแกรมแทน
    private static final String DIR = new java.io.File("src/main/java/com/mycompany/data").isDirectory()
            ? "src/main/java/com/mycompany/data" : "Data";

    public static final String FILE = DIR + "/character_stats.csv";

    private static final List<String> DEFAULT_ROWS = Arrays.asList(
        "id,gender,name,image,walk_image",
        "1,female,Serena,serena,",
        "2,female,Rosie,rosie,rosie_walk",
        "3,female,Rosalie,rosalie,rosalie_walk",
        "4,female,Raven,raven,",
        "5,female,Olive,olive,olive_walk",
        "6,female,Molly,molly,molly_walk",
        "7,female,Mabel,mabel,mabel_walk",
        "8,female,Luna,luna,luna_walk",
        "9,female,Eliana,eliana,eliana_walk"
    );

    private final int id;
    private final String gender;
    private final String name;
    private final String image;
    private final String walkImage;

    public CharacterData(int id, String gender, String name, String image, String walkImage) {
        this.id = id;
        this.gender = gender;
        this.name = name;
        this.image = image;
        this.walkImage = walkImage;
    }

    public int getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getWalkImage() {
        return walkImage;
    }

    public static final String SETTINGS_FILE = DIR + "/settings.csv";

    // บันทึกตัวละครที่ผู้เล่นเลือกและชื่อที่ตั้ง ลง Data/settings.csv (เก็บการเลือกล่าสุด เขียนทับของเดิม)
    public static void saveSettings(int characterId, String gender, String playerName) {
        try {
            Path path = Paths.get(SETTINGS_FILE);
            Files.createDirectories(path.getParent());
            // ชื่อห้ามมีเครื่องหมาย , หรือขึ้นบรรทัดใหม่ เพราะจะทำให้ csv เพี้ยน
            String name = playerName.replaceAll("[,\\r\\n]", " ").trim();
            Files.write(path, Arrays.asList(
                "gender,character_id,player_name",
                gender + "," + characterId + "," + name
            ), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("ไม่สามารถบันทึกตัวละครได้");
            e.printStackTrace();
        }
    }

    // อ่านการเลือกล่าสุด คืนค่า {gender, character_id, player_name} หรือ null ถ้ายังไม่เคยบันทึก
    public static String[] loadSettings() {
        try {
            Path path = Paths.get(SETTINGS_FILE);
            if (!Files.exists(path)) {
                return null;
            }
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.size() < 2) {
                return null;
            }
            String[] c = lines.get(1).split(",", 3);
            return c.length == 3 ? c : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // อ่านตัวละครของเพศที่ต้องการ (เรียงตาม id)
    public static List<CharacterData> load(String gender) {
        List<CharacterData> list = new ArrayList<>();
        try {
            Path path = Paths.get(FILE);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.write(path, DEFAULT_ROWS, StandardCharsets.UTF_8);
            }
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) { // ข้ามหัวตาราง
                String[] c = lines.get(i).split(",", -1);
                if (c.length >= 5 && c[1].trim().equalsIgnoreCase(gender)) {
                    list.add(new CharacterData(Integer.parseInt(c[0].trim()), c[1].trim(),
                            c[2].trim(), c[3].trim(), c[4].trim()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("ไม่สามารถอ่านข้อมูลตัวละครได้");
            e.printStackTrace();
        }
        return list;
    }
}
