package com.mycompany.screen;
import com.mycompany.data.SaveData;
import javax.swing.JPanel;

public class CharacterSelect extends JPanel {

    private int selectedCharacter;
    private String selectedGender;

    public CharacterSelect() {
        initComponents();
        SaveData.saveCharacter(
            selectedCharacter,
            selectedGender);
    }

    private void initComponents() {
        // สำหรับตอนนี้ยังไม่มี GUI
        // เดี๋ยวค่อยใส่ปุ่มเลือกตัวละคร
    }

    // เรียกเมื่อกดปุ่ม Confirm
    private void btnConfirmActionPerformed(
            java.awt.event.ActionEvent evt) {

        System.out.println(
                "Character: " + selectedCharacter
        );

        System.out.println(
                "Gender: " + selectedGender
        );
    }

    // เลือกตัวละคร
    public void setSelectedCharacter(int character) {
        this.selectedCharacter = character;
    }

    // เลือกเพศ
    public void setSelectedGender(String gender) {
        this.selectedGender = gender;
    }

    // เอาค่าตัวละครที่เลือกไปใช้ที่คลาสอื่น
    public int getSelectedCharacter() {
        return selectedCharacter;
    }

    public String getSelectedGender() {
        return selectedGender;
    }

    
}