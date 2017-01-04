package de.bitcoder.netduel;

/**
 * Created by thomas on 03.01.2017.
 */

public class PlayerModel {
    private static String TAG = "PlayerModel";

    private String name = "";
    private int angle = 45;
    private int power = 100;
    public int score = 0;

    public void setName(String new_name) {
        this.name = new_name;
    }
    public String getName() {
        return this.name;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
