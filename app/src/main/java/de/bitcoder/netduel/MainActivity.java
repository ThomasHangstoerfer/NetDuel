package de.bitcoder.netduel;

import android.annotation.SuppressLint;
import android.app.Activity;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static android.view.MotionEvent.ACTION_DOWN;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private TextView mTextView;
    private TextView mNetCommTextView;
    private EditText mIpEditText;
    private EditText mPortEditText;
    private EditText mUserNameEditText;
    private String userName = "Anonymous";

    private GameModel game = GameModel.getInstance();
    private PlayerModel player = null;

    private NetCommAsyncTask netCommServerTask = null;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null) {
//                actionBar.show();
//            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            if ( motionEvent.getAction() == ACTION_DOWN )
            {
                switch (view.getId() )
                {
                    case R.id.start_server:
                        if ( netCommServerTask != null )
                        {
                            log(TAG, "Stop Server");
                            ((Button)view).setText(R.string.start_server);
                            netCommServerTask.cancel(true);
                            netCommServerTask.stop();
                            netCommServerTask = null;
                        }
                        else
                        {
                            log(TAG, "Start Server " + getLocalIpAddress() );
                            int port = 12345;

                            netCommServerTask = new NetCommAsyncTask(MainActivity.this, mNetCommTextView, true, "", port );
                            netCommServerTask.execute("test1", "test2", "test3");
                            ((Button)view).setText(R.string.stop_server);

                            player = game.addPlayer(userName);
                        }
                        break;
                    case R.id.connect_to_server_button:
                        int port = Integer.parseInt(mPortEditText.getText().toString());
                        netCommServerTask = new NetCommAsyncTask(MainActivity.this, mNetCommTextView, false, mIpEditText.getText().toString(), port );
                        netCommServerTask.execute("test1", "test2", "test3");

                        break;
                    default:
                        break;
                }
            }
            return false;
        }
    };

    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&!inetAddress.isLinkLocalAddress()) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences("NetDualPrefs", 0);
        userName = settings.getString("userName", "Anonymous");

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mTextView = (TextView) findViewById(R.id.textView);
        mIpEditText = (EditText) findViewById(R.id.ip_editText);
        mPortEditText = (EditText) findViewById(R.id.port_editText);
        mUserNameEditText = (EditText) findViewById(R.id.user_name_editText);
        mUserNameEditText.setText(userName);
        mUserNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences settings = getSharedPreferences("NetDualPrefs", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("userName", s.toString() );
                editor.commit();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            });

        mIpEditText.setText("192.168.178.65");
        mPortEditText.setText("12345");

        mNetCommTextView = (TextView) findViewById(R.id.netcomm_textView);
        mNetCommTextView.setText("Start\n");
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        game.newGame();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.start_server).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.connect_to_server_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    public void log(String tag, String msg) {
        Log.v(tag, msg);
        mNetCommTextView.append(tag + ": " + msg+"\n");
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void handleMessage(String msg) {
        Log.d(this.TAG, String.format("handleMessage(%s)", msg));

        String[] separated = msg.split(" ");
        String cmd = separated[0].trim();
        switch (cmd)
        {
            case "hello":
                log(TAG, "Client has connected and said 'hello'");
                netCommServerTask.sendMessage("welcome player. What's your name?");
                break;
            case "welcome player":
                log(TAG, "Connected to server");
                netCommServerTask.sendMessage(String.format("name %s", userName) );
                break;
            case "name":
                if ( separated.length > 1 ) {
                    netCommServerTask.sendMessage("Hello " + separated[1]);
                    log(TAG, "'"+separated[1]+"' joined the game");
                    player = game.addPlayer(separated[1]);
                    if ( player != null )
                        netCommServerTask.sendMessage("yourTurn " + player.getName() );


                    Intent intent = new Intent(this, GameActivity.class);
                    //EditText editText = (EditText) findViewById(R.id.edit_message);
                    //String message = editText.getText().toString();
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                }
                break;
            case "yourTurn":
                if ( separated.length > 1 ) {
                    if (userName == separated[1]) {
                        log(TAG, "Server expects our turn");
                        String shoot_cmd = String.format("shoot " + player.getName() + ":%i power:%i", 45, 100);
                        log(TAG, shoot_cmd );
                        netCommServerTask.sendMessage(shoot_cmd);
                    }
                }
                else
                {
                    netCommServerTask.sendMessage("Invalid command: " + msg);
                }
                break;
            case "shoot":
                if ( separated.length > 3 ) {
                    log(TAG, "'"+separated[1]+"' shot angle:"+separated[2] + " power:"+separated[3]);
                    game.shoot(player, separated[2], separated[3]);
                }
                else
                {
                    netCommServerTask.sendMessage("Invalid command: " + msg);
                }
                break;
            default:
                Log.d(this.TAG, String.format("Unknown command '%s'", cmd));
                break;
        }
    }
}
