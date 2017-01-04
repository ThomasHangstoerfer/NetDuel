package de.bitcoder.netduel;

import android.util.Log;

/**
 * Created by thomas on 03.01.2017.
 */

public class PlayerModel {
    private static String TAG = "PlayerModel";

    private String name = "";
    private int angle = 45;
    private int power = 100;
    public int score = 0;

    public String toString() {
        String s = new String();
        s += "name: " + name + "\n";
        s += String.format("angle: %d\n", angle);
        s += String.format("power: %d\n", power);
        s += String.format("score: %d\n", score);
        return s;
    }
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
        Log.d(TAG, String.format("setAngle(%d)", angle));
        if ( angle <= 90 && angle >= -90 )
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
