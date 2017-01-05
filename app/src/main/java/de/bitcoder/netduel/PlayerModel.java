package de.bitcoder.netduel;

import android.util.Log;

import static java.lang.Math.abs;

/**
 * Created by thomas on 03.01.2017.
 */

public class PlayerModel {
    private static String TAG = "PlayerModel";

    private int playerNumber = 0;
    private int posX = 0;
    private int posY = 0;

    private int gunPosX = 0;
    private int gunPosY = 0;
    private String name = "";
    private int angle = 45;
    private int power = 100;
    private int score = 0;

    private BulletModel bullet = new BulletModel();

    public String toString() {
        String s = new String();
        s += "name: " + name + "\n";
        s += String.format("number: %d\n", playerNumber);
        s += String.format("angle: %d\n", angle);
        s += String.format("power: %d\n", power);
        s += String.format("score: %d\n", score);
        s += String.format("coord: %d|%d\n", posX, posY);
        s += "Bullet:\n";
        s += bullet.toString();
        return s;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        Log.d(TAG, String.format("setAngle(%d)", angle));
        if ( angle <= 90 && angle >= -90 )
            this.angle = angle;
    }

    public void setName(String new_name) {
        this.name = new_name;
    }
    public String getName() {
        return this.name;
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
    public int getPosX() {return posX;}
    public void setPosX(int posX) { this.posX = posX; }
    public int getPosY() {return posY;}
    public void setPosY(int posY) {this.posY = posY;}
    public int getPlayerNumber() {return playerNumber;}
    public void setPlayerNumber(int playerNumber) {this.playerNumber = playerNumber;}
    public int getGunPosX() {return gunPosX;}
    public void setGunPosX(int gunPosX) {this.gunPosX = gunPosX;}
    public int getGunPosY() {return gunPosY;}
    public void setGunPosY(int gunPosY) {this.gunPosY = gunPosY;}
    public BulletModel getBullet() {return bullet;}


    public void fire() {
        bullet.setPosX( gunPosX );
        bullet.setPosY( gunPosY );

        float sX = 100*abs(angle)/90;
        float sY = 100*(90-abs(angle))/90;
        sX = sX/10;
        sY = sY/10;

        bullet.setSpeedX(angle>0?sX*(-1):sX);
        bullet.setSpeedY(sY*(-1));

        bullet.setVisible(true);
    }

}
