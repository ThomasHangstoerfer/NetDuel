package de.bitcoder.netduel;

import android.content.Intent;
import android.util.Log;

/**
 * Created by thomas on 03.01.2017.
 */

public class GameModel {
    private static String TAG = "GameModel";

    public interface OnCustomStateListener {
        void stateChanged();
    }

    private static GameModel mInstance;
    private OnCustomStateListener mListener;
    private boolean mState;

    private PlayerModel player1 = null;
    private PlayerModel player2 = null;

    private GameModel() {}

    public String toString() {
        String s = new String();
        s += "GameModel:\n";
        s += "Player1:\n";
        if ( player1 == null )
            s += "null";
        else
            s += player1.toString();
        s += "Player2:\n";
        if ( player2 == null )
            s += "null";
        else
            s += player2.toString();
        return s;
    }

    public static GameModel getInstance() {
        if(mInstance == null) {
            mInstance = new GameModel();
        }
        return mInstance;
    }

    public void setListener(OnCustomStateListener listener) {
        mListener = listener;
    }

    public void changeState(boolean state) {
        if(mListener != null) {
            mState = state;
            notifyStateChange();
        }
    }

    public boolean getState() {
        return mState;
    }

    private void notifyStateChange() {
        mListener.stateChanged();
    }


    public PlayerModel addPlayer(String name) {
        if ( player1 == null )
        {
            player1 = new PlayerModel();
            player1.setName(name);
            Log.d(TAG, String.format("Player1 '%s' joined", name));
            return player1;
        }
        else if ( player2 == null )
        {
            player2 = new PlayerModel();
            player2.setName(name);
            Log.d(TAG, String.format("Player2 '%s' joined", name));
            return player2;
        }
        else
        {
            return null;
        }
    }

    public void newGame() {
        player1 = null;
        player2 = null;
    }

    public void shoot(PlayerModel player, String angle, String power) {
        Log.d(TAG, "Player " + player.getName() + " shot " + angle + " " + power);
        String[] angle_split = angle.split(":");
        if ( angle_split.length > 1 )
            angle_split[1].trim();
        player.setAngle(Integer.parseInt(angle_split[1]));
        String[] power_split = power.split(":");
        if ( power_split.length > 1 )
            power_split[1].trim();
        player.setPower(Integer.parseInt(power_split[1]));
    }

    public PlayerModel getPlayerByName(String name)
    {
        Log.d(TAG, "getPlayerByName '"+ name + "'");
        if ( player1.getName().equals(name) )
            return player1;
        else if ( player2.getName().equals(name) )
            return player2;
        else
        {
            Log.e(TAG, "Player '"+ name + "' not found!");
            return null;
        }
    }

    public PlayerModel getPlayer1() {
        return player1;
    }

    public PlayerModel getPlayer2() {
        return player2;
    }
}
