package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player implements KeyListener {

    private Character character;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    public Player(Character character) {
        this.character = character;
    }

    public void update(MazeMap map) {

        int speed = character.getSpeed();

        int dx = 0;
        int dy = 0;

        if (up) {
            dy = -speed;
        }

        if (down) {
            dy = speed;
        }

        if (left) {
            dx = -speed;
        }

        if (right) {
            dx = speed;
        }

        // เดินแนวนอน
        if (dx != 0) {
            int newX = character.getX() + dx;

            if (!map.isWall(
                    newX,
                    character.getY(),
                    character.getWidth(),
                    character.getHeight())) {

                character.setPosition(
                        newX,
                        character.getY()
                );
            }
        }

        // เดินแนวตั้ง
        if (dy != 0) {
            int newY = character.getY() + dy;

            if (!map.isWall(
                    character.getX(),
                    newY,
                    character.getWidth(),
                    character.getHeight())) {

                character.setPosition(
                        character.getX(),
                        newY
                );
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_W) {
            up = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            down = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            left = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_W) {
            up = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            down = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            left = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
