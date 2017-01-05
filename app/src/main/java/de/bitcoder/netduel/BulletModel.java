package de.bitcoder.netduel;

/**
 * Created by thomas on 05.01.2017.
 */

public class BulletModel {

    private double posX = 0.0;
    private double posY = 0.0;
    private double speedX = 0.0;
    private double speedY = 0.0;
    private boolean visible = false;

    public BulletModel() {
    }

    public String toString() {
        String s = new String();
        s += String.format("visible: %s\n", visible?"true":"false");
        s += String.format("coord: %f|%f\n", posX, posY);
        s += String.format("speed: %f|%f\n", speedX, speedY);
        return s;
    }

    public void update() {
        posX += speedX;
        posY += speedY;
        if ( posX < 0 || posX > 2000 )
        {
            speedX = 0;
            speedY = 0;
            visible = false;
        }
    }

    public double getPosX() {return posX;}
    public void setPosX(double posX) {this.posX = posX;}
    public double getPosY() {return posY;}
    public void setPosY(double posY) {this.posY = posY;}
    public double getSpeedX() {return speedX;}
    public void setSpeedX(double speedX) {this.speedX = speedX;}
    public double getSpeedY() {return speedY;}
    public void setSpeedY(double speedY) {this.speedY = speedY;}
    public boolean isVisible() {return visible;}
    public void setVisible(boolean visible) {this.visible = visible;}
}
