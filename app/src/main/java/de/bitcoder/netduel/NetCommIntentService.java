package de.bitcoder.netduel;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetCommIntentService extends IntentService {
    static final String TAG = "NetCommIntentService ";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "de.bitcoder.netduel.action.FOO";
    private static final String ACTION_BAZ = "de.bitcoder.netduel.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "de.bitcoder.netduel.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "de.bitcoder.netduel.extra.PARAM2";

    public NetCommIntentService() {
        super("NetCommIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NetCommIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NetCommIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w("NetCommIntentService", "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            } else
            {
                final int port = 12345;
                ServerSocket listener = null;
                try {
                    listener = new ServerSocket(port);
                    Log.d(this.TAG, String.format("listening on port = %d", port));
                    while (true) {
                        Log.d(this.TAG, "waiting for client");
                        Socket socket = listener.accept();
                        Log.d(this.TAG, String.format("client connected from: %s", socket.getRemoteSocketAddress().toString()));
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintStream out = new PrintStream(socket.getOutputStream());
                        for (String inputLine; (inputLine = in.readLine()) != null;) {
                            Log.d(this.TAG, "received");
                            Log.d(this.TAG, inputLine);
                            StringBuilder outputStringBuilder = new StringBuilder("");
                            char inputLineChars[] = inputLine.toCharArray();
                            for (char c : inputLineChars)
                                outputStringBuilder.append(Character.toChars(c + 1));
                            out.println(outputStringBuilder);
                        }
                    }
                } catch(IOException e) {
                    Log.d(this.TAG, e.toString());
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
